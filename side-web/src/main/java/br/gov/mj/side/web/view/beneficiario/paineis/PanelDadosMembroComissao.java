package br.gov.mj.side.web.view.beneficiario.paineis;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.metadata.MetaDataRoleAuthorizationStrategy;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
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
import br.gov.mj.infra.wicket.component.pagination.InfraAjaxPagingNavigator;
import br.gov.mj.infra.wicket.model.LambdaModel;
import br.gov.mj.seg.entidades.Usuario;
import br.gov.mj.seg.entidades.enums.EnumTipoUsuario;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.entidade.Pessoa;
import br.gov.mj.side.entidades.entidade.PessoaEntidade;
import br.gov.mj.side.entidades.enums.EnumStatusPessoa;
import br.gov.mj.side.entidades.enums.EnumTipoPessoa;
import br.gov.mj.side.entidades.programa.inscricao.ComissaoRecebimento;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoPrograma;
import br.gov.mj.side.web.dto.EntidadeDto;
import br.gov.mj.side.web.dto.EntidadePesquisaDto;
import br.gov.mj.side.web.dto.UsuarioPessoaDto;
import br.gov.mj.side.web.service.BeneficiarioService;
import br.gov.mj.side.web.service.GenericEntidadeService;
import br.gov.mj.side.web.service.InscricaoProgramaService;
import br.gov.mj.side.web.service.SegurancaService;
import br.gov.mj.side.web.service.UsuarioService;
import br.gov.mj.side.web.util.CPFUtils;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.EmailValidator;
import br.gov.mj.side.web.view.components.AjaxDialogButton;
import br.gov.mj.side.web.view.programa.inscricao.membroComissaoRecebimento.CadastrarMembroComissaoPage;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal.Backdrop;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.TextContentModal;

public class PanelDadosMembroComissao extends Panel {
    private static final long serialVersionUID = 1L;

    private static final String ONCHANGE = "onchange";

    private Page backPage;

    private PanelSomenteCpf panelSomenteCpf;
    private PanelPrincipalTitular panelPrincipalTitular;
    private PanelDataView panelDataView;
    private WebMarkupContainer containerBotoes;
    private WebMarkupContainer containerModal;

    private AjaxButton buttonAdicionar;
    private Button buttonAdicionarMembro;
    private AjaxButton buttonSalvarEdicao;
    private AjaxSubmitLink buttonCancelarEdicao;
    private DataView<PessoaEntidade> dataView;
    private TextField<String> fieldCpf;
    private Label labelMensagem;
    private AjaxButton buttonVerificarCpf;
    private Model<String> mensagem = Model.of("");
    private Modal<String> modalPessoaEncontrada;
    private Modal<String> modalConfirmarExclusao;
    private String msgExclusao = new String();

    private EntidadeDto entidadeDto = new EntidadeDto();
    private Pessoa entidadeDtoTemp = new Pessoa();
    private Pessoa pessoaEncontradaPelaDigitacaoCpf = new Pessoa();

    private boolean modoEdicao = false;
    private boolean mostrarBotaoCancelarEdicao = false;
    private boolean mostrarBotaoEditarTitular = false;
    private boolean botaoVisualizarClicado;
    private boolean readOnly;
    private Integer posicaoPessoaLista;
    private String cpfTemporario = "";
    private String msg = "";
    private String msgPessoaEncontrada;

    private List<PessoaEntidade> listaDeTitulares;
    private List<PessoaEntidade> listaDeRepresentantes;
    private List<PessoaEntidade> listaMembrosComissao;
    List<InscricaoPrograma> listaInscricoes;

    private Long idPessoaClicada;
    private String nomePessoa;
    private String numeroCpf;
    private String numeroTelefone;
    private String email;
    private EnumTipoPessoa tipoPessoaClicada;
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
    private InscricaoProgramaService inscricaoService;

    @Inject
    private UsuarioService usuarioService;

