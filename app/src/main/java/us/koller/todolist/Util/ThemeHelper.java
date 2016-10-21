package us.koller.todolist.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import us.koller.todolist.R;

/**
 * Created by Lukas on 13.06.2016.
 */
public class ThemeHelper {
    private static final String COLOR_TIMESTAMP = "colortimeStamp";

    public static final String FAB_COLOR = "fab_color";
    public static final String FAB_TEXT_COLOR = "fab_textcolor";
    public static final String TOOLBAR_COLOR = "toolbar_color";
    public static final String TOOLBAR_TEXT_COLOR = "toolbar_textcolor";
    public static final String CORD_COLOR = "cord_color";
    public static final String CORD_TEXT_COLOR = "cord_textcolor";

    public static final String COLOR = "color";
    public static final String TEXT_COLOR = "textcolor";
    private static final String DEFAULT_COLOR_INDEX = "defaultColorIndex";
    private static final String TOOLBAR_ICONS_TRANSLUCENT = "toolbarIconsTranslucent";

    public static final String LIGHT = "light";
    public static final String DARK = "dark";
    public static final String BLACK = "black";

    private int fab_color;
    private int fab_textcolor;

    private int toolbar_color;
    private int toolbar_textcolor;

    private int cord_color;
    private int cord_textcolor;

    private int[] colors;
    private int[] textColors;

    private int defaultColorIndex;

    private long timeStamp = 0;

    private boolean toolbarIconsTranslucent = false;

    public ThemeHelper(Context context) {
        colors = new int[13];
        textColors = new int[13];

        readData(context);
    }

    public ThemeHelper(JSONObject json) throws JSONException {
        fab_color = json.getInt(FAB_COLOR);
        fab_textcolor = json.getInt(FAB_TEXT_COLOR);
        toolbar_color = json.getInt(TOOLBAR_COLOR);
        toolbar_textcolor = json.getInt(TOOLBAR_TEXT_COLOR);
        cord_color = json.getInt(CORD_COLOR);
        cord_textcolor = json.getInt(CORD_TEXT_COLOR);

        colors = new int[13];
        textColors = new int[13];

        for (int i = 1; i < colors.length; i++) {
            colors[i] = json.getInt(COLOR + i);
        }

        for (int i = 1; i < textColors.length; i++) {
            textColors[i] = json.getInt(TEXT_COLOR + i);
        }
    }

    public ThemeHelper(Context context, int colorIndex) {
        colors = new int[13];
        textColors = new int[13];

        readColor(context, colorIndex);
    }

    public int getEventColor(int index) {
        return colors[index];
    }

    public int getEventColor_semitransparent(int index) {
        float transparency = 0.2f;
        return semiTransparentColor(colors[index], transparency);
    }

    public int getEventTextColor(int index) {
        return textColors[index];
    }

    public int getEventTextColor_semitransparent(int index) {
        int textColor = textColors[index];
        return Color.argb(60, Color.red(textColor), Color.green(textColor), Color.blue(textColor));
    }

    public boolean lightCoordColor() {
        return isColorLight(cord_color);
    }

    public void restoreDefaultTheme(Context context, String theme) {
        switch (theme) {
            case LIGHT:
                toolbar_color = ContextCompat.getColor(context, R.color.white);
                toolbar_textcolor = getDarkTextColor();
                break;
            case DARK:
                toolbar_color = ContextCompat.getColor(context, R.color.dark_background);
                toolbar_textcolor = getLightTextColor();
                break;
            default:
                toolbar_color = ContextCompat.getColor(context, R.color.black);
                toolbar_textcolor = getLightTextColor();
                break;
        }

        cord_color = toolbar_color;
        cord_textcolor = toolbar_textcolor;

        fab_color = ContextCompat.getColor(context, R.color.button_color);
        fab_textcolor = getLightTextColor();

        for (int i = 1; i < colors.length; i++) {
            colors[i] = getDefaultColors(context, i);
        }

        for (int i = 0; i < textColors.length; i++) {
            textColors[i] = getDefaultTextColors(i);

        }
    }

