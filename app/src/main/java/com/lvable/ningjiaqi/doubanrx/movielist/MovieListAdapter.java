package com.lvable.ningjiaqi.doubanrx.movielist;


import android.content.Context;
import android.content.pm.ProviderInfo;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
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

    public static final int STATUS_ERROR = 1;
    public static final int STATUS_LOADING = 2;
    public static final int STATUS_NO_MORE = 3;
    private int mFooterStatus = 2;
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

            vh.mIvPoster.setImageURI(Uri.parse(mData.get(i).imgUrl));
            // TODO: 16/4/29 add load image
        } else {
            FooterViewHolder vh = (FooterViewHolder) viewHolder;
            switch (mFooterStatus){
                case STATUS_LOADING:
                    vh.mLoadingFooter.setVisibility(View.VISIBLE);
                    vh.mNoMoreFooter.setVisibility(View.INVISIBLE);
                    vh.mErrorFooter.setVisibility(View.INVISIBLE);
                    vh.mLoadingView.startLoading();
                    break;
                case STATUS_ERROR:
                    vh.mLoadingView.stopLoading();
                    vh.mErrorFooter.setVisibility(View.VISIBLE);
                    vh.mNoMoreFooter.setVisibility(View.INVISIBLE);
                    vh.mLoadingFooter.setVisibility(View.INVISIBLE);
                    break;
                case STATUS_NO_MORE:
                    vh.mErrorFooter.setVisibility(View.INVISIBLE);
                    vh.mNoMoreFooter.setVisibility(View.VISIBLE);
                    vh.mLoadingFooter.setVisibility(View.INVISIBLE);
                    break;
            }
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
        SimpleDraweeView mIvPoster;

        public ItemViewHolder(View itemView) {
            super(itemView);

            mRoot = itemView.findViewById(R.id.root);
            mIvPoster = (SimpleDraweeView) itemView.findViewById(R.id.iv_movie_poster);
            mTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mDirector = (TextView) itemView.findViewById(R.id.tv_director);
            mOrder = (TextView) itemView.findViewById(R.id.tv_order);
            mRatingBar = (RatingBar) itemView.findViewById(R.id.rating_bar);
        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {
        PolyLoadingView mLoadingView;
        View mLoadingFooter;
        View mErrorFooter;
        View mNoMoreFooter;

        public FooterViewHolder(View itemView) {
            super(itemView);
            mLoadingView = (PolyLoadingView) itemView.findViewById(R.id.poly_loading);
            mLoadingFooter = itemView.findViewById(R.id.loading_footer);
            mErrorFooter = itemView.findViewById(R.id.error_footer);
            mNoMoreFooter = itemView.findViewById(R.id.no_more_footer);
        }
    }

    public void setOnClickListener(OnItemClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
    }

    public void setFooterStatus(int status) {
        this.mFooterStatus = status;
        notifyDataSetChanged();
    }
}

