/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies, Polarion Software and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.team.svn.core.discovery.util;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * A default implementation of an error handler that throws exceptions on all errors.
 * 
 * @author David Green
 * @author Igor Burilo
 */
public class DefaultSaxErrorHandler implements ErrorHandler {
	public void warning(SAXParseException exception) throws SAXException {
		// ignore
	}

	public void fatalError(SAXParseException exception) throws SAXException {
		throw exception;
	}

	public void error(SAXParseException exception) throws SAXException {
		throw exception;
	}
}