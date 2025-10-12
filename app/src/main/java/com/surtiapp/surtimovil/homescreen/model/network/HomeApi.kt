package com.surtiapp.surtimovil.core.homescreen.model.network

import com.surtiapp.surtimovil.homescreen.model.dto.HomeResponse
import retrofit2.Response
import retrofit2.http.GET

interface HomeApi {
    @GET("c8b664fc2a7c89474ea5a9393c0e53a4/raw/8d445be823e3e2265a82291347604cb0d5a02691/gistfile1.json")
    suspend fun getHome(): Response<HomeResponse>
}