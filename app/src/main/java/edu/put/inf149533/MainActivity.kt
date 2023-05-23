package edu.put.inf149533

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle


class MainActivity : AppCompatActivity() {
    val dbHandler = MyDBHandler(this, null, null, 1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(dbHandler.ifUser() == null){
            val fragment = edu.put.inf149533.NewUserFragment(this, dbHandler)
            val fram = supportFragmentManager.beginTransaction()
            fram.replace(R.id.layoutView, fragment)
            fram.addToBackStack("logInUser")
            fram.commit()
        }
            //var url = "https://boardgamegeek.com/xmlapi2/collection?username=" +
            //        username.text.toString() +
             //       "&subtype=boardgame&excludesubtype=boardgameexpansion&own=1"
            //DataLoader(this).loadData()
            //DataLoader(this).showData()
            //DataLoader(this).downloadFile(url)
    }
}