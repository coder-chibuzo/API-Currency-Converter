package com.ibrajix.currencycalculator.ui.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import com.hbb20.countrypicker.dialog.launchCountryPickerDialog
import com.hbb20.countrypicker.models.CPCountry
import com.ibrajix.currencycalculator.R
import com.ibrajix.currencycalculator.databinding.ActivityMainBinding
import com.ibrajix.currencycalculator.network.Rates
import com.ibrajix.currencycalculator.ui.viewModel.RatesViewModel
import com.ibrajix.currencycalculator.utils.Resource
import com.ibrajix.currencycalculator.utils.Utility
import com.ibrajix.currencycalculator.utils.withColor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var selectedItem1: String? = "AFN"
    private var selectedItem2: String? = "AFN"

    private val ratesViewModel: RatesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utility.makeStatusBarTransparent(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView(){
        setUpClickLister()
        getConvertedValue()
        setUpCountryPicker()
    }


    private fun setUpClickLister(){

        //on click btn
        binding.btnConvert.setOnClickListener {

            //check if the input is empty
            val numberToConvert = binding.etFirstCurrency.text.toString()

            if(numberToConvert.isEmpty() || numberToConvert == "0"){
                Snackbar.make(binding.mainLayout,"Input a value in the first text field, result will be shown in the second text field", Snackbar.LENGTH_LONG)
                    .withColor(ContextCompat.getColor(this, R.color.dark_red))
                    .setTextColor(ContextCompat.getColor(this, R.color.white))
                    .show()
            }

            //check if internet is available
            else if (!Utility.isNetworkAvailable(this)){
                Snackbar.make(binding.mainLayout,"You are not connected to the internet", Snackbar.LENGTH_LONG)
                    .withColor(ContextCompat.getColor(this, R.color.dark_red))
                    .setTextColor(ContextCompat.getColor(this, R.color.white))
                    .show()
            }

            //carry on and convert the value
            else{
                doConversion()
            }

        }

    }

    private fun doConversion(){

        //hide keyboard
        Utility.hideKeyboard(this)

        //make progress bar visible
        binding.prgLoading.visibility = View.VISIBLE

        //make button invisible
        binding.btnConvert.visibility = View.GONE

        //Get the data inputed
        val from = selectedItem1.toString()
        val to = selectedItem2.toString()
        val amount = binding.etFirstCurrency.text.toString().toDouble()

        //do the conversion
        ratesViewModel.getConvertedData(from, to, amount)

    }

    private fun getConvertedValue(){

        ratesViewModel.data.observe(this, androidx.lifecycle.Observer {result ->

            when(result.status){

                Resource.Status.SUCCESS -> {

                    if (result.data?.status == "success"){

                        val map: Map<String, Rates>

                        map = result.data.rates

                        map.keys.forEach {

                            val rateForAmount = map[it]?.rate_for_amount

                            ratesViewModel.convertedRate.value = rateForAmount

                            //format the result obtained e.g 1000 = 1,000
                            val formattedString = String.format("%,.2f", ratesViewModel.convertedRate.value)

                            //set the value in the second edit text field
                            binding.etSecondCurrency.setText(formattedString)

                        }


                        //stop progress bar
                        binding.prgLoading.visibility = View.GONE
                        //show button
                        binding.btnConvert.visibility = View.VISIBLE
                    }
                    else if(result.data?.status == "fail"){
                        val layout = binding.mainLayout
                        Snackbar.make(layout,"Ooops! something went wrong, Try again", Snackbar.LENGTH_LONG)
                            .withColor(ContextCompat.getColor(this, R.color.dark_red))
                            .setTextColor(ContextCompat.getColor(this, R.color.white))
                            .show()

                        //stop progress bar
                        binding.prgLoading.visibility = View.GONE
                        //show button
                        binding.btnConvert.visibility = View.VISIBLE
                    }
                }
                Resource.Status.ERROR -> {

                    val layout = binding.mainLayout
                    Snackbar.make(layout,  "Oopps! Something went wrong, Try again", Snackbar.LENGTH_LONG)
                        .withColor(ContextCompat.getColor(this, R.color.dark_red))
                        .setTextColor(ContextCompat.getColor(this, R.color.white))
                        .show()
                    //stop progress bar
                    binding.prgLoading.visibility = View.GONE
                    //show button
                    binding.btnConvert.visibility = View.VISIBLE
                }

                Resource.Status.LOADING -> {
                    //stop progress bar
                    binding.prgLoading.visibility = View.VISIBLE
                    //show button
                    binding.btnConvert.visibility = View.GONE
                }
            }
        })

    }


    private fun setUpCountryPicker() {

        //first country picker selector
       binding.firstCountryPicker.cpViewHelper.selectedCountry.observe(this) { firstSelectedCountry ->

           //set first text view
           binding.txtFirstCurrency.text = firstSelectedCountry?.currencyCode

           selectedItem1 = firstSelectedCountry?.currencyCode

       }

        //second country picker selector
        binding.secondCountryPicker.cpViewHelper.selectedCountry.observe(this){ secondSelectedCountry ->

            //set second text view to selected country
            binding.txtSecondCurrency.text = secondSelectedCountry?.currencyCode

            selectedItem2 = secondSelectedCountry?.currencyCode

        }

    }

}