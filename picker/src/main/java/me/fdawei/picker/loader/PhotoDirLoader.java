package me.fdawei.picker.loader;

import android.content.Context;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;

import static android.provider.MediaStore.MediaColumns.MIME_TYPE;

/**
 * Created by david on 2018/2/18.
 */

public class PhotoDirLoader extends CursorLoader {

  final String[] IMAGE_PROJECTION = {
      MediaStore.Images.Media._ID,
      MediaStore.Images.Media.DATA,
      MediaStore.Images.Media.BUCKET_ID,
      MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
      MediaStore.Images.Media.DATE_ADDED,
      MediaStore.Images.Media.SIZE
  };

  public PhotoDirLoader(Context context, boolean showGif) {
    super(context);

    setProjection(IMAGE_PROJECTION);
    setUri(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    setSortOrder(MediaStore.Images.Media.DATE_ADDED + " DESC");
    setSelection(MIME_TYPE + "=? or " + MIME_TYPE + "=? or " + MIME_TYPE + "=? " + (showGif ? ("or "
        + MIME_TYPE
        + "=?") : ""));

    String[] selectionArgs;
    if (showGif) {
      selectionArgs = new String[] { "image/jpeg", "image/png", "image/jpg", "image/gif" };
    } else {
      selectionArgs = new String[] { "image/jpeg", "image/png", "image/jpg" };
    }
    setSelectionArgs(selectionArgs);
  }
}
