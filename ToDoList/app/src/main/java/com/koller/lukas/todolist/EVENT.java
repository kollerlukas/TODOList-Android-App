package com.koller.lukas.todolist;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author (Lukas Koller)
 * @version (1.0)
 */
public class EVENT {
    private long id;
    private String WhatToDo;
    private int color;
    private long alarmTimeInMills = 0;
    private long timeStamp = 0;
    private long alarmId; //For canceling a set Alarm
    public boolean semi_transparent = false;
    public boolean is_expanded = false;

    public EVENT(String WhatToDo, int color, long id, boolean [] selected_categories, long timeStamp) {
        if(id == 0){
            this.id = System.currentTimeMillis();
        } else{
            this.id = id;
        }
        this.WhatToDo = WhatToDo;
        if(color != 0){
            this.color = color;
        } else if(selected_categories != null){
            ArrayList<Integer> possible_colors = new ArrayList();
            for (int i = 0; i < selected_categories.length; i++){
                if(selected_categories[i]){
                    possible_colors.add(i);
                }
            }
            if(possible_colors.size() == 0){
                this.color = new Random(System.currentTimeMillis()).nextInt(12) + 1;
            } else if(possible_colors.size() > 1){
                int color_index = new Random(System.currentTimeMillis()).nextInt(possible_colors.size()-1) + 1;
                this.color = possible_colors.get(color_index);
            } else if(possible_colors.size() == 1){
                this.color = possible_colors.get(0);
            }
        } else {
            this.color = new Random(System.currentTimeMillis()).nextInt(12) + 1;
        }
        this.timeStamp = timeStamp;
    }

    public String getWhatToDo() {
        return WhatToDo;
    }

    public void EditWhatToDo(String NewWhatToDo) {
        WhatToDo = NewWhatToDo;
        timeStamp = System.currentTimeMillis();
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        timeStamp = System.currentTimeMillis();
    }

    public long getAlarmTimeInMills() {
        return alarmTimeInMills;
    }

    public void setAlarmTimeInMills(long alarmTimeInMills) {
        this.alarmTimeInMills = alarmTimeInMills;
        timeStamp = System.currentTimeMillis();
    }

    public void setAlarmId(int id) {
        alarmId = id;
    }

    public long getAlarmId() {
        return alarmId;
    }

    public boolean hasAlarm() {
        return !(alarmTimeInMills == 0);
    }

    public long getId(){
        return id;
    }

    public void setIs_expanded(boolean b){
        is_expanded = b;
    }

    public long getTimeStamp(){
        return timeStamp;
    }

    public void update(String WhatToDo, int color, long timeStamp) {
        this.WhatToDo = WhatToDo;
        this.color = color;
        this.timeStamp = timeStamp;
    }

    public void updateAlarm(long AlarmId, long AlarmTime){
        this.alarmId = AlarmId;
        this.alarmTimeInMills = AlarmTime;
    }
}
