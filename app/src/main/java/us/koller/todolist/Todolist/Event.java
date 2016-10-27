package us.koller.todolist.Todolist;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author (Lukas Koller)
 * @version (1.0)
 */
public class Event {
    private static final String ID = "id";
    private static final String WHAT_TO_DO = "whatToDo";
    private static final String COLOR = "Color";
    private static final String ALARM = "alarm";
    private static final String ALARM_ID = "AlarmId";
    private static final String ALARM_TIME = "AlarmTime";

    private final long id;

    private String whatToDo;
    private int color;
    private Alarm alarm;

    public Event(JSONObject json) throws JSONException {
        id = json.getLong(ID);
        whatToDo = json.getString(WHAT_TO_DO);
        color = json.getInt(COLOR);

        if (json.getBoolean(ALARM)) {
            this.alarm = new Alarm(json.getLong(ALARM_ID), json.getLong(ALARM_TIME));
            if (json.getBoolean(Alarm.REPEATING)) {
                int repeatMode = json.getInt(Alarm.REPEAT_MODE);
                this.alarm.setRepeating(repeatMode);
                try {
                    if (repeatMode == 3) {
                        this.alarm.restoreCertainDays(json.getString(Alarm.CERTAIN_DAYS));
                    } else if (repeatMode == 4) {
                        this.alarm.set(Alarm.CUSTOM_INTERVALL, json.getLong(Alarm.CUSTOM_INTERVALL));
                        this.alarm.set(Alarm.NUMBER_PICKER_1_VALUE, json.getInt(Alarm.NUMBER_PICKER_1_VALUE));
                        this.alarm.set(Alarm.NUMBER_PICKER_2_VALUE, json.getInt(Alarm.NUMBER_PICKER_2_VALUE));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public Event(String whatToDo,
                 int color,
                 long id,
                 boolean[] selected_categories) {
        if (id == 0) {
            this.id = System.currentTimeMillis();
        } else {
            this.id = id;
        }
        this.whatToDo = whatToDo;
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
    }

    public long getId() {
        return id;
    }

    public String getWhatToDo() {
        return whatToDo;
    }

    public void editWhatToDo(String whatToDo) {
        this.whatToDo = whatToDo;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setAlarm(long id, long time) {
        this.alarm = new Alarm(id, time);
    }

    public void setAlarm(Alarm alarm) {
        this.alarm = alarm;
    }

    public void removeAlarm() {
        this.alarm = null;
    }

    public Alarm getAlarm() {
        return alarm;
    }

    public boolean hasAlarm() {
        return alarm != null;
    }

    public JSONObject saveData() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ID, getId());
        json.put(WHAT_TO_DO, getWhatToDo());
        json.put(COLOR, getColor());
        if (alarm != null) {
            if ((long) alarm.get(Alarm.TIME) > System.currentTimeMillis()) {
                json.put(ALARM, true);
                json.put(ALARM_ID, (long) alarm.get(Alarm.ID));
                json.put(ALARM_TIME, (long) alarm.get(Alarm.TIME));
                json.put(Alarm.REPEATING, (boolean) alarm.get(Alarm.REPEATING));
                if ((boolean) alarm.get(Alarm.REPEATING)) {
                    json.put(Alarm.REPEAT_MODE, (int) alarm.get(Alarm.REPEAT_MODE));
                    if ((int) alarm.get(Alarm.REPEAT_MODE) == 3) {
                        json.put(Alarm.CERTAIN_DAYS, alarm.getCertainDaysString());
                    } else if ((int) alarm.get(Alarm.REPEAT_MODE) == 4) {
                        json.put(Alarm.CUSTOM_INTERVALL, (long) alarm.get(Alarm.CUSTOM_INTERVALL));
                        json.put(Alarm.NUMBER_PICKER_1_VALUE, (int) alarm.get(Alarm.NUMBER_PICKER_1_VALUE));
                        json.put(Alarm.NUMBER_PICKER_2_VALUE, (int) alarm.get(Alarm.NUMBER_PICKER_2_VALUE));
                    }
                }
            } else {
                json.put(ALARM, false);
            }
        } else {
            json.put(ALARM, false);
        }
        return json;
    }

    public boolean equals(Event e) {
        boolean b = id == e.getId()
                && whatToDo.equals(e.getWhatToDo())
                && color == e.getColor();
        if (alarm != null) {
            b = b && alarm.equals(e.getAlarm());
        }
        return b;
    }
}
