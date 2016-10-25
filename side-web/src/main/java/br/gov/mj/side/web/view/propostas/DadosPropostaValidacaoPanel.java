package br.gov.mj.side.web.view.propostas;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.validation.validator.StringValidator;

import br.gov.mj.apoio.entidades.MotivoAnalise;
import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.listener.FeedbackPanelListener;
import br.gov.mj.infra.wicket.model.LambdaModel;
import br.gov.mj.side.entidades.enums.EnumStatusInscricao;
import br.gov.mj.side.entidades.enums.EnumTipoResposta;
import br.gov.mj.side.entidades.programa.ProgramaCriterioAvaliacaoOpcaoResposta;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoAnexoAvaliacao;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoPrograma;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoProgramaCriterioAvaliacao;
import br.gov.mj.side.entidades.programa.inscricao.analise.HistoricoAnaliseAvaliacao;
import br.gov.mj.side.entidades.programa.inscricao.analise.ListaAvaliacaoPublicado;
import br.gov.mj.side.web.dto.AnexoDto;
import br.gov.mj.side.web.service.InscricaoAnexoAvaliacaoService;
import br.gov.mj.side.web.service.InscricaoProgramaAvaliacaoService;
import br.gov.mj.side.web.service.InscricaoProgramaService;
import br.gov.mj.side.web.service.ListaPublicadoService;
import br.gov.mj.side.web.service.MotivoAnaliseService;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.SideUtil;
import br.gov.mj.side.web.view.components.AjaxDialogButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal.Backdrop;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.TextContentModal;

public class DadosPropostaValidacaoPanel extends Panel {
    private static final long serialVersionUID = 1L;

    private InscricaoPrograma inscricao;

    private PanelDataViewCriterio panelDataViewCriterio;
    private PanelAnaliseFinal panelAnaliseFinal;
    private PanelRecurso panelRecurso;
    private PanelMotivoCriterioNaoAceito panelMotivoCriterioNaoAceito;
    private PanelPontuacaoClassificacao panelPontuacaoClassificacao;
    private PanelHistorico panelHistorico;

    private HistoricoAnaliseAvaliacao historicoAvaliacao = new HistoricoAnaliseAvaliacao();
    private Boolean aceito;
    private Boolean readOnly;
    private Boolean faseRecursoAvaliacao = false;
    private Boolean finalizadoRecursoAvaliacao = false;
    private Integer notaCriterio = 0;

    private DataView<InscricaoProgramaCriterioAvaliacao> dataView;
    private Button btnDownload;
    private DropDownChoice<Boolean> dropDownChoiceAceito;
    private DropDownChoice<Integer> dropDownChoiceNota;
    private TextArea<String> textAreaMotivo;
    private TextArea<String> textMotivoPanel;
    private String msgConfirm;
    private Modal<String> modalConfirm;
    private Boolean alteradoResultadoAnalise = false;
    private int valorPontuacaoFinalAntesAlteracao;

    private List<InscricaoProgramaCriterioAvaliacao> listaCriterios = new ArrayList<InscricaoProgramaCriterioAvaliacao>();
    private List<ListaAvaliacaoPublicado> listaDeAvaliacaoPublicada = new ArrayList<ListaAvaliacaoPublicado>();
    private List<HistoricoAnaliseAvaliacao> listaDeHistorico = new ArrayList<HistoricoAnaliseAvaliacao>();

    @Inject
    private ListaPublicadoService listaPublicadoService;

    @Inject
    private InscricaoProgramaService inscricaoService;

    @Inject
    private InscricaoAnexoAvaliacaoService inscricaoAnexoAvaliacaoService;

    @Inject
    private MotivoAnaliseService motivoService;

    @Inject
    private InscricaoProgramaAvaliacaoService avaliacaoService;

    @Inject
    ComponentFactory componentFactory;

    public DadosPropostaValidacaoPanel(String id, InscricaoPrograma inscricao, Boolean readOnly) {
        super(id);

        this.inscricao = inscricao;
        this.readOnly = readOnly;

        initVariaveis();
        initComponents();
        isReadOnly();
    }

    private void initVariaveis() {
        listaCriterios = inscricaoService.buscarInscricaoProgramaCriterioAvaliacao(inscricao);
        historicoAvaliacao = avaliacaoService.buscarUltimoHistoricoAnaliseAvaliacao(inscricao);
        historicoAvaliacao.setDescricaoJustificativa(null);
        if (historicoAvaliacao.getPontuacaoFinal() != null) {
            valorPontuacaoFinalAntesAlteracao = historicoAvaliacao.getPontuacaoFinal();
        }

        faseRecursoAvaliacao = inscricao.getEstaEmFaseRecursoAvaliacao();
        finalizadoRecursoAvaliacao = inscricao.getFinalizadoRecursoAvaliacao();

        listaDeAvaliacaoPublicada = SideUtil.convertAnexoDtoToEntityListaAvaliacaoPublicado(listaPublicadoService.buscarListaAvaliacaoPublicadoPeloIdPrograma(inscricao.getPrograma().getId()));
        calcularNotaAvaliacaoPrimeiroAcesso();
    }

