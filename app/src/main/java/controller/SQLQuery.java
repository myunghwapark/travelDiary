package controller;

import android.content.ContentValues;
import android.util.Log;

import dao.PhotoDao;
import dao.TravelDao;

import static controller.DatabaseController.TABLE_TRAVEL;
import static controller.DatabaseController.TABLE_TRAVEL_PHOTO;

import static controller.DatabaseController.COLUMN_TRAVEL_LOCATION;
import static controller.DatabaseController.COLUMN_TRAVEL_CREATE_DATE;
import static controller.DatabaseController.COLUMN_TRAVEL_END_DT;
import static controller.DatabaseController.COLUMN_TRAVEL_ID;
import static controller.DatabaseController.COLUMN_TRAVEL_START_DT;
import static controller.DatabaseController.COLUMN_TRAVEL_TITLE;
import static controller.DatabaseController.COLUMN_COVER_PHOTO_ID;
import static controller.DatabaseController.COLUMN_TRAVEL_UPDATE_DATE;

import static controller.DatabaseController.COLUMN_PHOTO_TRAVEL_ID;
import static controller.DatabaseController.COLUMN_PHOTO_ID;
import static controller.DatabaseController.COLUMN_PHOTO_LOCATION;
import static controller.DatabaseController.COLUMN_PHOTO_LATITUDE;
import static controller.DatabaseController.COLUMN_PHOTO_LONGITUDE;
import static controller.DatabaseController.COLUMN_PHOTO_DATETIME;
import static controller.DatabaseController.COLUMN_PHOTO_MEMO;
import static controller.DatabaseController.COLUMN_PHOTO_URL;
import static controller.DatabaseController.COLUMN_PHOTO_COVER_YN;
import static controller.DatabaseController.COLUMN_PHOTO_CREATE_DATE;
import static controller.DatabaseController.COLUMN_PHOTO_UPDATE_DATE;

public class SQLQuery {

    public ContentValues getInsertTravelQuery(TravelDao travelDao) {

        ContentValues contentValue = new ContentValues();
        contentValue.put(COLUMN_TRAVEL_ID,          travelDao.getTravelId());
        contentValue.put(COLUMN_TRAVEL_START_DT,    travelDao.getTravelStartDate());
        contentValue.put(COLUMN_TRAVEL_END_DT,      travelDao.getTravelEndDate());
        contentValue.put(COLUMN_COVER_PHOTO_ID,     travelDao.getCoverPhotoId());
        contentValue.put(COLUMN_TRAVEL_TITLE,       travelDao.getTravelTitle());
        contentValue.put(COLUMN_TRAVEL_LOCATION,    travelDao.getTravelLocation());
        contentValue.put(COLUMN_TRAVEL_CREATE_DATE, travelDao.getCreateDate());

        return contentValue;
    }

    public ContentValues getUpdateTravelQuery(TravelDao travelDao) {

        ContentValues contentValue = new ContentValues();
        contentValue.put(COLUMN_TRAVEL_START_DT,    travelDao.getTravelStartDate());
        contentValue.put(COLUMN_TRAVEL_END_DT,      travelDao.getTravelEndDate());
        contentValue.put(COLUMN_COVER_PHOTO_ID,     travelDao.getCoverPhotoId());
        contentValue.put(COLUMN_TRAVEL_TITLE,       travelDao.getTravelTitle());
        contentValue.put(COLUMN_TRAVEL_LOCATION,    travelDao.getTravelLocation());
        contentValue.put(COLUMN_TRAVEL_UPDATE_DATE, travelDao.getUpdateDate());

        return contentValue;
    }

    public String getTravelListQuery(String startNumber) {
        String query = "select A."+COLUMN_TRAVEL_ID+", A."+COLUMN_TRAVEL_LOCATION+", A."+COLUMN_TRAVEL_START_DT
                +", A."+COLUMN_TRAVEL_END_DT+", B."+COLUMN_PHOTO_URL+", A."+COLUMN_TRAVEL_TITLE
                +" from "+TABLE_TRAVEL +" A, "+TABLE_TRAVEL_PHOTO+" B where A."+COLUMN_TRAVEL_ID+" = B."+COLUMN_PHOTO_TRAVEL_ID
                +" and A."+COLUMN_COVER_PHOTO_ID+" = B."+COLUMN_PHOTO_ID+" ORDER BY A."+COLUMN_TRAVEL_ID +" DESC";
        return query;
    }

    public String getSearchTravelListQuery(String startNumber, String searchText) {
        String query = "select A."+COLUMN_TRAVEL_ID+", A."+COLUMN_TRAVEL_LOCATION+", A."+COLUMN_TRAVEL_START_DT
                +", A."+COLUMN_TRAVEL_END_DT+", B."+COLUMN_PHOTO_URL+", A."+COLUMN_TRAVEL_TITLE
                +" from "+TABLE_TRAVEL +" A, "+TABLE_TRAVEL_PHOTO+" B where A."+COLUMN_TRAVEL_ID+" = B."+COLUMN_PHOTO_TRAVEL_ID
                +" and A."+COLUMN_COVER_PHOTO_ID+" = B."+COLUMN_PHOTO_ID
                +" and (A."+COLUMN_TRAVEL_LOCATION +" like '%"+searchText+"%' or A."+COLUMN_TRAVEL_TITLE+" like '%"+searchText+"%')"
                +" ORDER BY A."+COLUMN_TRAVEL_ID +" DESC";
        Log.d("query_travel_select", query);
        return query;
    }

