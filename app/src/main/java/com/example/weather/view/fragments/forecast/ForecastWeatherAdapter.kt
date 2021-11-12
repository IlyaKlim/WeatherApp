package com.example.weather.view.fragments.forecast

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.databinding.WeatherViewBinding
import com.example.weather.databinding.WeatherViewHolderBinding
import com.example.weather.network.retrofit.model.WeatherModel
import java.text.SimpleDateFormat
import java.util.*

class ForecastWeatherAdapter(private val weather: List<WeatherModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var currentDayOfTheWeek: Int = -1
    private val NEW_WEEK_VIEW_HOLDER = 0
    private val CURRENT_WEEK_VIEW_HOLDER = 1

    override fun getItemViewType(position: Int): Int {
        return when {
            position == 0 -> {
                NEW_WEEK_VIEW_HOLDER
            }
            checkIfItemInACurrentWeek(position) -> {
                CURRENT_WEEK_VIEW_HOLDER
            }
            else -> NEW_WEEK_VIEW_HOLDER
        }

    }

    inner class WeatherViewHolder(private val binding: WeatherViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setWeather(model: WeatherModel) {
            var time = model.date?.substringAfter(" ")
            time = time?.substringBeforeLast(":")
            val temp = model.main?.temp?.substringBefore(".")
            binding.temperatureView.text = "$temp℃"
            binding.weatherDescription.text = model.weather?.get(0)?.main
            binding.timeView.text = time
        }

    }

    inner class NewWeekViewHolder(private val binding: WeatherViewHolderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setData(model: WeatherModel) {
            when (getDayOfWeek(weather = model)) {
                Calendar.MONDAY -> binding.dayOfTheWeek.text = "Monday"
                Calendar.TUESDAY -> binding.dayOfTheWeek.text = "Tuesday"
                Calendar.WEDNESDAY -> binding.dayOfTheWeek.text = "Wednesday"
                Calendar.THURSDAY -> binding.dayOfTheWeek.text = "Thursday"
                Calendar.FRIDAY -> binding.dayOfTheWeek.text = "Friday"
                Calendar.SATURDAY -> binding.dayOfTheWeek.text = "Saturday"
                Calendar.SUNDAY -> binding.dayOfTheWeek.text = "Sunday"
            }
            var time = model.date?.substringAfter(" ")
            time = time?.substringBeforeLast(":")
            val temp = model.main?.temp?.substringBefore(".")
            binding.temperatureView.text = "$temp℃"
            binding.weatherDescription.text = model.weather?.get(0)?.main
            binding.timeView.text = time
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            NEW_WEEK_VIEW_HOLDER -> NewWeekViewHolder(
                WeatherViewHolderBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            CURRENT_WEEK_VIEW_HOLDER -> WeatherViewHolder(
                WeatherViewBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> return WeatherViewHolder(
                WeatherViewBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is NewWeekViewHolder) {
            holder.setData(weather[position])
        } else if (holder is WeatherViewHolder) {
            holder.setWeather(weather[position])
        }
    }

    override fun getItemCount(): Int {
        return weather.size
    }

    private fun checkIfItemInACurrentWeek(position: Int): Boolean {
        val format = SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss", Locale("Rus"))
        var date: Date? = format.parse(weather[position].date ?: "")
        val calendar: Calendar = Calendar.getInstance()
        calendar.time = date
        val currentDay: Int = calendar.get(Calendar.DAY_OF_WEEK)
        date = format.parse(weather[position - 1].date ?: "")
        calendar.time = date
        val previousDay: Int = calendar.get(Calendar.DAY_OF_WEEK)
        return (currentDay == previousDay)
    }

    private fun getDayOfWeek(weather:WeatherModel):Int{
        val format = SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss", Locale("Rus"))
        var date: Date? = format.parse(weather.date ?: "")
        val calendar: Calendar = Calendar.getInstance()
        calendar.time = date
        return calendar.get(Calendar.DAY_OF_WEEK)
    }
}