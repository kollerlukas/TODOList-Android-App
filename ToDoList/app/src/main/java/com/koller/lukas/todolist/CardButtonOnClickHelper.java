package com.koller.lukas.todolist;

import android.view.View;

/**
 * Created by Lukas on 09.03.2016.
 */
public class CardButtonOnClickHelper implements CardButtonOnClickInterface {

    private MainActivity mainActivity;

    public CardButtonOnClickHelper(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    @Override
    public void actionButtonClicked(View v, EVENT e) {
        mainActivity.actionButtonClicked(v, e);
    }

    @Override
    public void scrollToCard(int position){
        mainActivity.scrollToCard(position);
    }
}
