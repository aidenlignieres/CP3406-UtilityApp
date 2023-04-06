package com.example.weatherapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private var baseCurrency = "AUD"
    private var convertedToCurrency = "USD"
    var conversionRate = 0f
    private lateinit var resultTextView: TextView

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
                val amount = firstConversion.toString().toFloatOrNull() ?: 0f

                // Calculate the conversion rate
                val rate = amount * conversionRate

                // Set the result in the second conversion EditText
                secondConversion.setText(rate.toString())
                println(secondConversion.setText(rate.toString()))
            }

            override fun afterTextChanged(s: Editable?) {
                // Do nothing
            }
        })
    }

    private fun fetchExchangeRateData() {
        // Get references to the EditText views for the input and output amounts
        val firstConversion: EditText = findViewById(R.id.et_firstConversion)
        val spinner: Spinner = findViewById(R.id.spinner_firstConversion)
        val spinner2: Spinner = findViewById(R.id.spinner_secondConversion)

        val url =
            "https://api.apilayer.com/exchangerates_data/convert?to=$spinner&from=$spinner2&amount=$firstConversion"
        val apiKey = "0prUQRrfZv0V7hm4lHd9O23dIOmwfQLT"

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .addHeader("apikey", apiKey)
            .method("GET", null)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    // Response is successful, you can access the JSON data here
                    val jsonData = response.body()?.string()

                    runOnUiThread {
                        // Update the UI on the main thread
                        resultTextView.text = "JSON Data: $jsonData"
//                        println(resultTextView.text)
                    }
                } else {
                    // Handle unsuccessful response
                    println("Request failed with status code: ${response.code()}")
                }
            }
        })
    }

//    @OptIn(DelicateCoroutinesApi::class)
//    @SuppressLint("WrongViewCast")
//    private fun getApiResult() {
//        // Get references to the EditText views for the input and output amounts
//        val firstConversion: EditText = findViewById(R.id.et_firstConversion)
//
//        // Check if the input amount is not empty or blank
//        GlobalScope.launch(Dispatchers.IO) {
//            try {
//                val startTime = System.currentTimeMillis()
//                val response = OkHttpClient().newCall(
//                    Request.Builder()
//                        .url("https://api.apilayer.com/exchangerates_data/convert?to=$baseCurrency&from=$convertedToCurrency&amount=${firstConversion.text}")
//                        .addHeader("apikey", "0prUQRrfZv0V7hm4lHd9O23dIOmwfQLT")
//                        .method("GET", null)
//                        .build()).execute()
//                val endTime = System.currentTimeMillis()
//                println("API call took ${endTime - startTime} ms")
//                Thread.sleep(2000)
//                val responseBody = response.body()?.string()
//                val jsonObject = responseBody?.let { JSONObject(it) }
//                println("jsonObject")
//                print(jsonObject)
//                val rate = jsonObject?.getJSONObject("rate")?.getDouble(firstConversion.toString())
//                if (rate != null) {
//                    conversionRate = rate.toFloat()
//                }
//                withContext(Dispatchers.Main) {
//                    textChanged()
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }

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
                baseCurrency = parent?.getItemAtPosition(position).toString()
                fetchExchangeRateData()
                textChanged()
            }
        })

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
                convertedToCurrency = parent?.getItemAtPosition(position).toString()
                fetchExchangeRateData()
                textChanged()
            }
        })
    }

}