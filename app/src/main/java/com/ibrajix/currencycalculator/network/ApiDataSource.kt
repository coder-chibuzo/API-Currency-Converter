package com.ibrajix.currencycalculator.network

import javax.inject.Inject

class ApiDataSource @Inject constructor(private val apiService: ApiService) {

    suspend fun getAllRates(from: String, to: String, amount: Double) = apiService.getRates(from = from, to = to, amount = amount)

}