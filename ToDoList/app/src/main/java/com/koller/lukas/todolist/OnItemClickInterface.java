package com.koller.lukas.todolist;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Lukas on 11.03.2016.
 */
public interface OnItemClickInterface {
    void onItemClicked(RecyclerView recyclerView, int position, RecyclerView.ViewHolder holder);
}