package com.wjaronski.bricklistapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.view.View
import android.webkit.URLUtil
import android.widget.Toast
import com.wjaronski.bricklistapp.service.ZestawDownloaderTask
import kotlinx.android.synthetic.main.activity_new_project.*


class NewProject : AppCompatActivity() {
    companion object {
        val DOWNLOAD_INCOMPLETE = 100
        val DOWNLOAD_COMPLETE = 101
        val DOWNLOAD_ERROR = 102
    }

    val DEFAULT_URL_PREFERENCE = "default_url_preference"
    val DEFAULT_URL = "http://fcds.cs.put.poznan.pl/MyWeb/BL/"

    var url = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_project)

        val pref =  PreferenceManager.getDefaultSharedPreferences(this)
        url = pref.getString(DEFAULT_URL_PREFERENCE, DEFAULT_URL)

        baseURL.setText(url)
    }


    fun downloadProject(v: View) {
        var result = DOWNLOAD_INCOMPLETE
        val zdt = ZestawDownloaderTask(this)

        url = baseURL.text.toString()

        if (!URLUtil.isValidUrl(url)) {
            downloadStatus.setText(getString(R.string.incorrectURL))
            result = DOWNLOAD_ERROR
        } else {
            zdt.setBaseURL(url)

            try {
                val z = zdt.downloadSet( fileID.text.toString().replace(".xml", "").toString(), nameText.text.toString())

                result = DOWNLOAD_COMPLETE

            } catch (e: Exception) {
                result = DOWNLOAD_ERROR
                downloadStatus.text = getString(R.string.downloadError)
            }finally {
                this.setResult(result)
//                this.finish()
            }
        }

        val handler = Handler()
        handler.postDelayed(Runnable {
            this.setResult(result)
            finish()
        }, 1000)
    }

}
