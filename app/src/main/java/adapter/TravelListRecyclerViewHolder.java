package adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alice.mhp.traveldiary.R;

public class TravelListRecyclerViewHolder extends RecyclerView.ViewHolder {
    public ImageView coverPhoto;
    public TextView txtListTitle;
    public TextView textInfo;
    public TravelListRecyclerViewHolder(View itemView) {
        super(itemView);
        coverPhoto = itemView.findViewById(R.id.coverPhoto);
        txtListTitle = itemView.findViewById(R.id.txtListTitle);
        textInfo = itemView.findViewById(R.id.textInfo);
    }
}
