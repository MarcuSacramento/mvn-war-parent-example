package br.gov.mj.side.web.view.dashboard.paineis;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.StringValidator;

import br.gov.mj.infra.negocio.exception.BusinessException;
import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraAjaxConfirmButton;
import br.gov.mj.infra.wicket.component.InfraLocalDateTextField;
import br.gov.mj.infra.wicket.component.pagination.InfraAjaxPagingNavigator;
import br.gov.mj.seg.entidades.Usuario;
import br.gov.mj.seg.entidades.enums.EnumTipoUsuario;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.entidade.Pessoa;
import br.gov.mj.side.entidades.entidade.PessoaEntidade;
import br.gov.mj.side.entidades.enums.EnumSiglaSistema;
import br.gov.mj.side.entidades.enums.EnumStatusPessoa;
import br.gov.mj.side.entidades.enums.EnumTipoPessoa;
import br.gov.mj.side.web.dto.EntidadeDto;
import br.gov.mj.side.web.dto.EntidadePesquisaDto;
import br.gov.mj.side.web.service.BeneficiarioService;
import br.gov.mj.side.web.service.SegurancaService;
import br.gov.mj.side.web.util.CPFUtils;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.EmailValidator;
import br.gov.mj.side.web.view.dashboard.DashboardInfoEntidadePage;

public class PanelDadosTitularDashboard extends Panel {
    private static final long serialVersionUID = 1L;

    private static final String ONCHANGE = "onchange";

    private Page backPage;
    private DashboardInfoEntidadePage page;

    private PanelSomenteCpf panelSomenteCpf;
    private PanelPrincipalTitular panelPrincipalTitular;
    private PanelDataView panelDataView;

    private AjaxButton buttonAdicionar;
    private AjaxButton buttonSalvarEdicao;
    private AjaxSubmitLink buttonCancelarEdicao;
    private AjaxCheckBox checkRepresentante;
    private AjaxCheckBox checkAtivo;
    private Label labelAtivar;
    private DataView<PessoaEntidade> dataView;
    private DropDownChoice<Boolean> dropDownChoice;
    private DropDownChoice<Boolean> dropDownRepresentante;
    private TextField<String> fieldCpf;
    private Label labelMensagem;
    private Label lblNumeroCpf;
    private AjaxButton buttonVerificarCpf;
    private Model<String> mensagem = Model.of("");

    private EntidadeDto entidadeDto = new EntidadeDto();
    private Pessoa entidadeDtoTemp = new Pessoa();
    private Pessoa pessoaEncontradaPelaDigitacaoCpf = new Pessoa();

    private boolean modoEdicao = false;
    private boolean mostrarBotaoCancelarEdicao = false;
    private boolean mostrarBotaoEditarTitular = false;
    private int posicaoPessoaLista;
    private String cpfTemporario = "";
    private String msg = "";

    private List<PessoaEntidade> listaDePessoas = new ArrayList<PessoaEntidade>();

    private String nomePessoa;
    private String numeroCpf;
    private String descricaoCargo;
    private String numeroTelefone;
    private String email;
    private LocalDate dataInicioExercicio;
    private LocalDate dataFimExercicio;
    private String enderecoCorrespondencia;
    private Boolean titularRepresentante = null;
    private Boolean ativo = null;
    private Boolean cadastroNovo;
    private Boolean botaoEditarClicado = false;
    private Boolean mostrarBotaoAdicionarRepresentante = true;
    private Boolean mostrarBotaoVerificarCpf = true;
    private Boolean mostrarTextFieldVerificarCpf = true;
    private Boolean mostrarTextFieldEmail = true;
    private Boolean dadoAindaNaoSalvoNoBanco = true;

    @Inject
    private ComponentFactory componentFactory;

    @Inject
    private BeneficiarioService beneficiarioService;

    @Inject
    private SegurancaService segurancaService;

    public PanelDadosTitularDashboard(String id, Page backPage) {
        super(id);
        setOutputMarkupId(true);
        this.backPage = backPage;

        initVariaveis();
        add(panelPrincipalTitular = new PanelPrincipalTitular("panelPrincipalTitular"));
        add(panelSomenteCpf = new PanelSomenteCpf("panelSomenteCpf"));
        add(panelDataView = new PanelDataView("panelDataView"));

        panelPrincipalTitular.setEnabled(false);
        buttonAdicionar.setVisible(false);
    }

    private class PanelSomenteCpf extends WebMarkupContainer {

        public PanelSomenteCpf(String id) {
            super(id);
            setOutputMarkupId(true);

            labelMensagem = new Label("mensagemAnexo", mensagem);
            labelMensagem.setEscapeModelStrings(false);
            add(labelMensagem);

            add(getTextFieldCpf()); // entidadeRepresentante.numeroCpf
            add(getButtonVerificarCpf()); // btnVerificarCpf
        }
    }

    private class PanelPrincipalTitular extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelPrincipalTitular(String id) {
            super(id);
            setOutputMarkupId(true);

