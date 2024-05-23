# Particle Collision Simulation

This Java application simulates the motion and collision of particles within a confined space using basic physics principles. Particles move, interact with each other and bounce off the walls in real-time. Users can dynamically toggle gravity, add new particles, reset the simulation and quit the application using keyboard controls.

## Features

- **Real-time Particle Simulation**: Particles move and collide based on basic physics.
  
- **Interactive Controls**:
  - **Toggle Gravity**: Press `G` to toggle gravity on and off.
  - **Add Particle**: Press `A` to add a new particle to the simulation.
  - **Reset Simulation**: Press `R` to reset the simulation.
  - **Quit Application**: Press `Q` to quit the application.

## Getting Started

## Dependencies / Prerequisites

- Java Development Kit (JDK) 8 or higher
- JavaFX SDK

### Installing

1. **Clone the repository**:

    ```sh
    git clone https://github.com/mihcaoj/ParticleSimulation.git
    cd ParticleSimulation
    ```

2. **Set up JavaFX**:

    Ensure that the JavaFX library is included in your project. You can download the JavaFX SDK from the [official website](https://openjfx.io/). Add the JavaFX libraries to your project build path.

### Running the Program

1. **Compile and run**:

    Use your preferred IDE to run the `Main` class. Make sure to configure the VM arguments to include the JavaFX modules. For example:

    ```sh
    --module-path /path/to/javafx-sdk-15/lib --add-modules javafx.controls,javafx.fxml
    ```

    Replace `/path/to/javafx-sdk-15` with the actual path to your JavaFX SDK.
