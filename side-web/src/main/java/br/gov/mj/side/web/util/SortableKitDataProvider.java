package br.gov.mj.side.web.util;

import java.util.Iterator;

import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import br.gov.mj.apoio.entidades.Elemento;
import br.gov.mj.side.entidades.Bem;
import br.gov.mj.side.entidades.Kit;
import br.gov.mj.side.web.service.KitService;

public class SortableKitDataProvider extends SortableDataProvider<Kit, String> {
    private static final long serialVersionUID = 1L;

    private KitService kitService;
    private Kit kit;
    private Bem bem;
    private Elemento elemento;
    private String coluna;
    private int order;

    SortableKitDataProvider() {
    }

    public SortableKitDataProvider(Kit kit, Bem bem, Elemento elemento, KitService kitService, String coluna, int order) {
        this.kit = kit;
        this.kitService = kitService;
        this.bem = bem;
        this.elemento = elemento;
        this.coluna = coluna;
        this.order = order;
    }

    @Override
    public Iterator<Kit> iterator(long first, long count) {
        return kitService.buscarPaginado(getKit(), getBem(), getElemento(), (int) first, (int) count, coluna, order).iterator();
    }

    @Override
    public long size() {
        return kitService.pesquisar(kit, bem, elemento).size();
    }

    @Override
    public IModel<Kit> model(Kit object) {
        return new CompoundPropertyModel<Kit>(object);
    }

    public KitService getKitService() {
        return kitService;
    }

    public void setKitService(KitService kitService) {
        this.kitService = kitService;
    }

    public Kit getKit() {
        return kit;
    }

    public void setKit(Kit kit) {
        this.kit = kit;
    }

    public Bem getBem() {
        return bem;
    }

    public void setBem(Bem bem) {
        this.bem = bem;
    }

    public Elemento getElemento() {
        return elemento;
    }

    public void setElemento(Elemento elemento) {
        this.elemento = elemento;
    }

}