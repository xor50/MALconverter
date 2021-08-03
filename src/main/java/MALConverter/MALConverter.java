package MALConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static MALConverter.BitValues.*;

public class MALConverter {

	public static void main(String[] args) {

//		String testMalCode = "PC = PC + 1; fetch; goto (MBR)"; //todo
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
			if (userInput.contains(";") && userInput.contains("goto") && userInput.contains("=")) {
				printResults(doMainCalc(userInput));
				System.out.println("enter MAL line or 'exit':");
			} else {
				System.err.println("Please enter legal MAL line...");
				System.exit(-69);
			}
		}
		scanner.close();
	}

	public static long doMainCalc(String malFromUser) {

		String malCodeNoSpaces = malFromUser.replaceAll("\\s+", "");

		long microinstructionInDecimal = 0; // just start adding values for the bits as 2^n
		microinstructionInDecimal += calcCBusValue(findCparts(malCodeNoSpaces));
		microinstructionInDecimal += calcALUValue(findBALUPart(malCodeNoSpaces));
		microinstructionInDecimal += calcBBusValue(findBALUPart(malCodeNoSpaces));
		if (!(findMemParts(malCodeNoSpaces) == null)) {
			microinstructionInDecimal += calcMemActions(findMemParts(malCodeNoSpaces)[0]);
		}
		microinstructionInDecimal += calcNextAddress(findAddressPart(malCodeNoSpaces));

		return microinstructionInDecimal;
	}

	public static void printResults(long result) {
		String binaryString = decToBin(result);
		System.out.println("in bin: " + binaryString);
		System.out.println("in hex: " + binToHex(binaryString));
	}

	public static String[] findCparts(String s) {
		List<String> list = new ArrayList<>();
		while (s.contains("=")) {
			list.add(s.substring(0, s.indexOf("=")));
			s = s.substring(s.indexOf("=") + 1);
		}
		list.add(s);
		list.remove(list.size() - 1); //remove last entry, which is part after last '=' -> not C part
		return list.toArray(new String[0]);
	}

	public static String findBALUPart(String s) {
		while (s.contains("=")) {
			s = s.substring(s.indexOf("=") + 1);
		}
		return s.substring(0, s.indexOf(";"));
	}

	public static String[] findMemParts(String s) {
		List<String> list = new ArrayList<>();
		while (s.contains("=")) {
			s = s.substring(s.indexOf("=") + 1);
		}
		while (s.contains(";")) {
			list.add(s.substring(0, s.indexOf(";")));
			s = s.substring(s.indexOf(";") + 1);
		}
		list.add(s);
		list.remove(0); //remove first entry, which is part before first semicolon -> BALU
		list.remove(list.size() - 1); //remove last entry, which is part after last semicolon -> address part
		if (list.isEmpty())
			return null;
		return list.toArray(new String[0]);
	}

	public static String findAddressPart(String s) {
		while (s.contains("=")) {
			s = s.substring(s.indexOf("=") + 1);
		}
		while (s.contains(";")) {
			s = s.substring(s.indexOf(";") + 1);
		}
		return s.substring(4);
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

		//A
		if (s.matches("H"))
			return F1.value + ENA.value;
		//B
		if (s.matches("MDR|PC|MBR|MBRU|SP|LV|CPP|TOS|OPC"))
			return F1.value + ENB.value;
		//B + A
		if (s.matches("(MDR|PC|MBR|MBRU|SP|LV|CPP|TOS|OPC)\\+H"))
			return F0.value + F1.value + ENA.value + ENB.value;
		//B + A + 1
		if (s.matches("(MDR|PC|MBR|MBRU|SP|LV|CPP|TOS|OPC)\\+H\\+1"))
			return F0.value + F1.value + ENA.value + ENB.value + INC.value;
		//A + 1
		if (s.matches("H\\+1"))
			return F0.value + F1.value + ENA.value + INC.value;
		//B + 1
		if (s.matches("(MDR|PC|MBR|MBRU|SP|LV|CPP|TOS|OPC)\\+1"))
			return F0.value + F1.value + ENB.value + INC.value;
		//B - 1
		if (s.matches("(MDR|PC|MBR|MBRU|SP|LV|CPP|TOS|OPC)(-|−)1"))
			return F0.value + F1.value + ENB.value + INVA.value;
		//B - A
		if (s.matches("(MDR|PC|MBR|MBRU|SP|LV|CPP|TOS|OPC)(-|−)H"))
			return F0.value + F1.value + ENA.value + ENB.value + INVA.value + INC.value;
		//-A
		if (s.matches("(-|−)H"))
			return F0.value + F1.value + ENA.value + INVA.value + INC.value;
		//B AND A
		if (s.matches("(MDR|PC|MBR|MBRU|SP|LV|CPP|TOS|OPC)ANDH"))
			return ENA.value + ENB.value;
		//B OR A
		if (s.matches("(MDR|PC|MBR|MBRU|SP|LV|CPP|TOS|OPC)ORH"))
			return F1.value + ENA.value + ENB.value;
		//0
		if (s.matches("0"))
			return F1.value;
		//1
		if (s.matches("1"))
			return F0.value + F1.value + INC.value;
		//-1
		if (s.matches("(-|−)1"))
			return F0.value + F1.value + INVA.value;

		//left shift
		if (s.matches("(MDR|PC|MBR|MBRU|SP|LV|CPP|TOS|OPC|H)<<8"))
			return SLL8.value;
		//right shift
		if (s.matches("(MDR|PC|MBR|MBRU|SP|LV|CPP|TOS|OPC|H)>>1"))
			return SRA1.value;

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

		String binaryString = hexToBin(hexAddress).concat("000000000000000000000000000"); //NEXT_ADDRESS is at the beginning of the microinstruction
		return Long.parseLong(binaryString, 2);
	}

	//todo: fetch is possible at the same time as rd, wr (?); probably do like CBus calc
	public static long calcMemActions(String memPart) {
		long returnValue = 0;
		switch (memPart) {
			case "fetch":
				returnValue += FETCH.value;
				break;
			case "rd":
			case "read":
				returnValue += READ.value;
				break;
			case "wr":
			case "write":
				returnValue += WRITE.value;
				break;
			default:
				returnValue += 0;
		}
		return returnValue;
	}

	//multiple C bus outputs possible, loop and add, then return
	public static long calcCBusValue(String[] parts) {
		long returnValue = 0;
		for (String part : parts) {
			switch (part) {
				case "H":
					returnValue += H.value;
					break;
				case "OPC":
					returnValue += OPC.value;
					break;
				case "TOS":
					returnValue += TOS.value;
					break;
				case "CPP":
					returnValue += CPP.value;
					break;
				case "LV":
					returnValue += LV.value;
					break;
				case "SP":
					returnValue += SP.value;
					break;
				case "PC":
					returnValue += PC.value;
					break;
				case "MDR":
					returnValue += MDR.value;
					break;
				case "MAR":
					returnValue += MAR.value;
					break;
				default:
					returnValue += 0;
			}
		}
		return returnValue;
	}
}
