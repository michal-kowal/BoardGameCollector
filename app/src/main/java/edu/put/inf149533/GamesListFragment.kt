package edu.put.inf149533

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import kotlinx.coroutines.*
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.File
import java.io.FileWriter
import java.net.MalformedURLException
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory

class GamesListFragment(val db: MyDBHandler) : Fragment() {
    var gameInfo: MutableList<GameDesc> = mutableListOf()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_games_list, container, false)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val fragment = HomescreenFragment(db)
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
        val sortMenuButton = view.findViewById<Button>(R.id.orderButton)
        val listGames: MutableList<Game> = db.getGamesList()
        sortMenuButton.setOnClickListener {
            val context = ContextThemeWrapper(requireContext(), R.style.PopupMenuTheme)
            val popupMenu = PopupMenu(context, sortMenuButton)
            popupMenu.menu.add(Menu.NONE, 1, Menu.NONE, "By title \u2191")
            popupMenu.menu.add(Menu.NONE, 2, Menu.NONE, "By title \u2193")
            popupMenu.menu.add(Menu.NONE, 3, Menu.NONE, "By year \u2191")
            popupMenu.menu.add(Menu.NONE, 4, Menu.NONE, "By year \u2193")

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    1 -> listGames.sortBy { it.originalTitle }
                    2 -> listGames.sortByDescending { it.originalTitle }
                    3 -> listGames.sortBy { it.year }
                    4 -> listGames.sortByDescending { it.year }
                }
                showData(view, listGames)
                true
                }
            popupMenu.show()
        }
        showData(view, listGames)
    }


    fun showData(view: View, listGames: MutableList<Game>){
        val tableLayout = view.findViewById<TableLayout>(R.id.tableLayout)
        tableLayout.removeAllViews()
        tableLayout.setPadding(0,25,0,100)
        var i = 1
        for (game in listGames) {
            val tableRow = TableRow(requireContext())
            tableRow.setBackgroundResource(R.drawable.shape2)
            val cell1 = TextView(requireContext())
            cell1.text = i.toString()
            cell1.setTextColor(Color.WHITE)
            cell1.gravity = Gravity.CENTER_VERTICAL
            cell1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            tableRow.addView(cell1)

            val cell2 = TextView(requireContext())
            cell2.text = "foto"
            tableRow.addView(cell2)


            val linearLayout = LinearLayout(requireContext())
            linearLayout.orientation = LinearLayout.VERTICAL

            val text1 = TextView(requireContext())
            text1.text = game.originalTitle
            text1.setTextColor(Color.WHITE)
            text1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            text1.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            linearLayout.addView(text1)
            val text2 = TextView(requireContext())
            text2.text = "Year: " + game.year.toString()
            linearLayout.addView(text2)
            text2.setTextColor(Color.WHITE)

            tableRow.addView(linearLayout)
            tableLayout.addView(tableRow)
            val tableRow2 = TableRow(requireContext())
            tableRow2.setPadding(0,0,0, 25)
            tableLayout.addView(tableRow2)
            i += 1
            tableRow.setOnClickListener{
                CoroutineScope(Dispatchers.Main).launch {
                    val url =
                        "https://boardgamegeek.com/xmlapi2/thing?id=" + game.id.toString() + "&stats=1"
                    loadData("game_data.xml")
                    downloadFile(url, db, "game_data.xml", game.originalTitle.toString())
                    val text = "Loading content. Please wait!"
                    val toast = Toast.makeText(requireContext(), text, Toast.LENGTH_LONG)
                    toast.show()
                }
                CoroutineScope(Dispatchers.Main).launch {
                    delay(1500)
                    if (gameInfo.size != 0) {
                        val gamesDet = GameDetFragment(game, gameInfo[0])
                        val transaction: FragmentTransaction =
                            parentFragmentManager.beginTransaction()
                        transaction.hide(this@GamesListFragment)
                        transaction.add(android.R.id.content, gamesDet)
                        transaction.addToBackStack(null)
                        transaction.commit()
                    }
                }
            }
        }
    }

    fun downloadFile(BGCurl: String, myDB: MyDBHandler, filename: String, username: String) {
        val urlString = BGCurl
        val xmlDirectory = File("${requireContext().filesDir}/XML")
        if (!xmlDirectory.exists()) xmlDirectory.mkdir()
        val fileName = "$xmlDirectory/$filename"
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL(urlString)
                val reader = url.openStream().bufferedReader()
                val downloadFile = File(fileName).also { it.createNewFile() }
                val writer = FileWriter(downloadFile).buffered()
                var line: String?
                while (reader.readLine().also { line = it } != null)
                    writer.write(line)
                reader.close()
                writer.close()

                withContext(Dispatchers.Main) {
                    loadData(filename)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    when (e) {
                        is MalformedURLException -> print("Malformed URL")
                        else -> print("Error")
                    }
                    val incompleteFile = File(fileName)
                    if (incompleteFile.exists()) incompleteFile.delete()
                }
            }
        }
    }

    fun loadData(filename: String) {
        val path = requireContext().filesDir
        val inDir = File(path, "XML")
        if (inDir.exists()) {
            val file = File(inDir, filename)
            if (file.exists()) {
                val xmlDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)
                xmlDoc.documentElement.normalize()
                if (filename == "game_data.xml") {
                    var image = ""
                    var description = ""
                    var minplayers = 0
                    var maxplayers = 0
                    var rankValue = ""
                    val items: NodeList = xmlDoc.getElementsByTagName("item")
                    val itemNode: Node = items.item(0)
                    if (itemNode.nodeType == Node.ELEMENT_NODE) {
                        val elem = itemNode as Element
                        val children = elem.childNodes
                        for (j in 0 until children.length) {
                            val node = children.item(j)
                            if (node is Element) {
                                when (node.nodeName) {
                                    "image" -> {
                                        image = node.textContent.toString()
                                    }
                                    "description" -> {
                                        description = node.textContent.toString()
                                    }
                                    "minplayers" -> {
                                        minplayers = node.getAttribute("value").toInt()
                                    }
                                    "maxplayers" -> {
                                        maxplayers = node.getAttribute("value").toInt()
                                    }
                                    "statistics" -> {
                                        val statisticsNode = node as Element
                                        val ranksNode = statisticsNode.getElementsByTagName("ranks").item(0) as Element
                                        val rankNodes = ranksNode.getElementsByTagName("rank")

                                        for (k in 0 until rankNodes.length) {
                                            val rankNode = rankNodes.item(k) as Element
                                            val rankId = rankNode.getAttribute("id")

                                            if (rankId == "1") {
                                                rankValue = rankNode.getAttribute("value").toString()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        val info = GameDesc(image, description, minplayers, maxplayers, rankValue)
                        gameInfo.clear()
                        gameInfo.add(info)
                    }
                }
            }
        }
    }
}