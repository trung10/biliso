package com.trungpd.biliso.utils

import android.content.Context
import java.util.*

class AppUtils {
    companion object {
        lateinit var mContext: Context
        lateinit var mMainThread: Thread
        lateinit var mTimer: Timer
    }

    fun init(context: Context){
        mContext = context

    }
}