package com.novacodestudios.recall.presentation.util

import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavigationItem(
    val  title:String,
    val selectedIcon:ImageVector,
    val unSelectedIcon:ImageVector,
    val hasNews:Boolean,
    val badgeCount:Int?=null,
    val route:String
)
