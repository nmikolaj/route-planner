
# Constellations

JavaFX-based desktop application that allows users to visualize stars and constellations on a map. The application aims to provide a simple and interactive way to learn about the night sky.

## Features

- **Add Stars**: Dynamically add stars to the map.
- **Star Interaction**: Right-click on any star to perform the following actions:
  - **Delete**: Remove picked star.
  - **Rename**: Change name of an existing star.
  - **Change Brightness**: Adjust star's visibility within its constellation.
  - **Assign to Constellations**: Group stars into constellations.
- **Save and Load Data**: Save and load constellations in JSON format.
- **Show Coordinates**: Display a grid overlay to help locate the coordinates of stars on the map.

## Installation

#### Prerequisites

- **Java JDK 11+**
- **Maven**

### Steps:

1. **Clone this repository**:

   ```bash
   git clone https://github.com/yourusername/star-map-app.git
   cd star-map-app
   ```

2. **Build the project**:

   ```bash
   mvn clean install
   ```

3. **Run the application**:

   ```bash
   mvn javafx:run
   ```

