package br.gov.mj.side.web.view.programa.visualizacao;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.util.convert.IConverter;

import br.gov.mj.infra.wicket.converter.MoneyBigDecimalConverter;
import br.gov.mj.side.web.view.components.converters.NumeroProcessoSeiConverter;

public class ProgramaInformacoesGeraisPanel extends Panel {
    private static final long serialVersionUID = 1L;

    public ProgramaInformacoesGeraisPanel(String id) {
        super(id);
        initComponents();
    }

    private void initComponents() {
        add(newLabelCodigoPrograma());
        add(newLabelNomePrograma());
        add(newLabelNomeFantasia());
        add(newLabelDescricao());
        add(newLabelAno());
        add(newLabelFuncao());
        add(newLabelSubfuncao());
        add(newLabelNumeroSEI());
        add(newLabelValorMaximoProposta());
    }

    private Label newLabelCodigoPrograma() {
        return new Label("codigoIdentificadorProgramaPublicado");
    }

    private Label newLabelNomePrograma() {
        return new Label("nomePrograma");
    }

    private Label newLabelNomeFantasia() {
        return new Label("nomeFantasiaPrograma");
    }

    private Label newLabelDescricao() {
        return new Label("descricaoPrograma");
    }

    private Label newLabelAno() {
        return new Label("anoPrograma");
    }

    private Label newLabelFuncao() {
        return new Label("subFuncao.funcao.nomeFuncao");
    }

    private Label newLabelSubfuncao() {
        return new Label("subFuncao.nomeSubFuncao");
    }
    

    private Label newLabelNumeroSEI() {
        return new Label("numeroProcessoSEI") {
            private static final long serialVersionUID = 1L;

            @SuppressWarnings("unchecked")
            @Override
            public <C> IConverter<C> getConverter(Class<C> type) {
                return (IConverter<C>) new NumeroProcessoSeiConverter();
            }
        };
    }
    
    private Label newLabelValorMaximoProposta() {
        return new Label("valorMaximoProposta") {
            private static final long serialVersionUID = 1L;

            @SuppressWarnings("unchecked")
            @Override
            public <C> IConverter<C> getConverter(Class<C> type) {
                return (IConverter<C>) new MoneyBigDecimalConverter();
            }
        };
    }
    

}
