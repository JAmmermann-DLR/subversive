/*******************************************************************************
 * Copyright (c) 2005-2008 Polarion Software.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Alexander Gurov - Initial API and implementation
 *******************************************************************************/

package org.eclipse.team.svn.ui.action;

import org.eclipse.team.svn.ui.utility.IOperationWrapperFactory;
import org.eclipse.team.svn.ui.utility.WorkspaceModifyOperationWrapperFactory;

/**
 * Abstract UI repository action that is not indifferent to workspace modifications
 * 
 * @author Alexander Gurov
 */
public abstract class AbstractRepositoryModifyWorkspaceAction extends AbstractRepositoryTeamAction {

	public AbstractRepositoryModifyWorkspaceAction() {
		super();
	}

	protected IOperationWrapperFactory getOperationWrapperFactory() {
		return new WorkspaceModifyOperationWrapperFactory();
	}

}
