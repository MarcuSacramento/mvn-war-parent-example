package br.gov.mj.side.web.view.programa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.resource.ResourceRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.validation.validator.StringValidator;
import org.wicketstuff.jasperreports.JRConcreteResource;
import org.wicketstuff.jasperreports.handlers.CsvResourceHandler;
import org.wicketstuff.jasperreports.handlers.PdfResourceHandler;

import br.gov.mj.apoio.entidades.Elemento;
import br.gov.mj.apoio.entidades.Municipio;
import br.gov.mj.apoio.entidades.Orgao;
import br.gov.mj.apoio.entidades.Uf;
import br.gov.mj.apoio.entidades.UnidadeExecutora;
import br.gov.mj.infra.negocio.persistencia.IGenericPersister;
import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraAjaxConfirmButton;
import br.gov.mj.infra.wicket.component.InfraAjaxFallbackLink;
import br.gov.mj.infra.wicket.component.InfraDropDownChoice;
import br.gov.mj.infra.wicket.component.pagination.InfraAjaxPagingNavigator;
import br.gov.mj.infra.wicket.model.LambdaModel;
import br.gov.mj.side.entidades.AcaoOrcamentaria;
import br.gov.mj.side.entidades.EmendaParlamentar;
import br.gov.mj.side.entidades.enums.EnumAbaFaseAnalise;
import br.gov.mj.side.entidades.enums.EnumStatusPrograma;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.web.dto.AnaliseDto;
import br.gov.mj.side.web.dto.PermissaoProgramaDto;
import br.gov.mj.side.web.dto.ProgramaPesquisaDto;
import br.gov.mj.side.web.service.AcaoOrcamentariaService;
import br.gov.mj.side.web.service.MunicipioService;
import br.gov.mj.side.web.service.OrgaoService;
import br.gov.mj.side.web.service.ProgramaService;
import br.gov.mj.side.web.service.PublicizacaoService;
import br.gov.mj.side.web.service.UnidadeExecutoraService;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.RelatorioConsultaPublicaBuilder;
import br.gov.mj.side.web.util.RelatorioProgramaBuilder;
import br.gov.mj.side.web.util.SideUtil;
import br.gov.mj.side.web.util.SortableProgramaDataProvider;
import br.gov.mj.side.web.view.HomePage;
import br.gov.mj.side.web.view.components.AjaxDialogButton;
import br.gov.mj.side.web.view.components.converters.NumeroProcessoSeiConverter;
import br.gov.mj.side.web.view.components.validators.YearIntegerValidator;
import br.gov.mj.side.web.view.contrato.ContratoPesquisaPage;
import br.gov.mj.side.web.view.execucao.AcompanharOrdemFornecimentoPage;
import br.gov.mj.side.web.view.planejarLicitacao.PlanejamentoLicitacaoPage;
import br.gov.mj.side.web.view.programa.publicizacao.ProgramaPublicizacaoPage;
import br.gov.mj.side.web.view.propostas.PropostasEnviadasPage;
import br.gov.mj.side.web.view.template.TemplatePage;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal.Backdrop;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.TextContentModal;

@AuthorizeInstantiation({ ProgramaPesquisaPage.ROLE_MANTER_PROGRAMA_INCLUIR, ProgramaPesquisaPage.ROLE_MANTER_PROGRAMA_ALTERAR, ProgramaPesquisaPage.ROLE_MANTER_PROGRAMA_EXCLUIR, ProgramaPesquisaPage.ROLE_MANTER_PROGRAMA_VISUALIZAR, ProgramaPesquisaPage.ROLE_MANTER_PROGRAMA_PUBLICAR,
        ProgramaPesquisaPage.ROLE_MANTER_PROGRAMA_SUSPENDER, ProgramaPesquisaPage.ROLE_MANTER_PROGRAMA_CANCELAR, ProgramaPesquisaPage.ROLE_MANTER_PROGRAMA_SUSPENDER_PRAZO, ProgramaPesquisaPage.ROLE_MANTER_PROGRAMA_PRORROGAR_PRAZO, ProgramaPesquisaPage.ROLE_MANTER_PROGRAMA_REABRIR_PRAZO })
