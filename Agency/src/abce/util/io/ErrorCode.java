package abce.util.io;

public enum ErrorCode {
	Unknown(1), FileIO(2);

	private final Integer	code;

	ErrorCode(int val) {
		code = val;
	}

	public Integer code() {
		return code;
	}
}