    public void saveData(Context context) {
        timeStamp = System.currentTimeMillis();
        SharedPreferences sharedPreferences = context.getSharedPreferences("todolist", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(COLOR_TIMESTAMP, timeStamp)
                .putInt(FAB_COLOR, fab_color)
                .putInt(FAB_TEXT_COLOR, fab_textcolor)
                .putInt(TOOLBAR_COLOR, toolbar_color)
                .putInt(TOOLBAR_TEXT_COLOR, toolbar_textcolor)
                .putInt(CORD_COLOR, cord_color)
                .putInt(CORD_TEXT_COLOR, cord_textcolor);

        for (int i = 1; i < colors.length; i++) {
            editor.putInt(COLOR + i, colors[i]);
        }

        for (int i = 1; i < textColors.length; i++) {
            editor.putInt(TEXT_COLOR + i, textColors[i]);
        }

        editor.putInt(DEFAULT_COLOR_INDEX, defaultColorIndex);
        editor.putBoolean(TOOLBAR_ICONS_TRANSLUCENT, toolbarIconsTranslucent);
        editor.apply();
    }

    private void readData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("todolist", Context.MODE_PRIVATE);
        timeStamp = sharedPreferences.getLong(COLOR_TIMESTAMP, System.currentTimeMillis());

        fab_color = sharedPreferences.getInt(FAB_COLOR, ContextCompat.getColor(context, R.color.button_color));
        fab_textcolor = sharedPreferences.getInt(FAB_TEXT_COLOR, getLightTextColor());
        toolbar_color = sharedPreferences.getInt(TOOLBAR_COLOR, ContextCompat.getColor(context, R.color.white));
        toolbar_textcolor = sharedPreferences.getInt(TOOLBAR_TEXT_COLOR, getDarkTextColor());
        cord_color = sharedPreferences.getInt(CORD_COLOR, ContextCompat.getColor(context, R.color.white));
        cord_textcolor = sharedPreferences.getInt(CORD_TEXT_COLOR, getDarkTextColor());
        for (int i = 1; i < colors.length; i++) {
            colors[i] = sharedPreferences.getInt(COLOR + i, getDefaultColors(context, i));
        }
        for (int i = 1; i < textColors.length; i++) {
            textColors[i] = sharedPreferences.getInt(TEXT_COLOR + i, getDefaultTextColors(i));
        }
        defaultColorIndex = sharedPreferences.getInt(DEFAULT_COLOR_INDEX, 0);
        toolbarIconsTranslucent = sharedPreferences.getBoolean(TOOLBAR_ICONS_TRANSLUCENT, false);
    }

    private void readColor(Context context, int colorIndex) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("todolist", Context.MODE_PRIVATE);

        cord_color = sharedPreferences.getInt(CORD_COLOR, ContextCompat.getColor(context, R.color.white));

