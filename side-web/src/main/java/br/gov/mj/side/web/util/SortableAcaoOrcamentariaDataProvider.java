package br.gov.mj.side.web.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import br.gov.mj.infra.negocio.persistencia.EnumOrder;
import br.gov.mj.side.entidades.AcaoOrcamentaria;
import br.gov.mj.side.entidades.EmendaParlamentar;
import br.gov.mj.side.web.service.AcaoOrcamentariaService;

public class SortableAcaoOrcamentariaDataProvider extends SortableDataProvider<AcaoOrcamentaria, String> {

    private static final long serialVersionUID = 1L;

    private AcaoOrcamentariaService entityService;
    private AcaoOrcamentaria entity;
    private Boolean emendasVinculadas;
    private EmendaParlamentar emenda;

    public SortableAcaoOrcamentariaDataProvider(AcaoOrcamentariaService entityService, AcaoOrcamentaria entity, Boolean emendasVinculadas, EmendaParlamentar emenda) {
        this.entityService = entityService;
        this.entity = entity;
        this.emendasVinculadas = emendasVinculadas;
        this.emenda = emenda;
        setSort("nomeAcaoOrcamentaria", SortOrder.ASCENDING);
    }

    @Override
    public Iterator<AcaoOrcamentaria> iterator(long first, long count) {
        List<AcaoOrcamentaria> lista = entityService.buscarPaginado(entity, emendasVinculadas, emenda, (int) first, (int) count, getSort().isAscending() ? EnumOrder.ASC : EnumOrder.DESC, getSort().getProperty());
        List<AcaoOrcamentaria> listaFinal = new ArrayList<AcaoOrcamentaria>();
        for (AcaoOrcamentaria acaoOrcamentaria : lista) {
            acaoOrcamentaria.setEmendasParlamentares(entityService.buscarEmendaParlamentar(acaoOrcamentaria));
            listaFinal.add(acaoOrcamentaria);
        }

        return listaFinal.iterator();
    }

    @Override
    public long size() {
        return entityService.buscar(entity, emendasVinculadas, emenda).size();
    }

    @Override
    public IModel<AcaoOrcamentaria> model(AcaoOrcamentaria object) {
        return new CompoundPropertyModel<AcaoOrcamentaria>(object);
    }

    public AcaoOrcamentaria getEntity() {
        return entity;
    }

    public void setEntity(AcaoOrcamentaria entity) {
        this.entity = entity;
    }

    public Boolean getEmendasVinculadas() {
        return emendasVinculadas;
    }

    public void setEmendasVinculadas(Boolean emendasVinculadas) {
        this.emendasVinculadas = emendasVinculadas;
    }

    public EmendaParlamentar getEmenda() {
        return emenda;
    }

    public void setEmenda(EmendaParlamentar emenda) {
        this.emenda = emenda;
    }

}
