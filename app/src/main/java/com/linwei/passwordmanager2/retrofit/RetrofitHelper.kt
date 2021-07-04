package com.linwei.passwordmanager2.retrofit

import com.linwei.passwordmanager2.Constant
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {

    private val retrofit= Retrofit.Builder()
        .baseUrl(Constant.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> createretrofit(serviceClass:Class<T>):T{
        return retrofit.create(serviceClass)
    }

    inline fun <reified T> create():T {
        return createretrofit(T::class.java)
    }

}