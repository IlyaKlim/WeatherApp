package com.example.weather.view.activities

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.example.weather.presenter.Presenter
import com.example.weather.R
import com.example.weather.databinding.ActivityMainBinding
import com.example.weather.view.fragments.forecast.ForecastWeatherFragment
import com.example.weather.view.fragments.TodayWeatherFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.koin.android.ext.android.inject
import org.koin.androidx.scope.ScopeActivity
import org.koin.androidx.scope.activityScope
import org.koin.core.scope.Scope
import java.util.ArrayList
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.example.weather.network.retrofit.model.City
import com.example.weather.network.retrofit.model.WeatherModel
import com.example.weather.presenter.ViewController
import java.lang.Exception


class MainActivity : ScopeActivity(), ViewController {
    private var binding: ActivityMainBinding? = null
    override val scope: Scope by activityScope()
    private val presenter: Presenter by inject()
    private lateinit var locationManager: LocationManager
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val TODAY_WEATHER_ARGS_KEY: String = "today_weather_args_key"
    private val FORECAST_WEATHER_ARGS_KEY: String = "forecast_weather_args_key"
    private val CURRENT_CITY_ARGS: String = "current_city_args"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.attachView(this)
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
                    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                            presenter.setLocation(lat = it.latitude, lon = it.longitude)
                            presenter.getWeather()
                        }
                    } else {
                        buildAlertMessageNoGps()
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
                    R.id.today_weather -> {
                        launchTodayWeatherFragment()
                        it.toolBar.title = "Today"
                    }
                    R.id.other_days_weather -> {
                        launchForecastWeatherFragment()
                        it.toolBar.title = presenter.getCity()?.name
                    }
                }
                return@setOnItemSelectedListener true
            }
        }
        setContentView(binding?.root)
    }

    override fun onRestart() {
        if (ActivityCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
            presenter.setLocation(lat = it.latitude, lon = it.longitude)
            presenter.getWeather()
        }

        super.onRestart()
    }


    override fun launchTodayWeatherFragment() {
        val bundle = Bundle()
        bundle.putParcelable(TODAY_WEATHER_ARGS_KEY, presenter.getTodayWeather())
        bundle.putParcelable(CURRENT_CITY_ARGS, presenter.getCity())
        val fragment = TodayWeatherFragment()
        fragment.arguments = bundle
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment)
            .commit()

    }

    private fun launchForecastWeatherFragment() {
        presenter.getForecastWeather()?.let {
            val bundle = Bundle()
            bundle.putParcelableArrayList(
                FORECAST_WEATHER_ARGS_KEY,
                it as ArrayList<out Parcelable>
            )
            val fragment = ForecastWeatherFragment()
            fragment.arguments = bundle
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment)
                .commit()
        }
    }

    private fun buildAlertMessageNoGps() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id -> startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
            .setNegativeButton("No",
                DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
        val alert: AlertDialog = builder.create()
        alert.show()
    }

    override fun loadOnError(e: String) {
        TODO("Not yet implemented")
    }

    override fun enableProgress(isEnabled: Boolean) {
        when (isEnabled) {
            true -> {
                binding?.fragmentContainer?.visibility = View.INVISIBLE
                binding?.progressBar?.visibility = View.VISIBLE
            }
            false -> {
                binding?.fragmentContainer?.visibility = View.VISIBLE
                binding?.progressBar?.visibility = View.GONE
            }
        }
    }


}
