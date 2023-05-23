package edu.put.inf149533
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.Context
import android.content.ContentValues

class MyDBHandler (context: Context, name: String?, factory: SQLiteDatabase.CursorFactory?,
version: Int): SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION){
    companion object{
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "bgcDB.db"

        val TABLE_GAMES = "games"

        val COLUMN_ID = "id"
        val COLUMN_TITLE = "title"
        val COLUMN_ORIGINAL_TITLE = "originalTitle"
        val COLUMN_YEAR = "year"
        val COLUMN_IMG = "img"
    }

    override fun onCreate(db: SQLiteDatabase){
        val CREATE_GAMES_TABLE = ("CREATE TABLE " + TABLE_GAMES + "(" + COLUMN_ID + " INTEGER PRIMARY KEY," +
                COLUMN_TITLE + " TEXT," + COLUMN_ORIGINAL_TITLE + " TEXT," + COLUMN_YEAR + " INT," +
                COLUMN_IMG + " TEXT" + ")")
        db.execSQL(CREATE_GAMES_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAMES)
        onCreate(db)
    }

    fun addGame(game: Game){
        val values = ContentValues()
        values.put(COLUMN_ID, game.id)
        values.put(COLUMN_TITLE, game.title)
        values.put(COLUMN_ORIGINAL_TITLE, game.originalTitle)
        values.put(COLUMN_YEAR, game.year)
        values.put(COLUMN_IMG, game.img)
        val db = this.writableDatabase
        db.insert(TABLE_GAMES, null, values)
        db.close() // Czy na pewno?
    }

    fun findGame(gameID: Long):Game?{
        val query = "SELECT * FROM $TABLE_GAMES WHERE $COLUMN_ID = $gameID"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        var game: Game? =null
        if(cursor.moveToFirst()){
            val title = cursor.getString(1)
            val originalTitle = cursor.getString(2)
            val year = cursor.getInt(3)
            val img = cursor.getString(4)
            game = Game(title, originalTitle, year, gameID, img)
            cursor.close()
        }
        db.close()
        return game
    }
}