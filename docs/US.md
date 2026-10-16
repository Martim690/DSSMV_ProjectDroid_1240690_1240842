# User Stories Specification — MRGym

This document defines all functional User Stories for the MRGym native Android application, numbered sequentially without gaps, tracking their descriptions, artefacts, and implementation states.

---

## Summary Table

| US ID | Title | Description | State |
|---|---|---|---|
| **US01** | User Login | Authenticate registered users via RestDB and persist session. | **finished** |
| **US02** | User Registration | Create new user accounts in RestDB cloud database. | **finished** |
| **US03** | User Logout | Clear persistent local session and reset back stack. | **finished** |
| **US04** | Session Persistence & Auto-Login | Check local session on launch and bypass login automatically. | **finished** |
| **US05** | Authentication Error Feedback | Handle login errors with custom `InvalidCredentialsException`. | **finished** |
| **US06** | Main Toolbar & Options Menu | Provide application header with Profile and Logout actions. | **finished** |
| **US07** | User Profile Screen | View authenticated account details and logout action. | **finished** |
| **US08** | Core Screen Navigation | Explicit Intent navigation between Main, Workouts, Profile, and Journal. | **finished** |
| **US09** | Workout Management Interface | Interface layout for workouts with empty state and Add button. | **finished** |
| **US10** | Evolution Journal (Camera & Gallery) | Capture body progress photos, display chronological gallery, and delete. | **finished** |
| **US11** | Public Exercises List (Wger API) | Fetch and display public exercises from Wger REST API. | **working** |
| **US12** | Create Workout Plan | Compose and persist workout plans with selected exercises in RestDB. | **working** |
| **US13** | View User Workout Plans | Retrieve and display user's saved workout plans from RestDB. | **working** |
| **US14** | Edit Workout Plan | Update exercises and name of an existing workout plan in RestDB. | **working** |
| **US15** | Delete Workout Plan | Remove an unwanted workout plan from RestDB. | **working** |
| **US16** | Cross-Screen Data Passing | Pass exercise/workout details between activities via Intent extras. | **working** |
| **US17** | GPS Workout Location Tracking | Record workout geographical coordinates via device GPS sensor. | **working** |

