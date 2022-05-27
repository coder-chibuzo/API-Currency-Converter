package com.ibrajix.currencycalculator.network

import com.ibrajix.currencycalculator.utils.EndPoints
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET(EndPoints.CONVERT_URL)
    suspend fun getRates(
        @Query("api_key") access_key: String = EndPoints.API_KEY,
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("amount") amount: Double
    ) : Response<ApiResponse>

}