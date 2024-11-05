package mini_projectMain;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import mini_projectDAO.LotteryDatabase;

public class LotterySystem extends JFrame {
	private JTextField userIdField;
	private JTextField userNumbersField;
	private JLabel winningNumbersLabel;
	private JButton drawButton;
	private JButton randomDraw10Button;
	private DefaultListModel<String> listModel;
	public JList<String> entryList;
	private JComboBox<String> resultTypeComboBox;

	// 생성자 생성
	// GridBagLayout 을 활용하여 레이아웃을 설정, 컴포넌트 초기화
	public LotterySystem() {
		setTitle("복권 시스템");
		setSize(800, 700);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new GridBagLayout());
		initializeUIComponents();
	} // LotterySystem 생성자

	// initializeUIComponents 메서드
	private void initializeUIComponents() {

		// GridBagConstraints 를 생성하여 컴포넌트의 배치, 크기 조절
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.CENTER;

		// userIdField, userNumbersField 입력 필드 및 winningNumbersLabel 라벨 생성
		userIdField = new JTextField(20);
		userNumbersField = new JTextField(20);
		winningNumbersLabel = new JLabel("당첨 번호: ");

		// 추첨, 랜덤 추첨 버튼 생성
		drawButton = new JButton("추첨");
		randomDraw10Button = new JButton("랜덤 10번 추첨");

		// 위의 두버튼의 크기를 설정
		Dimension buttonSize = new Dimension(150, 40);
		drawButton.setPreferredSize(buttonSize);
		randomDraw10Button.setPreferredSize(buttonSize);

		// 버튼에 이벤트 리스너 추가
		drawButton.addActionListener(new DrawButtonListener(this));
		randomDraw10Button.addActionListener(new RandomDraw10ButtonListener(this));

		// 사용자 ID 라벨 배치
		gbc.gridx = 0;
		gbc.gridy = 0;
		add(new JLabel("사용자 ID: "), gbc);

		// 사용자 ID 입력 필드 배치
		gbc.gridx = 1;
		add(userIdField, gbc);

		// 번호 입력 라벨 배치
		gbc.gridx = 0;
		gbc.gridy = 1;
		add(new JLabel("번호 입력 (1~10중 5개 쉼표로 구분): "), gbc);

		// 번호 입력 필드 배치
		gbc.gridx = 1;
		add(userNumbersField, gbc);

		// 당첨 번호 라벨 배치
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 2;
		add(winningNumbersLabel, gbc);

		// 추첨 버튼 배치
		gbc.gridwidth = 1;
		gbc.gridy = 3;
		gbc.gridx = 0;
		add(drawButton, gbc);

		// 랜덤 추첨 버튼 배치
		gbc.gridx = 1;
		add(randomDraw10Button, gbc);

		// 결과 목록 JList 로 출력위한 설정
		listModel = new DefaultListModel<>();
		entryList = new JList<>(listModel);
		JScrollPane scrollPane = new JScrollPane(entryList);
		scrollPane.setPreferredSize(new Dimension(700, 250));
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridwidth = 3;
		gbc.fill = GridBagConstraints.BOTH;
		add(scrollPane, gbc);

		// 그리드 설정 초기화
		gbc.gridwidth = 1;
		gbc.gridy = 5;

		// 결과 유형 선택 콤보박스 추가
		resultTypeComboBox = new JComboBox<>(new String[] { "1등", "2등", "3등", "미당첨" });
		gbc.gridx = 0;
		gbc.gridy = 6;
		add(resultTypeComboBox, gbc);

		// 조회 버튼 추가
		gbc.gridx = 1;
		JButton viewButton = new JButton("조회");
		viewButton.setPreferredSize(buttonSize);
		viewButton.addActionListener(e -> {
			String resultType = (String) resultTypeComboBox.getSelectedItem();
			filterResults(resultType);
		}); // viewButton.addActionListner 문
		add(viewButton, gbc);

		// 전체 삭제 버튼 추가
		gbc.gridx = 2;
		gbc.gridy = 6;
		JButton deleteAllButton = new JButton("전체 삭제");
		deleteAllButton.setPreferredSize(buttonSize);
		deleteAllButton.addActionListener(new DeleteAllEntriesButtonListener(this));
		add(deleteAllButton, gbc);
	} // initializeUIComponents 메서드

	// filterResults 메서드
	// 결과 출력을 위한 필터링 시스템
	private void filterResults(String resultType) {

		// DB 연결 후 list 초기화
		try (Connection connection = LotteryDatabase.getConnection(); Statement stmt = connection.createStatement()) {

			// 필터링 된 결과 출력을 위한 SQL 구문
			String query = "SELECT * FROM lottery_entries WHERE result = '" + resultType + "'";
			ResultSet rs = stmt.executeQuery(query);
			listModel.clear();

			// 쿼리 결과 반복하면서 리스트에 추가
			while (rs.next()) {
				String entry = "ID: " + rs.getInt("id") + ", 사용자 ID: " + rs.getString("user_id") + ", 사용자 번호: "
						+ rs.getString("user_numbers") + ", 당첨 번호: " + rs.getString("winning_numbers") + ", 결과: "
						+ resultType;
				listModel.addElement(entry);
			} // while 문
		} // try 문

		// 예외 발생시 에러 메시지 표시
		catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "데이터베이스 오류: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
		} // catch 문
	} // filterResults 메서드

	// getter 메서드..
	public JTextField getUserIdField() {
		return userIdField;
	}

	public JTextField getUserNumbersField() {
		return userNumbersField;
	}

	public JLabel getWinningNumbersLabel() {
		return winningNumbersLabel;
	}

	public DefaultListModel<String> getListModel() {
		return listModel;
	}

	// 메인 메서드
	public static void main(String[] args) {
		// SwingUtilities.invokeLater 를 활용해 GUI 생성, 실행
		SwingUtilities.invokeLater(() -> {
			LotterySystem lotterySystem = new LotterySystem();
			lotterySystem.setVisible(true);
		}); // SwingUtilities.invokeLater 문
	} // main 메서드
} // LotterySystem Class