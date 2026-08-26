package com.saeidsharifi.italyaiculturaltourism

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

data class Experience(
    val title: String,
    val city: String,
    val category: String,
    val basePrice: Double,
    val capacity: Int,
    val story: String,
    val segments: List<String>
)

private val experiences = listOf(
    Experience(
        "Ancient Rome Heritage Walk", "Rome", "Heritage", 24.0, 28,
        "A guided cultural route connecting the Colosseum area, Roman Forum storytelling, and local identity.",
        listOf("History lovers", "First-time visitors", "Students")
    ),
    Experience(
        "Made in Italy Artisan Workshop", "Florence", "Craftsmanship", 45.0, 12,
        "A small-group workshop focused on Italian craftsmanship, authenticity, and experiential learning.",
        listOf("Creative tourists", "Families", "Luxury culture seekers")
    ),
    Experience(
        "Wine and Food Cultural Experience", "Tuscany", "Food & Wine", 68.0, 18,
        "A regional food and wine journey that links local production, storytelling, and cultural value.",
        listOf("Couples", "Food travelers", "High-value tourists")
    ),
    Experience(
        "Hidden Museum Evening Slot", "Rome", "Museum", 18.0, 40,
        "An off-peak museum visit designed to reduce crowding and improve visitor satisfaction.",
        listOf("Budget travelers", "Repeat visitors", "Local residents")
    )
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { App() }
    }
}

@Composable
fun App() {
    var tab by remember { mutableIntStateOf(0) }
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = androidx.compose.ui.graphics.Color(0xFF245C4A),
            secondary = androidx.compose.ui.graphics.Color(0xFF8B5E34),
            tertiary = androidx.compose.ui.graphics.Color(0xFFB43B2B)
        )
    ) {
        Scaffold(
            topBar = { TopBar() },
            bottomBar = {
                NavigationBar {
                    listOf("Home", "AI", "Pricing", "Dashboard").forEachIndexed { index, label ->
                        NavigationBarItem(
                            selected = tab == index,
                            onClick = { tab = index },
                            label = { Text(label) },
                            icon = { Text(listOf("🏛️", "🤖", "€", "📊")[index]) }
                        )
                    }
                }
            }
        ) { padding ->
            Box(Modifier.padding(padding)) {
                when (tab) {
                    0 -> HomeScreen()
                    1 -> AiRecommendationScreen()
                    2 -> DynamicPricingScreen()
                    else -> RevenueDashboardScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    CenterAlignedTopAppBar(
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Italy AI Cultural Tourism", fontWeight = FontWeight.Bold)
                Text("Made in Italy Revenue Assistant", style = MaterialTheme.typography.labelSmall)
            }
        }
    )
}

@Composable
fun HomeScreen() {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            HeroCard()
        }
        item { SectionTitle("Cultural Experiences") }
        items(experiences) { ExperienceCard(it) }
    }
}

@Composable
fun HeroCard() {
    ElevatedCard(shape = RoundedCornerShape(24.dp)) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("AI-driven marketing + cultural tourism + revenue management", fontWeight = FontWeight.Bold)
            Text("This prototype helps cultural tourism managers personalize visitor offers, simulate dynamic pricing, and monitor revenue performance for Italian cultural experiences.")
            AssistChip(onClick = {}, label = { Text("Based on Saeid Sharifi's thesis concept") })
        }
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
}

@Composable
fun ExperienceCard(exp: Experience) {
    ElevatedCard(shape = RoundedCornerShape(18.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(exp.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("${exp.city} • ${exp.category}")
            Text(exp.story)
            Text("Base price: €${exp.basePrice.roundToInt()} • Capacity: ${exp.capacity}")
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                exp.segments.take(2).forEach { AssistChip(onClick = {}, label = { Text(it) }) }
            }
        }
    }
}

