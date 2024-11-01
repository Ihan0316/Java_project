package mini_project;

import javax.swing.*;
import java.awt.*;

public class LotterySystem extends JFrame {
    private JTextField userIdField;
    private JTextField userNumbersField;
    private JLabel winningNumbersLabel;
    private JButton drawButton;
    private JButton randomDrawButton;
    private JButton randomDraw10Button;
    private DefaultListModel<String> listModel; // JList 모델
    public JList<String> entryList; // JList 선언

    public LotterySystem() {
        setTitle("복권 시스템");
        setSize(800, 600); // 프레임 크기 조정
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // 여백 추가
        gbc.fill = GridBagConstraints.HORIZONTAL; // 수평으로 채우기

        userIdField = new JTextField(20);
        userNumbersField = new JTextField(20);
        winningNumbersLabel = new JLabel("당첨 번호: ");
        drawButton = new JButton("추첨");
        randomDrawButton = new JButton("랜덤 추첨");
        randomDraw10Button = new JButton("랜덤 10번 추첨");

        drawButton.addActionListener(new DrawButtonListener(this));
        randomDrawButton.addActionListener(new RandomDrawButtonListener(this));
        randomDraw10Button.addActionListener(new RandomDraw10ButtonListener(this));

        // 사용자 ID 라벨 및 필드
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_END;
        add(new JLabel("사용자 ID: "), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        add(userIdField, gbc);

        // 번호 입력 라벨 및 필드
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        add(new JLabel("번호 입력 (쉼표로 구분): "), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        add(userNumbersField, gbc);

        // 당첨 번호 라벨
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2; // 두 열을 차지
        gbc.anchor = GridBagConstraints.CENTER;
        add(winningNumbersLabel, gbc);

        // 버튼 배치 (나란히)
        gbc.gridwidth = 1; // 원래 열 수로 복구
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        add(drawButton, gbc);

        gbc.gridx = 1;
        add(randomDrawButton, gbc);

        gbc.gridx = 2; // 다음 열에 랜덤 10번 추첨 버튼 추가
        add(randomDraw10Button, gbc);
        
        // JList 설정
        listModel = new DefaultListModel<>();
        entryList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(entryList);
        scrollPane.setPreferredSize(new Dimension(700, 250)); // JList 크기 조정
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 3; // JList가 3열을 차지
        gbc.fill = GridBagConstraints.BOTH; // JList가 공간을 모두 차지하게 함
        add(scrollPane, gbc); // JList 추가

        // 조회 및 삭제 버튼 배치
        JButton viewEntriesButton = new JButton("전체 테이블 조회");
        viewEntriesButton.addActionListener(new ViewEntriesButtonListener(this));
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridy = 5;
        gbc.gridwidth = 1; // 열 수를 원래대로
        gbc.anchor = GridBagConstraints.CENTER;
        add(viewEntriesButton, gbc);

        JButton deleteEntryButton = new JButton("삭제");
        deleteEntryButton.addActionListener(new DeleteEntryButtonListener(this));
        gbc.gridx = 1; // 다음 열에 삭제 버튼 추가
        add(deleteEntryButton, gbc);
        
        // 전체 테이블 삭제 버튼 추가
        JButton deleteAllButton = new JButton("전체 테이블 삭제");
        deleteAllButton.addActionListener(new DeleteAllEntriesButtonListener(this));
        gbc.gridx = 2; // 다음 열에 전체 테이블 삭제 버튼 추가
        add(deleteAllButton, gbc);
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

    public DefaultListModel<String> getListModel() {
        return listModel; // JList 모델 반환
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LotterySystem lotterySystem = new LotterySystem();
            lotterySystem.setVisible(true);
        });
    }
}
