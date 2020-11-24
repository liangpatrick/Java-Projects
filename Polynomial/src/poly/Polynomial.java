package poly;

import java.io.IOException;
import java.util.Scanner;

/**
 * This class implements evaluate, add and multiply for polynomials.
 * 
 * @author runb-cs112
 *
 */
public class Polynomial {

	/**
	 * Reads a polynomial from an input stream (file or keyboard). The storage
	 * format of the polynomial is:
	 * 
	 * <pre>
	 *     <coeff> <degree>
	 *     <coeff> <degree>
	 *     ...
	 *     <coeff> <degree>
	 * </pre>
	 * 
	 * with the guarantee that degrees will be in descending order. For example:
	 * 
	 * <pre>
	 *      4 5
	 *     -2 3
	 *      2 1
	 *      3 0
	 * </pre>
	 * 
	 * which represents the polynomial:
	 * 
	 * <pre>
	 * 4 * x ^ 5 - 2 * x ^ 3 + 2 * x + 3
	 * </pre>
	 * 
	 * @param sc Scanner from which a polynomial is to be read
	 * @throws IOException If there is any input error in reading the polynomial
	 * @return The polynomial linked list (front node) constructed from coefficients
	 *         and degrees read from scanner
	 */
	public static Node read(Scanner sc) throws IOException {
		Node poly = null;
		while (sc.hasNextLine()) {
			Scanner scLine = new Scanner(sc.nextLine());
			poly = new Node(scLine.nextFloat(), scLine.nextInt(), poly);
			scLine.close();
		}
		return poly;
	}

	private static Node addAndRemove(Node p) {
		Node curr = p, next, prev = null;

		do {
			next = curr.next;
			prev = curr;
			while (next != null) {
				if (next.term.degree == curr.term.degree) {
					float co = curr.term.coeff + next.term.coeff;
					curr.term.coeff = co;
					prev.next = next.next;

				} else {
					prev = next;
				}

				next = next.next;

			}

			curr = curr.next;

		} while (curr.next != null);
		return p;
	}

	private static Node sort(Node p) {
		Node temp = null, ptr = p, nnode = null, head = null;
		int plim = ptr.term.degree, lim = 0;
		ptr = ptr.next;
		while (ptr != null) {
			lim = Math.max(plim, ptr.term.degree);
			plim = ptr.term.degree;
			ptr = ptr.next;

		}
		for (int x = 0; x <= lim; x++) {
			ptr = p;
			while (ptr != null) {
				if (ptr.term.degree == x) {
					if (temp != null) {
						if (ptr.term.coeff == 0) {

						} else {
							nnode = new Node(ptr.term.coeff, ptr.term.degree, null);
							temp.next = nnode;
							temp = nnode;
						}

					} else {
						if (ptr.term.coeff == 0) {

						} else {
							temp = new Node(ptr.term.coeff, ptr.term.degree, temp);
							head = temp;
						}
					}

				}
				ptr = ptr.next;
			}

		}

		return head;
	}