@Composable
fun AiRecommendationScreen() {
    var interest by remember { mutableStateOf("History") }
    var budget by remember { mutableStateOf(45f) }
    val recommended = remember(interest, budget) {
        experiences.filter { it.category.contains(interest, true) || it.basePrice <= budget }
            .minByOrNull { kotlin.math.abs(it.basePrice - budget) } ?: experiences.first()
    }
    Column(Modifier.padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        SectionTitle("AI Recommendation Engine")
        Text("Visitor interest")
        SegmentedButtonRow(interest, listOf("History", "Museum", "Craft", "Food")) { interest = it }
        Text("Budget: €${budget.roundToInt()}")
        Slider(value = budget, onValueChange = { budget = it }, valueRange = 10f..100f)
        ElevatedCard(shape = RoundedCornerShape(20.dp)) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Recommended Experience", fontWeight = FontWeight.Bold)
                Text(recommended.title, style = MaterialTheme.typography.titleLarge)
                Text("Why: matches visitor interest, budget sensitivity, and cultural value.")
                Text("Suggested marketing message:")
                Text("Discover an authentic ${recommended.category.lowercase()} experience in ${recommended.city}, designed around storytelling, personalization, and Made in Italy value.")
            }
        }
    }
}

@Composable
fun SegmentedButtonRow(selected: String, options: List<String>, onSelect: (String) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        options.forEach { opt ->
            FilterChip(selected = selected == opt, onClick = { onSelect(opt) }, label = { Text(opt) })
        }
    }
}

@Composable
fun DynamicPricingScreen() {
    var demand by remember { mutableFloatStateOf(65f) }
    var occupancy by remember { mutableFloatStateOf(70f) }
    var weather by remember { mutableFloatStateOf(50f) }
    val base = 30.0
    val multiplier = 1 + ((demand - 50) * 0.006) + ((occupancy - 50) * 0.005) + ((weather - 50) * 0.002)
    val price = (base * multiplier).coerceIn(15.0, 55.0)
    Column(Modifier.padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        SectionTitle("Dynamic Pricing Simulator")
        ParameterSlider("Demand index", demand) { demand = it }
        ParameterSlider("Current occupancy", occupancy) { occupancy = it }
        ParameterSlider("Weather / event attractiveness", weather) { weather = it }
        ElevatedCard(shape = RoundedCornerShape(20.dp)) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("AI price recommendation", fontWeight = FontWeight.Bold)
                Text("€${String.format("%.2f", price)}", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
                Text("Strategy: increase yield in high-demand moments, encourage off-peak visits with lower prices, and protect visitor satisfaction.")
            }
        }
    }
}

@Composable
fun ParameterSlider(label: String, value: Float, onChange: (Float) -> Unit) {
    Text("$label: ${value.roundToInt()}%")
    Slider(value = value, onValueChange = onChange, valueRange = 0f..100f)
}

@Composable
fun RevenueDashboardScreen() {
    val totalCapacity = experiences.sumOf { it.capacity }
    val baseRevenue = experiences.sumOf { it.basePrice * it.capacity }
    val aiRevenue = baseRevenue * 1.18
    Column(Modifier.padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        SectionTitle("Revenue Dashboard")
        MetricCard("Total daily capacity", "$totalCapacity visitors")
        MetricCard("Baseline potential revenue", "€${baseRevenue.roundToInt()}")
        MetricCard("AI-optimized scenario", "€${aiRevenue.roundToInt()}")
        MetricCard("Estimated uplift", "+18%")
        ElevatedCard(shape = RoundedCornerShape(20.dp)) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Managerial Insights", fontWeight = FontWeight.Bold)
                Text("• Use personalization to match visitors with relevant cultural experiences.")
                Text("• Apply time-based prices to reduce crowding.")
                Text("• Use data from searches, bookings, reviews, events, and seasonality.")
                Text("• Keep pricing transparent to protect trust and accessibility.")
            }
        }
    }
}

@Composable
fun MetricCard(title: String, value: String) {
    ElevatedCard(shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(18.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(title)
            Text(value, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
        }
    }
}
