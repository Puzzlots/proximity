package org.example.exmod;

import org.apache.logging.log4j.core.util.StringBuilderWriter;

import java.io.PrintWriter;
import java.util.function.BiConsumer;

public class ThreadBuilder {

    private final String name;
    private final Runnable runnable;
    private BiConsumer<String, Throwable> handler;
    private boolean finished = false;
    private boolean daemon;

    private ThreadBuilder(String name, Runnable runnable) {
        this.name = name;
        this.runnable = runnable;
    }

    public void setExceptionHandler(BiConsumer<String, Throwable> consumer) {
        this.handler = consumer;
    }

    public Thread finish() {
        if (this.finished) throw new RuntimeException("Cannot finish builder more than once");
        this.finished = true;

        Thread thread = new Thread(runnable, name);
        thread.setDaemon(daemon);
        thread.setUncaughtExceptionHandler((t, e) -> {
            StringBuilderWriter builderWriter = new StringBuilderWriter();
            PrintWriter printWriter = new PrintWriter(builderWriter);

            printWriter.println("|/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\|");
            printWriter.println("-| Crash on thread \"" + name + "\":");
            e.printStackTrace(printWriter);
            printWriter.println("|/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\|");
            System.out.println(builderWriter.toString());
            printWriter.close();
            builderWriter.close();

            if (this.handler != null)
                this.handler.accept(t.getName(), e);
        });

        return thread;
    }

    public static ThreadBuilder create(String name, Runnable runnable) {
        return new ThreadBuilder(name, runnable);
    }


    public void setThreadDaemonState(boolean b) {
        this.daemon = b;
    }
}
