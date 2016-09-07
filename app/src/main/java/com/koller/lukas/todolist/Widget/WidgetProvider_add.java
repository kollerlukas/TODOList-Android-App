package com.koller.lukas.todolist.Widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.koller.lukas.todolist.Activities.MainActivity;
import com.koller.lukas.todolist.R;

/**
 * Created by Lukas on 29.02.2016.
 */
public class WidgetProvider_add extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.add_event_widget_layout);
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.setAction("widget_button");
        PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        remoteViews.setOnClickPendingIntent(R.id.widget_button, configPendingIntent);
        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
    }
}
