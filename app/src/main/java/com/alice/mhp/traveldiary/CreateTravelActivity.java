package com.alice.mhp.traveldiary;

import android.Manifest;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import adapter.WrapContentLinearLayoutManager;
import dao.ListItem;
import adapter.TravelCreateRecyclerViewAdapter;
import adapter.RecyclerViewDecoration;
import dao.PhotoDao;
import dao.TravelDao;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import common.Util;
import controller.DatabaseController;
import common.GeoDegree;
import controller.PermissionController;

import static android.content.ContentValues.TAG;

public class CreateTravelActivity extends CommonActivity {

    private PopupWindow mPopupWindow ;
    FrameLayout layout_startDt, layout_endDt;

    HorizontalScrollView view_years;
    LinearLayout layout_year;
    ArrayList<Button> btnYearList;
    CalendarView calendar;
    TextView text_startDt, text_endDt, text_calendar_title;
    EditText edit_travel_title;
    Button btn_cancel, btn_save, btnAddPhoto, btn_close;
    ImageButton btn_remove_travel;
    String startDate, endDate, tempDate;
    boolean startDateYn = true;
    Util util;
    PermissionController permissionController;
    static DatabaseController databaseController;

    private static final int PICK_FROM_ALBUM = 1;
    private static final int PICK_IMAGE_MULTIPLE = 1;
    private static ArrayList<ListItem> imagesEncodedList;
    private boolean firstAdd = true;
    String travelId = "";
    static String coverPhotoId;

