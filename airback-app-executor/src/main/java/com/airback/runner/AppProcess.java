package com.airback.runner;

import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.concurrent.TimeoutException;
import org.zeroturnaround.process.SystemProcess;
import org.zeroturnaround.process.ProcessUtil;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutionException;
import java.io.IOException;
import org.zeroturnaround.exec.StartedProcess;
import java.util.Iterator;
import org.zeroturnaround.process.Processes;
import java.io.OutputStream;
import org.zeroturnaround.exec.ProcessExecutor;
import java.util.Collection;
import java.util.Arrays;
import org.apache.commons.lang.SystemUtils;
import java.util.ArrayList;
import java.io.File;
import org.zeroturnaround.process.JavaProcess;
import org.slf4j.Logger;

class AppProcess
{
    private static Logger LOG = null;
    private String[] initialOptions = null;
    private JavaProcess wrappedJavaProcess = null;

    AppProcess(final String[] initialOptions) {
        this.initialOptions = initialOptions;
    }

    void start() throws IOException, ExecutionException, InterruptedException {

        new Thread(new Runnable() {
            @Override
            public void run() {
                  File workingDir = null;
                  ArrayList<String> javaOptions = null;
                  String javaHomePath = null;
                  String javaPath = null;
                  File javaExecutableFile = null;
                  ArrayList<String> options = null;
                  File libDir = null;
                  final RuntimeException ex = new RuntimeException();
                  StringBuilder classPaths = null;
                  File[] listFiles = null;
                  File[] jarFiles = null;
                  int length = 0;
                  int i = 0;
                  File subFile = null;
                  StringBuilder strBuilder = null;
                  Iterator<String> iterator = null;
                  String option = null;
                  StartedProcess javaProcess = null;
                  //String[] initialOptions2 = null;
                  //JavaProcess wrappedJavaProcess2 = null;

                    try {
                                workingDir = new File(System.getProperty("airback_APP_HOME"));
                                javaOptions = new ArrayList<String>();
                                javaHomePath = System.getProperty("java.home");
                                if (SystemUtils.IS_OS_WINDOWS) {
                                    javaPath = javaHomePath + "/bin/javaw.exe";
                                }
                                else {
                                    javaPath = javaHomePath + "/bin/java";
                                }
                                System.out.println("--printout(a): " + workingDir.toString() + "|" + javaPath );
                                javaExecutableFile = new File(javaPath);
                                if (javaExecutableFile.exists()) {
                                    javaOptions.add(javaExecutableFile.getAbsolutePath());
                                }
                                else {
                                    javaOptions.add("java");
                                }
                                if (initialOptions.length > 0) {
                                    options = new ArrayList<String>(Arrays.asList(initialOptions));
                                    if (options.contains("--start")) {
                                        options.remove("--start");
                                    }
                                    //javaOptions.addAll((Collection<?>)options);
                                    javaOptions.addAll(options);
                                }
                                libDir = new File(System.getProperty("airback_APP_HOME"), "lib");
                                if (!libDir.exists() || libDir.isFile()) {
                                    new RuntimeException("Can not find the library folder at " + libDir.getAbsolutePath());
                                    System.out.println("--printout(a): error occurred!");
                                    throw ex;
                                }
                                else {
                                    classPaths = new StringBuilder();
                                    jarFiles = (listFiles = libDir.listFiles(pathname -> pathname.isFile() && pathname.getName().endsWith("jar")));
                                    for (length = listFiles.length; i < length; ++i) {
                                        subFile = listFiles[i];
                                        classPaths.append(System.getProperty("path.separator"));
                                        classPaths.append("./lib/" + subFile.getName());
                                    }
                                    //javaOptions.addAll((Collection<?>)Arrays.asList("-cp", classPaths.toString(), "com.airback.server.DefaultServerRunner"));
                                    javaOptions.addAll(Arrays.asList("-cp", classPaths.toString(), "com.airback.server.DefaultServerRunner"));
                                    strBuilder = new StringBuilder();
                                    iterator = javaOptions.iterator();
                                    System.out.println("--printout(a): "+ javaOptions.toString());
                                    while (iterator.hasNext()) {
                                        option = iterator.next();
                                        strBuilder.append(option).append(" ");
                                    }
                                    AppProcess.LOG.info("airback options: " + strBuilder.toString());
                                    javaProcess = new ProcessExecutor().command((String[])javaOptions.toArray(new String[javaOptions.size()])).directory(workingDir).redirectOutput((OutputStream)System.out).readOutput(true).start();
                                    wrappedJavaProcess = Processes.newJavaProcess(javaProcess.getProcess());
                                    javaProcess.getFuture().get();
                                    System.out.println("--printout(a): done!");
                                }
                    } catch (Exception e) {
                        AppProcess.LOG.error("Error", (Throwable)e);
                    }
            }
        }).start();

  }

    void stop() throws InterruptedException, TimeoutException, IOException {
        AppProcess.LOG.info("Stopping airback process");
        ProcessUtil.destroyGracefullyOrForcefullyAndWait((SystemProcess)this.wrappedJavaProcess, 10L, TimeUnit.SECONDS, 10L, TimeUnit.SECONDS);
        AppProcess.LOG.info("Stopped airback process successfully");
    }

    static {
        AppProcess.LOG = LoggerFactory.getLogger((Class)AppProcess.class);
    }
}
