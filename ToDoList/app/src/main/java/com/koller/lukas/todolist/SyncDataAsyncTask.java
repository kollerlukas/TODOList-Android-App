package com.koller.lukas.todolist;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Lukas on 10.06.2016.
 */
public class SyncDataAsyncTask extends AsyncTask<Void, Void, String> {
    private TODOLIST todolist;
    private RVAdapter mAdapter;
    private String data;
    private SyncDataCallback syncDataCallback;
    private ArrayList<EVENT> driveList;
    private ArrayList<EVENT> driveList2;
    private ArrayList<EVENT> todolist2; // new List
    private ArrayList<EVENT> todolist3;
    private long driveListTimeStamp;
    private long eventAddedInSyncTimeStamp;
    private ArrayList<Integer> removeEvent_int;
    private ArrayList<EVENT> addEvent_Event;
    private ArrayList<Integer> addEvent_int;
    private ArrayList<EVENT> updateEvent_oldEvent;
    private ArrayList<EVENT> updateEvent_newEvent;
    private ArrayList<EVENT> moveEvent_Event;
    private ArrayList<Integer> moveEvent_to;
    private int[] newColors = new int[13];
    private int[] newTextColors = new int[13];
    private Context context;
    private ArrayList<EVENT> driveList_update;
    private ArrayList<EVENT> driveList_add;
    private SharedPreferences sharedPreferences;

    public SyncDataAsyncTask(TODOLIST todolist, RVAdapter mAdapter, String data, SyncDataCallback syncDataCallback, Context context) {
        this.todolist = todolist;
        this.mAdapter = mAdapter;
        this.data = data;
        this.syncDataCallback = syncDataCallback;
        removeEvent_int = new ArrayList<>();
        addEvent_Event = new ArrayList<>();
        addEvent_int = new ArrayList<>();
        updateEvent_oldEvent = new ArrayList<>();
        updateEvent_newEvent = new ArrayList<>();
        this.context = context;
        driveList_update = new ArrayList<>();
        driveList_add = new ArrayList<>();
        moveEvent_Event = new ArrayList<>();
        moveEvent_to = new ArrayList<>();
        sharedPreferences = context.getSharedPreferences("todolist", Context.MODE_PRIVATE);
        eventAddedInSyncTimeStamp = sharedPreferences.getLong("eventAddedInSyncTimeStamp", 0);
    }


