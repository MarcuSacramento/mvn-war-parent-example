package br.gov.mj.side.web.util;

import java.util.Iterator;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import br.gov.mj.infra.negocio.persistencia.EnumOrder;
import br.gov.mj.side.web.dto.TermoDoacaoDto;
import br.gov.mj.side.web.dto.enderecoLicitacao.DoacoesConcluidasDto;
import br.gov.mj.side.web.service.TermoDoacaoService;

public class SortableDoacaoConluidaDataProvider extends SortableDataProvider<DoacoesConcluidasDto, String> {
    private static final long serialVersionUID = 1L;
    private TermoDoacaoService termoDoacaoService;
    private TermoDoacaoDto termoDoacaoDto;

    public SortableDoacaoConluidaDataProvider(TermoDoacaoService termoDoacaoService, TermoDoacaoDto termoDoacaoDto) {
        this.termoDoacaoService = termoDoacaoService;
        this.termoDoacaoDto = termoDoacaoDto;
        setSort("nomeBeneficiario", SortOrder.ASCENDING);
    }

    @Override
    public Iterator<? extends DoacoesConcluidasDto> iterator(long first, long count) {
        termoDoacaoDto.setOrder(getSort().isAscending() ? EnumOrder.ASC : EnumOrder.DESC);
        termoDoacaoDto.setPropertyOrder(getSort().getProperty());
        return termoDoacaoService.buscarTodosItensDoados(termoDoacaoDto).iterator();
    }

    @Override
    public long size() {
        return termoDoacaoService.buscarTodosItensDoados(termoDoacaoDto).size();
    }

    @Override
    public IModel<DoacoesConcluidasDto> model(DoacoesConcluidasDto object) {
        return new CompoundPropertyModel<DoacoesConcluidasDto>(object);
    }
}
