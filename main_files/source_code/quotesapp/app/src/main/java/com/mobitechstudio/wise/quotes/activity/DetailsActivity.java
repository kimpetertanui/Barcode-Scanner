package com.mobitechstudio.wise.quotes.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ads.mobitechadslib.AdsModel;
import com.ads.mobitechadslib.MobiAdBanner;
import com.ads.mobitechadslib.MobitechAds;
import com.google.android.gms.ads.AdView;
import com.mobitechstudio.wise.quotes.R;
import com.mobitechstudio.wise.quotes.adapters.DetailPagerAdapter;
import com.mobitechstudio.wise.quotes.data.constant.AppConstant;
import com.mobitechstudio.wise.quotes.data.sqlite.FavoriteDbController;
import com.mobitechstudio.wise.quotes.data.sqlite.MarkerDbController;
import com.mobitechstudio.wise.quotes.models.favorite.FavoriteModel;
import com.mobitechstudio.wise.quotes.models.marker.MarkerModel;
import com.mobitechstudio.wise.quotes.utility.AdsUtilities;
import com.mobitechstudio.wise.quotes.utility.TtsEngine;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

public class DetailsActivity extends BaseActivity {
    private Activity mActivity;
    private Context mContext;
    private ArrayList<String> mItemList;
    private int mCurrentIndex;
    private ViewPager mViewPager;
    private DetailPagerAdapter mPagerAdapter = null;
    private TextView mTxtCounter;
    private ImageButton mBtnMarker, mBtnFavorite, mBtnShare, mBtnCopy;

    // Favourites view
    private List<FavoriteModel> mFavoriteList;
    private FavoriteDbController mFavoriteDbController;
    private boolean mIsFavorite = false;

    // Markers view
    private List<MarkerModel> mMarkerList;
    private MarkerDbController mMarkerDbController;
    private boolean mIsMarker = false;

