package com.example.martinosama.capstone2;

import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;


public class MainRecyclerViewAdaptor extends RecyclerView.Adapter<MainRecyclerViewAdaptor.ViewHolder> {

    private List<SubRedditInfo> subRedditInfos;
    private LayoutInflater mInflater;
    public ItemClickListener mClickListener;

    MainRecyclerViewAdaptor(Context context,List<SubRedditInfo> subRedditInfos){
        this.subRedditInfos = subRedditInfos;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_main, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int i) {
        holder.mSubreddit.setText(mInflater.getContext().getResources().getString(R.string.subreddit,subRedditInfos.get(i).getSubreddit()));
        holder.mTextView.setText(subRedditInfos.get(i).getTitle());
        holder.mAuthor.setText(mInflater.getContext().getResources().getString(R.string.posted_by,subRedditInfos.get(i).getAuthor(),TimeAgo.getTimeAgo(subRedditInfos.get(i).getCreatedUtc().longValue(),mInflater.getContext())));
        String URL = subRedditInfos.get(i).getThumbnail();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        ViewCompat.setTransitionName(holder.mCardRecycle,subRedditInfos.get(i).getTitle());
        if(URL.startsWith("http")) {
            holder.mImageView.setVisibility(View.VISIBLE);
            holder.mThumbnail.setVisibility(View.VISIBLE);
            GlideApp.with(mInflater.getContext()).load(URL).thumbnail(0.1f).centerCrop().placeholder(R.drawable.reddit).into(holder.mImageView);
        }
        else{
            GlideApp.with(mInflater.getContext()).clear(holder.mImageView);
            holder.mThumbnail.setVisibility(View.GONE);
            holder.mImageView.setVisibility(View.GONE);
        }
 }
    public void setDataOnline(List<SubRedditInfo> subRedditInfos){
        this.subRedditInfos = subRedditInfos;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return subRedditInfos.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView mImageView;
        TextView mTextView,mAuthor,mSubreddit;
        CardView mThumbnail,mCardRecycle;
        ViewHolder(View itemView) {
            super(itemView);
            mImageView =  itemView.findViewById(R.id.mainRecyclerImage);
            mTextView = itemView.findViewById(R.id.recyclerPostTitle);
            mAuthor = itemView.findViewById(R.id.author);
            mSubreddit = itemView.findViewById(R.id.subreddit);
            mThumbnail = itemView.findViewById(R.id.cardViewThumbnail);
            mCardRecycle = itemView.findViewById(R.id.cardRecycle);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mImageView =  itemView.findViewById(R.id.mainRecyclerImage);
            mTextView = itemView.findViewById(R.id.recyclerPostTitle);
            mAuthor = itemView.findViewById(R.id.author);
            mSubreddit = itemView.findViewById(R.id.subreddit);
            mThumbnail = itemView.findViewById(R.id.cardViewThumbnail);
            mCardRecycle = itemView.findViewById(R.id.cardRecycle);
             mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    String getItem(int id) {
        return subRedditInfos.get(id).getPermalink();
    }

    void setClickListener(final ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}