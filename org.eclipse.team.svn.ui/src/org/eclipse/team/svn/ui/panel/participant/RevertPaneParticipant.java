/*******************************************************************************
 * Copyright (c) 2005-2008 Polarion Software.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Igor Burilo - Initial API and implementation
 *******************************************************************************/

package org.eclipse.team.svn.ui.panel.participant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.team.internal.ui.synchronize.SynchronizePageConfiguration;
import org.eclipse.team.svn.ui.SVNTeamUIPlugin;
import org.eclipse.team.svn.ui.SVNUIMessages;
import org.eclipse.team.svn.ui.panel.participant.CommitPaneParticipant.CommitPaneActionGroup;
import org.eclipse.team.svn.ui.synchronize.AbstractSynchronizeActionGroup;
import org.eclipse.team.svn.ui.synchronize.action.CleanUpAction;
import org.eclipse.team.svn.ui.synchronize.action.CompareWithLatestRevisionPaneAction;
import org.eclipse.team.svn.ui.synchronize.action.CompareWithRevisionPaneAction;
import org.eclipse.team.svn.ui.synchronize.action.CompareWithWorkingCopyPaneAction;
import org.eclipse.team.svn.ui.synchronize.action.CreateBranchAction;
import org.eclipse.team.svn.ui.synchronize.action.CreatePatchFileAction;
import org.eclipse.team.svn.ui.synchronize.action.DeletePaneAction;
import org.eclipse.team.svn.ui.synchronize.action.ExtractOutgoingToAction;
import org.eclipse.team.svn.ui.synchronize.action.OpenInComparePaneAction;
import org.eclipse.team.svn.ui.synchronize.action.ReplaceWithLatestRevisionPaneAction;
import org.eclipse.team.svn.ui.synchronize.action.ReplaceWithRevisionPaneAction;
import org.eclipse.team.svn.ui.synchronize.update.action.LockAction;
import org.eclipse.team.svn.ui.synchronize.update.action.UnlockAction;
import org.eclipse.team.svn.ui.verifier.IValidationManager;
import org.eclipse.team.ui.synchronize.ISynchronizePageConfiguration;
import org.eclipse.team.ui.synchronize.ISynchronizeScope;

/**
 * Revert pane participant
 * 
 * @author Igor Burilo
 */
public class RevertPaneParticipant extends BasePaneParticipant {

	public RevertPaneParticipant(ISynchronizeScope scope, IValidationManager validationManager) {
		super(scope, validationManager);		
	}
	
	protected Collection<AbstractSynchronizeActionGroup> getActionGroups() {
    	List<AbstractSynchronizeActionGroup> actionGroups = new ArrayList<AbstractSynchronizeActionGroup>();
    	actionGroups.add(new RevertPaneActionGroup(this.validationManager));
    	return actionGroups;
    }
	
	/**
     * Revert pane's action set
     *
     * 
     * @author Igor Burilo
     */
    protected static class RevertPaneActionGroup extends BasePaneActionGroup {  	
    	    	
		public RevertPaneActionGroup(IValidationManager validationManager) {
			super(validationManager);		
		}

		protected MenuManager compareWithGroup;
    	protected MenuManager replaceWithGroup;    	    
		
