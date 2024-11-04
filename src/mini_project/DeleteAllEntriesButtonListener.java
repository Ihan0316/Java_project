package mini_project;

import javax.swing.*;

import java.awt.event.*;
import java.sql.*;
class DeleteAllEntriesButtonListener implements ActionListener {
	private LotterySystem lotterySystem;

	public DeleteAllEntriesButtonListener(LotterySystem lotterySystem) {
		this.lotterySystem = lotterySystem;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		int confirm = JOptionPane.showConfirmDialog(lotterySystem, "전체 테이블을 삭제하시겠습니까?", "확인",
				JOptionPane.YES_NO_OPTION);
		if (confirm == JOptionPane.YES_OPTION) {
			deleteAllEntriesFromDatabase();
		}
	}

	private void deleteAllEntriesFromDatabase() {
		try (Connection conn = LotteryDatabase.getConnection();
				PreparedStatement pstmt = conn.prepareStatement("DELETE FROM lottery_entries")) {
			int rowsAffected = pstmt.executeUpdate();

			if (rowsAffected > 0) {
				JOptionPane.showMessageDialog(lotterySystem, "모든 레코드가 삭제되었습니다.", "삭제 완료",
						JOptionPane.INFORMATION_MESSAGE);
				lotterySystem.getListModel().clear(); // JList 모델 업데이트
			} else {
				JOptionPane.showMessageDialog(lotterySystem, "삭제할 레코드가 없습니다.", "삭제 실패", JOptionPane.WARNING_MESSAGE);
			}
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(lotterySystem, "데이터베이스에서 레코드를 삭제하는 중 오류 발생", "DB 오류",
					JOptionPane.ERROR_MESSAGE);
		}
	}
}
