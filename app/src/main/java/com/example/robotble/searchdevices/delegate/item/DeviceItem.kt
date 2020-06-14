package com.example.robotble.searchdevices.delegate.item

import android.bluetooth.BluetoothDevice
import com.example.robotble.base.ListItem

/**
 * @property device - Identifies the remote device
 * @property rssi - The RSSI value for the remote device as reported by the Bluetooth hardware. 0 if no RSSI value is available.
 */
data class DeviceItem(
  val device: BluetoothDevice,
  val rssi: Int = 0
) : ListItem {

  override fun itemId(): Long? = device.address.replace(":","").toLong(16)
}