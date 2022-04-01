package com.trungpd.biliso.utils

import android.app.Activity
import android.app.PendingIntent
import android.app.PendingIntent.CanceledException
import android.content.*
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import com.trungpd.biliso.widget.NetworkService
import java.lang.Exception

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
        var CURRENT_NETWORK_STATE = -1

        fun getConnectivityManager(context: Context) =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        fun getTelephonyManager(context: Context) =
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        fun isConnected(context: Context): Boolean {
            val netInfo: NetworkInfo? = getConnectivityManager(context).activeNetworkInfo
            return netInfo != null && netInfo.isConnected
        }

        fun isInternetAvailable(context: Context): Boolean {
            var result = false
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val networkCapabilities = connectivityManager.activeNetwork ?: return false
                val actNw =
                    connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
                result = when {
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            } else {
                connectivityManager.run {
                    connectivityManager.activeNetworkInfo?.run {
                        result = when (type) {
                            ConnectivityManager.TYPE_WIFI -> true
                            ConnectivityManager.TYPE_MOBILE -> true
                            ConnectivityManager.TYPE_ETHERNET -> true
                            else -> false
                        }

                    }
                }
            }

            return result
        }

        /**
         * Judging that there is no network is being connected or connected (find network,
         * check, get IP, etc.) (including mobilenet, wifi)
         *
         * @return
         */
        fun isConnectedOrConnecting(context: Context): Boolean {
            val nets = getConnectivityManager(context).allNetworkInfo
            if (nets != null) {
                for (net in nets) {
                    if (net.isConnectedOrConnecting) {
                        return true
                    }
                }
            }
            return false
        }

        /**
         * Get current network types (WiFi or mobile network)
         *
         * @param context
         * @return
         */
        // todo change
        fun getConnectedType(context: Context): NetType {
            val net = getConnectivityManager(context).activeNetworkInfo
            return if (net != null) {
                when (net.type) {
                    ConnectivityManager.TYPE_WIFI -> NetType.Wifi
                    ConnectivityManager.TYPE_MOBILE -> NetType.Mobile
                    else -> NetType.Other
                }
            } else NetType.None
        }

        // todo change
        fun isWifiConnected(context: Context): Boolean {
            val net = getConnectivityManager(context).activeNetworkInfo
            return net != null && net.type == ConnectivityManager.TYPE_WIFI && net.isConnected
        }

        // todo change
        fun isMobileConnected(context: Context): Boolean {
            val net = getConnectivityManager(context).activeNetworkInfo
            return net != null && net.type == ConnectivityManager.TYPE_MOBILE && net.isConnected
        }

        /**
         * Detect whether the current network is available
         *
         */
        fun isAvailable(context: Context): Boolean {
            return isWifiAvailable(context) || (isMobileAvailable(context) && isMobileEnabled(
                context
            ))
        }

        /**
         * Determine if there is a WiFi that is available, followed by false:
         * 1. Device WiFi switch is turned off;
         * 2. The flight mode has been opened;
         * 3. The area where the device is located is not covered;
         * 4. The device is in the roaming area, and the network roaming is closed.
         *
         */
        fun isWifiAvailable(context: Context): Boolean {
            val nets = getConnectivityManager(context).allNetworkInfo
            if (nets != null) {
                for (net in nets) {
                    if (net.type == ConnectivityManager.TYPE_WIFI) {
                        return net.isAvailable
                    }
                }
            }
            return false
        }

        /**
         * It is judged that there is a mobile network that is available,
         * and it does not affect this function without affecting the device mobile network.
         * That is, even if the mobile network is turned off,
         * the mobile network may also be available (MMS and other services), that is, returns TRUE.
         *
         * The following situation is not available, will return false:
         * 1. Equipment opens flight mode;
         * 2. The area where the device is located is not covered;
         * 3. The device is in the roaming area and closes the network roaming.
         *
         */
        fun isMobileAvailable(context: Context): Boolean {
            val nets = getConnectivityManager(context).allNetworkInfo
            if (nets != null) {
                for (net in nets) {
                    if (net.type == ConnectivityManager.TYPE_MOBILE) {
                        return net.isAvailable
                    }
                }
            }
            return false
        }

        /**
         * Whether the equipment opens a cellular mobile network switch
         *
         */
        fun isMobileEnabled(context: Context): Boolean {
            try {
                val getMobileDataEnabledMethod =
                    ConnectivityManager::class.java.getDeclaredMethod("getMobileDataEnabled")
                getMobileDataEnabledMethod.isAccessible = true
                return (getMobileDataEnabledMethod.invoke(getConnectivityManager(context)) as Boolean)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return true // Reflection failed, the default is open

        }

        fun printNetworkInfo(context: Context): Boolean {
            val connectivity =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (connectivity != null) {
                val info = connectivity.allNetworkInfo
                if (info != null) {
                    for (i in info.indices) {
                        LogUtils.i(
                            TAG,
                            "NetworkInfo[" + i + "]isAvailable : " + info[i].isAvailable
                        )
                        LogUtils.i(
                            TAG,
                            "NetworkInfo[" + i + "]isConnected : " + info[i].isConnected
                        )
                        LogUtils.i(
                            TAG,
                            "NetworkInfo[" + i + "]isConnectedOrConnecting : " + info[i].isConnectedOrConnecting
                        )
                        LogUtils.i(TAG, "NetworkInfo[" + i + "]: " + info[i])
                    }
                    LogUtils.i(TAG, "\n")
                } else {
                    LogUtils.i(TAG, "getAllNetworkInfo is null")
                }
            }
            return false
        }

        /**
         * get connected network type by [ConnectivityManager]
         * such as WIFI, MOBILE, ETHERNET, BLUETOOTH, etc.
         *
         * @return [ConnectivityManager.TYPE_WIFI], [ConnectivityManager.TYPE_MOBILE],
         * [ConnectivityManager.TYPE_ETHERNET]...
         */
        fun getConnectedTypeINT(context: Context?): Int {
            val net = getConnectivityManager(context!!).activeNetworkInfo
            return net?.type ?: -1
        }

        /**
         * 获取网络连接类型
         * <p/>
         * GPRS    2G(2.5) General Packet Radia Service 114kbps
         * EDGE    2G(2.75G) Enhanced Data Rate for GSM Evolution 384kbps
         * UMTS    3G WCDMA 联通3G Universal Mobile Telecommunication System 完整的3G移动通信技术标准
         * CDMA    2G 电信 Code Division Multiple Access 码分多址
         * EVDO_0  3G (EVDO 全程 CDMA2000 1xEV-DO) Evolution - Data Only (Data Optimized) 153.6kps - 2.4mbps 属于3G
         * EVDO_A  3G 1.8mbps - 3.1mbps 属于3G过渡，3.5G
         * 1xRTT   2G CDMA2000 1xRTT (RTT - 无线电传输技术) 144kbps 2G的过渡,
         * HSDPA   3.5G 高速下行分组接入 3.5G WCDMA High Speed Downlink Packet Access 14.4mbps
         * HSUPA   3.5G High Speed Uplink Packet Access 高速上行链路分组接入 1.4 - 5.8 mbps
         * HSPA    3G (分HSDPA,HSUPA) High Speed Packet Access
         * IDEN    2G Integrated Dispatch Enhanced Networks 集成数字增强型网络 （属于2G，来自维基百科）
         * EVDO_B  3G EV-DO Rev.B 14.7Mbps 下行 3.5G
         * LTE     4G Long Term Evolution FDD-LTE 和 TDD-LTE , 3G过渡，升级版 LTE Advanced 才是4G
         * EHRPD   3G CDMA2000向LTE 4G的中间产物 Evolved High Rate Packet Data HRPD的升级
         * HSPAP   3G HSPAP 比 HSDPA 快些
         *
         * @return {@link  NetWorkType}
         */
        open fun getNetworkType(context: Context): NetWorkType {
            return when (getConnectedTypeINT(context)) {
                ConnectivityManager.TYPE_WIFI -> NetWorkType.Wifi

                ConnectivityManager.TYPE_MOBILE,
                ConnectivityManager.TYPE_MOBILE_DUN,
                ConnectivityManager.TYPE_MOBILE_HIPRI,
                ConnectivityManager.TYPE_MOBILE_MMS,
                ConnectivityManager.TYPE_MOBILE_SUPL -> {
                    when (getTelephonyManager(context).networkType) {
                        TelephonyManager.NETWORK_TYPE_GPRS,
                        TelephonyManager.NETWORK_TYPE_EDGE,
                        TelephonyManager.NETWORK_TYPE_CDMA,
                        TelephonyManager.NETWORK_TYPE_1xRTT,
                        TelephonyManager.NETWORK_TYPE_IDEN -> NetWorkType.Net2G

                        TelephonyManager.NETWORK_TYPE_UMTS,
                        TelephonyManager.NETWORK_TYPE_EVDO_0,
                        TelephonyManager.NETWORK_TYPE_EVDO_A,
                        TelephonyManager.NETWORK_TYPE_HSDPA,
                        TelephonyManager.NETWORK_TYPE_HSUPA,
                        TelephonyManager.NETWORK_TYPE_HSPA,
                        TelephonyManager.NETWORK_TYPE_EVDO_B,
                        TelephonyManager.NETWORK_TYPE_EHRPD,
                        TelephonyManager.NETWORK_TYPE_HSPAP -> NetWorkType.Net3G

                        TelephonyManager.NETWORK_TYPE_LTE -> NetWorkType.Net4G

                        TelephonyManager.NETWORK_TYPE_NR -> NetWorkType.Net5G

                        else -> NetWorkType.UnKnown
                    }
                }
                else -> NetWorkType.UnKnown
            }
        }

        fun isGpsEnabled(context: Context): Boolean {
            val locationManager: LocationManager =
                AppUtils.mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) // GPS

            val network =
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) // WLAN或移动网络(3G/2G)

            if (gps || network) {
                Log.i("demo", "GPS Ensable")
                return true
            }
            return false
        }

        fun openGPS(context: Context) {
            val gpsIntent = Intent()
            gpsIntent.setClassName(
                "com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider"
            )
            gpsIntent.addCategory("android.intent.category.ALTERNATIVE")
            gpsIntent.data = Uri.parse("custom:3")
            try {
                PendingIntent.getBroadcast(AppUtils.mContext, 0, gpsIntent, 0).send()
            } catch (e: CanceledException) {
                e.printStackTrace()
            }
        }

        fun openSetting(activity: Activity) {
            var intent: Intent? = null
            //Judging the version of the mobile phone system is API greater than 10 is 3.0
            // or above version
            if (Build.VERSION.SDK_INT > 10) {
                intent = Intent(Settings.ACTION_SETTINGS)
            } else {
                intent = Intent()
                val component = ComponentName("com.android.settings", "com.android.settings")
                intent.component = component
                intent.action = "android.intent.action.VIEW"
            }
            activity.startActivity(intent)
        }

        /**
         * Open service, listen to network changes in real time (need to configure service in
         * the manifest file)
         *
         */
        fun startNetService(context: Context) {
            //注册广播
            val intentFilter = IntentFilter()
            intentFilter.addAction(NET_BROADCAST_ACTION)
            context.registerReceiver(mReceiver, intentFilter)
            //开启服务
            val intent = Intent(context, NetworkService::class.java)
            context.bindService(intent, object : ServiceConnection {
                override fun onServiceDisconnected(name: ComponentName) {}
                override fun onServiceConnected(name: ComponentName, service: IBinder) {}
            }, Context.BIND_AUTO_CREATE)
        }

        private val mReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent != null) {
                    CURRENT_NETWORK_STATE = (intent.extras?.get(NET_STATE_NAME) as Int)
                    when (CURRENT_NETWORK_STATE) {
                        -1 -> {
                            ToastUtils.showSingleLongToast("Currently no network ")
                            NetworkUtils.setOnChangeInternet(false) // Set up network monitors
                            LogUtils.i(
                                TAG,
                                "Network change to network CURRENT_NETWORK_STATE =" + CURRENT_NETWORK_STATE
                            )
                        }
                        1 -> {
                            NetworkUtils.setOnChangeInternet(true) // Set up network monitors
                            LogUtils.i(
                                TAG,
                                "Network Change to WiFi Network CURRENT_NETWORK_STATE=" + CURRENT_NETWORK_STATE
                            )
                        }
                        2 -> {
                            NetworkUtils.setOnChangeInternet(true) // Set up network monitors
                            LogUtils.i(
                                TAG,
                                "Network change to mobile network CURRENT_NETWORK_STATE =" + CURRENT_NETWORK_STATE
                            )
                        }
                        else -> {}
                    }
                }
            }
        }

        private var mListener: OnChangeInternetListener? = null

        fun setOnChangeInternetListener(listener: OnChangeInternetListener) {
            mListener = listener
        }

        fun setOnChangeInternet(flag: Boolean) {
            mListener?.changeInternet(flag)
        }
    }

    interface OnChangeInternetListener {
        fun changeInternet(flag: Boolean)
    }


    enum class NetType(val value: Int, val desc: String) {
        None(1, "No network connection"),
        Mobile(2, "Honeycomb mobile network"),
        Wifi(4, "WiFi network"),
        Other(8, "Unknown network")
    }

    enum class NetWorkType(val value: Int, val desc: String) {
        UnKnown(-1, "Unknown network "),
        Wifi(1, "Wifi network"),
        Net2G(2, "2G network"),
        Net3G(3, "3G network"),
        Net4G(4, "4G network"),
        Net5G(5, "5G network")
    }


}