package edu.csupomona.cs.cs411.project1.trie;

/**
 * This interface represents a simple Trie data structure.
 *
 * @author Collin Smith <collinsmith@csupomona.edu>
 * @param <K> type of key this Trie uses.
 */
public interface Trie<K> {
	/**
	 * Inserts a specified key into this {@link Trie} if it is not already
	 * there.
	 *
	 * @param key key to insert into this Trie
	 */
	void insert(K key);

	/**
	 * Returns whether or not a specified key is contained within this
	 * {@link Trie}.
	 *
	 * @param key key to check
	 *
	 * @return {@code true} if it is, otherwise {@code false}.
	 */
	boolean contains(K key);

	/**
	 * Returns the number of distinct keys contained within this {@link Trie}.
	 *
	 * @return the number of keys contained within this Trie.
	 */
	int numKeys();
}
