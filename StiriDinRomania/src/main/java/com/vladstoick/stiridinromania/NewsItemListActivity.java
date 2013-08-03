package com.vladstoick.stiridinromania;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.vladstoick.DataModel.NewsSource;
import com.vladstoick.Fragments.NewsItemDetailFragment;
import com.vladstoick.Fragments.NewsItemListFragment;


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
public class NewsItemListActivity extends FragmentActivity
        implements NewsItemListFragment.Callbacks {
    NewsSource newsSource;
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
            newsSource = getIntent().getExtras().getParcelable(NewsSource.TAG);
        setTitle(newsSource.getTitle());
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
            ((NewsItemListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.newsitem_list))
                    .setData(newsSource);
        }

    }

    /**
     * Callback method from {@link NewsItemListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(NewsItemDetailFragment.ARG_ITEM_ID, id);
            NewsItemDetailFragment fragment = new NewsItemDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.newsitem_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, NewsItemDetailActivity.class);
            detailIntent.putExtra(NewsItemDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }
}
