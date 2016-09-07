package com.koller.lukas.todolist;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Lukas on 13.06.2016.
 */
public class ThemeHelper {
    private Context context;

    public int fab_color;
    public int fab_textcolor;

    public int toolbar_color;
    public int toolbar_textcolor;

    public int cord_color;

    public int color1;
    public int color2;
    public int color3;
    public int color4;
    public int color5;
    public int color6;
    public int color7;
    public int color8;
    public int color9;
    public int color10;
    public int color11;
    public int color12;

    public int textcolor1;
    public int textcolor2;
    public int textcolor3;
    public int textcolor4;
    public int textcolor5;
    public int textcolor6;
    public int textcolor7;
    public int textcolor8;
    public int textcolor9;
    public int textcolor10;
    public int textcolor11;
    public int textcolor12;

    public long timeStamp = 0;

    public ThemeHelper(Context context) {
        this.context = context;
        readData();
    }

    public int getEventColor(int index) {
        switch (index) {
            case 1:
                return color1;
            case 2:
                return color2;
            case 3:
                return color3;
            case 4:
                return color4;
            case 5:
                return color5;
            case 6:
                return color6;
            case 7:
                return color7;
            case 8:
                return color8;
            case 9:
                return color9;
            case 10:
                return color10;
            case 11:
                return color11;
            default:
                return color12;
        }
    }

    public int getEventColor_semitransparent(int index) {
        switch (index) {
            case 1:
                return Color.argb(60, Color.red(color1), Color.green(color1), Color.blue(color1));
            case 2:
                return Color.argb(60, Color.red(color2), Color.green(color2), Color.blue(color2));
            case 3:
                return Color.argb(60, Color.red(color3), Color.green(color3), Color.blue(color3));
            case 4:
                return Color.argb(60, Color.red(color4), Color.green(color4), Color.blue(color4));
            case 5:
                return Color.argb(60, Color.red(color5), Color.green(color5), Color.blue(color5));
            case 6:
                return Color.argb(60, Color.red(color6), Color.green(color6), Color.blue(color6));
            case 7:
                return Color.argb(60, Color.red(color7), Color.green(color7), Color.blue(color7));
            case 8:
                return Color.argb(60, Color.red(color8), Color.green(color8), Color.blue(color8));
            case 9:
                return Color.argb(60, Color.red(color9), Color.green(color9), Color.blue(color9));
            case 10:
                return Color.argb(60, Color.red(color10), Color.green(color10), Color.blue(color10));
            case 11:
                return Color.argb(60, Color.red(color11), Color.green(color11), Color.blue(color11));
            default:
                return Color.argb(60, Color.red(color12), Color.green(color12), Color.blue(color12));
        }
    }

    public int getEventTextColor(int index) {
        switch (index) {
            case 1:
                return textcolor1;
            case 2:
                return textcolor2;
            case 3:
                return textcolor3;
            case 4:
                return textcolor4;
            case 5:
                return textcolor5;
            case 6:
                return textcolor6;
            case 7:
                return textcolor7;
            case 8:
                return textcolor8;
            case 9:
                return textcolor9;
            case 10:
                return textcolor10;
            case 11:
                return textcolor11;
            default:
                return textcolor12;
        }
    }

    public int getEventTextColor_semitransparent(int index) {
        switch (index) {
            case 1:
                return Color.argb(60, Color.red(textcolor1), Color.green(textcolor1), Color.blue(textcolor1));
            case 2:
                return Color.argb(60, Color.red(textcolor2), Color.green(textcolor2), Color.blue(textcolor2));
            case 3:
                return Color.argb(60, Color.red(textcolor3), Color.green(textcolor3), Color.blue(textcolor3));
            case 4:
                return Color.argb(60, Color.red(textcolor4), Color.green(textcolor4), Color.blue(textcolor4));
            case 5:
                return Color.argb(60, Color.red(textcolor5), Color.green(textcolor5), Color.blue(textcolor5));
            case 6:
                return Color.argb(60, Color.red(textcolor6), Color.green(textcolor6), Color.blue(textcolor6));
            case 7:
                return Color.argb(60, Color.red(textcolor7), Color.green(textcolor7), Color.blue(textcolor7));
            case 8:
                return Color.argb(60, Color.red(textcolor8), Color.green(textcolor8), Color.blue(textcolor8));
            case 9:
                return Color.argb(60, Color.red(textcolor9), Color.green(textcolor9), Color.blue(textcolor9));
            case 10:
                return Color.argb(60, Color.red(textcolor10), Color.green(textcolor10), Color.blue(textcolor10));
            case 11:
                return Color.argb(60, Color.red(textcolor11), Color.green(textcolor11), Color.blue(textcolor11));
            default:
                return Color.argb(60, Color.red(textcolor12), Color.green(textcolor12), Color.blue(textcolor12));
        }
    }

