package com.saeid.italyaiculturaltourism

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.*
import com.saeid.italyaiculturaltourism.data.*
import com.saeid.italyaiculturaltourism.theme.ItalyTheme
import com.saeid.italyaiculturaltourism.viewmodel.TourismViewModel
import java.util.Locale

class MainActivity : ComponentActivity() {
    private val vm: TourismViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ItalyTheme { ItalyTourismApp(vm) } }
    }
}

data class NavItem(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

@Composable
fun ItalyTourismApp(vm: TourismViewModel) {
    val nav = rememberNavController()
    val items = listOf(
        NavItem("home", "Explore", Icons.Rounded.Explore),
        NavItem("ai", "AI Plan", Icons.Rounded.AutoAwesome),
        NavItem("pricing", "Pricing", Icons.Rounded.ShowChart),
        NavItem("revenue", "Revenue", Icons.Rounded.Analytics),
        NavItem("saved", "Saved", Icons.Rounded.Bookmark)
    )
    Scaffold(
        bottomBar = {
            NavigationBar {
                val current = nav.currentBackStackEntryAsState().value?.destination
                items.forEach { item ->
                    NavigationBarItem(
                        selected = current?.hierarchy?.any { it.route == item.route } == true,
                        onClick = { nav.navigate(item.route) { launchSingleTop = true; restoreState = true } },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(nav, startDestination = "home", modifier = Modifier.padding(padding)) {
            composable("home") { ExploreScreen(vm) }
            composable("ai") { AIPlanScreen(vm) }
            composable("pricing") { PricingScreen(vm) }
            composable("revenue") { RevenueScreen(vm) }
            composable("saved") { SavedScreen(vm) }
        }
    }
}

@Composable
private fun Header(title: String, subtitle: String, badge: String? = null) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 20.dp, vertical = 22.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(title, color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(subtitle, color = Color.White.copy(alpha = 0.90f), style = MaterialTheme.typography.bodyMedium)
            }
            badge?.let {
                Surface(shape = RoundedCornerShape(50), color = Color.White.copy(alpha = 0.14f)) {
                    Text(it, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), color = Color.White, style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
private fun SectionCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 7.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) { Column(Modifier.padding(18.dp), content = content) }
}

@Composable
private fun ExploreScreen(vm: TourismViewModel) {
    LazyColumn { 
        item {
            Header("Italy AI Cultural Tourism", "AI-assisted discovery for authentic Made in Italy experiences.", "THESIS DEMO")
        }
        item {
            SectionCard {
                Text("Research-to-product framework", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Text("AI-driven marketing → personalized cultural experience → revenue management", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(8.dp))
                Text("The prototype operationalizes the thesis framework around personalization, demand-aware pricing, visitor-flow management and heritage sustainability.")
            }
        }
        item { Text("Featured experiences", modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
        items(vm.experiences) { e -> ExperienceCard(e, vm.priceFor(e), vm::saveExperience) }
    }
}

@Composable
private fun ExperienceCard(e: Experience, price: PriceResult, onSave: (Experience) -> Unit) {
    SectionCard {
        Row(verticalAlignment = Alignment.Top) {
            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.secondaryContainer) {
                Text(e.imageEmoji, modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.headlineSmall)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(e.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text("${e.city} • ${e.category.label} • ${e.rating}/5", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = { onSave(e) }) { Icon(Icons.Rounded.BookmarkBorder, contentDescription = "Save") }
        }
        Spacer(Modifier.height(10.dp))
        Text(e.description)
        Spacer(Modifier.height(10.dp))
        LinearProgressIndicator(progress = { price.occupancy.toFloat() }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(5.dp))
        Text("Capacity ${e.booked}/${e.capacity} • ${percent(price.occupancy)} occupied", style = MaterialTheme.typography.labelMedium)
        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("AI price", style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.width(6.dp))
            Text("€${money(price.recommendedPrice)}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(8.dp))
            AssistChip(onClick = {}, label = { Text("${percent(price.demandIndex)} demand") })
        }
    }
}

@Composable
private fun AIPlanScreen(vm: TourismViewModel) {
    val profile = vm.profile.value
    var query by remember { mutableStateOf("") }
    LazyColumn {
        item { Header("AI Trip Planner", "Personalize your cultural itinerary from visitor preferences.") }
        item {
            SectionCard {
                Text("Visitor segment", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Segment.entries.forEach { segment ->
                    FilterChip(
                        selected = profile.segment == segment,
                        onClick = { vm.updateSegment(segment) },
                        label = { Text(segment.label) },
                        leadingIcon = if (profile.segment == segment) { { Icon(Icons.Rounded.Check, null) } } else null,
                        modifier = Modifier.padding(end = 6.dp, bottom = 6.dp)
                    )
                }
            }
        }
        item {
            SectionCard {
                Text("Interests", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Interest.entries.forEach { interest ->
                    FilterChip(
                        selected = interest in profile.interests,
                        onClick = { vm.toggleInterest(interest) },
                        label = { Text(interest.label) },
                        modifier = Modifier.padding(end = 6.dp, bottom = 6.dp)
                    )
                }
            }
        }
        item {
            SectionCard {
                Text("Trip budget: €${profile.budget.toInt()}", fontWeight = FontWeight.Bold)
                Slider(value = profile.budget.toFloat(), onValueChange = { vm.updateBudget(it.toDouble()) }, valueRange = 20f..150f)
                Text("Trip length: ${profile.days} day(s)", fontWeight = FontWeight.Bold)
                Slider(value = profile.days.toFloat(), onValueChange = { vm.updateDays(it.toInt().coerceIn(1,5)) }, valueRange = 1f..5f, steps = 3)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(checked = profile.prefersOffPeak, onCheckedChange = vm::updateOffPeak)
                    Spacer(Modifier.width(8.dp))
                    Text("Prefer off-peak / lower-crowd slots")
                }
            }
        }
        item {
            SectionCard {
                Text("Ask the local AI planner", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Example: art + food in Rome") })
                Spacer(Modifier.height(8.dp))
                Text("The demo uses your preferences to rank experiences locally; no API key is required.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        item { Text("Recommended for you", modifier = Modifier.padding(18.dp, 8.dp), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
        items(vm.recommended.take(5)) { e ->
            SectionCard {
                Text(e.title, fontWeight = FontWeight.Bold)
                Text("Why: ${reasonFor(e, profile)}")
                Spacer(Modifier.height(6.dp))
                Text("Offer strategy: ${offerText(profile.segment)}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                Button(onClick = { vm.saveExperience(e) }) { Icon(Icons.Rounded.Add, null); Spacer(Modifier.width(4.dp)); Text("Save experience") }
            }
        }
        item {
            SectionCard {
                Text("Generated itinerary", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                vm.itinerary.forEach { day ->
                    Spacer(Modifier.height(8.dp))
                    Text(day.title, fontWeight = FontWeight.SemiBold)
                    day.items.forEach { e -> Text("• ${e.title} - €${money(e.basePrice)}") }
                    Text("Estimated day spend: €${money(day.estimatedSpend)}", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
private fun PricingScreen(vm: TourismViewModel) {
    LazyColumn {
        item { Header("Dynamic Pricing Lab", "Demand forecasting and ethical pricing controls for cultural attractions.") }
        item {
            SectionCard {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Scenario controls", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    TextButton(onClick = vm::resetPricing) { Text("Reset") }
                }
                FactorSlider("Seasonal demand", vm.seasonDemand.doubleValue) { vm.seasonDemand.doubleValue = it }
                FactorSlider("Online trend", vm.onlineTrend.doubleValue) { vm.onlineTrend.doubleValue = it }
                FactorSlider("Competitor index", vm.competitorIndex.doubleValue) { vm.competitorIndex.doubleValue = it }
                FactorSlider("Weather / event score", vm.weatherScore.doubleValue) { vm.weatherScore.doubleValue = it }
            }
        }
        items(vm.experiences) { e ->
            val r = vm.priceFor(e)
            SectionCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(e.title, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                    Text("€${money(r.recommendedPrice)}", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
                Text("Base €${money(e.basePrice)} • Demand ${percent(r.demandIndex)} • Occupancy ${percent(r.occupancy)}")
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(progress = { r.demandIndex.toFloat() }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                Text(r.explanation)
                Spacer(Modifier.height(6.dp))
                Text(r.fairnessMessage, color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun FactorSlider(label: String, value: Double, update: (Double) -> Unit) {
    Column(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text("$label: ${percent(value)}", fontWeight = FontWeight.Medium)
        Slider(value = value.toFloat(), onValueChange = { update(it.toDouble()) }, valueRange = 0f..1f)
    }
}

@Composable
private fun RevenueScreen(vm: TourismViewModel) {
    LazyColumn {
        item { Header("Revenue & Sustainability", "Managerial decision support for cultural tourism operators.", "SIMULATION") }
        item {
            SectionCard {
                Text("Business logic", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(6.dp))
                Text("The dashboard connects demand, price, capacity and visitor-flow indicators. Results are simulated for demonstration and are not a verified forecast for a specific attraction.")
            }
        }
        items(vm.dashboard) { metric ->
            SectionCard {
                Text(metric.label, fontWeight = FontWeight.Bold)
                Text(metric.value, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Text(metric.insight, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        item {
            SectionCard {
                Text("Strategic recommendation", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(6.dp))
                Text("Start with visitor segmentation, collect ethical first-party signals, test dynamic pricing in off-peak periods, monitor satisfaction and crowding, and reinvest revenue into heritage conservation and visitor quality.")
            }
        }
    }
}

@Composable
private fun SavedScreen(vm: TourismViewModel) {
    val saved by vm.savedPlans.collectAsState()
    val ctx = LocalContext.current
    LazyColumn {
        item { Header("Saved Experiences", "Your locally stored shortlist for a future Made in Italy itinerary.") }
        if (saved.isEmpty()) {
            item {
                SectionCard {
                    Icon(Icons.Rounded.BookmarkBorder, null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(8.dp))
                    Text("No saved experiences yet.", fontWeight = FontWeight.Bold)
                    Text("Tap the bookmark on Explore or Save experience in AI Plan.")
                }
            }
        } else {
            items(saved, key = { it.id }) { plan ->
                SectionCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(plan.title, fontWeight = FontWeight.Bold)
                            Text("${plan.city} • €${money(plan.price)}")
                            Text(plan.note, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(onClick = { vm.deletePlan(plan) }) { Icon(Icons.Rounded.DeleteOutline, null) }
                    }
                    TextButton(onClick = {
                        val q = Uri.encode("${plan.title}, ${plan.city}, Italy")
                        ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=$q")))
                    }) { Icon(Icons.Rounded.Map, null); Spacer(Modifier.width(4.dp)); Text("Open in Maps") }
                }
            }
        }
    }
}

private fun reasonFor(e: Experience, p: VisitorProfile): String {
    val reasons = mutableListOf<String>()
    if (e.category in p.interests) reasons += "matches your ${e.category.label.lowercase(Locale.getDefault())} interest"
    if (e.basePrice <= p.budget) reasons += "fits your budget"
    if (e.booked.toDouble() / e.capacity < 0.70) reasons += "has useful off-peak capacity"
    if (reasons.isEmpty()) reasons += "strong rating and cultural heritage relevance"
    return reasons.joinToString(", ")
}

private fun offerText(segment: Segment): String = when (segment) {
    Segment.STUDENT -> "student-friendly price + off-peak slot"
    Segment.FAMILY -> "family bundle + storytelling add-on"
    Segment.LUXURY -> "premium private slot + artisan add-on"
    Segment.LOCAL_RESIDENT -> "resident loyalty pass"
    Segment.INTERNATIONAL -> "multilingual route + curated highlights"
    Segment.CULTURAL_EXPLORER -> "personalized itinerary + smart time slot"
}

private fun percent(value: Double): String = "${(value * 100).toInt()}%"
private fun money(value: Double): String = String.format(Locale.US, "%.2f", value)
