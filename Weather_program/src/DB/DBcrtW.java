package DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class DBcrtW {												// DB 테이블 currentWeather에 관한 메소드
	public void insertCrtW(String nick, int type, String univ) {	// 현재 날씨 아이콘 클릭 시 정보 추가 
		Connection conn = null;
	    PreparedStatement pstmt = null;
	    
	    
	    String sql = "INSERT INTO currentWeather VALUES (0, ?, ?, ?, ?)";
	    
	    try {
	        conn = DBconnect.connect();
	        pstmt = conn.prepareStatement(sql);	        

	        pstmt.setString(1, nick);
	        pstmt.setInt(2, type);
	        pstmt.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
	        pstmt.setString(4, univ);
	        
	        int r = pstmt.executeUpdate();
	        
	        System.out.println("return result = " + r);
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	        
	    } finally {
	        DBconnect.close();
	        
	    }
	}
	
	public int[] getCrtW(String date, String univ) {						// DB에서 아이콘 현황을 불러오는 메소드, date는 "YYYY-MM-DD"형식일것
		Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        java.sql.Date d = java.sql.Date.valueOf(date);		// 전달받은 date의 자료형을 변환

        int[] result = {0, 0, 0, 0, 0, 0};					// 아이콘은 몇가지?
        int i;        
        String sql = "SELECT crtW_type from currentWeather where crtW_Date = ? and member_Univ = ?";
        
        try {
            conn = DBconnect.connect();
            pstmt = conn.prepareStatement(sql);  
            
            pstmt.setDate(1, d);
            pstmt.setString(2, univ);
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
            	i = rs.getInt("crtW_type");					// DB에 입력된 타입의 숫자를 체크
            	
	        	result[i - 1]++;							// 타입의 숫자에 맞는 인덱스를 +1
	        	
	            System.out.println(i + "번의 기호는 현재" + result[i - 1] + "회!"); 			// 콘솔 확인용
	        }
            
        } catch (Exception e) {
            e.printStackTrace();
            
        } finally {
            DBconnect.close();
            
        }
        
        return result;
	}
	
	public boolean deleteCrtW(String date, String nick, String univ) { 	// 아이콘 취소 메소드
		Connection conn = null;
        PreparedStatement pstmt = null;
        Boolean result = false;

        java.sql.Date d = java.sql.Date.valueOf(date);		// 전달받은 date의 자료형을 변환        java.sql.Date d = java.sql.Date.valueOf(date);
        
        String del = "DELETE FROM currentWeather WHERE member_Nick = ? AND crtW_Date = ? AND member_univ = ?";	// 닉네임과 날짜를 통해 레코드를 찾음

        
        try {
            conn = DBconnect.connect();
            pstmt = conn.prepareStatement(del);            
            pstmt.setString(1, nick); 
            pstmt.setDate(2, d);
            pstmt.setString(3, univ);
            
			int r = pstmt.executeUpdate();
			
			if (r > 0) {
				System.out.println("crtW Delete Successed");			// 콘솔 확인용
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
