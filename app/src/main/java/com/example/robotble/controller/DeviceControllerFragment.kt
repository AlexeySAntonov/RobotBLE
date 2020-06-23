package com.example.robotble.controller

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.robotble.R
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.fragment_device_controller.*
import java.io.IOException
import java.nio.charset.Charset
import java.util.*
import kotlin.concurrent.thread

class DeviceControllerFragment : Fragment(R.layout.fragment_device_controller) {

  private lateinit var device: BluetoothDevice

  private val connectionHandlerThread = HandlerThread("BLE connection thread").apply { start() }
  private val connectionHandler: Handler = Handler(connectionHandlerThread.looper)

  private var messageThread: Thread? = null

  private var btSocket: BluetoothSocket? = null
  private val myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    retainInstance = true
    establishConnection()
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    forward.setOnTouchWithActionListener { sendSignal(FORWARD) }
    forwardLeft.setOnTouchWithActionListener { sendSignal(FORWARD_LEFT) }
    forwardRight.setOnTouchWithActionListener { sendSignal(FORWARD_RIGHT) }
    backward.setOnTouchWithActionListener { sendSignal(BACKWARD) }
    backwardLeft.setOnTouchWithActionListener { sendSignal(BACKWARD_LEFT) }
    backwardRight.setOnTouchWithActionListener { sendSignal(BACKWARD_RIGHT) }
    stop.setOnClickListener { sendSignal(STOP) }
  }

  override fun onDestroy() {
    disconnect()
    connectionHandlerThread.quitSafely()
    super.onDestroy()
  }

  private fun sendSignal(signal: Int) {
    try {
      btSocket?.outputStream?.write(signal)
    } catch (e: Exception) {
      onFail("BT Send signal", e)
    }
  }

  private fun establishConnection() {
    connectionHandler.post {
      try {
        btSocket = device.createInsecureRfcommSocketToServiceRecord(myUUID)
        btSocket?.connect()
        activity?.runOnUiThread {
          Toast.makeText(activity, "Bluetooth device connection established", Toast.LENGTH_LONG).show()
        }
        listenSocket()
        connectionHandlerThread.quitSafely()
      } catch (e: Exception) {
        onFail("BT Establish connection", e)
      }
    }
  }

  private fun listenSocket() {
    messageThread = thread {
      while (messageThread != null) {
        try {
          Thread.sleep(1000L)

          val bytesAvailable = btSocket?.inputStream?.available()
          if (bytesAvailable ?: 0 > 0) {
            var message = ""
            while (btSocket?.inputStream?.available() ?: 0 > 0) {
              message += btSocket?.inputStream?.read()?.toChar()
            }
            Log.w("BT Message: ", message)
            displayReport(message)
          } else {
            Log.w("BT Message: ", "No bytes available")
          }
        } catch (e: IOException) {
          messageThread?.interrupt()
          messageThread = null
          onFail("BT Read message from socket", e)
        } catch (ignore: InterruptedException) {
        }
      }
    }
  }

  private fun displayReport(message: String) {
    activity?.runOnUiThread {
      try {
        report.text = "Robot report: $message"
      } catch (e: Exception) {
        onFail("BT Display report", e)
      }
    }
  }

  private fun onFail(tag: String, e: Exception) {
    Log.e("$tag: ", "Failed with exception: $e")
    activity?.runOnUiThread {
      try {
        Toast.makeText(activity, "Bluetooth device connection failed", Toast.LENGTH_LONG).show()
        activity?.onBackPressed()
      } catch (e: Exception) {
        Log.e("BT Socket activity call: ", "Failed with exception: $e")
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

  @SuppressLint("ClickableViewAccessibility")
  private fun Button.setOnTouchWithActionListener(action: () -> Unit) {
    this.setOnTouchListener { v, event ->
      when (event.action) {
        MotionEvent.ACTION_DOWN -> {
          v.isPressed = true
          action.invoke()
        }
        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
          v.isPressed = false
          sendSignal(STOP)
        }
      }
      true
    }
  }

  companion object {
    const val STOP = 0
    const val AUTO = 1
    const val FORWARD = 100
    const val FORWARD_LEFT = 101
    const val FORWARD_RIGHT = 102
    const val BACKWARD = 103
    const val BACKWARD_LEFT = 104
    const val BACKWARD_RIGHT = 105

    fun newInstance(device: BluetoothDevice) = DeviceControllerFragment().apply {
      this.device = device
    }
  }
}