    public int rgbSum(){
        return Color.red(cord_color) + Color.green(cord_color) + Color.blue(cord_color);
    }

    public boolean lightCordColor(){
        return Color.red(cord_color) + Color.green(cord_color) + Color.blue(cord_color) > 510;
    }

    public void restoreDefaultTheme(String theme) {
        if(theme.equals("light")){
            toolbar_color = ContextCompat.getColor(context, R.color.white);
            toolbar_textcolor = ContextCompat.getColor(context, R.color.black);
            cord_color = ContextCompat.getColor(context, R.color.white);
        } else if(theme.equals("dark")){
            toolbar_color = ContextCompat.getColor(context, R.color.dark_background);
            toolbar_textcolor = ContextCompat.getColor(context, R.color.white);
            cord_color = ContextCompat.getColor(context, R.color.dark_background);
        } else {
            toolbar_color = ContextCompat.getColor(context, R.color.black);
            toolbar_textcolor = ContextCompat.getColor(context, R.color.white);
            cord_color = ContextCompat.getColor(context, R.color.black);
        }
        fab_color = ContextCompat.getColor(context, R.color.button_color);
        fab_textcolor = ContextCompat.getColor(context, R.color.white);

        color1 = ContextCompat.getColor(context, R.color.color1);
        color2 = ContextCompat.getColor(context, R.color.color2);
        color3 = ContextCompat.getColor(context, R.color.color3);
        color4 = ContextCompat.getColor(context, R.color.color4);
        color5 = ContextCompat.getColor(context, R.color.color5);
        color6 = ContextCompat.getColor(context, R.color.color6);
        color7 = ContextCompat.getColor(context, R.color.color7);
        color8 = ContextCompat.getColor(context, R.color.color8);
        color9 = ContextCompat.getColor(context, R.color.color9);
        color10 = ContextCompat.getColor(context, R.color.color10);
        color11 = ContextCompat.getColor(context, R.color.color11);
        color12 = ContextCompat.getColor(context, R.color.color12);

        textcolor1 = ContextCompat.getColor(context, R.color.dark_text_color);
        textcolor2 = ContextCompat.getColor(context, R.color.dark_text_color);
        textcolor3 = ContextCompat.getColor(context, R.color.dark_text_color);
        textcolor4 = ContextCompat.getColor(context, R.color.dark_text_color);
        textcolor5 = ContextCompat.getColor(context, R.color.light_text_color);
        textcolor6 = ContextCompat.getColor(context, R.color.light_text_color);
        textcolor7 = ContextCompat.getColor(context, R.color.dark_text_color);
        textcolor8 = ContextCompat.getColor(context, R.color.dark_text_color);
        textcolor9 = ContextCompat.getColor(context, R.color.light_text_color);
        textcolor10 = ContextCompat.getColor(context, R.color.dark_text_color);
        textcolor11 = ContextCompat.getColor(context, R.color.dark_text_color);
        textcolor12 = ContextCompat.getColor(context, R.color.dark_text_color);
    }

    public void saveData() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("todolist", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        timeStamp = System.currentTimeMillis();
        editor.putLong("colortimeStamp", timeStamp);

        editor.putInt("fab_color", fab_color);
        editor.putInt("fab_textcolor", fab_textcolor);
        editor.putInt("toolbar_color", toolbar_color);
        editor.putInt("toolbar_textcolor", toolbar_textcolor);
        editor.putInt("cord_color", cord_color);

        editor.putInt("color1", color1);
        editor.putInt("color2", color2);
        editor.putInt("color3", color3);
        editor.putInt("color4", color4);
        editor.putInt("color5", color5);
        editor.putInt("color6", color6);
        editor.putInt("color7", color7);
        editor.putInt("color8", color8);
        editor.putInt("color9", color9);
        editor.putInt("color10", color10);
        editor.putInt("color11", color11);
        editor.putInt("color12", color12);

        editor.putInt("textcolor1", textcolor1);
        editor.putInt("textcolor2", textcolor2);
        editor.putInt("textcolor3", textcolor3);
        editor.putInt("textcolor4", textcolor4);
        editor.putInt("textcolor5", textcolor5);
        editor.putInt("textcolor6", textcolor6);
        editor.putInt("textcolor7", textcolor7);
        editor.putInt("textcolor8", textcolor8);
        editor.putInt("textcolor9", textcolor9);
        editor.putInt("textcolor10", textcolor10);
        editor.putInt("textcolor11", textcolor11);
        editor.putInt("textcolor12", textcolor12);
        editor.apply();
    }

