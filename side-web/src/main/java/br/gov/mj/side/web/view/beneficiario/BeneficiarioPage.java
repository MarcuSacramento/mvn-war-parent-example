package br.gov.mj.side.web.view.beneficiario;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.validator.StringValidator;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraAjaxFallbackLink;
import br.gov.mj.infra.wicket.component.InfraRadioChoice;
import br.gov.mj.infra.wicket.model.LambdaModel;
import br.gov.mj.seg.entidades.Usuario;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.entidade.EntidadeAnexo;
import br.gov.mj.side.entidades.entidade.Pessoa;
import br.gov.mj.side.entidades.entidade.PessoaEntidade;
import br.gov.mj.side.entidades.enums.EnumOrigemCadastro;
import br.gov.mj.side.entidades.enums.EnumStatusPessoa;
import br.gov.mj.side.entidades.enums.EnumTipoArquivoEntidade;
import br.gov.mj.side.entidades.enums.EnumTipoPessoa;
import br.gov.mj.side.entidades.enums.EnumValidacaoCadastro;
import br.gov.mj.side.web.dto.EntidadeDto;
import br.gov.mj.side.web.dto.EntidadePesquisaDto;
import br.gov.mj.side.web.dto.ListasPorTipoPessoaDto;
import br.gov.mj.side.web.service.AnexoEntidadeService;
import br.gov.mj.side.web.service.BeneficiarioService;
import br.gov.mj.side.web.service.GenericEntidadeService;
import br.gov.mj.side.web.service.MailService;
import br.gov.mj.side.web.service.SegurancaService;
import br.gov.mj.side.web.util.CPFUtils;
import br.gov.mj.side.web.util.CnpjUtil;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.EmailValidator;
import br.gov.mj.side.web.util.SideUtil;
import br.gov.mj.side.web.view.HomePage;
import br.gov.mj.side.web.view.beneficiario.paineis.PanelAnexo;
import br.gov.mj.side.web.view.beneficiario.paineis.PanelDadosEntidade;
import br.gov.mj.side.web.view.beneficiario.paineis.PanelDadosRepresentante;
import br.gov.mj.side.web.view.beneficiario.paineis.PanelDadosTitular;
import br.gov.mj.side.web.view.components.AjaxDialogButton;
import br.gov.mj.side.web.view.template.TemplatePage;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal.Backdrop;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.TextContentModal;

@AuthorizeInstantiation({ BeneficiarioPesquisaPage.ROLE_MANTER_BENEFICIARIO_INCLUIR, BeneficiarioPesquisaPage.ROLE_MANTER_BENEFICIARIO_ALTERAR, BeneficiarioPesquisaPage.ROLE_MANTER_BENEFICIARIO_VALIDAR_CADASTRO })
public class BeneficiarioPage extends TemplatePage {
    private static final long serialVersionUID = 1L;

    private PanelAtualizarRepresentante panelAtualizarRepresentante;
    private PanelDadosRepresentante panelDadosRepresentante;
    private PanelDadosEntidade panelDadosEntidade;
    private PanelDadosTitular panelDadosTitular;
    private PanelBotoes panelBotoes;
    private PanelAprovacaoBotoes panelAprovacaoBotoes;
    private PanelAnexo panelAnexo;
    private PanelRadiosAprovarRecusarCadastro panelRadiosAprovarRecusarCadastro;
    private TextField<String> fieldCnpj;
    private Modal<String> modalConfirmUf;
    private Modal<String> modalLimparFormulario;
    private String msgConfirmUf = new String();

    private Form<EntidadeDto> form;
    private Page backPage;
    private Button buttonSalvar;
    private Button buttonAprovar;
    private Button buttonRecusar;
    private AjaxButton buttonVerificarCnpj;
    private AjaxSubmitLink buttonCancelarEdicao;
    private Link link;

    private EntidadeDto entidade = new EntidadeDto();
    private Entidade entidadePrincipal = new Entidade();
    private List<EntidadeAnexo> listaAnexos = new ArrayList<EntidadeAnexo>();
    private Boolean readOnly = false;
    private boolean botaoAlterarSituacoesClicado = false;
    private Boolean cadastroNovo = false;
    private Boolean existeEntidade = false;
    private Boolean mostrarBotaoVisualizarCnpj = true;
    private Boolean mostrarBotaoCancelarEdicao = false;
    private Boolean aprovarCadastro;
    boolean ativarCamposDeAnalisarProposta = true;
    private AjaxCheckBox checkRepresentante;
    private Label labelMensdfsagem;
    private Model<String> mensagem = Model.of("");

    private List<PessoaEntidade> listaDeRepresentantes = new ArrayList<PessoaEntidade>();
    private List<PessoaEntidade> listaDeTitulares = new ArrayList<PessoaEntidade>();
    private List<PessoaEntidade> listaDeMembrosComissao = new ArrayList<PessoaEntidade>();

    private String numeroCnpj;

    @Inject
    private ComponentFactory componentFactory;

    @Inject
    private BeneficiarioService beneficiarioService;

    @Inject
    private GenericEntidadeService genericEntidadeService;

    @Inject
    private MailService mailService;

    @Inject
    private AnexoEntidadeService anexoService;

    @Inject
    private SegurancaService segurancaService;

    public BeneficiarioPage(final PageParameters pageParameters) {
        super(pageParameters);

        initiVariaveis();
        initComponentes();

        String titulo = "";
        if (readOnly) {
            titulo = "Visualizar Beneficiário";
        } else {
            titulo = "Cadastrar Beneficiário";
        }

        setTitulo(titulo);

        boolean acionarCamposDeInput = false;
        if (cadastroNovo) {
            acionarCamposDeInput = false;
            buttonSalvar.setEnabled(false);
        } else {
            if (readOnly) {
                acionarCamposDeInput = false;
                fieldCnpj.setEnabled(false);
                numeroCnpj = entidadePrincipal.getNumeroCnpj();
                buttonSalvar.setEnabled(false);
            } else {
                acionarCamposDeInput = true;
                fieldCnpj.setEnabled(false);
                numeroCnpj = entidadePrincipal.getNumeroCnpj();
                mostrarBotaoCancelarEdicao = true;
            }
            buttonVerificarCnpj.setVisibilityAllowed(false);
        }

        panelDadosEntidade.setEnabled(acionarCamposDeInput);
        panelDadosTitular.setEnabled(acionarCamposDeInput);
        panelDadosRepresentante.setEnabled(acionarCamposDeInput);
        panelAnexo.setEnabled(acionarCamposDeInput);
    }

