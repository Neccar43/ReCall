package com.novacodestudios.recall.domain.util

sealed class WordOrder(val orderType: OrderType) {
    class CreationDate(orderType: OrderType) : WordOrder(orderType)
    class Alphabetically(orderType: OrderType) : WordOrder(orderType)

    fun copy(orderType: OrderType): WordOrder {
        return when (this) {
            is Alphabetically -> Alphabetically(orderType)
            is CreationDate -> CreationDate(orderType)
        }
    }
}
