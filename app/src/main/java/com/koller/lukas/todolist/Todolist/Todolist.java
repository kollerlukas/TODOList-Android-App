package com.koller.lukas.todolist.Todolist;

import android.content.Context;

import com.koller.lukas.todolist.RecyclerViewAdapters.RVAdapter;
import com.koller.lukas.todolist.Settings;

import org.json.JSONArray;
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

/**
 * Write a description of class Todolist here.
 * Todolist manages your EVENTs.
 *
 * @author (Lukas Koller)
 * @version (1.0)
 */

public class Todolist {

    private Settings settings;

    private ArrayList<Event> todolist;

    private ArrayList<Long> removedEvents;

    private ArrayList<Long> addedEvents;

    private Event lastRemovedEvent;
    private int lastRemovedEventPosition;

    public long lastSyncTimeStamp = 0;

    public Todolist(Settings settings) {
        this.settings = settings;

        todolist = new ArrayList<>();
        removedEvents = new ArrayList<>();
        addedEvents = new ArrayList<>();
    }

    public void addEvent(RVAdapter mAdapter, Event e) {
        todolist.add(e);
        mAdapter.addItem(e);

        if (settings.syncEnabled) {
            addedEvents.add(e.getId());
        }
    }

    public void initData(Context context) {
        //readSettings(context);
        try {
            readData(context);
        } catch (JSONException e) {

            //old; that the update correctly imports old data
            try {
                readData_old(context);
            } catch (JSONException e1) {
                e1.printStackTrace();
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }


            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void restoreLastRemovedEvent() {
        if (lastRemovedEvent != null) {
            todolist.add(lastRemovedEventPosition, lastRemovedEvent);
            if (settings.syncEnabled) {
                removedEvents.remove(lastRemovedEvent.getId());
            }
            lastRemovedEvent = null;
        }
    }

    public void removeEvent(Event e) {
        //Only for removing Event that are not in the Adapter-List!!!!
        lastRemovedEventPosition = todolist.indexOf(e);
        lastRemovedEvent = e;
        todolist.remove(e);

        if (settings.syncEnabled) {
            removedEvents.add(e.getId());
        }
    }

    public void removeEvent(RVAdapter mAdapter, int index) {
        Event e = mAdapter.getList().get(index);
        removeEvent(e);
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

    public Event getEventById(long id) {
        for (int i = 0; i < todolist.size(); i++) {
            Event e = todolist.get(i);
            if (e.getId() == id) {
                return e;
            }
        }
        return null;
    }

    public int getEventIndexById(long id) {
        for (int i = 0; i < todolist.size(); i++) {
            Event e = todolist.get(i);
            if (e.getId() == id) {
                return i;
            }
        }
        return todolist.size();
    }

    public boolean isAlarmScheduled() {
        for (int i = 0; i < todolist.size(); i++) {
            if (todolist.get(i).hasAlarm()
                    && !hasAlarmFired((todolist.get(i)))) {
                return true;
            }
        }
        return false;
    }

    public ArrayList initAdapterList() {
        ArrayList<Event> adapter_list = new ArrayList<>();
        for (int i = 0; i < todolist.size(); i++) {
            if (settings.selected_categories[todolist.get(i).getColor()]) {
                adapter_list.add(todolist.get(i));
            }
        }
        return adapter_list;
    }

    public void addOrRemoveEventFromAdapter(RVAdapter mAdapter) {
        Event[] tempAdapterList = new Event[mAdapter.getList().size()];
        //Needed for running through the whole mAdapterlist
        for (int i = 0; i < mAdapter.getList().size(); i++) {
            tempAdapterList[i] = mAdapter.getList().get(i);
        }
        for (int i = tempAdapterList.length - 1; i >= 0; i--) {
            if (!settings.selected_categories[tempAdapterList[i].getColor()]) {
                mAdapter.removeItem(mAdapter.getList().indexOf(tempAdapterList[i]));
            }
        }
        for (int i = 0; i < todolist.size(); i++) {
            if (settings.selected_categories[todolist.get(i).getColor()]
                    && !isEventInAdapterList(mAdapter, todolist.get(i))) {
                int index = getAdapterListPosition(mAdapter, todolist.get(i));
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
        Event[] tempAdapterList = new Event[mAdapter.getList().size()];
        //Needed for running through the whole mAdapterlist
        for (int i = 0; i < mAdapter.getList().size(); i++) {
            tempAdapterList[i] = mAdapter.getList().get(i);
        }
        for (int i = 0; i < tempAdapterList.length; i++) {
            if (tempAdapterList[i].semiTransparent) {
                mAdapter.removeItem(mAdapter.getList().indexOf(tempAdapterList[i]));
            }
        }
        //resetting all Events
        for (int i = 0; i < todolist.size(); i++) {
            todolist.get(i).semiTransparent = false;
        }
    }

    public void setEventsSemiTransparent(RVAdapter mAdapter) {
        for (int i = 0; i < todolist.size(); i++) {
            if (!isEventInAdapterList(mAdapter, todolist.get(i))) {
                todolist.get(i).semiTransparent = true;
            }
        }
    }

    public boolean isEventInAdapterList(RVAdapter mAdapter, Event e) {
        for (int i = 0; i < mAdapter.getList().size(); i++) {
            if (e.getId() == mAdapter.getList().get(i).getId()) {
                return true;
            }
        }
        return false;
    }

    public int getAdapterListPosition(RVAdapter mAdapter, Event e) {
        int adapterListPosition = 0;
        for (int i = 0; i < todolist.size(); i++) {
            if (todolist.get(i).getId() == e.getId()) {
                return adapterListPosition;
            }
            if (settings.selected_categories[todolist.get(i).getColor()]) {
                adapterListPosition++;
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

    public boolean isEventInTodolist(long id) {
        for (int i = 0; i < todolist.size(); i++) {
            if (todolist.get(i).getId() == id) {
                return true;
            }
        }
        return false;
    }

    public void EventMoved(int fromPosition, int toPosition) {
        todolist.get(fromPosition).setMove_timeStamp(System.currentTimeMillis());
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

    public void saveData(Context context) throws JSONException {
        saveFile(context, "events", getData());
        //save removed Todos for sync
        if (settings.syncEnabled) {
            saveRemovedEvents(context);

            saveAddedEvents(context);
        }
    }

    public void saveRemovedEvents(Context context) throws JSONException {
        saveFile(context, "removedEvents", getRemovedEventsString());
    }

    public void saveAddedEvents(Context context) throws JSONException {
        saveFile(context, "addedEvents", getAddedEventsString());
    }

    public void clearRemovedAndAdddedEvents(Context context) {
        removedEvents.clear();
        addedEvents.clear();
        try {
            saveData(context);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getData() throws JSONException {
        JSONArray array = new JSONArray();
        for (int i = 0; i < todolist.size(); i++) {
            array.put(todolist.get(i).saveData());
        }
        return array.toString();
    }

    public String getRemovedEventsString() throws JSONException {
        JSONArray array = new JSONArray();
        for (int i = 0; i < removedEvents.size(); i++) {
            array.put(removedEvents.get(i));
        }
        return array.toString();
    }

    public String getAddedEventsString() throws JSONException {
        JSONArray array = new JSONArray();
        for (int i = 0; i < addedEvents.size(); i++) {
            array.put(addedEvents.get(i));
        }
        return array.toString();
    }

    public void readData(Context context) throws JSONException, FileNotFoundException {
        String data = readFile(context, "events");
        JSONArray array = new JSONArray(data);
        for (int i = 0; i < array.length(); i++) {
            todolist.add(new Event(array.getJSONObject(i)));
        }

        // read removedEvents and addedEvents
        if (settings.syncEnabled) {
            String data_removedEvents = readFile(context, "removedEvents");
            JSONArray array_removedEvents = new JSONArray(data_removedEvents);
            for (int i = 0; i < array_removedEvents.length(); i++) {
                removedEvents.add(array_removedEvents.getLong(i));
            }

            String data_addedEvents = readFile(context, "addedEvents");
            JSONArray array_addedEvents = new JSONArray(data_addedEvents);
            for (int i = 0; i < array_addedEvents.length(); i++) {
                addedEvents.add(array_addedEvents.getLong(i));
            }
        }
    }

    public ArrayList<Long> EventRemoved(Context context)
            throws JSONException, FileNotFoundException {
        String data = readFile(context, "events");
        JSONArray array = new JSONArray(data);
        ArrayList<Long> newList = new ArrayList();
        for (int i = 0; i < array.length(); i++) {
            Event e = new Event(array.getJSONObject(i));
            newList.add(e.getId());
        }
        ArrayList<Long> currentList = new ArrayList();
        for (int i = 0; i < todolist.size(); i++) {
            currentList.add(todolist.get(i).getId());
        }
        currentList.removeAll(newList);
        return currentList;
    }

    public String readFile(Context context, String filename) {
        StringBuilder sb = new StringBuilder();
        try {
            FileInputStream fis = context.openFileInput(filename);
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

        return sb.toString();
    }

    public void saveFile(Context context, String filename, String data)
            throws JSONException {
        try {
            FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            fos.write(data.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setAllEventsSemitransparent(RVAdapter mAdapter) {
        for (int i = 0; i < mAdapter.getList().size(); i++) {
            mAdapter.getList().get(i).semiTransparent = true;
            mAdapter.itemChanged(i);
        }
    }

    public String getShareFileData(ArrayList<Event> eventsToShare)
            throws JSONException {
        JSONArray array = new JSONArray();
        for (int i = 0; i < eventsToShare.size(); i++) {
            array.put(eventsToShare.get(i).saveData());
        }
        return array.toString();
    }

    public void importEvents(ArrayList<Event> events) {
        for (int i = 0; i < events.size(); i++) {
            if (!isEventInTodolist(events.get(i).getId())) {
                todolist.add(events.get(i));
            }
        }
    }

    public String getSyncData() {
        JSONArray array = new JSONArray();
        long timeStamp = System.currentTimeMillis();
        array.put(timeStamp);

        try {
            array.put(new JSONArray(getData()));
            //array.put(new JSONArray(getRemovedEventsString()));
            //maybe Colors
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return array.toString();
    }

    public ArrayList<Long> getRemovedEvents() {
        return removedEvents;
    }

    public ArrayList<Long> getAddedEvents() {
        return addedEvents;
    }

    public boolean hasAlarmFired(Event e) {
        return e.getAlarm().time < System.currentTimeMillis();
    }

    public Event getLastRemovedEvent() {
        return lastRemovedEvent;
    }

    public ArrayList<Event> getTodolist() {
        return todolist;
    }

    //old; that the update correctly imports old data
    public void readData_old(Context context)
            throws JSONException, FileNotFoundException {
        StringBuilder sb = new StringBuilder();
        try {
            FileInputStream fis = context.openFileInput("events");
            BufferedReader reader
                    = new BufferedReader(new InputStreamReader(fis));
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
            try {
                timeStamp = json.getLong(i + "timeStamp");
            } catch (JSONException exception) {
                timeStamp = 0;
            }
            Event e = new Event(
                    json.getString(i + "WhatToDo"), 0,
                    json.getInt(i + "Color"), 0,
                    json.getLong(i + "Id"), null);
            if (json.getLong(i + "AlarmTime") > System.currentTimeMillis()) {
                e.setAlarm(json.getLong(i + "AlarmId"),
                        json.getLong(i + "AlarmTime"));
            }
            todolist.add(e);
        }
    }

}
