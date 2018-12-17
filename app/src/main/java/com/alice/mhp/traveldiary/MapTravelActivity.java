package com.alice.mhp.traveldiary;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import common.Util;
import controller.DatabaseController;
import controller.PermissionController;
import dao.MarkerItem;

import static android.content.ContentValues.TAG;


public class MapTravelActivity extends CommonActivity implements OnMapReadyCallback {

    ImageButton btn_back, btn_list, btn_edit, btn_info;
    TextView text_map_title;
    DatabaseController databaseController;
    ArrayList<MarkerOptions> mapArray;
    MapFragment mapFragment;
    PermissionController permissionController;
    GoogleMap mMap;
    int DEFAULT_ZOOM = 12;
    Util util;
    String prevPage, travelId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_travel);

        getSupportActionBar().hide();

        ArrayList<String> permissionList = new ArrayList<String>();
        permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissionList.add(Manifest.permission.INTERNET);

        permissionController = new PermissionController(this, permissionList);
        util = new Util(this);

        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(btnMenuClick);
        btn_list = findViewById(R.id.btn_list);
        btn_list.setOnClickListener(btnMenuClick);
        btn_edit = findViewById(R.id.btn_edit);
        btn_edit.setOnClickListener(btnMenuClick);
        btn_info = findViewById(R.id.btn_info);
        btn_info.setOnClickListener(btnMenuClick);
        text_map_title = findViewById(R.id.text_map_title);

        databaseController = new DatabaseController(this);
        prevPage = getIntent().getExtras().getString("prevPage");
        travelId = getIntent().getExtras().getString("travelId");


        FragmentManager fragmentManager = getFragmentManager();
        mapFragment = (MapFragment)fragmentManager
                .findFragmentById(R.id.travel_map);

        if(permissionController.permissionCheck) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {

        mMap = map;
        mMap.setOnMarkerClickListener(onMarkerClick);

        mapArray = new ArrayList<>();

        if(prevPage.equals("list")) {
            Cursor mapCursor = databaseController.getTravelMap(0);
            if(mapCursor != null && mapCursor.moveToFirst()) {
                do {
                    MarkerItem markerItem = new MarkerItem();
                    markerItem.setTravelId(mapCursor.getString(0));
                    markerItem.setLatitude(mapCursor.getDouble(3));
                    markerItem.setLongitude(mapCursor.getDouble(4));
                    markerItem.setPhotoUrl(mapCursor.getString(6));
                    markerItem.setPhotoLocation(mapCursor.getString(1));
                    markerItem.setTravelTitle(mapCursor.getString(7));
                    addMarker(markerItem, false);
                } while (mapCursor.moveToNext());
            }
        }
        else if(prevPage.equals("detail")) {
            String title = "";
            Cursor detailCursor = databaseController.getPhotoList(travelId);
            if(detailCursor != null && detailCursor.moveToFirst()) {
                do {
                    MarkerItem markerItem = new MarkerItem();
                    markerItem.setTravelId(detailCursor.getString(0));
                    markerItem.setLatitude(detailCursor.getDouble(3));
                    markerItem.setLongitude(detailCursor.getDouble(4));
                    markerItem.setPhotoUrl(detailCursor.getString(2));
                    markerItem.setPhotoLocation(detailCursor.getString(7));

                    title = detailCursor.getString(10);
                    addMarker(markerItem, false);
                } while (detailCursor.moveToNext());
            }
            text_map_title.setText(title);
        }

        mMap.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM));


    }

    private Marker addMarker(MarkerItem markerItem, boolean isSelectedMarker) {

        View marker_layout = LayoutInflater.from(this).inflate(R.layout.marker_layout, null);
        ImageView image_photo = marker_layout.findViewById(R.id.image_photo);


        LatLng position = new LatLng(markerItem.getLatitude(), markerItem.getLongitude());
        String photoUrl = markerItem.getPhotoUrl();

        Bitmap photo = util.decodeSampledBitmapFromResource(getFilesDir().toString()+photoUrl, 500, 500);
        if(photo != null) {
            image_photo.setImageBitmap(photo);
        }



        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title(markerItem.getPhotoLocation());
        markerOptions.position(position);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, marker_layout)));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(position));

        Marker marker = mMap.addMarker(markerOptions);
        marker.setTag(markerItem.getTravelId());

        return marker;

    }

    private Bitmap createDrawableFromView(Context context, View view) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    public GoogleMap.OnMarkerClickListener onMarkerClick = new GoogleMap.OnMarkerClickListener() {

        @Override
        public boolean onMarkerClick(Marker marker) {

            Intent intent = new Intent(MapTravelActivity.this, DetailTravelActivity.class);
            intent.putExtra("travelId", ""+marker.getTag());
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);

            return false;
        }


    };




    public View.OnClickListener btnMenuClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Intent intent;

            switch (view.getId()) {

                case R.id.btn_back:
                    finish();
                    break;

                case R.id.btn_list:

                    if(prevPage.equals("list")) {
                        intent = new Intent(MapTravelActivity.this, ListTravelActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                    else {
                        intent = new Intent(MapTravelActivity.this, DetailTravelActivity.class);
                        intent.putExtra("travelId", travelId);
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                    }
                    break;

                case R.id.btn_edit:
                    intent = new Intent(MapTravelActivity.this, CreateTravelActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    break;

                case R.id.btn_info:
                    intent = new Intent(MapTravelActivity.this, AboutMeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
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
                mapFragment.getMapAsync(this);
            }
            else {
                permissionController.permissionCheck = false;
            }

        }
        else {
            permissionController.permissionCheck = true;
            mapFragment.getMapAsync(this);

        }


    }

}
