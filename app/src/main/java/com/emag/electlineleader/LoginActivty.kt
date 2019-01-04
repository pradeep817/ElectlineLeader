package com.emag.electlineleader

import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_login_activty.*
import org.json.JSONObject
import java.lang.Exception

class LoginActivty : AppCompatActivity() {

    private var pDialog: ProgressDialog?= null
    private var prefrenceManager:PrefrenceManager?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_activty)

        pDialog= ProgressDialog(this)
        pDialog!!.setTitle("Authenticating")
        pDialog!!.setMessage("Please wait.....")
        pDialog!!.setCancelable(false)


        prefrenceManager = PrefrenceManager(this)

        login.setOnClickListener {

            getLogin()
        }


        create_account.setOnClickListener { startActivity(Intent(this, RegisterActivity::class.java)) }

        forgot_password.setOnClickListener { startActivity(Intent(this,ForgotPassword::class.java)) }
    }

    private fun getLogin() {
        var email: String = login_email.text.toString()
        var pass: String = login_pass.text.toString()

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            login_email.error = "Please enter valid email"
            login_email.requestFocus()
        } else if (pass.isEmpty()) {
            login_pass.error = "Please enter valid password"
            login_pass.requestFocus()
        } else {
            try {
                var jsonObject = JSONObject()
                jsonObject.put("email",email)
                jsonObject.put("password",pass)

                pDialog!!.show()
                var jsonObjectRequest =
                    JsonObjectRequest(Request.Method.POST, ConstantURL.get_login, jsonObject, Response.Listener {
                        pDialog!!.cancel()
                        Log.e("response",it.toString())

                        var status:Int = it.getInt("status")
                        var msg:String = it.getString("msg")

                        if (status == 3004){
                            Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this,LeaderProfile::class.java))
                            prefrenceManager?.putPaymentStatus(true)
                            prefrenceManager?.setLoginStatus(true,email)
                            finish()
                        }else if (status == 400){
                            Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this,LeaderProfile::class.java))
                            prefrenceManager?.putPaymentStatus(false)
                            prefrenceManager?.setLoginStatus(true,email)
                            finish()
                        }else{
                            Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()
                        }
                    }, Response.ErrorListener {
                        Log.e("error",it.toString())
                        pDialog!!.cancel()
                    })

                val queue= Volley.newRequestQueue(this)
                jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
                queue.add(jsonObjectRequest)


            } catch (e: Exception) {

            }


        }

    }
}
