package me.fdawei.picker.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by david on 2018/2/18.
 */

public class Photo implements Parcelable {
  private int id;
  private String path;
  private long size;

  public Photo(int id, String path, long size) {
    this.id = id;
    this.path = path;
    this.size = size;
  }

  protected Photo(Parcel in) {
    id = in.readInt();
    path = in.readString();
    size = in.readLong();
  }

  public static final Creator<Photo> CREATOR = new Creator<Photo>() {
    @Override public Photo createFromParcel(Parcel in) {
      return new Photo(in);
    }

    @Override public Photo[] newArray(int size) {
      return new Photo[size];
    }
  };

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(id);
    dest.writeString(path);
    dest.writeLong(size);
  }

  @Override public boolean equals(Object obj) {
    Photo photo = (Photo) obj;
    return this.id == photo.id;
  }
}
