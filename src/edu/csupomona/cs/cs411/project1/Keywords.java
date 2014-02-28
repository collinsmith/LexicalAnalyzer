package edu.csupomona.cs.cs411.project1;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * {@link Enum} representation for a set of {@link Token}s which exist within
 * the Toy language.
 *
 * @author Collin Smith <collinsmith@csupomona.edu>
 */
public enum Keywords implements Token {
	// Keywords
	_bool("bool"),
	_break("break"),
	_class("class"),
	_double("double"),
	_else("else"),
	_extends("extends"),
	_for("for"),
	_if("if"),
	_implements("implements"),
	_int("int"),
	_interface("interface"),
	_newarray("newarray"),
	_println("println"),
	_readln("readln"),
	_return("return"),
	_string("string"),
	_void("void"),
	_while("while"),

	// Operators & Punctuation
	_plus("+"),
	_minus("-"),
	_multiplication("*"),
	_division("/"),
	_less("<"),
	_lessequal("<="),
	_greater(">"),
	_greaterequal(">="),
	_equal("=="),
	_notequal("!="),
	_and("&&"),
	_or("||"),
	_not("!"),
	_assignop("="),
	_semicolon(";"),
	_comma(","),
	_period("."),
	_leftparen("("),
	_rightparen(")"),
	_leftbracket("["),
	_rightbracket("]"),
	_leftbrace("{"),
	_rightbrace("}"),

	// Constants
	_booleanliteral("(true|false)"),
	_integerliteral("[+-]?(([0-9]+)|(0(x|X)[a-fA-F0-9]+))"),
	_doubleliteral("[0-9]+\\.[0-9]*((e|E)[+-]?[0-9]+)?"),
	_stringliteral("\".*\""),

	// Other Identifiers
	_id("([a-zA-Z][a-zA-Z0-9_]*)");

	/**
	 * Subset of {@link Keywords} which do not contain any symbols or
	 * operators and should be used to compare literally.
	 */
	public static final Set<Keywords> ACTUAL_KEYWORDS;
	static {
		ACTUAL_KEYWORDS = Collections.unmodifiableSet(EnumSet.range(_bool, _while));
	}

	/**
	 * Subset of {@link Keywords} which are used as operators. These operators
	 * cannot be used in regular expression matches due to the fact that their
	 * definitions conflict with those of regular expressions.
	 */
	public static final Set<Keywords> OPERATORS;
	static {
		OPERATORS = Collections.unmodifiableSet(EnumSet.range(_plus, _rightbrace));
	}

	/**
	 * Regex representing Strings of this {@link Token}
	 */
	private final String regex;

	/**
	 * Constructs a {@link Token} the specified regex representing it.
	 *
	 * @param regex regex representing this Keyword.Token
	 */
	private Keywords(String regex) {
		this.regex = regex;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getRegex() {
		return regex;
	}
}
