package me.fdawei.picker;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import me.fdawei.picker.activity.PhotoPickerActivity;
import me.fdawei.picker.util.PermissionUtils;

/**
 * Created by david on 2018/2/16.
 */

public class PhotoPicker {

  public static final String OPTIONS_PHOTO_LIMIT = "photo_limit";
  public static final String OPTIONS_COLUMN_COUNT = "column_count";

  public static final String DATA_SELECTED_PHOTOS = "selected_photos";

  public static final int DEFAULT_PHOTO_LIMIT = 9;
  public static final int DEFAULT_COLUMN_COUNT = 4;

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private Bundle optionsBundle;

    public Builder() {
      optionsBundle = new Bundle();
      optionsBundle.putInt(OPTIONS_PHOTO_LIMIT, DEFAULT_PHOTO_LIMIT);
      optionsBundle.putInt(OPTIONS_COLUMN_COUNT, DEFAULT_COLUMN_COUNT);
    }

    public void open(Activity activity, int requestCode) {
      if (!PermissionUtils.checkPermissionStorage(activity)) {
        return;
      }
      Intent intent = new Intent(activity, PhotoPickerActivity.class);
      intent.putExtras(optionsBundle);
      activity.startActivityForResult(intent, requestCode);
    }

    public void open(Fragment fragment, int requestCode) {
      if (!PermissionUtils.checkPermissionStorage(fragment.getActivity())) {
        return;
      }
      Intent intent = new Intent(fragment.getActivity(), PhotoPickerActivity.class);
      intent.putExtras(optionsBundle);
      fragment.startActivityForResult(intent, requestCode);
    }

    public Builder setPhotoLimit(int count) {
      if (count < 1) {
        count = 1;
      }
      optionsBundle.putInt(OPTIONS_PHOTO_LIMIT, count);
      return this;
    }

    public Builder setColumnCount(int columnCount) {
      if (columnCount < 1) {
        columnCount = 1;
      }
      optionsBundle.putInt(OPTIONS_COLUMN_COUNT, columnCount);
      return this;
    }
  }
}
