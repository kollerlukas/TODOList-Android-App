package us.koller.todolist.Widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.widget.RemoteViews;

import us.koller.todolist.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import us.koller.todolist.Activities.MainActivity;
import us.koller.todolist.Util.ThemeHelper;

/**
 * Created by Lukas on 20.08.2016.
 */
public class WidgetProvider_List extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){
        ThemeHelper helper = new ThemeHelper(context);

        PendingIntent clickPI = PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class)
                        .setAction("START_MAIN_ACTIVITY")
                        .setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT
                                | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT),
                PendingIntent.FLAG_UPDATE_CURRENT);

        for (int i = 0; i < appWidgetIds.length; i++) {
            Intent svcIntent = new Intent(context, WidgetService_List.class);

            svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews widget = new RemoteViews(context.getPackageName(), R.layout.widget_list);
            widget.setRemoteAdapter(R.id.widget_list, svcIntent);

            //color toolbar textView
            colorBackground(widget, R.id.toolbar_textView, helper.get("toolbar_color"));
            widget.setTextColor(R.id.toolbar_textView, helper.get("toolbar_textcolor"));
            widget.setOnClickPendingIntent(R.id.toolbar_textView, clickPI);

            //color background
            colorBackground(widget, R.id.bg, helper.get("cord_color"));

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
            Method m = c.getMethod("setDrawableParameters", int.class, boolean.class, int.class,
                    int.class, PorterDuff.Mode.class, int.class);
            m.invoke(widget, id, true, -1, color, PorterDuff.Mode.SRC_IN, -1);
        } catch (ClassNotFoundException | NoSuchMethodException
                | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
