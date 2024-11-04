package mini_projectDTO;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class LotteryUtils {
	private static final int NUMBER_COUNT = 5;
	private static final int MAX_NUMBER = 10;

	public static int[] generateWinningNumbers() {
		Random random = new Random();
		Set<Integer> uniqueNumbers = new HashSet<>();

		while (uniqueNumbers.size() < NUMBER_COUNT) {
			int number = random.nextInt(MAX_NUMBER) + 1;
			uniqueNumbers.add(number);
		}

		int[] winningNumbers = uniqueNumbers.stream().mapToInt(Integer::intValue).toArray();
		return winningNumbers;
	}

	public static int[][] generateMultipleDraws(int drawCount) {
		int[][] draws = new int[drawCount][NUMBER_COUNT];
		for (int i = 0; i < drawCount; i++) {
			draws[i] = generateWinningNumbers();
		}
		return draws;
	}

	public static int countMatches(int[] userNumbers, int[] winningNumbers) {
		int count = 0;
		for (int userNum : userNumbers) {
			for (int winNum : winningNumbers) {
				if (userNum == winNum)
					count++;
			}
		}
		return count;
	}
}
