package com.example.ferreteriaomiltemi.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ferreteriaomiltemi.domain.repository.AuthenticationRepository
import com.example.ferreteriaomiltemi.view.auth.AuthActivity
import com.example.ferreteriaomiltemi.view.main.MainActivity

class SplashActivity : AppCompatActivity() {
    private lateinit var repository: AuthenticationRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = AuthenticationRepository(application)
        intent = verifyUserAuth()
        startActivity(intent)
        finish()
    }

    private fun verifyUserAuth(): Intent{
        lateinit var intent: Intent
        if (repository.firebaseUser == null) intent = Intent(this, AuthActivity::class.java)
        else intent = Intent(this, MainActivity::class.java)
        return intent
    }
}