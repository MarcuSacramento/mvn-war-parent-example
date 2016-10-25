package br.gov.mj.side.web.view.consultaPublica;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.resource.ResourceRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.validation.validator.StringValidator;
import org.wicketstuff.jasperreports.JRConcreteResource;
import org.wicketstuff.jasperreports.handlers.PdfResourceHandler;

import br.gov.mj.apoio.entidades.Elemento;
import br.gov.mj.apoio.entidades.Municipio;
import br.gov.mj.apoio.entidades.Orgao;
import br.gov.mj.apoio.entidades.PartidoPolitico;
import br.gov.mj.apoio.entidades.Uf;
import br.gov.mj.apoio.entidades.UnidadeExecutora;
import br.gov.mj.infra.negocio.persistencia.EnumOrder;
import br.gov.mj.infra.negocio.persistencia.IGenericPersister;
import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraDropDownChoice;
import br.gov.mj.infra.wicket.component.pagination.InfraAjaxPagingNavigator;
import br.gov.mj.infra.wicket.model.LambdaModel;
import br.gov.mj.side.entidades.AcaoOrcamentaria;
import br.gov.mj.side.entidades.EmendaParlamentar;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.enums.EnumPersonalidadeJuridica;
import br.gov.mj.side.entidades.enums.EnumStatusInscricao;
import br.gov.mj.side.entidades.enums.EnumStatusPrograma;
import br.gov.mj.side.entidades.enums.EnumTipoPrograma;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoPrograma;
import br.gov.mj.side.web.dto.PermissaoProgramaDto;
import br.gov.mj.side.web.dto.ProgramaPesquisaDto;
import br.gov.mj.side.web.dto.RetornoPermiteInscricaoDto;
import br.gov.mj.side.web.service.AcaoOrcamentariaService;
import br.gov.mj.side.web.service.InscricaoProgramaService;
import br.gov.mj.side.web.service.MunicipioService;
import br.gov.mj.side.web.service.OrgaoService;
import br.gov.mj.side.web.service.PartidoService;
import br.gov.mj.side.web.service.ProgramaService;
import br.gov.mj.side.web.service.PublicizacaoService;
import br.gov.mj.side.web.service.UfService;
import br.gov.mj.side.web.service.UnidadeExecutoraService;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.RelatorioConsultaPublicaBuilder;
import br.gov.mj.side.web.util.SideUtil;
import br.gov.mj.side.web.view.DashboardPanel;
import br.gov.mj.side.web.view.LoginPage;
import br.gov.mj.side.web.view.components.AjaxDialogButton;
import br.gov.mj.side.web.view.components.converters.NumeroProcessoSeiConverter;
import br.gov.mj.side.web.view.components.validators.YearIntegerValidator;
import br.gov.mj.side.web.view.programa.inscricao.InscricaoProgramaPage;
import br.gov.mj.side.web.view.template.TemplatePage;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal.Backdrop;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.TextContentModal;

public class ConsultaProgramasBeneficiarioPage extends TemplatePage {
    private static final long serialVersionUID = 1L;

    private Integer itensPorPagina = Constants.ITEMS_PER_PAGE_PAGINATION;

    private Form<ProgramaPesquisaDto> form;

    private Entidade entidadeLogada = new Entidade();

    private DashboardPanel dashboardPessoasPanel;
    private PanelComponentes panelComponentes;
    private PanelStatus panelStatus;

    private WebMarkupContainer municipioContainer;
    private ListMultipleChoice<Municipio> listMultipleChoiceMunicipio;
    private WebMarkupContainer ufContainer;
    private ListMultipleChoice<Uf> listMultipleChoiceUf;
    private WebMarkupContainer unidadeExecutoraContainer;
    private AjaxCheckBox checkPublicoComp;
    private AjaxCheckBox checkPrivateComp;
    private InfraDropDownChoice<UnidadeExecutora> dropDownChoiceUnidadeExecutora;
    private DataView<Programa> dataView;
    private PanelGridResultados panelGridResultados;
    private Modal<String> modalConfirmUf;
    private Boolean checkPublico = false;
    private Boolean checkPrivado = false;
    private Boolean apto;
    private String msgConfirmUf = new String();
    private Orgao orgao = new Orgao();
    private boolean pesquisado = false;
    private boolean habilitarBotaoVoltar = false;

