package com.example.howistagram_f16.navigation.util

    import com.example.howistagram_f16.navigation.model.PushDTO
    import com.google.firebase.firestore.FirebaseFirestore
    import com.google.gson.Gson
    import okhttp3.*

    import java.io.IOException

class FcmPush {

        var JSON = MediaType.parse("application/json; charset=utf-8")
        var url = "https://fcm.googleapis.com/fcm/send"
        var serverKey = "AAAAmuS4ckE:APA91bEaKzlGC1LfUHordL6On02ipXis67jd8lVIqV82kIbi-zpd07TkFG7AK-FivmTTR-YznsxAZsD0_QqHbE1YgMv2czrnWIdhyxlolNX-FylrrFaxLgXwg1gmopfzRt0KRdG4jB7z"
        var gson : Gson? = null
        var okHttpClient : OkHttpClient? = null

        // どこでも使えられるように
        companion object {
            var instance = FcmPush()
        }

    init {
        gson = Gson()
        okHttpClient = OkHttpClient()
    }
    // ＰＵＳＨメッセージ転送
    fun sendMessage (destinationUid : String, title : String , message: String) {
        FirebaseFirestore.getInstance().collection("pushtokens").document(destinationUid).get().addOnCompleteListener {
            if (it.isSuccessful) {
                var token = it?.result?.get("pushToken").toString()

                var pushDTO = PushDTO()
                pushDTO.to = token
                pushDTO.notification.title = title
                pushDTO.notification.body = message

                var body = RequestBody.create(JSON,gson?.toJson(pushDTO))
                var request = Request.Builder()
                    .addHeader("Content-Type","application/json")
                    .addHeader("Authorization", "key=" + serverKey)
                    .url(url)
                    .post(body)
                    .build()

                okHttpClient?.newCall(request)?.enqueue(object : Callback{
                    override fun onFailure(call: Call, e: IOException) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onResponse(call: Call, response: Response) {
                        // 成功
                        println(response?.body()?.string())
                    }

                })
            }
        }
    }
}