    @Override
    protected String doInBackground(Void... params) {
        try {
            JSONObject json = new JSONObject(data);
            driveListTimeStamp = json.getLong("timeStamp");

            //Sync Colors
            for (int i = 1; i < 13; i++) {
                newColors[i] = json.getInt("color" + i);
                newTextColors[i] = json.getInt("textcolor" + i);
            }

            driveList = new ArrayList<>();
            driveList2 = new ArrayList<>();
            todolist2 = new ArrayList<>();
            todolist3 = new ArrayList<>();
            for (int i = 0; i < json.getInt("todolist.size()"); i++) {
                EVENT e = new EVENT(json.getString(i + "WhatToDo"), json.getInt(i + "Color"), json.getLong(i + "Id"), null, json.getLong(i + "timeStamp"));
                if (json.getLong(i + "AlarmTime") < System.currentTimeMillis()) {
                    e.updateAlarm(0, 0);
                } else {
                    e.updateAlarm(json.getInt(i + "AlarmId"), json.getLong(i + "AlarmTime"));
                }
                driveList.add(e);
            }
            for (int i = 0; i < driveList.size(); i++) {
                driveList2.add(driveList.get(i));
            }
            for (int i = 0; i < todolist.getTodolist().size(); i++) {
                todolist2.add(todolist.getTodolist().get(i));
            }
            for (int i = 0; i < todolist.getTodolist().size(); i++) {
                todolist3.add(todolist.getTodolist().get(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (todolist.todolistTimeStamp != 0 && driveList != null) {
            //update Events
            for (int i = 0; i < driveList.size(); i++) {
                for (int k = 0; k < todolist.getTodolist().size(); k++) {
                    if (driveList.get(i).getId() == todolist.getTodolist().get(k).getId()) {
                        driveList_update.add(driveList.get(i));
                        if (driveList.get(i).getTimeStamp() > todolist.getTodolist().get(k).getTimeStamp()) {
                            updateEvent_oldEvent.add(todolist.getTodolist().get(k));
                            updateEvent_newEvent.add(driveList.get(i));
                        }
                    }
                }
            }
            for (int i = 0; i < driveList_update.size(); i++) {
                driveList.remove(driveList_update.get(i));
                todolist3.remove(driveList_update.get(i));
            }

            //add Events
            for (int i = 0; i < driveList.size(); i++) {
                if (!isEventInTodoList(driveList.get(i), todolist3) && driveList.get(i).getId() > eventAddedInSyncTimeStamp) {
                    // new Event
                    addEvent_Event.add(driveList.get(i));
                    addEvent_int.add(driveList2.indexOf(driveList.get(i)));
                    if(driveList2.indexOf(driveList.get(i)) >= todolist2.size()){
                        todolist2.add(driveList.get(i));
                    } else {
                        todolist2.add(driveList2.indexOf(driveList.get(i)), driveList.get(i));
                    }
                    driveList_add.add(driveList.get(i));
                }
            }
            for (int i = 0; i < driveList_add.size(); i++) {
                driveList.remove(driveList_add.get(i));
                todolist3.remove(driveList_add.get(i));
            }

            //remove Events
            for (int i = 0; i < todolist3.size(); i++) {
                if (!isEventInTodoList(todolist3.get(i), driveList2) && todolist3.get(i).getId() < todolist.lastSyncTimeStamp) {
                    removeEvent_int.add(todolist.getAdapterListPosition(mAdapter, todolist3.get(i)));
                    todolist2.remove(todolist3.get(i));
                }
            }

            if (driveListTimeStamp > todolist.todolistTimeStamp) {
                //Events Moved
                if (driveList2.size() == todolist2.size()) {
                    for (int i = 0; i < todolist2.size(); i++) {
                        for (int k = 0; k < driveList2.size(); k++){
                            if (todolist2.get(i).getId() == driveList2.get(k).getId()){
                                if(i != k){
                                    moveEvent_Event.add(todolist2.get(i));
                                    moveEvent_to.add(k);
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // was never synced!! -> add all Events
            for (int i = 0; i < driveList.size(); i++) {
                if (!isEventInTodoList(driveList.get(i), todolist.getTodolist())) {
                    addEvent_Event.add(driveList.get(i));
                    addEvent_int.add(i);
                }
            }
        }
        return null;
    }

    public boolean isEventInTodoList(EVENT e, ArrayList<EVENT> todolist) {
        if (todolist == null) {
            return false;
        }
        for (int i = 0; i < todolist.size(); i++) {
            if (e.getId() == todolist.get(i).getId()) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onPostExecute(String result) {
        for (int i = 0; i < updateEvent_newEvent.size(); i++) {
            syncDataCallback.updateEvent(updateEvent_oldEvent.get(i), updateEvent_newEvent.get(i));
        }
        for (int i = 0; i < addEvent_Event.size(); i++) {
            syncDataCallback.addEvent(addEvent_Event.get(i), addEvent_int.get(i));
        }
        for (int i = 0; i < removeEvent_int.size(); i++) {
            syncDataCallback.removeEvent(removeEvent_int.get(i));
        }
        if (driveListTimeStamp > sharedPreferences.getLong("moveTimeStamp", 0)) {
            for (int i = 0; i < moveEvent_Event.size(); i++) {
                syncDataCallback.moveEvent(moveEvent_Event.get(i), moveEvent_to.get(i));
            }
        }
        if (driveListTimeStamp > sharedPreferences.getLong("colortimeStamp", 0)) {
            syncDataCallback.updateColors(newColors, newTextColors);
        }
        syncDataCallback.DoneSyncingData();
    }
}
