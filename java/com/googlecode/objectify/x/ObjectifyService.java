/*
 */

package com.googlecode.objectify.x;

import java.util.ArrayDeque;
import java.util.Deque;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;

/**
 *
 * @author Jeff Schnitzer
 */
public class ObjectifyService
{
	/** */
	private static ObjectifyFactory factory = new ObjectifyFactory();

	/** */
	public static void setFactory(ObjectifyFactory fact) {
		factory = fact;
	}

	/**
	 * Thread local stack of Objectify instances corresponding to transaction depth
	 */
	private static final ThreadLocal<Deque<Objectify>> STACK = new ThreadLocal<Deque<Objectify>>() {
		@Override
		protected Deque<Objectify> initialValue() {
			return new ArrayDeque<Objectify>();
		}
	};

	/**
	 * The method to call at any time to get the current Objectify, which may change depending on txn context
	 */
	public static Objectify ofy() {
		Deque<Objectify> stack = STACK.get();
		if (stack.isEmpty())
			stack.add(factory.begin());

		return stack.getLast();
	}

	/**
	 * @return the factory
	 */
	public static ObjectifyFactory fact() {
		return factory;
	}

	/** Pushes new context onto stack when a transaction starts */
	public static void push(Objectify ofy) {
		STACK.get().add(ofy);
	}

	/** Pops context off of stack after a transaction completes */
	public static void pop() {
		STACK.get().removeLast();
	}

	/** Clear the stack of any leftover Objectify instances */
	public static void reset() {
		STACK.get().clear();
	}
}