package com.wjaronski.bricklistapp.model.Klocek

import android.media.Image
//import javax.xml.ws.Response
import android.graphics.Bitmap
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import android.graphics.BitmapFactory
import android.os.AsyncTask
import java.io.FileOutputStream
import java.net.URL


class GetImages (private val requestUrl: String, val klocek: Klocek) : AsyncTask<Any, Any, Any>() {
    private var bitmap: Bitmap? = null
//    private val fos: FileOutputStream? = null

    override fun doInBackground(vararg objects: Any): Any? {
        try {
            val url = URL(requestUrl)
            val conn = url.openConnection()
            bitmap = BitmapFactory.decodeStream(conn.getInputStream())
        } catch (ex: Exception) {
        }

        return null
    }

    override fun onPostExecute(result: Any?) {
        klocek.image = bitmap




    }
//
//    override fun onPostExecute(o: Any) {
//
//
//        if (!ImageStorage.checkifImageExists(imagename_)) {
//            view.setImageBitmap(bitmap)
//            ImageStorage.saveToSdCard(bitmap, imagename_)
//        }
//    }
}