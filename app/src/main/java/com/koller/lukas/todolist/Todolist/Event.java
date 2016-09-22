package com.koller.lukas.todolist.Todolist;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author (Lukas Koller)
 * @version (1.0)
 */
public class Event {
    private long id;

    private String whatToDo;
    private long whatToDo_timeStamp = 0;

    private int color;
    private long color_timeStamp = 0;

    private long move_timeStamp = 0;

    private Alarm alarm;
    private long alarm_timeStamp = 0;

    public Event(JSONObject json) throws JSONException {
        id = json.getLong("Id");
        whatToDo = json.getString("whatToDo");
        color = json.getInt("Color");

        try {
            whatToDo_timeStamp = json.getLong("whatToDo_timeStamp");
            color_timeStamp = json.getLong("color_timeStamp");
            move_timeStamp = json.getLong("move_timeStamp");
            alarm_timeStamp = json.getLong("alarm_timeStamp");
        } catch (JSONException exception) {
            exception.printStackTrace();
        }

        if (json.getBoolean("alarm")) {

            this.alarm = new Alarm(json.getLong("AlarmId"), json.getLong("AlarmTime"));
            this.alarm.set("lastChange_timeStamp", json.getLong("alarmTime_timeStamp"));

            if (json.getBoolean("repeating")) {
                int repeatMode = json.getInt("repeatMode");
                this.alarm.setRepeating(repeatMode);
                try {
                    if (repeatMode == 3) {
                        this.alarm.restoreCertainDays(json.getString("certainDays"));
                    } else if (repeatMode == 4) {
                        this.alarm.set("custom_intervall", json.getLong("customIntervall"));
                        this.alarm.set("numberPicker1_value", json.getInt("numberPicker1_value"));
                        this.alarm.set("numberPicker2_value", json.getInt("numberPicker2_value"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public Event(String whatToDo,
                 long whatToDo_timeStamp,
                 int color,
                 long color_timeStamp,
                 long id,
                 boolean[] selected_categories) {
        if (id == 0) {
            this.id = System.currentTimeMillis();
        } else {
            this.id = id;
        }
        this.whatToDo = whatToDo;
        this.whatToDo_timeStamp = whatToDo_timeStamp;
        if (color != 0) {
            this.color = color;
        } else if (selected_categories != null) {
            ArrayList<Integer> possible_colors = new ArrayList();
            for (int i = 0; i < selected_categories.length; i++) {
                if (selected_categories[i]) {
                    possible_colors.add(i);
                }
            }
            if (possible_colors.size() == 0) {
                this.color = new Random(System.currentTimeMillis()).nextInt(12) + 1;
            } else if (possible_colors.size() > 1) {
                int color_index = new Random(System.currentTimeMillis()).nextInt(possible_colors.size() - 1) + 1;
                this.color = possible_colors.get(color_index);
            } else if (possible_colors.size() == 1) {
                this.color = possible_colors.get(0);
            }
        } else {
            this.color = new Random(System.currentTimeMillis()).nextInt(12) + 1;
        }
        this.color_timeStamp = color_timeStamp;
    }

    public long getId() {
        return id;
    }

    public String getWhatToDo() {
        return whatToDo;
    }

    public void editWhatToDo(String whatToDo) {
        this.whatToDo = whatToDo;
        whatToDo_timeStamp = System.currentTimeMillis();
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        color_timeStamp = System.currentTimeMillis();
    }

    public void setAlarm(long id, long time) {
        this.alarm = new Alarm(id, time);

        this.alarm_timeStamp = System.currentTimeMillis();
    }

    public void setAlarm(Alarm alarm) {
        this.alarm = alarm;
    }

    public void removeAlarm() {
        this.alarm = null;

        this.alarm_timeStamp = System.currentTimeMillis();
    }

    public Alarm getAlarm() {
        return alarm;
    }

    public boolean hasAlarm() {
        return alarm != null;
    }

    public void update(String whatToDo, long whatToDo_timeStamp,
                       int color, long color_timeStamp) {
        this.whatToDo = whatToDo;
        this.whatToDo_timeStamp = whatToDo_timeStamp;
        this.color = color;
        this.color_timeStamp = color_timeStamp;
    }

    public long getWhatToDo_timeStamp() {
        return whatToDo_timeStamp;
    }

    public long getColor_timeStamp() {
        return color_timeStamp;
    }

    public long getAlarm_timeStamp() {
        return alarm_timeStamp;
    }

    public void setMove_timeStamp(long move_timeStamp) {
        this.move_timeStamp = move_timeStamp;
    }

    public long getMove_timeStamp() {
        return move_timeStamp;
    }

    public JSONObject saveData() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("Id", getId());
        json.put("whatToDo", getWhatToDo());
        json.put("whatToDo_timeStamp", getWhatToDo_timeStamp());
        json.put("Color", getColor());
        json.put("color_timeStamp", getColor_timeStamp());

        json.put("move_timeStamp", getMove_timeStamp());
        json.put("alarm_timeStamp", getAlarm_timeStamp());
        if (alarm != null) {
            if ((long) alarm.get("time") > System.currentTimeMillis()) {
                json.put("alarm", true);

                json.put("AlarmId", (long) alarm.get("id"));
                json.put("AlarmTime", (long) alarm.get("time"));
                json.put("alarmTime_timeStamp", (long) alarm.get("lastChange_timeStamp"));

                json.put("repeating", (boolean) alarm.get("repeating"));
                if ((boolean) alarm.get("repeating")) {
                    json.put("repeatMode", (int) alarm.get("repeatMode"));
                    if ((int) alarm.get("repeatMode") == 3) {
                        json.put("certainDays", alarm.getCertainDaysString());
                    } else if ((int) alarm.get("repeatMode") == 4) {
                        json.put("customIntervall", (long) alarm.get("custom_intervall"));
                        json.put("numberPicker1_value", (int) alarm.get("numberPicker1_value"));
                        json.put("numberPicker2_value", (int) alarm.get("numberPicker2_value"));
                    }
                }
            } else {
                json.put("alarm", false);
            }
        } else {
            json.put("alarm", false);
        }
        return json;
    }
}
