package com.example.locations.activities

import android.R.attr.bitmap
import android.R.attr.visibility
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.ContextWrapper
import android.graphics.BitmapFactory
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.locations.R
import com.example.locations.models.UserInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayInputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.createFile


class HappyPlaceDetails : AppCompatActivity(), View.OnClickListener {
    private var uid = FirebaseAuth.getInstance().uid
    private var storageRef = Firebase.storage.reference
    lateinit var db: FirebaseFirestore
    lateinit var docRef: DocumentReference
    lateinit var toolbar: androidx.appcompat.widget.Toolbar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_happpy_place_details)
        toolbar = findViewById(R.id.toolbar_place_detail)
        setSupportActionBar(toolbar)


//        findViewById<TextView>(R.id.tv_progress_text).text = "Loading your information"
        var mprogressBar = Dialog(this)
        mprogressBar.setContentView(R.layout.dialog_progress)
        mprogressBar.show()
        db = Firebase.firestore
        docRef = db.collection("users").document(uid!!)
        docRef.get()
            .addOnSuccessListener { info ->
                var details = info.toObject<UserInfo>()
                findViewById<TextView>(R.id.detail_name).text = details?.name
                findViewById<TextView>(R.id.detail_username).text = details?.uname
                findViewById<TextView>(R.id.detail_date).text = details?.date
                findViewById<TextView>(R.id.detail_location).text = details?.location
                toolbar.title = details?.name
            }
            .addOnFailureListener {
                Log.e("Retrieve", it.stackTrace.toString())
                Toast.makeText(this, "Cannot retrieve information", Toast.LENGTH_SHORT).show()
            }
        var pfpRef = storageRef.child("profile_pictures/${uid}.jpg")


        val wrapper = ContextWrapper(applicationContext)
        val image_directory = "PROFILE_PICTURES"
        var localFile = wrapper.getDir(image_directory, Context.MODE_PRIVATE)
        localFile = File(localFile, "${uid}.jpg")

        pfpRef.getFile(localFile).addOnSuccessListener {
            var btmp = BitmapFactory.decodeFile(localFile.absolutePath)
            findViewById<ImageView>(R.id.detail_circular_image).setImageBitmap(btmp)
            mprogressBar.dismiss()
        }.addOnFailureListener {
            Log.e("Image download", it.message.toString())
            Toast.makeText(this, "Can't load image", Toast.LENGTH_SHORT).show()
            mprogressBar.dismiss()
        }
        findViewById<ImageView>(R.id.detail_name_edit).setOnClickListener(this)
        findViewById<ImageView>(R.id.detail_username_edit).setOnClickListener(this)
        findViewById<ImageView>(R.id.detail_date_edit).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.detail_name_edit -> {
                showDialogueRecieveString(findViewById(R.id.detail_name), "name")
            }
            R.id.detail_username_edit -> {
                showDialogueRecieveString(findViewById(R.id.detail_username), "uname")
            }
            R.id.detail_date_edit -> {
                var cal = Calendar.getInstance()
                var editer = findViewById<TextView>(R.id.detail_date)
                fun updateDateInView() {
                    var sdf = SimpleDateFormat("dd/MM/yy", Locale.FRENCH)
                    editer.setText(sdf.format(cal.time).toString())
                    docRef.update("date",sdf.format(cal.time).toString())
                        .addOnSuccessListener {
                            Toast.makeText(this,"Edit Complete",Toast.LENGTH_SHORT).show()
                        }
                }
                var dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dateOfMonth ->
                    cal.set(year, month, dateOfMonth)
                    updateDateInView()
                }

                DatePickerDialog(
                    this, dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()

            }
        }
    }

    private fun showDialogueRecieveString(view: TextView, field:String): String {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.edit_dialogue, null)
        val editText = dialogLayout.findViewById<EditText>(R.id.dialogue_edit)
        var txt = ""

        with(builder) {
            setTitle("Edit value")
            setView(dialogLayout)
        }

        var box = builder.show()
        dialogLayout.findViewById<Button>(R.id.dialogue_okay).setOnClickListener {
            txt = dialogLayout.findViewById<EditText>(R.id.dialogue_edit).text.toString()
            if(txt.isNullOrEmpty()){
                dialogLayout.findViewById<TextView>(R.id.dialog_error).visibility = View.VISIBLE
            }
            else{
                dialogLayout.findViewById<TextView>(R.id.dialog_error).visibility = View.GONE
                view.text = txt
                docRef.update(field,txt)
                    .addOnSuccessListener {
                        Toast.makeText(this,"Edit Complete",Toast.LENGTH_SHORT).show()
                    }
                box.dismiss()
            }
        }
        dialogLayout.findViewById<Button>(R.id.dialogue_cancel).setOnClickListener{
            box.dismiss()
        }

        if(txt.isNullOrEmpty()) return view.text.toString()
        else return txt
    }


    fun validText(text: CharSequence?): Boolean {
        if (text.isNullOrEmpty()) {
            Toast.makeText(this, "Please enter some text", Toast.LENGTH_SHORT)
        }
        return true
    }
}





