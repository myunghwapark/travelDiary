package com.alice.mhp.traveldiary;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import adapter.RecyclerViewDecoration;
import adapter.TravelListRecyclerViewAdapter;
import common.Util;
import controller.DatabaseController;
import dao.ListItem;

public class ListTravelActivity extends CommonActivity {

    DatabaseController dbController;
    Util util;

    LinearLayout layout_no_list, layout_search_travel;
    Button btn_addTravel;
    ImageButton btn_search_travel;
    EditText edit_search_travel;
    TextView text_no_list;
    int listCnt = 0;
    Cursor listCursor;
    public ArrayList<ListItem> travelList;
    public TravelListRecyclerViewAdapter lAdapter;
    public boolean listFirst = true;
    private RecyclerView photo_list_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_travel);

        dbController = new DatabaseController(this);
        util = new Util(this);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.title_list));

        photo_list_view = findViewById(R.id.photo_list_view);
        photo_list_view.setLayoutManager(new LinearLayoutManager(this));

        layout_search_travel = findViewById(R.id.layout_search_travel);
        layout_no_list = findViewById(R.id.layout_no_list);
        btn_addTravel = findViewById(R.id.btn_addTravel);
        //photo_list_view = findViewById(R.id.photo_list_view);
        btn_search_travel = findViewById(R.id.btn_search_travel);
        btn_search_travel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                searchTravel();
            }
        });
        edit_search_travel = findViewById(R.id.edit_search_travel);
        edit_search_travel.setOnEditorActionListener(searchEditEvent);
        text_no_list = findViewById(R.id.text_no_list);

        travelList = new ArrayList<>();

        ShowListThread showListThread = new ShowListThread();
        Thread listThread = new Thread(showListThread);
        listThread.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu, menu);
        mMenu = menu;
        mMenu.getItem(0).setVisible(false);
        return true;
    }

    public void showTravelList() {
        listCursor = dbController.getTravelList(listCnt);
        if(listCursor.getCount() == 0) {
            layout_no_list.setVisibility(View.VISIBLE);
            photo_list_view.setVisibility(View.GONE);
            layout_search_travel.setVisibility(View.GONE);
            btn_addTravel.setOnClickListener(btnClickEvent);
        }
        else {
            photo_list_view.setVisibility(View.VISIBLE);
            layout_search_travel.setVisibility(View.VISIBLE);
            layout_no_list.setVisibility(View.GONE);
        }

        if(listCursor.getCount() != 0 && listCursor.moveToFirst()) {
            do {
                photo_list_view.setVisibility(View.VISIBLE);

                ListItem item = new ListItem();
                item.setTravelId(""+listCursor.getString(0));
                item.setAddress(listCursor.getString(1));
                item.setStartDate(listCursor.getString(2));
                item.setEndDate(listCursor.getString(3));
                item.setFileName(listCursor.getString(4));
                item.setTravelTitle(listCursor.getString(5));
                travelList.add(item);
            } while (listCursor.moveToNext());
        }
        else {
            photo_list_view.setVisibility(View.GONE);
        }
        listCursor.close();
        setImage();
    }

    public void setImage(){

        try{

            lAdapter = new TravelListRecyclerViewAdapter(travelList);
            photo_list_view.setAdapter(lAdapter);
            photo_list_view.addItemDecoration(new RecyclerViewDecoration(10));

        }
        catch(OutOfMemoryError e){
            e.printStackTrace();
        }
    }


    public void searchTravel() {
        util.hideKeyboard(edit_search_travel.getWindowToken());
        if(edit_search_travel.getText() != null) {
            listCnt = 0;
            String searchText = edit_search_travel.getText()+"";
            listCursor = dbController.getSearchTravelList(listCnt, searchText);
            travelList = new ArrayList<>();

            if(listCursor.getCount() != 0 && listCursor.moveToFirst()) {
                do {
                    text_no_list.setVisibility(View.GONE);
                    photo_list_view.setVisibility(View.VISIBLE);

                    ListItem item = new ListItem();
                    item.setTravelId(""+listCursor.getString(0));
                    item.setAddress(listCursor.getString(1));
                    item.setStartDate(listCursor.getString(2));
                    item.setEndDate(listCursor.getString(3));
                    item.setFileName(listCursor.getString(4));
                    item.setTravelTitle(listCursor.getString(5));
                    travelList.add(item);
                } while (listCursor.moveToNext());
            }
            else {
                text_no_list.setVisibility(View.VISIBLE);
                photo_list_view.setVisibility(View.GONE);
            }
            listCursor.close();
            setImage();
        }
        else {
            Toast.makeText(this, "Please, fill in the search word.", Toast.LENGTH_SHORT).show();
        }
    }


    public View.OnClickListener btnClickEvent = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.btn_addTravel:
                    Intent intent = new Intent(ListTravelActivity.this, CreateTravelActivity.class);
                    startActivity(intent);
                    break;

                default:
                    break;
            }

        }

    };

    TextView.OnEditorActionListener searchEditEvent = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if(actionId==EditorInfo.IME_ACTION_DONE){
                searchTravel();
            }
            return false;
        }
    };

    public class ShowListThread implements Runnable {
        @Override
        public void run() {

            Message msg = listHandler.obtainMessage();
            listHandler.sendMessage(msg);
        }
    }


    final Handler listHandler = new Handler() {

        public void handleMessage(Message msg) {
            showTravelList();
        }

    };


}
