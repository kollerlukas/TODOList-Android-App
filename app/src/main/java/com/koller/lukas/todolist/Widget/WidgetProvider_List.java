package com.koller.lukas.todolist.Widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.widget.RemoteViews;

import com.koller.lukas.todolist.Activities.MainActivity;
import com.koller.lukas.todolist.R;
import com.koller.lukas.todolist.Util.ThemeHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Lukas on 20.08.2016.
 */
public class WidgetProvider_List extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){
        ThemeHelper helper = new ThemeHelper(context);

        PendingIntent clickPI = PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class).setAction("START_MAIN_ACTIVITY"),
                PendingIntent.FLAG_UPDATE_CURRENT);

        for (int i = 0; i < appWidgetIds.length; i++) {
            Intent svcIntent = new Intent(context, WidgetService_List.class);

            svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews widget = new RemoteViews(context.getPackageName(), R.layout.widget_list);
            widget.setRemoteAdapter(R.id.widget_list, svcIntent);

            //color toolbar textView
            colorBackground(widget, R.id.toolbar_textView, helper.toolbar_color);
            widget.setTextColor(R.id.toolbar_textView, helper.toolbar_textcolor);
            widget.setOnClickPendingIntent(R.id.toolbar_textView, clickPI);

            //color background
            colorBackground(widget, R.id.bg, helper.cord_color);

            //read new Data
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds[i], R.id.widget_list);

            //update Data
            appWidgetManager.updateAppWidget(appWidgetIds[i], widget);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    public void colorBackground(RemoteViews widget, int id, int color){
        try {
            Class c = Class.forName("android.widget.RemoteViews");
            Method m = c.getMethod("setDrawableParameters", new Class[] {int.class, boolean.class, int.class, int.class, PorterDuff.Mode.class, int.class});
            m.invoke(widget, new Object[]{id, true, -1, color, PorterDuff.Mode.SRC_IN, -1});
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
