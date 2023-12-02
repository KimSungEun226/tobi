package springbook.user.domain;

public enum Level {
	BASIC(1), SILVER(2), GOLD(3);  // 세 개의 이늄 오브젝트 정의
	
	private final int value;
	
	Level(int value) { // DB에 저장할 값을 넣어줄 생성자를 만들어둔다.
		this.value = value;
	}
	
	public int intValue() { // 값을 가져오는 메소드
		return value;
	}
	
	public static Level valueOf(int value) { // 값으로부터 Level 타입 오브젝트를 가져오도록 만든 스태틱 메소드
		switch(value) {
		case 1: return BASIC;
		case 2: return SILVER;
		case 3: return GOLD;
		default: throw new AssertionError("Unknown value: " + value);
		}
	}
}
