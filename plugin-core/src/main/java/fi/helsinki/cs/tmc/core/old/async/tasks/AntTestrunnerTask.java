package fi.helsinki.cs.tmc.core.old.async.tasks;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.List;

import fi.helsinki.cs.tmc.core.old.async.BackgroundTask;
import fi.helsinki.cs.tmc.core.old.async.TaskStatusMonitor;
import fi.helsinki.cs.tmc.core.old.domain.ClassPath;
import fi.helsinki.cs.tmc.core.old.domain.TestRunResult;
import fi.helsinki.cs.tmc.core.old.services.Settings;
import fi.helsinki.cs.tmc.core.old.ui.IdeUIInvoker;
import fi.helsinki.cs.tmc.core.old.utils.TestResultParser;

/**
 * An abstract background task for building and running tmc-junit-runner for an
 * Ant project.
 *
 * Concrete classes must implement the abstract build method that tells the
 * class how to build and ant project using the argument "compile-test".
 */
public abstract class AntTestrunnerTask extends TestrunnerTask {

    public class ConcurrentAntBuildsException extends Exception { }

    private static final int THREAD_CHECK_INCREMENT_TIME_IN_MILLIS = 100;
    private static final int THREAD_NOT_FINISHED = -1;

    private List<String> args;
    private final ClassPath classpath;

    private final String rootPath;
    private final String testDirPath;
    private final String javaExecutable;
    private final Integer memoryLimit;
    private final String resultFilePath;
    private TestRunResult result;
    private Process process;

    private Settings settings;
    private final IdeUIInvoker invoker;

    /**
     * @param rootPath
     *            An absolute path to the root of the project.
     * @param testDir
     *            An absolute path to the root of the test directory.
     * @param javaExecutable
     *            An absolute path to the java executable that should be used.
     *            This means that you should be able to say
     *            "<javaExecutable> --version" in the CLI and it should print
     *            out the JRE version.
     * @param memoryLimit
     *            An optional memory limit in mb.
     * @param settings
     *            An instance of the Settings.
     * @param invoker
     *            An instance of a class that implements IdeUIInvoker.
     */
    public AntTestrunnerTask(final String rootPath,
                             final String testDir,
                             final String javaExecutable,
                             final Integer memoryLimit,
                             final Settings settings,
                             final IdeUIInvoker invoker) {

        super("Running tests");

        this.rootPath = rootPath;
        this.resultFilePath = rootPath + "/results.txt";
        this.testDirPath = testDir;
        this.javaExecutable = javaExecutable;
        this.memoryLimit = memoryLimit;
        this.settings = settings;

        this.settings = settings;
        this.invoker = invoker;

        this.classpath = new ClassPath(rootPath);
        classpath.addDirAndSubDirs(rootPath + "/lib");
        classpath.add(rootPath + "/build/classes/");
        classpath.add(rootPath + "/build/test/classes/");
    }

    public abstract void build(final String root) throws Exception;

    @Override
    public TestRunResult get() {

        return result;
    }

