package com.mobitechstudio.wise.quotes.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mobitechstudio.wise.quotes.R;
import com.mobitechstudio.wise.quotes.listeners.ListItemClickListener;
import com.mobitechstudio.wise.quotes.models.quote.Category;

import java.util.ArrayList;
import java.util.Random;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private Context mContext;
    private Activity mActivity;

    private ArrayList<Category> mItemList;
    private ListItemClickListener mItemClickListener;

    public CategoryAdapter(Context mContext, Activity mActivity, ArrayList<Category> mItemList) {
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.mItemList = mItemList;
    }

    public void setItemClickListener(ListItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }


    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_list, parent, false);
        return new CategoryAdapter.ViewHolder(view, viewType, mItemClickListener);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private CardView cardView;
        private TextView tvTitleText;
        private ImageView imgCategory;
        private ListItemClickListener itemClickListener;


        public ViewHolder(View itemView, int viewType, ListItemClickListener itemClickListener) {
            super(itemView);

            this.itemClickListener = itemClickListener;
            // Find all views ids
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            tvTitleText = (TextView) itemView.findViewById(R.id.category_title);
            imgCategory = (ImageView) itemView.findViewById(R.id.img);

            cardView.setOnClickListener(this);

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
    public void onBindViewHolder(CategoryAdapter.ViewHolder mainHolder, int position) {
        // setting data over views
        String title = mItemList.get(position).getTitle();
        mainHolder.tvTitleText.setText(title);


        String imgUrl = mItemList.get(position).getImage();
        if (imgUrl != null && !imgUrl.isEmpty()) {
            Glide.with(mContext)
                    .load(imgUrl)
                    .into(mainHolder.imgCategory);

            Random rand = new Random();
            int i = rand.nextInt(6) + 1;
            switch (i) {
                case 1:
                    mainHolder.tvTitleText.setBackground(ContextCompat.getDrawable(mContext, R.drawable.rectangle_blue_normal));
                    break;
                case 2:
                    mainHolder.tvTitleText.setBackground(ContextCompat.getDrawable(mContext, R.drawable.rectangle_deep_blue_normal));
                    break;
                case 3:
                    mainHolder.tvTitleText.setBackground(ContextCompat.getDrawable(mContext, R.drawable.rectangle_green_normal));
                    break;
                case 4:
                    mainHolder.tvTitleText.setBackground(ContextCompat.getDrawable(mContext, R.drawable.rectangle_orange_normal));
                    break;
                case 5:
                    mainHolder.tvTitleText.setBackground(ContextCompat.getDrawable(mContext, R.drawable.rectangle_purple_normal));
                    break;
                case 6:
                    mainHolder.tvTitleText.setBackground(ContextCompat.getDrawable(mContext, R.drawable.rectangle_red_normal));
                    break;

                default:
                    break;
            }
        }
    }
}
