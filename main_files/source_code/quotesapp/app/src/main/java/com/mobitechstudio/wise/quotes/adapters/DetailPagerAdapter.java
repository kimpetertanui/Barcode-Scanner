package com.mobitechstudio.wise.quotes.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mobitechstudio.wise.quotes.R;
import com.mobitechstudio.wise.quotes.data.constant.AppConstant;
import com.mobitechstudio.wise.quotes.data.preference.AppPreference;

import java.util.ArrayList;
import java.util.Random;

public class DetailPagerAdapter extends PagerAdapter {

    private Context mContext;

    private ArrayList<String> mItemList;
    private LayoutInflater inflater;

    public DetailPagerAdapter(Context mContext, ArrayList<String> mItemList) {
        this.mContext = mContext;
        this.mItemList = mItemList;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return mItemList.size();
    }


    @Override
    public Object instantiateItem(final ViewGroup view, final int position) {

        View rootView = inflater.inflate(R.layout.item_detail_view_pager, view, false);

        final TextView titleTextView = (TextView) rootView.findViewById(R.id.txt_title);
        final RelativeLayout lytContainer = (RelativeLayout) rootView.findViewById(R.id.content_layout);

        if (AppPreference.getInstance(mContext).getTextSize().equals(mContext.getResources().getString(R.string.small_text))) {
            titleTextView.setTextSize(AppConstant.SMALL_TEXT);
        } else if (AppPreference.getInstance(mContext).getTextSize().equals(mContext.getResources().getString(R.string.default_text))) {
            titleTextView.setTextSize(AppConstant.NORMAL_TEXT);
        } else if (AppPreference.getInstance(mContext).getTextSize().equals(mContext.getResources().getString(R.string.large_text))) {
            titleTextView.setTextSize(AppConstant.LARGE_TEXT);
        }

        final String titleText = mItemList.get(position);
        titleTextView.setText(Html.fromHtml(titleText));

        Random rand = new Random();
        int i = rand.nextInt(6) + 1;

        switch (i) {
            case 1:
                lytContainer.setBackground(ContextCompat.getDrawable(mContext, R.color.light_blue));
                break;
            case 2:
                lytContainer.setBackground(ContextCompat.getDrawable(mContext, R.color.light_dark_blue));
                break;
            case 3:
                lytContainer.setBackground(ContextCompat.getDrawable(mContext, R.color.light_green));
                break;
            case 4:
                lytContainer.setBackground(ContextCompat.getDrawable(mContext, R.color.light_orange));
                break;

            case 5:
                lytContainer.setBackground(ContextCompat.getDrawable(mContext, R.color.light_purple));
                break;
            case 6:
                lytContainer.setBackground(ContextCompat.getDrawable(mContext, R.color.light_red));
                break;

            default:
                break;
        }

        view.addView(rootView);

        return rootView;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

}
