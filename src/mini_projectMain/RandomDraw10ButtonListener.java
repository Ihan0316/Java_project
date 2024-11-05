package mini_projectMain;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import javax.swing.JOptionPane;

import mini_projectDAO.LotteryDatabase;
import mini_projectDTO.LotteryUtils;

// RandomDraw10ButtonListener 클래스 정의
// ActionListener 를 구현하여 버튼 클릭 이벤트 처리
public class RandomDraw10ButtonListener implements ActionListener {
	private LotterySystem lotterySystem;
	private Random random;
	private Set<String> usedUserIds;

	// 생성자 생성, lotterySystem 을 받아 초기화 후, 난수 생성기와 사용자 ID HashSet을 초기화
	public RandomDraw10ButtonListener(LotterySystem lotterySystem) {
		this.lotterySystem = lotterySystem;
		this.random = new Random();
		this.usedUserIds = new HashSet<>();
	} // RandomDraw10ButtonListener 생성자

	// 재정의
	// actionPerformed 매서드, 고유 사용자 id 생성, 10개 랜덤 추천 결과 생성
	@Override
	public void actionPerformed(ActionEvent e) {
		String userId = generateUniqueUserId();
		int[][] randomDraws = LotteryUtils.generateMultipleDraws(10);
		StringBuilder resultBuilder = new StringBuilder("랜덤 10번 추첨 결과:\n");

		// 10회 반복하며, 각 추첨의 랜덤 번호, 당첨 번호를 생성하여 일치하는 숫자의 개수를 matchCount에 저장하여 result 에 반환
		for (int i = 0; i < randomDraws.length; i++) {
			int[] randomNumbers = randomDraws[i];
			int[] winningNumbers = LotteryUtils.generateWinningNumbers();
			int matchCount = LotteryUtils.countMatches(randomNumbers, winningNumbers);
			String result = getResultString(matchCount);

			// 문자열 생성
			resultBuilder.append("추첨 ").append(i + 1).append(": ").append(Arrays.toString(randomNumbers)).append(" -> ")
					.append(result).append("\n");

			// 결과 DB에 저장
			saveResultToDatabase(userId, Arrays.toString(randomNumbers), Arrays.toString(winningNumbers), result);
		} // for 문

		// 결과 메시지를 JOptionPane.showMessageDialog 를 이용하여 표시
		JOptionPane.showMessageDialog(lotterySystem, resultBuilder.toString(), "랜덤 10번 추첨 결과",
				JOptionPane.INFORMATION_MESSAGE);
	} // actionPerformed 메서드

	// generateUniqueUserId 메서드
	// 랜덤으로 사용된 ID가 사용되었는지 확인 후, 중복되지 않는 ID 생성
	private String generateUniqueUserId() {
		String userId;
		do {
			userId = "user" + random.nextInt(10000);
		} // do 문
		while (usedUserIds.contains(userId));
		usedUserIds.add(userId);
		return userId;
	} // generateUniqueUserId 메서드

	// saveResultToDatabase 메서드
	// 주어진 결과를 DB에 저장하는 기능
	private void saveResultToDatabase(String userId, String userNumbers, String winningNumbers, String result) {
		try (Connection conn = LotteryDatabase.getConnection();
				// PreparedStatement 를 사용하여 SQL 삽입 쿼리 준비
				PreparedStatement pstmt = conn.prepareStatement(
						"INSERT INTO lottery_entries (id, user_id, user_numbers, winning_numbers, result) VALUES (lottery_id_seq.NEXTVAL, ?, ?, ?, ?)")) {
			pstmt.setString(1, userId);
			pstmt.setString(2, userNumbers);
			pstmt.setString(3, winningNumbers);
			pstmt.setString(4, result);
			pstmt.executeUpdate();
		} // PreparedStatement 문

		// 예외 처리
		// 에외 발생 시 에러 메시지 표시
		catch (SQLException ex) {
			JOptionPane.showMessageDialog(lotterySystem, "데이터베이스에 저장하는 중 오류 발생", "DB 오류", JOptionPane.ERROR_MESSAGE);
		} // catch 문
	} // saveResultToDatabase 메서드

	// getResultString 메서드
	// 일치하는 숫자 개수에 따라 case 문을 사용하여 결과값 반환
	private String getResultString(int matchCount) {
		switch (matchCount) {
		case 5:
			return "1등";
		case 4:
			return "2등";
		case 3:
			return "3등";
		default:
			return "미당첨";
		} // switch 문
	} // getResultString 메서드
} // RandomDraw10ButtonListener Class
