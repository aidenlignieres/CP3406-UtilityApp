package com.example.weatherapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner


class MainActivity : AppCompatActivity() {

    val exchangeRatesLiveData: MutableLiveData<Map<String, Double>> = MutableLiveData()

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_currency_rate, menu)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        spinnerSetup()
        setupTextWatcher()
        fetchExchangeRateData()
    }

    private fun setupTextWatcher() {
        val firstConversion: EditText = findViewById(R.id.et_firstConversion)
        val secondConversion: EditText = findViewById(R.id.et_secondConversion)
        val spinner1: Spinner = findViewById(R.id.spinner_firstConversion)
        val spinner2: Spinner = findViewById(R.id.spinner_secondConversion)

        // Create a function to calculate the conversion rate and update the second conversion EditText
        fun updateSecondConversion() {
            // Get the amount entered by the user
            val amount = firstConversion.text.toString().toFloatOrNull() ?: 0f

            // Get the selected currency from the second spinner
            val selectedCurrency1 = spinner1.selectedItem.toString()
            val selectedCurrency2 = spinner2.selectedItem.toString()

            // Get the exchange rate for the selected currency
            val exchangeRate1 = exchangeRatesLiveData.value?.get(selectedCurrency1)?.toFloat() ?: 0f
            val exchangeRate2 = exchangeRatesLiveData.value?.get(selectedCurrency2)?.toFloat() ?: 0f
//                println(exchangeRate)

            // Calculate the conversion rate
            val result = amount * exchangeRate2 / exchangeRate1
//                println(rate)
//            "AUD": 1
//            usd-aud: 1.498804
//            usd-nzd: 1.595408
//            "NZD": 1.064454

            // Set the result in the second conversion EditText
            secondConversion.setText(result.toString())
        }

        // Call the updateSecondConversion() function from the onTextChanged() method of all three inputs
        firstConversion.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateSecondConversion()
            }

            override fun afterTextChanged(s: Editable?) {
                // Do nothing
            }
        })

        spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                val selectedCurrency = spinner1.selectedItem.toString()
//                fetchExchangeRateData(selectedCurrency)
//                fetchExchangeRateData()
                updateSecondConversion()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

        spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateSecondConversion()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }


    private fun fetchExchangeRateData() {
        val url =
            "https://api.freecurrencyapi.com/v1/latest?apikey=y2G19Ycrce01gU4wJhmDjP47xeT43yt4ASXhZus5&=currencies=&base_currency=USD"

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .method("GET", null)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Most common error is timeout
                e.printStackTrace()
            }

            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body()?.string()
                val jsonObject = responseData?.let { JSONObject(it) }
//                println(jsonObject)

                val ratesObject = jsonObject?.getJSONObject("data")
                val exchangeRates = mutableMapOf<String, Double>()
                ratesObject?.keys()?.forEach { currency ->
                    exchangeRates[currency] = ratesObject.getDouble(currency)
                }

                // store the exchange rates in a dictionary
                val exchangeRateDict = mutableMapOf<String, Double>()
                exchangeRates.forEach { (currency, rate) ->
                    exchangeRateDict[currency] = rate
                }

                // initialize dictionary using kotlin
                val exchangeRateDictKotlin = mutableMapOf<String, Double>().apply {
                    putAll(exchangeRateDict)
                }
//                println(exchangeRateDict)

                // update the global exchangeRates variable
                exchangeRatesLiveData.postValue(exchangeRateDictKotlin)
//
            }
        })
    }

    private fun spinnerSetup() {
        val spinner: Spinner = findViewById(R.id.spinner_firstConversion)
        val spinner2: Spinner = findViewById(R.id.spinner_secondConversion)

        ArrayAdapter.createFromResource(
            this,
            R.array.currencies,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.currencies2,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner2.adapter = adapter
        }

        spinner.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                fetchExchangeRateData()
            }
        })
    }

}