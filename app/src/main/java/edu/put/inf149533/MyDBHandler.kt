package edu.put.inf149533
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.Context
import android.content.ContentValues
import android.view.View
import java.text.SimpleDateFormat
import java.util.*

class MyDBHandler (context: Context, name: String?, factory: SQLiteDatabase.CursorFactory?,
version: Int): SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION){

    companion object{
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "bgcDB.db"

        val TABLE_GAMES = "games"
        val TABLE_USERS_INFO = "users_info"
        val TABLE_EXPANSIONS = "expansions"

        val COLUMN_ID = "id"
        val COLUMN_TITLE = "title"
        val COLUMN_ORIGINAL_TITLE = "originalTitle"
        val COLUMN_YEAR = "year"
        val COLUMN_IMG = "img"
        val COLUMN_THUMBNAIL = "thumbnail"

        val COLUMN_NICK = "nick"
        val COLUMN_SYNC = "sync"
    }

    override fun onCreate(db: SQLiteDatabase){
        val CREATE_GAMES_TABLE = ("CREATE TABLE " + TABLE_GAMES + "(" + COLUMN_ID + " INTEGER PRIMARY KEY," +
                COLUMN_TITLE + " TEXT," + COLUMN_ORIGINAL_TITLE + " TEXT," + COLUMN_YEAR + " INT," +
                COLUMN_IMG + " TEXT," + COLUMN_THUMBNAIL + " TEXT"  + ")")
        db.execSQL(CREATE_GAMES_TABLE)

        val CREATE_USER_INFO_TABLE = ("CREATE TABLE " + TABLE_USERS_INFO + "(" + COLUMN_NICK + " TEXT," +
                COLUMN_SYNC + " DATE)")
        db.execSQL(CREATE_USER_INFO_TABLE)

        val CREATE_EXPANSIONS_TABLE = ("CREATE TABLE " + TABLE_EXPANSIONS + "(" + COLUMN_ID + " INTEGER PRIMARY KEY," +
                COLUMN_TITLE + " TEXT," + COLUMN_ORIGINAL_TITLE + " TEXT," + COLUMN_YEAR + " INT," +
                COLUMN_IMG + " TEXT," + COLUMN_THUMBNAIL + " TEXT"  + ")")
        db.execSQL(CREATE_EXPANSIONS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAMES)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPANSIONS)
        onCreate(db)
    }

    fun dropTables(){
        this.writableDatabase.execSQL("DROP TABLE $TABLE_USERS_INFO")
        this.writableDatabase.execSQL("DROP TABLE $TABLE_GAMES")
    }

    fun addGame(game: Game, where: String){
        val values = ContentValues()
        values.put(COLUMN_ID, game.id)
        values.put(COLUMN_TITLE, game.title)
        values.put(COLUMN_ORIGINAL_TITLE, game.originalTitle)
        values.put(COLUMN_YEAR, game.year)
        values.put(COLUMN_IMG, game.img)
        values.put(COLUMN_THUMBNAIL, game.thumbnail)
        val db = this.writableDatabase
        if(where=="games") {
            db.insert(TABLE_GAMES, null, values)
        }
        else if(where=="expansions"){
            db.insert(TABLE_EXPANSIONS, null, values)
        }
        db.close()
    }

    fun addUser(nickname: String){
        val values = ContentValues()
        values.put(COLUMN_NICK, nickname)
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        val currentDate = sdf.format(Date())
        values.put(COLUMN_SYNC, currentDate)
        val db = this.writableDatabase
        db.insert(TABLE_USERS_INFO, null, values)
        db.close()
    }

    fun deleteUsers(){
        val query = "DELETE FROM $TABLE_USERS_INFO"
        val db = this.writableDatabase
        db.execSQL(query)
        db.close()
    }

    fun deleteGames(where: String){
        var query = ""
        if(where=="games") {
            query = "DELETE FROM $TABLE_GAMES"
        }
        else if(where=="expansions"){
            query = "DELETE FROM $TABLE_EXPANSIONS"
        }
        val db = this.writableDatabase
        db.execSQL(query)
        db.close()
    }

    fun countGames(where: String): Int {
        var query = ""
        if(where=="games") {
            query = "SELECT COUNT(*) FROM $TABLE_GAMES"
        }
        else if(where=="expansions"){
            query = "SELECT COUNT(*) FROM $TABLE_EXPANSIONS"
        }
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        var res = 0
        if(cursor.moveToFirst()){
            res = cursor.getInt(0)
            cursor.close()
        }
        return res
    }

    fun getSync(): String {
        val query = "SELECT $COLUMN_SYNC FROM $TABLE_USERS_INFO"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        var res = ""
        if(cursor.moveToFirst()){
            res = cursor.getString(0)
            cursor.close()
        }
        return res
    }

    fun findGame(gameID: Long, where: String):Game?{
        var query = ""
        if(where=="games") {
            query = "SELECT * FROM $TABLE_GAMES WHERE $COLUMN_ID = $gameID"
        }
        else if(where=="expansions"){
            query = "SELECT * FROM $TABLE_EXPANSIONS WHERE $COLUMN_ID = $gameID"
        }
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        var game: Game? =null
        if(cursor.moveToFirst()){
            val title = cursor.getString(1)
            val originalTitle = cursor.getString(2)
            val year = cursor.getInt(3)
            val img = cursor.getString(4)
            val thumbnail = cursor.getString(5)
            game = Game(title, originalTitle, year, gameID, img, thumbnail)
            cursor.close()
        }
        db.close()
        return game
    }

    fun ifUser():String?{
        val query = "SELECT * FROM $TABLE_USERS_INFO"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        if(cursor.moveToFirst()){
            val user = cursor.getString(0)
            cursor.close()
            return user
        }
        db.close()
        return null
    }

    fun getGamesList(where: String):MutableList<Game>{
        val gamesList: MutableList<Game> = mutableListOf()
        var query = ""
        if(where=="games") {
            query = "SELECT * FROM $TABLE_GAMES"
        }
        else if(where=="expansions"){
            query = "SELECT * FROM $TABLE_EXPANSIONS"
        }
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        var game: Game ?= null
        if(cursor.moveToFirst()){
            val id = cursor.getLong(0)
            val title = cursor.getString(1)
            val originalTitle = cursor.getString(2)
            val year = cursor.getInt(3)
            val img = cursor.getString(4)
            val thumbnail = cursor.getString(5)
            game = Game(title, originalTitle, year, id, img, thumbnail)
            gamesList.add(game)
        }
        while(cursor.moveToNext()){
            val id = cursor.getLong(0)
            val title = cursor.getString(1)
            val originalTitle = cursor.getString(2)
            val year = cursor.getInt(3)
            val img = cursor.getString(4)
            val thumbnail = cursor.getString(5)
            game = Game(title, originalTitle, year, id, img, thumbnail)
            gamesList.add(game)
        }
        cursor.close()
        return gamesList
    }
}