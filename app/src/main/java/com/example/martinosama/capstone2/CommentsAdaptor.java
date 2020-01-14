package com.example.martinosama.capstone2;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


public class CommentsAdaptor extends RecyclerView.Adapter<CommentsAdaptor.ViewHolder> {

    private List<String> comments,time;
    private LayoutInflater mInflater;

    CommentsAdaptor(Context context, List<String> comments, List<String> time){
        this.comments = comments;
        this.time = time;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_comments, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int i) {
        holder.reply.setText(comments.get(i));
        holder.authorComment.setText(time.get(i));
    }
    public void setData(List<String> comments,List<String> time){
        this.comments = comments;
        this.time = time;
        notifyDataSetChanged();
    }



    @Override
    public int getItemCount() {
        return comments.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView reply,authorComment;
        ViewHolder(View itemView) {
            super(itemView);
            reply = itemView.findViewById(R.id.reply);
            authorComment = itemView.findViewById(R.id.authorComment);
        }

    }

}