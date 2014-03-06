package edu.csupomona.cs.cs411.project1.lexer;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * {@link Enum} representation for a set of {@link Token}s which exist within
 * the Toy language.
 *
 * @author Collin Smith <collinsmith@csupomona.edu>
 */
public enum ToyKeywords implements Token {
	// Special EOF token to mark end of file
	_EOF("$", false),
	
	// ToyKeywords
	_bool("bool", false),
	_break("break", false),
	_class("class", false),
	_double("double", false),
	_else("else", false),
	_extends("extends", false),
	_for("for", false),
	_if("if", false),
	_implements("implements", false),
	_int("int", false),
	_interface("interface", false),
	_newarray("newarray", false),
	_println("println", false),
	_readln("readln", false),
	_return("return", false),
	_string("string", false),
	_void("void", false),
	_while("while", false),

	// Operators & Punctuation
	_plus("+", false),
	_minus("-", false),
	_multiplication("*", false),
	_division("/", false),
	_less("<", false),
	_lessequal("<=", false),
	_greater(">", false),
	_greaterequal(">=", false),
	_equal("==", false),
	_notequal("!=", false),
	_and("&&", false),
	_or("||", false),
	_not("!", false),
	_assignop("=", false),
	_semicolon(";", false),
	_comma(",", false),
	_period(".", false),
	_leftparen("(", false),
	_rightparen(")", false),
	_leftbracket("[", false),
	_rightbracket("]", false),
	_leftbrace("{", false),
	_rightbrace("}", false),

	// Constants
	_booleanliteral("(true|false)", true),
	_integerliteral("[+-]?(([0-9]+)|(0(x|X)[a-fA-F0-9]+))", true),
	_doubleliteral("[0-9]+\\.[0-9]*((e|E)[+-]?[0-9]+)?", true),
	_stringliteral("\".*\"", true),

	// Other Identifiers
	_id("([a-zA-Z][a-zA-Z0-9_]*)", true);

	/**
	 * Subset of {@link ToyKeywords} which do not contain any symbols or
	 * operators and should be used to compare literally.
	 */
	public static final Set<ToyKeywords> ACTUAL_KEYWORDS;
	static {
		ACTUAL_KEYWORDS = Collections.unmodifiableSet(EnumSet.range(_bool, _while));
	}

	/**
	 * Subset of {@link ToyKeywords} which are used as operators. These operators
	 * cannot be used in regular expression matches due to the fact that their
	 * definitions conflict with those of regular expressions.
	 */
	public static final Set<ToyKeywords> OPERATORS;
	static {
		OPERATORS = Collections.unmodifiableSet(EnumSet.range(_plus, _rightbrace));
	}

	/**
	 * Regex representing Strings of this {@link Token}
	 */
	private final String REGEX;

	/**
	 * Whether or not the value of this {@link Token} should be treated as a
	 * regular expression or literal value
	 */
	private final Boolean IS_REGULAR_EXPRESSION;

	/**
	 * Constructs a {@link Token} the specified REGEX representing it.
	 *
	 * @param regex REGEX representing this Keyword.Token
	 * @param isRegex {@code true} if this token is represented by a regex
	 */
	private ToyKeywords(String regex, boolean isRegex) {
		this.REGEX = regex;
		this.IS_REGULAR_EXPRESSION = isRegex;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getRegex() {
		return REGEX;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isRegex() {
		return IS_REGULAR_EXPRESSION;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getId() {
		return ordinal();
	}
}
