package com.koller.lukas.todolist.DriveSync;

import android.os.AsyncTask;
import android.util.Log;

import com.koller.lukas.todolist.Todolist.Alarm;
import com.koller.lukas.todolist.Todolist.Event;
import com.koller.lukas.todolist.Util.Callbacks.SyncDataCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Lukas on 10.06.2016.
 */
public class SyncDataAsyncTask extends AsyncTask<Void, Void, String> {

    private com.koller.lukas.todolist.Todolist.Todolist Todolist;

    private String data;
    private SyncDataCallback syncDataCallback;

    private ArrayList<Event> driveList;

    private ArrayList<Long> todolist_rE;
    private ArrayList<Long> todolist_aE;

    private Long lastSyncTimeStamp;

    //private int[] newColors = new int[13];
    //private int[] newTextColors = new int[13];

    private ArrayList<Boolean> driveList_removed;
    private ArrayList<Boolean> todolist_removed;

    private ArrayList<Long> eventsToUpdate;

    private ArrayList<Long> alarmsToCancel;
    private ArrayList<Alarm> alarmsToSet;

    public SyncDataAsyncTask(com.koller.lukas.todolist.Todolist.Todolist todolist,
                             String data, SyncDataCallback syncDataCallback) {
        Todolist = todolist;
        this.data = data;
        this.syncDataCallback = syncDataCallback;

        driveList = new ArrayList<>();

        todolist_rE = todolist.getRemovedEvents();
        todolist_aE = todolist.getAddedEvents();

        Log.d("SyncDataAsyncTask", "todolist_rE.size(): " + String.valueOf(todolist_rE.size()));

        lastSyncTimeStamp = todolist.lastSyncTimeStamp;

        driveList_removed = new ArrayList<>();
        todolist_removed = new ArrayList<>();

        eventsToUpdate = new ArrayList<>();

        alarmsToCancel = new ArrayList<>();
        alarmsToSet = new ArrayList<>();
    }


    @Override
    protected String doInBackground(Void... params) {
        //Setup for syncing
        try {
            JSONArray array = new JSONArray(data);
            long timeStamp = array.getLong(0);

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(timeStamp);
            Log.d("SyncDataAsyncTask", "data timeStamp: " + cal.getTime().toString());

            JSONArray array_drL = array.getJSONArray(1);

            //Load Data into driveList
            if (array_drL != null) {
                for (int i = 0; i < array_drL.length(); i++) {
                    Event e = new Event(array_drL.getJSONObject(i));
                    driveList.add(e);

                    driveList_removed.add(false);
                }
            }

            Log.d("SyncDataAsyncTask", "driveList.size(): " + String.valueOf(driveList.size()));

            for (int i = 0; i <Todolist.getTodolist().size(); i++){
                todolist_removed.add(false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return "JSONException";
        }

        //Sync Data
        if (lastSyncTimeStamp != 0) {

            //remove Events from driveList
            for (int i = 0; i < todolist_rE.size(); i++) {
                for (int k = 0; k < driveList.size(); k++) {
                    if (todolist_rE.get(i) == driveList.get(k).getId()) {
                        Log.d("SyncDataAsyncTask", "removed event from driveList: " + driveList.get(k).getWhatToDo());
                        driveList_removed.set(k, true);
                    }
                }
            }

            //update Events
            for (int i = 0; i < driveList.size(); i++) {
                if (!driveList_removed.get(i)) {
                    for (int k = 0; k < Todolist.getTodolist().size(); k++) {
                        if (driveList.get(i).getId() == Todolist.getTodolist().get(k).getId()) {
                            driveList_removed.set(i, true);
                            todolist_removed.set(k, true);

                            Event event_T = Todolist.getTodolist().get(k);
                            Event event_d = driveList.get(i);

                            eventsToUpdate.add(event_T.getId());

                            String WhatToDo;
                            long whatToDo_timeStamp, color_timeStamp;
                            int color;

                            if (event_T.getWhatToDo_timeStamp() > event_d.getWhatToDo_timeStamp()){
                                WhatToDo = event_T.getWhatToDo();
                                whatToDo_timeStamp = event_T.getWhatToDo_timeStamp();
                            } else {
                                WhatToDo = event_d.getWhatToDo();
                                whatToDo_timeStamp = event_d.getWhatToDo_timeStamp();
                            }

                            if(event_T.getColor_timeStamp() > event_d.getColor_timeStamp()){
                                color = event_T.getColor();
                                color_timeStamp = event_T.getColor_timeStamp();
                            } else {
                                color = event_d.getColor();
                                color_timeStamp = event_d.getColor_timeStamp();
                            }

                            Todolist.getTodolist().get(k).update(WhatToDo, whatToDo_timeStamp, color, color_timeStamp);

                            //Alarms
                            if(event_T.hasAlarm() || event_d.hasAlarm()){
                                if(event_T.hasAlarm() && event_d.hasAlarm()){
                                    if(event_T.getAlarm().equals(event_d.getAlarm())
                                            && event_T.getAlarm_timeStamp() < event_d.getAlarm_timeStamp()){
                                        alarmsToCancel.add(event_T.getAlarm().id);
                                        alarmsToSet.add(event_d.getAlarm());
                                        event_T.setAlarm(event_d.getAlarm());
                                    }
                                } else {
                                    if(event_T.hasAlarm()){
                                        if(event_T.getAlarm_timeStamp() < event_d.getAlarm_timeStamp()){
                                            alarmsToCancel.add(event_T.getAlarm().id);
                                            event_T.removeAlarm();
                                        }
                                    } else {
                                        if(event_T.getAlarm_timeStamp() < event_d.getAlarm_timeStamp()){
                                            alarmsToSet.add(event_d.getAlarm());
                                            event_T.setAlarm(event_d.getAlarm());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            //remove Events from todolist
            for (int i = 0; i < todolist_aE.size(); i++){
                int index = Todolist.getEventIndexById(todolist_aE.get(i));
                if(index >= Todolist.getTodolist().size()){
                    break;
                }
                todolist_removed.set(index, true);
            }

            for (int i = 0; i < Todolist.getTodolist().size(); i++){
                if(!todolist_removed.get(i)){
                    Log.d("SyncDataAsyncTask", "removed Event: " + Todolist.getTodolist().get(i).getWhatToDo());
                    Todolist.getTodolist().remove(i);
                }
            }

            //add Events to todolist
            for (int i = 0; i < driveList.size(); i++) {
                if (!driveList_removed.get(i)) {
                    Event e = driveList.get(i);
                    if(i < Todolist.getTodolist().size()){
                        Todolist.getTodolist().add(i, e);
                    } else {
                        Todolist.getTodolist().add(e);
                    }
                }
            }

            //move Events

        } else {
            // was never synced!! -> add all Events
            for (int i = 0; i < driveList.size(); i++) {
                Todolist.getTodolist().add(driveList.get(i));
            }
        }
        return "Success";
    }

    @Override
    protected void onPostExecute(String result) {
        if(!result.equals("Success")){
            syncDataCallback.error(result);
            return;
        }
        syncDataCallback.DoneSyncingData(eventsToUpdate);
    }
}
