package com.ibrajix.currencycalculator.utils

import com.ibrajix.currencycalculator.BuildConfig

class EndPoints {

    companion object {

        const val BASE_URL = "https://api.getgeoapi.com/api/v2/currency/"
        const val API_KEY = BuildConfig.API_TOKEN_CONVERT
        const val  CONVERT_URL = "convert"

    }


}