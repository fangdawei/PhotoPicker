package me.fdawei.picker.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import me.fdawei.picker.R;
import me.fdawei.picker.adapter.PhotoDirAdapter;
import me.fdawei.picker.entity.PhotoDir;

/**
 * Created by david on 2018/2/18.
 */

public class PhotoDirActivity extends AppCompatActivity implements PhotoDirAdapter.OnItemClickListener {

  private static final String ARG_PHOTO_DIR_LIST = "photo_dir_list";
  public static final String RESULT_PHOTO_DIR = "selected_photo_dir";
  public static final int RESULT_FINISH = 10;

  private PhotoDirAdapter photoDirAdapter;
  private RecyclerView rvPhotoDir;

  public static void startForResult(Activity activity, int requestCode, ArrayList<PhotoDir> photoDirList) {
    Intent intent = new Intent(activity, PhotoDirActivity.class);
    intent.putParcelableArrayListExtra(ARG_PHOTO_DIR_LIST, photoDirList);
    activity.startActivityForResult(intent, requestCode);
  }

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_photo_dir);

    Toolbar toolbar = findViewById(R.id.ic_toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayShowTitleEnabled(false);
    toolbar.setNavigationIcon(null);
    TextView tvTitle = toolbar.findViewById(R.id.toolbar_title);
    tvTitle.setText(R.string.photo);
    TextView tvCancel = toolbar.findViewById(R.id.tv_right_text);
    tvCancel.setOnClickListener(v -> {
      setResult(RESULT_FINISH);
      finish();
    });

    List<PhotoDir> photoDirList = getIntent().getParcelableArrayListExtra(ARG_PHOTO_DIR_LIST);
    photoDirAdapter = new PhotoDirAdapter(this, photoDirList);
    photoDirAdapter.setOnItemClickListener(this);

    rvPhotoDir = findViewById(R.id.rv_photo_dir);
    rvPhotoDir.setLayoutManager(new LinearLayoutManager(this));
    rvPhotoDir.setAdapter(photoDirAdapter);
  }

  @Override public void onItemClick(PhotoDir photoDir) {
    Intent intent = new Intent();
    intent.putExtra(RESULT_PHOTO_DIR, photoDir);
    setResult(RESULT_OK, intent);
    finish();
  }
}
