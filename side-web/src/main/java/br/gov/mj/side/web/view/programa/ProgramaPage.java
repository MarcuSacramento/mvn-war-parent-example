package br.gov.mj.side.web.view.programa;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.resource.ResourceRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.wicketstuff.jasperreports.JRConcreteResource;
import org.wicketstuff.jasperreports.handlers.PdfResourceHandler;

import br.gov.mj.apoio.entidades.Funcao;
import br.gov.mj.apoio.entidades.Orgao;
import br.gov.mj.apoio.entidades.SubFuncao;
import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.model.LambdaModel;
import br.gov.mj.side.entidades.enums.EnumStatusPrograma;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.ProgramaBem;
import br.gov.mj.side.entidades.programa.ProgramaKit;
import br.gov.mj.side.entidades.programa.ProgramaPotencialBeneficiarioUf;
import br.gov.mj.side.web.service.ProgramaService;
import br.gov.mj.side.web.service.PublicizacaoService;
import br.gov.mj.side.web.util.RelatorioConsultaPublicaBuilder;
import br.gov.mj.side.web.util.SideUtil;
import br.gov.mj.side.web.view.components.AjaxDialogButton;
import br.gov.mj.side.web.view.programa.anexo.PanelAnexoPage;
import br.gov.mj.side.web.view.programa.criterioAcompanhamento.PanelCriterioAcompanhamentoPage;
import br.gov.mj.side.web.view.programa.criterioElegibilidade.PanelCriterioElegibilidadePage;
import br.gov.mj.side.web.view.programa.criterioavaliacao.CriterioAvaliacaoProgramaPanel;
import br.gov.mj.side.web.view.programa.informacoesGerais.PanelInformacoesGeraisPage;
import br.gov.mj.side.web.view.programa.potenciaisBeneficiarios.PanelPotenciaisBeneficiariosPage;
import br.gov.mj.side.web.view.programa.publicizacao.HistoricoProgramaPanel;
import br.gov.mj.side.web.view.programa.recursofinanceiro.RecursoFinanceiroPanel;
import br.gov.mj.side.web.view.programa.vincularBemKit.PanelVincularBemKit;
import br.gov.mj.side.web.view.programa.visualizacao.ProgramaAnexosPublicadosPanel;
import br.gov.mj.side.web.view.template.TemplatePage;

import com.googlecode.wicket.jquery.ui.form.button.AjaxButton;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal.Backdrop;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.TextContentModal;

@AuthorizeInstantiation({ ProgramaPesquisaPage.ROLE_MANTER_PROGRAMA_INCLUIR, ProgramaPesquisaPage.ROLE_MANTER_PROGRAMA_ALTERAR, ProgramaPesquisaPage.ROLE_MANTER_PROGRAMA_EXCLUIR, ProgramaPesquisaPage.ROLE_MANTER_PROGRAMA_VISUALIZAR, ProgramaPesquisaPage.ROLE_MANTER_PROGRAMA_PUBLICAR })
public class ProgramaPage extends TemplatePage {

    private static final long serialVersionUID = 1L;

    private Form<Programa> form;
    private List<ProgramaKit> listaKitsVinculados;
    List<String> listaAbas = new ArrayList<String>();
    private String abaAnterior = "recursoFinanceiro";
    private String abaAtual = "";

    private Programa entity = new Programa();
    private boolean readOnly;
    private Boolean botaoSalvarClicado = false;
    private Boolean botaoFinalizarClicado = false;
    private Boolean mostrarBotaoAnterior = true;
    private Boolean mostrarBotaoProximo = true;
    private Page backPage;

    private PanelFasePrograma panelFasePrograma;
    private PanelInformacoesGeraisPage panelInformacoesGerais;
    private RecursoFinanceiroPanel recursoFinanceiroPanel;
    private PanelPotenciaisBeneficiariosPage panelPotenciaisBeneficiarios;
    private PanelVincularBemKit panelVincularBemKit;
    private PanelCriterioElegibilidadePage panelCriterioElegibilidade;
    private PanelCriterioAcompanhamentoPage panelCriterioAcompanhamento;
    private CriterioAvaliacaoProgramaPanel criterioAvaliacaoPanel;
    private PanelAnexoPage panelAnexoPage;
    private PanelBotoesAvancar panelBotoesAvancar;
    private PanelBotoes panelBotoes;
    private PanelLis panelLis;

    private AjaxButton buttonSalvar;
    private AjaxButton buttonFinalizar;
    private AjaxButton buttonProximot;
    private AjaxSubmitLink buttonProximo;
    private AjaxSubmitLink buttonAnterior;

    private WebMarkupContainer containerInfoGerais;
    private WebMarkupContainer containerRecursos;
    private WebMarkupContainer containerBeneficiarios;
    private WebMarkupContainer containerVincular;
    private WebMarkupContainer containerElegibilidade;
    private WebMarkupContainer containerAvaliacao;
    private WebMarkupContainer containerAcompanhamento;
    private WebMarkupContainer containerAnexo;
    private WebMarkupContainer containerHistorico;

    PanelAvisoAbasPrograma avisoInfo;
    PanelAvisoAbasPrograma avisoRecurso;
    PanelAvisoAbasPrograma avisoBeneficiarios;
    PanelAvisoAbasPrograma avisoVincular;
    PanelAvisoAbasPrograma avisoElegibilidade;
    PanelAvisoAbasPrograma avisoAvaliacao;
    PanelAvisoAbasPrograma avisoAcompanhamento;
    PanelAvisoAbasPrograma avisoAnexos;
    PanelAvisoAbasPrograma avisoHistorico;

    private Boolean abaInformacoesGeraisClicada = false;
    private Boolean abaRecursoFinanceiroClicada = false;
    private Boolean abaPotenciaisBeneficiariosClicada = false;
    private Boolean abaVincularClicada = false;
    private Boolean abaElegibilidadeClicada = false;
    private Boolean abaAvaliacaoClicada = false;
    private Boolean abaAcompanhamentoClicada = false;
    private Boolean abaAnexoClicada = false;
    private Boolean abaHistoricoClicada = false;

    private Modal<String> modalAvisoSalvarFinalizar;
    private String msgAvisoSalvarFinalizar = new String();

    // Mensagens de aviso
    private Label labelAvisoInformacoes;
    private Label labelAvisoRecurso;
    private Label labelAvisoBeneficiarios;
    private Label labelAvisoVincular;
    private Label labelAvisoElegibilidade;
    private Label labelAvisoAvaliacao;
    private Label labelAvisoAcompanhamento;

    private Model<String> mensagensInformacoesGerais = Model.of("");
    private Model<String> mensagensRecurso = Model.of("");
    private Model<String> mensagensBeneficiarios = Model.of("");
    private Model<String> mensagensVincular = Model.of("");
    private Model<String> mensagensElegibilidade = Model.of("");
    private Model<String> mensagensAvaliacao = Model.of("");
    private Model<String> mensagensAcompanhamento = Model.of("");

    private String mensagemTempAvisoInformacoes = "";
    private String mensagemTempAvisoRecurso = "";
    private String mensagemTempAvisoBeneficiarios = "";
    private String mensagemTempAvisoVincular = "";
    private String mensagemTempAvisoElegibilidade = "";
    private String mensagemTempAvisoAvaliacao = "";
    private String mensagemTempAvisoAcompanhamento = "";
    private Integer abaClicada;

    private AttributeAppender classeActive = new AttributeAppender("class", "active", " ");
    private AttributeModifier classeInactive = new AttributeModifier("class", "");

    @Inject
    private ComponentFactory componentFactory;

    @Inject
    private ProgramaService programaService;

    @Inject
    private PublicizacaoService publicizacaoService;

    public ProgramaPage(final PageParameters pageParameters) {
        super(pageParameters);
        inicializarVariaveis();
        initComponents();
    }

    public ProgramaPage(final PageParameters pageParameters, Page backPage, Programa programa, boolean readOnly, AjaxRequestTarget target, Integer abaClicada) {
        super(pageParameters);

        this.backPage = backPage;
        this.abaClicada = abaClicada;

        if (programa != null && programa.getId() != null) {
            entity = programaService.buscarPeloId(programa.getId());
            entity.setCriteriosAcompanhamento(programaService.buscarProgramaCriterioAcompanhamento(programa));
            entity.setCriteriosElegibilidade(programaService.buscarProgramaCriterioElegibilidade(programa));
            entity.setProgramaBens(programaService.buscarProgramaBem(programa));
            entity.setProgramaKits(programaService.buscarProgramakit(programa));
            entity.setPotenciaisBeneficiariosUf(programaService.buscarProgramaPotencialBeneficiarioUf(programa));
            entity.setRecursosFinanceiros(programaService.buscarProgramaRecursoFinanceiroPeloIdPrograma(programa));
            entity.setHistoricoPublicizacao(programaService.buscarHistoricoPublicizacao(programa));
            entity.setCriteriosAvaliacao(programaService.buscarProgramaCriterioAvaliacao(programa));

            ativarAbas();

        } else {
            entity = programa;
        }

        inicializarVariaveis();
        initComponents();

        setTitulo("Gerenciar Programa");

        abaAnterior = "recursoFinanceiro";
        actionAba(target, "infoGerais");
    }

