package edu.put.inf149533

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import org.w3c.dom.Text


class GameDetFragment (val game: Game, val gameDet: GameDesc) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_game_det, container, false)
        generateTable(view)
        return view
    }

    @SuppressLint("SetTextI18n")
    fun generateTable(view: View){
        val header = view.findViewById<TextView>(R.id.textHeader)
        header.text = game.originalTitle.toString()

        val year = view.findViewById<TextView>(R.id.year)
        year.text = "Year: " + game.year.toString()
        val players = view.findViewById<TextView>(R.id.players)
        players.text = "Players: " + gameDet.minplayers.toString() + " - " +
                gameDet.maxplayers.toString()

        val rank = view.findViewById<TextView>(R.id.rankPos)
        rank.text = "Global rank: " + gameDet.rankValue


        val desc = view.findViewById<TextView>(R.id.description)
        desc.text = gameDet.description
    }

}