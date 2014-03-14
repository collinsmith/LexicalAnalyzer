package edu.csupomona.cs.cs411.project1.lexer;

import com.google.common.base.Preconditions;
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
	 * @return the next Token in the given Reader, {@link ToyKeywords#_EOF} if
	 *	the end of file has been reached, or {@code null} if the next token
	 *	is invalid.
	 */
	@Override
	public Token next(Reader r) {
		Preconditions.checkArgument(r.markSupported(), "Reader must support marking for efficient analyzing!");

		//Token t;
		int i = -1;
		try {
			//char c;
			Reader:
			while (r.ready()) {
				i = r.read();
				if (i == -1) {
					break Reader;
				}

				if (Character.isWhitespace(i)) {
					continue Reader;
				}

				if (Character.isLetter(i)) {
					// Attempt to read an identifier (or similar)
					StringBuilder idBuilder = new StringBuilder();
					idBuilder.appendCodePoint(i);
					while (r.ready()) {
						// Only append while we are not blocked
						r.mark(1);
						i = r.read();
						if (Character.isLetterOrDigit(i) || i == '_') {
							// Continually read remaining id characters
							idBuilder.appendCodePoint(i);
							continue;
						}

						r.reset();
						break;
					}

					// Check if the "id" is really a keyword or boolean literal
					String id = idBuilder.toString();
					if (trie.contains(id)) {
						return ToyKeywords.valueOf('_' + id);
					} else if (id.matches(ToyKeywords._booleanliteral.getRegex())) {
						return ToyKeywords._booleanliteral;
					}

					// What we have should be a valid identifier, so return it
					assert id.matches(ToyKeywords._id.getRegex());
					trie.insert(id, IDENTIFIER_SENTINEL);
					return ToyKeywords._id;
				} else if (Character.isDigit(i)) {
					// Attempt to read a number (int or double)
					StringBuilder intBuilder = new StringBuilder();
					intBuilder.appendCodePoint(i);

					// Check if the number is in hexadecimal
					if (i == '0') {
						// If our initial character is 0, assume hex
						if (!r.ready()) {
							// Because we cannot confirm that this is in hex, and we cannot read more, return 0 as an integer literal
							assert intBuilder.toString().matches(ToyKeywords._integerliteral.getRegex());
							return ToyKeywords._integerliteral;
						}

						// We need at least 2 lookahead reads to see if this is a valid hex number
						r.mark(2);
						i = r.read();
						if (i == 'x' || i == 'X') {
							if (!r.ready()) {
								// We cannot confirm that it is in hex, so return 0 as an integer literal and unread 'x' or 'X'
								r.reset();
								assert intBuilder.toString().matches(ToyKeywords._integerliteral.getRegex());
								return ToyKeywords._integerliteral;
							}

							// Confirm that the next character is a hex character so we have a confirmed hex starter
							StringBuilder hexBuilder = new StringBuilder(intBuilder);
							hexBuilder.appendCodePoint(i);
							i = r.read();
							if (isHexDigit(i)) {
								// We have a hexadecimal number
								hexBuilder.appendCodePoint(i);
								while (r.ready()) {
									// Only append while we are not blocked
									r.mark(1);
									i = r.read();
									if (isHexDigit(i)) {
										// Continually read remaining hexadecimal digits
										hexBuilder.appendCodePoint(i);
										continue;
									}

									r.reset();
									break;
								}

								// We should have a valid hexadecimal integer, so return it
								assert hexBuilder.toString().matches(ToyKeywords._integerliteral.getRegex());
								return ToyKeywords._integerliteral;
							}

							// We only had "0x", so interpret this as a 0 integer literal and unread previous 2 characters
							r.reset();
							assert intBuilder.toString().matches(ToyKeywords._integerliteral.getRegex());
							return ToyKeywords._integerliteral;
						}

						// We did not encounter an 'x' or 'X', so this is not a hexadecimal number
						r.reset();
					}

					// It was not in hex, check if we have a regular integer or double
					Integer_Loop:
					while (r.ready()) {
						r.mark(1);
						i = r.read();
						if (Character.isDigit(i)) {
							// Continuously append integer numbers
							intBuilder.appendCodePoint(i);
							continue Integer_Loop;
						}

						// We have an interger, now see if we really have a double
						Double_Switch:
						switch (i) {
							case '.':
								// We might have a double, so append the '.' and build it up
								StringBuilder doubleBuilder = new StringBuilder(intBuilder);
								doubleBuilder.appendCodePoint(i);

								Double_Loop:
								while (r.ready()) {
									// We need at least 3 lookahead reads to know if we have a double in exponential form
									r.mark(3);
									i = r.read();
									if (Character.isDigit(i)) {
										// Continuously append more integers to build up our double
										doubleBuilder.appendCodePoint(i);
										continue Double_Loop;
									}

									// We have a double, now check if we really have an exponent
									Exponent_Switch:
									switch (i) {
										case 'e':
										case 'E':
											// We really have an exponent, so append the 'e' or 'E' and build it up
											if (!r.ready()) {
												// We don't have an exponent, so unread the 'e/E' and return the double
												r.reset();
												break Double_Loop;
											}

											StringBuilder expBuilder = new StringBuilder(doubleBuilder);
											expBuilder.appendCodePoint(i);
											i = r.read();
											switch (i) {
												case '+':
												case '-':
													// We have a '+/-', but we need another integer afterwards
													if (!r.ready()) {
														// We were blocked, so return only the double and unread the 'e/E' and '+/-'
														r.reset();
														break Double_Loop;
													}

													// Read the character afterwards and let the fallthrough check whether it is a digit
													expBuilder.appendCodePoint(i);
													i = r.read();
												default:
													if (!Character.isDigit(i)) {
														// The current read is not a digit, unread whatever we read and return the double
														r.reset();
														break Double_Loop;
													}

													// We have a valid exponent
													expBuilder.appendCodePoint(i);
													Exponent_Loop:
													while (r.ready()) {
														r.mark(1);
														i = r.read();
														if (Character.isDigit(i)) {
															// Continuously append integers to build it up
															expBuilder.appendCodePoint(i);
															continue Exponent_Loop;
														}

														r.reset();
														break Exponent_Loop;
													}

													// We cannot perform any more read operations, but we have read an exponent so far
													assert expBuilder.toString().matches(ToyKeywords._doubleliteral.getRegex());
													return ToyKeywords._doubleliteral;
											}
										default:
											// We don't have an exponent, so unread the last few characters and return the double
											r.reset();
											break Double_Loop;
									}
								}

								// We cannot perform any more read operations, but we have read a double so far
								assert doubleBuilder.toString().matches(ToyKeywords._doubleliteral.getRegex());
								return ToyKeywords._doubleliteral;
							default:
								// We don't have a double, so unread the last character and return the integer
								r.reset();
								break Integer_Loop;
						}
					}

					// We cannot perform any more read operations from the initial integer check, so return the interger literal we parsed
					assert intBuilder.toString().matches(ToyKeywords._integerliteral.getRegex());
					return ToyKeywords._integerliteral;
				}

				switch (i) {
					case '\"':
						StringBuilder stringBuilder = new StringBuilder();
						String_Loop:
						while (r.ready()) {
							i = r.read();
							switch (i) {
								case '\"':
									break String_Loop;
								/*case '\r':
									r.mark(1);
									i = r.read();
									if (i != '\n') {
										r.reset();
									}*/
								case '\n':
									return null;
								default:
									stringBuilder.appendCodePoint(i);
							}
						}

						// We cannot perform any more read operations, but we have a string literal
						assert stringBuilder.toString().matches(ToyKeywords._stringliteral.getRegex());
						return ToyKeywords._stringliteral;
					case '/':
						if (!r.ready()) {
							return ToyKeywords._division;
						}

						r.mark(1);
						i = r.read();
						switch (i) {
							case '/':
								// There is no ready check here because we need to read until EOL or EOF else error
								while (i != '\n' && i != -1) {
									i = r.read();
								}

								return next(r);
							case '*':
								// There is no ready check here because we need to read until "*/" or EOF else error
								Block_Comment_Loop:
								while (i != -1) {
									i = r.read();
									while (i == '*') {
										i = r.read();
										if (i == '/') {
											break Block_Comment_Loop;
										}
									}
								}

								return next(r);
							default:
								r.reset();
								return ToyKeywords._division;
						}
					case '<':
						if (!r.ready()) {
							return ToyKeywords._less;
						}

						r.mark(1);
						i = r.read();
						switch (i) {
							case '=': return ToyKeywords._lessequal;
							default:
								r.reset();
								return ToyKeywords._less;
						}
					case '>':
						if (!r.ready()) {
							return ToyKeywords._less;
						}

						r.mark(1);
						i = r.read();
						switch (i) {
							case '=': return ToyKeywords._greaterequal;
							default:
								r.reset();
								return ToyKeywords._greater;
						}
					case '=':
						if (!r.ready()) {
							return ToyKeywords._assignop;
						}

						r.mark(1);
						i = r.read();
						switch (i) {
							case '=': return ToyKeywords._equal;
							default:
								r.reset();
								return ToyKeywords._assignop;
						}
					case '!':
						if (!r.ready()) {
							return ToyKeywords._not;
						}

						r.mark(1);
						i = r.read();
						switch (i) {
							case '=': return ToyKeywords._notequal;
							default:
								r.reset();
								return ToyKeywords._not;
						}
					case '&':
						if (!r.ready()) {
							return null;
						}

						r.mark(1);
						i = r.read();
						switch (i) {
							case '&': return ToyKeywords._and;
							default:
								r.reset();
								return null;
						}
					case '|':
						if (!r.ready()) {
							return null;
						}

						r.mark(1);
						i = r.read();
						switch (i) {
							case '|': return ToyKeywords._or;
							default:
								r.reset();
								return null;
						}
					case '+': return ToyKeywords._plus;
					case '-': return ToyKeywords._minus;
					case '*': return ToyKeywords._multiplication;
					case ';': return ToyKeywords._semicolon;
					case ',': return ToyKeywords._comma;
					case '.': return ToyKeywords._period;
					case '(': return ToyKeywords._leftparen;
					case ')': return ToyKeywords._rightparen;
					case '[': return ToyKeywords._leftbracket;
					case ']': return ToyKeywords._rightbracket;
					case '{': return ToyKeywords._leftbrace;
					case '}': return ToyKeywords._rightbrace;
				}
			}
		} catch (IOException e) {
		} finally {
			if (i == -1) {
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
		return isHexDigit((int)c);
	}

	private boolean isHexDigit(int codePoint) {
		return Character.isDigit(codePoint) || ('A' <= codePoint && codePoint <= 'F') || ('a' <= codePoint && codePoint <= 'f');
	}
}
