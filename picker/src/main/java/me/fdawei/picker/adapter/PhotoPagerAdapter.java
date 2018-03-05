package me.fdawei.picker.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import me.fdawei.picker.R;
import me.fdawei.picker.entity.Photo;
import me.fdawei.picker.util.ImageLoader;

/**
 * Created by david on 2018/2/24.
 */

public class PhotoPagerAdapter extends PagerAdapter {

  private Context context;
  private List<Photo> photoList = new ArrayList<>();
  private LinkedList<View> viewCache = new LinkedList<>();
  private ViewPager viewPager;
  private int current = -1;
  private OnPageChangedListener pageChangedListener;
  private OnPageClickListener pageClickListener;
  private View.OnClickListener itemClickListener;

  public PhotoPagerAdapter(Context context, List<Photo> data) {
    this.context = context;
    if (data != null) {
      photoList.addAll(data);
    }

    itemClickListener = v -> {
      if (pageClickListener != null) {
        pageClickListener.onPageClick();
      }
    };
  }

  public void attachToViewPager(ViewPager viewPager) {
    this.viewPager = viewPager;
    viewPager.setAdapter(this);
    viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

      }

      @Override public void onPageSelected(int position) {
        if (position != current) {
          if (pageChangedListener != null) {
            pageChangedListener.onPageChange(position, photoList.get(position));
          }
        }
      }

      @Override public void onPageScrollStateChanged(int state) {

      }
    });
  }

  public void setPageChangedListener(OnPageChangedListener pageChangedListener) {
    this.pageChangedListener = pageChangedListener;
  }

  public void setPageClickListener(OnPageClickListener pageClickListener) {
    this.pageClickListener = pageClickListener;
  }

  public void updateDataSet(List<Photo> data, Photo currentPhoto) {
    if (viewPager == null) {
      return;
    }
    photoList.clear();
    if (data != null) {
      photoList.addAll(data);
    }
    notifyDataSetChanged();
    int position = photoList.indexOf(currentPhoto);
    current = position;
    viewPager.setCurrentItem(current, false);
  }

  public void setCurrentItem(Photo photo) {
    int position = photoList.indexOf(photo);
    current = position;
    viewPager.setCurrentItem(position, false);
  }

  @Override public int getCount() {
    return photoList.size();
  }

  @Override public boolean isViewFromObject(View view, Object object) {
    return view == object;
  }

  @Override public Object instantiateItem(ViewGroup container, int position) {
    Photo photo = photoList.get(position);
    View itemView = viewCache.poll();
    if (itemView == null) {
      itemView = LayoutInflater.from(context).inflate(R.layout.item_photo_pager, container, false);
    }
    final ImageView iv = itemView.findViewById(R.id.iv_photo);
    String imagePath = photo.getPath();
    asynLoadImageByGlide(iv, imagePath);
    iv.setClickable(true);
    iv.setOnClickListener(itemClickListener);
    container.addView(itemView);
    return itemView;
  }

  private void asynLoadImage(final ImageView iv, String path) {
    ImageLoader.loadBitmap(path, bmp -> {
      int size = bmp.getByteCount();
      Log.d("load image", "size:" + size / 1024 + "KB");
      iv.setTag(bmp);
      iv.setImageBitmap(bmp);
    });
  }

  private void asynLoadImageByGlide(final ImageView iv, String path) {
    ImageLoader.loadBitmap(context, path, new SimpleTarget<Bitmap>() {
      @Override
      public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
        int size = resource.getByteCount();
        Log.d("load image", "size:" + size / 1024 + "KB");
        iv.setImageBitmap(resource);
      }
    });
  }


  @Override public void destroyItem(ViewGroup container, int position, Object object) {
    View itemView = (View) object;
    container.removeView(itemView);
    ImageView iv = itemView.findViewById(R.id.iv_photo);
    iv.setImageBitmap(null);
    Bitmap bitmap = (Bitmap) iv.getTag();
    iv.setTag(null);
    if(bitmap != null) {
      bitmap.recycle();
    }
    viewCache.push(itemView);
  }

  @Override public int getItemPosition(Object object) {
    return POSITION_NONE;
  }

  public interface OnPageChangedListener {
    void onPageChange(int position, Photo photo);
  }

  public interface OnPageClickListener {
    void onPageClick();
  }
}