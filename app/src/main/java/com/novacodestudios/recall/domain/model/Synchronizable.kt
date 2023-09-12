package com.novacodestudios.recall.domain.model

interface Synchronizable {
    val id:Int?
    val version:Long
}