package tmc.eclipse.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import fi.helsinki.cs.tmc.core.holders.TmcSettingsHolder;
import tmc.eclipse.activator.CoreInitializer;
import tmc.eclipse.ui.LoginDialog;

public class LoginManager {
	
	public LoginManager() {
	}
	
	public static synchronized void login() {
		if(loggedIn()) return; 
		
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				final Shell shell = CoreInitializer.getDefault().getWorkbenchHelper().getUsableShell();
				LoginDialog dialog = new LoginDialog(shell, SWT.SHEET);
				dialog.open();
			}
		});
	}
	
	public static boolean loggedIn() {
		return TmcSettingsHolder.get().getToken().isPresent();
	}
}
