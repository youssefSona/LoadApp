package com.udacity

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.udacity.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private lateinit var binding: ActivityMainBinding
    private lateinit var pendingIntent: PendingIntent
    private var selectedLink: String? = null
    private var selectedName: String? = null

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setSupportActionBar(binding.toolbar)
        setContentView(binding.root)

        // Download Button
        binding.contentMain.dBtn.setLoadingButtonState(ButtonState.Completed)
        binding.contentMain.dBtn.setOnClickListener {
            binding.contentMain.dBtn.setLoadingButtonState(ButtonState.Loading)
            download()
        }
        // End of Download Button

    } // End of Main()

    @SuppressLint("Range", "UnspecifiedImmutableFlag", "MissingPermission")
    private fun download() {
        if (selectedLink != null) {

            val request =
                DownloadManager.Request(Uri.parse(selectedLink))
                    .setTitle(getString(R.string.app_name))
                    .setDescription(getString(R.string.app_description))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID =
                downloadManager.enqueue(request)// enqueue puts the download request in the queue.

            //channel
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    "channelId",
                    getString(R.string.notification_channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                channel.description = "Channel Description"

                val notificationManager = getSystemService(
                    NotificationManager::class.java
                )
                notificationManager.createNotificationChannel(channel)
            }

            // go to Detail Activity
            pendingIntent = PendingIntent.getActivity(
                this,
                0,
                Intent(this, DetailActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            //builder
            val builder: NotificationCompat.Builder =
                NotificationCompat.Builder(this, "channelId")
                    .setSmallIcon(R.drawable.ic_assistant_black_24dp)
                    .setContentTitle(getString(R.string.notification_title))
                    .setContentText(getString(R.string.notification_description))
                    .setContentIntent(pendingIntent)
                    .addAction(
                        0,
                        "Check the status",
                        pendingIntent
                    )
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)

            val notificationManager = NotificationManagerCompat.from(this)
            notificationManager.notify(0, builder.build())

        } else Toast.makeText(this, "Please select the file to download", Toast.LENGTH_SHORT)
            .show()
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            val isChecked = view.isChecked
            when (view.getId()) {
                R.id.firstOption ->
                    if (isChecked) {
                        selectedLink = "https://github.com/bumptech/glide"
                        selectedName = getString(R.string.option1)
                    }

                R.id.secondOption ->
                    if (isChecked) {
                        selectedLink =
                            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"
                        selectedName = getString(R.string.option2)
                    }

                R.id.thirdOption -> {
                    if (isChecked) {
                        selectedLink = "https://github.com/square/retrofit"
                        selectedName = getString(R.string.option3)
                    }
                }
            }
        }
        val myApp = applicationContext as MyApplication
        myApp.selectedName = selectedName
    } // end of onRadioButtonClicked

}

