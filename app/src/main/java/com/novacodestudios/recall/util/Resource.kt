package com.novacodestudios.recall.util

import com.novacodestudios.recall.presentation.util.UIText

sealed class Resource<T>(val data: T? = null, val message: UIText? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: UIText, data: T? = null) : Resource<T>(data, message)
    class Loading<T> : Resource<T>()
}