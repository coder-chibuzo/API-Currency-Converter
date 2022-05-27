package com.ibrajix.currencycalculator.ui.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ibrajix.currencycalculator.network.ApiResponse
import com.ibrajix.currencycalculator.ui.data.RatesRepository
import com.ibrajix.currencycalculator.utils.Resource
import com.ibrajix.currencycalculator.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RatesViewModel @Inject constructor(private val ratesRepository: RatesRepository) : ViewModel() {

    //cached
    private val _data = SingleLiveEvent<Resource<ApiResponse>>()


    //public
    val data  =  _data
    val convertedRate = MutableLiveData<Double>()


    //Public function to get the result of conversion
    fun getConvertedData(from: String, to: String, amount: Double) {
        viewModelScope.launch {
            ratesRepository.getConvertedData(from, to, amount).collect {
                data.value = it
            }
        }
    }

}