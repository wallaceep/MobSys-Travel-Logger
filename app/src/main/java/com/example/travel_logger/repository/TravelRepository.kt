package com.example.travel_logger.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.travel_logger.model.TravelRecord
import org.json.JSONArray
import org.json.JSONObject

class TravelRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getTravelRecords(): List<TravelRecord> {
        val jsonString = prefs.getString(KEY_TRAVEL_LIST, null) ?: return emptyList()
        val list = mutableListOf<TravelRecord>()
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val record = TravelRecord(
                    id = obj.getString("id"),
                    locationName = obj.getString("locationName"),
                    photoUri = if (obj.has("photoUri") && !obj.isNull("photoUri")) obj.getString("photoUri") else null,
                    latitude = obj.optDouble("latitude", 0.0),
                    longitude = obj.optDouble("longitude", 0.0),
                    rating = obj.optDouble("rating", 0.0).toFloat(),
                    comment = obj.optString("comment", ""),
                    timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                )
                list.add(record)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun saveTravelRecords(records: List<TravelRecord>) {
        val jsonArray = JSONArray()
        for (record in records) {
            val obj = JSONObject().apply {
                put("id", record.id)
                put("locationName", record.locationName)
                put("photoUri", record.photoUri)
                put("latitude", record.latitude)
                put("longitude", record.longitude)
                put("rating", record.rating.toDouble())
                put("comment", record.comment)
                put("timestamp", record.timestamp)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString(KEY_TRAVEL_LIST, jsonArray.toString()).apply()
    }

    fun addTravelRecord(record: TravelRecord): List<TravelRecord> {
        val currentList = getTravelRecords().toMutableList()
        currentList.add(0, record)
        saveTravelRecords(currentList)
        return currentList
    }

    companion object {
        private const val PREFS_NAME = "travel_logger_prefs"
        private const val KEY_TRAVEL_LIST = "key_travel_records_json"
    }
}
