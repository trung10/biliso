package com.trungpd.biliso.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.FileUtils
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.io.Writer
import java.lang.reflect.Field
import java.text.SimpleDateFormat
import java.util.*

class CrashHandler: Thread.UncaughtExceptionHandler{

    companion object {
        private var INSTANCE: CrashHandler? = null

        fun getInstance(): CrashHandler {
            if (INSTANCE == null)
                INSTANCE = CrashHandler()
            return INSTANCE!!
        }

        // used to storage device information and exception information
        private val infos: Map<String, String> = HashMap()
    }

    private constructor()

    private var mDefaultHandler: Thread.UncaughtExceptionHandler? = null

    private lateinit var mContext: Context

    fun init(context: Context){
        this.mContext = context
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(this)
    }



    override fun uncaughtException(thread: Thread, ex: Throwable) {

    }

    private fun handleException(ex: Throwable?): Boolean{
        if (ex == null)
            return false
        collectDeviceInfo(mContext)
        saveCrashInfo2File(ex)
        return true
    }

    fun collectDeviceInfo(context: Context){
        try {
            val pm = context.packageManager
            val pi = pm.getPackageInfo(context.packageName, PackageManager.GET_ACTIVITIES)

            if (pi != null){
                val versionName = if (pi.versionName == null) "null" else pi.versionName
                val versionCode = pi.versionCode.toString() + ""
                (infos as HashMap).put("versionName", versionName)
                infos.put("versionCode", versionCode)
            }
        } catch (e: PackageManager.NameNotFoundException) {
            LogUtils.e(
                "CrashHandleran.NameNotFoundException---> error occured when collect package info",
                e
            )
        }

        val fields = Build::class.java.declaredFields
        for (f: Field in fields) {
            try {
                f.isAccessible = true
                (infos as HashMap).put(f.name, f.get(null).toString())
            } catch (e: Exception) {
                LogUtils.e(
                    "CrashHandler.NameNotFoundException---> an error occured when collect crash info",
                    e
                )
            }
        }
    }

    fun saveCrashInfo2File(ex: Throwable) {
        val sb = StringBuilder()
        sb.append(
            """
                <<<<<<<<<<<<<<<----START---->>>>>>>>>>>>>>>>>>
                
                """.trimIndent()
        )
        for ((key, value) in infos) {
            sb.append("$key=$value\n")
        }
        val writer: Writer = StringWriter()
        val printWriter = PrintWriter(writer)
        ex.printStackTrace(printWriter)
        var cause = ex.cause
        while (cause != null) {
            cause.printStackTrace(printWriter)
            cause = cause.cause
        }
        printWriter.close()
        val result = writer.toString()
        sb.append(result)
        sb.append(
            """
                <<<<<<<<<<<<<<<<<----END---->>>>>>>>>>>>>>>>>>
               
                """.trimIndent()
        )
        LogUtils.e(sb.toString())
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val fileName = format.format(Date()) + ".txt"
        val file: File = File(FileUtils.createRootPath(mContext).toString() + "/log/" + fileName)
        FileUtils.createFile(file)
        FileUtils.writeFile(file.absolutePath, sb.toString())
    }
}