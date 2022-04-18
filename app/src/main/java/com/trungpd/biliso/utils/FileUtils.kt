package com.trungpd.biliso.utils

import android.content.Context
import android.os.Environment
import java.io.*

class FileUtils {

    companion object {

        fun createRootPath(context: Context): String? {
            var cacheRootPath: String? = ""
            cacheRootPath = if (isSdCardAvailable()) {
                // /sdcard/Android/data/<application package>/cache
                context.externalCacheDir!!.path
            } else {
                // /data/data/<application package>/cache
                context.cacheDir.path
            }
            return cacheRootPath
        }

        fun isSdCardAvailable(): Boolean {
            return Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
        }

        fun createDir(dirPath: String?): String? {
            try {
                val file = File(dirPath)
                if (file.parentFile.exists()) {
                    LogUtils.i("----- Create a folder " + file.absolutePath)
                    file.mkdir()
                    return file.absolutePath
                } else {
                    createDir(file.parentFile.absolutePath)
                    LogUtils.i("----- Create a folder " + file.absolutePath)
                    file.mkdir()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return dirPath
        }

        fun createFile(file: File): String? {
            try {
                if (file.parentFile.exists()) {
                    LogUtils.i("----- create a file " + file.absolutePath)
                    file.createNewFile()
                    return file.absolutePath
                } else {
                    createDir(file.parentFile.absolutePath)
                    file.createNewFile()
                    LogUtils.i("----- create a file " + file.absolutePath)
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return ""
        }

        fun writeFile(filePathAndName: String?, fileContent: String) {
            try {
                val outStream: OutputStream = FileOutputStream(filePathAndName)
                val out = OutputStreamWriter(outStream)
                out.write(fileContent)
                out.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        fun fileChannelCopy(src: File?, desc: File?) {
            var fi: FileInputStream? = null
            var fo: FileOutputStream? = null
            try {
                fi = FileInputStream(src)
                fo = FileOutputStream(desc)
                val `in` = fi.channel //Get the corresponding file channel
                val out = fo.channel //Get the corresponding file channel
                `in`.transferTo(0, `in`.size(), out) //Connect two channels and read from the IN channel, then write an OUT channel
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                try {
                    fo?.close()
                    fi?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        fun openAssetFile(context: Context, fileName: String): InputStream? {
            val am = context.assets
            var `is`: InputStream? = null
            try {
                `is` = am.open(fileName)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return `is`
        }
    }
}