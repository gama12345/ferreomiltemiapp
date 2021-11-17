package com.example.ferreteriaomiltemi.domain.model

import android.os.Parcelable
import com.google.firebase.firestore.DocumentSnapshot
import java.lang.Exception

data class Client(val id: String,
                  val name: String,
                  val last_name: String,
                  val phone:String,
                  var email: String,
                  val password: String)
