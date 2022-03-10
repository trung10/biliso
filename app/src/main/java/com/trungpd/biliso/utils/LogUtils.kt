package com.trungpd.biliso.utils

class LogUtils {
    companion object {
        private var LOG_SWITCH = true
        private var LOG_TO_FILE = false
        private var LOG_TAG = "TAG"
        private var LOG_TYPE: Char = 'v' // V represent al information, W only output a waring,...
        private var LOG_SAVE_DAYS = 7


    }
}