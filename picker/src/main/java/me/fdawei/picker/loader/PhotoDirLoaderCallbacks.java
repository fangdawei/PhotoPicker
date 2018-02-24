package me.fdawei.picker.loader;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.fdawei.picker.R;
import me.fdawei.picker.entity.PhotoDir;

import static android.provider.BaseColumns._ID;
import static android.provider.MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME;
import static android.provider.MediaStore.Images.ImageColumns.BUCKET_ID;
import static android.provider.MediaStore.MediaColumns.DATA;
import static android.provider.MediaStore.MediaColumns.SIZE;

/**
 * Created by david on 2018/2/18.
 */

public class PhotoDirLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {

  private Context context;
  private OnPhotoDirResultListener photoDirResultListener;

  public PhotoDirLoaderCallbacks(Context context, OnPhotoDirResultListener listener) {
    this.context = context;
    this.photoDirResultListener = listener;
  }

  @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    return new PhotoDirLoader(context, false);
  }

  @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    if (data == null) {
      return;
    }

    Map<String, PhotoDir> photoDirMap = new HashMap<>();
    PhotoDir photoDirAll = new PhotoDir();
    photoDirAll.setName(context.getString(R.string.all_photo));
    photoDirAll.setId("ALL");

    while (data.moveToNext()) {

      int imageId  = data.getInt(data.getColumnIndexOrThrow(_ID));
      String bucketId = data.getString(data.getColumnIndexOrThrow(BUCKET_ID));
      String bucketName = data.getString(data.getColumnIndexOrThrow(BUCKET_DISPLAY_NAME));
      String path = data.getString(data.getColumnIndexOrThrow(DATA));
      long size = data.getInt(data.getColumnIndexOrThrow(SIZE));

      if (size < 1) {
        continue;
      }

      PhotoDir photoDir = photoDirMap.get(bucketId);
      if(photoDir == null) {
        photoDir = new PhotoDir();
        photoDir.setId(bucketId);
        photoDir.setName(bucketName);
        photoDirMap.put(bucketId, photoDir);
      }

      photoDir.addPhoto(imageId, path,size);
      photoDirAll.addPhoto(imageId, path, size);
    }

    List<PhotoDir> photoDirList = new ArrayList<>();
    photoDirList.add(photoDirAll);
    photoDirList.addAll(photoDirMap.values());

    if(photoDirResultListener != null) {
      photoDirResultListener.onPhotoDirResult(photoDirList);
    }
  }

  @Override public void onLoaderReset(Loader<Cursor> loader) {

  }

  public interface OnPhotoDirResultListener {
    void onPhotoDirResult(List<PhotoDir> photoDirList);
  }
}
