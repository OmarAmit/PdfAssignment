package com.udayytest.app.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.udayytest.app.R
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.btn_login
import kotlinx.android.synthetic.main.activity_otp.*

class OtpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        btn_submit_otp.setOnClickListener { submitotp() }
    }

    private fun submitotp() {
        val otp = et_otp.text?.toString()?.trim()
        if (otp.isNullOrEmpty() || !otp.equals("0000")) {
            Toast.makeText(this, "Please enter : 0000", Toast.LENGTH_SHORT).show()
        } else {
            startActivity(Intent(this, DownloadPdfActivity::class.java))
            finish()
        }
    }
}