package br.gov.mj.side.web.util;

import java.util.Iterator;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import br.gov.mj.infra.negocio.persistencia.EnumOrder;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.licitacao.contrato.notaRemessa.NotaRemessaOrdemFornecimentoContrato;
import br.gov.mj.side.web.service.NotaRemessaService;

public class SortableRecebimentoDataProvider extends SortableDataProvider<NotaRemessaOrdemFornecimentoContrato, String> {

    private static final long serialVersionUID = 1L;
    private NotaRemessaService notaRemessaService;
    private Programa programa;
    private Entidade entidadeEscolhida;

    public SortableRecebimentoDataProvider(NotaRemessaService notaRemessaService, Programa programa, Entidade entidadeEscolhida) {
        this.notaRemessaService = notaRemessaService;
        this.programa = programa;
        this.entidadeEscolhida = entidadeEscolhida;
        setSort("notaRemessaOrdemFornecimento.numeroNotaRemessa", SortOrder.ASCENDING);
    }

    @Override
    public Iterator<? extends NotaRemessaOrdemFornecimentoContrato> iterator(long first, long count) {
        return notaRemessaService.buscarNotasFiscaisPeloProgramaEBeneficiarioPaginadoOrdenado(programa, entidadeEscolhida, (int) first, (int) count, getSort().isAscending() ? EnumOrder.ASC : EnumOrder.DESC, getSort().getProperty()).iterator();
    }

    @Override
    public long size() {
        return notaRemessaService.buscarNotasFiscaisPeloProgramaEBeneficiario(programa, entidadeEscolhida).size();
    }

    @Override
    public IModel<NotaRemessaOrdemFornecimentoContrato> model(NotaRemessaOrdemFornecimentoContrato object) {
        return new CompoundPropertyModel<NotaRemessaOrdemFornecimentoContrato>(object);
    }

}
