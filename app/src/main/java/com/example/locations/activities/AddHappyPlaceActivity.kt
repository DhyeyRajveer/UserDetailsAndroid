package com.example.locations.activities

import android.app.Activity
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.locations.R
import com.example.locations.models.HappyPlaceModel
import com.example.locations.models.UserInfo
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
//import com.google.android.libraries.places.api.Places
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import de.hdodenhof.circleimageview.CircleImageView
import java.io.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import android.app.AlertDialog as AlertDialog

class AddHappyPlaceActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private var cal = Calendar.getInstance()
    private lateinit var galleryResult: ActivityResultLauncher<Intent>
    private lateinit var cameraResult: ActivityResultLauncher<Intent>
    private lateinit var googlePlacesResults: ActivityResultLauncher<Intent>
    private lateinit var galleryIntent: Intent
    private var savedImagePath: Uri? = null
    var mLatitude: Double = 0.0
    var mLongitude: Double = 0.0
    lateinit var editer: com.google.android.material.textfield.TextInputEditText
    var storageRef = Firebase.storage.reference
    var imageRef = Firebase.storage.reference
    private var uid = FirebaseAuth.getInstance().uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_happy_place)
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_add_place)
        setSupportActionBar(toolbar)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        toolbar.setNavigationOnClickListener {
//            onBackPressed()
//        }

        dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dateOfMonth ->
            cal.set(year, month, dateOfMonth)
            updateDateInView()
        }
        updateDateInView()

        findViewById<EditText>(R.id.inputs_date_edit).setOnClickListener(this)
        findViewById<Button>(R.id.img_btn).setOnClickListener(this)
        findViewById<Button>(R.id.save_btn).setOnClickListener(this)
        findViewById<EditText>(R.id.inputs_lctn_edit).setOnClickListener(this)

        if (!Places.isInitialized()) {
            Places.initialize(this, resources.getString(R.string.google_maps_api_key))
        }


//      Activity result contracts
        galleryResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                var profilePicture: CircleImageView = findViewById(R.id.img)
                if (it.resultCode == Activity.RESULT_OK) {
                    val receivedData = it.data
                    val receivedImageURI = receivedData!!.data
                    val SelectedimageBitmap: Bitmap
                    try {
                        SelectedimageBitmap = MediaStore.Images.Media.getBitmap(
                            this@AddHappyPlaceActivity.contentResolver,
                            receivedImageURI
                        )
                        profilePicture.setImageBitmap(SelectedimageBitmap)
                        storeImageToStorage(SelectedimageBitmap)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }

        cameraResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                var profilePicture: CircleImageView = findViewById(R.id.img)
                if (it.resultCode == Activity.RESULT_OK) {
                    val receivedData = it.data!!.extras!!.get("data") as Bitmap
                    profilePicture.setImageBitmap(receivedData)
                    storeImageToStorage(receivedData)
                }
            }

        googlePlacesResults =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val place: Place = Autocomplete.getPlaceFromIntent(it.data!!)

                    findViewById<EditText>(R.id.inputs_lctn_edit).setText(place.address)
                    mLatitude = place.latLng!!.latitude
                    mLongitude = place.latLng!!.longitude
                }
            }

    }

    fun updateDateInView() {
        var sdf = SimpleDateFormat("dd/MM/yy", Locale.FRENCH)
        editer =
            findViewById(R.id.inputs_date_edit)
        editer.setText(sdf.format(cal.time).toString())
    }

    public override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.inputs_date_edit -> {
                DatePickerDialog(
                    this@AddHappyPlaceActivity, dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
            R.id.img_btn -> {
                var pictureDialog = AlertDialog.Builder(this)
                pictureDialog.setTitle("Select the source for your photo")
                var options = arrayOf("Select photo from gallery", "Click photo from camera")
                pictureDialog.setItems(options) { _, selected ->
                    when (selected) {
                        0 -> {
                            galleryPermission()
                        }
                        1 -> {
                            cameraPermission()
                        }
                    }
                }.show()

            }
            R.id.save_btn -> {
                var name =
                    findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.inputs_title_edit)
                var uname =
                    findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.inputs_dsptn_edit)
                var date =
                    findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.inputs_lctn_edit)
                var location =
                    findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.inputs_lctn_edit)
                when {
                    name.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please enter name", Toast.LENGTH_SHORT).show()
                    }
                    uname.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please enter user name", Toast.LENGTH_SHORT).show()
                    }
                    location.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please enter Location", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        val user = UserInfo(
                            name.text.toString(),
                            uname.text.toString(),
                            date.text.toString(),
                            location.text.toString()
                        )
                        saveModelToFirestore(user)
                    }
                }
            }
//            R.id.inputs_lctn_edit -> {
//                try {
//                    // These are the list of fields which we required is passed
//                    val fields = listOf(
//                        Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG,
//                        Place.Field.ADDRESS
//                    )
//                    // Start the autocomplete intent with a unique request code.
//                    val intent =
//                        Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
//                            .build(this@AddHappyPlaceActivity)
//                    googlePlacesResults.launch(intent)
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }
        }
    }

    private fun saveModelToFirestore(user: UserInfo) {
        var db = Firebase.firestore
        db.collection("users")
            .document(uid!!)
            .set(user)
            .addOnSuccessListener {
            }
            .addOnFailureListener {
                Log.e("Database", it.stackTrace.toString())
            }
    }

    private fun cameraPermission() {
        Dexter.withContext(this)
            .withPermissions(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report!!.areAllPermissionsGranted()) {
                        cameraLauncher()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    AlertDialog.Builder(this@AddHappyPlaceActivity)
                        .setMessage("We need permission to work")
                        .setPositiveButton("Go to settings") { a, b ->
                            var intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            var uri = Uri.fromParts("package", packageName, null)
                            intent.data = uri
                            startActivity(intent)
                        }
                        .setNegativeButton("Cancel") { a, _ ->
                            a.dismiss()
                        }
                        .show()
                }
            }).onSameThread().check()
    }

    private fun cameraLauncher() {
        var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraResult.launch(intent)
    }

    private fun galleryPermission() {
        Dexter.withContext(this)
            .withPermissions(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report!!.areAllPermissionsGranted()) {
                        galleryLauncher()
                    }
                }

                fun onPermissionDenied() {
                    AlertDialog.Builder(this@AddHappyPlaceActivity)
                        .setMessage("We need permissions sir!")
                        .setPositiveButton("Go to settings") { _, _ ->
                            try {
                                val intent =
                                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                val uri = Uri.fromParts("package", packageName, null)
                                intent.data = uri
                                startActivity(intent)
                            } catch (e: ActivityNotFoundException) {
                                e.printStackTrace()
                            }
                        }
                        .setNegativeButton("Cancel") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    onPermissionDenied()
                }
            }).onSameThread().check()
    }

    private fun galleryLauncher() {
        galleryIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryResult.launch(galleryIntent)
    }

    private fun storeImageToStorage(bitmap: Bitmap) {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val uid = FirebaseAuth.getInstance().uid
        imageRef = storageRef.child("profile_pictures").child("${uid}.jpg")
        var uploadTask = imageRef.putBytes(data)
        uploadTask.addOnSuccessListener {
            startActivity(Intent(this, HappyPlaceDetails::class.java))
        }
        uploadTask.addOnFailureListener {
            Log.e("upload", it.stackTrace.toString())
        }
    }
}