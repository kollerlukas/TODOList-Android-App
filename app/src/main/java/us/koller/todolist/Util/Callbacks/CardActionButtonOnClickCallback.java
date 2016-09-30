package us.koller.todolist.Util.Callbacks;

import android.view.View;

import us.koller.todolist.Todolist.Event;

/**
 * Created by Lukas on 09.03.2016.
 */
public interface CardActionButtonOnClickCallback {
    void actionButtonClicked(View v, Event e);
    //void scrollToCard(int adapter_position);
}
