package com.example.weatherapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private var baseCurrency = "AUD"
    private var convertedToCurrency = "USD"
    var conversionRate = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        spinnerSetup()
        textChanged()
    }

    private fun textChanged() {
        val firstConversion: EditText = findViewById(R.id.et_firstConversion)
        val secondConversion: EditText = findViewById(R.id.et_secondConversion)

        firstConversion.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Get the amount entered by the user
                val amount = s.toString().toFloatOrNull() ?: 0f

                // Calculate the conversion rate
                val rate = amount * conversionRate

                // Set the result in the second conversion EditText
                secondConversion.setText(rate.toString())
            }

            override fun afterTextChanged(s: Editable?) {
                // Do nothing
            }
        })
    }

    @SuppressLint("WrongViewCast")
    private fun getApiResult() {
        // Get references to the EditText views for the input and output amounts
        val firstConversion: EditText = findViewById(R.id.et_firstConversion)
        val secondConversion: EditText = findViewById(R.id.et_secondConversion)

        // Check if the input amount is not empty or blank
        if (firstConversion.text.isNotEmpty() && firstConversion.text.isNotBlank()) {
            // Construct the API URL with the input and output currencies and amount
            val client = OkHttpClient().newBuilder().build()
            val request = Request.Builder()
                .url("https://api.apilayer.com/exchangerates_data/convert?to=$baseCurrency&from=$convertedToCurrency&amount=${firstConversion.text}")
                .addHeader("apikey", "0prUQRrfZv0V7hm4lHd9O23dIOmwfQLT")
                .method("GET", null)
                .build()

            // Send the API request using OkHttp
            val response = client.newCall(request).execute()

            // Extract the response body as a string and convert it to a JSONObject
            val responseString = response.body()?.string()
            val jsonObject = responseString?.let { JSONObject(it) }

            // Get the converted amount from the response and set it as the text of the output EditText view
            val convertedAmount = jsonObject?.getDouble("result")
            convertedAmount?.let {
                conversionRate = it.toFloat()
                secondConversion.setText(it.toString())
            }
        } else {
            // If the input amount is empty or blank, clear the output EditText view
            secondConversion.setText("")
        }
    }

    private fun spinnerSetup() {
        val spinner: Spinner = findViewById(R.id.spinner_firstConversion)
        val spinner2: Spinner = findViewById(R.id.spinner_secondConversion)

        // Set up the adapter for the first spinner
        ArrayAdapter.createFromResource(
            this,
            R.array.currencies,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        // Set up the adapter for the second spinner
        ArrayAdapter.createFromResource(
            this,
            R.array.currencies2,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner2.adapter = adapter
        }

        // Set up the event listener for the first spinner
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
                // Update the base currency and fetch the conversion rate
                baseCurrency = parent?.getItemAtPosition(position).toString()
                getApiResult()
            }
        })

        // Set up the event listener for the second spinner
        spinner2.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Update the converted currency and fetch the conversion rate
                convertedToCurrency = parent?.getItemAtPosition(position).toString()
                getApiResult()
            }
        })
    }

}