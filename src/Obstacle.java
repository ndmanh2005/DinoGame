// src/Obstacle.java
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage; // Import BufferedImage
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Obstacle {
    private int x, y;
    private int width, height; // Sẽ lưu kích thước sau khi scale
    private int speed;
    private BufferedImage sprite; // Đổi từ Image sang BufferedImage

    // --- KÍCH THƯỚC MONG MUỐN CHO CHƯỚNG NGẠI VẬT ---
    // Bạn có thể thay đổi các giá trị này để xương rồng to/nhỏ theo ý muốn
    private static final int TARGET_OBSTACLE_WIDTH = 30;  // Ví dụ: Chiều rộng mong muốn
    private static final int TARGET_OBSTACLE_HEIGHT = 60; // Ví dụ: Chiều cao mong muốn
    // -------------------------------------------------

    // Constructor này giữ nguyên tham số imagePath từ code GamePanel của bạn
    // nhưng hiện tại đang dùng đường dẫn cố định cho cactus.png bên trong
    public Obstacle(int startX, int groundLevel, int speed, String imagePath) {
        this.x = startX;
        this.speed = speed;
        this.width = TARGET_OBSTACLE_WIDTH;   // Gán kích thước mong muốn
        this.height = TARGET_OBSTACLE_HEIGHT; // Gán kích thước mong muốn

        try {
            // Sử dụng imagePath được truyền vào, hoặc đường dẫn cố định nếu muốn
            // Hiện tại, code bạn cung cấp dùng đường dẫn cố định là "DinoGame/assets/cactus.png"
            // Nếu bạn muốn dùng imagePath động, hãy thay thế dòng dưới:
            // BufferedImage originalImage = ImageIO.read(new File(imagePath));
            BufferedImage originalImage = ImageIO.read(new File("DinoGame/assets/cactus.png")); // Giữ theo code bạn cung cấp

            if (originalImage != null) {
                // Tạo một BufferedImage mới với kích thước mong muốn
                this.sprite = new BufferedImage(TARGET_OBSTACLE_WIDTH, TARGET_OBSTACLE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
                // Lấy đối tượng Graphics2D từ sprite mới để vẽ ảnh gốc đã scale lên đó
                Graphics2D g2dScaler = this.sprite.createGraphics();
                // Vẽ ảnh gốc (originalImage) vào sprite mới với kích thước target
                g2dScaler.drawImage(originalImage, 0, 0, TARGET_OBSTACLE_WIDTH, TARGET_OBSTACLE_HEIGHT, null);
                g2dScaler.dispose(); // Giải phóng tài nguyên của Graphics2D
                // System.out.println("Tải và scale ảnh chướng ngại vật (" + imagePath + ") thành công."); // Bỏ comment nếu muốn debug
            } else {
                // Trường hợp ImageIO.read trả về null (file không phải định dạng ảnh hợp lệ)
                System.err.println("LỖI: ImageIO.read trả về null cho đường dẫn: DinoGame/assets/cactus.png. File có thể bị lỗi hoặc không phải định dạng ảnh chuẩn.");
                this.sprite = null; // Đảm bảo sprite là null nếu không tải được
            }

        } catch (IOException e) {
            e.printStackTrace();
            // Kích thước mặc định đã được gán ở trên nếu có lỗi xảy ra
            // imagePath trong dòng lỗi dưới nên lấy từ tham số hoặc đường dẫn thực tế đã dùng
            System.err.println("LỖI IO Exception khi tải hoặc scale ảnh chướng ngại vật! Kiểm tra đường dẫn: DinoGame/assets/cactus.png");
            this.sprite = null; // Đảm bảo sprite là null nếu có IOException
        }
        // Đặt chướng ngại vật đứng trên mặt đất dựa trên chiều cao đã được scale (TARGET_OBSTACLE_HEIGHT)
        this.y = groundLevel - this.height;
    }

    public void update() {
        x -= speed; // Di chuyển sang trái
    }

    public void draw(Graphics2D g2d) {
        if (sprite != null) {
            g2d.drawImage(sprite, x, y, null); // Vẽ sprite đã được scale
        } else {
            // Vẽ hình chữ nhật thay thế nếu không có ảnh
            g2d.fillRect(x, y, width, height); // Vẽ với kích thước target
        }
    }

    public int getX() {
        return x;
    }

    public int getWidth() {
        return width; // Trả về chiều rộng đã được scale (TARGET_OBSTACLE_WIDTH)
    }

    public int getHeight() { // Thêm getter cho height để đồng bộ
        return height; // Trả về chiều cao đã được scale (TARGET_OBSTACLE_HEIGHT)
    }

    public Rectangle getBounds() {
        // Sử dụng width và height đã được scale cho việc kiểm tra va chạm
        return new Rectangle(x, y, width, height);
    }
}