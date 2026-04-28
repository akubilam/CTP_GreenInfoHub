package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProgressAdapter(private val progressList: List<ProgressRecord>) :
    RecyclerView.Adapter<ProgressAdapter.ProgressViewHolder>() {
    inner class ProgressViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvProgressTaskName)
        val tvDate: TextView = itemView.findViewById(R.id.tvProgressDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgressViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_progress, parent, false)
        return ProgressViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProgressViewHolder, position: Int) {
        val record = progressList[position]
        holder.tvName.text = "Completed: ${record.taskName}"
        holder.tvDate.text = "Earned on ${record.date}"
    }

    override fun getItemCount(): Int = progressList.size
}
