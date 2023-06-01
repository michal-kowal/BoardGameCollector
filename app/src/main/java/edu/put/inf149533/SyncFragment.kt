package edu.put.inf149533

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class SyncFragment(val db: MyDBHandler) : Fragment() {
    lateinit var lastSync: Date
    lateinit var layout: LinearLayout
    lateinit var yes: Button
    lateinit var no: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_sync, container, false)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val fragment = HomescreenFragment(db)
                parentFragmentManager.beginTransaction()
                    .replace(android.R.id.content, fragment)
                    .commit()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        val res = displaySyncTime(view)
        val button: Button = view.findViewById(R.id.SyncButton)
        layout = view.findViewById(R.id.syncConfirmLayout)
        yes = view.findViewById<Button>(R.id.ConfirmSync)
        no  = view.findViewById<Button>(R.id.RejectSync)
        val syncLayout = view.findViewById<LinearLayout>(R.id.progressBarLayout)
        button.setOnClickListener{
            syncLayout.visibility = View.INVISIBLE
            if(res<24.0){
                layout.visibility = View.VISIBLE
                yes.setOnClickListener(){
                    layout.visibility=View.INVISIBLE
                    syncLayout.visibility = View.VISIBLE
                    synchronise(view)
                }
                no.setOnClickListener(){
                    layout.visibility = View.INVISIBLE
                }
            }
            else{
                syncLayout.visibility = View.VISIBLE
                synchronise(view)
            }
        }
        return view
    }

    fun doSync(){
        db.deleteGames("games")
        CoroutineScope(Dispatchers.Main).launch{
            val url = "https://boardgamegeek.com/xmlapi2/collection?username=" +
                    db.ifUser().toString() +
                    "&subtype=boardgame&excludesubtype=boardgameexpansion&own=1"
            DataLoader(requireContext()).loadData("games.xml")
            DataLoader(requireContext()).showData(db, "games.xml", db.ifUser().toString())
            DataLoader(requireContext()).downloadFile(url, db, "games.xml", db.ifUser().toString())
        }
        db.deleteGames("expansions")
        CoroutineScope(Dispatchers.Main).launch {
            delay(1000)
            val url = "https://boardgamegeek.com/xmlapi2/collection?username=" +
                    db.ifUser().toString() +
                    "&subtype=boardgameexpansion&own=1"
            DataLoader(requireContext()).loadData("expansions.xml")
            DataLoader(requireContext()).showData(db, "expansions.xml", db.ifUser().toString())
            DataLoader(requireContext()).downloadFile(url, db, "expansions.xml", db.ifUser().toString())
        }
    }

    @SuppressLint("SetTextI18n")
    fun synchronise(view: View) {
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        val progressTxt = view.findViewById<TextView>(R.id.textView2)
        val gamesContentBefore = db.getGamesList("games")
        val expansionsContentBefore = db.getGamesList("expansions")
        CoroutineScope(Dispatchers.Main).launch {
            doSync()
            delay(2000)
            doSync()
            delay(2000)
            doSync()
        }
        CoroutineScope(Dispatchers.Main).launch {
            for (progress in 0..100) {
                progressBar.progress = progress
                progressTxt.text = "Synchronisation progress: " + progress.toString() + "%"
                delay(100)
            }
            progressTxt.text = "Synchronisation finished!"
            delay(500)
            val gamesContentAfter = db.getGamesList("games")
            val expansionsContentAfter = db.getGamesList("expansions")
            val missingGames: MutableList<Game> = mutableListOf()
            val missingExpansions: MutableList<Game> = mutableListOf()
            for(game in gamesContentBefore){
                if(game !in gamesContentAfter){
                    missingGames.add(game)
                }
            }
            for(expansion in expansionsContentBefore)
                if(expansion !in expansionsContentAfter){
                    missingExpansions.add(expansion)
                }
            if(missingGames.size>0 || missingExpansions.size>0){
                askUserIfDelete(missingGames, missingExpansions)
            }
        }
    }

    fun askUserIfDelete(game: MutableList<Game>, expansion: MutableList<Game>){
        val size = game.size
        val exSize = expansion.size
        val builder = AlertDialog.Builder(requireContext(), R.style.CustomDialogStyle)
        builder.setTitle("Missing Positions")
        builder.setMessage("It seems that $size games and $exSize expansions are no longer in your collection. Do you want " +
                "to delete them?")
        builder.setPositiveButton("Yes") { dialog, which ->

        }
        builder.setNegativeButton("No") { dialog, which ->
            for(g in game){
                db.addGame(g, "games")
            }
            for(e in expansion){
                db.addGame(e, "expansions")
            }
        }
        val dialog = builder.create()
        dialog.show()
    }
    fun displaySyncTime(view: View):Long{
        val sync = view.findViewById<TextView>(R.id.lastSyncInfo)
        sync.text = db.getSync()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        lastSync = dateFormat.parse(sync.text.toString())
        val calendar1 = Calendar.getInstance()
        val calendar2 = Calendar.getInstance()
        calendar1.time = Date()
        calendar2.time = lastSync
        return ((calendar1.timeInMillis - calendar2.timeInMillis)/(1000*60*60))
    }
}