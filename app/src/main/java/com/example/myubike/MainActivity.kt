package com.example.myubike

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.myubike.network.PathApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.net.URL


class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        setupActionBarWithNavController(navController)

//        val info = packageManager.getApplicationInfo(this.packageName, PackageManager.GET_META_DATA)
//        val bundle = info.metaData
//        val appId = bundle.getString("com.google.android.geo.API_KEY")
//        val appId = "AIzaSyBK0QfQOMqRsPWkRbXnZ1EWidGbwaiwihE"
//        Thread {
//            Log.e(TAG, URL("https://maps.googleapis.com/maps/api/directions/json?origin=成都&destination=深圳&key=$appId").readText())
//        }.start()

//        MainScope().launch {
//            val data = PathApi.retrofitService.getData("花蓮秀泰", "花蓮world gym", appId)
//            Log.e(TAG, data.toString())
//        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}