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
    private static final String WHAT_TO_DO_TIMESTAMP = "whatToDo_timeStamp";
    private static final String COLOR_TIMESTAMP = "color_timeStamp";
    private static final String MOVE_TIMESTAMP = "move_timeStamp";
    private static final String ALARM_TIMESTAMP = "alarm_timeStamp";
    private static final String ALARM = "alarm";
    private static final String ALARM_ID = "AlarmId";
    private static final String ALARM_TIME = "AlarmTime";
    private static final String ALARM_CHANGE_TIMESTAMP = "lastChange_timeStamp";
    private static final String ALARM_CERTAIN_DAYS = "certainDays";
    private static final String ALARM_CUSTOM_INTERVALL = "customIntervall";

    private final long id;

    private String whatToDo;
    private long whatToDo_timeStamp = 0;

    private int color;
    private long color_timeStamp = 0;

    private long move_timeStamp = 0;

    private Alarm alarm;
    private long alarm_timeStamp = 0;

    public Event(JSONObject json) throws JSONException {
        id = json.getLong(ID);
        whatToDo = json.getString(WHAT_TO_DO);
        color = json.getInt(COLOR);

        try {
            whatToDo_timeStamp = json.getLong(WHAT_TO_DO_TIMESTAMP);
            color_timeStamp = json.getLong(COLOR_TIMESTAMP);
            move_timeStamp = json.getLong(MOVE_TIMESTAMP);
            alarm_timeStamp = json.getLong(ALARM_TIMESTAMP);
        } catch (JSONException exception) {
            exception.printStackTrace();
        }

        if (json.getBoolean(ALARM)) {

            this.alarm = new Alarm(json.getLong(ALARM_ID), json.getLong(ALARM_TIME));
            this.alarm.set(ALARM_CHANGE_TIMESTAMP, json.getLong(ALARM_TIMESTAMP));

            if (json.getBoolean(Alarm.REPEATING)) {
                int repeatMode = json.getInt(Alarm.REPEAT_MODE);
                this.alarm.setRepeating(repeatMode);
                try {
                    if (repeatMode == 3) {
                        this.alarm.restoreCertainDays(json.getString(ALARM_CERTAIN_DAYS));
                    } else if (repeatMode == 4) {
                        this.alarm.set(Alarm.CUSTOM_INTERVALL, json.getLong(ALARM_CUSTOM_INTERVALL));
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

    void setMove_timeStamp(long move_timeStamp) {
        this.move_timeStamp = move_timeStamp;
    }

    public long getMove_timeStamp() {
        return move_timeStamp;
    }

    public JSONObject saveData() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ID, getId());
        json.put(WHAT_TO_DO, getWhatToDo());
        json.put(WHAT_TO_DO_TIMESTAMP, getWhatToDo_timeStamp());
        json.put(COLOR, getColor());
        json.put(COLOR_TIMESTAMP, getColor_timeStamp());

        json.put(MOVE_TIMESTAMP, getMove_timeStamp());
        json.put(ALARM_TIMESTAMP, getAlarm_timeStamp());
        if (alarm != null) {
            if ((long) alarm.get(Alarm.TIME) > System.currentTimeMillis()) {
                json.put(ALARM, true);

                json.put(ALARM_ID, (long) alarm.get(Alarm.ID));
                json.put(ALARM_TIME, (long) alarm.get(Alarm.TIME));
                json.put(ALARM_TIMESTAMP, (long) alarm.get(Alarm.LAST_CHANGE_TIMESTAMP));

                json.put(Alarm.REPEATING, (boolean) alarm.get(Alarm.REPEATING));
                if ((boolean) alarm.get(Alarm.REPEATING)) {
                    json.put(Alarm.REPEAT_MODE, (int) alarm.get(Alarm.REPEAT_MODE));
                    if ((int) alarm.get(Alarm.REPEAT_MODE) == 3) {
                        json.put(ALARM_CERTAIN_DAYS, alarm.getCertainDaysString());
                    } else if ((int) alarm.get(Alarm.REPEAT_MODE) == 4) {
                        json.put(ALARM_CUSTOM_INTERVALL, (long) alarm.get(Alarm.CUSTOM_INTERVALL));
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
}
