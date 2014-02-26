package edu.csupomona.cs.cs411.project1;

import edu.csupomona.cs.cs411.project1.trie.MultiTrie;
import edu.csupomona.cs.cs411.project1.trie.ArrayMultiTrie;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

/**
 * This class represents a lexical analyzer that can be used for the Toy
 * language.
 *
 * @author Collin Smith <collinsmith@csupomona.edu>
 */
public class Lexer implements Iterable<Token> {
	/**
	 * Sentinel used to mark identifiers within {@link #trie}.
	 */
	private static final char IDENTIFIER_SENTINEL = '$';

	/**
	 * {@link MultiTrie} used to store both {@link Keywords#ACTUAL_KEYWORDS}
	 * and other identifiers accepted under the constraints specified by
	 * {@link Keywords#_id}.
	 */
	private MultiTrie<String> trie;

	/**
	 * {@link Reader} to use when iterating through this {@link Lexer}.
	 */
	private final Reader r;

	/**
	 * Constructs a {@link Lexer} which will analyze a given {@link Reader}.
	 *
	 * @param r reader this lexer uses.
	 */
	public Lexer(Reader r) {
		this.r = r;

		trie = new ArrayMultiTrie();
		for (Keywords t : Keywords.ACTUAL_KEYWORDS) {
			trie.insert(t.getRegex());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<Token> iterator() {
		return new Iterator<Token>() {
			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean hasNext() {
				return Lexer.this.hasNext();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Token next() {
				try {
					return Lexer.this.next();
				} catch (IOException e) {
					return null;
				}
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void remove() {
				throw new UnsupportedOperationException("Not supported.");
			}
		};
	}

	/**
	 * Returns whether or not there is another {@link Token} in this
	 * {@link Lexer}
	 *
	 * @return {@code true} if there is, otherwise {@code false}
	 */
	public boolean hasNext() {
		try {
			return r.ready();
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * Returns the next {@link Token} within this {@link Lexer}
	 *
	 * @return the next Token
	 *
	 * @throws IOException when the Reader cannot be read or reset
	 */
	public Token next() throws IOException {
		char c;
		Token t = null;
		reader: while (r.ready()) {
			c = (char)r.read();

			if (Character.isWhitespace(c)) {
				// TODO uncomment this to print line separators in out stream when analyzing
				//if (c == '\n') {
				//	System.out.format("%n");
				//}

				continue;
			}

			t = null;
			if (Character.isLetter(c)) {
				StringBuilder tokenBuilder = new StringBuilder();
				tokenBuilder.append(c);

				while (r.ready()) {
					r.mark(1);
					c = (char)r.read();
					if (Character.isLetterOrDigit(c) || c == '_') {
						tokenBuilder.append(c);
					} else {
						r.reset();
						break;
					}
				}

				String token = tokenBuilder.toString();
				if (trie.contains(token)) {
					t = Keywords.valueOf('_' + token);
				} else if (token.matches(Keywords._boolconstant.getRegex())) {
					t = Keywords._boolconstant;
				} else {
					assert token.matches(Keywords._id.getRegex());
					trie.insert(token, IDENTIFIER_SENTINEL);
					t = Keywords._id;
				}

				return t;
			} else if (Character.isDigit(c)) {
				StringBuilder intBuilder = new StringBuilder();
				intBuilder.append(c);

				if (c == '0') {
					if (!r.ready()) {
						t = Keywords._intconstant;
						assert intBuilder.toString().matches(Keywords._intconstant.getRegex());
						return t;
					}

					r.mark(2);
					c = (char)r.read();
					if (c == 'x' || c == 'X') {
						if (!r.ready()) {
							r.reset();
							t = Keywords._intconstant;
							assert intBuilder.toString().matches(Keywords._intconstant.getRegex());
							return t;
						}

						StringBuilder hexBuilder = new StringBuilder(intBuilder);
						hexBuilder.append(c);

						c = (char)r.read();
						if (isHexDigit(c)) {
							t = Keywords._intconstant;
							hexBuilder.append(c);
						} else {
							r.reset();
							t = Keywords._intconstant;
							assert intBuilder.toString().matches(Keywords._intconstant.getRegex());
							return t;
						}

						while (r.ready()) {
							r.mark(1);
							c = (char)r.read();
							if (isHexDigit(c)) {
								hexBuilder.append(c);
							} else {
								r.reset();
								assert hexBuilder.toString().matches(Keywords._intconstant.getRegex());
								return t;
							}
						}

						assert hexBuilder.toString().matches(Keywords._intconstant.getRegex());
						return t;
					} else {
						r.reset();
						c = '0';
					}
				}

				if (Character.isDigit(c)) {
					t = Keywords._intconstant;
					intLoop: while (r.ready()) {
						r.mark(1);
						c = (char)r.read();
						if (Character.isDigit(c)) {
							intBuilder.append(c);
						} else {
							switch (c) {
								case '.':
									t = Keywords._doubleconstant;

									StringBuilder doubleBuilder = new StringBuilder(intBuilder);
									doubleBuilder.append(c);

									doubleLoop: while (r.ready()) {
										r.mark(3);
										c = (char)r.read();
										if (Character.isDigit(c)) {
											doubleBuilder.append(c);
										} else {
											switch (c) {
												case 'e':
												case 'E':
													if (!r.ready()) {
														r.reset();
														assert doubleBuilder.toString().matches(Keywords._doubleconstant.getRegex());
														return t;
													}

													StringBuilder expBuilder = new StringBuilder(doubleBuilder);
													expBuilder.append(c);

													c = (char)r.read();
													if (c == '+' || c == '-') {
														if (!r.ready()) {
															r.reset();
															assert doubleBuilder.toString().matches(Keywords._doubleconstant.getRegex());
															return t;
														}

														expBuilder.append(c);

														c = (char)r.read();
														if (Character.isDigit(c)) {
															expBuilder.append(c);
															expLoop: while (r.ready()) {
																r.mark(1);
																c = (char)r.read();
																if (Character.isDigit(c)) {
																	expBuilder.append(c);
																} else {
																	r.reset();
																	assert expBuilder.toString().matches(Keywords._doubleconstant.getRegex());
																	return t;
																}
															}
														} else {
															r.reset();
															assert doubleBuilder.toString().matches(Keywords._doubleconstant.getRegex());
															return t;
														}
													} else if (Character.isDigit(c)) {
														expBuilder.append(c);
														expLoop: while (r.ready()) {
															r.mark(1);
															c = (char)r.read();
															if (Character.isDigit(c)) {
																expBuilder.append(c);
															} else {
																r.reset();
																assert expBuilder.toString().matches(Keywords._doubleconstant.getRegex());
																return t;
															}
														}
													} else {
														r.reset();
														assert doubleBuilder.toString().matches(Keywords._doubleconstant.getRegex());
														return t;
													}

													break;
												default:
													r.reset();
													break doubleLoop;
											}
										}
									}

									break;
								default:
									r.reset();
									break intLoop;
							}
						}
					}

					assert intBuilder.toString().matches(Keywords._intconstant.getRegex());
					return t;
				}

				continue;
			} else if (c == '\"') {
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append(c);

				stringReader: while (r.ready()) {
					c = (char)r.read();
					switch (c) {
						case '\"':
							t = Keywords._stringconstant;
							stringBuilder.append(c);
							break stringReader;
						case '\n':
							t = null;
							break stringReader;
						default:
							stringBuilder.append(c);
							continue stringReader;
					}
				}

				if (t != null) {
					assert stringBuilder.toString().matches(Keywords._stringconstant.getRegex());
				}

				return t;
			}

			switch (c) {
				case '+':
					t = Keywords._plus;
					break;
				case '-':
					t = Keywords._minus;
					break;
				case '*':
					t = Keywords._multiplication;
					break;
				case '/':
					if (!r.ready()) {
						break;
					}

					r.mark(1);
					c = (char)r.read();
					switch (c) {
						case '/':
							while (r.ready()) {
								c = (char)r.read();
								if (c == '\n') {
									continue reader;
								}
							}

							break;
						case '*':
							while (r.ready()) {
								c = (char)r.read();
								if (c == '*') {
									if (r.ready()) {
										r.mark(1);
										c = (char)r.read();
										if (c == '/') {
											continue reader;
										}

										r.reset();
									}
								}
							}

							break;
						default:
							r.reset();
							t = Keywords._division;
					}

					break;
				case '<':
					if (!r.ready()) {
						break;
					}

					r.mark(1);
					c = (char)r.read();
					switch (c) {
						case '=':
							t = Keywords._lessequal;
							break;
						default:
							r.reset();
							t = Keywords._less;
					}

					break;
				case '>':
					if (!r.ready()) {
						break;
					}

					r.mark(1);
					c = (char)r.read();
					switch (c) {
						case '=':
							t = Keywords._greaterequal;
							break;
						default:
							r.reset();
							t = Keywords._greater;
					}

					break;
				case '=':
					if (!r.ready()) {
						break;
					}

					r.mark(1);
					c = (char)r.read();
					switch (c) {
						case '=':
							t = Keywords._equal;
							break;
						default:
							r.reset();
							t = Keywords._assignop;
					}

					break;
				case '!':
					if (!r.ready()) {
						break;
					}

					r.mark(1);
					c = (char)r.read();
					switch (c) {
						case '=':
							t = Keywords._notequal;
							break;
						default:
							r.reset();
							t = Keywords._not;
					}

					break;
				case '&':
					if (!r.ready()) {
						break;
					}

					r.mark(1);
					c = (char)r.read();
					switch (c) {
						case '&':
							t = Keywords._and;
							break;
						default:
							r.reset();
							t = null;
					}

					break;
				case '|':
					if (!r.ready()) {
						break;
					}

					r.mark(1);
					c = (char)r.read();
					switch (c) {
						case '|':
							t = Keywords._or;
							break;
						default:
							r.reset();
							t = null;
					}

					break;
				case ';':
					t = Keywords._semicolon;
					break;
				case ',':
					t = Keywords._comma;
					break;
				case '.':
					t = Keywords._period;
					break;
				case '(':
					t = Keywords._leftparen;
					break;
				case ')':
					t = Keywords._rightparen;
					break;
				case '[':
					t = Keywords._leftbrace;
					break;
				case ']':
					t = Keywords._rightbrace;
					break;
				case '{':
					t = Keywords._leftbracket;
					break;
				case '}':
					t = Keywords._rightbracket;
					break;
			}

			return t;
		}

		return null;
	}

	/**
	 * Returns the {@link MultiTrie} used to store {@link Token}s and
	 * identifiers.
	 *
	 * @return the MultiTrie used by this Lexer
	 */
	public MultiTrie getTrie() {
		return trie;
	}

	/**
	 * Returns whether or not a specified character is a hex character.
	 *
	 * E.g. {@code 0-9, a-f, A-F} are all valid hexadecimal characters.
	 *
	 * @param c character to check
	 * @return {@code true} if it is a hex character, otherwise {@code false}.
	 */
	private boolean isHexDigit(char c) {
		return Character.isDigit(c) || ('A' <= c && c <= 'F') || ('a' <= c && c <= 'f');
	}
}
