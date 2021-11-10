package com.example.weather.view

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.example.weather.Presenter
import com.example.weather.R
import com.example.weather.databinding.ActivityMainBinding
import com.example.weather.network.retrofit.model.WeatherModel
import com.example.weather.view.fragments.ForecastWeatherFragment
import com.example.weather.view.fragments.TodayWeatherFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.koin.android.ext.android.inject
import org.koin.androidx.scope.ScopeActivity
import org.koin.androidx.scope.activityScope
import org.koin.core.scope.Scope
import java.util.ArrayList

class MainActivity : ScopeActivity() {
    private var binding: ActivityMainBinding? = null
    override val scope: Scope by activityScope()
    private val presenter: Presenter by inject()
    private lateinit var locationManager: LocationManager
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val TODAY_WEATHER_ARGS_KEY: String = "today_weather_args_key"
    private val FORECAST_WEATHER_ARGS_KEY: String = "forecast_weather_args_key"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissionData ->
            Log.e("hz", permissionData.toString())
            if (permissionData[Manifest.permission.ACCESS_FINE_LOCATION] == true
                && permissionData[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            ) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Toast.makeText(this, "No permission granted ", Toast.LENGTH_LONG).show()
                } else {
                    fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                        presenter.setLocation(lat = it.latitude, lon = it.longitude)
                        presenter.getWeather()
                    }
                }
            } else {
                Toast.makeText(this, "Fail", Toast.LENGTH_LONG).show()
            }
        }
        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

        presenter.getWeatherReceivedLifeData().observe(this) {
            if (it) {
                launchTodayWeatherFragment()
            }
        }

        binding?.let {
            it.bottomNavigationBar.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.today_weather -> launchTodayWeatherFragment()
                    R.id.other_days_weather -> launchForecastWeatherFragment()
                }
                return@setOnItemSelectedListener true
            }
        }
        setContentView(binding?.root)
    }

    private fun launchTodayWeatherFragment() {
        presenter.getTodayWeather()?.let {
            val bundle: Bundle = Bundle()
            bundle.putParcelable(TODAY_WEATHER_ARGS_KEY, it)
            val fragment = TodayWeatherFragment()
            fragment.arguments = bundle
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment)
                .commit()
        }

    }

    private fun launchForecastWeatherFragment(){
        presenter.getForecastWeather()?.let {
            val bundle: Bundle = Bundle()
            bundle.putParcelableArrayList(FORECAST_WEATHER_ARGS_KEY,it as ArrayList<out Parcelable>)
            val fragment = ForecastWeatherFragment()
            fragment.arguments = bundle
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment)
                .commit()
        }
    }

}
