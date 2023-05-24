package edu.put.inf149533

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.w3c.dom.Text

class HomescreenFragment(val db: MyDBHandler) : Fragment() {
    lateinit var showUser: TextView
    lateinit var numGames: TextView
    lateinit var numExtensions: TextView
    lateinit var lastSync: TextView
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_homescreen, container, false)
        showUser = view.findViewById(R.id.WelcomeText)
        numGames = view.findViewById(R.id.GamesCount)
        numExtensions = view.findViewById(R.id.ExtensionsCount)
        lastSync = view.findViewById(R.id.LastSync)
        val userRes = db.ifUser()
        showUser.text = showUser.text.toString() + " $userRes"
        val lastSyncRes = db.getSync()
        lastSync.text = lastSync.text.toString() + " $lastSyncRes"
        val gamesRes = db.countGames()
        numGames.text = numGames.text.toString() + " $gamesRes"
        return view
    }
}