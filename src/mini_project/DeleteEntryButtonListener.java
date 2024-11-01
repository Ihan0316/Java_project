package mini_project;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

class DeleteEntryButtonListener implements ActionListener {
    private LotterySystem lotterySystem;

    public DeleteEntryButtonListener(LotterySystem lotterySystem) {
        this.lotterySystem = lotterySystem;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String idInput = JOptionPane.showInputDialog(lotterySystem, "삭제할 레코드 ID를 입력하세요:");
        if (idInput != null) {
            try {
                int id = Integer.parseInt(idInput);
                deleteEntryFromDatabase(id);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(lotterySystem, "유효한 ID를 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteEntryFromDatabase(int id) {
        try (Connection conn = LotteryDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM lottery_entries WHERE id = ?")) {
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(lotterySystem, "레코드 삭제 성공!", "삭제 완료", JOptionPane.INFORMATION_MESSAGE);
                updateEntryList(); // 레코드 삭제 후 JList 업데이트
            } else {
                JOptionPane.showMessageDialog(lotterySystem, "해당 ID의 레코드가 존재하지 않습니다.", "삭제 실패",
                        JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(lotterySystem, "데이터베이스에서 레코드를 삭제하는 중 오류 발생", "DB 오류",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateEntryList() {
        // JList를 갱신하여 최신 상태 반영
        DefaultListModel<String> listModel = lotterySystem.getListModel();
        listModel.clear(); // 기존 항목 삭제

        // 데이터베이스에서 현재 레코드 조회
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
                listModel.addElement("ID: " + id + ", 사용자 ID: " + userId +
                                     ", 사용자 번호: " + userNumbers +
                                     ", 당첨 번호: " + winningNumbers +
                                     ", 결과: " + result);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(lotterySystem, "데이터베이스에서 데이터를 조회하는 중 오류 발생", "DB 오류",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