    public void readData() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("todolist", Context.MODE_PRIVATE);
        timeStamp = sharedPreferences.getLong("colortimeStamp", System.currentTimeMillis());

        fab_color = sharedPreferences.getInt("fab_color", ContextCompat.getColor(context, R.color.button_color));
        fab_textcolor = sharedPreferences.getInt("fab_textcolor", ContextCompat.getColor(context, R.color.white));
        toolbar_color = sharedPreferences.getInt("toolbar_color", ContextCompat.getColor(context, R.color.dark_background));
        toolbar_textcolor = sharedPreferences.getInt("toolbar_textcolor", ContextCompat.getColor(context, R.color.white));
        cord_color = sharedPreferences.getInt("cord_color", ContextCompat.getColor(context, R.color.dark_background));

        color1 = sharedPreferences.getInt("color1", ContextCompat.getColor(context, R.color.color1));
        color2 = sharedPreferences.getInt("color2", ContextCompat.getColor(context, R.color.color2));
        color3 = sharedPreferences.getInt("color3", ContextCompat.getColor(context, R.color.color3));
        color4 = sharedPreferences.getInt("color4", ContextCompat.getColor(context, R.color.color4));
        color5 = sharedPreferences.getInt("color5", ContextCompat.getColor(context, R.color.color5));
        color6 = sharedPreferences.getInt("color6", ContextCompat.getColor(context, R.color.color6));
        color7 = sharedPreferences.getInt("color7", ContextCompat.getColor(context, R.color.color7));
        color8 = sharedPreferences.getInt("color8", ContextCompat.getColor(context, R.color.color8));
        color9 = sharedPreferences.getInt("color9", ContextCompat.getColor(context, R.color.color9));
        color10 = sharedPreferences.getInt("color10", ContextCompat.getColor(context, R.color.color10));
        color11 = sharedPreferences.getInt("color11", ContextCompat.getColor(context, R.color.color11));
        color12 = sharedPreferences.getInt("color12", ContextCompat.getColor(context, R.color.color12));

        textcolor1 = sharedPreferences.getInt("textcolor1", ContextCompat.getColor(context, R.color.dark_text_color));
        textcolor2 = sharedPreferences.getInt("textcolor2", ContextCompat.getColor(context, R.color.dark_text_color));
        textcolor3 = sharedPreferences.getInt("textcolor3", ContextCompat.getColor(context, R.color.dark_text_color));
        textcolor4 = sharedPreferences.getInt("textcolor4", ContextCompat.getColor(context, R.color.dark_text_color));
        textcolor5 = sharedPreferences.getInt("textcolor5", ContextCompat.getColor(context, R.color.light_text_color));
        textcolor6 = sharedPreferences.getInt("textcolor6", ContextCompat.getColor(context, R.color.light_text_color));
        textcolor7 = sharedPreferences.getInt("textcolor7", ContextCompat.getColor(context, R.color.dark_text_color));
        textcolor8 = sharedPreferences.getInt("textcolor8", ContextCompat.getColor(context, R.color.dark_text_color));
        textcolor9 = sharedPreferences.getInt("textcolor9", ContextCompat.getColor(context, R.color.light_text_color));
        textcolor10 = sharedPreferences.getInt("textcolor10", ContextCompat.getColor(context, R.color.dark_text_color));
        textcolor11 = sharedPreferences.getInt("textcolor11", ContextCompat.getColor(context, R.color.dark_text_color));
        textcolor12 = sharedPreferences.getInt("textcolor12", ContextCompat.getColor(context, R.color.dark_text_color));
    }

    public void setColors(int [] newColors){
        color1 = newColors[1];
        color2 = newColors[2];
        color3 = newColors[3];
        color4 = newColors[4];
        color5 = newColors[5];
        color6 = newColors[6];
        color7 = newColors[7];
        color8 = newColors[8];
        color9 = newColors[9];
        color10 = newColors[10];
        color11 = newColors[11];
        color12 = newColors[12];
    }

    public void setTextColors(int [] newTextColors){
        textcolor1 = newTextColors[1];
        textcolor2 = newTextColors[2];
        textcolor3 = newTextColors[3];
        textcolor4 = newTextColors[4];
        textcolor5 = newTextColors[5];
        textcolor6 = newTextColors[6];
        textcolor7 = newTextColors[7];
        textcolor8 = newTextColors[8];
        textcolor9 = newTextColors[9];
        textcolor10 = newTextColors[10];
        textcolor11 = newTextColors[11];
        textcolor12 = newTextColors[12];
    }
}
