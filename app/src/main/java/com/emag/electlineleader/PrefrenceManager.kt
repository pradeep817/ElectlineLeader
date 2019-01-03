package com.emag.electlineleader

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences

class PrefrenceManager(internal var context: Context) {


    lateinit var preferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor


    companion object {

        val PREFERENCE: String = "electline_leader_preference"
        val PAYMENTSTATUS: String = "payment_status"
        val EMAIL: String = "email"
        val ISLOGIN: String = "islogin"
    }

    init {
        preferences = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE)
        editor = preferences.edit()
    }

    fun putPaymentStatus(staus: Boolean) {

        editor.putBoolean(PAYMENTSTATUS, staus)
        editor.commit()

    }

    fun setLoginStatus(staus: Boolean, email: String) {
        editor.putString(EMAIL, email)
        editor.putBoolean(ISLOGIN, staus)
        editor.commit()
    }

    val isLogin: Boolean
        get() = preferences.getBoolean(ISLOGIN, false)

    val getPaymentStatus: Boolean
        get() = preferences.getBoolean(PAYMENTSTATUS, false)

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    val getEmail: String
        get() = preferences.getString(EMAIL, null)

    fun checkLogin() {
        // Check login status
        if (!this.isLogin) {
            // user is not logged in redirect him to Login Activity
            val i = Intent(context, LoginActivty::class.java)
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

            // Add new Flag to start new Activity
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK

            // Staring Login Activity
            context.startActivity(i)
        }

    }


    fun logoutUser() {
        // Clearing all data from Shared Preferences
        editor.clear()
        editor.commit()

        // After logout redirect user to Loing Activity
        val i = Intent(context, LoginActivty::class.java)
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        // Add new Flag to start new Activity
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        // Staring Login Activity
        context.startActivity(i)
    }


}