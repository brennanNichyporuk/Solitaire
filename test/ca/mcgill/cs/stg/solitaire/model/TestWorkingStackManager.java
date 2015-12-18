/*******************************************************************************
 * Solitaire
 *
 * Copyright (C) 2016 by Martin P. Robillard
 *
 * See: https://github.com/prmr/Solitaire
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
 *******************************************************************************/
package ca.mcgill.cs.stg.solitaire.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import ca.mcgill.cs.stg.solitaire.cards.Card;
import ca.mcgill.cs.stg.solitaire.cards.Card.Rank;
import ca.mcgill.cs.stg.solitaire.cards.Card.Suit;
import ca.mcgill.cs.stg.solitaire.model.GameModel.StackIndex;
import ca.mcgill.cs.stg.solitaire.cards.Deck;

public class TestWorkingStackManager
{
	private WorkingStackManager aWorkingStackManager = new WorkingStackManager();
	private static final Card CKC = Card.get(Rank.KING, Suit.CLUBS);
	private static final Card CAC = Card.get(Rank.ACE, Suit.CLUBS);
	private static final Card C5D = Card.get(Rank.FIVE, Suit.DIAMONDS);
	private static final Card C4D = Card.get(Rank.FOUR, Suit.DIAMONDS);
	private static final Card C4C = Card.get(Rank.FOUR, Suit.CLUBS);
	private static final Card C4S = Card.get(Rank.FOUR, Suit.SPADES);
	private static final Card C4H = Card.get(Rank.FOUR, Suit.HEARTS);
	private static final Card C3C = Card.get(Rank.THREE, Suit.CLUBS);
	private static final Card C3H = Card.get(Rank.THREE, Suit.HEARTS);
	
	@Test
	public void testInitialize()
	{
		for( StackIndex index : StackIndex.values())
		{
			assertEquals(0, aWorkingStackManager.getStack(index).length);
		}
		Deck deck = new Deck();
		aWorkingStackManager.initialize(deck);
		assertEquals(1,aWorkingStackManager.getStack(StackIndex.FIRST).length);
		assertEquals(2,aWorkingStackManager.getStack(StackIndex.SECOND).length);
		assertEquals(3,aWorkingStackManager.getStack(StackIndex.THIRD).length);
		assertEquals(4,aWorkingStackManager.getStack(StackIndex.FOURTH).length);
		assertEquals(5,aWorkingStackManager.getStack(StackIndex.FIFTH).length);
		assertEquals(6,aWorkingStackManager.getStack(StackIndex.SIXTH).length);
		assertEquals(7,aWorkingStackManager.getStack(StackIndex.SEVENTH).length);
		assertEquals(24, deck.size());
		deck.shuffle();
		aWorkingStackManager.initialize(deck);
		assertEquals(1,aWorkingStackManager.getStack(StackIndex.FIRST).length);
		assertEquals(2,aWorkingStackManager.getStack(StackIndex.SECOND).length);
		assertEquals(3,aWorkingStackManager.getStack(StackIndex.THIRD).length);
		assertEquals(4,aWorkingStackManager.getStack(StackIndex.FOURTH).length);
		assertEquals(5,aWorkingStackManager.getStack(StackIndex.FIFTH).length);
		assertEquals(6,aWorkingStackManager.getStack(StackIndex.SIXTH).length);
		assertEquals(7,aWorkingStackManager.getStack(StackIndex.SEVENTH).length);
		assertEquals(24, deck.size());
	}
	
	@Test
	public void testCanMoveTo()
	{
		assertFalse(aWorkingStackManager.canMoveTo(CAC, StackIndex.FIRST)); 
		assertTrue(aWorkingStackManager.canMoveTo(CKC, StackIndex.FIRST)); 
		aWorkingStackManager.push(C5D, StackIndex.FIRST);
		assertFalse(aWorkingStackManager.canMoveTo(CAC, StackIndex.FIRST));
		assertFalse(aWorkingStackManager.canMoveTo(C4D, StackIndex.FIRST));
		assertFalse(aWorkingStackManager.canMoveTo(C4H, StackIndex.FIRST));
		assertTrue(aWorkingStackManager.canMoveTo(C4C, StackIndex.FIRST));
		assertTrue(aWorkingStackManager.canMoveTo(C4S, StackIndex.FIRST));
	}
	
	@Test
	public void testGetSequence()
	{
		aWorkingStackManager.push(C5D, StackIndex.SECOND);
		Card[] sequence = aWorkingStackManager.getSequence(C5D, StackIndex.SECOND);
		assertEquals(1, sequence.length);
		assertEquals(C5D, sequence[0]);
		aWorkingStackManager.push(C4C, StackIndex.SECOND);
		sequence = aWorkingStackManager.getSequence(C5D, StackIndex.SECOND);
		assertEquals(2, sequence.length);
		assertEquals(C5D, sequence[0]);
		assertEquals(C4C, sequence[1]);
		sequence = aWorkingStackManager.getSequence(C4C, StackIndex.SECOND);
		assertEquals(1, sequence.length);
		assertEquals(C4C, sequence[0]);
	}
	
	@Test
	public void testIsInStacks()
	{
		assertFalse(aWorkingStackManager.isInStacks(CAC));
		aWorkingStackManager.push(C5D, StackIndex.SECOND);
		assertFalse(aWorkingStackManager.isInStacks(CAC));
		assertTrue(aWorkingStackManager.isInStacks(C5D));
	}
	
	@Test
	public void testPop()
	{
		aWorkingStackManager.pop(C5D); // Test absence of crash, no oracle
		aWorkingStackManager.push(C5D, StackIndex.SECOND);
		aWorkingStackManager.pop(C5D);
		CardView[] stack = aWorkingStackManager.getStack(StackIndex.SECOND);
		assertEquals(0, stack.length);
		aWorkingStackManager.push(CAC, StackIndex.FIRST);
		aWorkingStackManager.push(C5D, StackIndex.SECOND);
		aWorkingStackManager.push(C4C, StackIndex.SECOND);
		aWorkingStackManager.push(C3H, StackIndex.SECOND);
		aWorkingStackManager.pop(C3H);
		stack = aWorkingStackManager.getStack(StackIndex.SECOND);
		assertEquals(2, stack.length);
		assertEquals(stack[1].getCard(), C4C);
		assertEquals(stack[0].getCard(), C5D);
		aWorkingStackManager.pop(C4C);
		stack = aWorkingStackManager.getStack(StackIndex.SECOND);
		assertEquals(1, stack.length);
		assertEquals(stack[0].getCard(), C5D);
		aWorkingStackManager.pop(C5D);
		stack = aWorkingStackManager.getStack(StackIndex.SECOND);
		assertEquals(0, stack.length);
	}
	
}
