package me.fdawei.picker.util;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by david on 2018/2/18.
 */

public class DimenUtils {
  /**
   * sp转px
   */
  public static float sp2px(Context context, float sp) {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
  }

  /**
   * dp转px
   */
  public static float dp2px(Context context, float dp) {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
  }
}
