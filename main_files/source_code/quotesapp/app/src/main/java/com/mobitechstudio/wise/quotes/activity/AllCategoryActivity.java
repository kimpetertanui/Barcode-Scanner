package com.mobitechstudio.wise.quotes.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.ads.AdView;
import com.mobitechstudio.wise.quotes.R;
import com.mobitechstudio.wise.quotes.adapters.CategoryAdapter;
import com.mobitechstudio.wise.quotes.data.constant.AppConstant;
import com.mobitechstudio.wise.quotes.listeners.ListItemClickListener;
import com.mobitechstudio.wise.quotes.models.quote.Category;
import com.mobitechstudio.wise.quotes.utility.ActivityUtilities;
import com.mobitechstudio.wise.quotes.utility.AdsUtilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

public class AllCategoryActivity extends BaseActivity {
    private Activity mActivity;
    private Context mContext;

    private ArrayList<Category> mCategoryList;
    private RecyclerView mRvCategories;
    private CategoryAdapter mCategoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVar();
        initView();
        initFunctionality();
        initListener();
    }

    private void initVar() {
        mActivity = AllCategoryActivity.this;
        mContext = mActivity.getApplicationContext();

        mCategoryList = new ArrayList<>();

    }

    private void initView() {
        setContentView(R.layout.activity_common_recycler);

        mRvCategories = (RecyclerView) findViewById(R.id.rvCommon);
        mRvCategories.setLayoutManager(new GridLayoutManager(mActivity, 2, GridLayoutManager.VERTICAL, false));
        mCategoryAdapter = new CategoryAdapter(mContext, mActivity, mCategoryList);
        mRvCategories.setAdapter(mCategoryAdapter);

        initLoader();
        initToolbar(true);
        setToolbarTitle(getString(R.string.category_list));
        enableUpButton();
    }

    private void initFunctionality() {
        showLoader();

        loadCategoryQuotes();


        // show full-screen ads
        AdsUtilities.getInstance(mContext).showFullScreenAd();
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
                }
                mCategoryList.add(new Category(categoryTitle, categoryImage, quoteList));
            }

            Collections.shuffle(mCategoryList);

            mCategoryAdapter.notifyDataSetChanged();
            hideLoader();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void initListener() {
        mCategoryAdapter.setItemClickListener(new ListItemClickListener() {
            @Override
            public void onItemClick(final int position, View view) {
                final Category model = mCategoryList.get(position);
                switch (view.getId()) {
                    case R.id.card_view:
                        ActivityUtilities.getInstance().invokeItemListActiviy(mActivity, ItemListActivity.class, model.getDetails(), false);
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // load full screen ad
        AdsUtilities.getInstance(mContext).loadFullScreenAd(mActivity);
    }
}
