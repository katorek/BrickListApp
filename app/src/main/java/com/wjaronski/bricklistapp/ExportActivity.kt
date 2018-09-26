package com.wjaronski.bricklistapp

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import com.wjaronski.bricklistapp.model.Klocek.Klocek
import com.wjaronski.bricklistapp.model.Klocek.KlocekExportAdapter
import kotlinx.android.synthetic.main.activity_export.*
import org.w3c.dom.Document
import java.io.File
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.widget.RadioButton


class ExportActivity : AppCompatActivity() {

    lateinit var list: ArrayList<Klocek>
    lateinit var adapter: KlocekExportAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_export)

        list = intent.getParcelableArrayListExtra("klockiArrayList")

        adapter = KlocekExportAdapter(this, list)

        r.setOnCheckedChangeListener { buttonView, isChecked ->
            uncheck(rU,rN)
            adapter.setConditionTo("")
        }
        rU.setOnCheckedChangeListener { buttonView, isChecked ->
            uncheck(r,rN)
            adapter.setConditionTo("U")
        }
        rN.setOnCheckedChangeListener { buttonView, isChecked ->
            uncheck(rU,r)
            adapter.setConditionTo("N")
        }
        listView.adapter = adapter
    }

    private fun uncheck(r1: RadioButton, r2: RadioButton) {
        r1.isChecked = false
        r2.isChecked = false
    }

    fun export(v: View){
        try{
            val path = writeXML(list)

            askToOpen(path)
        }catch (e: Exception){
            Log.e("ERR", " "+e.cause+"\t"+e.message+"\t"+e.stackTrace)
        }
    }

    private fun askToOpen(path: String) {
        val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    try{
                    val uri = Uri.parse(path)
                    val intent = Intent()

                    val extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(uri.toString())
                    val mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)

                    intent.action= ACTION_VIEW
//                    intent.setDataAndType(uri,mimetype)
                    intent.setDataAndType(uri,mimetype)
                    startActivity(intent)
                    }catch (e:Exception){
                        Toast.makeText(this, "Nie udalo sie otworzyc pliku",Toast.LENGTH_LONG).show()
                        e.printStackTrace()
                    }finally {
                        finish()
                    }
                }

                DialogInterface.BUTTON_NEGATIVE -> {
                    finish()
                }
            }
        }

        val builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.ask_open_file)).setPositiveButton(getString(R.string.yes), dialogClickListener)
                .setNegativeButton(getString(R.string.no), dialogClickListener).show()


    }

    fun writeXML(list: List<Klocek>):String {
        val docBuilder: DocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc: Document = docBuilder.newDocument()

        val rootItem = doc.createElement("INVENTORY")

        list.forEach {
            val item = doc.createElement("ITEM")

            val itemType = doc.createElement("ITEMTYPE")
            itemType.appendChild(doc.createTextNode(""+it.TypeID))

            val itemId = doc.createElement("ITEMID")
            itemId.appendChild(doc.createTextNode(""+it.ItemID))

            val color = doc.createElement("COLOR")
            color.appendChild(doc.createTextNode(""+it.ColorID))

            val qtyfilled = doc.createElement("QTYFILLED")
            qtyfilled.appendChild(doc.createTextNode(""+(it.qty - it.qtyInStore)))

            item.appendChild(itemType)
            item.appendChild(itemId)
            item.appendChild(color)
            item.appendChild(qtyfilled)

            if(!it.Condition.equals("")){
                val condition = doc.createElement("CONDITION")
                condition.appendChild(doc.createTextNode(it.Condition))
                item.appendChild(condition)
            }

            rootItem.appendChild(item)
        }

        doc.appendChild(rootItem)

        val transformer: Transformer = TransformerFactory.newInstance().newTransformer()
        transformer.setOutputProperty(OutputKeys.INDENT, "yes")

        val path = Environment.getExternalStorageDirectory().toString()
        val outDir = File(path, "Output")
        outDir.mkdir()

        val file = File(outDir, "exported.xml")

        transformer.transform(DOMSource(doc), StreamResult(file))

        Toast.makeText(this,"Exported to "+file.absoluteFile,Toast.LENGTH_LONG).show()

        return file.absolutePath
    }
}
