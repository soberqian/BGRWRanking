package evaluation;

import java.util.ArrayList;

import utils.Utils;


public class DatSpliting {
	public static void main(String[] args) {
		
		Random random = Random.getInstance();
		ArrayList<String> lines = new ArrayList<>();
		ArrayList<String> train = new ArrayList<>();
		ArrayList<String> test = new ArrayList<>();
		Utils.readLines("recommend/ratings.txt", lines, "gbk");
		for (int i = 0; i < lines.size(); i++) {
			if (random.nextDouble() < 0.2)
		        test.add(lines.get(i));
		      else
		        train.add(lines.get(i));
		}
		Utils.writeLines("recommend/train2.txt", train, "gbk");
		Utils.writeLines("recommend/test2.txt", test, "gbk");
	}
}
