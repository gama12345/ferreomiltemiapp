package com.example.ferreteriaomiltemi.view

import android.util.Patterns
import java.util.regex.Pattern

class DataValidation {
    fun isNotEmpty(field: String): Boolean{
        return !field.isEmpty()
    }

    fun isValidEmail(email: String): Boolean{
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPasswordFormat(password: String): Boolean{
        return Pattern.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9]).{6,}\$", password)
    }
}