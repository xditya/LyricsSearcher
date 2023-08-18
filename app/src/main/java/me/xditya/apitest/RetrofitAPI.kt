package me.xditya.apitest

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface RetrofitAPI {
    @GET("lyrics")
    fun getData(@QueryMap params:Map<String, String>): Call<DataModel?>?
}