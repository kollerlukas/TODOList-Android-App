package com.koller.lukas.todolist;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.drive.DriveId;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by Lukas on 31.08.2016.
 */
public class Settings {

    private SharedPreferences sharedpreferences;

    public boolean vibrate;
    public boolean showNotification;
    public boolean syncEnabled = false;

    public boolean autoSync;

    public long lastSyncTimeStamp;

    public boolean importTutorialDialogShown;

    public boolean[] selected_categories;

    public DriveId driveId;

    private Context context;

    public Settings(Context context){
        sharedpreferences
                = context.getSharedPreferences("todolist", Context.MODE_PRIVATE);

        this.context = context;
    }

    public void readSettings() {
        vibrate
                = sharedpreferences.getBoolean("vibrate", true);
        showNotification
                = sharedpreferences.getBoolean("showNotification", true);
        autoSync
                = sharedpreferences.getBoolean("autoSync", false);
        lastSyncTimeStamp
                = sharedpreferences.getLong("lastSyncTimeStamp", 0);
        importTutorialDialogShown
                = sharedpreferences.getBoolean("importTutorialDialogShown", false);

        String driveIdString
                = sharedpreferences.getString("DriveId driveId", "Error");
        if(!driveIdString.equals("Error")){
            driveId = DriveId.decodeFromString(driveIdString);
        } else {
            driveId = null;
        }

        readSelectedCategories();
    }

    public void readSelectedCategories() {
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
            readSelectedCategoreies_old();
        }
    }

    public void saveSettings() {
        SharedPreferences.Editor editor
                = sharedpreferences.edit();
        editor.putBoolean("vibrate", vibrate);
        editor.putBoolean("showNotification", showNotification);
        editor.putBoolean("autoSync", autoSync);
        editor.putLong("lastSyncTimeStamp", lastSyncTimeStamp);
        editor.putBoolean("importTutorialDialogShown", importTutorialDialogShown);

        if(driveId != null){
            String driveIdString = driveId.encodeToString();
            editor.putString("driveId", driveIdString);
        }

        editor.apply();

        saveCategorySettings();
    }

    public void saveCategorySettings() {
        JSONArray array = new JSONArray();
        for (int i = 0; i < selected_categories.length; i++){
            array.put(selected_categories[i]);
        }

        SharedPreferences.Editor editor
                = sharedpreferences.edit();
        editor.putString("selected_categories", array.toString());
        editor.apply();
    }

    //old; that the update correctly imports old data
    public void readSelectedCategoreies_old(){
        selected_categories = new boolean[13];
        for (int i = 0; i < sharedpreferences.getInt("selected_categories.length", 0); i++) {
            selected_categories[i] = sharedpreferences.getBoolean("category" + i + "selected", true);
        }
    }

    public void set(String key, boolean b){

    }

    public void get(String key){

    }
}
