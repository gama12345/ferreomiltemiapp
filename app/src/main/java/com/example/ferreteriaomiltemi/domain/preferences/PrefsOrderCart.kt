package com.example.ferreteriaomiltemi.domain.preferences

import android.content.Context
import android.content.SharedPreferences
import com.example.ferreteriaomiltemi.domain.model.Product

class PrefsOrderCart(context: Context) {
    val PREFS_NAME = "Carrito de compra"
    val SHARED_NAME = "carrito"
    var prefsOrderCart: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var orderCart: String
        get() = prefsOrderCart.getString(SHARED_NAME, null) ?: "[]"
        set(value) { prefsOrderCart.edit().putString(SHARED_NAME, value).apply() }
}