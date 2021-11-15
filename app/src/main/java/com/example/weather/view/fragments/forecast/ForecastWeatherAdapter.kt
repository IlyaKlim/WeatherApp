package com.example.weather.view.fragments.forecast

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.databinding.WeatherViewBinding
import com.example.weather.databinding.WeatherViewHolderBinding
import com.example.weather.network.retrofit.model.WeatherModel
import java.text.SimpleDateFormat
import java.util.*

class ForecastWeatherAdapter(private val weather: List<WeatherModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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
            binding.weatherIcon.setImageResource(getImageId(model.weather?.get(0)?.icon ?: ""))
        }

    }

    inner class NewWeekViewHolder(private val binding: WeatherViewHolderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
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
            binding.weatherIcon.setImageResource(getImageId(model.weather?.get(0)?.icon ?: ""))
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
        calendar.time = date?: Date()
        val currentDay: Int = calendar.get(Calendar.DAY_OF_WEEK)
        date = format.parse(weather[position - 1].date ?: "")
        calendar.time = date?: Date()
        val previousDay: Int = calendar.get(Calendar.DAY_OF_WEEK)
        return (currentDay == previousDay)
    }

    private fun getDayOfWeek(weather: WeatherModel): Int {
        val format = SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss", Locale("Rus"))
        val date: Date? = format.parse(weather.date ?: "")
        val calendar: Calendar = Calendar.getInstance()
        calendar.time = date?:Date()
        return calendar.get(Calendar.DAY_OF_WEEK)
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
                R.drawable.wi_night_alt_rain
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
