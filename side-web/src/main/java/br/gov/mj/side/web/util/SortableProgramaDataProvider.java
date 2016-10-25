package br.gov.mj.side.web.util;

import java.util.Iterator;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import br.gov.mj.infra.negocio.persistencia.EnumOrder;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.web.dto.ProgramaPesquisaDto;
import br.gov.mj.side.web.service.ProgramaService;

public class SortableProgramaDataProvider extends SortableDataProvider<Programa, String> {

    private static final long serialVersionUID = 1L;
    private ProgramaService programaService;
    private ProgramaPesquisaDto programaPesquisaDto;

    public SortableProgramaDataProvider(ProgramaService programaService, ProgramaPesquisaDto programaPesquisaDto) {
        this.programaPesquisaDto = programaPesquisaDto;
        this.programaService = programaService;
        setSort("nomePrograma", SortOrder.ASCENDING);
    }

    @Override
    public Iterator<? extends Programa> iterator(long first, long count) {
        return programaService.buscarPaginado(programaPesquisaDto, (int) first, (int) count, getSort().isAscending() ? EnumOrder.ASC : EnumOrder.DESC, getSort().getProperty()).iterator();

    }

    @Override
    public long size() {
        return programaService.buscarSemPaginacao(programaPesquisaDto).size();
    }

    @Override
    public IModel<Programa> model(Programa object) {
        return new CompoundPropertyModel<Programa>(object);
    }

}
