package com.emag.electlineleader

import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_activty)

        pDialog= ProgressDialog(this)
        pDialog!!.setTitle("Authenticating")
        pDialog!!.setMessage("Please wait.....")
        pDialog!!.setCancelable(false)


        login.setOnClickListener {

            getLogin()
        }


        create_account.setOnClickListener { startActivity(Intent(this, RegisterActivity::class.java)) }
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
