package com.koller.lukas.todolist.Util.Callbacks;

import android.view.View;

import com.koller.lukas.todolist.Todolist.Event;

/**
 * Created by Lukas on 09.03.2016.
 */
public interface CardButtonOnClickInterface {
    void actionButtonClicked(View v, Event e);
    void scrollToCard(int adapter_position);
}
