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
import com.alice.mhp.traveldiary.DetailTravelActivity;
import com.alice.mhp.traveldiary.ListTravelActivity;
import com.alice.mhp.traveldiary.R;

import java.util.ArrayList;

import common.Util;
import dao.ListItem;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

public class TravelListRecyclerViewAdapter extends RecyclerView.Adapter<TravelListRecyclerViewHolder> {
    private ArrayList<ListItem> mListItems = null;
    Context mContext;

    public TravelListRecyclerViewAdapter(ArrayList<ListItem> itemList) {
        mListItems = itemList;
    }

    // Create View
    @Override
    public TravelListRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_list, parent, false);
        mContext = parent.getContext();

        TravelListRecyclerViewHolder holder = new TravelListRecyclerViewHolder(v);
        return holder;
    }

    // Call Recycler View, Adapter combines the data of corresponding position
    @Override
    public void onBindViewHolder(TravelListRecyclerViewHolder holder, final int position) {

        Util util = new Util(mContext);
        Bitmap photo = util.decodeSampledBitmapFromResource(mContext.getFilesDir().toString()+mListItems.get(position).getFileName(),  500, 500);
        holder.coverPhoto.setImageBitmap(photo);

        holder.coverPhoto.setTag(mListItems.get(position).getTravelId());

        String address = mListItems.get(position).getAddress();
        String date = mListItems.get(position).getStartDate();
        holder.txtListTitle.setText(mListItems.get(position).getTravelTitle());
        holder.textInfo.setText(util.removeNull(address)+" "+util.removeNull(date));

        holder.coverPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DetailTravelActivity.class);
                intent.addFlags(FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("travelId", ""+v.getTag());
                mContext.startActivity(intent);
            }
        });
    }

    // Return data count
    @Override
    public int getItemCount() {
        return mListItems.size();
    }
}
