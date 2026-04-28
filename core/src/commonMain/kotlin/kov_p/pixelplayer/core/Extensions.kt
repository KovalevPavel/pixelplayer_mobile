package kov_p.pixelplayer.core

fun Int?.orZero() = this ?: 0

fun Float?.orZero() = this ?: 0f

fun Boolean?.orFalse() = this ?: false
