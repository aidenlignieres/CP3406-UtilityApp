package com.example.weatherapp


//noinspection SuspiciousImport
import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate


@Suppress("DEPRECATION")
class SettingsActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        private lateinit var switchDarkMode: Switch

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_settings)

            switchDarkMode = findViewById(R.id.switch_dark_mode)
            switchDarkMode.isChecked = getDarkModePreference()

            val sharedPrefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
            sharedPrefs.registerOnSharedPreferenceChangeListener(this)

            // Add listener to toggle switch
            switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
                with(sharedPrefs.edit()) {
                    putBoolean("DarkMode", isChecked)
                    apply()
                }
            }

            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "Settings"
        }

        override fun onDestroy() {
            val sharedPrefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
            sharedPrefs.unregisterOnSharedPreferenceChangeListener(this)
            super.onDestroy()
        }

        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
            if (key == "DarkMode") {
                val isDarkMode = getDarkModePreference()
                AppCompatDelegate.setDefaultNightMode(
                    if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
                )
            }
        }

        private fun getDarkModePreference(): Boolean {
            val sharedPrefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
            return sharedPrefs.getBoolean("DarkMode", false)
        }
    }