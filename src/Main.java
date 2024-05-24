import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * The Main class is the entry point for the Particle Collision Simulation.
 * It sets up the JavaFX application, initializes the particles,
 * and handles the main animation loop to update and render the simulation.
 */
public class Main extends Application {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 800;
    private static final int FPS = 60;
    private static final double FRICTION = 0.0001;
    private static final int BUTTON_BOX_HEIGHT = 28;

    private List<Particle> particles;

    public static int getWidth() {
        return WIDTH;
    }

    public static int getHeight() {
        return HEIGHT;
    }

    public void addParticle(Particle particle) {
        particles.add(particle);
    }

    public boolean isOverlapping(Particle newParticle) {
        for (Particle particle : particles) {
            if (isColliding(newParticle, particle)) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method generates a particle with random position, velocity, radius, mass, color, and elasticity.
     * The position is constrained within a boundary with a 20-pixel margin, the velocity ranges between -200 and 200
     * for both x and y components, the radius is between 5 and 30 pixels, the mass is between 0.5 and 5 units,
     * and the elasticity is between 0.8 and 0.95.
     *
     * @return A randomly generated particle.
     */
    public Particle createRandomParticle() {
        Particle newParticle;
        do {
            double x = Math.random() * (WIDTH - 40) + 20;
            double y = Math.random() * (HEIGHT - 40) + 20;
            int radius = (int) (Math.random() * 25 + 5);
            double vx = Math.random() * 400 - 200;
            double vy = Math.random() * 400 - 200;
            double mass = Math.random() * 4.5 + 0.5;
            Color color = createRandomColor();
            double elasticity = Math.random() * 0.15 + 0.8;
            newParticle = new Particle(x, y, vx, vy, mass, elasticity, radius, color);
        } while (isOverlapping(newParticle));
        return newParticle;
    }

    public void removeParticle(Particle particle) {
        particles.remove(particle);
    }

    /**
     * Checks for and resolves collisions between particles using a sweep and prune algorithm.
     * This method sorts the particles by their minimum x and y coordinates, then iterates through
     * them to check for potential collisions, and resolves any detected collisions.
     */
    public void checkParticleCollisions() {
        // Sort particles by their minimum x and y coordinates
        particles.sort(Comparator.comparing(Particle::getMinX).thenComparing(Particle::getMinY));

        List<Particle> activeListX = new ArrayList<>();
        List<Particle> activeListY = new ArrayList<>();

        for (Particle particle : particles) {
            // Remove particles from the active list if they are no longer overlapping in the x-axis
            activeListX.removeIf(p -> p.getMaxX() < particle.getMinX());

            for (Particle other : activeListX) {
                if (isColliding(particle, other)) {
                    resolveCollision(particle, other);
                }
            }

            activeListX.add(particle);

            // Remove particles from the active list if they are no longer overlapping in the y-axis
            activeListY.removeIf(p -> p.getMaxY() < particle.getMinY());

            for (Particle other : activeListY) {
                if (isColliding(particle, other)) {
                    resolveCollision(particle, other);
                }
            }

            activeListY.add(particle);
        }
    }

    /**
     * Checks if two particles are colliding based on their positions and radii.
     *
     * @param p1 The first particle.
     * @param p2 The second particle.
     * @return true if the particles are colliding, false otherwise.
     */
    public boolean isColliding(Particle p1, Particle p2) {
        double dx = p2.getX() - p1.getX();
        double dy = p2.getY() - p1.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance < (p1.getRadius() + p2.getRadius());
    }

    /**
     * Checks for and resolves collisions between a particle and the walls of the simulation window.
     *
     * @param particle The particle to check for wall collisions.
     */
    public void checkCollisionWithWalls(Particle particle) {
        int screenHeight = Main.getHeight() - BUTTON_BOX_HEIGHT;

        if (particle.getX() - particle.getRadius() < 0) {
            particle.setVx(-particle.getVx());
            particle.setX(particle.getRadius());
        } else if (particle.getX() + particle.getRadius() > Main.getWidth()) {
            particle.setVx(-particle.getVx());
            particle.setX(getWidth() - particle.getRadius());
        }

        if (particle.getY() - particle.getRadius() < 0) {
            particle.setVy(-particle.getVy());
            particle.setY(particle.getRadius());
        } else if (particle.getY() + particle.getRadius() > screenHeight) {
            particle.setVy(-particle.getVy());
            particle.setY(screenHeight - particle.getRadius());
        }
    }

    /**
     * Resolves a collision between two particles by updating their velocities and positions based on their masses,
     * velocities, and elasticities. This method also changes the colors of the particles upon collision.
     *
     * @param p1 The first particle involved in the collision.
     * @param p2 The second particle involved in the collision.
     */
    public void resolveCollision(Particle p1, Particle p2) {
        double dx = p2.getX() - p1.getX();
        double dy = p2.getY() - p1.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        // Check for zero distance to avoid division by zero
        if (distance == 0) {
            // Add a small random offset to separate overlapping particles
            distance = 0.01;
            dx += Math.random() * 0.02 - 0.01; // Adding a random value between -0.01 and 0.01 to dx
            dy += Math.random() * 0.02 - 0.01; // Adding a random value between -0.01 and 0.01 to dy
        }

        // Normalize the normal vector
        double nx = dx / distance;
        double ny = dy / distance;

        // Relative velocity
        double rvx = p2.getVx() - p1.getVx();
        double rvy = p2.getVy() - p1.getVy();

        // Relative velocity in terms of the normal direction
        double rvn = rvx * nx + rvy * ny;

        // Do not resolve if velocities are separating
        if (rvn > 0) {
            return;
        }

        // Calculate restitution (elasticity)
        double e = Math.min(p1.getElasticity(), p2.getElasticity());

        // Calculate impulse scalar
        double impulseScalar;
        impulseScalar = -(1 + e) * rvn;
        impulseScalar /= 1 / p1.getMass() + 1 / p2.getMass();

        // Apply impulse
        double impulseX, impulseY;
        impulseX = impulseScalar * nx;
        impulseY = impulseScalar * ny;

        // Update velocities of the particles using impulse
        p1.setVx(p1.getVx() - impulseX / p1.getMass());
        p1.setVy(p1.getVy() - impulseY / p1.getMass());
        p2.setVx(p2.getVx() + impulseX / p2.getMass());
        p2.setVy(p2.getVy() + impulseY / p2.getMass());

        // Positional correction to avoid particles sticking
        double percent = 0.75;
        double slop = 0.01;
        double overlap = Math.max(distance - (p1.getRadius() + p2.getRadius()), 0);
        double correction = (overlap - slop) / (1 / p1.getMass() + 1 / p2.getMass()) * percent;
        double correctionX = correction * nx;
        double correctionY = correction * ny;

        p1.setX(p1.getX() - correctionX / p1.getMass());
        p1.setY(p1.getY() - correctionY / p1.getMass());
        p2.setX(p2.getX() + correctionX / p2.getMass());
        p2.setY(p2.getY() + correctionY / p2.getMass());

        // Change colors upon collision
        p1.setColor(createRandomColor());
        p2.setColor(createRandomColor());
    }

    /**
     * Generates a random color.
     *
     * @return A randomly generated color.
     */
    public static Color createRandomColor() {
        double red = Math.random() * 255;
        double green = Math.random() * 255;
        double blue = Math.random() * 255;
        return Color.rgb((int) red, (int) green, (int) blue);
    }

    /**
     * Starts the JavaFX application.
     * <p>
     * Initializes the primary stage with a canvas for rendering particles, sets up key event handlers for toggling
     * gravity and resetting the simulation and starts the animation timer for updating and rendering particles.
     *
     * @param primaryStage The primary stage for this application.
     */
    @Override
    public void start(Stage primaryStage) {
        particles = new ArrayList<>();

        // Create canvas
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Create buttons
        Button toggleGravityButton = new Button("Toggle Gravity");
        Button resetButton = new Button("Reset Simulation");
        Button addParticleButton = new Button("Add Particle");
        Button removeParticleButton = new Button("Remove Particle");
        Button quitButton = new Button("Quit Simulation");

        // Set button actions
        toggleGravityButton.setOnAction(e -> {
            Particle.isGravityEnabled = !Particle.isGravityEnabled;
            System.out.println(Particle.isGravityEnabled ? "[+][+][+] Gravity ON [+][+][+]" : "[-][-][-] Gravity OFF [-][-][-]");
        });

        addParticleButton.setOnAction(e -> {
            Particle newParticle = createRandomParticle();
            addParticle(newParticle);
            System.out.println("[+][+][+] Particle added: " + newParticle + " [+][+][+]");
        });

        removeParticleButton.setOnAction(e -> {
            if (!particles.isEmpty()) {
                Particle removedParticle = particles.get(particles.size() - 1);
                removeParticle(removedParticle);
                System.out.println("[-][-][-] Particle removed: " + removedParticle + " [-][-][-]");
            } else {
                System.out.println("[!][!][!] No particles left to remove [!][!][!]");
            }
        });

        resetButton.setOnAction(e -> {
            resetSimulation(gc);
            System.out.println("[*][*][*] Simulation was reset [*][*][*]");
        });

        quitButton.setOnAction(e -> {
            System.out.println("[!][!][!] Quitting the application [!][!][!]");
            Platform.exit();
        });

        // Create HBox to hold the buttons
        HBox buttonBox = new HBox(10, toggleGravityButton, addParticleButton, removeParticleButton, resetButton, quitButton);
        buttonBox.setStyle("-fx-padding: 0; -fx-alignment: center;");
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setBackground(Background.fill(Color.BLACK));

        // Create the main layout and add the canvas and buttonBox
        BorderPane root = new BorderPane();
        root.setCenter(canvas);
        root.setTop(buttonBox);

        // Create scene
        Scene scene = new Scene(root, WIDTH, HEIGHT);

        // Initialize particles
        initializeParticles();

        // Set up key event handlers
        scene.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.G) {
                Particle.isGravityEnabled = !Particle.isGravityEnabled;
                System.out.println(Particle.isGravityEnabled ? "[+][+][+] Gravity ON [+][+][+]" : "[-][-][-] Gravity OFF [-][-][-]");
            } else if (event.getCode() == KeyCode.R) {
                resetSimulation(gc);
                System.out.println("[*][*][*] Simulation was reset [*][*][*]");
            } else if (event.getCode() == KeyCode.A) {
                Particle newParticle = createRandomParticle();
                addParticle(newParticle);
                System.out.println("[+][+][+] " + newParticle + " was added [+][+][+]");
            } else if (event.getCode() == KeyCode.K) {
                if (!particles.isEmpty()) {
                    Particle removedParticle = particles.getLast();
                    removeParticle(removedParticle);
                    System.out.println("[-][-][-] " + removedParticle + " was removed [-][-][-]");
                } else {
                    System.out.println("[!][!][!] No particles left to remove [!][!][!]");
                }
            } else if (event.getCode() == KeyCode.Q) {
                System.out.println("[!][!][!] Quitting the application [!][!][!]");
                Platform.exit();
            } else if (event.getCode() == KeyCode.F) {
                Particle.isFrictionEnabled = !Particle.isFrictionEnabled;
                System.out.println(Particle.isFrictionEnabled ? "[+][+][+] Friction ON [+][+][+]" : "[-][-][-] Friction OFF [-][-][-]");
            }
        });