    public String getTravelMapQuery() {
        String query = "select A."+COLUMN_TRAVEL_ID+", A."+COLUMN_TRAVEL_LOCATION+", A."+COLUMN_TRAVEL_TITLE
                +", B."+COLUMN_PHOTO_LATITUDE+", B."+COLUMN_PHOTO_LONGITUDE+", B."+COLUMN_PHOTO_DATETIME
                +", B."+COLUMN_PHOTO_URL+", A."+COLUMN_TRAVEL_TITLE
                +" from "+TABLE_TRAVEL +" A, "+TABLE_TRAVEL_PHOTO+" B where A."+COLUMN_TRAVEL_ID+" = B."+COLUMN_PHOTO_TRAVEL_ID
                +" and A."+COLUMN_COVER_PHOTO_ID+" = B."+COLUMN_PHOTO_ID+" ORDER BY A."+COLUMN_TRAVEL_ID +" DESC";
        return query;
    }

    public String getLastTravelId() {
        String query = "select ifnull(MAX("+COLUMN_TRAVEL_ID+"), 0) from "+TABLE_TRAVEL;
        return query;
    }

    public String getCoverPhotoId(String travelId) {
        String query = "select "+COLUMN_PHOTO_ID+" from "+TABLE_TRAVEL_PHOTO+" where "+COLUMN_PHOTO_TRAVEL_ID+" = "+travelId
                +" and "+COLUMN_PHOTO_COVER_YN+" = 'Y'";
        return query;
    }

    public ContentValues getInsertTravelPhotoQuery(PhotoDao photoDao) {

        ContentValues contentValue = new ContentValues();
        contentValue.put(COLUMN_PHOTO_TRAVEL_ID,    photoDao.getTravelId());
        contentValue.put(COLUMN_PHOTO_LOCATION,     photoDao.getPhotoLocation());
        contentValue.put(COLUMN_PHOTO_LATITUDE,     photoDao.getPhotoLatitude());
        contentValue.put(COLUMN_PHOTO_LONGITUDE,    photoDao.getPhotoLongitude());
        contentValue.put(COLUMN_PHOTO_DATETIME,     photoDao.getPhotoDateTime());
        contentValue.put(COLUMN_PHOTO_MEMO,         photoDao.getPhotoMemo());
        contentValue.put(COLUMN_PHOTO_URL,          photoDao.getPhotoUrl());
        contentValue.put(COLUMN_PHOTO_COVER_YN,     photoDao.getPhotoCoverYn());
        contentValue.put(COLUMN_PHOTO_CREATE_DATE,  photoDao.getCreateDate());

        return contentValue;
    }

    public ContentValues getUpdateTravelPhotoQuery(PhotoDao photoDao) {

        ContentValues contentValue = new ContentValues();
        contentValue.put(COLUMN_PHOTO_LOCATION,     photoDao.getPhotoLocation());
        contentValue.put(COLUMN_PHOTO_LATITUDE,     photoDao.getPhotoLatitude());
        contentValue.put(COLUMN_PHOTO_LONGITUDE,    photoDao.getPhotoLongitude());
        contentValue.put(COLUMN_PHOTO_DATETIME,     photoDao.getPhotoDateTime());
        contentValue.put(COLUMN_PHOTO_MEMO,         photoDao.getPhotoMemo());
        contentValue.put(COLUMN_PHOTO_URL,          photoDao.getPhotoUrl());
        contentValue.put(COLUMN_PHOTO_COVER_YN,     photoDao.getPhotoCoverYn());
        contentValue.put(COLUMN_PHOTO_UPDATE_DATE,  photoDao.getUpdateDate());

        return contentValue;
    }

    public String getPhotoListQuery() {
        String query = "SELECT B."+COLUMN_PHOTO_TRAVEL_ID+", B."+COLUMN_PHOTO_ID
                +", B."+COLUMN_PHOTO_URL+", B."+COLUMN_PHOTO_LATITUDE+", B."+COLUMN_PHOTO_LONGITUDE
                +", B."+COLUMN_PHOTO_DATETIME+", B."+COLUMN_PHOTO_MEMO+", B."+COLUMN_PHOTO_LOCATION
                +", A."+COLUMN_TRAVEL_START_DT+", A."+COLUMN_TRAVEL_END_DT+", A."+COLUMN_TRAVEL_TITLE
                +", A."+COLUMN_COVER_PHOTO_ID+", B."+COLUMN_PHOTO_COVER_YN
                +" FROM "+TABLE_TRAVEL +" A, "+ TABLE_TRAVEL_PHOTO +" B"
                +" WHERE A."+ COLUMN_TRAVEL_ID +" = B."+ COLUMN_PHOTO_TRAVEL_ID
                +" AND A." + COLUMN_TRAVEL_ID + " = ? ORDER BY B."+COLUMN_PHOTO_COVER_YN +" ASC, B."+COLUMN_PHOTO_ID +" DESC";
        Log.d("query_travel_select", query);
        return query;
    }

    public ContentValues getUpdateTravelPhotoMemoQuery(PhotoDao photoDao) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(COLUMN_PHOTO_MEMO,         photoDao.getPhotoMemo());
        return contentValue;
    }

    public ContentValues getUpdateTravelPhotoCoverYnQuery(PhotoDao photoDao) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(COLUMN_PHOTO_COVER_YN,      photoDao.getPhotoCoverYn());
        return contentValue;
    }

    public ContentValues getUpdateTravelPhotoCoverIdQuery(PhotoDao photoDao) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(COLUMN_COVER_PHOTO_ID,      photoDao.getPhotoId());
        return contentValue;
    }

}
