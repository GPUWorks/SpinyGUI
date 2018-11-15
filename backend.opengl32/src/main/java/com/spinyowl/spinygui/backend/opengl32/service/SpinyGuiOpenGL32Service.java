package com.spinyowl.spinygui.backend.opengl32.service;

import com.spinyowl.spinygui.core.api.Monitor;
import com.spinyowl.spinygui.core.api.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

class SpinyGuiOpenGL32Service {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpinyGuiOpenGL32Service.class);
    private static final SpinyGuiOpenGL32Service INSTANCE = new SpinyGuiOpenGL32Service();

    private AtomicBoolean started = new AtomicBoolean(false);

    private SpinyGuiOpenGL32ServiceThread thread;

    private SpinyGuiOpenGL32Service() {
    }

    static SpinyGuiOpenGL32Service getInstance() {
        return INSTANCE;
    }

    private void check() {
        if (!started.get()) {
            throw new IllegalStateException("Service should be started before executing any service methods!");
        }
    }

    void startService() {
        if (started.compareAndSet(false, true)) {

            // register shutdown hook to release resources.
            Runtime.getRuntime().addShutdownHook(new Thread(this::stopService, "SpinyGui OpenGL 3.2 Service Thread Destroyer"));

            // create task executor.
            thread = new SpinyGuiOpenGL32ServiceThread();
            thread.start();
        }
    }

    void stopService() {
        LOGGER.debug("WAITING FOR ALL WINDOWS ARE CLOSED");
        while (!WindowService.getWindows().isEmpty()) {
            Thread.yield();
        }

        LOGGER.debug("STOPPING THE SERVICE");
        if (started.compareAndSet(true, false)) {
            Future<?> submit = thread.addTask(this::destroyAllResources);
            while (!submit.isDone()) {
                Thread.yield();
            }
            thread.stop();
        }
    }

    private void destroyAllResources() {
    }

    Monitor getPrimaryMonitor() {
        check();
        try {
            return thread.addTask(MonitorService::getPrimaryMonitor).get();
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Error occurred during creating Monitor instance.", e);
            return null;
        }
    }


    List<Monitor> getMonitors() {
        check();

        try {
            return thread.addTask(MonitorService::getMonitors).get();
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Error occurred during creating Monitor instances.", e);
            return null;
        }
    }

    Window createWindow(int width, int height, String title) {
        return this.createWindow(width, height, title, null);
    }

    Window createWindow(int width, int height, String title, Monitor monitor) {
        check();

        try {
            return thread.addTask(() -> WindowService.createWindow(width, height, title, monitor)).get();
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Error occurred during creating Window instance.", e);
            return null;
        }
    }

    void destroyWindow(Window window) {
        check();
        thread.addTask(() -> WindowService.destroyWindow(window));
    }

    List<Window> getWindows() {
        check();
        try {
            return thread.addTask(WindowService::getWindows).get();
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Error occurred during retrieving Window instances.", e);
            return null;
        }
    }
}