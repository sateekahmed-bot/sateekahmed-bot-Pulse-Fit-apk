package com.example.ui.screens.nutrition

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.model.DailyTarget
import com.example.ui.theme.FlameOrange

@Composable
fun EditDailyGoalsDialog(
    currentTarget: DailyTarget,
    onDismiss: () -> Unit,
    onSaveTarget: (DailyTarget) -> Unit
) {
    var caloriesText by remember { mutableStateOf(currentTarget.calorieGoal.toString()) }
    var proteinText by remember { mutableStateOf(currentTarget.proteinGoalGrams.toString()) }
    var carbsText by remember { mutableStateOf(currentTarget.carbsGoalGrams.toString()) }
    var fatsText by remember { mutableStateOf(currentTarget.fatsGoalGrams.toString()) }
    var waterText by remember { mutableStateOf(currentTarget.waterGoalMl.toString()) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("edit_daily_goals_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
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
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = FlameOrange
                        )
                        Text(
                            text = "Edit Daily Nutrition Goals",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = caloriesText,
                    onValueChange = { caloriesText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Daily Calorie Target (kcal)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("target_calories_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = proteinText,
                        onValueChange = { proteinText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Protein (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("target_protein_input")
                    )
                    OutlinedTextField(
                        value = carbsText,
                        onValueChange = { carbsText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Carbs (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("target_carbs_input")
                    )
                    OutlinedTextField(
                        value = fatsText,
                        onValueChange = { fatsText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Fats (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("target_fats_input")
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = waterText,
                    onValueChange = { waterText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Water Intake Target (ml)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("target_water_input")
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val newCal = caloriesText.toIntOrNull() ?: currentTarget.calorieGoal
                        val newP = proteinText.toIntOrNull() ?: currentTarget.proteinGoalGrams
                        val newC = carbsText.toIntOrNull() ?: currentTarget.carbsGoalGrams
                        val newF = fatsText.toIntOrNull() ?: currentTarget.fatsGoalGrams
                        val newW = waterText.toIntOrNull() ?: currentTarget.waterGoalMl

                        onSaveTarget(
                            currentTarget.copy(
                                calorieGoal = newCal,
                                proteinGoalGrams = newP,
                                carbsGoalGrams = newC,
                                fatsGoalGrams = newF,
                                waterGoalMl = newW
                            )
                        )
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("save_goals_button")
                ) {
                    Text("Save Goals", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
