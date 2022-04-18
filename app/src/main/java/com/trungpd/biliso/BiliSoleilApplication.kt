package com.trungpd.biliso

import android.app.Activity
import android.app.Application
import android.content.Context
import com.trungpd.biliso.di.AppComponent
import com.trungpd.biliso.utils.AppUtils
import com.trungpd.biliso.utils.CrashHandler
import com.trungpd.biliso.utils.LogUtils
import com.trungpd.biliso.utils.ToastUtils
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.qualifiers.ApplicationContext

/**
 * #                                                   #
 * #                       _oo0oo_                     #
 * #                      o8888888o                    #
 * #                      88" . "88                    #
 * #                      (| -_- |)                    #
 * #                      0\  =  /0                    #
 * #                    ___/`---'\___                  #
 * #                  .' \\|     |# '.                 #
 * #                 / \\|||  :  |||# \                #
 * #                / _||||| -:- |||||- \              #
 * #               |   | \\\  -  #/ |   |              #
 * #               | \_|  ''\---/''  |_/ |             #
 * #               \  .-\__  '-'  ___/-. /             #
 * #             ___'. .'  /--.--\  `. .'___           #
 * #          ."" '<  `.___\_<|>_/___.' >' "".         #
 * #         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       #
 * #         \  \ `_.   \_ __\ /__ _/   .-` /  /       #
 * #     =====`-.____`.___ \_____/___.-`___.-'=====    #
 * #                       `=---='                     #
 * #     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   #
 * #                                                   #
 * #               佛祖保佑         永无BUG              #
 * # @soleilyoyiyi                                     #
 */

@HiltAndroidApp
class BiliApplication : Application() {

    companion object{
        private var mContext: BiliApplication? = null
    }

    private val allActivities: Set<Activity>? = null
    private val mAppComponent: AppComponent? = null

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        // mContext = this
    }

    override fun onCreate() {
        super.onCreate()
        AppUtils.init(this)
        mContext = this
        initNetwork()
        initStetho()
        initCrashHandler()
        initLog()
        initPrefs()
        initComponent()
    }

    private fun initComponent() {
        ToastUtils.init(this)
    }

    private fun initPrefs() {
        TODO("Not yet implemented")
    }

    private fun initLog() {
        LogUtils.init(this)
    }

    private fun initCrashHandler() {
        CrashHandler.getInstance().init(this)
    }

    private fun initStetho() {
        TODO("Not yet implemented")
    }

    private fun initNetwork() {

    }


}