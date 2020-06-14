package com.example.robotble.searchdevices.delegate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.robotble.R
import com.example.robotble.base.ListItem
import com.example.robotble.searchdevices.delegate.item.LoadingItem
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate

class LoadingDelegate : AbsListItemAdapterDelegate<LoadingItem, ListItem, LoadingDelegate.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_list_progress, parent, false))

    override fun isForViewType(item: ListItem, items: MutableList<ListItem>, position: Int): Boolean = item is LoadingItem
    override fun onBindViewHolder(item: LoadingItem, holder: ViewHolder, payloads: MutableList<Any>) = Unit

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}