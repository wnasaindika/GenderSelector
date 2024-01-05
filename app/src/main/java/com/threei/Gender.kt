package com.threei

sealed class Gender {
    data object  Male: Gender()
    data object Female: Gender()
}