package us.koller.todolist.Activities;

import com.google.android.gms.common.GoogleApiAvailability;

import android.animation.ObjectAnimator;
import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import us.koller.todolist.BuildConfig;
import us.koller.todolist.R;
import us.koller.todolist.Util.ThemeHelper;


/**
 * Created by Lukas on 15.11.2015.
 */
public class InfoActivity extends AppCompatActivity {

    private ThemeHelper helper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_info);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.info_activity_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("");
        }

        initTheme(toolbar);

        final ScrollView scrollView = (ScrollView) findViewById(R.id.scroll_view);
        scrollView.getViewTreeObserver()
                .addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                    @Override
                    public void onScrollChanged() {
                        if(scrollView.getScrollY() != 0){
                            elevateToolbar(toolbar);
                        } else {
                            deelevateToolbar(toolbar);
                        }
                    }
                });

        TextView version = (TextView) findViewById(R.id.version);
        if(helper.lightCoordColor()){
            version.setTextColor(ContextCompat.getColor(this, R.color.grey700));
        }
        version.setText(BuildConfig.VERSION_NAME);

        ImageView icon = (ImageView) findViewById(R.id.icon);
        Glide.with(this)
                .load("http://todolist.koller.us/todolist_icon_512px")
                .into(icon);

        //Glide license
        View license_item_1 = findViewById(R.id.license_item_1);
        ((TextView) license_item_1.findViewById(R.id.text)).setText(R.string.glide);
        license_item_1.findViewById(R.id.text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW)
                        .setData(Uri.parse("https://github.com/bumptech/glide/blob/master/LICENSE")));
            }
        });
        license_item_1.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW)
                        .setData(Uri.parse("https://github.com/bumptech/glide")));
            }
        });

        //MaterialNumberPicker license
        View license_item_2 = findViewById(R.id.license_item_2);
        ((TextView) license_item_2.findViewById(R.id.text)).setText(R.string.materialnumberpicker);
        license_item_2.findViewById(R.id.text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW)
                        .setData(Uri.parse("https://github.com/KasualBusiness/MaterialNumberPicker/blob/master/LICENSE")));
            }
        });
        license_item_2.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW)
                        .setData(Uri.parse("https://github.com/KasualBusiness/MaterialNumberPicker")));
            }
        });

        //Google Play Service Attribution
        View license_item_3 = findViewById(R.id.license_item_3);
        ((TextView) license_item_3.findViewById(R.id.text)).setText(R.string.google_play_servcie_attribution);
        license_item_3.findViewById(R.id.text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WebView webView = new WebView(InfoActivity.this);
                webView.loadData(GoogleApiAvailability.getInstance()
                        .getOpenSourceSoftwareLicenseInfo(InfoActivity.this), "text/plain", "utf-8");

                AlertDialog dialog = new AlertDialog.Builder(InfoActivity.this)
                        .setTitle("Google Play Servcie Attribution")
                        .setView(webView)
                        .setPositiveButton("Ok", null)
                        .create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(helper.get(ThemeHelper.FAB_COLOR));
            }
        });
        license_item_3.findViewById(R.id.button).setVisibility(View.GONE);
    }

    public InfoActivity() {
    }

    public void initTheme(Toolbar toolbar) {
        helper = new ThemeHelper(this);

        findViewById(R.id.info_activity_layout).setBackgroundColor(helper.get(ThemeHelper.CORD_COLOR));
        toolbar.setBackgroundColor(helper.get(ThemeHelper.TOOLBAR_COLOR));
        toolbar.setTitleTextColor(helper.get(ThemeHelper.TOOLBAR_TEXT_COLOR));
        if (helper.get(ThemeHelper.CORD_COLOR) != helper.get(ThemeHelper.TOOLBAR_COLOR)) {
            elevateToolbar(toolbar);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && helper.lightCoordColor()) {
            toolbar.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        View drawerIcon;
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            if (toolbar.getChildAt(i) instanceof ImageView) {
                drawerIcon = toolbar.getChildAt(i);
                ((ImageView) drawerIcon).setColorFilter(helper.getToolbarIconColor(), PorterDuff.Mode.SRC_IN);
            }
        }

        String title = getString(R.string.app_name);
        BitmapDrawable icon = (BitmapDrawable) ContextCompat.getDrawable(InfoActivity.this, R.mipmap.ic_launcher);

        ActivityManager.TaskDescription tDesc = new ActivityManager.TaskDescription(title, icon.getBitmap(), helper.get(ThemeHelper.TOOLBAR_COLOR));
        this.setTaskDescription(tDesc);
    }

    public void reportBugButtonClicked() {
        String emailContent = "<b>" + getString(R.string.do_not_remove) + " " + "</b>" + "Android Version: "
                + System.getProperty("os.version") + "; " + "SDK: " + Build.VERSION.SDK_INT + "<b>" + " " + getString(R.string.do_not_remove) + "</b>";

        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .addEmailTo("lukaskoller6@gmail.com")
                .setSubject("TODOList " + BuildConfig.VERSION_NAME + " Bug Report")
                .setHtmlText(emailContent)
                .getIntent();
        if (shareIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(shareIntent);
        }
    }

    public void robbeCardClicked(View v) {
        openGPlus("102428112980509360621");
    }

    public void oemerCardClicked(View v) {
        openGPlus("116337351061902993856");
    }

    public void developerClicked(View v) {
        openGPlus("107903926422996280765");
    }

    public void openGPlus(String profile) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/" + profile + "/posts")));
    }

    public void elevateToolbar(Toolbar toolbar) {
        if(!((View) toolbar.getParent()).isSelected()){
            ObjectAnimator.ofFloat((View) toolbar.getParent(), "elevation", 0f,
                    getResources().getDimension(R.dimen.toolbar_elevation)).start();
            ((View) toolbar.getParent()).setSelected(true);
        }
    }

    public void deelevateToolbar(Toolbar toolbar) {
        if(!((View) toolbar.getParent()).isSelected()
                || helper.get(ThemeHelper.CORD_COLOR) != helper.get(ThemeHelper.TOOLBAR_COLOR)){
            return;
        }
        ObjectAnimator.ofFloat((View) toolbar.getParent(), "elevation",
                getResources().getDimension(R.dimen.toolbar_elevation), 0f).start();
        ((View) toolbar.getParent()).setSelected(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_info, menu);
        menu.getItem(0).getIcon().setColorFilter(helper.getToolbarIconColor(), PorterDuff.Mode.SRC_IN);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;
            case R.id.reportBug:
                reportBugButtonClicked();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public int getDialogTheme() {
        int theme;
        if (helper.lightCoordColor()) {
            theme = R.style.DialogTheme_Light;
        } else {
            theme = R.style.DialogTheme;
        }
        return theme;
    }

    @Override
    public void onResume() {
        super.onResume();
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
        this.overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);
    }
}
