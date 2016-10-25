package br.gov.mj.side.web.util;

import java.util.Iterator;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import br.gov.mj.infra.negocio.persistencia.EnumOrder;
import br.gov.mj.side.entidades.programa.licitacao.contrato.TermoDoacao;
import br.gov.mj.side.web.dto.TermoDoacaoDto;
import br.gov.mj.side.web.service.TermoDoacaoService;

public class SortableDoacaoDataProvider extends SortableDataProvider<TermoDoacao, String> {

    private static final long serialVersionUID = 1L;
    private TermoDoacaoService termoDoacaoService;
    private TermoDoacaoDto termoDoacaoDto;

    public SortableDoacaoDataProvider(TermoDoacaoService termoDoacaoService, TermoDoacaoDto termoDoacaoDto) {
        this.termoDoacaoService = termoDoacaoService;
        this.termoDoacaoDto = termoDoacaoDto;
        setSort("nomeBeneficiario", SortOrder.ASCENDING);
    }

    @Override
    public Iterator<? extends TermoDoacao> iterator(long first, long count) {
        return termoDoacaoService.buscarPaginado(termoDoacaoDto, (int) first, (int) count, getSort().isAscending() ? EnumOrder.ASC : EnumOrder.DESC, getSort().getProperty()).iterator();
    }

    @Override
    public long size() {
        return termoDoacaoService.buscarSemPaginacao(termoDoacaoDto).size();
    }

    @Override
    public IModel<TermoDoacao> model(TermoDoacao object) {
        return new CompoundPropertyModel<TermoDoacao>(object);
    }
}
