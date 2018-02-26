package me.fdawei.picker.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import me.fdawei.picker.PhotoPicker;
import me.fdawei.picker.R;
import me.fdawei.picker.adapter.PhotoPagerAdapter;
import me.fdawei.picker.adapter.PhotoPreviewListAdapter;
import me.fdawei.picker.entity.Photo;
import me.fdawei.picker.util.DimenUtils;
import me.fdawei.picker.util.SelectedFlagBitmapUtils;

/**
 * Created by david on 2018/2/18.
 */

public class PhotoPreviewActivity extends AppCompatActivity
    implements PhotoPreviewListAdapter.OnItemFocusChangedListener,
    PhotoPagerAdapter.OnPageChangedListener, PhotoPreviewListAdapter.OnPhotoOrderChangedListener,
    PhotoPagerAdapter.OnPageClickListener {

  private static final String ARG_SELETED_PHOTOS = "selected_photos";
  private static final String ARG_MAX_PHOTO_COUNT = "max_count";
  private static final String ARG_ALL_PHOTOS = "all_photos";
  private static final String ARG_CURRENT_PAGE = "current_page";

  private PhotoPreviewListAdapter photoPreviewListAdapter;
  private PhotoPagerAdapter photoPagerAdapter;
  private RecyclerView rvPreviewPhotoList;
  private ViewPager vpPhotoPager;
  private ImageView ivRightIcon;
  private View topBox;
  private View bottomBox;
  private List<Photo> selectedPhotoList;
  private List<Photo> allPhotoList;
  private int maxPhotoCount;
  private TextView tvComplete;
  private boolean isSynchro;
  private Photo currentPhoto;
  boolean isFullScreen = false;

  /**
   * 已选择照片列表预览
   */
  public static void startForResult(Activity activity, int requestCode,
      ArrayList<Photo> selectedPhotos, int max) {
    Intent intent = new Intent(activity, PhotoPreviewActivity.class);
    intent.putParcelableArrayListExtra(ARG_SELETED_PHOTOS, selectedPhotos);
    intent.putExtra(ARG_MAX_PHOTO_COUNT, max);
    activity.startActivityForResult(intent, requestCode);
  }

  /**
   * 所有照片预览
   */
  public static void startForResult(Activity activity, int requestCode,
      ArrayList<Photo> selectedPhotos, int max, ArrayList<Photo> allPhotos, int currentPage) {
    Intent intent = new Intent(activity, PhotoPreviewActivity.class);
    intent.putParcelableArrayListExtra(ARG_SELETED_PHOTOS, selectedPhotos);
    intent.putExtra(ARG_MAX_PHOTO_COUNT, max);
    intent.putExtra(ARG_ALL_PHOTOS, allPhotos);
    intent.putExtra(ARG_CURRENT_PAGE, currentPage);
    activity.startActivityForResult(intent, requestCode);
  }

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_preview);

    selectedPhotoList = getIntent().getParcelableArrayListExtra(ARG_SELETED_PHOTOS);
    maxPhotoCount = getIntent().getIntExtra(ARG_MAX_PHOTO_COUNT, 9);
    allPhotoList = getIntent().getParcelableArrayListExtra(ARG_ALL_PHOTOS);
    if (allPhotoList == null) {
      isSynchro = true;
      allPhotoList = selectedPhotoList;
      currentPhoto = selectedPhotoList.get(0);
    } else {
      isSynchro = false;
      int currentPage = getIntent().getIntExtra(ARG_CURRENT_PAGE, 0);
      currentPhoto = allPhotoList.get(currentPage);
    }

    Toolbar toolbar = findViewById(R.id.ic_toolbar);
    TextView tvTitle = toolbar.findViewById(R.id.toolbar_title);
    tvTitle.setVisibility(View.GONE);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayShowTitleEnabled(false);
    toolbar.setNavigationOnClickListener(v -> onBackPressed());
    ivRightIcon = toolbar.findViewById(R.id.iv_right_icon);
    ivRightIcon.setOnClickListener(v -> {
      onRightIconClick();
    });

    rvPreviewPhotoList = findViewById(R.id.rv_photo_list);
    rvPreviewPhotoList.setLayoutManager(
        new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    photoPreviewListAdapter = new PhotoPreviewListAdapter(this, selectedPhotoList);
    photoPreviewListAdapter.setItemFocusChangedListener(this);
    photoPreviewListAdapter.setPhotoOrderChangedListener(this);
    photoPreviewListAdapter.attachToRecyclerView(rvPreviewPhotoList);

    vpPhotoPager = findViewById(R.id.vp_photo_pager);
    photoPagerAdapter = new PhotoPagerAdapter(this, allPhotoList);
    photoPagerAdapter.setPageChangedListener(this);
    photoPagerAdapter.setPageClickListener(this);
    photoPagerAdapter.attachToViewPager(vpPhotoPager);

    initStartupStatus();

    tvComplete = findViewById(R.id.tv_complete);
    tvComplete.setText(String.format("完成(%d/%d)", selectedPhotoList.size(), maxPhotoCount));
    tvComplete.setOnClickListener(v -> onComplete());

    topBox = findViewById(R.id.rl_top_box);
    bottomBox = findViewById(R.id.ll_bottom_box);
  }

  private void initStartupStatus() {
    photoPreviewListAdapter.setCurrentFocusPhoto(currentPhoto);
    photoPagerAdapter.setCurrentItem(currentPhoto);

    int position = photoPreviewListAdapter.findPhotoPosition(currentPhoto);
    boolean isSelected = photoPreviewListAdapter.getItemSelection(position);
    setRightIcon(position + 1, isSelected);
  }

  private void setRightIcon(int position, boolean isSelected) {
    if (isSelected) {
      int size = (int) DimenUtils.dp2px(this, 24);
      ivRightIcon.setImageBitmap(
          SelectedFlagBitmapUtils.createFlagBitmap("" + position, size, size));
    } else {
      ivRightIcon.setImageResource(R.drawable.ic_gray_selection_status);
    }
  }

  private void onRightIconClick() {
    int position = photoPreviewListAdapter.findPhotoPosition(currentPhoto);
    if (position < 0 || position >= photoPreviewListAdapter.getItemCount()) {
      if (photoPreviewListAdapter.getSelectedCount() >= maxPhotoCount) {
        return;
      }
      photoPreviewListAdapter.addData(currentPhoto);
      photoPreviewListAdapter.setCurrentFocusPhoto(currentPhoto);
    } else {//已经存在
      boolean isSelected = photoPreviewListAdapter.getItemSelection(position);
      if (isSelected) {
        if (!isSynchro) {
          photoPreviewListAdapter.removeData(currentPhoto);
        } else {
          photoPreviewListAdapter.setItemSelection(position, false);
        }
        setRightIcon(position + 1, false);
      } else {
        photoPreviewListAdapter.setItemSelection(position, true);
        setRightIcon(position + 1, true);
      }
    }
    tvComplete.setText(
        String.format("完成(%d/%d)", photoPreviewListAdapter.getSelectedCount(), maxPhotoCount));
  }

  @Override public void onItemFocus(int position, Photo photo) {
    currentPhoto = photo;
    setRightIcon(position + 1, photoPreviewListAdapter.getItemSelection(position));
    photoPagerAdapter.setCurrentItem(photo);
  }

  @Override public void onPageChange(int position, Photo photo) {
    currentPhoto = photo;
    int selectedListPosition = photoPreviewListAdapter.findPhotoPosition(photo);
    photoPreviewListAdapter.setCurrentFocusPhoto(photo);
    if (selectedListPosition >= 0
        && selectedListPosition < photoPreviewListAdapter.getItemCount()) {
      setRightIcon(selectedListPosition + 1,
          photoPreviewListAdapter.getItemSelection(selectedListPosition));
    } else {
      setRightIcon(selectedListPosition + 1, false);
    }
  }

  @Override public void onPhotoOrderChange(List<Photo> data) {
    if (isSynchro) {
      photoPagerAdapter.updateDataSet(data, currentPhoto);
    }
  }

  @Override public void onPageClick() {
    if (isFullScreen) {
      topBox.setVisibility(View.VISIBLE);
      bottomBox.setVisibility(View.VISIBLE);
      isFullScreen = false;
    } else {
      topBox.setVisibility(View.GONE);
      bottomBox.setVisibility(View.GONE);
      isFullScreen = true;
    }
  }

  private void onComplete() {
    Intent intent = new Intent();
    ArrayList<Photo> selectedPhotoList =
        (ArrayList<Photo>) photoPreviewListAdapter.getSelectedPhotoList();
    intent.putParcelableArrayListExtra(PhotoPicker.DATA_SELECTED_PHOTOS, selectedPhotoList);
    setResult(RESULT_OK, intent);
    finish();
  }
}
