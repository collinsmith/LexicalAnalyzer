package edu.csupomona.cs.cs411.project1.lexer;

/**
 * Interface which represents a Token. A Token should have some kind of
 regex used to compare with other values to determine whether or not they
 represent a Token.
 *
 * @author Collin Smith <collinsmith@csupomona.edu>
 */
public interface Token {
	/**
	 * Returns the regex value associated with this {@link Token}.
	 *
	 * @return regex representing this Token
	 */
	String getRegex();

	/**
	 * Returns whether or not the regex value for this token should be
	 * interpreted as a regular expression or as a literal value.
	 *
	 * @return {@code true} if this token is actually represented by a regular
	 *	expression, otherwise {@code false} if it is a literal value
	 */
	boolean isRegex();

	/**
	 * Returns the unique index identifying this {@link Token}.
	 *
	 * @return integer representing this token
	 */
	int getId();
}
