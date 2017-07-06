import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Main {

	private static boolean GRAY = true;

	public static void main(String[] args) {
		int[] Z = new int[32];
		ArrayList<Integer> shuffle = new ArrayList<>();
		long sum = 0;
		int runs = 1000000;

		Random r = new Random();
		for (int z = 0; z < 32; z++) {
			shuffle.add(z);
			Z[z] = r.nextInt(2);
		}

		if (!GRAY) {
			// B-Adisch
			for (int run = 0; run < runs; run++) {
				Collections.shuffle(shuffle);
				for (int i = 0; i < 4; i++) {
					int index = shuffle.get(i);
					if (Z[index] == 0) {
						Z[index] = 1;
						sum += Math.pow(2, index);
					} else if (Z[index] == 1) {
						Z[index] = 0;
						sum -= Math.pow(2, index);
					}
				}
				if (run % (runs / 10) == 0)
					System.out.println(run);
			}
			long mean = sum / runs;
			System.out.println("b-adisch: " + mean);
			// Nach mehreren durchläufen mit immer mehr runs fällt auf:
			// Durchschnittliche Änderung --> 0

		} else {
			// Gray Code
			for (int z = 0; z < 32; z++) {
				shuffle.add(z);
				Z[z] = r.nextInt(2);
			}
			long old = GrayTests.fromGray(Z);

			for (int run = 0; run < runs; run++) {
				Collections.shuffle(shuffle);
				for (int i = 0; i < 4; i++) {
					int index = shuffle.get(i);
					if (Z[index] == 0) {
						Z[index] = 1;
						sum += old-GrayTests.fromGray(Z);
					} else if (Z[index] == 1) {
						Z[index] = 0;
						sum -= old-GrayTests.fromGray(Z);
					}
				}
				if (run % (runs / 10) == 0)
					System.out.println(run);
			}
			long mean = sum / runs;
			System.out.println("gray: " + mean);
		}
	}

}
