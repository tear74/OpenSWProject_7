import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SchoolSelection {

    // 학교 목록을 저장하는 리스트
    private static List<String> schoolList = new ArrayList<>();

    public static void main(String[] args) {
        // 학교 목록 초기화
        initializeSchoolList();

        // 학교 선택 기능 실행
        String selectedSchool = selectSchool();

        // 선택된 학교 출력
        if (selectedSchool != null) {
            System.out.println("선택된 학교: " + selectedSchool);
        } else {
            System.out.println("유효한 학교를 선택하지 않았습니다.");
        }
    }

    // 학교 목록을 초기화하는 메서드
    private static void initializeSchoolList() {
        schoolList.add("선문대학교");
        schoolList.add("충남대학교");
        schoolList.add("유원대학교");
        schoolList.add("순천향대학교");
        schoolList.add("충북대학교");
        // 추가적인 학교를 여기서 추가할 수 있습니다.
    }

    // 학교를 선택하는 메서드
    private static String selectSchool() {
        Scanner scanner = new Scanner(System.in);
        
        // 학교 목록 출력
        System.out.println("학교 목록:");
        for (int i = 0; i < schoolList.size(); i++) {
            System.out.println((i + 1) + ". " + schoolList.get(i));
        }

        // 사용자에게 학교 선택을 요청
        System.out.print("학교 번호를 선택하세요: ");
        int choice = scanner.nextInt();

        // 선택된 번호가 유효한지 확인
        if (choice > 0 && choice <= schoolList.size()) {
            return schoolList.get(choice - 1);
        } else {
            System.out.println("유효하지 않은 선택입니다.");
            return null;
        }
    }
}