    private RecyclerView viewEditRecycler;
    private static TravelCreateRecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_travel);
        util = new Util(this);

        ArrayList<String> permissionList = new ArrayList<String>();
        permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissionList.add(Manifest.permission.INTERNET);

        permissionController = new PermissionController(this, permissionList);
        databaseController = new DatabaseController(this);

        getSupportActionBar().hide();

        edit_travel_title = findViewById(R.id.edit_travel_title);
        btnAddPhoto = findViewById(R.id.btnAddPhoto);
        text_startDt = findViewById(R.id.text_startDt);
        text_endDt = findViewById(R.id.text_endDt);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_save = findViewById(R.id.btn_save);
        btn_remove_travel = findViewById(R.id.btn_remove_travel);

        viewEditRecycler = findViewById(R.id.viewEditRecycler);
        LinearLayoutManager linearLayoutManager = new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        viewEditRecycler.setLayoutManager(linearLayoutManager);


        layout_startDt = findViewById(R.id.layout_startDt);
        layout_endDt = findViewById(R.id.layout_endDt);

        btn_cancel.setOnClickListener(btnFunctionClick);
        btn_save.setOnClickListener(btnFunctionClick);
        btnAddPhoto.setOnClickListener(btnFunctionClick);

        layout_startDt.setOnClickListener(clickDateTab);
        layout_endDt.setOnClickListener(clickDateTab);


        if(getIntent().getExtras() != null) {
            travelId = getIntent().getExtras().getString("travelId");
            if(travelId != null && !travelId.equals("")) {
                ShowDetailThread showDetailThread = new ShowDetailThread();
                Thread listThread = new Thread(showDetailThread);
                listThread.start();
            }
        }
        else {
            initialize();
        }


    }

    public void initialize() {

        imagesEncodedList = new ArrayList<ListItem>();

        btn_remove_travel.setVisibility(View.GONE);

        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        startDate = dateFormat.format(date);
        endDate = dateFormat.format(date);


        try {
            SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd MMM yyyy");
            Date startDt = dateFormat.parse(startDate);
            Date endDt = dateFormat.parse(endDate);
            text_startDt.setText(dateFormat2.format(startDt));
            text_endDt.setText(dateFormat2.format(endDt));

        }
        catch (ParseException pe) {
            pe.printStackTrace();
        }
    }

    // Setting when modify travel
    public void showTravelDetail(String travelId) {

        btn_remove_travel.setOnClickListener(btnFunctionClick);

        Cursor cursor = databaseController.getPhotoList(travelId);
        imagesEncodedList = new ArrayList<ListItem>();

        if(cursor != null && cursor.moveToFirst()) {

            do {
                startDate = cursor.getString(8);
                endDate = cursor.getString(9);
                setEditDate();

                edit_travel_title.setText(cursor.getString(10));
                coverPhotoId = cursor.getString(11);

                ListItem listItem = new ListItem();
                listItem.setPhotoId(cursor.getString(1));
                listItem.setDateTime(cursor.getString(5));
                listItem.setAddress(cursor.getString(7));
                listItem.setLatitude(cursor.getString(3));
                listItem.setLongitude(cursor.getString(4));
                listItem.setCoverPhotoYn(cursor.getString(12));
                listItem.setFilePath(getFilesDir().toString()+cursor.getString(2));

                imagesEncodedList.add(listItem);
            } while (cursor.moveToNext());
            cursor.close();

            try{
                firstAdd = false;
                mAdapter = new TravelCreateRecyclerViewAdapter(imagesEncodedList);
                viewEditRecycler.setAdapter(mAdapter);
                viewEditRecycler.addItemDecoration(new RecyclerViewDecoration(10));
            }
            catch(OutOfMemoryError e){
                e.printStackTrace();
            }
        }
    }

    public void setEditDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd MMM yyyy");

        try {
            Date startDt = dateFormat.parse(startDate);
            text_startDt.setText(dateFormat2.format(startDt));

            Date endDt = dateFormat.parse(endDate);
            text_endDt.setText(dateFormat2.format(endDt));
        }
        catch (ParseException pe) {
            pe.printStackTrace();
        }
    }

    public void setDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd MMM yyyy");

        try {
            if(startDateYn) {
                startDate = tempDate;
                Date startDt = dateFormat.parse(startDate);
                text_startDt.setText(dateFormat2.format(startDt));
            }
            else {
                endDate = tempDate;
                Date endDt = dateFormat.parse(endDate);
                text_endDt.setText(dateFormat2.format(endDt));
            }
        }
        catch (ParseException pe) {
            pe.printStackTrace();
        }
    }

    public void createYears() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int startYear = year - 50;
        int endYear = year + 50;
        btnYearList = new ArrayList<>();
        for(int yearNum = startYear; yearNum <= endYear; yearNum++) {
            Button btnYear = new Button(this);
            btnYear.setBackground(getResources().getDrawable(R.drawable.btn_pink_no_border));
            btnYear.setTextColor(getResources().getColor(R.color.btnGrayBorder));
            btnYear.setText(""+yearNum);
            btnYear.setHeight(50);
            btnYear.setId(yearNum);
            if(yearNum == year) {
                btnYear.setBackground(getResources().getDrawable(R.drawable.btn_pink_border));
                btnYear.setTextColor(getResources().getColor(R.color.darkGray));
            }
            btnYear.setOnClickListener(btnYearOnClickListener);
            btnYearList.add(btnYear);
            layout_year.addView(btnYear);
        }


        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                final int center = view_years.getScrollX() + view_years.getWidth() / 2;
                final int viewLeft = layout_year.getLeft();
                final int viewWidth = layout_year.getWidth();

                view_years.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (center >= viewLeft && center <= viewLeft + viewWidth) {
                            view_years.scrollBy((viewLeft + viewWidth / 2) - center, 0);
                        }
                    }
                });
            }
        }, 500 );


    }

    public void setYearToCalendar(String year) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd");
            if(startDateYn) {
                tempDate = year+startDate.substring(4, startDate.length());
            }
            else {
                tempDate = year+endDate.substring(4, endDate.length());
            }

            Date date = df.parse(tempDate);
            long time = date.getTime();
            calendar.setDate(time);
        }
        catch (ParseException pe) {
            pe.printStackTrace();
        }
    }

    public void setCalendarToYear(int year, String strDate) {

        tempDate = strDate;

        for(int yearNum=0; yearNum<btnYearList.size(); yearNum++) {
            if(btnYearList.get(yearNum).getId() == year) {
                scrollButton(btnYearList.get(yearNum));
                break;
            }
        }
    }

    public void scrollButton(Button btnYear) {

        for(int yearLayout = 0; yearLayout < layout_year.getChildCount(); yearLayout++) {
            Button btn = (Button)layout_year.getChildAt(yearLayout);
            btn.setTextColor(getResources().getColor(R.color.btnGrayBorder));
            btn.setBackground(getResources().getDrawable(R.drawable.btn_pink_no_border));
        }

        int[] lo = new int[2];
        btnYear.getLocationOnScreen(lo);
        btnYear.setBackground(getResources().getDrawable(R.drawable.btn_pink_border));
        btnYear.setTextColor(getResources().getColor(R.color.darkGray));

        // x coordinate of button
        int posX = lo[0];

        // half the width of the button
        int halfWidth = (int) (btnYear.getWidth() * 0.5f);

        // width of screen
        int screenWith = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();

        // half the width of the screen
        int halfScreenWidth = (int) (screenWith * 0.5f);

        // Obtain the gap between the middle position of the screen and the middle position of the button.
        final int scroll = posX + halfWidth - halfScreenWidth;
        view_years.post(new Runnable()
        {
            @Override
            public void run()
            {
                view_years.smoothScrollBy(scroll, 0);
            }
        });

    }

    public void formCheck() {

        String sDate = startDate.replaceAll("\\.", "");
        String eDate = endDate.replaceAll("\\.", "");

        if(sDate.compareTo(eDate) > 0) {
            util.alert("Notice", "Start date must be before end date.", "OK");
            return;
        }
        else if(imagesEncodedList.size() == 0){
            util.alert("Notice", "Please, add photos.", "OK");
            return;
        }
        else if(edit_travel_title.getText().toString().length() == 0) {
            util.alert("Notice", "Please, fill in the title of travel", "OK");
            return;
        }


                if(travelId.equals("")) {
                    showProgress("Loading..");
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            SaveNewTravelThread saveNewTravelThread = new SaveNewTravelThread();
                            Thread newTravelThread = new Thread(saveNewTravelThread);
                            newTravelThread.start();
                        }
                    }, 500 );
                }
                else {
                    confirm("modify");
                }



    }

    public void saveNewTravel() {


        TravelDao travelDao = new TravelDao();

        try {

            ArrayList<String> travelLocations = new ArrayList<>();
            String fileName = "";
            for(int imageNum=0; imageNum<imagesEncodedList.size(); imageNum++) {

                fileName = "P"+util.getTodayWithTime()+"_"+imageNum;
                Bitmap photo = util.safeDecodeBitmapFile(imagesEncodedList.get(imageNum).getFilePath());
                boolean photoSaveResult = util.SaveBitmapToFileCache(fileName, photo);
                imagesEncodedList.get(imageNum).setFileName(fileName);

                if(photoSaveResult) {
                    ListItem listItem = imagesEncodedList.get(imageNum);
                    PhotoDao photoDao = new PhotoDao();
                    photoDao.setTravelId(databaseController.getLastTravelId()+1);
                    photoDao.setPhotoLocation(listItem.getAddress());
                    photoDao.setPhotoLatitude(listItem.getLatitude());
                    photoDao.setPhotoLongitude(listItem.getLongitude());
                    photoDao.setPhotoDateTime(listItem.getDateTime());
                    photoDao.setPhotoUrl(listItem.getFileName());
                    photoDao.setCreateDate(util.getTodayWithTime());

                    // save photos first to get cover photo id
                    if(imageNum == 0) {
                        photoDao.setPhotoCoverYn("Y");
                    }
                    else {
                        photoDao.setPhotoCoverYn("N");
                    }
                    databaseController.insertTravelPhoto(photoDao);
                }
                else {
                    Toast.makeText(this, "Failed saving photo", Toast.LENGTH_SHORT).show();
                }

                boolean addNewLoc = true;
                for(int location=0; location<travelLocations.size();location++) {
                    if(travelLocations.get(location).equals(imagesEncodedList.get(imageNum).getAddress())) {
                        addNewLoc = false;
                    }
                }
                if(addNewLoc) {
                    travelLocations.add(imagesEncodedList.get(imageNum).getAddress());
                }
            }


            String location = "";
            for(int locNum=0;locNum<travelLocations.size();locNum++) {

                location += travelLocations.get(locNum);
                if(!location.equals("") && locNum < travelLocations.size()-1) {
                    location += ", ";
                }
            }

            coverPhotoId = databaseController.getCoverPhotoId();

            travelDao.setTravelTitle(""+edit_travel_title.getText());
            travelDao.setTravelStartDate(startDate);
            travelDao.setTravelEndDate(endDate);
            travelDao.setCoverPhotoId(coverPhotoId);
            travelDao.setCreateDate(util.getTodayWithTime());
            travelDao.setTravelLocation(location);
            travelDao = databaseController.insertTravel(travelDao);

        }
        catch (Exception e) {
            hideProgress();
            e.printStackTrace();
        }
        hideProgress();


        if(travelDao.getResult()) {
            AlertDialog.Builder newTravelAlert = new AlertDialog.Builder(this);

            newTravelAlert.setTitle("Notice")
                    .setMessage("Saved successfully.")
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    Intent intent = new Intent(CreateTravelActivity.this, ListTravelActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }
                            });


            newTravelAlert.show();
        }
    }

    public void processModifyTravel() {
        showProgress("Loading..");
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                ModifyTravelThread modifyTravelThread = new ModifyTravelThread();
                Thread modifyThread = new Thread(modifyTravelThread);
                modifyThread.start();
            }
        }, 500 );
    }

    public void modifyTravel() {


        TravelDao travelDao = new TravelDao();

        try {
            ArrayList<String> travelLocations = new ArrayList<>();

            String fileName = "";
            for(int imageNum=0; imageNum<imagesEncodedList.size(); imageNum++) {

                ListItem listItem = imagesEncodedList.get(imageNum);
                if(listItem.getPhotoId() == null || listItem.getPhotoId().equals("")) {
                    fileName = "P"+util.getTodayWithTime()+"_"+imageNum;
                    Bitmap photo = util.safeDecodeBitmapFile(imagesEncodedList.get(imageNum).getFilePath());
                    util.SaveBitmapToFileCache(fileName, photo);
                    imagesEncodedList.get(imageNum).setFileName(fileName);
                }

                PhotoDao photoDao = new PhotoDao();
                photoDao.setTravelId(Integer.parseInt(travelId));
                photoDao.setPhotoLocation(listItem.getAddress());
                photoDao.setPhotoLatitude(listItem.getLatitude());
                photoDao.setPhotoLongitude(listItem.getLongitude());
                photoDao.setPhotoDateTime(listItem.getDateTime());
                photoDao.setUpdateDate(util.getTodayWithTime());
                photoDao.setPhotoUrl(listItem.getFileName());
                photoDao.setPhotoCoverYn(listItem.getCoverPhotoYn());

                // in case of adding new photo
                if(listItem.getPhotoId() == null || listItem.getPhotoId().equals("")) {
                    databaseController.insertTravelPhoto(photoDao);
                }
                else {
                    photoDao.setPhotoId(Integer.parseInt(listItem.getPhotoId()));
                    databaseController.updateTravelPhoto(photoDao);
                }

                boolean addNewLoc = true;
                for(int location=0; location<travelLocations.size();location++) {
                    if(travelLocations.get(location).equals(imagesEncodedList.get(imageNum).getAddress())) {
                        addNewLoc = false;
                    }
                }
                if(addNewLoc) {
                    travelLocations.add(imagesEncodedList.get(imageNum).getAddress());
                }
            }

            String location = "";
            for(int locNum=0;locNum<travelLocations.size();locNum++) {

                location += travelLocations.get(locNum);
                if(!location.equals("") && locNum < travelLocations.size()-1) {
                    location += ", ";
                }
            }

            travelDao.setTravelId(Integer.parseInt(travelId));
            travelDao.setTravelTitle(""+edit_travel_title.getText());
            travelDao.setTravelStartDate(startDate);
            travelDao.setTravelEndDate(endDate);
            if(coverPhotoId==null || coverPhotoId.equals("")) {
                coverPhotoId = imagesEncodedList.get(0).getPhotoId();
            }
            travelDao.setCoverPhotoId(coverPhotoId);
            travelDao.setUpdateDate(util.getTodayWithTime());
            travelDao.setTravelLocation(location);
            travelDao = databaseController.updateTravel(travelDao);


        }
        catch (Exception e) {
            hideProgress();
            e.printStackTrace();
        }

        hideProgress();


        if(travelDao.getResult()) {
            AlertDialog.Builder modifyTravelAlert = new AlertDialog.Builder(this);

            modifyTravelAlert.setTitle("Notice")
                    .setMessage("Modified successfully.")
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    Intent intent = new Intent(CreateTravelActivity.this, ListTravelActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }
                            });


            modifyTravelAlert.show();
        }

    }

    public void removeTravel() {

        try {
            for(int imageNum=0; imageNum<imagesEncodedList.size(); imageNum++) {
                ListItem listItem = imagesEncodedList.get(imageNum);
                String path = getFilesDir().toString() + listItem.getFileName();
                File photoFile = new File(path);
                if(photoFile.exists()) {
                    photoFile.delete();
                }
            }

            boolean delTravelResult = databaseController.deleteTravel(travelId);
            boolean delPhotoResult = databaseController.deleteTravelPhoto(travelId);

            if(delTravelResult && delPhotoResult) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

                alertDialogBuilder.setTitle("Notice")
                        .setMessage("Deleted successfully.")
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        Intent intent = new Intent(CreateTravelActivity.this, ListTravelActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                    }
                                });


                alertDialogBuilder.show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void showCalendarDialog() {
        View popupView = getLayoutInflater().inflate(R.layout.dialog_calendar, null);
        mPopupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        // when click dialog outside, popup window finish
        mPopupWindow.setFocusable(true);
        mPopupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        calendar = popupView.findViewById(R.id.calendar);
        calendar.setOnDateChangeListener(dateChangeListener);

        view_years = popupView.findViewById(R.id.view_years);
        layout_year = popupView.findViewById(R.id.layout_year);
        text_calendar_title = popupView.findViewById(R.id.text_calendar_title);
        if(startDateYn) {
            text_calendar_title.setText("Start Date");
        }
        else {
            text_calendar_title.setText("End Date");
        }

        btn_close = popupView.findViewById(R.id.btn_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        });

        Button cancel = (Button) popupView.findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        });

        Button apply = (Button) popupView.findViewById(R.id.btn_apply);
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate();
                mPopupWindow.dismiss();
            }
        });


        createYears();


    }

    public void confirm(final String confirmType) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        String title = "";
        if(confirmType.equals("delete")) {
            title = "Do you want to delete the travel?";
        }
        else if(confirmType.equals("modify")) {
            title = "Do you want to modify the travel?";
        }
        alertDialogBuilder.setTitle("Notice")
                .setMessage(title)
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                                if(confirmType.equals("delete")) {
                                    removeTravel();
                                }
                                else if(confirmType.equals("modify")) {
                                    processModifyTravel();


                                }
                            }
                        })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });


        alertDialogBuilder.show();
    }




    public View.OnClickListener btnYearOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {

            setYearToCalendar(""+view.getId());
            scrollButton((Button)view);

        }
    };

    public View.OnClickListener btnFunctionClick = new View.OnClickListener() {

        @Override
        public void onClick(View view) {

            switch(view.getId()) {
                case R.id.btn_cancel:
                    finish();
                    return;

                case R.id.btn_save:
                    formCheck();
                    return;

                case R.id.btnAddPhoto:
                    getAlbum();
                    return;

                case R.id.btn_remove_travel:
                    confirm("delete");
                    return;

                default:
                    return;
            }
        }
    };

    public View.OnClickListener clickDateTab = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            // hide keyboard
            util.hideKeyboard(edit_travel_title.getWindowToken());

            switch(view.getId()) {

                case R.id.layout_startDt:
                    layout_startDt.setBackground(getResources().getDrawable(R.color.white));
                    layout_endDt.setBackground(getResources().getDrawable(R.color.gray_light));
                    startDateYn = true;
                    showCalendarDialog();
                    return;

                case R.id.layout_endDt:
                    layout_endDt.setBackground(getResources().getDrawable(R.color.white));
                    layout_startDt.setBackground(getResources().getDrawable(R.color.gray_light));
                    startDateYn = false;
                    showCalendarDialog();
                    return;

                default:
                    return;
            }
        }
    };

    public CalendarView.OnDateChangeListener dateChangeListener = new CalendarView.OnDateChangeListener() {
        @Override
        public void onSelectedDayChange(CalendarView view, int year, int month, int day) {
            month += 1;

            String strMonth = ""+month;
            String strDay = ""+day;
            if(month < 10) {
                strMonth = "0"+month;
            }
            if(day < 10) {
                strDay = "0"+day;
            }

            String strDate = year+"."+strMonth+"."+strDay;
            setCalendarToYear(year, strDate);
        }
    };


    public void getAlbum() {

        if(permissionController.permissionCheck) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                try {

                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                    intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI); // images on the SD card.
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    startActivityForResult(Intent.createChooser(intent, "When it is multiple choice, choose photo"), PICK_FROM_ALBUM);
                }
                catch (Exception e) {
                    e.getStackTrace();
                }
            }
            else {
                Log.e("Kitkat under", "..");
            }
        }
        else {
            Toast.makeText(this, "You can not view photos because you have denied permission.", Toast.LENGTH_SHORT).show();
        }
    }

    public void setImage(){

        try{
            if(firstAdd) {
                firstAdd = false;
                mAdapter = new TravelCreateRecyclerViewAdapter(imagesEncodedList);
                viewEditRecycler.setAdapter(mAdapter);
                viewEditRecycler.addItemDecoration(new RecyclerViewDecoration(10));
            }
            else {
                mAdapter.notifyDataSetChanged();
            }

        }
        catch(OutOfMemoryError e){
            e.printStackTrace();
        }
    }

    public void removeData(final Context context, int num){

        // delete file
        String path = context.getFilesDir().toString() + imagesEncodedList.get(num).getFileName();
        File photoFile = new File(path);
        if(photoFile.exists()) {
            photoFile.delete();
        }

        // delete database
        String photoId = imagesEncodedList.get(num).getPhotoId();
        databaseController.deleteTravelPhotoIndividual(photoId);

        if(coverPhotoId!=null && coverPhotoId.equals(photoId)) {
            coverPhotoId = "";
        }

        // delete from view
        imagesEncodedList.remove(num);
        mAdapter.notifyDataSetChanged();

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            // When an Image is picked
            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK && null != data) {
                // Get the Image from data

                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                if (data.getClipData() != null) {
                    ClipData mClipData = data.getClipData();
                    ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                    for (int num = 0; num < mClipData.getItemCount(); num++) {

                        ListItem listItem = new ListItem();
                        ClipData.Item item = mClipData.getItemAt(num);
                        Uri uri = item.getUri();
                        mArrayUri.add(uri);
                        // Get the cursor
                        Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                        // Move to first row
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

                        // get Photo information
                        try {
                            ExifInterface exif = new ExifInterface(cursor.getString(columnIndex));
                            GeoDegree geoDegree = new GeoDegree(exif);

                            String address = "", latitude = "", longitude = "";
                            if(geoDegree.getLatitude() != null && geoDegree.getLongitude() != null) {
                                latitude = geoDegree.getLatitude().toString();
                                longitude = geoDegree.getLongitude().toString();

                                address = util.getLocation(latitude, longitude);
                            }

                            listItem.setDateTime(exif.getAttribute(ExifInterface.TAG_DATETIME));
                            listItem.setAddress(address);
                            listItem.setLatitude(latitude);
                            listItem.setLongitude(longitude);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        listItem.setFilePath(cursor.getString(columnIndex));
                        imagesEncodedList.add(listItem);
                        cursor.close();

                        setImage();
                    }
                }
                else if(data.getData()!=null){

                    Uri mImageUri=data.getData();

                    // Get the cursor
                    Cursor cursor = getContentResolver().query(mImageUri, filePathColumn, null, null, null);

                    if(cursor.moveToFirst()) {
                        do {
                            ListItem listItem = new ListItem();
                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

                            try {
                                ExifInterface exif = new ExifInterface(cursor.getString(columnIndex));
                                GeoDegree geoDegree = new GeoDegree(exif);

                                String address = "", latitude = "", longitude = "";
                                if(geoDegree.getLatitude() != null && geoDegree.getLongitude() != null) {
                                    latitude = geoDegree.getLatitude().toString();
                                    longitude = geoDegree.getLongitude().toString();

                                    address = util.getLocation(latitude, longitude);
                                }

                                listItem.setDateTime(exif.getAttribute(ExifInterface.TAG_DATETIME));
                                listItem.setAddress(address);
                                listItem.setLatitude(latitude);
                                listItem.setLongitude(longitude);

                            }catch (Exception e) {
                                e.printStackTrace();
                            }
                            listItem.setFilePath(cursor.getString(columnIndex));
                            imagesEncodedList.add(listItem);
                            setImage();
                        } while (cursor.moveToNext());
                    }
                    cursor.close();


                }
                else {
                    Log.d("data.getData", "null, data.getClipData() == null");
                }
            } else {
                Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // in case of more than version 23
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean permissionYn = false;
            for(int result=0; result<grantResults.length; result++) {
                if(grantResults[result]== PackageManager.PERMISSION_GRANTED){
                    Log.v(TAG,"Permission: "+permissions[result]+ "was "+grantResults[result]);
                    permissionYn = true;
                }
                else {
                    permissionYn = false;
                }
            }

            if(permissionYn) {
                permissionController.permissionCheck = true;
                getAlbum();
            }
            else {
                permissionController.permissionCheck = false;
            }

        }
        else {
            permissionController.permissionCheck = true;

        }
    }

    public class SaveNewTravelThread implements Runnable {
        @Override
        public void run() {
            Message msg = saveNewTravelHandler.obtainMessage();
            saveNewTravelHandler.sendMessage(msg);
        }
    }

    final Handler saveNewTravelHandler = new Handler() {

        public void handleMessage(Message msg) {
            saveNewTravel();
        }

    };

    public class ModifyTravelThread implements Runnable {
        @Override
        public void run() {
            Message msg = modifyTravelHandler.obtainMessage();
            modifyTravelHandler.sendMessage(msg);
        }
    }

    final Handler modifyTravelHandler = new Handler() {

        public void handleMessage(Message msg) {
            modifyTravel();
        }

    };

    public class ShowDetailThread implements Runnable {
        @Override
        public void run() {
            Message msg = detailHandler.obtainMessage();
            detailHandler.sendMessage(msg);
        }
    }

    final Handler detailHandler = new Handler() {

        public void handleMessage(Message msg) {
            showTravelDetail(travelId);
        }

    };


}
