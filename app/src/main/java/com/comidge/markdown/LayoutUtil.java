package com.comidge.markdown;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * Created by rubin on 2017. 3. 24..
 */

public class LayoutUtil {

    public static int getDp(Context context, float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static ViewGroup.LayoutParams inflateLayoutParams(View view, int width, int height) {
        ViewParent parent = view.getParent();
        ViewGroup.LayoutParams layoutParams;
        if(parent instanceof RelativeLayout) {
            layoutParams = new RelativeLayout.LayoutParams(width, height);
        } else if (parent instanceof LinearLayout) {
            layoutParams = new LinearLayout.LayoutParams(width, height);
        } else if (parent instanceof FrameLayout) {
            layoutParams = new FrameLayout.LayoutParams(width, height);
        } else {
            layoutParams = new ViewGroup.LayoutParams(width, height);
        }
        view.setLayoutParams(layoutParams);
        return layoutParams;
    }
}
