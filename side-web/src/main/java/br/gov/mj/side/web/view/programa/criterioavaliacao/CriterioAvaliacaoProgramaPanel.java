package br.gov.mj.side.web.view.programa.criterioavaliacao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.StringValidator;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraAjaxConfirmButton;
import br.gov.mj.infra.wicket.component.pagination.InfraAjaxPagingNavigator;
import br.gov.mj.side.entidades.enums.EnumTipoResposta;
import br.gov.mj.side.entidades.programa.ProgramaCriterioAvaliacao;
import br.gov.mj.side.entidades.programa.ProgramaCriterioAvaliacaoOpcaoResposta;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.view.components.EntityDataProvider;
import br.gov.mj.side.web.view.programa.ProgramaPage;

public class CriterioAvaliacaoProgramaPanel extends Panel {

    private static final long serialVersionUID = 1L;

    private Page backPage;
    private List<ProgramaCriterioAvaliacao> criteriosAvaliacao;
    private ProgramaCriterioAvaliacao criterioAvaliacao;
    private ProgramaCriterioAvaliacaoOpcaoResposta opcaoResposta;
    private OpcaoRespostaPanel opcaoRespostaPanel;
    private PanelDadosCriterioAvaliacao dadosCriterioAvaliacaoPanel;
    private PanelDataViewCriterio dataViewPanel;
    private PanelBotoes botoesPanel;
    private Label feedbackLabel;
    private boolean modoEdicao = false;

    private ProgramaCriterioAvaliacao criterioAvaliacaoSelecionado;

    @Inject
    private ComponentFactory componentFactory;

    public CriterioAvaliacaoProgramaPanel(String id, Page backPage,Boolean readOnly) {
        super(id);
        setOutputMarkupId(true);
        this.backPage = backPage;
        this.criteriosAvaliacao = ((ProgramaPage) backPage).getForm().getModelObject().getCriteriosAvaliacao();
        this.modoEdicao = readOnly;

        initVariables();
        initComponents();
    }

    private void initVariables() {
        criterioAvaliacao = new ProgramaCriterioAvaliacao();
        opcaoResposta = new ProgramaCriterioAvaliacaoOpcaoResposta();
        criterioAvaliacao.setCriteriosAvaliacaoOpcaoResposta(new ArrayList<ProgramaCriterioAvaliacaoOpcaoResposta>());
    }

    private void initComponents() {

        feedbackLabel = newFeedbackLabel();
        add(feedbackLabel);
        
        

        dadosCriterioAvaliacaoPanel = new PanelDadosCriterioAvaliacao("dadosCriterio");
        dadosCriterioAvaliacaoPanel.setEnabled(!modoEdicao);
        add(dadosCriterioAvaliacaoPanel);

        dataViewPanel = new PanelDataViewCriterio("criterios");
        add(dataViewPanel);
        
        botoesPanel = new PanelBotoes("botoes");
        botoesPanel.setEnabled(!modoEdicao);
        add(botoesPanel);
    }

    private Label newFeedbackLabel() {
        Label lbl = new Label("feedbackLabel", "");
        lbl.setEscapeModelStrings(false);
        lbl.setOutputMarkupId(true);
        return lbl;
    }

    private TextField<String> newTextFieldNome() {
        return componentFactory.newTextField("nomeCriterioAvaliacao", "Nome", false, new PropertyModel<String>(criterioAvaliacao, "nomeCriterioAvaliacao"));
    }

    private TextArea<String> newTextAreaDescricao() {
        TextArea<String> textArea = new TextArea<String>("descricaoCriterioAvaliacao", new PropertyModel<String>(criterioAvaliacao, "descricaoCriterioAvaliacao"));
        textArea.setLabel(Model.of("Descrição"));
        textArea.add(StringValidator.maximumLength(Constants.TAMANHO_CARACTERES_CAMPO_TEXT_AREA));
        return textArea;
    }

    private TextArea<String> newTextAreaFormaVerificacao() {
        TextArea<String> textArea = new TextArea<String>("formaVerificacao", new PropertyModel<String>(criterioAvaliacao, "formaVerificacao"));
        textArea.setLabel(Model.of("Forma de Verificação"));
        textArea.add(StringValidator.maximumLength(Constants.TAMANHO_CARACTERES_CAMPO_TEXT_AREA));
        return textArea;
    }