        colors[colorIndex] = sharedPreferences.getInt(COLOR + colorIndex, getDefaultColors(context, colorIndex));
        textColors[colorIndex] = sharedPreferences.getInt(TEXT_COLOR + colorIndex, getDefaultTextColors(colorIndex));
    }

    private int getDefaultColors(Context context, int index) {
        switch (index) {
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

    private int getDefaultTextColors(int index) {
        int textColor;
        if (isColorLight(getEventColor(index))) {
            //light
            textColor = getDarkTextColor();
        } else {
            //dark
            textColor = getLightTextColor();
        }
        return textColor;
    }

    public int getLightTextColor() {
        return Color.argb(255, 255, 255, 255);
    }

    public int getDarkTextColor() {
        return Color.argb(138, 0, 0, 0);
    }

    public void setEventColor(int index, int color) {
        colors[index] = color;
    }

    public void setEventTextColor(int index, int textColor) {
        textColors[index] = textColor;
    }

    private int semiTransparentColor(int color, float transparency) {
        int red, green, blue;
        red = (int) (transparency * Color.red(color) + (1 - transparency) * Color.red(cord_color));
        green = (int) (transparency * Color.green(color) + (1 - transparency) * Color.green(cord_color));
        blue = (int) (transparency * Color.blue(color) + (1 - transparency) * Color.blue(cord_color));
        return Color.rgb(red, green, blue);
    }

    public int get(String key) {
        switch (key) {
            case FAB_COLOR:
                return fab_color;
            case FAB_TEXT_COLOR:
                return fab_textcolor;
            case TOOLBAR_COLOR:
                return toolbar_color;
            case TOOLBAR_TEXT_COLOR:
                return toolbar_textcolor;
            case CORD_COLOR:
                return cord_color;
            case CORD_TEXT_COLOR:
                return cord_textcolor;
        }
        return Color.rgb(255, 255, 255);
    }

    public void set(String key, int color) {
        switch (key) {
            case FAB_COLOR:
                fab_color = color;
                break;
            case FAB_TEXT_COLOR:
                fab_textcolor = color;
                break;
            case TOOLBAR_COLOR:
                toolbar_color = color;
                break;
            case TOOLBAR_TEXT_COLOR:
                toolbar_textcolor = color;
                break;
            case CORD_COLOR:
                cord_color = color;
                break;
            case CORD_TEXT_COLOR:
                cord_textcolor = color;
                break;
        }
    }

    int getDefaultColorIndex() {
        return defaultColorIndex;
    }

    void setDefaultColorIndex(Context context, int defaultColorIndex) {
        this.defaultColorIndex = defaultColorIndex;
        context.getSharedPreferences("todolist", Context.MODE_PRIVATE)
                .edit().putInt(DEFAULT_COLOR_INDEX, defaultColorIndex).apply();
    }

    public boolean isToolbarIconsTranslucent() {
        return toolbarIconsTranslucent;
    }

    public void toggleToolbarIconsTranslucent() {
        toolbarIconsTranslucent = !toolbarIconsTranslucent;
    }

    public int getToolbarIconColor() {
        if (toolbarIconsTranslucent) {
            if (isColorLight(toolbar_color)) {
                //light
                return Color.argb(96, 0, 0, 0);
            } else {
                //dark
                return Color.argb(96, 255, 255, 255);
            }
        } else {
            return toolbar_textcolor;
        }
    }

    public int[] getSortedColors() {
        int[] sortedColors = new int[13];
        int[] colors = new int[13];
        int[] sortedColors_temp = new int[13];

        for (int i = 1; i < colors.length; i++) {
            colors[i] = i;
        }

        double shortestDistance = -1;

        for (int j = 1; j < colors.length; j++) {
            sortedColors_temp[1] = colors[j];

            double wholePathDistance = 0;

            ArrayList<Integer> unusedColors = new ArrayList<>();
            for (int i = 1; i < 13; i++) {
                unusedColors.add(i);
            }

            unusedColors.remove((Object) j);

            for (int i = 2; i < sortedColors_temp.length; i++) {
                int lastColor = getEventColor(sortedColors_temp[i - 1]);
                float[] lastColor_hsv = new float[3];
                Color.colorToHSV(lastColor, lastColor_hsv);

                int nextColor = -1;
                double nextColor_distance = 0;

                for (int k = 0; k < unusedColors.size(); k++) {
                    int color = getEventColor(unusedColors.get(k));
                    float[] color_hsv = new float[3];
                    Color.colorToHSV(color, color_hsv);

                    double temp = ((color_hsv[0] - lastColor_hsv[0]) * (color_hsv[0] - lastColor_hsv[0])
                            + (color_hsv[1] * 100 - lastColor_hsv[1] * 100) * (color_hsv[1] * 100 - lastColor_hsv[1] * 100)
                            + (color_hsv[2] * 100 - lastColor_hsv[2] * 100) * (color_hsv[2] * 100 - lastColor_hsv[2] * 100));

                    double distance = Math.sqrt(temp);

                    if (distance < nextColor_distance || nextColor == -1) {
                        nextColor = unusedColors.get(k);
                        nextColor_distance = distance;
                    }
                }
                sortedColors_temp[i] = nextColor;
                unusedColors.remove((Object) nextColor);
                wholePathDistance = wholePathDistance + nextColor_distance;
            }

            if (shortestDistance == -1 || wholePathDistance < shortestDistance) {
                shortestDistance = wholePathDistance;

                for (int i = 0; i < sortedColors_temp.length; i++) {
                    sortedColors[i] = sortedColors_temp[i];
                }
            }
        }
        return sortedColors;
    }

    public int[] getSortedColorsColorSelect() {
        int[] sortedColors = getSortedColors();
        int[] sortedColorsColorSelector = new int[13];
        sortedColorsColorSelector[1] = sortedColors[5];
        sortedColorsColorSelector[2] = sortedColors[3];
        sortedColorsColorSelector[3] = sortedColors[1];
        sortedColorsColorSelector[4] = sortedColors[6];
        sortedColorsColorSelector[5] = sortedColors[4];
        sortedColorsColorSelector[6] = sortedColors[2];
        sortedColorsColorSelector[7] = sortedColors[11];
        sortedColorsColorSelector[8] = sortedColors[9];
        sortedColorsColorSelector[9] = sortedColors[7];
        sortedColorsColorSelector[10] = sortedColors[12];
        sortedColorsColorSelector[11] = sortedColors[10];
        sortedColorsColorSelector[12] = sortedColors[8];
        return sortedColorsColorSelector;
    }

    private boolean isColorLight(int color) {
        int rgbSum = Color.red(color) + Color.green(color) + Color.blue(color);
        return rgbSum / 3 > 185;
    }
}
