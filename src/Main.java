public class Main {

	public static void main(String[] args) {
		System.out.println(binToHex("1111"));
	}

	public static String binToHex(String binaryString){
		return Integer.toHexString(Integer.parseInt(binaryString,2));
	}
}
