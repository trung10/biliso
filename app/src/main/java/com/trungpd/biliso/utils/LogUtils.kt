package com.trungpd.biliso.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Environment
import android.util.Log
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class LogUtils {
    companion object {
        private var LOG_SWITCH = true
        private var LOG_TO_FILE = false
        private var LOG_TAG = "TAG"
        private var LOG_TYPE: Char = 'v' // V represent al information, W only output a waring,...
        private var LOG_SAVE_DAYS = 7

        @SuppressLint("SimpleDateFormat")
        private val LOG_FORMAT = SimpleDateFormat("yyyy-MM-dd HH:mm:ss") // Log output format

        @SuppressLint("SimpleDateFormat")
        private val FILE_SUFFIX = SimpleDateFormat("yyyy-MM-dd") // Log file format

        private var LOG_FILE_PATH // Log file saves path
                : String? = null
        private var LOG_FILE_NAME // Log file saves name
                : String? = null

        fun init(context: Context) {
            LOG_FILE_NAME = "Log"
            LOG_FILE_PATH = Environment.getExternalStorageDirectory().path +
                    File.separator + context.packageName
        }

        /****************************
         * Warn
         */
        fun w(msg: Any) {
            w(LOG_TAG, msg)
        }

        fun w(tag: String, msg: Any) {
            w(tag, msg, null)
        }

        fun w(tag: String, msg: Any, tr: Throwable?) {
            LogUtils.log(tag, msg.toString(), tr, 'w')
        }

        /***************************
         * Error
         */
        fun e(msg: Any) {
            e(LOG_TAG, msg)
        }

        fun e(tag: String, msg: Any) {
            e(tag, msg, null)
        }

        fun e(tag: String, msg: Any, tr: Throwable?) {
            log(tag, msg.toString(), tr, 'e')
        }

        /***************************
         * Debug
         */
        fun d(msg: Any) {
            d(LOG_TAG, msg)
        }

        fun d(tag: String, msg: Any) { // 调试信息
            d(tag, msg, null)
        }

        fun d(tag: String, msg: Any, tr: Throwable?) {
            log(tag, msg.toString(), tr, 'd')
        }

        /****************************
         * Info
         */
        fun i(msg: Any) {
            i(LOG_TAG, msg)
        }

        fun i(tag: String, msg: Any) {
            i(tag, msg, null)
        }

        fun i(tag: String, msg: Any, tr: Throwable?) {
            log(tag, msg?.toString(), tr, 'i')
        }

        /**************************
         * Verbose
         */
        fun v(msg: Any) {
            v(LOG_TAG, msg)
        }

        fun v(tag: String, msg: Any) {
            v(tag, msg, null)
        }

        fun v(tag: String, msg: Any, tr: Throwable?) {
            log(tag, msg.toString(), tr, 'v')
        }

        private fun log(tag: String, msg: String, tr: Throwable?, level: Char) {
            if (LOG_SWITCH) {
                if ('e' == level && ('e' == LOG_TYPE || 'v' == LOG_TYPE)) { // 输出错误信息
                    Log.e(tag, msg, tr)
                } else if ('w' == level && ('w' == LOG_TYPE || 'v' == LOG_TYPE)) {
                    Log.w(tag, msg, tr)
                } else if ('d' == level && ('d' == LOG_TYPE || 'v' == LOG_TYPE)) {
                    Log.d(tag, msg, tr)
                } else if ('i' == level && ('d' == LOG_TYPE || 'v' == LOG_TYPE)) {
                    Log.i(tag, msg, tr)
                } else {
                    Log.v(tag, msg, tr)
                }
                if (LOG_TO_FILE) log2File(
                    level.toString(), tag, if (msg + tr == null) "" else """ 
                        ${Log.getStackTraceString(tr)}""".trimIndent()
                )
            }
        }

        @Synchronized
        private fun log2File(mylogtype: String, tag: String, text: String) {
            val nowtime = Date()
            val date = FILE_SUFFIX.format(nowtime)
            val dateLogContent =
                LOG_FORMAT.format(nowtime) + ":" + mylogtype + ":" + tag + ":" + text // Log output format
            val destDir = File(LOG_FILE_PATH)
            if (!destDir.exists()) {
                destDir.mkdirs()
            }
            val file = File(LOG_FILE_PATH, LOG_FILE_NAME + date)
            try {
                val filerWriter = FileWriter(file, true)
                val bufWriter = BufferedWriter(filerWriter)
                bufWriter.write(dateLogContent)
                bufWriter.newLine()
                bufWriter.close()
                filerWriter.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        fun delFile() { // Delete log files
            val needDelFiel = FILE_SUFFIX.format(LogUtils.getDateBefore())
            val file = File(LOG_FILE_PATH, needDelFiel + LOG_FILE_NAME)
            if (file.exists()) {
                file.delete()
            }
        }

        private fun getDateBefore(): Date? {
            val nowtime = Date()
            val now = Calendar.getInstance()
            now.time = nowtime
            now[Calendar.DATE] = now[Calendar.DATE] - LOG_SAVE_DAYS
            return now.time
        }
    }


}