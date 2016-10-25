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
import org.apache.wicket.validation.validator.StringValidator;

import br.gov.mj.apoio.entidades.MotivoAnalise;
import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraDropDownChoice;
import br.gov.mj.infra.wicket.model.LambdaModel;
import br.gov.mj.side.entidades.enums.EnumResultadoFinalAnaliseElegibilidade;
import br.gov.mj.side.entidades.enums.EnumStatusInscricao;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoAnexoElegibilidade;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoPrograma;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoProgramaCriterioElegibilidade;
import br.gov.mj.side.entidades.programa.inscricao.analise.HistoricoAnaliseElegibilidade;
import br.gov.mj.side.entidades.programa.inscricao.analise.ListaElegibilidadePublicado;
import br.gov.mj.side.web.dto.AnexoDto;
import br.gov.mj.side.web.service.InscricaoAnexoElegibilidadeService;
import br.gov.mj.side.web.service.InscricaoProgramaElegibilidadeService;
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

public class DadosPropostaElebigilidadePanel extends Panel {
    private static final long serialVersionUID = 1L;

    private InscricaoPrograma inscricao;

    private PanelDataViewCriterio panelDataViewCriterio;
    private PanelMotivoCriterioNaoAceito panelMotivoCriterioNaoAceito;
    private PanelRecurso panelRecurso;
    private PanelAnaliseFinal panelAnaliseFinal;
    private PanelHistorico panelHistorico;

    private DataView<InscricaoProgramaCriterioElegibilidade> dataView;
    private Button btnDownload;
    private DropDownChoice<Boolean> dropDownChoiceAceito;
    private Boolean faseRecursoElegibilidade = false;
    private Boolean finalizadoRecursoElegibilidade = false;
    private TextArea<String> textAreaMotivo;

    private HistoricoAnaliseElegibilidade historicoElegibilidade = new HistoricoAnaliseElegibilidade();
    private Boolean aceito;
    private Boolean readOnly;
    private String resultadoAnalise = "";
    private String msgConfirm;
    private Modal<String> modalConfirm;
    private Boolean alteradoResultadoAnalise = false;
    private EnumResultadoFinalAnaliseElegibilidade resultadoAnterior;

    private List<InscricaoProgramaCriterioElegibilidade> listaCriterios = new ArrayList<InscricaoProgramaCriterioElegibilidade>();
    private List<ListaElegibilidadePublicado> listaDeElegibilidadePublicada = new ArrayList<ListaElegibilidadePublicado>();
    private List<HistoricoAnaliseElegibilidade> listaDeHistorico = new ArrayList<HistoricoAnaliseElegibilidade>();

    @Inject
    private ListaPublicadoService listaPublicadoService;

    @Inject
    private InscricaoProgramaService inscricaoService;

    @Inject
    private MotivoAnaliseService motivoService;

    @Inject
    private InscricaoProgramaElegibilidadeService elegibilidadeService;

    @Inject
    private InscricaoAnexoElegibilidadeService inscricaoAnexoElegibilidadeService;

    @Inject
    ComponentFactory componentFactory;

    public DadosPropostaElebigilidadePanel(String id, InscricaoPrograma inscricao, Boolean readOnly) {
        super(id);

        this.inscricao = inscricao;
        this.readOnly = readOnly;

        initVariaveis();
        initComponents();
        isReadOnly();
    }

    private void initVariaveis() {
        listaCriterios = inscricaoService.buscarInscricaoProgramaCriterioElegibilidade(inscricao);
        historicoElegibilidade = elegibilidadeService.buscarUltimoHistoricoAnaliseElegibilidade(inscricao);

        resultadoAnterior = historicoElegibilidade.getResultadoFinalAnalise();
        historicoElegibilidade.setDescricaoJustificativa(null);

        acaoCriterioAceito();// Analisa os criterios aceitos para mostrar a
                             // frase de Elegivel ou não Elegivel que irá
                             // aparecer na Label 'Resultado da Análise'
        faseRecursoElegibilidade = inscricao.getEstaEmFaseRecursoElegibilidade();
        finalizadoRecursoElegibilidade = inscricao.getFinalizadoRecursoElegibilidade();

        listaDeElegibilidadePublicada = SideUtil.convertAnexoDtoToEntityListaElegibilidadePublicado(listaPublicadoService.buscarListaElegibilidadePublicadoPeloIdPrograma(inscricao.getPrograma().getId()));

    }

