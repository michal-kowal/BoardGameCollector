package edu.put.inf149533

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment

class GamesListFragment(val db: MyDBHandler) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view:View = inflater.inflate(R.layout.fragment_games_list, container, false)
        val listGames: MutableList<Game> = db.getGamesList()
        val tableLayout = view.findViewById<TableLayout>(R.id.tableLayout)
        for (game in listGames) {
            val tableRow = TableRow(requireContext())
            val cell1 = TextView(requireContext())
            cell1.text = game.id.toString()
            tableRow.addView(cell1)

            val cell2 = TextView(requireContext())
            cell2.text = game.originalTitle
            tableRow.addView(cell2)

            val cell3 = TextView(requireContext())
            cell3.text = game.year.toString()
            tableLayout.addView(tableRow)
        }
        return view
    }
}