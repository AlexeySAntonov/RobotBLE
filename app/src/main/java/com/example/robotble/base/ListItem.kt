package com.example.robotble.base

interface ListItem {
  fun itemId(): Long? = null

  companion object {
    const val PAGINATION_LOADING_ITEM_ID = -1L
  }
}