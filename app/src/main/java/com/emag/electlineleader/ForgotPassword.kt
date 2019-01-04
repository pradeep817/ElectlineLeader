package com.emag.electlineleader

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.forgot_password_second_layout.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class ForgotPassword : AppCompatActivity() {
    private var pDialog: ProgressDialog? = null
    var resPassId: Int? = null
    var poltyEmail: String? = null
    internal var myCalendar = Calendar.getInstance()
    internal lateinit var date: DatePickerDialog.OnDateSetListener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        pDialog = ProgressDialog(this)
        pDialog!!.setTitle("Receiving")
        pDialog!!.setMessage("Please wait.....")
        pDialog!!.setCancelable(false)

        forgot_verify.setOnClickListener {
            getDatabyEmail(forgot_email.text.toString().trim())
        }

        date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLabel()
        }
        val datePickerDialog = DatePickerDialog(

                this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)

        )

        forgot_dob.setOnClickListener { datePickerDialog.show() }

        forgot_dob.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                datePickerDialog.show()
            } else {
                // Hide your calender here
                datePickerDialog.dismiss()
            }
        }

        forgot_submit.setOnClickListener { forgotPassword() }

        goback.setOnClickListener {
            startActivity(Intent(this, LoginActivty::class.java))
            finish()
        }

    }


    fun getDatabyEmail(email: String) {

        pDialog!!.show()

        var stringRequest = StringRequest(
                Request.Method.GET,
                ConstantURL.poltyRegData + "/email-" + email,
                Response.Listener {
                    Log.e("response", it.toString())
                    pDialog!!.dismiss()

                    var jsonObject = JSONObject(it)
                    val poltyId: Int = jsonObject.getInt("poltyId")

                    if (poltyId == 0) {
                        Toast.makeText(this, "Please enter valid email address", Toast.LENGTH_SHORT).show()
                    } else {
                        val poltyFirstName: String = jsonObject.getString("poltyFirstName")
                        val poltyLastName: String = jsonObject.getString("poltyLastName")
                        poltyEmail = jsonObject.getString("poltyEmail")
                        val poltyContact: String = jsonObject.getString("poltyContact")
                        val poltyImg: String = jsonObject.getString("poltyImg")
                        val poltyDoB: String = jsonObject.getString("poltyDoB")

                        val electParty: JSONObject = jsonObject.getJSONObject("electParty")

                        val id: Int = electParty.getInt("id")
                        val partyName: String = electParty.getString("partyName")
                        val activeParty: Boolean = electParty.getBoolean("activeParty")

                        val vidhanSabha: JSONObject = jsonObject.getJSONObject("vidhanSabha")

                        val vidhanSabhaName: String = vidhanSabha.getString("vidhanSabhaName")
                        val vidhanSabhaId: Int = vidhanSabha.getInt("vidhanSabhaId")

                        val resetQues: JSONObject = jsonObject.getJSONObject("resetQues")
                        resPassId = resetQues.getInt("resPassId")
                        val passQues: String = resetQues.getString("passQues")
                        val activePolty: Boolean = jsonObject.getBoolean("activePolty")
                        val paid: Boolean = jsonObject.getBoolean("paid")


                        forgot_second_layout.visibility = View.VISIBLE
                        forgot_first_layout.visibility = View.GONE

                        forgot_leader_name.text = poltyFirstName + " " + poltyLastName

                        forgot_question.text = passQues

                        val data = Base64.decode(poltyImg, 0)

                        val text = Base64.encodeToString(data, 0)
                        Log.e("text", text)
                        //byte [] data=jsonObject.toString().getBytes(pic);
                        Log.e("pic", poltyImg.toString())
                        Log.e("data", data.toString())

                        val bmp = BitmapFactory.decodeByteArray(data, 0, data.size)
                        Log.e("bmp", bmp.toString())
                        forgot_leader_img.setImageBitmap(bmp)
                    }


                },
                Response.ErrorListener {
                    Log.e("error", it.toString())
                    pDialog!!.dismiss()
                })

        val queue = Volley.newRequestQueue(this)
        stringRequest.retryPolicy = DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        queue.add(stringRequest)
    }

    fun forgotPassword() {

        var ans: String = forgot_answer.text.toString()
        var dob: String = forgot_dob.text.toString()
        var newpass: String = forgot_new_password.text.toString()

        if (ans.isEmpty()) {
            forgot_answer.error = "This field can not be blank"
            forgot_answer.requestFocus()
        } else if (dob.isEmpty()) {
            forgot_dob.error = "This field can not be blank"
            forgot_dob.requestFocus()
        } else if (newpass.isEmpty()) {
            forgot_new_password.error = "This field can not be blank"
            forgot_new_password.requestFocus()
        } else {
            var jsonObject = JSONObject()
            jsonObject.put("answer", ans)
            jsonObject.put("email", poltyEmail)
            jsonObject.put("newPassword", newpass)
            jsonObject.put("poltyDoB", dob)
            jsonObject.put("resetPassId", resPassId)

            Log.e("params", jsonObject.toString())
            pDialog!!.show()
            var jsonObjectRequest = JsonObjectRequest(Request.Method.POST, ConstantURL.RESET_PASSWORD, jsonObject, Response.Listener {
                Log.e("response", it.toString())
                pDialog!!.dismiss()
                var status: Int = it.getInt("status")
                var ResetPass: String = it.getString("Reset Pass")
                if (status == 3004) {
                    Toast.makeText(this, "Password changed", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this, LoginActivty::class.java))
                    finish()
                } else {
                    Toast.makeText(this, ResetPass, Toast.LENGTH_LONG).show()
                }
            }, Response.ErrorListener {
                pDialog!!.dismiss()
                Log.e("error", it.toString())
            })

            val queue = Volley.newRequestQueue(this)
            jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
            queue.add(jsonObjectRequest)
        }

    }

    private fun updateLabel() {
        val myFormat = "yyyy-MM-dd" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.US)

        forgot_dob.setText(sdf.format(myCalendar.time))
    }

}