    // Este constutor é o primeiro a ser chamado, virá com os paineis todos
    // desabilitados permitindo somente a digitação do CNPJ
    public BeneficiarioPage(final PageParameters pageParameters, Page backPage, Entidade entidadePrincipal, Boolean readOnly, Boolean botaoAlterarSituacoesClicado) {
        super(pageParameters);

        this.backPage = backPage;
        this.entidadePrincipal = entidadePrincipal;
        this.readOnly = readOnly;
        this.botaoAlterarSituacoesClicado = botaoAlterarSituacoesClicado;
        initiVariaveis();
        inicializarAsListasDePessoas();
        initComponentes();

        String titulo = "";
        if (readOnly) {
            titulo = "Visualizar Beneficiário";
        } else {
            if (cadastroNovo) {
                titulo = "Cadastrar Beneficiário";
            } else {
                titulo = "Editar Beneficiário";
            }
        }

        setTitulo(titulo);

        boolean acionarCamposDeInput = false;
        if (cadastroNovo) {
            acionarCamposDeInput = false;
            buttonSalvar.setEnabled(false);
        } else {
            if (readOnly) {
                acionarCamposDeInput = false;
                fieldCnpj.setEnabled(false);
                numeroCnpj = entidadePrincipal.getNumeroCnpj();
                buttonSalvar.setEnabled(false);
            } else {
                acionarCamposDeInput = entidadePrincipal.getOrigemCadastro() != null && entidadePrincipal.getOrigemCadastro() == EnumOrigemCadastro.CADASTRO_EXTERNO ? false : true;
                fieldCnpj.setEnabled(false);
                numeroCnpj = entidadePrincipal.getNumeroCnpj();
                mostrarBotaoCancelarEdicao = true;
            }
            buttonVerificarCnpj.setVisibilityAllowed(false);
        }

        panelDadosEntidade.setEnabled(acionarCamposDeInput);
    }

    // Construtor chamado depois que for digitado o CNPJ da entidade, será
    // verificado se já existe entidade ou não
    public BeneficiarioPage(final PageParameters pageParameters, Page backPage, Entidade entidadePrincipal, Boolean readOnly, Boolean existeEntidade, String numeroCnpj) {
        super(pageParameters);

        this.backPage = backPage;
        this.entidadePrincipal = entidadePrincipal;
        this.readOnly = readOnly;
        this.existeEntidade = existeEntidade;
        mostrarBotaoVisualizarCnpj = false;
        mostrarBotaoCancelarEdicao = true;

        initiVariaveis();
        inicializarAsListasDePessoas();
        initComponentes();

        if (existeEntidade) {
            setTitulo("Editar Beneficiário");
            setMsgConfirmUf("Entidade já Cadastrada, os dados foram carregados.");
            modalConfirmUf.show(true);
            fieldCnpj.setEnabled(false);
        } else {
            setTitulo("Cadastrar Beneficiário");
            fieldCnpj.setEnabled(false);
        }
        this.numeroCnpj = numeroCnpj;
    }

    private void initiVariaveis() {
        if (entidadePrincipal != null && entidadePrincipal.getId() != null) {
            List<PessoaEntidade> representante = new ArrayList<PessoaEntidade>();
            List<PessoaEntidade> titular = new ArrayList<PessoaEntidade>();
            cadastroNovo = false;

            titular = beneficiarioService.buscarTitularEntidade(entidadePrincipal);
            if (titular != null && !titular.isEmpty() && titular.get(0).getPessoa().getPossuiFuncaoDeRepresentante()) {
                representante = titular;
            } else {
                representante = beneficiarioService.buscarRepresentanteEntidade(entidadePrincipal, false);
            }

            listaAnexos = SideUtil.convertAnexoDtoToEntityEntidadeAnexo(anexoService.buscarPeloIdEntidade(entidadePrincipal.getId()));
            entidade.setEntidadeTitular(titular == null || titular.isEmpty() ? new Pessoa() : titular.get(0).getPessoa());
            entidade.setEntidadeRepresentante(representante == null || representante.isEmpty() ? new Pessoa() : representante.get(0).getPessoa());

            entidade.setId(entidadePrincipal.getId());
            entidade.setBairro(entidadePrincipal.getBairro());
            entidade.setComplementoEndereco(entidadePrincipal.getComplementoEndereco());
            entidade.setDataAlteracao(entidadePrincipal.getDataAlteracao());
            entidade.setDataCadastro(entidadePrincipal.getDataCadastro());
            entidade.setDescricaoEndereco(entidadePrincipal.getDescricaoEndereco());
            entidade.setEmail(entidadePrincipal.getEmail());
            entidade.setMunicipio(entidadePrincipal.getMunicipio());
            entidade.setNomeEntidade(entidadePrincipal.getNomeEntidade());
            entidade.setNumeroCep(entidadePrincipal.getNumeroCep());
            entidade.setNumeroCnpj(entidadePrincipal.getNumeroCnpj());
            entidade.setNumeroEndereco(entidadePrincipal.getNumeroEndereco());
            entidade.setNumeroTelefone(entidadePrincipal.getNumeroTelefone());
            entidade.setNumeroFoneFax(entidadePrincipal.getNumeroFoneFax());
            entidade.setNumeroProcessoSEI(entidadePrincipal.getNumeroProcessoSEI());
            entidade.setPersonalidadeJuridica(entidadePrincipal.getPersonalidadeJuridica());
            entidade.setStatusEntidade(entidadePrincipal.getStatusEntidade());
            entidade.setTipoEndereco(entidadePrincipal.getTipoEndereco());
            entidade.setTipoEntidade(entidadePrincipal.getTipoEntidade());
            entidade.setOrigemCadastro(entidadePrincipal.getOrigemCadastro());
            entidade.setUsuarioAlteracao(entidadePrincipal.getUsuarioAlteracao());
            entidade.setUsuarioCadastro(entidadePrincipal.getUsuarioCadastro());
            entidade.setOrigemCadastro(entidadePrincipal.getOrigemCadastro());
        } else {
            cadastroNovo = true;
            entidade.setOrigemCadastro(EnumOrigemCadastro.CADASTRO_INTERNO);
        }

        entidade.setUsuario(getUsuarioLogadoDaSessao());
        entidade.setBotaoAlterarSituacoesClicado(botaoAlterarSituacoesClicado);
        verificarSeFoiFeitaAnaliseEmCasoDeCadastroExterno();
    }

