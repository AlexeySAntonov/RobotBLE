package com.example.robotble.searchdevices

import android.bluetooth.BluetoothDevice
import com.example.robotble.base.SimpleDiffAdapter
import com.example.robotble.searchdevices.delegate.DeviceItemDelegate
import com.example.robotble.searchdevices.delegate.LoadingDelegate

class DevicesAdapter(onDeviceClick: (BluetoothDevice) -> Unit) : SimpleDiffAdapter() {

    init {
        delegatesManager.apply {
            addDelegate(LoadingDelegate())
            addDelegate(DeviceItemDelegate(onDeviceClick))
        }
    }
}