    private DropDownChoice<EnumTipoResposta> newDropDownTipoResposta() {
        return componentFactory.newDropDownChoice("tipoResposta", "Tipo de Resposta", false, "valor", "descricao", new PropertyModel<EnumTipoResposta>(criterioAvaliacao, "tipoResposta"), Arrays.asList(EnumTipoResposta.values()), (target) -> exibirOpcoesResposta(target));
    }

    private void exibirOpcoesResposta(AjaxRequestTarget target) {
        // remove todos as opções caso seja alterado o tipo para diferente de
        // Lista de seleção
        if (!isTipoSelecionadoListaSelecao()) {
            criterioAvaliacao.getCriteriosAvaliacaoOpcaoResposta().clear();
        }
        opcaoRespostaPanel.setVisible(isTipoSelecionadoListaSelecao());
        target.add(opcaoRespostaPanel);
    }

    private TextField<String> newTextFieldOpcaoResposta() {
        return componentFactory.newTextField("descricaoOpcaoResposta", "Opções de Resposta", false, new PropertyModel<String>(opcaoResposta, "descricaoOpcaoResposta"));
    }

    private DropDownChoice<Integer> newTextFieldNota() {
        return componentFactory.newDropDownChoice("notaOpcaoResposta", "Nota", false, "", "", new PropertyModel<Integer>(opcaoResposta, "notaOpcaoResposta"), Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10), null);
    }

    private DataView<ProgramaCriterioAvaliacaoOpcaoResposta> newDataViewOpcaoRespostaNota() {
        return new DataView<ProgramaCriterioAvaliacaoOpcaoResposta>("respostas", new EntityDataProvider<ProgramaCriterioAvaliacaoOpcaoResposta>(criterioAvaliacao.getCriteriosAvaliacaoOpcaoResposta())) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<ProgramaCriterioAvaliacaoOpcaoResposta> item) {
                item.add(new Label("descricaoOpcaoResposta"));
                item.add(new Label("notaOpcaoResposta"));
                item.add(newButtonExcluirOpcaoResposta(item));
            }

        };
    }

    private InfraAjaxConfirmButton newButtonExcluirOpcaoResposta(Item<ProgramaCriterioAvaliacaoOpcaoResposta> item) {
        return componentFactory.newAJaxConfirmButton("btnExcluirOpcaoResposta", "MT004", null, (target, formz) -> excluirOpcaoResposta(target, item));
    }

    private void excluirOpcaoResposta(AjaxRequestTarget target, Item<ProgramaCriterioAvaliacaoOpcaoResposta> item) {
        criterioAvaliacao.getCriteriosAvaliacaoOpcaoResposta().remove(item.getModelObject());
        target.add(opcaoRespostaPanel);
    }

    private DropDownChoice<Integer> newDropDownPeso() {
        return componentFactory.newDropDownChoice("pesoResposta", "Peso", false, "", "", new PropertyModel<Integer>(criterioAvaliacao, "pesoResposta"), Arrays.asList(0, 1, 2, 3, 4, 5), null);
    }

    private DropDownChoice<Boolean> newDropDownAnexoObrigatorio() {
        DropDownChoice<Boolean> dropDownChoice = new DropDownChoice<Boolean>("possuiObrigatoriedadeDeAnexo", new PropertyModel<Boolean>(criterioAvaliacao, "possuiObrigatoriedadeDeAnexo"), Arrays.asList(Boolean.TRUE, Boolean.FALSE));
        dropDownChoice.setChoiceRenderer(new IChoiceRenderer<Boolean>() {
            private static final long serialVersionUID = 1L;

            public String getDisplayValue(Boolean object) {
                return object ? "Sim" : "Não";
            };

            public String getIdValue(Boolean object, int index) {
                return object.toString();
            };
        });
        dropDownChoice.setLabel(Model.of("Anexo Obrigatório"));
        return dropDownChoice;
    }

    private DropDownChoice<Boolean> newDropDownACriterioDesempate() {
        DropDownChoice<Boolean> dropDownChoice = new DropDownChoice<Boolean>("utilizadoParaCriterioDesempate", new PropertyModel<Boolean>(criterioAvaliacao, "utilizadoParaCriterioDesempate"), Arrays.asList(Boolean.TRUE, Boolean.FALSE));
        dropDownChoice.setChoiceRenderer(new IChoiceRenderer<Boolean>() {
            private static final long serialVersionUID = 1L;

            public String getDisplayValue(Boolean object) {
                return object ? "Sim" : "Não";
            };

            public String getIdValue(Boolean object, int index) {
                return object.toString();
            };
        });
        dropDownChoice.setLabel(Model.of("Anexo Obrigatório"));
        return dropDownChoice;
    }

    private Button newButtonAdicionar() {
        AjaxButton btnAdicionar = new AjaxButton("btnAdicionar") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form) {
                adicionar(target);
            }
        };
        btnAdicionar.setVisible(!isModoEdicao());
        return btnAdicionar;
    }

    private Button newButtonAdicionarOpcaoResposta() {
        return new AjaxButton("btnAdicionarOpcaoResposta") {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                adicionarOpcaoResposta(target);
            }
        };

    }

    private void adicionar(AjaxRequestTarget target) {
        if (validar(target)) {
            criteriosAvaliacao.add(criterioAvaliacao);
            criterioAvaliacao = new ProgramaCriterioAvaliacao();
            atualizarComponentes();
            target.add(dadosCriterioAvaliacaoPanel, dataViewPanel);
        }
    }

    private void atualizarComponentes() {
        dadosCriterioAvaliacaoPanel.addOrReplace(newTextFieldNome());
        dadosCriterioAvaliacaoPanel.addOrReplace(newTextAreaDescricao());
        dadosCriterioAvaliacaoPanel.addOrReplace(newTextAreaFormaVerificacao());
        dadosCriterioAvaliacaoPanel.addOrReplace(newDropDownTipoResposta());
        dadosCriterioAvaliacaoPanel.addOrReplace(newDropDownPeso());
        dadosCriterioAvaliacaoPanel.addOrReplace(newDropDownAnexoObrigatorio());
        dadosCriterioAvaliacaoPanel.addOrReplace(newDropDownACriterioDesempate());
        opcaoResposta = new ProgramaCriterioAvaliacaoOpcaoResposta();
        opcaoRespostaPanel = newOpcaoRespostaPanel();
        dadosCriterioAvaliacaoPanel.addOrReplace(opcaoRespostaPanel);
        botoesPanel.addOrReplace(newButtonCancelarAlteracao());
        botoesPanel.addOrReplace(newButtonSalvarAlteracao());
        botoesPanel.addOrReplace(newButtonAdicionar());
    }

    private void adicionarOpcaoResposta(AjaxRequestTarget target) {
        if(validarOpcaoResposta(target)){
            criterioAvaliacao.getCriteriosAvaliacaoOpcaoResposta().add(opcaoResposta);
            opcaoResposta = new ProgramaCriterioAvaliacaoOpcaoResposta();
            opcaoRespostaPanel.addOrReplace(newTextFieldNota());
            opcaoRespostaPanel.addOrReplace(newTextFieldOpcaoResposta());
            target.add(opcaoRespostaPanel);
        }
    }

    private InfraAjaxConfirmButton newButtonExcluir(Item<ProgramaCriterioAvaliacao> item) {
        InfraAjaxConfirmButton button = componentFactory.newAJaxConfirmButton("btnExcluir", "MT003", null, (target, formz) -> excluir(target, item));
        button.setEnabled(!modoEdicao);
        return button;
    }

    private void excluir(AjaxRequestTarget target, Item<ProgramaCriterioAvaliacao> item) {
        criteriosAvaliacao.remove(item.getModelObject());
        target.add(dataViewPanel);
    }

    public Page getBackPage() {
        return backPage;
    }

    public void setBackPage(Page backPage) {
        this.backPage = backPage;
    }

    public ProgramaCriterioAvaliacao getProgramaCriterioAvaliacao() {
        return criterioAvaliacao;
    }

    public void setProgramaCriterioAvaliacao(ProgramaCriterioAvaliacao programaCriterioAvaliacao) {
        this.criterioAvaliacao = programaCriterioAvaliacao;
    }

    public ProgramaCriterioAvaliacaoOpcaoResposta getOpcaoResposta() {
        return opcaoResposta;
    }

    public void setOpcaoResposta(ProgramaCriterioAvaliacaoOpcaoResposta opcaoResposta) {
        this.opcaoResposta = opcaoResposta;
    }

    private class OpcaoRespostaPanel extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public OpcaoRespostaPanel(String id) {
            super(id);
            add(newTextFieldOpcaoResposta());
            add(newTextFieldNota());
            add(newDataViewOpcaoRespostaNota());
            add(newButtonAdicionarOpcaoResposta());
            setVisible(isTipoSelecionadoListaSelecao());
        }
    }

    private boolean isTipoSelecionadoListaSelecao() {
        return EnumTipoResposta.LISTA_SELECAO.equals(criterioAvaliacao.getTipoResposta());
    }

    private DataView<ProgramaCriterioAvaliacao> newDataViewCriterioAvaliacao() {
        return new DataView<ProgramaCriterioAvaliacao>("criteriosAvaliacao", new EntityDataProvider<ProgramaCriterioAvaliacao>(criteriosAvaliacao)) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<ProgramaCriterioAvaliacao> item) {
                item.add(new Label("nomeCriterioAvaliacao"));
                item.add(new Label("descricaoCriterioAvaliacao"));
                item.add(new Label("formaVerificacao"));
                item.add(new Label("tipoResposta.descricao"));
                item.add(new Label("pesoResposta"));
                item.add(new Label("opcoesResposta", getOpcoesRespostaDescricao(item)).setEscapeModelStrings(false));
                item.add(new Label("possuiObrigatoriedadeDeAnexo", item.getModelObject().getPossuiObrigatoriedadeDeAnexo() ? "Sim" : "Não"));
                item.add(new Label("utilizadoParaCriterioDesempate", item.getModelObject().getUtilizadoParaCriterioDesempate() ? "Sim" : "Não"));
                item.add(newButtonExcluir(item));
                item.add(newButtonAlterar(item));
            }
        };
    }

    protected String getOpcoesRespostaDescricao(Item<ProgramaCriterioAvaliacao> item) {
        String retorno = "";
        List<ProgramaCriterioAvaliacaoOpcaoResposta> listOpcoesRespostas = item.getModelObject().getCriteriosAvaliacaoOpcaoResposta();
        if (!listOpcoesRespostas.isEmpty()) {
            for (ProgramaCriterioAvaliacaoOpcaoResposta programaCriterioAvaliacaoOpcaoResposta : listOpcoesRespostas) {
                retorno = retorno + ", " + programaCriterioAvaliacaoOpcaoResposta.getDescricaoOpcaoResposta() + "<span style='color:red;'> (" + programaCriterioAvaliacaoOpcaoResposta.getNotaOpcaoResposta() + ")</span>";
            }
            retorno = retorno.substring(2);
        } else {
            retorno = retorno + "-";
        }
        return retorno;
    }

    private class PanelDataViewCriterio extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelDataViewCriterio(String id) {
            super(id);
            setOutputMarkupId(true);

            DataView<ProgramaCriterioAvaliacao> dataViewCriteriosAvaliacao = newDataViewCriterioAvaliacao();
            dataViewCriteriosAvaliacao.setItemsPerPage(5l);
            add(dataViewCriteriosAvaliacao);
            add(new InfraAjaxPagingNavigator("paginator", dataViewCriteriosAvaliacao));

        }
    }   

    private class PanelDadosCriterioAvaliacao extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelDadosCriterioAvaliacao(String id) {
            super(id);
            setOutputMarkupId(true);
            opcaoRespostaPanel = newOpcaoRespostaPanel();
            add(newTextFieldNome());
            add(newTextAreaDescricao());
            add(newTextAreaFormaVerificacao());
            add(newDropDownTipoResposta());
            add(opcaoRespostaPanel);
            add(newDropDownPeso());
            add(newDropDownAnexoObrigatorio());
            add(newDropDownACriterioDesempate());
        }
    }

    private class PanelBotoes extends WebMarkupContainer{
        private static final long serialVersionUID = 1L;

        public PanelBotoes(String id) {
            super(id);
            add(newButtonAdicionar());
            add(newButtonSalvarAlteracao());
            add(newButtonCancelarAlteracao());
        }
        
    }
    
    private OpcaoRespostaPanel newOpcaoRespostaPanel() {
        return new OpcaoRespostaPanel("opcaoRespostaPanel");
    }

    public AjaxButton newButtonAlterar(Item<ProgramaCriterioAvaliacao> item) {
        AjaxButton button = new AjaxButton("btnAlterar") {
            private static final long serialVersionUID = 1L;

            @SuppressWarnings("rawtypes")
            @Override
            public void onSubmit(AjaxRequestTarget target, Form form) {
                alterar(target, item);
            }
        };
        button.setEnabled(!modoEdicao);
        return button;
    }

    private AjaxButton newButtonCancelarAlteracao() {
        AjaxButton btnCancelar = new AjaxButton("btnCancelarAlteracao") {
            private static final long serialVersionUID = 1L;

            @SuppressWarnings("rawtypes")
            @Override
            public void onSubmit(AjaxRequestTarget target, Form form) {
                cancelarAlteracao(target);
            }

        };
        btnCancelar.setDefaultFormProcessing(false); // Não seta o model
        btnCancelar.setVisible(isModoEdicao());
        return btnCancelar;
    }

    private void cancelarAlteracao(AjaxRequestTarget target) {
        criterioAvaliacao = new ProgramaCriterioAvaliacao();
        setModoEdicao(false);
        atualizarComponentes();
        target.add(dadosCriterioAvaliacaoPanel, dataViewPanel,botoesPanel);
    }

    private AjaxButton newButtonSalvarAlteracao() {
        AjaxButton btnSalvarAlteracao = new AjaxButton("btnSalvarAlteracao") {
            private static final long serialVersionUID = 1L;

            @SuppressWarnings("rawtypes")
            @Override
            public void onSubmit(AjaxRequestTarget target, Form form) {
                salvarAlteracao(target);
            }
        };
        btnSalvarAlteracao.setVisible(isModoEdicao());
        return btnSalvarAlteracao;
    }

    private void salvarAlteracao(AjaxRequestTarget target) {

        if (validar(target)) {
            criterioAvaliacaoSelecionado.setId(criterioAvaliacao.getId());
            criterioAvaliacaoSelecionado.setNomeCriterioAvaliacao(criterioAvaliacao.getNomeCriterioAvaliacao());
            criterioAvaliacaoSelecionado.setDescricaoCriterioAvaliacao(criterioAvaliacao.getDescricaoCriterioAvaliacao());
            criterioAvaliacaoSelecionado.setFormaVerificacao(criterioAvaliacao.getFormaVerificacao());
            criterioAvaliacaoSelecionado.setTipoResposta(criterioAvaliacao.getTipoResposta());
            criterioAvaliacaoSelecionado.setPesoResposta(criterioAvaliacao.getPesoResposta());
            criterioAvaliacaoSelecionado.setPossuiObrigatoriedadeDeAnexo(criterioAvaliacao.getPossuiObrigatoriedadeDeAnexo());
            criterioAvaliacaoSelecionado.setUtilizadoParaCriterioDesempate(criterioAvaliacao.getUtilizadoParaCriterioDesempate());
            criterioAvaliacaoSelecionado.setCriteriosAvaliacaoOpcaoResposta(criterioAvaliacao.getCriteriosAvaliacaoOpcaoResposta());

            criterioAvaliacao = new ProgramaCriterioAvaliacao();
            criterioAvaliacaoSelecionado = null;
            setModoEdicao(false);
            atualizarComponentes();
            target.add(dadosCriterioAvaliacaoPanel, dataViewPanel, botoesPanel);
        }
    }

    private void alterar(AjaxRequestTarget target, Item<ProgramaCriterioAvaliacao> item) {
        criterioAvaliacaoSelecionado = item.getModelObject();

        criterioAvaliacao = new ProgramaCriterioAvaliacao();
        criterioAvaliacao.setId(criterioAvaliacaoSelecionado.getId());
        criterioAvaliacao.setNomeCriterioAvaliacao(criterioAvaliacaoSelecionado.getNomeCriterioAvaliacao());
        criterioAvaliacao.setDescricaoCriterioAvaliacao(criterioAvaliacaoSelecionado.getDescricaoCriterioAvaliacao());
        criterioAvaliacao.setFormaVerificacao(criterioAvaliacaoSelecionado.getFormaVerificacao());
        criterioAvaliacao.setTipoResposta(criterioAvaliacaoSelecionado.getTipoResposta());
        criterioAvaliacao.setPesoResposta(criterioAvaliacaoSelecionado.getPesoResposta());
        criterioAvaliacao.setPossuiObrigatoriedadeDeAnexo(criterioAvaliacaoSelecionado.getPossuiObrigatoriedadeDeAnexo());
        criterioAvaliacao.setUtilizadoParaCriterioDesempate(criterioAvaliacaoSelecionado.getUtilizadoParaCriterioDesempate());
        criterioAvaliacao.setCriteriosAvaliacaoOpcaoResposta(new ArrayList<ProgramaCriterioAvaliacaoOpcaoResposta>(criterioAvaliacaoSelecionado.getCriteriosAvaliacaoOpcaoResposta()));

        setModoEdicao(true);
        atualizarComponentes();
        target.add(dadosCriterioAvaliacaoPanel,botoesPanel);
    }

    public boolean validar(AjaxRequestTarget target) {
        boolean validar = true;

        String msg = "";

        if (StringUtils.isBlank(criterioAvaliacao.getNomeCriterioAvaliacao())) {
            msg += "<p><li> Informe um 'Nome' para o critério.</li><p />";
            validar = false;
        }

        if (StringUtils.isBlank(criterioAvaliacao.getDescricaoCriterioAvaliacao())) {
            msg += "<p><li> Informe uma 'Descrição' para o critério.</li><p />";
            validar = false;
        }

        if (StringUtils.isBlank(criterioAvaliacao.getFormaVerificacao())) {
            msg += "<p><li> Informe uma 'Forma de Verificação' para o critério.</li><p />";
            validar = false;
        }

        if (criterioAvaliacao.getPesoResposta() == null) {
            msg += "<p><li> Informe o 'Peso' para o critério</li><p/>";
            validar = false;
        }

        if (criterioAvaliacao.getTipoResposta() == null) {
            msg += "<p><li> Informe o 'Tipo de Resposta' para o critério.</li><p />";
            validar = false;
        }

        if (criterioAvaliacao.getPossuiObrigatoriedadeDeAnexo() == null) {
            msg += "<p><li> Informe se é necessário 'Anexo Obrigatório' para o critério.</li><p/>";
            validar = false;
        }

        if (criterioAvaliacao.getUtilizadoParaCriterioDesempate() == null) {
            msg += "<p><li> Informe se o critério é utilizado como 'Critério de Desempate'.</li><p/>";
            validar = false;
        }

        if (isTipoSelecionadoListaSelecao() && (criterioAvaliacao.getCriteriosAvaliacaoOpcaoResposta().isEmpty() || criterioAvaliacao.getCriteriosAvaliacaoOpcaoResposta().size() < 2)) {
            msg += "<p><li> Para o tipo de resposta 'Lista de Seleção' é necessário incluir pelo menos duas opções de respostas.</li><p/>";
            validar = false;
        }
        
        feedbackLabel.setDefaultModel(Model.of(msg));
        target.add(feedbackLabel);
        return validar;
    }
    
    public boolean validarOpcaoResposta(AjaxRequestTarget target) {
        boolean validar = true;

        String msg = "";

        if (StringUtils.isBlank(opcaoResposta.getDescricaoOpcaoResposta())) {
            msg += "<p><li> Campo 'Opção de Resposta' é obrigatório para adicionar uma nova Opção de Resposta.</li><p />";
            validar = false;
        }

        if (opcaoResposta.getNotaOpcaoResposta() == null) {
            msg += "<p><li> Campo 'Nota da Resposta' é obrigatório para adicionar uma nova Opção de Resposta.</li><p />";
            validar = false;
        }

        feedbackLabel.setDefaultModel(Model.of(msg));
        target.add(feedbackLabel);
        return validar;
    }

    public boolean isModoEdicao() {
        return modoEdicao;
    }

    public void setModoEdicao(boolean modoEdicao) {
        this.modoEdicao = modoEdicao;
    }

}