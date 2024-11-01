package mini_project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LotterySystem extends JFrame {
    private JTextField userIdField; // 사용자 ID 입력 필드
    private JTextField userNumbersField; // 사용자 번호 입력 필드
    private JLabel winningNumbersLabel; // 당첨 번호 레이블
    private JButton drawButton; // 추첨 버튼
    private JButton randomDrawButton; // 랜덤 추첨 버튼
    private JButton randomDraw10Button; // 랜덤 10번 추첨 버튼
    private DefaultListModel<String> listModel; // JList 모델
    public JList<String> entryList; // JList 선언
    private Connection connection; // 데이터베이스 연결

    public LotterySystem() {
        setTitle("복권 시스템");
        setSize(900, 800); // 프레임 크기 조정
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // 여백 추가
        gbc.fill = GridBagConstraints.HORIZONTAL; // 수평으로 채우기
        gbc.anchor = GridBagConstraints.CENTER; // 모든 컴포넌트를 중앙 정렬

        userIdField = new JTextField(20);
        userNumbersField = new JTextField(20);
        winningNumbersLabel = new JLabel("당첨 번호: ");
        
        // Create buttons
        drawButton = new JButton("추첨");
        randomDrawButton = new JButton("랜덤 추첨");
        randomDraw10Button = new JButton("랜덤 10번 추첨");

        // Set all buttons to the same preferred size
        Dimension buttonSize = new Dimension(150, 40); // Change as needed
        drawButton.setPreferredSize(buttonSize);
        randomDrawButton.setPreferredSize(buttonSize);
        randomDraw10Button.setPreferredSize(buttonSize);

        drawButton.addActionListener(new DrawButtonListener(this));
        randomDrawButton.addActionListener(new RandomDrawButtonListener(this));
        randomDraw10Button.addActionListener(new RandomDraw10ButtonListener(this));

        // 사용자 ID 라벨 및 필드
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("사용자 ID: "), gbc);

        gbc.gridx = 1;
        add(userIdField, gbc);

        // 번호 입력 라벨 및 필드
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("번호 입력 (쉼표로 구분): "), gbc);

        gbc.gridx = 1;
        add(userNumbersField, gbc);

        // 당첨 번호 라벨
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2; // 두 열을 차지
        add(winningNumbersLabel, gbc);

        // 버튼 배치 (가운데 정렬)
        gbc.gridwidth = 1; // 원래 열 수로 복구
        gbc.gridy = 3; // 버튼을 추가할 행 설정
        gbc.gridx = 0;
        add(drawButton, gbc);

        gbc.gridx = 1; // 다음 열에 랜덤 추첨 버튼 추가
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

        // 결과 조회 버튼 추가 (모두 같은 행에 배치)
        gbc.gridwidth = 1; // 원래 열 수로 복구
        gbc.gridy = 5; // 5행으로 설정

        // 1등 조회 버튼
        gbc.gridx = 0;
        JButton view1stButton = new JButton("1등 조회");
        view1stButton.setPreferredSize(buttonSize); // Set the same size
        view1stButton.addActionListener(e -> filterResults("1등"));
        add(view1stButton, gbc);

        // 2등 조회 버튼
        gbc.gridx = 1; // 다음 열에 2등 조회 버튼 추가
        JButton view2ndButton = new JButton("2등 조회");
        view2ndButton.setPreferredSize(buttonSize); // Set the same size
        view2ndButton.addActionListener(e -> filterResults("2등"));
        add(view2ndButton, gbc);

        // 3등 조회 버튼
        gbc.gridx = 2; // 다음 열에 3등 조회 버튼 추가
        JButton view3rdButton = new JButton("3등 조회");
        view3rdButton.setPreferredSize(buttonSize); // Set the same size
        view3rdButton.addActionListener(e -> filterResults("3등"));
        add(view3rdButton, gbc);

        // 미당첨 조회 버튼
        gbc.gridx = 3; // 다음 열에 미당첨 조회 버튼 추가
        JButton viewNotWinnersButton = new JButton("미당첨 조회");
        viewNotWinnersButton.setPreferredSize(buttonSize); // Set the same size
        viewNotWinnersButton.addActionListener(e -> filterResults("미당첨"));
        add(viewNotWinnersButton, gbc);
    }

    // 결과 필터링 메서드
    private void filterResults(String resultType) {
        try {
            connection = LotteryDatabase.getConnection(); // 데이터베이스 연결
            Statement stmt = connection.createStatement();
            String query = "SELECT * FROM lottery_entries WHERE result = '" + resultType + "'";
            ResultSet rs = stmt.executeQuery(query);
            listModel.clear(); // 기존 목록 초기화
            
            while (rs.next()) {
                String entry = rs.getString("user_id") + ": " + rs.getString("user_numbers") + " (" + resultType + ")";
                listModel.addElement(entry); // 필터링된 결과 추가
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "데이터베이스 오류: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close(); // 연결 종료
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
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
