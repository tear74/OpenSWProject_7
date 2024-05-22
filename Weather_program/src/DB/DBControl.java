package DB;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class DBControl {
	
	public void creationTable() {	// 프로그램 실행 초기 테이블 생성 메소드
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "CREATE TABLE WHAT_NAME (TABLE_no varchar(10), )"; // 추후 수정

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
}
