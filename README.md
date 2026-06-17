# FitMind: AI-Powered Fitness & Nutrition Tracker

<p align="center">
  <img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" />
  <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white" />
  <img src="https://img.shields.io/badge/Room_DB-4285F4?style=for-the-badge&logo=sqlite&logoColor=white" />
  <img src="https://img.shields.io/badge/Material_UI-0081CB?style=for-the-badge&logo=material-design&logoColor=white" />
  <img src="https://img.shields.io/badge/Groq_AI-1E1E1E?style=for-the-badge&logo=openai&logoColor=white" />
</p>

FitMind is a modern, enterprise-grade Android application designed to track fitness and nutrition using Artificial Intelligence. Built with a robust architecture using Java and Android SDK 36, the app leverages the Groq API (LLaMA 3) to dynamically parse natural language meal entries into structured macro-nutritional data and generate personalized weekly workout plans.

---

## Features

- **AI Meal Analyzer:** Type your meals in natural language (e.g., "150g chicken breast and rice") and let the AI automatically calculate Calories, Protein, Carbs, and Fat.
- **AI Workout Generator:** Automatically generates a personalized, multi-week workout plan complete with sets, reps, and exercise tips based on user biometrics.
- **Smart Water Tracking:** Easy-to-use water tracking system with dynamic +250ml, +330ml, +500ml quick-add buttons and a revert option.
- **Advanced Body Measurements:** Track your neck, waist, and hip measurements. The app automatically calculates your Body Fat Percentage using the US Navy Formula.
- **Real-time Dashboard:** A responsive, fragment-based UI utilizing `NestedScrollView` and `BottomNavigationView` for seamless transitions between daily stats, workouts, and profiles.
- **Secure Local Storage:** 100% offline-first local data persistence using Room Database (v7 schema) with automated asynchronous background tasks (`Executors`).

---

## Tech Stack

- **Platform:** Android (SDK 36, Min SDK 27)
- **Language:** Java
- **UI Architecture:** Fragment-based Navigation, Material Design 3, ConstraintLayout
- **Database:** Room Database (SQLite), SharedPreferences
- **Networking:** Retrofit2, OkHttp3 (with Logging Interceptor), Gson
- **AI Integration:** Groq API (JSON Mode / LLaMA 3)
- **Build System:** Gradle (Kotlin DSL)

---

## Project Architecture

The project strictly follows modular programming principles, dividing responsibilities into specific packages:

- `com.example.fitmind.ui.*` - Contains Activities, Fragments, and Adapters (Home, Workout, Profile, Measurements, Onboarding).
- `com.example.fitmind.db.*` - Room Database configurations and Data Access Objects (DAOs).
- `com.example.fitmind.model.*` - Database Entity classes (`UserProfile`, `CalorieLog`, `WaterLog`, `MeasurementLog`).
- `com.example.fitmind.api.*` - Retrofit clients, Groq API interfaces, and request/response models.
- `com.example.fitmind.util.*` - Utility classes for complex calculations (`CalorieCalculator`, `WaterCalculator`).

---

## Built-in Algorithms

FitMind actively processes data using industry-standard fitness algorithms:
1. **BMR Calculation:** Uses the Harris-Benedict Equation based on age, weight, height, and gender.
2. **Target Calories:** Dynamically models caloric deficit/surplus depending on user goals (Lose Weight, Gain Muscle, Stay Fit).
3. **Body Fat:** Calculates accurate body fat percentages using the US Navy Body Fat Formula.
4. **Water Needs:** Computes daily hydration goals mathematically.

---

## Setup & Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/yourusername/FitMind.git
   ```
2. **Open in Android Studio:** Open the project directory using Android Studio.
3. **API Key Configuration:**
   - Create a `local.properties` file in the root directory of the project (if it doesn't exist).
   - Add your Groq API key:
     ```properties
     GROQ_API_KEY="YOUR_API_KEY_HERE"
     ```
4. **Build and Run:** Sync Gradle and run the application on an emulator or physical device.
