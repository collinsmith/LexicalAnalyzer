package edu.csupomona.cs.cs411.project1.lexer;

import java.io.Reader;

public abstract class AbstractLexer implements Lexer<Token> {
	public AbstractLexer() {
		//...
	}

	@Override
	public TokenStream lex(Reader r) {
		return new TokenStream(this, r);
	}
}
