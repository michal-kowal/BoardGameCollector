package edu.put.inf149533

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController


class HomescreenFragment(val db: MyDBHandler) : Fragment() {
    lateinit var showUser: TextView
    lateinit var numGames: TextView
    lateinit var numExtensions: TextView
    lateinit var lastSync: TextView
    lateinit var view1: View
    lateinit var clearDataButton: Button
    lateinit var confirmButton: Button
    lateinit var rejectButton: Button
    lateinit var clearLayout: LinearLayout
    lateinit var confirmLayout: LinearLayout
    lateinit var listGames: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view1 = inflater.inflate(R.layout.fragment_homescreen, container, false)
        view1.isFocusableInTouchMode = true
        view1.requestFocus()
        view1.setOnKeyListener { _, keyCode, _ ->
            keyCode == KeyEvent.KEYCODE_BACK
        }
        fillWithData()
        clearDataButton = view1.findViewById(R.id.ClearDataButton)
        confirmButton = view1.findViewById(R.id.confirmButton)
        rejectButton = view1.findViewById(R.id.rejectButton)
        clearLayout = view1.findViewById(R.id.layoutClearData)
        confirmLayout = view1.findViewById(R.id.layoutConfirm)
        listGames = view1.findViewById(R.id.GamesList)
        clearDataButton.setOnClickListener{
            confirmLayout.visibility = View.VISIBLE
            clearLayout.visibility = View.INVISIBLE
            rejectButton.setOnClickListener{
                confirmLayout.visibility = View.INVISIBLE
                clearLayout.visibility = View.VISIBLE
            }
            confirmButton.setOnClickListener{
                db.deleteUsers()
                db.deleteGames()
                requireActivity().finishAffinity()
            }
        }
        listGames.setOnClickListener {
            val gamesList = GamesListFragment(db)
            val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
            transaction.hide(this@HomescreenFragment)
            transaction.add(android.R.id.content, gamesList)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        return view1
    }
    @SuppressLint("SetTextI18n")
    fun fillWithData() {
        showUser = view1.findViewById(R.id.WelcomeText)
        numGames = view1.findViewById(R.id.GamesCount)
        numExtensions = view1.findViewById(R.id.ExtensionsCount)
        lastSync = view1.findViewById(R.id.LastSync)
        val userRes = db.ifUser()
        showUser.text = showUser.text.toString() + " $userRes"
        val lastSyncRes = db.getSync()
        lastSync.text = lastSync.text.toString() + " $lastSyncRes"
        println("tu już powinien być koniec")
        val gamesRes = db.countGames()
        numGames.text = numGames.text.toString() + " $gamesRes"
    }
}