package br.gov.mj.side.web.view.propostas;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.util.convert.IConverter;

import br.gov.mj.infra.wicket.converter.MoneyBigDecimalConverter;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.ProgramaRecursoFinanceiro;
import br.gov.mj.side.web.service.ProgramaService;

public class DadosProgramaPropostaPanel extends Panel {
    private static final long serialVersionUID = 1L;

    private Programa programa;

    @Inject
    private ProgramaService programaService;

    public DadosProgramaPropostaPanel(String id, Programa programa) {
        super(id);
        this.programa = programa;

        initComponents();
    }

    private void initComponents() {
        add(newLabelCodigoPrograma());
        add(newLabelNomePrograma());
        add(newLabelValorTotalPrograma());
        add(newOrgaoExecutor());
    }

    private Label newLabelCodigoPrograma() {
        return new Label("codigoIdentificadorProgramaPublicado", programa.getCodigoIdentificadorProgramaPublicado());
    }

    private Label newLabelNomePrograma() {
        return new Label("nomePrograma", programa.getNomePrograma());
    }

    private Label newLabelValorTotalPrograma() {
        return new Label("valorTotalPrograma", getValorTotalPrograma()) {
            private static final long serialVersionUID = 1L;

            @SuppressWarnings("unchecked")
            @Override
            public <C> IConverter<C> getConverter(Class<C> type) {
                return (IConverter<C>) new MoneyBigDecimalConverter();
            }
        };
    }

    private BigDecimal getValorTotalPrograma() {
        BigDecimal total = BigDecimal.ZERO;
        List<ProgramaRecursoFinanceiro> recursos = programaService.buscarProgramaRecursoFinanceiroPeloIdPrograma(programa);
        if (!recursos.isEmpty()) {
            for (ProgramaRecursoFinanceiro recurso : recursos) {
                total = total.add(recurso.getTotal());
            }
        }
        return total;
    }

    private Label newOrgaoExecutor() {
        return new Label("orgaoExecutor", programa.getUnidadeExecutora().getOrgao().getNomeOrgao());

    }
}
