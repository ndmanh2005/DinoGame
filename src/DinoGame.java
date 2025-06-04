// src/DinoGame.java
import javax.swing.*;

public class DinoGame extends JFrame {

    public DinoGame() {
        setTitle("Game Khủng Long Tùy Chỉnh - by 3 chàng lính ngự lâm");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        GamePanel gamePanel = new GamePanel();
        add(gamePanel);

        pack(); // Tự động điều chỉnh kích thước frame cho vừa với panel
        setLocationRelativeTo(null); // Căn giữa cửa sổ
        setVisible(true);

        gamePanel.startGame(); // Yêu cầu GamePanel bắt đầu nhận focus và game loop
    }

    public static void main(String[] args) {
        // Đảm bảo các tác vụ Swing được thực hiện trên Event Dispatch Thread
        SwingUtilities.invokeLater(() -> new DinoGame());
    }
}