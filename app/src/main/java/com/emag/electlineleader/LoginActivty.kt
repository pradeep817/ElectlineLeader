package com.emag.electlineleader

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_login_activty.*

class LoginActivty : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_activty)


        create_account.setOnClickListener { startActivity(Intent(this, RegisterActivity::class.java)) }
    }
}
