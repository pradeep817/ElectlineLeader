package com.emag.electlineleader

import android.Manifest.permission.CAMERA
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.*
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Base64
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.*
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.emag.electlineleader.modal.Party
import com.lni.emagadminapp.modal.Loksabha
import com.lni.emagadminapp.modal.Question
import com.lni.emagadminapp.modal.State
import com.lni.emagadminapp.modal.Vidhansabha
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.security_question_layout.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class RegisterActivity : AppCompatActivity() {

    private lateinit var question_adapter: ArrayAdapter<Question>
    private lateinit var adapter: ArrayAdapter<State>
    private lateinit var loksabhaadapter: ArrayAdapter<Loksabha>
    private var MY_PERMISSIONS_REQUEST:Int = 101
    var RESULT_LOAD_IMAGE = 101

    private lateinit var vidhansabhaadapter: ArrayAdapter<Vidhansabha>
    internal var myCalendar = Calendar.getInstance()
    internal lateinit var date: DatePickerDialog.OnDateSetListener

    private lateinit var partyAdapter: ArrayAdapter<Party>
    private var pDialog: ProgressDialog? = null
    val state = ArrayList<State>()
    val loksabha = ArrayList<Loksabha>()
    val vidhansabha = ArrayList<Vidhansabha>()
    val party = ArrayList<Party>()
    val quetion = ArrayList<Question>()

    var survId: Int? = null
    var vidhansabhaID: Int? = null
    var partyID: Int? = null
    var questionID: Int? = null
    lateinit var bitmap: Bitmap

    var imageEncode:String?=null


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

        register.setOnClickListener { getRegister() }

        sign_in.setOnClickListener { startActivity(Intent(this,LoginActivty::class.java)) }

        change_profile_pic.setOnClickListener {
            if (checkPermission()) {
                val i = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(i, RESULT_LOAD_IMAGE)
            } else {
                runtimePermission()
            }
        }
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
            password.error = "Please enter at least 6 characters"
            password.requestFocus()
        }else if (dofb.isEmpty()){
            dob.error = "Please enter date of birth"
            dob.requestFocus()
        }else{

            getQuestion()

            var alertDialog = AlertDialog.Builder(this,R.style.MaterialDialogSheet)
            var view =  layoutInflater.inflate(R.layout.security_question_layout,null)
            alertDialog.setView(view)
            var submit:Button = view.findViewById(R.id.submit)
            var question:Spinner = view.findViewById(R.id.question_spinner)
            var answer:EditText = view.findViewById(R.id.answer)
            question_adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, quetion)
            question.adapter = question_adapter


            question!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val question: Question = parent?.getItemAtPosition(position) as Question
                    //getLoksabha(question.id)
                    questionID = question.id
                }

            }

            var dialog:Dialog = alertDialog.create()
            submit.setOnClickListener {


                var ans:String = answer.text.toString()

                var ques:String = question.selectedItem as String

                if (ans.isEmpty()){
                    answer.error = "Enter your answer"
                    answer.requestFocus()
                }else{
                    dialog.dismiss()
                    var jsonObject = JSONObject()
                    jsonObject.put("poltyFirstName",fname)
                    jsonObject.put("poltyLastName",lname)
                    jsonObject.put("poltyEmail",email_id)
                    jsonObject.put("poltyPass",pass)
                    jsonObject.put("poltyContact",phone)

                    jsonObject.put("poltyImg",imageEncode)
                    jsonObject.put("poltyDoB",dofb)

                    jsonObject.put("resetAnswer",ans)

                    var vidhanSabha= JSONObject()
                    vidhanSabha.put("vidhanSabhaId",vidhansabhaID)

                    var resetQues = JSONObject()
                    resetQues.put("resPassId",questionID)

                    jsonObject.put("resetQues",resetQues)

                    var electParty = JSONObject()
                    electParty.put("id",partyID)

                    jsonObject.put("electParty",electParty)
                    jsonObject.put("vidhanSabha",vidhanSabha)

                    Log.e("jsonobject",jsonObject.toString())

                    pDialog!!.show()

                    var jsonObjectRequest = JsonObjectRequest(Request.Method.POST,ConstantURL.poltyRegData,jsonObject,Response.Listener {

                        pDialog!!.dismiss()
                        Log.e("response",it.toString())

                        var status:Int = it.getInt("status")
                        var msg:String = it.getString("msg")


                        if (status == 2000){
                            Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this,LoginActivty::class.java))
                            finish()
                        }else{
                            Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()
                            finish()
                        }

                    },Response.ErrorListener {
                        pDialog!!.dismiss()
                        Log.e("error",it.toString())
                    })

                    val queue = Volley.newRequestQueue(this)
                    jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
                            0,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
                    queue.add(jsonObjectRequest)
                }
            }
            dialog.show()

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
            partyAdapter.notifyDataSetChanged()
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
        val myFormat = "yyyy-MM-dd" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.US)

        dob.setText(sdf.format(myCalendar.time))
    }


    fun runtimePermission() {
        ActivityCompat.requestPermissions(this@RegisterActivity, arrayOf(CAMERA, WRITE_EXTERNAL_STORAGE), MY_PERMISSIONS_REQUEST)
    }

    fun checkPermission(): Boolean {

        val FirstPermissionResult = ContextCompat.checkSelfPermission(this@RegisterActivity, CAMERA)
        val SecondPermissionResult = ContextCompat.checkSelfPermission(this@RegisterActivity, WRITE_EXTERNAL_STORAGE)


        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED && SecondPermissionResult == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        when (requestCode) {
            MY_PERMISSIONS_REQUEST -> if (grantResults.size > 0) {
                val CameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val ReadStoragePermission = grantResults[1] == PackageManager.PERMISSION_GRANTED


                if (CameraPermission && ReadStoragePermission) {
                    Toast.makeText(this@RegisterActivity, "Permission Granted", Toast.LENGTH_LONG).show()
                } else {
                    Snackbar.make(this@RegisterActivity.findViewById(android.R.id.content),
                            "Please Grant Permissions to upload profile photo",
                            Snackbar.LENGTH_INDEFINITE).setAction("ENABLE"
                    ) { runtimePermission() }.show()
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data) {
            val selectedImage = data.data
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = contentResolver.query(selectedImage!!, filePathColumn, null, null, null)
            cursor!!.moveToFirst()
            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            val picturePath = cursor.getString(columnIndex)
            Log.e("picpath", picturePath)
            cursor.close()

            val bmOptions = BitmapFactory.Options()
            bmOptions.inJustDecodeBounds = true
            BitmapFactory.decodeFile(picturePath, bmOptions)
            /* Figure out which way needs to be reduced less */
            val scaleFactor = 1

            /* Set bitmap options to scale the image decode target */
            bmOptions.inJustDecodeBounds = false
            bmOptions.inSampleSize = scaleFactor
            bmOptions.inPurgeable = true

            bmOptions.inSampleSize = 8



            bitmap = BitmapFactory.decodeFile(
                picturePath,
                bmOptions
            )
            Log.e("bitmap", bitmap.toString())
            val img = getBitMap(bitmap)

            imageEncode = Base64.encodeToString(img, 0)
            Log.e("img", imageEncode.toString())

            //setProfile_img(text)
            profile_img.setImageBitmap(bitmap)
        }
    }

    fun getBitMap(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream)
        return outputStream.toByteArray()
    }


    fun getQuestion(){
        pDialog!!.show()
        val stringRequest = StringRequest(Request.Method.GET,ConstantURL.GET_QUESTION,Response.Listener {
            pDialog!!.dismiss()
            Log.e("question_response", it.toString())
            var jsonArray = JSONArray(it)

            for (i in 0..(jsonArray.length()-1)){
                var jsonObject = jsonArray.getJSONObject(i)
                var resPassId:Int = jsonObject.getInt("resPassId")
                var passQues:String = jsonObject.getString("passQues")

                quetion.add(Question(passQues,resPassId))
            }

            question_adapter.notifyDataSetChanged()

        }, Response.ErrorListener {
            pDialog!!.dismiss()
            Log.e("question_error", it.toString())
        })

        val queue = Volley.newRequestQueue(this)
        stringRequest.retryPolicy = DefaultRetryPolicy(
            0,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        queue.add(stringRequest)

    }


}
