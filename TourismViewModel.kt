package com.saeid.italyaiculturaltourism.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.saeid.italyaiculturaltourism.data.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TourismViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = TourismRepository()
    private val dao = AppDatabase.get(app).savedPlanDao()

    var profile = androidx.compose.runtime.mutableStateOf(VisitorProfile())
        private set
    var seasonDemand = androidx.compose.runtime.mutableDoubleStateOf(.70)
    var onlineTrend = androidx.compose.runtime.mutableDoubleStateOf(.75)
    var competitorIndex = androidx.compose.runtime.mutableDoubleStateOf(.55)
    var weatherScore = androidx.compose.runtime.mutableDoubleStateOf(.80)

    val experiences = repo.experiences
    val savedPlans = dao.all().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val recommended get() = repo.recommend(profile.value)
    val itinerary get() = repo.buildItinerary(profile.value)
    val dashboard get() = repo.revenueDashboard(seasonDemand.doubleValue, onlineTrend.doubleValue, competitorIndex.doubleValue, weatherScore.doubleValue)

    fun priceFor(e: Experience) = repo.dynamicPrice(e, seasonDemand.doubleValue, onlineTrend.doubleValue, competitorIndex.doubleValue, weatherScore.doubleValue)

    fun updateSegment(segment: Segment) { profile.value = profile.value.copy(segment = segment) }
    fun toggleInterest(interest: Interest) {
        val interests = profile.value.interests
        profile.value = profile.value.copy(interests = if (interest in interests) interests - interest else interests + interest)
    }
    fun updateBudget(value: Double) { profile.value = profile.value.copy(budget = value) }
    fun updateDays(value: Int) { profile.value = profile.value.copy(days = value) }
    fun updateOffPeak(value: Boolean) { profile.value = profile.value.copy(prefersOffPeak = value) }

    fun saveExperience(e: Experience) {
        viewModelScope.launch {
            dao.insert(SavedPlan(title = e.title, city = e.city, price = e.basePrice, note = "Saved from AI recommendations"))
        }
    }

    fun deletePlan(plan: SavedPlan) {
        viewModelScope.launch { dao.delete(plan) }
    }

    fun resetPricing() {
        seasonDemand.doubleValue = .70
        onlineTrend.doubleValue = .75
        competitorIndex.doubleValue = .55
        weatherScore.doubleValue = .80
    }
}
