package edu.put.inf149533

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso


class GameDetFragment (val game: Game, val gameDet: GameDesc, val db: MyDBHandler) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_game_det, container, false)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val fragment = GamesListFragment(db)
                parentFragmentManager.beginTransaction()
                    .replace(android.R.id.content, fragment)
                    .commit()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
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

        val img = view.findViewById<ImageView>(R.id.image)
        var imageUrl = game.thumbnail
        Picasso.get()
            .load(imageUrl)
            //.fit()
            .into(img)

        img.setOnClickListener {
            imageUrl = game.img
            val dialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
            val fullImageView = ImageView(context)
            dialog.setContentView(fullImageView)

            Picasso.get()
                .load(imageUrl)
                .into(fullImageView)

            fullImageView.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }
    }

}