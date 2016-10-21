package us.koller.todolist.FirebaseSync;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import us.koller.todolist.Todolist.Alarm;
import us.koller.todolist.Todolist.Event;
import us.koller.todolist.Util.Callbacks.SyncDataCallback;

/**
 * Created by Lukas on 10.06.2016.
 */
public class SyncDataAsyncTask extends AsyncTask<Void, Void, String> {

    private us.koller.todolist.Todolist.Todolist Todolist;

    private String data;
    private SyncDataCallback syncDataCallback;

    private boolean wasEverSynced;

    private ArrayList<Event> driveList;

    private ArrayList<Long> todolist_rE;
    private ArrayList<Long> todolist_aE;

    private ArrayList<Boolean> driveList_removed;
    private ArrayList<Boolean> todolist_removed;

    private ArrayList<Long> eventsToUpdate;

    private ArrayList<Long> alarmsToCancel;
    private ArrayList<Alarm> alarmsToSet;

    private JSONObject selected_categories;

    public SyncDataAsyncTask(us.koller.todolist.Todolist.Todolist todolist,
                             String data, boolean wasEverSynced, SyncDataCallback syncDataCallback) {
        Todolist = todolist;
        this.data = data;
        this.syncDataCallback = syncDataCallback;

        this.wasEverSynced = wasEverSynced;

        driveList = new ArrayList<>();

        todolist_rE = todolist.getRemovedEvents();
        todolist_aE = todolist.getAddedEvents();

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
            JSONArray array_drL = array.getJSONArray(1);
            try {
                selected_categories = array.getJSONObject(2);
            } catch (JSONException e){
                e.printStackTrace();
            }


            //Load Data into driveList
            if (array_drL != null) {
                for (int i = 0; i < array_drL.length(); i++) {
                    Event e = new Event(array_drL.getJSONObject(i));
                    driveList.add(e);

                    driveList_removed.add(false);
                }
            }

            for (int i = 0; i < Todolist.getTodolistArray().size(); i++) {
                todolist_removed.add(false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("SyncDataAsyncTask", "JSONException, data: " + data);
            return "JSONException";
        }

        //Sync Data
        if (wasEverSynced) {
            //remove Events from driveList
            for (int i = 0; i < todolist_rE.size(); i++) {
                for (int k = 0; k < driveList.size(); k++) {
                    if (todolist_rE.get(i) == driveList.get(k).getId()) {
                        driveList_removed.set(k, true);
                    }
                }
            }

            //update Events
            for (int i = 0; i < driveList.size(); i++) {
                if (!driveList_removed.get(i)) {
                    for (int k = 0; k < Todolist.getTodolistArray().size(); k++) {
                        if (driveList.get(i).getId() == Todolist.getTodolistArray().get(k).getId()) {
                            driveList_removed.set(i, true);
                            todolist_removed.set(k, true);

                            Event event_T = Todolist.getTodolistArray().get(k);
                            Event event_d = driveList.get(i);

                            eventsToUpdate.add(event_T.getId());

                            String WhatToDo;
                            long whatToDo_timeStamp, color_timeStamp;
                            int color;

                            if (event_T.getWhatToDo_timeStamp() > event_d.getWhatToDo_timeStamp()) {
                                WhatToDo = event_T.getWhatToDo();
                                whatToDo_timeStamp = event_T.getWhatToDo_timeStamp();
                            } else {
                                WhatToDo = event_d.getWhatToDo();
                                whatToDo_timeStamp = event_d.getWhatToDo_timeStamp();
                            }

                            if (event_T.getColor_timeStamp() > event_d.getColor_timeStamp()) {
                                color = event_T.getColor();
                                color_timeStamp = event_T.getColor_timeStamp();
                            } else {
                                color = event_d.getColor();
                                color_timeStamp = event_d.getColor_timeStamp();
                            }

                            Todolist.getTodolistArray().get(k)
                                    .update(WhatToDo, whatToDo_timeStamp, color, color_timeStamp);

                            //Alarms
                            if (event_T.hasAlarm() || event_d.hasAlarm()) {
                                if (event_T.hasAlarm() && event_d.hasAlarm()) {
                                    if (event_T.getAlarm().equals(event_d.getAlarm())
                                            && event_T.getAlarm_timeStamp() < event_d.getAlarm_timeStamp()) {
                                        alarmsToCancel.add((long) event_T.getAlarm().get(Alarm.ID));
                                        alarmsToSet.add(event_d.getAlarm());
                                        event_T.setAlarm(event_d.getAlarm());
                                    }
                                } else {
                                    if (event_T.hasAlarm()) {
                                        if (event_T.getAlarm_timeStamp() < event_d.getAlarm_timeStamp()) {
                                            alarmsToCancel.add((long) event_T.getAlarm().get(Alarm.ID));
                                            event_T.removeAlarm();
                                        }
                                    } else {
                                        if (event_T.getAlarm_timeStamp() < event_d.getAlarm_timeStamp()) {
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
            for (int i = 0; i < todolist_aE.size(); i++) {
                int index = Todolist.getEventIndexById(todolist_aE.get(i));
                if (index >= Todolist.getTodolistArray().size()) {
                    break;
                }
                todolist_removed.set(index, true);
            }

            for (int i = 0; i < Todolist.getTodolistArray().size(); i++) {
                if (!todolist_removed.get(i)) {
                    Todolist.getTodolistArray().remove(i);
                }
            }

            //add Events to todolist
            for (int i = 0; i < driveList.size(); i++) {
                if (!driveList_removed.get(i)) {
                    Event e = driveList.get(i);
                    if (!Todolist.isEventInTodolist(e.getId())) {
                        if (i < Todolist.getTodolistArray().size()) {
                            Todolist.getTodolistArray().add(i, e);
                        } else {
                            Todolist.getTodolistArray().add(e);
                        }

                        //set Alarms
                        if (e.hasAlarm()) {
                            alarmsToSet.add(e.getAlarm());
                        }
                    }
                }
            }

            //add Events to driveList
            for (int i = 0; i < todolist_aE.size(); i++) {
                int index = Todolist.getEventIndexById(todolist_aE.get(i));
                Event e = Todolist.getEventById(todolist_aE.get(i));
                if (index >= driveList.size()) {
                    driveList.add(e);
                } else {
                    driveList.add(index, e);
                }
            }

            //remove Events from driveList
            for (int i = 0; i < todolist_rE.size(); i++) {
                for (int k = 0; k < driveList.size(); k++) {
                    if (driveList.get(k).getId() == todolist_rE.get(i)) {
                        driveList.remove(k);
                        break;
                    }
                }
            }

            //move Events
            if ((Todolist.getTodolistArray().size() == driveList.size())) {
                for (int i = 0; i < driveList.size(); i++) {
                    int index = Todolist.getEventIndexById(driveList.get(i).getId());
                    if (i != index && index != -1) {
                        Event event_d = driveList.get(i);
                        Event event_t = Todolist.getEventById(event_d.getId());
                        if (event_t == null) {
                            break;
                        }
                        if (event_d.getMove_timeStamp() > event_t.getMove_timeStamp()) {
                            Todolist.eventMoved(index, i);
                        }
                    }
                }
            }
        } else {
            //if device was never synced -> add all Events
            for (int i = 0; i < driveList.size(); i++) {
                Event e = driveList.get(i);
                if (!Todolist.isEventInTodolist(e.getId())) {
                    if (i < Todolist.getTodolistArray().size()) {
                        Todolist.getTodolistArray().add(i, e);
                    } else {
                        Todolist.getTodolistArray().add(e);
                    }

                    //set Alarms
                    if (e.hasAlarm()) {
                        alarmsToSet.add(e.getAlarm());
                    }
                } else {
                    for (int j = 0; j < driveList.size(); j++) {
                        if (!driveList_removed.get(i)) {
                            for (int k = 0; k < Todolist.getTodolistArray().size(); k++) {
                                if (driveList.get(i).getId() == Todolist.getTodolistArray().get(k).getId()) {
                                    driveList_removed.set(j, true);
                                    todolist_removed.set(k, true);

                                    Event event_T = Todolist.getTodolistArray().get(k);
                                    Event event_d = driveList.get(j);

                                    eventsToUpdate.add(event_T.getId());

                                    String WhatToDo;
                                    long whatToDo_timeStamp, color_timeStamp;
                                    int color;

                                    if (event_T.getWhatToDo_timeStamp() > event_d.getWhatToDo_timeStamp()) {
                                        WhatToDo = event_T.getWhatToDo();
                                        whatToDo_timeStamp = event_T.getWhatToDo_timeStamp();
                                    } else {
                                        WhatToDo = event_d.getWhatToDo();
                                        whatToDo_timeStamp = event_d.getWhatToDo_timeStamp();
                                    }

                                    if (event_T.getColor_timeStamp() > event_d.getColor_timeStamp()) {
                                        color = event_T.getColor();
                                        color_timeStamp = event_T.getColor_timeStamp();
                                    } else {
                                        color = event_d.getColor();
                                        color_timeStamp = event_d.getColor_timeStamp();
                                    }

                                    Todolist.getTodolistArray().get(k)
                                            .update(WhatToDo, whatToDo_timeStamp, color, color_timeStamp);

                                    //Alarms
                                    if (event_T.hasAlarm() || event_d.hasAlarm()) {
                                        if (event_T.hasAlarm() && event_d.hasAlarm()) {
                                            if (event_T.getAlarm().equals(event_d.getAlarm())
                                                    && event_T.getAlarm_timeStamp() < event_d.getAlarm_timeStamp()) {
                                                alarmsToCancel.add((long) event_T.getAlarm().get(Alarm.ID));
                                                alarmsToSet.add(event_d.getAlarm());
                                                event_T.setAlarm(event_d.getAlarm());
                                            }
                                        } else {
                                            if (event_T.hasAlarm()) {
                                                if (event_T.getAlarm_timeStamp() < event_d.getAlarm_timeStamp()) {
                                                    alarmsToCancel.add((long) event_T.getAlarm().get(Alarm.ID));
                                                    event_T.removeAlarm();
                                                }
                                            } else {
                                                if (event_T.getAlarm_timeStamp() < event_d.getAlarm_timeStamp()) {
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
                }
            }
        }

        return "Success";
    }

    @Override
    protected void onPostExecute(String result) {
        if (!result.equals("Success")) {
            syncDataCallback.error(result);
            return;
        }
        syncDataCallback.updateAlarms(alarmsToCancel, alarmsToSet);
        syncDataCallback.doneSyncingData(selected_categories);
    }
}
