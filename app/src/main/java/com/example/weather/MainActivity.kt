package com.example.weather

import android.os.Bundle
import com.example.weather.databinding.ActivityMainBinding
import com.example.weather.network.retrofit.WeatherApi
import io.reactivex.Observer
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.koin.android.ext.android.inject
import org.koin.androidx.scope.ScopeActivity
import org.koin.androidx.scope.activityScope
import org.koin.core.scope.Scope
import rx.Subscriber

class MainActivity : ScopeActivity() {
    private var binding:ActivityMainBinding? = null
    override val scope: Scope by activityScope()
    private val presenter:Presenter by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        presenter.getWeather(lat = 35.0,lon = 139.0).subscribe{
            val data = it
        }
        setContentView(binding?.root)
    }

}