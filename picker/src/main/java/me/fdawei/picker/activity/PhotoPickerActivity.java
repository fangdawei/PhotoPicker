package me.fdawei.picker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import me.fdawei.picker.PhotoPicker;
import me.fdawei.picker.R;
import me.fdawei.picker.adapter.DividerGridItemDecoration;
import me.fdawei.picker.adapter.PhotoGridAdapter;
import me.fdawei.picker.entity.Photo;
import me.fdawei.picker.entity.PhotoDir;
import me.fdawei.picker.loader.PhotoDirLoaderCallbacks;
import me.fdawei.picker.util.DimenUtils;

/**
 * Created by david on 2018/2/16.
 */

public class PhotoPickerActivity extends AppCompatActivity
    implements PhotoDirLoaderCallbacks.OnPhotoDirResultListener,
    PhotoGridAdapter.OnItemSelectionChangedListener, PhotoGridAdapter.OnItemClickListener {

  private static final int REQUEST_PHOTO_DIR = 1000;
  private static final int REQUEST_PREVIEW = 2000;

  private int columnCount;
  private int selectCountLimit;
  private RecyclerView photoGridView;
  private List<PhotoDir> photoDirList;
  private PhotoGridAdapter photoGridAdapter;
  private TextView tvTitle;
  private TextView tvComplete;
  private PhotoDir currentDir;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_photo_picker);

    columnCount =
        getIntent().getIntExtra(PhotoPicker.OPTIONS_COLUMN_COUNT, PhotoPicker.DEFAULT_COLUMN_COUNT);
    selectCountLimit =
        getIntent().getIntExtra(PhotoPicker.OPTIONS_PHOTO_LIMIT, PhotoPicker.DEFAULT_PHOTO_LIMIT);

    Toolbar toolbar = findViewById(R.id.ic_toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayShowTitleEnabled(false);
    toolbar.setNavigationOnClickListener(
        v -> PhotoDirActivity.startForResult(this, REQUEST_PHOTO_DIR,
            (ArrayList<PhotoDir>) photoDirList));

    tvTitle = toolbar.findViewById(R.id.toolbar_title);
    tvTitle.setText(R.string.all_photo);

    TextView tvRightText = toolbar.findViewById(R.id.tv_right_text);
    tvRightText.setText(R.string.cancel);
    tvRightText.setOnClickListener(v -> onBackPressed());

    tvComplete = findViewById(R.id.tv_complete);
    tvComplete.setText(String.format("完成(%d/%d)", 0, selectCountLimit));
    tvComplete.setOnClickListener(v -> onComplete());

    TextView tvPreviewBtn = findViewById(R.id.tv_preview);
    tvPreviewBtn.setOnClickListener(v -> onPreviewSelectedPhoto());

    photoGridView = findViewById(R.id.rv_photo_grid);
    photoGridView.setLayoutManager(new GridLayoutManager(this, columnCount));
    int dividerWidth = (int) DimenUtils.dp2px(this, 5);
    int dividerColor = getResources().getColor(R.color.white);
    photoGridView.addItemDecoration(
        new DividerGridItemDecoration(this, columnCount, dividerWidth, dividerColor));
    photoGridAdapter = new PhotoGridAdapter(this, selectCountLimit);
    photoGridAdapter.setOnItemSelectionChangedListener(this);
    photoGridAdapter.setOnItemClickListener(this);
    photoGridAdapter.attachToRecyclerView(photoGridView);

    //创建Loader
    getSupportLoaderManager().initLoader(0, null, new PhotoDirLoaderCallbacks(this, this));
  }

  @Override public void onPhotoDirResult(List<PhotoDir> photoDirList) {
    this.photoDirList = photoDirList;
    currentDir = photoDirList.get(0);
    photoGridAdapter.setData(currentDir.getPhotos());
    tvComplete.setText(String.format("完成(%d/%d)", 0, selectCountLimit));
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REQUEST_PHOTO_DIR) {
      if (resultCode == RESULT_OK) {
        currentDir = data.getParcelableExtra(PhotoDirActivity.RESULT_PHOTO_DIR);
        photoGridAdapter.setData(currentDir.getPhotos());
        tvTitle.setText(currentDir.getName());
      } else if (resultCode == PhotoDirActivity.RESULT_FINISH) {
        onBackPressed();
      }
    } else if (requestCode == REQUEST_PREVIEW) {
      if (resultCode == RESULT_OK) {
        List<Photo> selectedPhotoList =
            data.getParcelableArrayListExtra(PhotoPicker.DATA_SELECTED_PHOTOS);
        setSelectResult(selectedPhotoList);
        finish();
      }
    }
  }

  @Override public void onItemSelectionChanged(int selectedCount) {
    tvComplete.setText(String.format("完成(%d/%d)", selectedCount, selectCountLimit));
  }

  private void onComplete() {
    setSelectResult(photoGridAdapter.getSelectedPhotoList());
    finish();
  }

  private void onPreviewSelectedPhoto() {
    ArrayList<Photo> selectedPhotos = (ArrayList<Photo>) photoGridAdapter.getSelectedPhotoList();
    PhotoPreviewActivity.startForResult(this, REQUEST_PREVIEW, selectedPhotos, selectCountLimit);
  }

  private void setSelectResult(List<Photo> photoList) {
    Intent intent = new Intent();
    ArrayList<String> photoPathList = new ArrayList<>();
    for (Photo photo : photoList) {
      photoPathList.add(photo.getPath());
    }
    intent.putStringArrayListExtra(PhotoPicker.DATA_SELECTED_PHOTOS, photoPathList);
    setResult(RESULT_OK, intent);
  }

  @Override public void onItemClick(int position) {
    ArrayList<Photo> selectedPhotos = (ArrayList<Photo>) photoGridAdapter.getSelectedPhotoList();
    ArrayList<Photo> dirPhotos = (ArrayList<Photo>) currentDir.getPhotos();
    PhotoPreviewActivity.startForResult(this, REQUEST_PREVIEW, selectedPhotos, selectCountLimit,
        dirPhotos, position);
  }
}
