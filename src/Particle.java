import javafx.scene.paint.Paint;

/**
 * The Particle class represents a particle in the simulation.
 * Each particle has a position, velocity, mass, elasticity, radius, and color.
 * The class also includes methods to update the particle's position based on its velocity and
 * to apply gravity if enabled.
 */
public class Particle {

    public static double GRAVITY = 9.81;
    public static boolean isGravityEnabled;
    public static boolean isFrictionEnabled;

    private double x;
    private double y;
    private double vx;
    private double vy;
    private double mass;
    private double elasticity;
    private int radius;
    private Paint color;

    public Particle(double x, double y, double vx, double vy, double mass, double elasticity, int radius, Paint color) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.mass = mass;
        this.elasticity = elasticity;
        this.radius = radius;
        this.color = color;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getVx() {
        return vx;
    }

    public void setVx(double vx) {
        this.vx = vx;
    }

    public double getVy() {
        return vy;
    }

    public void setVy(double vy) {
        this.vy = vy;
    }

    /**
     * Returns the minimum x-coordinate of the particle, which is the x-coordinate of the particle minus its radius.
     *
     * @return The minimum x-coordinate of the particle.
     */
    public double getMinX() {
        return x - radius;
    }

    /**
     * Returns the minimum y-coordinate of the particle, which is the y-coordinate of the particle minus its radius.
     *
     * @return The minimum y-coordinate of the particle.
     */
    public double getMinY() {
        return y - radius;
    }

    /**
     * Returns the maximum x-coordinate of the particle, which is the x-coordinate of the particle plus its radius.
     *
     * @return The maximum x-coordinate of the particle.
     */
    public double getMaxX() {
        return x + radius;
    }

    /**
     * Returns the maximum y-coordinate of the particle, which is the y-coordinate of the particle plus its radius.
     *
     * @return The maximum y-coordinate of the particle.
     */
    public double getMaxY() {
        return y + radius;
    }

    public double getMass() {
        return mass;
    }

    public double getElasticity() {
        return elasticity;
    }

    public int getRadius() {
        return radius;
    }

    public Paint getColor() {
        return color;
    }

    public void setColor(Paint color) {
        this.color = color;
    }

    /**
     * Updates the position of the particle based on its velocity and the given time delta.
     *
     * @param dt The time delta for which to update the particle's position.
     */
    public void move(double dt) {
        if (isGravityEnabled) {
            vy += GRAVITY * dt;
        }

        x += vx * dt;
        y += vy * dt;
    }

    /**
     * Applies friction to the particle's velocity.
     *
     * @param friction The friction coefficient to apply.
     */
    public void applyFriction(double friction) {
        this.vx *= (1 - friction);
        this.vy *= (1 - friction);
    }

}
