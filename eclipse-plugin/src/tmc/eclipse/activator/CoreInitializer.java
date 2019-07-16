package tmc.eclipse.activator;

import java.io.IOException;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import fi.helsinki.cs.tmc.core.TmcCore;
import fi.helsinki.cs.tmc.core.exceptions.AuthenticationFailedException;
import fi.helsinki.cs.tmc.core.holders.TmcLangsHolder;
import fi.helsinki.cs.tmc.core.holders.TmcSettingsHolder;
import fi.helsinki.cs.tmc.core.old.Core;
import fi.helsinki.cs.tmc.core.old.services.http.ServerManager;
import fi.helsinki.cs.tmc.langs.util.TaskExecutorImpl;
import tmc.eclipse.domain.TmcCoreSettingsImpl;
import tmc.eclipse.handlers.EclipseErrorHandler;
import tmc.eclipse.snapshots.EditorListener;
import tmc.eclipse.snapshots.ResourceEventListener;
import tmc.eclipse.tasks.EclipseTaskRunner;
import tmc.eclipse.tasks.RecurringTaskRunner;
import tmc.eclipse.tasks.TaskStarter;
import tmc.eclipse.util.LoginManager;
import tmc.eclipse.util.WorkbenchHelper;

public class CoreInitializer extends AbstractUIPlugin implements IStartup {

    public static final String PLUGIN_ID = "TestMyCode Eclipse plugin"; //$NON-NLS-1$
    private static CoreInitializer instance;

    private WorkbenchHelper workbenchHelper;
    private RecurringTaskRunner recurringTaskRunner;

    public CoreInitializer() {
    }

    @Override
	public void start(BundleContext context) throws Exception {
        super.start(context);

        ServerManager server = Core.getServerManager();

        ResourcesPlugin.getWorkspace().addResourceChangeListener(new ResourceEventListener(),
                IResourceChangeEvent.POST_CHANGE | IResourceChangeEvent.PRE_DELETE);

        instance = this;

        this.workbenchHelper = new WorkbenchHelper(Core.getProjectDAO());

        Core.setTaskRunner(new EclipseTaskRunner());
        recurringTaskRunner = new RecurringTaskRunner(Core.getSettings());
        recurringTaskRunner.startRecurringTasks();

        if (Core.getSettings().isCheckingForUnopenedAtStartup()) {
            TaskStarter.startOpenAllDownloadedExercisesTask();
        }

        TmcSettingsHolder.set(new TmcCoreSettingsImpl());
        TmcLangsHolder.set(new TaskExecutorImpl());
        TmcCore.setInstance(new TmcCore(TmcSettingsHolder.get(), TmcLangsHolder.get()));

        new Thread(() -> {
        	LoginManager loginManager = new LoginManager();
            try {
				loginManager.login();
			} catch (AuthenticationFailedException | InterruptedException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }).start();
    }

    @Override
	public void stop(BundleContext context) throws Exception {
        Core.getProjectDAO().save();

        instance = null;
        super.stop(context);
    }

    public static CoreInitializer getDefault() {
        return instance;
    }

    public static ImageDescriptor getImageDescriptor(String path) {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }

    @Override
    public void earlyStartup() {
        Display.getDefault().syncExec(new Runnable() {

            @Override
            public void run() {
                Core.setErrorHandler(new EclipseErrorHandler(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                        .getShell()));

                if (!(PlatformUI.getWorkbench().getActiveWorkbenchWindow() == null)) {
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                            .addPartListener(new EditorListener());
                }
            }
        });
    }

    public WorkbenchHelper getWorkbenchHelper() {
        return workbenchHelper;
    }

    public RecurringTaskRunner getRecurringTaskRunner() {
        return recurringTaskRunner;
    }
}
