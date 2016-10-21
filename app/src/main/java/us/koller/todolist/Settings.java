package us.koller.todolist;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Lukas on 31.08.2016.
 */
public class Settings {

    public static final String VIBRATE = "vibrate";
    public static final String NOTIFICATION_TOGGLE = "notificationToggle";
    public static final String SYNC_ENABLED = "syncEnabled";
    public static final String SIGNED_IN = "signedIn";
    public static final String AUTO_SYNC = "autoSync";
    public static final String WAS_EVER_SNYCED = "wasEverSynced";
    public static final String SELECTED_CATEGORIES = "selected_categories";

    private SharedPreferences sharedpreferences;

    private boolean vibrate;
    private boolean notificationToggle;
    private boolean[] selected_categories;

    private boolean syncEnabled; //if user was signed in; App might not be signed in yet
    private boolean signedIn = false; //app successfully signed in
    private boolean wasEverSynced = false;
    private boolean autoSync;

    public Settings(Context context) {
        sharedpreferences = context.getSharedPreferences("todolist", Context.MODE_PRIVATE);
    }

    public void readSettings() {
        vibrate = sharedpreferences.getBoolean(VIBRATE, true);
        notificationToggle = sharedpreferences.getBoolean(NOTIFICATION_TOGGLE, true);

        syncEnabled = sharedpreferences.getBoolean(SYNC_ENABLED, false);
        autoSync = sharedpreferences.getBoolean(AUTO_SYNC, true);
        wasEverSynced = sharedpreferences.getBoolean(WAS_EVER_SNYCED, false);

        readSelectedCategories("no data");
    }

    public void readSelectedCategories(String data) {
        selected_categories = new boolean[13];
        if(data.equals("no data")){
            data = sharedpreferences.getString(SELECTED_CATEGORIES, "");
        }
        try {
            /*JSONArray array = new JSONArray(s);

            for (int i = 0; i < selected_categories.length; i++) {
                selected_categories[i] = array.getBoolean(i);
            }*/
            JSONObject json = new JSONObject(data);
            for (int i = 0; i < selected_categories.length; i++){
                selected_categories[i] = json.getBoolean(SELECTED_CATEGORIES + i);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getCategoryCount(){
        return selected_categories.length;
    }

    public JSONObject getSelectedCategoriesJSON() throws JSONException {
        JSONObject json = new JSONObject();
        for (int i = 0; i < selected_categories.length; i++){
            json.put(SELECTED_CATEGORIES + i, selected_categories[i]);
        }
        return json;
    }

    public void saveSettings() {
        sharedpreferences.edit()
                .putBoolean(VIBRATE, vibrate)
                .putBoolean(NOTIFICATION_TOGGLE, notificationToggle)
                .putBoolean(SYNC_ENABLED, syncEnabled)
                .putBoolean(AUTO_SYNC, autoSync)
                .putBoolean(WAS_EVER_SNYCED, wasEverSynced)
                .apply();

        try {
            saveCategorySettings();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveCategorySettings() throws JSONException {
        /*JSONArray array = new JSONArray();
        for (int i = 0; i < selected_categories.length; i++) {
            array.put(selected_categories[i]);
        }*/

        SharedPreferences.Editor editor
                = sharedpreferences.edit();
        editor.putString(SELECTED_CATEGORIES, getSelectedCategoriesJSON().toString());
        editor.apply();
    }

    public void set(String key, Object o) {
        switch (key) {
            case VIBRATE:
                vibrate = (boolean) o;
                break;
            case NOTIFICATION_TOGGLE:
                notificationToggle = (boolean) o;
                break;
            case SYNC_ENABLED:
                syncEnabled = (boolean) o;
                break;
            case SELECTED_CATEGORIES:
                selected_categories = (boolean[]) o;
                break;
            case SIGNED_IN:
                signedIn = (boolean) o;
                break;
            case AUTO_SYNC:
                autoSync = (boolean) o;
                break;
            case WAS_EVER_SNYCED:
                wasEverSynced = (boolean) o;
                break;
        }
    }

    public Object get(String key) {
        switch (key) {
            case VIBRATE:
                return vibrate;
            case NOTIFICATION_TOGGLE:
                return notificationToggle;
            case SYNC_ENABLED:
                return syncEnabled;
            case SIGNED_IN:
                return signedIn;
            case AUTO_SYNC:
                return autoSync;
            case WAS_EVER_SNYCED:
                return wasEverSynced;
            case SELECTED_CATEGORIES:
                return selected_categories;
        }
        return "Error";
    }

    public void toggle(String key) {
        switch (key) {
            case VIBRATE:
                vibrate = !vibrate;
                break;
            case NOTIFICATION_TOGGLE:
                notificationToggle = !notificationToggle;
                break;
            case SYNC_ENABLED:
                syncEnabled = !syncEnabled;
                break;
            case AUTO_SYNC:
                autoSync = !autoSync;
                break;
        }
    }

    public void setCategory(int index, boolean b) {
        selected_categories[index] = b;
    }

    public boolean getCategory(int index) {
        return selected_categories[index];
    }
}
