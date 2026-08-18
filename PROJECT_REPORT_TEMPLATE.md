# Mobile Systems (MobSys 2026) - Project Final Report

## 1. Project Overview
- **Title of the App:** Travel Logger (Photo & Travel Journal)
- **Participants:** 
  - Participant 1: [Your Name] - Matrikel Number: [Your Matrikel Number]
  - Participant 2: [Partner Name] - Matrikel Number: [Partner Matrikel Number]
- **Target Platform:** Android 14..16 (API 34..37) | Kotlin | Android Studio 2025/2026

## 2. App Description & Features
`Travel Logger` is a modern mobile application designed for urban explorers and travelers to capture, rate, and map their travel memories.

### Core Features & Technical Specification Compliance:
1. **3 Activities Architecture:**
   - **`MainActivity` (Activity 1):** Main gallery displaying saved travel entries in a Material `RecyclerView` with dynamic item count.
   - **`AddTravelActivity` (Activity 2):** Form for creating new memories using hardware sensors (Camera photo capture, GPS location acquisition) and an interactive 5-star custom rating bar.
   - **`TravelDetailActivity` (Activity 3):** Full details view displaying high-res photo, rating, coordinates, notes, and a primary action button to open the location in Google Maps.

2. **Sensors Integration:**
   - **Camera Sensor:** Captures real-time travel photos via `ActivityResultContracts.TakePicturePreview()`.
   - **GPS Sensor:** Retrieves geographical coordinates (Latitude & Longitude) using `LocationManager` (`GPS_PROVIDER` / `NETWORK_PROVIDER`).

3. **CustomView (`Canvas` Drawing):**
   - **`CustomRatingBar.kt`:** Custom Android `View` subclass drawing 5 star shapes from scratch using `Canvas` and `Paint` (`drawPath`, vector math, clipping for partial fill, and touch event tracking).

4. **Intents & Data Exchange:**
   - **Explicit Intents:** Used for navigation between `MainActivity` -> `AddTravelActivity` (with `ActivityResultLauncher`) and `MainActivity` -> `TravelDetailActivity` (with `Serializable` data object transfer).
   - **Implicit Intent:** Opens external mapping applications (Google Maps) via `Intent(Intent.ACTION_VIEW, Uri.parse("geo:lat,lng..."))`.

5. **Data Storage & Persistence:**
   - Implemented `TravelRepository` utilizing `SharedPreferences` and JSON serialization to ensure all travel entries, photos, ratings, and GPS coordinates persist permanently across application restarts.

6. **UI & User Experience:**
   - Modernist Slate/Indigo visual design system with rounded Material Cards, custom typography, internationalized 100% English interface, and responsive empty state layout.

## 3. Special Notes & Technical Decisions
- **Emulator Hardware Handling:** To ensure flawless testing on emulators where camera hardware or fixed GPS signals may be disabled, fallback mechanisms were implemented to generate stylized travel preview bitmaps and fallback location coordinates.
