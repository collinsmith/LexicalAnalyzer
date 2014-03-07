package edu.csupomona.cs.cs411.project1.lexer;

import edu.csupomona.cs.cs411.project1.trie.ArrayMultiTrie;
import edu.csupomona.cs.cs411.project1.trie.MultiTrie;
import java.io.IOException;
import java.io.Reader;

/**
 * This class represents a lexical analyzer that can be used for the Toy
 * language.
 *
 * @author Collin Smith <collinsmith@csupomona.edu>
 */
public class ToyLexer extends AbstractLexer {
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
	 * Constructs a {@link Lexer} for the Toy Programming Language.
	 */
	public ToyLexer() {
		trie = new ArrayMultiTrie();
		for (ToyKeywords t : ToyKeywords.ACTUAL_KEYWORDS) {
			trie.insert(t.getRegex());
		}
	}

	/**
	 * Returns the next {@link Token} within this {@link ToyLexer}
	 *
	 * @param r the reader requesting to be analyzed
	 * @return the next Token in the given Reader, or {@code null} if stream
	 *	is empty or has encountered an invalid token.
	 */
	@Override
	public Token next(Reader r) {
		char c = (char)-1;
		try {
			Token t = null;
			reader: while (r.ready()) {
				c = (char)r.read();

				if ((int)c == -1) {
					return ToyKeywords._EOF;
				}

				if (Character.isWhitespace(c)) {
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
						t = ToyKeywords.valueOf('_' + token);
					} else if (token.matches(ToyKeywords._booleanliteral.getRegex())) {
						t = ToyKeywords._booleanliteral;
					} else {
						assert token.matches(ToyKeywords._id.getRegex());
						trie.insert(token, IDENTIFIER_SENTINEL);
						t = ToyKeywords._id;
					}

					return t;
				} else if (Character.isDigit(c)) {
					StringBuilder intBuilder = new StringBuilder();
					intBuilder.append(c);

					if (c == '0') {
						if (!r.ready()) {
							t = ToyKeywords._integerliteral;
							assert intBuilder.toString().matches(t.getRegex());
							return t;
						}

						r.mark(2);
						c = (char)r.read();
						if (c == 'x' || c == 'X') {
							if (!r.ready()) {
								r.reset();
								t = ToyKeywords._integerliteral;
								assert intBuilder.toString().matches(t.getRegex());
								return t;
							}

							StringBuilder hexBuilder = new StringBuilder(intBuilder);
							hexBuilder.append(c);

							c = (char)r.read();
							if (isHexDigit(c)) {
								t = ToyKeywords._integerliteral;
								hexBuilder.append(c);
							} else {
								r.reset();
								t = ToyKeywords._integerliteral;
								assert intBuilder.toString().matches(t.getRegex());
								return t;
							}

							while (r.ready()) {
								r.mark(1);
								c = (char)r.read();
								if (isHexDigit(c)) {
									hexBuilder.append(c);
								} else {
									r.reset();
									assert hexBuilder.toString().matches(t.getRegex());
									return t;
								}
							}

							assert hexBuilder.toString().matches(t.getRegex());
							return t;
						} else {
							r.reset();
							c = '0';
						}
					}

					if (Character.isDigit(c)) {
						t = ToyKeywords._integerliteral;
						intLoop: while (r.ready()) {
							r.mark(1);
							c = (char)r.read();
							if (Character.isDigit(c)) {
								intBuilder.append(c);
							} else {
								switch (c) {
									case '.':
										t = ToyKeywords._doubleliteral;

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
															assert doubleBuilder.toString().matches(t.getRegex());
															return t;
														}

														StringBuilder expBuilder = new StringBuilder(doubleBuilder);
														expBuilder.append(c);

														c = (char)r.read();
														if (c == '+' || c == '-') {
															if (!r.ready()) {
																r.reset();
																assert doubleBuilder.toString().matches(t.getRegex());
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
																		assert expBuilder.toString().matches(t.getRegex());
																		return t;
																	}
																}
															} else {
																r.reset();
																assert doubleBuilder.toString().matches(t.getRegex());
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
																	assert expBuilder.toString().matches(t.getRegex());
																	return t;
																}
															}
														} else {
															r.reset();
															assert doubleBuilder.toString().matches(t.getRegex());
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

						assert intBuilder.toString().matches(t.getRegex());
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
								t = ToyKeywords._stringliteral;
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
						assert stringBuilder.toString().matches(t.getRegex());
					}

					return t;
				}

				switch (c) {
					case '+':
						t = ToyKeywords._plus;
						break;
					case '-':
						t = ToyKeywords._minus;
						break;
					case '*':
						t = ToyKeywords._multiplication;
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
								t = ToyKeywords._division;
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
								t = ToyKeywords._lessequal;
								break;
							default:
								r.reset();
								t = ToyKeywords._less;
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
								t = ToyKeywords._greaterequal;
								break;
							default:
								r.reset();
								t = ToyKeywords._greater;
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
								t = ToyKeywords._equal;
								break;
							default:
								r.reset();
								t = ToyKeywords._assignop;
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
								t = ToyKeywords._notequal;
								break;
							default:
								r.reset();
								t = ToyKeywords._not;
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
								t = ToyKeywords._and;
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
								t = ToyKeywords._or;
								break;
							default:
								r.reset();
								t = null;
						}

						break;
					case ';':
						t = ToyKeywords._semicolon;
						break;
					case ',':
						t = ToyKeywords._comma;
						break;
					case '.':
						t = ToyKeywords._period;
						break;
					case '(':
						t = ToyKeywords._leftparen;
						break;
					case ')':
						t = ToyKeywords._rightparen;
						break;
					case '[':
						t = ToyKeywords._leftbracket;
						break;
					case ']':
						t = ToyKeywords._rightbracket;
						break;
					case '{':
						t = ToyKeywords._leftbrace;
						break;
					case '}':
						t = ToyKeywords._rightbrace;
						break;
				}

				return t;
			}
		} catch (IOException e) {
			if ((int)c == -1) {
				return ToyKeywords._EOF;
			}
		}

		return null;
	}

	/**
	 * Returns the {@link MultiTrie} used to store {@link Token}s and
	 * identifiers.
	 *
	 * @return the MultiTrie used by this ToyLexer
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
