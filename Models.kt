package com.saeid.italyaiculturaltourism.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class Interest(val label: String) {
    ART("Art"), HISTORY("History"), FOOD("Food"), WINE("Wine"),
    FASHION("Fashion"), CRAFT("Craft"), ARCHAEOLOGY("Archaeology")
}

enum class Segment(val label: String) {
    STUDENT("Student"), FAMILY("Family"), CULTURAL_EXPLORER("Cultural Explorer"),
    LUXURY("Luxury Traveler"), LOCAL_RESIDENT("Local Resident"), INTERNATIONAL("International Visitor")
}

data class Experience(
    val id: Int,
    val title: String,
    val city: String,
    val category: Interest,
    val description: String,
    val basePrice: Double,
    val capacity: Int,
    val booked: Int,
    val rating: Double,
    val storytelling: String,
    val latitude: Double,
    val longitude: Double,
    val durationHours: Double,
    val heritageValue: Int,
    val imageEmoji: String
)

data class VisitorProfile(
    val name: String = "Guest Visitor",
    val segment: Segment = Segment.CULTURAL_EXPLORER,
    val interests: Set<Interest> = setOf(Interest.ART, Interest.HISTORY),
    val budget: Double = 80.0,
    val days: Int = 2,
    val prefersOffPeak: Boolean = true
)

data class PriceResult(
    val recommendedPrice: Double,
    val demandIndex: Double,
    val occupancy: Double,
    val multiplier: Double,
    val fairnessMessage: String,
    val explanation: String
)

data class RevenueMetric(val label: String, val value: String, val insight: String)

data class ItineraryDay(val day: Int, val title: String, val items: List<Experience>, val estimatedSpend: Double)

@Entity(tableName = "saved_plans")
data class SavedPlan(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val city: String,
    val price: Double,
    val note: String,
    val createdAt: Long = System.currentTimeMillis()
)
