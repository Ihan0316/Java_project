package mini_project;

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

public class RandomDrawButtonListener implements ActionListener {
	private LotterySystem lotterySystem;
	private Random random;

	public RandomDrawButtonListener(LotterySystem lotterySystem) {
		this.lotterySystem = lotterySystem;
		this.random = new Random();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String userId = "user" + random.nextInt(10000); // 임의의 사용자 ID 생성
		int[] userNumbers = generateRandomNumbers(); // 랜덤 번호 생성

		int[] winningNumbers = LotteryUtils.generateWinningNumbers();
		lotterySystem.getWinningNumbersLabel().setText("당첨 번호: " + Arrays.toString(winningNumbers));

		// 일치하는 숫자의 개수 계산
		int matchCount = countMatches(userNumbers, winningNumbers);

		// 결과 출력
		showResult(userId, userNumbers, matchCount, winningNumbers); // 당첨 번호 포함하여 결과 출력

		// 데이터베이스에 결과 저장
		saveResultToDatabase(userId, Arrays.toString(userNumbers), Arrays.toString(winningNumbers),
				getResultString(matchCount));
	}

	private void showResult(String userId, int[] userNumbers, int matchCount, int[] winningNumbers) {
		StringBuilder result = new StringBuilder("사용자 ID: " + userId + "\n");
		result.append("당신의 번호: " + Arrays.toString(userNumbers) + "\n");
		result.append("당첨 번호: " + Arrays.toString(winningNumbers) + "\n"); // 당첨 번호 추가

		// 등수 판별 및 결과 출력
		if (matchCount == 5) {
			result.append("1등 당첨되었습니다!");
		} else if (matchCount == 4) {
			result.append("2등 당첨되었습니다!");
		} else if (matchCount == 3) {
			result.append("3등 당첨되었습니다!");
		} else {
			result.append("당첨되지 않았습니다.");
		}

		JOptionPane.showMessageDialog(lotterySystem, result.toString(), "추첨 결과", JOptionPane.INFORMATION_MESSAGE);
	}

	private int[] generateRandomNumbers() {
		int[] numbers = new int[5];
		Set<Integer> uniqueNumbers = new HashSet<>();

		while (uniqueNumbers.size() < 5) {
			int number = random.nextInt(20) + 1; // 1부터 20 사이의 숫자
			uniqueNumbers.add(number); // 중복이 아니면 Set에 추가
		}

		int index = 0;
		for (int num : uniqueNumbers) {
			numbers[index++] = num;
		}
		return numbers;
	}

	private void saveResultToDatabase(String userId, String userNumbers, String winningNumbers, String result) {
		try (Connection conn = LotteryDatabase.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(
						"INSERT INTO lottery_entries (id, user_id, user_numbers, winning_numbers, result) VALUES (lottery_id_seq.NEXTVAL, ?, ?, ?, ?)")) {
			pstmt.setString(1, userId);
			pstmt.setString(2, userNumbers);
			pstmt.setString(3, winningNumbers);
			pstmt.setString(4, result);
			pstmt.executeUpdate();
		} catch (SQLException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(lotterySystem, "데이터베이스에 저장하는 중 오류 발생", "오류", JOptionPane.ERROR_MESSAGE);
		}
	}

	// 일치하는 숫자의 개수를 세는 메서드
	private int countMatches(int[] userNumbers, int[] winningNumbers) {
		int count = 0;
		for (int num : userNumbers) {
			for (int winNum : winningNumbers) {
				if (num == winNum) {
					count++;
				}
			}
		}
		return count;
	}

	// 결과 문자열을 반환하는 메서드
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
		}
	}
}
