package com.trungpd.biliso.utils

import android.content.Context
import android.content.res.AssetManager
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import androidx.annotation.ArrayRes
import androidx.annotation.StringRes
import java.util.*

class AppUtils {
    companion object {
        // todo change, this is a memory leak
        lateinit var mContext: Context
        lateinit var mMainThread: Thread
        lateinit var mTimer: Timer
        var sHandler: Handler = Handler(Looper.getMainLooper())

        fun getAssets(): AssetManager = mContext.assets

        fun getDimension(id: Int): Float = getResource().getDimension(id)

        fun getResource(): Resources = mContext.resources

        fun getDrawable(id: Int): Drawable = getResource().getDrawable(id)

        fun getColor(resId: Int): Int = getResource().getColor(resId)

        fun getString(@StringRes resId: Int): String = getResource().getString(resId)

        fun getStringArray(@ArrayRes resId: Int): Array<String> =
            getResource().getStringArray(resId)

        fun isUIThread() = Thread.currentThread() == mMainThread

        fun runOnUI(runnable: Runnable) {
            sHandler.post(runnable)
        }

        fun runOnUIDelayed(r: Runnable, delay: Long) {
            sHandler.postDelayed(r, delay)
        }

        fun runOnUITask(r: TimerTask, delay: Long, rate: Long) {
            mTimer.schedule(r, delay, rate)
        }

        fun runCancel() {
            mTimer.cancel()
        }

        fun removeRunnable(r: Runnable) {
            if (r == null) {
                sHandler.removeCallbacksAndMessages(null)
            } else {
                sHandler.removeCallbacks(r)
            }
        }


    }

    fun init(context: Context) {
        mContext = context
        mMainThread = Thread.currentThread()
        mTimer = Timer()
    }


}
