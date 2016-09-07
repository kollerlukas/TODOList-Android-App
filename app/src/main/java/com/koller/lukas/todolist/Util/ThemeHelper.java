package com.koller.lukas.todolist.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;

import com.koller.lukas.todolist.R;

import org.json.JSONException;
import org.json.JSONObject;

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
    public int cord_textcolor;

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

    public int defaultColorIndex = 0;

    public long timeStamp = 0;

    public ThemeHelper(Context context) {
        this.context = context;
        readData();
    }

    public ThemeHelper(JSONObject json, Context context) throws JSONException{
        fab_color = json.getInt("fab_color");
        fab_textcolor = json.getInt("fab_textcolor");
        toolbar_color = json.getInt("toolbar_color");
        toolbar_textcolor = json.getInt("toolbar_textcolor");
        cord_color = json.getInt("cord_color");
        cord_textcolor = json.getInt("cord_textcolor");

        color1 = json.getInt("color1");
        color2 = json.getInt("color2");
        color3 = json.getInt("color3");
        color4 = json.getInt("color4");
        color5 = json.getInt("color5");
        color6 = json.getInt("color6");
        color7 = json.getInt("color7");
        color8 = json.getInt("color8");
        color9 = json.getInt("color9");
        color10 = json.getInt("color10");
        color11 = json.getInt("color11");
        color12 = json.getInt("color12");

        textcolor1 = json.getInt("textcolor1");
        textcolor2 = json.getInt("textcolor2");
        textcolor3 = json.getInt("textcolor3");
        textcolor4 = json.getInt("textcolor4");
        textcolor5 = json.getInt("textcolor5");
        textcolor6 = json.getInt("textcolor6");
        textcolor7 = json.getInt("textcolor7");
        textcolor8 = json.getInt("textcolor8");
        textcolor9 = json.getInt("textcolor9");
        textcolor10 = json.getInt("textcolor10");
        textcolor11 = json.getInt("textcolor11");
        textcolor12 = json.getInt("textcolor12");

        this.context = context;
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
        float transparency = 0.2f;
        switch (index) {
            case 1:
                return semiTransparentColor(color1, transparency);
            case 2:
                return semiTransparentColor(color2, transparency);
            case 3:
                return semiTransparentColor(color3, transparency);
            case 4:
                return semiTransparentColor(color4, transparency);
            case 5:
                return semiTransparentColor(color5, transparency);
            case 6:
                return semiTransparentColor(color6, transparency);
            case 7:
                return semiTransparentColor(color7, transparency);
            case 8:
                return semiTransparentColor(color8, transparency);
            case 9:
                return semiTransparentColor(color9, transparency);
            case 10:
                return semiTransparentColor(color10, transparency);
            case 11:
                return semiTransparentColor(color11, transparency);
            default:
                return semiTransparentColor(color12, transparency);
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
        } else if(theme.equals("dark")){
            toolbar_color = ContextCompat.getColor(context, R.color.dark_background);
            toolbar_textcolor = ContextCompat.getColor(context, R.color.white);
        } else {
            toolbar_color = ContextCompat.getColor(context, R.color.black);
            toolbar_textcolor = ContextCompat.getColor(context, R.color.white);
        }
        cord_color = toolbar_color;
        cord_textcolor = toolbar_textcolor;
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
        editor.putInt("cord_textcolor", cord_textcolor);

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

        editor.putInt("defaultColorIndex", defaultColorIndex);
        editor.apply();
    }

    public void readData() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("todolist", Context.MODE_PRIVATE);
        timeStamp = sharedPreferences.getLong("colortimeStamp", System.currentTimeMillis());

        fab_color = sharedPreferences.getInt("fab_color", ContextCompat.getColor(context, R.color.button_color));
        fab_textcolor = sharedPreferences.getInt("fab_textcolor", ContextCompat.getColor(context, R.color.white));
        toolbar_color = sharedPreferences.getInt("toolbar_color", ContextCompat.getColor(context, R.color.white));
        toolbar_textcolor = sharedPreferences.getInt("toolbar_textcolor", ContextCompat.getColor(context, R.color.light_text_color));
        cord_color = sharedPreferences.getInt("cord_color", ContextCompat.getColor(context, R.color.white));
        cord_textcolor = sharedPreferences.getInt("cord_textcolor", ContextCompat.getColor(context, R.color.light_text_color));

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

        defaultColorIndex = sharedPreferences.getInt("defaultColorIndex", 0);
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

    public int semiTransparentColor(int color, float transparency){
        int red, green, blue;
            red = (int) (transparency * Color.red(color) + (1 - transparency) *Color.red(cord_color));
            green = (int) (transparency * Color.green(color) + (1 - transparency) *Color.green(cord_color));
            blue = (int) (transparency * Color.blue(color) + (1 - transparency) *Color.blue(cord_color));
        return Color.rgb(red, green, blue);
    }
}
