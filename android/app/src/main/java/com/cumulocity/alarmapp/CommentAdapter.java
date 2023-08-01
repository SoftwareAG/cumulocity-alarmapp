package com.cumulocity.alarmapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.cumulocity.alarmapp.databinding.CommentListItemBindingImpl;
import com.cumulocity.alarmapp.fragments.C8yComment;

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final C8yComment[] commentList;
    private CommentListItemBindingImpl commentListItemBinding;

    public CommentAdapter(C8yComment[] commentList) {
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        commentListItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.comment_list_item, parent, false);
        return new ViewHolder(commentListItemBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        C8yComment c8yComment = commentList[position];
        commentListItemBinding.setVariable(BR.comment, c8yComment);
    }

    @Override
    public int getItemCount() {
        return commentList == null ? 0 : commentList.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(View v) {
            super(v);
        }
    }
}
