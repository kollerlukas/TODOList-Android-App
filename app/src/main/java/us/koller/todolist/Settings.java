package us.koller.todolist;

import com.google.android.gms.drive.DriveId;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by Lukas on 31.08.2016.
 */
public class Settings {

    private SharedPreferences sharedpreferences;

    private boolean vibrate;
    private boolean notificationToggle;
    private boolean[] selected_categories;

    private boolean syncEnabled; //if user signed in; App might not be signed in yet
    private boolean signedIn = false; //app successfully signed in
    private boolean autoSync;
    private long lastSyncTimeStamp;
    private DriveId driveId;
    private long lastReceivedDataTimeStamp;

    public Settings(Context context){
        sharedpreferences = context.getSharedPreferences("todolist", Context.MODE_PRIVATE);
    }

    public void readSettings() {
        vibrate = sharedpreferences.getBoolean("vibrate", true);
        notificationToggle = sharedpreferences.getBoolean("notificationToggle", true);

        syncEnabled = sharedpreferences.getBoolean("syncEnabled", false);
        autoSync = sharedpreferences.getBoolean("autoSync", true);
        lastSyncTimeStamp = sharedpreferences.getLong("lastSyncTimeStamp", 0);

        String driveIdString = sharedpreferences.getString("DriveId driveId", "Error");
        if(!driveIdString.equals("Error")){
            driveId = DriveId.decodeFromString(driveIdString);
        } else {
            driveId = null;
        }
        lastReceivedDataTimeStamp = sharedpreferences.getLong("lastReceivedDataTimeStamp", 0);

        readSelectedCategories();
    }

    private void readSelectedCategories() {
        selected_categories = new boolean[13];
        String s
                = sharedpreferences.getString("selected_categories", "");
        try {
            JSONArray array = new JSONArray(s);

            for (int i = 0; i < selected_categories.length; i++){
                selected_categories[i] = array.getBoolean(i);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void saveSettings() {
        SharedPreferences.Editor editor
                = sharedpreferences.edit();
        editor.putBoolean("vibrate", vibrate);
        editor.putBoolean("notificationToggle", notificationToggle);

        editor.putBoolean("syncEnabled", syncEnabled);
        editor.putBoolean("autoSync", autoSync);
        editor.putLong("lastSyncTimeStamp", lastSyncTimeStamp);

        if(driveId != null){
            String driveIdString = driveId.encodeToString();
            editor.putString("driveId", driveIdString);
        }

        editor.putLong("lastReceivedDataTimeStamp", lastReceivedDataTimeStamp);

        editor.apply();

        saveCategorySettings();
    }

    private void saveCategorySettings() {
        JSONArray array = new JSONArray();
        for (int i = 0; i < selected_categories.length; i++){
            array.put(selected_categories[i]);
        }

        SharedPreferences.Editor editor
                = sharedpreferences.edit();
        editor.putString("selected_categories", array.toString());
        editor.apply();
    }

    public void set(String key, Object o){
        switch (key){
            case "vibrate":
                vibrate = (boolean) o;
                break;
            case "notificationToggle":
                notificationToggle = (boolean) o;
                break;
            case "syncEnabled":
                syncEnabled = (boolean) o;
                break;
            case "selected_categories":
                selected_categories = (boolean[]) o;
                break;
            case "signedIn":
                signedIn = (boolean) o;
                break;
            case "autoSync":
                autoSync = (boolean) o;
                break;
            case "lastSyncTimeStamp":
                lastSyncTimeStamp = (long) o;
                break;
            case "driveId":
                driveId = (DriveId) o;
                break;
            case "lastReceivedDataTimeStamp":
                lastReceivedDataTimeStamp = (long) o;
                break;
        }
    }

    public Object get(String key){
        switch (key){
            case "vibrate":
                return vibrate;
            case "notificationToggle":
                return notificationToggle;
            case "syncEnabled":
                return syncEnabled;
            case "signedIn":
                return signedIn;
            case "autoSync":
                return autoSync;
            case "lastSyncTimeStamp":
                return lastSyncTimeStamp;
            case "selected_categories":
                return selected_categories;
            case "driveId":
                return driveId;
            case "lastReceivedDataTimeStamp":
                return lastReceivedDataTimeStamp;
        }
        return "Error";
    }

    public void toggle(String key){
        switch (key){
            case "vibrate":
                vibrate = !vibrate;
                break;
            case "notificationToggle":
                notificationToggle = !notificationToggle;
                break;
            case "syncEnabled":
                syncEnabled = !syncEnabled;
                break;
            case "autoSync":
                autoSync = !autoSync;
                break;
        }
    }

    public void setCategory(int index, boolean b){
        selected_categories[index] = b;
    }

    public boolean getCategory(int index){
        return selected_categories[index];
    }

    public boolean driveIdStored(){
        //return driveId != null;
        return false;
    }

    public void preventDriveChangeListener(){
        sharedpreferences.edit().putBoolean("preventDriveChangeListener", true).apply();
    }
}
