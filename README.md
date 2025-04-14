# 📌 PHI-Soccer-MVC

A **JavaFX-based** Soccer Management System built using the **MVC (Model-View-Controller)** pattern. This project is designed to manage teams, players, and matches efficiently with an interactive UI.

---

## 🚀 Features
- 🏆 Manage teams and players
- ⚽ Schedule and track matches
- 📊 View statistics and rankings
- 🎨 User-friendly JavaFX UI
- 📁 live Score

---

## 🛠 Tech Stack
- **Java** (Core logic & Fetching data & dealing with Json)
- **JavaFX** (GUI)
- **FXML** (UI Layout)

---

## 📦 Installation

## Requirments
- JDK-21
- API KEY: Replace with your Key Here [`src/main/resources/phi/phisoccerii/config.properties`](src/main/resources/phi/phisoccerii/config.properties)
### 1️⃣ Clone the Repository
```sh
git clone https://github.com/Yusuf-Hussien/PHI-Soccer-MVC.git
cd PHI-Soccer-MVC
```
### 2️⃣ Build the app For Your Local Machine
```sh
./mvnw clean package 
cd target
java -jar "PHI-Soccer-ii-1.0-SNAPSHOT.jar"
```

### OR -> Open it in IDE (IntelliJ / Eclipse)
- make sure of installing javafx sdk
- Build & Sync Maven Dependicies



# 🏗 Project Structure
```sh
PHI-Soccer-MVC/
│── src/
│   ├── model/        # Business logic (Teams, Players, Matches, Leagues)
│   ├── view/         # JavaFX UI components
│   ├── controller/   # Handles user interactions
│   └── App.java     # Application entry point
│── assets/           # UI assets (icons, images, CSS)
│── README.md         # Documentation
└── ...
```