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
}
