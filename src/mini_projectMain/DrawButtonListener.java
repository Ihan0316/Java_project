package mini_projectMain;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JOptionPane;

import mini_projectDAO.LotteryDatabase;
import mini_projectDTO.LotteryUtils;

// DrawButtonListener 을 ActionListener 을 이용하여 구현
public class DrawButtonListener implements ActionListener {
	private LotterySystem lotterySystem;

	// 생성자 생성, lotterySystem의 매개변수 받아서 필드에 할당
	public DrawButtonListener(LotterySystem lotterySystem) {
		this.lotterySystem = lotterySystem;
	} // DrawButtonListener 생성자

	// ActionListner의 메서드를 재정의 하여 버큰 클릭시 호출되는 메서드를 정의
	@Override
	public void actionPerformed(ActionEvent e) {

		// 문자의 앞 뒤 여백을 trim()으로 제거 후 사용자의 ID, 번호입력을 가져옴
		String userId = lotterySystem.getUserIdField().getText().trim();
		String userNumbersInput = lotterySystem.getUserNumbersField().getText().trim();

		// 입력 필드가 비어있을 경우, Error 를 보여주고 메서드 종료
		if (userId.isEmpty() || userNumbersInput.isEmpty()) {
			showError("사용자 ID와 번호를 입력하세요.");
			return;
		} // if 문

		// 입력된 번호를 int 배열로 변환
		int[] userNumbers = parseUserNumbers(userNumbersInput);

		// 실패시 null 을 반환 후 메서드 종료
		if (userNumbers == null)
			return;

		// LotteryUtils 클래스에서 당첨 번호를 생성 후, int 배열로 변환 후, UI 에 표시
		int[] winningNumbers = LotteryUtils.generateWinningNumbers();
		lotterySystem.getWinningNumbersLabel().setText("당첨 번호: " + Arrays.toString(winningNumbers));

		// 사용자가 입력한 번호와 당첨 번호를 비교하여 일치하는 숫자 개수 카운팅 후 matchCount에 반환
		int matchCount = countMatches(userNumbers, winningNumbers);

		// 결과 보여주기
		showResult(userId, userNumbers, matchCount, winningNumbers);

		// 결과를 DB에 저장 후 입력 필드 초기화
		saveResultToDatabase(userId, Arrays.toString(userNumbers), Arrays.toString(winningNumbers),
				getResultString(matchCount));
		clearInputFields();
	} // actionPerformed 함수

	// 오류 상황 발생시 "오류" 메시지 표시
	private void showError(String message) {
		JOptionPane.showMessageDialog(lotterySystem, message, "오류", JOptionPane.ERROR_MESSAGE);
	} // showError 함수

	// showResult 메서드
	// 사용자의 ID, 번호를 포함한 메시지를 구성
	private void showResult(String userId, int[] userNumbers, int matchCount, int[] winningNumbers) {
		StringBuilder result = new StringBuilder(
				"사용자 ID: " + userId + "\n당신의 번호: " + Arrays.toString(userNumbers) + "\n");

		// 일치하는 번호의 수를 matchCount에서 받아 온 후 이 결과에 따라 메시지 출력
		result.append("당첨 번호: " + Arrays.toString(winningNumbers) + "\n");
		result.append(matchCount == 5 ? "1등 당첨되었습니다!"
				: matchCount == 4 ? "2등 당첨되었습니다!" : matchCount == 3 ? "3등 당첨되었습니다!" : "당첨되지 않았습니다.");
		JOptionPane.showMessageDialog(lotterySystem, result.toString(), "추첨 결과", JOptionPane.INFORMATION_MESSAGE);
	} // showResult 함수

	// clearInputFields 메서드
	// 사용자 입력 필드를 비우는 기능
	private void clearInputFields() {
		lotterySystem.getUserIdField().setText("");
		lotterySystem.getUserNumbersField().setText("");
	} // clearInputFields 함수

	// DB에 연결 후 lottery_entries 테이블에 결과를 저장하기 위한 SQL 구문
	private void saveResultToDatabase(String userId, String userNumbers, String winningNumbers, String result) {
		try (Connection conn = LotteryDatabase.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(
						"INSERT INTO lottery_entries (id, user_id, user_numbers, winning_numbers, result) VALUES (lottery_id_seq.NEXTVAL, ?, ?, ?, ?)")) {

			// SQL 쿼리 실행, 오류 발생시 showError() 띄우기
			pstmt.setString(1, userId);
			pstmt.setString(2, userNumbers);
			pstmt.setString(3, winningNumbers);
			pstmt.setString(4, result);
			pstmt.executeUpdate();
		} // pstmt 문
		catch (SQLException ex) {
			showError("데이터베이스에 저장하는 중 오류 발생");
		} // catch 문
	} // saveResultToDatabase 함수

	// parseUserNumbers 메서드
	private int[] parseUserNumbers(String userNumbersInput) {

		// 입력된 문자열을 쉼표로 분리하여 numberStrings 에 배열로 저장
		String[] numberStrings = userNumbersInput.split(",");

		// 번호 개수를 검사하여 5개가 아닐 경우 에러 메시지 출력 후 null 반환
		if (numberStrings.length != 5) {
			showError("숫자는 5개를 입력해야 합니다.");
			lotterySystem.getUserNumbersField().setText("");
			return null;
		} // if 문

		// 유니크숫자를 저장하기 위한 HashSet 생성
		int[] numbers = new int[numberStrings.length];
		Set<Integer> uniqueNumbers = new HashSet<>();

		// 각 문자열을 정수로 번환, 공백 제거 후 1~20 사이인지 검사
		try {
			for (int i = 0; i < numberStrings.length; i++) {
				int number = Integer.parseInt(numberStrings[i].trim());
				if (number < 1 || number > 20) {
					showError("모든 숫자는 1에서 20 사이여야 합니다.");
					lotterySystem.getUserNumbersField().setText("");
					return null;
				} // if 1

				// 중복 검사로 Set에 숫자를 추가하며 중복 여부 확인
				if (!uniqueNumbers.add(number)) {
					showError("중복된 숫자가 입력되었습니다: " + number);
					lotterySystem.getUserNumbersField().setText("");
					return null;
				} // if 2

				numbers[i] = number;
			} // for 문
		} // try 문
			// 예외 처리
			// 숫자 변환 중 오류가 발생하면 에러 메시지 출력
		catch (NumberFormatException e) {
			showError("번호 입력 형식이 잘못되었습니다. 예: 1,2,3,4,5");
			lotterySystem.getUserNumbersField().setText("");
			return null;
		} // catch 문

		// 모든 숫자가 올바르게 입력시 배열 반환
		return numbers;
	} // parseUserNumbers 함수

	// countMatches 메서드
	// 일치하는 번호 수 세서 반환
	private int countMatches(int[] userNumbers, int[] winningNumbers) {
		int count = 0;
		for (int num : userNumbers) {
			for (int winNum : winningNumbers) {
				if (num == winNum)
					count++;
			} // for 1 - if 문
		} // for 2
		return count;
	} // countMatches 함수

	// getResultString 메서드
	// 당첨 결과를 matchCount를 통해 비교 후, 문자열 반환
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
	} // getResultString 함수
} // DrawButtonListener Class
