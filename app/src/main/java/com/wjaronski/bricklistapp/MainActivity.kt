package com.wjaronski.bricklistapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.wjaronski.bricklistapp.db.AndroidDatabaseManager
import com.wjaronski.bricklistapp.db.DbHelper
import com.wjaronski.bricklistapp.db.FileDownloadService
import com.wjaronski.bricklistapp.model.Zestaw.Zestaw
import com.wjaronski.bricklistapp.model.Zestaw.ZestawAdapter
import com.wjaronski.bricklistapp.util.FileUtils

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.File
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var database: DbHelper
    val allProjects = ArrayList<Zestaw>()
    val viewableProjects = ArrayList<Zestaw>()
//    val projekty = ArrayList<Zestaw>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        res = resources

//        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show()
//        }

        checkPermissions()
        downloadAndUnzipDB()
        connectToDB()
        setUpListView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setUpListView() {

        addToList()

//        viewableProjects.addAll(allProjects.filter { it -> it.Active==1 })
        val adapter = ZestawAdapter(this, viewableProjects)

        projectsListView.setOnItemClickListener { adapterView, view, i, l ->
            val z = adapter.getItem(i)

            if(z.Active==1){
                val intent = Intent(this, ProjectActivity::class.java)
                intent.putExtra("id", z.id)
                startActivity(intent)
            }

//            Toast.makeText(this, "Selected projekt: "+z.Name + ",id="+z.id,Toast.LENGTH_LONG).show()
            //projectsListView.getItemAtPosition(i)
            //todo nowy intent z klockami danego projektu
        }

    }

    private fun addToList() {
        val pref =  PreferenceManager.getDefaultSharedPreferences(this)
        val showArchivied = pref.getBoolean("show_archived", false)
        if(showArchivied){
            viewableProjects.addAll(allProjects)

        }else{
            viewableProjects.addAll(allProjects.filter { it -> it.Active==1 })
        }
    }

    fun updateListView() {
        updateProjects()

        projectsListView.adapter = ZestawAdapter(this, viewableProjects)
    }

    fun updateProjects() {
        allProjects.clear()
        viewableProjects.clear()
        val result = database.getData("Select * from Inventories")
        if(result[0] == null) return
        val cursor = result[0]!!

        if (cursor.moveToFirst()) {
            do {
                allProjects.add(
                        Zestaw(
                                id = cursor.getInt(cursor.getColumnIndex("id")),
                                listaKlockow = LinkedList(),
                                Name = cursor.getString(cursor.getColumnIndex("Name")),
                                Active = cursor.getInt(cursor.getColumnIndex("Active")),
                                LastAccessed = cursor.getInt(cursor.getColumnIndex("LastAccessed"))
                        ))
            } while (cursor.moveToNext())
        }
        addToList()
//        val pref =  PreferenceManager.getDefaultSharedPreferences(this)
//        val filter = pref.getString("show_archived", "false")
//        if(filter.equals("false")){
//            viewableProjects.addAll(allProjects.filter { it -> it.Active==1 })
//        }else{
//            viewableProjects.addAll(allProjects)
//        }

    }

    override fun onDestroy() {
        super.onDestroy()
        database.close()
    }

    private fun connectToDB() {
        val path = FileUtils.getDataDir(applicationContext, "ExtractLoc").getAbsolutePath() + "/BrickList.db"
        database = DbHelper.getInstance(applicationContext, path)!!
        updateListView()
    }

    fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(
                    Manifest.permission.INTERNET,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), 0)
        }
    }

    fun createProject(v: View) {
        startActivityForResult(Intent(this, NewProject::class.java),0)
    }

    override fun onResume() {
        super.onResume()
        updateListView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var msg = ""
        when(resultCode){
            NewProject.DOWNLOAD_COMPLETE -> msg = "Pobrano"
            NewProject.DOWNLOAD_INCOMPLETE -> msg = "Nie pobrano"
            NewProject.DOWNLOAD_ERROR -> msg = "Error podczas pobeirania"
            else -> {}
        }
//        if(!msg.equals(""))Toast.makeText(this,msg+":"+resultCode, Toast.LENGTH_LONG).show()
        updateListView()
    }

    fun showDB(v: View) {
        val intent = Intent(this, AndroidDatabaseManager::class.java)
        startActivity(intent)
    }

    fun downloadAndUnzipDB() {

        if (FileUtils.dbExists(applicationContext)) {
            connectToDB()
            return
        }

        val serverFilePath = "http://fcds.cs.put.poznan.pl/MyWeb/BL/bricklist.zip"

        val path = FileUtils.getDataDir(applicationContext).getAbsolutePath()

        val fileName = "BrickList"
        val file = File(path, fileName)

        val localPath = file.getAbsolutePath()
        val unzipPath = FileUtils.getDataDir(applicationContext, "ExtractLoc").getAbsolutePath()

        Log.e("PATH", "Local path " + localPath.toString())
        Log.e("PATH", "Unzip path " + unzipPath.toString())

        val downloadRequest = FileDownloadService.DownloadRequest(serverFilePath, localPath)
        downloadRequest.isRequiresUnzip = true
        downloadRequest.isDeleteZipAfterExtract = false
        downloadRequest.unzipAtFilePath = unzipPath

        val listener = object : FileDownloadService.OnDownloadStatusListener {

            override fun onDownloadStarted() {
//                subscriber.onNext(0.toString())
            }

            override fun onDownloadCompleted() {
                Toast.makeText(applicationContext, "Download complete", Toast.LENGTH_LONG).show()
                connectToDB()
//                subscriber.onNext(100.toString())
            }

            override fun onDownloadFailed() {}

            override fun onDownloadProgress(progress: Int) {}
        }

        val downloader = FileDownloadService.FileDownloader.getInstance(downloadRequest, listener)
        downloader.download(applicationContext)


    }


    companion object {
        public lateinit var res: Resources
    }
}
