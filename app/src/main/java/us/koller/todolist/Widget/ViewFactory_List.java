package us.koller.todolist.Widget;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import us.koller.todolist.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import us.koller.todolist.Activities.MainActivity;
import us.koller.todolist.Todolist.Event;
import us.koller.todolist.Util.ThemeHelper;

/**
 * Created by Lukas on 20.08.2016.
 */
class ViewFactory_List implements RemoteViewsService.RemoteViewsFactory {
    private ArrayList<Event> events = new ArrayList<>();
    private Context context = null;
    private ThemeHelper helper;

    ViewFactory_List(Context context, Intent intent) {
        this.context = context;
        helper = new ThemeHelper(context);

        loadData();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.widget_list_row);

        int id = R.id.list_row_text;
        if(position < events.size()){
            int color_index = events.get(position).getColor();

            remoteView.setTextViewText(id, events.get(position).getWhatToDo());

            remoteView.setTextColor(id, helper.getEventTextColor(color_index));

            colorBackground(remoteView, id, helper.getEventColor(color_index));

            PendingIntent clickPI = PendingIntent.getActivity(context, 0,
                    new Intent(context, MainActivity.class)
                            .setAction("START_MAIN_ACTIVITY")
                            .setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT
                                    | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT),
                    PendingIntent.FLAG_UPDATE_CURRENT);

            remoteView.setOnClickPendingIntent(id, clickPI);
        }

        return remoteView;
    }

    private void colorBackground(RemoteViews widget, int id, int color){
        try {
            Class c = Class.forName("android.widget.RemoteViews");
            Method m = c.getMethod("setDrawableParameters", int.class, boolean.class, int.class,
                    int.class, PorterDuff.Mode.class, int.class);
            m.invoke(widget, id, true, -1, color, PorterDuff.Mode.SRC_IN, -1);
        } catch (ClassNotFoundException | NoSuchMethodException
                | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public void onCreate() {}

    @Override
    public void onDataSetChanged() {
        events.clear();

        loadData();
    }

    @Override
    public void onDestroy() {}

    private void loadData(){
        JSONArray array = readData();
        if(array == null){
            return;
        }

        for (int i = 0; i < array.length(); i++) {
            try {
                events.add(new Event(array.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private JSONArray readData(){
        StringBuilder sb = new StringBuilder();
        try {
            FileInputStream fis = context.openFileInput("events");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return new JSONArray(sb.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}