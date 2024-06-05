package DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DBcrtW {												// DB 테이블 currentWeather에 관한 메소드
	public void insertCrtW(String nick, int type, String date) {	// 현재 날씨 아이콘 클릭 시 정보 추가 
		Connection conn = null;
	    PreparedStatement pstmt = null;
	    
	    String sql = "INSERT INTO currentWeather VALUES (0, ?, ?, ?)";
	    
	    try {
	        conn = DBconnect.connect();
	        pstmt = conn.prepareStatement(sql);	        

	        pstmt.setString(1, nick);
	        pstmt.setInt(2, type);
	        pstmt.setString(3, date);
	        
	        int r = pstmt.executeUpdate();
	        
	        System.out.println("return result = " + r);
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	        
	    } finally {
	        DBconnect.close();
	        
	    }
	}
	
	public int[] getCrtW(String date) {						// DB에서 아이콘 현황을 불러오는 메소드
		Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        

        int[] result = {0, 0, 0, 0, 0, 0};					// 아이콘은 몇가지?
        int i;        
        String sql = "SELECT crtW_type from currentWeather where crtW_Date = ?";
        
        try {
            conn = DBconnect.connect();
            pstmt = conn.prepareStatement(sql);  
            
            pstmt.setString(1, date);
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
            	i = rs.getInt("crtW_type");					// DB에 입력된 타입의 숫자를 체크
            	
	        	result[i - 1]++;							// 타입의 숫자에 맞는 인덱스를 +1
	        	
	            System.out.println(result);
	        }
            
        } catch (Exception e) {
            e.printStackTrace();
            
        } finally {
            DBconnect.close();
            
        }
        
        return result;
	}
	
	public boolean deleteCrtW(String date, String nick) { 	// 아이콘 취소 메소드
		Connection conn = null;
        PreparedStatement pstmt = null;
        Boolean result = false;

        String del = "DELETE FROM currentWeather WHERE member_Nick = ? AND crtW_Date = ?";	// 닉네임과 날짜를 통해 레코드를 찾음

        
        try {
            conn = DBconnect.connect();
            pstmt = conn.prepareStatement(del);            
            pstmt.setString(1, nick); 
            pstmt.setString(2, date);
            
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
