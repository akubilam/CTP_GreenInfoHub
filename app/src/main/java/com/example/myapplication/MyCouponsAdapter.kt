package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class MyCouponsAdapter(private val couponList: List<Pair<String, Int>>) :
    RecyclerView.Adapter<MyCouponsAdapter.CouponViewHolder>() {

    inner class CouponViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvCouponName)
        val tvQuantity: TextView = itemView.findViewById(R.id.tvCouponQuantity)
        val btnUse: Button = itemView.findViewById(R.id.btnUse)
        val ivCouponIcon: ImageView = itemView.findViewById(R.id.ivCouponIcon)

        init {
            btnUse.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val couponName = couponList[position].first
                    showRedeemDialog(itemView.context, couponName)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_my_coupon, parent, false)
        return CouponViewHolder(view)
    }

    override fun onBindViewHolder(holder: CouponViewHolder, position: Int) {
        val (name, quantity) = couponList[position]

        holder.tvName.text = name
        holder.tvQuantity.text = "Quantity: $quantity"

        when {
            name.contains("Voucher") || name.contains("優惠券") -> {
                holder.ivCouponIcon.setImageResource(R.drawable.ic_product_a)
            }
            name.contains("Paper") || name.contains("厠紙") -> {
                holder.ivCouponIcon.setImageResource(R.drawable.ic_product_b)
            }
            else -> {
                holder.ivCouponIcon.setImageResource(R.drawable.ic_store)
            }
        }
    }

    private fun showRedeemDialog(context: Context, name: String) {
        val dialog = android.app.AlertDialog.Builder(context).create()

        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.layout_dialog_redeem, null)

        view.findViewById<TextView>(R.id.dialogItemName).text = name
        view.findViewById<Button>(R.id.btnDone).setOnClickListener {
            dialog.dismiss()
        }

        dialog.setView(view)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }
    override fun getItemCount(): Int = couponList.size

}
