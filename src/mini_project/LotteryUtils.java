package mini_project;

import java.util.Random;
import java.util.HashSet;
import java.util.Set;

public class LotteryUtils {
    private static final int NUMBER_COUNT = 5;
    private static final int MAX_NUMBER = 10;

    public static int[] generateWinningNumbers() {
        Random random = new Random();
        Set<Integer> uniqueNumbers = new HashSet<>();

        // 중복되지 않는 번호 생성
        while (uniqueNumbers.size() < NUMBER_COUNT) {
            int number = random.nextInt(MAX_NUMBER) + 1; // 1부터 10 사이의 랜덤 숫자
            uniqueNumbers.add(number);
        }

        // Set을 배열로 변환
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
                if (userNum == winNum) count++;
            }
        }
        return count;
    }
}
