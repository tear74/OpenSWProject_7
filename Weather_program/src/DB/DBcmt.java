package DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;

public class DBcmt {												// DB 테이블 comment 에 관한 메소드
	
	public void insertCmt(String nick, String chat) {	// 코멘트 입력
		Connection conn = null;
	    PreparedStatement pstmt = null;
	    
	    String sql = "INSERT INTO comment (`cmt_NO`, `member_Nick`, `cmt_Content`, `cmt_Date`) VALUES (0, ?, ?, ?)";
	    
	    try {
	        conn = DBconnect.connect();
	        pstmt = conn.prepareStatement(sql);	        

	        pstmt.setString(1, nick);
	        pstmt.setString(2, chat);
	        pstmt.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
	        
	        int r = pstmt.executeUpdate();
	        
	        System.out.println("return result = " + r);
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	        
	    } finally {
	        DBconnect.close();
	        
	    }
	}

	public ArrayList<String> getCmt(String date) {				// DB에서 채팅을 불러옴
		Connection conn = null;
	    PreparedStatement pstmt = null;
	    ResultSet rs;
	    
	    java.sql.Date d = java.sql.Date.valueOf(date);		// 전달받은 date의 자료형을 변환
	    
	    String nick, script;
	    
	    String sql = "SELECT * from comment where cmt_Date = ?";// 특정 날짜의 코멘트들을 불러오는 sql문
	    
	    ArrayList<String> result = new ArrayList<>();
	    
	    try {
	        conn = DBconnect.connect();
	        pstmt = conn.prepareStatement(sql);
	        
	        pstmt.setDate(1, d);
	        
	        rs = pstmt.executeQuery();   
	        
	        while (rs.next()) {
	        	nick = rs.getString("member_Nick");
	        	script = rs.getString("cmt_Content");   
	        	
	            result.add(nick + ": " + script);
	            System.out.println(result);
	        }
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	        
	    } finally {
	        DBconnect.close();
	        
	    }
	    return result;
	}
	
	public boolean deleteCmt(int num) { 						// 코멘트 삭제메소드
		Connection conn = null;
        PreparedStatement pstmt = null;
        Boolean result = false;

        String del = "DELETE FROM comment WHERE cmt_NO = ?";	// 기본키를 통해 레코드를 찾음

        
        try {
            conn = DBconnect.connect();
            pstmt = conn.prepareStatement(del);            
            pstmt.setInt(1, num);;
            
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