    private void initComponents() {
        form = componentFactory.newForm("form", new CompoundPropertyModel<Programa>(entity));
        add(form);

        panelFasePrograma = new PanelFasePrograma("panelFasePrograma", entity, backPage, abaClicada);
        form.add(panelFasePrograma);

        recursoFinanceiroPanel = new RecursoFinanceiroPanel("recursoFinanceiroPanel", this, readOnly);
        form.add(recursoFinanceiroPanel);

        panelVincularBemKit = new PanelVincularBemKit("panelVincularBemKit", this, readOnly);
        form.add(panelVincularBemKit);

        criterioAvaliacaoPanel = new CriterioAvaliacaoProgramaPanel("panelCriterioAvaliacao", this, readOnly);
        form.add(criterioAvaliacaoPanel);

        panelCriterioElegibilidade = new PanelCriterioElegibilidadePage("panelCriterioElegibilidade", this, readOnly);
        form.add(panelCriterioElegibilidade);

        panelCriterioAcompanhamento = new PanelCriterioAcompanhamentoPage("panelCriterioAcompanhamento", this, readOnly);
        form.add(panelCriterioAcompanhamento);

        panelInformacoesGerais = new PanelInformacoesGeraisPage("panelInformacoesGerais", this);
        panelInformacoesGerais.setEnabled(!readOnly);
        form.add(panelInformacoesGerais);

        panelPotenciaisBeneficiarios = new PanelPotenciaisBeneficiariosPage("panelPotenciaisBeneficiarios", this);
        panelPotenciaisBeneficiarios.setEnabled(!readOnly);
        form.add(panelPotenciaisBeneficiarios);

        panelAnexoPage = new PanelAnexoPage("panelAnexoPagePrograma", this, readOnly);
        form.add(panelAnexoPage);

        form.add(newLabelCriacaoUsuario());

        HistoricoProgramaPanel historicoPanel = new HistoricoProgramaPanel("historicoPanel", getEntity());
        historicoPanel.setVisible(isCreateMode());
        form.add(historicoPanel);

        ProgramaAnexosPublicadosPanel programaAnexosPublicadosPanel = new ProgramaAnexosPublicadosPanel("programaAnexosPublicadosPanel", getEntity());

        // Somente será visivel caso exista algum item na lista publicado.
        programaAnexosPublicadosPanel.setVisible(programaAnexosPublicadosPanel.getMostrarPanelAnexosPublicados());
        add(programaAnexosPublicadosPanel);
        form.add(programaAnexosPublicadosPanel);

        // Painel que irá possibilitar avançar e retroceder entre os paineis
        form.add(panelBotoesAvancar = new PanelBotoesAvancar("panelBotoesAvancar"));

        // Painel somente com os botões para salvar, finalizar e cancelar.
        panelBotoes = new PanelBotoes("panelBotoes");
        form.add(panelBotoes);

        // Panel com todas as abas
        panelLis = new PanelLis("panelLis");
        form.add(panelLis);

        setarMensagensAviso();

        // Adicionando a Modal
        modalAvisoSalvarFinalizar = newModal("modalAvisoSalvarFinalizar");
        modalAvisoSalvarFinalizar.show(false);
        form.add(modalAvisoSalvarFinalizar);

    }

    // Panel Principal com todas as abas
    private class PanelLis extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelLis(String id) {
            super(id);
            setOutputMarkupId(true);

