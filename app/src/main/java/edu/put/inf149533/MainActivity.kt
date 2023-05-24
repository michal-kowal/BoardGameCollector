package edu.put.inf149533

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle


class MainActivity : AppCompatActivity() {
    val dbHandler = MyDBHandler(this, null, null, 1)

    override fun onBackPressed() {
        // zablokuj
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(dbHandler.ifUser() == null){
            val fragment = edu.put.inf149533.NewUserFragment(this, dbHandler)
            val fram = supportFragmentManager.beginTransaction()
            fram.replace(R.id.layoutView, fragment)
            fram.addToBackStack(null)
            fram.commit()
        }
        else {
            val fragmentHomeScreen = edu.put.inf149533.HomescreenFragment(dbHandler)
            val fragHomeScreen = supportFragmentManager.beginTransaction()
            fragHomeScreen.replace(R.id.layoutView, fragmentHomeScreen)
            fragHomeScreen.addToBackStack("HomeScreen")
            fragHomeScreen.commit()
        }

    }
}