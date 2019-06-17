import java.awt.*;

public class Spaceship extends Sprite {

    private static final int bulletSpeed = 10;

    Spaceship(int x, int y) {
        super("res/Spaceship.png", x, y);
        xVel = 0;
        yVel = 0;
    }

    public Bullet shoot() {
        return new Bullet("res/Bullet.png", x + getWidth(), y + getHeight() / 2, bulletSpeed);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x + 20, y + 20, getWidth() - 40,
                getHeight() - 40);
    }
}
