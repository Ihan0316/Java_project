package mini_project;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class DrawButtonListener implements ActionListener {
    private LotterySystem lotterySystem;

    public DrawButtonListener(LotterySystem lotterySystem) {
        this.lotterySystem = lotterySystem;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String userId = lotterySystem.getUserIdField().getText().trim();
        String userNumbersInput = lotterySystem.getUserNumbersField().getText().trim();

        if (userId.isEmpty() || userNumbersInput.isEmpty()) {
            showError("사용자 ID와 번호를 입력하세요.");
            return;
        }

        int[] userNumbers = parseUserNumbers(userNumbersInput);
        if (userNumbers == null) return; // 오류 발생 시 메서드 종료

        int[] winningNumbers = LotteryUtils.generateWinningNumbers();
        lotterySystem.getWinningNumbersLabel().setText("당첨 번호: " + Arrays.toString(winningNumbers));
        
        int matchCount = countMatches(userNumbers, winningNumbers);
        showResult(userId, userNumbers, matchCount, winningNumbers); // 당첨 번호 전달

        saveResultToDatabase(userId, Arrays.toString(userNumbers), Arrays.toString(winningNumbers), getResultString(matchCount));
        clearInputFields();
    }


    private void showError(String message) {
        JOptionPane.showMessageDialog(lotterySystem, message, "오류", JOptionPane.ERROR_MESSAGE);
    }

    private void showResult(String userId, int[] userNumbers, int matchCount, int[] winningNumbers) {
        StringBuilder result = new StringBuilder("사용자 ID: " + userId + "\n당신의 번호: " + Arrays.toString(userNumbers) + "\n");
        result.append("당첨 번호: " + Arrays.toString(winningNumbers) + "\n"); // 당첨 번호 추가
        result.append(matchCount == 5 ? "1등 당첨되었습니다!" :
                      matchCount == 4 ? "2등 당첨되었습니다!" :
                      matchCount == 3 ? "3등 당첨되었습니다!" : "당첨되지 않았습니다.");
        JOptionPane.showMessageDialog(lotterySystem, result.toString(), "추첨 결과", JOptionPane.INFORMATION_MESSAGE);
    }


    private void clearInputFields() {
        lotterySystem.getUserIdField().setText("");
        lotterySystem.getUserNumbersField().setText("");
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
            showError("데이터베이스에 저장하는 중 오류 발생");
        }
    }

    private int[] parseUserNumbers(String userNumbersInput) {
        String[] numberStrings = userNumbersInput.split(",");
        
        if (numberStrings.length != 5) { // 숫자 개수가 5개인지 확인
            showError("숫자는 5개를 입력해야 합니다.");
            lotterySystem.getUserNumbersField().setText(""); // 입력란 비우기
            return null; // 잘못된 입력 형식
        }

        int[] numbers = new int[numberStrings.length];
        Set<Integer> uniqueNumbers = new HashSet<>(); // 중복 확인을 위한 Set

        try {
            for (int i = 0; i < numberStrings.length; i++) {
                int number = Integer.parseInt(numberStrings[i].trim());
                if (number < 1 || number > 10) {
                    showError("모든 숫자는 1에서 10 사이여야 합니다.");
                    lotterySystem.getUserNumbersField().setText(""); // 입력란 비우기
                    return null; // 잘못된 입력 형식
                }

                // 중복된 숫자인지 확인
                if (!uniqueNumbers.add(number)) {
                    showError("중복된 숫자가 입력되었습니다: " + number);
                    lotterySystem.getUserNumbersField().setText(""); // 입력란 비우기
                    return null;
                }
                
                numbers[i] = number;
            }
        } catch (NumberFormatException e) {
            showError("번호 입력 형식이 잘못되었습니다. 예: 1,2,3,4,5");
            lotterySystem.getUserNumbersField().setText(""); // 입력란 비우기
            return null;
        }

        return numbers;
    }

    private int countMatches(int[] userNumbers, int[] winningNumbers) {
        int count = 0;
        for (int num : userNumbers) {
            for (int winNum : winningNumbers) {
                if (num == winNum) count++;
            }
        }
        return count;
    }

    private String getResultString(int matchCount) {
        switch (matchCount) {
            case 5: return "1등";
            case 4: return "2등";
            case 3: return "3등";
            default: return "미당첨";
        }
    }
}
