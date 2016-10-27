package us.koller.todolist.Activities;

import com.google.android.gms.common.GoogleApiAvailability;

import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import us.koller.todolist.BuildConfig;
import us.koller.todolist.R;
import us.koller.todolist.RecyclerViewAdapters.Info_RVAdapter;
import us.koller.todolist.Util.Callbacks.OnItemClickInterface;
import us.koller.todolist.Util.ClickHelper.OnItemClickHelper;
import us.koller.todolist.Util.ThemeHelper;


/**
 * Created by Lukas on 15.11.2015.
 */
public class InfoActivity extends AppCompatActivity {

    private ThemeHelper helper;

    private RecyclerView mRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*if (!getResources().getBoolean(R.bool.tablet)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }*/

        setContentView(R.layout.activity_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.info_activity_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        initRecyclerView();

        initTheme(toolbar);
    }

    public InfoActivity() {
    }

    public void initRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.info_recyclerView);
        mRecyclerView.setHasFixedSize(true);

        addOnItemTouchListenerToRecyclerView();

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        String[] itemsText = {getString(R.string.version),
                getString(R.string.developer), getString(R.string.supported_languages),
                getString(R.string.translators), getString(R.string.legal_notices),
                getString(R.string.report_a_bug), getString(R.string.share_app)};

        String[] itemsText_small = {BuildConfig.VERSION_NAME,
                getString(R.string.developer_name), getString(R.string.english_german),
                "", "", "", ""};

        Drawable[] drawables = {ContextCompat.getDrawable(InfoActivity.this, R.drawable.ic_info_outline_grey_700_48dp),
                ContextCompat.getDrawable(InfoActivity.this, R.drawable.ic_account_circle_grey_700_48dp),
                ContextCompat.getDrawable(InfoActivity.this, R.drawable.ic_language_grey_700_48dp),
                ContextCompat.getDrawable(InfoActivity.this, R.drawable.ic_translate_black_48dp),
                ContextCompat.getDrawable(InfoActivity.this, R.drawable.ic_description_grey_700_48dp),
                ContextCompat.getDrawable(InfoActivity.this, R.drawable.ic_bug_report_grey_700_48dp),
                ContextCompat.getDrawable(InfoActivity.this, R.drawable.ic_share_white_48dp),};

        Info_RVAdapter mAdapter = new Info_RVAdapter(itemsText, itemsText_small, drawables);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void addOnItemTouchListenerToRecyclerView() {
        OnItemClickHelper.addTo(mRecyclerView).setOnItemClickListener(new OnItemClickInterface() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, final int position, final RecyclerView.ViewHolder holder) {
                switch (position) {
                    case 0: /*nothing*/
                        break;
                    case 1:
                        developerClicked();
                        break;
                    case 2: /*nothing*/
                        break;
                    case 3:
                        translatorsClicked();
                        break;
                    case 4:
                        licencesClicked();
                        break;
                    case 5:
                        reportBugButtonClicked();
                        break;
                    case 6:
                        shareApp();
                        break;
                }
            }
        });
    }

    public void initTheme(Toolbar toolbar) {
        helper = new ThemeHelper(this);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.info_activity_layout);

        linearLayout.setBackgroundColor(helper.get(ThemeHelper.CORD_COLOR));
        toolbar.setBackgroundColor(helper.get(ThemeHelper.TOOLBAR_COLOR));
        toolbar.setTitleTextColor(helper.get(ThemeHelper.TOOLBAR_TEXT_COLOR));
        if (helper.get(ThemeHelper.CORD_COLOR) != helper.get(ThemeHelper.TOOLBAR_COLOR)) {
            toolbar.setSelected(true);
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

    public void licencesClicked() {
        View layout = View.inflate(this, R.layout.licences_layout, null);

        TextView text_view_1 = (TextView) layout.findViewById(R.id.text_view_1);
        TextView text_view_2 = (TextView) layout.findViewById(R.id.text_view_2);
        TextView text_view_3 = (TextView) layout.findViewById(R.id.text_view_3);

        WebView web_view_1 = (WebView) layout.findViewById(R.id.web_view_1);
        WebView web_view_2 = (WebView) layout.findViewById(R.id.web_view_2);
        WebView web_view_3 = (WebView) layout.findViewById(R.id.web_view_3);

        text_view_1.setTextColor(getDialogTextColor());
        text_view_2.setTextColor(getDialogTextColor());
        text_view_3.setTextColor(getDialogTextColor());

        web_view_1.loadDataWithBaseURL("",
                getString(R.string.licences_text2), "text/html", "UTF-8", "");
        web_view_2.loadDataWithBaseURL("",
                getString(R.string.licences_text4), "text/html", "UTF-8", "");
        web_view_3.loadDataWithBaseURL("", GoogleApiAvailability.getInstance()
                .getOpenSourceSoftwareLicenseInfo(InfoActivity.this), "text/html", "UTF-8", "");

        AlertDialog dialog =
                new AlertDialog.Builder(InfoActivity.this, getDialogTheme())
                        .setTitle(getString(R.string.legal_notices))
                        .setView(layout)
                        .setCancelable(true)
                        .create();
        dialog.show();
    }

    public void translatorsClicked() {
        View layout = View.inflate(this, R.layout.translators, null);

        TextView text_view0 = (TextView) layout.findViewById(R.id.name0);
        TextView text_view1 = (TextView) layout.findViewById(R.id.name1);
        TextView text_view2 = (TextView) layout.findViewById(R.id.name2);
        TextView text_view3 = (TextView) layout.findViewById(R.id.name3);
        TextView text_view4 = (TextView) layout.findViewById(R.id.name4);

        text_view0.setTextColor(getDialogTextColor());
        text_view1.setTextColor(getDialogTextColor());
        text_view2.setTextColor(getDialogTextColor());
        text_view3.setTextColor(getDialogTextColor());
        text_view4.setTextColor(getDialogTextColor());

        AlertDialog dialog = new AlertDialog.Builder(InfoActivity.this, getDialogTheme())
                .setView(layout)
                .setCancelable(true)
                .create();
        if(dialog.getWindow() != null){
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimations;
        }
        dialog.show();
    }

    public void robbeCardClicked(View v) {
        openGPlus("102428112980509360621");
    }

    public void oemerCardClicked(View v) {
        openGPlus("116337351061902993856");
    }

    public void developerClicked() {
        openGPlus("107903926422996280765");
    }

    public void openGPlus(String profile) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setClassName("com.google.android.apps.plus",
                    "com.google.android.apps.plus.phone.UrlGatewayActivity");
            intent.putExtra("customAppUri", profile);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/" + profile + "/posts")));
        }
    }

    public void shareApp() {
        String text = "Hey check out that awesome App called TODOList at: todolist.koller.us";

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void doesNothing(View v) {
        //Only for the ripple Effect
    }

    public int getDialogTheme() {
        int theme;
        if (helper.lightCoordColor()) {
            theme = R.style.DialogTheme_light;
        } else {
            theme = R.style.DialogTheme_dark;
        }
        return theme;
    }

    public int getDialogTextColor() {
        if (helper.lightCoordColor()) {
            return helper.getDarkTextColor();
        }
        return helper.getLightTextColor();
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
