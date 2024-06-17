package DB;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

public class DB {										// 전반적인 DB 메소드
	
	public void creationTable() {						// 프로그램 실행 초기 테이블 생성 메소드
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = DBconnect.connect();	
			DatabaseMetaData metadata = conn.getMetaData();
			ResultSet resultSet;
			resultSet = metadata.getTables(null, null, "currentWeather", null);
			if(resultSet.next()) {
				DBconnect.close();
				return;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			DBconnect.close();
			
		}

		
		// DB 테이블 존재 유무를 검사하는 코드
		String sql = "CREATE TABLE member ("			
				+ "member_ID    VARCHAR(20) PRIMARY KEY,"
				+ "member_PW    VARCHAR(20) NOT NULL,"
				+ "member_Nick  VARCHAR(20),"
				+ "member_Email VARCHAR(30) NOT NULL,"
				+ "UNIQUE KEY unique_nick (member_Nick)"
				+ ")";
		// DB에서 생성할 회원정보 테이블

		try {
			conn = DBconnect.connect();
			pstmt = conn.prepareStatement(sql);
			pstmt.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			DBconnect.close();
			
		}
		
		sql = "CREATE TABLE comment ("								// 코멘트 테이블
				+ "cmt_NO   	 INT AUTO_INCREMENT PRIMARY KEY,"	// 고유번호
				+ "member_Nick   VARCHAR(20),"						// 코멘트를 작성한 사람의 닉네임
				+ "cmt_Content	 VARCHAR(30) NOT NULL,"				// 댓글 내용
				+ "cmt_Date		 DATE		 NOT NULL,"				// 댓글 날짜
				+ "CONSTRAINT fk_comment_nick FOREIGN KEY (member_Nick)"
				+ "			  REFERENCES member (member_Nick)"
				+ ")"; 				
		// DB에서 생성할 코멘트 테이블

		try {
			conn = DBconnect.connect();
			pstmt = conn.prepareStatement(sql);
			pstmt.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			DBconnect.close();
			
		}

		sql = "CREATE TABLE currentWeather ("						// 실제 날씨 수집 정보 테이블
				+ "crtW_NO  	 INT AUTO_INCREMENT PRIMARY KEY,"	// 고유번호
				+ "member_Nick   VARCHAR(20),"						// 아이콘을 누른 사람의 닉네임
				+ "crtW_Type 	 INT			NOT NULL,"			// 아이콘 타입
				+ "crtW_Date 	 DATE			NOT NULL," 			// 날짜
				+ "CONSTRAINT fk_weather_nick FOREIGN KEY (member_Nick)"
				+ "			  REFERENCES member (member_Nick)"
				+ ")"; 
		// DB에서 생성할 날씨현황(아이콘) 테이블

		try {
			conn = DBconnect.connect();
			pstmt = conn.prepareStatement(sql);
			pstmt.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			DBconnect.close();
			
		}

	}

	public boolean checkChar(String word, int length) {	// DB에 입력받을 문자열을 체크
		Boolean result = true;
		
		// DB 형식에 안맞을시(빈칸 & 공백을 포함)
		if(word.replaceAll(" ", "").equals(word) == false || word.replaceAll(" ", "").equals("") == true) {	
        	JOptionPane.showMessageDialog(null, "공백은 입력할 수 없습니다.", "알림!", JOptionPane.WARNING_MESSAGE);
			result = false;
		}
		else if (word.length() > length) { 				// DB 형식에 안맞을시(글자 수 초과)
        	JOptionPane.showMessageDialog(null, length + "글자 이하로 입력해주세요.", "알림!", JOptionPane.WARNING_MESSAGE);
			result = false;
		}
		
		return result;
	}
	
	public boolean isValidEmail(String email) {		    // 이메일 양식이 올바른지 체크
		boolean err = false;
		// \ \w<문자> + @ <@> \ \w<문자> + \ \.<.>  \ \w<문자> + (\ \.<.> \ \w<문자> +<앞의 표현식이 1개이상>)<소괄호: 있을수도있다> ?<앞의 표현식이 없거나 1개>
		String regex = "\\w+@\\w+\\.\\w+(\\.\\w+)?";    // 이메일을 체크하는 정규식 생성
		Pattern p = Pattern.compile(regex);				// 정규식 패턴 생성
		Matcher m = p.matcher(email);					// 패턴에 맞춰 문자열을 필터링	

		err = m.matches();								// 이메일이 양식에 올바른지 확인(일치시 true)
		  
		if(err == false) {
			JOptionPane.showMessageDialog(null, "올바른 이메일을 입력해주세요.", "알림!", JOptionPane.WARNING_MESSAGE);
		}
		
		return err;
	}
}
