// src/GamePanel.java
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File; // THÊM import này
import java.io.IOException;                // THÊM import này
import java.util.ArrayList;           // THÊM import này
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.*;         // THÊM import này

public class GamePanel extends JPanel implements ActionListener {

    private final int SCREEN_WIDTH = 800;
    private final int SCREEN_HEIGHT = 400;
    private final int GROUND_Y_POSITION = SCREEN_HEIGHT - 50;

    private Timer timer;
    private Dinosaur dinosaur;
    private List<Obstacle> obstacles;
    private Random random;
    private BufferedImage backgroundImage; // THÊM: Biến cho ảnh nền

    private int obstacleSpawnTimer;
    private final int INITIAL_OBSTACLE_SPEED = 5;
    private int score;

    private enum GameState {
        START_SCREEN,
        PLAYING,
        GAME_OVER
    }
    private GameState currentState;

    public GamePanel() {
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        // setBackground(Color.WHITE); // Sẽ được thay bằng ảnh nền hoặc màu fallback
        setFocusable(true);

        loadBackgroundImage(); // THÊM: Gọi hàm tải ảnh nền

        // Giữ nguyên cách bạn khởi tạo Dinosaur và Obstacle nếu bạn chưa thay đổi constructor của chúng
        dinosaur = new Dinosaur(50, GROUND_Y_POSITION);
        obstacles = new ArrayList<>();
        random = new Random();

        currentState = GameState.START_SCREEN;

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                switch (currentState) {
                    case START_SCREEN:
                        if (keyCode == KeyEvent.VK_SPACE) {
                            startGame();
                        }
                        break;
                    case PLAYING:
                        if (keyCode == KeyEvent.VK_SPACE) {
                            dinosaur.jump(GROUND_Y_POSITION);
                        }
                        break;
                    case GAME_OVER:
                        if (keyCode == KeyEvent.VK_R) {
                            currentState = GameState.START_SCREEN;
                            // Reset khủng long cho màn hình bắt đầu
                            dinosaur = new Dinosaur(50, GROUND_Y_POSITION); // Giữ nguyên cách khởi tạo
                            repaint();
                        }
                        break;
                }
            }
        });

        timer = new Timer(1000 / 60, this);
        timer.start();
        
        SwingUtilities.invokeLater(() -> requestFocusInWindow());
    }
    
    // THÊM: Phương thức tải ảnh nền
    private void loadBackgroundImage() {
        try {
            // Giả sử background.png nằm trong DinoGame/assets/ giống như dino.png và cactus.png
            // nếu các file đó đang được tải đúng từ Dinosaur.java và Obstacle.java
            // Nếu Dinosaur và Obstacle dùng "assets/file.png" thì ở đây cũng vậy.
            // Nếu chúng dùng "DinoGame/assets/file.png" thì ở đây cũng vậy.
            // Dựa trên output trước đó của bạn, "DinoGame/assets/" là đường dẫn đúng từ thư mục làm việc.
            backgroundImage = ImageIO.read(new File("assets/background.png"));
            if (backgroundImage != null) {
                System.out.println("Tải ảnh nền thành công từ: DinoGame/assets/background.png");
            } else {
                System.err.println("LỖI: ImageIO.read trả về null cho ảnh nền. File có thể bị lỗi hoặc không phải định dạng ảnh chuẩn.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            backgroundImage = null; 
            System.err.println("Lỗi tải ảnh nền: DinoGame/assets/background.png");
        }
    }

    public void startGame() {
        // Giữ nguyên cách bạn khởi tạo Dinosaur
        dinosaur = new Dinosaur(50, GROUND_Y_POSITION);
        obstacles.clear();
        obstacleSpawnTimer = 0;
        score = 0;
        currentState = GameState.PLAYING;

        requestFocusInWindow();
    }

    private void spawnObstacle() {
        // Giữ nguyên cách bạn truyền imagePath cho Obstacle
        String cactusImagePath = "DinoGame/assets/cactus.png"; 
        int currentObstacleSpeed = INITIAL_OBSTACLE_SPEED + (score / 500);
        Obstacle newObstacle = new Obstacle(SCREEN_WIDTH, GROUND_Y_POSITION, currentObstacleSpeed, cactusImagePath);
        obstacles.add(newObstacle);
    }

    private void checkCollisions() {
        Rectangle dinoBounds = dinosaur.getBounds();
        for (Obstacle obstacle : obstacles) {
            Rectangle obstacleBounds = obstacle.getBounds();
            if (dinoBounds.intersects(obstacleBounds)) {
                currentState = GameState.GAME_OVER;
                break;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // THÊM: Vẽ ảnh nền trước tiên
        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, this);
        } else {
            // Nếu không có ảnh nền, tô màu trắng (hoặc màu bạn muốn làm fallback)
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        }

        // Vẽ mặt đất luôn hiển thị
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawLine(0, GROUND_Y_POSITION, SCREEN_WIDTH, GROUND_Y_POSITION);

        switch (currentState) {
            case START_SCREEN:
                dinosaur.draw(g2d);
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.BOLD, 30));
                FontMetrics fmStart = g2d.getFontMetrics();
                String startMsg = "Nhấn SPACE để bắt đầu";
                int msgWidthStart = fmStart.stringWidth(startMsg);
                g2d.drawString(startMsg, (SCREEN_WIDTH - msgWidthStart) / 2, SCREEN_HEIGHT / 2);
                break;

            case PLAYING:
                dinosaur.draw(g2d);
                for (Obstacle obstacle : obstacles) {
                    obstacle.draw(g2d);
                }
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.BOLD, 20));
                g2d.drawString("Score: " + score, 10, 20);
                break;

            case GAME_OVER:
                dinosaur.draw(g2d);
                for (Obstacle obstacle : obstacles) {
                    obstacle.draw(g2d);
                }

                g2d.setColor(Color.RED);
                g2d.setFont(new Font("Arial", Font.BOLD, 50));
                FontMetrics fmGO = g2d.getFontMetrics();
                String gameOverMsg = "GAME OVER";
                int msgWidthGO = fmGO.stringWidth(gameOverMsg);
                g2d.drawString(gameOverMsg, (SCREEN_WIDTH - msgWidthGO) / 2, SCREEN_HEIGHT / 2 - 60);

                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.PLAIN, 20));
                String finalScoreMsg = "Điểm của bạn: " + score;
                FontMetrics fmScore = g2d.getFontMetrics();
                int msgWidthScore = fmScore.stringWidth(finalScoreMsg);
                g2d.drawString(finalScoreMsg, (SCREEN_WIDTH - msgWidthScore) / 2, SCREEN_HEIGHT / 2 - 10);
                
                String restartMsg = "Nhấn 'R' để chơi lại";
                FontMetrics fmRestart = g2d.getFontMetrics();
                int msgWidthRestart = fmRestart.stringWidth(restartMsg);
                g2d.drawString(restartMsg, (SCREEN_WIDTH - msgWidthRestart) / 2, SCREEN_HEIGHT / 2 + 30);
                break;
        }
        Toolkit.getDefaultToolkit().sync();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (currentState == GameState.PLAYING) {
            dinosaur.update(GROUND_Y_POSITION);
            score++;

            obstacleSpawnTimer++;
            int spawnThreshold = Math.max(50, 120 - (score / 100)); 
            if (obstacleSpawnTimer > spawnThreshold + random.nextInt(spawnThreshold / 2)) {
                spawnObstacle();
                obstacleSpawnTimer = 0;
            }

            java.util.Iterator<Obstacle> iterator = obstacles.iterator();
            while (iterator.hasNext()) {
                Obstacle obstacle = iterator.next();
                obstacle.update();
                if (obstacle.getX() + obstacle.getWidth() < 0) {
                    iterator.remove();
                }
            }
            checkCollisions();
        }
        repaint();
    }
}