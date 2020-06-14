package com.example.robotble.searchdevices

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.example.robotble.R
import com.example.robotble.base.ListItem
import com.example.robotble.searchdevices.delegate.item.DeviceItem
import com.example.robotble.searchdevices.delegate.item.LoadingItem
import kotlinx.android.synthetic.main.fragment_search_devices.*
import java.util.concurrent.atomic.AtomicBoolean

class SearchBluetoothLowEnergyDevicesFragment : Fragment(R.layout.fragment_search_devices) {

  private val handlerThread = HandlerThread("BLE scan thread").apply { start() }
  private val handler: Handler = Handler(handlerThread.looper)
  private val busy = AtomicBoolean(false)

  private val bluetoothAdapter by lazy { BluetoothAdapter.getDefaultAdapter() }

  private val bluetoothLeScanner by lazy { bluetoothAdapter.bluetoothLeScanner }

  private val devicesAdapter by lazy { DevicesAdapter() }

  private val scanCallback = object : ScanCallback() {
    override fun onScanResult(callbackType: Int, result: ScanResult) {
      activity?.runOnUiThread {
        Log.e("BLE Device: ", "${result.device}")
        addDevice(result.device, result.rssi)
      }
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    recycler.adapter = devicesAdapter
    scan.setOnClickListener { scanLeDevice() }
    back.setOnClickListener { activity?.onBackPressed() }
  }

  private fun scanLeDevice() {
    when (bluetoothAdapter.isEnabled) {
      true -> {
        if (busy.compareAndSet(false, true)) {
          scan.isEnabled = false
          scanLabel.text = "Scanning..."
          devicesAdapter.items = listOf(LoadingItem)
          handler.postDelayed({
            bluetoothLeScanner?.stopScan(scanCallback)
            activity?.runOnUiThread {
              devicesAdapter.items = devicesAdapter.items.filterIsInstance<DeviceItem>()
              scan.isEnabled = true
              scanLabel.text = "Founded devices"
            }
            busy.set(false)
          }, SCAN_PERIOD)
          bluetoothLeScanner?.startScan(scanCallback)
        }
      }
      else -> {
        bluetoothLeScanner?.stopScan(scanCallback)
        startActivity(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
      }
    }
  }

  private fun addDevice(device: BluetoothDevice, rssi: Int) {
    val currentItems = devicesAdapter.items

    val existedDeviceItem = currentItems.find { it is DeviceItem && it.device.address == device.address } as? DeviceItem
    if (existedDeviceItem != null && !existedDeviceItem.device.name.isNullOrBlank()) return
    // Update name
    if (existedDeviceItem != null) {
      devicesAdapter.items = currentItems.map { if (it == existedDeviceItem) existedDeviceItem.copy(device = device) else it }
    } else {
      devicesAdapter.items = currentItems.filterIsInstance<DeviceItem>().toMutableList<ListItem>().apply {
        add(0, DeviceItem(device = device, rssi = rssi))
        add(LoadingItem)
      }
    }
  }

  override fun onDestroyView() {
    bluetoothLeScanner?.stopScan(scanCallback)
    super.onDestroyView()
  }

  override fun onDestroy() {
    handlerThread.quitSafely()
    super.onDestroy()
  }

  companion object {
    private const val SCAN_PERIOD = 20000L

    fun newInstance() = SearchBluetoothLowEnergyDevicesFragment()
  }
}