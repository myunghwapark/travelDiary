package adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.alice.mhp.traveldiary.CommonActivity;
import com.alice.mhp.traveldiary.DetailTravelActivity;
import com.alice.mhp.traveldiary.FullscreenActivity;
import com.alice.mhp.traveldiary.ListTravelActivity;
import com.alice.mhp.traveldiary.R;

import java.util.ArrayList;

import common.Util;
import controller.DatabaseController;
import dao.ListItem;
import dao.PhotoDao;

public class TravelDetailRecyclerViewAdapter extends RecyclerView.Adapter<TravelDetailRecyclerViewHolder> {
    private ArrayList<ListItem> mListItems = null;
    Context mContext;
    DatabaseController databaseController;

    public TravelDetailRecyclerViewAdapter(ArrayList<ListItem> itemList) {
        mListItems = itemList;
    }

    // Create View
    @Override
    public TravelDetailRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_detail, parent, false);
        mContext = parent.getContext();

        TravelDetailRecyclerViewHolder holder = new TravelDetailRecyclerViewHolder(v);
        return holder;
    }

    // Call Recycler View, Adapter combines the data of corresponding position
    @Override
    public void onBindViewHolder(final TravelDetailRecyclerViewHolder holder, final int position) {

        final Util util = new Util(mContext);
        Bitmap photo = util.decodeSampledBitmapFromResource(mContext.getFilesDir().toString()+mListItems.get(position).getFileName(), 500, 500);
        holder.image_photo.setImageBitmap(photo);


        String location = mListItems.get(position).getPhotoLocation();
        String date = mListItems.get(position).getDateTime();
        holder.text_list_title.setText(util.removeNull(location)+" "+util.removeNull(date));
        if(mListItems.get(position).getMemo() == null || mListItems.get(position).getMemo().equals("")) {
            holder.text_memo.setVisibility(View.GONE);
        }
        else {
            holder.text_memo.setText(mListItems.get(position).getMemo());
        }

        if(mListItems.get(position).getMemo() != null && !mListItems.get(position).getMemo().equals("")) {
            holder.btn_add.setText(mContext.getResources().getString(R.string.btn_edit));
            holder.btn_add_memo.setText(mContext.getResources().getString(R.string.btn_edit_memo));
        }
        holder.image_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, FullscreenActivity.class);
                intent.putExtra("photoUrl", mContext.getFilesDir().toString()+mListItems.get(position).getFileName());
                intent.putExtra("location", mListItems.get(position).getPhotoLocation());
                intent.putExtra("memo", mListItems.get(position).getMemo());
                mContext.startActivity(intent);
            }
        });

        holder.btn_add_memo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.layout_memo_edit.setVisibility(View.VISIBLE);
                holder.text_memo.setVisibility(View.GONE);
                holder.btn_add_memo.setVisibility(View.GONE);
            }
        });
        holder.btn_add_memo.setImeOptions(EditorInfo.IME_ACTION_DONE);

        holder.btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // hide keyboard
                InputMethodManager imManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imManager.hideSoftInputFromWindow(holder.layout_memo_edit.getWindowToken(), 0);

                holder.layout_memo_edit.setVisibility(View.GONE);
                holder.text_memo.setVisibility(View.VISIBLE);
                holder.btn_add_memo.setVisibility(View.VISIBLE);

                holder.text_memo.setText(holder.edit_memo.getText());

                holder.btn_add.setText(mContext.getResources().getString(R.string.btn_edit));
                holder.btn_add_memo.setText(mContext.getResources().getString(R.string.btn_edit_memo));

                PhotoDao photoDao = new PhotoDao();
                photoDao.setPhotoMemo(holder.text_memo.getText()+"");
                int photoId = Integer.parseInt(mListItems.get(position).getPhotoId()+"");
                photoDao.setPhotoId(photoId);
                databaseController = new DatabaseController(mContext);
                databaseController.updateMemo(photoDao);
            }
        });

        holder.btn_cover_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PhotoDao photoDao = new PhotoDao();
                int travelId = Integer.parseInt(mListItems.get(position).getTravelId()+"");
                int photoId = Integer.parseInt(mListItems.get(position).getPhotoId()+"");
                photoDao.setTravelId(travelId);
                photoDao.setPhotoId(photoId);
                databaseController = new DatabaseController(mContext);
                boolean result = databaseController.updateCoverImage(photoDao);
                if(result) {
                    util.alert("Notice", "Changed Successfully.", "OK");
                }
            }
        });

    }

    // Return data count
    @Override
    public int getItemCount() {
        return mListItems.size();
    }
}
