package com.example.robotble.searchdevices

import com.example.robotble.base.SimpleDiffAdapter
import com.example.robotble.searchdevices.delegate.DeviceItemDelegate
import com.example.robotble.searchdevices.delegate.LoadingDelegate

class DevicesAdapter : SimpleDiffAdapter() {

    init {
        delegatesManager.apply {
            addDelegate(LoadingDelegate())
            addDelegate(DeviceItemDelegate())
        }
    }
}