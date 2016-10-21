package us.koller.todolist.Util;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;

import biz.kasual.materialnumberpicker.MaterialNumberPicker;
import us.koller.todolist.R;
import us.koller.todolist.Settings;
import us.koller.todolist.Todolist.Alarm;
import us.koller.todolist.Todolist.Event;
import us.koller.todolist.Util.Callbacks.AlarmInfoDialogOnPositiveCallback;

/**
 * Created by Lukas on 20.10.2016.
 */

public class DialogBuilder {

    public static int selectedColor = 0;
    public static String addEventHint = "hint";

    public static AlertDialog.Builder getAddEventDialog(final View layout, final ThemeHelper helper, int dialogTheme,
                                                        int dialogTextColor) {
        final TextInputEditText editText = (TextInputEditText) layout.findViewById(R.id.edit_text);
        final RadioButton color_rb = (RadioButton) layout.findViewById(R.id.radio_button_color);
        final HorizontalScrollView horizontalScrollView
                = (HorizontalScrollView) layout.findViewById(R.id.color_scroll_view);
        horizontalScrollView.setVisibility(View.GONE);

        final ImageButton[] buttons = getColorButtons(layout);
        final int[] sortedColors = helper.getSortedColors();
        for (int i = 1; i < buttons.length; i++) {
            buttons[i].getBackground().setColorFilter(helper.getEventColor(sortedColors[i]), PorterDuff.Mode.SRC_IN);
            buttons[i].getDrawable().setColorFilter(helper.getEventTextColor(sortedColors[i]), PorterDuff.Mode.SRC_IN);
        }

        if (helper.getDefaultColorIndex() != 0) {
            int index = 0;
            int defaultColor = helper.getDefaultColorIndex();
            for (int i = 1; i < sortedColors.length; i++) {
                if (defaultColor == sortedColors[i]) {
                    index = i;
                    break;
                }
            }
            buttons[index].setSelected(true);
            selectedColor = index;
        } else {
            selectedColor = 0;
        }
        Button.OnClickListener onClickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = getColorIndexByButtonId(v.getId());
                if (selectedColor != 0 || index == selectedColor) {
                    buttons[selectedColor].setSelected(false);
                }
                if (index != selectedColor) {
                    v.setSelected(true);
                    selectedColor = index;
                } else {
                    selectedColor = 0;
                }
            }
        };

        Button.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int index = getColorIndexByButtonId(v.getId());
                int colorIndex = sortedColors[index];
                int default_color = helper.getDefaultColorIndex();
                if (default_color != colorIndex) {
                    v.setSelected(true);
                    if (selectedColor != 0 && selectedColor != index) {
                        buttons[selectedColor].setSelected(false);
                    }
                    selectedColor = index;
                    helper.setDefaultColorIndex(layout.getContext(), colorIndex);
                    Toast.makeText(layout.getContext(), "Default Color set", Toast.LENGTH_SHORT).show();
                } else {
                    buttons[index].setImageResource(android.R.color.transparent);
                    selectedColor = 0;
                    helper.setDefaultColorIndex(layout.getContext(), 0);
                    Toast.makeText(layout.getContext(), "Default Color removed", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        };

        for (int i = 1; i < buttons.length; i++) {
            buttons[i].setOnClickListener(onClickListener);
            buttons[i].setOnLongClickListener(onLongClickListener);
        }
        editText.setText("");
        editText.setTextColor(dialogTextColor);
        if (helper.lightCoordColor()) {
            editText.setHintTextColor(ContextCompat.getColor(layout.getContext(), R.color.light_grey));
        } else {
            editText.setHintTextColor(ContextCompat.getColor(layout.getContext(), R.color.grey700));
        }

        color_rb.setTextColor(dialogTextColor);
        color_rb.setChecked(false);
        color_rb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                horizontalScrollView.setVisibility(View.VISIBLE);
            }
        });

        addEventHint = getAddEventHint(layout.getContext());
        editText.setHint(addEventHint);

        return new AlertDialog.Builder(layout.getContext(), dialogTheme)
                .setTitle(layout.getContext().getString(R.string.add_event))
                .setView(layout)
                .setNegativeButton(layout.getContext().getString(R.string.cancel), null);
    }

    private static String getAddEventHint(Context context) {
        Random rand = new Random();
        String string;
        switch (rand.nextInt(3)) {
            case 0:
                string = context.getString(R.string.do_homework);
                break;
            case 1:
                string = context.getString(R.string.clean_kitchen);
                break;
            default:
                string = context.getString(R.string.do_laundry);
                break;
        }
        return string;
    }

    public static AlertDialog.Builder getColorEventDialog(View layout, int dialogTheme, ThemeHelper helper) {
        return new AlertDialog.Builder(layout.getContext(), dialogTheme)
                .setView(inflateColorSelector(layout, helper))
                .setTitle(layout.getContext().getString(R.string.choose_a_color))
                .setCancelable(true)
                .setNegativeButton(layout.getContext().getString(R.string.cancel), null);
    }

    public static AlertDialog.Builder getEditEventDialog(final View dialogView, int dialogTheme, int dialogTextColor,
                                                         final Event e) {
        final EditText editText = (EditText) dialogView.findViewById(R.id.edit_text);
        editText.setTextColor(dialogTextColor);
        editText.setText(e.getWhatToDo());
        editText.setSelection(e.getWhatToDo().length());

        return new AlertDialog.Builder(dialogView.getContext(), dialogTheme)
                .setTitle(dialogView.getContext().getString(R.string.edit_event))
                .setView(dialogView)
                .setNegativeButton(dialogView.getContext().getString(R.string.cancel), null);
                /*.setPositiveButton(dialogView.getContext().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editEventCallback.onPositiveButton(editText.getText().toString());
                    }
                });*/
    }

    public static AlertDialog.Builder getAlarmInfoDialog(final View layout, final ThemeHelper helper, int dialogTheme,
                                                         final int dialogTextColor, final Event e, final AlarmInfoDialogOnPositiveCallback callback) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis((long) e.getAlarm().get(Alarm.TIME));

        boolean timeFormat = android.text.format.DateFormat.is24HourFormat(layout.getContext());

        int Hour = calendar.get(Calendar.HOUR_OF_DAY);

        String am_pm = "";

        if (!timeFormat) {
            am_pm = " am";
        }

        if (Hour > 12 && !timeFormat) {
            Hour = Hour - 12;
            am_pm = " pm";
        }

        int Minutes = calendar.get(Calendar.MINUTE);
        Calendar currentTime = Calendar.getInstance(TimeZone.getDefault());
        String s;
        String month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, layout.getContext().getResources().getConfiguration().locale);
        if (calendar.get(Calendar.YEAR) == currentTime.get(Calendar.YEAR) && calendar.get(Calendar.MONTH) == currentTime.get(Calendar.MONTH)) {
            if (calendar.get(Calendar.DATE) == currentTime.get(Calendar.DATE)) {
                s = layout.getContext().getString(R.string.today);
            } else if (currentTime.getTimeInMillis() + 24 * 60 * 60 * 1000 > calendar.getTimeInMillis()) {
                s = layout.getContext().getString(R.string.tomorrow);
            } else {
                s = String.valueOf(calendar.get(Calendar.DATE)) + ". " + month + " " + calendar.get(Calendar.YEAR);
            }
        } else {
            s = String.valueOf(calendar.get(Calendar.DATE)) + ". " + month + " " + calendar.get(Calendar.YEAR);
        }

        String content = "<b>" + s + " "
                + layout.getContext().getString(R.string.at)
                + " " + Hour + ":"
                + String.format(layout.getContext().getResources().getConfiguration().locale, "%02d", Minutes) + am_pm + "</b>";

        final TextView alarmInfoText1 = (TextView) layout.findViewById(R.id.alarmInfoText1);
        alarmInfoText1.setText(Html.fromHtml(layout.getContext().getString(R.string.alarm_scheduled_for)));
        alarmInfoText1.setTextColor(dialogTextColor);

        final TextView alarmInfoText2 = (TextView) layout.findViewById(R.id.alarmInfoText2);
        alarmInfoText2.setText(Html.fromHtml(content));
        alarmInfoText2.setTextColor(dialogTextColor);

        final AppCompatCheckBox checkbox = (AppCompatCheckBox) layout.findViewById(R.id.checkbox);
        checkbox.setSupportButtonTintList(getColorStateListForCheckbox(layout.getContext(), helper));
        checkbox.setTextColor(dialogTextColor);
        checkbox.setChecked((boolean) e.getAlarm().get(Alarm.REPEATING));

        final ScrollView certain_days = (ScrollView) layout.findViewById(R.id.certain_days);
        if ((int) e.getAlarm().get(Alarm.REPEAT_MODE) != 3) {
            certain_days.setVisibility(View.GONE);
        }

        final LinearLayout numberPickers = (LinearLayout) layout.findViewById(R.id.numberPickers);
        if ((int) e.getAlarm().get(Alarm.REPEAT_MODE) != 4) {
            numberPickers.setVisibility(View.GONE);
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                (int) DPCalc.dpIntoPx(layout.getContext().getResources(), 100));

        final MaterialNumberPicker numberPicker1 = new MaterialNumberPicker.Builder(layout.getContext())
                .minValue(1)
                .maxValue(25)
                .defaultValue(1)
                .backgroundColor(Color.TRANSPARENT)
                .separatorColor(Color.TRANSPARENT)
                .textColor(dialogTextColor)
                .textSize(16)
                .enableFocusability(false)
                .wrapSelectorWheel(false)
                .build();
        numberPicker1.setLayoutParams(params);

        final MaterialNumberPicker numberPicker2 = new MaterialNumberPicker.Builder(layout.getContext())
                .minValue(1)
                .maxValue(6)
                .defaultValue(1)
                .backgroundColor(Color.TRANSPARENT)
                .separatorColor(Color.TRANSPARENT)
                .textColor(dialogTextColor)
                .textSize(16)
                .enableFocusability(false)
                .wrapSelectorWheel(true)
                .formatter(new NumberPicker.Formatter() {
                    @Override
                    public String format(int i) {
                        switch (i) {
                            case 1:
                                return layout.getContext().getString(R.string.hours);
                            case 2:
                                return layout.getContext().getString(R.string.days);
                            case 3:
                                return layout.getContext().getString(R.string.weeks);
                            //needed for wheelWrap
                            case 4:
                                return layout.getContext().getString(R.string.hours);
                            case 5:
                                return layout.getContext().getString(R.string.days);
                            case 6:
                                return layout.getContext().getString(R.string.weeks);
                        }
                        return "";
                    }
                })
                .build();
        numberPicker2.setLayoutParams(params);

        numberPickers.addView(numberPicker1);
        numberPickers.addView(numberPicker2);

        if ((int) e.getAlarm().get(Alarm.REPEAT_MODE) == 4) {
            numberPicker1.setValue((int) e.getAlarm().get(Alarm.NUMBER_PICKER_1_VALUE));
            numberPicker2.setValue((int) e.getAlarm().get(Alarm.NUMBER_PICKER_2_VALUE));
        }

        final String[] state = {layout.getContext().getString(R.string.daily),
                layout.getContext().getString(R.string.weekly),
                layout.getContext().getString(R.string.monthly),
                layout.getContext().getString(R.string.certain_days),
                layout.getContext().getString(R.string.custom)};

        final AppCompatSpinner spinner = (AppCompatSpinner) layout.findViewById(R.id.spinner);
        spinner.setEnabled((boolean) e.getAlarm().get(Alarm.REPEATING));

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(layout.getContext(), android.R.layout.simple_spinner_item, state);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSupportBackgroundTintList(getColorStateListForSpinner(layout.getContext(), helper));
        final AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                boolean showCustomIntervall = false;
                boolean showCertainDays = false;
                if (checkbox.isChecked()) {
                    showCertainDays = i == 3;
                    showCustomIntervall = i == 4;
                }
                hideOrShowView(certain_days, showCertainDays);
                hideOrShowView(numberPickers, showCustomIntervall);
                colorSpinnerTextView(((TextView) spinner.getSelectedView()), checkbox.isChecked(), dialogTextColor, layout.getContext());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        };
        spinner.setOnItemSelectedListener(onItemSelectedListener);
        spinner.setSelection((int) e.getAlarm().get(Alarm.REPEAT_MODE));

        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                onItemSelectedListener.onItemSelected(null, null, spinner.getSelectedItemPosition(), 0);
                spinner.setEnabled(b);
                colorSpinnerTextView(((TextView) spinner.getSelectedView()), b, dialogTextColor, layout.getContext());
            }
        });

        final Button[] buttons = getWeekButtons(layout);
        for (int i = 0; i < buttons.length; i++) {
            int color = helper.get(ThemeHelper.FAB_COLOR);
            int textcolor = helper.get(ThemeHelper.FAB_TEXT_COLOR);
            if (color == ContextCompat.getColor(layout.getContext(), R.color.white)) {
                color = ContextCompat.getColor(layout.getContext(), R.color.grey);
                textcolor = ContextCompat.getColor(layout.getContext(), R.color.white);
            }

            if (e.getAlarm().getCertainDay(i)) {
                buttons[i].setTextColor(textcolor);
                buttons[i].getBackground().setColorFilter(color, PorterDuff.Mode.SRC_IN);
            } else {
                buttons[i].setTextColor(ContextCompat.getColor(layout.getContext(), R.color.black));
                buttons[i].getBackground().setColorFilter(
                        ContextCompat.getColor(layout.getContext(), R.color.white), PorterDuff.Mode.SRC_IN);
            }
        }

        Button.OnClickListener onClickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView clickedButton = (TextView) v;
                boolean b;
                if (clickedButton.getCurrentTextColor() == helper.get(ThemeHelper.FAB_TEXT_COLOR)) {
                    clickedButton.setTextColor(ContextCompat.getColor(layout.getContext(), R.color.black));
                    clickedButton.getBackground().setColorFilter(
                            ContextCompat.getColor(layout.getContext(), R.color.white), PorterDuff.Mode.SRC_IN);
                    b = false;
                } else {
                    clickedButton.setTextColor(helper.get(ThemeHelper.FAB_COLOR));
                    clickedButton.getBackground().setColorFilter(helper.get(ThemeHelper.FAB_COLOR), PorterDuff.Mode.SRC_IN);
                    b = true;
                }
                e.getAlarm().setCertainDay(getWeekButtonsIndexById(v), b);
            }
        };

        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setOnClickListener(onClickListener);
        }

        return new AlertDialog.Builder(layout.getContext(), dialogTheme)
                .setTitle(layout.getContext().getString(R.string.alarm))
                .setView(layout)
                .setPositiveButton(layout.getContext().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (checkbox.isChecked()) {
                            int index = spinner.getSelectedItemPosition();
                            if (e.getAlarm() == null) {
                                return;
                            }
                            e.getAlarm().setRepeating(index);
                            if (index == 4) {
                                int multiplier = 0;
                                switch (numberPicker2.getValue()) {
                                    case 1:
                                        multiplier = 60 * 60 * 1000;
                                        e.getAlarm().set(Alarm.NUMBER_PICKER_2_VALUE, 1);
                                        break;
                                    case 2:
                                        multiplier = 24 * 60 * 60 * 1000;
                                        e.getAlarm().set(Alarm.NUMBER_PICKER_2_VALUE, 2);
                                        break;
                                    case 3:
                                        multiplier = 7 * 24 * 60 * 60 * 1000;
                                        e.getAlarm().set(Alarm.NUMBER_PICKER_2_VALUE, 3);
                                        break;
                                    //needed for wheelwrapping
                                    case 4:
                                        multiplier = 60 * 60 * 1000;
                                        e.getAlarm().set(Alarm.NUMBER_PICKER_2_VALUE, 1);
                                        break;
                                    case 5:
                                        multiplier = 24 * 60 * 60 * 1000;
                                        e.getAlarm().set(Alarm.NUMBER_PICKER_2_VALUE, 2);
                                        break;
                                    case 6:
                                        multiplier = 7 * 24 * 60 * 60 * 1000;
                                        e.getAlarm().set(Alarm.NUMBER_PICKER_2_VALUE, 3);
                                        break;
                                }
                                e.getAlarm().set(Alarm.CUSTOM_INTERVALL, (long) numberPicker1.getValue() * multiplier);
                                e.getAlarm().set(Alarm.NUMBER_PICKER_1_VALUE, numberPicker1.getValue());
                            }
                        } else {
                            e.getAlarm().unRepeat();
                        }
                        callback.onPositive();
                    }
                });
    }

    public static TimePickerDialog getTimePickerDialog(Context context, ThemeHelper helper,
                                                       TimePickerDialog.OnTimeSetListener onTimeSetListener) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.add(Calendar.MINUTE, 1);
        int theme;
        if (android.os.Build.VERSION.SDK_INT > 21) {
            if (helper.lightCoordColor()) {
                theme = android.R.style.Theme_DeviceDefault_Light_Dialog_Alert;
            } else {
                theme = android.R.style.Theme_DeviceDefault_Dialog_Alert;
            }
        } else {
            if (helper.lightCoordColor()) {
                theme = TimePickerDialog.THEME_DEVICE_DEFAULT_LIGHT;
            } else {
                theme = TimePickerDialog.THEME_DEVICE_DEFAULT_DARK;
            }
        }

        boolean timeFormat = android.text.format.DateFormat.is24HourFormat(context);

        return new TimePickerDialog(context, theme,
                onTimeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), timeFormat);
    }

    public static DatePickerDialog getDatePickerDialog(Context context, ThemeHelper helper,
                                                       DatePickerDialog.OnDateSetListener onDateSetListener){
        final Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int theme;
        if (android.os.Build.VERSION.SDK_INT > 21) {
            if (helper.lightCoordColor()) {
                theme = android.R.style.Theme_DeviceDefault_Light_Dialog_Alert;
            } else {
                theme = android.R.style.Theme_DeviceDefault_Dialog_Alert;
            }
        } else {
            if (helper.lightCoordColor()) {
                theme = TimePickerDialog.THEME_DEVICE_DEFAULT_LIGHT;
            } else {
                theme = TimePickerDialog.THEME_DEVICE_DEFAULT_DARK;
            }
        }

        return new DatePickerDialog(context, theme, onDateSetListener, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    public static AlertDialog.Builder getCategorySelectorDialog(View layout, ThemeHelper helper, int dialogTheme,
                                                                final Settings settings, boolean[] categoriesToDisable){
        final ImageButton[] buttons = getColorButtons(layout);
        int[] sortedColors = helper.getSortedColorsColorSelect();
        for (int i = 1; i < buttons.length; i++) {
            buttons[i].getBackground().setColorFilter(helper.getEventColor(sortedColors[i]), PorterDuff.Mode.SRC_IN);
            buttons[i].setImageDrawable(ContextCompat.getDrawable(layout.getContext(), R.drawable.ic_color_select).mutate());
            buttons[i].getDrawable().setColorFilter(helper.getEventTextColor(sortedColors[i]), PorterDuff.Mode.SRC_IN);
        }

        boolean[] selected_categories = (boolean[]) settings.get(Settings.SELECTED_CATEGORIES);

        for (int i = 1; i < selected_categories.length; i++) {
            int colorIndex = sortedColors[getColorIndexByButtonId(buttons[i].getId())];
            if (categoriesToDisable[colorIndex]) {
                buttons[i].setEnabled(false);
                buttons[i].getBackground().setAlpha(60);
                buttons[i].setSelected(false);
            } else if (selected_categories[colorIndex]) {
                buttons[i].setSelected(true);
            }
        }

        View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int colorIndex = getColorIndexByButtonId(v.getId());
                for (int i = 1; i < buttons.length; i++) {
                    if (i != colorIndex) {
                        buttons[i].setSelected(false);
                        settings.setCategory(i, false);
                    } else {
                        buttons[i].setSelected(true);
                        settings.setCategory(i, true);
                    }
                }
                return false;
            }
        };

        for (int i = 1; i < buttons.length; i++) {
            buttons[i].setOnLongClickListener(onLongClickListener);
        }

        return new AlertDialog.Builder(layout.getContext(), dialogTheme)
                .setView(layout)
                .setTitle(layout.getContext().getString(R.string.choose_a_category));
    }


    private static ColorStateList getColorStateListForSpinner(Context context, ThemeHelper helper) {
        int color = helper.get(ThemeHelper.FAB_COLOR);
        if (helper.get(ThemeHelper.FAB_COLOR) == ContextCompat.getColor(context, R.color.white)) {
            color = ContextCompat.getColor(context, R.color.grey);
        }

        int color_grey = ContextCompat.getColor(context, R.color.grey);
        final int[][] states_spinner = new int[3][];
        final int[] colors_spinner = new int[3];
        int k_spinner = 0;
        // Disabled state
        states_spinner[k_spinner] = new int[]{-android.R.attr.state_enabled};
        colors_spinner[k_spinner] = Color.argb(72, Color.red(color_grey), Color.green(color_grey), Color.blue(color_grey));
        k_spinner++;
        states_spinner[k_spinner] = new int[]{android.R.attr.state_checked};
        colors_spinner[k_spinner] = color;
        k_spinner++;
        // Default enabled state
        states_spinner[k_spinner] = new int[0];
        colors_spinner[k_spinner] = color;

        return new ColorStateList(states_spinner, colors_spinner);
    }

    private static ColorStateList getColorStateListForCheckbox(Context context, ThemeHelper helper) {
        int color = helper.get(ThemeHelper.FAB_COLOR);
        if (helper.get(ThemeHelper.FAB_COLOR) == ContextCompat.getColor(context, R.color.white)) {
            color = ContextCompat.getColor(context, R.color.grey);
        }

        int color_grey = ContextCompat.getColor(context, R.color.grey);
        final int[][] states = new int[3][];
        final int[] colors = new int[3];
        int k = 0;
        // Disabled state
        states[k] = new int[]{-android.R.attr.state_enabled};
        colors[k] = Color.argb(72, Color.red(color_grey), Color.green(color_grey), Color.blue(color_grey));
        k++;
        states[k] = new int[]{android.R.attr.state_checked};
        colors[k] = color;
        k++;
        // Default enabled state
        states[k] = new int[0];
        colors[k] = color_grey;
        return new ColorStateList(states, colors);
    }

    private static void hideOrShowView(View v, boolean show) {
        if (show) {
            v.setVisibility(View.VISIBLE);
        } else {
            v.setVisibility(View.GONE);
        }
    }

    private static Button[] getWeekButtons(View layout) {
        Button[] buttons = new Button[7];
        buttons[0] = (Button) layout.findViewById(R.id.monday_button);
        buttons[1] = (Button) layout.findViewById(R.id.tuesday_button);
        buttons[2] = (Button) layout.findViewById(R.id.wednesday_button);
        buttons[3] = (Button) layout.findViewById(R.id.thursday_button);
        buttons[4] = (Button) layout.findViewById(R.id.friday_button);
        buttons[5] = (Button) layout.findViewById(R.id.saturday_button);
        buttons[6] = (Button) layout.findViewById(R.id.sunday_button);
        return buttons;
    }

    private static int getWeekButtonsIndexById(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.monday_button:
                return 0;
            case R.id.tuesday_button:
                return 1;
            case R.id.wednesday_button:
                return 2;
            case R.id.thursday_button:
                return 3;
            case R.id.friday_button:
                return 4;
            case R.id.saturday_button:
                return 5;
            case R.id.sunday_button:
                return 6;
        }
        return 0;
    }

    private static void colorSpinnerTextView(TextView textView, boolean enabled, int dialogTextColor, Context context) {
        if (enabled) {
            textView.setTextColor(dialogTextColor);
        } else {
            textView.setTextColor(Color.argb(72,
                    Color.red(ContextCompat.getColor(context, R.color.grey)),
                    Color.green(ContextCompat.getColor(context, R.color.grey)),
                    Color.blue(ContextCompat.getColor(context, R.color.grey))));
        }
    }

    private static View inflateColorSelector(View layout, ThemeHelper helper) {
        final ImageButton[] buttons = getColorButtons(layout);

        int[] sortedColors = helper.getSortedColorsColorSelect();
        for (int i = 1; i < buttons.length; i++) {
            buttons[i].getBackground().setColorFilter(helper.getEventColor(sortedColors[i]), PorterDuff.Mode.SRC_IN);
        }

        return layout;
    }

    private static ImageButton[] getColorButtons(View layout) {
        final ImageButton[] buttons = new ImageButton[13];
        buttons[1] = (ImageButton) layout.findViewById(R.id.color1_button);
        buttons[2] = (ImageButton) layout.findViewById(R.id.color2_button);
        buttons[3] = (ImageButton) layout.findViewById(R.id.color3_button);
        buttons[4] = (ImageButton) layout.findViewById(R.id.color4_button);
        buttons[5] = (ImageButton) layout.findViewById(R.id.color5_button);
        buttons[6] = (ImageButton) layout.findViewById(R.id.color6_button);
        buttons[7] = (ImageButton) layout.findViewById(R.id.color7_button);
        buttons[8] = (ImageButton) layout.findViewById(R.id.color8_button);
        buttons[9] = (ImageButton) layout.findViewById(R.id.color9_button);
        buttons[10] = (ImageButton) layout.findViewById(R.id.color10_button);
        buttons[11] = (ImageButton) layout.findViewById(R.id.color11_button);
        buttons[12] = (ImageButton) layout.findViewById(R.id.color12_button);
        return buttons;
    }

    private static int getColorIndexByButtonId(int button_id) {
        switch (button_id) {
            case R.id.color1_button:
                return 1;
            case R.id.color2_button:
                return 2;
            case R.id.color3_button:
                return 3;
            case R.id.color4_button:
                return 4;
            case R.id.color5_button:
                return 5;
            case R.id.color6_button:
                return 6;
            case R.id.color7_button:
                return 7;
            case R.id.color8_button:
                return 8;
            case R.id.color9_button:
                return 9;
            case R.id.color10_button:
                return 10;
            case R.id.color11_button:
                return 11;
            default:
                return 12;
        }
    }
}
