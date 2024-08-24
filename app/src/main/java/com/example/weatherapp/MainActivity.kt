package com.example.weatherapp

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.weatherapp.databinding.ActivityMainBinding
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//e4b2d425c57c998e2879f9a68e70e013

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        fetchWeatherData("Nagpur")
        SearchCity()
    }

    private fun SearchCity() {
        val searchView=binding.searchView
        searchView.setOnQueryTextListener(object:SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?):Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchWeatherData(cityName:String) {
        val retrofit=Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val response=retrofit.getWeatherData(cityName, "e4b2d425c57c998e2879f9a68e70e013", "metric")
        response.enqueue(object :Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody= response.body()
                if(response.isSuccessful && responseBody!=null){
                    val temperature=responseBody.main.temp.toString()
                    val humidity=responseBody.main.humidity
                    val windSpeed=responseBody.wind.speed
                    val sunRise=responseBody.sys.sunrise.toLong()
                    val sunSet=responseBody.sys.sunset.toLong()
                    val seaLevel=responseBody.main.pressure
                    val condition=responseBody.weather.firstOrNull()?.main?:"unknown"
                    val maxTemp=responseBody.main.temp_max
                    val minTemp=responseBody.main.temp_min

                    binding.temp.text= "$temperature °C"
                    binding.weather.text=condition
                    binding.maxTemp.text="Max Temp:$maxTemp °C"
                    binding.minTemp.text="Min Temp:$minTemp °C"
                    binding.humidity.text="$humidity %"
                    binding.windspeed.text="$windSpeed m/s"
                    binding.sunrise.text="${time(sunRise)}"
                    binding.sunset.text="${time(sunSet)}"
                    binding.sea.text="$seaLevel hPa"
                    binding.condition.text=condition
                    binding.day.text=dayName(System.currentTimeMillis())
                    binding.date.text=date()
                    binding.cityName.text="$cityName"


                    changeImagesAccordingToWeatherCondition(condition)

                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })


    }

    private fun changeImagesAccordingToWeatherCondition(conditions: String) {
        when(conditions){

            "Haze","Partly Clouds","Clouds","Overcast","Mist","Foggy" -> {
                binding.root.setBackgroundResource(R.drawable.cloudback)
                binding.lottieAnimationView4.setAnimation(R.raw.cloudy)
            }

            "Clear Sky","Sunny","Clear"->{
             binding.root.setBackgroundResource(R.drawable.sunnyback)
                binding.lottieAnimationView4.setAnimation(R.raw.sunny)
            }
            "Light Rain","Drizzle","Moderate Rain","Showers","Heavy Rain","Rain"->{
                binding.root.setBackgroundResource(R.drawable.rainback)
                binding.lottieAnimationView4.setAnimation(R.raw.rainy)
            }

            "Light Snow","Moderate Snow","Heavy Snow","Blizzard","Snow"->{
                binding.root.setBackgroundResource(R.drawable.snowback)
                binding.lottieAnimationView4.setAnimation(R.raw.snowy)
            }

            else ->{
                binding.root.setBackgroundResource(R.drawable.sunnyback)
                binding.lottieAnimationView4.setAnimation(R.raw.sunny)
            }

        }
        binding.lottieAnimationView4.playAnimation()

    }

    private fun date(): String {
        val sdf= SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun time(timestamp: Long): String {
        val sdf= SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp*1000))
    }

    fun dayName(timestamp:Long):String{
        val sdf= SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date())
    }
}