package com.linwei.passwordmanager2.bean

data class AlipayData(
    val bank: String,
    var bankName:String?,
    val cardType: String,
    var cardTypeName:String?,
    val key: String,
    var messages: List<Any>,
    var stat: String?,
    var validated: Boolean?
)