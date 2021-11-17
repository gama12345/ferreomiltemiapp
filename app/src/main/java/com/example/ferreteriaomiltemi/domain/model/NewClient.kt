package com.example.ferreteriaomiltemi.domain.model

import android.os.Parcelable
import com.google.firebase.firestore.DocumentSnapshot
import java.lang.Exception

data class NewClient(val name: String,
                     val last_name: String,
                     val phone:String,
                     val email: String,
                     val password: String)
