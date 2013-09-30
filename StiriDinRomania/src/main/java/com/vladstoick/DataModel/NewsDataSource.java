package com.vladstoick.DataModel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.internal.r;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.otto.Subscribe;
import com.vladstoick.DialogFragment.RenameDialogFragment;
import com.vladstoick.OttoBus.BusProvider;
import com.vladstoick.OttoBus.DataLoadedEvent;
import com.vladstoick.OttoBus.NewsItemLoadedEvent;
import com.vladstoick.OttoBus.SearchResultsEvent;
import com.vladstoick.Utils.Utils;
import com.vladstoick.stiridinromania.StiriApp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by vlad on 7/20/13.
 */
public class
        NewsDataSource {
    private static NewsDataSource instance;
    public int unparsedFeeds = 0;
    public ArrayList<NewsSource> feeds;
    public boolean isDataLoaded = true;
    public ArrayList<Integer> unreadIds;
    public AsyncHttpClient client;
    private String BASE_URL = "http://37.139.26.80/user/";
    private int userId;
    private String token;
    private SqlHelper sqlHelper;

    //CONSTRUCTORS
    //0. constructor is called
    //1. loadUnreadNews is called
    //2. loadGroupsAndSources is called;
    //3. Once groups and sourcesa are added loadNewsItems in called
    //4. For each news source getNewsItems is being called
    //5. after they are done addNewsSourceInDb is called
    public NewsDataSource(Application app, SharedPreferences settings) {
        this.userId = settings.getInt("user_id", 0);
        this.token = settings.getString("key", "");
        if (Utils.isOnline(app)) {
            loadFeeds();
            loadData();
        }

        sqlHelper = new SqlHelper(app);
        BusProvider.getInstance().register(this);
        NewsDataSource.instance = this;
    }

    public static NewsDataSource getInstance(){
        return instance;
    }

    public static void setInstance(NewsDataSource newsDataSource){
        instance = newsDataSource;
    }

    public void loadFeeds(){
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest("http://37.139.26.80/newssource", null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                feeds = JSONParsing.parseFeeds(jsonObject);
                                loadData();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                volleyError.printStackTrace();
                            }
                        });
        StiriApp.queue.add(jsonObjectRequest);
    }

    public void loadData() {
        loadUnreadNews();
    }

    public void loadUnreadNews() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(BASE_URL + userId + "/unread" +
                Utils.tokenWithoutAnd(token), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        unreadIds = JSONParsing.parseUnreadIds(jsonObject);
                        loadGroupsAndSources();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        }
        );
        StiriApp.queue.add(jsonObjectRequest);
    }

    public void loadGroupsAndSources() {
        isDataLoaded = false;
        StringRequest request = new StringRequest(Request.Method.GET,
                BASE_URL + userId + Utils.tokenWithoutAnd(token), new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                ArrayList<NewsGroup> allNewsGroups = JSONParsing.parseNewsDataSource(s);
                sqlHelper.deleteOldNewsGroupsAndSources(allNewsGroups);
                for (int i = 0; i < allNewsGroups.size(); i++) {
                    for (int j = 0; j < allNewsGroups.get(i).newsSources.size(); j++) {
                        sqlHelper.insertNewsSourceInDb(allNewsGroups.get(i).newsSources.get(j));
                        unparsedFeeds++;
                    }
                    sqlHelper.insertNewsGroupInDb(allNewsGroups.get(i));
                }
                loadNewsItems();
                isDataLoaded = true;

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

    public void loadNewsItems() {
        ArrayList<NewsSource> newsSources = sqlHelper.getAllNewsSources();
        for (int j = 0; j < newsSources.size(); j++) {
            NewsSource ns = newsSources.get(j);
            getNewsItems(ns);
        }
    }

    public void getNewsItems(NewsSource ns) {
        String url = NewsSource.BASE_URL + ns.getId();
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

    public void addNewsSourceInDb(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String sFeedId = jsonObject.getString("feedId");
            int feedId = Integer.parseInt(sFeedId);
            JSONArray feedArray = jsonObject.getJSONArray("articles");
            ArrayList<NewsItem> newsItems = JSONParsing.parseFeed(feedArray);
            NewsSource ns = getNewsSource(feedId);
            ns.news = newsItems;
            sqlHelper.insertNewsSourceInDb(ns);
            sqlHelper.insertNewsItemsInDb(ns);
            newsItems = sqlHelper.getNewsItems(ns.getId());
            for (int i = 0; i < newsItems.size(); i++)
                if (newsItems.get(i).getDescription() == "null")
                    paperizeNewsItem(newsItems.get(i));
            BusProvider.getInstance().post(new
                    DataLoadedEvent(DataLoadedEvent.TAG_NEWSDATASOURCE_MODIFIED));
            unparsedFeeds--;
            if (unparsedFeeds == 0) {
                sqlHelper.updateUnreadNews(unreadIds);
                BusProvider.getInstance().post(new DataLoadedEvent(
                        DataLoadedEvent.TAG_NEWSDATASOURCE));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //MULTIPLE

    @Subscribe
    public void renameElement(RenameDialogFragment.ElementRenamedEvent event) {
        String url;
        if (event.type == RenameDialogFragment.GROUP_TAG) {
            sqlHelper.renameNewsGroup(event);
            url = BASE_URL + userId + "/" + event.id;
        } else {
            sqlHelper.renameNewsSource(event);
            NewsSource ns = getNewsSource(event.id);
            url = BASE_URL + userId + "/" + ns.getGroupId() + "/" + ns.getId();
        }
        StringRequest request = new StringRequest(Request.Method.PUT, url +
                Utils.tokenWithoutAnd(token) + "&title=" + event.newName,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        }
        );
        StiriApp.queue.add(request);
        BusProvider.getInstance().post(new DataLoadedEvent(
                DataLoadedEvent.TAG_NEWSDATASOURCE_MODIFIED));
    }

    public void addNewsGroupAndNewsSource(final String groupTitle, final NewsSource ns) {

        StringRequest request = new StringRequest(Request.Method.POST,
                BASE_URL + userId + Utils.tokenWithoutAnd(token) + "&title=" + groupTitle,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        NewsGroup ng = new NewsGroup(groupTitle,
                                JSONParsing.parseAddNewsGroupResponse(s));
                        sqlHelper.insertNewsGroupInDb(ng);
                        addNewsSource(ns, ng.getId());
                        BusProvider.getInstance().post(new
                                DataLoadedEvent(DataLoadedEvent.TAG_NEWSDATASOURCE_MODIFIED));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                    }
                }
        );
        StiriApp.queue.add(request);
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
        StringRequest request = new StringRequest(Request.Method.DELETE, BASE_URL + userId + "/" +
                id + Utils.tokenWithoutAnd(token), new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                    }
                }
        );
        StiriApp.queue.add(request);
    }

    //NewsSource

    public NewsSource getNewsSource(int id) {
        return sqlHelper.getNewsSource(id);
    }

    public void addNewsSource(final NewsSource newsSource, final int groupId) {
        StringRequest request = new StringRequest(Request.Method.POST, BASE_URL + userId + "/"
                + groupId + Utils.tokenWithoutAnd(token) + "&url=" + newsSource.getRssLink(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        JSONParsing.parseAddNewsSource(newsSource, s);
                        newsSource.setGroupId(groupId);
                        sqlHelper.insertNewsSourceInDb(newsSource);
                        getNewsItems(newsSource);
                        sqlHelper.updateNewsGroupNoFeeds(groupId);
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

    public void deleteNewsSource(NewsSource ns) {
        sqlHelper.deleteNewsSource(ns.getId());
        sqlHelper.updateNewsGroupNoFeeds(ns.getGroupId());
        BusProvider.getInstance().post(new DataLoadedEvent(
                DataLoadedEvent.TAG_NEWSDATASOURCE_MODIFIED));
        String url = BASE_URL + userId + "/" + ns.getGroupId() + "/" + ns.getId()
                + Utils.tokenWithoutAnd(token);
        StringRequest request = new StringRequest(Request.Method.DELETE, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
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

    public int getNumberOfNewsForNewsSource(int id) {
        return sqlHelper.getNumberOfNewsForNewsSource(id);
    }
    //NEWSITEM

    public void makeNewsRead(String url, int newsId) {
        StringRequest request = new StringRequest(Request.Method.DELETE, BASE_URL +
                userId + "/unread?article_id=" + newsId + Utils.tokenWithAnd(token)
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                    }
                }
        );
        StiriApp.queue.add(request);
        sqlHelper.makeNewsRead(url);
    }


    public NewsItem getNewsItem(String url) {
        return sqlHelper.getNewsItem(url);
    }

    public ArrayList<NewsItem> searchNewsItemsLocal(String query) {
        return sqlHelper.searchNewsItem(query);
    }

    public void searchNewsItemOnline(final String query) {
        String url = "http://37.139.8.146:8983/solr/collection1/select?start=0&rows=20" +
                "&wt=json&indent=true&fl=title,content,url&q=description:" + query;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jobj) {
                        ArrayList<NewsItem> searchResults = JSONParsing.parseSearchResults(jobj);
                        BusProvider.getInstance()
                                .post(new SearchResultsEvent(searchResults, query));
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
