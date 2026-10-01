package com.example.ui.screens.nutrition

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.FoodCatalog
import com.example.data.model.FoodItem
import com.example.ui.theme.CarbsColor
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.FatsColor
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.ProteinColor

@Composable
fun AddMealDialog(
    initialMealType: String = "Breakfast",
    onDismiss: () -> Unit,
    onSaveMeal: (mealType: String, name: String, calories: Int, protein: Float, carbs: Float, fats: Float) -> Unit
) {
    var mealType by remember { mutableStateOf(initialMealType) }
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Food Library, 1 = Custom Food

    // Custom food states
    var foodName by remember { mutableStateOf("") }
    var caloriesText by remember { mutableStateOf("") }
    var proteinText by remember { mutableStateOf("") }
    var carbsText by remember { mutableStateOf("") }
    var fatsText by remember { mutableStateOf("") }

    // Library food states
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedFoodItem by remember { mutableStateOf<FoodItem?>(null) }
    var servingMultiplier by remember { mutableFloatStateOf(1.0f) }

    val mealTypes = listOf("Breakfast", "Lunch", "Dinner", "Snack", "Post-Workout")
    val foodCategories = listOf("All", "Proteins", "Carbs & Grains", "Fats & Oils", "Fruits & Veggies", "Dairy & Snacks")

    val filteredFoods = remember(searchQuery, selectedCategory) {
        FoodCatalog.foods.filter { item ->
            (selectedCategory == "All" || item.category == selectedCategory) &&
                    (searchQuery.isBlank() || item.name.contains(searchQuery, ignoreCase = true))
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .testTag("add_meal_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Restaurant,
                            contentDescription = null,
                            tint = FlameOrange
                        )
                        Text(
                            text = "Log Daily Intake",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_add_meal_button")) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Meal category row
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(mealTypes) { type ->
                        FilterChip(
                            selected = mealType == type,
                            onClick = { mealType = type },
                            label = { Text(type, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Tab Selector: Library vs Custom
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Food Database", style = MaterialTheme.typography.labelMedium) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Custom / Quick", style = MaterialTheme.typography.labelMedium) }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (selectedTab == 0) {
                    // FOOD LIBRARY MODE
                    if (selectedFoodItem != null) {
                        // Detailed Serving Size Adjuster View
                        val currentFood = selectedFoodItem!!
                        val calculated = currentFood.calculateForQuantity(servingMultiplier)

                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = currentFood.name,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Base Serving: ${currentFood.servingSize}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    IconButton(onClick = { selectedFoodItem = null }) {
                                        Icon(Icons.Default.Close, contentDescription = "Deselect", modifier = Modifier.size(18.dp))
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Calculated Nutrition
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = "${calculated.baseCalories} kcal",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = FlameOrange
                                        )
                                        Text("Calories", style = MaterialTheme.typography.labelSmall)
                                    }
                                    Column {
                                        Text(
                                            text = "${calculated.baseProtein}g",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = ProteinColor
                                        )
                                        Text("Protein", style = MaterialTheme.typography.labelSmall)
                                    }
                                    Column {
                                        Text(
                                            text = "${calculated.baseCarbs}g",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = CarbsColor
                                        )
                                        Text("Carbs", style = MaterialTheme.typography.labelSmall)
                                    }
                                    Column {
                                        Text(
                                            text = "${calculated.baseFats}g",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = FatsColor
                                        )
                                        Text("Fats", style = MaterialTheme.typography.labelSmall)
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Text(
                                    text = "Serving Multiplier: ${String.format("%.1f", servingMultiplier)}x",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf(0.5f, 1.0f, 1.5f, 2.0f, 3.0f).forEach { mult ->
                                        FilterChip(
                                            selected = servingMultiplier == mult,
                                            onClick = { servingMultiplier = mult },
                                            label = { Text("${mult}x", style = MaterialTheme.typography.labelSmall) }
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Button(
                                    onClick = {
                                        val finalName = "${currentFood.name} (${String.format("%.1f", servingMultiplier)}x ${currentFood.servingSize})"
                                        onSaveMeal(
                                            mealType,
                                            finalName,
                                            calculated.baseCalories,
                                            calculated.baseProtein,
                                            calculated.baseCarbs,
                                            calculated.baseFats
                                        )
                                        onDismiss()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("confirm_log_food_button")
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Add to $mealType", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    } else {
                        // Food search & browsing list
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search chicken, rice, eggs, oats...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("food_search_input")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(foodCategories) { cat ->
                                FilterChip(
                                    selected = selectedCategory == cat,
                                    onClick = { selectedCategory = cat },
                                    label = { Text(cat, style = MaterialTheme.typography.labelSmall) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyColumn(
                            modifier = Modifier.height(280.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(filteredFoods) { food ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedFoodItem = food
                                            servingMultiplier = 1.0f
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = food.name,
                                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "${food.servingSize} • ${food.baseCalories} kcal",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = FlameOrange
                                            )
                                            Text(
                                                text = "P: ${food.baseProtein}g • C: ${food.baseCarbs}g • F: ${food.baseFats}g",
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Select",
                                            tint = CyanNeon,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // CUSTOM / QUICK MACRO ENTRY MODE
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        OutlinedTextField(
                            value = foodName,
                            onValueChange = { foodName = it },
                            label = { Text("Meal / Food Name") },
                            placeholder = { Text("e.g. Protein Smoothie Bowl") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("food_name_input")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = caloriesText,
                            onValueChange = { caloriesText = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Calories (kcal) *") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("calories_input")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Quick calorie buttons
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(100, 250, 500).forEach { delta ->
                                OutlinedButton(
                                    onClick = {
                                        val cur = caloriesText.toIntOrNull() ?: 0
                                        caloriesText = (cur + delta).toString()
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("+$delta")
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = proteinText,
                                onValueChange = { proteinText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                                label = { Text("Protein (g)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("protein_input")
                            )
                            OutlinedTextField(
                                value = carbsText,
                                onValueChange = { carbsText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                                label = { Text("Carbs (g)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("carbs_input")
                            )
                            OutlinedTextField(
                                value = fatsText,
                                onValueChange = { fatsText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                                label = { Text("Fats (g)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("fats_input")
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                val cal = caloriesText.toIntOrNull() ?: 0
                                val p = proteinText.toFloatOrNull() ?: 0f
                                val c = carbsText.toFloatOrNull() ?: 0f
                                val f = fatsText.toFloatOrNull() ?: 0f
                                val name = foodName.ifBlank { "Custom $mealType" }
                                onSaveMeal(mealType, name, cal, p, c, f)
                                onDismiss()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("save_meal_button"),
                            enabled = foodName.isNotBlank() || caloriesText.isNotBlank()
                        ) {
                            Text("Save to $mealType", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
