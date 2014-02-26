package edu.csupomona.cs.cs411.project1.trie;

import java.util.Arrays;

/**
 * This class represents a {@link MultiTrie} of Strings represented using arrays
 * to store state data.
 *
 * @author Collin Smith <collinsmith@csupomona.edu>
 */
public class ArrayMultiTrie implements MultiTrie<String> {
	/**
	 * The number of transitions to create within the {@link #base} array.
	 * This is configured to only work with 52 characters (a-z and A-Z).
	 */
	private static final int ALPHABET_SIZE = 52;

	/**
	 * Default maximum number of cells allocated to the {@link #data} and
	 * {@link #next} arrays.
	 */
	private static final int DEFAULT_DATA_SIZE = 1<<8;

	/**
	 * Default sentinel character to use for {@link #insert(java.lang.String)}.
	 */
	private static final char DEFAULT_SENTINEL = '*';

	/**
	 * Default integer value used to fill {@link #base} and {@link #next}
	 * during initialization.
	 */
	private static final int DEFAULT_INT = -1;

	/**
	 * Stores the first transition of each character, specified using values
	 * returned from passing that character into
	 * {@link #getBaseIndexForChar(char)}.
	 */
	private int[] base;

	/**
	 * Stores character data used in transitions and comparisons.
	 */
	private char[] data;

	/**
	 * Stores possible links to other variations in transitions of the
	 * transition contained within {@link #data} is invalid.
	 */
	private int[] next;

	/**
	 * Index of the first empty position within {@link #data}.
	 */
	private int tail;

	/**
	 * Total number of keys within this {@link ArrayMultiTrie}.
	 */
	private int numKeys;

	/**
	 * Default constructor which initializes an empty {@link ArrayMultiTrie}.
	 */
	public ArrayMultiTrie() {
		base = new int[ALPHABET_SIZE];
		Arrays.fill(base, DEFAULT_INT);

		data = new char[DEFAULT_DATA_SIZE];
		next = new int[data.length];
		Arrays.fill(next, DEFAULT_INT);

		tail = 0;
		numKeys = 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void insert(String key) {
		insert(key, DEFAULT_SENTINEL);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void insert(String key, char sentinel) {
		if (key == null) {
			throw new NullPointerException();
		}

		if (key.isEmpty()) {
			throw new IllegalArgumentException("Empty keys are not allowed.");
		}

		if (Character.isLetterOrDigit(sentinel) || sentinel == '_') {
			throw new IllegalArgumentException("Marker cannot be a letter, digit or underscore.");
		}

		char[] chars = key.toCharArray();

		int baseId = getBaseIndexForChar(chars[0]);
		if (baseId < 0 || base.length <= baseId) {
			return;
		}

		int dataId = base[baseId];
		if (dataId == DEFAULT_INT) {
			base[baseId] = tail;
			create(chars, 1, sentinel);
			return;
		}

		int i;
		keyComparator: for (i = 1; i < chars.length; i++) {
			nextComparator: while (true) {
				if (data[dataId] == chars[i]) {
					dataId++;
					break nextComparator;
				} else if (0 <= next[dataId]) {
					dataId = next[dataId];
					continue nextComparator;
				} else {
					next[dataId] = tail;
					create(chars, i, sentinel);
					return;
				}
			}
		}

		if (data[dataId] == sentinel) {
			return;
		} else {
			while (0 <= next[dataId]) {
				dataId = next[dataId];
				if (data[dataId] == sentinel) {
					return;
				}
			}
		}

		checkAndGrow(1);
		next[dataId] = tail;
		data[tail++] = sentinel;
		numKeys++;
	}

	private void checkAndGrow(int size) {
		if (data.length > (tail+size)) {
			return;
		}

		char[] newData = new char[data.length+DEFAULT_DATA_SIZE];
		System.arraycopy(data, 0, newData, 0, tail);
		data = newData;

		int[] newNext = new int[data.length];
		Arrays.fill(newNext, DEFAULT_INT);
		System.arraycopy(next, 0, newNext, 0, tail);
		next = newNext;

		checkAndGrow(size);
	}

	/**
	 * Copies the specified character array into {@link #data} from the
	 * specified starting index and using the specified character marker
	 * to mark the end of the string.
	 *
	 * @param chars string to insert
	 * @param start index of string to start inserting at
	 * @param sentinel symbol used to mark end of string
	 */
	private void create(char[] chars, int start, char sentinel) {
		assert chars != null;
		// TODO: start < chars.length? for loop will handle
		assert 0 <= start;
		checkAndGrow(chars.length-start+1);
		for (int i = start; i < chars.length; i++) {
			data[tail++] = chars[i];
		}

		data[tail++] = sentinel;
		numKeys++;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean contains(String key) {
		return contains(key, DEFAULT_SENTINEL);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean contains(String key, char sentinel) {
		if (key == null) {
			throw new NullPointerException();
		}

		if (key.isEmpty()) {
			throw new IllegalArgumentException("Empty keys are not allowed.");
		}

		if (Character.isLetterOrDigit(sentinel) || sentinel == '_') {
			throw new IllegalArgumentException("Marker cannot be a letter, digit or underscore.");
		}

		char[] chars = key.toCharArray();

		int baseId = getBaseIndexForChar(chars[0]);
		if (baseId < 0 || base.length <= baseId) {
			return false;
		}

		int dataId = base[baseId];
		if (dataId == DEFAULT_INT) {
			return false;
		}

		int i;
		keyComparator: for (i = 1; i < chars.length; i++) {
			nextComparator: while (true) {
				if (data[dataId] == chars[i]) {
					dataId++;
					break nextComparator;
				} else if (0 <= next[dataId]) {
					dataId = next[dataId];
					continue nextComparator;
				} else {
					return false;
				}
			}
		}

		if (data[dataId] == sentinel) {
			return true;
		} else {
			while (0 <= next[dataId]) {
				dataId = next[dataId];
				if (data[dataId] == sentinel) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int numKeys() {
		return numKeys;
	}

	/**
	 * Returns the index associated with a specified character. This index
	 * is determined using the characters ASCII value with A-Z represented
	 * by 0-25 and a-z represented by 26-51.
	 *
	 * @param c character to find index within {@link #base} for.
	 *
	 * @return Index corresponding with the character.
	 */
	private int getBaseIndexForChar(char c) {
		assert ('A' <= c && c <= 'Z') || ('a' <= c && c <= 'z');
		return (c < 'a') ? (c-'A') : 26 + (c-'a');
	}
}
