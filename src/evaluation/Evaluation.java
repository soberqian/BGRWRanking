package evaluation;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class Evaluation {
	/**
	 * Compute the precision@N of a list of ranked items at several N.
	 * @param ranked_items a list of ranked item IDs, the highest-ranking item first
	 * @param correct_items a collection of positive/correct item IDs
	 * @param ignore_items a collection of item IDs which should be ignored for the evaluation
	 * @param ns the cutoff positions in the list
	 * @return the precision@N for the given data at the different positions N
	 */
	public static HashMap<Integer, Double> precisionAt(
			List<Integer> ranked_items,
			Collection<Integer> correct_items,
			Collection<Integer> ignore_items,
			int[] ns) {

		HashMap<Integer, Double> precision_at_n = new HashMap<Integer, Double>();
		for (int n : ns)
			precision_at_n.put(n, precisionAt(ranked_items, correct_items, ignore_items, n));

		return precision_at_n;
	}


	/**
	 * Compute the precision@N of a list of ranked items.
	 * @param ranked_items a list of ranked item IDs, the highest-ranking item first
	 * @param correct_items a collection of positive/correct item IDs
	 * @param ignore_items a collection of item IDs which should be ignored for the evaluation
	 * @param n the cutoff position in the list
	 * @return the precision@N for the given data
	 */
	public static double precisionAt(
			List<Integer> ranked_items,
			Collection<Integer> correct_items,
			Collection<Integer> ignore_items,
			int n) {

		return (double) hitsAt(ranked_items, correct_items, ignore_items, n) / n;
	}
	/**
	 * Compute the recall@N of a list of ranked items at several N.
	 * @param ranked_items a list of ranked item IDs, the highest-ranking item first
	 * @param correct_items a collection of positive/correct item IDs
	 * @param ignore_items a collection of item IDs which should be ignored for the evaluation
	 * @param ns the cutoff positions in the list
	 * @return the recall@N for the given data at the different positions N
	 */
	public static HashMap<Integer, Double> recallAt(
			List<Integer> ranked_items,
			Collection<Integer> correct_items,
			Collection<Integer> ignore_items,
			int[] ns)
	{

		HashMap<Integer, Double> recall_at_n = new HashMap<Integer, Double>();
		for (int n : ns)
			recall_at_n.put(n, recallAt(ranked_items, correct_items, ignore_items, n));

		return recall_at_n;
	}
	/**
	 * Compute the recall@N of a list of ranked items.
	 * @param ranked_items a list of ranked item IDs, the highest-ranking item first
	 * @param correct_items a collection of positive/correct item IDs
	 * @param ignore_items a collection of item IDs which should be ignored for the evaluation
	 * @param n the cutoff position in the list
	 * @return the recall@N for the given data
	 */
	public static double recallAt(
			List<Integer> ranked_items,
			Collection<Integer> correct_items,
			Collection<Integer> ignore_items,
			int n) {

		return (double) hitsAt(ranked_items, correct_items, ignore_items, n) / correct_items.size();
	}
	/**
	 * Compute the number of hits until position N of a list of ranked items.
	 * @param ranked_items a list of ranked item IDs, the highest-ranking item first
	 * @param correct_items a collection of positive/correct item IDs
	 * @param ignore_items a collection of item IDs which should be ignored for the evaluation
	 * @param n the cutoff position in the list
	 * @return the hits@N for the given data
	 */
	public static int hitsAt(
			List<Integer> ranked_items,
			Collection<Integer> correct_items,
			Collection<Integer> ignore_items,
			int n) {

		if (n < 1)
			throw new IllegalArgumentException("n must be at least 1.");

		int hit_count = 0;
		int left_out  = 0;

		for (int i = 0; i < ranked_items.size(); i++) {
			int item_id = ranked_items.get(i);
			if (ignore_items.contains(item_id)) {
				left_out++;
				continue;
			}

			if (!correct_items.contains(item_id))
				continue;

			if (i < n + left_out)
				hit_count++;
			else
				break;
		}

		return hit_count;
	}

}
