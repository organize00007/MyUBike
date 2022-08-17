package com.example.myubike.model

import com.google.android.gms.maps.model.LatLng
import java.lang.Math.*
import java.text.DecimalFormat

data class UBike(val sno: String, val sna: String, val tot: Int, val sbi: Int, val sarea: String, val mday: String, val lat: Double, val lng: Double, val ar: String, val sareaen: String, val snaen: String, val aren: String, val bemp: Int, val act: String, val srcUpdateTime: String, val updateTime: String, val infoTime: String, val infoDate: String, var distance: Int?) {
    val formatSna = this.sna.replace("YouBike2.0_", "")
}

fun UBike.getDistanceMeter(currentLatLng: LatLng): Int {
    val radius = 6371
    val dLat = toRadians(this.lat - currentLatLng.latitude)
    val dLng = toRadians(this.lng - currentLatLng.longitude)
    val a = sin(dLat / 2) * sin(dLat / 2) + cos(toRadians(currentLatLng.latitude)) * cos(toRadians(this.lat)) * sin(dLng / 2) * sin(dLng / 2)
    val c = 2 * asin(sqrt(a))
    val valueResult = radius * c
    val newFormat = DecimalFormat("####")
    this.distance = Integer.valueOf(newFormat.format(valueResult * 1000))
    return distance!!
}