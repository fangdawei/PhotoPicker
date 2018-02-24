package me.fdawei.picker.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

/**
 * Created by david on 2018/2/16.
 */

public class PermissionUtils {

  public static boolean checkPermissionStorage(Context context) {
    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
      return true;
    }
    int result =
        ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    return result == PackageManager.PERMISSION_GRANTED;
  }
}
