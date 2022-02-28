package com.trungpd.biliso.base

import android.os.Build
import android.os.Handler
import android.os.Looper

class MainHandler {
    companion object {
        lateinit var mainHandler: Handler
    }

    init {
        var handler: Handler
        var looper: Looper = Looper.getMainLooper()

        if (Build.VERSION.SDK_INT >= 28){
            handler = Handler.createAsync(looper)
        } else {
            try {
                handler = Handler::class.java.getDeclaredConstructor(Looper::class.java,
                    Handler.Callback::class.java, Boolean::class.java).newInstance(mainHandler,
                null, true)
            } catch (e: IllegalAccessException) {

            }
        }
    }
}