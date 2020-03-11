package com.airback.runner;

import org.slf4j.LoggerFactory;
import java.security.CodeSource;
import java.util.Iterator;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.io.FileWriter;
import java.util.zip.ZipFile;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.io.FileOutputStream;
import java.util.zip.ZipInputStream;
import java.io.FileInputStream;
import org.apache.commons.io.FileUtils;
import java.io.File;
import org.slf4j.Logger;

public class Executor
{
    private static Logger LOG;
    private static String PID_FILE;

    private static void unpackFile(final File upgradeFile) throws IOException {
        if (isValidZipFile(upgradeFile)) {
            final File libFolder = new File(getUserDir(), "lib");
            final File i18nFolder = new File(getUserDir(), "i18n");
            assertFolderWritePermission(libFolder);
            assertFolderWritePermission(i18nFolder);
            int tryTimes = 0;
            while (tryTimes < 10) {
                try {
                    FileUtils.deleteDirectory(libFolder);
                    FileUtils.deleteDirectory(i18nFolder);
                }
                catch (Exception e2) {
                    ++tryTimes;
                    try {
                        Thread.sleep(10000L);
                    }
                    catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    continue;
                }
                break;
            }
            final byte[] buffer = new byte[2048];
            try (final ZipInputStream inputStream = new ZipInputStream(new FileInputStream(upgradeFile))) {
                ZipEntry entry;
                while ((entry = inputStream.getNextEntry()) != null) {
                    if (!entry.isDirectory() && (entry.getName().startsWith("lib/") || entry.getName().startsWith("i18n"))) {
                        final File candidateFile = new File(getUserDir(), entry.getName());
                        candidateFile.getParentFile().mkdirs();
                        Executor.LOG.info("Copy file: " + entry.getName());
                        try (final FileOutputStream output = new FileOutputStream(candidateFile)) {
                            int len;
                            while ((len = inputStream.read(buffer)) > 0) {
                                output.write(buffer, 0, len);
                            }
                        }
                    }
                }
            }
            return;
        }
        throw new RuntimeException("Invalid installer file. It is not a zip file");
    }

    private static boolean isValidZipFile(final File file) {
        try (final ZipFile zipFile = new ZipFile(file)) {
            return true;
        }
        catch (IOException e) {
            return false;
        }
    }

    private static void assertFolderWritePermission(final File folder) throws IOException {
        if (!folder.canWrite()) {
            throw new IOException(System.getProperty("user.name") + " does not have write permission on folder " + folder.getAbsolutePath() + ". The upgrade could not be proceeded. Please correct permission before you upgrade airback again");
        }
    }

    private Executor() {
        final File jarFile = getUserDir();
        System.setProperty("airback_APP_HOME", jarFile.getAbsolutePath());
    }