        // Animation timer for rendering
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
                render(gc);
            }
        };
        timer.start();

        // Set up stage
        primaryStage.setTitle("Particle Collision Simulation");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Draws all particles on the canvas.
     *
     * @param gc The GraphicsContext of the canvas.
     */
    private void drawParticles(GraphicsContext gc) {
        for (Particle particle : particles) {
            gc.setFill(particle.getColor());
            gc.fillOval(particle.getX() - particle.getRadius(), particle.getY() - particle.getRadius(),
                    2 * particle.getRadius(), 2 * particle.getRadius());
        }
    }

    /**
     * Initializes the particles for the simulation.
     * <p>
     * This method creates a specified number of particles with random attributes
     * and adds them to the particles list.
     */
    private void initializeParticles() {

        for (int i = 0; i < 75; i++) {
            addParticle(createRandomParticle());
        }
    }

    /**
     * Updates the state of all particles.
     * <p>
     * This method updates the position and velocity of each particle, checks for
     * collisions with walls, and checks for collisions between particles.
     */
    private void update() {
        double dt = 1.0 / FPS;

        for (Particle particle : particles) {
            // Apply friction to the particle
            particle.applyFriction(FRICTION);
            // Update particle position and velocity
            particle.move(dt);

            checkCollisionWithWalls(particle);
        }
        checkParticleCollisions();
    }

    /**
     * Renders the particles on the canvas.
     * <p>
     * This method clears the canvas and draws all particles.
     *
     * @param gc The GraphicsContext of the canvas.
     */
    private void render(GraphicsContext gc) {
        // Set background color to black
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        // Render particles
        drawParticles(gc);
    }

    /**
     * Resets the simulation by clearing and reinitializing particles.
     * <p>
     * This method clears the canvas and the particles list, and then
     * reinitializes the particles with new random attributes.
     *
     * @param gc The GraphicsContext of the canvas.
     */
    private void resetSimulation(GraphicsContext gc) {
        // Clear canvas
        gc.clearRect(0, 0, WIDTH, HEIGHT);

        // Clear particles list
        particles.clear();

        // Reinitialize particles
        initializeParticles();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
