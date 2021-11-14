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
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build


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
                getData()
            } else {
                launchNoLocationPermissionError()
            }
        }
        requestPermission()

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


    override fun launchTodayWeatherFragment() {
        val bundle = Bundle()
        bundle.putParcelable(TODAY_WEATHER_ARGS_KEY, presenter.getTodayWeather())
        bundle.putParcelable(CURRENT_CITY_ARGS, presenter.getCity())
        val fragment = TodayWeatherFragment()
        fragment.arguments = bundle
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment)
            .commit()

    }

    override fun launchErrorView(errorText: String, iconId: Int) {
        binding?.let {
            it.progressBar.visibility = View.INVISIBLE
            it.errorView.visibility = View.VISIBLE
            it.errorIcon.setImageResource(iconId)
            it.errorTextView.text = errorText
            it.tryAgainButton.setOnClickListener { _ ->
                it.errorView.visibility = View.INVISIBLE
                getData()
            }
        }
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

    fun launchNoLocationPermissionError() {
        binding?.let {
            it.progressBar.visibility = View.INVISIBLE
            it.errorView.visibility = View.VISIBLE
            it.errorIcon.setImageResource(R.drawable.ic_location_off)
            it.errorTextView.text = getString(R.string.no_location_permission)
            it.tryAgainButton.text = getString(R.string.grant_permission)
            it.tryAgainButton.setOnClickListener { _ ->
                it.errorView.visibility = View.INVISIBLE
                requestPermission()
            }
        }

    }

    fun isOnline(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                //for other device how are able to connect with Ethernet
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                //for check internet over Bluetooth
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        } else {
            val nwInfo = connectivityManager.activeNetworkInfo ?: return false
            return nwInfo.isConnected
        }
    }

    fun getData() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            launchNoLocationPermissionError()
        } else {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                if (isOnline()) {
                    fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                        presenter.setLocation(lat = it.latitude, lon = it.longitude)
                        presenter.getWeather()
                    }
                } else {
                    launchErrorView("No internet connection", R.drawable.ic_internet)
                }
            } else {
                launchErrorView("Location is not enabled", R.drawable.ic_location_off)
            }
        }
    }

    private fun requestPermission() {
        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
}
