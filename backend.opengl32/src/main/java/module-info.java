/**
 * Created by ShchAlexander on 09.08.2018.
 */
open module org.liquidengine.legui.backend.opengl32 {
    requires org.liquidengine.legui.core;
    requires org.liquidengine.legui.backend;

    requires org.lwjgl;
    requires org.lwjgl.natives;
    requires org.lwjgl.glfw;
    requires org.lwjgl.glfw.natives;
    requires org.lwjgl.opengl;
    requires org.lwjgl.opengl.natives;

    requires org.apache.logging.log4j;

    exports org.liquidengine.legui.backend.opengl32;
}