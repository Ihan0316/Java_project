package mini_projectDTO;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

// 로또 랜덤 넘버 생성기
public class LotteryUtils {

	// 생성할 숫자의 범위와 최댓값 설정
	private static final int NUMBER_COUNT = 5;
	private static final int MAX_NUMBER = 10;

	// 당첨 번호 생성 메서드
	public static int[] generateWinningNumbers() {

		// 랜덤 변수 할당, 유니크넘버 해시태그로 설정
		Random random = new Random();
		Set<Integer> uniqueNumbers = new HashSet<>();

		// 유니크 넘버를 5번 생성하도록 하여 해시태그에 추가
		while (uniqueNumbers.size() < NUMBER_COUNT) {
			int number = random.nextInt(MAX_NUMBER) + 1;
			uniqueNumbers.add(number);
		} // while

		// 생성된 숫자를 정수 배열로 변환하기
		int[] winningNumbers = uniqueNumbers.stream().mapToInt(Integer::intValue).toArray();
		return winningNumbers;
	} // generateWinningNumbers 메서드

	// 다중 추첨 생성하는 메서드
	public static int[][] generateMultipleDraws(int drawCount) {

		// 2차원 배열로 설정하여 각 행에 로또 번호 저장
		int[][] draws = new int[drawCount][NUMBER_COUNT];
		for (int i = 0; i < drawCount; i++) {
			draws[i] = generateWinningNumbers();
		} // for 구문
		return draws;
	}// generateMultipleDraws 메서드

	// 당첨 일치 개수를 확인하는 메서드
	public static int countMatches(int[] userNumbers, int[] winningNumbers) {
		int count = 0;

		// 반복문 사용으로 각 숫자마다 당첨번호와 일치하면 카운트를 증가시킴
		for (int userNum : userNumbers) {
			for (int winNum : winningNumbers) {
				if (userNum == winNum)
					count++;
			} // for-each 문 2
		} // for-each 문 1
		return count;
	} // countMatches 메서드
} // LotteryUtils Class
