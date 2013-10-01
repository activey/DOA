package pl.doa.wicket.ui.list;

import java.util.Iterator;

import org.apache.wicket.Component;
import org.apache.wicket.markup.IMarkupFragment;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.repeater.AbstractRepeater;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.collections.ReadOnlyIterator;

public abstract class IteratorView<T> extends AbstractRepeater {
    private static final long serialVersionUID = 1L;

    /**
     * @see org.apache.wicket.Component#Component(String)
     */
    public IteratorView(final String id) {
        super(id);
    }

    /**
     * @param id
     * @param model
     * @see org.apache.wicket.Component#Component(String, IModel)
     */
    public IteratorView(final String id,
                        final IModel<? extends Iterable<? extends T>> model) {
        super(id, model);

        if (model == null) {
            throw new IllegalArgumentException(
                    "Null models are not allowed. If you have no model, you may prefer a Loop instead");
        }

        // A reasonable default for viewSize can not be determined right now,
        // because list items might be added or removed until ListView
        // gets rendered.
    }

    /**
     * @param id   See Component
     * @param list List to cast to Serializable
     * @see org.apache.wicket.Component#Component(String, IModel)
     */
    public IteratorView(final String id, final Iterable<? extends T> list) {
        this(id, new AbstractReadOnlyModel<Iterable<? extends T>>() {

            @Override
            public Iterable<? extends T> getObject() {
                return list;
            }
        });
    }

    /**
     * Create a new ListItem for list item at index.
     *
     * @param index
     * @param itemModel object in the list that the item represents
     * @return ListItem
     */
    protected ListItem<T> newItem(final int index, IModel<T> itemModel) {
        return new ListItem<T>(index, itemModel);
    }

    /**
     * @see org.apache.wicket.markup.repeater.AbstractRepeater#onPopulate()
     */
    @SuppressWarnings("unchecked")
    @Override
    protected final void onPopulate() {
        boolean hasChildren = size() != 0;

        Iterable<T> iterable = getModelObject();
        int index = 0;
        for (final T iteratorElement : iterable) {
            ListItem<T> item = null;
            if (hasChildren) {
                // If this component does not already exist, populate it
                item = (ListItem<T>) get(Integer.toString(index));
            }
            if (item == null) {
                // Create item for index
                item = newItem(index, new AbstractReadOnlyModel<T>() {

                    @Override
                    public T getObject() {
                        return iteratorElement;
                    }
                });

                if (item != null) {

                    // Add list item
                    add(item);

                    // Populate the list item
                    onBeginPopulateItem(item);
                    populateItem(item);
                }
            }
            index++;
        }

    }

    /**
     * Comes handy for ready made ListView based components which must implement
     * populateItem() but you don't want to lose compile time error checking
     * reminding the user to implement abstract populateItem().
     *
     * @param item
     */
    protected void onBeginPopulateItem(final ListItem<T> item) {
    }

    /**
     * Populate a given item.
     * <p>
     * <b>be careful</b> to add any components to the list item. So, don't do:
     * <p/>
     * <pre>
     * add(new Label(&quot;foo&quot;, &quot;bar&quot;));
     * </pre>
     * <p/>
     * but:
     * <p/>
     * <pre>
     * item.add(new Label(&quot;foo&quot;, &quot;bar&quot;));
     * </pre>
     * <p/>
     * </p>
     *
     * @param item The item to populate
     */
    protected abstract void populateItem(final ListItem<T> item);

    /**
     * @see org.apache.wicket.markup.repeater.AbstractRepeater#renderChild(org.apache.wicket.Component)
     */
    @Override
    protected final void renderChild(Component child) {
        renderItem((ListItem<?>) child);
    }

    /**
     * Render a single item.
     *
     * @param item The item to be rendered
     */
    protected void renderItem(final ListItem<?> item) {
        if (item == null) {
            return;
        }
        Object modelObject = item.getModelObject();
        if (modelObject == null) {
            afterRender();
            return;
        }
        item.render();
    }

    /**
     * @see org.apache.wicket.markup.repeater.AbstractRepeater#renderIterator()
     */
    @Override
    protected Iterator<Component> renderIterator() {
        final int size = size();
        return new ReadOnlyIterator<Component>() {
            private int index = 0;

            public boolean hasNext() {
                return index < size;
            }

            public Component next() {
                final String id = Integer.toString(index);
                index++;
                return get(id);
            }
        };
    }

    /**
     * Gets model
     *
     * @return model
     */
    @SuppressWarnings("unchecked")
    public final IModel<Iterable<T>> getModel() {
        return (IModel<Iterable<T>>) getDefaultModel();
    }

    /**
     * Sets model
     *
     * @param model
     */
    public final void setModel(IModel<? extends Iterable<T>> model) {
        setDefaultModel(model);
    }

    /**
     * Gets model object
     *
     * @return model object
     */
    @SuppressWarnings("unchecked")
    public final Iterable<T> getModelObject() {
        return (Iterable<T>) getDefaultModelObject();
    }

    /**
     * Sets model object
     *
     * @param object
     */
    public final void setModelObject(Iterable<T> object) {
        setDefaultModelObject(object);
    }

    /**
     * @see org.apache.wicket.markup.repeater.AbstractRepeater#getMarkup(org.apache.wicket.Component)
     */
    @Override
    public IMarkupFragment getMarkup(Component child) {
        // The childs markup is always equal to the parents markup.
        return getMarkup();
    }
}
