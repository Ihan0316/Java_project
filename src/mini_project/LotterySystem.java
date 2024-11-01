package mini_project;

import javax.swing.*;
import java.awt.*;

public class LotterySystem extends JFrame {
    private JTextField userIdField;
    private JTextField userNumbersField;
    private JLabel winningNumbersLabel;
    private JButton drawButton;
    private JButton randomDrawButton;

    public LotterySystem() {
        setTitle("복권 시스템");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        userIdField = new JTextField(20);
        userNumbersField = new JTextField(20);
        winningNumbersLabel = new JLabel("당첨 번호: ");
        drawButton = new JButton("추첨");
        randomDrawButton = new JButton("랜덤 추첨");

        drawButton.addActionListener(new DrawButtonListener(this)); 
        randomDrawButton.addActionListener(new RandomDrawButtonListener(this));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_END;
        add(new JLabel("사용자 ID: "), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        add(userIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        add(new JLabel("번호 입력 (쉼표로 구분): "), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        add(userNumbersField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(winningNumbersLabel, gbc);

        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.LINE_END;
        add(drawButton, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        add(randomDrawButton, gbc);
    }

    public JTextField getUserIdField() {
        return userIdField;
    }

    public JTextField getUserNumbersField() {
        return userNumbersField;
    }

    public JLabel getWinningNumbersLabel() {
        return winningNumbersLabel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LotterySystem lotterySystem = new LotterySystem();
            lotterySystem.setVisible(true);
        });
    }
}