    private void runServer(final String[] args) throws Exception {
        Executor.LOG.info("Start airback server process");
        final AppProcess process = new AppProcess(args);
        final File pIdFile = new File(getUserDir(), Executor.PID_FILE);
        try (final FileWriter writer = new FileWriter(new File(getUserDir(), Executor.PID_FILE), false)) {
            writer.write("START");
        }
        catch (Exception e) {
            Executor.LOG.error("Error to write pid file", (Throwable)e);
            System.exit(-1);
        }
        new Thread(() -> {
          final File file = null;
          final Path toWatch;
          WatchService pIdWatcher = null;
          long lastInvokeTime = 0;
          String lastFileContent = null;
          WatchKey key = null;
          final Iterator<WatchEvent<?>> iterator = null;
          WatchEvent event = null;
          WatchEvent.Kind<Path> kind = null;
          Path modifiedPath = null;
          String fileName = null;
          String fileContent = null;
          long currentTime = 0;
          String filePath = null;
          File upgradeFile = null;
          final AppProcess appProcess = null;
          boolean isValid = false;
          final Throwable t4 = null;
          //Path home = Paths.get(System.getProperty("executor.jar"));
            //file = new File(System.getProperty("executor.jar") + "/.");
            toWatch = Paths.get(file.getParent(), new String[0]);
            //Path home = Paths.get("./", "target/", "executor.jar");
            //Path toWatch = home.getParent();
            //Path toWatch = Paths.get(System.getProperty("executor.jar"));
            System.out.println("--printout(e): " + toWatch);
            try {
                pIdWatcher = FileSystems.getDefault().newWatchService();
                try {
                    toWatch.register(pIdWatcher, (WatchEvent.Kind<?>[])new WatchEvent.Kind[] { StandardWatchEventKinds.ENTRY_MODIFY });
                    lastInvokeTime = System.currentTimeMillis();
                    lastFileContent = "";
                    while (true) {
                        key = pIdWatcher.take();
                        Executor.LOG.info("Watch key: " + key);
                        key.pollEvents().iterator();
                        while (iterator.hasNext()) {
                            event = iterator.next();
                            kind = event.kind();
                            modifiedPath = (Path)event.context();
                            fileName = modifiedPath.toFile().getName();
                            if (StandardWatchEventKinds.ENTRY_MODIFY == kind && Executor.PID_FILE.equals(fileName)) {
                                fileContent = FileUtils.readFileToString(file);
                                currentTime = System.currentTimeMillis();
                                Executor.LOG.info("A: " + (currentTime - lastInvokeTime) + "--" + fileContent + "---" + lastFileContent);
                                if (currentTime - lastInvokeTime > 5000L || !fileContent.equalsIgnoreCase(lastFileContent)) {
                                    lastInvokeTime = currentTime;
                                    lastFileContent = fileContent;
                                    Executor.LOG.info("Processing " + kind.hashCode() + "---" + event + "---" + fileContent + "--" + lastFileContent);
                                    if (fileContent.startsWith("UPGRADE")) {
                                        filePath = fileContent.substring("UPGRADE:".length());
                                        Executor.LOG.info(String.format("Upgrade airback with file %s", filePath));
                                        upgradeFile = new File(filePath);
                                        if (upgradeFile.exists()) {
                                            appProcess.stop();
                                            unpackFile(upgradeFile);
                                            appProcess.start();
                                        }
                                        else {
                                            Executor.LOG.error("Can not upgrade airback because the upgrade file is not existed " + upgradeFile.getAbsolutePath());
                                        }
                                    }
                                    else if (fileContent.startsWith("STOP")) {
                                        appProcess.stop();
                                        Executor.LOG.info("Stop wrapper process");
                                        System.exit(-1);
                                    }
                                    else if (fileContent.equals("RESTART")) {
                                        Executor.LOG.info("Restart service ...");
                                        appProcess.stop();
                                        appProcess.start();
                                    }
                                    Executor.LOG.info("Stop processing");
                                    break;
                                }
                                else {
                                    continue;
                                }
                            }
                        }
                        isValid = key.reset();
                        if (!isValid) {
                            break;
                        }
                    }
                    return;
                }
                catch (Throwable t3) {
                    throw t3;
                }
                finally {
                    if (pIdWatcher != null) {
                        if (t4 != null) {
                            try {
                                pIdWatcher.close();
                            }
                            catch (Throwable exception2) {
                                t4.addSuppressed(exception2);
                            }
                        }
                        else {
                            pIdWatcher.close();
                        }
                    }
                }
            }
            catch (Exception e2) {
                Executor.LOG.error("Error", (Throwable)e2);
                return;
            }
        }).start();
        process.start();
        System.out.println("--printout(e): processs has started!!!!!!!!");
    }

    private void stopServer() {
        try (final FileWriter writer = new FileWriter(new File(getUserDir(), Executor.PID_FILE), false)) {
            writer.write("STOP");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static File getUserDir() {
        try {
            final CodeSource codeSource = Executor.class.getProtectionDomain().getCodeSource();
            final File jarFile = new File(codeSource.getLocation().toURI().getPath());
            final String jarDir = jarFile.getParentFile().getPath();
            return new File(jarDir);
        }
        catch (Exception e) {
            return new File(System.getProperty("user.dir"));
        }
    }

    public static void main(final String[] args) throws Exception {
        if (args.length > 0 && args[0].equals("--stop")) {
            new Executor().stopServer();
        }
        else {
            new Executor().runServer(args);
        }
    }

    static {
        Executor.LOG = LoggerFactory.getLogger((Class)Executor.class);
        Executor.PID_FILE = ".airback.pid";
    }
}
