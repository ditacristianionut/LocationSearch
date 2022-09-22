package com.dci.dev.locationsearch.utils

import android.content.res.Resources

val displayDensity: Float
    get() = Resources.getSystem().displayMetrics.density

val scaledDensity: Float
    get() = Resources.getSystem().displayMetrics.scaledDensity

val Int.dp2px: Int
    get() = (this * displayDensity).toInt()

val Int.sp2px: Int
    get() = (this * scaledDensity + 0.5f).toInt()

val Int.px2dp: Int
    get() = (this / displayDensity).toInt()

val screenWidth: Int
    get() = Resources.getSystem().displayMetrics.widthPixels

val screenHeight: Int
    get() = Resources.getSystem().displayMetrics.heightPixels