package adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alice.mhp.traveldiary.CreateTravelActivity;
import com.alice.mhp.traveldiary.FullscreenActivity;
import com.alice.mhp.traveldiary.R;
import dao.ListItem;

import java.util.ArrayList;

import common.Util;

public class TravelCreateRecyclerViewAdapter extends RecyclerView.Adapter<TravelCreateRecyclerViewHolder> {
    private ArrayList<ListItem> mListItems = null;
    Context mContext;

    public TravelCreateRecyclerViewAdapter(ArrayList<ListItem> itemList) {
        mListItems = itemList;
    }

    // Create View
    @Override
    public TravelCreateRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_edit_list, parent, false);
        mContext = parent.getContext();

        TravelCreateRecyclerViewHolder holder = new TravelCreateRecyclerViewHolder(v);
        return holder;
    }

    // Call Recycler View, Adapter combines the data of corresponding position
    @Override
    public void onBindViewHolder(TravelCreateRecyclerViewHolder holder, final int position) {

        Util util = new Util(mContext);

        Bitmap photo = util.decodeSampledBitmapFromResource(mListItems.get(position).getFilePath(), 500, 500);
        holder.image_photo.setImageBitmap(photo);


        holder.text_address.setText(mListItems.get(position).getAddress());

        holder.btn_del_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateTravelActivity createTravelActivity = new CreateTravelActivity();
                createTravelActivity.removeData(mContext, position);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, FullscreenActivity.class);
                intent.putExtra("photoUrl", mListItems.get(position).getFilePath());
                intent.putExtra("location", mListItems.get(position).getPhotoLocation());
                intent.putExtra("memo", mListItems.get(position).getMemo());
                mContext.startActivity(intent);
            }
        });

    }

    // Return data count
    @Override
    public int getItemCount() {
        return mListItems.size();
    }

    public ArrayList<ListItem> getList() {
        return mListItems;
    }
}
