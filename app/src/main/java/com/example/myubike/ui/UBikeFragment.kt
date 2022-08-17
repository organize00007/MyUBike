package com.example.myubike.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.example.myubike.R
import com.example.myubike.adapter.UBikeListAdapter
import com.example.myubike.databinding.FragmentUBikeBinding
import com.example.myubike.model.UBikeViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class UBikeFragment : Fragment() {

    companion object {
        private const val TAG = "UBikeFragment"
    }

    private val viewModel: UBikeViewModel by activityViewModels()
    private var _binding: FragmentUBikeBinding? = null
    private val binding get() = _binding!!

    private lateinit var permissionsRequestLauncher: ActivityResultLauncher<String>

    private lateinit var googleMap: GoogleMap
    private lateinit var adapter: UBikeListAdapter
    private val markerList: ArrayList<Marker?> = arrayListOf()

    @SuppressLint("MissingPermission")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentUBikeBinding.inflate(inflater, container, false)

        permissionsRequestLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { permissions ->
            if (permissions) {
                googleMap.isMyLocationEnabled = true
                moveCamera()
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = UBikeListAdapter(this)
        binding.recyclerView.adapter = adapter
        viewModel.uBikeList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshUBikeData()
            Toast.makeText(requireContext(), "已刷新資料", Toast.LENGTH_SHORT)
                .show()
            binding.swipeRefresh.isRefreshing = false
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_u_bike) as SupportMapFragment
        mapFragment.getMapAsync { gm ->
            googleMap = gm
            googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            enableMyLocation()
            setMarker()
        }
    }

    private fun setMarker() {
        viewModel.uBikeList.observe(viewLifecycleOwner) {
            it.forEachIndexed { index, uBike ->
                val options = MarkerOptions().position(LatLng(uBike.lat, uBike.lng))
                    .title(uBike.formatSna)
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
            moveCamera()
        } else {
            permissionsRequestLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun moveCamera() {
        moveToMyLocation()
    }

    fun moveCameraToSelectedLocation(position: Int) {
        val uBike = viewModel.uBikeList.value?.get(position)
        if (uBike != null) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(uBike.lat, uBike.lng), 17f))
            markerList[position]?.showInfoWindow()
        } else {
            moveToMyLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun moveToMyLocation() {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
        fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val location = task.result
                if (location != null) {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 15f))
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
}