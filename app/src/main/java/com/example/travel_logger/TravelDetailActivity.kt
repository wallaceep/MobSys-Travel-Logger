package com.example.travel_logger

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.travel_logger.model.TravelRecord
import com.example.travel_logger.view.CustomRatingBar

class TravelDetailActivity : AppCompatActivity() {

    private lateinit var ivDetailPhoto: ImageView
    private lateinit var tvDetailLocationName: TextView
    private lateinit var tvDetailRatingPill: TextView
    private lateinit var customRatingBarDetail: CustomRatingBar
    private lateinit var tvDetailGpsCoordinates: TextView
    private lateinit var tvDetailNotes: TextView
    private lateinit var btnOpenGoogleMaps: Button

    private var travelRecord: TravelRecord? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_travel_detail)

        extractTravelRecord()
        initViews()
        populateData()
    }

    private fun extractTravelRecord() {
        travelRecord = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(EXTRA_TRAVEL_RECORD, TravelRecord::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra(EXTRA_TRAVEL_RECORD) as? TravelRecord
        }
    }

    private fun initViews() {
        val btnBackHeader = findViewById<ImageView>(R.id.btnBackDetailHeader)
        btnBackHeader?.setOnClickListener { finish() }

        ivDetailPhoto = findViewById(R.id.ivDetailPhoto)
        tvDetailLocationName = findViewById(R.id.tvDetailLocationName)
        tvDetailRatingPill = findViewById(R.id.tvDetailRatingPill)
        customRatingBarDetail = findViewById(R.id.customRatingBarDetail)
        tvDetailGpsCoordinates = findViewById(R.id.tvDetailGpsCoordinates)
        tvDetailNotes = findViewById(R.id.tvDetailNotes)
        btnOpenGoogleMaps = findViewById(R.id.btnOpenGoogleMaps)

        btnOpenGoogleMaps.setOnClickListener {
            openInGoogleMaps()
        }
    }

    private fun populateData() {
        val record = travelRecord ?: return

        tvDetailLocationName.text = record.locationName
        tvDetailRatingPill.text = "★ %.1f".format(record.rating)
        customRatingBarDetail.rating = record.rating

        if (record.latitude != 0.0 || record.longitude != 0.0) {
            tvDetailGpsCoordinates.text = "Latitude: %.4f | Longitude: %.4f".format(record.latitude, record.longitude)
        } else {
            tvDetailGpsCoordinates.text = "Coordinates: Location not captured"
        }

        tvDetailNotes.text = record.comment.ifEmpty { "No extra travel notes provided for this memory." }

        if (!record.photoUri.isNullOrEmpty()) {
            try {
                ivDetailPhoto.setImageURI(Uri.parse(record.photoUri))
            } catch (e: Exception) {
                ivDetailPhoto.setImageResource(android.R.drawable.ic_menu_camera)
            }
        } else {
            ivDetailPhoto.setImageResource(android.R.drawable.ic_menu_camera)
        }
    }

    private fun openInGoogleMaps() {
        val record = travelRecord ?: return

        val uriStr = if (record.latitude != 0.0 || record.longitude != 0.0) {
            "geo:${record.latitude},${record.longitude}?q=${record.latitude},${record.longitude}(${Uri.encode(record.locationName)})"
        } else {
            "geo:0,0?q=${Uri.encode(record.locationName)}"
        }

        val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uriStr))
        try {
            startActivity(mapIntent)
        } catch (e: Exception) {
            Toast.makeText(this, "Opening map application...", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val EXTRA_TRAVEL_RECORD = "EXTRA_TRAVEL_RECORD"
    }
}
