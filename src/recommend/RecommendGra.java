package recommend;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import evaluation.Evaluation;
import utils.Utils;

public class RecommendGra {
	static Map<Integer, Integer> userIdToIndex;
	static Map<Integer, Integer> ItemIdToIndex;
	static int iter = 0;
	static double[][] score;
	static double epson = Math.pow(10, -17);
	static double c = 0.15;
	static RealMatrix ua_update;
	static List<Integer> indexToProductMap = new ArrayList<Integer>();
	static List<Integer> indexToUserMap = new ArrayList<Integer>();
	public static void main(String[] args) throws IOException {
		String train_file = "recommend/train2.txt";
		String test_file = "recommend/test2.txt";
		String output_file = "recommend/result2.txt";
		RealMatrix matrix = RatingFileProcessing(train_file);
		RealMatrix PA = getPA(matrix);
		RealMatrix uaMatrix = getUa(PA);
		Map<Integer, List<Integer>> reco_data = new HashMap<>();
		for (int i = 0; i < matrix.getRowDimension(); i++) {
			System.out.println("当前用户为:" + indexToUserMap.get(i));
			RealMatrix ua_update = InterRandom(PA,uaMatrix,i);
			RankTopK(ua_update, matrix.getRowDimension(), i);
			reco_data.put(indexToUserMap.get(i), RankTopK(ua_update, matrix.getRowDimension(), i));
		}
		//precision evalution
		Map<Integer, List<Integer>> testData = testRatingFileProcessing(test_file);
		Map<String, Double> pre = precision_Evalution_New(testData,reco_data);
		StringBuilder sBuilder = new StringBuilder();
		for (String key : pre.keySet()){
			sBuilder.append(key + "\t" + pre.get(key) + "\n");
		}
		Utils.writeFile(output_file, sBuilder.toString(), "gbk");
	}
	/**
	 * Get the weight of the edge
	 * matrix M
	 * @param  rating file
	 * @return matrix M
	 */
	public static RealMatrix RatingFileProcessing(String file) {
		ArrayList<String> lines = new ArrayList<>();
		Utils.readLines(file, lines, "gbk");
		userIdToIndex = new HashMap<>();
		ItemIdToIndex = new HashMap<>();
		for (int i = 0; i < lines.size(); i++) {
			Integer user_id = Integer.parseInt(lines.get(i).split("\\s+")[0]);
			Integer item_id = Integer.parseInt(lines.get(i).split("\\s+")[1]);
			if (!userIdToIndex.containsKey(user_id)) {
				int newIndex = userIdToIndex.size();
				userIdToIndex.put(user_id, newIndex);
				indexToUserMap.add(user_id);
			}
			if (!ItemIdToIndex.containsKey(item_id)) {
				int newIndex = ItemIdToIndex.size();
				ItemIdToIndex.put(item_id, newIndex);
				indexToProductMap.add(item_id);

			}
		}
		score = new double[userIdToIndex.size()][ItemIdToIndex.size()];
		for (int i = 0; i < lines.size(); i++) {
			int row = userIdToIndex.get(Integer.parseInt(lines.get(i).split("\\s+")[0]));
			int column = ItemIdToIndex.get(Integer.parseInt(lines.get(i).split("\\s+")[1]));
			score[row][column] = Double.parseDouble(lines.get(i).split("\\s+")[2]);
		}
		RealMatrix M_Matrix = new Array2DRowRealMatrix(score);
		return M_Matrix;
	}
	/**
	 * read the test data
	 * Map
	 * @param  rating file
	 * @return matrix M
	 */
	public static Map<Integer, List<Integer>> testRatingFileProcessing(String file){
		Map<Integer, List<Integer>> test_data = new HashMap<>();
		ArrayList<String> lines = new ArrayList<>();
		Utils.readLines(file, lines, "gbk");
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
		return test_data;
	}
	/**
	 * Construct PA
	 * @param  matrix M
	 * @return matrix PA
	 */
	public static RealMatrix getPA(RealMatrix M) {
		RealMatrix matrixtranspose = M.transpose();
		double[][] Mdata = M.getData();
		double[][] matrixtransposeData = matrixtranspose.getData();
		int k = M.getRowDimension();
		int n = M.getColumnDimension();
		double MA[][] = new double[k + n][k + n];
		double[][] PA = new double[k + n][k + n];
		double sum[] = new double[k+n];
		for (int i = 0; i < k; i++) {
			for (int j = 0; j < n; j++) {
				double value = Mdata[i][j];
				MA[i][j + k] = value;
				sum[j + k] += value;
			}
		}
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < k; j++) {
				double value = matrixtransposeData[i][j];
				MA[k + i][j] = value;
				sum[j] += value;
			}
		}
		for(int i = 0; i < MA.length; i++){
			for(int j = 0;j < MA[0].length; j++){
				PA[i][j] = MA[i][j]/sum[j];
			}
		}
		RealMatrix PAmatrix = new Array2DRowRealMatrix(PA);
		return PAmatrix;
	}
	/**
	 * random walk
	 * repeatedly until it converges.
	 * @param  matrix PA
	 * @param steady-state probability vector ua
	 * @param node a
	 * @return matrix PA
	 */
	public static RealMatrix InterRandom(RealMatrix PA,RealMatrix ua,int rowNodeA){
		RealMatrix PA_input = PA;
		int number_row_input = rowNodeA;
		//initialize vector qa=0,except the a-th element
		double[] qa = new double[PA_input.getColumnDimension()];
		qa[number_row_input] = 1;
		double qa_new[][] = new double[1][PA_input.getColumnDimension()];
		qa_new[0] = qa;
		//qa to two dimension matrix
		RealMatrix qaMatrix = new Array2DRowRealMatrix(qa_new); 
		RealMatrix ua_new = PA_input.multiply(ua.transpose()).scalarMultiply(1-c).add(qaMatrix.scalarMultiply(c).transpose());
		double[] ua_change = ua_new.add(ua.scalarMultiply(-1.0).transpose()).getColumn(0);
		double delta = 0.0;
		for (int i = 0; i < ua_change.length; i++) {
			delta += ua_change[i] * ua_change[i];
		}
		if (delta < epson) {
			System.out.println("收敛");
			ua_update = ua_new;
		}else {
			iter++;
			System.out.println("iter:"+iter);
			ua = ua_new;
			InterRandom(PA_input,ua.transpose(),number_row_input);
		}
		return ua_update;
	}
	/**
	 * Ua 
	 * repeatedly until it converges.
	 * @param  matrix PA
	 * @return matrix UA
	 */
	public static RealMatrix getUa(RealMatrix PA){
		double[] ua = new double[PA.getData().length];
		for (int i = 0; i < ua.length; i++) {
			ua[i] = 1.0/ua.length;
		}
		double ua_new[][] = new double[1][PA.getData().length];
		ua_new[0] = ua;
		//ua to two dimension matrix
		RealMatrix uaMatrix = new Array2DRowRealMatrix(ua_new); 
		return uaMatrix;
	}
	/**
	 * Get top K product 
	 * @param  matrix ua
	 * @return matrix UA
	 */
	public static List<Integer> RankTopK(RealMatrix ua, int k, int userIndex){
		double[] d = ua.transpose().getData()[0];
		double[] product_pagerankVlaue = new double[d.length - k];
		for (int i = k; i < d.length; i++) {
			product_pagerankVlaue[i-k] = d[i];
		}
		int[] index = arraySort(product_pagerankVlaue);
		List<Integer> product = new ArrayList<>();
		for (int i = 0; i < index.length; i++) {
			if (!(score[userIndex][index[i]]>0)) {
				product.add(indexToProductMap.get(index[i]));
			}
		}
		return product;
	}
	public static int[] arraySort(double[]arr) {
		double temp;
		int index;
		int k = arr.length;
		int[]Index = new int[k];
		for(int i = 0;i < k; i++){
			Index[i] = i;
		}
		for(int i=0;i<arr.length;i++){    
			for(int j=0;j<arr.length-i-1;j++){        
				if(arr[j] < arr[j+1]){            
					temp = arr[j];            
					arr[j] = arr[j+1];            
					arr[j+1] = temp;
					index = Index[j];
					Index[j] = Index[j+1];            
					Index[j+1] = index;
				}    
			}
		}
		return Index;
	}
	/**
	 * precision
	 * 
	 * @param test_data
	 * @param reco_data
	 * @param K
	 * @return
	 */
	public static Map<String, Double> precision_Evalution_New(Map<Integer, List<Integer>> test_data, Map<Integer, List<Integer>> reco_data) {
		int num_users = 0;
		Map<String, Double> result = new HashMap<>();
		int[] positions = new int[] { 5, 10,15,20};
		for (int i = 0; i < positions.length; i++) {
			result.put("prec@" + positions[i], 0.0);
		}
		for (Integer key : test_data.keySet()) {
			if (indexToUserMap.contains(key)) {
				List<Integer> ranked_items = reco_data.get(key);
				HashSet<Integer> correct_items = new HashSet<Integer>(Utils.intersect(test_data.get(key), indexToProductMap));
				Collection<Integer> ignore_items = new ArrayList<Integer>();
				Map<Integer, Double> prec = Evaluation.precisionAt(ranked_items, correct_items, ignore_items, positions);
				num_users++;
				result.put("prec@5", prec.get(5) + result.get("prec@5"));
				result.put("prec@10", prec.get(10) + result.get("prec@10"));
				result.put("prec@15", prec.get(15) + result.get("prec@15"));
				result.put("prec@20", prec.get(20) + result.get("prec@20"));
			}
		}
		for (String key : result.keySet()){
			result.put(key, result.get(key) / num_users);
		}
		return result;
	}
}