public class ProgramaPesquisaPage extends TemplatePage {
    private static final long serialVersionUID = 1L;

    public static final String ROLE_MANTER_PROGRAMA_INCLUIR = "manter_programa:incluir";
    public static final String ROLE_MANTER_PROGRAMA_ALTERAR = "manter_programa:alterar";
    public static final String ROLE_MANTER_PROGRAMA_EXCLUIR = "manter_programa:excluir";
    public static final String ROLE_MANTER_PROGRAMA_VISUALIZAR = "manter_programa:visualizar";

    public static final String ROLE_MANTER_PROGRAMA_PUBLICAR = "manter_programa:publicar";
    public static final String ROLE_MANTER_PROGRAMA_SUSPENDER = "manter_programa:suspender";
    public static final String ROLE_MANTER_PROGRAMA_CANCELAR = "manter_programa:cancelar";
    public static final String ROLE_MANTER_PROGRAMA_SUSPENDER_PRAZO = "manter_programa:suspender_prazo";
    public static final String ROLE_MANTER_PROGRAMA_PRORROGAR_PRAZO = "manter_programa:prorrogar_prazo";
    public static final String ROLE_MANTER_PROGRAMA_REABRIR_PRAZO = "manter_programa:reabrir_prazo";

    private Form<ProgramaPesquisaDto> form;

    private WebMarkupContainer municipioContainer;
    private ListMultipleChoice<Municipio> listMultipleChoiceMunicipio;
    private WebMarkupContainer ufContainer;
    private ListMultipleChoice<Uf> listMultipleChoiceUf;
    private WebMarkupContainer unidadeExecutoraContainer;
    private InfraDropDownChoice<UnidadeExecutora> dropDownChoiceUnidadeExecutora;
    private DataView<Programa> dataView;
    private PanelGridResultados panelGridResultados;
    private Modal<String> modalConfirmUf;
    private String msgConfirmUf = new String();
    private Orgao orgao = new Orgao();
    private boolean pesquisado = false;
    private SortableProgramaDataProvider dp;

    private List<Programa> programas;
    private List<Uf> ufsSelecionadasComparacao = new ArrayList<Uf>();

    private Integer itensPorPagina = Constants.ITEMS_PER_PAGE_PAGINATION;

    @Inject
    private MunicipioService municipioService;

    @Inject
    private IGenericPersister genericPersister;

    @Inject
    private OrgaoService orgaoService;

    @Inject
    private ProgramaService programaService;

    @Inject
    private AcaoOrcamentariaService acaoOrcamentariaService;

    @Inject
    private UnidadeExecutoraService unidadeExecutoraService;

    @Inject
    private ComponentFactory componentFactory;

    @Inject
    private PublicizacaoService publicizacaoService;

    private Programa programa;

    public ProgramaPesquisaPage(final PageParameters pageParameters) {
        super(pageParameters);

        programas = programaService.buscar(null);

        // criarBreadcrumb();
        initComponents();
    }

