package com.emre.navigationartbook.view.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.room.Room
import androidx.room.RoomDatabase
import com.emre.navigationartbook.R
import com.emre.navigationartbook.view.model.Arts
import com.emre.navigationartbook.view.roomdb.ArtDB
import com.emre.navigationartbook.view.roomdb.ArtDao

class MainActivity : AppCompatActivity() {

    private lateinit var listFragment: ListFragment
    private lateinit var db: ArtDB
    private lateinit var artDao: ArtDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listFragment = ListFragment()



        db = Room.databaseBuilder(applicationContext,ArtDB::class.java,"Arts").build()
        artDao = db.artDao()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.art_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.listFragment)
        return navController.navigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.addArt) {
            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
            val navController = navHostFragment.navController
            val action =
                ListFragmentDirections.actionListFragmentToDetailsFragment().setInfo("new")
            navController.navigate(action)
        }
        return super.onOptionsItemSelected(item)
    }
}