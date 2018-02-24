package me.fdawei.picker.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.fdawei.picker.R;
import me.fdawei.picker.entity.Photo;
import me.fdawei.picker.util.DimenUtils;
import me.fdawei.picker.util.ImageLoader;

import static android.support.v7.widget.helper.ItemTouchHelper.DOWN;
import static android.support.v7.widget.helper.ItemTouchHelper.LEFT;
import static android.support.v7.widget.helper.ItemTouchHelper.RIGHT;
import static android.support.v7.widget.helper.ItemTouchHelper.UP;

/**
 * Created by david on 2018/2/20.
 */

public class PhotoPreviewListAdapter extends RecyclerView.Adapter {

  private Context context;
  private List<Item> itemList = new ArrayList<>();
  private OnItemFocusChangedListener itemFocusChangedListener;
  private OnPhotoOrderChangedListener photoOrderChangedListener;
  private View.OnClickListener itemClickListener;
  private RecyclerView recyclerView;
  private Item currentFocusItem;

  public PhotoPreviewListAdapter(Context context, List<Photo> photoList) {
    this.context = context;
    if (photoList != null) {
      for (int i = 0; i < photoList.size(); i++) {
        Item item = new Item(photoList.get(i));
        itemList.add(item);
      }
    }

    itemClickListener = v -> {
      Integer position = (Integer) v.getTag();
      if (position != null) {
        onItemClicked(position);
      }
    };

  }

