package com.example.robotble

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.robotble.routing.RoutingFragment
import com.example.robotble.searchdevices.SearchBluetoothDevicesFragment
import com.example.robotble.searchdevices.SearchBluetoothLowEnergyDevicesFragment

class MainActivity : AppCompatActivity(R.layout.activity_main) {

  private val permissions: Array<String> = arrayOf(
    Manifest.permission.BLUETOOTH,
    Manifest.permission.BLUETOOTH_ADMIN,
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION
  )

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    if (savedInstanceState == null) {
      requestPermissions()
    }
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    if (requestCode == REQUEST_CODE) {
      var allPermissionsGranted = true
      for (result in grantResults) {
        if (result != PackageManager.PERMISSION_GRANTED) {
          allPermissionsGranted = false
          requestPermissions()
          break
        }
      }
      if (allPermissionsGranted) setFragment()
    }
  }

  private fun requestPermissions() {
    requestPermissions(permissions, REQUEST_CODE)
  }

  private fun setFragment() {
    supportFragmentManager
      .beginTransaction()
      .replace(R.id.container, RoutingFragment.newInstance())
      .commitAllowingStateLoss()
  }

  companion object {
    private const val REQUEST_CODE = 2358
  }
}