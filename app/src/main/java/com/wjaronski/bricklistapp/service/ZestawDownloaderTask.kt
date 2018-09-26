package com.wjaronski.bricklistapp.service

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import android.util.Xml
import android.widget.Toast
import com.wjaronski.bricklistapp.db.DbHelper
import com.wjaronski.bricklistapp.model.Klocek.Klocek
import com.wjaronski.bricklistapp.model.Zestaw.Zestaw
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.*


/**
 * Created by wojta on 28.05.2018.
 */
class ZestawDownloaderTask(var context: Context) {

    var message = ""
    private var ns = null

    var zestaw = Zestaw()
//    public var cont: Context? = null

    private var url: String = "http://fcds.cs.put.poznan.pl/MyWeb/BL/";
    private val FILE_EXTENSION = ".xml"

    fun setBaseURL(newUrl: String) {
        url = newUrl
    }

    fun downloadSet(id: String, name: String): Zestaw {
        val tempUrl = url + id + FILE_EXTENSION
        val tast = BgTask()
        tast.execute(tempUrl, name)
        zestaw.id = id.toInt()
//        Toast.makeText(context,message,Toast.LENGTH_SHORT).show()
//        zestaw.Name = name
        return zestaw
    }

    private inner class BgTask : AsyncTask<String, Void, Zestaw>() {

        override fun onPreExecute() {
            super.onPreExecute()

        }

        override fun onPostExecute(result: Zestaw) {
            super.onPostExecute(result)
            zestaw = result

            if(message.equals("Pobrano")){

                val db = DbHelper.getInstance(context)!!
                val wdb = db.writableDatabase

                db.addToDatabase(zestaw, wdb)
                wdb.beginTransaction()
                try{
                    zestaw.listaKlockow.forEach { db.addToDatabase(it, wdb) }
                    wdb.setTransactionSuccessful()
                }catch (e: Exception){
                    Log.e("ERROR", e.cause.toString())
                }
                finally {
                    wdb.endTransaction()
                }
            }

        }

        override fun doInBackground(vararg p0: String): Zestaw { //Zestaw zmaist Inta
            val url = URL(p0[0])
            val name = p0[1]
//            Log.e("background", name)
            val urlConnection = url.openConnection()
            val z = Zestaw()
//            Log.e("ID", p0[0])
            z.id = p0[0].split("/").last().replace(".xml","").toInt()
            z.Name = name
            try{
                val klocki = parse(urlConnection.getInputStream())
                klocki.forEach {
                    it.inventoryId = z.id
                    z.listaKlockow.add(it)
                }
                message = "Pobrano"
            }catch (e: FileNotFoundException){
                message = "Nie poprawny URL"
            }catch (e: Exception){
                message = "Blad podczas pobierania"
            }

            return z
        }

        @Throws(XmlPullParserException::class, IOException::class)
        fun parse(ins: InputStream): LinkedList<Klocek> {
            try {
                val parser = Xml.newPullParser()
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
                parser.setInput(ins, null)
                parser.nextTag()
                return readFeed(parser)
            } finally {
                ins.close()
            }
        }

        @Throws(XmlPullParserException::class, IOException::class)
        private fun readFeed(parser: XmlPullParser): LinkedList<Klocek> {
            val entries = LinkedList<Klocek>()

            parser.require(XmlPullParser.START_TAG, ns, "INVENTORY")
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.eventType != XmlPullParser.START_TAG) {
                    continue
                }
                val name = parser.name
                // Starts by looking for the entry tag
                if (name == "ITEM") {
                    entries.add(readKlocek(parser))
                } else {
                    skip(parser)
                }
            }
            return entries
        }


        @Throws(XmlPullParserException::class, IOException::class)
        private fun readKlocek(parser: XmlPullParser): Klocek {
            parser.require(XmlPullParser.START_TAG, null, "ITEM")
            val k = Klocek()
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.eventType != XmlPullParser.START_TAG) {
                    continue
                }
                when (parser.name) {
                    "ITEMTYPE" -> k.TypeID = readS("ITEMTYPE", parser)
                    "ITEMID" -> k.ItemID = readS("ITEMID", parser)
                    "QTY" -> k.qty = readI("QTY", parser)
                    "COLOR" -> k.ColorID = readI("COLOR", parser)
                    "EXTRA" -> k.extra = readS("EXTRA", parser)
                    "ALTERNATE" -> k.alternate = readS("ALTERNATE", parser)
                    else -> skip(parser)

                }
            }
            return k
        }

        private fun readS(field: String, parser: XmlPullParser): String {
            parser.require(XmlPullParser.START_TAG, ns, field)
            val value = readText(parser)
            parser.require(XmlPullParser.END_TAG, ns, field)
            return value
        }

        private fun readI(field: String, parser: XmlPullParser): Int {
            parser.require(XmlPullParser.START_TAG, ns, field)
            val value = readText(parser)
            parser.require(XmlPullParser.END_TAG, ns, field)
            return value.toInt()
        }

        @Throws(IOException::class, XmlPullParserException::class)
        private fun readText(parser: XmlPullParser): String {
            var result = ""
            if (parser.next() == XmlPullParser.TEXT) {
                result = parser.text
                parser.nextTag()
            }
            return result
        }

        @Throws(XmlPullParserException::class, IOException::class)
        private fun skip(parser: XmlPullParser) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                throw IllegalStateException()
            }
            var depth = 1
            while (depth != 0) {
                when (parser.next()) {
                    XmlPullParser.END_TAG -> depth--
                    XmlPullParser.START_TAG -> depth++
                }
            }
        }

    }
}