    private void initComponents() {
        add(panelDataViewCriterio = new PanelDataViewCriterio("panelDataViewCriterio"));
        add(panelAnaliseFinal = new PanelAnaliseFinal("panelAnaliseFinal"));
        add(panelRecurso = new PanelRecurso("panelRecurso"));
        add(panelHistorico = new PanelHistorico("panelHistorico"));

        panelRecurso.setVisible(listaDeElegibilidadePublicada != null && listaDeElegibilidadePublicada.size() > 0);
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

        public PanelMotivoCriterioNaoAceito(String id, Item<InscricaoProgramaCriterioElegibilidade> item) {
            super(id);
            setOutputMarkupId(true);

            add(newLabelMotivoNaoAceito(item)); // lblMotivoNaoAceito
            add(newTextAreaMotivo(item)); // txtMotivo
        }
    }

    // Panel que irá mostrar os detalhes dos critérios
    private class PanelCriterio extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelCriterio(String id, Item<InscricaoProgramaCriterioElegibilidade> item) {
            super(id);
            setOutputMarkupId(true);

            add(newLabelNumeroCriterio(item)); // lblNumero
            add(newLabelNomeCriterio(item)); // lblNome
            add(newLabelDescricaoCriterio(item)); // lblDescricao
            add(newLabelFormaVerificacaoCriterio(item)); // lblFormaVerificacao
            add(newLabelAtendeCriterio(item)); // lblAtendeCriterio
            add(getContainerDeDownload(item)); //containerDownloadElegiblidade
            add(newDropDownCriterioAceito(item)); // dropCriterioAceito
            add(panelMotivoCriterioNaoAceito = new PanelMotivoCriterioNaoAceito("panelMotivoCriterioNaoAceito", item));
        }
    }
    
    private WebMarkupContainer getContainerDeDownload(Item<InscricaoProgramaCriterioElegibilidade> item){
        WebMarkupContainer container = new WebMarkupContainer("containerDownloadElegiblidade");
        
        List<AnexoDto> listaAnexos = inscricaoAnexoElegibilidadeService.buscarInscricaoAnexoElegibilidade(item.getModelObject().getId());
        container.add(newListViewCriterios(SideUtil.convertAnexoDtoToEntityInscricaoAnexoElegibilidade(inscricaoAnexoElegibilidadeService.buscarInscricaoAnexoElegibilidade(item.getModelObject().getId()))));
        
        if(listaAnexos != null && listaAnexos.isEmpty()){
            container.setVisible(false);
        }
        
        return container;
        
    }

    private class PanelRecurso extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelRecurso(String id) {
            super(id);
            setOutputMarkupId(true);

            add(newCheckfaseRecursoElegibilidade()); // checkfaseRecursoElegibilidade
            add(newCheckfinalizadoRecursoElegibilidade()); // checkfinalizadoRecursoElegibilidade
            add(newTextFieldProcesso()); // txtNumeroProcesso
            add(newLabelProcesso()); // lblProcesso
            add(newLabelRecursoAnalisado()); // lblRecursoAnalisado
        }
    }

    private class PanelAnaliseFinal extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelAnaliseFinal(String id) {
            super(id);
            setOutputMarkupId(true);

            add(newLabelResultadoAnalise()); // lblResultadoAnalise
            add(newDropDownResultadoFinal()); // dropResultadoFinal
            add(newTextAreaJustificativa());// txtJustificativa
            add(newDropDownMotivoAnalise()); // dropMotivoAnalse

            add(newLabelResultadoFinal()); // lblResultadoFinal
            add(newLabelMotivo()); // lblMotivoFinal
            add(newLabelJustificativa()); // lblJustificativaFinal
        }
    }

    private class PanelHistorico extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelHistorico(String id) {
            super(id);
            setOutputMarkupId(true);

            listaDeHistorico = elegibilidadeService.buscarHistoricoAnaliseElegibilidade(inscricao);
            add(newDataViewCriterios(listaDeHistorico));
        }
    }

    // Componentes

    private DataView<InscricaoProgramaCriterioElegibilidade> newDataViewCriterios() {
        dataView = new DataView<InscricaoProgramaCriterioElegibilidade>("dataViewCriterios", new CriteriosProvider()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<InscricaoProgramaCriterioElegibilidade> item) {
                item.add(new PanelCriterio("panelCriterio", item));
            }
        };
        return dataView;
    }

    private PropertyListView<InscricaoAnexoElegibilidade> newListViewCriterios(List<InscricaoAnexoElegibilidade> lista) {
        PropertyListView<InscricaoAnexoElegibilidade> listView = new PropertyListView<InscricaoAnexoElegibilidade>("listaAnexos", lista) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<InscricaoAnexoElegibilidade> item) {
                item.add(newButtonDownload(item)); // btnDownload
                // item.add(newLinkDownload(item));//btnDownload
                item.add(new Label("lblNomeAnexo", item.getModelObject().getNomeAnexo()));
            }
        };

        listView.setOutputMarkupId(true);
        return listView;
    }

    private Label newLabelNumeroCriterio(Item<InscricaoProgramaCriterioElegibilidade> item) {
        Label labelNumero = new Label("lblNumero", item.getIndex() + 1);
        labelNumero.setOutputMarkupId(true);
        return labelNumero;
    }

    private Label newLabelNomeCriterio(Item<InscricaoProgramaCriterioElegibilidade> item) {
        Label labelNome = new Label("lblNome", item.getModelObject().getProgramaCriterioElegibilidade().getNomeCriterioElegibilidade());
        labelNome.setOutputMarkupId(true);
        return labelNome;
    }

    private Label newLabelDescricaoCriterio(Item<InscricaoProgramaCriterioElegibilidade> item) {
        Label labelDescricao = new Label("lblDescricao", item.getModelObject().getProgramaCriterioElegibilidade().getDescricaoCriterioElegibilidade());
        labelDescricao.setOutputMarkupId(true);
        return labelDescricao;
    }

    private Label newLabelFormaVerificacaoCriterio(Item<InscricaoProgramaCriterioElegibilidade> item) {
        Label labelForma = new Label("lblFormaVerificacao", item.getModelObject().getProgramaCriterioElegibilidade().getFormaVerificacao());
        labelForma.setOutputMarkupId(true);
        return labelForma;
    }

    private Label newLabelAtendeCriterio(Item<InscricaoProgramaCriterioElegibilidade> item) {
        // item.getModelObject().get
        String atende;
        if (item.getModelObject().getAtendeCriterioElegibilidade() != null) {
            atende = item.getModelObject().getAtendeCriterioElegibilidade() ? "Sim" : "Não";
        } else {
            atende = "-";
        }

        Label labelForma = new Label("lblAtendeCriterio", atende);
        labelForma.setOutputMarkupId(true);
        return labelForma;
    }

    private Button newButtonDownload(ListItem<InscricaoAnexoElegibilidade> item) {
        btnDownload = componentFactory.newButton("btnDownload", () -> download(item));
        btnDownload.setOutputMarkupId(true);
        return btnDownload;
    }

    private Link newLinkDownload(ListItem<InscricaoAnexoElegibilidade> item) {
        Link link1 = new Link("btnDownload") {
            public void onClick() {
                download(item);
            }
        };
        link1.setOutputMarkupId(true);
        return link1;
    }

    private void download(ListItem<InscricaoAnexoElegibilidade> item) {
        InscricaoAnexoElegibilidade a = item.getModelObject();
        if (a.getId() != null) {
            AnexoDto retorno = inscricaoAnexoElegibilidadeService.buscarPeloId(a.getId());
            SideUtil.download(retorno.getConteudo(), retorno.getNomeAnexo());
        } else {
            SideUtil.download(a.getConteudo(), a.getNomeAnexo());
        }
    }

    private DropDownChoice<Boolean> newDropDownCriterioAceito(Item<InscricaoProgramaCriterioElegibilidade> item) {
        dropDownChoiceAceito = new DropDownChoice<Boolean>("aceitaCriterioElegibilidade", null, Arrays.asList(Boolean.TRUE, Boolean.FALSE));
        dropDownChoiceAceito.setLabel(Model.of("Critério Aceito"));
        dropDownChoiceAceito.setNullValid(true);
        dropDownChoiceAceito.setEnabled(mostrarCriteriosEditaveis());
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
                acaoCriterioAceito();
                panelAnaliseFinal.addOrReplace(newLabelResultadoAnalise());
                panelMotivoCriterioNaoAceito.addOrReplace(newTextAreaJustificativa());

                target.add(panelAnaliseFinal);
                target.add(panelDataViewCriterio);
                target.add(panelMotivoCriterioNaoAceito);
            }
        });
        return dropDownChoiceAceito;
    }

    private Label newLabelMotivoNaoAceito(Item<InscricaoProgramaCriterioElegibilidade> item) {
        Label lbl = new Label("lblMotivoNaoAceito", "* Motivo");
        lbl.setVisible(mostrarLabelMotivoNaoAceito(item));
        return lbl;
    }

    private TextArea<String> newTextAreaMotivo(Item<InscricaoProgramaCriterioElegibilidade> item) {

        textAreaMotivo = new TextArea<String>("descricaoMotivo", null);
        textAreaMotivo.setLabel(Model.of("Motivo"));
        textAreaMotivo.setOutputMarkupId(true);
        textAreaMotivo.setEnabled(mostrarCriteriosEditaveis());
        textAreaMotivo.setVisible(mostraTextFieldMotivo(item));
        textAreaMotivo.add(StringValidator.maximumLength(Constants.TAMANHO_CARACTERES_CAMPO_TEXT_AREA));
        actionTextArea(textAreaMotivo);
        return textAreaMotivo;
    }

    private Label newLabelResultadoAnalise() {
        Label labelFinal = new Label("lblResultadoAnalise", resultadoAnalise);
        labelFinal.setOutputMarkupId(true);
        return labelFinal;
    }

    private DropDownChoice<EnumResultadoFinalAnaliseElegibilidade> newDropDownResultadoFinal() {

        InfraDropDownChoice<EnumResultadoFinalAnaliseElegibilidade> dropResultado = componentFactory.newDropDownChoice("dropResultadoFinal", "Resultado Finald", false, "valor", "descricao", new PropertyModel<EnumResultadoFinalAnaliseElegibilidade>(historicoElegibilidade, "resultadoFinalAnalise"),
                Arrays.asList(EnumResultadoFinalAnaliseElegibilidade.values()), null);
        dropResultado.setEnabled(mostrarHistoricoEditaveis());
        actionDropResultadoFinal(dropResultado);
        dropResultado.setVisible(mostrarPainelAnalise());
        return dropResultado;
    }

    private DropDownChoice<MotivoAnalise> newDropDownMotivoAnalise() {

        DropDownChoice<MotivoAnalise> dropResultado = new DropDownChoice<MotivoAnalise>("dropMotivoAnalse", new PropertyModel<MotivoAnalise>(historicoElegibilidade, "motivoAnalise"), motivoService.buscarTodos(), new ChoiceRenderer<MotivoAnalise>("nomeMotivo"));
        dropResultado.setNullValid(true);
        dropResultado.setEnabled(mostrarHistoricoEditaveis());
        dropResultado.setVisible(mostrarPainelAnalise());
        return dropResultado;
    }

    private Label newLabelResultadoFinal() {
        Label lbl = new Label("lblResultadoFinal", "Resultado Final");
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

    public TextArea<String> newTextAreaJustificativa() {

        TextArea<String> text = new TextArea<String>("txtJustificativa", new PropertyModel<String>(historicoElegibilidade, "descricaoJustificativa"));
        text.setOutputMarkupId(true);
        text.setEnabled(mostrarHistoricoEditaveis());
        text.add(StringValidator.maximumLength(Constants.TAMANHO_CARACTERES_CAMPO_TEXT_AREA));
        text.setVisible(mostrarPainelAnalise());
        actionTextArea(text);
        return text;
    }

    private DataView<HistoricoAnaliseElegibilidade> newDataViewCriterios(List<HistoricoAnaliseElegibilidade> lista) {
        DataView<HistoricoAnaliseElegibilidade> dataView = new DataView<HistoricoAnaliseElegibilidade>("dataViewHistorico", new ListDataProvider<HistoricoAnaliseElegibilidade>(lista)) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<HistoricoAnaliseElegibilidade> item) {
                item.add(new Label("histDtCadastro", dataCadastroBR(item.getModelObject().getDataCadastro()) == null ? " - " : dataCadastroBR(item.getModelObject().getDataCadastro())));
                item.add(new Label("histMotivoAnalise", item.getModelObject().getMotivoAnalise() == null ? " - " : item.getModelObject().getMotivoAnalise().getNomeECodigo()));
                item.add(new Label("histJustificativa", item.getModelObject().getDescricaoJustificativa() == null ? " - " : item.getModelObject().getDescricaoJustificativa()));
                item.add(new Label("histResultadoFinal", item.getModelObject().getResultadoFinalAnalise().getDescricao() == null ? " - " : item.getModelObject().getResultadoFinalAnalise().getDescricao()));
                item.add(new Label("histUsuarioCadastro", item.getModelObject().getUsuarioCadastro() == null ? " - " : item.getModelObject().getUsuarioCadastro()));
            }
        };
        return dataView;
    }

    public AjaxCheckBox newCheckfaseRecursoElegibilidade() {
        AjaxCheckBox checkApresentarRecurso = new AjaxCheckBox("checkfaseRecursoElegibilidade", new PropertyModel<Boolean>(this, "faseRecursoElegibilidade")) {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                actionCheckfaseRecursoElegibilidade(target);
            }
        };
        checkApresentarRecurso.setOutputMarkupId(true);
        checkApresentarRecurso.setEnabled(ativarCheckfaseRecursoElegibilidade());
        return checkApresentarRecurso;
    }

    public AjaxCheckBox newCheckfinalizadoRecursoElegibilidade() {
        AjaxCheckBox checkfinalizadoRecursoElegibilidade = new AjaxCheckBox("checkfinalizadoRecursoElegibilidade", new PropertyModel<Boolean>(this, "finalizadoRecursoElegibilidade")) {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                actionCheckfinalizadoRecursoElegibilidade(target);
            }
        };
        checkfinalizadoRecursoElegibilidade.setOutputMarkupId(true);
        // checkfinalizadoRecursoElegibilidade.setVisible(mostrarCheckfinalizadoRecursoElegibilidade());
        checkfinalizadoRecursoElegibilidade.setVisible(faseRecursoElegibilidade);
        checkfinalizadoRecursoElegibilidade.setEnabled(ativarCheckFinalizadoRecursoElegibilidade());
        return checkfinalizadoRecursoElegibilidade;
    }

    private TextField<String> newTextFieldProcesso() {
        TextField<String> field = componentFactory.newTextField("txtNumeroProcesso", "Número do Processo", false, new PropertyModel(inscricao, "numeroProcessoSEIRecursoElegibilidade"));
        field.add(StringValidator.maximumLength(20));
        actionTextFieldNome(field);
        field.setEnabled(!isGeradaSegundaListaDeElegibilidade());
        field.setVisible(faseRecursoElegibilidade);
        return field;
    }

    private Label newLabelProcesso() {
        Label labelProcesso = new Label("lblProcesso", "Número do Processo");
        labelProcesso.setVisible(faseRecursoElegibilidade);
        return labelProcesso;
    }

    private Label newLabelRecursoAnalisado() {
        Label labelProcesso = new Label("lblRecursoAnalisado", "Recurso Analisado");
        labelProcesso.setVisible(faseRecursoElegibilidade);
        return labelProcesso;
    }

    private Modal<String> newModal(String id) {
        Modal<String> modal = new TextContentModal(id, new LambdaModel<String>(this::getMsgConfirm, this::setMsgConfirm));
        modal.setBackdrop(Backdrop.STATIC);
        modal.addButton(newButtonFecharModal(modal));
        return modal;
    }

    // PROVIDER

    private class CriteriosProvider extends SortableDataProvider<InscricaoProgramaCriterioElegibilidade, String> {
        private static final long serialVersionUID = 1L;

        public CriteriosProvider() {
            // contructor
        }

        @Override
        public Iterator<InscricaoProgramaCriterioElegibilidade> iterator(long first, long size) {
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
        public IModel<InscricaoProgramaCriterioElegibilidade> model(InscricaoProgramaCriterioElegibilidade object) {
            return new CompoundPropertyModel<InscricaoProgramaCriterioElegibilidade>(object);
        }
    }

    // Action

    private boolean mostrarPainelAnalise() {
        if (listaDeElegibilidadePublicada != null && listaDeElegibilidadePublicada.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    private boolean mostrarDataViewHistorico() {
        if ((listaDeElegibilidadePublicada != null && listaDeElegibilidadePublicada.size() > 0) && (listaDeHistorico != null && listaDeHistorico.size() > 0)) {
            return true;
        } else {
            return false;
        }
    }

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

    public void actionCheckfaseRecursoElegibilidade(AjaxRequestTarget target) {
        if (faseRecursoElegibilidade) {
            inscricao.setEstaEmFaseRecursoElegibilidade(true);
        } else {
            inscricao.setEstaEmFaseRecursoAvaliacao(false);
        }

        panelRecurso.addOrReplace(newCheckfinalizadoRecursoElegibilidade());
        panelRecurso.addOrReplace(newLabelRecursoAnalisado());
        panelRecurso.addOrReplace(newTextFieldProcesso());
        panelRecurso.addOrReplace(newLabelProcesso());
        target.add(panelRecurso);
    }

    public void actionCheckfinalizadoRecursoElegibilidade(AjaxRequestTarget target) {

        if (finalizadoRecursoElegibilidade) {
            inscricao.setFinalizadoRecursoElegibilidade(true);
        } else {
            inscricao.setFinalizadoRecursoElegibilidade(false);
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

    public String dataCadastroBR(LocalDateTime dataCadastro) {
        DateTimeFormatter sdfPadraoBR = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        if (dataCadastro != null) {
            return sdfPadraoBR.format(dataCadastro);
        }
        return " - ";
    }

    private boolean mostrarCriteriosEditaveis() {
        boolean mostrar = true;
        if (readOnly) {
            mostrar = false;
        } else {
            if (inscricao.getStatusInscricao() != null) {
                EnumStatusInscricao status = inscricao.getStatusInscricao();
                if (listaDeElegibilidadePublicada == null || listaDeElegibilidadePublicada.size() > 0) {
                    mostrar = false;
                } else {
                    if (status == EnumStatusInscricao.ANALISE_ELEGIBILIDADE || status == EnumStatusInscricao.ENVIADA_ANALISE) {
                        mostrar = true;
                    } else {
                        mostrar = false;
                    }
                }
            } else {
                mostrar = true;
            }
        }
        return mostrar;
    }

    // Ira avaliar se o campo abaixo dos critérios (Análise de Elegibilidade)
    // estará ou não em estado Editavel
    private boolean mostrarHistoricoEditaveis() {
        boolean mostrar = true;
        if (readOnly) {
            mostrar = false;
        } else {
            if (!isGeradaSegundaListaDeElegibilidade() || inscricao.getStatusInscricao() == EnumStatusInscricao.ANALISE_ELEGIBILIDADE || inscricao.getStatusInscricao() == EnumStatusInscricao.ENVIADA_ANALISE) {
                mostrar = true;
            } else {
                mostrar = false;
            }
        }
        return mostrar;
    }

    // Se tiver sido gerada a segunda lista de elegiveis não permitir mais a
    // edição de nada
    private boolean isGeradaSegundaListaDeElegibilidade() {

        if (listaDeElegibilidadePublicada == null || listaDeElegibilidadePublicada.size() == 0) {
            return false;
        } else {
            if (listaDeElegibilidadePublicada.size() > 1) {
                return true;
            } else {
                return false;
            }
        }
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

    private void actionDropResultadoFinal(DropDownChoice<EnumResultadoFinalAnaliseElegibilidade> drop) {
        drop.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1L;

            protected void onUpdate(AjaxRequestTarget target) {
                if (historicoElegibilidade.getId() == null) {
                    if (!drop.getModelObject().getDescricao().equalsIgnoreCase(resultadoAnalise)) {
                        alteradoResultadoAnalise = true;
                        modalConfirm.show(true);
                        target.add(modalConfirm);
                    }
                } else {
                    if (drop.getModelObject() != resultadoAnterior) {
                        alteradoResultadoAnalise = true;
                        modalConfirm.show(true);
                        target.add(modalConfirm);
                    } else {
                        alteradoResultadoAnalise = false;
                    }
                }
            }
        });
    }

    public InscricaoPrograma getInscricao() {
        return inscricao;
    }

    private void isReadOnly() {
        if (readOnly) {
            panelAnaliseFinal.setEnabled(false);
        }
    }

    private void acaoCriterioAceito() {
        int tamanhoLista = listaCriterios.size();
        for (InscricaoProgramaCriterioElegibilidade ip : this.listaCriterios) {
            if (ip.getAceitaCriterioElegibilidade() != null && ip.getAceitaCriterioElegibilidade()) {
                tamanhoLista--;
            }
        }

        if (tamanhoLista == 0) {
            resultadoAnalise = "Elegível";
            if (panelMotivoCriterioNaoAceito != null) {
                panelMotivoCriterioNaoAceito.setVisible(true);
            }

        } else {
            resultadoAnalise = "Não Elegível";
            if (panelMotivoCriterioNaoAceito != null) {
                panelMotivoCriterioNaoAceito.setVisible(false);
            }
        }
    }

    private boolean mostrarLabelMotivoNaoAceito(Item<InscricaoProgramaCriterioElegibilidade> item) {
        boolean mostrarLbl = true;

        if (item.getModelObject().getAceitaCriterioElegibilidade() == null) {
            mostrarLbl = false;
        } else {
            mostrarLbl = !item.getModelObject().getAceitaCriterioElegibilidade();
        }
        return mostrarLbl;
    }

    private boolean mostraTextFieldMotivo(Item<InscricaoProgramaCriterioElegibilidade> item) {
        boolean mostrarText = true;

        if (item.getModelObject().getAceitaCriterioElegibilidade() == null) {
            mostrarText = false;
        } else {
            mostrarText = !item.getModelObject().getAceitaCriterioElegibilidade();
            if (!mostrarText) {
                item.getModelObject().setDescricaoMotivo("");
            }
        }
        return mostrarText;
    }

    private boolean mostrarCheckfinalizadoRecursoElegibilidade() {
        if (!inscricao.getEstaEmFaseRecursoElegibilidade()) {
            return false;
        } else {
            return true;
        }
    }

    private boolean ativarCheckFinalizadoRecursoElegibilidade() {
        if (inscricao.getFinalizadoRecursoElegibilidade()) {
            return false;
        } else {
            return true;
        }
    }

    private boolean ativarCheckfaseRecursoElegibilidade() {

        if (isGeradaSegundaListaDeElegibilidade()) {
            return false;
        } else {
            if (inscricao.getEstaEmFaseRecursoElegibilidade()) {
                return false;
            } else {
                return true;
            }
        }
    }

    public void setInscricao(InscricaoPrograma inscricao) {
        this.inscricao = inscricao;
    }

    public List<InscricaoProgramaCriterioElegibilidade> getListaCriterios() {
        return listaCriterios;
    }

    public void setListaCriterios(List<InscricaoProgramaCriterioElegibilidade> listaCriterios) {
        this.listaCriterios = listaCriterios;
    }

    public HistoricoAnaliseElegibilidade getHistoricoElegibilidade() {
        return historicoElegibilidade;
    }

    public void setHistoricoElegibilidade(HistoricoAnaliseElegibilidade historicoElegibilidade) {
        this.historicoElegibilidade = historicoElegibilidade;
    }

    public String getMsgConfirm() {
        return msgConfirm;
    }

    public void setMsgConfirm(String msgConfirm) {
        this.msgConfirm = msgConfirm;
    }

    public String getResultadoAnalise() {
        return resultadoAnalise;
    }

    public void setResultadoAnalise(String resultadoAnalise) {
        this.resultadoAnalise = resultadoAnalise;
    }

    public Boolean getAlteradoResultadoAnalise() {
        return alteradoResultadoAnalise;
    }

    public void setAlteradoResultadoAnalise(Boolean alteradoResultadoAnalise) {
        this.alteradoResultadoAnalise = alteradoResultadoAnalise;
    }
}
