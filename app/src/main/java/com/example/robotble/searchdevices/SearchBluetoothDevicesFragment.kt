package com.example.robotble.searchdevices

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.robotble.R
import com.example.robotble.base.ListItem
import com.example.robotble.searchdevices.delegate.item.DeviceItem
import com.example.robotble.searchdevices.delegate.item.LoadingItem
import kotlinx.android.synthetic.main.fragment_search_devices.*

class SearchBluetoothDevicesFragment : Fragment(R.layout.fragment_search_devices) {

  private val bluetoothAdapter by lazy { BluetoothAdapter.getDefaultAdapter() }
  private val devicesAdapter by lazy { DevicesAdapter() }

  private val receiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
      when (intent.action) {
        BluetoothDevice.ACTION_FOUND -> {
          // Discovery has found a device. Get the BluetoothDevice
          // object and its info from the Intent.
          val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
          device?.let { addDevice(it) }
        }
      }
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    recycler.adapter = devicesAdapter
    bluetoothPrepare()
    scan.setOnClickListener {
      if (bluetoothAdapter.isDiscovering) stopScan()
      else startScan()
    }
  }

  override fun onDestroyView() {
    bluetoothAdapter.cancelDiscovery()
    activity?.unregisterReceiver(receiver)
    super.onDestroyView()
  }

  private fun bluetoothPrepare() {
    if (bluetoothAdapter.isEnabled) {
      registerReceiver()
    } else {
      startActivity(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
    }
  }

  private fun startScan() {
    devicesAdapter.items = listOf(LoadingItem)
    scanLabel.text = "Scanning..."
    scan.text = "cancel scan"
    bluetoothAdapter.startDiscovery()
  }

  private fun stopScan() {
    devicesAdapter.items = devicesAdapter.items.filterIsInstance<DeviceItem>()
    scanLabel.text = "Founded devices"
    scan.text = "scan devices"
    bluetoothAdapter.cancelDiscovery()
  }

  private fun registerReceiver() {
    activity?.registerReceiver(receiver, IntentFilter(BluetoothDevice.ACTION_FOUND))
  }

  private fun addDevice(device: BluetoothDevice) {
    if (device.name.isNullOrBlank()) return
    val currentItems = devicesAdapter.items
    val currentItemsAddresses = currentItems.filterIsInstance<DeviceItem>().map { it.device.address }.toSet()
    if (currentItemsAddresses.contains(device.address)) return

    devicesAdapter.items = currentItems.filterIsInstance<DeviceItem>().toMutableList<ListItem>().apply {
      add(0, DeviceItem(device = device))
      add(LoadingItem)
    }
  }

  companion object {
    fun newInstance() = SearchBluetoothDevicesFragment()
  }
}