package com.mobitechstudio.wise.quotes.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ads.mobitechadslib.AdsModel;
import com.ads.mobitechadslib.MobiAdBanner;
import com.google.android.gms.ads.AdView;
import com.mobitechstudio.wise.quotes.R;
import com.mobitechstudio.wise.quotes.adapters.AuthorAdapter;
import com.mobitechstudio.wise.quotes.adapters.CategoryAdapter;
import com.mobitechstudio.wise.quotes.adapters.RandomPagerAdapter;
import com.mobitechstudio.wise.quotes.data.constant.AppConstant;
import com.mobitechstudio.wise.quotes.data.sqlite.NotificationDbController;
import com.mobitechstudio.wise.quotes.listeners.ListItemClickListener;
import com.mobitechstudio.wise.quotes.models.quote.Category;
import com.mobitechstudio.wise.quotes.models.quote.Author;
import com.mobitechstudio.wise.quotes.models.notification.NotificationModel;
import com.mobitechstudio.wise.quotes.utility.ActivityUtilities;
import com.mobitechstudio.wise.quotes.utility.AdsUtilities;
import com.mobitechstudio.wise.quotes.utility.AppUtilities;
import com.mobitechstudio.wise.quotes.utility.RateItDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import okhttp3.Response;

import static com.ads.mobitechadslib.MobitechAds.getBannerAd;
import static com.ads.mobitechadslib.MobitechAds.getBannerAdValues;

public class MainActivity extends BaseActivity {
    private AdsModel adsModel ;
    private MobiAdBanner mobiAdBanner ;
    private CompositeDisposable disposable = new CompositeDisposable();
    private String adCategory = "3" ; //specify the ad category you want to show.


    private Activity mActivity;
    private Context mContext;

    private RelativeLayout mNotificationView;
    private ImageButton mImgBtnSearch;

    // random quotes
    private ArrayList<String> mAllRandomQuotes;
    private ViewPager mPagerRandomQuotes;
    private RandomPagerAdapter mRandomAdapter = null;
    private TextView mSeeAllRandom;

    // quotes by category
    private ArrayList<Category> mCategoryList;
    private ArrayList<Category> mHomeCategoryList;
    private RecyclerView mRvCategories;
    private CategoryAdapter mCategoryAdapter;
    private TextView mSeeAllCategory;

    // quotes by author
    private ArrayList<Author> mAuthorList;
    private ArrayList<Author> mHomeAuthorList;
    private RecyclerView mRvAuthors;
    private AuthorAdapter mAuthorAdapter;
    private TextView mSeeAllAuthor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RateItDialogFragment.show(this, getSupportFragmentManager());

