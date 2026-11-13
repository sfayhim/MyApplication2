package com.example.myapplication.data.room

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class UserEntity(
    val email: String,
    val password: String,
    val fullName: String
)

class UserDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "pierre_fabre_users.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_USERS = "users"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PASSWORD = "password"
        private const val COLUMN_FULL_NAME = "full_name"

        @Volatile
        private var INSTANCE: UserDatabaseHelper? = null

        fun getInstance(context: Context): UserDatabaseHelper {
            return INSTANCE ?: synchronized(this) {
                val instance = UserDatabaseHelper(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = "CREATE TABLE $TABLE_USERS (" +
                "$COLUMN_EMAIL TEXT PRIMARY KEY, " +
                "$COLUMN_PASSWORD TEXT NOT NULL, " +
                "$COLUMN_FULL_NAME TEXT NOT NULL)"
        db.execSQL(createTable)

        val cv = ContentValues().apply {
            put(COLUMN_EMAIL, "chercheur.oncologie@pierre-fabre.com")
            put(COLUMN_PASSWORD, "pierre-fabre-2026")
            put(COLUMN_FULL_NAME, "Chercheur Oncologue Pierre Fabre")
        }
        db.insert(TABLE_USERS, null, cv)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    fun getUserByEmail(email: String): UserEntity? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_EMAIL, COLUMN_PASSWORD, COLUMN_FULL_NAME),
            "$COLUMN_EMAIL = ?",
            arrayOf(email),
            null, null, null
        )
        var user: UserEntity? = null
        if (cursor.moveToFirst()) {
            val e = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL))
            val p = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD))
            val n = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FULL_NAME))
            user = UserEntity(email = e, password = p, fullName = n)
        }
        cursor.close()
        return user
    }

    fun insertUser(user: UserEntity) {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COLUMN_EMAIL, user.email)
            put(COLUMN_PASSWORD, user.password)
            put(COLUMN_FULL_NAME, user.fullName)
        }
        db.insertWithOnConflict(TABLE_USERS, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun getUserCount(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_USERS", null)
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        return count
    }
}
