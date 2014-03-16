package edu.csupomona.cs.cs411.project1.lexer;

import java.io.Reader;

/**
 * This class represents an abstract {@link Lexer} which implements
 * {@link Lexer#lex(java.io.Reader)} to return a {@link TokenStream}.
 *
 * @author Collin Smith <collinsmith@csupomona.edu>
 */
public abstract class AbstractLexer implements Lexer<Token> {
	/**
	 * Default constructor which is unused.
	 */
	public AbstractLexer() {
		//...
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TokenStream lex(Reader r) {
		return new TokenStream(this, r);
	}
}
