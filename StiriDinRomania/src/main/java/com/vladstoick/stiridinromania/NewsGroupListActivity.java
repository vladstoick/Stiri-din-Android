package com.vladstoick.stiridinromania;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.vladstoick.Fragments.NewsGroupDetailFragment;
import com.vladstoick.Fragments.NewsGroupListFragment;
import com.vladstoick.Utils.Tags;


/**
 * An activity representing a list of NewsSources. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link com.vladstoick.stiridinromania.NewsGroupDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link com.vladstoick.Fragments.NewsGroupListFragment} and the item details
 * (if present) is a {@link com.vladstoick.Fragments.NewsGroupDetailFragment}.
 * <p/>
 * This activity also implements the required
 * {@link com.vladstoick.Fragments.NewsGroupListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class NewsGroupListActivity extends SherlockFragmentActivity
        implements NewsGroupListFragment.Callbacks,
        NewsGroupDetailFragment.NewsGroupDetailFragmentCommunicationInterface {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsgroup_list);

        if (findViewById(R.id.newsgroup_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((NewsGroupListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.newsgroup_list))
                    .setActivateOnItemClick(true);
        }

        // TODO: If exposing deep links into your app, handle intents here.
    }

    /**
     * Callback method from {@link com.vladstoick.Fragments.NewsGroupListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(int id) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putInt(Tags.NEWSGROUP_TAG_ID, id);
            NewsGroupDetailFragment fragment = new NewsGroupDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.newsgroup_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, NewsGroupDetailActivity.class);
            detailIntent.putExtra(Tags.NEWSGROUP_TAG_ID, id);
            startActivity(detailIntent);
        }
    }

    @Override
    public void selectedNewsSource(int id) {
        Intent intent = new Intent(this, NewsItemListActivity.class);
        intent.putExtra(Tags.NEWSOURCE_TAG_ID, id);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.news_group_list_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add: {
                Intent intent = new Intent(this, AddElementAcitvitiy.class);
                startActivity(intent);
                break;
            }
            case R.id.action_logout:{
                SharedPreferences settings = getSharedPreferences("appPref", Context.MODE_PRIVATE);
                settings.edit().remove("user_id").commit();
                ((StiriApp)getApplication()).newsDataSource = null;
                Intent intent = new Intent(this,LoginActivity.class);
                intent.putExtra(Tags.LOGOUT_TAG,true);
                startActivity(intent);
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
