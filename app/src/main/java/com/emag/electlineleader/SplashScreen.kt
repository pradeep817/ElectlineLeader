package com.emag.electlineleader

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ProgressBar

class SplashScreen : AppCompatActivity() {

    var prefrenceManager:PrefrenceManager?=null
    var progressBar:ProgressBar?=null
    private var SPLASH_TIME_OUT:Int = 3000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        prefrenceManager= PrefrenceManager(this)

        Handler().postDelayed({
            // This method will be executed once the timer is over
            // Start your app main activity
            if (prefrenceManager?.isLogin === true) {
                progressBar?.visibility = View.GONE
                val i = Intent(this@SplashScreen, LeaderProfile::class.java)
                startActivity(i)

            } else {
                progressBar?.visibility = View.GONE
                val i = Intent(this@SplashScreen, LoginActivty::class.java)
                startActivity(i)
            }


            // close this activity
            finish()
        }, SPLASH_TIME_OUT.toLong())
    }


}
