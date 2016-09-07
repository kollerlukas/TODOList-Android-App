package com.koller.lukas.todolist;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by Lukas on 13.06.2016.
 */
public class ThemeActivity extends AppCompatActivity {

    private ThemeHelper helper;
    private int[] colors;
    private int[] textcolors;
    public int fab_color;
    public int fab_textcolor;

    public int toolbar_color;
    public int toolbar_textcolor;

    public int cord_color;

    private Toolbar toolbar;
    private FloatingActionButton fab;
    private CoordinatorLayout cord;
    private View drawerIcon;
    private MenuItem infoIcon;
    private Drawable overflowIcon;

    private RecyclerView mRecyclerView;
    private Theme_RVAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    private AlertDialog dialog;
    private AlertDialog themePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        helper = new ThemeHelper(this);

        if (!getResources().getBoolean(R.bool.tablet)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        setContentView(R.layout.activity_theme);

        toolbar = (Toolbar) findViewById(R.id.toolbar_theme);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        fab = (FloatingActionButton) findViewById(R.id.fab_theme);
        cord = (CoordinatorLayout) findViewById(R.id.coordinatorLayout_theme);

        setupRecyclerView();
        setupTheme();

        //showInfoDialog();
    }

    public ThemeActivity() {
    }

    public void setupRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_theme);
        mRecyclerView.setHasFixedSize(true);
        addOnItemTouchListenerToRecyclerView();

        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        int[] colors = new int[12];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = i + 1;
        }
        mAdapter = new Theme_RVAdapter(colors, this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                CheckToolbarElevation();
            }
        });
    }

    public void addOnItemTouchListenerToRecyclerView() {
        OnItemClickHelper.addTo(mRecyclerView).setOnItemClickListener(new OnItemClickInterface() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, final int position, final RecyclerView.ViewHolder holder) {
                ColorPickerDialogCallback colorPickerDialogCallback = new ColorPickerDialogCallback() {
                    @Override
                    public void colorPicked(int red, int green, int blue, int textColor) {
                        colors[position + 1] = Color.rgb(red, green, blue);
                        textcolors[position + 1] = Color.rgb(textColor, textColor, textColor);
                        ((Theme_RVAdapter.EventViewHolder) holder).colorCard(colors[position + 1], textcolors[position + 1]);
                    }
                };
                showColorPickerDialog(colorPickerDialogCallback, true, colors[position + 1], textcolors[position + 1]);
            }
        });
    }

    public void setupTheme() {
        colors = new int[13];
        for (int i = 1; i < colors.length; i++) {
            colors[i] = helper.getEventColor(i);
        }
        textcolors = new int[13];
        for (int i = 1; i < textcolors.length; i++) {
            textcolors[i] = helper.getEventTextColor(i);
        }
        fab_color = helper.fab_color;
        fab_textcolor = helper.fab_textcolor;
        toolbar_color = helper.toolbar_color;
        toolbar_textcolor = helper.toolbar_textcolor;
        cord_color = helper.cord_color;
        toolbar.setBackgroundColor(toolbar_color);
        toolbar.setTitleTextColor(toolbar_textcolor);
        ChangeColorOfToolbarDrawerIcon(toolbar_textcolor);
        colorInfoIcon(toolbar_textcolor);
        setOverflowButtonColor(toolbar_textcolor);
        fab.setBackgroundTintList(ColorStateList.valueOf(fab_color));
        fab.getDrawable().setTint(fab_textcolor);
        cord.setBackgroundColor(cord_color);
        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            mAdapter.itemChanged(i);
        }
    }

    public void FabClicked(final View v) {
        ColorPickerDialogCallback colorPickerDialogCallback = new ColorPickerDialogCallback() {
            @Override
            public void colorPicked(int red, int green, int blue, int textColor) {
                fab.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(red, green, blue)));
                fab.getDrawable().setTint(Color.rgb(textColor, textColor, textColor));
                fab_color = Color.rgb(red, green, blue);
                fab_textcolor = Color.rgb(textColor, textColor, textColor);
            }
        };
        showColorPickerDialog(colorPickerDialogCallback, true, fab_color, fab_textcolor);
    }

    public void ToolbarClicked(View v) {
        ColorPickerDialogCallback colorPickerDialogCallback = new ColorPickerDialogCallback() {
            @Override
            public void colorPicked(int red, int green, int blue, int textColor) {
                cord.setBackgroundColor(Color.rgb(red, green, blue));
                cord_color = Color.rgb(red, green, blue);

                toolbar.setBackgroundColor(Color.rgb(red, green, blue));
                toolbar.setTitleTextColor(Color.rgb(textColor, textColor, textColor));
                ChangeColorOfToolbarDrawerIcon(Color.rgb(textColor, textColor, textColor));
                colorInfoIcon(Color.rgb(textColor, textColor, textColor));
                setOverflowButtonColor(Color.rgb(textColor, textColor, textColor));
                toolbar_color = Color.rgb(red, green, blue);
                toolbar_textcolor = Color.rgb(textColor, textColor, textColor);
            }
        };
        showColorPickerDialog(colorPickerDialogCallback, true, toolbar_color, toolbar_textcolor);
    }

    public void ChangeColorOfToolbarDrawerIcon(int color) {
        if (drawerIcon != null) {
            ((ImageView) drawerIcon).setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        } else {
            for (int i = 0; i < toolbar.getChildCount(); i++) {
                if (toolbar.getChildAt(i) instanceof ImageView) {
                    drawerIcon = toolbar.getChildAt(i);
                    ((ImageView) drawerIcon).setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                }
            }
        }
    }

    public void colorInfoIcon(int color) {
        if (infoIcon != null) {
            infoIcon.getIcon().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
            this.invalidateOptionsMenu();
        }
    }

    public void setOverflowButtonColor(int color) {
        if (overflowIcon == null) {
            overflowIcon = toolbar.getOverflowIcon();
            if (overflowIcon != null) {
                overflowIcon.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
            }
        } else {
            overflowIcon.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }
    }

    public void CoordinatorLayoutClicked(View v) {
        ToolbarClicked(v);
        /*ColorPickerDialogCallback colorPickerDialogCallback = new ColorPickerDialogCallback() {
            @Override
            public void colorPicked(int red, int green, int blue, int textColor) {
                cord.setBackgroundColor(Color.rgb(red, green, blue));
                cord_color = Color.rgb(red, green, blue);
            }
        };
        showColorPickerDialog(colorPickerDialogCallback, false, cord_color, 0);*/
    }

    public void showColorPickerDialog(final ColorPickerDialogCallback colorPickerDialogCallback, boolean eventColor, int oldColor, int oldTextColor) {
        LayoutInflater layoutInflater = this.getLayoutInflater();
        View layout = layoutInflater.inflate(R.layout.theme_picker, null);
        final String format = "%1$03d";

        final CardView colorCard = (CardView) layout.findViewById(R.id.colorCard);
        final TextView colorTextView = (TextView) layout.findViewById(R.id.colorTextView);
        final EditText hexEditText = (EditText) layout.findViewById(R.id.edit_text);
        hexEditText.setTextColor(ContextCompat.getColor(this, R.color.light_text_color));
        CharSequence hexColor_argb = Integer.toHexString(oldColor);
        String hexColor = "#" + hexColor_argb.charAt(2) + hexColor_argb.charAt(3) + hexColor_argb.charAt(4) + hexColor_argb.charAt(5) + hexColor_argb.charAt(6) + hexColor_argb.charAt(7);
        hexEditText.setText(hexColor);
        SeekBar[] colorSeekbars = new SeekBar[3];

        final SeekBar seekBarRed = (SeekBar) layout.findViewById(R.id.seekbar_red);
        final TextView red_text = (TextView) layout.findViewById(R.id.red_text);
        seekBarRed.setProgress(Color.red(oldColor));
        red_text.setText(String.format(format, Color.red(oldColor)));
        colorSeekbars[0] = seekBarRed;

        final SeekBar seekBarGreen = (SeekBar) layout.findViewById(R.id.seekbar_green);
        final TextView green_text = (TextView) layout.findViewById(R.id.green_text);
        seekBarGreen.setProgress(Color.green(oldColor));
        green_text.setText(String.format(format, Color.green(oldColor)));
        colorSeekbars[1] = seekBarGreen;

        final SeekBar seekBarBlue = (SeekBar) layout.findViewById(R.id.seekbar_blue);
        final TextView blue_text = (TextView) layout.findViewById(R.id.blue_text);
        seekBarBlue.setProgress(Color.blue(oldColor));
        blue_text.setText(String.format(format, Color.blue(oldColor)));
        colorSeekbars[2] = seekBarBlue;

        final SeekBar seekBarGrey = (SeekBar) layout.findViewById(R.id.seekbar_grey);
        final TextView grey_text = (TextView) layout.findViewById(R.id.grey_text);
        seekBarGrey.setProgress(Color.red(oldTextColor));
        grey_text.setText(String.format(format, Color.red(oldTextColor)));

        colorCard.setCardBackgroundColor(Color.rgb(seekBarRed.getProgress(), seekBarGreen.getProgress(), seekBarBlue.getProgress()));
        colorTextView.setTextColor(Color.rgb(seekBarGrey.getProgress(), seekBarGrey.getProgress(), seekBarGrey.getProgress()));

        if (!eventColor) {
            colorTextView.setVisibility(View.GONE);
            seekBarGrey.setVisibility(View.GONE);
        }

        for (int i = 0; i < colorSeekbars.length; i++) {
            colorSeekbars[i].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    colorCard.setCardBackgroundColor(Color.rgb(seekBarRed.getProgress(), seekBarGreen.getProgress(), seekBarBlue.getProgress()));
                    red_text.setText(String.format(format, seekBarRed.getProgress()));
                    green_text.setText(String.format(format, seekBarGreen.getProgress()));
                    blue_text.setText(String.format(format, seekBarBlue.getProgress()));
                    CharSequence hexColor_argb = Integer.toHexString(Color.rgb(seekBarRed.getProgress(), seekBarGreen.getProgress(), seekBarBlue.getProgress()));
                    String hexColor = "#" + hexColor_argb.charAt(2) + hexColor_argb.charAt(3) + hexColor_argb.charAt(4) + hexColor_argb.charAt(5) + hexColor_argb.charAt(6) + hexColor_argb.charAt(7);
                    hexEditText.setText(hexColor);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }


        seekBarGrey.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                colorTextView.setTextColor(Color.rgb(seekBarGrey.getProgress(), seekBarGrey.getProgress(), seekBarGrey.getProgress()));
                grey_text.setText(String.format(format, seekBarGrey.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        hexEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 7 && s.charAt(0) == '#') {
                    int color = Color.parseColor(String.valueOf(s));
                    int red = Color.red(color);
                    int green = Color.green(color);
                    int blue = Color.blue(color);
                    colorCard.setCardBackgroundColor(Color.rgb(red, green, blue));
                    seekBarRed.setProgress(red);
                    red_text.setText(String.format(format, red));
                    seekBarGreen.setProgress(green);
                    green_text.setText(String.format(format, green));
                    seekBarBlue.setProgress(blue);
                    blue_text.setText(String.format(format, blue));
                }
            }
        });


        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme_light_theme);
        builder.setView(layout)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        colorPickerDialogCallback.colorPicked(seekBarRed.getProgress(), seekBarGreen.getProgress(), seekBarBlue.getProgress(), seekBarGrey.getProgress());
                    }
                });
        themePickerDialog = builder.create();
        themePickerDialog.show();
        changeDialogButtonColor(themePickerDialog);
    }

    public AlertDialog changeDialogButtonColor(AlertDialog dialog) {
        Button positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        if (positive != null) {
            positive.setTextColor(fab_color);
        }
        Button neutral = dialog.getButton(DialogInterface.BUTTON_NEUTRAL);
        if (neutral != null) {
            neutral.setTextColor(fab_color);
        }
        Button negative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        if (negative != null) {
            negative.setTextColor(fab_color);
        }
        return dialog;
    }

    public void saveColorData() {
        helper.fab_color = fab_color;
        helper.fab_textcolor = fab_textcolor;
        helper.toolbar_color = toolbar_color;
        helper.toolbar_textcolor = toolbar_textcolor;
        helper.cord_color = cord_color;
        helper.color1 = colors[1];
        helper.color2 = colors[2];
        helper.color3 = colors[3];
        helper.color4 = colors[4];
        helper.color5 = colors[5];
        helper.color6 = colors[6];
        helper.color7 = colors[7];
        helper.color8 = colors[8];
        helper.color9 = colors[9];
        helper.color10 = colors[10];
        helper.color11 = colors[11];
        helper.color12 = colors[12];
        helper.textcolor1 = textcolors[1];
        helper.textcolor2 = textcolors[2];
        helper.textcolor3 = textcolors[3];
        helper.textcolor4 = textcolors[4];
        helper.textcolor5 = textcolors[5];
        helper.textcolor6 = textcolors[6];
        helper.textcolor7 = textcolors[7];
        helper.textcolor8 = textcolors[8];
        helper.textcolor9 = textcolors[9];
        helper.textcolor10 = textcolors[10];
        helper.textcolor11 = textcolors[11];
        helper.textcolor12 = textcolors[12];
        helper.saveData();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        saveColorData();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        this.finish();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(themePickerDialog != null){
            themePickerDialog.dismiss();
        }
        saveColorData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_theme, menu);
        for (int i = 0; i < toolbar.getMenu().size(); i++) {
            if (toolbar.getMenu().getItem(i).getItemId() == R.id.info_theme) {
                infoIcon = toolbar.getMenu().getItem(i);
                infoIcon.getIcon().setColorFilter(toolbar_textcolor, PorterDuff.Mode.SRC_ATOP);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.info_theme:
                //infoIcon = item.getIcon();
                showInfoDialog();
                return true;
            case R.id.restore_default_colors:
                LayoutInflater layoutInflater = this.getLayoutInflater();
                View layout = layoutInflater.inflate(R.layout.theme_restore, null);
                CardView light = (CardView) layout.findViewById(R.id.light_cardview);
                CardView dark = (CardView) layout.findViewById(R.id.dark_cardview);
                CardView black = (CardView) layout.findViewById(R.id.black_cardview);

                View.OnClickListener onClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        restoreTheme(v);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                };
                light.setOnClickListener(onClickListener);
                dark.setOnClickListener(onClickListener);
                black.setOnClickListener(onClickListener);

                dialog = new AlertDialog.Builder(this)
                        .setView(layout)
                        .setCancelable(true)
                        .create();
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimations;
                dialog.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void restoreTheme(View v) {
        String theme = "dark";
        switch (v.getId()) {
            case R.id.light_cardview:
                theme = "light";
                break;
            case R.id.dark_cardview:
                theme = "dark";
                break;
            case R.id.black_cardview:
                theme = "black";
                break;
        }
        helper.restoreDefaultTheme(theme);
        helper.saveData();
        setupTheme();
    }

    public void showInfoDialog() {
        AlertDialog infoDialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme_light)
                .setTitle(getString(R.string.themeActivityInfoDialogTitle))
                .setMessage(getString(R.string.themeActivityInfoDialogContent))
                .setPositiveButton(getString(R.string.ok), null);
        infoDialog = builder.create();
        infoDialog.show();
        changeDialogButtonColor(infoDialog);
    }

    public void CheckToolbarElevation() {
        int position = mLinearLayoutManager.findFirstCompletelyVisibleItemPosition();
        if (position == 0) {
            deelevateToolbar();
        } else {
            elevateToolbar();
        }
    }

    public void elevateToolbar() {
        toolbar.setElevation(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics()));
    }

    public void deelevateToolbar() {
        toolbar.setElevation(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics()));
    }

    public int getEventTextColor(int index) {
        return textcolors[index];
    }

    public int getEventColor(int index) {
        return colors[index];
    }
}


