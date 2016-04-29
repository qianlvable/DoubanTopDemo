package com.lvable.ningjiaqi.doubanrx.movielist;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lvable.ningjiaqi.doubanrx.R;
import com.lvable.ningjiaqi.doubanrx.UI.PolyLoadingView;
import com.lvable.ningjiaqi.doubanrx.data.Movie;

import java.util.List;


/**
 * Created by ningjiaqi on 16/4/26.
 */
public class MovieListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;

    private List<Movie> mData;
    private OnItemClickListener mOnClickListener;
    private Context mContext;
    public MovieListAdapter(Context context, List dataSet) {
        mData = dataSet;
        mContext = context;
    }

    public interface OnItemClickListener {
        void onClick(View view, Movie data);
    }

    @Override
    public int getItemViewType(int position) {
        if (position+1  == getItemCount()) return TYPE_FOOTER;

        return TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {

        if (type == TYPE_FOOTER) {
            View view = LayoutInflater.from(mContext)
                    .inflate(R.layout.loading_footer,viewGroup,false);
            return new FooterViewHolder(view);
        }else if (type == TYPE_ITEM){
            View view = LayoutInflater.from(mContext)
                    .inflate(R.layout.item_layout, viewGroup, false);
            return new ItemViewHolder(view);
        }else {
            return null;
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int i) {
        int type = getItemViewType(i);
        if (type == TYPE_ITEM) {
            ItemViewHolder vh = (ItemViewHolder) viewHolder;
            vh.mRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnClickListener != null)
                        mOnClickListener.onClick(vh.mIvPoster,mData.get(i));
                }
            });
            vh.mDirector.setText(mData.get(i).directors);
            vh.mOrder.setText("No." + (i + 1));
            vh.mRatingBar.setRating(mData.get(i).rating * 0.5f);
            vh.mTitle.setText(mData.get(i).title);
            Glide.with(mContext).load(mData.get(i).imgUrl)
                    .crossFade()
                    .centerCrop()
//                .transform(new TopCrop(mContext))
                    .into(vh.mIvPoster);
        } else {
            FooterViewHolder vh = (FooterViewHolder) viewHolder;
            vh.mLoadingView.startLoading();
        }

    }

    @Override
    public int getItemCount() {
        return mData.size() + 1;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        View mRoot;
        TextView mTitle;
        TextView mDirector;
        TextView mOrder;
        RatingBar mRatingBar;
        ImageView mIvPoster;

        public ItemViewHolder(View itemView) {
            super(itemView);

            mRoot = itemView.findViewById(R.id.root);
            mIvPoster = (ImageView) itemView.findViewById(R.id.iv_movie_poster);
            mTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mDirector = (TextView) itemView.findViewById(R.id.tv_director);
            mOrder = (TextView) itemView.findViewById(R.id.tv_order);
            mRatingBar = (RatingBar) itemView.findViewById(R.id.rating_bar);
        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {
        PolyLoadingView mLoadingView;

        public FooterViewHolder(View itemView) {
            super(itemView);
            mLoadingView = (PolyLoadingView) itemView.findViewById(R.id.poly_loading);
        }
    }

    public void setOnClickListener(OnItemClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
    }
}

