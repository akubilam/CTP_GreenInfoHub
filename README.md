# GreenInfo Hub - Gamified Recycling Platform

GreenInfo Hub is an Android-based gamified recycling application developed using **Kotlin**. It aims to solve the waste crisis in Hong Kong by providing accurate recycling knowledge and encouraging sustainable habits through a behavioral incentive model (Nudge Theory & SDT).

## 🚀 Key Features
- **Smart Recycling Map**: Locate recycling stations within 1km using Google Maps SDK.
- **AI Label Scanner**: Instant identification of plastic codes and processing tips.
- **Gamification System**: Dual-track system (Points & XP) to reward environmental actions.
- **Comprehensive Guide**: Interactive UI for 1-7 plastic types and paper recycling.

## 📂 Project Structure & Navigation
To assist the technical review, the core components can be found in the following paths:

### 🛠️ Kotlin Logic (`app/src/main/java/com/example/myapplication/`)
- `MainActivity.kt`: Core dashboard and real-time UI synchronization.
- `MapActivity.kt`: Implementation of geospatial services and custom InfoWindows.
- `ScannerActivity.kt`: Logic for AI recognition and instructional overlays.
- `StoreActivity.kt` & `StoreAdapter.kt`: Data persistence using GSON and point redemption logic.
- `LevelManager.kt`: Singleton-based XP and leveling algorithm.

### 🎨 UI Design (`app/src/main/res/layout/`)
- `activity_main.xml`: Main dashboard interface.
- `custom_info_window.xml`: Solution for the auto-wrapping text in Map InfoWindows.
- `item_store.xml`: Design for the rewards catalog list.

### 📊 Assets
- `app/src/main/assets/recycle_stations.csv`: Pre-processed dataset for Hong Kong recycling points.

## ⚙️ Tech Stack
- **Language**: Kotlin (Null Safety, Data Classes)
- **Architecture**: Singleton Pattern, Activity Lifecycle Management
- **Persistence**: SharedPreferences + GSON Serialization
- **External APIs**: Google Maps SDK, FusedLocationProviderClient
