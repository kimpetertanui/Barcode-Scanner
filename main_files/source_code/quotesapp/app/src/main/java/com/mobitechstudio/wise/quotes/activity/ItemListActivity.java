package com.mobitechstudio.wise.quotes.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.ads.mobitechadslib.AdsModel;
import com.ads.mobitechadslib.MobiAdBanner;
import com.ads.mobitechadslib.MobitechAds;
import com.google.android.gms.ads.AdView;
import com.mobitechstudio.wise.quotes.R;
import com.mobitechstudio.wise.quotes.adapters.ItemAdapter;
import com.mobitechstudio.wise.quotes.data.constant.AppConstant;
import com.mobitechstudio.wise.quotes.data.sqlite.MarkerDbController;
import com.mobitechstudio.wise.quotes.listeners.ListItemClickListener;
import com.mobitechstudio.wise.quotes.models.item.ItemModel;
import com.mobitechstudio.wise.quotes.models.marker.MarkerModel;
import com.mobitechstudio.wise.quotes.utility.ActivityUtilities;
import com.mobitechstudio.wise.quotes.utility.AdsUtilities;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;


public class ItemListActivity extends BaseActivity {
    private Activity mActivity;
    private Context mContext;

    private ArrayList<ItemModel> mQuoteItemList;
    private ArrayList<String> mItemList;
    private RecyclerView mRecycler;
    private ItemAdapter mAdapter;

    // Markers view
    private List<MarkerModel> mMarkerList;
    private MarkerDbController mMarkerDbController;

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
        mActivity = ItemListActivity.this;
        mContext = mActivity.getApplicationContext();

        mMarkerList = new ArrayList<>();
        mQuoteItemList = new ArrayList<>();

        Intent intent = getIntent();
        if (intent != null) {
            mItemList = intent.getStringArrayListExtra(AppConstant.BUNDLE_KEY_ITEM);
        }
    }

    private void initView() {
        setContentView(R.layout.activity_item_list);

        mRecycler = (RecyclerView) findViewById(R.id.rvContent);
        mRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new ItemAdapter(mContext, mActivity, mQuoteItemList);
        mRecycler.setAdapter(mAdapter);

        initLoader();
        initToolbar(true);
        setToolbarTitle(getString(R.string.quote_list));
        enableUpButton();
        // ....................Interstitial Ad ...............
        MobitechAds.getIntertistialAd (
                ItemListActivity.this ,
                adCategory);
// ...................End of Interstitial ad............
    }

    private void initFunctionality() {
        showLoader();

        // show full-screen ads
        AdsUtilities.getInstance(mContext).showFullScreenAd();
        // show banner ads
        AdsUtilities.getInstance(mContext).showBannerAd((AdView) findViewById(R.id.adsView));
    }

    public void initListener() {
        mAdapter.setItemClickListener(new ListItemClickListener() {
            @Override
            public void onItemClick(final int position, View view) {
                switch (view.getId()) {
                    case R.id.lyt_container:
                        ActivityUtilities.getInstance().invokeDetailsActiviy(mActivity, DetailsActivity.class, position, mItemList, false);
                        break;
                    default:
                        break;
                }
            }
        });

    }

    public void updateUI() {
        showLoader();

        if (mMarkerDbController == null) {
            mMarkerDbController = new MarkerDbController(mContext);
        }
        mMarkerList.clear();
        mQuoteItemList.clear();
        mMarkerList.addAll(mMarkerDbController.getAllData());

        for (int i = 0; i < mItemList.size(); i++) {
            boolean isMarkerSet = false;
            for (int j = 0; j < mMarkerList.size(); j++) {
                if (mItemList.get(i).equals(mMarkerList.get(j).getDetails())) {
                    isMarkerSet = true;
                    break;
                }
            }
            mQuoteItemList.add(new ItemModel(mItemList.get(i), isMarkerSet));
        }

        mAdapter.notifyDataSetChanged();

        hideLoader();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter != null) {
            updateUI();
        }
    }

}
