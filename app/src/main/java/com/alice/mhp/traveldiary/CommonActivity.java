package com.alice.mhp.traveldiary;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class CommonActivity extends AppCompatActivity {

    Menu mMenu;
    ProgressDialog asyncDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;

        switch (item.getItemId()) {
            case R.id.action_show_list:
                intent = new Intent(CommonActivity.this, ListTravelActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case R.id.action_show_map:
                intent = new Intent(CommonActivity.this, MapTravelActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("prevPage", "list");
                startActivity(intent);
                return true;
            case R.id.action_plus_trip:
                intent = new Intent(CommonActivity.this, CreateTravelActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            case R.id.action_go_aboutMe:
                intent = new Intent(CommonActivity.this, AboutMeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (asyncDialog != null) {
            asyncDialog.dismiss();
            asyncDialog = null;
        }
    }

    public void showProgress(String msg) {

        if(asyncDialog == null) {
            asyncDialog = new ProgressDialog(CommonActivity.this);
        }
        asyncDialog.setMessage(msg);
        asyncDialog.show();
    }

    public void hideProgress(){
        if( asyncDialog != null && asyncDialog.isShowing() ) {
            asyncDialog.dismiss();
        }
    }


}
