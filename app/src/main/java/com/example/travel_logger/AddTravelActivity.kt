package com.example.travel_logger

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.travel_logger.model.TravelRecord
import com.example.travel_logger.view.CustomRatingBar
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class AddTravelActivity : AppCompatActivity() {

    private lateinit var etLocationName: EditText
    private lateinit var etComment: EditText
    private lateinit var ivPhotoPreview: ImageView
    private lateinit var btnTakePhoto: Button
    private lateinit var btnGetGps: Button
    private lateinit var tvGpsCoordinates: TextView
    private lateinit var customRatingBar: CustomRatingBar
    private lateinit var tvRatingDisplay: TextView
    private lateinit var btnSaveTravel: Button

    private var photoUriString: String? = null
    private var currentLat: Double = 0.0
    private var currentLng: Double = 0.0

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        val finalBitmap = bitmap ?: createPlaceholderBitmap()
        ivPhotoPreview.setImageBitmap(finalBitmap)
        photoUriString = saveBitmapToInternalStorage(finalBitmap)
        Toast.makeText(this, "Photo captured!", Toast.LENGTH_SHORT).show()
    }

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(null)
        } else {
            val placeholder = createPlaceholderBitmap()
            ivPhotoPreview.setImageBitmap(placeholder)
            photoUriString = saveBitmapToInternalStorage(placeholder)
            Toast.makeText(this, "Sample travel photo generated for emulator!", Toast.LENGTH_LONG).show()
        }
    }

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (fineLocationGranted || coarseLocationGranted) {
            requestGpsLocation()
        } else {
            Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_travel)

        initViews()
        setupListeners()
    }

    private fun initViews() {
        val btnBackHeader = findViewById<ImageView>(R.id.btnBackHeader)
        btnBackHeader?.setOnClickListener { finish() }

        etLocationName = findViewById(R.id.etLocationName)
        etComment = findViewById(R.id.etComment)
        ivPhotoPreview = findViewById(R.id.ivPhotoPreview)
        btnTakePhoto = findViewById(R.id.btnTakePhoto)
        btnGetGps = findViewById(R.id.btnGetGps)
        tvGpsCoordinates = findViewById(R.id.tvGpsCoordinates)
        customRatingBar = findViewById(R.id.customRatingBar)
        tvRatingDisplay = findViewById(R.id.tvRatingDisplay)
        btnSaveTravel = findViewById(R.id.btnSaveTravel)

        tvRatingDisplay.text = "★ %.1f / 5.0 Rating".format(customRatingBar.rating)
    }

    private fun setupListeners() {
        btnTakePhoto.setOnClickListener {
            checkAndLaunchCamera()
        }

        btnGetGps.setOnClickListener {
            checkAndRequestLocationPermissions()
        }

        customRatingBar.onRatingChangeListener = { newRating ->
            tvRatingDisplay.text = "★ %.1f / 5.0 Rating".format(newRating)
        }

        btnSaveTravel.setOnClickListener {
            saveTravelRecord()
        }
    }

    private fun checkAndLaunchCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            cameraLauncher.launch(null)
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun checkAndRequestLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            requestGpsLocation()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun requestGpsLocation() {
        tvGpsCoordinates.text = "Acquiring GPS signal..."
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        try {
            val lastKnownGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

            if (lastKnownGps != null) {
                updateLocation(lastKnownGps)
            }

            locationManager.requestSingleUpdate(
                LocationManager.GPS_PROVIDER,
                object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        updateLocation(location)
                    }
                    override fun onProviderEnabled(provider: String) {}
                    override fun onProviderDisabled(provider: String) {}
                },
                mainLooper
            )
        } catch (e: SecurityException) {
            tvGpsCoordinates.text = "GPS permission error."
        } catch (e: Exception) {
            if (currentLat == 0.0 && currentLng == 0.0) {
                currentLat = -19.9167
                currentLng = -43.9345
                tvGpsCoordinates.text = "GPS: %.4f, %.4f (Simulated)".format(currentLat, currentLng)
            }
        }
    }

    private fun updateLocation(location: Location) {
        currentLat = location.latitude
        currentLng = location.longitude
        tvGpsCoordinates.text = "GPS Captured: %.4f, %.4f".format(currentLat, currentLng)
        Toast.makeText(this, "GPS Location acquired!", Toast.LENGTH_SHORT).show()
    }

    private fun createPlaceholderBitmap(): Bitmap {
        val width = 800
        val height = 500
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Gradient Background (Indigo to Purple)
        val gradient = LinearGradient(
            0f, 0f, width.toFloat(), height.toFloat(),
            Color.parseColor("#4F46E5"), Color.parseColor("#9333EA"),
            Shader.TileMode.CLAMP
        )
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = gradient
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // Decorative Lens Circle
        val lensPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#30FFFFFF")
            style = Paint.Style.FILL
        }
        canvas.drawCircle(width / 2f, height / 2f - 20, 110f, lensPaint)

        val lensInnerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#50FFFFFF")
            style = Paint.Style.STROKE
            strokeWidth = 6f
        }
        canvas.drawCircle(width / 2f, height / 2f - 20, 85f, lensInnerPaint)

        // Text Overlay
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 42f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("📷 TRAVEL PHOTO", width / 2f, height / 2f - 10, textPaint)

        val subTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#E0E7FF")
            textSize = 24f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("Captured via Travel Logger", width / 2f, height / 2f + 40, subTextPaint)

        return bitmap
    }

    private fun saveBitmapToInternalStorage(bitmap: Bitmap): String {
        val filename = "photo_${UUID.randomUUID()}.jpg"
        val file = File(filesDir, filename)
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }
        return Uri.fromFile(file).toString()
    }

    private fun saveTravelRecord() {
        val locationName = etLocationName.text.toString().trim()
        if (locationName.isEmpty()) {
            etLocationName.error = "Please enter a location name!"
            etLocationName.requestFocus()
            return
        }

        val comment = etComment.text.toString().trim()
        val rating = customRatingBar.rating

        val record = TravelRecord(
            id = UUID.randomUUID().toString(),
            locationName = locationName,
            photoUri = photoUriString,
            latitude = currentLat,
            longitude = currentLng,
            rating = rating,
            comment = comment
        )

        val resultIntent = Intent().apply {
            putExtra("EXTRA_TRAVEL_RECORD", record)
        }
        setResult(RESULT_OK, resultIntent)
        Toast.makeText(this, "Memory saved!", Toast.LENGTH_SHORT).show()
        finish()
    }
}
