package org.liquidengine.legui.backend.opengl32;

import org.liquidengine.legui.core.Legui;
import org.liquidengine.legui.core.api.Monitor;
import org.liquidengine.legui.core.api.Window;

import java.util.List;

public class LeguiOpenGL32 extends Legui {
    {
        System.out.println("INITIALIZED WITH OpenGL 32 CORE");
    }

    @Override
    protected Monitor _getPrimaryMonitor() {
        return null;
    }

    @Override
    protected List<Monitor> _getMonitors() {
        return null;
    }

    @Override
    protected Window _createWindow(int width, int height, String title, Monitor monitor) {
        return null;
    }

    @Override
    protected void _destroyWindow(Window window) {

    }

    @Override
    protected List<Window> _getWindows() {
        return null;
    }
}
