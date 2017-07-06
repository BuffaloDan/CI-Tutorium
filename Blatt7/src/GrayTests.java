import java.util.Random;

public class GrayTests {

	public static int[] fromGrayToBin(int[] gray) {
		int[] ret = new int[gray.length];
		ret[ret.length - 1] = gray[gray.length - 1];
		for (int i = gray.length - 2; i >= 0; i--) {
			ret[i] = gray[i] ^ ret[i + 1];
		}
		return ret;
	}

	public static long fromGray(int[] gray) {
		long ret = 0;
		int last = gray[gray.length - 1];
		if (last == 1) {
			ret += Math.pow(2, gray.length-1);
		}
		for (int i = gray.length - 2; i >= 0; i--) {
			last = gray[i] ^ last;
			if (last == 1)
				ret += Math.pow(2, i);

		}
		return ret;
	}

	public static void main(String[] args) {
		int[] Z = new int[4];
		Random r = new Random();
		for (int z = 0; z < Z.length; z++) {
			Z[z] = r.nextInt(2);
			System.out.println(Z[z]);
		}
		System.out.println();
		int[] B = fromGrayToBin(Z);
		for (int i = 0; i < B.length; i++) {
			System.out.println(B[i]);
		}
		System.out.println();
		System.out.println(fromGray(Z));
	}

}
