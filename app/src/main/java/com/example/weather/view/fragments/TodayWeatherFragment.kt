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
                when (weatherIconId) {
                    "01n" -> {
                        mBinding.weatherIcon.setImageResource(R.drawable.ic_sunny)
                    }
                    "02n" -> {
                        mBinding.weatherIcon.setImageResource(R.drawable.ic_cloudy)
                    }
                }
                mBinding.weatherTextView.text =
                    temp + resources.getString(R.string.celsius) + " | " + data?.weather?.get(
                        0
                    )?.main
                mBinding.location.text = city?.name + "," + city?.country
            }
        }
        super.onViewCreated(view, savedInstanceState)
    }
}