            add(newContainerInfoGerais());
            add(newContainerRecurso());
            add(newContainerBeneficiarios());
            add(newContainerVincular());
            add(newContainerElegibilidade());
            add(newContainerAvaliacao());
            add(newContainerAcompanhamento());
            add(newContainerAnexos());
            add(newContainerHistorico());
        }
    }

    private class PanelBotoes extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelBotoes(String id) {
            super(id);

            add(newButtonSalvarPrograma()); // btnSalvarPrograma
            add(newButtonFinalizarPrograma()); // btnFinalizarPrograma
            add(getButtonVoltar()); // btnCancelar
            add(getButtonRelatorio()); // btnPdf
        }
    }

    // Botões que ficam no final dos paineis para voltar e avançar para a
    // próxima tela
    private class PanelBotoesAvancar extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelBotoesAvancar(String id) {
            super(id);
            setOutputMarkupId(true);

            add(newButtonAnterior()); // btnAnterior
            add(newButtonProximo()); // btnProximo
        }
    }

    // Abas individuais
    private WebMarkupContainer newContainerInfoGerais() {
        containerInfoGerais = new WebMarkupContainer("infoGerais");

        AjaxFallbackLink button = newLinkInfoGerais();
        containerInfoGerais.add(button);
        button.add(newLabelIconeInfo()); // lblInfo

        return containerInfoGerais;
    }

    private WebMarkupContainer newContainerRecurso() {
        containerRecursos = new WebMarkupContainer("recursoFinanceiro");

        AjaxFallbackLink button = newLinkRecursos();
        containerRecursos.add(button);
        button.add(newLabelIconeRecurso()); // lblRecurso
        return containerRecursos;
    }

    private WebMarkupContainer newContainerBeneficiarios() {
        containerBeneficiarios = new WebMarkupContainer("potenciaisBeneficiarios");

        AjaxFallbackLink button = newLinkBeneficiarios();
        containerBeneficiarios.add(button);
        button.add(newLabelIconeBeneficiarios()); // lblBeneficiarios
        return containerBeneficiarios;
    }

    private WebMarkupContainer newContainerVincular() {
        containerVincular = new WebMarkupContainer("vincularBemKit");

        AjaxFallbackLink button = newLinkVincular();
        containerVincular.add(button);
        button.add(newLabelIconeVincular()); // lblVincular
        return containerVincular;
    }

    private WebMarkupContainer newContainerElegibilidade() {
        containerElegibilidade = new WebMarkupContainer("criteriosElegibilidade");

        AjaxFallbackLink button = newLinkElegibilidade();
        containerElegibilidade.add(button);
        button.add(newLabelIconeElegibilidade()); // lblElegibilidade
        return containerElegibilidade;
    }

    private WebMarkupContainer newContainerAvaliacao() {
        containerAvaliacao = new WebMarkupContainer("criteriosAvaliacao");

        AjaxFallbackLink button = newLinkAvaliacao();
        containerAvaliacao.add(button);
        button.add(newLabelIconeAvaliacao()); // lblAvaliacao
        return containerAvaliacao;
    }

    private WebMarkupContainer newContainerAcompanhamento() {
        containerAcompanhamento = new WebMarkupContainer("criteriosAcompanhamento");

        AjaxFallbackLink button = newLinkAcompanhamento();
        containerAcompanhamento.add(button);
        button.add(newLabelIconeAcompanhamento()); // lblAcompanhamento
        return containerAcompanhamento;
    }

    private WebMarkupContainer newContainerAnexos() {
        containerAnexo = new WebMarkupContainer("anexos");

        AjaxFallbackLink button = newLinkAnexos();
        containerAnexo.add(button);
        button.add(newLabelIconeAnexos()); // lblAnexos
        return containerAnexo;
    }

    private WebMarkupContainer newContainerHistorico() {
        containerHistorico = new WebMarkupContainer("historico");

        AjaxFallbackLink button = newLinkHistorico();
        containerHistorico.add(button);
        button.add(newLabelIconeHistorico()); // lblHistorico
        return containerHistorico;
    }

    public void inicializarVariaveis() {
        if (listaKitsVinculados == null) {
            listaKitsVinculados = new ArrayList<ProgramaKit>();
        }

        if (entity == null || entity.getId() == null) {
            readOnly = false;
        } else {
            EnumStatusPrograma status = entity.getStatusPrograma();
            if (status == EnumStatusPrograma.EM_ELABORACAO || status == EnumStatusPrograma.FORMULADO) {
                readOnly = false;
            } else {
                readOnly = true;
            }
        }
    }

    /*
     * OS COMPONENTES SERÃO IMPLEMENTADOS ABAIXO
     */

    // BOTÕES DE CADA UMA DAS ABAS
    private AjaxFallbackLink<Void> newLinkInfoGerais() {
        AjaxFallbackLink<Void> btnInfo = componentFactory.newAjaxFallbackLink("btn-infoGerais", (target) -> actionAba(target, "infoGerais"));
        btnInfo.setOutputMarkupId(true);
        return btnInfo;
    }

    private AjaxFallbackLink<Void> newLinkRecursos() {
        AjaxFallbackLink<Void> btnInfo = componentFactory.newAjaxFallbackLink("btn-recursoFinanceiro", (target) -> actionAba(target, "recursoFinanceiro"));
        btnInfo.setOutputMarkupId(true);
        return btnInfo;
    }

    private AjaxFallbackLink<Void> newLinkBeneficiarios() {
        AjaxFallbackLink<Void> btnInfo = componentFactory.newAjaxFallbackLink("btn-potenciaisBeneficiarios", (target) -> actionAba(target, "potenciaisBeneficiarios"));
        btnInfo.setOutputMarkupId(true);
        return btnInfo;
    }

    private AjaxFallbackLink<Void> newLinkVincular() {
        AjaxFallbackLink<Void> btnInfo = componentFactory.newAjaxFallbackLink("btn-vincularBemKit", (target) -> actionAba(target, "vincularBemKit"));
        btnInfo.setOutputMarkupId(true);
        return btnInfo;
    }

    private AjaxFallbackLink<Void> newLinkElegibilidade() {
        AjaxFallbackLink<Void> btnInfo = componentFactory.newAjaxFallbackLink("btn-criteriosElegibilidade", (target) -> actionAba(target, "criteriosElegibilidade"));
        btnInfo.setOutputMarkupId(true);
        return btnInfo;
    }

    private AjaxFallbackLink<Void> newLinkAvaliacao() {
        AjaxFallbackLink<Void> btnInfo = componentFactory.newAjaxFallbackLink("btn-criteriosAvaliacao", (target) -> actionAba(target, "criteriosAvaliacao"));
        btnInfo.setOutputMarkupId(true);
        return btnInfo;
    }

    private AjaxFallbackLink<Void> newLinkAcompanhamento() {
        AjaxFallbackLink<Void> btnInfo = componentFactory.newAjaxFallbackLink("btn-criteriosAcompanhamento", (target) -> actionAba(target, "criteriosAcompanhamento"));
        btnInfo.setOutputMarkupId(true);
        return btnInfo;
    }

    private AjaxFallbackLink<Void> newLinkAnexos() {
        AjaxFallbackLink<Void> btnInfo = componentFactory.newAjaxFallbackLink("btn-anexos", (target) -> actionAba(target, "anexos"));
        btnInfo.setOutputMarkupId(true);
        return btnInfo;
    }

    private AjaxFallbackLink<Void> newLinkHistorico() {
        AjaxFallbackLink<Void> btnInfo = componentFactory.newAjaxFallbackLink("btn-historico", (target) -> actionAba(target, "historico"));
        btnInfo.setOutputMarkupId(true);
        return btnInfo;
    }

    // LABEL QUE IRÁ VARIAR CONFORME ESTADO DA ABA, V PARA OK, '!' PARA ALGO
    // ERRADO E 'LAPIS' PARA EDITANDO
    private Label newLabelIconeInfo() {
        String string = validarAbaInfo();
        Label lbl = new Label("lblInfo", "");

        AttributeAppender classe = new AttributeAppender("class", string, " ");
        lbl.add(classe);
        lbl.setOutputMarkupId(true);
        lbl.setEscapeModelStrings(false);
        return lbl;
    }

    private Label newLabelIconeRecurso() {
        String string = validarAbaRecurso();
        Label lbl = new Label("lblRecurso", "");

        AttributeAppender classe = new AttributeAppender("class", string, " ");
        lbl.add(classe);
        lbl.setOutputMarkupId(true);
        lbl.setEscapeModelStrings(false);
        return lbl;
    }

    private Label newLabelIconeBeneficiarios() {
        String string = validarAbaBeneficiarios();
        Label lbl = new Label("lblBeneficiarios", "");

        AttributeAppender classe = new AttributeAppender("class", string, " ");
        lbl.add(classe);
        lbl.setOutputMarkupId(true);
        lbl.setEscapeModelStrings(false);
        return lbl;
    }

    private Label newLabelIconeAvaliacao() {
        String string = validarAbaAvaliacao();
        Label lbl = new Label("lblAvaliacao", "");

        AttributeAppender classe = new AttributeAppender("class", string, " ");
        lbl.add(classe);
        lbl.setOutputMarkupId(true);
        lbl.setEscapeModelStrings(false);
        return lbl;
    }

    private Label newLabelIconeElegibilidade() {
        String string = validarAbaElegibilidade();
        Label lbl = new Label("lblElegibilidade", "");

        AttributeAppender classe = new AttributeAppender("class", string, " ");
        lbl.add(classe);
        lbl.setOutputMarkupId(true);
        lbl.setEscapeModelStrings(false);
        return lbl;
    }

    private Label newLabelIconeVincular() {
        String string = validarAbaVincular();
        Label lbl = new Label("lblVincular", "");

        AttributeAppender classe = new AttributeAppender("class", string, " ");
        lbl.add(classe);
        lbl.setOutputMarkupId(true);
        lbl.setEscapeModelStrings(false);
        return lbl;
    }

    private Label newLabelIconeAcompanhamento() {
        String string = validarAbaAcompanhamento();
        Label lbl = new Label("lblAcompanhamento", "");

        AttributeAppender classe = new AttributeAppender("class", string, " ");
        lbl.add(classe);
        lbl.setOutputMarkupId(true);
        lbl.setEscapeModelStrings(false);
        return lbl;
    }

    private Label newLabelIconeAnexos() {
        String string = validarAbaAnexos();
        Label lbl = new Label("lblAnexos", "");

        AttributeAppender classe = new AttributeAppender("class", string, " ");
        lbl.add(classe);
        lbl.setOutputMarkupId(true);
        lbl.setEscapeModelStrings(false);
        return lbl;
    }

    private Label newLabelIconeHistorico() {
        String string = validarAbaHistorico();
        Label lbl = new Label("lblHistorico", "");

        AttributeAppender classe = new AttributeAppender("class", string, " ");
        lbl.add(classe);
        lbl.setOutputMarkupId(true);
        lbl.setEscapeModelStrings(false);
        return lbl;
    }

    private Label newLabelAvisoInformacoes() {
        labelAvisoInformacoes = new Label("lblAvisoInformacoesGerais", mensagensInformacoesGerais);
        labelAvisoInformacoes.setEscapeModelStrings(false);
        labelAvisoInformacoes.setOutputMarkupId(true);
        return labelAvisoInformacoes;
    }

    private Label newLabelAvisoRecurso() {
        labelAvisoRecurso = new Label("lblAvisoRecurso", mensagensRecurso);
        labelAvisoRecurso.setEscapeModelStrings(false);
        labelAvisoRecurso.setOutputMarkupId(true);
        return labelAvisoRecurso;
    }

    private Label newLabelAvisoBeneficiarios() {
        labelAvisoBeneficiarios = new Label("lblAvisoBeneficiarios", mensagensBeneficiarios);
        labelAvisoBeneficiarios.setEscapeModelStrings(false);
        labelAvisoBeneficiarios.setOutputMarkupId(true);
        return labelAvisoBeneficiarios;
    }

    private Label newLabelAvisoVincular() {
        labelAvisoVincular = new Label("lblAvisoVincular", mensagensVincular);
        labelAvisoVincular.setEscapeModelStrings(false);
        labelAvisoVincular.setOutputMarkupId(true);
        return labelAvisoVincular;
    }

    private Label newLabelAvisoElegibilidade() {
        labelAvisoElegibilidade = new Label("lblAvisoElegibilidade", mensagensElegibilidade);
        labelAvisoElegibilidade.setEscapeModelStrings(false);
        labelAvisoElegibilidade.setOutputMarkupId(true);
        return labelAvisoElegibilidade;
    }

    private Label newLabelAvisoAvaliacao() {
        labelAvisoAvaliacao = new Label("lblAvisoAvaliacao", mensagensAvaliacao);
        labelAvisoAvaliacao.setEscapeModelStrings(false);
        labelAvisoAvaliacao.setOutputMarkupId(true);
        return labelAvisoAvaliacao;
    }

    private Label newLabelAvisoAcompanhamento() {
        labelAvisoAcompanhamento = new Label("lblAvisoAcompanhamento", mensagensAcompanhamento);
        labelAvisoAcompanhamento.setEscapeModelStrings(false);
        labelAvisoAcompanhamento.setOutputMarkupId(true);
        return labelAvisoAcompanhamento;
    }

    private ContextImage getImage() {
        ContextImage image = new ContextImage("imagem1", "images/ice-asc.gif");
        return image;
    }

    private Label newLabelCriacaoUsuario() {
        String frase = "";
        String usuario = "";
        if (entity.getDataAlteracao() == null) {
            frase += "<strong>Data de criação do programa: </strong>" + dataCadastroBR(entity.getDataCadastro());
            usuario += entity.getUsuarioCadastro();
        } else {
            frase += "<strong>Data da última alteração: </strong>" + dataCadastroBR(entity.getDataAlteracao());
            usuario += entity.getUsuarioAlteracao();
        }

        Label label = new Label("lblUsuario", frase + ", <strong>usuário: </strong>" + usuario);
        label.setEscapeModelStrings(false);
        label.setVisible(entity.getId() != null);
        return label;
    }

    public String dataCadastroBR(LocalDateTime dataCadastro) {
        DateTimeFormatter sdfPadraoBR = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        if (dataCadastro != null) {
            return sdfPadraoBR.format(dataCadastro);
        }
        return " - ";
    }

    public AjaxSubmitLink newButtonSalvarPrograma() {
        AjaxSubmitLink buttonSalvarPrograma = new AjaxSubmitLink("btnSalvarPrograma", form) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form form) {
                salvarPrograma(target);
            }
        };
        buttonSalvarPrograma.setOutputMarkupId(true);
        if (entity.getStatusPrograma() != null && !entity.getStatusPrograma().equals(EnumStatusPrograma.EM_ELABORACAO)) {
            buttonSalvarPrograma.setVisible(false);
        }
        buttonSalvarPrograma.setDefaultFormProcessing(true);
        return buttonSalvarPrograma;
    }

    public AjaxSubmitLink newButtonFinalizarPrograma() {
        AjaxSubmitLink buttonFinalizarPrograma = new AjaxSubmitLink("btnFinalizarPrograma", form) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form form) {
                finalizarPrograma(target);
            }
        };
        buttonFinalizarPrograma.setOutputMarkupId(true);
        buttonFinalizarPrograma.setVisible(mostrarBotaoFinalizar());
        buttonFinalizarPrograma.setDefaultFormProcessing(true);
        return buttonFinalizarPrograma;
    }

    private boolean mostrarBotaoFinalizar() {
        if (entity == null || entity.getId() == null) {
            return true;
        } else {
            EnumStatusPrograma status = entity.getStatusPrograma();
            if (status == EnumStatusPrograma.EM_ELABORACAO || status == EnumStatusPrograma.FORMULADO) {
                return true;
            } else {
                return false;
            }
        }
    }

    private void republicar() {
        if (!getSideSession().hasRole(ProgramaPesquisaPage.ROLE_MANTER_PROGRAMA_PUBLICAR)) {
            throw new SecurityException();
        }
        getEntity().setNumeroProcessoSEI(limparNumeroSei(getEntity().getNumeroProcessoSEI()));
        publicizacaoService.republicar(getEntity(), getIdentificador());
        getSession().info("Publicado com sucesso");
        setResponsePage(new ProgramaPesquisaPage(null));
    }

    private Button getButtonRelatorio() {
        Button buttonCsv = componentFactory.newButton("btnPdf", () -> gerarPdf());
        buttonCsv.setVisible(entity.getId() != null);
        return buttonCsv;
    }

    private Button getButtonVoltar() {
        Button button = componentFactory.newButton("btnVoltar", () -> voltar());
        button.setDefaultFormProcessing(false);
        return button;
    }

    public AjaxSubmitLink newButtonProximo() {
        buttonProximo = new AjaxSubmitLink("btnProximo", form) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form form) {
                actionButtonProximoAnterior(target, buttonProximo);
            }
        };
        buttonProximo.setOutputMarkupId(true);
        buttonProximo.setVisible(mostrarBotaoProximo);
        return buttonProximo;
    }

    public AjaxSubmitLink newButtonAnterior() {
        buttonAnterior = new AjaxSubmitLink("btnAnterior", form) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form form) {
                actionButtonProximoAnterior(target, buttonAnterior);
            }
        };
        buttonAnterior.setOutputMarkupId(true);
        buttonAnterior.setVisible(mostrarBotaoAnterior);
        return buttonAnterior;
    }

    private void actionButtonProximoAnterior(AjaxRequestTarget target, AjaxSubmitLink botaoClicado) {

        String proximaAba = "";
        if ("btnProximo".equalsIgnoreCase(botaoClicado.getId())) {
            proximaAba = proximaAba();
        } else {
            proximaAba = abaAnterior();
        }

        verificarAbasClicadas();

        String idAtual = proximaAba + "-aba";
        String idAnterior = abaAnterior + "-aba";

        // oculta a aba Anterior
        target.appendJavaScript("ocultarAba('" + idAnterior + "');");

        // Mostra a aba clicada
        target.appendJavaScript("mostrarAba('" + idAtual + "');");
        abaAnterior = proximaAba;
        abaAtual = proximaAba;

        atualizarAbas(target);
        target.appendJavaScript("ancorarResultadoPesquisa('#paginaPrograma');");
    }

    private Modal<String> newModal(String id) {
        Modal<String> modal = new TextContentModal(id, new LambdaModel<String>(this::getMsgAvisoSalvarFinalizar, this::setMsgAvisoSalvarFinalizar));
        modal.addButton(newButtonOk(modal));
        modal.setBackdrop(Backdrop.STATIC);
        return modal;
    }

    private AjaxDialogButton newButtonOk(Modal<String> modal) {
        return new AjaxDialogButton(Model.of("Ok"), Buttons.Type.Primary) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                modal.show(false);
                modal.close(target);
            }
        };
    }

    /*
     * AQUI VIRÃO AS AÇÕES
     */

    public void actionAba(AjaxRequestTarget target, String id) {

        // Esta aba sempre será a primeira a ser mostrada, então
        // Zeramos ela para evitar conflitos.
        target.appendJavaScript("ocultarAba('infoGerais-aba');");

        abaAtual = id;
        verificarAbasClicadas();

        String idAtual = id + "-aba";
        String idAnterior = abaAnterior + "-aba";

        // oculta a aba Anterior
        target.appendJavaScript("ocultarAba('" + idAnterior + "');");

        // Mostra a aba clicada
        target.appendJavaScript("mostrarAba('" + idAtual + "');");
        abaAnterior = id;

        verificarQuaisBotoesProximoAnteriorMostrar();
        atualizarAbas(target);
    }

    private void verificarQuaisBotoesProximoAnteriorMostrar() {
        mostrarBotaoAnterior = true;
        mostrarBotaoProximo = true;
        if ("infoGerais".equalsIgnoreCase(abaAnterior)) {
            mostrarBotaoAnterior = false;
        }

        if ("historico".equalsIgnoreCase(abaAnterior)) {
            mostrarBotaoProximo = false;
        }
    }

    private void atualizarAbas(AjaxRequestTarget target) {
        panelLis.addOrReplace(newContainerInfoGerais());
        panelLis.addOrReplace(newContainerRecurso());
        panelLis.addOrReplace(newContainerBeneficiarios());
        panelLis.addOrReplace(newContainerVincular());
        panelLis.addOrReplace(newContainerElegibilidade());
        panelLis.addOrReplace(newContainerAvaliacao());
        panelLis.addOrReplace(newContainerAcompanhamento());
        panelLis.addOrReplace(newContainerAnexos());
        panelLis.addOrReplace(newContainerHistorico());

        panelBotoesAvancar.addOrReplace(newButtonAnterior());
        panelBotoesAvancar.addOrReplace(newButtonProximo());

        target.add(panelLis);
        target.add(labelAvisoInformacoes);
        target.add(labelAvisoRecurso);
        target.add(labelAvisoBeneficiarios);
        target.add(labelAvisoVincular);
        target.add(labelAvisoElegibilidade);
        target.add(labelAvisoAvaliacao);
        target.add(labelAvisoAcompanhamento);
        target.add(panelBotoesAvancar);

        panelVincularBemKit.getPanelMaximoProposta().addOrReplace(panelVincularBemKit.newLabelValorTotal());
        target.add(panelVincularBemKit.getPanelMaximoProposta());

        if ("infoGerais".equalsIgnoreCase(abaAnterior)) {
            containerInfoGerais.add(classeActive);
            containerRecursos.add(classeInactive);
            containerBeneficiarios.add(classeInactive);
            containerVincular.add(classeInactive);
            containerElegibilidade.add(classeInactive);
            containerAvaliacao.add(classeInactive);
            containerAcompanhamento.add(classeInactive);
            containerAnexo.add(classeInactive);
            containerHistorico.add(classeInactive);
        }

        if ("recursoFinanceiro".equalsIgnoreCase(abaAnterior)) {
            containerInfoGerais.add(classeInactive);
            containerRecursos.add(classeActive);
            containerBeneficiarios.add(classeInactive);
            containerVincular.add(classeInactive);
            containerElegibilidade.add(classeInactive);
            containerAvaliacao.add(classeInactive);
            containerAcompanhamento.add(classeInactive);
            containerAnexo.add(classeInactive);
            containerHistorico.add(classeInactive);
        }

        if ("potenciaisBeneficiarios".equalsIgnoreCase(abaAnterior)) {
            containerInfoGerais.add(classeInactive);
            containerRecursos.add(classeInactive);
            containerBeneficiarios.add(classeActive);
            containerVincular.add(classeInactive);
            containerElegibilidade.add(classeInactive);
            containerAvaliacao.add(classeInactive);
            containerAcompanhamento.add(classeInactive);
            containerAnexo.add(classeInactive);
            containerHistorico.add(classeInactive);
        }

        if ("vincularBemKit".equalsIgnoreCase(abaAnterior)) {
            containerInfoGerais.add(classeInactive);
            containerRecursos.add(classeInactive);
            containerBeneficiarios.add(classeInactive);
            containerVincular.add(classeActive);
            containerElegibilidade.add(classeInactive);
            containerAvaliacao.add(classeInactive);
            containerAcompanhamento.add(classeInactive);
            containerAnexo.add(classeInactive);
            containerHistorico.add(classeInactive);
        }

        if ("criteriosElegibilidade".equalsIgnoreCase(abaAnterior)) {
            containerInfoGerais.add(classeInactive);
            containerRecursos.add(classeInactive);
            containerBeneficiarios.add(classeInactive);
            containerVincular.add(classeInactive);
            containerElegibilidade.add(classeActive);
            containerAvaliacao.add(classeInactive);
            containerAcompanhamento.add(classeInactive);
            containerAnexo.add(classeInactive);
            containerHistorico.add(classeInactive);
        }

        if ("criteriosAvaliacao".equalsIgnoreCase(abaAnterior)) {
            containerInfoGerais.add(classeInactive);
            containerRecursos.add(classeInactive);
            containerBeneficiarios.add(classeInactive);
            containerVincular.add(classeInactive);
            containerElegibilidade.add(classeInactive);
            containerAvaliacao.add(classeActive);
            containerAcompanhamento.add(classeInactive);
            containerAnexo.add(classeInactive);
            containerHistorico.add(classeInactive);
        }

        if ("criteriosAcompanhamento".equalsIgnoreCase(abaAnterior)) {
            containerInfoGerais.add(classeInactive);
            containerRecursos.add(classeInactive);
            containerBeneficiarios.add(classeInactive);
            containerVincular.add(classeInactive);
            containerElegibilidade.add(classeInactive);
            containerAvaliacao.add(classeInactive);
            containerAcompanhamento.add(classeActive);
            containerAnexo.add(classeInactive);
            containerHistorico.add(classeInactive);
        }

        if ("anexos".equalsIgnoreCase(abaAnterior)) {
            containerInfoGerais.add(classeInactive);
            containerRecursos.add(classeInactive);
            containerBeneficiarios.add(classeInactive);
            containerVincular.add(classeInactive);
            containerElegibilidade.add(classeInactive);
            containerAvaliacao.add(classeInactive);
            containerAcompanhamento.add(classeInactive);
            containerAnexo.add(classeActive);
            containerHistorico.add(classeInactive);
        }

        if ("historico".equalsIgnoreCase(abaAnterior)) {
            containerInfoGerais.add(classeInactive);
            containerRecursos.add(classeInactive);
            containerBeneficiarios.add(classeInactive);
            containerVincular.add(classeInactive);
            containerElegibilidade.add(classeInactive);
            containerAvaliacao.add(classeInactive);
            containerAcompanhamento.add(classeInactive);
            containerAnexo.add(classeInactive);
            containerHistorico.add(classeActive);
        }

    }

    private void verificarAbasClicadas() {
        if ("infoGerais".equalsIgnoreCase(abaAtual)) {
            abaInformacoesGeraisClicada = true;
        }

        if ("recursoFinanceiro".equalsIgnoreCase(abaAtual)) {
            abaRecursoFinanceiroClicada = true;
        }

        if ("potenciaisBeneficiarios".equalsIgnoreCase(abaAtual)) {
            abaPotenciaisBeneficiariosClicada = true;
        }

        if ("vincularBemKit".equalsIgnoreCase(abaAtual)) {
            abaVincularClicada = true;
        }

        if ("criteriosElegibilidade".equalsIgnoreCase(abaAtual)) {
            abaElegibilidadeClicada = true;
        }

        if ("criteriosAvaliacao".equalsIgnoreCase(abaAtual)) {
            abaAvaliacaoClicada = true;
        }

        if ("criteriosAcompanhamento".equalsIgnoreCase(abaAtual)) {
            abaAcompanhamentoClicada = true;
        }

        if ("anexos".equalsIgnoreCase(abaAtual)) {
            abaAnexoClicada = true;
        }

        if ("historico".equalsIgnoreCase(abaAtual)) {
            abaHistoricoClicada = true;
        }
    }

    private void ativarAbas() {

        abaInformacoesGeraisClicada = true;
        abaRecursoFinanceiroClicada = true;
        abaPotenciaisBeneficiariosClicada = true;
        abaVincularClicada = true;
        abaElegibilidadeClicada = true;
        abaAvaliacaoClicada = true;
        abaAcompanhamentoClicada = true;
        abaAnexoClicada = true;
        abaHistoricoClicada = true;
    }

    private void desativarAbas() {

        abaInformacoesGeraisClicada = false;
        abaRecursoFinanceiroClicada = false;
        abaPotenciaisBeneficiariosClicada = false;
        abaVincularClicada = false;
        abaElegibilidadeClicada = false;
        abaAvaliacaoClicada = false;
        abaAcompanhamentoClicada = false;
        abaAnexoClicada = false;
        abaHistoricoClicada = false;
    }

    // Chamado somente quando clicar no botão de salvar ou finalizar
    private void setarBooleansComoFalse() {

        abaInformacoesGeraisClicada = false;
        abaRecursoFinanceiroClicada = false;
        abaPotenciaisBeneficiariosClicada = false;
        abaVincularClicada = false;
        abaElegibilidadeClicada = false;
        abaAvaliacaoClicada = false;
        abaAcompanhamentoClicada = false;
        abaAnexoClicada = false;
        abaHistoricoClicada = false;
    }

    private void ativarAbasQuandoClicarEmSalvarOuFinalizar() {

        /*
         * Estas são as unicas validações obrigatórias quando clicar no botão de
         * salvar Desta forma, somente irá aparecer as 'exclamações' para os
         * campos não preenchidos para estas abas abaixo.
         */
        setarBooleansComoFalse();
        if (botaoSalvarClicado || botaoFinalizarClicado) {
            abaInformacoesGeraisClicada = true;
            abaRecursoFinanceiroClicada = true;
            abaPotenciaisBeneficiariosClicada = true;
        }

        if (botaoFinalizarClicado) {
            abaVincularClicada = true;
            abaElegibilidadeClicada = true;
            abaAvaliacaoClicada = true;
            abaAcompanhamentoClicada = true;
            abaAnexoClicada = true;
            abaHistoricoClicada = true;
        }
    }

    private String proximaAba() {

        String proximaAba = "";
        mostrarBotaoProximo = true;
        mostrarBotaoAnterior = true;

        // desativarAbas();

        switch (abaAtual) {
        case "infoGerais":
            proximaAba = "recursoFinanceiro";
            abaAnterior = "infoGerais";
            abaRecursoFinanceiroClicada = true;
            break;

        case "recursoFinanceiro":
            proximaAba = "potenciaisBeneficiarios";
            abaAnterior = "recursoFinanceiro";
            abaPotenciaisBeneficiariosClicada = true;
            break;

        case "potenciaisBeneficiarios":
            proximaAba = "vincularBemKit";
            abaAnterior = "potenciaisBeneficiarios";
            abaVincularClicada = true;
            break;

        case "vincularBemKit":
            proximaAba = "criteriosElegibilidade";
            abaAnterior = "vincularBemKit";
            abaElegibilidadeClicada = true;
            break;

        case "criteriosElegibilidade":
            proximaAba = "criteriosAvaliacao";
            abaAnterior = "criteriosElegibilidade";
            abaAvaliacaoClicada = true;
            break;

        case "criteriosAvaliacao":
            proximaAba = "criteriosAcompanhamento";
            abaAnterior = "criteriosAvaliacao";
            abaAcompanhamentoClicada = true;
            break;

        case "criteriosAcompanhamento":
            proximaAba = "anexos";
            abaAnterior = "criteriosAcompanhamento";
            abaAnexoClicada = true;
            break;

        case "anexos":
            proximaAba = "historico";
            abaAnterior = "anexos";
            mostrarBotaoProximo = false;
            abaHistoricoClicada = true;
            break;
        }

        return proximaAba;
    }

    private String abaAnterior() {

        String proximaAba = "";
        mostrarBotaoAnterior = true;
        mostrarBotaoProximo = true;

        // desativarAbas();

        switch (abaAtual) {
        case "recursoFinanceiro":
            proximaAba = "infoGerais";
            abaAnterior = "recursoFinanceiro";
            mostrarBotaoAnterior = false;
            abaInformacoesGeraisClicada = true;
            break;

        case "potenciaisBeneficiarios":
            proximaAba = "recursoFinanceiro";
            abaAnterior = "potenciaisBeneficiarios";
            abaRecursoFinanceiroClicada = true;
            break;

        case "vincularBemKit":
            proximaAba = "potenciaisBeneficiarios";
            abaAnterior = "vincularBemKit";
            abaPotenciaisBeneficiariosClicada = true;
            break;

        case "criteriosElegibilidade":
            proximaAba = "vincularBemKit";
            abaAnterior = "criteriosElegibilidade";
            abaVincularClicada = true;
            break;

        case "criteriosAvaliacao":
            proximaAba = "criteriosElegibilidade";
            abaAnterior = "criteriosAvaliacao";
            abaElegibilidadeClicada = true;
            break;

        case "criteriosAcompanhamento":
            proximaAba = "criteriosAvaliacao";
            abaAnterior = "criteriosAcompanhamento";
            abaAvaliacaoClicada = true;
            break;

        case "anexos":
            proximaAba = "criteriosAcompanhamento";
            abaAcompanhamentoClicada = true;
            abaAnterior = "anexos";
            break;

        case "historico":
            proximaAba = "anexos";
            abaAnexoClicada = true;
            abaAnterior = "historico";
            break;

        }

        return proximaAba;
    }

    private void setarMensagensAviso() {
        form.add(newLabelAvisoInformacoes()); // lblAvisoInformacoesGerais
        form.add(newLabelAvisoRecurso()); // lblAvisoRecurso
        form.add(newLabelAvisoBeneficiarios()); // lblAvisoBeneficiarios
        form.add(newLabelAvisoVincular()); // lblAvisoVincular
        form.add(newLabelAvisoElegibilidade()); // lblAvisoElegibilidade
        form.add(newLabelAvisoAvaliacao()); // lblAvisoAvaliacao
        form.add(newLabelAvisoAcompanhamento()); // lblAvisoAcompanhamento

    }

    private boolean isCreateMode() {
        return getEntity().getId() != null ? true : false;
    }

    public void atualizarDropDownRecursoFinanceiro(AjaxRequestTarget target) {
        recursoFinanceiroPanel.addOrReplace(recursoFinanceiroPanel.newDropDownAcaoOrcamentaria());
        target.add(recursoFinanceiroPanel);
    }

    public void removerRecursosFinanceiros(AjaxRequestTarget target) {
        getForm().getModelObject().getRecursosFinanceiros().clear();
        recursoFinanceiroPanel.atualizarLabelTotal();
        atualizarDropDownRecursoFinanceiro(target);
        target.add(recursoFinanceiroPanel);
    }

    public void salvarPrograma(AjaxRequestTarget target) {

        if (!getSideSession().hasAnyRole(new String[] { ProgramaPesquisaPage.ROLE_MANTER_PROGRAMA_INCLUIR, ProgramaPesquisaPage.ROLE_MANTER_PROGRAMA_ALTERAR })) {
            throw new SecurityException();
        }
        panelPotenciaisBeneficiarios.nivelarListTempUfselected();

        botaoFinalizarClicado = false;
        botaoSalvarClicado = true;

        Programa programaTemp = new Programa();
        programaTemp = form.getModelObject();
        programaTemp.setStatusPrograma(EnumStatusPrograma.EM_ELABORACAO);
        String numeroSei = limparNumeroSei(programaTemp.getNumeroProcessoSEI());
        programaTemp.setNumeroProcessoSEI(numeroSei);
        programaTemp.setDataCadastro(LocalDateTime.now());

        boolean editando = programaTemp.getId() == null ? false : true;

        if (!validarAoClicarEmSalvar()) {

            if (abaAtual == null || "".equalsIgnoreCase(abaAtual)) {
                abaAtual = "infoGerais";
            }

            ativarAbasQuandoClicarEmSalvarOuFinalizar();
            atualizarTodasAsMensagens();
            actionAba(target, abaAtual);

            setMsgAvisoSalvarFinalizar("Verifique os campos obrigatórios quando executar a ação de 'Salvar'.");
            modalAvisoSalvarFinalizar.show(true);
            target.add(modalAvisoSalvarFinalizar);

            return;
        }

        zerarTodasAsMensagens();
        atualizarAbas(target);

        String numero = limparNumeroSei(programaTemp.getNumeroProcessoSEI());
        programaTemp.setNumeroProcessoSEI(numero);
        String usuarioLogado = getIdentificador();
        programaTemp.setUsuarioAlteracao(usuarioLogado);
        Programa programaSalvo = programaService.incluirAlterar(programaTemp, usuarioLogado);

        if (editando) {
            getSession().info("Alterado com sucesso");
        } else {
            getSession().info("Salvo com sucesso.");
        }

        setResponsePage(new ProgramaPage(new PageParameters(), backPage, programaSalvo, false, target, abaClicada));
    }

    public void finalizarPrograma(AjaxRequestTarget target) {
        Programa programaTemp = new Programa();
        programaTemp = form.getModelObject();

        String numeroSei = limparNumeroSei(programaTemp.getNumeroProcessoSEI());
        programaTemp.setNumeroProcessoSEI(numeroSei);
        boolean editando = programaTemp.getId() == null ? false : true;

        botaoFinalizarClicado = true;
        botaoSalvarClicado = false;
        if (!validarAoClicarEmFinalizar()) {

            if (abaAtual == null || "".equalsIgnoreCase(abaAtual)) {
                abaAtual = "infoGerais";
            }

            ativarAbasQuandoClicarEmSalvarOuFinalizar();
            atualizarTodasAsMensagens();
            actionAba(target, abaAtual);

            setMsgAvisoSalvarFinalizar("Verifique os campos obrigatórios quando executar a ação de 'Concluir'.");
            modalAvisoSalvarFinalizar.show(true);
            target.add(modalAvisoSalvarFinalizar);

            return;
        }

        Programa programa = recuperarProgramaAtualMontado();
        if (!validarSomaValoresKitsEBens(programa, true)) {
            addMsgError("MN034");
            target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
            return;
        }

        zerarTodasAsMensagens();
        atualizarAbas(target);

        String usuarioLogado = getIdentificador();
        programaTemp.setStatusPrograma(EnumStatusPrograma.FORMULADO);
        Programa programaSalvo = programaService.incluirAlterar(programaTemp, usuarioLogado);

        if (editando) {
            getSession().info("Processo de 'Definição' do programa foi alterado com sucesso");
        } else {
            getSession().info("Processo de 'Definição' do programa foi finalizado com sucesso.");
        }
        setResponsePage(new ProgramaPage(new PageParameters(), backPage, programaSalvo, false, target, abaClicada));
    }

    private void gerarPdf() {

        List<Programa> lista = new ArrayList<Programa>();
        lista.add(entity);
        RelatorioConsultaPublicaBuilder builder = new RelatorioConsultaPublicaBuilder(programaService, SideUtil.getCaminhoCompletoRelatorios(getHttpServletRequest().getServletContext()));
        JRConcreteResource<PdfResourceHandler> relatorioResource = builder.export(lista);
        ResourceRequestHandler handler = new ResourceRequestHandler(relatorioResource, getPageParameters());
        RequestCycle requestCycle = getRequestCycle();
        requestCycle.scheduleRequestHandlerAfterCurrent(handler);
    }

    public void voltar() {
        setResponsePage(backPage);
    }

    /*
     * VALIDAÇÕES DAS ABAS
     */

    private Programa recuperarProgramaAtualMontado() {
        Programa programaTemp = new Programa();
        programaTemp = form.getModelObject();
        String numeroSei = limparNumeroSei(programaTemp.getNumeroProcessoSEI());
        programaTemp.setNumeroProcessoSEI(numeroSei);
        programaTemp.setDataCadastro(LocalDateTime.now());
        return programaTemp;
    }

    private boolean validarAoClicarEmSalvar() {
        boolean validar = true;

        Programa programa = recuperarProgramaAtualMontado();

        validar = validarNomePrograma(programa, validar);
        validar = validarNomeFantasia(programa, validar);
        validar = validarDescricao(programa, validar);
        validar = validarAnoPrograma(programa, validar);
        validar = validarFuncao(validar);
        validar = validarSubFuncao(validar);
        validar = validarRecursoFinanceiro(programa, validar);
        validar = validarOrgao(validar);
        validar = validarUnidadeExecutora(programa, validar);
        validar = validarLimitacaoGeografica(programa, validar);
        validar = validarNomePersonalidadeJuridica(programa, validar);
        validar = validarNumeroProcessoSei(programa, validar);

        return validar;
    }

    private boolean validarAoClicarEmFinalizar() {
        boolean validar = true;

        Programa programa = recuperarProgramaAtualMontado();

        // validar = validarSomaValoresKitsEBens(programa, validar);
        validar = validarNomePrograma(programa, validar);
        validar = validarNomeFantasia(programa, validar);
        validar = validarDescricao(programa, validar);
        validar = validarAnoPrograma(programa, validar);
        validar = validarFuncao(validar);
        validar = validarSubFuncao(validar);
        validar = validarRecursoFinanceiro(programa, validar);
        validar = validarValorMaximoProposta(programa, validar);
        validar = validarOrgao(validar);
        validar = validarUnidadeExecutora(programa, validar);
        validar = validarLimitacaoGeografica(programa, validar);
        validar = validarPotenciaisBeneficiarios(programa, validar);
        validar = validarNomePersonalidadeJuridica(programa, validar);
        validar = validarBensKits(programa, validar);
        validar = validarCriterioElegibilidade(programa, validar);
        validar = validarCriterioAcompanhamento(programa, validar);
        validar = validarNumeroProcessoSei(programa, validar);
        validar = validarCriterioAvaliacao(programa, validar);

        return validar;
    }

    private void atualizarTodasAsMensagens() {
        mensagensInformacoesGerais.setObject(mensagemTempAvisoInformacoes);
        mensagensRecurso.setObject(mensagemTempAvisoRecurso);
        mensagensBeneficiarios.setObject(mensagemTempAvisoBeneficiarios);
        mensagensVincular.setObject(mensagemTempAvisoVincular);
        mensagensElegibilidade.setObject(mensagemTempAvisoElegibilidade);
        mensagensAvaliacao.setObject(mensagemTempAvisoAvaliacao);
        mensagensAcompanhamento.setObject(mensagemTempAvisoAcompanhamento);
    }

    private void zerarTodasAsMensagens() {

        mensagensInformacoesGerais.setObject("");
        mensagensRecurso.setObject("");
        mensagensBeneficiarios.setObject("");
        mensagensVincular.setObject("");
        mensagensElegibilidade.setObject("");
        mensagensAvaliacao.setObject("");
        mensagensAcompanhamento.setObject("");
    }

    private String validarAbaInfo() {

        Programa programaTemp = recuperarProgramaAtualMontado();
        Boolean valida = false;

        if (abaInformacoesGeraisClicada) {
            valida = validarAbaInformacoes(programaTemp);
        }

        if (abaInformacoesGeraisClicada) {
            if ("infoGerais".equalsIgnoreCase(abaAtual)) {
                return "fa fa-pencil fa-lg mj_editando_aba";
            } else {
                if (valida) {
                    return "fa fa-check fa-lg mj_cor_verificado";
                } else {
                    return "fa fa-exclamation fa-lg mj_cor_erro";
                }
            }
        }
        return "";
    }

    private String validarAbaRecurso() {

        Programa programaTemp = recuperarProgramaAtualMontado();
        Boolean valida = false;
        if (abaRecursoFinanceiroClicada) {
            valida = validarAbaRecurso(programaTemp);
        }

        if (abaRecursoFinanceiroClicada) {
            if ("recursoFinanceiro".equalsIgnoreCase(abaAtual)) {
                return "fa fa-pencil fa-lg mj_editando_aba";
            } else {
                if (valida) {
                    return "fa fa-check fa-lg mj_cor_verificado";
                } else {
                    return "fa fa-exclamation fa-lg mj_cor_erro";
                }
            }
        }
        return "";
    }

    private String validarAbaBeneficiarios() {

        Programa programaTemp = recuperarProgramaAtualMontado();
        Boolean valida = false;
        if (abaPotenciaisBeneficiariosClicada) {
            valida = validarAbaBeneficiarios(programaTemp);
        }

        if (abaPotenciaisBeneficiariosClicada) {
            if ("potenciaisBeneficiarios".equalsIgnoreCase(abaAtual)) {
                return "fa fa-pencil fa-lg mj_editando_aba";
            } else {
                if (valida) {
                    return "fa fa-check fa-lg mj_cor_verificado";
                } else {
                    return "fa fa-exclamation fa-lg mj_cor_erro";
                }
            }
        }
        return "";
    }

    private String validarAbaVincular() {

        Programa programaTemp = recuperarProgramaAtualMontado();
        Boolean valida = false;
        if (abaVincularClicada) {
            valida = validarAbaVincular(programaTemp);
        }

        if (abaVincularClicada) {
            if ("vincularBemKit".equalsIgnoreCase(abaAtual)) {
                return "fa fa-pencil fa-lg mj_editando_aba";
            } else {
                if (valida) {
                    return "fa fa-check fa-lg mj_cor_verificado";
                } else {
                    return "fa fa-exclamation fa-lg mj_cor_erro";
                }
            }
        }
        return "";
    }

    private String validarAbaElegibilidade() {

        Programa programaTemp = recuperarProgramaAtualMontado();
        Boolean valida = false;
        if (abaElegibilidadeClicada) {
            valida = validarAbaElegibilidade(programaTemp);
        }

        if (abaElegibilidadeClicada) {
            if ("criteriosElegibilidade".equalsIgnoreCase(abaAtual)) {
                return "fa fa-pencil fa-lg mj_editando_aba";
            } else {
                if (valida) {
                    return "fa fa-check fa-lg mj_cor_verificado";
                } else {
                    return "fa fa-exclamation fa-lg mj_cor_erro";
                }
            }
        }
        return "";
    }

    private String validarAbaAvaliacao() {

        Programa programaTemp = recuperarProgramaAtualMontado();
        Boolean valida = false;
        if (abaAvaliacaoClicada) {
            valida = validarAbaAvaliacao(programaTemp);
        }

        if (abaAvaliacaoClicada) {
            if ("criteriosAvaliacao".equalsIgnoreCase(abaAtual)) {
                return "fa fa-pencil fa-lg mj_editando_aba";
            } else {
                if (valida) {
                    return "fa fa-check fa-lg mj_cor_verificado";
                } else {
                    return "fa fa-exclamation fa-lg mj_cor_erro";
                }
            }
        }
        return "";
    }

    private String validarAbaAcompanhamento() {

        Programa programaTemp = recuperarProgramaAtualMontado();
        Boolean valida = false;
        if (abaAcompanhamentoClicada) {
            valida = validarAbaAcompanhamento(programaTemp);
        }

        if (abaAcompanhamentoClicada) {
            if ("criteriosAcompanhamento".equalsIgnoreCase(abaAtual)) {
                return "fa fa-pencil fa-lg mj_editando_aba";
            } else {
                if (valida) {
                    return "fa fa-check fa-lg mj_cor_verificado";
                } else {
                    return "fa fa-exclamation fa-lg mj_cor_erro";
                }
            }
        }
        return "";
    }

    private String validarAbaAnexos() {

        if (abaAnexoClicada) {
            if ("anexos".equalsIgnoreCase(abaAtual)) {
                return "fa fa-pencil fa-lg mj_editando_aba";
            }
        }
        return "";
    }

    private String validarAbaHistorico() {

        if (abaHistoricoClicada) {
            if ("historico".equalsIgnoreCase(abaAtual)) {
                return "fa fa-pencil fa-lg mj_editando_aba";
            }
        }
        return "";
    }

    public boolean validarAbaInformacoes(Programa programa) {
        boolean validar = true;

        mensagemTempAvisoInformacoes = "";

        validar = validarNomePrograma(programa, validar);
        validar = validarNomeFantasia(programa, validar);
        validar = validarDescricao(programa, validar);
        validar = validarAnoPrograma(programa, validar);
        validar = validarFuncao(validar);
        validar = validarSubFuncao(validar);
        validar = validarNumeroProcessoSei(programa, validar);
        validar = validarOrgao(validar);
        validar = validarUnidadeExecutora(programa, validar);

        if (!validar) {
            mensagensInformacoesGerais.setObject(mensagemTempAvisoInformacoes);
        } else {
            mensagensInformacoesGerais.setObject("");
        }

        return validar;
    }

    private boolean validarAbaRecurso(Programa programa) {
        boolean validar = true;

        mensagemTempAvisoRecurso = "";

        validar = validarRecursoFinanceiro(programa, validar);

        if (!validar) {
            mensagensRecurso.setObject(mensagemTempAvisoRecurso);
        } else {
            mensagensRecurso.setObject("");
        }

        return validar;
    }

    private boolean validarAbaBeneficiarios(Programa programa) {
        boolean validar = true;

        mensagemTempAvisoBeneficiarios = "";

        validar = validarPotenciaisBeneficiarios(programa, validar);
        validar = validarNomePersonalidadeJuridica(programa, validar);

        if (!validar) {
            mensagensBeneficiarios.setObject(mensagemTempAvisoBeneficiarios);
        } else {
            mensagensBeneficiarios.setObject("");
        }

        return validar;
    }

    private boolean validarAbaVincular(Programa programa) {
        boolean validar = true;

        mensagemTempAvisoVincular = "";

        validar = validarBensKits(programa, validar);
        validar = validarValorMaximoProposta(programa, validar);

        if (!validar) {
            mensagensVincular.setObject(mensagemTempAvisoVincular);
        } else {
            mensagensVincular.setObject("");
        }

        return validar;
    }

    private boolean validarAbaElegibilidade(Programa programa) {
        boolean validar = true;

        mensagemTempAvisoElegibilidade = "";

        validar = validarCriterioElegibilidade(programa, validar);

        if (!validar) {
            mensagensElegibilidade.setObject(mensagemTempAvisoElegibilidade);
        } else {
            mensagensElegibilidade.setObject("");
        }

        return validar;
    }

    private boolean validarAbaAvaliacao(Programa programa) {
        boolean validar = true;

        mensagemTempAvisoAvaliacao = "";

        validar = validarCriterioAvaliacao(programa, validar);

        if (!validar) {
            mensagensAvaliacao.setObject(mensagemTempAvisoAvaliacao);
        } else {
            mensagensAvaliacao.setObject("");
        }

        return validar;
    }

    private boolean validarAbaAcompanhamento(Programa programa) {
        boolean validar = true;

        mensagemTempAvisoAcompanhamento = "";

        validar = validarCriterioAcompanhamento(programa, validar);

        if (!validar) {
            mensagensAcompanhamento.setObject(mensagemTempAvisoAcompanhamento);
        } else {
            mensagensAcompanhamento.setObject("");
        }

        return validar;
    }

    public boolean validarSalvar(Programa programa) {
        boolean validar = true;

        validar = validarNomePrograma(programa, validar);
        validar = validarNomeFantasia(programa, validar);
        validar = validarDescricao(programa, validar);
        validar = validarAnoPrograma(programa, validar);
        validar = validarFuncao(validar);
        validar = validarSubFuncao(validar);
        validar = validarValorMaximoProposta(programa, validar);
        validar = validarOrgao(validar);
        validar = validarUnidadeExecutora(programa, validar);
        validar = validarLimitacaoGeografica(programa, validar);
        validar = validarNomePersonalidadeJuridica(programa, validar);
        validar = validarNumeroProcessoSei(programa, validar);
        return validar;
    }

    public boolean validarFinalizar(Programa programa) {
        boolean validar = true;

        validar = validarNomePrograma(programa, validar);
        validar = validarNomeFantasia(programa, validar);
        validar = validarDescricao(programa, validar);
        validar = validarAnoPrograma(programa, validar);
        validar = validarFuncao(validar);
        validar = validarSubFuncao(validar);
        validar = validarRecursoFinanceiro(programa, validar);
        validar = validarValorMaximoProposta(programa, validar);
        validar = validarOrgao(validar);
        validar = validarUnidadeExecutora(programa, validar);
        validar = validarLimitacaoGeografica(programa, validar);
        validar = validarPotenciaisBeneficiarios(programa, validar);
        validar = validarNomePersonalidadeJuridica(programa, validar);
        validar = validarBensKits(programa, validar);
        validar = validarCriterioElegibilidade(programa, validar);
        validar = validarCriterioAcompanhamento(programa, validar);
        validar = validarNumeroProcessoSei(programa, validar);
        validar = validarCriterioAvaliacao(programa, validar);

        return validar;
    }

    public boolean validarSomaValoresKitsEBens(Programa programa, boolean valor) {
        boolean validar = valor;

        // Irá validar se a quantidade de kits e bens possuem mais gastos do que
        // o recurso financeiro.
        if (!publicizacaoService.recursosSupremGastosDeBensEKits(programa)) {
            if ((botaoSalvarClicado || botaoFinalizarClicado)) {
                mensagemTempAvisoInformacoes += "<p><li>Soma total de gastos com Bens e Kits não podem ser maiores que a soma total de Recursos Financeiros com Emendas.</li><p />";
                validar = false;
            }
        }
        return validar;
    }

    public boolean validarNomePrograma(Programa programa, boolean valor) {
        boolean validar = valor;

        if (programa.getNomePrograma() == null || "".equalsIgnoreCase(programa.getNomePrograma())) {

            // Somente será renderizada quando clicar ou no botão de salvar ou
            // concluir;
            if ((botaoSalvarClicado || botaoFinalizarClicado)) {
                mensagemTempAvisoInformacoes += "<p><li>O campo 'Nome do Programa' é obrigatório.</li><p />";
            }

            validar = false;
        }

        return validar;
    }

    public boolean validarNomeFantasia(Programa programa, boolean valor) {
        boolean validar = valor;

        if (programa.getNomeFantasiaPrograma() == null || "".equalsIgnoreCase(programa.getNomeFantasiaPrograma())) {

            if ((botaoSalvarClicado || botaoFinalizarClicado)) {
                mensagemTempAvisoInformacoes += "<p><li>O campo 'Nome Fantasia' é obrigatório.</li><p />";
            }

            validar = false;
        }

        return validar;
    }

    public boolean validarDescricao(Programa programa, boolean valor) {
        boolean validar = valor;

        if (programa.getDescricaoPrograma() == null || "".equalsIgnoreCase(programa.getDescricaoPrograma())) {

            if ((botaoSalvarClicado || botaoFinalizarClicado)) {
                mensagemTempAvisoInformacoes += "<p><li>O campo 'Descrição' é obrigatório.</li><p />";
            }

            validar = false;
        }

        return validar;
    }

    public boolean validarAnoPrograma(Programa programa, boolean valor) {
        boolean validar = valor;
        if (programa.getAnoPrograma() == null || programa.getAnoPrograma() == 0) {

            if ((botaoSalvarClicado || botaoFinalizarClicado)) {
                mensagemTempAvisoInformacoes += "<p><li>O campo 'Ano' é obrigatório.</li><p />";
            }
            validar = false;
        } else {
            boolean anoValido = programa.getAnoPrograma().toString().length() == 4;
            if (!anoValido) {

                if ((botaoSalvarClicado || botaoFinalizarClicado)) {
                    mensagemTempAvisoInformacoes += "<p><li>'Ano' não é um valor válido.</li><p />";
                }
                validar = false;
            }
        }
        return validar;
    }

    public boolean validarFuncao(boolean valor) {
        boolean validar = valor;

        Funcao func = panelInformacoesGerais.getFuncao();
        if (func == null || func.getId() == null) {

            if ((botaoSalvarClicado || botaoFinalizarClicado)) {
                mensagemTempAvisoInformacoes += "<p><li>O campo 'Função' é obrigatório.</li><p />";
            }
            validar = false;
        }

        return validar;
    }

    public boolean validarSubFuncao(boolean valor) {
        boolean validar = valor;

        SubFuncao sub = panelInformacoesGerais.getSubFuncao();
        if (sub == null || sub.getId() == null) {

            if ((botaoSalvarClicado || botaoFinalizarClicado)) {
                mensagemTempAvisoInformacoes += "<p><li>O campo 'Subfunção' é obrigatório.</li><p />";
            }
            validar = false;
        }

        return validar;
    }

    public boolean validarRecursoFinanceiro(Programa programa, boolean valor) {
        boolean validar = valor;

        if (programa.getRecursosFinanceiros() == null || programa.getRecursosFinanceiros().isEmpty()) {

            if (botaoFinalizarClicado || botaoSalvarClicado) {
                mensagemTempAvisoRecurso += "<p><li>Vincule ao menos 1 Recurso Financeiro.</li><p />";
            }
            validar = false;
        }
        return validar;
    }

    private boolean validarValorMaximoProposta(Programa programa, boolean valor) {
        boolean validar = valor;

        if (programa.getValorMaximoProposta() == null || programa.getValorMaximoProposta().intValue() == 0) {

            if ((botaoFinalizarClicado)) {
                mensagemTempAvisoVincular += "<p><li>O Valor Máximo por Proposta não pode ser '0'.</li><p />";
            }
            return false;
        }

        if (programa.getValorMaximoProposta() != null && recursoFinanceiroPanel.getValorTotal() != null) {
            if (programa.getValorMaximoProposta().doubleValue() > recursoFinanceiroPanel.getValorTotal().doubleValue()) {

                if ((botaoFinalizarClicado)) {
                    mensagemTempAvisoVincular += "<p><li>O Valor Máximo por Proposta não pode ser maior do que o Valor Total do Recurso Financeiro.</li><p />";
                }
                validar = false;
            }
        }
        return validar;
    }

    public boolean validarCriterioAvaliacao(Programa programa, boolean valor) {
        boolean validar = valor;

        if (programa.getCriteriosAvaliacao() == null || programa.getCriteriosAvaliacao().isEmpty()) {

            if (botaoFinalizarClicado) {
                mensagemTempAvisoAvaliacao += "<p><li>Vincule ao menos 1 Critério de Avaliação.</li><p />";
            }
            validar = false;
        }

        return validar;
    }

    public boolean validarOrgao(boolean valor) {
        boolean validar = valor;

        Orgao org = panelInformacoesGerais.getOrgao();
        if (org == null || org.getId() == null) {

            if ((botaoSalvarClicado || botaoFinalizarClicado)) {
                mensagemTempAvisoInformacoes += "<p><li>O campo 'Órgão (Cód. SIORG)' é obrigatório.</li><p />";
            }
            validar = false;
        }

        return validar;
    }

    public boolean validarUnidadeExecutora(Programa programa, boolean valor) {
        boolean validar = valor;

        if (programa.getUnidadeExecutora() == null || programa.getUnidadeExecutora().getId() == null) {

            if ((botaoSalvarClicado || botaoFinalizarClicado)) {
                mensagemTempAvisoInformacoes += "<p><li>O campo 'Unidade Executora' é obrigatório.</li><p />";
            }
            validar = false;
        }

        return validar;
    }

    public boolean validarLimitacaoGeografica(Programa programa, boolean valor) {
        boolean validar = valor;

        if (programa.getPossuiLimitacaoGeografica() == null) {

            if ((botaoSalvarClicado || botaoFinalizarClicado)) {
                mensagemTempAvisoBeneficiarios += "<p><li>O campo 'Limitação Geográfica' é obrigatório.</li><p />";
            }
            validar = false;
        }

        return validar;
    }

    public boolean validarPotenciaisBeneficiarios(Programa programa, boolean valor) {
        boolean validar = valor;

        if (programa.getPossuiLimitacaoGeografica() != null && programa.getPossuiLimitacaoGeografica()) {
            if (programa.getPotenciaisBeneficiariosUf() == null || programa.getPotenciaisBeneficiariosUf().isEmpty()) {

                if (botaoFinalizarClicado) {
                    mensagemTempAvisoBeneficiarios += "<p><li>É necessário a escolha de pelo menos uma UF'.</li><p />";
                }
                validar = false;
            }

        }

        if (programa.getPossuiLimitacaoMunicipalEspecifica() != null && programa.getPossuiLimitacaoMunicipalEspecifica()) {
            if (programa.getPotenciaisBeneficiariosUf() != null || !programa.getPotenciaisBeneficiariosUf().isEmpty()) {
                int i = 0;
                for (ProgramaPotencialBeneficiarioUf p : programa.getPotenciaisBeneficiariosUf()) {
                    if (p.getPotencialBeneficiarioMunicipios() != null && !p.getPotencialBeneficiarioMunicipios().isEmpty()) {
                        i++;
                    }
                }

                if (i == 0) {
                    if (botaoFinalizarClicado) {
                        mensagemTempAvisoBeneficiarios += "<p><li>É necessário a escolha de pelo menos um Município'.</li><p />";
                    }
                    validar = false;
                }
            }

        }

        return validar;
    }

    public boolean validarNomePersonalidadeJuridica(Programa programa, boolean valor) {
        boolean validar = valor;

        if (programa.getTipoPersonalidadeJuridica() == null) {

            if ((botaoSalvarClicado || botaoFinalizarClicado)) {
                mensagemTempAvisoBeneficiarios += "<p><li>O campo 'Personalidade Jurídica' é obrigatório.</li><p />";
            }
            validar = false;
        }

        return validar;
    }

    public boolean validarBensKits(Programa programa, boolean valor) {
        boolean validar = valor;

        if ((programa.getProgramaKits() == null || programa.getProgramaKits().isEmpty()) && (programa.getProgramaBens() == null || programa.getProgramaBens().isEmpty())) {

            if (botaoFinalizarClicado) {
                mensagemTempAvisoVincular += "<p><li>Adicione ao menos 1 Bem ou 1 Kit.</li><p />";
            }
            validar = false;
        } else {
            if (programa.getProgramaKits() == null || !programa.getProgramaKits().isEmpty()) {
                List<ProgramaKit> listaKit = programa.getProgramaKits();
                int flag = 0;
                int qtdMaximaMaior = 0;
                for (ProgramaKit kit : listaKit) {
                    if (kit.getQuantidade() == null || kit.getQuantidade() < 1) {
                        flag++;
                    }

                    if (kit.getQuantidadePorProposta() == null) {
                        kit.setQuantidadePorProposta(0);
                    }

                    if (kit.getQuantidadePorProposta() != null && (kit.getQuantidadePorProposta() > kit.getQuantidade())) {
                        qtdMaximaMaior++;
                    }

                    if (botaoFinalizarClicado) {
                        Double valorEstimadoKit = kit.getKit().getValorEstimado().doubleValue();
                        Double valorMaximoProposta = recursoFinanceiroPanel.getValorTotal().doubleValue();

                        if (valorEstimadoKit > valorMaximoProposta) {
                            mensagemTempAvisoVincular += "<p><li>O valor do kit '" + kit.getKit().getNomeKit() + "' é de " + formatoDinheiro(kit.getKit().getValorEstimado()) + " sendo maior do que o 'Valor máximo por proposta'.</li><p />";
                            validar = false;
                        }
                    }
                }

                if (flag > 0) {

                    if ((botaoSalvarClicado || botaoFinalizarClicado)) {
                        mensagemTempAvisoVincular += "<p><li>Existe Kit adicionado a lista com a QUANTIDADE TOTAL '0', informe um valor para este campo.</li><p />";
                    }
                    validar = false;
                } else {
                    if (qtdMaximaMaior > 0) {

                        if ((botaoSalvarClicado || botaoFinalizarClicado)) {
                            mensagemTempAvisoVincular += "<p><li>Existe Kit com a QUANTIDADE MÁXIMA POR PROPOSTA maior do que a QUANTIDADE TOTAL.</li><p />";
                        }
                        validar = false;
                    }
                }
            }

            if (programa.getProgramaBens() == null || !programa.getProgramaBens().isEmpty()) {
                List<ProgramaBem> listaBem = programa.getProgramaBens();
                int flag = 0;
                int qtdMaximaMaior = 0;
                for (ProgramaBem bem : listaBem) {
                    if (bem.getQuantidade() == null || bem.getQuantidade() < 1) {
                        flag++;
                    }

                    if (bem.getQuantidadePorProposta() == null) {
                        bem.setQuantidadePorProposta(0);
                    }

                    if (bem.getQuantidadePorProposta() != null && (bem.getQuantidadePorProposta() > bem.getQuantidade())) {
                        qtdMaximaMaior++;
                    }
                }

                if (flag > 0) {

                    if ((botaoSalvarClicado || botaoFinalizarClicado)) {
                        mensagemTempAvisoVincular += "<p><li>Existe Bem adicionado a lista com a QUANTIDADE TOTAL '0', informe um valor para este campo.</li><p />";
                    }
                    validar = false;
                } else {

                    if (qtdMaximaMaior > 0) {

                        if ((botaoSalvarClicado || botaoFinalizarClicado)) {
                            mensagemTempAvisoVincular += "<p><li>Existe Bem com a QUANTIDADE MÁXIMA POR PROPOSTA maior do que a QUANTIDADE TOTAL.</li><p />";
                        }
                        validar = false;
                    }
                }
            }
        }

        return validar;
    }

    public boolean validarNumeroProcessoSei(Programa programa, boolean valor) {
        boolean validar = valor;

        programa.setNumeroProcessoSEI(panelInformacoesGerais.getNumeroProcessoSEI());
        if (programa.getNumeroProcessoSEI() == null || "".equalsIgnoreCase(programa.getNumeroProcessoSEI())) {

            if ((botaoSalvarClicado || botaoFinalizarClicado)) {
                mensagemTempAvisoInformacoes += "<p><li>O campo 'Número do Processo SEI' é obrigatório.</li><p />";
            }
            validar = false;
        } else {
            if (programa.getNumeroProcessoSEI().length() < 17) {

                if ((botaoSalvarClicado || botaoFinalizarClicado)) {
                    mensagemTempAvisoInformacoes += "<p><li>O campo 'Número do Processo SEI' deverá conter 17 caracteres númericos.</li><p />";
                }
                validar = false;
            }
        }

        return validar;
    }

    public boolean validarCriterioElegibilidade(Programa programa, boolean valor) {
        boolean validar = valor;

        if (programa.getCriteriosElegibilidade() == null || programa.getCriteriosElegibilidade().isEmpty()) {

            if (botaoFinalizarClicado) {
                mensagemTempAvisoElegibilidade += "<p><li>Adicione ao menos 1 Critério de Elegibilidade e de Avaliação de Propostas.</li><p />";
            }
            validar = false;
        }

        return validar;
    }

    public boolean validarCriterioAcompanhamento(Programa programa, boolean valor) {
        boolean validar = valor;

        if (programa.getCriteriosAcompanhamento() == null || programa.getCriteriosAcompanhamento().isEmpty()) {

            if (botaoFinalizarClicado) {
                mensagemTempAvisoAcompanhamento += "<p><li>Adicione ao menos 1 Critério de Acompanhamento.</li><p />";
            }
            validar = false;
        }

        return validar;
    }

    public String formatoDinheiro(BigDecimal valor) {
        return NumberFormat.getCurrencyInstance().format(valor);
    }

    private String limparNumeroSei(String valor) {
        if (valor == null || "".equalsIgnoreCase(valor)) {
            return "";
        }
        String value = valor;
        value = value.replace(".", "");
        value = value.replace("/", "");
        value = value.replace("-", "");
        return value;
    }

    public Form<Programa> getForm() {
        return form;
    }

    public void setForm(Form<Programa> form) {
        this.form = form;
    }

    public RecursoFinanceiroPanel getRecursoFinanceiroPanel() {
        return recursoFinanceiroPanel;
    }

    public void setRecursoFinanceiroPanel(RecursoFinanceiroPanel recursoFinanceiroPanel) {
        this.recursoFinanceiroPanel = recursoFinanceiroPanel;
    }

    public Programa getEntity() {
        return entity;
    }

    public void setEntity(Programa entity) {
        this.entity = entity;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public PanelInformacoesGeraisPage getPanelInformacoesGerais() {
        return panelInformacoesGerais;
    }

    public void setPanelInformacoesGerais(PanelInformacoesGeraisPage panelInformacoesGerais) {
        this.panelInformacoesGerais = panelInformacoesGerais;
    }

    public String getMsgAvisoSalvarFinalizar() {
        return msgAvisoSalvarFinalizar;
    }

    public void setMsgAvisoSalvarFinalizar(String msgAvisoSalvarFinalizar) {
        this.msgAvisoSalvarFinalizar = msgAvisoSalvarFinalizar;
    }
}
