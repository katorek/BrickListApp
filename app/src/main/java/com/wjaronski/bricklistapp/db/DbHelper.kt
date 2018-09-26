package com.wjaronski.bricklistapp.db


import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.wjaronski.bricklistapp.model.Klocek.Klocek
import com.wjaronski.bricklistapp.model.Zestaw.Zestaw

class DbHelper
private constructor(var context: Context, path: String) : SQLiteOpenHelper(context, path, null, 1) {


    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
    }

    override fun onCreate(p0: SQLiteDatabase?) {
    }


    companion object {
        @Volatile
        private var sInstance: DbHelper? = null
        @Volatile
        private var path = ""

        @Synchronized
        fun getInstance(context: Context, path: String): DbHelper? {
            // Use the application context, which will ensure that you
            // don't accidentally leak an Activity's context.
            // See this article for more information: http://bit.ly/6LRzfx
            this.path = path
            if (sInstance == null) {
                sInstance = DbHelper(context.applicationContext, path)
            }
            return sInstance
        }

        @Synchronized
        fun getInstance(context: Context): DbHelper? {
            return getInstance(context.applicationContext, path)
        }
    }

    /** 1st cursor has result
     * 2nd stores error message
     * */
    fun getData(Query: String): ArrayList<Cursor?> {
        //get writable database
        val sqlDB = this.writableDatabase
        val columns = arrayOf("message")
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        val alc = ArrayList<Cursor?>(2)
        val Cursor2 = MatrixCursor(columns)
        alc.add(null)
        alc.add(null)

        try {
//execute the query results will be save in Cursor c
            val c = sqlDB.rawQuery(Query, null)

            //add value to cursor2
            Cursor2.addRow(arrayOf<Any>("Success"))

            alc[1] = Cursor2
            if (null != c && c.count > 0) {

                alc[0] = c
                c.moveToFirst()

                return alc
            }
            return alc
        } catch (sqlEx: SQLException) {
            Log.d("printing exception", sqlEx.message)
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(arrayOf<Any>("" + sqlEx.message))
            alc[1] = Cursor2
            return alc
        } catch (ex: Exception) {
            Log.d("printing exception", ex.message)

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(arrayOf<Any>("" + ex.message))
            alc[1] = Cursor2
            return alc
        }

    }

    fun addToDatabase(zestaw: Zestaw, db: SQLiteDatabase) {
        Log.e("ADD_TO_DB","adding to db")
//        val db = writableDatabase

        db.beginTransaction()

        try{
            val values = ContentValues()

//            values.put(Zestaw.ID, -1)
            values.put(Zestaw.ACTIVE, zestaw.Active)
            values.put(Zestaw.LAST_ACCESSED, zestaw.LastAccessed)
            values.put(Zestaw.NAME, ""+zestaw.id+ " - " + zestaw.Name)

            val rowId = db.insertOrThrow(Zestaw.TABLE_NAME, null, values)

            Log.e("ILE_KLOCKOW", ""+zestaw.listaKlockow.size)
//            Toast.makeText(context, "ile ${zestaw.listaKlockow.size}", Toast.LENGTH_LONG).show()

            zestaw.listaKlockow.forEach {
                Log.e("KLOCEK",it.ItemID)
                it.inventoryId = rowId.toInt()
                addToDatabase(it, db)
            }

//            addToDatabase()

            db.setTransactionSuccessful();

        }catch (e: Exception){
            Log.e("INSERT", "Couldnt insert into Inventories")
            e.printStackTrace()
        }finally {
            db.endTransaction()
        }


    }

    fun addToDatabase(klocek: Klocek, db: SQLiteDatabase){


        //uzupelnic dane o codes i sprobowac pobrac image

        try{
            val values = ContentValues()

//            values.put(Klocek.ID, 0)
            values.put(Klocek.INVENTORY_ID, klocek.inventoryId)

            values.put(Klocek.COLOR_ID, klocek.ColorID)
            values.put(Klocek.ITEM_ID, klocek.ItemID)
            values.put(Klocek.TYPE_ID, klocek.TypeID)


            values.put(Klocek.QUANTITY_IN_STORE, 0)
            values.put(Klocek.QUANTITY_IN_SET, klocek.qty)
            values.put(Klocek.EXTRA, klocek.extra)


            /*
         */

            db.insertOrThrow(Klocek.TABLE_NAME,null,values)
        }catch (e: Exception){
            Log.e("INSERT", "Couldnt insert into InventoriesParts")
            e.printStackTrace()
        }
    }


}