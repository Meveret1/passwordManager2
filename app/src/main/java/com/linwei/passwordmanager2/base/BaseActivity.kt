package com.linwei.passwordmanager2.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 *@Auther Everet
 *@Date 2021/7/4
 **/
open class BaseActivity: AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    open fun initView() {}
}