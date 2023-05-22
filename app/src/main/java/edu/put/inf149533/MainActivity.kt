package edu.put.inf149533

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.DocumentsContract
import android.widget.Button
import android.widget.EditText
import androidx.core.content.ContentProviderCompat.requireContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.Document
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.File
import java.io.FileWriter
import java.net.MalformedURLException
import java.net.URL
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import android.content.Context

class MainActivity : AppCompatActivity() {
    private lateinit var username: EditText
    private lateinit var loginButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        username = findViewById(R.id.userName)
        loginButton = findViewById(R.id.configButton)
        loginButton.setOnClickListener(){
            var url = "https://boardgamegeek.com/xmlapi2/collection?username=" +
                    username.text.toString() +
                    "&subtype=boardgame&excludesubtype=boardgameexpansion&own=1"
            DataLoader(this).loadData()
            DataLoader(this).showData()
            DataLoader(this).downloadFile(url)
        }
    }

}