# Photo & Travel Logger

An Android application designed to help independent travelers log their journeys, capturing the essence of local urban architecture, cultural histories, and memorable spots like specialty coffee shops.

## Overview
This project is a personal travel diary that fulfills key mobile application development requirements in Android Studio, including the implementation of multiple Activities, Intents, Custom Views, and hardware sensor integration.

## Features & Architecture

The application is structured into three main components:

### 1. Travel Gallery (Activity 1)
*   **Home Screen:** Serves as the primary landing page.
*   **RecyclerView:** Utilizes a mandatory `RecyclerView` to display the travel history. The list shows photo thumbnails, location names, and user ratings.
*   **Navigation:** Includes an action to add a new trip. This launches an `Intent` to Activity 2, specifically configured to wait for the newly saved data to return.

### 2. New Travel Record (Activity 2)
*   **Hardware Sensors:** Actively reads from at least two device sensors to fulfill structural requirements:
    *   **GPS:** Captures current geographical coordinates.
    *   **Camera:** Captures photos of the landscape or points of interest.
*   **CustomView:** Features a custom-built interactive star rating bar. Drawn from scratch using `Canvas`, it replaces standard UI buttons by allowing the user to tap and set a trip rating dynamically.
*   **Data Handling:** After capturing the photo, location, rating, and a text comment, an `Intent` returns all consolidated data back to Activity 1.

### 3. Memory Details (Activity 3)
*   **Structural Requirement:** Fulfills the project rule of containing a minimum of 3 Activities.
*   **Data Display:** Triggered by tapping a saved trip item in Activity 1's `RecyclerView`. It displays a full-screen view of the photo, the user's comment, and the assigned rating.
*   **Implicit Intent:** Extracts the saved GPS location data and uses an Implicit Intent to open those exact coordinates directly in the device's Google Maps application.