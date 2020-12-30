package friends;

import java.util.ArrayList;
import java.util.Arrays;

import structures.Queue;
import structures.Stack;

public class Friends {

	/**
	 * Finds the shortest chain of people from p1 to p2. Chain is returned as a
	 * sequence of names starting with p1, and ending with p2. Each pair (n1,n2) of
	 * consecutive names in the returned chain is an edge in the graph.
	 * 
	 * @param g  Graph for which shortest chain is to be found.
	 * @param p1 Person with whom the chain originates
	 * @param p2 Person at whom the chain terminates
	 * @return The shortest chain from p1 to p2. Null or empty array list if there
	 *         is no path from p1 to p2
	 */
	// BFS starting at some vertex v
	// private void bfs(int start, boolean[] visited, Queue<Integer> queue) {
	// visited[start] = true;
	// // System.out.println("visiting " + adjLists[start].name);
	// queue.enqueue(start);

	// while (!queue.isEmpty()) {
	// int v = queue.dequeue();

	// }
	// }

	public static ArrayList<String> shortestChain(Graph g, String p1, String p2) {
		ArrayList<String> list = new ArrayList<String>();

		boolean[] visited = new boolean[g.members.length];
		int start = 0;
		Queue<Integer> queue = new Queue<Integer>();
		int[] indexes = new int[g.members.length];
		for (int x = 0; x < g.members.length; x++) {
			if (p1.equals(g.members[x].name)) {
				start = x;
				indexes[x] = -1;
				queue.enqueue(x);
				visited[x] = true;
				break;
			}
		}
		int last = -1;
		while (!queue.isEmpty()) {
			int temp = queue.dequeue();
			for (Friend friend = g.members[temp].first; friend != null; friend = friend.next) {
				int vnum = friend.fnum;
				if (!visited[vnum]) {
					visited[vnum] = true;
					indexes[vnum] = temp;
					if (p2.equals(g.members[vnum].name)) {
						last = vnum;
						list.add(p2);
						break;
					}
					queue.enqueue(vnum);
				}
			}
			if (last != -1)
				break;
		}
		if (last == -1)
			return list;
		int temp = last;
		while (temp != start) {
			temp = indexes[temp];
			list.add(0, g.members[temp].name);
		}
		return list;

	}

	/**
	 * Finds all cliques of students in a given school.
	 * 
	 * Returns an array list of array lists - each constituent array list contains
	 * the names of all students in a clique.
	 * 
	 * @param g      Graph for which cliques are to be found.
	 * @param school Name of school
	 * @return Array list of clique array lists. Null or empty array list if there
	 *         is no student in the given school
	 */
	public static ArrayList<ArrayList<String>> cliques(Graph g, String school) {
		ArrayList<ArrayList<String>> comp = new ArrayList<ArrayList<String>>();
		int t = 0;
		for (int x = 0; x < g.members.length; x++) {
			if (g.members[x].student && g.members[x].school.equals(school)) {
				t++;
				break;
			}
		}
		if (t == 0) {
			return comp;
		}
		boolean[] visited = new boolean[g.members.length];
		for (int x = 0; x < g.members.length; x++) {
			ArrayList<String> list = new ArrayList<String>();
			if (!visited[x] && g.members[x].student && g.members[x].school.equals(school)) {
				visited[x] = true;
				list.add(g.members[x].name);
			}
			search(g, x, visited, list, school);

			if (!list.isEmpty())
				comp.add(list);
		}

		return comp;

	}

	private static void search(Graph g, int ind, boolean[] visit, ArrayList<String> names, String school) {
		visit[ind] = true;
		for (Friend temp = g.members[ind].first; temp != null; temp = temp.next) {
			int vnum = temp.fnum;
			if (!visit[vnum] && g.members[vnum].student && g.members[vnum].school.equals(school)) {
				names.add(g.members[temp.fnum].name);
				search(g, temp.fnum, visit, names, school);
			}

		}

	}

	// public void dfs(Graph g) {
	// boolean[] visited = new boolean[g.members.length];
	// for (int v = 0; v < visited.length; v++) {
	// visited[v] = false;
	// }
	// for (int v = 0; v < visited.length; v++) {
	// if (!visited[v]) {
	// System.out.println("\nSTARTING AT " + g.members[v].name + "\n");
	// dfs(g, v, visited);
	// }
	// }
	// }
	// recursive dfs

	private static void dfs(Graph g, int v, boolean[] visited, int[] dfsnum, int[] back, int count,
			ArrayList<String> conn) {
		visited[v] = true;
		dfsnum[v] = count;
		back[v] = count++;
		for (Friend e = g.members[v].first; e != null; e = e.next) {
			int vnum = e.fnum;
			if (!visited[vnum]) {
				dfs(g, vnum, visited, dfsnum, back, count, conn);
				if (!conn.contains(g.members[v].name) && dfsnum[v] <= back[vnum]) {
					conn.add(g.members[v].name);
				} else {
					back[v] = Math.min(back[v], back[vnum]);
				}

			} else {
				back[v] = Math.min(back[v], dfsnum[vnum]);
			}
		}
	}

	private static void print(Graph g) {
		for (int x = 0; x < g.members.length; x++) {
			System.out.print(g.members[x].name + ":");
			for (Friend temp = g.members[x].first; temp != null; temp = temp.next) {
				System.out.print(" " + g.members[temp.fnum].name + ", " + g.members[temp.fnum].school + ";");
			}
			System.out.println();

		}
	}

	/**
	 * Finds and returns all connectors in the graph.
	 * 
	 * @param g Graph for which connectors needs to be found.
	 * @return Names of all connectors. Null or empty array list if there are no
	 *         connectors.
	 */
	public static ArrayList<String> connectors(Graph g) {
		// print(g);
		ArrayList<String> conn = new ArrayList<String>();
		int[] dfsnum = new int[g.members.length];
		for (int x = 0; x < dfsnum.length; x++) {
			dfsnum[x] = 0;
		}
		int[] back = new int[g.members.length];
		boolean[] visited = new boolean[g.members.length];
		for (int v = 0; v < visited.length; v++) {
			visited[v] = false;
		}
		for (int v = 0; v < visited.length; v++) {
			if (!visited[v]) {
				dfs(g, v, visited, dfsnum, back, 1, conn);
			}
		}

		int count = 0;
		boolean connector = true;
		for (Friend f = g.members[0].first; f != null; f = f.next) {
			count++;
			int vnum = f.fnum;
			String name = g.members[vnum].name;
			if (conn.contains(name))
				connector = false;
		}
		if (count <= 2 || connector == false) {
			String name = g.members[0].name;
			for (int x = 0; x < conn.size(); x++)
				if (conn.get(x).equals(name)) {
					conn.remove(x);
					break;
				}
		}
		return conn;

	}
}
