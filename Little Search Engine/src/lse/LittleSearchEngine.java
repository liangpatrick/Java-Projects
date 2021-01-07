package lse;

import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages
 * in which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {

	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the
	 * associated value is an array list of all occurrences of the keyword in
	 * documents. The array list is maintained in DESCENDING order of frequencies.
	 */
	HashMap<String, ArrayList<Occurrence>> keywordsIndex;

	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;

	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String, ArrayList<Occurrence>>(1000, 2.0f);
		noiseWords = new HashSet<String>(100, 2.0f);
	}

	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword
	 * occurrences in the document. Uses the getKeyWord method to separate keywords
	 * from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an
	 *         Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String, Occurrence> loadKeywordsFromDocument(String docFile) throws FileNotFoundException {
		/** COMPLETE THIS METHOD **/
		try {
			HashMap<String, Occurrence> hash = new HashMap<String, Occurrence>();
			File file = new File(docFile);
			Scanner scan = new Scanner(file);
			while (scan.hasNext()) {
				String word = scan.next();
				word = getKeyword(word);
				if (word != null) {
					if (hash.containsKey(word))
						hash.get(word).frequency++;
					else {
						Occurrence temp = new Occurrence(docFile, 1);
						hash.put(word, temp);
					}

				}

			}
			scan.close();
			return hash;
		} catch (FileNotFoundException file) {
			throw new FileNotFoundException();
		}

		// following line is a placeholder to make the program compile
		// you should modify it as needed when you write your code

	}

	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of
	 * any trailing punctuation(s), consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!' NO
	 * OTHER CHARACTER SHOULD COUNT AS PUNCTUATION
	 * 
	 * If a word has multiple trailing punctuation characters, they must all be
	 * stripped So "word!!" will become "word", and "word?!?!" will also become
	 * "word"
	 * 
	 * See assignment description for examples
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyword(String word) {
		word = word.toLowerCase();

		for (int x = word.length() - 1; x >= 0; x--) {
			char last = word.charAt(x);
			if (last != '.' && last != ',' && last != '?' && last != ':' && last != ';' && last != '!') {
				// System.out.println("h");
				if (x == word.length() - 1) {
					// System.out.println("kill");
					break;
				} else {
					word = word.substring(0, x + 1);
					// System.out.println(word);
					break;
				}
			}
			if (x == 0) {
				// System.out.println("shit");
				return null;
			}

		}
		// System.out.println(word);
		for (int x = 0; x < word.length(); x++) {
			char curr = word.charAt(x);
			if (!Character.isLetter(curr)) {
				// System.out.println("crap");
				return null;
			}
		}
		// System.out.println(word);

		if (noiseWords.contains(word)) {
			return null;
		}

		return word;
	}

	/**
	 * Merges the keywords for a single document into the master keywordsIndex hash
	 * table. For each keyword, its Occurrence in the current document must be
	 * inserted in the correct place (according to descending order of frequency) in
	 * the same keyword's Occurrence list in the master hash table. This is done by
	 * calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String, Occurrence> kws) {
		/** COMPLETE THIS METHOD **/
		for (Map.Entry<String, Occurrence> x : kws.entrySet()) {
			if (keywordsIndex.containsKey(x.getKey())) {
				ArrayList<Occurrence> temp = keywordsIndex.get(x.getKey());
				temp.add(x.getValue());
				insertLastOccurrence(temp);

			} else {
				ArrayList<Occurrence> temp = new ArrayList<Occurrence>();
				temp.add(x.getValue());
				keywordsIndex.put(x.getKey(), temp);
			}

		}
	}

	/**
	 * Inserts the last occurrence in the parameter list in the correct position in
	 * the list, based on ordering occurrences on descending frequencies. The
	 * elements 0..n-2 in the list are already in the correct order. Insertion is
	 * done by first finding the correct spot using binary search, then inserting at
	 * that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary
	 *         search process, null if the size of the input list is 1. This
	 *         returned array list is only used to test your code - it is not used
	 *         elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		/** COMPLETE THIS METHOD **/
		ArrayList<Integer> list = mod(occs);
		// int upp = occs.size() - 2;
		int len = occs.size() - 1;
		int index = list.get(list.size() - 1);
		if (index != len) {
			Occurrence temp = occs.get(len);
			occs.remove(len);
			occs.add(index, temp);
		}
		list.remove(list.size() - 1);
		// following line is a placeholder to make the program compile
		// you should modify it as needed when you write your code
		return list;
	}

	private ArrayList<Integer> mod(ArrayList<Occurrence> occs) {
		int upBounds = occs.size() - 2;
		int lowBounds = 0;
		int targ = occs.get(upBounds + 1).frequency;

		// int index = modbinSearch(occs, upBounds, lowBounds);
		ArrayList<Integer> list = new ArrayList<Integer>();
		return search(occs, list, targ, upBounds, lowBounds);
	}

	private ArrayList<Integer> search(ArrayList<Occurrence> occs, ArrayList<Integer> list, int target, int up,
			int low) {
		if (up >= low) {
			int mid = (low + up) / 2;
			list.add(mid);
			if (occs.get(mid).frequency == target)
				return list;
			else if (occs.get(mid).frequency > target) {
				low = mid + 1;
				if (low > up) {
					int a = mid + 1;
					list.add(a);
					return list;
				}
				return search(occs, list, target, up, low);
			} else {
				up = mid - 1;
				if (low > up) {
					int a = mid;
					list.add(a);
					return list;
				}
				return search(occs, list, target, up, low);

			}

		}
		return null;
	}

	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all
	 * keywords, each of which is associated with an array list of Occurrence
	 * objects, arranged in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile       Name of file that has a list of all the document file
	 *                       names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise
	 *                       word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input
	 *                               files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}

		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String, Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}
		sc.close();
	}

	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2
	 * occurs in that document. Result set is arranged in descending order of
	 * document frequencies.
	 * 
	 * Note that a matching document will only appear once in the result.
	 * 
	 * Ties in frequency values are broken in favor of the first keyword. That is,
	 * if kw1 is in doc1 with frequency f1, and kw2 is in doc2 also with the same
	 * frequency f1, then doc1 will take precedence over doc2 in the result.
	 * 
	 * The result set is limited to 5 entries. If there are no matches at all,
	 * result is null.
	 * 
	 * See assignment description for examples
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in
	 *         descending order of frequencies. The result size is limited to 5
	 *         documents. If there are no matches, returns null or empty array list.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		// return null;
		kw1 = kw1.toLowerCase();
		kw2 = kw2.toLowerCase();
		if (!keywordsIndex.containsKey(kw1) && !keywordsIndex.containsKey(kw2))
			return null;

		ArrayList<String> arr = new ArrayList<String>();
		if (keywordsIndex.containsKey(kw1) && keywordsIndex.containsKey(kw2)) {
			ArrayList<Occurrence> oc1 = keywordsIndex.get(kw1);
			ArrayList<Occurrence> oc2 = keywordsIndex.get(kw2);
			HashSet<String> duplicate = new HashSet<>();
			int index1 = 0;
			int index2 = 0;

			while (arr.size() < 5 && (index1 < oc1.size() && index2 < oc2.size())) {
				Occurrence temp;
				if (oc1.get(index1).frequency >= oc2.get(index2).frequency) {
					temp = oc1.get(index1);
					index1++;
				} else {
					temp = oc2.get(index2);
					index2++;
				}
				if (!duplicate.contains(temp.document)) {
					arr.add(temp.document);
					duplicate.add(temp.document);
				}

			}
			if (arr.size() != 5) {
				if (index1 == oc1.size() && index2 < oc2.size()) {
					for (int x = index2; x < oc2.size(); x++) {
						if (arr.size() == 5)
							break;
						if (!duplicate.contains(oc2.get(x).document)) {
							arr.add(oc2.get(x).document);
							duplicate.add(oc2.get(x).document);
						}
					}
				} else if (index2 == oc2.size() && index1 < oc1.size()) {
					for (int x = index1; x < oc1.size(); x++) {
						if (arr.size() == 5)
							break;
						if (!duplicate.contains(oc1.get(x).document)) {
							arr.add(oc1.get(x).document);
							duplicate.add(oc1.get(x).document);
						}
					}
				}
			}

		} else if (keywordsIndex.containsKey(kw1) && !keywordsIndex.containsKey(kw2)) {
			for (int x = 0; x < keywordsIndex.get(kw1).size(); x++) {
				if (arr.size() == 5)
					break;
				if (!arr.contains(keywordsIndex.get(kw1).get(x).document)) {
					arr.add(keywordsIndex.get(kw1).get(x).document);
				}
			}
		} else if (!keywordsIndex.containsKey(kw1) && keywordsIndex.containsKey(kw2)) {
			for (int x = 0; x < keywordsIndex.get(kw2).size(); x++) {
				if (arr.size() == 5)
					break;
				if (!arr.contains(keywordsIndex.get(kw2).get(x).document)) {
					arr.add(keywordsIndex.get(kw2).get(x).document);
				}
			}
		}
		// if (oc1 == null && oc2 == null)
		// return null;
		// int count = 0;
		if (arr.size() == 0)
			return null;
		return arr;
	}

}
