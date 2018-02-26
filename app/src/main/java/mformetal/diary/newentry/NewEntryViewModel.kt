package mformetal.diary.newentry

import android.annotation.SuppressLint
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.location.Address
import com.google.gson.Gson
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
    private val addressLiveData : MutableLiveData<Address> = MutableLiveData()
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
                    weather = weatherLiveData.value?.run {
                        Gson().toJson(this)
                    },
                    address = addressLiveData.value?.run {
                        EntryAddress.fromAddress(this)
                    })
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
    fun getPlace() : LiveData<Address> {
        if (addressLiveData.value == null) {
            addressRequest = getAddress.getCurrentAddress()
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