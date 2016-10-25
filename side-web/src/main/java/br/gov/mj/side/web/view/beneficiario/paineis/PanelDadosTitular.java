package br.gov.mj.side.web.view.beneficiario.paineis;

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

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraAjaxConfirmButton;
import br.gov.mj.infra.wicket.component.InfraLocalDateTextField;
import br.gov.mj.infra.wicket.component.pagination.InfraAjaxPagingNavigator;
import br.gov.mj.seg.entidades.Usuario;
import br.gov.mj.seg.entidades.enums.EnumTipoUsuario;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.entidade.Pessoa;
import br.gov.mj.side.entidades.entidade.PessoaEntidade;
import br.gov.mj.side.entidades.enums.EnumOrigemCadastro;
import br.gov.mj.side.entidades.enums.EnumStatusPessoa;
import br.gov.mj.side.entidades.enums.EnumTipoPessoa;
import br.gov.mj.side.entidades.enums.EnumValidacaoCadastro;
import br.gov.mj.side.web.dto.EntidadeDto;
import br.gov.mj.side.web.dto.EntidadePesquisaDto;
import br.gov.mj.side.web.dto.UsuarioPessoaDto;
import br.gov.mj.side.web.service.BeneficiarioService;
import br.gov.mj.side.web.service.GenericEntidadeService;
import br.gov.mj.side.web.service.SegurancaService;
import br.gov.mj.side.web.service.UsuarioService;
import br.gov.mj.side.web.util.CPFUtils;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.EmailValidator;
import br.gov.mj.side.web.view.beneficiario.BeneficiarioPage;

public class PanelDadosTitular extends Panel {
    private static final long serialVersionUID = 1L;

    private static final String ONCHANGE = "onchange";

    private Page backPage;

    private PanelSomenteCpf panelSomenteCpf;
    private PanelPrincipalTitular panelPrincipalTitular;
    private PanelDataView panelDataView;
    private WebMarkupContainer containerBotaoAdicionarTitular;
    private WebMarkupContainer containerAtivarDesativar;

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
    private Label labelMensagemAtivarDesativar;
    private Label lblNumeroCpf;
    private AjaxButton buttonVerificarCpf;
    private Button buttonAdicionarTitular;
    private Model<String> mensagem = Model.of("");
    private Model<String> mensagemAtivarDesativar = Model.of("");

    private EntidadeDto entidadeDto = new EntidadeDto();
    private Pessoa entidadeDtoTemp = new Pessoa();
    private Pessoa pessoaEncontradaPelaDigitacaoCpf = new Pessoa();

    private boolean modoEdicao = false;
    private boolean mostrarBotaoCancelarEdicao = false;
    private boolean mostrarBotaoEditarTitular = false;
    private boolean readOnly;
    private Integer posicaoPessoaLista;
    private String cpfTemporario = "";
    private String msg = "";

    private List<PessoaEntidade> listaDeTitulares;
    private List<PessoaEntidade> listaDeRepresentantes;
    private List<PessoaEntidade> listaMembrosComissao;

    private Long idPessoaClicada;
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

    @Inject
    private GenericEntidadeService genericService;
    @Inject
    private UsuarioService usuarioService;

    public PanelDadosTitular(String id, Page backPage, Boolean cadastroNovo, EntidadeDto entidade, List<PessoaEntidade> listaDeTitulares, List<PessoaEntidade> listaDeRepresentantes, List<PessoaEntidade> listaMembrosComissao, boolean readOnly) {
        super(id);
        setOutputMarkupId(true);
        this.backPage = backPage;
        this.cadastroNovo = cadastroNovo;
        this.entidadeDto = entidade;
        this.listaDeTitulares = listaDeTitulares;
        this.listaDeRepresentantes = listaDeRepresentantes;
        this.listaMembrosComissao = listaMembrosComissao;
        this.readOnly = readOnly;

        initVariaveis();

        panelPrincipalTitular = new PanelPrincipalTitular("panelPrincipalTitular");
        panelPrincipalTitular.setVisible(false);
        add(panelPrincipalTitular);

        add(newContainerMensagem("containerMensagemAtivarDesativar"));
        
        panelSomenteCpf = new PanelSomenteCpf("panelSomenteCpf");
        panelSomenteCpf.setVisible(false);
        add(panelSomenteCpf);
        add(panelDataView = new PanelDataView("panelDataView"));
        add(containerBotaoAdicionarTitular = getPanelBotaoAdicionar());

        panelPrincipalTitular.setEnabled(false);
        buttonAdicionar.setVisible(false);
    }

