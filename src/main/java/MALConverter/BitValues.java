package MALConverter;

public enum BitValues {
	FETCH(4),
	READ(5),
	WRITE(6),
	MAR(7),
	MDR(8),
	PC(9),
	SP(10),
	LV(11),
	CPP(12),
	TOS(13),
	OPC(14),
	H(15),
	INC(16),
	INVA(17),
	ENB(18),
	ENA(19),
	F1(20),
	F0(21),
	SRA1(22),
	SLL8(23),
	JAMZ(24),
	JAMN(25),
	JMPC(26)
	;

	public final long value;

	private BitValues(int exponent){
		this.value = (long) Math.pow(2, exponent);
	}
}
