package DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class DBcmt {							// DB 테이블 comment에 관한 메소드
	
	public void insertCmt(String chat, int roomID, String user) {	// 코멘트 입력
		Connection conn = null;
	    PreparedStatement pstmt = null;
	    PreparedStatement get = null;
	    ResultSet rs = null;
	    
	    int cnt = 1;
	    String sql = "INSERT INTO comment (`cmt_NO`, `cmt_Nick`, `cmt`, `logNum`) VALUES (?, ?, ?, ?)";
	    
	    try {
	        conn = DBconnect.connect();
	        pstmt = conn.prepareStatement(sql);
	        
	        get = conn.prepareStatement("SELECT logNum FROM chatLog where roomID = ?");
	        get.setInt(1, roomID);
	        rs = get.executeQuery();
	        while(rs.next()) {
	            if(cnt == rs.getInt("logNum")) {
	            	cnt++;
	                System.out.println(cnt);
	        	}
	        }
	
	        pstmt.setString(1, chat);
	        pstmt.setString(2, user);
	        pstmt.setInt(3, roomID);
	        pstmt.setInt(4, cnt);
	        int r = pstmt.executeUpdate();
	        System.out.println("return result = " + r);
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        DBconnect.close();
	    }
	}

	public ArrayList<String> getCmt(String date) {		// DB에서 채팅을 불러옴
		Connection conn = null;
	    PreparedStatement pstmt = null;
        PreparedStatement nstmt = null;
	    ResultSet rs, rn;
	    
	    String nick, script, ID;
	    int i = 0;
	    
	    String sql = "SELECT * from comment where cmt_Date = ?";				// 특정 날짜의 코멘트들을 불러오는 sql문
	    String find = "SELECT member_Nick from member where member_ID = ?";		// 코멘트를 등록한 ID의 닉네임을 불러오는 sql문
	    
	    ArrayList<String> result = new ArrayList<>();
	    
	    try {
	        conn = DBconnect.connect();
	        pstmt = conn.prepareStatement(sql);
	        nstmt = conn.prepareStatement(find);
	        
	        pstmt.setString(1, date);
	        
	        rs = pstmt.executeQuery();   
	        
	        while (rs.next()) {
	        	ID = rs.getString("cmt_ID");
	        	
	        	nstmt.setString(1, ID);

		        rn = nstmt.executeQuery();
		        
	        	if(rn.next())
	        		nick = rn.getString("member_Nick");
	        	else 
	        		nick = null;
	        	
	        	script = rs.getString("cmt_Content");   
	        	
	            result.add(nick + ": " + script);
	            System.out.println(result);
	            i++;
	        }
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	        
	    } finally {
	        DBconnect.close();
	        
	    }
	    return result;
}

}
