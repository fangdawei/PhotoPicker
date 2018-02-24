package me.fdawei.picker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import me.fdawei.picker.R;
import me.fdawei.picker.entity.PhotoDir;
import me.fdawei.picker.util.ImageLoader;

/**
 * Created by david on 2018/2/18.
 */

public class PhotoDirAdapter extends RecyclerView.Adapter implements View.OnClickListener {

  private List<PhotoDir> photoDirList = new ArrayList<>();
  private Context context;
  private OnItemClickListener onItemClickListener;

  public PhotoDirAdapter(Context context, List<PhotoDir> data) {
    this.context = context;
    if (data != null) {
      photoDirList.addAll(data);
    }
  }

  public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
    this.onItemClickListener = onItemClickListener;
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(context).inflate(R.layout.item_photo_dir, parent, false);
    return new PhotoDirViewHolder(itemView);
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    PhotoDirViewHolder photoDirViewHolder = (PhotoDirViewHolder) holder;
    PhotoDir photoDir = photoDirList.get(position);
    photoDirViewHolder.bind(photoDir);
    photoDirViewHolder.setClickListener(position, this);
    photoDirViewHolder.setDividerVisiable(position != photoDirList.size() - 1);
  }

  @Override public int getItemCount() {
    return photoDirList.size();
  }

  @Override public void onClick(View v) {
    Integer position = (Integer) v.getTag();
    if (position != null && onItemClickListener != null) {
      PhotoDir photoDir = photoDirList.get(position);
      onItemClickListener.onItemClick(photoDir);
    }
  }

  public interface OnItemClickListener {
    void onItemClick(PhotoDir photoDir);
  }

  class PhotoDirViewHolder extends RecyclerView.ViewHolder {

    private ImageView ivPreview;
    private TextView tvName;
    private TextView tvCount;
    private View bottomDivider;
    private View rootView;

    public PhotoDirViewHolder(View itemView) {
      super(itemView);
      ivPreview = itemView.findViewById(R.id.iv_preview);
      tvName = itemView.findViewById(R.id.tv_dir_name);
      tvCount = itemView.findViewById(R.id.tv_dir_count);
      bottomDivider = itemView.findViewById(R.id.divider);
      rootView = itemView.findViewById(R.id.rl_item_root);
    }

    public void bind(PhotoDir photoDir) {
      ImageLoader.load(ivPreview, photoDir.getPreviewPhoto());
      tvName.setText(photoDir.getName());
      tvCount.setText("" + photoDir.getPhotoCount());
    }

    public void setClickListener(int position, View.OnClickListener listener) {
      rootView.setTag(position);
      rootView.setOnClickListener(listener);
    }

    public void setDividerVisiable(boolean visiable) {
      if (visiable) {
        bottomDivider.setVisibility(View.VISIBLE);
      } else {
        bottomDivider.setVisibility(View.GONE);
      }
    }
  }
}
