package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StoreAdapter(
    private val items: List<StoreItem>,
    private val onItemExchanged: (StoreItem) -> Unit
) : RecyclerView.Adapter<StoreAdapter.StoreViewHolder>() {

    inner class StoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemImage: ImageView = itemView.findViewById(R.id.itemImage)
        val itemName: TextView = itemView.findViewById(R.id.itemName)
        val itemPoints: TextView = itemView.findViewById(R.id.itemPoints)
        val exchangeButton: Button = itemView.findViewById(R.id.exchangeButton)

        init {
            exchangeButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemExchanged(items[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_store, parent, false)
        return StoreViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoreViewHolder, position: Int) {
        val currentItem = items[position]
        holder.itemName.text = currentItem.name
        holder.itemPoints.text = "${currentItem.pointsRequired} Pts"
        holder.itemImage.setImageResource(currentItem.imageResId)

        val context = holder.itemView.context
        val inventoryPrefs = context.getSharedPreferences("UserInventory", Context.MODE_PRIVATE)
        val count = inventoryPrefs.getInt(currentItem.name, 0)

    }

    override fun getItemCount() = items.size
}
