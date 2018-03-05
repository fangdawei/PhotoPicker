package me.fdawei.picker.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Created by david on 2018/2/18.
 */

public class ImageLoader {

  public static void load(Context context, ImageView imageView, String image) {
    if (imageView == null) {
      return;
    }
    int width = imageView.getWidth();
    int height = imageView.getHeight();
    RequestOptions options = new RequestOptions();
    options.override(width, height).centerCrop();
    Glide.with(context).load(image).apply(options).into(imageView);
  }

  public static void loadBitmap(String path, OnBitmapReadyListener listener) {
    Observable<Bitmap> observable = Observable.create(emitter -> {

      BitmapFactory.Options options = new BitmapFactory.Options();
      options.inPreferredConfig = Bitmap.Config.RGB_565;
      options.inJustDecodeBounds = false;
      options.inSampleSize = 2;
      Bitmap bitmap = BitmapFactory.decodeFile(path, options);

      emitter.onNext(bitmap);
    });

    observable.observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(bmp -> {
          if (listener != null) {
            listener.onBitmapReady(bmp);
          }
        });
  }

  public interface OnBitmapReadyListener {
    void onBitmapReady(Bitmap bitmap);
  }

  public static void loadBitmap(Context context, String image, Target<Bitmap> listener) {
    BitmapFactory.Options boundsOptions = new BitmapFactory.Options();
    boundsOptions.inJustDecodeBounds = true;
    BitmapFactory.decodeFile(image, boundsOptions);

    int width = boundsOptions.outWidth;
    int height = boundsOptions.outHeight;

    RequestOptions options = new RequestOptions();
    options.dontAnimate().override(width / 2, height / 2);
    Glide.with(context).asBitmap().load(image).apply(options).into(listener);
  }
}
