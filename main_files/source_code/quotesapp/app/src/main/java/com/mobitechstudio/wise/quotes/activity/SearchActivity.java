package com.mobitechstudio.wise.quotes.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.ads.mobitechadslib.AdsModel;
import com.ads.mobitechadslib.MobiAdBanner;
import com.ads.mobitechadslib.MobitechAds;
import com.google.android.gms.ads.AdView;
import com.mobitechstudio.wise.quotes.R;
import com.mobitechstudio.wise.quotes.adapters.SearchAdapter;
import com.mobitechstudio.wise.quotes.data.constant.AppConstant;
import com.mobitechstudio.wise.quotes.listeners.ListItemClickListener;
import com.mobitechstudio.wise.quotes.utility.ActivityUtilities;
import com.mobitechstudio.wise.quotes.utility.AdsUtilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import io.reactivex.disposables.CompositeDisposable;

public class SearchActivity extends BaseActivity {

    private Activity mActivity;
    private Context mContext;
    private SearchAdapter mAdapter = null;
    private RecyclerView mRecyclerContent;
    private ArrayList<String> mAllQuotes;
    private ArrayList<String> mSearchList;
    private AdsModel adsModel ;
    private MobiAdBanner mobiAdBanner ;
    private CompositeDisposable disposable = new CompositeDisposable();
    private String adCategory = "2" ; //specify the ad category you want to show.



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVar();
        initView();
        initFunctionality();
        initListener();

    }

    private void initVar() {
        mActivity = SearchActivity.this;
        mContext = mActivity.getApplicationContext();

        mAllQuotes = new ArrayList<>();
        mSearchList = new ArrayList<>();
    }

    private void initView() {
        setContentView(R.layout.activity_item_list);

        mRecyclerContent = (RecyclerView) findViewById(R.id.rvContent);
        mRecyclerContent.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        mAdapter = new SearchAdapter(mContext, mActivity, mSearchList);
        mRecyclerContent.setAdapter(mAdapter);

        initLoader();
        initToolbar(true);
        enableUpButton();
        setToolbarTitle(getString(R.string.search));
        // ....................Interstitial Ad ...............
        MobitechAds.getIntertistialAd (
                SearchActivity.this ,
                adCategory);
// ...................End of Interstitial ad............
    }

    private void initFunctionality() {

        showLoader();
        loadCategoryQuotes();

        // show full-screen ads
        AdsUtilities.getInstance(mContext).showFullScreenAd();
        // show banner ads
        AdsUtilities.getInstance(mContext).showBannerAd((AdView) findViewById(R.id.adsView));
    }

    public void initListener() {
        // Recent post list item click listener
        mAdapter.setItemClickListener(new ListItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                ActivityUtilities.getInstance().invokeDetailsActiviy(mActivity, DetailsActivity.class, position, mSearchList, false);
            }

        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setQueryHint(getString(R.string.search));
        searchView.postDelayed(new Runnable() {
            @Override
            public void run() {
                searchView.setIconifiedByDefault(true);
                searchView.setFocusable(true);
                searchView.setIconified(false);
                searchView.requestFocusFromTouch();
            }
        }, 200);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //some texts here
                showLoader();
                mSearchList.clear();

                for (String temp : mAllQuotes) {
                    if (temp.toLowerCase().contains(newText)) {
                        mSearchList.add(temp);
                    }
                }
                if (!mSearchList.isEmpty() && mSearchList.size() > 0) {
                    mAdapter.notifyDataSetChanged();
                    hideLoader();
                } else {
                    showEmptyView();
                }

                return false;
            }
        });


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadCategoryQuotes() {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(getAssets().open(AppConstant.CATEGORY_FILE)));
            String temp;
            while ((temp = br.readLine()) != null)
                sb.append(temp);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        parseCategoryJson(sb.toString());
    }

    private void parseCategoryJson(String jsonData) {
        try {

            JSONObject jsonObjMain = new JSONObject(jsonData);
            JSONArray jsonArray1 = jsonObjMain.getJSONArray(AppConstant.JSON_KEY_ITEMS);

            for (int i = 0; i < jsonArray1.length(); i++) {
                JSONObject jsonObj = jsonArray1.getJSONObject(i);

                ArrayList<String> quoteList = new ArrayList<>();
                JSONArray jsonArray2 = jsonObj.getJSONArray(AppConstant.JSON_KEY_QUOTES);
                for (int j = 0; j < jsonArray2.length(); j++) {
                    String details = jsonArray2.get(j).toString();
                    quoteList.add(details);
                    mAllQuotes.add(details);
                }
            }

            loadAuthorQuotes();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void loadAuthorQuotes() {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(getAssets().open(AppConstant.AUTHOR_FILE)));
            String temp;
            while ((temp = br.readLine()) != null)
                sb.append(temp);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        parseAuthorJson(sb.toString());
    }

    private void parseAuthorJson(String jsonData) {
        try {

            JSONObject jsonObjMain = new JSONObject(jsonData);
            JSONArray jsonArray1 = jsonObjMain.getJSONArray(AppConstant.JSON_KEY_ITEMS);

            for (int i = 0; i < jsonArray1.length(); i++) {
                JSONObject jsonObj = jsonArray1.getJSONObject(i);

                ArrayList<String> quoteList = new ArrayList<>();
                JSONArray jsonArray2 = jsonObj.getJSONArray(AppConstant.JSON_KEY_QUOTES);
                for (int j = 0; j < jsonArray2.length(); j++) {
                    String details = jsonArray2.get(j).toString();
                    quoteList.add(details);
                    mAllQuotes.add(details);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
