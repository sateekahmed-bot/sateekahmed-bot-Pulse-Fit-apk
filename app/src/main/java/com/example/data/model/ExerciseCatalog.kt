package com.example.data.model

data class ExerciseTemplate(
    val name: String,
    val muscleGroup: String, // "Chest", "Back", "Legs", "Shoulders", "Arms", "Core", "Cardio"
    val equipment: String, // "Barbell", "Dumbbells", "Machine", "Cable", "Bodyweight", "Cardio"
    val defaultSets: Int = 3,
    val defaultReps: Int = 10,
    val defaultWeightKg: Float = 20f,
    val formCue: String
)

object ExerciseCatalog {
    val allExercises: List<ExerciseTemplate> = listOf(
        // Chest
        ExerciseTemplate("Barbell Bench Press", "Chest", "Barbell", 4, 8, 60f, "Retract shoulder blades, lower bar to mid-chest, drive up explosively."),
        ExerciseTemplate("Incline Dumbbell Press", "Chest", "Dumbbells", 3, 10, 22.5f, "Set bench to 30 degrees, press dumbbells upward in a slight arc."),
        ExerciseTemplate("Cable Chest Flyes", "Chest", "Cable", 3, 12, 12.5f, "Maintain slight elbow bend, squeeze pectoral muscles at peak contraction."),
        ExerciseTemplate("Push-ups", "Chest", "Bodyweight", 3, 15, 0f, "Keep core braced, full depth until chest grazes the floor."),
        ExerciseTemplate("Dips", "Chest", "Bodyweight", 3, 10, 0f, "Lean slightly forward to target lower chest, descend until 90-degree arm bend."),

        // Back
        ExerciseTemplate("Barbell Deadlift", "Back", "Barbell", 4, 6, 90f, "Hinge at hips, maintain flat spine, drive through heels to lockout."),
        ExerciseTemplate("Pull-Ups", "Back", "Bodyweight", 3, 8, 0f, "Full hang at bottom, drive elbows down to ribs, chin over the bar."),
        ExerciseTemplate("Lat Pulldown", "Back", "Cable", 3, 10, 50f, "Pull wide bar towards upper chest, keep chest lifted high."),
        ExerciseTemplate("Barbell Bent-Over Row", "Back", "Barbell", 4, 8, 55f, "Torso at 45 degrees, pull bar to belly button while squeezing lats."),
        ExerciseTemplate("Seated Cable Row", "Back", "Cable", 3, 12, 45f, "Keep back neutral, squeeze shoulder blades together at completion."),

        // Legs
        ExerciseTemplate("Barbell Back Squat", "Legs", "Barbell", 4, 8, 80f, "Break at hips and knees simultaneously, descend below parallel."),
        ExerciseTemplate("Romanian Deadlift", "Legs", "Barbell", 3, 10, 60f, "Push hips backward, stretch hamstrings, keep bar close to shins."),
        ExerciseTemplate("Leg Press", "Legs", "Machine", 3, 12, 140f, "Feet shoulder-width on platform, control descent without rounding lower back."),
        ExerciseTemplate("Walking Dumbbell Lunges", "Legs", "Dumbbells", 3, 12, 15f, "Long strides, front knee at 90 degrees, torso upright."),
        ExerciseTemplate("Standing Calf Raises", "Legs", "Machine", 4, 15, 45f, "Full stretch at bottom, hold 1-second pause at top peak."),

        // Shoulders
        ExerciseTemplate("Overhead Barbell Press", "Shoulders", "Barbell", 4, 8, 40f, "Tight core and glutes, press straight overhead clearing head."),
        ExerciseTemplate("Dumbbell Lateral Raise", "Shoulders", "Dumbbells", 4, 15, 10f, "Lead with elbows, raise to parallel with ground, control descent."),
        ExerciseTemplate("Rear Delt Reverse Fly", "Shoulders", "Dumbbells", 3, 15, 8f, "Hinge forward, pull dumbbells out to sides squeezing rear delts."),
        ExerciseTemplate("Face Pulls", "Shoulders", "Cable", 3, 15, 20f, "Pull rope towards bridge of nose, rotate hands outward."),

        // Arms
        ExerciseTemplate("Barbell Bicep Curl", "Arms", "Barbell", 3, 10, 30f, "Elbows pinned to sides, curl bar up without swinging torso."),
        ExerciseTemplate("Incline Dumbbell Curl", "Arms", "Dumbbells", 3, 12, 12f, "Seated on incline bench for maximum long-head bicep stretch."),
        ExerciseTemplate("Triceps Rope Pushdown", "Arms", "Cable", 3, 12, 25f, "Keep upper arms stationary, spread rope apart at bottom."),
        ExerciseTemplate("Skull Crushers", "Arms", "Barbell", 3, 10, 25f, "Lower EZ bar towards forehead, extend through triceps."),

        // Core
        ExerciseTemplate("Hanging Leg Raise", "Core", "Bodyweight", 3, 12, 0f, "Hang from bar, curl pelvis upward, avoid swinging."),
        ExerciseTemplate("Cable Woodchoppers", "Core", "Cable", 3, 12, 15f, "Diagonal torso rotation, engage obliques and brace core."),
        ExerciseTemplate("Plank Hold", "Core", "Bodyweight", 3, 45, 0f, "Straight line from heels to head, glutes and abs squeezed tight."),

        // Cardio / HIIT
        ExerciseTemplate("HIIT Jump Rope", "Cardio", "Cardio", 4, 60, 0f, "Fast interval skip, stay light on balls of feet."),
        ExerciseTemplate("Treadmill Incline Run", "Cardio", "Cardio", 1, 20, 0f, "Steady state or interval incline jog for maximum VO2 max burn."),
        ExerciseTemplate("Rowing Machine Sprint", "Cardio", "Cardio", 5, 250, 0f, "Full stroke drive with legs, lean back, finish with arms.")
    )
}