    private void initComponents() {
        add(panelDataViewCriterio = new PanelDataViewCriterio("panelDataViewCriterio"));
        add(panelAnaliseFinal = new PanelAnaliseFinal("panelAnaliseFinal"));
        add(panelRecurso = new PanelRecurso("panelRecurso"));
        add(panelHistorico = new PanelHistorico("panelHistorico"));

        panelRecurso.setVisible(listaDeAvaliacaoPublicada != null && listaDeAvaliacaoPublicada.size() > 0);
        // panelAnaliseFinal.setVisible(mostrarPainelAnalise());
        panelHistorico.setVisible(mostrarDataViewHistorico());

        modalConfirm = newModal("modalConfirm");
        modalConfirm.show(false);
        add(modalConfirm);
        setMsgConfirm("Ao alterar o resultado da análise, justifique-se no campo abaixo.");
    }

    // Panels

    private class PanelDataViewCriterio extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelDataViewCriterio(String id) {
            super(id);
            setOutputMarkupId(true);

            add(newDataViewCriterios());// dataViewCriterios
        }
    }

    private class PanelMotivoCriterioNaoAceito extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelMotivoCriterioNaoAceito(String id, Item<InscricaoProgramaCriterioAvaliacao> item) {
            super(id);
            setOutputMarkupId(true);

            add(newLabelMotivoNaoAceito(item)); // lblMotivoNaoAceito
            add(newTextAreaMotivo(item)); // descricaoMotivo
        }
    }

    private class PanelAnaliseFinal extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelAnaliseFinal(String id) {
            super(id);
            setOutputMarkupId(true);

            add(newTextPontuacaoFinal());// txtPontuacaoFinal
            add(newTextAreaJustificativa());// txtJustificativa
            add(newDropDownMotivoAnalise()); // dropMotivoAnalse
            panelPontuacaoClassificacao = new PanelPontuacaoClassificacao("panelPontuacaoClassificacao");
            add(panelPontuacaoClassificacao);

            add(newLabelResultadoFinal()); // lblResultadoFinal
            add(newLabelMotivo()); // lblMotivoFinal
            add(newLabelJustificativa()); // lblJustificativaFinal
        }
    }

    private class PanelRecurso extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelRecurso(String id) {
            super(id);
            setOutputMarkupId(true);

            add(newCheckfaseRecursoAvaliacao()); // checkfaseRecursoAvaliacao
            add(newCheckfinalizadoRecursoAvaliacao()); // checkfinalizadoRecursoAvaliacao
            add(newTextFieldProcesso()); // txtNumeroProcesso
            add(newLabelProcesso()); // lblProcesso
            add(newLabelRecursoAnalisado()); // lblRecursoAnalisado
        }
    }

    private class PanelPontuacaoClassificacao extends WebMarkupContainer {
        public PanelPontuacaoClassificacao(String id) {
            super(id);
            add(newLabelPontuacaoValidacao()); // lblPontuacaoValidacao
        }
    }

    private class PanelCriterio extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelCriterio(String id, Item<InscricaoProgramaCriterioAvaliacao> item) {
            super(id);
            setOutputMarkupId(true);

            add(newLabelNumeroCriterio(item)); // lblNumero
            add(newLabelNomeCriterio(item)); // lblNome
            add(newLabelDescricaoCriterio(item)); // lblDescricao
            add(newLabelFormaVerificacaoCriterio(item)); // lblFormaVerificacao
            add(newLabelRespostaCriterio(item)); // lblResposta
            add(newLabelPesoCriterio(item)); // lblPeso
            add(newLabelCriterioDesempate(item)); // lblCriterioDesempate
            add(getContainerDeDownload(item));//containerDownload
            
            add(newDropDownCriterioAceito(item)); // aceitaCriterioAvaliacao
            add(newDropDownNota(item)); // notaCriterio
            add(panelMotivoCriterioNaoAceito = new PanelMotivoCriterioNaoAceito("panelMotivoCriterioNaoAceito", item));
        }
    }
    
    private WebMarkupContainer getContainerDeDownload(Item<InscricaoProgramaCriterioAvaliacao> item){
        WebMarkupContainer container = new WebMarkupContainer("containerDownload");
        
        List<AnexoDto> listaAnexos = inscricaoAnexoAvaliacaoService.buscarInscricaoAnexoAvaliacao(item.getModelObject().getId());
        container.add(newListViewAnexosCriterios(SideUtil.convertAnexoDtoToEntityInscricaoAnexoAvaliacao(listaAnexos)));
        
        if(listaAnexos != null && listaAnexos.isEmpty()){
            container.setVisible(false);
        }
        
        return container;
        
    }

    private class PanelHistorico extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelHistorico(String id) {
            super(id);
            setOutputMarkupId(true);

            listaDeHistorico = avaliacaoService.buscarHistoricoAnaliseAvaliacao(inscricao);
            add(newDataViewCriterios(listaDeHistorico));
        }
    }

    // Componentes

    private DataView<InscricaoProgramaCriterioAvaliacao> newDataViewCriterios() {
        dataView = new DataView<InscricaoProgramaCriterioAvaliacao>("dataViewCriterios", new CriteriosProvider()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<InscricaoProgramaCriterioAvaliacao> item) {
                item.add(new PanelCriterio("panelCriterio", item));
            }
        };
        dataView.setOutputMarkupId(true);
        return dataView;
    }

    private PropertyListView<InscricaoAnexoAvaliacao> newListViewAnexosCriterios(List<InscricaoAnexoAvaliacao> lista) {
        PropertyListView<InscricaoAnexoAvaliacao> listView = new PropertyListView<InscricaoAnexoAvaliacao>("listaAnexos", lista) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<InscricaoAnexoAvaliacao> item) {
                item.add(newButtonDownload(item)); // btnDownload
                item.add(new Label("lblNomeAnexo", item.getModelObject().getNomeAnexo()));
            }
        };

        listView.setOutputMarkupId(true);
        return listView;
    }

    private Label newLabelNumeroCriterio(Item<InscricaoProgramaCriterioAvaliacao> item) {
        Label labelNumero = new Label("lblNumero", item.getIndex() + 1);
        labelNumero.setOutputMarkupId(true);
        return labelNumero;
    }

    private Label newLabelNomeCriterio(Item<InscricaoProgramaCriterioAvaliacao> item) {
        Label labelNome = new Label("lblNome", item.getModelObject().getProgramaCriterioAvaliacao().getNomeCriterioAvaliacao());
        labelNome.setOutputMarkupId(true);
        return labelNome;
    }

    private Label newLabelDescricaoCriterio(Item<InscricaoProgramaCriterioAvaliacao> item) {
        Label labelDescricao = new Label("lblDescricao", item.getModelObject().getProgramaCriterioAvaliacao().getDescricaoCriterioAvaliacao());
        labelDescricao.setOutputMarkupId(true);
        return labelDescricao;
    }

    private Label newLabelFormaVerificacaoCriterio(Item<InscricaoProgramaCriterioAvaliacao> item) {
        Label labelForma = new Label("lblFormaVerificacao", item.getModelObject().getProgramaCriterioAvaliacao().getFormaVerificacao());
        labelForma.setOutputMarkupId(true);
        return labelForma;
    }

    private Label newLabelRespostaCriterio(Item<InscricaoProgramaCriterioAvaliacao> item) {
        Label labelResposta = new Label("lblResposta", item.getModelObject().getDescricaoResposta());
        labelResposta.setOutputMarkupId(true);
        return labelResposta;
    }

    private Label newLabelPesoCriterio(Item<InscricaoProgramaCriterioAvaliacao> item) {
        Label labelPeso = new Label("lblPeso", item.getModelObject().getProgramaCriterioAvaliacao().getPesoResposta());
        labelPeso.setOutputMarkupId(true);
        return labelPeso;
    }

    private Label newLabelCriterioDesempate(Item<InscricaoProgramaCriterioAvaliacao> item) {
        Label labelDesempate = new Label("lblCriterioDesempate", item.getModelObject().getProgramaCriterioAvaliacao().getUtilizadoParaCriterioDesempate() ? "Sim" : "Não");
        labelDesempate.setOutputMarkupId(true);
        return labelDesempate;
    }

    private Button newButtonDownload(ListItem<InscricaoAnexoAvaliacao> item) {
        Button btnDownload = componentFactory.newButton("btnDownload", () -> download(item));
        btnDownload.setOutputMarkupId(true);
        return btnDownload;
    }

    private Link newLinkDownload(ListItem<InscricaoAnexoAvaliacao> item) {
        Link link1 = new Link("btnDownload") {
            public void onClick() {
                download(item);
            }
        };
        link1.setOutputMarkupId(true);
        return link1;
    }

    private void download(ListItem<InscricaoAnexoAvaliacao> item) {
        InscricaoAnexoAvaliacao a = item.getModelObject();
        if (a.getId() != null) {
            AnexoDto retorno = inscricaoAnexoAvaliacaoService.buscarPeloId(a.getId());
            SideUtil.download(retorno.getConteudo(), retorno.getNomeAnexo());
        } else {
            SideUtil.download(a.getConteudo(), a.getNomeAnexo());
        }
    }

    private DropDownChoice<Boolean> newDropDownCriterioAceito(Item<InscricaoProgramaCriterioAvaliacao> item) {
        dropDownChoiceAceito = new DropDownChoice<Boolean>("aceitaCriterioAvaliacao", new PropertyModel(item.getModelObject(), "aceitaCriterioAvaliacao"), Arrays.asList(Boolean.TRUE, Boolean.FALSE));
        dropDownChoiceAceito.setLabel(Model.of("Critério Aceito"));
        dropDownChoiceAceito.setEnabled(mostrarCriteriosEditaveis());
        dropDownChoiceAceito.setNullValid(true);
        dropDownChoiceAceito.setChoiceRenderer(new IChoiceRenderer<Boolean>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Object getDisplayValue(Boolean object) {
                if (object != null && object) {
                    return "Sim";
                } else {
                    return "Não";
                }
            }

            @Override
            public String getIdValue(Boolean object, int index) {
                return object.toString();
            }
        });

        dropDownChoiceAceito.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1L;

            protected void onUpdate(AjaxRequestTarget target) {
                acaoDropCriterioAceito(target, item);
                mostrarTextAreaMotivo(target, item);
            }
        });
        return dropDownChoiceAceito;
    }

    private Label newLabelMotivoNaoAceito(Item<InscricaoProgramaCriterioAvaliacao> item) {
        Label lbl = new Label("lblMotivoNaoAceito", "* Motivo");
        lbl.setVisible(mostrarLabelMotivoNaoAceito(item));
        return lbl;
    }

    private DropDownChoice<Integer> newDropDownNota(Item<InscricaoProgramaCriterioAvaliacao> item) {
        List<Integer> listaNotas = gerarNotasDropDown();
        dropDownChoiceNota = new DropDownChoice<Integer>("notaCriterio", new PropertyModel(item.getModelObject(), "notaCriterio"), listaNotas);
        dropDownChoiceNota.setEnabled(ativarDropNota(item));
        respostaTipoListaSelecao(item);
        dropDownChoiceNota.setOutputMarkupId(true);

        dropDownChoiceNota.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1L;

            protected void onUpdate(AjaxRequestTarget target) {

                acaoCalcularNotaValidacao(target);
                target.add(panelDataViewCriterio);
            }
        });
        return dropDownChoiceNota;
    }

    public TextArea<String> newTextAreaMotivo(Item<InscricaoProgramaCriterioAvaliacao> item) {

        textMotivoPanel = new TextArea<String>("descricaoMotivo", null);
        textMotivoPanel.setLabel(Model.of("Motivo"));
        textMotivoPanel.setOutputMarkupId(true);
        textMotivoPanel.setEnabled(mostrarCriteriosEditaveis());
        textMotivoPanel.setVisible(mostraTextFieldMotivo(item));
        textMotivoPanel.add(StringValidator.maximumLength(Constants.TAMANHO_CARACTERES_CAMPO_TEXT_AREA));
        actionTextArea(textMotivoPanel);
        return textMotivoPanel;
    }

    private TextField<Integer> newTextPontuacaoFinal() {
        TextField<Integer> text = componentFactory.newTextField("txtPontuacaoFinal", "Pontuação Final", false, new PropertyModel<Integer>(historicoAvaliacao, "pontuacaoFinal"));
        text.setEnabled(mostrarHistoricoEditaveis());
        text.setVisible(mostrarPainelAnalise());

        text.add(new AjaxFormComponentUpdatingBehavior("onkeyup") {
            private static final long serialVersionUID = 1L;

            protected void onUpdate(AjaxRequestTarget target) {
                desabilitarFeedBackListener();
                if (historicoAvaliacao != null && (text.getModelObject() == null || text.getModelObject().intValue() != valorPontuacaoFinalAntesAlteracao)) {
                    alteradoResultadoAnalise = true;
                } else {
                    alteradoResultadoAnalise = false;
                }
            }
        });

        return text;
    }

    private DropDownChoice<MotivoAnalise> newDropDownMotivoAnalise() {

        DropDownChoice<MotivoAnalise> dropResultado = new DropDownChoice<MotivoAnalise>("dropMotivoAnalse", new PropertyModel<MotivoAnalise>(historicoAvaliacao, "motivoAnalise"), motivoService.buscarTodos(), new ChoiceRenderer<MotivoAnalise>("nomeMotivo"));
        dropResultado.setNullValid(true);
        dropResultado.setEnabled(mostrarHistoricoEditaveis());
        dropResultado.setVisible(mostrarPainelAnalise());
        return dropResultado;
    }

    public TextArea<String> newTextAreaJustificativa() {

        TextArea<String> text = new TextArea<String>("txtJustificativa", new PropertyModel<String>(historicoAvaliacao, "descricaoJustificativa"));
        text.setOutputMarkupId(true);
        text.setEnabled(mostrarHistoricoEditaveis());
        text.setVisible(mostrarPainelAnalise());
        text.add(StringValidator.maximumLength(Constants.TAMANHO_CARACTERES_CAMPO_TEXT_AREA));
        actionTextArea(text);
        return text;
    }

    private Label newLabelPontuacaoValidacao() {

        Label labelValidacao = new Label("lblPontuacaoValidacao", notaCriterio);
        labelValidacao.setOutputMarkupId(true);
        return labelValidacao;
    }

    private Label newLabelResultadoFinal() {
        Label lbl = new Label("lblResultadoFinal", "Pontuação Final");
        lbl.setVisible(mostrarPainelAnalise());
        return lbl;
    }

    private Label newLabelMotivo() {
        Label lbl = new Label("lblMotivoFinal", "Motivo");
        lbl.setVisible(mostrarPainelAnalise());
        return lbl;
    }

    private Label newLabelJustificativa() {
        Label lbl = new Label("lblJustificativaFinal", "Justificativa");
        lbl.setVisible(mostrarPainelAnalise());
        return lbl;
    }

    public TextArea<String> newTextAreaMotivoAnaliseFinal() {

        TextArea<String> text = new TextArea<String>("txtJustificativa", new PropertyModel<String>(historicoAvaliacao, "descricaoJustificativa"));
        text.setLabel(Model.of("Motivo"));
        text.setOutputMarkupId(true);
        text.add(StringValidator.maximumLength(Constants.TAMANHO_CARACTERES_CAMPO_TEXT_AREA));
        actionTextArea(text);
        return text;
    }

    private DataView<HistoricoAnaliseAvaliacao> newDataViewCriterios(List<HistoricoAnaliseAvaliacao> lista) {
        DataView<HistoricoAnaliseAvaliacao> dataView = new DataView<HistoricoAnaliseAvaliacao>("dataViewHistorico", new ListDataProvider<HistoricoAnaliseAvaliacao>(lista)) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<HistoricoAnaliseAvaliacao> item) {
                item.add(new Label("histDtCadastro", dataCadastroBR(item.getModelObject().getDataCadastro()) == null ? " - " : dataCadastroBR(item.getModelObject().getDataCadastro())));
                item.add(new Label("histMotivoAnalise", item.getModelObject().getMotivoAnalise() == null ? " - " : item.getModelObject().getMotivoAnalise().getNomeECodigo()));
                item.add(new Label("histJustificativa", item.getModelObject().getDescricaoJustificativa() == null ? " - " : item.getModelObject().getDescricaoJustificativa()));
                item.add(new Label("histResultadoFinal", item.getModelObject().getPontuacaoFinal() == null ? " - " : item.getModelObject().getPontuacaoFinal()));
                item.add(new Label("histUsuarioCadastro", item.getModelObject().getUsuarioCadastro() == null ? " - " : item.getModelObject().getUsuarioCadastro()));
            }
        };
        dataView.setVisible(mostrarDataViewHistorico());
        return dataView;
    }

    public AjaxCheckBox newCheckfaseRecursoAvaliacao() {
        AjaxCheckBox checkApresentarRecurso = new AjaxCheckBox("checkfaseRecursoAvaliacao", new PropertyModel<Boolean>(this, "faseRecursoAvaliacao")) {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                actionCheckfaseRecursoAvaliacao(target);
            }
        };
        checkApresentarRecurso.setOutputMarkupId(true);
        checkApresentarRecurso.setEnabled(ativarCheckfaseRecursoAvaliacao());
        return checkApresentarRecurso;
    }

    public AjaxCheckBox newCheckfinalizadoRecursoAvaliacao() {
        AjaxCheckBox checkfinalizadoRecursoAvaliacao = new AjaxCheckBox("checkfinalizadoRecursoAvaliacao", new PropertyModel<Boolean>(this, "finalizadoRecursoAvaliacao")) {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                actionCheckfinalizadoRecursoAvaliacao(target);
            }
        };
        checkfinalizadoRecursoAvaliacao.setOutputMarkupId(true);
        checkfinalizadoRecursoAvaliacao.setVisible(mostrarCheckfinalizadoRecursoAvaliacao());
        checkfinalizadoRecursoAvaliacao.setEnabled(ativarCheckFinalizadoRecursoAvaliacao());
        return checkfinalizadoRecursoAvaliacao;
    }

    private TextField<String> newTextFieldProcesso() {
        TextField<String> field = componentFactory.newTextField("txtNumeroProcesso", "Número do Processo", false, new PropertyModel(inscricao, "numeroProcessoSEIRecursoAvaliacao"));
        field.add(StringValidator.maximumLength(20));
        actionTextFieldNome(field);
        field.setEnabled(!finalizadoRecursoAvaliacao && !isGeradaSegundaListaDeAvaliacao());
        field.setVisible(faseRecursoAvaliacao);
        return field;
    }

    private Label newLabelProcesso() {
        Label labelProcesso = new Label("lblProcesso", "Número do Processo");
        labelProcesso.setVisible(faseRecursoAvaliacao);
        return labelProcesso;
    }

    private Label newLabelRecursoAnalisado() {
        Label labelProcesso = new Label("lblRecursoAnalisado", "Recurso Analisado");
        labelProcesso.setVisible(faseRecursoAvaliacao);
        return labelProcesso;
    }

    private Modal<String> newModal(String id) {
        Modal<String> modal = new TextContentModal(id, new LambdaModel<String>(this::getMsgConfirm, this::setMsgConfirm));
        modal.setBackdrop(Backdrop.STATIC);
        modal.addButton(newButtonFecharModal(modal));
        return modal;
    }

    // PROVIDER

    private class CriteriosProvider extends SortableDataProvider<InscricaoProgramaCriterioAvaliacao, String> {
        private static final long serialVersionUID = 1L;

        public CriteriosProvider() {
            // contructor
        }

        @Override
        public Iterator<InscricaoProgramaCriterioAvaliacao> iterator(long first, long size) {
            return listaCriterios.iterator();
            // return
            // inscricaoService.buscarSemPaginacao(getInscricao()).iterator();
        }

        @Override
        public long size() {
            return listaCriterios.size();
            // return inscricaoService.contarPaginado(new InscricaoPrograma());
        }

        @Override
        public IModel<InscricaoProgramaCriterioAvaliacao> model(InscricaoProgramaCriterioAvaliacao object) {
            return new CompoundPropertyModel<InscricaoProgramaCriterioAvaliacao>(object);
        }
    }

    // AÇÕES

    private AjaxDialogButton newButtonFecharModal(Modal<String> modal) {
        return new AjaxDialogButton(Model.of("Fechar"), Buttons.Type.Primary) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                modal.show(false);
                modal.close(target);
            }
        };
    }

    private boolean mostrarPainelAnalise() {
        if (listaDeAvaliacaoPublicada != null && listaDeAvaliacaoPublicada.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    private boolean mostrarDataViewHistorico() {
        if ((listaDeAvaliacaoPublicada != null && listaDeAvaliacaoPublicada.size() > 0) && (listaDeHistorico != null && listaDeHistorico.size() > 0)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isGeradaSegundaListaDeAvaliacao() {

        if (listaDeAvaliacaoPublicada == null || listaDeAvaliacaoPublicada.size() == 0) {
            return false;
        } else {
            if (listaDeAvaliacaoPublicada.size() > 1) {
                return true;
            } else {
                return false;
            }
        }
    }

    private void actionTextFieldNome(TextField<String> field) {
        AjaxFormComponentUpdatingBehavior onChangeAjaxBehavior = new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                // Setar no Model
            }
        };
        field.add(onChangeAjaxBehavior);
    }

    private boolean mostrarCheckfinalizadoRecursoAvaliacao() {
        if (!inscricao.getEstaEmFaseRecursoAvaliacao()) {
            return false;
        } else {
            return true;
        }
    }

    private boolean ativarCheckFinalizadoRecursoAvaliacao() {
        if (inscricao.getFinalizadoRecursoAvaliacao() || isGeradaSegundaListaDeAvaliacao()) {
            return false;
        } else {
            return true;
        }
    }

    private boolean ativarCheckfaseRecursoAvaliacao() {
        if (inscricao.getEstaEmFaseRecursoAvaliacao() || isGeradaSegundaListaDeAvaliacao()) {
            return false;
        } else {
            return true;
        }
    }

    public void actionCheckfaseRecursoAvaliacao(AjaxRequestTarget target) {
        if (faseRecursoAvaliacao) {
            inscricao.setEstaEmFaseRecursoAvaliacao(true);
        } else {
            inscricao.setEstaEmFaseRecursoAvaliacao(false);
        }

        panelRecurso.addOrReplace(newTextFieldProcesso());
        panelRecurso.addOrReplace(newLabelProcesso());
        panelRecurso.addOrReplace(newCheckfinalizadoRecursoAvaliacao());
        panelRecurso.addOrReplace(newLabelRecursoAnalisado());
        target.add(panelRecurso);
    }

    public void actionCheckfinalizadoRecursoAvaliacao(AjaxRequestTarget target) {

        if (finalizadoRecursoAvaliacao) {
            inscricao.setFinalizadoRecursoAvaliacao(true);
        } else {
            inscricao.setFinalizadoRecursoAvaliacao(false);
        }
    }

    public String dataCadastroBR(LocalDateTime dataCadastro) {
        DateTimeFormatter sdfPadraoBR = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        if (dataCadastro != null) {
            return sdfPadraoBR.format(dataCadastro);
        }
        return " - ";
    }

    private boolean ativarDropNota(Item<InscricaoProgramaCriterioAvaliacao> item) {
        boolean mostrar = true;

        if (readOnly) {
            mostrar = false;
        } else {
            if (item.getModelObject().getInscricaoPrograma().getStatusInscricao() == EnumStatusInscricao.CONCLUIDA_ANALISE_AVALIACAO) {
                mostrar = false;
            } else {
                if (item.getModelObject().getProgramaCriterioAvaliacao().getTipoResposta() == EnumTipoResposta.LISTA_SELECAO) {
                    mostrar = true;
                } else {
                    boolean aceito = dropDownChoiceAceito.getModelObject() == null ? false : dropDownChoiceAceito.getModelObject();
                    if (aceito) {
                        if (inscricao.getStatusInscricao() != null) {
                            EnumStatusInscricao status = inscricao.getStatusInscricao();
                            if (status == EnumStatusInscricao.ANALISE_AVALIACAO || status == EnumStatusInscricao.CONCLUIDA_ANALISE_ELEGIBILIDADE) {
                                mostrar = true;
                            } else {
                                mostrar = false;
                            }
                        } else {
                            mostrar = true;
                        }
                    } else {
                        mostrar = false;
                    }
                }
            }
        }
        return mostrar;
    }

    private boolean mostrarCriteriosEditaveis() {
        boolean mostrar = true;
        if (readOnly) {
            mostrar = false;
        } else {
            if (inscricao.getStatusInscricao() != null) {
                EnumStatusInscricao status = inscricao.getStatusInscricao();
                if (status == EnumStatusInscricao.ANALISE_AVALIACAO || status == EnumStatusInscricao.CONCLUIDA_ANALISE_ELEGIBILIDADE) {
                    mostrar = true;
                } else {
                    mostrar = false;
                }
            } else {
                mostrar = true;
            }
        }
        return mostrar;
    }

    // Ira avaliar se o campo abaixo dos critérios (Análise de Avaliacao) estará
    // ou não em estado Editavel
    private boolean mostrarHistoricoEditaveis() {
        boolean mostrar = true;
        if (readOnly) {
            mostrar = false;
        } else {
            if (!isGeradaSegundaListaDeAvaliacao()) {
                mostrar = true;
            } else {
                mostrar = false;
            }
        }
        return mostrar;
    }

    private void actionTextArea(TextArea field) {
        AjaxFormComponentUpdatingBehavior onChangeAjaxBehavior = new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                // Action
            }
        };
        field.add(onChangeAjaxBehavior);
    }

    private List<Integer> gerarNotasDropDown() {
        List<Integer> listaNotas = new ArrayList<Integer>();
        for(int i=0;i<101;i++){
            listaNotas.add(i);
        }
        return listaNotas;
    }

    private void isReadOnly() {
        if (readOnly) {
            panelAnaliseFinal.setEnabled(false);
        }
    }

    private void acaoDropCriterioAceito(AjaxRequestTarget target, Item<InscricaoProgramaCriterioAvaliacao> item) {
        if (item.getModelObject().getAceitaCriterioAvaliacao() == null && item.getModelObject().getProgramaCriterioAvaliacao().getTipoResposta() == EnumTipoResposta.LISTA_SELECAO) {
            Integer nota = devolverNotaOpcaoResposta(item.getModelObject().getProgramaCriterioAvaliacao().getCriteriosAvaliacaoOpcaoResposta(), item.getModelObject().getDescricaoResposta());
            item.getModelObject().setNotaCriterio(0);
        } else {
            item.getModelObject().setNotaCriterio(0);
            if (item.getModelObject().getAceitaCriterioAvaliacao() == null || !item.getModelObject().getAceitaCriterioAvaliacao()) {
                item.getModelObject().setNotaCriterio(0);
            }
        }
        acaoCalcularNotaValidacao(target);
        target.add(panelDataViewCriterio);
    }

    private void acaoCalcularNotaValidacao(AjaxRequestTarget target) {
        notaCriterio = 0;
        for (InscricaoProgramaCriterioAvaliacao ia : this.listaCriterios) {
            if (ia.getNotaCriterio() != null && ia.getAceitaCriterioAvaliacao() != null) {
                Integer notaTemp = 0;
                if (ia.getProgramaCriterioAvaliacao().getTipoResposta() == EnumTipoResposta.LISTA_SELECAO) {
                    if (ia.getAceitaCriterioAvaliacao() != null && ia.getAceitaCriterioAvaliacao()) {
                        notaTemp = devolverNotaOpcaoResposta(ia.getProgramaCriterioAvaliacao().getCriteriosAvaliacaoOpcaoResposta(), ia.getDescricaoResposta()) * ia.getProgramaCriterioAvaliacao().getPesoResposta();
                    } else {
                        notaTemp = ia.getNotaCriterio() * ia.getProgramaCriterioAvaliacao().getPesoResposta();
                    }
                } else {
                    notaTemp = ia.getProgramaCriterioAvaliacao().getPesoResposta() * ia.getNotaCriterio();
                }
                notaCriterio += notaTemp;
            }
        }
        panelPontuacaoClassificacao.addOrReplace(newLabelPontuacaoValidacao());
        target.add(panelPontuacaoClassificacao);
    }

    private void calcularNotaAvaliacaoPrimeiroAcesso() {
        notaCriterio = 0;
        for (InscricaoProgramaCriterioAvaliacao ia : this.listaCriterios) {
            if (ia.getNotaCriterio() != null && ia.getAceitaCriterioAvaliacao() != null) {
                Integer notaTemp = 0;
                if (ia.getProgramaCriterioAvaliacao().getTipoResposta() == EnumTipoResposta.LISTA_SELECAO) {
                    if (ia.getAceitaCriterioAvaliacao() != null && ia.getAceitaCriterioAvaliacao()) {
                        notaTemp = devolverNotaOpcaoResposta(ia.getProgramaCriterioAvaliacao().getCriteriosAvaliacaoOpcaoResposta(), ia.getDescricaoResposta()) * ia.getProgramaCriterioAvaliacao().getPesoResposta();
                    } else {
                        notaTemp = ia.getNotaCriterio() * ia.getProgramaCriterioAvaliacao().getPesoResposta();
                    }
                } else {
                    notaTemp = ia.getProgramaCriterioAvaliacao().getPesoResposta() * ia.getNotaCriterio();
                }
                notaCriterio += notaTemp;
            }
        }
    }

    private boolean mostrarLabelMotivoNaoAceito(Item<InscricaoProgramaCriterioAvaliacao> item) {
        boolean mostrarLbl = true;

        if (item.getModelObject().getAceitaCriterioAvaliacao() == null) {
            mostrarLbl = false;
        } else {
            mostrarLbl = !item.getModelObject().getAceitaCriterioAvaliacao();
        }
        return mostrarLbl;
    }

    private boolean mostraTextFieldMotivo(Item<InscricaoProgramaCriterioAvaliacao> item) {
        boolean mostrarText = true;

        if (item.getModelObject().getAceitaCriterioAvaliacao() == null) {
            mostrarText = false;
        } else {
            mostrarText = !item.getModelObject().getAceitaCriterioAvaliacao();
            if (!mostrarText) {
                item.getModelObject().setDescricaoMotivo("");
            }
        }
        return mostrarText;

    }

    private void respostaTipoListaSelecao(Item<InscricaoProgramaCriterioAvaliacao> item) {
        EnumTipoResposta tipo = item.getModelObject().getProgramaCriterioAvaliacao().getTipoResposta();
        if (tipo == EnumTipoResposta.LISTA_SELECAO) {
            if (item.getModelObject().getAceitaCriterioAvaliacao() == null || item.getModelObject().getAceitaCriterioAvaliacao()) {

                String descricaoResposta = item.getModelObject().getDescricaoResposta();
                List<ProgramaCriterioAvaliacaoOpcaoResposta> lista = item.getModelObject().getProgramaCriterioAvaliacao().getCriteriosAvaliacaoOpcaoResposta();
                for (ProgramaCriterioAvaliacaoOpcaoResposta par : lista) {
                    if (par.getDescricaoOpcaoResposta().equalsIgnoreCase(descricaoResposta)) {
                        item.getModelObject().setNotaCriterio((par.getNotaOpcaoResposta()));
                        dropDownChoiceNota.setEnabled(false);
                        break;
                    }
                }
            }
        }
    }

    private Integer devolverNotaOpcaoResposta(List<ProgramaCriterioAvaliacaoOpcaoResposta> lista, String descricaoResposta) {
        Integer nota = 0;
        for (ProgramaCriterioAvaliacaoOpcaoResposta par : lista) {
            if (par.getDescricaoOpcaoResposta().equalsIgnoreCase(descricaoResposta)) {
                nota = par.getNotaOpcaoResposta();
                break;
            }
        }
        return nota;
    }

    private void mostrarTextAreaMotivo(AjaxRequestTarget target, Item<InscricaoProgramaCriterioAvaliacao> item) {
        if (item.getModelObject().getAceitaCriterioAvaliacao() == null || !item.getModelObject().getAceitaCriterioAvaliacao()) {
            panelMotivoCriterioNaoAceito.setVisible(false);
        } else {
            panelMotivoCriterioNaoAceito.setVisible(true);
        }

    }

    public InscricaoPrograma getInscricao() {
        return inscricao;
    }

    public void setInscricao(InscricaoPrograma inscricao) {
        this.inscricao = inscricao;
    }

    public HistoricoAnaliseAvaliacao getHistoricoAvaliacao() {
        return historicoAvaliacao;
    }

    public void setHistoricoAvaliacao(HistoricoAnaliseAvaliacao historicoAvaliacao) {
        this.historicoAvaliacao = historicoAvaliacao;
    }

    public List<InscricaoProgramaCriterioAvaliacao> getListaCriterios() {
        return listaCriterios;
    }

    public void setListaCriterios(List<InscricaoProgramaCriterioAvaliacao> listaCriterios) {
        this.listaCriterios = listaCriterios;
    }

    public String getMsgConfirm() {
        return msgConfirm;
    }

    private void desabilitarFeedBackListener() {
        RequestCycle.get().setMetaData(FeedbackPanelListener.SKIP_FEEDBACK_LISTENER, Boolean.TRUE);
    }

    public void setMsgConfirm(String msgConfirm) {
        this.msgConfirm = msgConfirm;
    }

    public Boolean getAlteradoResultadoAnalise() {
        return alteradoResultadoAnalise;
    }

    public void setAlteradoResultadoAnalise(Boolean alteradoResultadoAnalise) {
        this.alteradoResultadoAnalise = alteradoResultadoAnalise;
    }

    public Integer getNotaCriterio() {
        return notaCriterio;
    }

    public void setNotaCriterio(Integer notaCriterio) {
        this.notaCriterio = notaCriterio;
    }
}
