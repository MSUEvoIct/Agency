package abce.agency.finance;

public class FinanceException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public FinanceException(String reason) {
		super(reason);
	}
}
