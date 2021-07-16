package com.udayytest.app.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.udayytest.app.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        btn_login.setOnClickListener { login() }
    }

    private fun login() {
        val phoneNo = et_phone_no.text?.toString()?.trim()
        if (phoneNo.isNullOrEmpty() || phoneNo.length < 10) {
            Toast.makeText(this, "Please enter a valid mobile no.", Toast.LENGTH_SHORT).show()
        } else {
            startActivity(Intent(this, OtpActivity::class.java))
        }
    }
}