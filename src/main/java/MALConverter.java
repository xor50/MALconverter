import java.util.Scanner;

public class MALConverter {

	public static void main(String[] args) {

//		String testMalCode = "PC = PC + 1; fetch; goto (MBR)"; //todo
//		String testMalCode = "MDR = TOS = MDR + H; wr; goto Main1";
//		String testMalCode = "PC = PC + 1; goto 00d";
//		String testMalCode = "H = PC; goto 00e";
//		String testMalCode = "OPC = TOS = PC + H; goto 00c";
//		String testMalCode = "H = MBRU << 8; goto Main1";
//		System.out.println("test input: " + testMalCode);
//		doMainCalc(testMalCode);

		doMainLoop();
	}

	public static void doMainLoop() {
		Scanner scanner = new Scanner(System.in);
		System.out.println("please enter your MAL line; leave with 'exit':");
		while (scanner.hasNext()) {
			String userInput = scanner.nextLine();
			if (userInput.equals("exit"))
				System.exit(0);
			String malFromUser = userInput;
			printResults(doMainCalc(malFromUser));
			System.out.println("enter MAL line or 'exit':");
		}
		scanner.close();
	}

	public static long doMainCalc(String malFromUser) {
		String malCodeNoSpaces = malFromUser.replaceAll("\\s+", "");
		int numberOfCBusTargets = (int) malCodeNoSpaces.chars().mapToObj(c -> (char) c).filter(x -> x.equals('=')).count();
//		int numberOfSemicolons = (int) malCodeNoSpaces.chars().mapToObj(c -> (char) c).filter(x -> x.equals(';')).count();

		long microinstructionInDecimal = 0; // just start adding values for the bits as 2^n

		String[] malPartsSplitAtEqualSigns = malCodeNoSpaces.split("=");
		microinstructionInDecimal += calcCBusValue(malPartsSplitAtEqualSigns, numberOfCBusTargets);

		String[] malPartsSplitAtSemicolons = malPartsSplitAtEqualSigns[malPartsSplitAtEqualSigns.length - 1].split(";");
		String malPartNextAddress = malPartsSplitAtSemicolons[malPartsSplitAtSemicolons.length - 1].substring(4); //take last array entry; remove goto (via substring)
		microinstructionInDecimal += calcNextAddress(malPartNextAddress);

		if (malPartsSplitAtSemicolons.length == 3) {
			microinstructionInDecimal += calcMemActions(malPartsSplitAtSemicolons[malPartsSplitAtSemicolons.length - 2]);
		}

		microinstructionInDecimal += calcALUValue(malPartsSplitAtSemicolons[0]);

		microinstructionInDecimal += calcBBusValue(malPartsSplitAtSemicolons[0]);

		return microinstructionInDecimal;
	}

	public static String binToHex(String binaryString) {
		return Long.toHexString(Long.parseLong(binaryString, 2));
	}

	public static Long binToDec(String binaryString) {
		return Long.parseLong(binaryString, 2);
	}

	public static String hexToBin(String hexString) {
		return Integer.toBinaryString(Integer.parseInt(hexString, 16));
	}

	public static String decToBin(Long decLong) {
		return Long.toBinaryString(decLong);
	}

	public static void printResults(long result) {
		String binaryString = decToBin(result);
		System.out.println("in bin: " + binaryString);
		System.out.println("in hex: " + binToHex(binaryString));
	}

	//from B bus only one value possible -> direct return
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

	//directly match ALU input -> direct return
	public static long calcALUValue(String s) {

		final long INC = (long) Math.pow(2, 16);
		final long INVA = (long) Math.pow(2, 17);
		final long ENB = (long) Math.pow(2, 18);
		final long ENA = (long) Math.pow(2, 19);
		final long F1 = (long) Math.pow(2, 20);
		final long F0 = (long) Math.pow(2, 21);
		final long SRA1 = (long) Math.pow(2, 22);
		final long SLL8 = (long) Math.pow(2, 23);

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

		//left shift
		if (s.matches("(MDR|PC|MBR|MBRU|SP|LV|CPP|TOS|OPC|H)<<8"))
			return SLL8;
		//right shift
		if (s.matches("(MDR|PC|MBR|MBRU|SP|LV|CPP|TOS|OPC|H)>>1"))
			return SRA1;

		System.err.println("error in calculation ALU value");
		return 0;
	}

//	public static long calcJumpBits(String s) {
//
//		long returnValue = 0;
//
//		if (s.equalsIgnoreCase("MBR"))
//			returnValue = (long) Math.pow(2, 26);
//		//todo:
//		else returnValue = 0;
//		return 0;
//	}

	public static long calcNextAddress(String addressPart) {

		String hexAddress;

		//todo:
//		if (addressPart.equalsIgnoreCase("(MBR)"))
//			calcJumpBits("MBR");
		//input "Main1" is special case
		if (addressPart.equalsIgnoreCase("main1"))
			hexAddress = "100"; //Main1 is at 0x100
		else hexAddress = addressPart;

		String binaryString = hexToBin(hexAddress).concat("000000000000000000000000000"); //NEXT_ADDRESS is the beginning of the microinstruction
		return Long.parseLong(binaryString, 2);
	}

	//todo: fetch is possible at the same time as rd, wr (?)
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

	//multiple C bus outputs possible, loop and add, then return
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
