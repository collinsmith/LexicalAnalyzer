package edu.csupomona.cs.cs411.project1.lexer;

import java.io.Reader;

public class TokenStream {
	private final Lexer<Token> LEXER;
	private final Reader READER;

	private Token nextToken;

	TokenStream(Lexer<Token> l, Reader r) {
		this.LEXER = l;
		this.READER = r;
	}

	public boolean hasMore() {
		return peek() != null;
	}

	public Token peek() {
		if (nextToken != null) {
			return nextToken;
		}

		nextToken = LEXER.next(READER);
		return nextToken;
	}

	public Token next() {
		if (nextToken != null) {
			Token t = nextToken;
			nextToken = null;
			return t;
		}

		return LEXER.next(READER);
	}
}
