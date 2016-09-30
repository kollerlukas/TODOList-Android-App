package us.koller.todolist.Util.Callbacks;

import android.support.v7.widget.RecyclerView;

/**
 * Created by Lukas on 11.03.2016.
 */
public interface OnItemClickInterface {
    void onItemClicked(RecyclerView recyclerView, int position, RecyclerView.ViewHolder holder);
}