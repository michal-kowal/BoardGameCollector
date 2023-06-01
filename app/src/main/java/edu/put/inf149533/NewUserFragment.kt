package edu.put.inf149533

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NewUserFragment(val context1: Context, val db: MyDBHandler) : Fragment() {
    lateinit var logInButton: Button
    lateinit var  username: EditText
    lateinit var nextButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_new_user, container, false)
        logInButton = view.findViewById(R.id.configButton)
        username = view.findViewById(R.id.userName)
        nextButton = view.findViewById<Button>(R.id.LogIn)
        val duration = Toast.LENGTH_LONG

        logInButton.setOnClickListener{
            db.deleteUsers()
            var url = "https://boardgamegeek.com/xmlapi2/user?name="+username.text.toString()
            DataLoader(context1).loadData("user.xml")
            DataLoader(context1).showData(db, "user.xml", username.text.toString())
            DataLoader(context1).downloadFile(url, db, "user.xml", username.text.toString())
            val text = "Loading content. Press Log In to continue when visible"
            val toast = Toast.makeText(context1, text, duration)
            toast.show()
            CoroutineScope(Dispatchers.Main).launch {
                db.deleteGames("games")
                db.deleteGames("expansions")
                doLoad()
                delay(2000)
                db.deleteGames("games")
                db.deleteGames("expansions")
                doLoad()
            }
            CoroutineScope(Dispatchers.Main).launch {
                delay(4000)
                nextButton.visibility = View.VISIBLE
            }
        }

        nextButton.setOnClickListener{
            val logInResult = db.ifUser()
            var text = ""
            if(logInResult == null){
                text = "User doesn't exist in BoardGameGeek"
                val toast = Toast.makeText(context1, text, duration) // in Activity
                toast.show()
                nextButton.visibility = View.INVISIBLE
            }else {
                text = "Welcome $logInResult"
                val toast = Toast.makeText(context1, text, duration) // in Activity
                toast.show()
                val intent = Intent(context1, MainActivity::class.java)
                startActivity(intent)
            }
        }
        return view
    }

    fun doLoad(){
        CoroutineScope(Dispatchers.Main).launch {
            val url = "https://boardgamegeek.com/xmlapi2/collection?username=" +
                    username.text.toString() +
                    "&subtype=boardgame&excludesubtype=boardgameexpansion&own=1"
            DataLoader(context1).loadData("games.xml")
            DataLoader(context1).showData(db, "games.xml", username.text.toString())
            DataLoader(context1).downloadFile(url, db, "games.xml", username.text.toString())
        }
        CoroutineScope(Dispatchers.Main).launch {
            delay(1000)
            val url = "https://boardgamegeek.com/xmlapi2/collection?username=" +
                    username.text.toString() +
                    "&subtype=boardgameexpansion&own=1"
            DataLoader(requireContext()).loadData("expansions.xml")
            DataLoader(requireContext()).showData(db, "expansions.xml", username.text.toString())
            DataLoader(requireContext()).downloadFile(url, db, "expansions.xml", username.text.toString())
        }
    }
}