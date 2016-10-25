package br.gov.mj.side.web.view.beneficiario.paineis;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

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

public class PanelDadosRepresentante extends Panel {
    private static final long serialVersionUID = 1L;

    private static final String ONCHANGE = "onchange";

    private EntidadeDto entidade;

    private PanelSomenteCpf panelSomenteCpf;
    private PanelPrincipalBeneficiario panelPrincipalBeneficiario;
    private PanelDataView panelDataView;
    private WebMarkupContainer containerBotaoAdicionarRepresentante;
    private WebMarkupContainer containerAtivarDesativar;

    private InfraLocalDateTextField field1;
    private InfraLocalDateTextField field2;

    private LocalDate dataInicio;
    private LocalDate dataFim;

    private List<PessoaEntidade> listaDeTitulares;
    private List<PessoaEntidade> listaDeRepresentantes;
    private List<PessoaEntidade> listaMembrosComissao;

    private AjaxButton buttonAdicionar;
    private AjaxButton buttonSalvarEdicao;
    private AjaxSubmitLink buttonCancelarEdicao;
    private AjaxButton buttonVerificarCpf;
    private TextField<String> fieldCpf;
    private DropDownChoice<Boolean> dropDownChoice;
    private Button buttonAdicionarRepresentante;

    private AjaxCheckBox checkAtivo;
    private Label labelAtivar;
    private DataView<PessoaEntidade> dataView;
    private Label labelMensagem;
    private Label labelMensagemAtivarDesativar;
    private Label lblNumeroCpf;
    private Model<String> mensagem = Model.of("");
    private Model<String> mensagemAtivarDesativar = Model.of("");

    private EntidadeDto entidadeDto = new EntidadeDto();
    private Pessoa entidadeDtoTemp = new Pessoa();
    private Pessoa pessoaEncontradaPelaDigitacaoCpf = new Pessoa();
    private boolean modoEdicao = false;
    private boolean mostrarBotaoCancelarEdicao = false;
    private Integer posicaoPessoaLista = null;
    private String cpfTemporario = "";
    private String msg = "";

    private Long idPessoaClicada;
    private String nomePessoa;
    private String numeroCpf;
    private String descricaoCargo;
    private String numeroTelefone;
    private String email;
    private LocalDate dataInicioExercicio;
    private LocalDate dataFimExercicio;
    private String enderecoCorrespondencia;
    private Boolean ativo = null;
    private Boolean cadastroNovo;
    private Boolean mostrarBotaoAdicionarRepresentante = true;
    private Boolean mostrarBotaoVerificarCpf = true;
    private Boolean mostrarTextFieldVerificarCpf = true;
    private Boolean mostrarTextFieldEmail = true;
    private Boolean dadoAindaNaoSalvoNoBanco = true;
    private boolean readOnly;

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

    public PanelDadosRepresentante(String id, EntidadeDto entidade, List<PessoaEntidade> listaDeRepresentantes, List<PessoaEntidade> listaDeTitulares, List<PessoaEntidade> listaMembrosComissao, Boolean cadastroNovo, boolean readOnly) {
        super(id);
        this.entidade = entidade;
        this.cadastroNovo = cadastroNovo;
        this.readOnly = readOnly;
        this.listaDeRepresentantes = listaDeRepresentantes;
        this.listaDeTitulares = listaDeTitulares;
        this.listaMembrosComissao = listaMembrosComissao;
        initVariaveis();
        setOutputMarkupId(true);

        panelPrincipalBeneficiario = new PanelPrincipalBeneficiario("panelPrincipalBeneficiario");
        panelPrincipalBeneficiario.setVisible(false);
        add(panelPrincipalBeneficiario);

        add(newContainerMensagem("containerMensagemAtivarDesativar"));
        
        panelSomenteCpf = new PanelSomenteCpf("panelSomenteCpf");
        panelSomenteCpf.setVisible(false);
        add(panelSomenteCpf);

        add(panelDataView = new PanelDataView("panelDataView"));
        add(containerBotaoAdicionarRepresentante = getPanelBotaoAdicionar());// containerBotaoAdicionarRepresentante

        panelPrincipalBeneficiario.setEnabled(false);
        buttonAdicionar.setVisible(false);
    }

