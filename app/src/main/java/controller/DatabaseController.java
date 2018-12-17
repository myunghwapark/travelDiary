package controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import dao.PhotoDao;
import dao.TravelDao;

public class DatabaseController extends SQLiteOpenHelper {

    SQLQuery query;

    public static final String DATABASE_NAME = "dbTravelDiary";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_TRAVEL = "tbTravel";
    public static final String TABLE_TRAVEL_PHOTO = "tbTravelPhoto";

    public static final String COLUMN_TRAVEL_ID = "TRAVEL_ID";
    public static final String COLUMN_TRAVEL_START_DT = "TRAVEL_START_DATE";
    public static final String COLUMN_TRAVEL_END_DT = "TRAVEL_END_DATE";
    public static final String COLUMN_COVER_PHOTO_ID = "COVER_PHOTO_ID";
    public static final String COLUMN_TRAVEL_TITLE = "TRAVEL_TITLE";
    public static final String COLUMN_TRAVEL_LOCATION = "TRAVEL_LOCATION";
    public static final String COLUMN_TRAVEL_CREATE_DATE = "CREATE_DATE";
    public static final String COLUMN_TRAVEL_UPDATE_DATE = "UPDATE_DATE";

    public static final String COLUMN_PHOTO_TRAVEL_ID = "TRAVEL_ID";
    public static final String COLUMN_PHOTO_ID = "PHOTO_ID";
    public static final String COLUMN_PHOTO_LOCATION = "PHOTO_LOCATION";
    public static final String COLUMN_PHOTO_LATITUDE = "PHOTO_LATITUDE";
    public static final String COLUMN_PHOTO_LONGITUDE = "PHOTO_LONGITUDE";
    public static final String COLUMN_PHOTO_DATETIME = "PHOTO_DATETIME";
    public static final String COLUMN_PHOTO_MEMO = "PHOTO_MEMO";
    public static final String COLUMN_PHOTO_URL = "PHOTO_URL";
    public static final String COLUMN_PHOTO_COVER_YN = "PHOTO_COVER_YN";
    public static final String COLUMN_PHOTO_CREATE_DATE = "CREATE_DATE";
    public static final String COLUMN_PHOTO_UPDATE_DATE = "UPDATE_DATE";


