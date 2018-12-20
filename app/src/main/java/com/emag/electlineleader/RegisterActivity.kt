package com.emag.electlineleader

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        overridePendingTransition(R.anim.left_in,R.anim.left_out)

        sign_in.setOnClickListener { startActivity(Intent(this,LoginActivty::class.java)) }
    }
}
