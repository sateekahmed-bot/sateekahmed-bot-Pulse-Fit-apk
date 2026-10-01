package com.example.ui.screens.plans

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.FlameOrange

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreatePlanWizardDialog(
    initialGoal: String = "Build Muscle",
    initialLevel: String = "Intermediate",
    initialEquipment: String = "Full Gym",
    initialDays: Int = 4,
    onDismiss: () -> Unit,
    onCreatePlan: (title: String, goal: String, level: String, equipment: String, daysPerWeek: Int, durationWeeks: Int) -> Unit
) {
    var goal by remember { mutableStateOf(initialGoal) }
    var level by remember { mutableStateOf(initialLevel) }
    var equipment by remember { mutableStateOf(initialEquipment) }
    var daysPerWeek by remember { mutableIntStateOf(initialDays) }
    var durationWeeks by remember { mutableIntStateOf(8) }
    var planTitle by remember { mutableStateOf("") }

    val goals = listOf("Build Muscle", "Fat Loss & Cut", "Strength & Power", "Athletic Endurance")
    val levels = listOf("Beginner", "Intermediate", "Advanced")
    val equipmentList = listOf("Full Gym", "Dumbbells Only", "Bodyweight & Bands")
    val daysOptions = listOf(3, 4, 5, 6)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
                .testTag("create_plan_wizard_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
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
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Personalized",
                            tint = CyanNeon
                        )
                        Text(
                            text = "Personalize Plan",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_plan_wizard_button")) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Text(
                    text = "Crafted for your body, equipment, and weekly schedule.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Primary Goal
                Text(
                    text = "1. Primary Fitness Goal",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    goals.forEach { g ->
                        FilterChip(
                            selected = goal == g,
                            onClick = { goal = g },
                            label = { Text(g) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Experience Level
                Text(
                    text = "2. Experience Level",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    levels.forEach { lvl ->
                        FilterChip(
                            selected = level == lvl,
                            onClick = { level = lvl },
                            label = { Text(lvl) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Equipment
                Text(
                    text = "3. Equipment Available",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    equipmentList.forEach { eq ->
                        FilterChip(
                            selected = equipment == eq,
                            onClick = { equipment = eq },
                            label = { Text(eq) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Days per week
                Text(
                    text = "4. Training Days Per Week",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    daysOptions.forEach { d ->
                        FilterChip(
                            selected = daysPerWeek == d,
                            onClick = { daysPerWeek = d },
                            label = { Text("$d Days") }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Plan Name
                OutlinedTextField(
                    value = planTitle,
                    onValueChange = { planTitle = it },
                    label = { Text("Plan Name (Optional)") },
                    placeholder = { Text("$goal $daysPerWeek-Day Split") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("plan_title_input")
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val finalTitle = planTitle.ifBlank { "$goal $daysPerWeek-Day Elite" }
                        onCreatePlan(finalTitle, goal, level, equipment, daysPerWeek, durationWeeks)
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("submit_create_plan_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                    Text(
                        text = "Generate & Activate Plan",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}
