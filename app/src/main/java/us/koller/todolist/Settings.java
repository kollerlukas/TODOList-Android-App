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
    public static final String SYNC_TOGGLE = "sync_toggle";
    public static final String WAS_EVER_SYNCED = "wasEverSynced";
    public static final String SELECTED_CATEGORIES = "selected_categories";

    private SharedPreferences sharedpreferences;

    private boolean vibrate;
    private boolean notificationToggle;
    private boolean[] selected_categories;

    private boolean syncEnabled; //if user was signed in; App might not be signed in yet
    private boolean signedIn = false; //app successfully signed in
    private boolean wasEverSynced = false;
    private boolean sync_toggle;

    public Settings(Context context) {
        sharedpreferences = context.getSharedPreferences("todolist", Context.MODE_PRIVATE);
    }

    public void readSettings() {
        vibrate = sharedpreferences.getBoolean(VIBRATE, true);
        notificationToggle = sharedpreferences.getBoolean(NOTIFICATION_TOGGLE, true);

        syncEnabled = sharedpreferences.getBoolean(SYNC_ENABLED, false);
        sync_toggle = sharedpreferences.getBoolean(SYNC_TOGGLE, true);
        wasEverSynced = sharedpreferences.getBoolean(WAS_EVER_SYNCED, false);

        readSelectedCategories("no data");
    }

    public void readSelectedCategories(String data) {
        selected_categories = new boolean[13];
        if(data.equals("no data")){
            data = sharedpreferences.getString(SELECTED_CATEGORIES, "");
        }
        try {
            JSONObject json = new JSONObject(data);
            for (int i = 0; i < selected_categories.length; i++){
                selected_categories[i] = json.getBoolean(SELECTED_CATEGORIES + i);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                .putBoolean(SYNC_TOGGLE, sync_toggle)
                .putBoolean(WAS_EVER_SYNCED, wasEverSynced)
                .apply();

        try {
            saveCategorySettings();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveCategorySettings() throws JSONException {
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
            case SYNC_TOGGLE:
                sync_toggle = (boolean) o;
                break;
            case WAS_EVER_SYNCED:
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
            case SYNC_TOGGLE:
                return sync_toggle;
            case WAS_EVER_SYNCED:
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
            case SYNC_TOGGLE:
                sync_toggle = !sync_toggle;
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
