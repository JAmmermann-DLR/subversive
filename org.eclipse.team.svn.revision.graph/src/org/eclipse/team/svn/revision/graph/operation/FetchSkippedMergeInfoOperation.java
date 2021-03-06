/*******************************************************************************
 * Copyright (c) 2005-2010 Polarion Software.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Igor Burilo (Polarion Software) - initial API and implementation
 *******************************************************************************/
package org.eclipse.team.svn.revision.graph.operation;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.team.svn.core.connector.ISVNConnector;
import org.eclipse.team.svn.core.resource.IRepositoryResource;
import org.eclipse.team.svn.revision.graph.cache.RepositoryCache;

/**
 * Fetch previously skipped merge info
 * 
 * @author Igor Burilo
 */
public class FetchSkippedMergeInfoOperation extends BaseFetchOperation {

	public FetchSkippedMergeInfoOperation(IRepositoryResource resource, RepositoryCache repositoryCache, boolean isSkipFetchErrors) {		
		super("Operation_FetchSkippedMergeInfo", resource, repositoryCache, isSkipFetchErrors);	 //$NON-NLS-1$
	}

	@Override
	protected void prepareData(IProgressMonitor monitor) throws Exception {		
		long startRevision = this.repositoryCache.getCacheInfo().getMergeStartSkippedRevision();
		long endRevision = this.repositoryCache.getCacheInfo().getMergeEndSkippedRevision();
		this.canRun = startRevision != 0;
		if (this.canRun) {			
			this.logOptions = ISVNConnector.Options.INCLUDE_MERGED_REVISIONS;
			//don't retrieve any revision properties, e.g. author, date, comment
			this.revProps = new String[] {""}; //$NON-NLS-1$
			this.logEntryCallback = new MergeLogEntryCallback(this, monitor, (int) (endRevision - startRevision + 1), this.repositoryCache);	
		}
	}
	
	protected long getStartSkippedRevision() {
		return this.repositoryCache.getCacheInfo().getMergeStartSkippedRevision();
	}
	
	protected long getEndSkippedRevision() {
		return this.repositoryCache.getCacheInfo().getMergeEndSkippedRevision();
	}
}