    private List<Uf> ufsSelecionadasComparacao = new ArrayList<Uf>();
    private List<EmendaParlamentar> listaEmendas = new ArrayList<EmendaParlamentar>();
    private List<InscricaoPrograma> listaDeProgramasInscritos = new ArrayList<InscricaoPrograma>();

    @Inject
    private MunicipioService municipioService;
    @Inject
    private IGenericPersister genericPersister;
    @Inject
    private OrgaoService orgaoService;
    @Inject
    private ProgramaService programaService;
    @Inject
    private UnidadeExecutoraService unidadeExecutoraService;
    @Inject
    private AcaoOrcamentariaService acaoOrcamentariaService;
    @Inject
    private PublicizacaoService publicizacaoService;
    @Inject
    private PartidoService partidoService;
    @Inject
    private UfService ufService;
    @Inject
    private InscricaoProgramaService inscricaoService;
    @Inject
    private ComponentFactory componentFactory;

    private Programa programa;

    public ConsultaProgramasBeneficiarioPage(final PageParameters pageParameters) {
        super(pageParameters);

        entidadeLogada = (Entidade) getSessionAttribute("entidade");
        // todasEmendas();
        initVariaveis();
        initComponents();

        if (entidadeLogada == null) {
            setTitulo("Pesquisa Aberta");
            this.habilitarBotaoVoltar = true;
        } else {
            setTitulo(entidadeLogada.getNomeEntidade());
            this.habilitarBotaoVoltar = false;
        }

    }

    private void initComponents() {

        form = componentFactory.newForm("form", new ProgramaPesquisaDto());
        form.getModelObject().setPesquisaPublica(true);

        // panelBreadcrump = new PanelBreadcrump("panelBreadcrump");
        dashboardPessoasPanel = new DashboardPanel("dashboardPessoasPanel");
        form.add(dashboardPessoasPanel);

        dashboardPessoasPanel.setVisible(true);
        this.habilitarBotaoVoltar = false;

        panelComponentes = new PanelComponentes("panelComponentes");
        panelStatus = new PanelStatus("panelStatus");

        form.add(panelComponentes);
        panelComponentes.add(panelStatus);

        Button newButtonPesquisar = newButtonPesquisar();
        form.add(newButtonPesquisar);
        form.add(newButtonLimpar()); // btnLimpar
        Button newButtonVoltar = newButtonVoltar();
        newButtonVoltar.setVisible(this.habilitarBotaoVoltar);
        form.add(newButtonVoltar);

        panelGridResultados = new PanelGridResultados("panelGridResultados");
        panelGridResultados.setVisible(isPesquisado());
        form.add(panelGridResultados);

        modalConfirmUf = newModal("modalConfirmUf");
        modalConfirmUf.show(false);
        form.add(modalConfirmUf);
        add(form);
    }

    private void initVariaveis() {
        if (entidadeLogada != null) {
            listaDeProgramasInscritos = inscricaoService.buscarProgramasInscritosParaEntidade(entidadeLogada);
        }
    }

    private class PanelComponentes extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelComponentes(String id) {
            super(id);

            add(newTextFieldCodigo());
            add(newTextFieldNome());
            add(newTextFieldNomeParlamentar());
            add(newTextFieldCargoParlamentar());
            add(newDropDownPartidos());
            add(newDropDownUf());

            add(newTextFieldNomeFantasia());
            add(newTextFieldAno());
            add(newDropDownOrgao());

            dropDownChoiceUnidadeExecutora = newDropDownUnidadeExecutora();
            unidadeExecutoraContainer = new WebMarkupContainer("unidadeExecutoraContainer");
            unidadeExecutoraContainer.add(dropDownChoiceUnidadeExecutora);
            add(unidadeExecutoraContainer);

            add(newDropDownLimitacaoGeografica());

            listMultipleChoiceUf = newListMultipleChoiceUf();
            ufContainer = new WebMarkupContainer("ufContainer");
            ufContainer.add(listMultipleChoiceUf);
            ufContainer.setVisible(false);
            add(ufContainer);

