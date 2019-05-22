import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.Timer;


public class Board extends JPanel implements ActionListener, KeyListener {
    private Timer timer;
    private boolean gameOver;
    private int width, height;
    private boolean[] keys = new boolean[0xE3];

    private final int screenVel = 5;
    private final int shipSpeed = 8;
    private final int maxCooldown = 10;
    private int cooldown;

    private Spaceship spaceShip;
    private ArrayList<Bullet> shots;
    private ArrayList<Obstacle> obstacles;
    private ScrollingBackground backOne, backTwo;



    public Board(int width, int height) {
        this.addKeyListener(this);
        this.setFocusable(true);

        this.width = width;
        this.height = height;
        gameOver = false;
        shots = new ArrayList<>();

        spaceShip = new Spaceship(0, 0);
        obstacles = new ArrayList<>();
        backOne = new ScrollingBackground(0);
        backTwo = new ScrollingBackground(backOne.getWidth());
        timer = new Timer(10, this);
        timer.start();
        cooldown = 0;

        Obstacle testObstacle = new Obstacle("res/thing_test.png", 1000, 500);
        obstacles.add(testObstacle);
    }

    public void shoot() {
        Bullet shot = new Bullet("res/Bullet.png", spaceShip.getX() + spaceShip.getWidth(),
                spaceShip.getY() + spaceShip.getHeight() / 2);
        shots.add(shot);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            //Movement of the ship
            if (keys[KeyEvent.VK_W]) {
                spaceShip.setY(spaceShip.getY() - shipSpeed);
            }
            if (keys[KeyEvent.VK_S]) {
                spaceShip.setY(spaceShip.getY() + shipSpeed);
            }
            if (keys[KeyEvent.VK_A]) {
                spaceShip.setX(spaceShip.getX() - shipSpeed);
            }
            if (keys[KeyEvent.VK_D]) {
                spaceShip.setX(spaceShip.getX() + shipSpeed);
            }

            //Check if the ship should shoot
            if (keys[KeyEvent.VK_SPACE]) {
                if (cooldown <= 0) {
                    cooldown = maxCooldown;
                    shoot();
                }
            }
            cooldown -= 1;

            //move each shot on the board
            ArrayList<Bullet> outOfBounds = new ArrayList<>();
            for (Bullet shot : shots) {
                shot.move();
                //remove the shot if it moves out of bounds
                if (shot.getX() > width || shot.getX() < 0 - shot.getWidth()) {
                    outOfBounds.add(shot);
                }
            }
            shots.removeAll(outOfBounds);

            checkCollisions();

            scrollScreen();
        }

        this.repaint();
    }

    public void checkCollisions() {
        Rectangle shipHitBox = spaceShip.getBounds();
        for (Obstacle obstacle : obstacles) {
            if (shipHitBox.intersects(obstacle.getBounds())) {
                gameOver = true;
            }
        }

        ArrayList<Obstacle> toDelete = new ArrayList<>();
        for (Bullet shot : shots) {
            Rectangle shotHitBox = shot.getBounds();

            for (Obstacle obstacle : obstacles) {
                if (shotHitBox.intersects(obstacle.getBounds())) {
                    toDelete.add(obstacle);
                }
            }
        }
        obstacles.removeAll(toDelete);
    }

    //Moves objects on the screen to the left to create scrolling effect
    public void scrollScreen() {
        backOne.setX(backOne.getX() - screenVel);
        if (backOne.getX() <= -1 * backOne.getWidth()) {
            backOne.setX(backOne.getWidth());
        }
        backTwo.setX(backTwo.getX() - screenVel);
        if (backTwo.getX() <= -1 * backTwo.getWidth()) {
            backTwo.setX(backOne.getWidth());
        }

        for (Obstacle obstacle : obstacles) {
            obstacle.setX(obstacle.getX() - screenVel);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

//        g2d.setColor(Color.BLACK);
//        g2d.fillRect(0, 0, width, height);
        g2d.drawImage(backOne.getImage(), backOne.getX(), 0, this);
        g2d.drawImage(backTwo.getImage(), backTwo.getX(), 0, this);

        g2d.drawImage(spaceShip.getImage(), spaceShip.getX(),
                spaceShip.getY(), this);
        
        for (Bullet shot : shots) {
            g2d.drawImage(shot.getImage(), shot.getX(), shot.getY(), this);
        }

        for (Obstacle obstacle : obstacles) {
            g2d.drawImage(obstacle.getImage(), obstacle.getX(), obstacle.getY(), this);
        }

        if (gameOver) {
            //placeholder game over
            System.out.println("Game Over");
        }

        Toolkit.getDefaultToolkit().sync();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }
}