        initVar();
        initView();
        loadData();
        initListener();

    }

    @Override
    protected void onPause() {
        super.onPause();

        //unregister broadcast receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(newNotificationReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //register broadcast receiver
        IntentFilter intentFilter = new IntentFilter(AppConstant.NEW_NOTI);
        LocalBroadcastManager.getInstance(this).registerReceiver(newNotificationReceiver, intentFilter);

        initNotification();

        // load full screen ad
        AdsUtilities.getInstance(mContext).loadFullScreenAd(mActivity);
    }

    // received new broadcast
    private BroadcastReceiver newNotificationReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            initNotification();
        }
    };


    @Override
    public void onBackPressed() {
        AppUtilities.tapPromptToExit(mActivity);
    }

    private void initVar() {
        mActivity = MainActivity.this;
        mContext = getApplicationContext();

        mAllRandomQuotes = new ArrayList<>();
        mCategoryList = new ArrayList<>();
        mHomeCategoryList = new ArrayList<>();
        mAuthorList = new ArrayList<>();
        mHomeAuthorList = new ArrayList<>();
    }

    private void initView() {
        setContentView(R.layout.activity_main);
        mNotificationView = (RelativeLayout) findViewById(R.id.notificationView);
        mImgBtnSearch = (ImageButton) findViewById(R.id.imgBtnSearch);

        mSeeAllRandom = (TextView) findViewById(R.id.txt_see_all_random);
        mSeeAllCategory = (TextView) findViewById(R.id.txt_see_all_category);
        mSeeAllAuthor = (TextView) findViewById(R.id.txt_see_all_author);

        mPagerRandomQuotes = (ViewPager) findViewById(R.id.pagerRandomQuote);

        mRvCategories = (RecyclerView) findViewById(R.id.rvCategories);
        mRvCategories.setLayoutManager(new GridLayoutManager(mActivity, 2, GridLayoutManager.VERTICAL, false));
        mCategoryAdapter = new CategoryAdapter(mContext, mActivity, mHomeCategoryList);
        mRvCategories.setAdapter(mCategoryAdapter);

        mRvAuthors = (RecyclerView) findViewById(R.id.rvAuthors);
        mRvAuthors.setLayoutManager(new GridLayoutManager(mActivity, 2, GridLayoutManager.VERTICAL, false));
        mAuthorAdapter = new AuthorAdapter(mContext, mActivity, mHomeAuthorList);
        mRvAuthors.setAdapter(mAuthorAdapter);


        initToolbar(false);
        initDrawer();
        initLoader();
        // ----------------------Banner Ad --------------------.
        mobiAdBanner = findViewById(R.id. bannerAd );
        mobiAdBanner .setOnClickListener(v -> {
            mobiAdBanner .viewBannerAd(MainActivity. this ,
                    adsModel .getAd_urlandroid());
        });
        Timer timer = new Timer();
        TimerTask myTask = new TimerTask() {
            @Override
            public void run() {
                Observable<okhttp3.Response> observable = getBannerAd ( adCategory );
                observable.subscribe(new Observer<Response>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onNext(Response response) {
                        try {
                            adsModel = getBannerAdValues (response.body().string());
                            mobiAdBanner .showAd(getApplicationContext(),
                                    adsModel .getAd_upload());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log. e ( "Banner" , "onError : " +e.getMessage() );

                    }

                    @Override
                    public void onComplete() {

                    }
                });

            }
        };timer.schedule(myTask, 100 , 20000 ); //refresh after
//...............................end of banner ad ........................
    }

    private void initListener() {
        //notification view click listener
        mNotificationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtilities.getInstance().invokeNewActivity(mActivity, NotificationListActivity.class, false);
            }
        });

        // Search button click listener
        mImgBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityUtilities.getInstance().invokeNewActivity(mActivity, SearchActivity.class, false);
            }
        });

        mCategoryAdapter.setItemClickListener(new ListItemClickListener() {
            @Override
            public void onItemClick(final int position, View view) {
                final Category model = mHomeCategoryList.get(position);
                switch (view.getId()) {
                    case R.id.card_view:
                        ActivityUtilities.getInstance().invokeItemListActiviy(mActivity, ItemListActivity.class, model.getDetails(), false);
                        break;
                    default:
                        break;
                }
            }
        });

        mAuthorAdapter.setItemClickListener(new ListItemClickListener() {
            @Override
            public void onItemClick(final int position, View view) {
                final Author model = mHomeAuthorList.get(position);
                switch (view.getId()) {
                    case R.id.card_view:
                        ActivityUtilities.getInstance().invokeItemListActiviy(mActivity, ItemListActivity.class, model.getDetails(), false);
                        break;
                    default:
                        break;
                }
            }
        });

        // See All Random Quotes click listener
        mSeeAllRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityUtilities.getInstance().invokeNewActivity(mActivity, AllQuoteActivity.class, false);
            }
        });

        // See All Category Quotes click listener
        mSeeAllCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityUtilities.getInstance().invokeNewActivity(mActivity, AllCategoryActivity.class, false);
            }
        });

        // See All Author Quotes click listener
        mSeeAllAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityUtilities.getInstance().invokeNewActivity(mActivity, AllAuthorActivity.class, false);
            }
        });
    }

    private void loadData() {
        showLoader();
        loadCategoryQuotes();

        // show banner ads
        AdsUtilities.getInstance(mContext).showBannerAd((AdView) findViewById(R.id.adsView));
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

                String categoryTitle = jsonObj.getString(AppConstant.JSON_KEY_CATEGORY_NAME);
                String categoryImage = jsonObj.getString(AppConstant.JSON_KEY_CATEGORY_IMAGE);

                ArrayList<String> quoteList = new ArrayList<>();
                JSONArray jsonArray2 = jsonObj.getJSONArray(AppConstant.JSON_KEY_QUOTES);
                for (int j = 0; j < jsonArray2.length(); j++) {
                    String details = jsonArray2.get(j).toString();
                    quoteList.add(details);
                    mAllRandomQuotes.add(details);
                }
                mCategoryList.add(new Category(categoryTitle, categoryImage, quoteList));
            }

            Collections.shuffle(mCategoryList);

            int maxLoop;
            if (AppConstant.BUNDLE_KEY_HOME_INDEX > mCategoryList.size()) {
                maxLoop = mCategoryList.size();
            } else {
                maxLoop = AppConstant.BUNDLE_KEY_HOME_INDEX;
            }
            for (int i = 0; i < maxLoop; i++) {
                mHomeCategoryList.add(mCategoryList.get(i));
            }
            mCategoryAdapter.notifyDataSetChanged();

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

                String authorTitle = jsonObj.getString(AppConstant.JSON_KEY_AUTHOR_NAME);
                String authorImage = jsonObj.getString(AppConstant.JSON_KEY_AUTHOR_IMAGE);

                ArrayList<String> quoteList = new ArrayList<>();
                JSONArray jsonArray2 = jsonObj.getJSONArray(AppConstant.JSON_KEY_QUOTES);
                for (int j = 0; j < jsonArray2.length(); j++) {
                    String details = jsonArray2.get(j).toString();
                    quoteList.add(details);
                    mAllRandomQuotes.add(details);
                }
                mAuthorList.add(new Author(authorTitle, authorImage, quoteList));
            }

            Collections.shuffle(mAuthorList);
            Collections.shuffle(mAllRandomQuotes);


            int maxLoop;
            if (AppConstant.BUNDLE_KEY_HOME_INDEX > mAuthorList.size()) {
                maxLoop = mAuthorList.size();
            } else {
                maxLoop = AppConstant.BUNDLE_KEY_HOME_INDEX;
            }
            for (int i = 0; i < maxLoop; i++) {
                mHomeAuthorList.add(mAuthorList.get(i));
            }

            mAuthorAdapter.notifyDataSetChanged();

            setRandomQuotePager();

            hideLoader();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void setRandomQuotePager() {
        // random quote pager adapter
        mRandomAdapter = new RandomPagerAdapter(mActivity, mAllRandomQuotes);
        mPagerRandomQuotes.setAdapter(mRandomAdapter);

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                int setPosition = mPagerRandomQuotes.getCurrentItem() + 1;
                if (setPosition == AppConstant.BUNDLE_KEY_MAX_INDEX) {
                    setPosition = AppConstant.BUNDLE_KEY_ZERO_INDEX;
                }
                mPagerRandomQuotes.setCurrentItem(setPosition, true);
            }
        };

        //  Auto animated timer
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 10000, 10000);

    }


    public void initNotification() {
        NotificationDbController notificationDbController = new NotificationDbController(mContext);
        TextView notificationCount = (TextView) findViewById(R.id.notificationCount);
        notificationCount.setVisibility(View.INVISIBLE);

        ArrayList<NotificationModel> notiArrayList = notificationDbController.getUnreadData();

        if (notiArrayList != null && !notiArrayList.isEmpty()) {
            int totalUnread = notiArrayList.size();
            if (totalUnread > 0) {
                notificationCount.setVisibility(View.VISIBLE);
                notificationCount.setText(String.valueOf(totalUnread));
            } else {
                notificationCount.setVisibility(View.INVISIBLE);
            }
        }

    }

}