    private class PanelPrincipalBeneficiario extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelPrincipalBeneficiario(String id) {
            super(id);
            setOutputMarkupId(true);

            add(getTextFieldNome()); // entidadeRepresentante.nomePessoa
            add(getTextFieldCargo()); // entidadeRepresentante.descricaoCargo
            add(getTextFieldTelefone()); // entidadeRepresentante.numeroTelefone
            add(getTextFieldEmail()); // entidadeRepresentante.email
            add(getDateTextFieldPeriodo1()); // dataPeriodo1
            add(getDateTextFieldPeriodo2()); // dataPeriodo2
            add(getTextFieldEndereco()); // entidadeRepresentante.enderecoCorrespondencia
            add(getDropDownAtivarDesativarBeneficiario()); // dropAtivo
            add(getLabelAtivar()); // lblAtivarRepresentante
        }
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
            
            add(getLabelMensagem());
            add(getTextFieldCpf()); // entidadeRepresentante.numeroCpf
            add(getButtonVerificarCpf()); // btnVerificarCpf
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
        containerBotaoAdicionarRepresentante = new WebMarkupContainer("containerBotaoAdicionarRepresentante");
        containerBotaoAdicionarRepresentante.add(getButtonAdicionarRepresentante()); // btnAdicionarRepresentante
        containerBotaoAdicionarRepresentante.setOutputMarkupId(true);
        return containerBotaoAdicionarRepresentante;
    }

    private void initVariaveis() {
        entidadeDto = entidade;

        // Se estiver editando o titular
        if (entidadeDto.getId() != null) {

            // é necessário inicializar esta variavel para que os campos de
            // input do titular venham todos em branco
            entidadeDto.setEntidadeTitular(new Pessoa());
        }

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private TextField<String> getTextFieldNome() {
        TextField<String> field = componentFactory.newTextField("pessoa.nomePessoa", "Nome (Representante)", false, new PropertyModel(this, "nomePessoa"));
        field.add(StringValidator.maximumLength(200));
        actionTextFieldNome(field);
        return field;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private TextField<String> getTextFieldCpf() {
        fieldCpf = componentFactory.newTextField("entidadeRepresentante.numeroCpf", "CPF (Representante)", false, new PropertyModel(this, "numeroCpf"));
        fieldCpf.add(StringValidator.maximumLength(14));
        actionTextFieldNome(fieldCpf);
        fieldCpf.setOutputMarkupId(true);
        fieldCpf.setEnabled(ativarTextFieldCpf());
        return fieldCpf;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private TextField<String> getTextFieldCargo() {
        TextField<String> field = componentFactory.newTextField("entidadeRepresentante.descricaoCargo", "Cargo (Representante)", false, new PropertyModel(this, "descricaoCargo"));
        field.add(StringValidator.maximumLength(200));
        actionTextFieldNome(field);
        return field;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private TextField<String> getTextFieldTelefone() {
        TextField<String> field = componentFactory.newTextField("entidadeRepresentante.numeroTelefone", "Telefone (Representante)", false, new PropertyModel(this, "numeroTelefone"));
        field.add(StringValidator.maximumLength(13));
        actionTextFieldNome(field);
        return field;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private TextField<String> getTextFieldEmail() {
        TextField<String> field = componentFactory.newTextField("entidadeRepresentante.email", "E-mail (Representante)", false, new PropertyModel(this, "email"));
        field.add(StringValidator.maximumLength(200));
        actionTextFieldNome(field);
        field.setEnabled(mostrarTextFieldEmail);
        return field;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private InfraLocalDateTextField getDateTextFieldPeriodo1() {
        field1 = componentFactory.newDateTextFieldWithDatePicker("entidadeRepresentante.dataInicioExercicio", "Período de Exercício (inicial)", false, new PropertyModel(this, "dataInicioExercicio"), "dd/MM/yyyy", "pt-BR");
        actionFimExercicio(field1);
        return field1;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private InfraLocalDateTextField getDateTextFieldPeriodo2() {
        field2 = componentFactory.newDateTextFieldWithDatePicker("entidadeRepresentante.dataFimExercicio", "Período de Exercício (final)", false, new PropertyModel(this, "dataFimExercicio"), "dd/MM/yyyy", "pt-BR");
        actionInicioExercicio(field2);
        return field2;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private TextField<String> getTextFieldEndereco() {
        TextField<String> field = componentFactory.newTextField("entidadeRepresentante.enderecoCorrespondencia", "Endereço de Correspondência", false, new PropertyModel(this, "enderecoCorrespondencia"));
        field.add(StringValidator.maximumLength(200));
        // actionTextFieldEndereco(field);
        return field;
    }

    private DataView<PessoaEntidade> getDataViewResultado() {
        dataView = new DataView<PessoaEntidade>("dataTitulares", new TitularProvider()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<PessoaEntidade> item) {

                String nomeDaPessoa = "";

                item.add(new Label("pessoa.numeroCpf", CPFUtils.format(item.getModelObject().getPessoa().getNumeroCpf())));

                if (mostrarPalavraTitularNoDataView(item)) {
                    nomeDaPessoa = item.getModelObject().getPessoa().getNomePessoa() + "<font color='red'> - TITULAR </font>";
                    Label label = new Label("pessoa.nomePessoa", nomeDaPessoa);
                    label.setEscapeModelStrings(false);
                    item.add(label);
                } else {
                    item.add(new Label("pessoa.nomePessoa"));
                }
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

                item.add(getButtonEditar(item, false).setVisible(mostrarBotoesDeAcao(item)));
                item.add(getButtonVisualizar(item, true).setVisible(mostrarBotoesDeAcao(item)));
                
                item.add(getButtonAtivar(item).setVisible(pessoaPodeAtivarOuDesativar(item)?mostrarBotaoAtivar:false));
                item.add(getButtonDesativar(item).setVisible(pessoaPodeAtivarOuDesativar(item)?!mostrarBotaoAtivar:false));
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
    
    public AjaxSubmitLink getButtonEditar(Item<PessoaEntidade> item, boolean modoVisualizar) {
        AjaxSubmitLink button = new AjaxSubmitLink("btnEditar") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form) {
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
            public void onSubmit(AjaxRequestTarget target, Form<?> form) {
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
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                salvarEdicao(target);
            }
        };
        buttonSalvarEdicao.setVisible(modoEdicao);
        buttonSalvarEdicao.setOutputMarkupId(true);
        return buttonSalvarEdicao;
    }

    public AjaxSubmitLink getButtonCancelarEdicao() {
        buttonCancelarEdicao = new AjaxSubmitLink("btnCancelarEdicao") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form) {
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

    public Button getButtonVerificarCpf() {
        buttonVerificarCpf = new AjaxButton("btnVerificarCpf") {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                verificarCpf(target);
            }
        };
        buttonVerificarCpf.setVisible(mostrarBotaoVerificarCpf());
        buttonVerificarCpf.setOutputMarkupId(true);
        return buttonVerificarCpf;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
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
                acaoDrop(target);
            }

        });

        dropDownChoice.setVisible(mostrarLabelEDropDownAtivarTitular());
        return dropDownChoice;
    }

    private Label getLabelAtivar() {
        labelAtivar = new Label("lblAtivarRepresentante", "* Ativar Representante");
        labelAtivar.setVisible(mostrarLabelEDropDownAtivarTitular());
        return labelAtivar;
    }
    
    private Label getLabelMensagem(){
        labelMensagem = new Label("mensagemAnexo", mensagem);
        labelMensagem.setEscapeModelStrings(false);
        return labelMensagem;
    }
    
    private Label getLabelMensagemAtivarDesativar(){
        labelMensagemAtivarDesativar = new Label("mensagemAtivarDesativar", mensagemAtivarDesativar);
        labelMensagemAtivarDesativar.setEscapeModelStrings(false);
        return labelMensagemAtivarDesativar;
    }
    
    

    // AÇÕES
    
    private boolean mostrarBotaoAtivar(Item<PessoaEntidade> item){
        if(item.getModelObject().getEntidade().getOrigemCadastro() == EnumOrigemCadastro.CADASTRO_EXTERNO){
           if(item.getModelObject().getPessoa().getStatusPessoa() == EnumStatusPessoa.ATIVO){
               return false;
           }else{
               return true;
           }
        }else{
            return false;
        }
    }
    
    private boolean mostrarBotaoAdicionar(){
        if(readOnly){
            return false;
        }else{
            return mostrarBotaoAdicionarRepresentante; 
        }
    }

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

    private void ocultarPaineis() {
        panelPrincipalBeneficiario.setVisible(false);
        panelSomenteCpf.setVisible(false);
        containerBotaoAdicionarRepresentante.setVisible(true);
    }

    private void mostrarTodosPaineis(AjaxRequestTarget target) {
        panelPrincipalBeneficiario.setVisible(true);
        panelSomenteCpf.setVisible(true);
        panelDataView.setVisible(true);
        containerBotaoAdicionarRepresentante.setVisible(false);
        buttonAdicionar.setVisible(true);

        target.add(panelPrincipalBeneficiario);
        target.add(panelSomenteCpf);
        target.add(panelDataView);
        target.add(containerBotaoAdicionarRepresentante);
        target.add(buttonAdicionar);
    }

    private Button getButtonAdicionarRepresentante() {
        buttonAdicionarRepresentante = new AjaxButton("btnAdicionarRepresentante") {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                mostrarTodosPaineis(target);
            }
        };
        buttonAdicionarRepresentante.setEnabled(!readOnly);
        return buttonAdicionarRepresentante;
    }

    private boolean mostrarBotaoVerificarCpf() {
        if (readOnly) {
            return false;
        } else {
            return mostrarBotaoVerificarCpf;
        }
    }

    private boolean ativarBotaoEditarTitular() {
        if (readOnly) {
            return false;
        } else {
            return true;
        }
    }

    private boolean ativarTextFieldCpf() {
        if (readOnly) {
            return false;
        } else {
            return mostrarTextFieldVerificarCpf;
        }
    }

    // Se o cadastro for externo então todos os titulares e representantes serão
    // desativados por padrão.
    private boolean mostrarLabelEDropDownAtivarTitular() {
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

    @SuppressWarnings("unused")
    private void actionTextFieldCpf(TextField<String> field) {
        AjaxFormComponentUpdatingBehavior onChangeAjaxBehavior = new AjaxFormComponentUpdatingBehavior(ONCHANGE) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {

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
                enti.setRepresentante(pessoa);
                enti.setUsuarioLogado(entidade.getUsuario());
                List<Entidade> lista = beneficiarioService.buscarSemPaginacao(enti);

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
                            // boolean isAtivo =
                            // entidadeDtoTemp.getStatusPessoa().getValor().equalsIgnoreCase("A")
                            // ? true : false;
                            // ativo = isAtivo;

                            pessoaEncontradaPelaDigitacaoCpf = result.getPessoa();
                            break;
                        }
                    }

                }
                panelPrincipalBeneficiario.setEnabled(true);
                atualizarInputs(target);
            }
        };
        field.add(onChangeAjaxBehavior);
    }

    // O botão será mostrado se a pessoa for representante ou se for um cadastro
    // ainda novo.
    private boolean mostrarBotoesDeAcao(Item<PessoaEntidade> item) {
        return (!readOnly && (item.getModelObject().getPessoa().getId() == null) || item.getModelObject().getPessoa().getTipoPessoa() == EnumTipoPessoa.REPRESENTANTE_ENTIDADE);
    }
    
    private boolean pessoaPodeAtivarOuDesativar(Item<PessoaEntidade> item) {

        if(readOnly && !entidadeDto.isBotaoAlterarSituacoesClicado()){
            return false;
        }else{            
            
            //Se for um cadastro novo não mostrar
            if(item.getModelObject().getEntidade() == null || item.getModelObject().getEntidade().getId() == null){
                return false;
            }else{
                
                // Se não existir usuário logado então a tela que esta sendo visualizada é a do cadastro externo, 
                // o botão não irá aparecer
                if (entidadeDto.getUsuario() != null) {
                    
                    //se o usuário logado for nulo, então é um novo cadastro externo, se a pessoa já estiver logada e o usuário for externo
                    //não mostrar o botão
                    if(item.getModelObject().getPessoa().getUsuario() == null || item.getModelObject().getPessoa().getUsuario().getTipoUsuario() == EnumTipoUsuario.INTERNO){
                        return false;
                    }else{
                        
                        // Se não for cadastro externo o botão não ira aparecer de forma nenhuma
                        if (item.getModelObject().getEntidade().getOrigemCadastro() == EnumOrigemCadastro.CADASTRO_EXTERNO) {

                            // Se a pessoa não for representante o botão não irá aparecer
                            if ((item.getModelObject().getPessoa().getId() == null) || item.getModelObject().getPessoa().getTipoPessoa() == EnumTipoPessoa.REPRESENTANTE_ENTIDADE) {
                                return true;
                            } else {
                                return false;
                            }
                        } else {
                            return false;
                        }
                    }
                }else{
                    return false;
                }
            }
        }
    }

    // Se esta pessoa for titular mas possuir função de representante será
    // mostrado com a palavara 'titular' na frente
    private boolean mostrarPalavraTitularNoDataView(Item<PessoaEntidade> item) {
        return item.getModelObject().getPessoa().isTitularComFuncaoDeRepesentante() && item.getModelObject().getPessoa().getStatusPessoa() == EnumStatusPessoa.ATIVO;
    }

    private void actionTextFieldNome(TextField<String> field) {
        AjaxFormComponentUpdatingBehavior onChangeAjaxBehavior = new AjaxFormComponentUpdatingBehavior(ONCHANGE) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                // Setar no Model
            }
        };
        field.add(onChangeAjaxBehavior);
    }

    private void actionInicioExercicio(TextField<LocalDate> field) {
        AjaxFormComponentUpdatingBehavior onChangeAjaxBehavior = new AjaxFormComponentUpdatingBehavior(ONCHANGE) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                // Setar no Model
            }
        };
        field.add(onChangeAjaxBehavior);
    }

    private void actionFimExercicio(TextField<LocalDate> field) {
        AjaxFormComponentUpdatingBehavior onChangeAjaxBehavior = new AjaxFormComponentUpdatingBehavior(ONCHANGE) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                // Setar no Model
            }
        };
        field.add(onChangeAjaxBehavior);
    }

    public void adicionar(AjaxRequestTarget target) {
        if (!validarEditarPessoa(target)) {
            return;
        }

        modoEdicao = false;
        mostrarBotaoCancelarEdicao = false;

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
                statusPessoa = ativo ? EnumStatusPessoa.ATIVO : EnumStatusPessoa.INATIVO;
            } else {
                statusPessoa = EnumStatusPessoa.INATIVO;
            }
        } else {
            statusPessoa = ativo ? EnumStatusPessoa.ATIVO : EnumStatusPessoa.INATIVO;
        }

        entidade.setStatusPessoa(statusPessoa);
        PessoaEntidade pe = new PessoaEntidade();
        Entidade entidadeCadastro = new Entidade();
        entidadeCadastro.setOrigemCadastro(entidadeDto.getOrigemCadastro());
        pe.setEntidade(entidadeCadastro);
        if (pessoaEncontradaPelaDigitacaoCpf.getId() != null) {
            pe.setPessoa(pessoaEncontradaPelaDigitacaoCpf);
        } else {
            pe.setPessoa(entidade);
        }
        listaDeRepresentantes.add(pe);

        panelPrincipalBeneficiario.setEnabled(false);
        mostrarBotaoAdicionarRepresentante = false;
        mostrarBotaoVerificarCpf = true;
        mostrarTextFieldEmail = true;
        mostrarTextFieldVerificarCpf = true;
        zerarCamposDadosBasicos();
        ocultarPaineis();
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
        entidadeDtoTemp.setPossuiFuncaoDeRepresentante(true);

        EnumStatusPessoa statusPessoa = ativo ? EnumStatusPessoa.ATIVO : EnumStatusPessoa.INATIVO;
        entidadeDtoTemp.setStatusPessoa(statusPessoa);

        modoEdicao = false;
        mostrarBotaoCancelarEdicao = false;

        panelPrincipalBeneficiario.setEnabled(false);
        zerarCamposDadosBasicos();
        mostrarBotaoVerificarCpf = true;
        mostrarTextFieldEmail = true;
        mostrarTextFieldVerificarCpf = true;
        mostrarBotaoAdicionarRepresentante = false;

        ocultarPaineis();

        atualizarInputs(target);
    }

    public void cancelarEdicao(AjaxRequestTarget target) {
        modoEdicao = false;
        mostrarBotaoCancelarEdicao = false;

        panelPrincipalBeneficiario.setEnabled(false);
        zerarCamposDadosBasicos();
        mostrarBotaoAdicionarRepresentante = false;
        mostrarBotaoVerificarCpf = true;
        mostrarTextFieldVerificarCpf = true;
        mostrarTextFieldEmail = true;

        ocultarPaineis();

        atualizarInputs(target);
    }

    public void excluirTitular(AjaxRequestTarget target, Item<PessoaEntidade> item) {
        Pessoa itemSelecionadoExcluir = item.getModelObject().getPessoa();

        for (PessoaEntidade entidade : listaDeRepresentantes) {
            if (entidade.getPessoa().getNumeroCpf().equalsIgnoreCase(itemSelecionadoExcluir.getNumeroCpf())) {
                listaDeRepresentantes.remove(entidade);
                break;
            }
        }

        modoEdicao = false;
        mostrarBotaoCancelarEdicao = false;

        panelPrincipalBeneficiario.setEnabled(false);
        zerarCamposDadosBasicos();
        mostrarBotaoVerificarCpf = true;
        mostrarTextFieldVerificarCpf = true;
        mostrarTextFieldEmail = true;
        mostrarBotaoAdicionarRepresentante = false;
        atualizarInputs(target);
    }

    public void editarRepresentante(AjaxRequestTarget target, Item<PessoaEntidade> item) {

    }

    private boolean compararCpfsCadastradosDosDoisPaineis() {
        Boolean cpfUnico = true;

        int contador = 0;
        for (PessoaEntidade entidadeExterna : listaDeRepresentantes) {
            if (CPFUtils.clean(entidadeExterna.getPessoa().getNumeroCpf()).equalsIgnoreCase(CPFUtils.clean(numeroCpf))) {
                if (posicaoPessoaLista == null || posicaoPessoaLista != contador) {
                    msg += "<p><li> O CPF informado já está na lista de membros da comissão de 'Representantes'.</li><p />";
                    cpfUnico = false;
                    break;
                }
            }
            if (!cpfUnico) {
                break;
            }
            contador++;
        }

        for (PessoaEntidade entidadeExterna : listaDeTitulares) {
            if (CPFUtils.clean(entidadeExterna.getPessoa().getNumeroCpf()).equalsIgnoreCase(CPFUtils.clean(numeroCpf))) {
                msg += "<p><li> O CPF informado já está cadastrado na lista de 'Titulares'.</li><p />";
                cpfUnico = false;
                break;
            }
            if (!cpfUnico) {
                break;
            }
        }

        for (PessoaEntidade entidadeExterna : listaMembrosComissao) {
            if (CPFUtils.clean(entidadeExterna.getPessoa().getNumeroCpf()).equalsIgnoreCase(CPFUtils.clean(numeroCpf))) {
                if(!entidadeExterna.getPessoa().getId().equals(entidadeDtoTemp.getId())){
                    msg += "<p><li> O CPF informado já está cadastrado na lista de 'Membros da Comissão de Recebimento'.</li><p />";
                    cpfUnico = false;
                    break;
                }
            }
            if (!cpfUnico) {
                break;
            }
        }

        return cpfUnico;
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
        ativo = null;
        posicaoPessoaLista = null;
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

    private void atualizarInputs(AjaxRequestTarget target) {
        panelSomenteCpf.addOrReplace(getTextFieldCpf());
        panelSomenteCpf.addOrReplace(getButtonVerificarCpf());
        panelSomenteCpf.addOrReplace(getLabelMensagem());
        panelPrincipalBeneficiario.addOrReplace(getTextFieldNome());
        panelPrincipalBeneficiario.addOrReplace(getTextFieldCargo());
        panelPrincipalBeneficiario.addOrReplace(getTextFieldTelefone());
        panelPrincipalBeneficiario.addOrReplace(getTextFieldEmail());
        panelPrincipalBeneficiario.addOrReplace(getDateTextFieldPeriodo1());
        panelPrincipalBeneficiario.addOrReplace(getDateTextFieldPeriodo2());
        panelPrincipalBeneficiario.addOrReplace(getTextFieldEndereco());
        panelDataView.addOrReplace(getButtonCancelarEdicao());
        panelDataView.addOrReplace(getButtonSalvarEdicao());
        panelDataView.addOrReplace(getButtonAdicionar());
        mensagem.setObject("");
        mensagemAtivarDesativar.setObject("");

        target.add(panelSomenteCpf);
        target.add(panelPrincipalBeneficiario);
        target.add(panelDataView);
        target.add(labelMensagem);
        target.add(containerBotaoAdicionarRepresentante);
        target.add(containerAtivarDesativar);
    }

    // O InputText do E-Mail somente será editavel se a propria pessoa logada
    // clicar nele.
    private boolean mostrarInputTextDeEmail(Pessoa usarioClicado) {
        boolean mostrar = true;
        Usuario usuarioLogado = entidade.getUsuario();

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
                    for (PessoaEntidade b : listaDeRepresentantes) {
                        String cpfLista = CPFUtils.clean(b.getPessoa().getNumeroCpf());

                        if (CPFUtils.clean(numeroCpf).equalsIgnoreCase(cpfLista)) {
                            if (posicaoPessoaLista != contador) {
                                msg += "<p><li> O campo 'CPF' informado já está em uso.</li><p />";
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
            msg += "<p><li> O Telefone deverá conter ao menos 10 números.</li><p />";
            validar = false;
        }

        if (email != null && !EmailValidator.validate(email)) {
            msg += "<p><li> O Email está em um formato inválido.</li><p />";
            validar = false;
        }
        return validar;
    }

    @SuppressWarnings("unused")
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
            msg += "<p><li> O campo 'Período de Exercício (Inicial)' é obrigatório.</li><p />";
            validar = false;
        }

        if (dataInicioExercicio != null && dataFimExercicio != null) {
            if (dataInicioExercicio.isAfter(dataFimExercicio)) {
                msg += "<p><li> O Período de Exercício Inicial não poderá ser superior ao Período de Exercício Final.</li><p />";
                validar = false;
            }
        }

        if (enderecoCorrespondencia == null || "".equalsIgnoreCase(enderecoCorrespondencia)) {
            msg += "<p><li> O campo 'Endereço de Correspondência' é obrigatório.</li><p />";
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
        for (PessoaEntidade entidadeExterna : listaDeRepresentantes) {
            if (entidadeExterna.getPessoa().getEmail().equalsIgnoreCase(email)) {
                if (entidadeExterna.getPessoa().getId() != entidadeDtoTemp.getId() || posicaoPessoaLista == null || posicaoPessoaLista != contador) {
                    msg += "<p><li> O E-Mail informado já esta cadastrado na lista de membros da comissão de 'Representantes'.</li><p />";
                    validar = false;
                    break;
                }
            }
            contador++;
        }

        for (PessoaEntidade entidadeExterna : listaDeTitulares) {
            if (entidadeExterna.getPessoa().getEmail().equalsIgnoreCase(email)) {
                msg += "<p><li> O E-Mail informado já esta cadastrado na lista de 'Titulares'.</li><p />";
                validar = false;
                break;
            }
        }

        for (PessoaEntidade entidadeExterna : listaMembrosComissao) {
            if (entidadeExterna.getPessoa().getEmail().equalsIgnoreCase(email)) {
                if(!entidadeExterna.getPessoa().getId().equals(entidadeDtoTemp.getId())){
                    msg += "<p><li> O E-Mail informado já esta cadastrado na lista de 'Membros da Comissão de Recebimento'.</li><p />";
                    validar = false;
                    break;
                }
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

            if (busca.getUsuario() != null && idPessoaClicada != null) {
                if (idPessoaClicada.intValue() != busca.getPessoa().getId().intValue()) {
                    unico = false;
                }
            }

            if (!unico) {
                msg += "<p><li> O 'E-Mail' já esta em uso.</li><p />";
                valido = false;
            }
        }
        return valido;
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
            for (PessoaEntidade k : listaDeRepresentantes) {
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
            return listaDeRepresentantes.size();
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
            if (!CPFUtils.validate(CPFUtils.clean(cpf))) {
                msg += "<p><li> O 'CPF' informado está em um formato inválido.</li><p />";
                valido = false;
            }
        }
        return valido;
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
        enti.setUsuarioLogado(entidade.getUsuario());
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
        mostrarTextFieldEmail = true;
        mostrarTextFieldVerificarCpf = false;
        mostrarBotaoAdicionarRepresentante = true;
        mostrarBotaoCancelarEdicao = true;
        panelPrincipalBeneficiario.setEnabled(true);

        atualizarInputs(target);
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

    private void acaoDrop(AjaxRequestTarget target) {
        ativo = dropDownChoice.getModelObject();
    }

    private void acaoVisualizarEditar(AjaxRequestTarget target, Item<PessoaEntidade> item, Boolean modoVisualizar) {
        mostrarBotaoCancelarEdicao = true;
        entidadeDtoTemp = item.getModelObject().getPessoa();

        idPessoaClicada = entidadeDtoTemp.getId();
        nomePessoa = entidadeDtoTemp.getNomePessoa();
        numeroCpf = new String(entidadeDtoTemp.getNumeroCpf());
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
        mostrarTextFieldEmail = mostrarInputTextDeEmail(entidadeDtoTemp);
        mostrarBotaoAdicionarRepresentante = false;

        if (modoVisualizar) {
            panelPrincipalBeneficiario.setEnabled(false);
            modoEdicao = false;
        } else {
            panelPrincipalBeneficiario.setEnabled(true);
            modoEdicao = true;
        }

        fieldCpf.setEnabled(false);
        mostrarTodosPaineis(target);
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
        entidade.setPossuiFuncaoDeRepresentante(true);
        entidade.setTipoPessoa(EnumTipoPessoa.REPRESENTANTE_ENTIDADE);

        return entidade;
    }
}