    public PanelDadosMembroComissao(String id, EntidadeDto entidade, List<PessoaEntidade> listaDeMembrosComissao, List<PessoaEntidade> listaDeTitulares, List<PessoaEntidade> listaDeRepresentantes, boolean readOnly) {
        super(id);
        setOutputMarkupId(true);
        this.cadastroNovo = cadastroNovo;
        this.entidadeDto = entidade;
        this.listaMembrosComissao = listaDeMembrosComissao;
        this.listaDeTitulares = listaDeTitulares;
        this.listaDeRepresentantes = listaDeRepresentantes;
        this.readOnly = readOnly;

        initVariaveis();
        panelPrincipalTitular = new PanelPrincipalTitular("panelPrincipalTitular");
        panelPrincipalTitular.setVisible(false);
        add(panelPrincipalTitular);

        panelSomenteCpf = new PanelSomenteCpf("panelSomenteCpf");
        panelSomenteCpf.setVisible(false);
        add(panelSomenteCpf);

        panelDataView = new PanelDataView("panelDataView");
        panelDataView.setVisible(true);
        add(panelDataView);

        containerBotoes = getPanelBotoes();
        add(containerBotoes); // containerBotoes

        panelPrincipalTitular.setEnabled(false);
        buttonAdicionar.setVisible(false);

        modalPessoaEncontrada = newModal("modalPessoaEncontrada");
        modalPessoaEncontrada.show(false);
        add(modalPessoaEncontrada);

        containerModal = getPanelModalConfirmarAtivacaoDesativacao();
        add(containerModal);

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
            add(getTextFieldTelefone()); // entidadeTitular.numeroTelefone
            add(getTextFieldEmail()); // entidadeTitular.email
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

    private WebMarkupContainer getPanelBotoes() {
        containerBotoes = new WebMarkupContainer("containerBotoes");
        containerBotoes.add(getButtonAdicionarMembro()); // btnAdicionarMembro
        containerBotoes.setOutputMarkupId(true);
        return containerBotoes;
    }

    private WebMarkupContainer getPanelModalConfirmarAtivacaoDesativacao() {
        containerModal = new WebMarkupContainer("containerModal");

        modalConfirmarExclusao = newModalConfirmarExclusao("modalAtivarDesativar","", null);
        modalConfirmarExclusao.show(false);
        containerModal.add(modalConfirmarExclusao);

        containerModal.setOutputMarkupId(true);
        return containerModal;
    }

    private void initVariaveis() {

        // Se estiver editando o titular
        if (entidadeDto.getId() != null) {

            // é necessário inicializar esta variavel para que os campos de
            // input do titular venham todos em branco
            entidadeDto.setEntidadeTitular(new Pessoa());
        }

        listaInscricoes = inscricaoService.buscarProgramasInscritosParaEntidade(entidadeDto.getEntidade());

    }

    private TextField<String> getTextFieldNome() {
        TextField<String> fieldNome = componentFactory.newTextField("entidadeTitular.nomePessoa", "Nome (Titular)", false, new PropertyModel(this, "nomePessoa"));
        fieldNome.add(StringValidator.maximumLength(200));
        actionTextField(fieldNome);
        fieldNome.setEnabled(mostrarTextFieldNomeETelefone());
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

    private TextField<String> getTextFieldTelefone() {
        TextField<String> field = componentFactory.newTextField("entidadeTitular.numeroTelefone", "Telefone (Titular)", false, new PropertyModel(this, "numeroTelefone"));
        field.add(StringValidator.maximumLength(13));
        actionTextField(field);
        field.setEnabled(mostrarTextFieldNomeETelefone());
        return field;
    }

    private TextField<String> getTextFieldEmail() {

        TextField<String> field = componentFactory.newTextField("entidadeTitular.email", "E-Mail (Titular)", false, new PropertyModel(this, "email"));
        field.add(StringValidator.maximumLength(200));
        actionTextField(field);
        field.setEnabled(mostrarTextFieldEmail);
        return field;
    }

    private DataView<PessoaEntidade> getDataViewResultado() {
        dataView = new DataView<PessoaEntidade>("dataTitulares", new TitularProvider()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<PessoaEntidade> item) {

                item.add(new Label("pessoa.nomePessoa"));
                item.add(new Label("pessoa.numeroCpf", CPFUtils.format(item.getModelObject().getPessoa().getNumeroCpf())));
                item.add(new Label("pessoa.numeroTelefone"));
                item.add(new Label("pessoa.email"));
                item.add(new Label("pessoa.statusPessoa"));

                item.add(getButtonReativar(item, false));// btnReativar
                item.add(getButtonEditar(item, false));
                item.add(getButtonVisualizar(item, true));

                dadoAindaNaoSalvoNoBanco = item.getModelObject().getPessoa().getId() == null ? true : false;

                item.add(getButtonExcluir(item)); // btnExcluir
            }
        };
        dataView.setItemsPerPage(Constants.ITEMS_PER_PAGE_PAGINATION);
        dataView.setOutputMarkupId(true);
        return dataView;
    }

    private String mostrarBotaoDesabilitarMembro(Item<PessoaEntidade> item) {

        dadoAindaNaoSalvoNoBanco = item.getModelObject().getId() == null ? true : false;
        
        String botaoMostrar = "excluir";

        if(item.getModelObject().getPessoa().getStatusPessoa() == EnumStatusPessoa.ATIVO)
        {
            botaoMostrar = "inativar";
        }else if(item.getModelObject().getPessoa().getStatusPessoa() == EnumStatusPessoa.INATIVO){
            botaoMostrar = "ativar";
        }else{
            botaoMostrar = "excluir";
        }
        
        if (dadoAindaNaoSalvoNoBanco) {
            return botaoMostrar;
        }

        List<ComissaoRecebimento> listaComissao = new ArrayList<ComissaoRecebimento>();
        for (InscricaoPrograma ins : listaInscricoes) {
            listaComissao = inscricaoService.buscarComissaoRecebimento(ins);

            // Se esta pessoa estiver vinculada a algum programa o mesmo somente
            // poderá ser
            // ativado ou inativado
            for (ComissaoRecebimento comissao : listaComissao) {
                int idPessoa = item.getModelObject().getPessoa().getId().intValue();
                int idPessoaListaComissao = comissao.getMembroComissao().getId().intValue();

                if (idPessoa == idPessoaListaComissao) {
                    if (item.getModelObject().getPessoa().getStatusPessoa() == EnumStatusPessoa.ATIVO) {
                        botaoMostrar = "aviso";
                    }
                    break;
                }
            }
        }
        return botaoMostrar;
    }

    private Button getButtonAdicionarMembro() {
        buttonAdicionarMembro = new AjaxButton("btnAdicionarMembro") {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                mostrarTodosPaineis(target);
            }
        };
        buttonAdicionarMembro.setEnabled(!readOnly);
        return buttonAdicionarMembro;
    }