    public DatabaseController(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        query = new SQLQuery();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "Create Table " + TABLE_TRAVEL + "(" + COLUMN_TRAVEL_ID +" Integer PRIMARY KEY, "+COLUMN_TRAVEL_START_DT + " Text, "
                +COLUMN_TRAVEL_END_DT + " Text, " +COLUMN_COVER_PHOTO_ID + " Text, " +COLUMN_TRAVEL_TITLE + " Text, "
                +COLUMN_TRAVEL_LOCATION + " Text, "+COLUMN_TRAVEL_CREATE_DATE+ " Text, " +COLUMN_TRAVEL_UPDATE_DATE + " Text)";
        db.execSQL(query);

        query = "Create Table " + TABLE_TRAVEL_PHOTO + "(" + COLUMN_PHOTO_TRAVEL_ID +" Integer, " +COLUMN_PHOTO_ID + " Integer PRIMARY KEY AUTOINCREMENT, "
                +COLUMN_PHOTO_LOCATION + " Text, "+COLUMN_PHOTO_LATITUDE + " Text, " +COLUMN_PHOTO_LONGITUDE + " Text, "
                +COLUMN_PHOTO_DATETIME + " Text, "+COLUMN_PHOTO_MEMO + " Text, "+COLUMN_PHOTO_URL + " Text, "
                +COLUMN_PHOTO_COVER_YN + " Text, "+COLUMN_PHOTO_CREATE_DATE + " Text, "+COLUMN_PHOTO_UPDATE_DATE + " Text)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // dropping the table
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_TRAVEL);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_TRAVEL_PHOTO);
        onCreate(db); // creating table by calling constructor
    }

    public int getLastTravelId() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query.getLastTravelId(),null);
        int lastId = 1;
        if(cursor != null && cursor.moveToFirst()) {
            lastId = cursor.getInt(0);
        }
        return lastId;
    }

    public String getCoverPhotoId() {
        String photoId = "";
        int travelId = (getLastTravelId()+1);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query.getCoverPhotoId(""+travelId),null);
        if(cursor != null && cursor.moveToFirst()) {
            photoId = cursor.getString(0);
        }
        return photoId;
    }


    public TravelDao insertTravel(TravelDao travelDao) {

        travelDao.setTravelId((getLastTravelId()+1));
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValue = query.getInsertTravelQuery(travelDao);
        long result = db.insert(TABLE_TRAVEL, null, contentValue);
        travelDao.setResult(result>0);
        db.close();

        return travelDao;
    }

    public TravelDao updateTravel(TravelDao travelDao) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValue = query.getUpdateTravelQuery(travelDao);
        long result = db.update(TABLE_TRAVEL, contentValue,COLUMN_TRAVEL_ID+" = ?",
                new String[] { ""+travelDao.getTravelId() });
        db.close();

        travelDao.setResult(result>0);

        return travelDao;
    }

    public Cursor getTravelList(int startNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery(query.getTravelListQuery(""+startNumber),null);
        return res;
    }

    public Cursor getSearchTravelList(int startNumber, String searchText) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery(query.getSearchTravelListQuery(""+startNumber, searchText),null);
        return res;
    }

    public Cursor getTravelMap(int startNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery(query.getTravelMapQuery(),null);
        return res;
    }

    public boolean deleteTravel (String travelId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_TRAVEL, COLUMN_TRAVEL_ID+" = ?",new String[] {travelId}) > 0;
    }

    public void insertTravelPhoto(PhotoDao photoDao) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValue = query.getInsertTravelPhotoQuery(photoDao);
        db.insert(TABLE_TRAVEL_PHOTO, null, contentValue);
        db.close();
    }

    public Cursor getPhotoList(String travelId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery(query.getPhotoListQuery(), new String[] { travelId });
        return res;
    }

    public boolean updateTravelPhoto(PhotoDao photoDao) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValue = query.getUpdateTravelPhotoQuery(photoDao);
        long result = db.update(TABLE_TRAVEL_PHOTO, contentValue,COLUMN_PHOTO_TRAVEL_ID+" = ?",
                new String[] { ""+photoDao.getPhotoId() });
        db.close();

        if(result == -1)
            return false;
        else
            return true;
    }

    public boolean deleteTravelPhoto (String travelId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_TRAVEL_PHOTO, COLUMN_PHOTO_TRAVEL_ID+" = ?",new String[] {travelId}) > 0;
    }

    public boolean deleteTravelPhotoIndividual (String photoId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_TRAVEL_PHOTO, COLUMN_PHOTO_ID+" = ?",new String[] {photoId}) > 0;
    }

    public boolean updateMemo(PhotoDao photoDao) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValue = query.getUpdateTravelPhotoMemoQuery(photoDao);
        long result = db.update(TABLE_TRAVEL_PHOTO, contentValue,COLUMN_PHOTO_ID+" = ?",
                new String[] { ""+photoDao.getPhotoId() });
        db.close();

        if(result == -1)
            return false;
        else
            return true;
    }

    public boolean updateCoverImage(PhotoDao photoDao) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValue = query.getUpdateTravelPhotoCoverYnQuery(photoDao);
        long result1 = db.update(TABLE_TRAVEL_PHOTO, contentValue,COLUMN_PHOTO_ID+" = ?",
                new String[] { ""+photoDao.getPhotoId() });

        contentValue = query.getUpdateTravelPhotoCoverIdQuery(photoDao);
        long result2 = db.update(TABLE_TRAVEL, contentValue,COLUMN_TRAVEL_ID+" = ?",
                new String[] { ""+photoDao.getTravelId() });
        db.close();

        if(result1 > 0 && result2 > 0)
            return true;
        else
            return false;
    }
}
