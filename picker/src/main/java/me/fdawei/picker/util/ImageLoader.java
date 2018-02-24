package me.fdawei.picker.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

/**
 * Created by david on 2018/2/18.
 */

public class ImageLoader {

  public static void load(ImageView imageView, String image) {
    if (imageView == null) {
      return;
    }
    Glide.with(imageView.getContext()).load(image).into(imageView);
  }

  public static void loadBitmap(Context context, String image, Target<Bitmap> listener) {
    Glide.with(context).asBitmap().load(image).into(listener);
  }
}
