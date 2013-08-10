package com.vladstoick.DataModel;

import android.app.Application;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.otto.Subscribe;
import com.vladstoick.DialogFragment.RenameDialogFragment;
import com.vladstoick.OttoBus.BusProvider;
import com.vladstoick.OttoBus.DataLoadedEvent;
import com.vladstoick.OttoBus.NewsItemLoadedEvent;
import com.vladstoick.sql.SqlHelper;
import com.vladstoick.stiridinromania.StiriApp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by vlad on 7/20/13.
 */
public class NewsDataSource {
    private AsyncHttpClient httpClient;
    private String BASE_URL = "http://stiriromania.eu01.aws.af.cm/user/";
    private int userId;
    private SqlHelper sqlHelper;
    private Date updateAt;
    public boolean isDataLoaded = false;
    public AsyncHttpClient client;
    //CONSTRUCTORS
    public NewsDataSource(int userId, Application app) {
        this.userId = userId;
        loadDataFromInternet();
        sqlHelper = new SqlHelper(app);
        BusProvider.getInstance().register(this);
    }

    public void loadDataFromInternet() {
        isDataLoaded = false;
        httpClient = new AsyncHttpClient();
        StringRequest request = new StringRequest(Request.Method.GET,
                BASE_URL + userId, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                ArrayList<NewsGroup> allNewsGroups = JSONParsing.parseNewsDataSource(s);
                sqlHelper.deleteAllNewsGroupsAndNewsSources();
                for (int i = 0; i < allNewsGroups.size(); i++) {
                    for (int j = 0; j < allNewsGroups.get(i).newsSources.size(); j++)
                        sqlHelper.insertNewsSourceInDb(allNewsGroups.get(i).newsSources.get(j));
                    sqlHelper.insertNewsGroupInDb(allNewsGroups.get(i));
                }
                BusProvider.getInstance().post(new DataLoadedEvent(
                        DataLoadedEvent.TAG_NEWSDATASOURCE));
                isDataLoaded = true;

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        });
        StiriApp.queue.add(request);
    }

    @Subscribe
    public void OnDataLoaded(DataLoadedEvent event) {
        final String dateString = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"))
                .format(Calendar.getInstance().getTime());
        if (event.dataLoadedType == DataLoadedEvent.TAG_NEWSDATASOURCE) {
            ArrayList<NewsSource> newsSources = sqlHelper.getAllNewsSources();
            for (int j = 0; j < newsSources.size(); j++) {
                NewsSource ns = newsSources.get(j);
                getNewsSourceItems(ns);
            }
        }
    }

    public void getNewsSourceItems(NewsSource ns) {
        String url = NewsSource.BASE_URL + ns.getRssLink() + "&feedId=" + ns.getId();
//                            +"&date="+dateString;
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                addNewSource(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        }
        );
        StiriApp.queue.add(stringRequest);
    }

    public void addNewSource(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String sFeedId = jsonObject.getString("feedId");
            int feedId = Integer.parseInt(sFeedId);
            JSONArray feedArray = jsonObject.getJSONArray("articles");
            ArrayList<NewsItem> newsItems = JSONParsing.parseFeed(feedArray);
            for (int i = 0; i < newsItems.size(); i++)
                if (newsItems.get(i).getDescription() == "null")
                    getNewsItemPaperized(newsItems.get(i));
            NewsSource ns = getNewsSource(feedId);
            ns.news = newsItems;
            ns.setNumberOfUnreadNews(newsItems.size());
            sqlHelper.insertNewsSourceInDb(ns);
            sqlHelper.insertNewsItemsInDb(ns);
            BusProvider.getInstance().post(new
                    DataLoadedEvent(DataLoadedEvent.TAG_NEWSDATASOURCE_MODIFIED));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //MODIFYING DATA
    public void addNewsSource(final NewsSource newsSource, final int groupId) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("title", newsSource.getTitle());
        requestParams.put("description", newsSource.getDescription());
        requestParams.put("url", newsSource.getRssLink());
        httpClient.post(BASE_URL + userId + "/" + groupId, requestParams,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String s) {
                        int newsSourceId = JSONParsing.getNewsSourceId(s);
                        newsSource.setId(newsSourceId);
                        newsSource.setGroupId(groupId);
                        sqlHelper.insertNewsSourceInDb(newsSource);
                        getNewsSourceItems(newsSource);
                        sqlHelper.updateNewsGroupNoFeeds(groupId);
                    }
                });
    }

    public void deleteNewsGroup(final int id) {
        sqlHelper.deleteNewsGroup(id);
        httpClient = new AsyncHttpClient();
        BusProvider.getInstance().post(new DataLoadedEvent(
                DataLoadedEvent.TAG_NEWSDATASOURCE_MODIFIED));
        httpClient.delete(BASE_URL + userId + "/" + id,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String s) {

                    }
                });

    }
    @Subscribe
    public void renameElement(RenameDialogFragment.ElementRenamedEvent event){
        if(event.type == RenameDialogFragment.GROUP_TAG){
            sqlHelper.renameNewsGroup(event);
            RequestParams requestParams = new RequestParams();
            requestParams.put("title",event.newName);
            httpClient.put(BASE_URL + userId + "/" + event.id,
                    requestParams, new AsyncHttpResponseHandler(){

            });
        } else {
            sqlHelper.renameNewsSource(event);
        }
        BusProvider.getInstance().post(new DataLoadedEvent(
                DataLoadedEvent.TAG_NEWSDATASOURCE_MODIFIED));
    }

    public void addNewsGroupAndNewsSource(final String groupTitle, final NewsSource ns) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("title", groupTitle);
        httpClient.post(BASE_URL + userId, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String s) {
                NewsGroup ng = new NewsGroup(groupTitle, JSONParsing.parseAddNewsGroupResponse(s));
                sqlHelper.insertNewsGroupInDb(ng);
                addNewsSource(ns, ng.getId());
                BusProvider.getInstance().post(new
                        DataLoadedEvent(DataLoadedEvent.TAG_NEWSDATASOURCE_MODIFIED));
            }
        });
    }

    public void getNewsItemPaperized(NewsItem ni) {
        String url = "http://37.139.8.146:8080/?url=" + ni.getUrlLink();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            String url = jsonObject.getString("url");
                            updateNewsItem(url,jsonObject.getString("response"));
                            BusProvider.getInstance().post(
                                    new NewsItemLoadedEvent(getNewsItem(url)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        }
        );
        StiriApp.queue.add(request);
    }

    public ArrayList<NewsGroup> getAllNewsGroups() {
        return sqlHelper.getAllNewsGroups();
    }

    public NewsGroup getNewsGroup(int id) {
        return sqlHelper.getNewsGroup(id);
    }

    public NewsSource getNewsSource(int id) {
        return sqlHelper.getNewsSource(id);
    }

    public NewsItem getNewsItem(String url){
        return  sqlHelper.getNewsItem(url);
    }

    public void updateNewsItem(String url, String paperized) {
        sqlHelper.updateNewsItem(url, paperized);
    }
}
