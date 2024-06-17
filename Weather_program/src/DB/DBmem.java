package DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.JOptionPane;

public class DBmem {									// DB 테이블 member 에 관한 메소드
	
	public boolean register(String ID, String PW, String nick, String email) { // 회원가입 메소드
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "INSERT INTO member VALUES (?, ?, ?, ?)";
		boolean result = false;

		try {
			conn = DBconnect.connect();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, ID);
			pstmt.setString(2, PW);
			pstmt.setString(3, nick);
			pstmt.setString(4, email);

			int r = pstmt.executeUpdate(); 				// 생성 성공시(만들어진 row 들) 값을 받음(없을시 0)
			System.out.println("return result = " + r); // 콘솔 체크용
			
			if (r > 0) { 								// 생성된 row 가 있을 때
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
        
        String sql = "SELECT member_Nick from member where member_ID = ?";
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
            
            while(rs.next()) {										// 다음 행이 존재하는 한 반복
	            if(PW.equals(rs.getString("member_PW")) == true) { 	// 입력받은 PW와 일치하는지 확인
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
        
        if (result == false && exist == 0) {					// 아이디가 존재하지 않을 시
        	JOptionPane.showMessageDialog(null, "존재하지 않는 아이디입니다.", "알림!", JOptionPane.WARNING_MESSAGE);
        }
        
        return result;
	}
	
	public boolean deleteMember(String ID) { 					// 회원 탈퇴 메소드
		Connection conn = null;
        PreparedStatement pstmt = null;
        Boolean result = false;

        String del = "DELETE FROM member WHERE member_ID = ?";	// 기본키인 아이디를 통해 레코드를 찾음

        
        try {
            conn = DBconnect.connect();
            pstmt = conn.prepareStatement(del);            
            pstmt.setString(1, ID); 
            
			int r = pstmt.executeUpdate();
			
			if (r > 0) {
				System.out.println("Delete Successed");
				result = true;
			}

        } catch(Exception e) {
        	e.printStackTrace();
        	
        } finally {
        	DBconnect.close();
        	
        }

	return result;
	}
	
}
