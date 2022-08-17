package com.example.myubike.model

import androidx.lifecycle.*
import com.example.myubike.network.UBikeApi
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

class UBikeViewModel : ViewModel() {
    private val _uBikeList = MutableLiveData<List<UBike>>()
    val uBikeList: LiveData<List<UBike>> get() = _uBikeList

    private val _latLng = MutableLiveData<LatLng>()
    val latLng: LiveData<LatLng> get() = _latLng
    init {
        refreshUBikeData()
    }

    fun refreshUBikeData() {
        viewModelScope.launch {
            _uBikeList.value = UBikeApi.retrofitService.getUBikeData()
        }
    }

    fun refreshLatLng(latLng: LatLng) {
        this._latLng.value = latLng
    }
}