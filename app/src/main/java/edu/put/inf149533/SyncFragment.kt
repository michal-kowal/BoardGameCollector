package edu.put.inf149533

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
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
        val res = displaySyncTime(view)
        val button: Button = view.findViewById(R.id.SyncButton)
        layout = view.findViewById(R.id.syncConfirmLayout)
        yes = view.findViewById<Button>(R.id.confirmSync)
        no  = view.findViewById<Button>(R.id.RejectSync)
        button.setOnClickListener{
            if(res<24.0){
                layout.visibility = View.VISIBLE
                yes.setOnClickListener(){
                    println("synchronizuj")
                }
                no.setOnClickListener(){
                    layout.visibility = View.INVISIBLE
                }
            }
            else{
                println("synchronizuj")
            }
        }
        return view
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