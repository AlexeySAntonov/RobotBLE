package com.example.robotble.searchdevices.delegate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.robotble.R
import com.example.robotble.base.ListItem
import com.example.robotble.searchdevices.delegate.item.DeviceItem
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import kotlinx.android.synthetic.main.item_device.view.*

class DeviceItemDelegate : AbsListItemAdapterDelegate<DeviceItem, ListItem, DeviceItemDelegate.ViewHolder>() {

  override fun onCreateViewHolder(parent: ViewGroup): ViewHolder =
    ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_device, parent, false))

  override fun isForViewType(item: ListItem, items: MutableList<ListItem>, position: Int): Boolean = item is DeviceItem

  override fun onBindViewHolder(item: DeviceItem, holder: ViewHolder, payloads: MutableList<Any>) = holder.bind(item)

  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(item: DeviceItem) {
      with(itemView) {
        name.text = item.device.name ?: "Undefined"
        address.text = "Address: ${item.device.address}"
        rssi.text = "rssi: ${item.rssi}"
      }
    }
  }
}