    private TtsEngine mTtsEngine;
    private boolean mIsTtsPlaying = false;
    private String mTtsText;
    private MenuItem menuItemTTS;
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
        // ....................Interstitial Ad ...............
        MobitechAds. getIntertistialAd (
                DetailsActivity. this ,
                adCategory );
// ...................End of Interstitial ad............
    }

    private void initVar() {
        mActivity = DetailsActivity.this;
        mContext = mActivity.getApplicationContext();

        mFavoriteList = new ArrayList<>();
        mMarkerList = new ArrayList<>();

        Intent intent = getIntent();
        if (intent != null) {
            mCurrentIndex = intent.getIntExtra(AppConstant.BUNDLE_KEY_INDEX, 0);
            mItemList = intent.getStringArrayListExtra(AppConstant.BUNDLE_KEY_ITEM);
        }
    }

    private void initView() {
        setContentView(R.layout.activity_details);

        mViewPager = (ViewPager) findViewById(R.id.pager);

        mTxtCounter = (TextView) findViewById(R.id.menus_counter);
        mBtnFavorite = (ImageButton) findViewById(R.id.menus_fav);
        mBtnShare = (ImageButton) findViewById(R.id.menus_share);
        mBtnCopy = (ImageButton) findViewById(R.id.menus_copy);
        mBtnMarker = (ImageButton) findViewById(R.id.menus_marker);
        mBtnMarker.setVisibility(View.VISIBLE);

        initLoader();
        initToolbar(false);
        setToolbarTitle(getString(R.string.details_text));
        enableUpButton();
    }

    private void initFunctionality() {
        showLoader();

        mPagerAdapter = new DetailPagerAdapter(mActivity, mItemList);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(mCurrentIndex);

        mTtsText = Html.fromHtml(mItemList.get(mViewPager.getCurrentItem())).toString();

        mFavoriteDbController = new FavoriteDbController(mContext);
        mFavoriteList.addAll(mFavoriteDbController.getAllData());
        isFavorite();

        mMarkerDbController = new MarkerDbController(mContext);
        mMarkerList.addAll(mMarkerDbController.getAllData());
        isMarker();

        mTtsEngine = new TtsEngine(mActivity);

        updateCounter();

        hideLoader();

        // load full screen ad
        AdsUtilities.getInstance(mContext).loadFullScreenAd(mActivity);
        // show banner ads
        AdsUtilities.getInstance(mContext).showBannerAd((AdView) findViewById(R.id.adsView));
    }


    public void initListener() {
        mBtnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIsFavorite = !mIsFavorite;
                if (mIsFavorite) {
                    mFavoriteDbController.insertData(mItemList.get(mViewPager.getCurrentItem()));
                    mFavoriteList.add(new FavoriteModel(AppConstant.BUNDLE_KEY_ZERO_INDEX, mItemList.get(mViewPager.getCurrentItem())));
                    Toast.makeText(getApplicationContext(), getString(R.string.added_to_fav), Toast.LENGTH_SHORT).show();
                } else {
                    mFavoriteDbController.deleteEachFav(mItemList.get(mViewPager.getCurrentItem()));
                    for (int i = 0; i < mFavoriteList.size(); i++) {
                        if (mFavoriteList.get(i).getDetails().equals(mItemList.get(mViewPager.getCurrentItem()))) {
                            mFavoriteList.remove(i);
                            break;
                        }
                    }
                    Toast.makeText(getApplicationContext(), getString(R.string.removed_from_fav), Toast.LENGTH_SHORT).show();
                }
                setFavorite();
            }
        });

        mBtnMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIsMarker = !mIsMarker;
                if (mIsMarker) {
                    mMarkerDbController.insertData(mItemList.get(mViewPager.getCurrentItem()));
                    mMarkerList.add(new MarkerModel(AppConstant.BUNDLE_KEY_ZERO_INDEX, mItemList.get(mViewPager.getCurrentItem())));
                    Toast.makeText(getApplicationContext(), getString(R.string.added_to_marker), Toast.LENGTH_SHORT).show();
                } else {
                    mMarkerDbController.deleteEachMarker(mItemList.get(mViewPager.getCurrentItem()));
                    for (int i = 0; i < mMarkerList.size(); i++) {
                        if (mMarkerList.get(i).getDetails().equals(mItemList.get(mViewPager.getCurrentItem()))) {
                            mMarkerList.remove(i);
                            break;
                        }
                    }
                    Toast.makeText(getApplicationContext(), getString(R.string.removed_from_marker), Toast.LENGTH_SHORT).show();
                }
                setMarker();
            }
        });

        mBtnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String appPackageName = mActivity.getPackageName();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(mItemList.get(mViewPager.getCurrentItem()))
                        + AppConstant.EMPTY_STRING
                        + mActivity.getResources().getString(R.string.share_text)
                        + " https://play.google.com/store/apps/details?id=" + appPackageName);
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
            }
        });

        mBtnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("Text Label", Html.fromHtml(mItemList.get(mViewPager.getCurrentItem())));
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), getString(R.string.copy_to_clipboard), Toast.LENGTH_SHORT).show();
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                updateCounter();
                mIsFavorite = false;
                isFavorite();
                mIsMarker = false;
                isMarker();

                // show full-screen ads
                AdsUtilities.getInstance(mContext).showFullScreenAd();

                if (position % AppConstant.ADS_INTERVAL == 0) {
                    // load full screen ad
                    AdsUtilities.getInstance(mContext).loadFullScreenAd(mActivity);
                }

                mTtsText = Html.fromHtml(mItemList.get(position)).toString();
                if (mIsTtsPlaying) {
                    toggleTtsPlay(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void isFavorite() {
        for (int i = 0; i < mFavoriteList.size(); i++) {
            if (mFavoriteList.get(i).getDetails().equals(mItemList.get(mViewPager.getCurrentItem()))) {
                mIsFavorite = true;
                break;
            }
        }
        setFavorite();
    }

    public void setFavorite() {
        if (mIsFavorite) {
            mBtnFavorite.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.ic_fav));
        } else {
            mBtnFavorite.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.ic_un_fav));
        }
    }

    public void isMarker() {
        for (int i = 0; i < mMarkerList.size(); i++) {
            if (mMarkerList.get(i).getDetails().equals(mItemList.get(mViewPager.getCurrentItem()))) {
                mIsMarker = true;
                break;
            }
        }
        setMarker();
    }

    public void setMarker() {
        if (mIsMarker) {
            mBtnMarker.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.ic_pin));
        } else {
            mBtnMarker.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.ic_un_pin));
        }
    }

    public void updateCounter() {
        String counter = String.format(getString(R.string.item_counter), mViewPager.getCurrentItem() + AppConstant.BUNDLE_KEY_FIRST_INDEX, mItemList.size());
        mTxtCounter.setText(counter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                // show full-screen ads
                AdsUtilities.getInstance(mContext).showFullScreenAd();
                return true;
            case R.id.menus_read_article:
                if (mItemList != null) {
                    toggleTtsPlay(false);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggleTtsPlay(boolean isPageScrolledWhilePlaying) {
        if (mIsTtsPlaying & !isPageScrolledWhilePlaying) {
            mTtsEngine.releaseEngine();
            mIsTtsPlaying = false;
        } else if (mIsTtsPlaying & isPageScrolledWhilePlaying) {
            mTtsEngine.releaseEngine();
            mTtsEngine.startEngine(mTtsText);
            mIsTtsPlaying = true;
        } else {
            mTtsEngine.startEngine(mTtsText);
            mIsTtsPlaying = true;
        }
        toggleTtsView();
    }

    private void toggleTtsView() {
        if (mIsTtsPlaying) {
            menuItemTTS.setTitle(R.string.site_menu_stop_reading);
        } else {
            menuItemTTS.setTitle(R.string.read_post);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mTtsEngine.releaseEngine();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTtsEngine.releaseEngine();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_details, menu);

        menuItemTTS = menu.findItem(R.id.menus_read_article);

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // show full-screen ads
        AdsUtilities.getInstance(mContext).showFullScreenAd();
    }

}