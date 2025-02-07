# Route Planner

A JavaFX-based desktop application for creating and managing custom maps with interactive points and routes, offering an intuitive way to plan and organize journeys.

## Features

- **Custom Map Upload** – Use any image as your map background.
- **Trip Planning** – Add points for landmarks, stops and waypoints.
- **Route Management** – Create, rename and delete custom routes.
- **Interactive Editing**:
  - Right-click points to rename, resize, assign to routes or delete.
  - Drag and drop points to adjust locations.
- **Map Customization**:
  - Toggle a coordinate grid for better navigation.
  - Change background image anytime.
- **Save & Load Maps** – Store maps with all routes and points in JSON format.

## Screenshots

| <img width="437" alt="point_right_click" src="https://github.com/user-attachments/assets/d6b8c75d-9e64-4fb2-bcb6-b54cf12f5500"> | <img width="437" alt="new_route" src="https://github.com/user-attachments/assets/0b221bd0-e9e6-46ca-9cb6-5bca9206fe1f"> |
|:---------------------------------------:|:---------------------------------------:|

## Installation

#### Prerequisites

- **Java JDK 17+**
- **Maven**

### Steps:

1. **Clone this repository**:

   ```bash
   git clone https://github.com/nmikolaj/route-planner.git
   cd route-planner
   ```

2. **Build the project**:

   ```bash
   mvn clean install
   ```

3. **Run the application**:

   ```bash
   mvn javafx:run
   ```
