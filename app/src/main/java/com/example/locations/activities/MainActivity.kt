package com.example.locations.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.locations.R
import com.example.locations.adaptars.main_rv
import com.example.locations.models.HappyPlaceModel
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
    companion object{
        val ADD_PLACE_ACTIVITY_RESULT_CODE = 1
        val EXTRA_PLACES_DETAILS = "extra_place_details"
        val EDIT_PLACE_REQUEST_CODE = "edit_place_details"
    }
}
//
////        val myBtn: FloatingActionButton = findViewById(R.id.fabAddHappyPlace)
////        myBtn.setOnClickListener{
////            val intent = Intent(this, AddHappyPlaceActivity::class.java)
////            getResult.launch(intent)
////        }
////        getHappyPlacesFromDB()
////    }
//
////    private val getResult =
////        registerForActivityResult(
////            ActivityResultContracts.StartActivityForResult()){
////            if(it.resultCode == Activity.RESULT_OK){
////                getHappyPlacesFromDB()
////            }else{
////                Log.e("Activity","Cancelled or Back pressed")
////            }
////        }
//
////    private fun getHappyPlacesFromDB(){
////        val dbHandler = DatabaseHandler(this)
////        val happyPlaces = dbHandler.getHappyPlaceList()
////        if(happyPlaces.size > 0){
////
////            var recycler = findViewById<RecyclerView>(R.id.rv_places_list)
////            recycler.visibility = View.VISIBLE
////            findViewById<TextView>(R.id.NoRecordsText).visibility = View.GONE
////            setUpRecyclerView(happyPlaces)
////        }else{
////            var recycler = findViewById<RecyclerView>(R.id.rv_places_list)
////            recycler.visibility = View.GONE
////            findViewById<TextView>(R.id.NoRecordsText).visibility = View.VISIBLE
////        }
//}
//
////    private fun setUpRecyclerView(happyPlaceModels: ArrayList<HappyPlaceModel>){
////        var recycler = findViewById<RecyclerView>(R.id.rv_places_list)
////        recycler.setHasFixedSize(true)
////        recycler.layoutManager = LinearLayoutManager(this)
////        var adaptar = main_rv(this, happyPlaceModels)
////        adaptar.setOnClickListener(object :main_rv.OnClickListener{
////            override fun onClick(position: Int, model: HappyPlaceModel) {
////                val intent = Intent(this@MainActivity, HappyPlaceDetails::class.java)
////                intent.putExtra(EXTRA_PLACES_DETAILS, model)
////                startActivity(intent)
////            }
////
////            override fun onEditClick(position: Int, model: HappyPlaceModel) {
////                val intent = Intent(this@MainActivity, AddHappyPlaceActivity::class.java)
////                intent.putExtra(EDIT_PLACE_REQUEST_CODE, model)
////                startActivity(intent)
////            }
////
////        })
////        recycler.adapter = adaptar
////
////    }
////
//
//