  public void attachToRecyclerView(RecyclerView recyclerView) {
    this.recyclerView = recyclerView;
    recyclerView.setAdapter(this);
    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemDragCallback());
    itemTouchHelper.attachToRecyclerView(recyclerView);
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView =
        LayoutInflater.from(context).inflate(R.layout.item_preview_photo, parent, false);
    return new PreviewPhotoViewHolder(itemView);
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    PreviewPhotoViewHolder previewPhotoViewHolder = (PreviewPhotoViewHolder) holder;
    Item item = itemList.get(position);
    previewPhotoViewHolder.bind(item.photo);
    previewPhotoViewHolder.setFocus(item.isFocus);
    previewPhotoViewHolder.setSelection(item.isSelected);
    previewPhotoViewHolder.itemView.setTag(position);
    previewPhotoViewHolder.itemView.setOnClickListener(itemClickListener);
    updateMargin(position, previewPhotoViewHolder);
  }

  public void updateMargin(int position, PreviewPhotoViewHolder holder) {
    if (position == 0) {//第一项
      RecyclerView.LayoutParams layoutParams =
          (RecyclerView.LayoutParams) holder.getItemView().getLayoutParams();
      layoutParams.leftMargin = (int) DimenUtils.dp2px(context, 19);
      layoutParams.rightMargin = (int) DimenUtils.dp2px(context, 10);
    } else if (position == itemList.size() - 1) {//最后一项
      RecyclerView.LayoutParams layoutParams =
          (RecyclerView.LayoutParams) holder.getItemView().getLayoutParams();
      layoutParams.rightMargin = (int) DimenUtils.dp2px(context, 19);
      layoutParams.leftMargin = 0;
    } else {
      RecyclerView.LayoutParams layoutParams =
          (RecyclerView.LayoutParams) holder.getItemView().getLayoutParams();
      layoutParams.rightMargin = (int) DimenUtils.dp2px(context, 10);
      layoutParams.leftMargin = 0;
    }
  }

  @Override public int getItemCount() {
    return itemList.size();
  }

  public void setItemFocusChangedListener(OnItemFocusChangedListener itemFocusChangedListener) {
    this.itemFocusChangedListener = itemFocusChangedListener;
  }

  public void setPhotoOrderChangedListener(OnPhotoOrderChangedListener photoOrderChangedListener) {
    this.photoOrderChangedListener = photoOrderChangedListener;
  }

  public void setItemSelection(int position, boolean isSelected) {
    if (position >= 0 && position < itemList.size()) {
      Item item = itemList.get(position);
      item.isSelected = isSelected;
    }
    throw new IndexOutOfBoundsException(
        String.format("item size = %d, position = %d", itemList.size(), position));
  }

  public boolean getItemSelection(int position) {
    if (position >= 0 && position < itemList.size()) {
      Item item = itemList.get(position);
      return item.isSelected;
    }
    return false;
  }

  public int findPhotoPosition(Photo photo) {
    int position = -1;
    for(int i = 0; i < itemList.size(); i++) {
      Item item = itemList.get(i);
      if(item.photo.equals(photo)) {
        position = i;
        break;
      }
    }
    return position;
  }

  public void setCurrentFocusPhoto(Photo photo) {
    int position = findPhotoPosition(photo);
    if(position >= 0 && position < itemList.size()) {
      onItemClicked(position);
    } else {
      clearFocus();
    }
  }

  private void clearFocus() {
    if (currentFocusItem != null) {
      currentFocusItem.isFocus = false;
      int lastFocusPosition = itemList.indexOf(currentFocusItem);
      PreviewPhotoViewHolder lastHolder =
          (PreviewPhotoViewHolder) recyclerView.findViewHolderForAdapterPosition(
              lastFocusPosition);
      if (lastHolder != null) {
        lastHolder.setFocus(false);
      } else {
        notifyItemChanged(lastFocusPosition);
      }
      currentFocusItem = null;
    }
  }

  public void onItemClicked(int position) {
    Item item = itemList.get(position);
    if (!item.isFocus) {
      clearFocus();

      item.isFocus = true;
      currentFocusItem = item;
      PreviewPhotoViewHolder currentHolder =
          (PreviewPhotoViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
      if (currentHolder != null) {
        currentHolder.setFocus(true);
      } else {
        recyclerView.smoothScrollToPosition(position);
        notifyItemChanged(position);
      }

      if (itemFocusChangedListener != null) {
        itemFocusChangedListener.onItemFocus(position, item.photo);
      }
    }
  }

  class Item {
    private boolean isSelected = true;
    private boolean isFocus = false;
    private Photo photo;

    public Item(Photo photo) {
      this.photo = photo;
    }
  }

  class PreviewPhotoViewHolder extends RecyclerView.ViewHolder {

    private ImageView ivPhoto;
    private View whiteCover;
    private View blueFrame;

    public PreviewPhotoViewHolder(View itemView) {
      super(itemView);

      ivPhoto = itemView.findViewById(R.id.iv_photo);
      whiteCover = itemView.findViewById(R.id.white_cover);
      blueFrame = itemView.findViewById(R.id.blue_frame);
    }

    public void bind(Photo photo) {
      ImageLoader.load(ivPhoto, photo.getPath());
    }

    public void setFocus(boolean isFocus) {
      blueFrame.setVisibility(isFocus ? View.VISIBLE : View.GONE);
    }

    public void setSelection(boolean isSelected) {
      whiteCover.setVisibility(isSelected ? View.GONE : View.VISIBLE);
    }

    public View getItemView() {
      return itemView;
    }
  }

  class ItemDragCallback extends ItemTouchHelper.Callback {

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
      return makeMovementFlags(UP | DOWN | LEFT | RIGHT, 0);
    }

    @Override public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
        RecyclerView.ViewHolder target) {
      int from = viewHolder.getAdapterPosition();
      int to = target.getAdapterPosition();
      Collections.swap(itemList, from, to);
      PreviewPhotoViewHolder fromHolder = (PreviewPhotoViewHolder) viewHolder;
      PreviewPhotoViewHolder toHolder = (PreviewPhotoViewHolder) target;
      updateMargin(to, fromHolder);
      updateMargin(from, toHolder);
      fromHolder.itemView.setTag(to);
      toHolder.itemView.setTag(from);
      notifyItemMoved(from, to);
      return true;
    }

    @Override public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

    }

    @Override public boolean isLongPressDragEnabled() {
      return true;
    }

    @Override public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
      super.onSelectedChanged(viewHolder, actionState);
    }

    @Override public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
      super.clearView(recyclerView, viewHolder);
      if(photoOrderChangedListener != null){
        List<Photo> photoList = new ArrayList<>();
        for(Item item : itemList) {
          photoList.add(item.photo);
        }
        photoOrderChangedListener.onPhotoOrderChange(photoList);
      }
    }
  }

  public interface OnItemFocusChangedListener {
    void onItemFocus(int position, Photo photo);
  }

  public interface OnPhotoOrderChangedListener {
    void onPhotoOrderChange(List<Photo> data);
  }
}
