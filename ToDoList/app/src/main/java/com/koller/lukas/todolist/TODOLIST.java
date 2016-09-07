package com.koller.lukas.todolist;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;

/**
 * Write a description of class TODOLIST here.
 * TODOLIST manages your EVENTs.
 *
 * @author (Lukas Koller)
 * @version (1.0)
 */
public class TODOLIST {
    private ArrayList<EVENT> todolist;
    private SharedPreferences sharedpreferences;
    private EVENT LastRemovedEvent;
    private int LastRemovedEventPosition;
    private boolean vibrate;
    private boolean showNotification;
    public long todolistTimeStamp = 0;
    public long lastSyncTimeStamp = 0;

    public TODOLIST() {
        todolist = new ArrayList<>();
    }

    public ArrayList<EVENT> getTodolist() {
        return todolist;
    }

    public void addEvent(RVAdapter mAdapter, EVENT e) {
        todolist.add(e);
        mAdapter.addItem(e);
    }

    public void addEvent(EVENT e, int index, RVAdapter mAdapter) {
        if (index < todolist.size()) {
            todolist.add(index, e);
        } else {
            todolist.add(e);
        }
        int adapterIndex = getAdapterListPosition(mAdapter, e);
        if (adapterIndex < mAdapter.getList().size()) {
            mAdapter.addItem(adapterIndex, e);
        } else {
            mAdapter.addItem(e);
        }
    }

    public void restoreLastRemovedEvent() {
        if (LastRemovedEvent != null) {
            todolist.add(LastRemovedEventPosition, LastRemovedEvent);
            LastRemovedEvent = null;
        }
    }

    public void removeEvent(EVENT e) {
        //Only for removing Event that are not in the Adapter-List!!!!
        LastRemovedEventPosition = todolist.indexOf(e);
        LastRemovedEvent = e;
        todolist.remove(e);
    }

    public void removeEvent(RVAdapter mAdapter, int index) {
        EVENT e = mAdapter.getList().get(index);
        LastRemovedEventPosition = todolist.indexOf(e);
        LastRemovedEvent = e;
        todolist.remove(e);
        mAdapter.removeItem(index);
    }

    public int getIndexOfEventInAdapterListById(RVAdapter mAdapter, long id) {
        for (int i = 0; i < mAdapter.getList().size(); i++) {
            if (mAdapter.getList().get(i).getId() == id) {
                return i;
            }
        }
        return todolist.size();
    }

    public EVENT getEventById(long id) {
        for (int i = 0; i < todolist.size(); i++) {
            EVENT e = todolist.get(i);
            if (e.getId() == id) {
                return e;
            }
        }
        return null;
    }

    public boolean hasAlarmFired(EVENT e) {
        return e.getAlarmTimeInMills() < System.currentTimeMillis();
    }


    public EVENT getLastRemovedEvent() {
        return LastRemovedEvent;
    }

    public boolean isAlarmScheduled() {
        for (int i = 0; i < todolist.size(); i++) {
            if (todolist.get(i).hasAlarm() && !hasAlarmFired((todolist.get(i)))) {
                return true;
            }
        }
        return false;
    }

    public ArrayList initAdapterList(boolean[] selected_categories) {
        ArrayList<EVENT> adapter_list = new ArrayList<>();
        for (int i = 0; i < todolist.size(); i++) {
            if (selected_categories[todolist.get(i).getColor()]) {
                adapter_list.add(todolist.get(i));
            }
        }
        return adapter_list;
    }

    public void addOrRemoveEventFromAdapter(RVAdapter mAdapter, boolean[] selected_categories) {
        EVENT[] temp_adapter_list = new EVENT[mAdapter.getList().size()]; //Needed for running through the whole mAdapterlist
        for (int i = 0; i < mAdapter.getList().size(); i++) {
            temp_adapter_list[i] = mAdapter.getList().get(i);
        }
        for (int i = temp_adapter_list.length - 1; i >= 0; i--) {
            if (!selected_categories[temp_adapter_list[i].getColor()]) {
                mAdapter.removeItem(mAdapter.getList().indexOf(temp_adapter_list[i]));
                Log.d("Removed", String.valueOf(i));
            }
        }
        for (int i = 0; i < todolist.size(); i++) {
            if (selected_categories[todolist.get(i).getColor()] && !isEventInAdapterList(mAdapter, todolist.get(i))) {
                int index = getAdapterListPosition(mAdapter, todolist.get(i), selected_categories);
                mAdapter.addItem(index, todolist.get(i));
            }
        }
    }