    public Button getButtonAdicionar() {
        buttonAdicionar = new AjaxButton("btnAdicionar") {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                adicionar(target);
            }
        };
        addAuthorization(buttonAdicionar, RENDER, CadastrarMembroComissaoPage.ROLE_MANTER_MEMBRO_COMISSAO_INCLUIR);
        buttonAdicionar.setVisible(mostrarBotaoAdicionarRepresentante);
        buttonAdicionar.setOutputMarkupId(true);
        return buttonAdicionar;
    }

    public AjaxSubmitLink getButtonEditar(Item<PessoaEntidade> item, Boolean modoVisualizar) {
        AjaxSubmitLink button = new AjaxSubmitLink("btnEditar") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form form) {
                botaoVisualizarClicado = false;
                acaoVisualizarEditar(target, item);
            }
        };
        addAuthorization(button, RENDER, CadastrarMembroComissaoPage.ROLE_MANTER_MEMBRO_COMISSAO_EDITAR);
        button.setDefaultFormProcessing(false);
        button.setEnabled(ativarBotaoEditarTitular());
        return button;
    }

    public AjaxSubmitLink getButtonVisualizar(Item<PessoaEntidade> item, boolean modoVisualizar) {
        AjaxSubmitLink button = new AjaxSubmitLink("btnVisualizar") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form form) {
                botaoVisualizarClicado = true;
                acaoVisualizarEditar(target, item);
            }
        };
        addAuthorization(buttonAdicionar, RENDER, CadastrarMembroComissaoPage.ROLE_MANTER_MEMBRO_COMISSAO_INCLUIR);
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

    private Button getButtonExcluir(Item<PessoaEntidade> item) {
        AjaxButton buttonEditar = new AjaxButton("btnExcluir") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                excluirTitular(target, item);
            }
        };
        buttonEditar.setOutputMarkupId(true);
        return buttonEditar;
    }

    public InfraAjaxConfirmButton getButtonDesabilitar(Item<PessoaEntidade> item) {
        return componentFactory.newAJaxConfirmButton("btnInativar", "MSG009", null, (target, formz) -> desabilitarMembro(target, item));
    }

    public InfraAjaxConfirmButton getButtonAtivar(Item<PessoaEntidade> item) {
        return componentFactory.newAJaxConfirmButton("btnAtivar", "MSG010", null, (target, formz) -> ativarMembro(target, item));
    }

    public AjaxSubmitLink getButtonReativar(Item<PessoaEntidade> item, Boolean modoVisualizar) {
        AjaxSubmitLink button = new AjaxSubmitLink("btnReativar") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form form) {
                acaoReativarBeneficiario(target, item, modoVisualizar);
            }
        };
        button.setDefaultFormProcessing(false);
        button.setEnabled(ativarBotaoEditarTitular());
        button.setVisible(true);
        button.setVisible(mostrarBotaoReativarBeneficiario(item));
        return button;
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

    private Modal<String> newModal(String id) {
        Modal<String> modal = new TextContentModal(id, new LambdaModel<String>(this::getMsgPessoaEncontrada, this::setMsgPessoaEncontrada));
        modal.setBackdrop(Backdrop.STATIC);
        modal.addButton(newButtonAdicionarBeneficiario(modal));
        modal.addButton(newButtonFecharModal(modal, "Não"));
        return modal;
    }

    private PessoaEntidade pessoaTemp;

    private Modal<String> newModalConfirmarExclusao(String id, String acaoASerFeita,Item<PessoaEntidade> item) {

        EnumStatusPessoa statusAtual = EnumStatusPessoa.ATIVO;
        if (item != null) {
            statusAtual = item.getModelObject().getPessoa().getStatusPessoa();
        }

        if ("ativar".equalsIgnoreCase(acaoASerFeita)) {
            setMsgExclusao("Atualmente o status dele é 'INATIVO'. Deseja ativá-lo?");
        } else {
            if ("aviso".equalsIgnoreCase(acaoASerFeita)) {
                setMsgExclusao("Este Membro de Comissão não pode ser excluido pois está vinculado a uma comissão de recebimento, exclua-o primeiramente.");
            }else{
                setMsgExclusao("Atualmente o status dele é 'ATIVO'. Deseja Desativar ou Excluir o membro de comissão?");
            }
        }
 
        Modal<String> modal = new TextContentModal(id, new LambdaModel<String>(this::getMsgExclusao, this::setMsgExclusao));
        modal.setBackdrop(Backdrop.STATIC);
        modal.addButton(newButtonAtivarMembro(modal, item).setVisible("ativar".equalsIgnoreCase(acaoASerFeita)));
        modal.addButton(newButtonDesativarMembro(modal, item).setVisible("inativar".equalsIgnoreCase(acaoASerFeita)));
        modal.addButton(newButtonExcluirMembro(modal, item).setVisible("inativar".equalsIgnoreCase(acaoASerFeita)));
        modal.addButton(newButtonFecharModal(modal, "Fechar"));
        return modal;
    }

    private AjaxDialogButton newButtonAtivarMembro(Modal<String> modal, Item<PessoaEntidade> item) {
        return new AjaxDialogButton(Model.of("Ativar"), Buttons.Type.Primary) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                item.getModelObject().getPessoa().setStatusPessoa(EnumStatusPessoa.ATIVO);
                atualizarTabelaAposAtivarDesativarPessoa(target, modal);
            }
        };
    }

    private AjaxDialogButton newButtonDesativarMembro(Modal<String> modal, Item<PessoaEntidade> item) {
        return new AjaxDialogButton(Model.of("Desativar"), Buttons.Type.Primary) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                item.getModelObject().getPessoa().setStatusPessoa(EnumStatusPessoa.INATIVO);
                atualizarTabelaAposAtivarDesativarPessoa(target, modal);
            }
        };
    }
    
    private AjaxDialogButton newButtonExcluirMembro(Modal<String> modal, Item<PessoaEntidade> item) {
        return new AjaxDialogButton(Model.of("Excluir"), Buttons.Type.Primary) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                excluirMembro(modal,item,target);
            }
        };
    }

    // AÇÕES

    private void excluirMembro(Modal<String> modal,Item<PessoaEntidade> item, AjaxRequestTarget target){
        Pessoa itemSelecionadoExcluir = item.getModelObject().getPessoa();

        for (PessoaEntidade entidade : listaMembrosComissao) {
            if (entidade.getPessoa().getNumeroCpf().equalsIgnoreCase(itemSelecionadoExcluir.getNumeroCpf())) {
                listaMembrosComissao.remove(entidade);
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
        modal.show(false);
        modal.close(target);
    }
    
    private void atualizarTabelaAposAtivarDesativarPessoa(AjaxRequestTarget target, Modal<String> modal) {
        panelDataView.addOrReplace(getDataViewResultado());
        target.add(panelDataView);
        modal.show(false);
        modal.close(target);
    }

    private void addAuthorization(Component component, Action action, String... roles) {
        String s = StringUtils.join(roles, ",");
        MetaDataRoleAuthorizationStrategy.authorize(component, action, s);
    }

    private void acaoReativarBeneficiario(AjaxRequestTarget target, Item<PessoaEntidade> item, Boolean modoVisualizar) {
        Usuario alterado = usuarioService.resetarExpiracaoData(item.getModelObject().getPessoa().getUsuario());

        for (PessoaEntidade p : listaMembrosComissao) {
            if (p.getPessoa().getUsuario().getId() == alterado.getId().intValue()) {
                p.getPessoa().getUsuario().setDataExpiracaoSenha(alterado.getDataExpiracaoSenha());
                break;
            }
        }

        panelDataView.addOrReplace(getDataViewResultado());
        target.add(panelDataView);
    }

    private void atualizarBotoesAposVerificarCpf(AjaxRequestTarget target) {
        mostrarBotaoVerificarCpf = false;
        mostrarTextFieldEmail = mostrarInputTextDeEmail(pessoaEncontradaPelaDigitacaoCpf);
        mostrarTextFieldVerificarCpf = false;
        mostrarBotaoAdicionarRepresentante = true;
        panelPrincipalTitular.setEnabled(true);
        mostrarBotaoCancelarEdicao = true;
        mostrarBotaoEditarTitular = false;

        atualizarInputs(target);
    }

    private AjaxDialogButton newButtonFecharModal(Modal<String> modal, String lblModal) {
        return new AjaxDialogButton(Model.of(lblModal), Buttons.Type.Primary) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                cancelarEdicao(target);
                modal.show(false);
                modal.close(target);
            }
        };
    }

    private AjaxDialogButton newButtonAdicionarBeneficiario(Modal<String> modal) {
        return new AjaxDialogButton(Model.of("Sim"), Buttons.Type.Primary) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {

                nomePessoa = pessoaEncontradaPelaDigitacaoCpf.getNomePessoa();
                numeroTelefone = pessoaEncontradaPelaDigitacaoCpf.getNumeroTelefone();
                email = pessoaEncontradaPelaDigitacaoCpf.getEmail();
                tipoPessoaClicada = pessoaEncontradaPelaDigitacaoCpf.getTipoPessoa();
                idPessoaClicada = pessoaEncontradaPelaDigitacaoCpf.getId();

                modal.show(false);
                modal.close(target);
                atualizarBotoesAposVerificarCpf(target);
            }
        };
    }

    private boolean mostrarDataView() {
        if (listaMembrosComissao.size() == 0) {
            return false;
        } else {
            return true;
        }
    }

    private void ocultarTodosPaineis(AjaxRequestTarget target) {
        panelPrincipalTitular.setVisible(false);
        panelSomenteCpf.setVisible(false);
        containerBotoes.setVisible(true);
        buttonAdicionar.setVisible(false);

        target.add(panelPrincipalTitular);
        target.add(panelSomenteCpf);
        target.add(panelDataView);
        target.add(containerBotoes);
        target.add(buttonAdicionar);
    }

    private void mostrarTodosPaineis(AjaxRequestTarget target) {
        panelPrincipalTitular.setVisible(true);
        panelSomenteCpf.setVisible(true);
        panelDataView.setVisible(true);
        containerBotoes.setVisible(false);
        buttonAdicionar.setVisible(true);

        target.add(panelPrincipalTitular);
        target.add(panelSomenteCpf);
        target.add(panelDataView);
        target.add(containerBotoes);
        target.add(buttonAdicionar);
    }

    private boolean ativarTextFieldCpf() {
        if (readOnly) {
            return false;
        } else {
            return mostrarTextFieldVerificarCpf;
        }
    }

    private boolean mostrarBotaoReativarBeneficiario(Item<PessoaEntidade> item) {

        if (item.getModelObject().getPessoa().getUsuario() == null) {
            return false;
        }
        LocalDate expirar = item.getModelObject().getPessoa().getUsuario().getDataExpiracaoSenha();
        LocalDate atual = LocalDate.now();

        if (item.getModelObject().getPessoa().getTipoPessoa() == EnumTipoPessoa.REPRESENTANTE_ENTIDADE && atual.isAfter(expirar) ||
                item.getModelObject().getPessoa().getTipoPessoa() == EnumTipoPessoa.MEMBRO_COMISSAO_RECEBIMENTO && atual.isAfter(expirar)) {
            return true;
        } else {
            return false;
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
        if (readOnly) {
            return false;
        } else {
            return mostrarTextFieldVerificarCpf;
        }
    }

    private boolean mostrarBotaoDeVerificarCpf() {
        if (readOnly) {
            return false;
        } else {
            return mostrarBotaoVerificarCpf;
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

        PessoaEntidade pe = new PessoaEntidade();
        pe.setPessoa(entidade);
        listaMembrosComissao.add(pe);

        panelPrincipalTitular.setEnabled(false);
        mostrarBotaoAdicionarRepresentante = false;
        mostrarBotaoCancelarEdicao = false;
        mostrarBotaoEditarTitular = false;
        mostrarBotaoVerificarCpf = true;
        mostrarTextFieldEmail = true;
        mostrarTextFieldVerificarCpf = true;
        modoEdicao = true;

        zerarCamposDadosBasicos();
        ocultarTodosPaineis(target);
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
        entidadeDtoTemp.setNumeroTelefone(numeroTelefone);
        entidadeDtoTemp.setEmail(email);

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

        ocultarTodosPaineis(target);
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

        ocultarTodosPaineis(target);
        atualizarInputs(target);
    }

    private void desabilitarMembro(AjaxRequestTarget target, Item<PessoaEntidade> item) {
        item.getModelObject().getPessoa().setStatusPessoa(EnumStatusPessoa.INATIVO);
        panelDataView.addOrReplace(getDataViewResultado());
        target.add(panelDataView);
    }

    private void ativarMembro(AjaxRequestTarget target, Item<PessoaEntidade> item) {
        item.getModelObject().getPessoa().setStatusPessoa(EnumStatusPessoa.ATIVO);
        panelDataView.addOrReplace(getDataViewResultado());
        target.add(panelDataView);
    }

    public void excluirTitular(AjaxRequestTarget target, Item<PessoaEntidade> item) {

        String tipoBotaoMostrar = mostrarBotaoDesabilitarMembro(item);
       

            modalConfirmarExclusao = newModalConfirmarExclusao("modalAtivarDesativar",tipoBotaoMostrar, item);
            containerModal.addOrReplace(modalConfirmarExclusao);
            modalConfirmarExclusao.show(true);
            pessoaTemp = item.getModelObject();
            target.add(containerModal);
         
         if (!"excluir".equalsIgnoreCase(tipoBotaoMostrar)) {
            return;
        }        
    }

    public void zerarCamposDadosBasicosSemCpf() {
        nomePessoa = "";
        numeroTelefone = "";
        email = "";
        ativo = null;
    }

    public void zerarCamposDadosBasicos() {
        nomePessoa = "";
        numeroCpf = "";
        numeroTelefone = "";
        email = "";
        ativo = null;
        posicaoPessoaLista = null;
        tipoPessoaClicada = null;
    }

    private void atualizarInputs(AjaxRequestTarget target) {
        panelSomenteCpf.addOrReplace(getTextFieldCpf());
        panelSomenteCpf.addOrReplace(getButtonVerificarCpf());
        panelPrincipalTitular.addOrReplace(getTextFieldNome());
        panelPrincipalTitular.addOrReplace(getTextFieldTelefone());
        panelPrincipalTitular.addOrReplace(getTextFieldEmail());
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
        Usuario usuarioLogado = entidadeDto.getUsuario();
        
        if(usarioClicado.getTipoPessoa() == EnumTipoPessoa.MEMBRO_COMISSAO_RECEBIMENTO){
            mostrar = true;
        }else{
            if (usarioClicado.getId() == null || verificarSeOUsuarioLogadoPossuiFuncaoDeUsuarioInterno(usuarioLogado)) {
                mostrar = true;
            } else {
                if (usarioClicado == null || usarioClicado.getUsuario() == null) {
                    mostrar = false;
                } else {
                    if (usuarioLogado != null && usuarioLogado.getId() == usarioClicado.getUsuario().getId()) {
                        mostrar = true;
                    } else {
                        mostrar = false;
                    }
                }
            }
        }
        return mostrar;
    }

    public boolean validarEditarPessoa(AjaxRequestTarget target) {
        boolean validar = true;
        msg = "";

        // validar = validarCpfCompleto(validar);
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
            if (validar) {
                validar = validarCPf(numeroCpf, validar);
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

        if (numeroTelefone == null || "".equalsIgnoreCase(numeroTelefone)) {
            msg += "<p><li> O campo 'Telefone' é obrigatório.</li><p />";
            validar = false;
        }

        if (email == null || "".equalsIgnoreCase(email)) {
            msg += "<p><li> O campo 'E-Mail' é obrigatório.</li><p />";
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
        for (PessoaEntidade entidadeExterna : listaMembrosComissao) {
            if (entidadeExterna.getPessoa().getEmail().equalsIgnoreCase(email)) {
                if (entidadeExterna.getPessoa().getId() != entidadeDtoTemp.getId()) {
                    msg += "<p><li> O E-Mail informado já está cadastrado na lista de membros da comissão de recebimento.</li><p />";
                    validar = false;
                    break;
                }
            }
            contador++;
        }

        for (PessoaEntidade entidadeExterna : listaDeRepresentantes) {
            if (entidadeExterna.getPessoa().getEmail().equalsIgnoreCase(email) && posicaoPessoaLista != null) {
                msg += "<p><li> O E-Mail informado já está cadastrado na lista de Representantes.</li><p />";
                validar = false;
                break;
            }
        }

        for (PessoaEntidade entidadeExterna : listaDeTitulares) {
            if (entidadeExterna.getPessoa().getEmail().equalsIgnoreCase(email) && posicaoPessoaLista != null) {
                msg += "<p><li> O E-Mail informado já está cadastrado na lista de Titulares.</li><p />";
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
                msg += "<p><li> O 'E-Mail' já está em uso.</li><p />";
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
    
    private boolean mostrarTextFieldNomeETelefone(){
        if(readOnly){
            return false;
        }else{
            if(tipoPessoaClicada == EnumTipoPessoa.TITULAR || tipoPessoaClicada == EnumTipoPessoa.REPRESENTANTE_ENTIDADE){
                return false;
            }else{
                if(botaoVisualizarClicado){
                    return false;
                }else{
                    return true;
                }
            }
        }
    }

    private void acaoVisualizarEditar(AjaxRequestTarget target, Item<PessoaEntidade> item) {
        modoEdicao = true;
        entidadeDtoTemp = item.getModelObject().getPessoa();

        idPessoaClicada = entidadeDtoTemp.getId();
        nomePessoa = entidadeDtoTemp.getNomePessoa();
        numeroCpf = entidadeDtoTemp.getNumeroCpf();
        numeroTelefone = entidadeDtoTemp.getNumeroTelefone();
        email = entidadeDtoTemp.getEmail();
        tipoPessoaClicada = entidadeDtoTemp.getTipoPessoa();

        cpfTemporario = entidadeDtoTemp.getNumeroCpf();
        posicaoPessoaLista = item.getIndex();
        botaoEditarClicado = true;

        mostrarBotaoCancelarEdicao = true;
        mostrarBotaoVerificarCpf = false;
        mostrarTextFieldVerificarCpf = false;
        mostrarTextFieldEmail = mostrarInputTextDeEmail(entidadeDtoTemp);
        mostrarBotaoAdicionarRepresentante = false;
        buttonAdicionar.setVisible(true);

        if (botaoVisualizarClicado) {
            panelPrincipalTitular.setEnabled(false);
            mostrarBotaoEditarTitular = false;
        } else {
            panelPrincipalTitular.setEnabled(true);
            mostrarBotaoEditarTitular = true;
        }

        mostrarTodosPaineis(target);
        atualizarInputs(target);
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
            for (PessoaEntidade k : listaMembrosComissao) {
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
            return listaMembrosComissao.size();
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
        List<Entidade> lista = genericService.buscarSemPaginacao(enti);

        for (Entidade entit : lista) {
            for (PessoaEntidade pe : entit.getPessoas()) {
                if (pe.getPessoa().getNumeroCpf().equalsIgnoreCase(CPFUtils.clean(numeroCpf))) {
                    if (entidadeDtoTemp == null || entidadeDtoTemp.getId() == null || pe.getPessoa().getId().intValue() != entidadeDtoTemp.getId().intValue()) {
                        if (pe.getEntidade().getId().intValue() != entidadeDto.getEntidade().getId().intValue()) {

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
                }
            }
        }
        pessoaEncontradaPelaDigitacaoCpf = new Pessoa();

        for (PessoaEntidade pe : listaDeTitulares) {

            String emailCadastrado = CPFUtils.clean(pe.getPessoa().getNumeroCpf());
            String cpfDigitado = CPFUtils.clean(numeroCpf);
            if (emailCadastrado.equalsIgnoreCase(cpfDigitado)) {

                if (pe.getPessoa().getStatusPessoa() == EnumStatusPessoa.INATIVO) {
                    msg += "<p><li> Este CPF está cadastrado como 'Titular', porém está inativo.</li><p />";
                    mensagem.setObject(msg);
                    target.add(labelMensagem);
                    return;
                }

                pessoaEncontradaPelaDigitacaoCpf = pe.getPessoa();
                setMsgPessoaEncontrada("Este CPF já está cadastrado como 'Titular', deseja associá-lo também a lista de membros de comissão de recebimento?");
                modalPessoaEncontrada.show(true);
                target.add(modalPessoaEncontrada);
            }
        }

        for (PessoaEntidade pe : listaDeRepresentantes) {

            String emailCadastrado = CPFUtils.clean(pe.getPessoa().getNumeroCpf());
            String cpfDigitado = CPFUtils.clean(numeroCpf);
            if (emailCadastrado.equalsIgnoreCase(cpfDigitado)) {

                if (pe.getPessoa().getStatusPessoa() == EnumStatusPessoa.INATIVO) {
                    msg += "<p><li> Este CPF está cadastrado como 'Representante', porém está inativo.</li><p />";
                    mensagem.setObject(msg);
                    target.add(labelMensagem);
                    return;
                }

                pessoaEncontradaPelaDigitacaoCpf = pe.getPessoa();
                setMsgPessoaEncontrada("Este CPF já está cadastrado como 'Representante', deseja associá-lo também a lista de membros de comissão de recebimento?");
                modalPessoaEncontrada.show(true);
                target.add(modalPessoaEncontrada);
            }
        }

        for (PessoaEntidade pe : listaMembrosComissao) {
            String emailCadastrado = CPFUtils.clean(pe.getPessoa().getNumeroCpf());
            String cpfDigitado = CPFUtils.clean(numeroCpf);
            if (emailCadastrado.equalsIgnoreCase(cpfDigitado)) {
                msg += "<p><li> Este CPF já está cadastrado na lista.</li><p />";
                mensagem.setObject(msg);
                target.add(labelMensagem);
                return;
            }
        }

        mostrarBotaoVerificarCpf = false;
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
        entidade.setNumeroTelefone(numeroTelefone);
        entidade.setEmail(email);
        entidade.setStatusPessoa(entidade.getStatusPessoa() == null ? EnumStatusPessoa.ATIVO : entidade.getStatusPessoa());

        if (idPessoaClicada == null) {
            entidade.setTipoPessoa(EnumTipoPessoa.MEMBRO_COMISSAO_RECEBIMENTO);
        } else {
            entidade.setTipoPessoa(tipoPessoaClicada);
        }

        return entidade;
    }

    public static String formatCpf(String cpf) {
        StringBuilder builder = new StringBuilder(cpf.replaceAll("[^\\d]", ""));
        builder.insert(3, '.');
        builder.insert(7, '.');
        builder.insert(11, '-');
        return builder.toString();
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

    public String getMsgPessoaEncontrada() {
        return msgPessoaEncontrada;
    }

    public void setMsgPessoaEncontrada(String msgPessoaEncontrada) {
        this.msgPessoaEncontrada = msgPessoaEncontrada;
    }

    public String getMsgExclusao() {
        return msgExclusao;
    }

    public void setMsgExclusao(String msgExclusao) {
        this.msgExclusao = msgExclusao;
    }
}
