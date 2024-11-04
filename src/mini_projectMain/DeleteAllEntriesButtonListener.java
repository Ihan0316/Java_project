package mini_projectMain;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import mini_projectDAO.LotteryDatabase;

// 전체 삭제 클래스 정의 ActionListner를 이용하여 구현
class DeleteAllEntriesButtonListener implements ActionListener {

	// lotterySystem의 인스턴스를 저장할 필드 선언
	private LotterySystem lotterySystem;

	// 생성자 생성, lotterySystem의 매개변수 받아서 필드에 할당
	public DeleteAllEntriesButtonListener(LotterySystem lotterySystem) {
		this.lotterySystem = lotterySystem;
	} // DeleteAllEntriesButtonListener 생성자

	// ActionListner의 메서드를 재정의 하여 버큰 클릭시 호출되는 메서드를 정의
	@Override
	public void actionPerformed(ActionEvent e) {

		// JOptionPane.showConfirmDialog를 통해 확인 대화상자를 띄워 예, 아니오의 옵션 제공
		int confirm = JOptionPane.showConfirmDialog(lotterySystem, "전체 테이블을 삭제하시겠습니까?", "확인",
				JOptionPane.YES_NO_OPTION);

		// 예를 누를시 전체 데이터 삭제 함수 호출
		if (confirm == JOptionPane.YES_OPTION) {
			deleteAllEntriesFromDatabase();
		} // JOptionPane.YES_OPTION 이 "예" 일 경우 deleteAllEntriesFromDatabase 함수 호출
	} // 버튼 클릭 메서드

	// DB에서 모든 레코드를 삭제하는 함수
	private void deleteAllEntriesFromDatabase() {

		// LotteryDatabase.getConnection 을 통해 DB연결을 생성
		try (Connection conn = LotteryDatabase.getConnection();
				// PreparedStatement 을 사용해 삭제 SQL문 생성
				PreparedStatement pstmt = conn.prepareStatement("DELETE FROM lottery_entries")) {
			int rowsAffected = pstmt.executeUpdate();

			// 삭제된 행이 있는지 확인
			// 있을 경우 if 문 실행
			if (rowsAffected > 0) {
				JOptionPane.showMessageDialog(lotterySystem, "모든 레코드가 삭제되었습니다.", "삭제 완료",
						JOptionPane.INFORMATION_MESSAGE);
				lotterySystem.getListModel().clear();
			} // if 문
				// 없을 경우 else 문 실행
			else {
				JOptionPane.showMessageDialog(lotterySystem, "삭제할 레코드가 없습니다.", "삭제 실패", JOptionPane.WARNING_MESSAGE);
			} // else 문
		} // PreparedStatement 문
			// 예외 처리
		catch (SQLException ex) {
			JOptionPane.showMessageDialog(lotterySystem, "데이터베이스에서 레코드를 삭제하는 중 오류 발생", "DB 오류",
					JOptionPane.ERROR_MESSAGE);
		} // catch 문
	} // deleteAllEntriesFromDatabase 함수
} // DeleteAllEntriesButtonListener Class