		protected void configureActions(ISynchronizePageConfiguration configuration) {	
			super.configureActions(configuration);
			
			//Open in compare editor: handle double click		
			final OpenInComparePaneAction openInCompareAction = new OpenInComparePaneAction(configuration);
			configuration.setProperty(SynchronizePageConfiguration.P_OPEN_ACTION, new Action() {
				public void run() {
					openInCompareAction.run();
				}
			});
			
			//separator
			this.appendToGroup(
					ISynchronizePageConfiguration.P_CONTEXT_MENU, 
					CommitPaneActionGroup.GROUP_SYNC_NORMAL,
					new Separator());		

	        //create patch
	        CreatePatchFileAction patchAction = new CreatePatchFileAction(SVNUIMessages.CreatePatchCommand_label, configuration);
	        this.appendToGroup(
					ISynchronizePageConfiguration.P_CONTEXT_MENU, 
					CommitPaneActionGroup.GROUP_SYNC_NORMAL,
					patchAction);		        
	        	        
			//create branch
			CreateBranchAction branchAction = new CreateBranchAction(SVNUIMessages.SynchronizeActionGroup_Branch, configuration);
			branchAction.setImageDescriptor(SVNTeamUIPlugin.instance().getImageDescriptor("icons/common/actions/branch.gif")); //$NON-NLS-1$
			this.appendToGroup(
						ISynchronizePageConfiguration.P_CONTEXT_MENU, 
						CommitPaneActionGroup.GROUP_SYNC_NORMAL,
						branchAction);				 			
			 
			//separator
			this.appendToGroup(
					ISynchronizePageConfiguration.P_CONTEXT_MENU, 
					CommitPaneActionGroup.GROUP_SYNC_NORMAL,
					new Separator());												
			
			//lock
			LockAction lockAction = new LockAction(SVNUIMessages.UpdateActionGroup_Lock, configuration);
			lockAction.setImageDescriptor(SVNTeamUIPlugin.instance().getImageDescriptor("icons/common/actions/lock.gif")); //$NON-NLS-1$
			this.appendToGroup(
					ISynchronizePageConfiguration.P_CONTEXT_MENU, 
					CommitPaneActionGroup.GROUP_SYNC_NORMAL,
					lockAction);			
			
			//unlock
			UnlockAction unlockAction = new UnlockAction(SVNUIMessages.UpdateActionGroup_Unlock, configuration);
			unlockAction.setImageDescriptor(SVNTeamUIPlugin.instance().getImageDescriptor("icons/common/actions/unlock.gif")); //$NON-NLS-1$
			this.appendToGroup(
					ISynchronizePageConfiguration.P_CONTEXT_MENU, 
					CommitPaneActionGroup.GROUP_SYNC_NORMAL,
					unlockAction);	
			
			//separator
			this.appendToGroup(
					ISynchronizePageConfiguration.P_CONTEXT_MENU, 
					CommitPaneActionGroup.GROUP_SYNC_NORMAL,
					new Separator());		
			
			//Compare With group 
			this.compareWithGroup = new MenuManager(SVNUIMessages.CommitPanel_CompareWith_Group);
			this.appendToGroup(
					ISynchronizePageConfiguration.P_CONTEXT_MENU, 
					CommitPaneActionGroup.GROUP_SYNC_NORMAL, 
					this.compareWithGroup);
						
			//compare with working copy
			CompareWithWorkingCopyPaneAction compareWithWorkingCopyAction = new CompareWithWorkingCopyPaneAction(SVNUIMessages.CompareWithWorkingCopyAction_label, configuration);
			this.compareWithGroup.add(compareWithWorkingCopyAction);						
			
			//compare with latest revision
			CompareWithLatestRevisionPaneAction compareWithLatestRevisionPaneAction = new CompareWithLatestRevisionPaneAction(SVNUIMessages.CompareWithLatestRevisionAction_label, configuration);
			this.compareWithGroup.add(compareWithLatestRevisionPaneAction);			
						
			//compare with revision
			CompareWithRevisionPaneAction compareWithRevisionPaneAction = new CompareWithRevisionPaneAction(SVNUIMessages.CompareWithRevisionAction_label, configuration);
			this.compareWithGroup.add(compareWithRevisionPaneAction);			
			
			//Replace with group
			this.replaceWithGroup = new MenuManager(SVNUIMessages.CommitPanel_ReplaceWith_Group);
			this.appendToGroup(
					ISynchronizePageConfiguration.P_CONTEXT_MENU, 
					CommitPaneActionGroup.GROUP_SYNC_NORMAL, 
					this.replaceWithGroup);
			
			//replace with latest revision
			ReplaceWithLatestRevisionPaneAction replaceWithLatestRevisionPaneAction = new ReplaceWithLatestRevisionPaneAction(SVNUIMessages.ReplaceWithLatestRevisionAction_label, configuration);
			this.replaceWithGroup.add(replaceWithLatestRevisionPaneAction);
			
			//replace with revision
			ReplaceWithRevisionPaneAction replaceWithRevisionPaneAction = new ReplaceWithRevisionPaneAction(SVNUIMessages.ReplaceWithRevisionAction_label, configuration);
			this.replaceWithGroup.add(replaceWithRevisionPaneAction);
			
			//separator
			this.appendToGroup(
					ISynchronizePageConfiguration.P_CONTEXT_MENU, 
					CommitPaneActionGroup.GROUP_SYNC_NORMAL,
					new Separator());	
			
			//export
			ExtractOutgoingToAction extractActionOutgoing = new ExtractOutgoingToAction(SVNUIMessages.ExtractToAction_Label, configuration);
			this.appendToGroup(
					ISynchronizePageConfiguration.P_CONTEXT_MENU, 
					CommitPaneActionGroup.GROUP_SYNC_NORMAL,
					extractActionOutgoing);	
			
			//cleanup
			CleanUpAction cleanUpAction = new CleanUpAction(SVNUIMessages.SynchronizeActionGroup_Cleanup, configuration);
			this.appendToGroup(
					ISynchronizePageConfiguration.P_CONTEXT_MENU, 
					CommitPaneActionGroup.GROUP_SYNC_NORMAL,
					cleanUpAction);	
			
			//separator
			this.appendToGroup(
					ISynchronizePageConfiguration.P_CONTEXT_MENU, 
					CommitPaneActionGroup.GROUP_SYNC_NORMAL,
					new Separator());		
			
			//delete
			DeletePaneAction deleteAction = new DeletePaneAction(SVNUIMessages.CommitPanel_Delete_Action, configuration);
			deleteAction.setImageDescriptor(SVNTeamUIPlugin.instance().getImageDescriptor("icons/common/delete.gif")); //$NON-NLS-1$
			this.appendToGroup(
					ISynchronizePageConfiguration.P_CONTEXT_MENU, 
					CommitPaneActionGroup.GROUP_SYNC_NORMAL,
					deleteAction);	
		}
		
		public void dispose() {
			if (this.compareWithGroup != null) {
				this.compareWithGroup.removeAll();
				this.compareWithGroup.dispose();
			}
			
			if (this.replaceWithGroup != null) {
				this.replaceWithGroup.removeAll();
				this.replaceWithGroup.dispose();
			}
			
			super.dispose();
		}
    	
    }

}
