object LevelManager {
    private const val XP_PER_LEVEL = 500

    fun getLevelInfo(totalXp: Int): String {
        val level = (totalXp / XP_PER_LEVEL) + 1
        val currentLevelXp = totalXp % XP_PER_LEVEL

        return "Lv.$level ($currentLevelXp/$XP_PER_LEVEL)"
    }

    fun getLevelTitle(totalXp: Int): String {
        return when {
            totalXp >= 2000 -> "Eco Guardian"
            totalXp >= 1500 -> "Green Pioneer"
            totalXp >= 500 -> "Green Sprout"
            else -> "Green Seed"
        }
    }

    fun getLevelColor(totalXp: Int): Int {
        return android.graphics.Color.parseColor(
            when {
                totalXp >= 2000 -> "#FFA000"
                totalXp >= 1500 -> "#2E7D32"
                else -> "#4CAF50"
            }
        )
    }
}
