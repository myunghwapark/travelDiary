package com.alice.mhp.traveldiary;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import adapter.RecyclerViewDecoration;
import adapter.TravelDetailRecyclerViewAdapter;
import controller.DatabaseController;
import dao.ListItem;

public class DetailTravelActivity extends CommonActivity {

    RecyclerView photo_detail_list;
    ImageButton btn_back, btn_map, btn_edit;
    TextView text_detail_title;
    DatabaseController databaseController;
    public ArrayList<ListItem> travelList;
    public TravelDetailRecyclerViewAdapter dAdapter;
    public boolean detailListFirst = true;
    String travelId, coverPhotoId = "";
    Paint paint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_travel);

        getSupportActionBar().hide();

        databaseController = new DatabaseController(this);

        photo_detail_list = findViewById(R.id.photo_detail_list);
        photo_detail_list.setLayoutManager(new LinearLayoutManager(this));

        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(btnClickMenu);

        btn_map = findViewById(R.id.btn_map);
        btn_map.setOnClickListener(btnClickMenu);

        btn_edit = findViewById(R.id.btn_edit);
        btn_edit.setOnClickListener(btnClickMenu);

        text_detail_title = findViewById(R.id.text_detail_title);

        paint = new Paint();
        travelList = new ArrayList<>();

        travelId = getIntent().getExtras().getString("travelId");

        ShowDetailListThread showDetailListThread = new ShowDetailListThread();
        showDetailListThread.start();

    }


    public void showTravelDetail(String travelId) {

        Cursor cursor = databaseController.getPhotoList(travelId);
        String title = "";

        if(cursor != null && cursor.moveToFirst()) {

            do {
                ListItem item = new ListItem();
                item.setTravelId(""+cursor.getString(0));
                item.setPhotoId(""+cursor.getString(1));
                item.setFileName(cursor.getString(2));
                item.setLatitude(cursor.getString(3));
                item.setLongitude(cursor.getString(4));
                item.setDateTime(cursor.getString(5));
                item.setMemo(cursor.getString(6));
                item.setPhotoLocation(cursor.getString(7));
                travelList.add(item);

                title = cursor.getString(10);
            } while (cursor.moveToNext());
            cursor.close();

            text_detail_title.setText(title);

            try{
                if(detailListFirst) {
                    detailListFirst = false;
                    dAdapter = new TravelDetailRecyclerViewAdapter(travelList);
                    photo_detail_list.setAdapter(dAdapter);
                    photo_detail_list.addItemDecoration(new RecyclerViewDecoration(10));
                }
                else {
                    dAdapter.notifyDataSetChanged();
                }

            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }


    public void removeData(int num){

        // delete file
        String path = getFilesDir().toString() + travelList.get(num).getFileName();
        File photoFile = new File(path);
        if(photoFile.exists()) {
            photoFile.delete();
        }

        // delete database
        String photoId = travelList.get(num).getPhotoId();
        databaseController.deleteTravelPhotoIndividual(photoId);

        if(coverPhotoId.equals(photoId)) {
            coverPhotoId = "";
        }

        // delete from view
        travelList.remove(num);
        dAdapter.notifyDataSetChanged();
    }



    public View.OnClickListener btnClickMenu = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            Intent intent;
            switch (v.getId()) {

                case R.id.btn_back:
                    intent = new Intent(DetailTravelActivity.this, ListTravelActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    break;

                case R.id.btn_map:
                    intent = new Intent(DetailTravelActivity.this, MapTravelActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("travelId", travelId);
                    intent.putExtra("prevPage", "detail");
                    startActivity(intent);
                    break;

                case R.id.btn_edit:
                    intent = new Intent(DetailTravelActivity.this, CreateTravelActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("travelId", travelId);
                    startActivity(intent);
                    break;

                default:
                    break;
            }

        }

    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                Intent intent = new Intent(DetailTravelActivity.this, ListTravelActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;

            default:
                return true;
        }

    }

    public class ShowDetailListThread extends Thread {
        @Override
        public void run() {
            showTravelDetail(travelId);
        }
    }

}
