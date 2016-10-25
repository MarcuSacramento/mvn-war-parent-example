package br.gov.mj.side.web.view.propostas;

import javax.inject.Inject;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.util.convert.IConverter;

import br.gov.mj.infra.wicket.converter.MoneyBigDecimalConverter;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoPrograma;
import br.gov.mj.side.web.service.InscricaoProgramaService;
import br.gov.mj.side.web.service.ProgramaService;

public class DadosInscricaoPropostaPanel extends Panel {
    private static final long serialVersionUID = 1L;

    private InscricaoPrograma inscricao;

    @Inject
    private ProgramaService programaService;

    @Inject
    private InscricaoProgramaService inscricaoService;

    public DadosInscricaoPropostaPanel(String id, InscricaoPrograma inscricao) {
        super(id);
        this.inscricao = inscricao;
        initComponents();
    }

    private void initComponents() {

        add(newLabelValorProposta()); // valorProposta
        add(newLabelNomeRepresentante()); // nomeRepresentante
    }

    private Label newLabelValorProposta() {

        inscricao.setProgramasKit(inscricaoService.buscarInscricaoProgramaKit(inscricao));
        inscricao.setProgramasBem(inscricaoService.buscarInscricaoProgramaBem(inscricao));
        return new Label("valorProposta", inscricao.getTotalUtilizado()) {
            private static final long serialVersionUID = 1L;

            @SuppressWarnings("unchecked")
            @Override
            public <C> IConverter<C> getConverter(Class<C> type) {
                return (IConverter<C>) new MoneyBigDecimalConverter();
            }
        };
    }

    private Label newLabelNomeRepresentante() {
        return new Label("nomeRepresentante", inscricao.getPessoaEntidade().getPessoa().getNomePessoa());
    }

}
