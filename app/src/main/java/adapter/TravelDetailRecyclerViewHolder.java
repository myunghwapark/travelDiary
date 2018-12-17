package adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alice.mhp.traveldiary.R;

public class TravelDetailRecyclerViewHolder extends RecyclerView.ViewHolder {
    public ImageView image_photo;
    public TextView text_list_title, text_memo;
    public EditText edit_memo;
    public Button btn_add_memo, btn_add, btn_cover_image, btn_del_photo;
    public LinearLayout layout_memo_edit;
    public TravelDetailRecyclerViewHolder(View itemView) {
        super(itemView);
        image_photo = itemView.findViewById(R.id.image_photo);
        text_list_title = itemView.findViewById(R.id.text_list_title);
        text_memo = itemView.findViewById(R.id.text_memo);
        edit_memo = itemView.findViewById(R.id.edit_memo);
        btn_add_memo = itemView.findViewById(R.id.btn_add_memo);
        btn_add = itemView.findViewById(R.id.btn_add);
        btn_cover_image = itemView.findViewById(R.id.btn_cover_image);
        layout_memo_edit = itemView.findViewById(R.id.layout_memo_edit);
        btn_del_photo = itemView.findViewById(R.id.btn_del_photo);
    }
}
