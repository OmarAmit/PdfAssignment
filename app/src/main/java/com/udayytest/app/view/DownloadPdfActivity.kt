package com.udayytest.app.view

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.udayytest.app.BuildConfig
import com.udayytest.app.R
import com.udayytest.app.viewmodel.DownloadPdfViewModel
import kotlinx.android.synthetic.main.activity_download_pdf.*
import java.io.File


class DownloadPdfActivity : AppCompatActivity() {
    
    private lateinit var downloadPdfViewModel: DownloadPdfViewModel
    private lateinit var url: String
    private val PERMISSIONS_REQUEST_STORAGE: Int = 101

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download_pdf)
        downloadPdfViewModel = ViewModelProvider(this).get(DownloadPdfViewModel::class.java)
        setObservers()
        btn_download.setOnClickListener { startDownload() }
    }

    private fun setObservers() {
        downloadPdfViewModel.downloadSuccessLiveData.removeObservers(this)
        downloadPdfViewModel.downloadSuccessLiveData.observe(
            this,
            Observer { downloadFileName -> showDownloadFile(downloadFileName) })
    }

    private fun showDownloadFile(downloadFileName: String) {
        btn_download.text = getString(R.string.download)
        cp_pbar.visibility = View.GONE
        showNotification(downloadFileName);
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun startDownload() {
        url = et_enter_url.text?.trim().toString()
        if (Patterns.WEB_URL.matcher(url).matches()) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) !== PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    ), PERMISSIONS_REQUEST_STORAGE
                )
            } else {
                getFileName()
            }
        } else {
            Toast.makeText(this, "Please enter url.", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_STORAGE) {
            getFileName()
        } else {
            Toast.makeText(this, "Please provide the storage permission.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun getFileName() {
        cp_pbar.visibility = View.VISIBLE
        btn_download.text = getString(R.string.downloading)
        downloadPdfViewModel.downloadPfdFile(url, "MyPdf", "dummy", this)
    }

    private fun showNotification(filePath: String) {
        val CHANNEL_ID = BuildConfig.APPLICATION_ID
        val CHANNEL_NAME = getString(R.string.app_name)
        val file = File(filePath)
        val intent = Intent(this, DownloadPdfActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        var notificationChannel: NotificationChannel? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(true)
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            val manager = this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(notificationChannel)
        }
        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setColor(ContextCompat.getColor(this, R.color.white))
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        this.resources,
                        R.mipmap.ic_launcher_round
                    )
                )
                .setContentTitle(CHANNEL_NAME)
                .setContentText("${file.name} download.")
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText("${file.name} download.")
                )
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
        val notificationManager =
            this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(100, notificationBuilder.build())

        Log.e("File ", file.path)
    }
}