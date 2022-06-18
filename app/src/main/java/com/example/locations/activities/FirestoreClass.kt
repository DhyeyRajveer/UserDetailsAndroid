package com.example.locations.activities

import com.example.locations.models.UserInfo
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class FirestoreClass {
    private val db = FirebaseFirestore.getInstance()

    fun registerUser(userID :String, info :UserInfo){
        db.collection(USERSCOLLECTION).document(userID)
            .set(info)
            .addOnSuccessListener {  }
    }

    companion object{
        val USERSCOLLECTION :String= "USERS"
    }
}