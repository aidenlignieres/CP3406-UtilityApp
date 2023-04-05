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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import kotlinx.coroutines.*


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
                println(secondConversion.setText(rate.toString()))
            }

            override fun afterTextChanged(s: Editable?) {
                // Do nothing
            }
        })
    }

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("WrongViewCast")
    private fun getApiResult() {
        // Get references to the EditText views for the input and output amounts
        val firstConversion: EditText = findViewById(R.id.et_firstConversion)

        // Check if the input amount is not empty or blank
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = OkHttpClient().newCall(Request.Builder()
                    .url("https://api.apilayer.com/exchangerates_data/convert?to=$baseCurrency&from=$convertedToCurrency&amount=${firstConversion.text}")
                    .addHeader("apikey", "0prUQRrfZv0V7hm4lHd9O23dIOmwfQLT")
                    .method("GET", null).build()).execute()
                val responseBody = response.body()?.string()
                val jsonObject = responseBody?.let { JSONObject(it) }
                print(jsonObject)
                val rate = jsonObject?.getJSONObject("rate")?.getDouble(firstConversion.toString())
                if (rate != null) {
                    conversionRate = rate.toFloat()
                }
                withContext(Dispatchers.Main) {
                    textChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
//        if (firstConversion.text.isNotEmpty() && firstConversion.text.isNotBlank()) {
//            val client = OkHttpClient().newBuilder().build()
//            val request = Request.Builder()
//                .url("https://api.apilayer.com/exchangerates_data/convert?to=$baseCurrency&from=$convertedToCurrency&amount=${firstConversion.text}")
//                .addHeader("apikey", "0prUQRrfZv0V7hm4lHd9O23dIOmwfQLT")
//                .method("GET", null)
//                .build()
//            val response = client.newCall(request).execute()
//            val jsonResponse = JSONObject(response.body()!!.string())
//            conversionRate = jsonResponse.getDouble("result").toFloat()
//        }
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
                baseCurrency = parent?.getItemAtPosition(position).toString()
                getApiResult()
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
                getApiResult()
                textChanged()
            }
        })
    }

}