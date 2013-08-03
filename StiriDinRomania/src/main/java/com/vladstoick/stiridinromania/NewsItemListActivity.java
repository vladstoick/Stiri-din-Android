package com.vladstoick.stiridinromania;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.vladstoick.DataModel.NewsItem;
import com.vladstoick.DataModel.NewsSource;
import com.vladstoick.Fragments.NewsItemDetailFragment;
import com.vladstoick.Fragments.NewsItemListFragment;
import com.vladstoick.Utils.Tags;


/**
 * An activity representing a list of NewsItems. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link NewsItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link com.vladstoick.Fragments.NewsItemListFragment} and the item details
 * (if present) is a {@link com.vladstoick.Fragments.NewsItemDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link com.vladstoick.Fragments.NewsItemListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class NewsItemListActivity extends SherlockFragmentActivity
        implements NewsItemListFragment.Callbacks {
    private int newsSourceId;
    public NewsSource newsSource;
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsitem_list);
        if(getIntent().getExtras()!=null )
        {
            newsSourceId = getIntent().getExtras().getInt(Tags.NEWSOURCE_TAG_ID);
            newsSource = ((StiriApp)getApplication()).newsDataSource.getNewsSource(newsSourceId);
            setTitle(newsSource.getTitle());
            ((NewsItemListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.newsitem_list))
                    .setData(newsSource);
            if (findViewById(R.id.newsitem_detail_container) != null) {
                // The detail container view will be present only in the
                // large-screen layouts (res/values-large and
                // res/values-sw600dp). If this view is present, then the
                // activity should be in two-pane mode.
                mTwoPane = true;
                // In two-pane mode, list items should be given the
                // 'activated' state when touched.
                ((NewsItemListFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.newsitem_list))
                        .setActivateOnItemClick(true);

            }
        }

    }

    /**
     * Callback method from {@link NewsItemListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        NewsItem ni = null ;
        for(int i=0;i<newsSource.news.size();i++)
            if(id==newsSource.news.get(i).getUrlLink())
                ni=newsSource.news.get(i);
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            //TODO scapa

            arguments.putParcelable(NewsItemDetailFragment.ARG_ITEM, ni);

            NewsItemDetailFragment fragment = new NewsItemDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.newsitem_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, NewsItemDetailActivity.class);
            detailIntent.putExtra(NewsItemDetailFragment.ARG_ITEM, ni);
            startActivity(detailIntent);
        }
    }
    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                NavUtils.navigateUpTo(this, new Intent(this, NewsItemListActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