    private void initComponentes() {
        form = componentFactory.newForm("form", new CompoundPropertyModel<EntidadeDto>(entidade));
        add(form);

        criarBreadcrump();
        form.add(getTextFieldCnpj()); // numeroCnpj
        form.add(getButtonVerificarCnpj()); // btnVerificarCnpj
        form.add(getButtonCancelarEdicao()); // btnCancelarEdicao

        form.add(panelAtualizarRepresentante = new PanelAtualizarRepresentante("panelAtualizarRepresentante"));
        form.add(panelDadosEntidade = new PanelDadosEntidade("panelDadosEntidade", this, form.getModelObject()));
        form.add(panelDadosTitular = new PanelDadosTitular("panelDadosTitular", this, cadastroNovo, form.getModelObject(), listaDeTitulares, listaDeRepresentantes, listaDeMembrosComissao, readOnly));

        form.add(panelAnexo = new PanelAnexo("panelAnexo", this, listaAnexos, form.getModelObject(), readOnly));

        form.add(panelBotoes = new PanelBotoes("panelBotoes"));
        panelBotoes.setVisible(entidade.getOrigemCadastro() == EnumOrigemCadastro.CADASTRO_INTERNO);

        form.add(panelAprovacaoBotoes = new PanelAprovacaoBotoes("panelAprovacaoBotoes"));
        panelAprovacaoBotoes.setVisible(entidade.getOrigemCadastro() == EnumOrigemCadastro.CADASTRO_EXTERNO);

        form.add(panelRadiosAprovarRecusarCadastro = new PanelRadiosAprovarRecusarCadastro("panelRadiosAprovarRecusarCadastro"));
        panelRadiosAprovarRecusarCadastro.setVisible(entidade.getOrigemCadastro() == EnumOrigemCadastro.CADASTRO_EXTERNO);

        modalConfirmUf = newModal("modalConfirmUf");
        modalConfirmUf.show(false);
        modalLimparFormulario = newModalLimparFormulario("modalLimparFormulario");
        modalLimparFormulario.show(false);

        form.add(modalLimparFormulario);
        form.add(modalConfirmUf);
        add(form);
    }

    private void criarBreadcrump() {
        link = new Link("homePage") {
            @Override
            public void onClick() {
                setResponsePage(HomePage.class);
            }
        };
        form.add(link);

        form.add(new Link("beneficiarioPage") {
            @Override
            public void onClick() {
                setResponsePage(backPage);
            }
        });
    }

