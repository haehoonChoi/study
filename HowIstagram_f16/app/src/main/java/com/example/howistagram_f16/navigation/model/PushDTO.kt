package com.example.howistagram_f16.navigation.model

import android.app.Notification

data class PushDTO(
    var to : String? = null,
    var notification: Notification = Notification()
) {
    data class Notification(
     var body : String? = null,
     var title : String? = null
    )
}