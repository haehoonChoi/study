package com.example.howistagram_f16.navigation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import bolts.Task
import com.example.howistagram_f16.R
import com.example.howistagram_f16.navigation.model.ContentDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add_photo.*
import java.text.SimpleDateFormat
import java.util.*

class AddPhotoActivity : AppCompatActivity() {

    var PICK_IMAGE_FROM_ALBUM  = 0
    var storage : FirebaseStorage? = null
    var photoUri : Uri? = null
    var auth : FirebaseAuth? = null
    var firestore : FirebaseFirestore? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

        //Initiate storage
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        //Open the album
        var photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent,PICK_IMAGE_FROM_ALBUM)

        //add   image Upload event
        addPhoto_btn_upload.setOnClickListener {
            contentUpload()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_FROM_ALBUM) {

            if(resultCode == Activity.RESULT_OK) {
                // 写真選択する時写真の経路を渡す
                photoUri = data?.data
                addPhoto_image.setImageURI(photoUri)

            } else {
                // Exit if you leave album  without selecting
                finish()
            }
        }
    }
    fun contentUpload() {
        //make filename
        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageFileName = "IMAGE_" + timestamp + "_.png"

        var storageRef = storage?.reference?.child("images")?.child(imageFileName)

        // Promise Method
        storageRef?.putFile(photoUri!!)?.continueWithTask {
            return@continueWithTask storageRef.downloadUrl
        }?.addOnSuccessListener { uri->
            var contentDTO = ContentDTO()

            // Insert downloadUrl of image
            contentDTO.imageUrl = uri.toString()

            // Insert uri of user
            contentDTO.uid = auth?.currentUser?.uid

            // Insert userId
            contentDTO.userId = auth?.currentUser?.email

            // Insert content
            contentDTO.explain = addPhoto_edit_explain.text.toString()

            // Insert timestamp
            contentDTO.timestamp = System.currentTimeMillis()

            // Save to fireStore
            firestore?.collection("images") ?.document()?.set(contentDTO)
            setResult(Activity.RESULT_OK)
            finish()

        }


        // Callback Method
//        storageRef?.putFile(photoUri!!)?.addOnSuccessListener {
//            storageRef.downloadUrl.addOnSuccessListener { uri ->
//                var contentDTO = ContentDTO()
//
//                // Insert downloadUrl of image
//                contentDTO.imageUrl = uri.toString()
//
//                // Insert uri of user
//                contentDTO.uid = auth?.currentUser?.uid
//
//                // Insert userId
//                contentDTO.userId = auth?.currentUser?.email
//
//                // Insert content
//                contentDTO.explain = addPhoto_edit_explain.text.toString()
//
//                // Insert timestamp
//                contentDTO.timestamp = System.currentTimeMillis()
//
//                // Save to fireStore
//                firestore?.collection("images") ?.document()?.set(contentDTO)
//                setResult(Activity.RESULT_OK)
//                finish()
//            }
//        }

    }

}
