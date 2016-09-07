package com.koller.lukas.todolist;

import android.app.Application;

/**
 * Created by Lukas on 03.08.2016.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        /*TypefaceUtil.overrideFont(getApplicationContext(),
                "SERIF", "fonts/Roboto-Regular.ttf"); // font from assets: "assets/fonts/Roboto-Regular.ttf*/
    }
}
