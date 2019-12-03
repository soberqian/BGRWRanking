package evaluation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import utils.Utils;


public class DataProcessing {
	public static void main(String[] args) {
		ArrayList<String> lines = new ArrayList<>();
		Utils.readLines("recommend/ratings.txt", lines, "gbk");
		Map<Integer, List<Integer>> test_data = new HashMap<>();
		for (int i = 0; i < lines.size(); i++) {
			Integer user_id = Integer.parseInt(lines.get(i).split("\\s+")[0]);
			Integer item_id = Integer.parseInt(lines.get(i).split("\\s+")[1]);
			if (!test_data.containsKey(user_id)) {
				List<Integer> data = new ArrayList<>();
				data.add(item_id);
				test_data.put(user_id, data);
			}else {
				List<Integer> data = test_data.get(user_id);
				data.add(item_id);
				test_data.put(user_id, data);
			}
		}
		List<Integer> listUser = new ArrayList<>();
		for (Integer key : test_data.keySet()){
			if (test_data.get(key).size()<2) {
				listUser.add(key);
			}
		}
		System.out.println(listUser.size());
//		ArrayList<String> output = new ArrayList<>(); 
//		for (int i = 0; i < lines.size(); i++) {
//			Integer user_id = Integer.parseInt(lines.get(i).split("\\s+")[0]);
//			if (!listUser.contains(user_id)) {
//				output.add(lines.get(i));
//			}
//		}
//		Utils.writeLines("recommend/ratingprocess.txt", output, "utf-8");
	}
}
