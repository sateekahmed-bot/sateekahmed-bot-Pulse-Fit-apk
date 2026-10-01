package com.example.data.model

data class FoodItem(
    val name: String,
    val category: String, // "Proteins", "Carbs & Grains", "Fats & Oils", "Fruits & Veggies", "Dairy & Snacks"
    val servingSize: String, // "100g", "1 Scoop (30g)", "1 Large (50g)", "1 Medium (118g)"
    val baseCalories: Int,
    val baseProtein: Float,
    val baseCarbs: Float,
    val baseFats: Float
) {
    fun calculateForQuantity(multiplier: Float): FoodItem {
        return copy(
            baseCalories = (baseCalories * multiplier).toInt(),
            baseProtein = (baseProtein * multiplier * 10f).toInt() / 10f,
            baseCarbs = (baseCarbs * multiplier * 10f).toInt() / 10f,
            baseFats = (baseFats * multiplier * 10f).toInt() / 10f
        )
    }
}

object FoodCatalog {
    val foods: List<FoodItem> = listOf(
        // Proteins
        FoodItem("Boneless Chicken Breast (Cooked)", "Proteins", "100g", 165, 31.0f, 0.0f, 3.6f),
        FoodItem("Whey Protein Isolate", "Proteins", "1 Scoop (30g)", 120, 25.0f, 2.0f, 1.0f),
        FoodItem("Wild Atlantic Salmon (Baked)", "Proteins", "100g", 208, 22.0f, 0.0f, 13.0f),
        FoodItem("Lean Ground Beef 90/10", "Proteins", "100g", 215, 26.0f, 0.0f, 11.5f),
        FoodItem("Whole Large Egg", "Proteins", "1 Egg (50g)", 72, 6.3f, 0.4f, 4.8f),
        FoodItem("Egg Whites", "Proteins", "100g", 52, 11.0f, 0.7f, 0.2f),
        FoodItem("Canned Albacore Tuna", "Proteins", "100g", 128, 28.0f, 0.0f, 1.0f),
        FoodItem("Ground Turkey Breast 93/7", "Proteins", "100g", 150, 22.0f, 0.0f, 7.0f),
        FoodItem("Tofu (Firm)", "Proteins", "100g", 83, 10.0f, 2.0f, 5.0f),

        // Carbs & Grains
        FoodItem("Rolled Oats (Dry)", "Carbs & Grains", "50g", 190, 6.5f, 34.0f, 3.0f),
        FoodItem("White Jasmine Rice (Cooked)", "Carbs & Grains", "100g", 130, 2.7f, 28.0f, 0.3f),
        FoodItem("Brown Rice (Cooked)", "Carbs & Grains", "100g", 112, 2.6f, 23.5f, 0.9f),
        FoodItem("Baked Sweet Potato", "Carbs & Grains", "100g", 90, 2.0f, 20.7f, 0.2f),
        FoodItem("Whole Wheat Bread", "Carbs & Grains", "1 Slice (35g)", 80, 4.0f, 14.0f, 1.0f),
        FoodItem("Quinoa (Cooked)", "Carbs & Grains", "100g", 120, 4.4f, 21.3f, 1.9f),
        FoodItem("Pasta (Cooked)", "Carbs & Grains", "100g", 158, 5.8f, 31.0f, 0.9f),
        FoodItem("Rice Cakes (Plain)", "Carbs & Grains", "2 Cakes (18g)", 70, 1.5f, 15.0f, 0.5f),

        // Fruits & Veggies
        FoodItem("Banana", "Fruits & Veggies", "1 Medium (118g)", 105, 1.3f, 27.0f, 0.3f),
        FoodItem("Fresh Blueberries", "Fruits & Veggies", "100g", 57, 0.7f, 14.5f, 0.3f),
        FoodItem("Apple with Skin", "Fruits & Veggies", "1 Medium (182g)", 95, 0.5f, 25.0f, 0.3f),
        FoodItem("Steamed Broccoli", "Fruits & Veggies", "100g", 35, 2.4f, 7.2f, 0.4f),
        FoodItem("Baby Spinach", "Fruits & Veggies", "100g", 23, 2.9f, 3.6f, 0.4f),
        FoodItem("Strawberries", "Fruits & Veggies", "100g", 32, 0.7f, 7.7f, 0.3f),

        // Fats & Oils
        FoodItem("Hass Avocado", "Fats & Oils", "1/2 Fruit (75g)", 120, 1.5f, 6.4f, 11.0f),
        FoodItem("Extra Virgin Olive Oil", "Fats & Oils", "1 Tbsp (14ml)", 119, 0.0f, 0.0f, 13.5f),
        FoodItem("Raw Almonds", "Fats & Oils", "28g (Handful)", 164, 6.0f, 6.1f, 14.2f),
        FoodItem("Natural Peanut Butter", "Fats & Oils", "2 Tbsp (32g)", 190, 8.0f, 7.0f, 16.0f),
        FoodItem("Walnuts", "Fats & Oils", "28g", 185, 4.3f, 3.9f, 18.5f),
        FoodItem("Chia Seeds", "Fats & Oils", "1 Tbsp (12g)", 58, 2.0f, 5.0f, 3.7f),

        // Dairy & Snacks
        FoodItem("Greek Yogurt 0% Fat (Plain)", "Dairy & Snacks", "150g", 90, 15.0f, 5.5f, 0.0f),
        FoodItem("Cottage Cheese 2% Low Fat", "Dairy & Snacks", "100g", 84, 11.0f, 4.3f, 2.3f),
        FoodItem("Almond Milk (Unsweetened)", "Dairy & Snacks", "1 Cup (240ml)", 30, 1.0f, 1.0f, 2.5f),
        FoodItem("Fairlife Ultra-Filtered Milk", "Dairy & Snacks", "1 Cup (240ml)", 120, 13.0f, 6.0f, 4.5f),
        FoodItem("Protein Bar (Chocolate Peanut)", "Dairy & Snacks", "1 Bar (60g)", 210, 20.0f, 22.0f, 7.0f),
        FoodItem("Dark Chocolate 85%", "Dairy & Snacks", "25g", 145, 2.2f, 9.0f, 12.0f)
    )
}
