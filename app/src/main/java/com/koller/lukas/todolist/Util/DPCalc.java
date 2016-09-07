package com.koller.lukas.todolist.Util;

import android.content.res.Resources;
import android.util.TypedValue;

/**
 * Created by Lukas on 20.08.2016.
 */
public class DPCalc {

    public static float dpIntoPx(Resources r, int dp){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }
}
