package trie;

import java.util.ArrayList;

/**
 * This class implements a Trie.
 * 
 * @author Sesh Venugopal
 *
 */
public class Trie {

	// prevent instantiation
	private Trie() {
	}

	/**
	 * Builds a trie by inserting all words in the input array, one at a time, in
	 * sequence FROM FIRST TO LAST. (The sequence is IMPORTANT!) The words in the
	 * input array are all lower case.
	 * 
	 * @param allWords Input array of words (lowercase) to be inserted.
	 * @return Root of trie with all words inserted from the input array
	 */

	public static TrieNode prefix(String one, String two, TrieNode ptr) {
		short count = 0;
		while (one.substring(count, count + 1).equals(two.substring(count, count + 1))) {
			count++;
		}
//		System.out.println(count + " kar");
		short end = ptr.substr.endIndex;
		ptr.substr.endIndex = (short) (count - 1);
		Indexes next = new Indexes(ptr.substr.wordIndex, (short) (count), end);

		TrieNode temp = new TrieNode(next, null, null);
//		System.out.println(temp.substr.wordIndex + " nam" + ptr.substr.startIndex + " , " + ptr.substr.endIndex);
		return temp;
	}

	public static TrieNode search(TrieNode ptr, int sub, String[] arr, int x, int count) {
		int ind = ptr.substr.wordIndex;
		if (sub > ptr.substr.endIndex) {
			return search(ptr.firstChild, sub, arr, x, 0);
		}
		if (arr[ind].charAt(sub) == arr[x].charAt(sub)) {

			if (ptr.firstChild != null) {
				return search(ptr, ++sub, arr, x, ++count);

			}
			ptr.firstChild = prefix(arr[ind], arr[x], ptr);
			return ptr;
		}
		if (ptr.sibling == null && ptr.firstChild != null) {

			Indexes t = new Indexes(ptr.substr.wordIndex, ptr.substr.startIndex, ptr.substr.endIndex);

			t.startIndex = (short) (sub);

			t.endIndex = (short) sub;

			TrieNode temp = new TrieNode(t, ptr.firstChild, null);
			ptr.firstChild = temp;
			ptr.substr.endIndex = (short) (sub - 1);

			return ptr;
		}
		if (ptr.sibling == null) {

			return ptr;
		}

		return search(ptr.sibling, sub, arr, x, count);

	}

	public static TrieNode buildTrie(String[] allWords) {
		TrieNode head = new TrieNode(null, null, null);
		Indexes tt = new Indexes(0, (short) 0, (short) (allWords[0].length() - 1));
		TrieNode te = new TrieNode(tt, null, null);
		head.firstChild = te;
		for (int x = 1; x < allWords.length; x++) {
			TrieNode ptr = head.firstChild;
			print(ptr,allWords);
			while (ptr != null) {

				int ind = ptr.substr.wordIndex;

				if (allWords[x].substring(0, 1).equals(allWords[ind].substring(0, 1))) {
					if (ptr.firstChild == null) {
						ptr.firstChild = prefix(allWords[ind], allWords[x], ptr);
						Indexes temp = new Indexes(x, ptr.firstChild.substr.startIndex,
								(short) (allWords[x].length() - 1));
						ptr.firstChild.sibling = new TrieNode(temp, null, null);
						break;
					} else {
						TrieNode t = search(ptr, 1, allWords, x, 0);
						if (t.firstChild != null) {
							t = t.firstChild;
						}
						Indexes y = new Indexes(x, t.substr.startIndex, (short) (allWords[x].length() - 1));
						t.sibling = new TrieNode(y, null, null);

						break;

					}

				}
				if (ptr.sibling == null) {
					Indexes temp = new Indexes(x, (short) 0, (short) (allWords[x].length() - 1));
					ptr.sibling = new TrieNode(temp, null, null);
					break;
				}
				ptr = ptr.sibling;
			}
		}

		return head;
	}

