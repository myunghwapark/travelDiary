package common;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.view.Display;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.alice.mhp.traveldiary.CommonActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Util {

    Context context;
    InputMethodManager imManager;

    public Util(Context cont) {
        context = cont;
        imManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    public void alert(String title, String message, String btnText) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        alertDialogBuilder.setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(btnText,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });


        alertDialogBuilder.show();

    }


    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(String filePath, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    public String getLocation (String longitude, String latitude) {

        String address = "";
        final Geocoder geocoder = new Geocoder(context);
        List<Address> list = null;
        try {
            double d1 = Double.parseDouble(longitude);
            double d2 = Double.parseDouble(latitude);

            list = geocoder.getFromLocation(
                    d1, // longitude
                    d2, // latitude
                    1); // maximum result count
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (list != null) {
            if (list.size()!=0) {
                address = list.get(0).getLocality();
            }
        }

        return address;

    }

    public static String getTodayWithTime(){

        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat ( "yyyyMMddHHmmss", Locale.getDefault() );
        Date currentTime = new Date();
        String TODAY = mSimpleDateFormat.format ( currentTime );
        return TODAY;
    }

    //Save image file
    public boolean SaveBitmapToFileCache(String fileName, Bitmap bitmap){
        final String PATH = context.getFilesDir().toString();

        File fileCacheItem = new File(PATH);
        OutputStream out = null;

        try {

            fileCacheItem.createNewFile();
            out = new FileOutputStream(PATH+fileName);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            return true;
        }
        catch (OutOfMemoryError oe) {
            oe.printStackTrace();
            return false;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
        finally{

            try{
                if(out != null){
                    out.close();
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public Bitmap safeDecodeBitmapFile(String strFilePath) {
        Bitmap bitmap = null;

        try {
            File file = new File(strFilePath);

            if (file.exists() == false)
            {
                return null;
            }


            // Horizontal, Vertical Maximum Size (If larger images come in than the maximum size, reduce the size.)
            final int IMAGE_MAX_SIZE    = 700000;

            BitmapFactory.Options bfo   = new BitmapFactory.Options();
            //bfo.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bfo.inJustDecodeBounds      = true;
            ArrayList<Integer> screenSize = getScreenSize((CommonActivity)context);
            bfo.inSampleSize = calculateInSampleSize(bfo, 500, 210);


            BitmapFactory.decodeFile(strFilePath, bfo);


            if(bfo.outHeight * bfo.outWidth >= IMAGE_MAX_SIZE * IMAGE_MAX_SIZE) {

                bfo.inSampleSize = (int)Math.pow(2, (int)Math.round(Math.log(IMAGE_MAX_SIZE
                        / (double) Math.max(bfo.outHeight, bfo.outWidth)) / Math.log(0.5)));
            }


            bfo.inJustDecodeBounds = false;
            bfo.inPurgeable = true;
            bfo.inDither = true;


            bitmap = BitmapFactory.decodeFile(strFilePath, bfo);


        }
        catch (Exception e) {
            e.printStackTrace();
        }
        catch (OutOfMemoryError oe) {
            Toast.makeText(context, "Out of memory exception", Toast.LENGTH_SHORT).show();
            oe.printStackTrace();
        }

        return bitmap;
    }



    // Delete image file
    public void delImage(String ImageNm){
        final String PATH = context.getFilesDir().toString();
        File file = new File(PATH + "/" + ImageNm);
        file.delete();
    }

    public ArrayList<Integer> getScreenSize(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Integer width = size.x;
        Integer height = size.y;

        ArrayList<Integer> screenSize = new ArrayList<>();
        screenSize.add(width);
        screenSize.add(height);

        return screenSize;
    }

    public void hideKeyboard(IBinder windowToken) {

        // hide keyboard
        imManager.hideSoftInputFromWindow(windowToken, 0);
    }

    public String removeNull(String str) {
        if(str == null) {
            str = "";
        }
        return str;
    }

}
