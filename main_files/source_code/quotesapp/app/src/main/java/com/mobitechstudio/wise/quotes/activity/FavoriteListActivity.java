package com.mobitechstudio.wise.quotes.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.ads.mobitechadslib.AdsModel;
import com.ads.mobitechadslib.MobiAdBanner;
import com.ads.mobitechadslib.MobitechAds;
import com.google.android.gms.ads.AdView;
import com.mobitechstudio.wise.quotes.R;
import com.mobitechstudio.wise.quotes.adapters.FavoriteAdapter;
import com.mobitechstudio.wise.quotes.data.constant.AppConstant;
import com.mobitechstudio.wise.quotes.data.sqlite.FavoriteDbController;
import com.mobitechstudio.wise.quotes.listeners.ListItemClickListener;
import com.mobitechstudio.wise.quotes.models.favorite.FavoriteModel;
import com.mobitechstudio.wise.quotes.utility.ActivityUtilities;
import com.mobitechstudio.wise.quotes.utility.AdsUtilities;
import com.mobitechstudio.wise.quotes.utility.DialogUtilities;

import java.util.ArrayList;

import io.reactivex.disposables.CompositeDisposable;


public class FavoriteListActivity extends BaseActivity implements DialogUtilities.OnCompleteListener {

    private Activity mActivity;
    private Context mContext;

    private ArrayList<FavoriteModel> mFavouriteList;
    private ArrayList<String> mDetailsList;
    private FavoriteAdapter mFavoriteAdapter = null;
    private RecyclerView mRecycler;
    private FavoriteDbController mFavoriteDbController;
    private MenuItem mMenuItemDeleteAll;
    private int mAdapterPosition;

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
        mActivity = FavoriteListActivity.this;
        mContext = mActivity.getApplicationContext();

        mFavouriteList = new ArrayList<>();
        mDetailsList = new ArrayList<>();
    }

    private void initView() {
        setContentView(R.layout.activity_item_list);

        mRecycler = (RecyclerView) findViewById(R.id.rvContent);
        mRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mFavoriteAdapter = new FavoriteAdapter(mContext, mActivity, (ArrayList) mDetailsList);
        mRecycler.setAdapter(mFavoriteAdapter);

        initToolbar(true);
        setToolbarTitle(getString(R.string.site_menu_fav));
        enableUpButton();
        initLoader();
        // ....................Interstitial Ad ...............
        MobitechAds.getIntertistialAd (
                FavoriteListActivity.this ,
                adCategory);
// ...................End of Interstitial ad............
    }

    private void initFunctionality() {

        // show full-screen ads
        AdsUtilities.getInstance(mContext).showFullScreenAd();
        // show banner ads
        AdsUtilities.getInstance(mContext).showBannerAd((AdView) findViewById(R.id.adsView));
    }

    public void updateUI() {
        showLoader();

        if (mFavoriteDbController == null) {
            mFavoriteDbController = new FavoriteDbController(mContext);
        }
        mFavouriteList.clear();
        mDetailsList.clear();
        mFavouriteList.addAll(mFavoriteDbController.getAllData());

        for (int i = 0; i < mFavouriteList.size(); i++) {
            mDetailsList.add(mFavouriteList.get(i).getDetails());
        }
        mFavoriteAdapter.notifyDataSetChanged();

        hideLoader();

        if (mFavouriteList.size() == 0) {
            showEmptyView();
            if (mMenuItemDeleteAll != null) {
                mMenuItemDeleteAll.setVisible(false);
            }
        } else {
            if (mMenuItemDeleteAll != null) {
                mMenuItemDeleteAll.setVisible(true);
            }
        }
    }

    public void initListener() {
        mFavoriteAdapter.setItemClickListener(new ListItemClickListener() {
            @Override
            public void onItemClick(final int position, View view) {
                mAdapterPosition = position;
                final FavoriteModel model = mFavouriteList.get(position);
                switch (view.getId()) {
                    case R.id.lyt_container:
                        ActivityUtilities.getInstance().invokeDetailsActiviy(mActivity, DetailsActivity.class, position, mDetailsList, false);
                        break;
                    case R.id.btn_delete:
                        FragmentManager manager = getSupportFragmentManager();
                        DialogUtilities dialog = DialogUtilities.newInstance(getString(R.string.site_menu_fav), getString(R.string.delete_fav_item), getString(R.string.yes), getString(R.string.no), AppConstant.BUNDLE_KEY_DELETE_EACH_FAV);
                        dialog.show(manager, AppConstant.BUNDLE_KEY_DIALOG_FRAGMENT);
                        break;
                    default:
                        break;
                }
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menus_delete_all:
                FragmentManager manager = getSupportFragmentManager();
                DialogUtilities dialog = DialogUtilities.newInstance(getString(R.string.site_menu_fav), getString(R.string.delete_all_fav_item), getString(R.string.yes), getString(R.string.no), AppConstant.BUNDLE_KEY_DELETE_ALL_FAV);
                dialog.show(manager, AppConstant.BUNDLE_KEY_DIALOG_FRAGMENT);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_delete_all, menu);
        mMenuItemDeleteAll = menu.findItem(R.id.menus_delete_all);

        updateUI();

        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mFavoriteAdapter != null) {
            updateUI();
        }
    }

    @Override
    public void onComplete(Boolean isOkPressed, String viewIdText) {
        if (isOkPressed) {
            if (viewIdText.equals(AppConstant.BUNDLE_KEY_DELETE_ALL_FAV)) {
                mFavoriteDbController.deleteAllFav();
                updateUI();
            } else if (viewIdText.equals(AppConstant.BUNDLE_KEY_DELETE_EACH_FAV)) {
                mFavoriteDbController.deleteEachFav(mDetailsList.get(mAdapterPosition));
                updateUI();
            }
        }
    }
}