    public void addAllEventToAdapterList(RVAdapter mAdapter) {
        for (int i = 0; i < todolist.size(); i++) {
            if (!isEventInAdapterList(mAdapter, todolist.get(i))) {
                int index = todolist.indexOf(todolist.get(i));
                mAdapter.addItem(index, todolist.get(i));
            }
        }
    }

    public void resetAllSemiTransparentEvents(RVAdapter mAdapter) {
        EVENT[] temp_adapter_list = new EVENT[mAdapter.getList().size()]; //Needed for running through the whole mAdapterlist
        for (int i = 0; i < mAdapter.getList().size(); i++) {
            temp_adapter_list[i] = mAdapter.getList().get(i);
        }
        for (int i = 0; i < temp_adapter_list.length; i++) {
            if (temp_adapter_list[i].semi_transparent) {
                mAdapter.removeItem(mAdapter.getList().indexOf(temp_adapter_list[i]));
            }
        }
        //resetting all Events
        for (int i = 0; i < todolist.size(); i++) {
            todolist.get(i).semi_transparent = false;
        }
    }

    public void setEventsSemiTransparent(RVAdapter mAdapter) {
        for (int i = 0; i < todolist.size(); i++) {
            if (!isEventInAdapterList(mAdapter, todolist.get(i))) {
                todolist.get(i).semi_transparent = true;
            }
        }
    }

    public boolean isEventInAdapterList(RVAdapter mAdapter, EVENT e) {
        for (int i = 0; i < mAdapter.getList().size(); i++) {
            if (e.getId() == mAdapter.getList().get(i).getId()) {
                return true;
            }
        }
        return false;
    }

    public int getAdapterListPosition(RVAdapter mAdapter, EVENT e, boolean[] selected_categories) {
        int adapter_list_position = 0;
        for (int i = 0; i < todolist.size(); i++) {
            if (todolist.get(i).getId() == e.getId()) {
                return adapter_list_position;
            }
            if (selected_categories[todolist.get(i).getColor()]) {
                adapter_list_position++;
            }
        }
        return mAdapter.getList().size();
    }

    public int getAdapterListPosition(RVAdapter mAdapter, EVENT e) {
        for (int i = 0; i < mAdapter.getList().size(); i++) {
            if (mAdapter.getList().get(i).getId() == e.getId()) {
                return i;
            }
        }
        return mAdapter.getList().size();
    }

    public boolean isAdapterListTodolist(RVAdapter mAdapter) {
        if (mAdapter.getList().size() != todolist.size()) {
            return false;
        }
        for (int i = 0; i < todolist.size(); i++) {
            if (mAdapter.getList().get(i).getId() != todolist.get(i).getId()) {
                return false;
            }
        }
        return true;
    }