            add(getTextFieldNome()); // entidadeTitular.nomePessoa
            add(getTextFieldCargo()); // entidadeTitular.descricaoCargo
            add(getTextFieldTelefone()); // entidadeTitular.numeroTelefone
            add(getTextFieldEmail()); // entidadeTitular.email
            add(getDateTextFieldPeriodo1()); // entidadeTitular.dataInicioExercicio
            add(getDateTextFieldPeriodo2()); // entidadeTitular.dataFimExercicio
            add(getTextFieldEndereco()); // entidadeTitular.enderecoCorrespondencia
            add(getDropDownAtivarDesativarBeneficiario()); // dropAtivo
            add(getDropDownRepresentante()); // dropRepresentante
            add(getLabelAtivar()); // lblAtivar

        }
    }

    private class PanelDataView extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelDataView(String id) {
            super(id);
            add(getDataViewResultado()); // dataTitulares
            add(new InfraAjaxPagingNavigator("pagination", dataView));
            add(getButtonAdicionar()); // btnAdicionar
            add(getButtonCancelarEdicao()); // btnCancelarEdicao
            add(getButtonSalvarEdicao()); // btnSalvarEdicao
        }
    }

    private void initVariaveis() {
        page = (DashboardInfoEntidadePage) backPage;
        entidadeDto = page.getForm().getModelObject();

        // Se estiver editando o titular
        if (entidadeDto.getId() != null) {
            Entidade entidadeBuscar = new Entidade();
            entidadeBuscar.setId(entidadeDto.getId());
            listaDePessoas = beneficiarioService.buscarTitularEntidade(entidadeBuscar);

            // é necessário inicializar esta variavel para que os campos de
            // input do titular venham todos em branco
            entidadeDto.setEntidadeTitular(new Pessoa());
        }

    }

    private TextField<String> getTextFieldNome() {
        TextField<String> fieldNome = componentFactory.newTextField("entidadeTitular.nomePessoa", "Nome (Titular)", false, new PropertyModel(this, "nomePessoa"));
        fieldNome.add(StringValidator.maximumLength(200));
        actionTextField(fieldNome);
        return fieldNome;
    }

    private TextField<String> getTextFieldCpf() {

        fieldCpf = componentFactory.newTextField("entidadeTitular.numeroCpf", "CPF (Titular)", false, new PropertyModel(this, "numeroCpf"));
        fieldCpf.add(StringValidator.maximumLength(14));
        actionTextField(fieldCpf);
        fieldCpf.setOutputMarkupId(true);
        //Quando puder editar retirar o comentário abaixo
        fieldCpf.setEnabled(false);
        //fieldCpf.setEnabled(mostrarTextFieldVerificarCpf);
        return fieldCpf;
    }

    private TextField<String> getTextFieldCargo() {

        TextField<String> field = componentFactory.newTextField("entidadeTitular.descricaoCargo", "Cargo (Titular)", false, new PropertyModel(this, "descricaoCargo"));
        field.add(StringValidator.maximumLength(200));
        actionTextField(field);
        return field;
    }

    private TextField<String> getTextFieldTelefone() {
        TextField<String> field = componentFactory.newTextField("entidadeTitular.numeroTelefone", "Telefone (Titular)", false, new PropertyModel(this, "numeroTelefone"));
        field.add(StringValidator.maximumLength(13));
        actionTextField(field);
        return field;
    }

    private TextField<String> getTextFieldEmail() {

        TextField<String> field = componentFactory.newTextField("entidadeTitular.email", "E-Mail (Titular)", false, new PropertyModel(this, "email"));
        field.add(StringValidator.maximumLength(200));
        actionTextField(field);
        field.setEnabled(mostrarTextFieldEmail);
        return field;
    }

    private InfraLocalDateTextField getDateTextFieldPeriodo1() {
        InfraLocalDateTextField field = componentFactory.newDateTextFieldWithDatePicker("entidadeTitular.dataInicioExercicio", "Período de Mandato (Inicial)", false, new PropertyModel(this, "dataInicioExercicio"), "dd/MM/yyyy", "pt-BR");
        return field;
    }

    private InfraLocalDateTextField getDateTextFieldPeriodo2() {
        InfraLocalDateTextField field = componentFactory.newDateTextFieldWithDatePicker("entidadeTitular.dataFimExercicio", "Período de Mandato (Final)", false, new PropertyModel(this, "dataFimExercicio"), "dd/MM/yyyy", "pt-BR");
        return field;
    }

    private TextField<String> getTextFieldEndereco() {
        TextField<String> field = componentFactory.newTextField("entidadeTitular.enderecoCorrespondencia", "Endereço de Correspondência (Titular)", false, new PropertyModel(this, "enderecoCorrespondencia"));
        field.add(StringValidator.maximumLength(200));
        actionTextField(field);
        return field;
    }

    private Label getLabelAtivar() {
        labelAtivar = new Label("lblAtivar", "* Ativar Titular");
        labelAtivar.setVisible(true);
        return labelAtivar;
    }

    private DataView<PessoaEntidade> getDataViewResultado() {
        dataView = new DataView<PessoaEntidade>("dataTitulares", new TitularProvider()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<PessoaEntidade> item) {

                String cpf= CPFUtils.format(item.getModelObject().getPessoa().getNumeroCpf());
                String cpfMascarado=CPFUtils.mascararCpf(cpf,'*',3);        
                        
                item.add(new Label("pessoa.numeroCpf", cpfMascarado));
                item.add(new Label("pessoa.nomePessoa"));
                item.add(new Label("pessoa.statusPessoa", item.getModelObject().getPessoa().getStatusPessoa().getDescricao()));
                item.add(new Label("pessoa.descricaoCargo"));

                String dataInicial = dataDocumentoBR(item.getModelObject().getPessoa().getDataInicioExercicio());
                String dataFinal;
                if (item.getModelObject().getPessoa().getDataFimExercicio() != null) {
                    dataFinal = " a " + dataDocumentoBR(item.getModelObject().getPessoa().getDataFimExercicio());
                } else {
                    dataFinal = "";
                }
                item.add(new Label("exercicio", dataInicial + dataFinal));

                item.add(getButtonEditar(item, false));
                item.add(getButtonVisualizar(item, true));
                dadoAindaNaoSalvoNoBanco = item.getModelObject().getId() == null;
                item.add(getButtonExcluir(item).setVisible(dadoAindaNaoSalvoNoBanco));

            }
        };
        dataView.setItemsPerPage(Constants.ITEMS_PER_PAGE_PAGINATION);
        dataView.setOutputMarkupId(true);
        return dataView;
    }

    public Button getButtonAdicionar() {
        buttonAdicionar = new AjaxButton("btnAdicionar") {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                adicionar(target);
            }
        };
        buttonAdicionar.setVisible(ativarOuDesativarBotaoDeInserirNovosTitulares());
        return buttonAdicionar;
    }

    public AjaxSubmitLink getButtonEditar(Item<PessoaEntidade> item, Boolean modoVisualizar) {
        AjaxSubmitLink button = new AjaxSubmitLink("btnEditar") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form form) {
                acaoVisualizarEditar(target, item, modoVisualizar);
            }
        };
        //Quando puder editar retirar o comentário abaixo
        button.setVisible(false);
        button.setDefaultFormProcessing(false);
        return button;
    }

    public AjaxSubmitLink getButtonVisualizar(Item<PessoaEntidade> item, boolean modoVisualizar) {
        AjaxSubmitLink button = new AjaxSubmitLink("btnVisualizar") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form form) {
                acaoVisualizarEditar(target, item, modoVisualizar);
            }
        };
        button.setDefaultFormProcessing(false);
        return button;
    }

    public AjaxButton getButtonSalvarEdicao() {
        buttonSalvarEdicao = new AjaxButton("btnSalvarEdicao") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                salvarEdicao(target);
            }
        };
        //Quando puder editar retirar o comentário abaixo
        buttonSalvarEdicao.setVisible(false);
        //buttonSalvarEdicao.setVisible(mostrarBotaoEditarTitular);
        buttonSalvarEdicao.setOutputMarkupId(true);
        return buttonSalvarEdicao;
    }

    public AjaxSubmitLink getButtonCancelarEdicao() {
        buttonCancelarEdicao = new AjaxSubmitLink("btnCancelarEdicao") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form form) {
                cancelarEdicao(target);
            }
        };
        buttonCancelarEdicao.setDefaultFormProcessing(false);
        buttonCancelarEdicao.setVisible(mostrarBotaoCancelarEdicao);
        buttonCancelarEdicao.setOutputMarkupId(true);
        return buttonCancelarEdicao;
    }

    public InfraAjaxConfirmButton getButtonExcluir(Item<PessoaEntidade> item) {
        return componentFactory.newAJaxConfirmButton("btnExcluir", "MSG001", null, (target, formz) -> excluirTitular(target, item));
    }

    private DropDownChoice<Boolean> getDropDownAtivarDesativarBeneficiario() {
        dropDownChoice = new DropDownChoice<Boolean>("dropAtivo", new PropertyModel(this, "ativo"), Arrays.asList(Boolean.TRUE, Boolean.FALSE));
        dropDownChoice.setLabel(Model.of("Ativar Usuário"));
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
                // acaoDrop(target);
            }

        });
        dropDownChoice.setVisible(true);

        return dropDownChoice;
    }

    private DropDownChoice<Boolean> getDropDownRepresentante() {
        dropDownRepresentante = new DropDownChoice<Boolean>("dropRepresentante", new PropertyModel(this, "titularRepresentante"), Arrays.asList(Boolean.TRUE, Boolean.FALSE));
        dropDownRepresentante.setLabel(Model.of("Ativar Usuário"));
        dropDownRepresentante.setNullValid(true);
        dropDownRepresentante.setChoiceRenderer(new IChoiceRenderer<Boolean>() {
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

        dropDownRepresentante.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1L;

            protected void onUpdate(AjaxRequestTarget target) {
                // acaoDrop(target);
            }

        });

        return dropDownRepresentante;
    }

    public Button getButtonVerificarCpf() {
        buttonVerificarCpf = new AjaxButton("btnVerificarCpf") {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                verificarCpf(target);
            }
        };
        
        //Quando puder editar retirar o comentário abaixo
        buttonVerificarCpf.setVisible(false);
        //buttonVerificarCpf.setVisible(mostrarBotaoVerificarCpf);
        buttonVerificarCpf.setOutputMarkupId(true);
        return buttonVerificarCpf;
    }

    // AÇÕES

    private void actionTextField(TextField<String> field) {
        AjaxFormComponentUpdatingBehavior onChangeAjaxBehavior = new AjaxFormComponentUpdatingBehavior(ONCHANGE) {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                // setar no Model
            }
        };
        field.add(onChangeAjaxBehavior);
    }

    public void adicionar(AjaxRequestTarget target) {

        if (!validarEditarPessoa(target)) {
            return;
        }

        modoEdicao = false;

        Pessoa entidade = new Pessoa();

        if (pessoaEncontradaPelaDigitacaoCpf != null && pessoaEncontradaPelaDigitacaoCpf.getId() == null) {
            
            entidade=setarValoresNaEntidade(entidade);
            
        } else {
            entidade = pessoaEncontradaPelaDigitacaoCpf;
            entidade=setarValoresNaEntidade(entidade);
        }

        EnumStatusPessoa statusPessoa;
        if (cadastroNovo) {
            statusPessoa = EnumStatusPessoa.ATIVO;
        } else {
            statusPessoa = dropDownChoice.getModelObject() ? EnumStatusPessoa.ATIVO : EnumStatusPessoa.INATIVO;
        }

        entidade.setStatusPessoa(statusPessoa);
        PessoaEntidade pe = new PessoaEntidade();
        pe.setPessoa(entidade);
        listaDePessoas.add(pe);

        panelPrincipalTitular.setEnabled(false);
        mostrarBotaoAdicionarRepresentante = false;
        mostrarBotaoCancelarEdicao = false;
        mostrarBotaoEditarTitular = false;
        mostrarBotaoVerificarCpf = true;
        mostrarTextFieldEmail = true;
        mostrarTextFieldVerificarCpf = true;
        modoEdicao = true;
        zerarCamposDadosBasicos();
        buttonAdicionar.setVisible(ativarOuDesativarBotaoDeInserirNovosTitulares());
        atualizarInputs(target);
    }

    public void salvarEdicao(AjaxRequestTarget target) {

        numeroCpf = entidadeDtoTemp.getNumeroCpf();
        if (!validarEditarPessoa(target)) {
            return;
        }

        entidadeDtoTemp.setNomePessoa(nomePessoa);
        entidadeDtoTemp.setNumeroCpf(numeroCpf);
        entidadeDtoTemp.setDescricaoCargo(descricaoCargo);
        entidadeDtoTemp.setNumeroTelefone(numeroTelefone);
        entidadeDtoTemp.setEmail(email);
        entidadeDtoTemp.setDataInicioExercicio(dataInicioExercicio);
        entidadeDtoTemp.setDataFimExercicio(dataFimExercicio);
        entidadeDtoTemp.setEnderecoCorrespondencia(enderecoCorrespondencia);
        entidadeDtoTemp.setPossuiFuncaoDeRepresentante(titularRepresentante);
        entidadeDtoTemp.setTipoPessoa(EnumTipoPessoa.TITULAR);

        EnumStatusPessoa statusPessoa = dropDownChoice.getModelObject() ? EnumStatusPessoa.ATIVO : EnumStatusPessoa.INATIVO;
        entidadeDtoTemp.setStatusPessoa(statusPessoa);

        modoEdicao = false;
        botaoEditarClicado = false;

        panelPrincipalTitular.setEnabled(false);
        zerarCamposDadosBasicos();
        mostrarBotaoVerificarCpf = true;
        mostrarTextFieldVerificarCpf = true;
        mostrarTextFieldEmail = false;
        mostrarBotaoAdicionarRepresentante = false;
        mostrarBotaoCancelarEdicao = false;
        mostrarBotaoEditarTitular = false;

        buttonAdicionar.setVisible(ativarOuDesativarBotaoDeInserirNovosTitulares());
        atualizarInputs(target);
    }

    public void cancelarEdicao(AjaxRequestTarget target) {
        modoEdicao = false;
        mostrarBotaoCancelarEdicao = false;
        mostrarBotaoEditarTitular = false;
        botaoEditarClicado = false;

        panelPrincipalTitular.setEnabled(false);
        zerarCamposDadosBasicos();
        mostrarBotaoAdicionarRepresentante = false;
        mostrarBotaoVerificarCpf = true;
        mostrarTextFieldVerificarCpf = true;
        mostrarTextFieldEmail = true;
        mostrarBotaoCancelarEdicao = false;
        mostrarBotaoEditarTitular = false;
        atualizarInputs(target);
    }

    public void excluirTitular(AjaxRequestTarget target, Item<PessoaEntidade> item) {
        Pessoa itemSelecionadoExcluir = item.getModelObject().getPessoa();

        for (PessoaEntidade entidade : listaDePessoas) {
            if (entidade.getPessoa().getNumeroCpf().equalsIgnoreCase(itemSelecionadoExcluir.getNumeroCpf())) {
                listaDePessoas.remove(entidade);
                break;
            }
        }

        modoEdicao = false;
        botaoEditarClicado = false;
        panelPrincipalTitular.setEnabled(false);
        zerarCamposDadosBasicos();
        mostrarBotaoVerificarCpf = true;
        mostrarTextFieldVerificarCpf = true;
        mostrarTextFieldEmail = true;
        mostrarBotaoAdicionarRepresentante = false;
        mostrarBotaoCancelarEdicao = false;
        mostrarBotaoEditarTitular = false;
        atualizarInputs(target);
    }

    public void zerarCamposDadosBasicosSemCpf() {
        nomePessoa = "";
        descricaoCargo = "";
        numeroTelefone = "";
        email = "";
        dataInicioExercicio = null;
        dataFimExercicio = null;
        enderecoCorrespondencia = "";
        ativo = null;
    }

    public void zerarCamposDadosBasicos() {
        nomePessoa = "";
        numeroCpf = "";
        descricaoCargo = "";
        numeroTelefone = "";
        email = "";
        dataInicioExercicio = null;
        dataFimExercicio = null;
        enderecoCorrespondencia = "";
        titularRepresentante = null;
        ativo = null;
    }

    private void atualizarInputs(AjaxRequestTarget target) {
        panelSomenteCpf.addOrReplace(getTextFieldCpf());
        panelSomenteCpf.addOrReplace(getButtonVerificarCpf());
        panelPrincipalTitular.addOrReplace(getTextFieldNome());
        panelPrincipalTitular.addOrReplace(getTextFieldCargo());
        panelPrincipalTitular.addOrReplace(getTextFieldTelefone());
        panelPrincipalTitular.addOrReplace(getTextFieldEmail());
        panelPrincipalTitular.addOrReplace(getDateTextFieldPeriodo1());
        panelPrincipalTitular.addOrReplace(getDateTextFieldPeriodo2());
        panelPrincipalTitular.addOrReplace(getTextFieldEndereco());
        panelPrincipalTitular.addOrReplace(getDropDownAtivarDesativarBeneficiario());
        panelPrincipalTitular.addOrReplace(getDropDownRepresentante());
        panelDataView.addOrReplace(getButtonCancelarEdicao());
        panelDataView.addOrReplace(getButtonSalvarEdicao());
        panelDataView.addOrReplace(getButtonAdicionar());
        mensagem.setObject("");

        target.add(panelSomenteCpf);
        target.add(panelPrincipalTitular);
        target.add(panelDataView);
        target.add(labelMensagem);
    }

    // O InputText do E-Mail somente será editavel se a propria pessoa logada
    // clicar nele.
    private boolean mostrarInputTextDeEmail(Pessoa usarioClicado) {
        boolean mostrar = true;
        Usuario usuarioLogado = page.receberUsuarioLogado();
        
        
        if (usarioClicado.getId() == null || verificarSeOUsuarioLogadoPossuiFuncaoDeUsuarioInterno(usuarioLogado)) {
            mostrar = true;
        } else {
            if (usarioClicado == null || usarioClicado.getUsuario() == null) {
                mostrar = false;
            } else {
                if (usuarioLogado.getId() == usarioClicado.getUsuario().getId()) {
                    mostrar = true;
                } else {
                    mostrar = false;
                }
            }
        }
        return mostrar;
    }

    // O botão somente irá aparecer se não houverem titulares cadastrados e se
    // houver algum que todos estejam inativos, pois assim pode-se adicionar
    // outro ativo.
    private boolean ativarOuDesativarBotaoDeInserirNovosTitulares() {
        boolean mostrarBotaoAdicionarTitular = true;
        if (botaoEditarClicado) {
            mostrarBotaoAdicionarTitular = false;
        } else {
            for (PessoaEntidade enti : listaDePessoas) {
                if (enti.getPessoa().getStatusPessoa() == EnumStatusPessoa.ATIVO) {
                    mostrarBotaoAdicionarTitular = false;
                    break;
                }
            }
        }
        return mostrarBotaoAdicionarTitular;
    }

    public boolean validarEditarPessoa(AjaxRequestTarget target) {
        boolean validar = true;
        msg = "";

        validar = validarCpfCompleto(validar);
        validar = validarTitular(validar);
        validar = validarCamposObrigatorios(validar);
        validar = validarEmailUnico(validar);

        if (!validar) {
            mensagem.setObject(msg);
        } else {
            mensagem.setObject("");
        }
        target.add(labelMensagem);

        return validar;
    }

    public boolean validarCpfCompleto(boolean validar) {
        if (numeroCpf == null || "".equalsIgnoreCase(numeroCpf)) {
            msg += "<p><li> O Campo CPF é obrigatório.</li><p />";
            validar = false;
        } else {
            if (compararCpfsCadastradosDosDoisPaineis()) {
                if (validar) {
                    int i = 0;
                    int contador = 0;
                    for (PessoaEntidade b : listaDePessoas) {
                        String cpfLista = CPFUtils.clean(b.getPessoa().getNumeroCpf());

                        if (CPFUtils.clean(numeroCpf).equalsIgnoreCase(cpfLista)) {
                            if (posicaoPessoaLista != contador) {
                                msg += "<p><li> O campo 'CPF' informado já esta em uso.</li><p />";
                                validar = false;
                                i++;
                            }
                        }
                        contador++;
                    }

                    if (i > 0) {
                        validar = false;
                    } else {
                        validar = validarCPf(numeroCpf, validar);
                    }
                }
            } else {
                validar = false;
            }
        }

        return validar;
    }

    public boolean validarTitular(boolean validar) {
        if (numeroTelefone != null && numeroTelefone.length() < 10) {
            msg += "<p><li> O Telefone do Titular deverá conter ao menos 10 números.</li><p />";
            validar = false;
        }

        if (email != null && !EmailValidator.validate(email)) {
            msg += "<p><li> O Email do Titular está em um formato inválido.</li><p />";
            validar = false;
        }
        
        if(listaDePessoas!=null && listaDePessoas.size()>0)
        {
            if(dropDownChoice.getModelObject()==true)
            {
                for(PessoaEntidade pe:listaDePessoas)
                {
                    if(pe.getPessoa().getStatusPessoa()==EnumStatusPessoa.ATIVO)
                    {
                        if(entidadeDtoTemp.getId()!= pe.getPessoa().getId())
                        {
                            msg += "<p><li> Não é possível tornar este Titular ativo pois já existe outro em atividade.</li><p />";
                            validar = false;
                            break;
                        }
                    }
                }
            }
        }
        
        return validar;
    }

    private boolean validarCamposObrigatorios(boolean validar) {
        if (nomePessoa == null || "".equalsIgnoreCase(nomePessoa)) {
            msg += "<p><li> O campo 'Nome' é obrigatório.</li><p />";
            validar = false;
        }

        if (numeroCpf == null || "".equalsIgnoreCase(numeroCpf)) {
            msg += "<p><li> O campo 'CPF' é obrigatório.</li><p />";
            validar = false;
        }

        if (descricaoCargo == null || "".equalsIgnoreCase(descricaoCargo)) {
            msg += "<p><li> O campo 'Cargo' é obrigatório.</li><p />";
            validar = false;
        }

        if (numeroTelefone == null || "".equalsIgnoreCase(numeroTelefone)) {
            msg += "<p><li> O campo 'Telefone' é obrigatório.</li><p />";
            validar = false;
        }

        if (email == null || "".equalsIgnoreCase(email)) {
            msg += "<p><li> O campo 'E-Mail' é obrigatório.</li><p />";
            validar = false;
        }

        int datasInseridas = 2;
        if (dataInicioExercicio == null) {

            datasInseridas--;
            msg += "<p><li> O campo 'Período de Mandato (Inicial)' é obrigatório.</li><p />";
            validar = false;
        }

        if (dataInicioExercicio != null && dataFimExercicio != null) {
            if (dataInicioExercicio.isAfter(dataFimExercicio)) {
                msg += "<p><li> O Período de Mandato Inicial não poderá ser superior ao Período de Mandato Final.</li><p />";
                validar = false;
            }
        }

        if (enderecoCorrespondencia == null || "".equalsIgnoreCase(enderecoCorrespondencia)) {
            msg += "<p><li> O campo 'Endereço de Correspondência' é obrigatório.</li><p />";
            validar = false;
        }

        if (!cadastroNovo) {
            if (ativo == null) {
                msg += "<p><li> O campo 'Ativar Representante' é obrigatório.</li><p />";
                validar = false;
            }
        }

        if (titularRepresentante == null) {
            msg += "<p><li> O campo 'Tornar o Titular também Representante' é obrigatório.</li><p />";
            validar = false;
        }

        return validar;
    }

    private boolean validarEmailUnico(boolean validar) {

        // Irá validar se este e-mail é unico entre os dataViews da tela
        validar = validarEmailUnicoNosDataViews(validar);
        if (!validar) {
            validar = false;
            return validar;
        }

        if(pessoaEncontradaPelaDigitacaoCpf == null || pessoaEncontradaPelaDigitacaoCpf.getId() == null)
        {
         // Irá verificar se este email já esta cadastrado na tabela de Pessoas
            if (!validarEmailUnicoNoSalvoNoBancoDeDados(validar)) {
                validar = false;
                return validar;
            }

            // Irá verificar se este email já esta cadastrado na tabela de Usuarios.
            if (!validarEmailUnicoSalvoNaTabelaDeUsuariosDoBancoDeDados(validar)) {
                validar = false;
                return validar;
            }
        }
        
        return validar;
    }

    private boolean validarEmailUnicoNosDataViews(boolean validar) {
        List<PessoaEntidade> outraListaPessoa = page.getPanelDadosRepresentante().getListaDePessoas();
        for (PessoaEntidade entidadeExterna : outraListaPessoa) {
            if (entidadeExterna.getPessoa().getEmail().equalsIgnoreCase(email)) {
                if (entidadeExterna.getPessoa().getId() != entidadeDtoTemp.getId()) {
                    msg += "<p><li> O E-Mail informado já esta cadastrado na lista de Representantes.</li><p />";
                    validar = false;
                    break;
                }
            }
        }
        return validar;
    }

    private boolean validarEmailUnicoNoSalvoNoBancoDeDados(boolean validar) {
        EntidadePesquisaDto pesquisaEmail = new EntidadePesquisaDto();
        Pessoa pessoa = new Pessoa();
        pessoa.setEmail(email);
        pesquisaEmail.setRepresentante(pessoa);
        pesquisaEmail.setUsuarioLogado(page.getUsuarioLogadoDaSessao());
        List<Entidade> lista = beneficiarioService.buscarSemPaginacao(pesquisaEmail);
        if (lista.size() > 0) {
            boolean encontrado=false;
            for (Entidade ent : lista) {
                for (PessoaEntidade pe : ent.getPessoas()) {
                    String emailEncontrado = pe.getPessoa().getEmail();

                    if (emailEncontrado.equalsIgnoreCase(email)) {
                        if (entidadeDtoTemp == null || pe.getPessoa().getId() != entidadeDtoTemp.getId()) {
                            msg += "<p><li> O E-Mail Informado já esta cadastrado no sistema.</li><p />";
                            validar = false;
                            encontrado=true;
                            break;
                        }
                    }
                }
                if(encontrado)
                {
                    break;
                }
            }
        }
        return validar;
    }

    private boolean validarEmailUnicoSalvoNaTabelaDeUsuariosDoBancoDeDados(boolean validar) {
        try {
            Usuario usuarioEncontrado = segurancaService.buscarUsuarioPeloEmail(email, EnumSiglaSistema.SIDE.getValor());
            if (usuarioEncontrado.getId() != entidadeDtoTemp.getUsuario().getId()) {
                validar = false;
                msg += "<p><li> O E-Mail Informado já esta cadastrado no sistema.</li><p />";
            }
        } catch (BusinessException be) {
            return validar;
        }
        return validar;
    }
    
    private boolean verificarSeOUsuarioLogadoPossuiFuncaoDeUsuarioInterno(Usuario usuario)
    {
            if(usuario==null)
            {
                return false;
            }
            else
            {
                if(usuario.getTipoUsuario()==EnumTipoUsuario.INTERNO)
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
    }

    private void acaoVisualizarEditar(AjaxRequestTarget target, Item<PessoaEntidade> item, Boolean modoVisualizar) {
        modoEdicao = true;
        entidadeDtoTemp = item.getModelObject().getPessoa();

        nomePessoa = entidadeDtoTemp.getNomePessoa();
        String numeroCpfPrimario= new String(CPFUtils.format(entidadeDtoTemp.getNumeroCpf()));
        numeroCpf=CPFUtils.mascararCpf(numeroCpfPrimario,'*',3); 
        descricaoCargo = entidadeDtoTemp.getDescricaoCargo();
        numeroTelefone = entidadeDtoTemp.getNumeroTelefone();
        email = entidadeDtoTemp.getEmail();
        dataInicioExercicio = entidadeDtoTemp.getDataInicioExercicio();
        dataFimExercicio = entidadeDtoTemp.getDataFimExercicio();
        enderecoCorrespondencia = entidadeDtoTemp.getEnderecoCorrespondencia();
        titularRepresentante = entidadeDtoTemp.getPossuiFuncaoDeRepresentante();

        boolean isAtivo = entidadeDtoTemp.getStatusPessoa().getValor().equalsIgnoreCase("A") ? true : false;
        ativo = isAtivo;

        cpfTemporario = entidadeDtoTemp.getNumeroCpf();
        posicaoPessoaLista = item.getIndex();
        botaoEditarClicado = true;

        mostrarBotaoCancelarEdicao = true;
        mostrarBotaoVerificarCpf = false;
        mostrarTextFieldVerificarCpf = false;
        mostrarTextFieldEmail = mostrarInputTextDeEmail(entidadeDtoTemp);
        mostrarBotaoAdicionarRepresentante = false;
        buttonAdicionar.setVisible(ativarOuDesativarBotaoDeInserirNovosTitulares());

        if (modoVisualizar) {
            panelPrincipalTitular.setEnabled(false);
            mostrarBotaoEditarTitular = false;
        } else {
            panelPrincipalTitular.setEnabled(true);
            mostrarBotaoEditarTitular = true;
        }

        atualizarInputs(target);
    }

    private void acaoVisualizar(AjaxRequestTarget target, Item<PessoaEntidade> item, Boolean modoVisualizar) {
        modoEdicao = true;
        entidadeDtoTemp = item.getModelObject().getPessoa();

        nomePessoa = entidadeDtoTemp.getNomePessoa();
        numeroCpf = entidadeDtoTemp.getNumeroCpf();
        descricaoCargo = entidadeDtoTemp.getDescricaoCargo();
        numeroTelefone = entidadeDtoTemp.getNumeroTelefone();
        email = entidadeDtoTemp.getEmail();
        dataInicioExercicio = entidadeDtoTemp.getDataInicioExercicio();
        dataFimExercicio = entidadeDtoTemp.getDataFimExercicio();
        enderecoCorrespondencia = entidadeDtoTemp.getEnderecoCorrespondencia();

        boolean isAtivo = entidadeDtoTemp.getStatusPessoa().getValor().equalsIgnoreCase("A") ? true : false;
        ativo = isAtivo;

        cpfTemporario = entidadeDtoTemp.getNumeroCpf();
        posicaoPessoaLista = item.getIndex();

        mostrarBotaoVerificarCpf = false;
        mostrarTextFieldVerificarCpf = false;
        mostrarBotaoAdicionarRepresentante = false;

        if (modoVisualizar) {
            panelPrincipalTitular.setEnabled(false);
        }
        atualizarInputs(target);
    }

    private void acaoDrop(AjaxRequestTarget target) {
        ativo = dropDownChoice.getModelObject();
    }

    public String dataDocumentoBR(LocalDate dataDocumento) {
        DateTimeFormatter sdfPadraoBR = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        if (dataDocumento != null) {
            dataDocumento.format(sdfPadraoBR);
            return sdfPadraoBR.format(dataDocumento);
        }
        return " - ";
    }

    // PROVIDER

    private class TitularProvider extends SortableDataProvider<PessoaEntidade, String> {
        private static final long serialVersionUID = 1L;

        public TitularProvider() {
            // setSort("nomeCriterio", SortOrder.ASCENDING);
        }

        @Override
        public Iterator<PessoaEntidade> iterator(long first, long size) {

            List<PessoaEntidade> listTemp = new ArrayList<PessoaEntidade>();
            int firstTemp = 0;
            int flagTemp = 0;
            for (PessoaEntidade k : listaDePessoas) {
                if (firstTemp >= first) {
                    if (flagTemp <= size) {
                        listTemp.add(k);
                        flagTemp++;
                    }
                }
                firstTemp++;
            }

            return listTemp.iterator();
        }

        @Override
        public long size() {
            return listaDePessoas.size();
        }

        @Override
        public IModel<PessoaEntidade> model(PessoaEntidade object) {
            return new CompoundPropertyModel<PessoaEntidade>(object);
        }
    }

    public boolean validarCPf(String cpf, boolean validar) {
        boolean valido = validar;
        if (CPFUtils.clean(cpf).length() < 11) {
            msg += "<p><li> O 'CPF' deverá conter 11 digitos.</li><p />";
            valido = false;
        } else {
            if (!CPFUtils.validate(cpf)) {
                msg += "<p><li> O 'CPF' informado esta em um formato inválido.</li><p />";
                valido = false;
            }
        }
        return valido;
    }

    private boolean compararCpfsCadastradosDosDoisPaineis() {
        List<PessoaEntidade> outraListaPessoa = page.getPanelDadosRepresentante().getListaDePessoas();
        Boolean cpfUnico = true;
        for (PessoaEntidade entidadeExterna : outraListaPessoa) {
            if (CPFUtils.clean(entidadeExterna.getPessoa().getNumeroCpf()).equalsIgnoreCase(CPFUtils.clean(numeroCpf))) {
                if (entidadeExterna.getPessoa().getId() != entidadeDtoTemp.getId()) {
                    msg += "<p><li> O CPF informado já esta cadastrado na lista de Representantes.</li><p />";
                    cpfUnico = false;
                }
                break;
            }
            if (!cpfUnico) {
                break;
            }
        }
        return cpfUnico;
    }

    private void verificarCpf(AjaxRequestTarget target) {
        boolean validar = true;
        msg = "";
        if (!validarCpfCompleto(validar)) {
            mensagem.setObject(msg);
            target.add(labelMensagem);
            return;
        }
        mensagem.setObject("");
        target.add(labelMensagem);
        zerarCamposDadosBasicosSemCpf();

        Pessoa pessoa = new Pessoa();
        pessoa.setNumeroCpf(CPFUtils.clean(numeroCpf));

        EntidadePesquisaDto enti = new EntidadePesquisaDto();
        enti.setTitular(pessoa);
        enti.setUsuarioLogado(page.getUsuarioLogadoDaSessao());  
        List<Entidade> lista = beneficiarioService.buscarSemPaginacao(enti);

        EntidadePesquisaDto entiRepresentante = new EntidadePesquisaDto();
        entiRepresentante.setRepresentante(pessoa);
        entiRepresentante.setUsuarioLogado(page.getUsuarioLogadoDaSessao());  
        List<Entidade> listaDeRepresentantes = beneficiarioService.buscarSemPaginacao(entiRepresentante);

        if (listaDeRepresentantes.size() > 0) {

            for (Entidade entit : listaDeRepresentantes) {
                for (PessoaEntidade pe : entit.getPessoas()) {
                    if (pe.getPessoa().getNumeroCpf().equalsIgnoreCase(CPFUtils.clean(numeroCpf)) && (pe.getPessoa().getTipoPessoa() == EnumTipoPessoa.REPRESENTANTE_ENTIDADE || pe.getPessoa().getTipoPessoa() == EnumTipoPessoa.TITULAR && !pe.getPessoa().getPossuiFuncaoDeRepresentante())) {
                        msg += "<p><li> Esta pessoa não pode ser cadastrada como Titular pois já é Representante em outra Entidade.</li><p />";
                        mensagem.setObject(msg);
                        target.add(labelMensagem);
                        return;
                    }
                }
            }
        }

        pessoaEncontradaPelaDigitacaoCpf = new Pessoa();
        if (lista.size() > 0) {
            for (Entidade entit : lista) {
                for (PessoaEntidade result : entit.getPessoas()) {
                    if (CPFUtils.clean(result.getPessoa().getNumeroCpf()).equalsIgnoreCase(CPFUtils.clean(numeroCpf))) {
                        if (result.getPessoa().getStatusPessoa() == EnumStatusPessoa.ATIVO) {
                            String nomeEntidadeAtivo = entit.getNomeEntidade();
                            msg += "<p><li>Não é possível adicionar este Titular pois o mesmo já esta ativo na Entidade '" + nomeEntidadeAtivo + "'.</li><p />";
                            mensagem.setObject(msg);
                            target.add(labelMensagem);
                            return;
                        }
                        pessoaEncontradaPelaDigitacaoCpf = result.getPessoa();
                        nomePessoa = result.getPessoa().getNomePessoa();
                        descricaoCargo = result.getPessoa().getDescricaoCargo();
                        numeroTelefone = result.getPessoa().getNumeroTelefone();
                        email = result.getPessoa().getEmail();
                        dataInicioExercicio = result.getPessoa().getDataInicioExercicio();
                        dataFimExercicio = result.getPessoa().getDataFimExercicio();
                        enderecoCorrespondencia = result.getPessoa().getEnderecoCorrespondencia();

                        pessoaEncontradaPelaDigitacaoCpf = result.getPessoa();
                        break;
                    }
                }
            }
        }
        mostrarBotaoVerificarCpf = false;
        mostrarTextFieldEmail = mostrarInputTextDeEmail(entidadeDtoTemp);
        mostrarTextFieldVerificarCpf = false;
        mostrarBotaoAdicionarRepresentante = true;
        panelPrincipalTitular.setEnabled(true);
        mostrarBotaoCancelarEdicao = true;
        mostrarBotaoEditarTitular = false;

        atualizarInputs(target);
    }

    private Pessoa setarValoresNaEntidade(Pessoa entidade)
    {
        entidade.setNomePessoa(nomePessoa);
        entidade.setNumeroCpf(numeroCpf);
        entidade.setDescricaoCargo(descricaoCargo);
        entidade.setNumeroTelefone(numeroTelefone);
        entidade.setEmail(email);
        entidade.setDataInicioExercicio(dataInicioExercicio);
        entidade.setDataFimExercicio(dataFimExercicio);
        entidade.setEnderecoCorrespondencia(enderecoCorrespondencia);
        entidade.setPossuiFuncaoDeRepresentante(titularRepresentante);
        entidade.setTipoPessoa(EnumTipoPessoa.TITULAR);
        
        return entidade;
    }
    
    public static String formatCpf(String cpf) {
        StringBuilder builder = new StringBuilder(cpf.replaceAll("[^\\d]", ""));
        builder.insert(3, '.');
        builder.insert(7, '.');
        builder.insert(11, '-');
        return builder.toString();
    }

    public List<PessoaEntidade> getListaDePessoas() {
        return listaDePessoas;
    }

    public void setListaDePessoas(List<PessoaEntidade> listaDePessoas) {
        this.listaDePessoas = listaDePessoas;
    }

    public String getNomePessoa() {
        return nomePessoa;
    }

    public void setNomePessoa(String nomePessoa) {
        this.nomePessoa = nomePessoa;
    }

    public String getNumeroCpf() {
        return numeroCpf;
    }

    public void setNumeroCpf(String numeroCpf) {
        this.numeroCpf = numeroCpf;
    }

    public String getNumeroTelefone() {
        return numeroTelefone;
    }

    public void setNumeroTelefone(String numeroTelefone) {
        this.numeroTelefone = numeroTelefone;
    }
}
