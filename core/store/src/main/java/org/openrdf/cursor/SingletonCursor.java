/*
 * Copyright Aduna (http://www.aduna-software.com/) (c) 2008.
 *
 * Licensed under the Aduna BSD-style license.
 */
package org.openrdf.cursor;

import org.openrdf.store.StoreException;

/**
 * @author James Leigh
 */
public class SingletonCursor<E> implements Cursor<E> {

	private E element;

	public SingletonCursor(E element) {
		this.element = element;
	}

	public E next()
		throws StoreException
	{
		E next = element;
		element = null;
		return next;
	}

	public void close()
		throws StoreException
	{
		element = null;
	}

	@Override
	public String toString() {
		return element.toString();
	}
}