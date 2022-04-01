package com.trungpd.biliso.widget

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.IBinder
import com.trungpd.biliso.utils.NetworkUtils

class NetworkService : Service() {

    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val connectivityManager =
                context!!.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            if (connectivityManager == null) {
                networkBroadCast(context, intent, -1)
                return
            }
            val info = connectivityManager.activeNetworkInfo
            if (info == null) {
                networkBroadCast(context, intent, -1)
                return
            }
            val type = info.type
            when (type) {
                ConnectivityManager.TYPE_WIFI -> networkBroadCast(context, intent, 1)
                ConnectivityManager.TYPE_MOBILE -> networkBroadCast(context, intent, 2)
                else -> {}
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun networkBroadCast(context: Context, intent: Intent?, netState: Int) {
        intent.let {
            it?.action = NetworkUtils.NET_BROADCAST_ACTION
            it?.putExtra(NetworkUtils.NET_STATE_NAME, netState)
            context.sendBroadcast(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        val filter = IntentFilter()
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(mReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mReceiver)
    }

}