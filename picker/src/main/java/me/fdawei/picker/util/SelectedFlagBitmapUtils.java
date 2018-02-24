package me.fdawei.picker.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by david on 2018/2/19.
 */

public class SelectedFlagBitmapUtils {

  public static Bitmap createFlagBitmap(String text, int width, int height) {
    String firstLetter = text.substring(0, 1);
    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(bitmap);
    canvas.drawColor(0x00FFFFFF);
    Paint bgPaint = new Paint();
    bgPaint.setColor(0xFF20A0FF);
    canvas.drawCircle(width / 2, height / 2, (Math.min(width, height)) / 2, bgPaint);

    Paint paint = new Paint();
    paint.setColor(0xFFFFFFFF);
    paint.setAntiAlias(true);
    paint.setTextAlign(Paint.Align.CENTER);
    paint.setTextSize(width * 2 / 3);

    paint.getTextBounds(text, 0, text.length(), new Rect());
    Paint.FontMetricsInt fontMetricsInt = paint.getFontMetricsInt();

    int baseLine =
        height / 2 - (fontMetricsInt.bottom - fontMetricsInt.top) / 2 - fontMetricsInt.top;

    canvas.drawText(firstLetter, width / 2, baseLine, paint);
    return bitmap;
  }
}
