package com.mobitechstudio.wise.quotes.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.mobitechstudio.wise.quotes.R;
import com.mobitechstudio.wise.quotes.adapters.DetailPagerAdapter;
import com.mobitechstudio.wise.quotes.data.constant.AppConstant;
import com.mobitechstudio.wise.quotes.data.sqlite.FavoriteDbController;
import com.mobitechstudio.wise.quotes.models.favorite.FavoriteModel;
import com.mobitechstudio.wise.quotes.utility.AdsUtilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AllQuoteActivity extends BaseActivity {
    private Activity mActivity;
    private Context mContext;
    private ArrayList<String> mAllQuotes;
    private ViewPager mViewPager;
    private DetailPagerAdapter mPagerAdapter = null;
    private TextView mTxtCounter;
    private ImageButton mBtnFavorite, mBtnShare, mBtnCopy;

    // Favourites view
    private List<FavoriteModel> mFavoriteList;
    private FavoriteDbController mFavoriteDbController;
    private boolean mIsFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVar();
        initView();
        initFunctionality();
        initListener();
    }

    private void initVar() {
        mActivity = AllQuoteActivity.this;
        mContext = mActivity.getApplicationContext();

        mFavoriteList = new ArrayList<>();
        mAllQuotes = new ArrayList<>();

    }

    private void initView() {
        setContentView(R.layout.activity_details);

        mViewPager = (ViewPager) findViewById(R.id.pager);

        mTxtCounter = (TextView) findViewById(R.id.menus_counter);
        mBtnFavorite = (ImageButton) findViewById(R.id.menus_fav);
        mBtnShare = (ImageButton) findViewById(R.id.menus_share);
        mBtnCopy = (ImageButton) findViewById(R.id.menus_copy);

        initLoader();
        initToolbar(true);
        setToolbarTitle(getString(R.string.details_text));
        enableUpButton();
    }

    private void initFunctionality() {
        showLoader();

        loadCategoryQuotes();

        mFavoriteDbController = new FavoriteDbController(mContext);
        mFavoriteList.addAll(mFavoriteDbController.getAllData());
        isFavorite();

        updateCounter();

        // load full screen ad
        AdsUtilities.getInstance(mContext).loadFullScreenAd(mActivity);
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

            Collections.shuffle(mAllQuotes);

            mPagerAdapter = new DetailPagerAdapter(mActivity, mAllQuotes);
            mViewPager.setAdapter(mPagerAdapter);

            hideLoader();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public void initListener() {
        mBtnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIsFavorite = !mIsFavorite;
                if (mIsFavorite) {
                    mFavoriteDbController.insertData(mAllQuotes.get(mViewPager.getCurrentItem()));
                    mFavoriteList.add(new FavoriteModel(AppConstant.BUNDLE_KEY_ZERO_INDEX, mAllQuotes.get(mViewPager.getCurrentItem())));
                    Toast.makeText(getApplicationContext(), getString(R.string.added_to_fav), Toast.LENGTH_SHORT).show();
                } else {
                    mFavoriteDbController.deleteEachFav(mAllQuotes.get(mViewPager.getCurrentItem()));
                    for (int i = 0; i < mFavoriteList.size(); i++) {
                        if (mFavoriteList.get(i).getDetails().equals(mAllQuotes.get(mViewPager.getCurrentItem()))) {
                            mFavoriteList.remove(i);
                            break;
                        }
                    }
                    Toast.makeText(getApplicationContext(), getString(R.string.removed_from_fav), Toast.LENGTH_SHORT).show();
                }
                setFavorite();
            }
        });

        mBtnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String appPackageName = mActivity.getPackageName();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(mAllQuotes.get(mViewPager.getCurrentItem()))
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
                android.content.ClipData clip = android.content.ClipData.newPlainText("Text Label", Html.fromHtml(mAllQuotes.get(mViewPager.getCurrentItem())));
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

                // show full-screen ads
                AdsUtilities.getInstance(mContext).showFullScreenAd();

                if (position % AppConstant.ADS_INTERVAL == 0) {
                    // load full screen ad
                    AdsUtilities.getInstance(mContext).loadFullScreenAd(mActivity);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void isFavorite() {
        for (int i = 0; i < mFavoriteList.size(); i++) {
            if (mFavoriteList.get(i).getDetails().equals(mAllQuotes.get(mViewPager.getCurrentItem()))) {
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

    public void updateCounter() {
        String counter = String.format(getString(R.string.item_counter), mViewPager.getCurrentItem() + AppConstant.BUNDLE_KEY_FIRST_INDEX, mAllQuotes.size());
        mTxtCounter.setText(counter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // show full-screen ads
                AdsUtilities.getInstance(mContext).showFullScreenAd();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // show full-screen ads
        AdsUtilities.getInstance(mContext).showFullScreenAd();
    }

}
