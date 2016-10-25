package br.gov.mj.side.web.view.components;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

public class EntityDataProvider<T> implements IDataProvider<T> {
    private static final long serialVersionUID = 1L;

    private List<T> entityList;

    public EntityDataProvider(List<T> entityList) {
        this.setEntityList(entityList);
    }

    @Override
    public void detach() {
        // Verificar a usabilidade desse método
    }

    @Override
    public Iterator<? extends T> iterator(long first, long count) {
        List<T> listReturn = new ArrayList<T>();

        if (!getEntityList().isEmpty()) {

            int begin = (int) first;
            int end = (int) (first + count);

            if (end > getEntityList().size()) {
                end = getEntityList().size();
            }
            for (int i = begin; i < end; i++) {
                listReturn.add(getEntityList().get(i));
            }
        }
        return listReturn.iterator();
    }

    @Override
    public long size() {
        return (long) (getEntityList().size());
    }

    @Override
    public IModel<T> model(T object) {
        return new CompoundPropertyModel<T>(object);
    }

    public List<T> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<T> entityList) {
        this.entityList = entityList;
    }
}