	/**
	 * Given a trie, returns the "completion list" for a prefix, i.e. all the leaf
	 * nodes in the trie whose words start with this prefix. For instance, if the
	 * trie had the words "bear", "bull", "stock", and "bell", the completion list
	 * for prefix "b" would be the leaf nodes that hold "bear", "bull", and "bell";
	 * for prefix "be", the completion would be the leaf nodes that hold "bear" and
	 * "bell", and for prefix "bell", completion would be the leaf node that holds
	 * "bell". (The last example shows that an input prefix can be an entire word.)
	 * The order of returned leaf nodes DOES NOT MATTER. So, for prefix "be", the
	 * returned list of leaf nodes can be either hold [bear,bell] or [bell,bear].
	 *
	 * @param root     Root of Trie that stores all words to search on for
	 *                 completion lists
	 * @param allWords Array of words that have been inserted into the trie
	 * @param prefix   Prefix to be completed with words in trie
	 * @
	 * return List of all leaf nodes in trie that hold words that start with the
	 *         prefix, order of leaf nodes does not matter. If there is no word in
	 *         the tree that has this prefix, null is returned.
	 */
	public static TrieNode findMatch(TrieNode ptr, int sub, String[] arr, String prefix) {
		int ind = ptr.substr.wordIndex;
		if (sub > ptr.substr.endIndex) {
			return findMatch(ptr.firstChild, sub, arr, prefix);
		}
		if (arr[ind].charAt(sub) == prefix.charAt(sub)) {
			if (prefix.length() - 1 <= ptr.substr.endIndex) {

				return ptr;
			}
			if (ptr.firstChild != null) {
				return findMatch(ptr, ++sub, arr, prefix);
			}

		}

		return findMatch(ptr.sibling, sub, arr, prefix);

	}

	public static ArrayList<TrieNode> add(TrieNode ptr, ArrayList<TrieNode> list, String[] arr, String prefix) {
		if (ptr != null) {
			if (ptr.firstChild != null) {


				add(ptr.firstChild, list, arr, prefix);
			}


			list.add(ptr);
			if (ptr.sibling!= null &&arr[ptr.sibling.substr.wordIndex].contains(prefix))
				add(ptr.sibling, list, arr, prefix);
		}

		return list;
	}

	public static ArrayList<TrieNode> completionList(TrieNode root, String[] allWords, String prefix) {
		/** COMPLETE THIS METHOD **/
		if (root.firstChild == null) {
			return null;
		}
		ArrayList<TrieNode> list = new ArrayList<TrieNode>();

		TrieNode ptr = root.firstChild;
		TrieNode temp = new TrieNode(null, null, null);

		while (ptr != null) {
			int ind = ptr.substr.wordIndex;
			if (allWords[ind].charAt(0) == prefix.charAt(0)) {

				temp = findMatch(ptr, 0, allWords, prefix);
				break;

			}
			ptr = ptr.sibling;
		}

		while (temp != null) {
			int ind = temp.substr.wordIndex;
			if (allWords[ind].contains(prefix)) {
				TrieNode k = null;
				if (temp.firstChild != null)
					k = temp.firstChild;
				else {
					k = temp;
				}

				list = add(k, list, allWords, prefix);
			}
			temp = temp.sibling;

		}

		return list;
	}

	public static void print(TrieNode root, String[] allWords) {
		System.out.println("\nTRIE\n");
		print(root, 1, allWords);
	}

	private static void print(TrieNode root, int indent, String[] words) {
		if (root == null) {
			return;
		}
		for (int i = 0; i < indent - 1; i++) {
			System.out.print("    ");
		}

		if (root.substr != null) {
			String pre = words[root.substr.wordIndex].substring(0, root.substr.endIndex + 1);
			System.out.println("      " + pre);
		}

		for (int i = 0; i < indent - 1; i++) {
			System.out.print("    ");
		}
		System.out.print(" ---");
		if (root.substr == null) {
			System.out.println("root");
		} else {
			System.out.println(root.substr);
		}

		for (TrieNode ptr = root.firstChild; ptr != null; ptr = ptr.sibling) {
			for (int i = 0; i < indent - 1; i++) {
				System.out.print("    ");
			}
			System.out.println("     |");
			print(ptr, indent + 1, words);
		}
	}
}
