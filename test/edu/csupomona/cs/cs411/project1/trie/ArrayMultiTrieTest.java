/*
 * Copyright (C) 2014 Collin Smith <collinsmith@csupomona.edu>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package edu.csupomona.cs.cs411.project1.trie;

import junit.framework.Assert;
import org.junit.Test;

/**
 *
 * @author Collin Smith <collinsmith@csupomona.edu>
 */
public class ArrayMultiTrieTest {

	public ArrayMultiTrieTest() {
	}

	/**
	 * Test of insert method, of class DoubleArrayTrie.
	 */
	@Test
	public void testInsert_String() {
		System.out.println("insert");
		ArrayMultiTrie instance = new ArrayMultiTrie();
		instance.insert("notequal");
		instance.insert("not");
		instance.insert("note");
		instance.insert("notequals");
		Assert.assertEquals(4, instance.numKeys());
	}

	@Test
	public void testInsert_String_char() {
	}

	@Test
	public void testContains_String() {
		System.out.println("contains");
		ArrayMultiTrie instance = new ArrayMultiTrie();
		instance.insert("notequal");
		instance.insert("not");
		instance.insert("int");
		instance.insert("interface");
		instance.insert("i");
		instance.insert("a");
		instance.insert("asdf");

		Assert.assertEquals(true, instance.contains("notequal"));
		Assert.assertEquals(true, instance.contains("not"));
		Assert.assertEquals(false, instance.contains("note"));
		Assert.assertEquals(false, instance.contains("notequals"));
		Assert.assertEquals(true, instance.contains("int"));
		Assert.assertEquals(true, instance.contains("interface"));
		Assert.assertEquals(true, instance.contains("i"));
		Assert.assertEquals(true, instance.contains("a"));
		Assert.assertEquals(true, instance.contains("asdf"));
	}

	@Test
	public void testContains_String_char() {
	}

	@Test
	public void testNumKeys() {
	}

}
