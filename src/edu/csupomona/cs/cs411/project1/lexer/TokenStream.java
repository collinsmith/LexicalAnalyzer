package edu.csupomona.cs.cs411.project1.lexer;

import java.io.IOException;
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
		try {
			READER.ready();
		} catch (IOException e) {
			return false;
		}

		if (nextToken != null) {
			return true;
		}

		nextToken = LEXER.next(READER);
		return nextToken != null;
	}

	public Token getNext() {
		if (nextToken != null) {
			Token t = nextToken;
			nextToken = null;
			return t;
		}

		return LEXER.next(READER);
	}

	public Token peek() {
		if (nextToken != null) {
			return nextToken;
		}

		nextToken = LEXER.next(READER);
		return nextToken;
	}
}
