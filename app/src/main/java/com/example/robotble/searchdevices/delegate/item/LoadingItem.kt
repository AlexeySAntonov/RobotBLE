package com.example.robotble.searchdevices.delegate.item

import com.example.robotble.base.ListItem
import com.example.robotble.base.ListItem.Companion.PAGINATION_LOADING_ITEM_ID

object LoadingItem : ListItem {
    override fun itemId(): Long? = PAGINATION_LOADING_ITEM_ID
}