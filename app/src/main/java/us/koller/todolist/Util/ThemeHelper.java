package us.koller.todolist.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;

import us.koller.todolist.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Lukas on 13.06.2016.
 */
public class ThemeHelper {
    private int fab_color;
    private int fab_textcolor;

    private int toolbar_color;
    private int toolbar_textcolor;

    private int cord_color;
    private int cord_textcolor;

    private int [] colors;
    private int [] textColors;

    private int defaultColorIndex;

    private long timeStamp = 0;

    private boolean toolbarIconsTranslucent = false;

    public ThemeHelper(Context context) {
        colors = new int [13];
        textColors = new int [13];

        readData(context);
    }

    public ThemeHelper(JSONObject json, Context context) throws JSONException {
        fab_color = json.getInt("fab_color");
        fab_textcolor = json.getInt("fab_textcolor");
        toolbar_color = json.getInt("toolbar_color");
        toolbar_textcolor = json.getInt("toolbar_textcolor");
        cord_color = json.getInt("cord_color");
        cord_textcolor = json.getInt("cord_textcolor");

        colors = new int [13];
        textColors = new int [13];

        for (int i = 1; i < colors.length; i++){
            colors[i] = json.getInt("color" +i);
        }

        for (int i = 1; i < textColors.length; i++){
            textColors[i] = json.getInt("textcolor" +i);
        }
    }

    public ThemeHelper(Context context, int colorIndex){
        colors = new int [13];
        textColors = new int [13];

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

    public boolean lightCordColor() {
        return isColorLight(cord_color);
    }

    public void restoreDefaultTheme(Context context, String theme) {
        if (theme.equals("light")) {
            toolbar_color = ContextCompat.getColor(context, R.color.white);
            toolbar_textcolor = getDarkTextColor();
        } else if (theme.equals("dark")) {
            toolbar_color = ContextCompat.getColor(context, R.color.dark_background);
            toolbar_textcolor = getLightTextColor();
        } else {
            toolbar_color = ContextCompat.getColor(context, R.color.black);
            toolbar_textcolor = getLightTextColor();
        }

        cord_color = toolbar_color;
        cord_textcolor = toolbar_textcolor;

        fab_color = ContextCompat.getColor(context, R.color.button_color);
        fab_textcolor = getLightTextColor();

        for (int i = 1; i < colors.length; i++){
            colors[i] = getDefaultColors(context, i);
        }

        for (int i = 0; i < textColors.length; i++){
            textColors[i] = getDefaultTextColors(i);

        }
    }

    public void saveData(Context context) {
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

        for (int i = 1; i < colors.length; i++){
            editor.putInt("color" +i, colors[i]);
        }

        for (int i = 1; i < textColors.length; i++){
            editor.putInt("textcolor" +i, textColors[i]);
        }

        editor.putInt("defaultColorIndex", defaultColorIndex);
        editor.putBoolean("toolbarIconsTranslucent", toolbarIconsTranslucent);
        editor.apply();
    }

    private void readData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("todolist", Context.MODE_PRIVATE);
        timeStamp = sharedPreferences.getLong("colortimeStamp", System.currentTimeMillis());

        fab_color = sharedPreferences.getInt("fab_color", ContextCompat.getColor(context, R.color.button_color));
        fab_textcolor = sharedPreferences.getInt("fab_textcolor", getLightTextColor());
        toolbar_color = sharedPreferences.getInt("toolbar_color", ContextCompat.getColor(context, R.color.white));
        toolbar_textcolor = sharedPreferences.getInt("toolbar_textcolor", getDarkTextColor());
        cord_color = sharedPreferences.getInt("cord_color", ContextCompat.getColor(context, R.color.white));
        cord_textcolor = sharedPreferences.getInt("cord_textcolor", getDarkTextColor());

        for (int i = 1; i < colors.length; i++){
            colors[i] = sharedPreferences.getInt("color" +i, getDefaultColors(context, i));
        }

        for (int i = 1; i < textColors.length; i++){
            textColors[i] = sharedPreferences.getInt("textcolor" +i, getDefaultTextColors(i));
        }

        defaultColorIndex = sharedPreferences.getInt("defaultColorIndex", 0);
        toolbarIconsTranslucent = sharedPreferences.getBoolean("toolbarIconsTranslucent", false);
    }

    private void readColor(Context context, int colorIndex){
        SharedPreferences sharedPreferences = context.getSharedPreferences("todolist", Context.MODE_PRIVATE);

        cord_color = sharedPreferences.getInt("cord_color", ContextCompat.getColor(context, R.color.white));

        colors[colorIndex] = sharedPreferences.getInt("color" +colorIndex, getDefaultColors(context, colorIndex));
        textColors[colorIndex] = sharedPreferences.getInt("textcolor" +colorIndex, getDefaultTextColors(colorIndex));
    }

    private int getDefaultColors(Context context, int index){
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

    private int getDefaultTextColors(int index){
        int textColor;
        if(isColorLight(getEventColor(index))){
            //light
            textColor = getDarkTextColor();
        } else {
            //dark
            textColor = getLightTextColor();
        }
        return textColor;
    }

    public int getLightTextColor(){
        return Color.argb(222, 255, 255, 255);
    }

    public int getDarkTextColor(){
        return Color.argb(138, 0, 0, 0);
    }

    public void setEventColor(int index, int color){
        colors[index] = color;
    }

    public void setEventTextColor(int index, int textColor){
        textColors[index] = textColor;
    }

    public void setColors(int[] newColors) {
        for (int i = 1; i < colors.length; i++){
            colors[i] = newColors[i];
        }
    }

    public void setTextColors(int[] newTextColors) {
        for (int i = 1; i < textColors.length; i++){
            textColors[i] = newTextColors[i];
        }
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
            case "fab_color":
                return fab_color;
            case "fab_textcolor":
                return fab_textcolor;
            case "toolbar_color":
                return toolbar_color;
            case "toolbar_textcolor":
                return toolbar_textcolor;
            case "cord_color":
                return cord_color;
            case "cord_textcolor":
                return cord_textcolor;
        }
        return Color.rgb(255, 255, 255);
    }

    public void set(String key, int color) {
        switch (key) {
            case "fab_color":
                fab_color = color;
                break;
            case "fab_textcolor":
                fab_textcolor = color;
                break;
            case "toolbar_color":
                toolbar_color = color;
                break;
            case "toolbar_textcolor":
                toolbar_textcolor = color;
                break;
            case "cord_color":
                cord_color = color;
                break;
            case "cord_textcolor":
                cord_textcolor = color;
                break;
        }
    }

    public int getDefaultColorIndex() {
        return defaultColorIndex;
    }

    public void setDefaultColorIndex(Context context, int defaultColorIndex) {
        this.defaultColorIndex = defaultColorIndex;
        context.getSharedPreferences("todolist", Context.MODE_PRIVATE)
                .edit().putInt("defaultColorIndex", defaultColorIndex).apply();
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
                return Color.argb(138, 255, 255, 255);
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

    public boolean isColorLight(int color){
        int rgbSum = Color.red(color) + Color.green(color) + Color.blue(color);
        return rgbSum/3 > 185;
    }
}
