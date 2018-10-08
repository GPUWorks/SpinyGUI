package org.liquidengine.legui.core;

import io.github.classgraph.ClassGraph;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.liquidengine.legui.core.api.Monitor;
import org.liquidengine.legui.core.api.Window;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public abstract class Legui {

    private static final Logger LOGGER = LogManager.getLogger();
    private static Legui insance;

    static Legui getInstance() {
        return SystemInitializer.instance;
    }

    public static Window createWindow(int width, int height, String title, boolean fullscreen) {
        return getInstance()._createWindow(width, height, title, fullscreen);
    }

    public static Monitor getPrimaryMonitor() {
        return getInstance()._getPrimaryMonitor();
    }

    public static List<Monitor> getMonitors() {
        return getInstance()._getMonitors();
    }

    protected abstract Monitor _getPrimaryMonitor();

    protected abstract List<Monitor> _getMonitors();

    protected abstract Window _createWindow(int width, int height, String title, boolean fullscreen);

    private static final class SystemInitializer {
        private static Legui instance;

        static {
            try (var scanResult = new ClassGraph().enableAllInfo().scan()) {
                // get all subclasses
                var leguiClassRefs = scanResult.getSubclasses("org.liquidengine.legui.core.Legui").loadClasses();
                // check if found implementations.
                if (leguiClassRefs != null && !leguiClassRefs.isEmpty()) {
                    // log existing implementations
                    if (LOGGER.isDebugEnabled()) {
                        leguiClassRefs.stream()
                                .map(lc -> String.format("Legui class implementation found: %s", lc.getName()))
                                .forEach(LOGGER::debug);
                    }

                    //get property
                    var beImpl = Configuration.BACKEND.getState();

                    if (beImpl != null) {
                        LOGGER.debug("Trying to load specified implementation: '" + beImpl + "'.");
                        for (Class<?> aClass : leguiClassRefs) {
                            if (beImpl.equals(aClass.getName()) || beImpl.equals(aClass.getSimpleName())) {
                                createInstance(aClass);
                            }
                        }
                        if (instance == null) {
                            LOGGER.debug("Specified implementation '" + beImpl + "' not found.");
                        } else {
                            LOGGER.debug("Specified implementation '" + beImpl + "' loaded.");
                        }
                    }

                    if (instance == null) {
                        //TODO: Create backend selection based on system ability to work with opengl/vulkan/etc
                        createInstance(leguiClassRefs.get(0));
                    }
                }
                // if no implementation found - throwing exception
                if (instance == null) {
                    throw new IllegalStateException("Can't find any appropriate Legui implementations.");
                }
            }
        }

        private static void createInstance(Class<?> aClass2) {
            try {
                instance = (Legui) aClass2.getDeclaredConstructor().newInstance();
            } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }
}
