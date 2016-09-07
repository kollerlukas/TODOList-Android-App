package com.koller.lukas.todolist;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.util.concurrent.ExecutionException;


/**
 * Created by Lukas on 15.11.2015.
 */
public class InfoActivity extends AppCompatActivity {

    private String selected_theme;
    private ProgressDialog mProgressDialog;
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

        setupTheme(toolbar);
    }

    public InfoActivity() {
    }

    public void setupTheme(Toolbar toolbar) {
        helper = new ThemeHelper(this);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.info_activity_layout);
        if (helper.rgbSum() == 0) {
            CardView iconCard = (CardView) findViewById(R.id.icon_card);
            iconCard.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.dark));
            CardView infocard = (CardView) findViewById(R.id.infocard);
            infocard.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.dark));
            ImageView image2 = (ImageView) findViewById(R.id.image2);
            image2.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.white), PorterDuff.Mode.SRC_ATOP);
            ImageView image3 = (ImageView) findViewById(R.id.image3);
            image3.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.white), PorterDuff.Mode.SRC_ATOP);
            ImageView image4 = (ImageView) findViewById(R.id.image4);
            image4.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.white), PorterDuff.Mode.SRC_ATOP);
            ImageView image5 = (ImageView) findViewById(R.id.image5);
            image5.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.white), PorterDuff.Mode.SRC_ATOP);
            ImageView image6 = (ImageView) findViewById(R.id.image6);
            image6.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.white), PorterDuff.Mode.SRC_ATOP);
            ImageView image7 = (ImageView) findViewById(R.id.image7);
            image7.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.white), PorterDuff.Mode.SRC_ATOP);
            ImageView image8 = (ImageView) findViewById(R.id.image8);
            image8.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.white), PorterDuff.Mode.SRC_ATOP);
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

        relativeLayout.setBackgroundColor(helper.cord_color);
        toolbar.setBackgroundColor(helper.toolbar_color);
        toolbar.setTitleTextColor(helper.toolbar_textcolor);

        View drawerIcon;
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            if (toolbar.getChildAt(i) instanceof ImageView) {
                drawerIcon = toolbar.getChildAt(i);
                ((ImageView) drawerIcon).setColorFilter(helper.toolbar_textcolor, PorterDuff.Mode.SRC_ATOP);
            }
        }
    }

    public void reportBugButtonClicked(View v) {
        String emailContent = "<b>" + getString(R.string.do_not_remove) + " " + "</b>" + "Android Version: " + System.getProperty("os.version") + "; " + "SDK: " + Build.VERSION.SDK_INT + "<b>" + " " + getString(R.string.do_not_remove) + "</b>";
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "lukaskoller6@gmail.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "TODOList " + getString(R.string.app_version) + " Bug Report");
        emailIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(emailContent));
        String[] addresses = new String[1];
        addresses[0] = "lukaskoller6@gmail.com";
        emailIntent.putExtra(Intent.EXTRA_EMAIL, addresses);
        startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email)));
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
        mProgressDialog = new ProgressDialog(this);
        //mProgressDialog.show();
        LayoutInflater layoutInflater = InfoActivity.this.getLayoutInflater();
        View layout = layoutInflater.inflate(R.layout.licences_layout, null);
        TextView text_view1 = (TextView) layout.findViewById(R.id.text_view1);
        TextView text_view2 = (TextView) layout.findViewById(R.id.text_view2);
        CardView card_text_1 = (CardView) layout.findViewById(R.id.card_text_1);
        TextView text_view3 = (TextView) layout.findViewById(R.id.text_view3);
        WebView webView = (WebView) layout.findViewById(R.id.web_view);
        text_view1.setTextColor(getDialogTextColor());
        text_view2.setTextColor(getDialogTextColor());
        text_view3.setTextColor(getDialogTextColor());
        if (!helper.lightCordColor()) {
            card_text_1.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.dark_grey));
        }
        webView.loadDataWithBaseURL("", GoogleApiAvailability.getInstance().getOpenSourceSoftwareLicenseInfo(InfoActivity.this), "text/html", "UTF-8", "");
        android.support.v7.app.AlertDialog dialog = new android.support.v7.app.AlertDialog.Builder(InfoActivity.this, getDialogTheme())
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
}
