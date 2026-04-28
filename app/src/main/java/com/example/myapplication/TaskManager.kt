package com.example.myapplication

import android.content.Context
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

object TaskManager {
    fun completeTask(context: Context, taskName: String, rewardPoints: Int, rewardXp: Int) {
        val taskPrefs = context.getSharedPreferences("TaskStatus", Context.MODE_PRIVATE)
        val userPrefs = context.getSharedPreferences("UserData", Context.MODE_PRIVATE)

        if (!taskPrefs.getBoolean(taskName, false)) {
            taskPrefs.edit().putBoolean(taskName, true).apply()

            val sdf = SimpleDateFormat("MMM dd, HH:mm", Locale.ENGLISH)
            val currentTime = sdf.format(Date())


            taskPrefs.edit()
                .putString("last_completed_task_name", taskName)
                .putString("last_completed_task_time", currentTime)
                .apply()

            val currentPoints = userPrefs.getInt("current_points", 0)
            val currentXp = userPrefs.getInt("user_xp", 0)

            userPrefs.edit()
                .putInt("current_points", currentPoints + rewardPoints)
                .putInt("user_xp", currentXp + rewardXp)
                .apply()
            saveRecordToList(context, ProgressRecord(taskName, currentTime))

            Toast.makeText(context, "🎉 Task Completed: $taskName\n+ $rewardPoints Pts & XP!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveRecordToList(context: Context, newRecord: ProgressRecord) {
        val prefs = context.getSharedPreferences("TaskStatus", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = prefs.getString("progress_list", "[]")
        val type = object : TypeToken<MutableList<ProgressRecord>>() {}.type
        val list: MutableList<ProgressRecord> = gson.fromJson(json, type)

        list.add(0, newRecord)
        val savedList = if (list.size > 5) list.subList(0, 5) else list
        prefs.edit().putString("progress_list", gson.toJson(savedList)).apply()
    }
}
