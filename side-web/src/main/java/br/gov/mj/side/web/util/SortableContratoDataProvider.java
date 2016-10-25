package br.gov.mj.side.web.util;

import java.util.Iterator;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import br.gov.mj.infra.negocio.persistencia.EnumOrder;
import br.gov.mj.side.entidades.programa.licitacao.contrato.Contrato;
import br.gov.mj.side.web.dto.ContratoDto;
import br.gov.mj.side.web.service.ContratoService;

public class SortableContratoDataProvider extends SortableDataProvider<Contrato, String> {
    private static final long serialVersionUID = 1L;

    private ContratoService contratoService;
    private ContratoDto contratoDto;

    public SortableContratoDataProvider(ContratoService contratoService, ContratoDto contratoDto) {
        this.contratoService = contratoService;
        this.contratoDto = contratoDto;
        setSort("numeroContrato", SortOrder.ASCENDING);
    }

    @Override
    public Iterator<? extends Contrato> iterator(long first, long count) {
        return contratoService.buscarPaginado(contratoDto, (int) first, (int) count, getSort().isAscending() ? EnumOrder.ASC : EnumOrder.DESC, getSort().getProperty()).iterator();
    }

    @Override
    public long size() {
        return contratoService.buscarSemPaginacao(contratoDto).size();
    }

    @Override
    public IModel<Contrato> model(Contrato object) {
        return new CompoundPropertyModel<Contrato>(object);
    }

}
