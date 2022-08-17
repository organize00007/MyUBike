package com.example.myubike.model

import com.squareup.moshi.Json

data class GoogleMapPath(@Json(name = "geocoded_waypoints") val geocodedWaypoints: List<GeocodedWaypoints>, val routes: List<Route>)

data class GeocodedWaypoints(@Json(name = "geocoder_status") val geocoderStatus: String, @Json(name = "place_id") val placeId: String, val types: List<String>)
data class Route(val bounds: Bounds, val copyrights: String, val legs: List<Leg>, @Json(name = "overview_polyline") val overviewPolyline: OverviewPolyline, val summary: String)

data class Bounds(val northeast: Location, val southwest: Location)
data class Location(val lat: Double, val lng: Double)

data class Leg(val distance: Distance, val duration: Duration, @Json(name = "end_address") val endAddress: String?, @Json(name = "end_location") val endLocation: Location
               , @Json(name = "start_address") val startAddress: String?, @Json(name = "start_location") val startLocation: Location, val steps: List<Step>)
data class Distance(val text: String, val value: Int)
data class Duration(val text: String, val value: Int)
data class Step(val distance: Distance, val duration: Duration, @Json(name = "end_address") val endAddress: String?, @Json(name = "end_location") val endLocation: Location
                , @Json(name = "start_address") val startAddress: String?, @Json(name = "start_location") val startLocation: Location, @Json(name = "html_instructions") val htmlInstructions: String, val polyline: Polyline, @Json(name = "travel_mode") val travelMode: String)
data class Polyline(val points: String)
data class OverviewPolyline(val points: String)