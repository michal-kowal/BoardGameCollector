package edu.put.inf149533

import android.annotation.SuppressLint
import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import java.io.File
import android.Manifest
import android.app.AlertDialog
import android.media.Image
import android.view.Gravity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream


class GameDetFragment (val game: Game, val gameDet: GameDesc, val db: MyDBHandler) : Fragment() {
    lateinit var newImage: ImageView
    private lateinit var resultLauncher: ActivityResultLauncher<Uri>
    private lateinit var mGetContent: ActivityResultLauncher<String>
    lateinit var tempImageUri: Uri
    lateinit var imagesDir: File
    var delete = false
    private fun initTempUri(): Uri {
        val tempImagesDir = File(requireContext().filesDir, getString(R.string.temp_images_dir))
        tempImagesDir.mkdir()
        val tempImage = File(tempImagesDir, getString(R.string.temp_image))
        return FileProvider.getUriForFile(requireContext(), getString(R.string.authorities), tempImage)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mGetContent = registerForActivityResult(ActivityResultContracts.GetContent()){
                result ->
            if (result != null){
                newImage = ImageView(requireContext())
                newImage.setImageURI(result)

                val rand = (0..100000).random()
                val destImageFileName = "from_gallery_${rand}.jpg"
                val destImagePath = "${imagesDir.absolutePath}/$destImageFileName"
                val destImageFile = File(destImagePath)
                val inputStream: InputStream? = requireContext().contentResolver.openInputStream(result)
                val outputStream: OutputStream? = FileOutputStream(destImageFile)
                inputStream?.copyTo(outputStream!!)
                inputStream?.close()
                outputStream?.close()
                loadGallery(requireView())
            }
        }

        resultLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { result ->
            if (result) {
                newImage.setImageURI(null)
                newImage.setImageURI(tempImageUri)

                val rand = (0..1000000).random()
                val destImageFileName = "captured_image_${rand}.jpg"
                val destImagePath = "${imagesDir.absolutePath}/$destImageFileName"
                val destImageFile = File(destImagePath)
                val inputStream: InputStream? = requireContext().contentResolver.openInputStream(tempImageUri)
                val outputStream: OutputStream? = FileOutputStream(destImageFile)
                inputStream?.copyTo(outputStream!!)
                inputStream?.close()
                outputStream?.close()
                loadGallery(requireView())
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        imagesDir = File(requireContext().filesDir, game.id.toString())
        imagesDir.mkdirs()
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
        val add = view.findViewById<Button>(R.id.addButton)
        add.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext(), R.style.CustomDialogStyle)
            builder.setTitle("Add photos")
            builder.setMessage("Add from device gallery or take picture using camera")
            builder.setPositiveButton("Add from gallery") { dialog, which ->
                mGetContent.launch("image/*")
            }
            builder.setNegativeButton("Take picture") { dialog, which ->
                takePic()
            }
            val dialog = builder.create()
            dialog.show()
        }
        val del = view.findViewById<Button>(R.id.delButton)
        del.setOnClickListener{
            val toast = Toast.makeText(requireContext(),
                "Click on picture to delete. You can't delete original picture.",
                Toast.LENGTH_LONG)
            toast.show()
            delete = true
        }
        generateTable(view)
        return view
    }

    private fun takePic() {
        newImage = ImageView(requireContext())
        tempImageUri = initTempUri()
        resultLauncher.launch(tempImageUri)
    }

    @SuppressLint("SetTextI18n")
    fun generateTable(view: View){
        val del = view.findViewById<Button>(R.id.delButton)
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

        val imgThumb = view.findViewById<ImageView>(R.id.image)
        Picasso.get()
            .load(game.img)
            .fit()
            .into(imgThumb)
        imgThumb.setOnClickListener {
            val imageUrl = game.img
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
        loadGallery(view)
    }

    fun loadGallery(view: View){
        var row: TableRow? = null
        val gallery = view.findViewById<TableLayout>(R.id.gallery)
        val rowLayoutParams = TableLayout.LayoutParams(
            TableLayout.LayoutParams.MATCH_PARENT,
            TableLayout.LayoutParams.WRAP_CONTENT
        )

        val imageLayoutParams = TableRow.LayoutParams(
            0,
            TableRow.LayoutParams.WRAP_CONTENT,
            1.0f
        )
        imageLayoutParams.setMargins(0, 0, 0, 8)
        imageLayoutParams.gravity = Gravity.CENTER
        gallery.removeAllViews()
        var count = 0
        val files = imagesDir.listFiles()
        if (files != null) {
            for (file in files) {
                val image = ImageView(requireContext())
                image.layoutParams = imageLayoutParams
                Picasso.get()
                    .load(file)
                    .resize(250,200)
                    .into(image)
                if(count % 3 == 0){
                    row = TableRow(requireContext())
                    row.layoutParams = rowLayoutParams
                    gallery.addView(row)
                }
                row?.addView(image)
                count++
                image.setOnClickListener {
                    if(delete){
                        file.delete()
                        loadGallery(view)
                        delete = false
                    }
                    else {
                        val dialog = Dialog(
                            requireContext(),
                            android.R.style.Theme_Black_NoTitleBar_Fullscreen
                        )
                        val fullImageView = ImageView(context)
                        dialog.setContentView(fullImageView)

                        Picasso.get()
                            .load(file)
                            .into(fullImageView)

                        fullImageView.setOnClickListener {
                            dialog.dismiss()
                        }

                        dialog.show()
                    }
                }
            }
        }
        else{
            delete = false
        }
    }
}