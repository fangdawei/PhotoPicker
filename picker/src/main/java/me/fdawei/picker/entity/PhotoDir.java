package me.fdawei.picker.entity;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by david on 2018/2/18.
 */

public class PhotoDir implements Parcelable {
  private String id;
  private String name;
  private List<Photo> photos = new ArrayList<>();

  public PhotoDir() {

  }

  protected PhotoDir(Parcel in) {
    id = in.readString();
    name = in.readString();
    photos = in.createTypedArrayList(Photo.CREATOR);
  }

  public static final Creator<PhotoDir> CREATOR = new Creator<PhotoDir>() {
    @Override public PhotoDir createFromParcel(Parcel in) {
      return new PhotoDir(in);
    }

    @Override public PhotoDir[] newArray(int size) {
      return new PhotoDir[size];
    }
  };

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPreviewPhoto() {
    if(photos.size() > 0) {
      return photos.get(0).getPath();
    } else {
      return null;
    }
  }

  public int getPhotoCount() {
    return photos.size();
  }

  public void addPhoto(int id, String path, long size) {
    photos.add(new Photo(id, path, size));
  }

  public List<Photo> getPhotos() {
    return photos;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(id);
    dest.writeString(name);
    dest.writeTypedList(photos);
  }
}
