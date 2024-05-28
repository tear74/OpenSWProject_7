package DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

public class DBControl {
	
	public void creationTable() {						// 프로그램 실행 초기 테이블 생성 메소드
		Connection conn = null;
		PreparedStatement pstmt = null;
		// 추후 DB 테이블 존재 유무를 검사하는 코드 추가
		String sql = "CREATE TABLE member ("			
				+ " member_ID    varchar(20) PRIMARY KEY,"
				+ " member_PW    varchar(20),"
				+ " member_Nick  varchar(12) UNIQUE KEY,"
				+ " member_Email varchar(30))";
		// DB에서 생성할 사용자 테이블

		try {
			conn = DBconnect.connect();
			pstmt = conn.prepareStatement(sql);
			pstmt.executeQuery();
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			DBconnect.close();
			
		}
		
		sql = "CREATE TABLE comment ("
				+ "cmt_NO    INT(6)		 PRIMARY KEY,"
				+ "cmt_ID    varchar(20),   "
				+ "cmt_Title varchar(30),"
				+ "cmt_Date  varchar(30))"; 
		// DB에서 생성할 코멘트 테이블

		try {
			conn = DBconnect.connect();
			pstmt = conn.prepareStatement(sql);
			pstmt.executeQuery();
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			DBconnect.close();
			
		}

		sql = "CREATE TABLE currentWeather ("
				+ "crtW_NO    INT(6)		 PRIMARY KEY,"
				+ "crtW_ID    varchar(20),   "
				+ "crtW_Type  INT(1),"
				+ "crtW_Date  varchar(30))"; 
		// DB에서 생성할 날씨현황 테이블

		try {
			conn = DBconnect.connect();
			pstmt = conn.prepareStatement(sql);
			pstmt.executeQuery();
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			DBconnect.close();
			
		}

	}
	
	public boolean register(String ID, String PW, String Nick, String email) { // 회원가입 메소드
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "INSERT INTO member VALUES (?, ?, ?, ?)";
		boolean result = false;

		try {
			conn = DBconnect.connect();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, ID);
			pstmt.setString(2, PW);
			pstmt.setString(3, Nick);
			pstmt.setString(4, email);

			int r = pstmt.executeUpdate(); 				// 생성 성공시(만들어진 row들) 값을 받음(없을시 0)
			System.out.println("return result = " + r); // 체크용
			if (r > 0) { 								// 생성된 row가 있을 때
				result = true; 							// true 반환
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBconnect.close();
		}

		return result;
	}
	
	public String getNick(String ID) {					// DB에서 닉네임을 불러옴
		Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        String sql = "SELECT userName from member where member_ID = ?";
        String result = null;
        
        try {
            conn = DBconnect.connect();
            pstmt = conn.prepareStatement(sql);  
            pstmt.setString(1, ID);
            rs = pstmt.executeQuery();
            if(rs.next())
            	result = rs.getString("member_Nick");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBconnect.close();
        }
        
        return result;
	}

	public boolean checkLogin(String ID, String PW) {	// 로그인 메소드
		Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        int exist = 0;									// 아이디 존재유무 카운트
        String sql = "SELECT member_PW from member where member_ID = ?";
        Boolean result = false;
        
        try {
            conn = DBconnect.connect();
            pstmt = conn.prepareStatement(sql);            
            pstmt.setString(1, ID);   
            rs = pstmt.executeQuery();
            
            while(rs.next()) {							// 다음 행이 존재하는 한 반복
	            if(PW.equals(rs.getString("member_PW")) == true) { // 입력받은 PW와 일치하는지 확인
	                result = true;
	            }
	            else {
	                exist++;	
	                JOptionPane.showMessageDialog(null, "비밀번호가 일치하지 않습니다.", "알림!", JOptionPane.WARNING_MESSAGE);
	                // 불일치시 경고메세지 출력
	            }
	        }            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBconnect.close();
        }
        
        if (result == false && exist == 0) {			// 아이디가 존재하지 않을 시
        	JOptionPane.showMessageDialog(null, "존재하지 않는 아이디입니다.", "알림!", JOptionPane.WARNING_MESSAGE);
        }
        
        return result;
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
