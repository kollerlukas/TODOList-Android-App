package us.koller.todolist.Widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import us.koller.todolist.R;

import us.koller.todolist.Activities.MainActivity;

/**
 * Created by Lukas on 29.02.2016.
 */
public class WidgetProvider_add extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.fab_widget);
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT
                | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.setAction(MainActivity.ADD_EVENT);
        PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        remoteViews.setOnClickPendingIntent(R.id.widget_button, configPendingIntent);
        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
    }
}
