/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.csupomona.cs.cs411.project1;

import edu.csupomona.cs.cs411.project1.lexer.ToyKeywords;
import junit.framework.Assert;
import org.junit.Test;

/**
 * 
 * @author Collin Smith <collinsmith@csupomona.edu>
 */
public class TokensTest {

	public TokensTest() {
	}

	/**
	 * Test of values method, of class ToyKeywords.
	 */
	@Test
	public void testValues() {
	}

	/**
	 * Test of valueOf method, of class ToyKeywords.
	 */
	@Test
	public void testValueOf() {
	}

	/**
	 * Test of getRegex method, of class ToyKeywords.
	 */
	@Test
	public void testGetRegex() {
		System.out.println("token regexes");

		String regex = ToyKeywords._booleanliteral.getRegex();
		System.out.println("\t" + ToyKeywords._booleanliteral);
		Assert.assertTrue("true".matches(regex));
		Assert.assertTrue("false".matches(regex));
		Assert.assertFalse("nottrue".matches(regex));
		Assert.assertFalse("falses".matches(regex));

		regex = ToyKeywords._integerliteral.getRegex();
		System.out.println("\t" + ToyKeywords._integerliteral);
		Assert.assertTrue("8".matches(regex));
		Assert.assertTrue("012".matches(regex));
		Assert.assertTrue("0x0".matches(regex));
		Assert.assertTrue("0x12aE".matches(regex));
		Assert.assertFalse("".matches(regex));
		Assert.assertTrue("0".matches(regex));
		Assert.assertFalse("00x01".matches(regex));

		regex = ToyKeywords._doubleliteral.getRegex();
		System.out.println("\t" + ToyKeywords._doubleliteral);
		Assert.assertFalse(".12".matches(regex));
		Assert.assertTrue("0.12".matches(regex));
		Assert.assertTrue("12.".matches(regex));
		Assert.assertTrue("12.2E+2".matches(regex));
		Assert.assertFalse(".12E+2".matches(regex));
		Assert.assertFalse("12E+2".matches(regex));
		Assert.assertFalse("1.2E".matches(regex));
		Assert.assertTrue("12.E2".matches(regex));

		regex = ToyKeywords._stringliteral.getRegex();
		System.out.println("\t" + ToyKeywords._stringliteral);
		Assert.assertTrue("\"\"".matches(regex));
		Assert.assertTrue("\"regex\"".matches(regex));
		Assert.assertFalse("\"regex".matches(regex));
		Assert.assertFalse("regex\"".matches(regex));
		Assert.assertFalse("\"".matches(regex));

		regex = ToyKeywords._id.getRegex();
		System.out.println("\t" + ToyKeywords._id);
		Assert.assertTrue("regex".matches(regex));
		Assert.assertTrue("regex_".matches(regex));
		Assert.assertTrue("regex123".matches(regex));
		Assert.assertFalse("1regex".matches(regex));
		Assert.assertTrue("regex_123".matches(regex));
		Assert.assertTrue("ReGeX_123".matches(regex));
	}

}
