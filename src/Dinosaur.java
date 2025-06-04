// src/Dinosaur.java
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Dinosaur {
    private int x, y;
    private int width, height;
    private double velocityY;
    private boolean isJumping;
    private final double GRAVITY = 0.8;
    private final double JUMP_STRENGTH = -18;
    // Bỏ GROUND_LEVEL ở đây, sẽ nhận từ GamePanel

    private Image sprite;

    public Dinosaur(int startX, int initialYForLoad) { // initialYForLoad chỉ dùng để tính toán ban đầu
        this.x = startX;
        this.isJumping = false;
        this.velocityY = 0;

        try {
            
            sprite = ImageIO.read(new File("DinoGame/assets/dino.png"));
            this.width = sprite.getWidth(null);
            this.height = sprite.getHeight(null);
        } catch (IOException e) {
            e.printStackTrace();
            this.width = 40; // Kích thước mặc định
            this.height = 40; // Kích thước mặc định
            System.err.println("Không thể tải ảnh khủng long! Kiểm tra đường dẫn: assets/dino.png");
        }
        // Vị trí y sẽ được đặt chính xác dựa trên GROUND_Y_POSITION và height
        this.y = initialYForLoad - this.height;
    }

    public void jump(int groundYPosition) { // Nhận vị trí mặt đất
        if (!isJumping) {
            isJumping = true;
            velocityY = JUMP_STRENGTH;
        }
    }

    public void update(int groundYPosition) { // Nhận vị trí mặt đất
        if (isJumping) {
            y += velocityY;
            velocityY += GRAVITY;

            if (y >= groundYPosition - this.height) { // Chạm đất
                y = groundYPosition - this.height;
                isJumping = false;
                velocityY = 0;
            }
        }
        // Nếu không nhảy, đảm bảo khủng long vẫn ở trên mặt đất (phòng trường hợp có lỗi)
        else if (y < groundYPosition - this.height) {
             y = groundYPosition - this.height;
        }
    }


    public void draw(Graphics2D g2d) {
        if (sprite != null) {
            g2d.drawImage(sprite, x, y, null);
        } else {
            g2d.fillRect(x, y, width, height);
        }
    }

    public int getX() { return x; }
    public int getY() { return y; } // Thêm getter cho y nếu cần
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}