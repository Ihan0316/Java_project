package mini_project;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

class ViewEntriesButtonListener implements ActionListener {
    private LotterySystem lotterySystem;

    public ViewEntriesButtonListener(LotterySystem lotterySystem) {
        this.lotterySystem = lotterySystem;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        StringBuilder resultBuilder = new StringBuilder();
        try (Connection conn = LotteryDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM lottery_entries");
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String userId = rs.getString("user_id");
                String userNumbers = rs.getString("user_numbers");
                String winningNumbers = rs.getString("winning_numbers");
                String result = rs.getString("result");

                // JList에 표시할 문자열 구성
                resultBuilder.append("ID: ").append(id)
                             .append(", 사용자 ID: ").append(userId)
                             .append(", 사용자 번호: ").append(userNumbers)
                             .append(", 당첨 번호: ").append(winningNumbers)
                             .append(", 결과: ").append(result)
                             .append("\n");
            }

            // JList 모델에 결과 추가
            DefaultListModel<String> listModel = lotterySystem.getListModel();
            listModel.clear(); // 기존 항목 삭제
            if (resultBuilder.length() == 0) {
                listModel.addElement("조회된 결과가 없습니다.");
            } else {
                String[] results = resultBuilder.toString().split("\n");
                for (String entry : results) {
                    listModel.addElement(entry);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(lotterySystem, "데이터베이스에서 데이터를 조회하는 중 오류 발생", "DB 오류",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
    }
}
