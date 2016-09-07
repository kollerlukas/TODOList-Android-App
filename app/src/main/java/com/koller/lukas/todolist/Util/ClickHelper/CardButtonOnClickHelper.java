package com.koller.lukas.todolist.Util.ClickHelper;

import android.view.View;

import com.koller.lukas.todolist.Activities.MainActivity;
import com.koller.lukas.todolist.Todolist.Event;
import com.koller.lukas.todolist.Util.Callbacks.CardButtonOnClickInterface;

/**
 * Created by Lukas on 09.03.2016.
 */
public class CardButtonOnClickHelper implements CardButtonOnClickInterface {

    private MainActivity mainActivity;

    public CardButtonOnClickHelper(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    @Override
    public void actionButtonClicked(View v, Event e) {
        mainActivity.actionButtonClicked(v, e);
    }

    @Override
    public void scrollToCard(int position){
        mainActivity.scrollToCard(position);
    }
}
