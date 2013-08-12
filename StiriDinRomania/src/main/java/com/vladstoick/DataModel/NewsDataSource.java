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
import com.vladstoick.Utils.Utils;
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
    public boolean isDataLoaded = true;
    public AsyncHttpClient client;
    //CONSTRUCTORS
    public NewsDataSource(int userId, Application app) {
        this.userId = userId;
        if(Utils.isOnline(app)){
            loadDataFromInternet();
        }
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
                getNewsItems(ns);
            }
        }
    }

    //MULTIPLE

    @Subscribe
    public void renameElement(RenameDialogFragment.ElementRenamedEvent event){
        String url;
        RequestParams requestParams = new RequestParams();
        requestParams.put("title",event.newName);
        if(event.type == RenameDialogFragment.GROUP_TAG){
            sqlHelper.renameNewsGroup(event);
            url = BASE_URL + userId + "/" + event.id;
        } else {
            sqlHelper.renameNewsSource(event);
            NewsSource ns = getNewsSource(event.id);
            url = BASE_URL + userId + "/" + ns.getGroupId() + "/" + ns.getId();
        }
        httpClient.put(url, requestParams, new AsyncHttpResponseHandler(){});
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


    //NEWSGROUP

    public ArrayList<NewsGroup> getAllNewsGroups() {
        return sqlHelper.getAllNewsGroups();
    }

    public NewsGroup getNewsGroup(int id) {
        return sqlHelper.getNewsGroup(id);
    }

    public void deleteNewsGroup(final int id) {
        sqlHelper.deleteNewsGroup(id);
        BusProvider.getInstance().post(new DataLoadedEvent(
                DataLoadedEvent.TAG_NEWSDATASOURCE_MODIFIED));
        httpClient.delete(BASE_URL + userId + "/" + id,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String s) {

                    }
                });

    }

   //NewsSource

    public NewsSource getNewsSource(int id) {
        return sqlHelper.getNewsSource(id);
    }

    public void addNewsSourceInDb(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String sFeedId = jsonObject.getString("feedId");
            int feedId = Integer.parseInt(sFeedId);
            JSONArray feedArray = jsonObject.getJSONArray("articles");
            ArrayList<NewsItem> newsItems = JSONParsing.parseFeed(feedArray);
            NewsSource ns = getNewsSource(feedId);
            ns.news = newsItems;
            ns.setNumberOfUnreadNews(newsItems.size());
            sqlHelper.insertNewsSourceInDb(ns);
            sqlHelper.insertNewsItemsInDb(ns);
            newsItems = sqlHelper.getNewsItems(ns);
            for (int i = 0; i < newsItems.size(); i++)
                if (newsItems.get(i).getDescription() == "null")
                    paperizeNewsItem(newsItems.get(i));
            BusProvider.getInstance().post(new
                    DataLoadedEvent(DataLoadedEvent.TAG_NEWSDATASOURCE_MODIFIED));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
                        getNewsItems(newsSource);
                        sqlHelper.updateNewsGroupNoFeeds(groupId);
                    }
                });
    }

    public void deleteNewsSource(NewsSource ns){
        sqlHelper.deleteNewsSource(ns.getId());
        sqlHelper.updateNewsGroupNoFeeds(ns.getGroupId());
        BusProvider.getInstance().post(new DataLoadedEvent(
                DataLoadedEvent.TAG_NEWSDATASOURCE_MODIFIED));
        String url = BASE_URL + userId + "/" + ns.getGroupId() + "/" + ns.getId();
        httpClient.delete(url, new AsyncHttpResponseHandler(){});
    }
    //NEWSITEM

    public void getNewsItems(NewsSource ns) {
        String url = NewsSource.BASE_URL + ns.getRssLink() + "&feedId=" + ns.getId();
//                            +"&date="+dateString;
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                addNewsSourceInDb(response);
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

    public NewsItem getNewsItem(String url){
        return  sqlHelper.getNewsItem(url);
    }

    public ArrayList<NewsItem> searchNewsItems(String query){
        return  sqlHelper.searchNewsItem(query);
    }

    public void paperizeNewsItem(NewsItem ni) {
        String url = "http://37.139.8.146:8080/?url=" + ni.getUrlLink();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            String url = jsonObject.getString("url");
                            addPaperizedStringToNewsItem(url, jsonObject.getString("response"));
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


    public void addPaperizedStringToNewsItem(String url, String paperized) {
        sqlHelper.updateNewsItem(url, paperized);
    }
}
