package com.example.myubike.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.example.myubike.databinding.InfoWindowCustomBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class MyInfoWindowAdapter(context: Context): GoogleMap.InfoWindowAdapter {

    private var contentView: InfoWindowCustomBinding
    init {
        contentView = InfoWindowCustomBinding.inflate(LayoutInflater.from(context))
    }

    override fun getInfoContents(p0: Marker): View? {
        contentView.tvInfoWindowName.text = p0.title
        contentView.tvInfoWindowDistance.text = ""
        contentView.tvInfoWindowSbi.text = p0.snippet
        return contentView.root
    }

    override fun getInfoWindow(p0: Marker): View? {
        return null
    }
}