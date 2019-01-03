package com.emag.electlineleader

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_leader_profile.*
import org.json.JSONObject

class LeaderProfile : AppCompatActivity() {

    private var prefrenceManager: PrefrenceManager? = null
    private var pDialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leader_profile)


        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        prefrenceManager = PrefrenceManager(this)

        pDialog = ProgressDialog(this)
        pDialog!!.setTitle("Receiving")
        pDialog!!.setMessage("Please wait.....")
        pDialog!!.setCancelable(false)


        if (prefrenceManager?.getPaymentStatus == true) {
            payment_layout.visibility = View.GONE
        } else {
            payment_layout.visibility = View.VISIBLE
        }

        getDatabyEmail()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        if (item?.itemId == R.id.logout) {
           prefrenceManager?.logoutUser()
        }

        return super.onOptionsItemSelected(item)
    }


    fun getDatabyEmail() {

        pDialog!!.show()

        var stringRequest = StringRequest(
            Request.Method.GET,
            ConstantURL.poltyRegData + "/email-" + prefrenceManager?.getEmail,
            Response.Listener {
                Log.e("response", it.toString())
                pDialog!!.dismiss()

                var jsonObject = JSONObject(it)
                val poltyId: Int = jsonObject.getInt("poltyId")
                val poltyFirstName: String = jsonObject.getString("poltyFirstName")
                val poltyLastName: String = jsonObject.getString("poltyLastName")
                val poltyEmail: String = jsonObject.getString("poltyEmail")
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

                val resetQues: String = jsonObject.getString("resetQues")
                val activePolty: Boolean = jsonObject.getBoolean("activePolty")
                val paid: Boolean = jsonObject.getBoolean("paid")


                leader_name.text = poltyFirstName + " " + poltyLastName
                leader_vidhansabha.text = vidhanSabhaName


                val data = android.util.Base64.decode(poltyImg, 0)

                val text = Base64.encodeToString(data, 0)
                Log.e("text", text)
                //byte [] data=jsonObject.toString().getBytes(pic);
                Log.e("pic", poltyImg.toString())
                Log.e("data", data.toString())

                val bmp = BitmapFactory.decodeByteArray(data, 0, data.size)
                Log.e("bmp", bmp.toString())
                leader_img.setImageBitmap(bmp)

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
}
