package us.koller.todolist.Util.Callbacks;

import org.json.JSONObject;

import java.util.ArrayList;

import us.koller.todolist.Todolist.Alarm;

/**
 * Created by Lukas on 10.06.2016.
 */
public interface SyncDataCallback {
    void doneSyncingData(JSONObject selected_categories);
    void updateAlarms(ArrayList<Long> alarmsToCancel, ArrayList<Alarm> alarmsToSet);
    void error(String error);
}
