package com.emag.electlineleader

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.emag.electlineleader.modal.Party
import com.lni.emagadminapp.modal.Loksabha
import com.lni.emagadminapp.modal.State
import com.lni.emagadminapp.modal.Vidhansabha
import kotlinx.android.synthetic.main.activity_register.*
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var adapter: ArrayAdapter<State>
    private lateinit var loksabhaadapter: ArrayAdapter<Loksabha>

    private lateinit var vidhansabhaadapter: ArrayAdapter<Vidhansabha>
    internal var myCalendar = Calendar.getInstance()
    internal lateinit var date: DatePickerDialog.OnDateSetListener

    private lateinit var partyAdapter: ArrayAdapter<Party>
    private var pDialog: ProgressDialog? = null
    val state = ArrayList<State>()
    val loksabha = ArrayList<Loksabha>()
    val vidhansabha = ArrayList<Vidhansabha>()
    val party = ArrayList<Party>()

    var survId: Int? = null
    var vidhansabhaID: Int? = null
    var partyID: Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        overridePendingTransition(R.anim.left_in,R.anim.left_out)
        pDialog = ProgressDialog(this)
        pDialog!!.setMessage("Please wait.....")
        pDialog!!.setCancelable(false)


        getAllParty()
        getState()

        adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, state)

        state_spinner.adapter = adapter

        partyAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,party)

        party_spinner.adapter = partyAdapter

        loksabhaadapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, loksabha)

        loksabha_spinner.adapter = loksabhaadapter


        vidhansabhaadapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, vidhansabha)

        vidhansabha_spinner.adapter = vidhansabhaadapter


        party_spinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.e("Party Id", "123")
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                val party: Party = parent?.getItemAtPosition(position) as Party
                partyID = party.id
                Log.e("Party Id", partyID.toString())
            }
        }

        vidhansabha_spinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.e("vidhansabha Id", "098")
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                val vidhansabha: Vidhansabha = parent?.getItemAtPosition(position) as Vidhansabha
                vidhansabhaID = vidhansabha.id
                Log.e("vidhansabha Id", vidhansabhaID.toString())
            }
        }



        loksabha_spinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val loksabha: Loksabha = parent?.getItemAtPosition(position) as Loksabha
                getVidhansabha(loksabha.id)
            }

        }

        state_spinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val state: State = parent?.getItemAtPosition(position) as State
                getLoksabha(state.id)
            }

        }

        date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLabel()
        }
        val datePickerDialog = DatePickerDialog(
            this@RegisterActivity, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        )



        dob.setOnClickListener { datePickerDialog.show() }

        dob.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                datePickerDialog.show()
            } else {
                // Hide your calender here
                datePickerDialog.dismiss()
            }
        }

        register.setOnClickListener {  }

        sign_in.setOnClickListener { startActivity(Intent(this,LoginActivty::class.java)) }
    }

    private fun getRegister(){

        var fname:String = f_name.text.toString()
        var lname:String = l_name.text.toString()
        var phone:String = phone_no.text.toString()
        var email_id:String = email.text.toString()
        var pass:String = password.text.toString()
        var dofb:String = dob.text.toString()

        val vidhanSabha = JSONObject()
        vidhanSabha.put("vidhanSabhaId", vidhansabhaID)

        if (fname.isEmpty()){
            f_name.error = "Enter first name"
            f_name.requestFocus()
        }else if (lname.isEmpty()){
            l_name.error = "Enter last name"
            l_name.requestFocus()
        }else if (phone.isEmpty() || phone.length<10){
            phone_no.error = "Enter valid phone no"
            phone_no.requestFocus()
        }else if (email_id.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email_id).matches()){
            email.error = "Enter valid email id"
            email.requestFocus()
        }else if(pass.isEmpty() || pass.length<6){
            password.error = "Please enter atleast 6 characters"
            password.requestFocus()
        }else if (dofb.isEmpty()){
            dob.error = "Please enter date of birth"
            dob.requestFocus()
        }else{
            var jsonObject = JSONObject()
            jsonObject.put("poltyFirstName",fname)
            jsonObject.put("poltyLastName",lname)
            jsonObject.put("poltyEmail",email_id)
            jsonObject.put("poltyPass",pass)
            jsonObject.put("poltyContact",phone)

            jsonObject.put("poltyImg","")
            jsonObject.put("poltyDoB",dofb)


        }
        try{

        }catch (e:Exception){

        }



    }


    fun getState() {
        pDialog!!.show()
        val stringRequest = StringRequest(Request.Method.GET, ConstantURL.GET_STATE, Response.Listener {
            Log.e("response", it);
            pDialog!!.dismiss()
            val jsonArray = JSONArray(it)

            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)

                val stateId: Int = jsonObject.getInt("stateId")
                val stateName: String = jsonObject.getString("stateName")

                state.add(State(stateName, stateId))


            }

            adapter.notifyDataSetChanged()


        }, Response.ErrorListener {
            pDialog!!.dismiss()
            Log.e("error", it.toString())
        })
        val queue = Volley.newRequestQueue(this)
        stringRequest.retryPolicy = DefaultRetryPolicy(
            0,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        queue.add(stringRequest)


    }

    fun getLoksabha(id: Int) {

        pDialog!!.show()
        val stringRequest = StringRequest(Request.Method.GET, ConstantURL.GET_LOKSABHA + id, Response.Listener {
            Log.e("response", it);
            pDialog!!.dismiss()
            val jsonArray = JSONArray(it)

            if (jsonArray.length() == 0) {
                loksabha.clear()
                vidhansabha.clear()
                loksabhaadapter.notifyDataSetChanged()
                vidhansabhaadapter.notifyDataSetChanged()
                Log.e("lok", loksabha.toString())
            } else {
                loksabha.clear()
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)

                    val lokId: Int = jsonObject.getInt("lokId")
                    val lokSabhaName: String = jsonObject.getString("lokSabhaName")

                    loksabha.add(Loksabha(lokSabhaName, lokId))

                }

                loksabhaadapter.notifyDataSetChanged()

            }


        }, Response.ErrorListener {
            pDialog!!.dismiss()
            Log.e("error", it.toString())
        })
        val queue = Volley.newRequestQueue(this)
        stringRequest.retryPolicy = DefaultRetryPolicy(
            0,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        queue.add(stringRequest)


    }

    fun getVidhansabha(id: Int) {

        pDialog!!.show()
        val stringRequest = StringRequest(Request.Method.GET, ConstantURL.GET_VIDHANSABHA + id, Response.Listener {
            Log.e("response", it);
            pDialog!!.dismiss()
            val jsonArray = JSONArray(it)

            if (jsonArray.length() == 0) {
                vidhansabha.clear()
                vidhansabhaadapter.notifyDataSetChanged()
                Log.e("vidhan", loksabha.toString())
            } else {
                vidhansabha.clear()
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)

                    val vidhanSabhaId: Int = jsonObject.getInt("vidhanSabhaId")
                    val vidhanSabhaName: String = jsonObject.getString("vidhanSabhaName")

                    vidhansabha.add(Vidhansabha(vidhanSabhaName, vidhanSabhaId))


                }

                vidhansabhaadapter.notifyDataSetChanged()

            }


        }, Response.ErrorListener {
            pDialog!!.dismiss()
            Log.e("error", it.toString())
        })
        val queue = Volley.newRequestQueue(this)
        stringRequest.retryPolicy = DefaultRetryPolicy(
            0,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        queue.add(stringRequest)


    }

    fun getAllParty(){

        pDialog?.show()
        val stringRequest = StringRequest(Request.Method.GET,ConstantURL.GET_PARTY,Response.Listener {

            pDialog?.dismiss()
            Log.e("party",it)

            val jsonArray = JSONArray(it)

            for (i in 0..(jsonArray.length()-1)){
                var status: String

                val jsonObject = jsonArray.getJSONObject(i)

                val partyName: String=jsonObject.getString("partyName")
                val activeParty: Boolean= jsonObject.getBoolean("activeParty")
                val id: Int=jsonObject.getInt("id")

                if (activeParty){
                    status="Active"
                }else{
                    status="Inactive"
                }
                party.add(Party(partyName,status,id))

            }
            adapter.notifyDataSetChanged()
        },Response.ErrorListener {
            Log.e("Error", it.toString())
            pDialog?.dismiss()
        })
        val queue= Volley.newRequestQueue(this)
        stringRequest.retryPolicy = DefaultRetryPolicy(
            0,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        queue.add(stringRequest)
    }

    private fun updateLabel() {
        val myFormat = "yyyy-dd-MM" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.US)

        dob.setText(sdf.format(myCalendar.time))
    }

}