    private void initComponents() {

        form = componentFactory.newForm("form", new ProgramaPesquisaDto());

        form.add(newTextFieldCodigo());
        form.add(newTextFieldNome());
        form.add(newTextFieldNomeFantasia());
        form.add(newTextFieldAno());
        form.add(newDropDownOrgao());

        dropDownChoiceUnidadeExecutora = newDropDownUnidadeExecutora();
        unidadeExecutoraContainer = new WebMarkupContainer("unidadeExecutoraContainer");
        unidadeExecutoraContainer.add(dropDownChoiceUnidadeExecutora);
        form.add(unidadeExecutoraContainer);

        form.add(newDropDownLimitacaoGeografica());

        listMultipleChoiceUf = newListMultipleChoiceUf();
        ufContainer = new WebMarkupContainer("ufContainer");
        ufContainer.add(listMultipleChoiceUf);
        ufContainer.setVisible(false);
        form.add(ufContainer);

        listMultipleChoiceMunicipio = newListMultipleChoiceMunicipio();
        municipioContainer = new WebMarkupContainer("municipioContainer");
        municipioContainer.add(listMultipleChoiceMunicipio);
        municipioContainer.setVisible(false);
        form.add(municipioContainer);

        form.add(newDropDownEmendaParlamentar());
        form.add(newDropDownAcaoOrcamentaria());
        form.add(newDropDownElemento());
        form.add(newTextFieldNumeroProcessoSEI());
        form.add(newDropDownStatus());

        InfraAjaxFallbackLink<Void> newLinkNovo = newLinkNovo();
        authorize(newLinkNovo, RENDER, ROLE_MANTER_PROGRAMA_INCLUIR);
        form.add(newLinkNovo);

        Button newButtonPesquisar = newButtonPesquisar();
        authorize(newButtonPesquisar, RENDER, ROLE_MANTER_PROGRAMA_VISUALIZAR);
        form.add(newButtonPesquisar);

        panelGridResultados = new PanelGridResultados("panelGridResultados");
        panelGridResultados.setVisible(isPesquisado());
        form.add(panelGridResultados);

        modalConfirmUf = newModal("modalConfirmUf");
        modalConfirmUf.show(false);
        form.add(modalConfirmUf);
        add(form);
        setTitulo("Pesquisar Programas");
    }

