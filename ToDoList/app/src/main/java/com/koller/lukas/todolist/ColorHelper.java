package com.koller.lukas.todolist;

import android.content.Context;
import android.support.v4.content.ContextCompat;

/**
 * Created by Lukas on 09.03.2016.
 */
public class ColorHelper {

    private Context context;

    public ColorHelper(Context context){
        this.context = context;
    }

    public int getColor(int color_index) {
        switch (color_index) {
            case 1:
                return ContextCompat.getColor(context, R.color.color1);
            case 2:
                return ContextCompat.getColor(context, R.color.color2);
            case 3:
                return ContextCompat.getColor(context, R.color.color3);
            case 4:
                return ContextCompat.getColor(context, R.color.color4);
            case 5:
                return ContextCompat.getColor(context, R.color.color5);
            case 6:
                return ContextCompat.getColor(context, R.color.color6);
            case 7:
                return ContextCompat.getColor(context, R.color.color7);
            case 8:
                return ContextCompat.getColor(context, R.color.color8);
            case 9:
                return ContextCompat.getColor(context, R.color.color9);
            case 10:
                return ContextCompat.getColor(context, R.color.color10);
            case 11:
                return ContextCompat.getColor(context, R.color.color11);
            default:
                return ContextCompat.getColor(context, R.color.color12);
        }
    }

    public int getColor_semitransparent(int color_index) {
        switch (color_index) {
            case 1:
                return ContextCompat.getColor(context, R.color.color1_semi_transparent);
            case 2:
                return ContextCompat.getColor(context, R.color.color2_semi_transparent);
            case 3:
                return ContextCompat.getColor(context, R.color.color3_semi_transparent);
            case 4:
                return ContextCompat.getColor(context, R.color.color4_semi_transparent);
            case 5:
                return ContextCompat.getColor(context, R.color.color5_semi_transparent);
            case 6:
                return ContextCompat.getColor(context, R.color.color6_semi_transparent);
            case 7:
                return ContextCompat.getColor(context, R.color.color7_semi_transparent);
            case 8:
                return ContextCompat.getColor(context, R.color.color8_semi_transparent);
            case 9:
                return ContextCompat.getColor(context, R.color.color9_semi_transparent);
            case 10:
                return ContextCompat.getColor(context, R.color.color10_semi_transparent);
            case 11:
                return ContextCompat.getColor(context, R.color.color11_semi_transparent);
            default:
                return ContextCompat.getColor(context, R.color.color12_semi_transparent);
        }
    }

    public int getTextColor (int color_index) {
        switch (color_index) {
            case 1:
                return ContextCompat.getColor(context, R.color.dark_text_color);
            case 2:
                return ContextCompat.getColor(context, R.color.dark_text_color);
            case 3:
                return ContextCompat.getColor(context, R.color.dark_text_color);
            case 4:
                return ContextCompat.getColor(context, R.color.dark_text_color);
            case 5:
                return ContextCompat.getColor(context, R.color.light_text_color);
            case 6:
                return ContextCompat.getColor(context, R.color.light_text_color);
            case 7:
                return ContextCompat.getColor(context, R.color.dark_text_color);
            case 8:
                return ContextCompat.getColor(context, R.color.dark_text_color);
            case 9:
                return ContextCompat.getColor(context, R.color.light_text_color);
            case 10:
                return ContextCompat.getColor(context, R.color.dark_text_color);
            case 11:
                return ContextCompat.getColor(context, R.color.dark_text_color);
            default:
                return ContextCompat.getColor(context, R.color.dark_text_color);
        }
    }

    public int getTextColor_semitransparent (int color_index) {
        switch (color_index) {
            case 1:
                return ContextCompat.getColor(context, R.color.dark_text_color_semi_transparent);
            case 2:
                return ContextCompat.getColor(context, R.color.dark_text_color_semi_transparent);
            case 3:
                return ContextCompat.getColor(context, R.color.dark_text_color_semi_transparent);
            case 4:
                return ContextCompat.getColor(context, R.color.dark_text_color_semi_transparent);
            case 5:
                return ContextCompat.getColor(context, R.color.light_text_color_semi_transparent);
            case 6:
                return ContextCompat.getColor(context, R.color.light_text_color_semi_transparent);
            case 7:
                return ContextCompat.getColor(context, R.color.dark_text_color_semi_transparent);
            case 8:
                return ContextCompat.getColor(context, R.color.dark_text_color_semi_transparent);
            case 9:
                return ContextCompat.getColor(context, R.color.light_text_color_semi_transparent);
            case 10:
                return ContextCompat.getColor(context, R.color.dark_text_color_semi_transparent);
            case 11:
                return ContextCompat.getColor(context, R.color.dark_text_color_semi_transparent);
            default:
                return ContextCompat.getColor(context, R.color.dark_text_color_semi_transparent);
        }
    }
}
