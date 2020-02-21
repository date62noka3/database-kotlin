package com.example.databasepractice

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

object PhotoTable : BaseColumns {
    const val TABLE_NAME = "photo"
    const val COLUMN_NAME_BITMAP = "bitmap"
}

private const val SQL_CREATE_PHOTO = "CREATE TABLE ${PhotoTable.TABLE_NAME}" +
        " (${BaseColumns._ID} INTEGER PRIMARY KEY," +
        " ${PhotoTable.COLUMN_NAME_BITMAP} BLOB NOT NULL)"
private const val SQL_DELETE_PHOTO = "DROP TABLE IF EXISTS ${PhotoTable.TABLE_NAME}"

private const val DATABASE_VERSION = 1
private const val DATABASE_NAME = "Practice.db"

class DbHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_PHOTO)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_PHOTO)
        onCreate(db)
    }
}