    @Override
    public int start(final TaskStatusMonitor progress) {

        progress.startProgress(this.getDescription(), 4);

        try {

            build(rootPath);

        } catch (final ConcurrentAntBuildsException e) {

            invoker.raiseVisibleException("Unable to run tests: " +
                                          "\nUnable to build the same project multiple times concurrently." +
                                          "\nPlease wait for the first test run to finish or cancel it.");
            return BackgroundTask.RETURN_FAILURE;

        } catch (final Exception e) {

            invoker.raiseVisibleException("Unable to run tests: Error when building project.");
            return BackgroundTask.RETURN_FAILURE;

        }

        progress.incrementProgress(1);
        if (shouldStop(progress)) {
            return BackgroundTask.RETURN_INTERRUPTED;
        }

        try {
            buildTestRunnerArgs(progress);
        } catch (final InterruptedException e) {
            return BackgroundTask.RETURN_INTERRUPTED;
        }

        progress.incrementProgress(1);
        if (shouldStop(progress)) {
            return BackgroundTask.RETURN_INTERRUPTED;
        }

        final ProcessBuilder pb = new ProcessBuilder(args);
        pb.redirectError(Redirect.INHERIT);

        try {
            process = pb.start();

            int status = THREAD_NOT_FINISHED;
            while (status == THREAD_NOT_FINISHED) {

                try {
                    status = process.exitValue();
                } catch (final IllegalThreadStateException e) {
                    Thread.sleep(THREAD_CHECK_INCREMENT_TIME_IN_MILLIS);
                }

                if (shouldStop(progress)) {
                    process.destroy();
                    return BackgroundTask.RETURN_INTERRUPTED;
                }

            }

            progress.incrementProgress(1);
            if (shouldStop(progress)) {
                return BackgroundTask.RETURN_INTERRUPTED;
            }

            final File resultFile = new File(resultFilePath);
            result = new TestResultParser().parseTestResults(resultFile);
            resultFile.delete();
            progress.incrementProgress(1);

            return BackgroundTask.RETURN_SUCCESS;

        } catch (final IOException e) {
            invoker.raiseVisibleException("Failed to parse test results.");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final InterruptedException e) {
            invoker.raiseVisibleException("Failed to run tests");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return BackgroundTask.RETURN_FAILURE;
    }

    private List<String> buildTestScannerArgs(final String testPath) {

        final List<String> testScannerArgs = new ArrayList<String>();

        testScannerArgs.add(args.get(0));
        testScannerArgs.add("-cp");
        testScannerArgs.add(classpath.toString());
        testScannerArgs.add("fi.helsinki.cs.tmc.testscanner.TestScanner");
        testScannerArgs.add(testPath);
        testScannerArgs.add("--test-runner-format");

        return testScannerArgs;
    }

    private boolean buildTestRunnerArgs(final TaskStatusMonitor progress) throws InterruptedException {

        args = new ArrayList<String>();

        args.add(javaExecutable);

        final List<String> testMethods = findProjectTests(testDirPath, progress);

        args.add("-Dtmc.test_class_dir=" + testDirPath);
        args.add("-Dtmc.results_file=" + resultFilePath);
        args.add("-Dfi.helsinki.cs.tmc.edutestutils.defaultLocale=" + settings.getErrorMsgLocale().toString());

        if (endorserLibsExists(rootPath)) {
            args.add("-Djava.endorsed.dirs=" + endorsedLibsPath(rootPath));
        }

        if (memoryLimit != null) {
            args.add("-Xmx" + memoryLimit + "M");
        }

        args.add("-cp");
        args.add(classpath.toString());

        args.add("fi.helsinki.cs.tmc.testrunner.Main");

        for (final String method : testMethods) {
            args.add(method);
        }

        return true;
    }

    private boolean endorserLibsExists(final String rootPath) {

        final File endorsedDir = endorsedLibsPath(rootPath);
        return endorsedDir.exists() && endorsedDir.isDirectory();
    }

    private File endorsedLibsPath(final String rootPath) {

        return new File(rootPath + "/lib/endorsed");
    }

    private List<String> findProjectTests(final String testPath, final TaskStatusMonitor progress) throws InterruptedException {

        final List<String> testScannerArgs = buildTestScannerArgs(testPath);

        final ProcessBuilder pb = new ProcessBuilder(testScannerArgs);
        pb.redirectError(Redirect.INHERIT);

        try {
            process = pb.start();

            int status = THREAD_NOT_FINISHED;
            while (status == THREAD_NOT_FINISHED) {

                try {
                    status = process.exitValue();
                } catch (final IllegalThreadStateException e) {
                    Thread.sleep(THREAD_CHECK_INCREMENT_TIME_IN_MILLIS);
                }

                if (shouldStop(progress)) {
                    process.destroy();
                    throw new InterruptedException();
                }
            }

            final BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            final List<String> results = new ArrayList<String>();

            String line = br.readLine();
            while (line != null && !line.equals("")) {
                results.add(line);
                line = br.readLine();
            }

            return results;

        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        invoker.raiseVisibleException("Testrunner failure: failed to find test methods.");

        return null;
    }
}
