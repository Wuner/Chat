package heath.com.chat.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.Collections;
import java.util.List;

import heath.com.chat.R;


public class FriendAdapter extends RecyclerView.Adapter<FriendHolder> {

    private List<NimUserInfo> listdata;
    private Activity context;
    private LayoutInflater mInflater;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public FriendAdapter(Activity context, List<NimUserInfo> listdata) {
        this.context = context;
        this.listdata = listdata;
        mInflater = LayoutInflater.from(context);
    }


    @NonNull
    @Override
    public FriendHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View convertView = mInflater.inflate(R.layout.item_friend, viewGroup, false);
        FriendHolder viewHolder = new FriendHolder(convertView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final FriendHolder holder, int position) {
//        if (listdata.get(position).getUserInfo().getIcon() != null && !listdata.get(position).getUserInfo().getIcon().equals("null")) {
//            ImageUitl imageUitl = new ImageUitl(BaseActivity.cache);
//            imageUitl.asyncloadImage(holder.mIvHeadPhoto, Common.HTTP_ADDRESS + Common.USER_FOLDER_PATH + "/" + listdata.get(position).getUserInfo().getIcon());
//        }
        if (listdata.get(position).getName() != null) {
            holder.mTvNickname.setText(listdata.get(position).getName());
        }
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int layoutPosition = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.itemView, layoutPosition);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public void setData(int position, List<NimUserInfo> list) {
        Collections.reverse(list);
        for (NimUserInfo friendBean : list) {
            listdata.add(position, friendBean);
            notifyItemInserted(position);
        }
    }

}

class FriendHolder extends RecyclerView.ViewHolder {
    ImageView mIvHeadPhoto;
    TextView mTvNickname;

    FriendHolder(@NonNull View itemView) {
        super(itemView);
        mIvHeadPhoto = itemView.findViewById(R.id.iv_head_photo);
        mTvNickname = itemView.findViewById(R.id.tv_nickname);
    }
}
