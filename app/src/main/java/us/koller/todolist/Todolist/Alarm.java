package us.koller.todolist.Todolist;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by Lukas on 22.08.2016.
 */
public class Alarm {

    public static final String ID = "id";
    public static final String TIME = "time";
    public static final String REPEATING = "repeating";
    public static final String REPEAT_MODE = "repeatMode";
    static final String CERTAIN_DAYS = "certain_days";
    public static final String CUSTOM_INTERVALL = "custom_intervall";
    public static final String NUMBER_PICKER_1_VALUE = "numberPicker1_value";
    public static final String NUMBER_PICKER_2_VALUE = "numberPicker2_value";

    private long id;
    private long time;

    private boolean repeating = false;
    private int repeatMode; //0: daily; 1: weekly; 2: monthly; 3: certain days of the week; 4: custom intervall;

    private boolean[] certain_days = new boolean[7] ;

    private long custom_intervall;
    private int numberPicker1_value;
    private int numberPicker2_value;

    Alarm(long id, long time) {
        this.id = id;
        this.time = time;
    }

    public void setTime(long time){
        this.time = time;
    }

    public void setRepeating(int repeatMode) {
        repeating = true;
        this.repeatMode = repeatMode;
    }

    public void unRepeat() {
        repeating = false;
        repeatMode = 0;
        certain_days = null;
        custom_intervall = 0;
    }

    void restoreCertainDays(String s) {
        String[] strings = s.split(";");
        if (strings.length == 7) {
            for (int i = 0; i < certain_days.length; i++) {
                certain_days[i] = Boolean.valueOf(strings[i]);
            }
        }
    }

    String getCertainDaysString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < certain_days.length; i++) {
            sb.append(certain_days[i]);
            sb.append(";");
        }
        return sb.toString();
    }

    public long nextAlarmTime() {
        //only works if an alarm just fired
        if (!repeating) {
            return 0;
        }

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        if (repeatMode == 0 || repeatMode == 1 || repeatMode == 2) {
            int field = Calendar.DATE;
            switch (repeatMode) {
                case 1:
                    field = Calendar.WEEK_OF_YEAR;
                    break;
                case 2:
                    field = Calendar.MONTH;
                    break;
            }
            calendar.add(field, 1);

        } else if (repeatMode == 3) {
            boolean nextDayFound = false;
            int daysUntilNextAlarm = 1;
            int day = getDayOfTheWeekIndex();
            if (day == 6) {
                day = 0;
            } else {
                day++;
            }
            while (!nextDayFound) {
                if (certain_days[day]) {
                    nextDayFound = true;
                } else {
                    if (day == 6) {
                        day = 0;
                    } else {
                        day++;
                    }
                    daysUntilNextAlarm++;
                }
            }
            calendar.add(Calendar.DAY_OF_MONTH, daysUntilNextAlarm);

        } else if (repeatMode == 4) {
            calendar.add(Calendar.MILLISECOND, (int) custom_intervall);
        }

        return calendar.getTimeInMillis();
    }

    private int getDayOfTheWeekIndex() {
        int day = Calendar.getInstance(TimeZone.getDefault()).get(Calendar.DAY_OF_WEEK);
        switch (day) {
            case Calendar.MONDAY:
                return 0;
            case Calendar.TUESDAY:
                return 1;
            case Calendar.WEDNESDAY:
                return 2;
            case Calendar.THURSDAY:
                return 3;
            case Calendar.FRIDAY:
                return 4;
            case Calendar.SATURDAY:
                return 5;
            default:
                return 6;
        }
    }

    public boolean equals(Alarm alarm){
        if(alarm != null){
            return this.id == alarm.id ||
                    this.time == alarm.time ||
                    this.repeating == alarm.repeating ||
                    this.repeatMode == alarm.repeatMode ||
                    equalsCertainDays(alarm.certain_days) ||
                    this.custom_intervall == alarm.custom_intervall;
        }
        return false;
    }

    private boolean equalsCertainDays(boolean[] certain_days){
        for (int i = 0; i < this.certain_days.length; i++){
            if(this.certain_days[i] != certain_days[i]){
                return false;
            }
        }
        return true;
    }

    public void set(String key, Object o){
        switch (key){
            case CUSTOM_INTERVALL:
                custom_intervall = (long) o;
                break;
            case NUMBER_PICKER_1_VALUE:
                numberPicker1_value = (int) o;
                break;
            case NUMBER_PICKER_2_VALUE:
                numberPicker2_value = (int) o;
                break;
        }
    }

    public Object get(String key){
        switch (key){
            case ID:
                return id;
            case TIME:
                return time;
            case REPEATING:
                return repeating;
            case REPEAT_MODE:
                return repeatMode;
            case CUSTOM_INTERVALL:
                return custom_intervall;
            case NUMBER_PICKER_1_VALUE:
                return numberPicker1_value;
            case NUMBER_PICKER_2_VALUE:
                return numberPicker2_value;
        }
        return "Error";
    }

    public void setCertainDay(int index, boolean b){
        if(index < 7){
            certain_days[index] = b;
        }
    }

    public boolean getCertainDay(int index) {
        return index < 7 && certain_days[index];
    }

    public boolean noDaySelected(){
        if(certain_days == null){
            return true;
        }
        for (int i = 0; i < certain_days.length; i++){
            if(certain_days[i]){
                return false;
            }
        }
        return true;
    }
}
