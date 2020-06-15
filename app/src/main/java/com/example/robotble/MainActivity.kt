package com.example.robotble

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.robotble.routing.RoutingFragment

class MainActivity : AppCompatActivity(R.layout.activity_main) {

  private val permissions: Array<String> = arrayOf(
    Manifest.permission.BLUETOOTH,
    Manifest.permission.BLUETOOTH_ADMIN,
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION
  )

  private val bluetoothAdapter by lazy { BluetoothAdapter.getDefaultAdapter() }
  private val receiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
      when (intent.action) {
        BluetoothAdapter.ACTION_STATE_CHANGED -> {
          when (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)) {
            BluetoothAdapter.STATE_OFF -> {
              Log.e("Bluetooth state: ", "STATE_OFF")
              startActivity(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
            }
            BluetoothAdapter.STATE_TURNING_ON -> {
              Log.e("Bluetooth state: ", "STATE_TURNING_ON")
              // noop
            }
            BluetoothAdapter.STATE_ON -> {
              Log.e("Bluetooth state: ", "STATE_ON")
              setFragment()
            }
            BluetoothAdapter.STATE_TURNING_OFF -> {
              Log.e("Bluetooth state: ", "STATE_TURNING_OFF")
              supportFragmentManager.apply {
                while (backStackEntryCount != 0) {
                  popBackStackImmediate()
                }
                findFragmentByTag(ROUTING_TAG)?.let { beginTransaction().remove(it).commitAllowingStateLoss() }
              }
            }
          }
        }
      }
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    registerReceiver(receiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
    if (savedInstanceState == null) {
      requestPermissions()
    }
  }

  override fun onDestroy() {
    unregisterReceiver(receiver)
    super.onDestroy()
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
      if (allPermissionsGranted) {
        if (bluetoothAdapter.isEnabled) {
          setFragment()
        } else {
          startActivity(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
        }
      }
    }
  }

  private fun requestPermissions() {
    requestPermissions(permissions, REQUEST_CODE)
  }

  private fun setFragment() {
    supportFragmentManager
      .beginTransaction()
      .replace(R.id.container, RoutingFragment.newInstance(), ROUTING_TAG)
      .commitAllowingStateLoss()
  }

  companion object {
    private const val REQUEST_CODE = 2358
    private const val ROUTING_TAG = "ROUTING_TAG"
  }
}