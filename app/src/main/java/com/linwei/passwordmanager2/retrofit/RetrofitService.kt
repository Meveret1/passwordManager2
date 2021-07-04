package com.linwei.passwordmanager2.retrofit


import com.linwei.passwordmanager2.bean.AlipayData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitService {

    /**
     * 获取已经收藏的文章列表
     */

    @GET("/validateAndCacheCardInfo.json")
    fun getBank(
        @Query("_input_charset") charset:String,
        @Query("cardNo") carno:String,
        @Query("cardBinCheck") cardBinCheck:Boolean
    ): Call<AlipayData>


}