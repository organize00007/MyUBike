package com.example.myubike.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.myubike.R
import com.example.myubike.adapter.MyInfoWindowAdapter
import com.example.myubike.adapter.UBikeListAdapter
import com.example.myubike.databinding.FragmentUBikeBinding
import com.example.myubike.model.Polyline
import com.example.myubike.model.UBikeViewModel
import com.example.myubike.network.PathApi
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.launch


class UBikeFragment : Fragment() {

    companion object {
        private const val TAG = "UBikeFragment"
        private const val MARKER_ZOOM = 16f
    }

    private val viewModel: UBikeViewModel by activityViewModels()
    private var _binding: FragmentUBikeBinding? = null
    private val binding get() = _binding!!

    private lateinit var permissionsRequestLauncher: ActivityResultLauncher<String>

    private lateinit var googleMap: GoogleMap
    private lateinit var adapter: UBikeListAdapter
    private val markerList: ArrayList<Marker?> = arrayListOf()
    private var lastLocation: LatLng? = null
    private var lastPath: com.google.android.gms.maps.model.Polyline? = null

    @SuppressLint("MissingPermission")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentUBikeBinding.inflate(inflater, container, false)

        (activity as AppCompatActivity).supportActionBar?.hide()

        permissionsRequestLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { permissions ->
            if (permissions) {
                googleMap.isMyLocationEnabled = true
                getMyLocation()
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = UBikeListAdapter(this)
        binding.viewModel = viewModel
        binding.recyclerView.adapter = adapter
        viewModel.uBikeList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            binding.recyclerView.scrollToPosition(0)
        }
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshUBikeData()
            Toast.makeText(requireContext(), "已刷新資料", Toast.LENGTH_SHORT)
                .show()
            binding.swipeRefresh.isRefreshing = false
        }
        binding.etSearch.uBikeFragment = this
        binding.cbEmpty.setOnCheckedChangeListener { _, isChecked ->
            viewModel.hideEmpty = isChecked
            viewModel.refreshUBikeData()
        }
        binding.cbFaraway.setOnCheckedChangeListener { _, isChecked ->
            viewModel.hideFaraway = isChecked
            viewModel.refreshUBikeData()
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_u_bike) as SupportMapFragment
        mapFragment.getMapAsync { gm ->
            googleMap = gm
            googleMap.setInfoWindowAdapter(MyInfoWindowAdapter(requireContext()))
            googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            enableMyLocation()
            setMarker()

            googleMap.setOnMarkerClickListener {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(it.position, MARKER_ZOOM))
                it.showInfoWindow()
                true
            }
        }
    }

    private fun setMarker() {
        viewModel.uBikeList.observe(viewLifecycleOwner) {
            googleMap.clear()
            markerList.clear()
            it.forEachIndexed { index, uBike ->
                val options = MarkerOptions().position(LatLng(uBike.lat, uBike.lng))
                    .title(uBike.formatSna)
                    .icon(BitmapDescriptorFactory.fromBitmap(createCircleBitmap(uBike.sbi)))
                    .snippet("${uBike.sbi} 可借")
                markerList.add(googleMap.addMarker(options))
            }
        }
//        viewModel.uBikeList.value?.let { uBikeData ->
//            uBikeData.forEachIndexed { index, uBike ->
//                val options = MarkerOptions().position(LatLng(uBike.lat, uBike.lng))
//                    .title(uBike.formatSna)
//                    .snippet("${uBike.sbi} 可借")
//            }
//        }
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.isMyLocationEnabled = true
            getMyLocation()
        } else {
            permissionsRequestLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    fun moveCameraToSelectedLocation(position: Int) {
        if (position >= 0 && position < markerList.size) {
            markerList[position]?.let {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(it.position, MARKER_ZOOM))
                it.showInfoWindow()
            }
        } else {
            getMyLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getMyLocation(withAnimation: Boolean = true) {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
        fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val location = task.result
                if (location != null) {
                    lastLocation = LatLng(location.latitude, location.longitude)
                    moveToMyLocation(withAnimation)
                } else {
                    Toast.makeText(requireContext(), "取得位置發生錯誤", Toast.LENGTH_LONG)
                        .show()
                }
            } else {
                Toast.makeText(requireContext(), "取得位置發生錯誤", Toast.LENGTH_LONG)
                    .show()
                Log.e(TAG, "取得位置發生錯誤: ${task.exception.toString()}")
            }
        }
    }

    fun moveToMyLocation(withAnimation: Boolean = true) {
        lastLocation?.let {
            viewModel.refreshLatLng(it)
            if (withAnimation) googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 15f))
            else googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 15f))
        }
        (requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(binding.etSearch.windowToken, 0)
        binding.mapUBike.requestFocus()
    }

    private fun createCircleBitmap(num: Int): Bitmap {
        val totWidth = 76
        val totHeight = 85
        val width = 76
        val height = 76
        val borderWidth = 3
        val bitmap = Bitmap.createBitmap(totWidth, totHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val circlePaint = Paint().apply {
            color = if (num == 0) Color.RED else ContextCompat.getColor(requireContext(), R.color.ubike_circle_background_green_color)
        }
        val circleBorderPaint = Paint().apply {
            color = Color.WHITE
        }
        // 畫圓形
        canvas.drawCircle(height.toFloat() / 2, height.toFloat() / 2, height.toFloat() / 2, circleBorderPaint)
        canvas.drawCircle(height.toFloat() / 2, height.toFloat() / 2, height.toFloat() / 2 - borderWidth, circlePaint)
        // 設定三角形路徑作為指標
        val trianglePath = Path()
        trianglePath.moveTo(totWidth.toFloat() / 4, height.toFloat() / 2)
        trianglePath.lineTo(totWidth.toFloat() * 3 / 4, height.toFloat() / 2)
        trianglePath.lineTo((totWidth.toFloat() / 2), totHeight.toFloat())
        trianglePath.lineTo(totWidth.toFloat() / 4, height.toFloat() / 2)
        trianglePath.close()
        canvas.drawPath(trianglePath, circlePaint)

        val numPaint = Paint().apply {
            color = Color.WHITE
            textSize = 40f
            textScaleX = 1f
        }
        val numBounds = Rect()
        numPaint.getTextBounds(num.toString(), 0, num.toString().length, numBounds)
        canvas.drawText(num.toString(), (width - numBounds.width()).toFloat() / 2, height - ((height - numBounds.height()).toFloat() / 2), numPaint)
        return bitmap
    }

    fun showPath(position: Int, mode: String) {
        moveCameraToSelectedLocation(position)
        val appId = "AIzaSyBK0QfQOMqRsPWkRbXnZ1EWidGbwaiwihE"
        if (position >= 0 && position < markerList.size) {
            markerList[position]?.let { marker ->
                lastLocation?.let {
                    lifecycleScope.launch {
                        val start = "${it.latitude},${it.longitude}"
                        val end = "${marker.position.latitude},${marker.position.longitude}"
                        val data = PathApi.retrofitService.getData(start, end, mode, appId)
                        Log.e("UBikeFragment", data.toString())

                        val polylineOptions = PolylineOptions().color(Color.RED)
                        polylineOptions.add(LatLng(data.routes[0].legs[0].steps[0].startLocation.lat, data.routes[0].legs[0].steps[0].startLocation.lng))
                        data.routes[0].legs[0].steps.forEach {
                            polylineOptions.add(LatLng(it.endLocation.lat, it.endLocation.lng))
                        }
                        if (lastPath != null) lastPath!!.remove()
                        lastPath = googleMap.addPolyline(polylineOptions)

//
//                        var line = ""
//                        data.routes[0].legs[0].steps.forEach {
//                            line += it.polyline
//                        }
//
//                        val decodedPath = PolyUtil.decode(line)
//                        val lineOptions = PolylineOptions().apply {
//                            addAll(decodedPath)
//                            color(Color.RED)
//                            jointType(JointType.ROUND)
//                            width(10f)
//                        }
//                        googleMap.addPolyline(lineOptions)
                    }
                }
            }
        }
    }
}