            listMultipleChoiceMunicipio = newListMultipleChoiceMunicipio();
            municipioContainer = new WebMarkupContainer("municipioContainer");
            municipioContainer.add(listMultipleChoiceMunicipio);
            municipioContainer.add(getCheckPrivado()); // checkPrivado
            municipioContainer.add(getCheckPublico()); // checkPublico
            municipioContainer.setVisible(false);
            add(municipioContainer);

            // add(newDropDownEmendaParlamentar());
            add(newDropDownAcaoOrcamentaria());
            add(newDropDownElemento());
            add(newTextFieldNumeroProcessoSEI());
            add(newDropDownAptoProposta());
            add(newDropDownTipoPrograma());
            add(newTextFieldCnpjBeneficiario());
        }
    }

    private class PanelStatus extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelStatus(String id) {
            super(id);
            add(newDropDownStatus());
        }
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

    private AjaxCheckBox getCheckPublico() {
        checkPublicoComp = new AjaxCheckBox("checkPublico", new Model<Boolean>(false)) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                acaoCheck(target);
            }
        };
        checkPublicoComp.setOutputMarkupId(true);
        return checkPublicoComp;
    }

    private AjaxCheckBox getCheckPrivado() {
        checkPrivateComp = new AjaxCheckBox("checkPrivado", new Model<Boolean>(false)) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                acaoCheck(target);
            }
        };
        checkPrivateComp.setOutputMarkupId(true);

        return checkPrivateComp;
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

    public AjaxSubmitLink newButtonLimpar() {
        AjaxSubmitLink buttonCancelarEdicao = new AjaxSubmitLink("btnLimpar", form) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> formulario) {

                form.setModelObject(new ProgramaPesquisaDto());
                form.getModelObject().setPesquisaPublica(true);
                form.getModelObject().setPrograma(new Programa());
                checkPrivateComp.setModelObject(false);
                checkPublicoComp.setModelObject(false);
                ufsSelecionadasComparacao.clear();
                setOrgao(new Orgao());
                apto = null;
                panelComponentes.addOrReplace(newDropDownAptoProposta());

                municipioContainer.setVisible(false);
                ufContainer.setVisible(false);

                listMultipleChoiceMunicipio.setChoices(new ArrayList<Municipio>());
                municipioContainer.addOrReplace(listMultipleChoiceMunicipio);
                ufContainer.addOrReplace(listMultipleChoiceUf);
                target.add(panelComponentes);

                SortableProvider dp = new SortableProvider(programaService, form.getModelObject());
                dataView = newDataViewPrograma(dp);
                panelGridResultados.addOrReplace(dataView);
                panelGridResultados.addOrReplace(new InfraAjaxPagingNavigator("pagination", dataView));
                panelGridResultados.setVisible(false);

                target.add(panelGridResultados);
            }
        };
        return buttonCancelarEdicao;
    }

    private Button newButtonVoltar() {
        return componentFactory.newButton("btnVoltar", () -> setResponsePage(LoginPage.class));
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
    }

    public void limpar() {
        form.setModelObject(new ProgramaPesquisaDto());
    }

    private InfraDropDownChoice<EnumStatusPrograma> newDropDownStatus() {
        List<EnumStatusPrograma> status = new ArrayList<EnumStatusPrograma>();
        List<EnumStatusPrograma> temp = Arrays.asList(EnumStatusPrograma.values());
        for (EnumStatusPrograma s : temp) {
            if (s != EnumStatusPrograma.EM_ELABORACAO && s != EnumStatusPrograma.FORMULADO) {
                status.add(s);
            }
        }

        InfraDropDownChoice<EnumStatusPrograma> dropDownChoice = componentFactory.newDropDownChoice("programa.statusPrograma", "Status", false, "valor", "descricao", null, status, null);
        dropDownChoice.setNullValid(true);
        return dropDownChoice;
    }

    private InfraDropDownChoice<EnumTipoPrograma> newDropDownTipoPrograma() {
        InfraDropDownChoice<EnumTipoPrograma> dropDownChoice = componentFactory.newDropDownChoice("tipoPrograma", "Tipo Programa", false, "valor", "descricao", null, Arrays.asList(EnumTipoPrograma.values()), null);
        dropDownChoice.setNullValid(true);
        return dropDownChoice;
    }

    private DropDownChoice<Boolean> newDropDownAptoProposta() {

        DropDownChoice<Boolean> drop = new DropDownChoice<Boolean>("aptoProposta", new PropertyModel<Boolean>(this, "apto"), Arrays.asList(Boolean.TRUE, Boolean.FALSE));
        drop.setNullValid(true);
        drop.setChoiceRenderer(new IChoiceRenderer<Boolean>() {
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
        drop.setVisible(false);
        acaoAptoProposta(drop);
        return drop;
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
        TextField<String> textField = componentFactory.newTextField("programa.nomeFantasiaPrograma", "Nome Fantasia", false, null);
        textField.add(StringValidator.maximumLength(200));
        return textField;
    }

    private TextField<String> newTextFieldNome() {
        TextField<String> textField = componentFactory.newTextField("programa.nomePrograma", "Nome Programa", false, null);
        textField.add(StringValidator.maximumLength(200));
        return textField;
    }

    private TextField<String> newTextFieldNomeParlamentar() {
        TextField<String> textField = componentFactory.newTextField("nomeParlamentar", "Nome Parlamentar", false, null);
        textField.add(StringValidator.maximumLength(200));
        return textField;
    }

    private TextField<String> newTextFieldCargoParlamentar() {
        TextField<String> textField = componentFactory.newTextField("cargoParlamentar", "Cargo Parlamentar", false, null);
        textField.add(StringValidator.maximumLength(200));
        return textField;
    }

    private TextField<String> newTextFieldCnpjBeneficiario() {
        TextField<String> textField = componentFactory.newTextField("cnpjBeneficiario.numeroCnpjBeneficiario", "CNPJ Beneficiário", false, null);
        textField.add(StringValidator.maximumLength(18));
        return textField;
    }

    private InfraDropDownChoice<PartidoPolitico> newDropDownPartidos() {
        InfraDropDownChoice<PartidoPolitico> dropDownPartidos = componentFactory.newDropDownChoice("partidoParlamentar", "Partido", false, "id", "siglaNome", null, partidoService.buscarTodos(), null);
        dropDownPartidos.setNullValid(true);
        dropDownPartidos.setOutputMarkupId(true);
        return dropDownPartidos;
    }

    private InfraDropDownChoice<Uf> newDropDownUf() {
        InfraDropDownChoice<Uf> dropDownUf = componentFactory.newDropDownChoice("ufParlamentar", "UF", false, "id", "nomeSigla", null, ufService.buscarTodos(), null);
        dropDownUf.setNullValid(true);
        return dropDownUf;
    }

    private TextField<String> newTextFieldCodigo() {
        TextField<String> textField = componentFactory.newTextField("codigoIdentificadorProgramaPublicado", "Código", false, null);
        textField.add(StringValidator.maximumLength(30));
        return textField;

    }

    private List<AcaoOrcamentaria> getListProgramaAcaoOrcamentaria() {
        return acaoOrcamentariaService.buscarAcaoOrcamentariaUtilizada();

    }

    private DataView<Programa> newDataViewPrograma(SortableProvider dp) {
        DataView<Programa> dataView = new DataView<Programa>("programas", dp) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<Programa> item) {
                item.add(new Label("identificadorProgramaPublicado", item.getModelObject().getCodigoIdentificadorProgramaPublicado().toString()));
                item.add(new Label("nomePrograma"));
                item.add(new Label("numeroProcessoSEI") {
                    private static final long serialVersionUID = 1L;

                    @SuppressWarnings("unchecked")
                    @Override
                    public <C> IConverter<C> getConverter(Class<C> type) {
                        return (IConverter<C>) new NumeroProcessoSeiConverter();
                    }

                });
                item.add(new Label("statusPrograma.descricao", item.getModelObject().getStatusPrograma().getDescricao()));

                Button btnVisualizar = componentFactory.newButton("btnVisualizar", () -> visualizar(item));
                item.add(btnVisualizar);
                item.add(getButtonRelatorio(item)); // btnPdf

                Boolean permiteInscrever = verificarSeBotaoVisivel(item.getModelObject());
                Boolean mostrarBotaoInscrito = mostrarBotaoInscrito(item);

                item.add(getButtonInscrever(item, permiteInscrever)); // btnInscrever
                item.add(getButtonEditarInscricao(item)); // btnEditarInscricao
                item.add(getButtonErro(item, permiteInscrever, mostrarBotaoInscrito)); // btnErro
                item.add(getButtonInscrito(item, mostrarBotaoInscrito)); // btnIsInscrito
            }
        };
        dataView.setItemsPerPage(Constants.ITEMS_PER_PAGE_PAGINATION);
        return dataView;
    }

    private void visualizar(Item<Programa> item) {
        setResponsePage(new ConsultaPublicaPage(new PageParameters(), item.getModelObject(), this));
    }

    private class PanelGridResultados extends WebMarkupContainer {

        private static final long serialVersionUID = 1L;

        public PanelGridResultados(String id) {
            super(id);

            SortableProvider dp = new SortableProvider(programaService, form.getModelObject());
            dataView = newDataViewPrograma(dp);
            add(dataView);
            add(getDropItensPorPagina());

            add(new OrderByBorder<String>("orderByCodigo", "identificadorProgramaPublicado", dp));
            add(new OrderByBorder<String>("orderByNomePrograma", "nomePrograma", dp));
            add(new OrderByBorder<String>("orderByNumeroProcessoSEI", "numeroProcessoSEI", dp));
            add(new OrderByBorder<String>("orderByStatusPrograma", "statusPrograma", dp));
            add(new InfraAjaxPagingNavigator("pagination", dataView));
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

    private Button getButtonRelatorio(Item<Programa> item) {
        Button buttonCsv = componentFactory.newButton("btnPdf", () -> gerarPdf(item));
        return buttonCsv;
    }

    private Button getButtonInscrever(Item<Programa> item, Boolean permiteInscrever) {
        Button buttonInscricao = componentFactory.newButton("btnInscrever", () -> inscrever(item));

        // Se já estiver inscrito neste programa então o botão de inscrever não
        // será mostrado
        permiteInscrever = verificarSeJaInscritoNestePrograma(permiteInscrever, item.getModelObject());
        buttonInscricao.setVisible(permiteInscrever);

        // Se não estiver logado irá mostrar a mensagem abaixo
        String avisoBotao = "";
        if (entidadeLogada == null) {
            avisoBotao = "Efetue o Login para Inscrever-se neste programa.";
        }

        AttributeAppender classDropDown = new AttributeAppender("title", avisoBotao, " ");
        buttonInscricao.add(classDropDown);
        return buttonInscricao;
    }

    private Button getButtonEditarInscricao(Item<Programa> item) {
        InscricaoPrograma inscrito = new InscricaoPrograma();
        boolean mostrarBotaoEditarInscricao = false;
        if (entidadeLogada != null) {

            // Irá tentar recuperar a inscrição feita neste programa
            inscrito = recuperarInscricaoDoPrograma(item.getModelObject());
            if (inscrito != null && inscrito.getId() != null && inscrito.getStatusInscricao() == EnumStatusInscricao.EM_ELABORACAO) {
                mostrarBotaoEditarInscricao = true;
            }
        }

        Button btnDetalhes = componentFactory.newButton("btnEditarInscricao", () -> irPaginaEditarInscricao(item.getModelObject()));
        btnDetalhes.setVisible(mostrarBotaoEditarInscricao && item.getModelObject().getStatusPrograma() == EnumStatusPrograma.ABERTO_REC_PROPOSTAS);
        return btnDetalhes;
    }

    private Button getButtonInscrito(Item<Programa> item, boolean mostrarButton) {
        // InscricaoPrograma inscrito = new InscricaoPrograma();
        Button btnInscrito = componentFactory.newButton("btnIsInscrito", () -> irPaginaEditarInscricao(item.getModelObject()));
        btnInscrito.setOutputMarkupId(true);
        btnInscrito.setVisible(mostrarButton);
        AttributeAppender classDropDown = new AttributeAppender("title", "Inscrição Submetida", " ");
        btnInscrito.add(classDropDown);

        return btnInscrito;
    }

    private Button getButtonErro(Item<Programa> item, Boolean permiteInscrever, Boolean inscritoNoPrograma) {
        Button btnErro = componentFactory.newButton("btnErro", () -> irPaginaEditarInscricao(item.getModelObject()));
        String mensagemErro = "";

        if (inscritoNoPrograma) {
            permiteInscrever = true;
        } else {
            if (entidadeLogada == null) {
                permiteInscrever = true;
            } else {
                RetornoPermiteInscricaoDto dto = permiteInscreverNestePrograma(item.getModelObject());
                mensagemErro = dto.getRetornoMensagem();
            }
        }

        AttributeAppender classDropDown = new AttributeAppender("data-content", mensagemErro, " ");
        btnErro.add(classDropDown);
        btnErro.setVisible(!permiteInscrever);
        return btnErro;
    }

    private void gerarPdf(Item<Programa> item) {

        List<Programa> lista = new ArrayList<Programa>();
        lista.add(item.getModelObject());
        RelatorioConsultaPublicaBuilder builder = new RelatorioConsultaPublicaBuilder(programaService, SideUtil.getCaminhoCompletoRelatorios(getHttpServletRequest().getServletContext()));
        try {

            // Somente será mostrado o histórico se NÃO estiver no modo de
            // consulta pública.
            builder.setMostrarHistorico(false);
            JRConcreteResource<PdfResourceHandler> relatorioResource = builder.export(lista);
            ResourceRequestHandler handler = new ResourceRequestHandler(relatorioResource, getPageParameters());
            RequestCycle requestCycle = getRequestCycle();
            requestCycle.scheduleRequestHandlerAfterCurrent(handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void inscrever(Item<Programa> item) {

        PageParameters parameters = new PageParameters();
        parameters.add("programa", item.getModelObject().getId());
        setResponsePage(InscricaoProgramaPage.class, parameters);

        addSessionAttribute("programa", item.getModelObject().getId());

        // TODO remover posteriormente
        // setResponsePage(new InscricaoProgramaPage(parameters,item.getModelObject(),this));
    }

    private void irPaginaEditarInscricao(Programa item) {
        InscricaoPrograma inscrito = recuperarInscricaoDoPrograma(item);
        setResponsePage(new InscricaoProgramaPage(new PageParameters(), inscrito, this, false));
    }

    private RetornoPermiteInscricaoDto permiteInscreverNestePrograma(Programa programa) {
        RetornoPermiteInscricaoDto permiteInscricao;
        if (programa == null) {
            return permiteInscricao = new RetornoPermiteInscricaoDto(false, "");
        }
        permiteInscricao = inscricaoService.permiteInscricaoDaEntidadeNoPrograma(programa, entidadeLogada);
        return permiteInscricao;
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

    // AÇÕES

    private boolean mostrarBotaoInscrito(Item<Programa> item) {
        InscricaoPrograma inscrito = new InscricaoPrograma();
        boolean mostrarButton = false;
        if (entidadeLogada != null) {
            inscrito = recuperarInscricaoDoPrograma(item.getModelObject());
            if (inscrito != null && inscrito.getId() != null && inscrito.getStatusInscricao() != EnumStatusInscricao.EM_ELABORACAO) {

                // Se o status desta inscrição for diferente de 'Em elaboração',
                // então ele já foi inscrito, o status poderá ser: Em analise ou
                // finalizada a inscrição
                mostrarButton = true;
            }
        }
        return mostrarButton;
    }

    public void todasEmendas() {
        List<EmendaParlamentar> todasEmendas = acaoOrcamentariaService.buscarEmendaParlamentarUtilizada();
        for (EmendaParlamentar emenda : todasEmendas) {
            if (listaEmendas.isEmpty()) {
                listaEmendas.add(emenda);
            } else {
                int flag = 0;
                for (EmendaParlamentar emenda2 : listaEmendas) {
                    if (emenda2.getNumeroEmendaParlamantar().equalsIgnoreCase(emenda.getNumeroEmendaParlamantar())) {
                        flag++;
                        break;
                    }
                }

                if (flag == 0) {
                    listaEmendas.add(emenda);
                }
                flag = 0;
            }
        }
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

    private void acaoAptoProposta(DropDownChoice<Boolean> drop) {
        drop.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1L;

            protected void onUpdate(AjaxRequestTarget target) {
                if (apto) {
                    form.getModelObject().setPrograma(new Programa());
                    form.getModelObject().getPrograma().setStatusPrograma(EnumStatusPrograma.ABERTO_REC_PROPOSTAS);
                    panelStatus.addOrReplace(newDropDownStatus());
                    target.add(panelStatus);
                }
            }

        });
    }

    private void acaoCheck(AjaxRequestTarget target) {
        checkPrivado = checkPrivateComp.getModelObject();
        checkPublico = checkPublicoComp.getModelObject();

        if (checkPrivado == true && checkPublico == true) {
            form.getModelObject().getPrograma().setTipoPersonalidadeJuridica(EnumPersonalidadeJuridica.TODAS);
        } else {
            if (checkPrivado == true && checkPublico == false) {
                form.getModelObject().getPrograma().setTipoPersonalidadeJuridica(EnumPersonalidadeJuridica.PRIVADA);
            } else {
                if (checkPrivado == false && checkPublico == true) {
                    form.getModelObject().getPrograma().setTipoPersonalidadeJuridica(EnumPersonalidadeJuridica.PUBLICA);
                } else {
                    form.getModelObject().getPrograma().setTipoPersonalidadeJuridica(null);
                }
            }
        }
    }

    // PROVIDER

    private class SortableProvider extends SortableDataProvider<Programa, String> {

        private static final long serialVersionUID = 1L;
        private ProgramaService programaService;
        private ProgramaPesquisaDto programaPesquisaDto;

        public SortableProvider(ProgramaService programaService, ProgramaPesquisaDto programaPesquisaDto) {
            this.programaPesquisaDto = programaPesquisaDto;
            this.programaService = programaService;
            setSort("nomePrograma", SortOrder.ASCENDING);
        }

        @Override
        public Iterator<? extends Programa> iterator(long first, long count) {
            return programaService.buscarPublicados(programaPesquisaDto, (int) first, (int) count, getSort().isAscending() ? EnumOrder.ASC : EnumOrder.DESC, getSort().getProperty()).iterator();
        }

        @Override
        public long size() {
            return programaService.buscarPublicadosSemPaginacao(programaPesquisaDto).size();
        }

        @Override
        public IModel<Programa> model(Programa object) {
            return new CompoundPropertyModel<Programa>(object);
        }

    }

    private boolean verificarSeBotaoVisivel(Programa programa) {
        boolean permiteInscrever = false;
        if (programa == null) {
            return false;
        }
        RetornoPermiteInscricaoDto dto = permiteInscreverNestePrograma(programa);
        // Se a pessoa não estiver logada então será apenas verificado se o
        // programa esta aberto a inscrições
        if (entidadeLogada == null) {
            PermissaoProgramaDto permissaoProgramaDto = publicizacaoService.buscarPermissoesPrograma(programa);
            if (permissaoProgramaDto.getInscrever()) {
                permiteInscrever = true;
            } else {
                permiteInscrever = false;
            }
        } else {
            permiteInscrever = dto.isPermiteInscricao();
        }

        return permiteInscrever;
    }

    private boolean verificarSeJaInscritoNestePrograma(boolean verificar, Programa programa) {
        if (listaDeProgramasInscritos.size() > 0) {
            for (InscricaoPrograma ip : listaDeProgramasInscritos) {
                if (programa.getId() == ip.getPrograma().getId()) {
                    verificar = false;
                    break;
                }
            }
        }

        return verificar;
    }

    private InscricaoPrograma recuperarInscricaoDoPrograma(Programa programa) {
        if (listaDeProgramasInscritos.size() > 0) {
            for (InscricaoPrograma ip : listaDeProgramasInscritos) {
                if (programa.getId() == ip.getPrograma().getId()) {
                    return ip;
                }
            }
        }
        return new InscricaoPrograma();
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
