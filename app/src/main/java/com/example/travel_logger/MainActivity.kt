package com.example.travel_logger

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travel_logger.adapter.TravelAdapter
import com.example.travel_logger.model.TravelRecord
import com.example.travel_logger.repository.TravelRepository
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutEmptyState: LinearLayout
    private lateinit var fabAddTravel: FloatingActionButton
    private lateinit var tvTripCount: TextView
    private lateinit var travelAdapter: TravelAdapter
    private lateinit var travelRepository: TravelRepository
    private val travelRecordsList = mutableListOf<TravelRecord>()

    private val addTravelLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val record = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                result.data?.getSerializableExtra("EXTRA_TRAVEL_RECORD", TravelRecord::class.java)
            } else {
                @Suppress("DEPRECATION")
                result.data?.getSerializableExtra("EXTRA_TRAVEL_RECORD") as? TravelRecord
            }

            if (record != null) {
                val updatedList = travelRepository.addTravelRecord(record)
                travelRecordsList.clear()
                travelRecordsList.addAll(updatedList)
                updateUIState()
                Toast.makeText(this, "Travel memory saved permanently!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        travelRepository = TravelRepository(this)
        loadSavedData()

        initViews()
        setupRecyclerView()
        updateUIState()
    }

    private fun loadSavedData() {
        travelRecordsList.clear()
        travelRecordsList.addAll(travelRepository.getTravelRecords())
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerViewTravels)
        layoutEmptyState = findViewById(R.id.layoutEmptyState)
        fabAddTravel = findViewById(R.id.fabAddTravel)
        tvTripCount = findViewById(R.id.tvTripCount)

        fabAddTravel.setOnClickListener {
            val intent = Intent(this, AddTravelActivity::class.java)
            addTravelLauncher.launch(intent)
        }
    }

    private fun setupRecyclerView() {
        travelAdapter = TravelAdapter(travelRecordsList) { record ->
            val intent = Intent(this, TravelDetailActivity::class.java).apply {
                putExtra(TravelDetailActivity.EXTRA_TRAVEL_RECORD, record)
            }
            startActivity(intent)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = travelAdapter
    }

    private fun updateUIState() {
        val count = travelRecordsList.size
        tvTripCount.text = if (count == 1) "1 memory logged" else "$count memories logged"

        if (travelRecordsList.isEmpty()) {
            recyclerView.visibility = View.GONE
            layoutEmptyState.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            layoutEmptyState.visibility = View.GONE
            travelAdapter.updateData(travelRecordsList)
        }
    }
}