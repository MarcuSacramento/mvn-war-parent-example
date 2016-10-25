package br.gov.mj.side.web.util;

import java.util.Iterator;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import br.gov.mj.infra.negocio.persistencia.EnumOrder;
import br.gov.mj.side.entidades.Bem;
import br.gov.mj.side.web.service.BemService;

public class SortableBemDataProvider extends SortableDataProvider<Bem, String> {
    private static final long serialVersionUID = 1L;

    private BemService entityService;
    private Bem entity;

    SortableBemDataProvider() {
    }

    public SortableBemDataProvider(Bem entity, BemService entityService) {
        this.entityService = entityService;
        this.entity = entity;
        setSort("nomeBem", SortOrder.ASCENDING);
    }

    @Override
    public Iterator<Bem> iterator(long first, long count) {
        return entityService.buscarPaginado(getEntity(), (int) first, (int) count, getSort().isAscending() ? EnumOrder.ASC : EnumOrder.DESC, getSort().getProperty()).iterator();
    }

    @Override
    public long size() {
        return entityService.contarPaginado(getEntity());
    }

    @Override
    public IModel<Bem> model(Bem object) {
        return new CompoundPropertyModel<Bem>(object);
    }

    public Bem getEntity() {
        return entity;
    }

    public void setEntity(Bem entity) {
        this.entity = entity;
    }

    public BemService getEntityService() {
        return entityService;
    }

    public void setEntityService(BemService entityService) {
        this.entityService = entityService;
    }
}