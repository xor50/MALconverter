import java.util.Arrays;
import java.util.Locale;

public class Main {

	public static void main(String[] args) {

//		String malCode = "PC = PC + 1; fetch; goto (MBR)";
		String malCode = "MDR = TOS = MDR + H; wr; goto Main1";
//		String malCode =  "PC = PC + 1; goto 00d";
//		String malCode =  "H = PC; goto 00e";
//		String malCode =  "OPC = TOS = PC + H; goto 00c";
		System.out.println("input: " + malCode);

		long microinstructionDec = 0; // just start adding values for the bits as 2^n

		String malCodeNoSpaces = malCode.replaceAll("\\s+", "");

		int numberOfCBusTargets = (int) malCodeNoSpaces.chars().mapToObj(c -> (char) c).filter(x -> x.equals('=')).count();

		String[] malPartsSplitEquals = malCodeNoSpaces.split("=");
//		System.out.println(Arrays.toString(malPartsSplitEquals));
		String[] malPartsSplitSemicolon = malPartsSplitEquals[malPartsSplitEquals.length - 1].split(";");
//		System.out.println(Arrays.toString(malPartsSplitSemicolon));

		microinstructionDec += calcCBusValue(malPartsSplitEquals, numberOfCBusTargets);

		String malPartNextAddress = malPartsSplitSemicolon[malPartsSplitSemicolon.length - 1].substring(4); //take last array entry; remove goto (via substring)
		microinstructionDec += calcNextAddress(malPartNextAddress);

		if (malPartsSplitSemicolon.length == 3) {
			microinstructionDec += calcMemActions(malPartsSplitSemicolon[malPartsSplitSemicolon.length - 2]);
		}

		microinstructionDec += calcALUValue(malPartsSplitSemicolon[0]);

		microinstructionDec += calcBBusValue(malPartsSplitSemicolon[0]);

//		int [] microinstruction = new int[36];


//		System.out.println(hexToBin("100"));

//		String binaryString = "1111";
//		System.out.println(binToHex(binaryString));

		printResults(microinstructionDec);
	}

	public static String binToHex(String binaryString) {
		return Long.toHexString(Long.parseLong(binaryString, 2));
	}

	public static String hexToBin(String hexString) {
		return Integer.toBinaryString(Integer.parseInt(hexString, 16));
	}

	public static String decToBin(Long decLong) {
		return Long.toBinaryString(decLong);
	}

	public static void printResults(long result){
		String binaryString = decToBin(result);
		System.out.println("in bin: " + binaryString);
		System.out.println("in hex: " + binToHex(binaryString));
	}

	//only one B bus possible: direct return
	public static long calcBBusValue(String s) {
		if (s.contains("MDR"))
			return 0;
		if (s.contains("PC"))
			return 1;
		if (s.contains("MBR"))
			return 2;
		if (s.contains("MBRU"))
			return 3;
		if (s.contains("SP"))
			return 4;
		if (s.contains("LV"))
			return 5;
		if (s.contains("CPP"))
			return 6;
		if (s.contains("TOS"))
			return 7;
		if (s.contains("OPC"))
			return 8;
		return 0;
	}

	public static long calcALUValue(String s) {

		final long INC = (long) Math.pow(2, 16);
		final long INVA = (long) Math.pow(2, 17);
		final long ENB = (long) Math.pow(2, 18);
		final long ENA = (long) Math.pow(2, 19);
		final long F1 = (long) Math.pow(2, 20);
		final long F0 = (long) Math.pow(2, 21);

		//A
		if (s.matches("H"))
			return F1 + ENA;
		//B
		if (s.matches("MDR|PC|MBR|MBRU|SP|LV|CPP|TOS|OPC"))
			return F1 + ENB;
		//B + A
		if (s.matches("(MDR|PC|MBR|MBRU|SP|LV|CPP|TOS|OPC)\\+H"))
			return F0 + F1 + ENA + ENB;
		//B + A + 1
		if (s.matches("(MDR|PC|MBR|MBRU|SP|LV|CPP|TOS|OPC)\\+H\\+1"))
			return F0 + F1 + ENA + ENB + INC;
		//A + 1
		if (s.matches("H\\+1"))
			return F0 + F1 + ENA + INC;
		//B + 1
		if (s.matches("(MDR|PC|MBR|MBRU|SP|LV|CPP|TOS|OPC)\\+1"))
			return F0 + F1 + ENB + INC;
		//B - 1
		if (s.matches("(MDR|PC|MBR|MBRU|SP|LV|CPP|TOS|OPC)(-|−)1"))
			return F0 + F1 + ENB + INVA;
		//B - A
		if (s.matches("(MDR|PC|MBR|MBRU|SP|LV|CPP|TOS|OPC)(-|−)H"))
			return F0 + F1 + ENA + ENB + INVA + INC;
		//-A
		if (s.matches("(-|−)H"))
			return F0 + F1 + ENA + INVA + INC;
		//B AND A
		if (s.matches("(MDR|PC|MBR|MBRU|SP|LV|CPP|TOS|OPC)ANDH"))
			return ENA + ENB;
		//B OR A
		if (s.matches("(MDR|PC|MBR|MBRU|SP|LV|CPP|TOS|OPC)ORH"))
			return F1 + ENA + ENB;
		//0
		if (s.matches("0"))
			return F1;
		//1
		if (s.matches("1"))
			return F0 + F1 + INC;
		//-1
		if (s.matches("(-|−)1"))
			return F0 + F1 + INVA;

		System.out.println("error in calculation ALU value");
		return 0;
	}

	public static long calcNextAddress(String addressPart) {
		String hexString;

		if (addressPart.equalsIgnoreCase("main1"))
			hexString = "100"; //Main1 is at 0x100
		else hexString = addressPart;

		String binaryString = hexToBin(hexString).concat("000000000000000000000000000");
		return Long.parseLong(binaryString, 2);
	}

	public static long calcMemActions(String memPart) {
		long returnValue = 0;
		switch (memPart) {
			case "fetch":
				returnValue += Math.pow(2, 4);
				break;
			case "rd":
			case "read":
				returnValue += Math.pow(2, 5);
				break;
			case "wr":
			case "write":
				returnValue += Math.pow(2, 6);
				break;
			default:
				returnValue += 0;
		}
		return returnValue;
	}

	public static long calcCBusValue(String[] parts, int stopAt) {
		long returnValue = 0;
		for (int i = 0; i < stopAt; i++) {
			switch (parts[i]) {
				case "H":
					returnValue += Math.pow(2, 15);
					break;
				case "OPC":
					returnValue += Math.pow(2, 14);
					break;
				case "TOS":
					returnValue += Math.pow(2, 13);
					break;
				case "CPP":
					returnValue += Math.pow(2, 12);
					break;
				case "LV":
					returnValue += Math.pow(2, 11);
					break;
				case "SP":
					returnValue += Math.pow(2, 10);
					break;
				case "PC":
					returnValue += Math.pow(2, 9);
					break;
				case "MDR":
					returnValue += Math.pow(2, 8);
					break;
				case "MAR":
					returnValue += Math.pow(2, 7);
					break;
				default:
					returnValue += 0;
			}
		}
		return returnValue;
	}
}
