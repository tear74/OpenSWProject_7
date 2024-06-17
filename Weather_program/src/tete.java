import DB.*;

public class tete {			

	public static void main(String[] args) {
		DB DB = new DB();
		DBcmt DBcmt = new DBcmt();
		DBmem mem = new DBmem();
		DBcrtW crtW = new DBcrtW();
		int cnt = 1;
		
		String readme =
		"※※※※※※※※※※※※※※※ 이 클래스는 DB관련 메소드를 실행하기 위한 실험횽 클래스 입니다.※※※※※※※※※※※※※※※";
		System.out.println(readme);
		
		
		DB.creationTable();
		cnt++;
		System.out.println(cnt);
		System.out.println();
		
		mem.register("1234", "1234", "test", "tete@tete.tete", "ㅅㅁㄷ");
		cnt++;
		System.out.println(cnt);
		System.out.println();
		
		String a = mem.getNick("1234");
		System.out.println(a + "ㄷㄷㄷ");
		cnt++;
		System.out.println(cnt);
		System.out.println();
		
		mem.checkLogin(a, a);			//존재하지 않았기에 나오지도 않았다
		cnt++;
		System.out.println(cnt);
		System.out.println();
		
		mem.checkLogin("1234", "1234");
		cnt++;
		System.out.println(cnt);
		System.out.println();
		
		DBcmt.insertCmt("test", "이거 진짜 되는거에요?", "ㅅㅁㄷ");
		cnt++;
		System.out.println(cnt);
		System.out.println();
		
		DBcmt.getCmt("2024-06-17", "ㅅㅁㄷ");
		cnt++;
		System.out.println(cnt);
		System.out.println();
//		
//		DBcmt.deleteCmt(3);
		//DBcmt.deleteCmt(1);
		
		crtW.insertCrtW("test", 3, "ㅅㅁㄷ");
		cnt++;
		System.out.println(cnt);
		System.out.println();
		
		int[] fa = crtW.getCrtW("2024-06-17", "ㅅㅁㄷ");
		for(int i = 0; i < fa.length; i++){
			System.out.println(fa[i]);
		}
		cnt++;
		System.out.println(cnt);
		System.out.println();
		
//		crtW.deleteCrtW("2024-06-17", "test", "ㅅㅁㄷ");
//		cnt++;
//		System.out.println(cnt);
//		System.out.println();
//		
//
//		mem.deleteMember("1234");
//		cnt++;
//		System.out.println(cnt);
//		System.out.println();
		
	}

}
