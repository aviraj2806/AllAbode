package com.mp2.allabode.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.mp2.allabode.R
import com.mp2.allabode.databse.FlatDatabase
import com.mp2.allabode.databse.FlatEntity
import com.mp2.allabode.databse.RequestEntity
import com.mp2.allabode.databse.UserDatabase
import com.squareup.picasso.Picasso

class RequestAdapter(
    val context: Context,
    val data: List<RequestEntity>,
    val requestInterface: RequestInterface
) : RecyclerView.Adapter<RequestAdapter.RequestViewHolder>() {


    class RequestViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtName = view.findViewById<TextView>(R.id.txtRName)
        val txtEmail = view.findViewById<TextView>(R.id.txtREmail)
        val txtMobil = view.findViewById<TextView>(R.id.txtRMobile)
        val txtStatus = view.findViewById<TextView>(R.id.txtRStatus)
        val txtAdd = view.findViewById<TextView>(R.id.txtRAddress)
        val txtAccept = view.findViewById<TextView>(R.id.txtRAccept)
        val txtReject = view.findViewById<TextView>(R.id.txtRReject)
        val llStatus = view.findViewById<LinearLayout>(R.id.llRStatus)
        val txtDate = view.findViewById<TextView>(R.id.txtRReqDate)
        val txtRent = view.findViewById<TextView>(R.id.txtRRent)
        val imgR = view.findViewById<ImageView>(R.id.imgR)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_req, parent, false)
        return RequestViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        if(getSharedPrefData("type",context) == "Student"){
            holder.llStatus.visibility = View.GONE
            val flatEntity = Room.databaseBuilder(context,FlatDatabase::class.java,"flat")
                .allowMainThreadQueries().build().flatDao().getFlatByTime(data[position].flatTimeStamp)
            val userEntity = Room.databaseBuilder(context,UserDatabase::class.java,"user")
                .allowMainThreadQueries().build().userDao().getUserByMobile(flatEntity.ownerMobile)

            holder.txtName.text = userEntity.name
            holder.txtEmail.text = userEntity.email
            holder.txtMobil.text = userEntity.mobile
            Picasso.get().load(userEntity.image).error(R.drawable.avatar_light).into(holder.imgR)

            if(data[position].status == 0){
                holder.txtStatus.text = "Status : Pending"
            }else if(data[position].status == 1){
                holder.txtStatus.text = "Status : Accepted"
            }else if(data[position].status == 2){
                holder.txtStatus.text = "Status : Rejected"
            }

            holder.txtAdd.text = "Address : ${flatEntity.fullAdd}"
            holder.txtRent.text = "Rent : ₹${flatEntity.rent} "
            holder.txtDate.text = "Requested On : ${data[position].timeStamp}"
        }else{
            if(data[position].status == 0) {
                holder.llStatus.visibility = View.VISIBLE
            }else{
                holder.llStatus.visibility = View.GONE
            }
            val flatEntity = Room.databaseBuilder(context,FlatDatabase::class.java,"flat")
                .allowMainThreadQueries().build().flatDao().getFlatByTime(data[position].flatTimeStamp)
            val userEntity = Room.databaseBuilder(context,UserDatabase::class.java,"user")
                .allowMainThreadQueries().build().userDao().getUserByMobile(data[position].student)

            holder.txtName.text = userEntity.name
            holder.txtEmail.text = userEntity.email
            holder.txtMobil.text = userEntity.mobile
            Picasso.get().load(userEntity.image).error(R.drawable.avatar_light).into(holder.imgR)

            if(data[position].status == 0){
                holder.txtStatus.text = "Status : Pending"
            }else if(data[position].status == 1){
                holder.txtStatus.text = "Status : Accepted"
            }else if(data[position].status == 2){
                holder.txtStatus.text = "Status : Rejected"
            }

            holder.txtAdd.text = "Address : ${flatEntity.fullAdd}"
            holder.txtRent.text = "Rent : ₹${flatEntity.rent} "
            holder.txtDate.text = "Requested On : ${data[position].timeStamp}"

            holder.txtAccept.setOnClickListener {
                requestInterface.onAccept(data[position])
            }

            holder.txtReject.setOnClickListener {
                requestInterface.onReject(data[position])
            }
        }
    }

}

fun getSharedPrefData(key: String,context: Context): String {
    val sharedPreferences = context.getSharedPreferences(context.getString(R.string.pref_file), Context.MODE_PRIVATE)
    return sharedPreferences.getString(key, "").toString()
}

interface RequestInterface {
    fun onAccept(requestEntity: RequestEntity)
    fun onReject(requestEntity: RequestEntity)
}