package com.example.locations.adaptars

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.locations.R
import com.example.locations.activities.AddHappyPlaceActivity
import com.example.locations.activities.MainActivity
import com.example.locations.models.HappyPlaceModel
import de.hdodenhof.circleimageview.CircleImageView

open class main_rv(
    private val context: Context,
    var happyPlaceModels: ArrayList<HappyPlaceModel>
) : RecyclerView.Adapter<MyViewHolder>() {


    //     Basically, we create an interface that makes sure that an on click method is implemented everytime
//    Then we define a variable of the same interface type in our adapter
//    Now we create a method, setOnClickListener. This method will take an interface of the type that we created
//    and set that interface to our local interface variable
//    This local interface variable will be set as a listener to every itemView

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.happy_place_item, parent, false)
        return MyViewHolder(view)
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.placeName.text = happyPlaceModels[position].title
        holder.placeDescription.text = happyPlaceModels[position].description
        holder.placeImageView.setImageURI(Uri.parse(happyPlaceModels[position].image))
        holder.itemView.setOnClickListener() {
            if (onClickListener != null) {
                onClickListener!!.onClick(position, happyPlaceModels[position])
            }
        }

        holder.editButton.setOnClickListener() {
            if (onClickListener != null) {
                onClickListener!!.onEditClick(position, happyPlaceModels[position])
            }
        }

//        holder.editButton.setOnClickListener{
//            happyPlaceModels[position].let { itemEditButton(position ,it) }
//        }
    }


    interface OnClickListener {
        fun onClick(position: Int, model: HappyPlaceModel)
        fun onEditClick(position: Int, model: HappyPlaceModel)

    }

    fun notifyEditItem(activity: Activity, position: Int, requestCode: Int) {
        val intent = Intent(context, AddHappyPlaceActivity::class.java)
        intent.putExtra(MainActivity.EXTRA_PLACES_DETAILS, happyPlaceModels)
        activity.startActivityForResult(intent, requestCode)
        notifyItemChanged(position)
    }

    override fun getItemCount(): Int {
        return happyPlaceModels.size
    }
}


class MyViewHolder(
    itemView: View,
) : RecyclerView.ViewHolder(itemView) {
    var placeName = itemView.findViewById<TextView>(R.id.item_name)
    var placeDescription = itemView.findViewById<TextView>(R.id.item_description)
    var placeImageView = itemView.findViewById<CircleImageView>(R.id.item_image)
    var editButton = itemView.findViewById<ImageView>(R.id.editItemButton)
}