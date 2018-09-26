package com.wjaronski.bricklistapp

import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.util.Log
import android.view.View
import android.widget.Toast
import com.wjaronski.bricklistapp.db.DbHelper
import com.wjaronski.bricklistapp.model.Klocek.GetImages
import com.wjaronski.bricklistapp.model.Klocek.Klocek
import com.wjaronski.bricklistapp.model.Klocek.KlocekAdapter
import com.wjaronski.bricklistapp.model.Zestaw.Zestaw
import kotlinx.android.synthetic.main.activity_project.*
import java.util.stream.Collector

class ProjectActivity : AppCompatActivity() {

    //    private
    private var id: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project)

        if (savedInstanceState == null) {
            val extras = intent.extras
            if (extras == null) {
                id = 0
            } else {
                id = extras.getInt("id")
            }
        } else {
            id = savedInstanceState.getSerializable("id") as Int
        }

//        val list = getPartsForInvId(id)

        val list = getPartsForInvId(id)



        listView.adapter = KlocekAdapter(this, list)

        projectHeader.text = getHeaderText(id, list)
//        projectsListView.adapter = ZestawAdapter(this, projekty)

    }

    private fun getHeaderText(id: Int, list: ArrayList<Klocek>): String {

        val projectName = getProjectName(id)

        var missing = 0
        list.forEach {
            val t = if ((it.qty - it.qtyInStore) > 0) it.qty - it.qtyInStore else 0
            missing += t
        }
        if (missing == 0) return projectName + "\nSkompletowano wymagane klocki !"
        return projectName + "\nBrakuje $missing" + if (missing == 1) " klocka" else " klockow"
    }

    private fun getProjectName(id: Int): String {
        val cursor = DbHelper.getInstance(this)!!.getData("SELECT Name FROM Inventories WHERE id = " + id)[0]!!
        var result = "Zestaw "

        if (cursor.moveToFirst()) {
            result += cursor.getString(cursor.getColumnIndex("Name"))
        }
        cursor.close()

        return result
    }

    private fun getPartsForInvId(id: Int): ArrayList<Klocek> {
        val db = DbHelper.getInstance(this)!!

        val list = ArrayList<Klocek>()

        val cursor = db.getData("SELECT * FROM InventoriesParts where InventoryID = " + id + ";")[0]!!

        if (cursor.moveToFirst()) {
            do {

                val TypeID = cursor.getString(cursor.getColumnIndex(Klocek.TYPE_ID))
                val ItemID = cursor.getString(cursor.getColumnIndex(Klocek.ITEM_ID))
                val ColorID = cursor.getInt(cursor.getColumnIndex(Klocek.COLOR_ID))
//                val Code = getCodeForKlocek(ItemID, ColorID)
//                val Desc = getDescriptionForKlocek(Code)

                val k = Klocek(
                        id = cursor.getInt(cursor.getColumnIndex("id")),
                        TypeID = TypeID,
                        ItemID = ItemID,
                        qty = cursor.getInt(cursor.getColumnIndex(Klocek.QUANTITY_IN_SET)),
                        qtyInStore = cursor.getInt(cursor.getColumnIndex(Klocek.QUANTITY_IN_STORE)),
                        ColorID = ColorID,
                        extra = cursor.getString(cursor.getColumnIndex(Klocek.EXTRA)),
                        alternate = "",
                        inventoryId = cursor.getInt(cursor.getColumnIndex(Klocek.INVENTORY_ID)),
                        Code = "",
                        Desc = "",
                        UniqueCode = 0,
                        Image = "",
                        Color = 0,
                        Condition = ""
                )
                k.UniqueCode = getCodeForKlocek(ItemID, ColorID)
//                if(k.UniqueCode!=0){
//                    //try download Image
////                    val gi = GetImages(k)
//                }
                k.Desc = getDescriptionForKlocek(ItemID)
//                val Code = getCodeForKlocek(ItemID, ColorID)
//                val Desc = getDescriptionForKlocek(Code)

                list.add(k)
            } while (cursor.moveToNext())
        }
        cursor.close()


        list.sortByDescending { if (it.qty - it.qtyInStore > 0) it.qty - it.qtyInStore else 0 }

        return list
    }

    private fun getDescriptionForKlocek(itemID: String): String {
        val cursor = DbHelper.getInstance(this)!!.getData("SELECT * FROM Parts where Code = \"" + itemID + "\";")[0]

        var description = ""

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                description = cursor.getString(cursor.getColumnIndex("Name"))
            }
            cursor.close()
        }

        return description

    }

    private fun getCodeForKlocek(itemID: String, colorID: Int): Int {
        val db = DbHelper.getInstance(this)!!
//        val cursor = DbHelper.getInstance(this)!!.
        var cursor = db.getData("SELECT * FROM Codes where Code = \"" + itemID + "\";")[0]
        var Code = 0
        var id = 0

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                id = cursor.getInt(cursor.getColumnIndex("id"))


            }
            cursor.close()

            cursor = db.getData("Select * FROM Codes where ItemID = " + id + " and ColorID = " + colorID + ";")[0]
            if (cursor != null) {
                if (cursor.moveToFirst())
                    Code = cursor.getInt(cursor.getColumnIndex("Code"))
                cursor.close()
            }
        }

        return Code
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun saveChanges(v: View) {
        val wdb = DbHelper.getInstance(this)!!.writableDatabase

        val adapter = listView.adapter as KlocekAdapter

        wdb.beginTransaction()
        try {

            adapter.list.stream().filter { it.changed }.forEach { updateKlocek(it, wdb) }
//            adapter.list.forEach { updateKlocek(it, wdb) }
            wdb.setTransactionSuccessful()
        } catch (e: Exception) {
            Log.e("UPDATE", "couldnty update, reason: " + e.cause)
            e.printStackTrace()
        } finally {
            wdb.endTransaction()
        }

//        for (i in 0..adapter.count){
//            val k = adapter.list.get(i)
//
//        }
        finish()
    }

    private fun updateKlocek(klocek: Klocek, db: SQLiteDatabase) {

        val values = ContentValues()
//        db.beginTransaction()

        values.put(Klocek.QUANTITY_IN_STORE, klocek.qtyInStore)

        val rowId = db.update(
                Klocek.TABLE_NAME,
                values,
//                "id = ? and ItemID = ? and ColorID = ?"
                "id = ?"
                ,
//                arrayOf(klocek.inventoryId.toString(),klocek.ItemID, klocek.Color.toString())
                arrayOf(klocek.id.toString())
        )

        Log.e("ILE", "" + klocek.qty + ":" + klocek.qtyInStore + ", rowId " + rowId + ", id " + klocek.id)
    }

    fun discardChanges(v: View) {
        finish()
    }

    fun export(v: View) {
        val list = getExportList()

        val intent = Intent(this, ExportActivity::class.java).apply {
            putParcelableArrayListExtra("klockiArrayList", list)
        }
        startActivity(intent)
    }

    fun getExportList(): ArrayList<Klocek> {
        val list = ArrayList<Klocek>()

        val adapter = listView.adapter as KlocekAdapter

//        Log.e("EXPORT", "Count: "+adapter.list.count())

        adapter.list.forEach {
            if (it.qty - it.qtyInStore > 0) list.add(it)
        }

//        Log.e("EXPORT", "Count: "+list.count())

        return list
    }

    fun archiveProject(v: View) {
//        id
        val db = DbHelper.getInstance(this)!!.writableDatabase
        db.beginTransaction()
        try{
            val values = ContentValues()
            values.put(Zestaw.ACTIVE,0)

            db.update(Zestaw.TABLE_NAME,values,"id = ?", arrayOf(id.toString()))

            db.setTransactionSuccessful()
            Toast.makeText(this, "Archived project", Toast.LENGTH_SHORT).show()
        }catch (e: Exception){
            Log.e("err", "Couldnt archive proejct")
        }finally {
            db.endTransaction()
        }
        finish()
    }
}
