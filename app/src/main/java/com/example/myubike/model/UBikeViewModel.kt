package com.example.myubike.model

import android.util.Log
import androidx.lifecycle.*
import com.example.myubike.network.UBikeApi
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

class UBikeViewModel : ViewModel() {
    val _uBikeList = MutableLiveData<List<UBike>>()

    //    val uBikeList: LiveData<List<UBike>> get() = _uBikeList
    val uBikeList: LiveData<List<UBike>>
        get() = Transformations.map(_uBikeList) {
            val temp = it.filter { uBike ->
                ((!hideEmpty) || (hideEmpty && uBike.sbi > 0)) && ((!hideFaraway) || (hideFaraway && (uBike.distance ?: 0) < 500))
            }
            temp
        }

    private val _latLng = MutableLiveData<LatLng?>()
    val latLng: LiveData<LatLng?> get() = _latLng

    var hideEmpty = false
    var hideFaraway = false

    init {
        refreshUBikeData()
    }

    fun refreshUBikeData() {
        viewModelScope.launch {
            var tempList = UBikeApi.retrofitService.getUBikeData()
//                .run {
//                latLng.value?.let {
//                    val tempList = this.sortedBy { uBike ->
//                        uBike.getDistanceMeter(it)
//                    }
//                    tempList
//                }
//                this
//            }
            latLng.value?.let {
                tempList = tempList.sortedBy { uBike ->
                    uBike.getDistanceMeter(it)
                }
            }
            _uBikeList.value = tempList
        }
    }

    fun refreshLatLng(latLng: LatLng) {
        this._latLng.value = latLng

        val tempList = this.uBikeList.value
        tempList?.let {
            tempList.sortedBy { it.getDistanceMeter(latLng) }
            this._uBikeList.value = tempList
        }
    }
}