    private class PanelAtualizarRepresentante extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelAtualizarRepresentante(String id) {
            super(id);
            setOutputMarkupId(true);
            add(panelDadosRepresentante = new PanelDadosRepresentante("panelDadosRepresentante", form.getModelObject(), listaDeRepresentantes, listaDeTitulares, listaDeMembrosComissao, cadastroNovo, readOnly));
        }
    }

    private class PanelBotoes extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelBotoes(String id) {
            super(id);
            setOutputMarkupId(true);
            add(getButtonSalvar()); // btnSalvar
            add(getButtonVoltar()); // btnVoltar
        }
    }

    private class PanelAprovacaoBotoes extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelAprovacaoBotoes(String id) {
            super(id);
            setOutputMarkupId(true);

            add(getButtonAprovar()); // btnAprovar
            add(getButtonVoltarAprovacao()); // btnVoltarAprovacao
        }
    }

    private class PanelRadiosAprovarRecusarCadastro extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelRadiosAprovarRecusarCadastro(String id) {
            super(id);
            setOutputMarkupId(true);

            add(getRadioAprovarRecursar()); // radioAprovarRecusar
            add(getLabelMotivoRecusa()); // lblMotivoRecusa
            add(getTextAreaMotivoRecusa()); // textAreaMotivoRecusa
            add(getButtonReenviarEmail()); // btnReenviarEmail
        }
    }

    // COMPONENTES
    public TextField<String> getTextFieldCnpj() {

        fieldCnpj = componentFactory.newTextField("numeroCnpj", "CNPJ", false, new PropertyModel(this, "numeroCnpj"));
        fieldCnpj.add(StringValidator.maximumLength(18));
        actionTextFieldCnpj(fieldCnpj);
        fieldCnpj.setOutputMarkupId(true);
        return fieldCnpj;
    }

    private Button getButtonSalvar() {
        buttonSalvar = new AjaxButton("btnSalvar") {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                actionSalvarBeneficiario(target);
            }
        };
        buttonSalvar.setEnabled(!readOnly);
        return buttonSalvar;
    }

    private Button getButtonVoltar() {
        Button button = componentFactory.newButton("btnVoltar", () -> actionSair());
        button.setDefaultFormProcessing(false);
        return button;
    }

    private Button getButtonVoltarAprovacao() {
        Button button = componentFactory.newButton("btnVoltarAprovacao", () -> actionSair());
        button.setDefaultFormProcessing(false);
        return button;
    }

    private Button getButtonAprovar() {
        buttonAprovar = new AjaxButton("btnAprovar") {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                actionAprovarBeneficiario(target);
            }
        };
        authorize(buttonAprovar, RENDER, BeneficiarioPesquisaPage.ROLE_MANTER_BENEFICIARIO_VALIDAR_CADASTRO);
        buttonAprovar.setEnabled(true);
        buttonAprovar.setEnabled(ativarCamposDeAnalisarProposta);
        return buttonAprovar;
    }

    private InfraAjaxFallbackLink<Void> getButtonReenviarEmail() {
        InfraAjaxFallbackLink<Void> btnReenviar = componentFactory.newAjaxFallbackLink("btnReenviarEmail", (target) -> actionReenviarEmail(target));
        btnReenviar.setVisible(mostrarBotaoReeviar());
        return btnReenviar;
    }

    private boolean mostrarBotaoReeviar() {
        if (ativarCamposDeAnalisarProposta) {
            return false;
        } else {
            return true;
        }
    }

    public Button getButtonVerificarCnpj() {
        buttonVerificarCnpj = new AjaxButton("btnVerificarCnpj") {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                verificarCnpj(target);
            }
        };
        buttonVerificarCnpj.setOutputMarkupId(true);
        buttonVerificarCnpj.setVisible(mostrarBotaoVisualizarCnpj);
        return buttonVerificarCnpj;
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

    private Modal<String> newModal(String id) {
        Modal<String> modal = new TextContentModal(id, new LambdaModel<String>(this::getMsgConfirmUf, this::setMsgConfirmUf));
        modal.setBackdrop(Backdrop.STATIC);
        modal.addButton(newButtonFecharModal(modal));
        return modal;
    }

    private Modal<String> newModalLimparFormulario(String id) {
        Modal<String> modal = new TextContentModal(id, new LambdaModel<String>(this::getMsgConfirmUf, this::setMsgConfirmUf));
        modal.setBackdrop(Backdrop.STATIC);
        modal.addButton(newButtonLimparFormulario(modal));
        modal.addButton(newButtonFecharModal(modal));
        return modal;
    }

    private InfraRadioChoice<Boolean> getRadioAprovarRecursar() {
        InfraRadioChoice<Boolean> radioAprovar = componentFactory.newRadioChoice("radioAprovarRecusar", "Aprovar Recusar", true, false, "", "", new PropertyModel<Boolean>(this, "aprovarCadastro"), Arrays.asList(Boolean.TRUE, Boolean.FALSE), (target) -> actionAprovarRecusarCadastro(target));
        radioAprovar.setChoiceRenderer(new IChoiceRenderer<Boolean>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Object getDisplayValue(Boolean object) {
                if (object != null && object) {
                    return "Aprovado";
                } else {
                    return "Recusado";
                }
            }

            @Override
            public String getIdValue(Boolean object, int index) {
                return object.toString();
            }
        });
        radioAprovar.setEnabled(true);
        radioAprovar.setEnabled(ativarCamposDeAnalisarProposta);
        return radioAprovar;
    }

    private Label getLabelMotivoRecusa() {
        Label lbl = new Label("lblMotivoRecusa", "* Informe o motivo da não aprovação");
        lbl.setOutputMarkupId(true);
        lbl.setVisible(aprovarCadastro != null && !aprovarCadastro);
        return lbl;
    }

    private TextArea<String> getTextAreaMotivoRecusa() {

        TextArea<String> textAreaMotivoRecusa = new TextArea<String>("textAreaMotivoRecusa", new PropertyModel<String>(entidadePrincipal, "motivoValidacao"));
        textAreaMotivoRecusa.setLabel(Model.of("Descrição"));
        textAreaMotivoRecusa.setOutputMarkupId(true);
        textAreaMotivoRecusa.add(StringValidator.maximumLength(Constants.TAMANHO_CARACTERES_CAMPO_TEXT_AREA));
        textAreaMotivoRecusa.setVisible(aprovarCadastro != null && !aprovarCadastro);
        textAreaMotivoRecusa.setEnabled(ativarCamposDeAnalisarProposta);
        return textAreaMotivoRecusa;
    }

    // AÇÕES

    private void verificarSeFoiFeitaAnaliseEmCasoDeCadastroExterno() {
        if (entidadePrincipal.getOrigemCadastro() != null && entidadePrincipal.getOrigemCadastro() == EnumOrigemCadastro.CADASTRO_EXTERNO && entidadePrincipal.getValidacaoCadastro() != EnumValidacaoCadastro.NAO_ANALISADO) {

            aprovarCadastro = entidadePrincipal.getValidacaoCadastro() == EnumValidacaoCadastro.VALIDADO ? true : false;
            ativarCamposDeAnalisarProposta = false;
        }
    }

    private void actionAprovarRecusarCadastro(AjaxRequestTarget target) {
        panelRadiosAprovarRecusarCadastro.addOrReplace(getTextAreaMotivoRecusa());
        panelRadiosAprovarRecusarCadastro.addOrReplace(getLabelMotivoRecusa());
        target.add(panelRadiosAprovarRecusarCadastro);
    }

    private void inicializarAsListasDePessoas() {
        // Se estiver editando o titular
        if (entidadePrincipal.getId() != null) {
            ListasPorTipoPessoaDto listasPorTipoPessoaDto = genericEntidadeService.buscarPessoasPorTipo(entidadePrincipal);
            listaDeTitulares.addAll(listasPorTipoPessoaDto.getListaTitular());
            listaDeRepresentantes.addAll(listasPorTipoPessoaDto.getListaRepresentante());
            listaDeMembrosComissao.addAll(listasPorTipoPessoaDto.getListaMembroComissao());
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

    private AjaxDialogButton newButtonLimparFormulario(Modal<String> modal) {
        return new AjaxDialogButton(Model.of("OK Limpar"), Buttons.Type.Primary) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(new BeneficiarioPage(null, backPage, new Entidade(), false, botaoAlterarSituacoesClicado));
            }
        };
    }

    private void actionTextFieldCnpj(TextField<String> field) {
        AjaxFormComponentUpdatingBehavior onChangeAjaxBehavior = new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {

                // setar no model
            }
        };
        field.add(onChangeAjaxBehavior);
    }

    public void actionAprovarBeneficiario(AjaxRequestTarget target) {

        if (!validarValidacaoCadastro()) {
            target.focusComponent(link);
            return;
        }
        String mensagem = "";
        String urlBase = "";
        Long idPessoaReceberMensagemQuandoRecusado = pegarMenorIdRepresentanteAtual(listaDeRepresentantes);
        if (aprovarCadastro) {
            entidadePrincipal.setValidacaoCadastro(EnumValidacaoCadastro.VALIDADO);
            urlBase = getUrlBase(Constants.PAGINA_ALTERACAO_SENHA);
            mensagem = "Entidade aprovada com sucesso!";
        } else {
            entidadePrincipal.setValidacaoCadastro(EnumValidacaoCadastro.RECUSADO);
            urlBase = getUrlBase(Constants.PAGINA_ALTERACAO_CADASTRO_RECUSADO);
            mensagem = "Entidade recusada com sucesso!";
        }

        entidadePrincipal.setAnexos(listaAnexos);
        Entidade entidadePersistida = beneficiarioService.validarBeneficiarioExterno(entidadePrincipal, getUsuarioLogadoDaSessao().getNomeCompleto());
        mailService.enviarEmailAprovacaoReprovacaoCadastroEntidade(entidadePersistida, idPessoaReceberMensagemQuandoRecusado, urlBase);

        getSession().info(mensagem);

        setResponsePage(new BeneficiarioPage(new PageParameters(), backPage, entidadePrincipal, true, botaoAlterarSituacoesClicado));
    }

    private void actionReenviarEmail(AjaxRequestTarget target) {
        if (!validarValidacaoCadastro()) {
            target.focusComponent(link);
            return;
        }
        String mensagem = "";
        String urlBase = "";
        Long idPessoaReceberMensagemQuandoRecusado = pegarMenorIdRepresentanteAtual(listaDeRepresentantes);
        List<PessoaEntidade> lista = genericEntidadeService.buscarPessoa(entidadePrincipal);
        entidadePrincipal.setPessoas(lista);
        entidadePrincipal.setAnexos(listaAnexos);

        if (aprovarCadastro) {
            entidadePrincipal.setValidacaoCadastro(EnumValidacaoCadastro.VALIDADO);
            urlBase = getUrlBase(Constants.PAGINA_ALTERACAO_SENHA);
        } else {
            entidadePrincipal.setValidacaoCadastro(EnumValidacaoCadastro.RECUSADO);
            urlBase = getUrlBase(Constants.PAGINA_ALTERACAO_CADASTRO_RECUSADO);
        }

        mensagem = "E-Mail reenviado com sucesso!";

        entidadePrincipal.setAnexos(listaAnexos);
        Entidade entidadePersistida = beneficiarioService.revalidarBeneficiarioExterno(entidadePrincipal, getUsuarioLogadoDaSessao().getNomeCompleto());
        mailService.enviarEmailAprovacaoReprovacaoCadastroEntidade(entidadePersistida, idPessoaReceberMensagemQuandoRecusado, urlBase);

        getSession().info(mensagem);
        setResponsePage(new BeneficiarioPage(new PageParameters(), backPage, entidadePrincipal, true, botaoAlterarSituacoesClicado));
    }

    private boolean validarValidacaoCadastro() {

        if (aprovarCadastro == null) {
            addMsgError("O campo 'Situação do Cadastro' é obrigatório.");
            return false;
        }

        if (!aprovarCadastro) {
            if (entidadePrincipal.getMotivoValidacao() == null || "".equalsIgnoreCase(entidadePrincipal.getMotivoValidacao())) {
                addMsgError("Ao recusar um cadastro é obrigatório informar um motivo.");
                return false;
            }
        }
        return true;
    }

    public void actionSalvarBeneficiario(AjaxRequestTarget target) {

        if (!getSideSession().hasAnyRole(new String[] { BeneficiarioPesquisaPage.ROLE_MANTER_BENEFICIARIO_INCLUIR, BeneficiarioPesquisaPage.ROLE_MANTER_BENEFICIARIO_ALTERAR })) {
            throw new SecurityException();
        }

        List<PessoaEntidade> listaPessoa = montarListaTitularesERepresententesBeneficiario();
        EntidadeDto entidadeDto = form.getModelObject();

        if (!validarCamposEntidade(entidadeDto, listaPessoa, target)) {
            buttonCancelarEdicao.setMarkupId("btnCancelarInclusaoEdicao");
            target.focusComponent(link);
            return;
        }
        montarEntidadeParaSalvar(entidadeDto, listaPessoa);

        String usuarioLogado = getIdentificador();
        entidadePrincipal.setUsuarioAlteracao(usuarioLogado);

        Long maiorIdPessoaAtual = pegarMaiorIdPessoaAtual(entidadePrincipal.getPessoas());

        if (cadastroNovo) {
            entidadePrincipal.setOrigemCadastro(EnumOrigemCadastro.CADASTRO_INTERNO);
            entidadePrincipal.setValidacaoCadastro(EnumValidacaoCadastro.VALIDADO);
        }

        Entidade entidadePersistida = beneficiarioService.incluirAlterar(entidadePrincipal, usuarioLogado);
        mailService.enviarEmailCadastroNovaEntidadePorUsuarioInterno(entidadePersistida, maiorIdPessoaAtual, getUrlBase(Constants.PAGINA_ALTERACAO_SENHA));

        if (cadastroNovo) {
            getSession().info("Salvo com sucesso.");
        } else {
            getSession().info("Alterado com sucesso");
        }

        setResponsePage(new BeneficiarioPage(new PageParameters(), backPage, entidadePersistida, false, botaoAlterarSituacoesClicado));
    }

    private Long pegarMaiorIdPessoaAtual(List<PessoaEntidade> listaPessoasEntidade) {
        Long retorno = 0L;
        for (PessoaEntidade pessoaEntidade : listaPessoasEntidade) {
            if (pessoaEntidade.getPessoa().getId() != null) {
                if (pessoaEntidade.getPessoa().getId() > retorno) {
                    retorno = pessoaEntidade.getPessoa().getId();
                }
            }
        }
        return retorno;
    }

    private Long pegarMenorIdRepresentanteAtual(List<PessoaEntidade> listaPessoasEntidade) {
        Long retorno = 0L;
        int cont = 0;
        for (PessoaEntidade pessoaEntidade : listaPessoasEntidade) {
            if (cont == 0) {
                retorno = pessoaEntidade.getId();
                cont++;
            } else {
                if (pessoaEntidade.getPessoa().getId() != null && pessoaEntidade.getPessoa().isTitularComFuncaoDeRepesentante()) {
                    if (pessoaEntidade.getPessoa().getId() < retorno) {
                        retorno = pessoaEntidade.getPessoa().getId();
                    }
                }
            }
        }
        return retorno;
    }

    public void cancelarEdicao(AjaxRequestTarget target) {
        setMsgConfirmUf("Esta ação irá limpar todo o Formulário, deseja continuar?.");
        modalLimparFormulario.show(true);
        target.add(modalLimparFormulario);
    }

    public void montarEntidadeParaSalvar(EntidadeDto entidadeDto, List<PessoaEntidade> listaPessoa) {
        // adiciona a listaPessoa já com os titulares e representantes
        entidadePrincipal.setPessoas(listaPessoa);

        entidadePrincipal.setNumeroCnpj(limparCampos(numeroCnpj));
        entidadePrincipal.setTipoEntidade(entidadeDto.getTipoEntidade());
        entidadePrincipal.setNomeEntidade(entidadeDto.getNomeEntidade());
        entidadePrincipal.setPersonalidadeJuridica(entidadeDto.getPersonalidadeJuridica());
        entidadePrincipal.setMunicipio(entidadeDto.getMunicipio());
        entidadePrincipal.setTipoEndereco(entidadeDto.getTipoEndereco());
        entidadePrincipal.setDescricaoEndereco(entidadeDto.getDescricaoEndereco());
        entidadePrincipal.setNumeroEndereco(entidadeDto.getNumeroEndereco());
        entidadePrincipal.setComplementoEndereco(entidadeDto.getComplementoEndereco());
        entidadePrincipal.setBairro(entidadeDto.getBairro());
        entidadePrincipal.setNumeroCep(limparCampos(entidadeDto.getNumeroCep()));
        entidadePrincipal.setNumeroTelefone(limparCampos(entidadeDto.getNumeroTelefone()));
        entidadePrincipal.setStatusEntidade(entidadeDto.getStatusEntidade());
        entidadePrincipal.setNumeroProcessoSEI(limparCampos(entidadeDto.getNumeroProcessoSEI()));

        if (entidadeDto.getNumeroFoneFax() != null) {
            entidadePrincipal.setNumeroFoneFax(limparCampos(entidadeDto.getNumeroFoneFax()));
        } else {
            entidadePrincipal.setNumeroFoneFax(null);
        }

        entidadePrincipal.setEmail(entidadeDto.getEmail());
        entidadePrincipal.setAnexos(panelAnexo.getListAnexoTemp());
    }

    public void actionSair() {
        setResponsePage(backPage);
    }

    public boolean validarCamposEntidade(EntidadeDto entityTemp, List<PessoaEntidade> listaPessoa, AjaxRequestTarget target) {
        boolean validar = true;
        String msg = "";

        if (!validarAnexos()) {
            addMsgError("É necessário anexar ao menos 1 arquivo de cada tipo.");
            validar = false;
        }

        if (listaPessoa.size() == 0) {
            addMsgError("É necessário adicionar ao menos 1 Titular e 1 Representante ou  1 Titular com função de Representante.");
            validar = false;
        } else {
            boolean possuiRepresentante = false;
            boolean possuiTitular = false;
            boolean possuiTitularAtivo = Boolean.FALSE; // valida titular ativo
            for (PessoaEntidade ent : listaPessoa) {
                if (ent.getPessoa().getPossuiFuncaoDeRepresentante() || ent.getPessoa().isTitularComFuncaoDeRepesentante()) {
                    possuiRepresentante = true;
                }

                if (ent.getPessoa().isTitular()) {
                    possuiTitular = true;
                }

                // valida titular ativo
                if (ent.getPessoa().isTitular() && ent.getPessoa().getStatusPessoa().equals(EnumStatusPessoa.ATIVO)) {
                    possuiTitularAtivo = Boolean.TRUE;
                }
            }

            if (!possuiRepresentante) {
                addMsgError("Adicione alguém com função de representante ou um titular com função de representante.");
                validar = false;
            }

            if (!possuiTitular) {
                addMsgError("Adicione alguém com função de titular.");
                validar = false;
            }

            // valida titular ativo
            if (!possuiTitularAtivo) {
                addMsgError("É necessário adicionar ao menos 1 Titular Ativo.");
                validar = Boolean.FALSE;
            }
        }

        if (numeroCnpj == null || numeroCnpj.equalsIgnoreCase("")) {
            msg += "<p><li> O campo 'CNPJ' é obrigatório.</li><p />";
            validar = false;
        }

        if (entityTemp.getTipoEntidade() == null) {
            addMsgError("O campo 'Tipo' é obrigatório.");
            validar = false;
        }

        if (entityTemp.getNomeEntidade() == null || entityTemp.getNomeEntidade().equalsIgnoreCase("")) {
            addMsgError("O campo 'Nome' é obrigatório.");
            validar = false;
        }

        if (entityTemp.getPersonalidadeJuridica() == null) {
            addMsgError("O campo 'Natureza Jurídica' é obrigatório.");
            validar = false;
        }

        if (panelDadosEntidade.getNomeUf() == null || panelDadosEntidade.getNomeUf().getId() == null) {
            addMsgError("O campo 'Estado' é obrigatório.");
            validar = false;
        }
        if (entityTemp.getMunicipio() == null) {
            addMsgError("O campo 'Município' é obrigatório.");
            validar = false;
        }

        if (entityTemp.getTipoEndereco() == null) {
            addMsgError("O campo 'Tipo de Endereço' é obrigatório.");
            validar = false;
        }

        if (entityTemp.getDescricaoEndereco() == null || entityTemp.getDescricaoEndereco().equalsIgnoreCase("")) {
            addMsgError("O campo 'Endereço' é obrigatório.");
            validar = false;
        }

        if (entityTemp.getNumeroEndereco() == null || entityTemp.getNumeroEndereco().equalsIgnoreCase("")) {
            addMsgError("O campo 'Número' é obrigatório.");
            validar = false;
        }

        if (entityTemp.getBairro() == null || entityTemp.getBairro().equalsIgnoreCase("")) {
            addMsgError("O campo 'Bairro' é obrigatório.");
            validar = false;
        }

        if (entityTemp.getNumeroCep() == null || entityTemp.getNumeroCep().equalsIgnoreCase("")) {
            addMsgError("O campo 'CEP' é obrigatório.");
            validar = false;
        } else {
            if (limparCampos(entityTemp.getNumeroCep()).length() < 8) {
                addMsgError("O CEP deverá contar 8 caracteres númericos.");
                validar = false;
            }
        }

        if (entityTemp.getNumeroTelefone() == null || entityTemp.getNumeroTelefone().equalsIgnoreCase("")) {
            addMsgError("O campo 'Telefone' é obrigatório.");
            validar = false;
        } else {
            if (entityTemp.getNumeroTelefone().length() < 10) {
                addMsgError("O Telefone da Entidade deverá conter ao menos 10 números.");
                validar = false;
            }
        }

        if (entityTemp.getNumeroFoneFax() != null && limparCampos(entityTemp.getNumeroFoneFax()).length() < 10) {
            addMsgError("O Telefone/FAX da Entidade deverá conter ao menos 10 números");
            validar = false;
        }

        if (entityTemp.getEmail() == null || entityTemp.getEmail().equalsIgnoreCase("")) {
            addMsgError(" O campo 'E-Mail' é obrigatório.");
            validar = false;
        } else {
            validar = validarEmailUnico(validar, entityTemp.getEmail());
        }

        if (entityTemp.getNumeroProcessoSEI() == null || "".equalsIgnoreCase(entityTemp.getNumeroProcessoSEI())) {
            addMsgError("O campo 'Número do Processo (NUP)' é obrigatório.");
            validar = false;
        } else {
            if (entityTemp.getNumeroProcessoSEI().length() < 17) {
                addMsgError("O campo 'Nº Processo (NUP)' deverá conter 17 caracteres númericos.");
                validar = false;
            } else {
                EntidadePesquisaDto dto = new EntidadePesquisaDto();
                Entidade buscarNUP = new Entidade();
                buscarNUP.setNumeroProcessoSEI(limparCampos(entityTemp.getNumeroProcessoSEI()));
                dto.setEntidade(buscarNUP);
                dto.setUsuarioLogado(getUsuarioLogadoDaSessao());
                List<Entidade> lista = beneficiarioService.buscarSemPaginacao(dto);

                if (lista.size() > 0) {
                    if (entidadePrincipal == null || entidadePrincipal.getId() == null && lista.get(0).getId() != entityTemp.getId()) {
                        addMsgError("O 'Nº Processo (NUP)' informado já esta cadastrado no sistema.");
                        validar = false;
                    }
                }
            }
        }

        if (entityTemp.getStatusEntidade() == null) {
            addMsgError("O campo 'Ativar Entidade' é obrigatório.");
            validar = false;
        }

        if (!validar) {
            mensagem.setObject(msg);
        } else {
            mensagem.setObject("");
        }
        // target.add(labelMensagem);

        return validar;
    }

    public boolean validarEntidade(boolean validar) {
        validar = validarCnpj(entidade.getNumeroCnpj(), validar);

        int tamanhoCepEntidade = limparCampos(entidade.getNumeroCep()).length();
        if (tamanhoCepEntidade < 7) {
            addMsgError("O CEP da Entidade deverá conter 7 números.");
            validar = false;
        }

        int tamanhoTelefoneEntidade = limparCampos(entidade.getNumeroTelefone()).length();
        if (tamanhoTelefoneEntidade < 10) {
            addMsgError("O Telefone da Entidade deverá conter ao menos 10 números.");
            validar = false;
        }

        if (entidade.getNumeroFoneFax() != null || !"".equalsIgnoreCase(entidade.getNumeroFoneFax())) {
            int tamanhoFaxEntidade = limparCampos(entidade.getNumeroFoneFax()).length();
            if (tamanhoFaxEntidade < 10) {
                addMsgError("O Telefone/FAX da Entidade deverá conter ao menos 10 números.");
                validar = false;
            }
        }

        return validar;
    }

    public boolean validarTitular(boolean validar, Pessoa titular) {
        String cpfTitular = limparCampos(titular.getNumeroCpf());
        if (!CPFUtils.validate(cpfTitular)) {
            addMsgError("O CPF do Titular está em um formato inválido.");
            validar = false;
        }

        int tamanhoTelefoneTitular = limparCampos(titular.getNumeroTelefone()).length();
        if (tamanhoTelefoneTitular < 10) {
            addMsgError("O Telefone do Titular deverá conter ao menos 10 números.");
            validar = false;
        }

        String emailTitular = titular.getEmail();
        if (!EmailValidator.validate(emailTitular)) {
            addMsgError("O Email do Titular está em um formato inválido");
            validar = false;
        }

        return validar;
    }

    public boolean validarRepresentante(boolean validar, Pessoa representante) {
        String cpfRepresentante = representante.getNumeroCpf();
        if (!CPFUtils.validate(cpfRepresentante)) {
            addMsgError("O CPF do Representante está em um formato inválido");
            validar = false;
        }

        int tamanhoTelefoneRepresentante = limparCampos(representante.getNumeroTelefone()).length();
        if (tamanhoTelefoneRepresentante < 10) {
            addMsgError("O Telefone do Representante deverá conter ao menos 10 números.");
            validar = false;
        }

        String emailRepresentante = representante.getEmail();
        if (!EmailValidator.validate(emailRepresentante)) {
            addMsgError("O Email do Representante está em um formato inválido");
            validar = false;
        }

        return validar;
    }

    public boolean validarAnexos() {
        boolean validar = true;
        List<EnumTipoArquivoEntidade> tipoArquivo = new ArrayList<EnumTipoArquivoEntidade>();
        tipoArquivo.add(EnumTipoArquivoEntidade.NORMA_INSTITUTIVA);
        tipoArquivo.add(EnumTipoArquivoEntidade.COMPROVANTE_ENDERECO);
        tipoArquivo.add(EnumTipoArquivoEntidade.DOCUMENTACAO_PESSOAL);
        tipoArquivo.add(EnumTipoArquivoEntidade.DEFINICAO_COMPETENCIA_SERVIDOR);
        List<EnumTipoArquivoEntidade> tiposCadastrados = new ArrayList<EnumTipoArquivoEntidade>();

        for (EntidadeAnexo anexo : panelAnexo.getListAnexoTemp()) {
            if (!tiposCadastrados.contains(anexo.getTipoArquivo())) {
                tiposCadastrados.add(anexo.getTipoArquivo());
            }
        }

        if (tiposCadastrados.size() < tipoArquivo.size()) {
            validar = false;
        }

        return validar;
    }

    private boolean validarEmailUnico(boolean validar, String emailTemp) {

        if (!EmailValidator.validate(emailTemp)) {
            addMsgError("O E-Mail da Entidade está em um formato inválido");
            validar = false;
            return validar;
        }

        // Irá validar se este e-mail é unico entre as Entidades
        if (!validarEmailUnicoSalvoNoBancoDeDados(validar, emailTemp)) {
            validar = false;
            return validar;
        }
        return validar;
    }

    private boolean validarEmailUnicoSalvoNoBancoDeDados(boolean validar, String emailTemp) {
        EntidadePesquisaDto dto = new EntidadePesquisaDto();
        Entidade buscar = new Entidade();
        buscar.setEmail(emailTemp);
        dto.setEntidade(buscar);
        dto.setUsuarioLogado(getUsuarioLogadoDaSessao());
        List<Entidade> lista = genericEntidadeService.buscarSemPaginacao(dto);
        if (lista.size() > 0) {
            for (Entidade ent : lista) {
                if (ent.getEmail().equalsIgnoreCase(entidade.getEmail())) {
                    if (entidade.getId() == null && entidadePrincipal.getId() != ent.getId()) {
                        addMsgError("O E-Mail Informado na seção 'Dados da Entidade' já esta cadastrado no sistema para outra Entidade.");
                        validar = false;
                        break;
                    }
                }
            }
        }
        return validar;
    }

    public List<PessoaEntidade> montarListaTitularesERepresententesBeneficiario() {
        // Lista que conterá todos os titulares e representantes cadastrados
        List<PessoaEntidade> listaPessoa = new ArrayList<PessoaEntidade>();

        // Pega a lista de titulares adicionados e limpa os campos
        for (PessoaEntidade tit : listaDeTitulares) {
            tit.getPessoa().setNumeroCpf(limparCampos(tit.getPessoa().getNumeroCpf()));
            tit.getPessoa().setNumeroTelefone(limparCampos(tit.getPessoa().getNumeroTelefone()));
            listaPessoa.add(tit);
        }

        // Pega a lista de representantes adicionados e limpa os campos
        for (PessoaEntidade rep : listaDeRepresentantes) {
            if (rep.getPessoa().getTipoPessoa() == EnumTipoPessoa.REPRESENTANTE_ENTIDADE) {
                rep.getPessoa().setNumeroCpf(limparCampos(rep.getPessoa().getNumeroCpf()));
                rep.getPessoa().setNumeroTelefone(limparCampos(rep.getPessoa().getNumeroTelefone()));
                listaPessoa.add(rep);
            }
        }

        // Pega a lista de membros da comissão adicionados e limpa os campos
        for (PessoaEntidade rep : listaDeMembrosComissao) {
            if (rep.getPessoa().getTipoPessoa() == EnumTipoPessoa.MEMBRO_COMISSAO_RECEBIMENTO) {
                rep.getPessoa().setNumeroCpf(limparCampos(rep.getPessoa().getNumeroCpf()));
                rep.getPessoa().setNumeroTelefone(limparCampos(rep.getPessoa().getNumeroTelefone()));
                listaPessoa.add(rep);
            }
        }

        return listaPessoa;
    }

    public boolean validarCnpj(String cnpj, boolean validar) {
        boolean valido = validar;
        if (cnpj.length() < 14) {
            addMsgError("O 'CNPJ' deverá conter 14 digitos.");
            valido = false;
        } else {
            CnpjUtil cnpjValido = new CnpjUtil(cnpj);
            if (!cnpjValido.isCnpjValido()) {
                addMsgError("O 'CNPJ' informado esta em um formato inválido.");
                valido = false;
            }
        }
        return valido;
    }

    private void acaoCheck(AjaxRequestTarget target) {
        boolean checkLocal = checkRepresentante.getModelObject();

        if (checkLocal) {
            panelAtualizarRepresentante.setVisible(false);
        } else {
            panelAtualizarRepresentante.setVisible(true);
        }
        target.add(panelAtualizarRepresentante);
    }

    public void atualizarPaineisBaseadoNoCnpj(AjaxRequestTarget target, List<Entidade> entidade) {
        boolean possuiEntidade = false;
        if (entidade.size() > 0) {
            entidadePrincipal = entidade.get(0);
            possuiEntidade = true;
        } else {
            entidadePrincipal = new Entidade();
        }

        initiVariaveis();
        setResponsePage(new BeneficiarioPage(new PageParameters(), backPage, entidadePrincipal, false, possuiEntidade, numeroCnpj));
    }

    private void verificarCnpj(AjaxRequestTarget target) {
        boolean validar = true;
        if (numeroCnpj == null || "".equalsIgnoreCase(numeroCnpj)) {
            addMsgError("Informe um número de 'CNPJ' valido.");
            return;
        } else {
            String cnpj = limparCampos(numeroCnpj);
            validar = validarCnpj(cnpj, validar);
            if (!validar) {
                return;
            } else {

                EntidadePesquisaDto entidadePesquisaDto = new EntidadePesquisaDto();
                Entidade entidade = new Entidade();
                entidade.setNumeroCnpj(cnpj);

                entidadePesquisaDto.setEntidade(entidade);
                entidadePesquisaDto.setUsuarioLogado(getUsuarioLogadoDaSessao());

                List<Entidade> entidadeCnpj = new ArrayList<Entidade>();
                entidadeCnpj = genericEntidadeService.buscar(entidadePesquisaDto);

                atualizarPaineisBaseadoNoCnpj(target, entidadeCnpj);
            }
        }
    }

    public Usuario receberUsuarioLogado() {
        return getUsuarioLogadoDaSessao();
    }

    private String limparCampos(String valor) {
        String value = valor;
        value = value.replace(".", "");
        value = value.replace("/", "");
        value = value.replace("-", "");
        value = value.replace("(", "");
        value = value.replace(")", "");
        return value;
    }

    public Form<EntidadeDto> getForm() {
        return form;
    }

    public void setForm(Form<EntidadeDto> form) {
        this.form = form;
    }

    public PanelDadosRepresentante getPanelDadosRepresentante() {
        return panelDadosRepresentante;
    }

    public void setPanelDadosRepresentante(PanelDadosRepresentante panelDadosRepresentante) {
        this.panelDadosRepresentante = panelDadosRepresentante;
    }

    public PanelDadosEntidade getPanelDadosEntidade() {
        return panelDadosEntidade;
    }

    public void setPanelDadosEntidade(PanelDadosEntidade panelDadosEntidade) {
        this.panelDadosEntidade = panelDadosEntidade;
    }

    public PanelDadosTitular getPanelDadosTitular() {
        return panelDadosTitular;
    }

    public void setPanelDadosTitular(PanelDadosTitular panelDadosTitular) {
        this.panelDadosTitular = panelDadosTitular;
    }

    public Boolean getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(Boolean readOnly) {
        this.readOnly = readOnly;
    }

    public String getMsgConfirmUf() {
        return msgConfirmUf;
    }

    public void setMsgConfirmUf(String msgConfirmUf) {
        this.msgConfirmUf = msgConfirmUf;
    }
}
