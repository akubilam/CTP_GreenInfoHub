package com.example.myapplication

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(private val tasks: List<TaskItem>) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskName: TextView = itemView.findViewById(R.id.taskName)
        val taskButton: Button = itemView.findViewById(R.id.taskButton)

        init {
            taskButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val context = itemView.context
                    val task = tasks[position]

                    val intent = when (task.name) {
                        "Scan 3 plastic bottles" ->
                            Intent(context, ScannerActivity::class.java)

                        "Visit a Recycling station", "Check for nearby collection points" ->
                            Intent(context, MapActivity::class.java)

                        "Read the Plastic Recycling Guide" ->
                            Intent(context, KnowledgeActivity::class.java)

                        "Redeem your first reward" ->
                            Intent(context, MyCouponsActivity::class.java)

                        "Update your profile picture" ->
                            Intent(context, ProfileActivity::class.java)

                        "Complete the GreenInfo Hub Feedback Questionnaire" -> {
                            val url = "https://forms.gle/hpT5NecXajHv8MnHA"
                            Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url))
                        }
                        else -> null
                    }

                    if (intent != null) {
                        context.startActivity(intent)
                        if (task.name.contains("Complete the GreenInfo Hub Feedback Questionnaire")) {
                            TaskManager.completeTask(context, task.name, task.points, task.xpReward)
                        }
                    } else {
                        Toast.makeText(context, "Task: ${task.name} is in progress!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentTask = tasks[position]
        val context = holder.itemView.context

        val taskPrefs = context.getSharedPreferences("TaskStatus", android.content.Context.MODE_PRIVATE)
        val isDone = taskPrefs.getBoolean(currentTask.name, false) // 預設為未完成

        holder.taskName.text = currentTask.name

        val tvPoints = holder.itemView.findViewById<TextView>(R.id.taskPoints)
        tvPoints.text = "+${currentTask.points} Pts  |  +${currentTask.xpReward} XP"

        if (isDone) {
            holder.taskButton.text = "DONE"
            holder.taskButton.isEnabled = false
            holder.taskButton.backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.LTGRAY)
            holder.taskButton.setTextColor(android.graphics.Color.WHITE)

            holder.taskName.alpha = 0.5f
            tvPoints.alpha = 0.5f
        } else {
            holder.taskButton.text = currentTask.status
            holder.taskButton.isEnabled = true
            holder.taskButton.backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#4CAF50"))

            holder.taskName.alpha = 1.0f
            tvPoints.alpha = 1.0f
        }
    }

    override fun getItemCount() = tasks.size
}
