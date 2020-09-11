package com.mp2.allabode.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.mp2.allabode.R
import com.mp2.allabode.databse.FlatEntity
import com.mp2.allabode.databse.UserDatabase
import com.mp2.allabode.databse.UserEntity
import com.squareup.picasso.Picasso

class PropertyAdapter(val context:Context, val data:List<FlatEntity>,val onStudentRequest: OnStudentRequest): RecyclerView.Adapter<PropertyAdapter.PropertyViewHolder>() {

    val sharedPreferences = context.getSharedPreferences(context.getString(R.string.pref_file), Context.MODE_PRIVATE)

    class PropertyViewHolder(view: View): RecyclerView.ViewHolder(view){
        val txtName = view.findViewById<TextView>(R.id.txtAPName)
        val txtEmail = view.findViewById<TextView>(R.id.txtAPEmail)
        val txtMobile = view.findViewById<TextView>(R.id.txtAPMobile)
        val txtAdd = view.findViewById<TextView>(R.id.txtAPAddress)
        val imgAP = view.findViewById<ImageView>(R.id.imgAP)
        val txtListed = view.findViewById<TextView>(R.id.txtAPListed)
        val txtRequest = view.findViewById<TextView>(R.id.txtAPRequest)
        val txtRent = view.findViewById<TextView>(R.id.txtAPRent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_property,parent,false)
        return PropertyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: PropertyViewHolder, position: Int) {

        val userEntity = Room.databaseBuilder(context,UserDatabase::class.java,"user")
            .allowMainThreadQueries().build().userDao().getUserByMobile(data[position].ownerMobile)

        if(getSharedPrefData("type") == "Student"){
            holder.txtRequest.visibility = View.VISIBLE
        }else{
            holder.txtRequest.visibility = View.GONE
        }

        holder.txtName.text = userEntity.name
        holder.txtMobile.text = userEntity.mobile
        holder.txtEmail.text = userEntity.email
        holder.txtAdd.text = data[position].fullAdd
        holder.txtListed.text = "Listed On : ${data[position].timeStamp} "
        holder.txtRent.text = "Rent : ₹${data[position].rent} "
        Picasso.get().load(data[position].image).error(R.drawable.avatar).into(holder.imgAP)

        holder.txtRequest.setOnClickListener {
            onStudentRequest.onStudentRequest(data[position])
        }

        holder.imgAP.setOnClickListener {
            onStudentRequest.onImageExpand(data[position].image)
        }
    }

    fun getSharedPrefData(key: String) : String{
        return sharedPreferences.getString(key,"").toString()
    }

    interface OnStudentRequest{
        fun onStudentRequest(flatEntity: FlatEntity)
        fun onImageExpand(url: String)
    }
}