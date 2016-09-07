package com.koller.lukas.todolist;

import android.view.View;

/**
 * Created by Lukas on 09.03.2016.
 */
public interface CardButtonOnClickInterface {
    void actionButtonClicked(View v, EVENT e);
    void scrollToCard(int adapter_position);
}
