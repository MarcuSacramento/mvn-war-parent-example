package br.gov.mj.side.web.util;

import java.util.Iterator;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import br.gov.mj.infra.negocio.persistencia.EnumOrder;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.web.dto.EntidadePesquisaDto;
import br.gov.mj.side.web.service.BeneficiarioService;

public class SortableEntidadeDataProvider extends SortableDataProvider<Entidade, String> {

    private static final long serialVersionUID = 1L;
    private BeneficiarioService beneficiarioService;
    private EntidadePesquisaDto entidadePesquisaDto;

    public SortableEntidadeDataProvider(BeneficiarioService beneficiarioService, EntidadePesquisaDto entidadePesquisaDto) {
        this.entidadePesquisaDto = entidadePesquisaDto;
        this.beneficiarioService = beneficiarioService;
        setSort("nomeEntidade", SortOrder.ASCENDING);
    }

    @Override
    public Iterator<? extends Entidade> iterator(long first, long count) {
        return beneficiarioService.buscarPaginado(entidadePesquisaDto, (int) first, (int) count, getSort().isAscending() ? EnumOrder.ASC : EnumOrder.DESC, getSort().getProperty()).iterator();
    }

    @Override
    public long size() {
        return beneficiarioService.buscarSemPaginacao(entidadePesquisaDto).size();
    }

    @Override
    public IModel<Entidade> model(Entidade object) {
        return new CompoundPropertyModel<Entidade>(object);
    }

}
