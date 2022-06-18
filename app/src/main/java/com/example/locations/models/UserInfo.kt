package com.example.locations.models

import java.io.Serializable

data class UserInfo(
    val name: String = "",
    val uname: String = "",
    val date: String= "",
    val location: String=""
): Serializable
{
    constructor(): this("","","","")
}