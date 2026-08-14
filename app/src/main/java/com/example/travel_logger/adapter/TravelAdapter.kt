package com.example.travel_logger.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.travel_logger.R
import com.example.travel_logger.model.TravelRecord

class TravelAdapter(
    private var travelList: List<TravelRecord>,
    private val onItemClick: (TravelRecord) -> Unit
) : RecyclerView.Adapter<TravelAdapter.TravelViewHolder>() {

    class TravelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivThumbnail: ImageView = itemView.findViewById(R.id.ivThumbnail)
        val tvLocationName: TextView = itemView.findViewById(R.id.tvLocationName)
        val tvCoordinates: TextView = itemView.findViewById(R.id.tvCoordinates)
        val tvCommentSnippet: TextView = itemView.findViewById(R.id.tvCommentSnippet)
        val tvRating: TextView = itemView.findViewById(R.id.tvRating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TravelViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_travel_record, parent, false)
        return TravelViewHolder(view)
    }

    override fun onBindViewHolder(holder: TravelViewHolder, position: Int) {
        val record = travelList[position]
        holder.tvLocationName.text = record.locationName
        holder.tvCoordinates.text = "GPS: %.4f, %.4f".format(record.latitude, record.longitude)
        holder.tvCommentSnippet.text = record.comment.ifEmpty { "No additional notes provided." }
        holder.tvRating.text = "★ %.1f".format(record.rating)

        if (!record.photoUri.isNullOrEmpty()) {
            try {
                holder.ivThumbnail.setImageURI(Uri.parse(record.photoUri))
            } catch (e: Exception) {
                holder.ivThumbnail.setImageResource(android.R.drawable.ic_menu_camera)
            }
        } else {
            holder.ivThumbnail.setImageResource(android.R.drawable.ic_menu_camera)
        }

        holder.itemView.setOnClickListener {
            onItemClick(record)
        }
    }

    override fun getItemCount(): Int = travelList.size

    fun updateData(newList: List<TravelRecord>) {
        travelList = newList
        notifyDataSetChanged()
    }
}
