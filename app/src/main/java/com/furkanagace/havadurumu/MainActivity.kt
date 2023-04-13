@file:Suppress("DEPRECATION")

package com.furkanagace.havadurumu

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL


class MainActivity : AppCompatActivity() {
    private var cityTextView: TextView? = null
    private var temperatureTextView: TextView? = null
    private var descriptionTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cityTextView = findViewById<View>(R.id.city_textview) as TextView
        temperatureTextView = findViewById<View>(R.id.temperature_textview) as TextView
        descriptionTextView = findViewById<View>(R.id.description_textview) as TextView

        WeatherAsyncTask().execute()
    }

    @SuppressLint("StaticFieldLeak")
    private inner class WeatherAsyncTask : AsyncTask<Void?, Void?, WeatherData?>() {
        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg params: Void?): WeatherData {
            val apiKey = "fc7184aa47efaffc1c789b4cc4ce49a5"
            val city = "Van"
            val url =
                "https://api.openweathermap.org/data/2.5/weather?q=$city&units=metric&appid=$apiKey"
            val urlConnection: HttpURLConnection = URL(url).openConnection() as HttpURLConnection

            try {
                val inputString = urlConnection.inputStream.bufferedReader().readText()
                return WeatherData.fromJson(inputString)
            } finally {
                urlConnection.disconnect()
            }
        }

        @SuppressLint("SetTextI18n")
        @Deprecated("Deprecated in Java")
        override fun onPostExecute(weatherData: WeatherData?) {
            if (weatherData != null) {
                cityTextView?.text = weatherData.city
                temperatureTextView?.text = "${weatherData.temperature}°C"
                descriptionTextView?.text = weatherData.description
            } else {
                Toast.makeText(
                    this@MainActivity,
                    "Hava durumu bilgileri alınamadı.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
data class WeatherData(
    val city: String,
    val temperature: Int,
    val description: String
) {
    companion object {
        fun fromJson(json: String): WeatherData {
            val jsonObject = JSONObject(json)
            val city = jsonObject.getString("name")
            val main = jsonObject.getJSONObject("main")
            val temperature = main.getDouble("temp").toInt()
            val weather = jsonObject.getJSONArray("weather").getJSONObject(0)
            val description = weather.getString("description")
            return WeatherData(city, temperature, description)
        }
    }
}
