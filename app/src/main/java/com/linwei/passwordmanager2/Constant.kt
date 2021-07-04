package com.linwei.passwordmanager2

import android.app.Activity
import android.widget.Toast

object Constant {
    const val BASE_URL="https://ccdcapi.alipay.com"//支付宝查询银行卡所属银行
    const val DEFAULT_KEY_NAME="test_key"




    fun ToastUtil(activity: Activity,msg:String,time:Int){
        Toast.makeText(activity,msg,time).show()
    }
}