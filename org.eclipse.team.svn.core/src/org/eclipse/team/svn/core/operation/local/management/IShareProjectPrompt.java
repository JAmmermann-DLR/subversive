/*******************************************************************************
 * Copyright (c) 2005-2008 Polarion Software.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Sergiy Logvin (Polarion Software) - initial API and implementation
 *******************************************************************************/

package org.eclipse.team.svn.core.operation.local.management;

import org.eclipse.core.resources.IProject;

/**
 * Prompt to share project to remote folder which is not empty
 * 
 * @author Sergiy Logvin
 */
public interface IShareProjectPrompt {
	public boolean prompt(IProject []projects);
}
