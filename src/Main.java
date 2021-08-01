import java.util.Arrays;
import java.util.Locale;

public class Main {

	public static void main(String[] args) {

//		String malCode = "PC = PC + 1; fetch; goto (MBR)";
		String malCode = "MDR = TOS = MDR + H; wr; goto Main1";
		System.out.println("input: " + malCode);

		String malCodeNoSpaces = malCode.replaceAll("\\s+","");
//		System.out.println(malCodeNoSpaces);

		long numberOfCBusTargets = malCodeNoSpaces.chars().mapToObj(c -> (char) c).filter(x -> x.equals('=')).count();

		String [] malPartsSplitEquals = malCodeNoSpaces.split("=");
//		System.out.println(Arrays.toString(malPartsSplitEquals));
		String [] malPartsSplitSemicolon = malPartsSplitEquals[malPartsSplitEquals.length-1].split(";");
//		System.out.println(Arrays.toString(malPartsSplitSemicolon));

		System.out.println(calcCBusValue(malPartsSplitEquals, (int) (numberOfCBusTargets)));

//		int [] microinstruction = new int[36];
		long microinstructionDec = 0; // just start adding values for the bits as 2^n

		String malPartNextAddress = malPartsSplitSemicolon[malPartsSplitSemicolon.length-1].substring(4); //take last array entry; remove goto (via substring)
		System.out.println(calcNextAddress(malPartNextAddress));


//		System.out.println(hexToBin("100"));

//		String binaryString = "1111";
//		System.out.println(binToHex(binaryString));
	}

	public static String binToHex(String binaryString){
		return Integer.toHexString(Integer.parseInt(binaryString,2));
	}

	public static String hexToBin(String hexString){
		return Integer.toBinaryString(Integer.parseInt(hexString,16));
	}

	public static long calcNextAddress(String addressPart) {
		String hexString;

		if (addressPart.equalsIgnoreCase("main1"))
			hexString = "100"; //Main1 is at 0x100
		else hexString = addressPart;

		String binaryString = hexToBin(hexString).concat("000000000000000000000000000");
		return Long.parseLong(binaryString,2);
	}

	public static long calcMemActions(String memPart) {
		return 0;
	}

	public static long calcCBusValue(String [] parts, int stopAt){
		long returnValue = 0;
		for (int i = 0; i < stopAt; i++){
			switch (parts[i]) {
				case "H": returnValue += Math.pow(2,15);
				break;
				case "OPC": returnValue += Math.pow(2,14);
				break;
				case "TOS": returnValue += Math.pow(2,13);
				break;
				case "CPP": returnValue += Math.pow(2,12);
				break;
				case "LV": returnValue += Math.pow(2,11);
				break;
				case "SP": returnValue += Math.pow(2,10);
				break;
				case "PC": returnValue += Math.pow(2,9);
				break;
				case "MDR": returnValue += Math.pow(2,8);
				break;
				case "MAR": returnValue += Math.pow(2,7);
				break;
				default: returnValue += 0;
			}
		}
		return returnValue;
	}
}
