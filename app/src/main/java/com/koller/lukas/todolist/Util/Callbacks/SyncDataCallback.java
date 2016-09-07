package com.koller.lukas.todolist.Util.Callbacks;

import com.koller.lukas.todolist.Todolist.Alarm;

import java.util.ArrayList;

/**
 * Created by Lukas on 10.06.2016.
 */
public interface SyncDataCallback {
    void DoneSyncingData(ArrayList<Long> eventsToUpdate);
    void updateAlarms(ArrayList<Long> alarmsToCancel, ArrayList<Alarm> alarmsToSet);
    void error(String error);
    void updateColors(int [] newColors, int [] newTextColors);
}
