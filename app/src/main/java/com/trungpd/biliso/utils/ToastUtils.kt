package com.trungpd.biliso.utils

import android.app.Application
import android.content.Context
import android.widget.Toast

class ToastUtils {
    companion object {
        private var context: Context? = null
        private var mToast: Toast? = null


        fun showSingleLongToast(message: String){
            getSingleToast(message, Toast.LENGTH_LONG).show()
        }

        fun showSingleLongToast(resId: Int){
            getSingleToast(resId, Toast.LENGTH_LONG).show()
        }

        fun showSingleShortToast(message: String){
            getSingleToast(message, Toast.LENGTH_SHORT).show()
        }

        fun showSingleShortToast(resId: Int){
            getSingleToast(resId, Toast.LENGTH_LONG).show()
        }

        fun init(context: Application){
            this.context = context
        }

        fun getSingleToast(resId: Int, duration: Int): Toast {
            return getSingleToast(context!!.resources.getText(resId).toString(), duration)
        }

        fun getSingleToast(text: String?, duration: Int): Toast {
            if (mToast == null) {
                mToast = Toast.makeText(context, text, duration)
            } else {
                mToast!!.setText(text)
            }
            return mToast!!
        }



    }
}