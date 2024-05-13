package com.example.weatherapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "locations.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "locations"
        private const val COLUMN_ID = "id"
        private const val COLUMN_LOCATION = "location"
        private const val COLUMN_LATITUDE = "latitude"
        private const val COLUMN_LONGITUDE = "longitude"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery =
            "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_LOCATION TEXT, $COLUMN_LATITUDE REAL, $COLUMN_LONGITUDE REAL)"
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addLocation(location: String, latitude: Double, longitude: Double) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_LOCATION, location)
            put(COLUMN_LATITUDE, latitude)
            put(COLUMN_LONGITUDE, longitude)
        }
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun getAllLocations(): List<LocationData> {
        val locations = mutableListOf<LocationData>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_ID, COLUMN_LOCATION, COLUMN_LATITUDE, COLUMN_LONGITUDE),
            null,
            null,
            null,
            null,
            null
        )
        with(cursor) {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow(COLUMN_ID))
                val location = getString(getColumnIndexOrThrow(COLUMN_LOCATION))
                val latitude = getDouble(getColumnIndexOrThrow(COLUMN_LATITUDE))
                val longitude = getDouble(getColumnIndexOrThrow(COLUMN_LONGITUDE))
                locations.add(LocationData(id, location, latitude, longitude))
            }
            close()
        }
        db.close()
        return locations
    }
}

data class LocationData(val id: Long, val location: String, val latitude: Double, val longitude: Double)


