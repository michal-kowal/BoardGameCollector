package edu.put.inf149533

import android.content.Context
import android.os.Bundle
import android.provider.ContactsContract.Data
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText

class NewUserFragment(val context1: Context, val db: MyDBHandler) : Fragment() {
    lateinit var logInButton: Button
    lateinit var  username: EditText
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_new_user, container, false)
        logInButton = view.findViewById(R.id.configButton)
        username = view.findViewById(R.id.userName)
        logInButton.setOnClickListener{
            println(username.text.toString())
            val url = "https://boardgamegeek.com/xmlapi2/user?name="+username.text.toString()
            DataLoader(context1).loadData("user.xml")
            DataLoader(context1).showData(db, "user.xml", username.text.toString())
            DataLoader(context1).downloadFile(url, db, "user.xml", username.text.toString())
        }
        return view
    }

}