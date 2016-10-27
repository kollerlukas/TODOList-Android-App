package us.koller.todolist.FirebaseSync;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

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

        alarmsToCancel = new ArrayList<>();
        alarmsToSet = new ArrayList<>();

        Log.d("SyncDataAsyncTask", String.valueOf(Todolist.getTodolistArray().size()));
    }


    @Override
    protected String doInBackground(Void... params) {
        //Setup for syncing
        try {
            JSONArray array = new JSONArray(data);
            JSONArray array_drL = array.getJSONArray(1);
            try {
                selected_categories = array.getJSONObject(2);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            //Load Data into driveList
            if (array_drL != null) {
                for (int i = 0; i < array_drL.length(); i++) {
                    driveList.add(new Event(array_drL.getJSONObject(i)));
                }
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
                    if (driveList.get(k).getId() == todolist_rE.get(i)) {
                        driveList.remove(k);
                        break;
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

            //update Events
            for (int i = 0; i < Todolist.getTodolistArray().size(); i++) {
                for (int j = 0; j < driveList.size(); j++) {
                    Event e = Todolist.getTodolistArray().get(i);
                    Event e_d = driveList.get(j);
                    if (e_d != null) {
                        if (e.getId() == e_d.getId()) {
                            if (!e.equals(e_d)) {
                                e.editWhatToDo(e_d.getWhatToDo());
                                e.setColor(e_d.getColor());
                                if (e.getAlarm() != null) {
                                    alarmsToCancel.add((long) e.getAlarm().get(Alarm.ID));
                                }
                                e.setAlarm(e_d.getAlarm());
                                if (e.getAlarm() != null) {
                                    alarmsToSet.add(e.getAlarm());
                                }
                                int adapter_index = Todolist.getAdapterIndexFromTodolistIndex(i);
                                syncDataCallback.notifyItemChanged(adapter_index);
                            }
                        }
                    }
                }
            }

            //add Events to TodoList
            for (int i = 0; i < driveList.size(); i++) {
                Event e = driveList.get(i);
                if (e != null) {
                    if (!Todolist.isEventInTodolist(e.getId())) {
                        //add Event
                        if (i < Todolist.getTodolistArray().size()) {
                            Todolist.getTodolistArray().add(i, e);
                            int adapter_index = Todolist.getAdapterIndexFromTodolistIndex(i);
                            Todolist.getAdapterList().add(adapter_index, e);
                            syncDataCallback.notifyItemInserted(adapter_index);
                        } else {
                            Todolist.getTodolistArray().add(e);
                            int adapter_index = Todolist.getAdapterIndexFromTodolistIndex(Todolist.getTodolistArray().size() -1);
                            Todolist.getAdapterList().add(adapter_index, e);
                            syncDataCallback.notifyItemInserted(adapter_index);
                        }
                        //set Alarms
                        if (e.hasAlarm()) {
                            alarmsToSet.add(e.getAlarm());
                        }
                    }
                }
            }

            //remove Events from TodoList
            for (int i = 0; i < Todolist.getTodolistArray().size(); i++) {
                boolean found = false;
                Event e = Todolist.getTodolistArray().get(i);
                for (int k = 0; k < driveList.size(); k++) {
                    if(driveList.get(k) != null){
                        if (driveList.get(k).getId()
                                == e.getId()) {
                            found = true;
                        }
                    }
                }
                if (!found) {
                    //remove Event
                    int adapter_index = Todolist.getIndexOfEventInAdapterListById(e.getId());
                    Todolist.getAdapterList().remove(adapter_index);
                    Todolist.getTodolistArray().remove(i);
                    syncDataCallback.notifyItemRemoved(adapter_index);
                }
            }

            //move events
            if ((Todolist.getTodolistArray().size() == driveList.size())) {
                for (int i = 0; i < driveList.size(); i++) {
                    int index = Todolist.getEventIndexById(driveList.get(i).getId());
                    if (i != index && index != -1) {
                        Event event_d = driveList.get(i);
                        Event event_t = Todolist.getEventById(event_d.getId());
                        if (event_t == null) {
                            break;
                        }
                        Todolist.eventMoved(index, i);
                        int from = Todolist.getAdapterIndexFromTodolistIndex(index);
                        int to = Todolist.getAdapterIndexFromTodolistIndex(i);

                        //swap event in adapter-List
                        if (from < to) {
                            for (int j = from; j < to; j++) {
                                Collections.swap(Todolist.getAdapterList(), j, j + 1);
                            }
                        } else {
                            for (int k = from; k > to; k--) {
                                Collections.swap(Todolist.getAdapterList(), k, k - 1);
                            }
                        }
                        syncDataCallback.notifyItemMoved(from, to);
                    }
                }
            }

        } else {
            //if device was never synced -> add all Events
            Log.d("SyncDataAsyncTask", "device was never synced");
            for (int i = 0; i < driveList.size(); i++) {
                Event e = driveList.get(i);
                if (!Todolist.isEventInTodolist(e.getId())) {
                    //add events
                    if (i < Todolist.getTodolistArray().size()) {
                        Todolist.getTodolistArray().add(i, e);
                        int adapter_index = Todolist.getAdapterIndexFromTodolistIndex(i);
                        Todolist.getAdapterList().add(adapter_index, e);
                        syncDataCallback.notifyItemInserted(adapter_index);
                    } else {
                        Todolist.getTodolistArray().add(e);
                        int adapter_index = Todolist.getAdapterIndexFromTodolistIndex(Todolist.getTodolistArray().size() -1);
                        Todolist.getAdapterList().add(adapter_index, e);
                        syncDataCallback.notifyItemInserted(adapter_index);
                    }

                    //set Alarms
                    if (e.hasAlarm()) {
                        alarmsToSet.add(e.getAlarm());
                    }
                }
            }
        }

        return "Success";
    }

    @Override
    protected void onPostExecute(String result) {
        if(syncDataCallback != null){
            if (!result.equals("Success")) {
                syncDataCallback.error(result);
                return;
            }

            syncDataCallback.updateAlarms(alarmsToCancel, alarmsToSet);
            syncDataCallback.doneSyncingData(selected_categories);

            //when device was sync for the first time, write data back
            if (!wasEverSynced) {
                syncDataCallback.doneFirstEverSync();
            }
        }
    }

    public void dettach(){
        syncDataCallback = null;
        cancel(true);
    }
}
