package com.koller.lukas.todolist.Util.Callbacks;

import com.koller.lukas.todolist.Todolist.Event;

/**
 * Created by Lukas on 15.08.2016.
 */
public interface ShareEventCallback {
    public void eventClicked(int index, Event e);
    public void shareEvents();
    public void cancel();
}
