package mformetal.diary.newentry

import android.annotation.SuppressLint
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.Disposable
import io.realm.Realm
import mformetal.diary.data.model.realm.Entry
import mformetal.diary.data.model.realm.EntryAddress
import mformetal.diary.data.model.weather.WeatherResponse
import org.threeten.bp.Instant

/**
 * @author - mbpeele on 2/22/18.
 */
class NewEntryViewModel(
        private val getWeather: GetWeather,
        private val getAddress: GetAddress) : ViewModel() {

    private val realm = Realm.getDefaultInstance()
    private val addressLiveData : MutableLiveData<EntryAddress> = MutableLiveData()
    private val weatherLiveData : MutableLiveData<WeatherResponse> = MutableLiveData()

    private var addressRequest: Disposable ?= null
    private var weatherRequest : Disposable ?= null

    override fun onCleared() {
        super.onCleared()

        weatherRequest?.dispose()
        addressRequest?.dispose()
        realm.close()
    }

    fun saveEntry(bodyInput: String) : LiveData<Unit> {
        val realmLiveData = MutableLiveData<Unit>()

        realm.executeTransactionAsync(Realm.Transaction { realm ->
            val entry = Entry(createdAtSeconds = Instant.now().epochSecond,
                    body = bodyInput,
                    uri = null,
                    weather = weatherLiveData.value?.oneLineTemperatureString,
                    address = addressLiveData.value)
            realm.insert(entry)
        }, Realm.Transaction.OnSuccess {
            realmLiveData.postValue(Unit)
        })

        return realmLiveData
    }

    @SuppressLint("MissingPermission")
    fun getWeather() : LiveData<WeatherResponse> {
        if (weatherLiveData.value == null) {
            weatherRequest = getWeather.getCurrentWeather()
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
    fun getPlace() : LiveData<EntryAddress> {
        if (addressLiveData.value == null) {
            addressRequest = getAddress.getCurrentAddress()
                    .subscribe({
                        val entryAddress = EntryAddress.fromAddress(it)
                        addressLiveData.postValue(entryAddress)
                    },  {
                        // Ignore error for now
                        it
                    })
        }

        return addressLiveData
    }
}