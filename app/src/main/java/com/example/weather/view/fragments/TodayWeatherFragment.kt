package com.example.weather.view.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.weather.R
import com.example.weather.databinding.FragmentTodayWeatherBinding
import com.example.weather.network.retrofit.model.City
import com.example.weather.network.retrofit.model.WeatherModel
import kotlin.math.roundToInt

class TodayWeatherFragment : Fragment() {
    private var binding: FragmentTodayWeatherBinding? = null
    private val TODAY_WEATHER_ARGS_KEY: String = "today_weather_args_key"
    private val CURRENT_CITY_ARGS: String = "current_city_args"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTodayWeatherBinding.inflate(layoutInflater)
        return binding?.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.let {
            val data = it.getParcelable<WeatherModel>(TODAY_WEATHER_ARGS_KEY)
            val city = it.getParcelable<City>(CURRENT_CITY_ARGS)
            binding?.let { mBinding ->
                val temp = data?.main?.temp?.substringBefore(".")
                val weatherIconId = data?.weather?.get(0)?.icon ?: ""
                mBinding.weatherIcon.setImageResource(getImageId(data?.weather?.get(0)?.icon ?: ""))
                mBinding.weatherTextView.text =
                    temp + resources.getString(R.string.celsius) + " | " + data?.weather?.get(
                        0
                    )?.main
                mBinding.location.text = city?.name + "," + city?.country
            }
        }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun getImageId(imageId: String): Int {
        return when (imageId) {
            "01d" -> {
                R.drawable.ic_sunny
            }
            "02d" -> {
                R.drawable.wi_day_sunny_overcast
            }
            "03d" -> {
                R.drawable.wi_day_sunny_overcast
            }
            "04d" -> {
                R.drawable.wi_day_cloudy
            }
            "9d" -> {
                R.drawable.wi_day_rain_mix
            }
            "10d" -> {
                R.drawable.wi_day_rain
            }
            "11d" -> {
                R.drawable.wi_day_thunderstorm
            }
            "13d" -> {
                R.drawable.wi_day_snow
            }
            "50d" -> {
                R.drawable.wi_day_fog
            }
            "01n" -> {
                R.drawable.wi_night_clear
            }
            "02n" -> {
                R.drawable.wi_night_alt_partly_cloudy
            }
            "03n" -> {
                R.drawable.wi_night_alt_partly_cloudy
            }
            "04n" -> {
                R.drawable.wi_night_alt_cloudy
            }
            "10n" -> {
                R.drawable.wi_night_alt_snow
            }
            "9n" -> {
                R.drawable.wi_night_alt_rain_mix
            }
            "11n" -> {
                R.drawable.wi_night_alt_thunderstorm
            }
            "13n" -> {
                R.drawable.wi_night_alt_snow
            }
            "50n" -> {
                R.drawable.wi_night_fog
            }
            else -> R.drawable.ic_cloudy
        }
    }
}