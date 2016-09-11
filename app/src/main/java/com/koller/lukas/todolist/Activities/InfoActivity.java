package com.koller.lukas.todolist.Activities;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.GoogleApiAvailability;
import com.koller.lukas.todolist.R;
import com.koller.lukas.todolist.Util.ThemeHelper;


/**
 * Created by Lukas on 15.11.2015.
 */
public class InfoActivity extends AppCompatActivity {

    private ThemeHelper helper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!getResources().getBoolean(R.bool.tablet)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        setContentView(R.layout.activity_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.info_activity_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        initTheme(toolbar);
    }

    public InfoActivity() {
    }

    public void initTheme(Toolbar toolbar) {
        helper = new ThemeHelper(this);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.info_activity_layout);
        if (helper.coordColorRgbSum() == 0) {
            CardView iconCard = (CardView) findViewById(R.id.icon_card);
            iconCard.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.dark));
            CardView infocard = (CardView) findViewById(R.id.infocard);
            infocard.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.dark));
            ImageView image2 = (ImageView) findViewById(R.id.image2);
            image2.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.white), PorterDuff.Mode.SRC_IN);
            ImageView image3 = (ImageView) findViewById(R.id.image3);
            image3.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.white), PorterDuff.Mode.SRC_IN);
            ImageView image4 = (ImageView) findViewById(R.id.image4);
            image4.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.white), PorterDuff.Mode.SRC_IN);
            ImageView image5 = (ImageView) findViewById(R.id.image5);
            image5.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.white), PorterDuff.Mode.SRC_IN);
            ImageView image6 = (ImageView) findViewById(R.id.image6);
            image6.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.white), PorterDuff.Mode.SRC_IN);
            ImageView image7 = (ImageView) findViewById(R.id.image7);
            image7.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.white), PorterDuff.Mode.SRC_IN);
            ImageView image8 = (ImageView) findViewById(R.id.image8);
            image8.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.white), PorterDuff.Mode.SRC_IN);
            TextView text_view = (TextView) findViewById(R.id.text_view);
            text_view.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            TextView text_view1 = (TextView) findViewById(R.id.text_view1);
            text_view1.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            TextView text_view2 = (TextView) findViewById(R.id.text_view2);
            text_view2.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            TextView text_view3 = (TextView) findViewById(R.id.text_view3);
            text_view3.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            TextView text_view4 = (TextView) findViewById(R.id.text_view4);
            text_view4.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            TextView text_view5 = (TextView) findViewById(R.id.text_view5);
            text_view5.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            TextView text_view6 = (TextView) findViewById(R.id.text_view6);
            text_view6.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            TextView text_view7 = (TextView) findViewById(R.id.text_view7);
            text_view7.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            TextView text_view8 = (TextView) findViewById(R.id.text_view8);
            text_view8.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            TextView text_view9 = (TextView) findViewById(R.id.text_view9);
            text_view9.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            TextView text_view10 = (TextView) findViewById(R.id.text_view10);
            text_view10.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        }

        relativeLayout.setBackgroundColor(helper.get("cord_color"));
        toolbar.setBackgroundColor(helper.get("toolbar_color"));
        toolbar.setTitleTextColor(helper.get("toolbar_textcolor"));
        if(helper.get("cord_color") != helper.get("toolbar_color")){
            elevateToolbar(toolbar);
        }

        View drawerIcon;
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            if (toolbar.getChildAt(i) instanceof ImageView) {
                drawerIcon = toolbar.getChildAt(i);
                ((ImageView) drawerIcon).setColorFilter(helper.getToolbarIconColor(), PorterDuff.Mode.SRC_IN);
            }
        }
    }

    public void reportBugButtonClicked(View v) {
        String emailContent = "<b>" + getString(R.string.do_not_remove) + " " + "</b>" + "Android Version: "
                + System.getProperty("os.version") + "; " + "SDK: " + Build.VERSION.SDK_INT + "<b>" + " " + getString(R.string.do_not_remove) + "</b>";

        Intent shareIntent = ShareCompat.IntentBuilder.from(InfoActivity.this)
                .setType("text/plain")
                .addEmailTo("lukaskoller6@gmail.com")
                .setSubject("TODOList " + getString(R.string.app_version) + " Bug Report")
                .setHtmlText(emailContent)
                .getIntent();
        if (shareIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(shareIntent);
        }
    }

    public void ContactMeClicked(View v) {
        /*Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "lukaskoller6@gmail.com", null));
        String[] addresses = new String[1];
        addresses[0] = "lukaskoller6@gmail.com";
        emailIntent.putExtra(Intent.EXTRA_EMAIL, addresses);
        startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email)));*/
        developerClicked();
    }

    public void LicencesClicked(View v) {
        LayoutInflater layoutInflater = InfoActivity.this.getLayoutInflater();
        View layout = layoutInflater.inflate(R.layout.licences_layout, null);

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

        android.support.v7.app.AlertDialog dialog =
                new android.support.v7.app.AlertDialog.Builder(InfoActivity.this, getDialogTheme())
                .setTitle(getString(R.string.legal_notices))
                .setView(layout)
                .setCancelable(true)
                .create();
        dialog.show();
    }

    public void TranslatorsClicked(View v) {
        LayoutInflater layoutInflater = this.getLayoutInflater();
        View layout = layoutInflater.inflate(R.layout.translators, null);

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
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimations;
        dialog.show();
    }

    public void robbeCardClicked(View v){
        openGPlus("102428112980509360621");
    }

    public void oemerCardClicked(View v){
        openGPlus("116337351061902993856");
    }

    public void developerClicked(){
        openGPlus("107903926422996280765");
    }

    public void openGPlus(String profile) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setClassName("com.google.android.apps.plus", "com.google.android.apps.plus.phone.UrlGatewayActivity");
            intent.putExtra("customAppUri", profile);
            startActivity(intent);
        } catch(ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/"+profile+"/posts")));
        }
    }

    public void shareApp(View v) {
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
        if (helper.lightCordColor()) {
            theme = R.style.DialogTheme_light;
        } else {
            theme = R.style.DialogTheme_dark;
        }
        return theme;
    }

    public int getDialogTextColor() {
        int TextColor;
        if (helper.lightCordColor()) {
            TextColor = ContextCompat.getColor(getApplicationContext(), R.color.light_text_color);
        } else {
            TextColor = ContextCompat.getColor(getApplicationContext(), R.color.dark_text_color);
        }
        return TextColor;
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
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void elevateToolbar(Toolbar toolbar) {
        AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(InfoActivity.this, R.anim.toolbar_raise);
        set.setTarget(toolbar);
        set.start();
    }
}
