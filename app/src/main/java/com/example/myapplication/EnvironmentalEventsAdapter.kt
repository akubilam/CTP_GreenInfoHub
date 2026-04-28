package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EnvironmentalEventsAdapter(
    private val events: List<EnvironmentalEvent>,
    private val onClick: (EnvironmentalEvent) -> Unit
) : RecyclerView.Adapter<EnvironmentalEventsAdapter.EventViewHolder>() {

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventName: TextView = itemView.findViewById(R.id.eventName)
        val eventDate: TextView = itemView.findViewById(R.id.eventDate)
        val eventType: TextView = itemView.findViewById(R.id.eventType)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.event_item_view, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.eventName.text = event.eventName
        holder.eventDate.text = event.eventDate
        holder.eventType.text = event.eventType

        holder.itemView.setOnClickListener { onClick(event) }
    }

    override fun getItemCount() = events.size
}