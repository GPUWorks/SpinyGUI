package org.spinyowl.spinygui.core.component.base;


import com.google.common.base.Objects;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class Container extends Component {

    private Map<String, String> attributes = new ConcurrentHashMap<>();

    private List<Component> childComponents = new CopyOnWriteArrayList<>();

    @Override
    public void removeChild(Component component) {
        childComponents.remove(component);
    }

    @Override
    public void addChild(Component component) {
        if (component == null || component == this || isContainsByRef(component)) return;

        Component parent = component.getParent();
        if (parent != null) parent.removeChild(component);

        childComponents.add(component);

        component.setParent(this);
    }

    @Override
    public List<Component> getChildComponents() {
        return Collections.unmodifiableList(childComponents);
    }

    /**
     * Returns unmodifiable collection of node attributes.
     *
     * @return unmodifiable collection of node attributes.
     */
    @Override
    public Map<String, String> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    /**
     * Used to set attribute.
     *
     * @param key   attribute name.
     * @param value attribute value.
     */
    @Override
    public void setAttribute(String key, String value) {
        attributes.put(key, value);
    }

    /**
     * Used to get attribute.
     *
     * @param key attribute name.
     * @return attribute value.
     */
    @Override
    public String getAttribute(String key) {
        return attributes.get(key);
    }

    /**
     * Used to remove attribute.
     *
     * @param key attribute name.
     */
    @Override
    public void removeAttribute(String key) {
        attributes.remove(key);
    }

    private boolean isContainsByRef(Component component) {
        return childComponents.stream().anyMatch(n -> n == component);
    }

    public String getClassAttribute() {
        return getAttribute("class");
    }

    public void setClassAttribute(String classAttribute) {
        setAttribute("class", classAttribute);
    }

    public String getId() {
        return getAttribute("id");
    }

    public void setId(String id) {
        setAttribute("id", id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Container container = (Container) o;
        return Objects.equal(attributes, container.attributes) &&
                Objects.equal(childComponents, container.childComponents);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(attributes, childComponents);
    }
}
