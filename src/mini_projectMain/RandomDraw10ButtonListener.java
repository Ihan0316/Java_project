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

public class RandomDraw10ButtonListener implements ActionListener {
	private LotterySystem lotterySystem;
	private Random random;
	private Set<String> usedUserIds;

	public RandomDraw10ButtonListener(LotterySystem lotterySystem) {
		this.lotterySystem = lotterySystem;
		this.random = new Random();
		this.usedUserIds = new HashSet<>();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String userId = generateUniqueUserId();
		int[][] randomDraws = LotteryUtils.generateMultipleDraws(10);
		StringBuilder resultBuilder = new StringBuilder("랜덤 10번 추첨 결과:\n");

		for (int i = 0; i < randomDraws.length; i++) {
			int[] randomNumbers = randomDraws[i];
			int[] winningNumbers = LotteryUtils.generateWinningNumbers();
			int matchCount = LotteryUtils.countMatches(randomNumbers, winningNumbers);
			String result = getResultString(matchCount);

			resultBuilder.append("추첨 ").append(i + 1).append(": ").append(Arrays.toString(randomNumbers)).append(" -> ")
					.append(result).append("\n");

			saveResultToDatabase(userId, Arrays.toString(randomNumbers), Arrays.toString(winningNumbers), result);
		}

		JOptionPane.showMessageDialog(lotterySystem, resultBuilder.toString(), "랜덤 10번 추첨 결과",
				JOptionPane.INFORMATION_MESSAGE);
	}

	private String generateUniqueUserId() {
		String userId;
		do {
			userId = "user" + random.nextInt(10000);
		} while (usedUserIds.contains(userId));
		usedUserIds.add(userId);
		return userId;
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
			JOptionPane.showMessageDialog(lotterySystem, "데이터베이스에 저장하는 중 오류 발생", "DB 오류", JOptionPane.ERROR_MESSAGE);
		}
	}

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
