package com.example.criminalintent.utils

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Insets
import android.graphics.Point
import android.view.WindowInsets
import android.view.WindowManager
import kotlin.math.roundToInt

class PictureUtils {

    companion object{
        fun getScaledBitmap(path: String, activity: Activity): Bitmap{
            val size = activity.windowManager.currentWindowMetricsPointCompat()
            return getScaledBitmap(path, size.x, size.y)
        }

        private fun getScaledBitmap(path: String, destWidth: Int, destHeight: Int): Bitmap{
            var options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(path, options)

            val srcWidth = options.outWidth.toFloat()
            val srcHeight = options.outHeight.toFloat()

            var inSampleSize = 1
            if (srcHeight > destHeight || srcWidth > destWidth){
                val heightScale = srcHeight / destHeight
                val widthScale = srcWidth / destWidth

                val sampleScale = if (heightScale > widthScale){
                    heightScale
                }else{
                    widthScale
                }
                inSampleSize = sampleScale.roundToInt()
            }
            options = BitmapFactory.Options()
            options.inSampleSize = inSampleSize
            return BitmapFactory.decodeFile(path, options)
        }

        private fun WindowManager.currentWindowMetricsPointCompat(): Point {
            return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                val windowInsets = currentWindowMetrics.windowInsets
                var insets: Insets = windowInsets.getInsets(WindowInsets.Type.navigationBars())
                windowInsets.displayCutout?.run {
                    insets = Insets.max(insets, Insets.of(safeInsetLeft, safeInsetTop, safeInsetRight, safeInsetBottom))
                }
                val insetsWidth = insets.right + insets.left
                val insetsHeight = insets.top + insets.bottom
                Point(currentWindowMetrics.bounds.width() - insetsWidth, currentWindowMetrics.bounds.height() - insetsHeight)
            }else{
                Point().apply {
                    defaultDisplay.getSize(this)
                }
            }
        }
    }
}