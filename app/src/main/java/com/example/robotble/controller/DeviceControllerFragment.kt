package com.example.robotble.controller

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.robotble.R
import kotlinx.android.synthetic.main.fragment_device_controller.*
import java.io.IOException
import java.util.*

class DeviceControllerFragment : Fragment(R.layout.fragment_device_controller) {

  private lateinit var device: BluetoothDevice

  private val handlerThread = HandlerThread("BLE connection thread").apply { start() }
  private val handler: Handler = Handler(handlerThread.looper)

  private var btSocket: BluetoothSocket? = null
  private val myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    retainInstance = true
    establishConnection()
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    forward.setOnClickListener { sendSignal("forward") }
    backward.setOnClickListener { sendSignal("backward") }
  }

  override fun onDestroy() {
    disconnect()
    handlerThread.quitSafely()
    super.onDestroy()
  }

  private fun sendSignal(signal: String) {
    btSocket?.outputStream?.write(signal.toByteArray())
  }

  private fun establishConnection() {
    handler.post {
      try {
        btSocket = device.createInsecureRfcommSocketToServiceRecord(myUUID)
        btSocket?.connect()
        activity?.runOnUiThread {
          Toast.makeText(activity, "Bluetooth device connection established", Toast.LENGTH_LONG).show()
        }
      } catch (e: Exception) {
        disconnect()
        Log.e("BT Socket connection: ", "Failed with exception: $e")
        activity?.runOnUiThread {
          Toast.makeText(activity, "Bluetooth device connection failed", Toast.LENGTH_LONG).show()
          activity?.onBackPressed()
        }
      }
    }
  }

  private fun disconnect() {
    try {
      btSocket?.close()
    } catch (e: IOException) {
      Log.e("BT Socket connection: ", "Socket close failed")
    }
  }

  companion object {
    fun newInstance(device: BluetoothDevice) = DeviceControllerFragment().apply {
      this.device = device
    }
  }
}