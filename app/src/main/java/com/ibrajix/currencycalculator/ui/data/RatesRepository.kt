package com.ibrajix.currencycalculator.ui.data

import com.ibrajix.currencycalculator.network.ApiDataSource
import com.ibrajix.currencycalculator.network.ApiResponse
import com.ibrajix.currencycalculator.network.BaseDataSource
import com.ibrajix.currencycalculator.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class RatesRepository @Inject constructor(private val apiDataSource: ApiDataSource) : BaseDataSource() {

    //Using coroutines flow to get the response from
    suspend fun getConvertedData(from: String, to: String, amount: Double): Flow<Resource<ApiResponse>> {
        return flow {
            emit(safeApiCall { apiDataSource.getAllRates(from, to, amount) })
        }.flowOn(Dispatchers.IO)
    }

}