    public void EventMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(todolist, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(todolist, i, i - 1);
            }
        }
    }

    public boolean doesCategoryContainEvents(int category_index) {
        for (int i = 0; i < todolist.size(); i++) {
            if (todolist.get(i).getColor() == category_index) {
                return true;
            }
        }
        return false;
    }

    public void saveSettings(Context context) {
        sharedpreferences = context.getSharedPreferences("todolist", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean("showNotification", showNotification);
        editor.putBoolean("vibrate", vibrate);
        editor.putLong("todolistTimeStamp", todolistTimeStamp);
        editor.putLong("lastSyncTimeStamp", lastSyncTimeStamp);
        editor.apply();
    }

    public void saveCategorySettings(Context context, boolean[] selected_categories) {
        sharedpreferences = context.getSharedPreferences("todolist", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt("selected_categories.length", selected_categories.length);
        for (int i = 0; i < selected_categories.length; i++) {
            editor.putBoolean("category" + i + "selected", selected_categories[i]);
        }
        editor.apply();
    }

    public void readCategorySettings(Context context, MainActivity mainActivity) {
        sharedpreferences = context.getSharedPreferences("todolist", Context.MODE_PRIVATE);
        boolean[] selected_categories = new boolean[13];
        for (int i = 0; i < sharedpreferences.getInt("selected_categories.length", 0); i++) {
            selected_categories[i] = sharedpreferences.getBoolean("category" + i + "selected", false);
        }
        mainActivity.setSelectedCategories(selected_categories);
    }

    public void saveData(Context context) throws JSONException {
        try {
            FileOutputStream fos = context.openFileOutput("events", Context.MODE_PRIVATE);
            fos.write(getData().getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getData() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("todolist.size()", todolist.size());
        json.put("timeStamp", System.currentTimeMillis());
        for (int i = 0; i < todolist.size(); i++) {
            EVENT e = todolist.get(i);
            json.put(i + "Id", e.getId());
            json.put(i + "WhatToDo", e.getWhatToDo());
            json.put(i + "Color", e.getColor());
            json.put(i + "AlarmTime", e.getAlarmTimeInMills());
            json.put(i + "AlarmId", e.getAlarmId());
            json.put(i + "timeStamp", e.getTimeStamp());
        }
        return json.toString();
    }

    public String getDataWithColors(ThemeHelper helper) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("todolist.size()", todolist.size());
        json.put("timeStamp", System.currentTimeMillis());
        for (int i = 0; i < todolist.size(); i++) {
            EVENT e = todolist.get(i);
            json.put(i + "Id", e.getId());
            json.put(i + "WhatToDo", e.getWhatToDo());
            json.put(i + "Color", e.getColor());
            json.put(i + "AlarmTime", e.getAlarmTimeInMills());
            json.put(i + "AlarmId", e.getAlarmId());
            json.put(i + "timeStamp", e.getTimeStamp());
        }
        for (int i = 1; i < 13; i++){
            json.put("color" + i, helper.getEventColor(i));
            json.put("textcolor" + i, helper.getEventTextColor(i));
        }
        return json.toString();
    }

    public void readSettings(Context context) {
        sharedpreferences = context.getSharedPreferences("todolist", Context.MODE_PRIVATE);
        showNotification = sharedpreferences.getBoolean("showNotification", true);
        vibrate = sharedpreferences.getBoolean("vibrate", true);
        todolistTimeStamp = sharedpreferences.getLong("todolistTimeStamp", 0);
        lastSyncTimeStamp = sharedpreferences.getLong("lastSyncTimeStamp", 0);;
    }

    public void readData(Context context) throws JSONException, FileNotFoundException {
        StringBuilder sb = new StringBuilder();
        try {
            FileInputStream fis = context.openFileInput("events");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line;
            if (fis != null) {
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
            }
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject json = new JSONObject(sb.toString());
        for (int i = 0; i < json.getInt("todolist.size()"); i++) {
            long timeStamp;
            try{
                timeStamp = json.getLong(i + "timeStamp");
            } catch (JSONException exception){
                timeStamp = 0;
            }
            EVENT e = new EVENT(
                    json.getString(i + "WhatToDo"),
                    json.getInt(i + "Color"),
                    json.getLong(i + "Id"), null,
                    timeStamp);
            if (json.getLong(i + "AlarmTime") < System.currentTimeMillis()) {
                e.updateAlarm(0, 0);
            } else {
                e.updateAlarm(json.getLong(i + "AlarmId"), json.getLong(i + "AlarmTime"));
            }
            todolist.add(e);
        }
    }

    public void saveDefaultColor(int default_color, Context context) {
        sharedpreferences = context.getSharedPreferences("todolist", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt("default_color", default_color);
        editor.apply();
    }

    public int readDefaultColor(Context context) {
        sharedpreferences = context.getSharedPreferences("todolist", Context.MODE_PRIVATE);
        return sharedpreferences.getInt("default_color", 0);
    }

    public boolean getShowNotification() {
        return showNotification;
    }

    public void setShowNotification(boolean b) {
        showNotification = b;
    }

    public void setVibrate(boolean b) {
        vibrate = b;
    }

    public boolean getVibrate() {
        return vibrate;
    }

    public void saveMoveTimeStamp(Context context){
        sharedpreferences = context.getSharedPreferences("todolist", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        long moveTimeStamp = System.currentTimeMillis();
        editor.putLong("moveTimeStamp", moveTimeStamp);
        editor.apply();
    }

    public void saveEventAddedInSyncTimeStamp(Context context){
        sharedpreferences = context.getSharedPreferences("todolist", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        long eventAddedInSyncTimeStamp = System.currentTimeMillis();
        editor.putLong("eventAddedInSyncTimeStamp", eventAddedInSyncTimeStamp);
        editor.apply();
    }

    public boolean hasSomethingChanged(Context context){
        for (int i = 0; i < todolist.size(); i++){
            if(todolist.get(i).getTimeStamp() > lastSyncTimeStamp){
                return true;
            }
        }
        if(context.getSharedPreferences("todolist", Context.MODE_PRIVATE).getLong("moveTimeStamp", 0) > lastSyncTimeStamp){
            return true;
        }
        return false;
    }
}