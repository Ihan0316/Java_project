package mini_project;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LotterySystem extends JFrame {
    private JTextField userIdField;
    private JTextField userNumbersField;
    private JLabel winningNumbersLabel;
    private JButton drawButton;
    private JButton randomDraw10Button;
    private DefaultListModel<String> listModel;
    public JList<String> entryList;
    private JComboBox<String> resultTypeComboBox;

    public LotterySystem() {
        setTitle("복권 시스템");
        setSize(900, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        initializeUIComponents();
    }

    private void initializeUIComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        userIdField = new JTextField(20);
        userNumbersField = new JTextField(20);
        winningNumbersLabel = new JLabel("당첨 번호: ");
        
        drawButton = new JButton("추첨");
        randomDraw10Button = new JButton("랜덤 10번 추첨");

        Dimension buttonSize = new Dimension(150, 40);
        drawButton.setPreferredSize(buttonSize);
        randomDraw10Button.setPreferredSize(buttonSize);

        drawButton.addActionListener(new DrawButtonListener(this));
        randomDraw10Button.addActionListener(new RandomDraw10ButtonListener(this));

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("사용자 ID: "), gbc);

        gbc.gridx = 1;
        add(userIdField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("번호 입력 (1~20중 5개 쉼표로 구분): "), gbc);

        gbc.gridx = 1;
        add(userNumbersField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        add(winningNumbersLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 3;
        gbc.gridx = 0;
        add(drawButton, gbc);

        gbc.gridx = 1;
        add(randomDraw10Button, gbc);

        listModel = new DefaultListModel<>();
        entryList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(entryList);
        scrollPane.setPreferredSize(new Dimension(700, 250));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        add(scrollPane, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 5;

        resultTypeComboBox = new JComboBox<>(new String[]{"1등", "2등", "3등", "미당첨"});
        gbc.gridx = 0;
        gbc.gridy = 6;
        add(resultTypeComboBox, gbc);

        gbc.gridx = 1;
        JButton viewButton = new JButton("조회");
        viewButton.setPreferredSize(buttonSize);
        viewButton.addActionListener(e -> {
            String resultType = (String) resultTypeComboBox.getSelectedItem();
            filterResults(resultType);
        });
        add(viewButton, gbc);

        gbc.gridx = 2;
        gbc.gridy = 6 ;
        JButton deleteAllButton = new JButton("전체 삭제");
        deleteAllButton.setPreferredSize(buttonSize);
        deleteAllButton.addActionListener(new DeleteAllEntriesButtonListener(this));
        add(deleteAllButton, gbc);
    }

    private void filterResults(String resultType) {
        try (Connection connection = LotteryDatabase.getConnection();
             Statement stmt = connection.createStatement()) {
             
            String query = "SELECT * FROM lottery_entries WHERE result = '" + resultType + "'";
            ResultSet rs = stmt.executeQuery(query);
            listModel.clear();
            
            while (rs.next()) {
                String entry = "ID: " + rs.getInt("id") + ", 사용자 ID: " + rs.getString("user_id") +
                               ", 사용자 번호: " + rs.getString("user_numbers") +
                               ", 당첨 번호: " + rs.getString("winning_numbers") +
                               ", 결과: " + resultType;
                listModel.addElement(entry);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "데이터베이스 오류: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
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
        return listModel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LotterySystem lotterySystem = new LotterySystem();
            lotterySystem.setVisible(true);
        });
    }
}
