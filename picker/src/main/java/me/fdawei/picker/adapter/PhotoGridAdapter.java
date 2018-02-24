package me.fdawei.picker.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import java.util.ArrayList;
import java.util.List;
import me.fdawei.picker.R;
import me.fdawei.picker.entity.Photo;
import me.fdawei.picker.util.DimenUtils;
import me.fdawei.picker.util.ImageLoader;
import me.fdawei.picker.util.SelectedFlagBitmapUtils;

/**
 * Created by david on 2018/2/18.
 */

public class PhotoGridAdapter extends RecyclerView.Adapter {

  private List<Item> photoItemList = new ArrayList<>();
  private Context context;
  private int maxSelectedCount;
  private List<Item> selectedItemList = new ArrayList<>();
  private View.OnClickListener selectionStatusClickListener;
  private View.OnClickListener itemClickListener;
  private OnItemSelectionChangedListener onItemSelectionChangedListener;
  private OnItemClickListener onItemClickListener;
  private RecyclerView recyclerView;

  public PhotoGridAdapter(Context context, int maxSelectedCount) {
    this.context = context;
    this.maxSelectedCount = maxSelectedCount;

    selectionStatusClickListener = v -> {
      Integer position = (Integer) v.getTag();
      if (position != null) {
        onItemSelectionStatusClick(position);
      }
    };

    itemClickListener = v -> {
      Integer position = (Integer) v.getTag();
      if (position != null &&  onItemClickListener != null) {
        onItemClickListener.onItemClick(position);
      }
    };
  }

  public void attachToRecyclerView(RecyclerView recyclerView) {
    this.recyclerView = recyclerView;
    recyclerView.setAdapter(this);
  }

  public void setData(List<Photo> photoList) {
    photoItemList.clear();
    selectedItemList.clear();
    if (photoList != null) {
      for (Photo photo : photoList) {
        Item item = new Item(photo);
        photoItemList.add(item);
      }
    }
    notifyDataSetChanged();
  }

  public void setOnItemSelectionChangedListener(
      OnItemSelectionChangedListener onItemSelectionChangedListener) {
    this.onItemSelectionChangedListener = onItemSelectionChangedListener;
  }

  public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
    this.onItemClickListener = onItemClickListener;
  }

  public List<Photo> getSelectedPhotoList() {
    List<Photo> photoList = new ArrayList<>();
    for(Item item : selectedItemList) {
      photoList.add(item.photo);
    }
    return photoList;
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false);
    return new PhotoViewHolder(itemView);
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    PhotoViewHolder photoViewHolder = (PhotoViewHolder) holder;
    Item item = photoItemList.get(position);
    photoViewHolder.bind(item.photo);
    photoViewHolder.setSelectionStatusClickListener(position, selectionStatusClickListener);
    photoViewHolder.setItemClickListener(position, itemClickListener);
    if (item.isSelected) {
      int selectedindex = selectedItemList.indexOf(item) + 1;
      photoViewHolder.setSelected(selectedindex);
    } else {
      photoViewHolder.setUnselected();
    }
  }

  @Override public int getItemCount() {
    return photoItemList.size();
  }

  private void onItemSelectionStatusClick(int position) {
    Item item = photoItemList.get(position);
    if (item.isSelected) {
      item.isSelected = false;
      selectedItemList.remove(item);

      PhotoViewHolder nowItemHolder = (PhotoViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
      if(nowItemHolder != null) {
        nowItemHolder.setUnselected();
      }

      //更新其他已选中项目
      for(int i = 0; i < selectedItemList.size(); i++) {
        Item selectedItem = selectedItemList.get(i);
        int adapterPosition = photoItemList.indexOf(selectedItem);
        PhotoViewHolder holder = (PhotoViewHolder) recyclerView.findViewHolderForAdapterPosition(adapterPosition);
        if(holder != null) {
          int selectedPosition = i + 1;
          holder.setSelected(selectedPosition);
        } else {
          notifyItemChanged(adapterPosition);
        }
      }
    } else {
      if (selectedItemList.size() >= maxSelectedCount) {
        return;
      }
      item.isSelected = true;
      selectedItemList.add(item);

      PhotoViewHolder holder = (PhotoViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
      if(holder != null) {
        int selectedPosition = selectedItemList.size();
        holder.setSelected(selectedPosition);
      }
    }

    if (onItemSelectionChangedListener != null) {
      onItemSelectionChangedListener.onItemSelectionChanged(selectedItemList.size());
    }
  }

  class Item {
    private boolean isSelected = false;
    private Photo photo;

    public Item(Photo photo) {
      this.photo = photo;
    }
  }

  class PhotoViewHolder extends RecyclerView.ViewHolder {

    private ImageView ivPhoto;
    private ImageView ivSelectionStatus;

    public PhotoViewHolder(View itemView) {
      super(itemView);
      ivPhoto = itemView.findViewById(R.id.iv_photo);
      ivSelectionStatus = itemView.findViewById(R.id.iv_selection);
    }

    public void bind(Photo photo) {
      ImageLoader.load(ivPhoto, photo.getPath());
    }

    public void setSelected(int selectedIndex) {
      int size = (int) DimenUtils.dp2px(context, 18);
      Bitmap flagBmp = SelectedFlagBitmapUtils.createFlagBitmap("" + selectedIndex, size, size);
      ivSelectionStatus.setImageBitmap(flagBmp);
    }

    public void setUnselected() {
      ivSelectionStatus.setImageResource(R.drawable.ic_selection_status);
    }

    public void setSelectionStatusClickListener(int position, View.OnClickListener listener) {
      ivSelectionStatus.setTag(position);
      ivSelectionStatus.setOnClickListener(listener);
    }

    public void setItemClickListener(int position, View.OnClickListener listener) {
      itemView.setTag(position);
      itemView.setOnClickListener(listener);
    }
  }

  public interface OnItemSelectionChangedListener {
    void onItemSelectionChanged(int selectedCount);
  }

  public interface OnItemClickListener {
    void onItemClick(int position);
  }
}
