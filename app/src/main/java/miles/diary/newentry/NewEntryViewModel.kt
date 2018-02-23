package miles.diary.newentry

import android.annotation.SuppressLint
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.google.android.gms.location.places.Place
import io.reactivex.disposables.Disposable
import miles.diary.data.model.weather.WeatherResponse

/**
 * @author - mbpeele on 2/22/18.
 */
class NewEntryViewModel(
        private val weatherApi: WeatherApiContract,
        private val placeDetectionContract: PlaceDetectionContract) : ViewModel() {

    private val placeLiveData: MutableLiveData<Place> = MutableLiveData()
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
    fun getPlace() : LiveData<Place> {
        if (placeLiveData.value == null) {
            placeRequest = placeDetectionContract.getCurrentPlace()
                    .subscribe({
                        placeLiveData.postValue(it)
                    },  {
                        // Ignore error for now
                        it
                    })
        }

        return placeLiveData
    }
}