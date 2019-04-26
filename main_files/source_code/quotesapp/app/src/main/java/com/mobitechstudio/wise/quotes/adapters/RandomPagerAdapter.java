package com.mobitechstudio.wise.quotes.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobitechstudio.wise.quotes.R;
import com.mobitechstudio.wise.quotes.data.constant.AppConstant;

import java.util.ArrayList;

public class RandomPagerAdapter extends PagerAdapter {

    private Context mContext;

    private ArrayList<String> mItemList;
    private LayoutInflater inflater;

    public RandomPagerAdapter(Context mContext, ArrayList<String> mItemList) {
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
        return AppConstant.BUNDLE_KEY_MAX_INDEX;
    }


    @Override
    public Object instantiateItem(final ViewGroup view, final int position) {

        View rootView = inflater.inflate(R.layout.item_random_view_pager, view, false);

        final TextView titleTextView = (TextView) rootView.findViewById(R.id.txt_title);

        final String titleText = mItemList.get(position);
        titleTextView.setText(Html.fromHtml(titleText));

        view.addView(rootView);

        return rootView;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

}
