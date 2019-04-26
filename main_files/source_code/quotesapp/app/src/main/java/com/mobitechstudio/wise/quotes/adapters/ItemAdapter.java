package com.mobitechstudio.wise.quotes.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mobitechstudio.wise.quotes.R;
import com.mobitechstudio.wise.quotes.listeners.ListItemClickListener;
import com.mobitechstudio.wise.quotes.models.item.ItemModel;

import java.util.ArrayList;
import java.util.Random;


public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private Context mContext;
    private Activity mActivity;

    private ArrayList<ItemModel> mItemList;
    private ListItemClickListener mItemClickListener;

    public ItemAdapter(Context mContext, Activity mActivity, ArrayList<ItemModel> mItemList) {
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.mItemList = mItemList;
    }

    public void setItemClickListener(ListItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }


    @Override
    public ItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_item_recycler, parent, false);
        return new ItemAdapter.ViewHolder(view, viewType, mItemClickListener);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private RelativeLayout lytContainer;
        private TextView tvTagText, tvTitleText;
        private ImageView imgMarker;
        private ListItemClickListener itemClickListener;


        public ViewHolder(View itemView, int viewType, ListItemClickListener itemClickListener) {
            super(itemView);

            this.itemClickListener = itemClickListener;
            // Find all views ids
            tvTagText = (TextView) itemView.findViewById(R.id.item_tag);
            tvTitleText = (TextView) itemView.findViewById(R.id.item_title);
            imgMarker = (ImageView) itemView.findViewById(R.id.img_marker);
            lytContainer = (RelativeLayout) itemView.findViewById(R.id.lyt_container);
            lytContainer.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(getLayoutPosition(), view);
            }
        }
    }

    @Override
    public int getItemCount() {
        return (null != mItemList ? mItemList.size() : 0);

    }


    @Override
    public void onBindViewHolder(ItemAdapter.ViewHolder mainHolder, int position) {

        // setting data over views
        String title = mItemList.get(position).getQuoteText();
        mainHolder.tvTitleText.setText(Html.fromHtml(title));
        mainHolder.tvTagText.setText(String.valueOf(position + 1));


        Random rand = new Random();
        int i = rand.nextInt(6) + 1;


        switch (i) {
            case 1:
                mainHolder.tvTagText.setBackground(ContextCompat.getDrawable(mContext, R.drawable.circle_blue));
                break;
            case 2:
                mainHolder.tvTagText.setBackground(ContextCompat.getDrawable(mContext, R.drawable.circle_red));
                break;

            case 3:
                mainHolder.tvTagText.setBackground(ContextCompat.getDrawable(mContext, R.drawable.circle_orange));
                break;

            case 4:
                mainHolder.tvTagText.setBackground(ContextCompat.getDrawable(mContext, R.drawable.circle_purple));
                break;
            case 5:
                mainHolder.tvTagText.setBackground(ContextCompat.getDrawable(mContext, R.drawable.circle_green));
                break;
            case 6:
                mainHolder.tvTagText.setBackground(ContextCompat.getDrawable(mContext, R.drawable.circle_deep_blue));
                break;
            default:
                break;
        }

        if (mItemList.get(position).isMarkerSet()) {
            mainHolder.imgMarker.setVisibility(View.VISIBLE);
        } else {
            mainHolder.imgMarker.setVisibility(View.GONE);
        }
    }
}
