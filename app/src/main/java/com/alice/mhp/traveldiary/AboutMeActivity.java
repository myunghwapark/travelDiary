package com.alice.mhp.traveldiary;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

import controller.PermissionController;

public class AboutMeActivity extends CommonActivity {

    TextView text_email, text_mobile;
    TableRow btn_policy;
    PermissionController permissionController;
    int permissionTarget = 0; // 0:internet, 1:phone call
    private PopupWindow mPopupWindow ;
    String privacyPolicyUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_me);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.title_about_me));
        actionBar.setDisplayHomeAsUpEnabled(true);

        text_email = findViewById(R.id.text_email);
        text_email.setOnClickListener(btnAboutMeClick);

        text_mobile = findViewById(R.id.text_mobile);
        text_mobile.setOnClickListener(btnAboutMeClick);

        btn_policy = findViewById(R.id.btn_policy);
        btn_policy.setOnClickListener(btnAboutMeClick);

        privacyPolicyUrl = getResources().getString(R.string.url_privacy_policy);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu, menu);
        mMenu = menu;
        mMenu.getItem(3).setVisible(false);
        return true;
    }

    public void checkInternetPermission() {
        ArrayList<String> permissionList = new ArrayList<String>();
        permissionList.add(Manifest.permission.INTERNET);
        permissionTarget = 0;
        permissionController = new PermissionController(this, permissionList);

        if(permissionController.permissionCheck) {
            showPrivacyPolicy();
        }
    }

    public void showPrivacyPolicy() {

        try {
            View popupView = getLayoutInflater().inflate(R.layout.webview, null);
            mPopupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

            // when click dialog outside, popup window finish
            mPopupWindow.setFocusable(true);
            mPopupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

            Button btnClose = popupView.findViewById(R.id.btn_close);
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPopupWindow.dismiss();
                }
            });

            WebView webView = popupView.findViewById(R.id.webview_policy);
            webView.setWebViewClient(new WebViewClient());
            WebSettings mWebSettings = webView.getSettings();
            mWebSettings.setJavaScriptEnabled(true);

            webView.loadUrl(privacyPolicyUrl);
        }
        catch (SecurityException e) {
            e.printStackTrace();
        }


    }

    public void sendEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        try {
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{text_email.getText().toString()});

            emailIntent.setType("text/html");
            emailIntent.setPackage("com.google.android.gm");
            if(emailIntent.resolveActivity(getPackageManager())!=null)
                startActivity(emailIntent);

            startActivity(emailIntent);
        } catch (Exception e) {
            e.printStackTrace();

            emailIntent.setType("text/html");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{text_email.getText().toString()});

            startActivity(Intent.createChooser(emailIntent, "Send Email"));
        }
    }

    public void callToDeveloper() {

        ArrayList<String> permissionList = new ArrayList<String>();
        permissionList.add(Manifest.permission.CALL_PHONE);
        permissionTarget = 1;
        permissionController = new PermissionController(this, permissionList);

        if(permissionController.permissionCheck) {
            try {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "+642108860160"));
                startActivity(intent);
            }
            catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    public View.OnClickListener btnAboutMeClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.text_email:
                    sendEmail();
                    break;

                case R.id.text_mobile:
                    callToDeveloper();
                    break;

                case R.id.btn_policy:
                    checkInternetPermission();
                    break;

                default:
                    break;

            }
        }
    };


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if(permissionTarget == 0) {
                showPrivacyPolicy();
            }
            else if(permissionTarget == 1) {
                try {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "+642108860160"));
                    startActivity(intent);
                }
                catch (SecurityException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    // When back button click
    @Override
    public boolean onSupportNavigateUp()

    {
        this.finish();
        return super.onSupportNavigateUp();

    }

}
