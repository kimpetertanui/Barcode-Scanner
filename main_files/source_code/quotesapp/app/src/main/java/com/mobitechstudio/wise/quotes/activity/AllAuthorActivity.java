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
import com.mobitechstudio.wise.quotes.adapters.AuthorAdapter;
import com.mobitechstudio.wise.quotes.data.constant.AppConstant;
import com.mobitechstudio.wise.quotes.listeners.ListItemClickListener;
import com.mobitechstudio.wise.quotes.models.quote.Author;
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

public class AllAuthorActivity extends BaseActivity {
    private Activity mActivity;
    private Context mContext;

    private ArrayList<Author> mAuthorList;
    private RecyclerView mRvAuthors;
    private AuthorAdapter mAuthorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVar();
        initView();
        initFunctionality();
        initListener();
    }

    private void initVar() {
        mActivity = AllAuthorActivity.this;
        mContext = mActivity.getApplicationContext();

        mAuthorList = new ArrayList<>();

    }

    private void initView() {
        setContentView(R.layout.activity_common_recycler);

        mRvAuthors = (RecyclerView) findViewById(R.id.rvCommon);
        mRvAuthors.setLayoutManager(new GridLayoutManager(mActivity, 2, GridLayoutManager.VERTICAL, false));
        mAuthorAdapter = new AuthorAdapter(mContext, mActivity, mAuthorList);
        mRvAuthors.setAdapter(mAuthorAdapter);

        initLoader();
        initToolbar(true);
        setToolbarTitle(getString(R.string.author_list));
        enableUpButton();
    }

    private void initFunctionality() {
        showLoader();

        loadAuthorQuotes();


        // show full-screen ads
        AdsUtilities.getInstance(mContext).showFullScreenAd();
        // show banner ads
        AdsUtilities.getInstance(mContext).showBannerAd((AdView) findViewById(R.id.adsView));
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
                }
                mAuthorList.add(new Author(authorTitle, authorImage, quoteList));
            }

            Collections.shuffle(mAuthorList);
            mAuthorAdapter.notifyDataSetChanged();

            hideLoader();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public void initListener() {
        mAuthorAdapter.setItemClickListener(new ListItemClickListener() {
            @Override
            public void onItemClick(final int position, View view) {
                final Author model = mAuthorList.get(position);
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
