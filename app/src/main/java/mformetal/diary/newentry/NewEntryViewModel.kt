package mformetal.diary.newentry

import android.annotation.SuppressLint
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.location.Address
import io.reactivex.disposables.Disposable
import mformetal.diary.data.model.weather.WeatherResponse

/**
 * @author - mbpeele on 2/22/18.
 */
class NewEntryViewModel(
        private val weatherApi: GetWeather,
        private val getAddress: GetAddress) : ViewModel() {

    private val addressLiveData : MutableLiveData<Address> = MutableLiveData()
    private val weatherLiveData : MutableLiveData<WeatherResponse> = MutableLiveData()

    private var placeRequest : Disposable ?= null
    private var weatherRequest : Disposable ?= null

    override fun onCleared() {
        super.onCleared()

        weatherRequest?.dispose()
        placeRequest?.dispose()
    }

    @SuppressLint("MissingPermission")
    fun getWeather() : LiveData<WeatherResponse> {
        if (weatherLiveData.value == null) {
            weatherRequest = weatherApi.getCurrentWeather()
                    .subscribe({
                        weatherLiveData.postValue(it)
                    }, {
                        // Ignore error for now
                        it
                    })
        }

        return weatherLiveData
    }

    @SuppressLint("MissingPermission")
    fun getPlace() : LiveData<Address> {
        if (addressLiveData.value == null) {
            placeRequest = getAddress.getCurrentAddress()
                    .subscribe({
                        addressLiveData.postValue(it)
                    },  {
                        // Ignore error for now
                        it
                    })
        }

        return addressLiveData
    }
}