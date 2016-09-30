package us.koller.todolist.Util.Callbacks;

import java.util.ArrayList;

import us.koller.todolist.Todolist.Alarm;

/**
 * Created by Lukas on 10.06.2016.
 */
public interface SyncDataCallback {
    void DoneSyncingData(ArrayList<Long> eventsToUpdate);
    void updateAlarms(ArrayList<Long> alarmsToCancel, ArrayList<Alarm> alarmsToSet);
    void error(String error);
    void updateColors(int[] newColors, int[] newTextColors);
}
