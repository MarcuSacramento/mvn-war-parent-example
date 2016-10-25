package br.gov.mj.side.web.view.planejarLicitacao;

import java.time.LocalDate;

import javax.inject.Inject;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.StringValidator;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraLocalDateTextField;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.licitacao.LicitacaoPrograma;
import br.gov.mj.side.web.service.ProgramaService;
import br.gov.mj.side.web.util.Constants;

public class PanelInformacoesBasicasLicitacao extends Panel {
    private static final long serialVersionUID = 1L;

    private PanelDadosLicitacao panelDadosLicitacao;
    
    private Button buttonSalvar;
    private Page backPage;
    private PlanejamentoLicitacaoPage licitacaoPage;

    private boolean readOnly;
    private LicitacaoPrograma licitacaoPrograma;
    private Programa programa;
    private LocalDate dataInicio;
    private LocalDate dataFinal;

    @Inject
    private ComponentFactory componentFactory;

    @Inject
    private ProgramaService programaService;

    public PanelInformacoesBasicasLicitacao(String id, LicitacaoPrograma licitacaoPrograma, Page backPage,boolean readOnly) {
        super(id);
        this.licitacaoPrograma = licitacaoPrograma;
        this.readOnly = readOnly;
        this.backPage = backPage;

        initVariaveis();
        initComponents();
    }

    private void initVariaveis(){
        licitacaoPage = (PlanejamentoLicitacaoPage) backPage;
    }
    
    private void initComponents() {
        add(panelDadosLicitacao = new PanelDadosLicitacao("panelDadosLicitacao"));
    }
    

    /*
     * OS PAINEIS SÃO ADICIONADOS ABAIXO
     */

    private class PanelDadosLicitacao extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelDadosLicitacao(String id) {
            super(id);
            setOutputMarkupId(true);

            add(newTextAreaObjeto());
            add(newTextAreaJustificativa());
            add(newTextAreaEspecificacao());
            add(newTextAreaRecebimento());
            add(newTextAreaPrazo());
            add(newTextAreaMetodologia());
            add(newDateTextFieldPeriodo1());
            add(newDateTextFieldPeriodo2());
        }
    }

    /*
     * OS COMPONENTES DOS PAINEIS ESTÃO ABAIXO
     */

    public TextArea<String> newTextAreaObjeto() {
        TextArea<String> text = new TextArea<String>("txtObjeto", new PropertyModel<String>(licitacaoPrograma, "objeto"));
        text.setLabel(Model.of("Objeto"));
        text.setOutputMarkupId(true);
        text.add(StringValidator.maximumLength(Constants.TAMANHO_CARACTERES_CAMPO_TEXT_AREA));
        text.setEnabled(!modoVisualizacao());

        return text;
    }

    public TextArea<String> newTextAreaJustificativa() {
        TextArea<String> text = new TextArea<String>("txtJustificativa", new PropertyModel<String>(licitacaoPrograma, "justificativa"));
        text.setLabel(Model.of("Justificativa"));
        text.setOutputMarkupId(true);
        text.add(StringValidator.maximumLength(Constants.TAMANHO_CARACTERES_CAMPO_TEXT_AREA));
        text.setEnabled(!modoVisualizacao());

        return text;
    }

    public TextArea<String> newTextAreaEspecificacao() {
        TextArea<String> text = new TextArea<String>("txtEspecificacao", new PropertyModel<String>(licitacaoPrograma, "especificacoesEQuantidadeDoObjeto"));
        text.setLabel(Model.of("Especificações e Quantidade do Objeto"));
        text.setOutputMarkupId(true);
        text.add(StringValidator.maximumLength(Constants.TAMANHO_CARACTERES_CAMPO_TEXT_AREA));
        text.setEnabled(!modoVisualizacao());

        return text;
    }

    public TextArea<String> newTextAreaRecebimento() {
        TextArea<String> text = new TextArea<String>("txtRecebimento", new PropertyModel<String>(licitacaoPrograma, "recebimentoEAceitacaoDosMateriais"));
        text.setLabel(Model.of("Recebimento e Aceitação dos Materiais"));
        text.setOutputMarkupId(true);
        text.add(StringValidator.maximumLength(Constants.TAMANHO_CARACTERES_CAMPO_TEXT_AREA));
        text.setEnabled(!modoVisualizacao());

        return text;
    }

    public TextArea<String> newTextAreaPrazo() {
        TextArea<String> text = new TextArea<String>("txtPrazo", new PropertyModel<String>(licitacaoPrograma, "prazoLocalEFormaDeEntrega"));
        text.setLabel(Model.of("Prazo, Local e Forma de Entrega"));
        text.setOutputMarkupId(true);
        text.add(StringValidator.maximumLength(Constants.TAMANHO_CARACTERES_CAMPO_TEXT_AREA));
        text.setEnabled(!modoVisualizacao());

        return text;
    }

    public TextArea<String> newTextAreaMetodologia() {
        TextArea<String> text = new TextArea<String>("txtMetodologia", new PropertyModel<String>(licitacaoPrograma, "metodologiaDeAvaliacaoEAceiteDosMateriais"));
        text.setLabel(Model.of("Metodologia de Avaliação e Aceite dos Materiais"));
        text.setOutputMarkupId(true);
        text.add(StringValidator.maximumLength(Constants.TAMANHO_CARACTERES_CAMPO_TEXT_AREA));
        text.setEnabled(!modoVisualizacao());

        return text;
    }

    private InfraLocalDateTextField newDateTextFieldPeriodo1() {
        InfraLocalDateTextField field = componentFactory.newDateTextFieldWithDatePicker("dataInicio", "Período de Execução (Inicial)", false, new PropertyModel<LocalDate>(licitacaoPrograma, "dataInicialPeriodoExecucao"), "dd/MM/yyyy", "pt-BR");
        field.setEnabled(!modoVisualizacao());
        return field;
    }

    private InfraLocalDateTextField newDateTextFieldPeriodo2() {
        InfraLocalDateTextField field = componentFactory.newDateTextFieldWithDatePicker("dataFinal", "Período de Execução (Final)", false, new PropertyModel<LocalDate>(licitacaoPrograma, "dataFinalPeriodoExecucao"), "dd/MM/yyyy", "pt-BR");
        field.setEnabled(!modoVisualizacao());
        return field;
    }

    /*
     * O PROVIDER SERÁ IMPLEMENTADO ABAIXO
     */

    /*
     * AS AÇÕES SERÃO IMPLEMENTADAS ABAIXO
     */
    
    /*
     * Os programas que serão listados no DropDown são os que já passaram pela geração da segunda lista de avaliação
     */
    
    private boolean modoVisualizacao()
    {
        return readOnly;
    }
}
