package edu.csupomona.cs.cs411.project1.trie;

/**
 * This interface represents a {@link Trie} which can store multiple Tries with
 * homogenous keys marked with different sentinels for each Trie.
 *
 * @author Collin Smith <collinsmith@csupomona.edu>
 * @param <K> type of key this Trie uses.
 */
public interface MultiTrie<K> extends Trie<K> {
	/**
	 * Inserts a specified key into this {@link MultiTrie} using the specified
	 * sentinel to tell which {@link Trie} is belongs to.
	 *
	 * @param key key to insert into this MultiTrie
	 * @param sentinel character representing the Trie this key belongs to
	 *
	 * @see Trie#insert(java.lang.Object)
	 */
	void insert(K key, char sentinel);

	/**
	 * Returns whether or not a specified key is contained within this
	 * {@link MultiTrie} using the specified sentinel to check a specific
	 * {@link Trie}.
	 *
	 * @param key key to check
	 * @param sentinel character representing the Trie this key belongs to
	 *
	 * @return {@code true} if it is, otherwise {@code false}.
	 *
	 * @see Trie#contains(java.lang.Object)
	 */
	boolean contains(K key, char sentinel);
}
