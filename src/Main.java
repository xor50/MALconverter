import java.util.Arrays;

public class Main {

	public static void main(String[] args) {

//		String malCode = "PC = PC + 1; fetch; goto (MBR)";
		String malCode = "MDR = TOS = MDR + H; wr; goto Main1";
		System.out.println("input: " + malCode);
		String malCodeNoSpaces = malCode.replaceAll("\\s+","");
//		System.out.println(malCodeNoSpaces);

		long numberOfCBusTargets = malCodeNoSpaces.chars().mapToObj(c -> (char) c).filter(x -> x.equals('=')).count();

		String [] malParts = malCodeNoSpaces.split("=");
//		System.out.println(Arrays.toString(malParts));

		System.out.println(calcCBusValue(malParts, (int) (numberOfCBusTargets)));

//		int [] microinstruction = new int[36];
		long microinstructionDec = 0; // just start adding values for the bits as 2^n


		String binaryString = "1111";
		System.out.println(binToHex(binaryString));
	}

	public static String binToHex(String binaryString){
		return Integer.toHexString(Integer.parseInt(binaryString,2));
	}

	public static long calcCBusValue(String [] parts, int stopAt){
		long v = 0;
		for (int i = 0; i < stopAt; i++){
			switch (parts[i]) {
				case "H": v += Math.pow(2,15);
				break;
				case "OPC": v += Math.pow(2,14);
				break;
				case "TOS": v += Math.pow(2,13);
				break;
				case "CPP": v += Math.pow(2,12);
				break;
				case "LV": v += Math.pow(2,11);
				break;
				case "SP": v += Math.pow(2,10);
				break;
				case "PC": v += Math.pow(2,9);
				break;
				case "MDR": v += Math.pow(2,8);
				break;
				case "MAR": v += Math.pow(2,7);
				break;
				default: v += 0;
			}
		}
		return v;
	}
}
