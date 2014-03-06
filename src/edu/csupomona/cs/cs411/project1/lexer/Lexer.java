package edu.csupomona.cs.cs411.project1.lexer;

import java.io.Reader;

public interface Lexer<E> {
	TokenStream lex(Reader r);
	E next(Reader r);
}
