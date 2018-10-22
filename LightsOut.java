package src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Optional;

/**
 * 一行目に対して二行目のON/OFFを切り替える
 * 0:ON
 * 1:OFF
 *
 * 101 | ABC
 * 111 | DEF
 * 111 | GHI
 *
 * この場合
 * Aを0に変えるためにDを押す。
 *
 * 001 | ABC
 * 001 | DEF
 * 011 | GHI
 *
 * 次にCを0に変えるためにFを押す
 *
 * 000 | ABC
 * 010 | DEF
 * 010 | GHI
 *
 * 次にEを0に変えるためにHを押したいが、最下段が全て0にならないのでNG
 * その場合、一行目を左から順に組み合わせで押した場合をスタートとして考え全ての手順を試す。
 * Aを押した場合、AとBを押した場合、、、、
 * 011 | ABC
 * 011 | DEF
 * 111 | GHI
 */
public class LightsOut {
	/**
	 * Iterate through each line of input.
	 */
	public static void main(String[] args) throws IOException {
		InputStreamReader reader = new InputStreamReader(System.in, StandardCharsets.UTF_8);
		BufferedReader in = new BufferedReader(reader);
		String line;
		boolean lineCulFlag = true;
		String lineCul = "";
		int lineNum  = 0;
		int culNum = 0;

		String lightStr = "";

		// 入力回数
		int inputCount = 0;

		// Push回数
		int count = 0;

		//入力例 three;seven;eight;nine;two
		while ((line = in.readLine()) != null) {

			if (lineCulFlag) {
				lineCul = line;

				lineNum = Integer.parseInt(lineCul.split(" ")[0].toString());
				culNum = Integer.parseInt(lineCul.split(" ")[1].toString());

				lineCulFlag = false;
			} else {
				lightStr = lightStr + line;
				inputCount = inputCount + 1;
			}
			if (inputCount == lineNum) {
				break;
			}
		}

		/**
		 * 0 : ON
		 * 1 : OFF
		 */
		String lightLine[] = lightStr.replace("\n", "").replace(".", "1").split("");

		int[][] lightsOriginal = new int[lineNum][culNum];

		for (int l = 0; l < lineNum; l++) {
			for (int c = 0; c < culNum; c++) {
				lightsOriginal[l][c] = Integer.parseInt(lightLine[ l * culNum + c]);
			}
		}

		// 一行目を押す組み合わせ
		int firstLinePattern = (int) Math.pow(2, culNum);
		String firstLinePushStr[] = new String[firstLinePattern];
		firstLinePush(firstLinePushStr, culNum, firstLinePattern);

		// 格納完了
		ArrayList<Integer> resultTimes = new ArrayList<Integer>();

		// 計算
		firstLineLights(resultTimes , firstLinePushStr, count, lineNum, culNum, lightsOriginal);

		// 全てをOFFにした結果の中から最小値を出力
		// 成功しなければ-1を出力
		if (resultTimes.size() == 0) {
			System.out.println("-1");
		} else {

			Optional<Integer> resultMin = resultTimes.stream().min((a, b) -> a.compareTo(b));
			System.out.println(resultMin.get());
		}

	}

	// 一行目を押すパターン。押すか押さないかの2パターンだけなので列数の2進数で表せられる。
	public static void firstLinePush (String firstLineStr[], int culNum, int pattern){
		for (int i = 0; i < pattern; i++) {
			firstLineStr[i] = String.format("%0" + culNum + "d", Integer.parseInt(Integer.toBinaryString(i)));
		}
	}
	// 一行目を変更する
	public static ArrayList<Integer> firstLineLights(ArrayList<Integer> resultTimes, String firstLineStr[], int count, int lineNum, int culNum, int lightsArg[][]) {
		boolean nochangeFlag = true;
		for (int i = 0; i < firstLineStr.length; i++) {
			count = 0;
			for (int j = 0; j < firstLineStr[i].length(); j++) {
				int[][] lights = new int[lineNum][culNum];
				for(int n = 0; n < lightsArg.length; n++) {
					lights[n] = lightsArg[n].clone();
				}
				if (Integer.parseInt(firstLineStr[i].split("")[j]) == 1) {
					nochangeFlag = false;
					count = count + 1;
					lights[0][j] = lights[0][j] + 1;
					//真下
					lights[0 + 1][j] = lights[0 + 1][j] + 1;
					//左
					if (j > 0) {
						lights[0][j - 1] = lights[0][j - 1] + 1;
					}
					//右
					if (j < culNum -1) {
						lights[0][j + 1] = lights[0][j + 1] + 1;
					}
					checkLights(resultTimes, count, lineNum, culNum, lights);
				}
				if (nochangeFlag) {
					checkLights(resultTimes, count, lineNum, culNum, lights);
				}

			}
		}
		return resultTimes;
	}

	/**
	 * 二行目以降を変更してすべてOFFになったか確認
	 * @return クリア回数のリスト
	 */
	public static ArrayList<Integer> checkLights(ArrayList<Integer> resultTimes, int count, int lineNum, int culNum, int lightsArg[][]) {
		int cnt = 0;
		int[][] lights = new int[lineNum][culNum];
		for(int i = 0; i < lightsArg.length; i++) {
			lights[i] = lightsArg[i].clone();
		}
		for (int l = 0; l < lineNum - 1; l++) {
			for (int c = 0; c < culNum; c++) {
				if (lights[l][c] % 2 == 0) {
					cnt = cnt + 1;
					lights[l][c] = lights[l][c] + 1;
					//真下
					lights[l + 1][c] = lights[l + 1][c] + 1;
					// 2つ下
					if (l + 2 < lineNum) {
						lights[l + 2][c] = lights[l + 2][c] + 1;
					}
					//左下
					if (c > 0) {
						lights[l + 1][c - 1] = lights[l + 1][c - 1] + 1;
					}
					//右下
					if (c + 1 < culNum) {
						lights[l + 1][c + 1] = lights[l + 1][c + 1] + 1;
					}
				}
			}
		}
		for (int l = 0; l < lineNum; l++) {
			for (int c = 0; c < culNum; c++) {
				if (lights[l][c] % 2 == 0) {
					return resultTimes;
				}
			}
		}
		resultTimes.add(count + cnt);
		return resultTimes;
	}
}
