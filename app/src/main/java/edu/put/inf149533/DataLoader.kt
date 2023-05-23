package edu.put.inf149533

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.File
import java.io.FileWriter
import java.net.MalformedURLException
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory



class DataLoader(val context: Context) {
    var games:MutableList<Game> = mutableListOf()
    val dbHandler = MyDBHandler(context, null, null, 1)
    fun downloadFile(BGCurl : String){
        val urlString = BGCurl
        val xmlDirectory = File("${context.filesDir}/XML")
        if(!xmlDirectory.exists()) xmlDirectory.mkdir()
        val fileName = "$xmlDirectory/user.xml"
        CoroutineScope(Dispatchers.IO).launch {
            try{
                val url = URL(urlString)
                val reader = url.openStream().bufferedReader()
                val downloadFile = File(fileName).also{it.createNewFile()}
                val writer = FileWriter(downloadFile).buffered()
                var line: String
                while(reader.readLine().also{line=it?.toString()?:""}!=null)
                    writer.write(line)
                reader.close()
                writer.close()

                withContext(Dispatchers.Main){
                    loadData()
                    showData()
                }
            } catch(e: Exception){
                withContext(Dispatchers.Main){
                    when(e){
                        is MalformedURLException -> print("Malformed URL")
                        else -> print("Error")
                    }
                    val incompleteFile = File(fileName)
                    if(incompleteFile.exists()) incompleteFile.delete()
                }
            }
        }
    }
    fun loadData(){
        val filename = "user.xml"
        val path = context.filesDir
        val inDir = File(path, "XML")
        if(inDir.exists()){
            val file = File(inDir, filename)
            if(file.exists()){
                val xmlDoc: Document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)
                xmlDoc.documentElement.normalize()
                val items: NodeList = xmlDoc.getElementsByTagName("item")
                for(i in 0..items.length-1){
                    val itemNode: Node = items.item(i)
                    if(itemNode.nodeType==Node.ELEMENT_NODE){
                        val elem = itemNode as Element
                        val children = elem.childNodes

                        var id: Long = 0
                        var title = "N/A"
                        var originalTitle = "N/A"
                        var year: Int = 0
                        var img = "https://ibij.put.poznan.pl/wp-content/uploads/2020/05/putLogoColor.png"
                        id = elem.getAttribute("objectid").toLong()
                        for(j in 0..children.length-1){
                            val node = children.item(j)
                            if(node is Element){
                                when(node.nodeName){
                                    "name" -> {
                                        originalTitle = node.textContent.toString()
                                    }
                                    "yearpublished" -> {
                                        year = node.textContent.toInt()
                                    }
                                    "thumbnail" -> {
                                        img = node.textContent.toString()
                                    }
                                }
                            }
                        }
                        val game = Game(title,originalTitle,year,id,img)
                        games.add(game)
                    }
                }
            }
        }
    }
    fun showData(){
        val idList: MutableList<Long> = mutableListOf()
        for(game in games){
            if(idList.contains(game.id)){
                continue
            }
            idList.add(game.id)
            if(dbHandler.findGame(game.id) == null) {
                dbHandler.addGame(game)
            }
        }
    }
}