	/**
	 * Returns the sum of two polynomials - DOES NOT change either of the input
	 * polynomials. The returned polynomial MUST have all new nodes. In other words,
	 * none of the nodes of the input polynomials can be in the result.
	 * 
	 * @param poly1 First input polynomial (front of polynomial linked list)
	 * @param poly2 Second input polynomial (front of polynomial linked list
	 * @return A new polynomial which is the sum of the input polynomials - the
	 *         returned node is the front of the result polynomial
	 */
	public static Node add(Node poly1, Node poly2) {
		/** COMPLETE THIS METHOD **/
		if (poly1 == null && poly2 == null) {
			Node temp = null;
			return temp;
		} else if (poly2 == null) {
			poly2 = new Node(0, 0, null);
		} else if (poly1 == null) {
			poly1 = new Node(0, 0, null);
		}
		Node ptr1 = poly1, ptr2 = poly2, sum = null, nnode = null, head = null;
		while (ptr1 != null && ptr2 != null) {
			if (ptr1.term.degree < ptr2.term.degree) {
				if (sum != null) {
					nnode = new Node(ptr1.term.coeff, ptr1.term.degree, null);
					sum.next = nnode;
					sum = nnode;

				} else {
					sum = new Node(ptr1.term.coeff, ptr1.term.degree, sum);
					head = sum;
				}
				ptr1 = ptr1.next;
			} else if (ptr2.term.degree < ptr1.term.degree) {
				if (sum != null) {
					nnode = new Node(ptr2.term.coeff, ptr2.term.degree, null);
					sum.next = nnode;
					sum = nnode;

				} else {
					sum = new Node(ptr2.term.coeff, ptr2.term.degree, sum);
					head = sum;
				}
				ptr2 = ptr2.next;
			} else {

				if (sum != null) {
					float co = ptr1.term.coeff + ptr2.term.coeff;
					nnode = new Node(co, ptr1.term.degree, null);
					sum.next = nnode;
					sum = nnode;

				} else {
					float co = ptr1.term.coeff + ptr2.term.coeff;
					sum = new Node(co, ptr1.term.degree, sum);
					head = sum;
				}
				ptr1 = ptr1.next;
				ptr2 = ptr2.next;

			}

		}
		Node nptr = null;
		if (ptr1 != null) {
			nptr = ptr1;
		} else if (ptr2 != null) {
			nptr = ptr2;
		}
		while (nptr != null) {
			nnode = new Node(nptr.term.coeff, nptr.term.degree, null);
			sum.next = nnode;
			sum = nnode;

			nptr = nptr.next;

		}

		return sort(head);

	}

	/**
	 * Returns the product of two polynomials - DOES NOT change either of the input
	 * polynomials. The returned polynomial MUST have all new nodes. In other words,
	 * none of the nodes of the input polynomials can be in the result.
	 * 
	 * @param poly1 First input polynomial (front of polynomial linked list)
	 * @param poly2 Second input polynomial (front of polynomial linked list)
	 * @return A new polynomial which is the product of the input polynomials - the
	 *         returned node is the front of the result polynomial
	 */

	public static Node multiply(Node poly1, Node poly2) {

		/** COMPLETE THIS METHOD **/
		if (poly1 == null || poly2 == null) {
			Node temp = null;
			return temp;
		}

		Node ptr1 = poly1, ptr2 = poly2, prod = null, nnode = null, head = null;
		while (ptr1 != null) {
			while (ptr2 != null) {
				if (prod != null) {

					float co = ptr1.term.coeff * ptr2.term.coeff;
					int degree = ptr1.term.degree + ptr2.term.degree;
					nnode = new Node(co, degree, null);
					prod.next = nnode;
					prod = nnode;

				} else {

					float co = ptr1.term.coeff * ptr2.term.coeff;
					int degree = ptr1.term.degree + ptr2.term.degree;
					prod = new Node(co, degree, prod);
					head = prod;
				}
				ptr2 = ptr2.next;
			}
			ptr1 = ptr1.next;
			ptr2 = poly2;

		}

		return sort(addAndRemove(head));
	}

	/**
	 * Evaluates a polynomial at a given value.
	 * 
	 * @param poly Polynomial (front of linked list) to be evaluated
	 * @param x    Value at which evaluation is to be done
	 * @return Value of polynomial p at x
	 */
	public static float evaluate(Node poly, float x) {
		/** COMPLETE THIS METHOD **/

		float sum = 0;
		while (poly != null) {
			float temp = poly.term.coeff * (float) Math.pow(x, poly.term.degree);
			sum += temp;
			poly = poly.next;

		}
		return sum;
	}

	/**
	 * Returns string representation of a polynomial
	 * 
	 * @param poly Polynomial (front of linked list)
	 * @return String representation, in descending order of degrees
	 */
	public static String toString(Node poly) {
		if (poly == null) {
			return "0";
		}

		String retval = poly.term.toString();
		for (Node current = poly.next; current != null; current = current.next) {
			retval = current.term.toString() + " + " + retval;
		}
		return retval;
	}
}
