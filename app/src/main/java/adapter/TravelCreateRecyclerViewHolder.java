package adapter;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;

import com.alice.mhp.traveldiary.R;

public class TravelCreateRecyclerViewHolder extends RecyclerView.ViewHolder {
    public ImageView image_photo;
    public Button btn_edit_location, btn_del_photo;
    public TextView text_address;
    public TravelCreateRecyclerViewHolder(View itemView) {
        super(itemView);
        image_photo = itemView.findViewById(R.id.image_photo);
        btn_del_photo = itemView.findViewById(R.id.btn_del_photo);
        text_address = itemView.findViewById(R.id.text_address);
    }
}
