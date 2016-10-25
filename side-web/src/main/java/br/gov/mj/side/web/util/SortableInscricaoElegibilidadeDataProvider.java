package br.gov.mj.side.web.util;

import java.util.Iterator;

import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import br.gov.mj.side.entidades.enums.EnumStatusInscricao;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoPrograma;
import br.gov.mj.side.web.service.InscricaoProgramaService;

public class SortableInscricaoElegibilidadeDataProvider extends SortableDataProvider<InscricaoPrograma, String> {
    private static final long serialVersionUID = 1L;

    private InscricaoProgramaService entityService;
    private Programa entity;
    private EnumStatusInscricao statusBuscar;

    public SortableInscricaoElegibilidadeDataProvider(Programa entity, InscricaoProgramaService entityService, EnumStatusInscricao statusBuscar) {
        this.setEntity(entity);
        this.setEntityService(entityService);
        this.statusBuscar = statusBuscar;
    }

    @Override
    public Iterator<InscricaoPrograma> iterator(long first, long count) {

        return entityService.gerarListaClassificacaoElegibilidadePaginado(getEntity(), statusBuscar, (int) first, (int) count).iterator();
    }

    @Override
    public long size() {
        return entityService.countClassificacaoElegibilidade(getEntity(), statusBuscar);
    }

    @Override
    public IModel<InscricaoPrograma> model(InscricaoPrograma object) {
        return new CompoundPropertyModel<InscricaoPrograma>(object);
    }

    public InscricaoProgramaService getEntityService() {
        return entityService;
    }

    public void setEntityService(InscricaoProgramaService entityService) {
        this.entityService = entityService;
    }

    public Programa getEntity() {
        return entity;
    }

    public void setEntity(Programa entity) {
        this.entity = entity;
    }

}
