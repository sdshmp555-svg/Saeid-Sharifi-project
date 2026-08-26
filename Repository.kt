package com.saeid.italyaiculturaltourism.data

import kotlin.math.roundToInt

class TourismRepository {
    val experiences = listOf(
        Experience(1, "Colosseum Smart Heritage Route", "Rome", Interest.ARCHAEOLOGY,
            "An AI-assisted Roman heritage route combining crowd-aware timing, contextual storytelling and a capacity-conscious visit plan.",
            32.0, 120, 98, 4.8, "Ancient Rome becomes a living narrative through personalized interpretation.",
            41.8902, 12.4922, 2.5, 98, "🏛️"),
        Experience(2, "Vatican Art & Spiritual Culture", "Rome", Interest.ART,
            "A personalized art journey balancing high-demand time slots with visitor satisfaction and cultural depth.",
            45.0, 80, 62, 4.9, "Art, spirituality and visitor-flow management meet in one cultural journey.",
            41.9065, 12.4536, 3.0, 100, "🎨"),
        Experience(3, "Tuscan Wine & Farm-to-Table Lab", "Florence", Interest.WINE,
            "A Made in Italy food-and-wine experience connecting local producers, storytelling and premium packaging.",
            95.0, 30, 21, 4.7, "Local craftsmanship and taste are presented as premium cultural assets.",
            43.7696, 11.2558, 4.0, 92, "🍷"),
        Experience(4, "Venetian Glass Artisan Workshop", "Venice", Interest.CRAFT,
            "A small-capacity workshop using segmented offers to connect visitors with traditional craftsmanship.",
            70.0, 18, 15, 4.8, "The visitor meets the maker and understands the cultural value of craftsmanship.",
            45.4408, 12.3155, 2.0, 97, "🪞"),
        Experience(5, "Milan Fashion Heritage Walk", "Milan", Interest.FASHION,
            "A personalized design route linking Made in Italy branding, fashion heritage and urban storytelling.",
            58.0, 40, 25, 4.6, "Italian style is interpreted as heritage, creativity and economic identity.",
            45.4642, 9.1900, 2.5, 90, "👗"),
        Experience(6, "Naples Culinary Storytelling Tour", "Naples", Interest.FOOD,
            "A culinary route with local storytelling, satisfaction tracking and responsible upselling.",
            55.0, 35, 30, 4.7, "Food becomes a language of memory, identity and place.",
            40.8518, 14.2681, 3.0, 94, "🍝")
    )

    fun recommend(profile: VisitorProfile): List<Experience> = experiences.sortedByDescending { e ->
        var score = e.rating * 10
        if (profile.interests.contains(e.category)) score += 45
        if (e.basePrice <= profile.budget) score += 20 else score -= (e.basePrice - profile.budget).coerceAtMost(20.0)
        score += (1.0 - e.booked.toDouble() / e.capacity) * 12
        if (profile.prefersOffPeak && e.booked.toDouble() / e.capacity < 0.70) score += 8
        if (profile.segment == Segment.LUXURY && e.basePrice >= 60) score += 12
        if (profile.segment == Segment.STUDENT && e.basePrice <= 60) score += 12
        score
    }

    fun buildItinerary(profile: VisitorProfile): List<ItineraryDay> {
        val picks = recommend(profile).take((profile.days.coerceIn(1, 5) * 2).coerceAtMost(experiences.size))
        return picks.chunked(2).mapIndexed { index, dayItems ->
            ItineraryDay(
                day = index + 1,
                title = "Day ${index + 1}: ${dayItems.firstOrNull()?.city ?: "Italy"}",
                items = dayItems,
                estimatedSpend = dayItems.sumOf { it.basePrice }
            )
        }
    }

    fun dynamicPrice(
        experience: Experience,
        seasonDemand: Double,
        onlineTrend: Double,
        competitorIndex: Double,
        weatherScore: Double
    ): PriceResult {
        val occupancy = experience.booked.toDouble() / experience.capacity
        val demand = (occupancy * .35) + (seasonDemand * .25) + (onlineTrend * .20) + (weatherScore * .10) + (competitorIndex * .10)
        val multiplier = when {
            demand > .82 -> 1.22
            demand > .68 -> 1.12
            demand < .32 -> 0.82
            demand < .48 -> 0.92
            else -> 1.0
        }
        // Ethical guardrail: cap the change at +/-22% in this prototype.
        val price = (experience.basePrice * multiplier).coerceIn(experience.basePrice * 0.78, experience.basePrice * 1.22)
        val fairness = when {
            multiplier > 1.05 -> "High-demand uplift is capped at 22% to reduce fairness and accessibility risk."
            multiplier < 0.95 -> "Off-peak discount is used to stimulate demand and reduce under-utilization."
            else -> "Stable price: use personalization and storytelling rather than price pressure."
        }
        val explanation = when {
            multiplier > 1.05 -> "Demand signals are strong; prioritize capacity protection and targeted premium slots."
            multiplier < 0.95 -> "Demand signals are softer; use a visitor-friendly discount and personalized promotion."
            else -> "Demand is balanced; maintain the base price and focus on experience quality."
        }
        return PriceResult(price, demand, occupancy, multiplier, fairness, explanation)
    }

    fun revenueDashboard(seasonDemand: Double, onlineTrend: Double, competitorIndex: Double, weatherScore: Double): List<RevenueMetric> {
        val currentRevenue = experiences.sumOf { it.basePrice * it.booked }
        val aiRevenue = experiences.sumOf { dynamicPrice(it, seasonDemand, onlineTrend, competitorIndex, weatherScore).recommendedPrice * it.booked }
        val capacity = experiences.sumOf { it.capacity }
        val booked = experiences.sumOf { it.booked }
        val uplift = ((aiRevenue / currentRevenue) - 1.0) * 100
        val utilization = booked.toDouble() / capacity * 100
        return listOf(
            RevenueMetric("Booked Revenue", "€${currentRevenue.roundToInt()}", "Current simulated revenue from booked cultural experiences."),
            RevenueMetric("AI-Optimized Revenue", "€${aiRevenue.roundToInt()}", "Scenario revenue after applying the prototype's ethical dynamic-pricing guardrails."),
            RevenueMetric("Capacity Utilization", "${utilization.roundToInt()}%", "Visitor-flow indicator for crowding and under-use."),
            RevenueMetric("Scenario Uplift", "${if (uplift >= 0) "+" else ""}${uplift.roundToInt()}%", "Simulated change vs. current booked revenue; not a field-validated forecast.")
        )
    }
}
