package com.trungpd.biliso.utils

import android.content.Context
import android.net.ConnectivityManager
import android.telephony.TelephonyManager

class NetworkUtils {
    companion object {
        private val TAG = NetworkUtils::class.java.simpleName
        val NET_BROADCAST_ACTION = "com.network.state.action"
        val NET_STATE_NAME = "network_state"

        /**
         * Real-time update network status <br>
         * -1 is no connection to the network <br>
         * 1 is WiFi <br>
         * 2 is mobile network <br>
         */
        val CURRENT_NETWORK_STATE = -1

        fun getConnectivityManager(context: Context) =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        fun getTelephonyManager(context: Context) =
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        /*fun isConnected(context: Context) : Boolean {

        }*/
    }

    enum class NetType (val value: Int, val desc: String){
        None(1, "No network connection"),
        Mobile(2, "Honeycomb mobile network"),
        Wifi(4, "WiFi network"),
        Other(8, "Unknown network")
    }

    enum class NetWorkType (val value: Int, val desc: String){
        UnKnown(-1, "Unknown network "),
        Wifi(1, "Wifi network"),
        Net2G(2, "2G network"),
        Net3G(3, "3G network"),
        Net4G(4, "4G network"),
        Net5G(5, "5G network")
    }
}