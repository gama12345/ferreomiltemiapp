package com.example.ferreteriaomiltemi.domain.repository

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import java.util.Base64
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class AuthenticationRepository (application: Application){
    private val auth = FirebaseAuth.getInstance()
    var firebaseUser = MutableLiveData<FirebaseUser>()
    private val application = application

    init{
        firebaseUser.postValue(auth.currentUser)
    }

    suspend fun register(email: String, password: String): FirebaseUser? {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val encodedPass = Base64.getEncoder().encodeToString(password.toByteArray())
            var res: FirebaseUser? = null
            try {
                auth.createUserWithEmailAndPassword(email, encodedPass)
                        .addOnCompleteListener { task -> if (task.isSuccessful) res = auth.currentUser }
                        .await()
            } catch (e: Exception) {
                Toast.makeText(application, "El email ya se encuentra en uso", Toast.LENGTH_SHORT).show()
            }
            return res
        } else {
            val toastMsg = "Su dispositivo no cuenta con la versón minima para ejecutar la app"
            Toast.makeText(application, toastMsg, Toast.LENGTH_SHORT).show()
            return null
        }
    }

    suspend fun login(email: String, password: String){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val encodedPass = Base64.getEncoder().encodeToString(password.toByteArray())
            auth.signInWithEmailAndPassword(email, encodedPass).addOnCompleteListener{task ->
                when(task.isSuccessful){
                    true -> {
                        val clientRepo = ClientRepository(application)
                        GlobalScope.launch(Dispatchers.Main) {
                            if (!clientRepo.isClientRegistered(email)) {
                                firebaseUser.postValue(auth.currentUser)
                            } else {
                                logout()
                                Toast.makeText(application, "Email o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    false -> Toast.makeText(application, "Email o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            val toastMsg = "Su dispositivo no cuenta con la versón minima para ejecutar la app"
            Toast.makeText(application, toastMsg, Toast.LENGTH_SHORT).show()
        }
    }

    fun logout(){
        auth.signOut()
        firebaseUser.postValue(auth.currentUser)
    }

    suspend fun updateEmail(newEmail: String): Boolean{
        var res = false
        try {
            auth.currentUser
                    ?.updateEmail(newEmail)
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            res = true
                            firebaseUser.postValue(auth.currentUser)
                        }
                    }
                    ?.await()
        }catch(e: FirebaseAuthException){
            var errorMsg = ""
            when(e.errorCode){
                "ERROR_EMAIL_ALREADY_IN_USE" -> errorMsg = "Este email ya se encuentra en uso por otra cuenta"
                "ERROR_REQUIRES_RECENT_LOGIN" -> errorMsg = "Debe iniciar sesión nuevamente antes de validar esta operación"
            }
            Toast.makeText(application, errorMsg, Toast.LENGTH_SHORT).show()
        }
        return res
    }

    suspend fun updatePassword(newPassword: String): Boolean{
        var res = false
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val encodedPass = Base64.getEncoder().encodeToString(newPassword.toByteArray())
            try{
                auth.currentUser
                        ?.updatePassword(encodedPass)
                        ?.addOnCompleteListener { task ->
                            if(task.isSuccessful){
                                res = true
                                firebaseUser.postValue(auth.currentUser)
                            }
                        }
                        ?.await()
            }catch(e: FirebaseAuthException){
                var errorMsg = ""
                when(e.errorCode){
                    "ERROR_REQUIRES_RECENT_LOGIN" -> errorMsg = "Debe iniciar sesión nuevamente antes de validar esta operación"
                }
                Toast.makeText(application, errorMsg, Toast.LENGTH_SHORT).show()
            }
        } else {
            val toastMsg = "Su dispositivo no cuenta con la versón minima para ejecutar la app"
            Toast.makeText(application, toastMsg, Toast.LENGTH_SHORT).show()
        }
        return res
    }
}