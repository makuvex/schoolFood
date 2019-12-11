package com.jungbae.schoolfood.view

import android.app.Activity
import android.graphics.Rect
import android.view.TouchDelegate
import android.view.View

fun Activity.increaseTouchArea(view: View, increaseBy: Int) {
    val parent = view.parent
    (parent as View).post({
        val rect = Rect()
        view.getHitRect(rect)
        val intValue = increaseBy.toInt()
        rect.top -= intValue    // increase top hit area
        rect.left -= intValue   // increase left hit area
        rect.bottom += intValue // increase bottom hit area
        rect.right += intValue  // increase right hit area
        parent.setTouchDelegate(TouchDelegate(rect, view));
    });
}