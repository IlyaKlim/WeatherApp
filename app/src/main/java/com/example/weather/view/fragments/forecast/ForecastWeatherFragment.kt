package com.example.weather.view.fragments.forecast

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather.R
import com.example.weather.databinding.FragmentForecastWeatherBinding
import com.example.weather.network.retrofit.model.WeatherModel

class ForecastWeatherFragment : Fragment() {
    private var binding: FragmentForecastWeatherBinding? = null
    private lateinit var adapter: ForecastWeatherAdapter
    private val FORECAST_WEATHER_ARGS_KEY: String = "forecast_weather_args_key"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentForecastWeatherBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.let { bundle ->
            val weatherModelList: List<WeatherModel>? =
                bundle.getParcelableArrayList<WeatherModel>(FORECAST_WEATHER_ARGS_KEY)?.toList()
            weatherModelList?.let { list ->
                adapter = ForecastWeatherAdapter(list)
                binding?.let { mBinding ->
                    mBinding.forecastWeatherRecyclerView.adapter = adapter
                    mBinding.forecastWeatherRecyclerView.layoutManager =
                        LinearLayoutManager(requireContext())

                }
            }

        }
        super.onViewCreated(view, savedInstanceState)
    }
}