    private WebMarkupContainer newContainerMensagem(String id){
        containerAtivarDesativar = new WebMarkupContainer(id);
        containerAtivarDesativar.add(getLabelMensagemAtivarDesativar()); //mensagemAtivarDesativar
        containerAtivarDesativar.setOutputMarkupId(true);
        return containerAtivarDesativar;
        
    }
    
    private class PanelSomenteCpf extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

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

    private WebMarkupContainer getPanelBotaoAdicionar() {
        containerBotaoAdicionarTitular = new WebMarkupContainer("containerBotaoAdicionarTitular");
        containerBotaoAdicionarTitular.add(getButtonAdicionarTitular()); // btnAdicionarTitular
        containerBotaoAdicionarTitular.setOutputMarkupId(true);
        return containerBotaoAdicionarTitular;
    }

    private void initVariaveis() {

        // Se estiver editando o titular
        if (entidadeDto.getId() != null) {

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
        fieldCpf.setEnabled(ativarBotaoDeVerificarCpf());
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
        labelAtivar.setVisible(mostrarLabelEDropDownAtivarTitular());
        return labelAtivar;
    }
    
    private Label getLabelMensagemAtivarDesativar(){
        labelMensagemAtivarDesativar = new Label("mensagemAtivarDesativar", mensagemAtivarDesativar);
        labelMensagemAtivarDesativar.setEscapeModelStrings(false);
        return labelMensagemAtivarDesativar;
    }

    private DataView<PessoaEntidade> getDataViewResultado() {
        dataView = new DataView<PessoaEntidade>("dataTitulares", new TitularProvider()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<PessoaEntidade> item) {

                item.add(new Label("pessoa.numeroCpf", CPFUtils.format(item.getModelObject().getPessoa().getNumeroCpf())));
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

                boolean mostrarBotaoAtivar = mostrarBotaoAtivar(item);

                item.add(getButtonAtivar(item).setVisible(pessoaPodeAtivarOuDesativar(item) ? mostrarBotaoAtivar : false));
                item.add(getButtonDesativar(item).setVisible(pessoaPodeAtivarOuDesativar(item) ? !mostrarBotaoAtivar : false));

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
        buttonAdicionar.setVisible(mostrarBotaoAdicionar());
        buttonAdicionar.setOutputMarkupId(true);
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
        button.setDefaultFormProcessing(false);
        button.setEnabled(ativarBotaoEditarTitular());
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

    public InfraAjaxConfirmButton getButtonAtivar(Item<PessoaEntidade> item) {
        InfraAjaxConfirmButton btnAtivar = componentFactory.newAJaxConfirmButton("btnAtivarRepresentante", "MT031", null, (target, formz) -> alterarSituacao(target, item));
        btnAtivar.setVisible(mostrarBotaoAtivar(item));
        return btnAtivar;
    }

    public InfraAjaxConfirmButton getButtonDesativar(Item<PessoaEntidade> item) {
        InfraAjaxConfirmButton btnDesativar = componentFactory.newAJaxConfirmButton("btnDesativarRepresentante", "MT031", null, (target, formz) -> alterarSituacao(target, item));
        btnDesativar.setVisible(mostrarBotaoAtivar(item));
        return btnDesativar;
    }

    public AjaxButton getButtonSalvarEdicao() {
        buttonSalvarEdicao = new AjaxButton("btnSalvarEdicao") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                salvarEdicao(target);
            }
        };
        buttonSalvarEdicao.setVisible(mostrarBotaoEditarTitular);
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
        dropDownChoice.setVisible(mostrarLabelEDropDownAtivarTitular());

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
        buttonVerificarCpf.setVisible(mostrarBotaoDeVerificarCpf());
        buttonVerificarCpf.setOutputMarkupId(true);
        return buttonVerificarCpf;
    }

    private Button getButtonAdicionarTitular() {
        buttonAdicionarTitular = new AjaxButton("btnAdicionarTitular") {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                mostrarTodosPaineis(target);
            }
        };
        buttonAdicionarTitular.setEnabled(!readOnly);
        return buttonAdicionarTitular;
    }