    private ListMultipleChoice<Uf> newListMultipleChoiceUf() {
        ListMultipleChoice<Uf> listMultipleChoice = new ListMultipleChoice<Uf>("listaUf", genericPersister.findAll(Uf.class), new ChoiceRenderer<Uf>("nomeSigla", "siglaUf"));

        listMultipleChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                if (!getMunicipiosParaExclusao().isEmpty()) {
                    setMsgConfirmUf(getString("MN030"));
                    modalConfirmUf.show(true);
                    target.add(modalConfirmUf);
                } else {
                    atualizarListaMunicipios();
                    atualizarListaUfComparacao();
                    target.add(municipioContainer);
                }

            }
        });
        return listMultipleChoice;
    }

    private void atualizarListaUfComparacao() {
        List<Uf> ufs = form.getModelObject().getListaUf();
        ufsSelecionadasComparacao.clear();
        if (!ufs.isEmpty()) {
            for (Uf uf : ufs) {
                ufsSelecionadasComparacao.add(uf);
            }
        }
    }

    private ListMultipleChoice<Municipio> newListMultipleChoiceMunicipio() {
        ListMultipleChoice<Municipio> listMultipleChoice = new ListMultipleChoice<Municipio>("ListaMunicipio", getListaMunicipios(), new ChoiceRenderer<Municipio>("nomeMunicipioUf"));
        listMultipleChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                listMultipleChoiceMunicipio.setModelObject(form.getModelObject().getListaMunicipio());
                target.add(municipioContainer);
            }
        });
        return listMultipleChoice;
    }

    private List<Municipio> getListaMunicipios() {
        List<Uf> listaUf = form.getModelObject().getListaUf();
        if (!listaUf.isEmpty()) {
            List<Municipio> listaMunicipios = new ArrayList<Municipio>();
            for (Uf uf : listaUf) {
                listaMunicipios.addAll(municipioService.buscarPelaUfId(uf.getId()));
            }
            return listaMunicipios;
        }
        return Collections.emptyList();
    }

    private List<UnidadeExecutora> getListUnidadeExecutora() {

        Orgao orgao = getOrgao();
        if (orgao != null && orgao.getId() != null) {
            return unidadeExecutoraService.buscarPeloOrgaoId(orgao.getId());
        }

        return Collections.emptyList();
    }

    private Button newButtonPesquisar() {
        return componentFactory.newButton("btnPesquisar", () -> pesquisar());
    }

    private void pesquisar() {
        setPesquisado(true);
        programa = form.getModelObject().getPrograma();

        if (programa == null && orgao != null) {
            programa = new Programa();
            UnidadeExecutora unidade = new UnidadeExecutora();
            unidade.setOrgao(orgao);
            programa.setUnidadeExecutora(unidade);
            form.getModelObject().setPrograma(programa);
        } else if (programa != null && programa.getUnidadeExecutora() == null && orgao != null) {
            UnidadeExecutora unidade = new UnidadeExecutora();
            unidade.setOrgao(orgao);
            form.getModelObject().getPrograma().setUnidadeExecutora(unidade);
        }
        panelGridResultados.setVisible(isPesquisado());
        if (dp.size() == 0) {
            addMsgInfo("Pesquisa não encontrou resultado.");
        }
    }

    private InfraAjaxFallbackLink<Void> newLinkNovo() {
        return componentFactory.newAjaxFallbackLink("btnNovo", (target) -> adicionarNovo(target));
    }

    private InfraDropDownChoice<EnumStatusPrograma> newDropDownStatus() {
        InfraDropDownChoice<EnumStatusPrograma> dropDownChoice = componentFactory.newDropDownChoice("programa.statusPrograma", "Status", false, "valor", "descricao", null, Arrays.asList(EnumStatusPrograma.values()), null);
        dropDownChoice.setNullValid(true);
        return dropDownChoice;
    }

    private TextField<String> newTextFieldNumeroProcessoSEI() {
        TextField<String> textField = new TextField<String>("programa.numeroProcessoSEI") {
            private static final long serialVersionUID = 1L;

            @SuppressWarnings("unchecked")
            @Override
            public <C> IConverter<C> getConverter(Class<C> type) {
                return (IConverter<C>) new NumeroProcessoSeiConverter();
            }
        };
        textField.setLabel(Model.of("Nº do Processo SEI"));
        textField.add(StringValidator.maximumLength(20));
        return textField;
    }

    private InfraDropDownChoice<Elemento> newDropDownElemento() {
        InfraDropDownChoice<Elemento> dropDownChoice = componentFactory.newDropDownChoice("elemento", "Elemento", false, "id", "nomeECodigo", null, genericPersister.findAll(Elemento.class), null);
        dropDownChoice.setNullValid(true);
        return dropDownChoice;
    }

    private InfraDropDownChoice<EmendaParlamentar> newDropDownEmendaParlamentar() {
        InfraDropDownChoice<EmendaParlamentar> dropDownChoice = componentFactory.newDropDownChoice("emendaParlamentar", "Emenda Parlamentar", false, "id", "nomeEmendaParlamentar", null, getListProgramaEmendaParlamentar(), null);
        dropDownChoice.setNullValid(true);
        return dropDownChoice;

    }

    private InfraDropDownChoice<AcaoOrcamentaria> newDropDownAcaoOrcamentaria() {
        InfraDropDownChoice<AcaoOrcamentaria> dropDownChoice = componentFactory.newDropDownChoice("acaoOrcamentaria", "Ação Orçamentária", false, "id", "numeroNomeAcaoOrcamentaria", null, getListProgramaAcaoOrcamentaria(), null);
        dropDownChoice.setNullValid(true);
        return dropDownChoice;

    }

    private DropDownChoice<Boolean> newDropDownLimitacaoGeografica() {
        DropDownChoice<Boolean> dropDownChoice = new DropDownChoice<Boolean>("programa.possuiLimitacaoGeografica", Arrays.asList(Boolean.TRUE, Boolean.FALSE));
        dropDownChoice.setLabel(Model.of("Limitação Geográfica"));
        dropDownChoice.setNullValid(true);
        dropDownChoice.setChoiceRenderer(new IChoiceRenderer<Boolean>() {
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

        dropDownChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1L;

            protected void onUpdate(AjaxRequestTarget target) {
                limitar(target);
            }

        });

        return dropDownChoice;
    }

    private void limitar(AjaxRequestTarget target) {

        Boolean visible = form.getModelObject().getPrograma().getPossuiLimitacaoGeografica() == null ? Boolean.FALSE : form.getModelObject().getPrograma().getPossuiLimitacaoGeografica();

        municipioContainer.setVisible(visible);
        ufContainer.setVisible(visible);
        target.add(municipioContainer, ufContainer);
    }

    private InfraDropDownChoice<UnidadeExecutora> newDropDownUnidadeExecutora() {
        InfraDropDownChoice<UnidadeExecutora> dropDownChoice = componentFactory.newDropDownChoice("programa.unidadeExecutora", "Unidade Executora", false, "id", "codigoNome", null, getListUnidadeExecutora(), null);
        dropDownChoice.setNullValid(true);
        return dropDownChoice;
    }

    private InfraDropDownChoice<Orgao> newDropDownOrgao() {
        InfraDropDownChoice<Orgao> dropDownChoice = componentFactory.newDropDownChoice("programa.unidadeExecutora.orgao", "Órgão", false, "id", "codigoOrgaoSigla", new LambdaModel<Orgao>(this::getOrgao, this::setOrgao), orgaoService.buscarTodos(), null);
        dropDownChoice.setNullValid(true);

        dropDownChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                dropDownChoiceUnidadeExecutora.setChoices(getListUnidadeExecutora());
                target.add(unidadeExecutoraContainer);
            }
        });
        return dropDownChoice;

    }

    private TextField<Integer> newTextFieldAno() {
        return componentFactory.newTextField("programa.anoPrograma", "Ano", false, null, new YearIntegerValidator());

    }

    private TextField<String> newTextFieldNomeFantasia() {
        return componentFactory.newTextField("programa.nomeFantasiaPrograma", "Nome Fantasia", false, null);
    }

    private TextField<String> newTextFieldNome() {
        return componentFactory.newTextField("programa.nomePrograma", "Nome", false, null);
    }

    private TextField<String> newTextFieldCodigo() {
        return componentFactory.newTextField("programa.id", "Código", false, null);

    }

    @SuppressWarnings("unused")
    private void criarBreadcrumb() {
        form.add(new Link<Void>("dashboard") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                setResponsePage(HomePage.class);
            }
        });
    }

    public InfraAjaxFallbackLink<Void> getButtonNovo() {
        return componentFactory.newAjaxFallbackLink("btnNovo", (target) -> adicionarNovo(target));
    }

    private InfraAjaxFallbackLink<Void> newButtonVisualizar(Item<Programa> item) {
        InfraAjaxFallbackLink<Void> btn = componentFactory.newAjaxFallbackLink("btnVisualizar", (target) -> visualizar(item, target));
        btn.setOutputMarkupId(true);
        return btn;
    }

    private void adicionarNovo(AjaxRequestTarget target) {
        if (!getSideSession().hasRole(ROLE_MANTER_PROGRAMA_INCLUIR)) {
            throw new SecurityException();
        }
        setResponsePage(new ProgramaPage(new PageParameters(), this, new Programa(), false, target, 0));
    }

    private List<AcaoOrcamentaria> getListProgramaAcaoOrcamentaria() {
        return acaoOrcamentariaService.buscarAcaoOrcamentariaUtilizada();

    }

    private List<EmendaParlamentar> getListProgramaEmendaParlamentar() {
        return acaoOrcamentariaService.buscarEmendaParlamentarUtilizada();

    }

    private DataView<Programa> newDataViewPrograma(SortableProgramaDataProvider dp) {
        DataView<Programa> dataView = new DataView<Programa>("programas", dp) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<Programa> item) {

                PermissaoProgramaDto permissaoPrograma = publicizacaoService.buscarPermissoesPrograma(item.getModelObject());

                EnumStatusPrograma status = item.getModelObject().getStatusPrograma();
                String idPrograma = "";
                if (status == EnumStatusPrograma.EM_ELABORACAO || status == EnumStatusPrograma.FORMULADO) {
                    idPrograma = item.getModelObject().getId().toString();
                } else {
                    idPrograma = item.getModelObject().getCodigoIdentificadorProgramaPublicado();
                }

                item.add(new Label("id", idPrograma));
                item.add(new Label("nomePrograma"));
                item.add(new Label("numeroProcessoSEI") {
                    private static final long serialVersionUID = 1L;

                    @SuppressWarnings("unchecked")
                    @Override
                    public <C> IConverter<C> getConverter(Class<C> type) {
                        return (IConverter<C>) new NumeroProcessoSeiConverter();
                    }

                });
                item.add(new Label("statusPrograma.descricao"));

                InfraAjaxFallbackLink<Void> btnVisualizar = newButtonVisualizar(item);
                authorize(btnVisualizar, RENDER, ROLE_MANTER_PROGRAMA_VISUALIZAR);
                item.add(btnVisualizar);

                InfraAjaxConfirmButton btnExcluir = componentFactory.newAJaxConfirmButton("btnExcluir", "MN031", form, (target, formz) -> excluir(target, item));
                btnExcluir.setVisible(permissaoPrograma.getExcluir());
                authorize(btnExcluir, RENDER, ROLE_MANTER_PROGRAMA_EXCLUIR);
                item.add(btnExcluir);

                Button btnExportarPdf = getButtonRelatorio(item);
                authorize(btnExportarPdf, RENDER, ROLE_MANTER_PROGRAMA_VISUALIZAR);
                item.add(btnExportarPdf); // btnPdf
            }

        };
        dataView.setItemsPerPage(Constants.ITEMS_PER_PAGE_PAGINATION);
        return dataView;
    }

    private Button getButtonRelatorio(Item<Programa> item) {
        Button buttonCsv = componentFactory.newButton("btnPdf", () -> gerarPdf(item));
        return buttonCsv;
    }

    private void excluir(AjaxRequestTarget target, Item<Programa> item) {
        if (!getSideSession().hasRole(ROLE_MANTER_PROGRAMA_EXCLUIR)) {
            throw new SecurityException();
        }
        Programa programa = item.getModelObject();
        programaService.excluir(programa.getId());
        addMsgInfo("MN032");
        target.add(panelGridResultados);
    }

    private void visualizar(Item<Programa> item, AjaxRequestTarget target) {

        Programa programaClicado = item.getModelObject();
        EnumStatusPrograma status = item.getModelObject().getStatusPrograma();

        // Se estiver só na fase de elaboração do programa
        if (status == EnumStatusPrograma.EM_ELABORACAO) {
            setResponsePage(new ProgramaPage(new PageParameters(), this, programaClicado, false, target, 0));
            return;
        }

        // Se já foi publicado o programa
        if (status == EnumStatusPrograma.FORMULADO || status == EnumStatusPrograma.ABERTO_REC_PROPOSTAS || status == EnumStatusPrograma.SUSPENSO || status == EnumStatusPrograma.SUSPENSO_PRAZO_PROPOSTAS || status == EnumStatusPrograma.CANCELADO || status == EnumStatusPrograma.PUBLICADO) {

            PermissaoProgramaDto permissaoPrograma = new PermissaoProgramaDto();

            if (status == EnumStatusPrograma.FORMULADO) {
                permissaoPrograma.setPublicar(true);
            }

            if (programaClicado.getStatusPrograma() == EnumStatusPrograma.ABERTO_REC_PROPOSTAS || programaClicado.getStatusPrograma() == EnumStatusPrograma.PUBLICADO) {
                permissaoPrograma.setEscolherProrrogarSuspenderPrograma(true);
            }

            if (programaClicado.getStatusPrograma() == EnumStatusPrograma.SUSPENSO_PRAZO_PROPOSTAS) {
                permissaoPrograma.setReabrirPrazo(true);
            }

            if (programaClicado.getStatusPrograma() == EnumStatusPrograma.SUSPENSO) {
                permissaoPrograma.setSuspenderPrograma(true);
            }

            if (programaClicado.getStatusPrograma() == EnumStatusPrograma.CANCELADO) {
                permissaoPrograma.setCancelar(true);
            }

            setResponsePage(new ProgramaPublicizacaoPage(new PageParameters(), programaClicado, this, permissaoPrograma, 2));
            return;
        }

        if (status == EnumStatusPrograma.EM_ANALISE || status == EnumStatusPrograma.ABERTO_REC_LOC_ENTREGA) {
            AnaliseDto analiseDto = new AnaliseDto();
            analiseDto.setAbaClicada(EnumAbaFaseAnalise.ELEGIBILIDADE);
            setResponsePage(new PropostasEnviadasPage(new PageParameters(), programaClicado, this, target, analiseDto, 3));
            return;
        }

        if (status == EnumStatusPrograma.ABERTO_PLANEJAMENTO_LICITACAO) {
            setResponsePage(new PlanejamentoLicitacaoPage(new PageParameters(), this, programaClicado, target, 4));
            return;
        }

        if (status == EnumStatusPrograma.ABERTO_GERACAO_CONTRATO) {
            // setResponsePage(new ContratoPesquisaPage(new PageParameters(), programaClicado, this));
            setResponsePage(new PlanejamentoLicitacaoPage(new PageParameters(), this, programaClicado, target, 4));
            return;
        }

        if (status == EnumStatusPrograma.EM_EXECUCAO) {
            setResponsePage(new AcompanharOrdemFornecimentoPage(new PageParameters(), programaClicado, this, 5));
        }
        
        if (status == EnumStatusPrograma.ACOMPANHAMENTO) {
            setResponsePage(new AcompanharOrdemFornecimentoPage(new PageParameters(), programaClicado, this, 6));
        }
    }

    private class PanelGridResultados extends WebMarkupContainer {

        private static final long serialVersionUID = 1L;

        public PanelGridResultados(String id) {
            super(id);

            dp = new SortableProgramaDataProvider(programaService, form.getModelObject());
            dataView = newDataViewPrograma(dp);
            add(dataView);
            add(getDropItensPorPagina());

            add(new OrderByBorder<String>("orderByCodigo", "id", dp));
            add(new OrderByBorder<String>("orderByNomePrograma", "nomePrograma", dp));
            add(new OrderByBorder<String>("orderByNumeroProcessoSEI", "numeroProcessoSEI", dp));
            add(new OrderByBorder<String>("orderByStatusPrograma", "statusPrograma", dp));
            add(new InfraAjaxPagingNavigator("pagination", dataView));
            add(newButtonExportarCsv());
        }
    }

    private DropDownChoice<Integer> getDropItensPorPagina() {
        DropDownChoice<Integer> dropDownChoice = new DropDownChoice<Integer>("itensPorPagina", new LambdaModel<Integer>(this::getItensPorPagina, this::setItensPorPagina), Constants.QUANTIDADE_ITENS_TABELA);
        dropDownChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                dataView.setItemsPerPage(getItensPorPagina());
                target.add(panelGridResultados);
            };
        });
        return dropDownChoice;
    }

    public Integer getItensPorPagina() {
        return itensPorPagina;
    }

    public void setItensPorPagina(Integer itensPorPagina) {
        this.itensPorPagina = itensPorPagina;
    }

    private Button newButtonExportarCsv() {
        return componentFactory.newButton("btnExportarCsv", () -> exportarCsv());

    }

    private void exportarCsv() {
        List<Programa> programas = programaService.buscarSemPaginacao(form.getModelObject());
        if (!programas.isEmpty()) {
            RelatorioProgramaBuilder builder = new RelatorioProgramaBuilder();
            JRConcreteResource<CsvResourceHandler> relatorioResource = builder.buildJrConcreteResourceCsv(programas);
            ResourceRequestHandler handler = new ResourceRequestHandler(relatorioResource, getPageParameters());
            RequestCycle requestCycle = getRequestCycle();
            requestCycle.scheduleRequestHandlerAfterCurrent(handler);
        } else {
            addMsgError("MSG004");
        }
    }

    public String getMsgConfirmUf() {
        return msgConfirmUf;
    }

    public void setMsgConfirmUf(String msgConfirmUf) {
        this.msgConfirmUf = msgConfirmUf;
    }

    private Modal<String> newModal(String id) {
        Modal<String> modal = new TextContentModal(id, new LambdaModel<String>(this::getMsgConfirmUf, this::setMsgConfirmUf));
        modal.setBackdrop(Backdrop.STATIC);
        modal.addButton(newButtonCorfirmarExclusaoMunicipio(modal));
        modal.addButton(newButtonCancelarExclusaoMunicipio(modal));
        return modal;
    }

    private AjaxDialogButton newButtonCorfirmarExclusaoMunicipio(Modal<String> modal) {
        return new AjaxDialogButton(Model.of("Confirmar"), Buttons.Type.Primary) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                removerMunicipiosModel(getMunicipiosParaExclusao());
                atualizarListaUfComparacao();
                atualizarListaMunicipios();
                modal.show(false);
                modal.close(target);
                target.add(municipioContainer);
            }
        };
    }

    private AjaxDialogButton newButtonCancelarExclusaoMunicipio(Modal<String> modal) {
        return new AjaxDialogButton(Model.of("Cancelar"), Buttons.Type.Danger) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {

                form.getModelObject().getListaUf().clear();
                for (Uf uf : ufsSelecionadasComparacao) {
                    form.getModelObject().getListaUf().add(uf);
                }
                target.add(ufContainer);
                modal.show(false);
                modal.close(target);
            }
        };

    }

    private List<Municipio> getMunicipiosParaExclusao() {
        List<Uf> ufsSelecionada = form.getModelObject().getListaUf();
        List<Municipio> municipiosSelecionados = form.getModelObject().getListaMunicipio();
        List<Municipio> municipiosExclusao = new ArrayList<Municipio>();

        if (!municipiosSelecionados.isEmpty()) {
            for (Municipio municipio : municipiosSelecionados) {
                if (!ufsSelecionada.contains(municipio.getUf())) {
                    municipiosExclusao.add(municipio);
                }
            }
        }
        return municipiosExclusao;
    }

    private void removerMunicipiosModel(List<Municipio> municipiosExclusao) {
        for (Municipio municipio : municipiosExclusao) {
            form.getModelObject().getListaMunicipio().remove(municipio);
        }
    }

    private void gerarPdf(Item<Programa> item) {

        List<Programa> lista = new ArrayList<Programa>();
        lista.add(item.getModelObject());
        RelatorioConsultaPublicaBuilder builder = new RelatorioConsultaPublicaBuilder(programaService, SideUtil.getCaminhoCompletoRelatorios(getHttpServletRequest().getServletContext()));
        try {
            JRConcreteResource<PdfResourceHandler> relatorioResource = builder.export(lista);
            ResourceRequestHandler handler = new ResourceRequestHandler(relatorioResource, getPageParameters());
            RequestCycle requestCycle = getRequestCycle();
            requestCycle.scheduleRequestHandlerAfterCurrent(handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void atualizarListaMunicipios() {
        listMultipleChoiceMunicipio.setModelObject(form.getModelObject().getListaMunicipio());
        listMultipleChoiceMunicipio.setChoices(getListaMunicipios());
    }

    public boolean isPesquisado() {
        return pesquisado;
    }

    public void setPesquisado(boolean pesquisado) {
        this.pesquisado = pesquisado;
    }

    public Orgao getOrgao() {
        return orgao;
    }

    public void setOrgao(Orgao orgao) {
        this.orgao = orgao;
    }
}
