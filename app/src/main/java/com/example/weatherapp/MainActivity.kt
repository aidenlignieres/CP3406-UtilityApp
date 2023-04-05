package com.example.weatherapp


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONObject


class MainActivity : AppCompatActivity() {

    var baseCurrency = "AUD"
    var convertedToCurrency = "USD"
    var conversionRate = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        spinnerSetup()
        textChanged()

    }


    @SuppressLint("WrongViewCast")
    private fun getApiResult() {
        // Get references to the EditText views for the input and output amounts
        val firstConversion: EditText = findViewById(R.id.et_firstConversion)
        val secondConversion: EditText = findViewById(R.id.ll_secondConversion)

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
            val jsonObject = JSONObject(responseString)

            // Get the converted amount from the response and set it as the text of the output EditText view
            val convertedAmount = jsonObject.getDouble("result")
            secondConversion.setText(convertedAmount.toString())
        } else {
            // If the input amount is empty or blank, clear the output EditText view
            secondConversion.setText("")
        }
    }
}