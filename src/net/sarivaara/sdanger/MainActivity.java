/*
 * 
 *     Author Samu Sarivaara
 *            samusarivaara@gmail.com
 *        
 *     All rights reserved.
 */
package net.sarivaara.sdanger;

import java.util.ArrayList;
import java.util.List;

import net.sarivaara.sdanger.model.Venue;
import android.os.Bundle;
import android.app.ListActivity;
import android.util.Log;
import android.view.Menu;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ListActivity implements IMainView, SearchView.OnQueryTextListener {

	
	IMainPresenter mPresenter;
	List<Venue> mVenues = new ArrayList<Venue>();	
	SearchView mSearchView;
	TextView mStatusText;
	ArrayAdapter<Venue> mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);		
		mPresenter = new MainPresenter(this, this.getApplicationContext());	    
	    
	    // Adding the progress bar to the root of the layout
	    ViewGroup root = (ViewGroup) findViewById(android.R.id.content);	    
	    
	    mStatusText = new TextView(this);	    
	    mStatusText.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
	            LayoutParams.WRAP_CONTENT));
	    getListView().setEmptyView(mStatusText);
	    root.addView(mStatusText);
	    	    
	    mAdapter = new ArrayAdapter<Venue>(this, android.R.layout.simple_list_item_1, mVenues);
	    setListAdapter(mAdapter);
	    
	    mPresenter.activityCreated(savedInstanceState);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mPresenter.activityResumed();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		mPresenter.activityPaused();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
				
		super.onSaveInstanceState(outState);
		mPresenter.activityOnSaveInstanceState(outState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		// Add SearchView into ActionBar
		getMenuInflater().inflate(R.menu.main, menu);
		SearchView searchView = (SearchView)menu.findItem(R.id.action_search).getActionView();
		searchView.setQueryHint(getText(R.string.search_query_hint));
		searchView.setIconified(false);
		searchView.setOnQueryTextListener(this);
		mSearchView = searchView;
		
		mPresenter.activityMenuReady();
		return true;
	}
	
	@Override
	public void setVenues(List<Venue> items) {
		
		mVenues.clear();
		mVenues.addAll(items);
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void showErrorMessage(IMainView.ErrorMessage error) {
 
		int res = R.string.error_generic;
		
		switch(error) {
			
			case EGeneric:
				break;
			case EHttp:
				res = R.string.error_http;
				break;
				
			default:					
				Log.e("LOGTAG", "Error not implemented");
				break;			
		}
		
		Toast.makeText(this, getString(res), Toast.LENGTH_LONG).show();
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		
		mPresenter.queryStringModified(newText);
		return true;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		mPresenter.queryStringModified(query);
		return true;
	}

	@Override
	public void showSearchProgress(boolean show) {
						
		// Searches are fast, don't show progress at all.
		// It just confuses the user. Future improvement:
		// show small progress icon in action bar if there is space for that.
	}

	@Override
	public void setQueryString(String query) {
		
		mSearchView.setQuery(query, false);		
	}

	@Override
	public void setStatusText(IMainView.Status statusText, float accuracyInMeters) {

		switch(statusText) {
			
			case EStatusFirstLocationQueryOnGoing:
				mStatusText.setText(R.string.status_first_location_query);
				break;
				
			case EStatusNoLocation:
				mStatusText.setText(R.string.status_no_location);
				break;
				
			case EStatusNoNetwork:
				mStatusText.setText(R.string.status_no_network);
				break;
				
			case EStatusNoSearchMatches:
				mStatusText.setText(getString(R.string.status_no_matches, accuracyInMeters));
				break;
				
			default:
				Log.e("LOGTAG", "Error not implemented2");
				break;
		}
		mVenues.clear();
		mAdapter.notifyDataSetChanged();
	}
	
}