    // AÇÕES
    
    private void alterarSituacao(AjaxRequestTarget target, Item<PessoaEntidade> item) {

        Pessoa representante = item.getModelObject().getPessoa();

        String status = "";
        if (representante.getStatusPessoa().equals(EnumStatusPessoa.ATIVO)) {
            representante.setStatusPessoa((EnumStatusPessoa.INATIVO));
            status = "inativado";
        } else {
            representante.setStatusPessoa((EnumStatusPessoa.ATIVO));
            status = "ativado";
        }

        Pessoa pessoaRecebida = genericService.alterarStatusPessoa(representante);
        
        msg = "<p><li> Situação da pessoa alterada com sucesso.</li><p />";
        mensagemAtivarDesativar.setObject(msg);
        target.add(labelMensagemAtivarDesativar);
        target.add(panelDataView);
    }

    private boolean pessoaPodeAtivarOuDesativar(Item<PessoaEntidade> item) {

        if (readOnly && !entidadeDto.isBotaoAlterarSituacoesClicado()) {
            return false;
        } else {
            // Se for um cadastro novo não mostrar
            if (item.getModelObject().getEntidade() == null || item.getModelObject().getEntidade().getId() == null) {
                return false;
            } else {

                // Se não existir usuário logado então a tela que esta sendo visualizada é a do cadastro externo, 
                // o botão não irá aparecer
                if (entidadeDto.getUsuario() != null) {

                    // Se não for cadastro externo o botão não ira aparecer de
                    // forma nenhuma
                    if (item.getModelObject().getEntidade().getOrigemCadastro() == EnumOrigemCadastro.CADASTRO_EXTERNO) {

                        // Se a pessoa não for representante o botão não irá
                        // aparecer
                        if ((item.getModelObject().getPessoa().getId() == null) || item.getModelObject().getPessoa().getTipoPessoa() == EnumTipoPessoa.TITULAR) {
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }

            }
        }
    }

    private boolean mostrarBotaoAtivar(Item<PessoaEntidade> item) {
        if (item.getModelObject().getEntidade().getOrigemCadastro() == EnumOrigemCadastro.CADASTRO_EXTERNO) {
            if (item.getModelObject().getPessoa().getStatusPessoa() == EnumStatusPessoa.ATIVO) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    private void ocultarPaineis() {
        panelPrincipalTitular.setVisible(false);
        panelSomenteCpf.setVisible(false);
        containerBotaoAdicionarTitular.setVisible(true);
    }

    private void mostrarTodosPaineis(AjaxRequestTarget target) {
        panelPrincipalTitular.setVisible(true);
        panelSomenteCpf.setVisible(true);
        panelDataView.setVisible(true);
        containerBotaoAdicionarTitular.setVisible(false);
        buttonAdicionar.setVisible(true);

        target.add(panelPrincipalTitular);
        target.add(panelSomenteCpf);
        target.add(panelDataView);
        target.add(containerBotaoAdicionarTitular);
        target.add(buttonAdicionar);
    }

    private boolean ativarTextFieldCpf() {
        if (readOnly) {
            return false;
        } else {
            return mostrarTextFieldVerificarCpf;
        }
    }

    private boolean ativarBotaoEditarTitular() {
        if (readOnly) {
            return false;
        } else {
            return true;
        }
    }

    private boolean ativarBotaoDeVerificarCpf() {
        if (entidadeDto.getOrigemCadastro() == EnumOrigemCadastro.CADASTRO_EXTERNO) {
            if (listaDeTitulares.size() > 0) {
                return false;
            } else {
                return mostrarTextFieldVerificarCpf;
            }
        } else {
            if (readOnly) {
                return false;
            } else {
                if (cadastroNovo) {
                    if (listaDeTitulares.size() > 0) {
                        return false;
                    } else {
                        return mostrarTextFieldVerificarCpf;
                    }
                }
                return mostrarTextFieldVerificarCpf;
            }
        }
    }
    
    private boolean mostrarBotaoAdicionar(){
        if(readOnly){
            return false;
        }else{
            return mostrarBotaoAdicionarRepresentante; 
        }
    }

    private boolean mostrarBotaoDeVerificarCpf() {
        if (entidadeDto.getOrigemCadastro() == EnumOrigemCadastro.CADASTRO_EXTERNO) {
            if (listaDeTitulares.size() > 0) {
                return false;
            } else {
                return true;
            }
        } else {
            if (readOnly) {
                return false;
            } else {

                // Se o cadastro for novo então só poderá cadastrar 1 Titular
                if (cadastroNovo) {
                    if (listaDeTitulares.size() > 0) {
                        return false;
                    } else {
                        return mostrarBotaoVerificarCpf;
                    }
                }
                return mostrarBotaoVerificarCpf;
            }
        }
    }

    // Se o cadastro for externo então todos os titulares e representantes serão
    // desativados por padrão.
    private boolean mostrarLabelEDropDownAtivarTitular() {
        if (cadastroNovo) {
            return false;
        } else {
            if (entidadeDto.getOrigemCadastro() == EnumOrigemCadastro.CADASTRO_INTERNO) {
                return true;
            } else {
                if (entidadeDto.getUsuario() == null) {
                    return false;
                } else {
                    return true;
                }
            }
        }
    }

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

            entidade = setarValoresNaEntidade(entidade);

        } else {
            entidade = pessoaEncontradaPelaDigitacaoCpf;
            entidade = setarValoresNaEntidade(entidade);
        }

        EnumStatusPessoa statusPessoa;
        if (cadastroNovo) {
            if (entidadeDto.getOrigemCadastro() == EnumOrigemCadastro.CADASTRO_INTERNO) {
                statusPessoa = EnumStatusPessoa.ATIVO;
            } else {
                statusPessoa = EnumStatusPessoa.INATIVO;
            }
        } else {
            statusPessoa = dropDownChoice.getModelObject() ? EnumStatusPessoa.ATIVO : EnumStatusPessoa.INATIVO;
        }

        entidade.setStatusPessoa(statusPessoa);
        PessoaEntidade pe = new PessoaEntidade();
        Entidade entidadeCadastro = new Entidade();
        entidadeCadastro.setOrigemCadastro(entidadeDto.getOrigemCadastro());
        pe.setEntidade(entidadeCadastro);
        pe.setPessoa(entidade);
        listaDeTitulares.add(pe);

        panelPrincipalTitular.setEnabled(false);
        mostrarBotaoAdicionarRepresentante = false;
        mostrarBotaoCancelarEdicao = false;
        mostrarBotaoEditarTitular = false;
        mostrarBotaoVerificarCpf = true;
        mostrarTextFieldEmail = true;
        mostrarTextFieldVerificarCpf = true;
        modoEdicao = true;
        zerarCamposDadosBasicos();
        ocultarPaineis();
        atualizarInputs(target);
    }

    public void salvarEdicao(AjaxRequestTarget target) {

        numeroCpf = entidadeDtoTemp.getNumeroCpf();
        if (entidadeDtoTemp.getId() == null) {
            if (!validarEditarPessoa(target)) {
                return;
            }
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

        ocultarPaineis();

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

        ocultarPaineis();

        atualizarInputs(target);
    }

    public void excluirTitular(AjaxRequestTarget target, Item<PessoaEntidade> item) {
        Pessoa itemSelecionadoExcluir = item.getModelObject().getPessoa();

        for (PessoaEntidade entidade : listaDeTitulares) {
            if (entidade.getPessoa().getNumeroCpf().equalsIgnoreCase(itemSelecionadoExcluir.getNumeroCpf())) {
                listaDeTitulares.remove(entidade);
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
        posicaoPessoaLista = null;
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
        mensagemAtivarDesativar.setObject("");

        target.add(panelSomenteCpf);
        target.add(panelPrincipalTitular);
        target.add(panelDataView);
        target.add(labelMensagem);
        target.add(containerBotaoAdicionarTitular);
        target.add(containerAtivarDesativar);
    }

    // O InputText do E-Mail somente será editavel se a propria pessoa logada
    // clicar nele.
    private boolean mostrarInputTextDeEmail(Pessoa usarioClicado) {
        boolean mostrar = true;
        Usuario usuarioLogado = entidadeDto.getUsuario();

        if (usarioClicado.getId() == null || verificarSeOUsuarioLogadoPossuiFuncaoDeUsuarioInterno(usuarioLogado)) {
            mostrar = true;
        } else {
            if (usarioClicado == null || usarioClicado.getUsuario() == null) {
                mostrar = false;
            } else {
                if (usuarioLogado == null || usuarioLogado.getId() == usarioClicado.getUsuario().getId()) {
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
            for (PessoaEntidade enti : listaDeTitulares) {
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
                    for (PessoaEntidade b : listaDeTitulares) {
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

        if (listaDeTitulares != null && listaDeTitulares.size() > 0) {
            if (dropDownChoice.getModelObject() != null && dropDownChoice.getModelObject() == true) {
                for (PessoaEntidade pe : listaDeTitulares) {
                    if (pe.getPessoa().getStatusPessoa() == EnumStatusPessoa.ATIVO) {
                        if (entidadeDtoTemp.getId() != pe.getPessoa().getId()) {
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

        if (pessoaEncontradaPelaDigitacaoCpf == null || pessoaEncontradaPelaDigitacaoCpf.getId() == null) {
            // Irá verificar se este email já esta cadastrado na tabela de
            // Pessoas
            if (!validarEmailUnicoNoSalvoNoBancoDeDados(validar)) {
                validar = false;
                return validar;
            }
        }

        return validar;
    }

    private boolean validarEmailUnicoNosDataViews(boolean validar) {

        int contador = 0;
        for (PessoaEntidade entidadeExterna : listaDeTitulares) {
            if (entidadeExterna.getPessoa().getEmail().equalsIgnoreCase(email)) {
                if (entidadeExterna.getPessoa().getId() != entidadeDtoTemp.getId() || posicaoPessoaLista == null || posicaoPessoaLista != contador) {
                    msg += "<p><li> O E-Mail informado já esta cadastrado na lista de membros da comissão de 'Titulares'.</li><p />";
                    validar = false;
                    break;
                }
            }
            contador++;
        }

        for (PessoaEntidade entidadeExterna : listaDeRepresentantes) {
            if (entidadeExterna.getPessoa().getEmail().equalsIgnoreCase(email)) {
                msg += "<p><li> O E-Mail informado já esta cadastrado na lista de Representantes.</li><p />";
                validar = false;
                break;
            }
        }

        for (PessoaEntidade entidadeExterna : listaMembrosComissao) {
            if (entidadeExterna.getPessoa().getEmail().equalsIgnoreCase(email)) {
                msg += "<p><li> O E-Mail informado já esta cadastrado na lista de 'Membros da Comissão de Recebimento'.</li><p />";
                validar = false;
                break;
            }
        }
        return validar;
    }

    private boolean validarEmailUnicoNoSalvoNoBancoDeDados(boolean validar) {

        boolean valido = true;

        UsuarioPessoaDto busca = segurancaService.buscarPessoaOuUsuarioComEmail(email);
        if (busca.getPessoa() != null || busca.getUsuario() != null) {
            boolean unico = true;

            /*
             * Se for um cadastro novo e tiver sido encontrado alguém com o
             * email retornar false Caso o idTemp não seja nulo então esta sendo
             * editado, neste caso se o idTemp e o id retornado da pessoa for o
             * mesmo então blz, é a mesma pessoa.
             */
            if (entidadeDtoTemp == null || entidadeDtoTemp.getId() == null || busca.getPessoa().getId().intValue() != entidadeDtoTemp.getId().intValue()) {
                unico = false;
            }

            if (busca.getUsuario() != null) {
                unico = false;
            }

            if (!unico) {
                msg += "<p><li> O 'E-Mail' já esta em uso.</li><p />";
                valido = false;
            }
        }
        return valido;
    }

    private boolean verificarSeOUsuarioLogadoPossuiFuncaoDeUsuarioInterno(Usuario usuario) {
        if (usuario == null) {
            return false;
        } else {
            if (usuario.getTipoUsuario() == EnumTipoUsuario.INTERNO) {
                return true;
            } else {
                return false;
            }
        }
    }

    private void acaoVisualizarEditar(AjaxRequestTarget target, Item<PessoaEntidade> item, Boolean modoVisualizar) {
        modoEdicao = true;
        entidadeDtoTemp = item.getModelObject().getPessoa();

        idPessoaClicada = entidadeDtoTemp.getId();
        nomePessoa = entidadeDtoTemp.getNomePessoa();
        numeroCpf = entidadeDtoTemp.getNumeroCpf();
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
        mostrarTodosPaineis(target);
        atualizarInputs(target);
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
            for (PessoaEntidade k : listaDeTitulares) {
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
            return listaDeTitulares.size();
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
        Boolean cpfUnico = true;

        int contador = 0;
        for (PessoaEntidade entidadeExterna : listaDeTitulares) {
            if (CPFUtils.clean(entidadeExterna.getPessoa().getNumeroCpf()).equalsIgnoreCase(CPFUtils.clean(numeroCpf))) {
                if (posicaoPessoaLista == null || posicaoPessoaLista != contador) {
                    msg += "<p><li> O CPF informado já está na lista de membros da comissão de 'Titulares'.</li><p />";
                    cpfUnico = false;
                    break;
                }
            }
            if (!cpfUnico) {
                break;
            }
            contador++;
        }

        for (PessoaEntidade entidadeExterna : listaDeRepresentantes) {
            if (CPFUtils.clean(entidadeExterna.getPessoa().getNumeroCpf()).equalsIgnoreCase(CPFUtils.clean(numeroCpf))) {
                msg += "<p><li> O CPF informado já está cadastrado na lista de 'Representantes'.</li><p />";
                cpfUnico = false;
                break;
            }
            if (!cpfUnico) {
                break;
            }
        }

        for (PessoaEntidade entidadeExterna : listaMembrosComissao) {
            if (CPFUtils.clean(entidadeExterna.getPessoa().getNumeroCpf()).equalsIgnoreCase(CPFUtils.clean(numeroCpf))) {
                msg += "<p><li> O CPF informado já está cadastrado na lista de 'Membros da Comissão de Recebimento'.</li><p />";
                cpfUnico = false;
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
        enti.setTodos(pessoa);
        enti.setUsuarioLogado(entidadeDto.getUsuario());
        List<Entidade> lista = genericService.buscarSemPaginacao(enti);

        for (Entidade entit : lista) {
            for (PessoaEntidade pe : entit.getPessoas()) {
                if (pe.getPessoa().getNumeroCpf().equalsIgnoreCase(CPFUtils.clean(numeroCpf))) {

                    String nomeEntidadeAtivo = entit.getNomeEntidade();
                    if (pe.getPessoa().getTipoPessoa() == EnumTipoPessoa.REPRESENTANTE_ENTIDADE) {
                        msg += "<p><li>Não é possível adicionar este CPF pois o mesmo já foi cadastrado como 'Representante' da Entidade '" + nomeEntidadeAtivo + "'.</li><p />";
                    } else {
                        if (pe.getPessoa().getTipoPessoa() == EnumTipoPessoa.PREPOSTO_FORNECEDOR) {
                            msg += "<p><li>Não é possível adicionar este CPF pois o mesmo já foi cadastrado como 'Preposto' do fornecedor '" + nomeEntidadeAtivo + "'.</li><p />";
                        } else {
                            if (pe.getPessoa().getTipoPessoa() == EnumTipoPessoa.REPRESENTANTE_LEGAL) {
                                msg += "<p><li>Não é possível adicionar este CPF pois o mesmo já foi cadastrado como 'Representante Legal' do fornecedor '" + nomeEntidadeAtivo + "'.</li><p />";
                            } else {
                                if (pe.getPessoa().getTipoPessoa() == EnumTipoPessoa.TITULAR) {
                                    msg += "<p><li>Não é possível adicionar este CPF pois o mesmo já foi cadastrado como 'Titular' da entidade '" + nomeEntidadeAtivo + "'.</li><p />";
                                }
                            }
                        }
                    }
                    mensagem.setObject(msg);
                    target.add(labelMensagem);
                    return;
                }
            }
        }/*
          * 
          * // Esta verificação impede que cadastremos qualquer pessoa já
          * cadastrada // nesta entidade. if (lista != null && lista.size() > 0)
          * { msg +=
          * "<p><li> Não é possível realizar este cadastro pois esta pessoa já está cadastrada em outra Entidade.</li><p />"
          * ; mensagem.setObject(msg); target.add(labelMensagem); return; }
          * 
          * EntidadePesquisaDto entiTitular = new EntidadePesquisaDto();
          * entiTitular.setTitular(pessoa);
          * entiTitular.setUsuarioLogado(entidade.getUsuario()); List<Entidade>
          * listaTitular = beneficiarioService.buscarSemPaginacao(entiTitular);
          * 
          * if (listaTitular.size() > 0) { msg +=
          * "<p><li> Não é possível realizar este cadastro pois esta pessoa já está cadastrada como Titular em outra Entidade.</li><p />"
          * ; mensagem.setObject(msg); target.add(labelMensagem); return; }
          */

        pessoaEncontradaPelaDigitacaoCpf = new Pessoa();
        if (lista.size() > 0) {
            for (PessoaEntidade result : lista.get(0).getPessoas()) {
                if (CPFUtils.clean(result.getPessoa().getNumeroCpf()).equalsIgnoreCase(CPFUtils.clean(numeroCpf))) {
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

        mostrarBotaoVerificarCpf = false;
        // mostrarTextFieldEmail = mostrarInputTextDeEmail(entidadeDtoTemp);
        mostrarTextFieldEmail = true;
        mostrarTextFieldVerificarCpf = false;
        mostrarBotaoAdicionarRepresentante = true;
        panelPrincipalTitular.setEnabled(true);
        mostrarBotaoCancelarEdicao = true;
        mostrarBotaoEditarTitular = false;

        atualizarInputs(target);
    }

    private Pessoa setarValoresNaEntidade(Pessoa entidade) {
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
}
