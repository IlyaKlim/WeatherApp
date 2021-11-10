package com.example.weather.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.weather.R
import com.example.weather.databinding.FragmentTodayWeatherBinding
import com.example.weather.network.retrofit.model.WeatherModel

class TodayWeatherFragment : Fragment() {
    private var binding:FragmentTodayWeatherBinding? = null
    private val TODAY_WEATHER_ARGS_KEY: String = "today_weather_args_key"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val data = it.getParcelable<WeatherModel>(TODAY_WEATHER_ARGS_KEY)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentTodayWeatherBinding.inflate(layoutInflater)
        return binding?.root
    }

}