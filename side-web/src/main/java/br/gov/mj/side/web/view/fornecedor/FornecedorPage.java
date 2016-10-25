package br.gov.mj.side.web.view.fornecedor;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.validator.StringValidator;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.model.LambdaModel;
import br.gov.mj.seg.entidades.Usuario;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.entidade.EntidadeAnexo;
import br.gov.mj.side.entidades.entidade.Pessoa;
import br.gov.mj.side.entidades.entidade.PessoaEntidade;
import br.gov.mj.side.entidades.enums.EnumOrigemCadastro;
import br.gov.mj.side.entidades.enums.EnumPerfilEntidade;
import br.gov.mj.side.entidades.enums.EnumPersonalidadeJuridica;
import br.gov.mj.side.entidades.enums.EnumStatusEntidade;
import br.gov.mj.side.entidades.enums.EnumStatusPessoa;
import br.gov.mj.side.entidades.enums.EnumTipoPessoa;
import br.gov.mj.side.entidades.enums.EnumValidacaoCadastro;
import br.gov.mj.side.web.dto.EntidadePesquisaDto;
import br.gov.mj.side.web.service.AnexoEntidadeService;
import br.gov.mj.side.web.service.FornecedorService;
import br.gov.mj.side.web.service.GenericEntidadeService;
import br.gov.mj.side.web.service.MailService;
import br.gov.mj.side.web.util.CPFUtils;
import br.gov.mj.side.web.util.CnpjUtil;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.EmailValidator;
import br.gov.mj.side.web.util.SideUtil;
import br.gov.mj.side.web.view.HomePage;
import br.gov.mj.side.web.view.components.AjaxDialogButton;
import br.gov.mj.side.web.view.contrato.ContratoPage;
import br.gov.mj.side.web.view.fornecedor.paineis.DadosFornecedorPanel;
import br.gov.mj.side.web.view.fornecedor.paineis.PanelAnexo;
import br.gov.mj.side.web.view.fornecedor.paineis.PanelDadosPessoaFornecedor;
import br.gov.mj.side.web.view.planejarLicitacao.PlanejamentoLicitacaoPage;
import br.gov.mj.side.web.view.template.TemplatePage;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal.Backdrop;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.TextContentModal;

/**
 * Classe destinada a pagina de cadastro de fornecedores
 * 
 * @author diego.mota & ronald.calazans
 *
 */

@AuthorizeInstantiation({ PlanejamentoLicitacaoPage.ROLE_MANTER_PLANEJAR_LICITACAO_VISUALIZAR, PlanejamentoLicitacaoPage.ROLE_MANTER_PLANEJAR_LICITACAO_INCLUIR, PlanejamentoLicitacaoPage.ROLE_MANTER_PLANEJAR_LICITACAO_ALTERAR, PlanejamentoLicitacaoPage.ROLE_MANTER_PLANEJAR_LICITACAO_EXCLUIR })
public class FornecedorPage extends TemplatePage {
    private static final long serialVersionUID = 1L;

    public static final String ROLE_MANTER_FORNECEDOR_VISUALIZAR = "manter_fornecedor:visualizar";
    public static final String ROLE_MANTER_FORNECEDOR_INCLUIR = "manter_fornecedor:incluir";
    public static final String ROLE_MANTER_FORNECEDOR_ALTERAR = "manter_fornecedor:alterar";
    public static final String ROLE_FORNECEDOR_EXCLUIR = "manter_fornecedor:excluir";

    private Entidade fornecedor = new Entidade();
    private List<PessoaEntidade> representantes = new ArrayList<PessoaEntidade>();
    private List<PessoaEntidade> prepostos = new ArrayList<PessoaEntidade>();

    private PanelBreadcrump panelBreadcrump;
    private PanelBotoes panelBotoes;
    private PanelCnpj panelCnpj;

    private TextField<String> fieldCnpj;
    private AjaxButton buttonVerificarCnpj;
    private AjaxSubmitLink buttonCancelarEdicao;
    private Form<Entidade> form;
    private Button buttonSalvar;
    private AjaxSubmitLink buttonSalvarCadastro;
    private Page backPage;
    private Label lblBread;
    private DadosFornecedorPanel dadosFornecedorPanel;
    private PanelDadosPessoaFornecedor panelDadosRepresentanteLegal;
    private PanelDadosPessoaFornecedor panelDadosPreposto;
    private PanelAnexo panelAnexo;
    private Usuario usuarioLogado;
    private String frase = "";

    private Boolean readOnly = true;
    private Boolean cadastrarContrato = false;
    private Boolean deixarTodosPaineisHabilitados = true;
    private Boolean habilitarPanel = false;
    private Boolean textFieldCnpjHabilitar = true;
    private Boolean botaoVerificarCnpjVisible = true;
    private Boolean botaoLimparFormularioVisible = true;
    private String numeroCnpjPesquisa;
    private Label labelMensagem;
    private Model<String> mensagem = Model.of("");
    private String msg = "";
    private String numeroCnpj;

    // Modal
    private String msgConfirm;
    private Modal<String> modalConfirm;
    private Modal<String> modalLimparFormulario;

    private List<PessoaEntidade> listaDeRepresentante = new ArrayList<PessoaEntidade>();
    private List<PessoaEntidade> listaDePreposto = new ArrayList<PessoaEntidade>();
    private List<EntidadeAnexo> listaDeanexos = new ArrayList<EntidadeAnexo>();

    // ###################################################################################
    // injeçãod e dependencia
    @Inject
    private ComponentFactory componentFactory;
    @Inject
    private GenericEntidadeService genericEntidadeService;
    @Inject
    private FornecedorService fornecedorService;
    @Inject
    private AnexoEntidadeService anexoService;
    @Inject
    private MailService mailService;
    @Inject
    private AnexoEntidadeService anexoEntidadeService;

    public FornecedorPage(final PageParameters pageParameters) {
        super(pageParameters);
        initComponents();
    }

    // Esta sendo usado este
    public FornecedorPage(final PageParameters pageParameters, Page backPage, Entidade entidade, boolean readOnly) {
        super(pageParameters);

        this.backPage = backPage;
        this.readOnly = readOnly;
        fornecedor = entidade;

        if (readOnly) {
            frase = "Visualizar Fornecedor";
        } else {
            if (fornecedor == null || fornecedor.getId() == null) {
                frase = "Cadastrar Fornecedor";
            } else {
                frase = "Editar Fornecedor";
            }
        }

        initVariaveis();
        initComponents();
    }

    // Construtor Chamado quando estivar na tela de cadastrar Contrato
    public FornecedorPage(final PageParameters pageParameters, Page backPage, Entidade entidade, boolean readOnly, boolean cadastrarContrato) {
        super(pageParameters);

        this.backPage = backPage;
        this.readOnly = readOnly;
        this.cadastrarContrato = cadastrarContrato;
        fornecedor = entidade;

        initVariaveis();
        initComponents();
    }

    private void initVariaveis() {
        if (fornecedor.getId() == null) {
            textFieldCnpjHabilitar = true;
            botaoVerificarCnpjVisible = true;
            botaoLimparFormularioVisible = false;
            deixarTodosPaineisHabilitados = false;
        } else {
            textFieldCnpjHabilitar = false;
            botaoVerificarCnpjVisible = false;
            botaoLimparFormularioVisible = true;
            deixarTodosPaineisHabilitados = true;
            numeroCnpjPesquisa = fornecedor.getNumeroCnpj();

            pesquisarRepresentantes();
            listaDeanexos = SideUtil.convertAnexoDtoToEntityEntidadeAnexo(anexoService.buscarPeloIdEntidade(fornecedor.getId()));

        }

    }

    // Busca todos os representantes cadastros e os divide em Representante
    // legal e Prepostos
    private void pesquisarRepresentantes() {
        List<PessoaEntidade> listaDerepresentantes = genericEntidadeService.buscarPessoa(fornecedor);
        fornecedor.setPessoas(listaDerepresentantes);

        for (PessoaEntidade p : fornecedor.getPessoas()) {
            if (p.getPessoa().getTipoPessoa().equals(EnumTipoPessoa.PREPOSTO_FORNECEDOR)) {
                prepostos.add(p);
            } else {
                representantes.add(p);
            }
        }
    }

    /**
     * Este construtor deve ser utilizado para edição/exbição, passando a
     * entidade a ser editada/exibida no campo entidade
     * 
     * @param pageParameters
     * @param backPage
     * @param entidade
     */
    public FornecedorPage(final PageParameters pageParameters, Page backPage, Entidade entidade, Boolean readOnly) {
        super(pageParameters);
        this.backPage = backPage;
        this.readOnly = readOnly;

        fornecedor = entidade;
        for (PessoaEntidade p : fornecedor.getPessoas()) {
            if (p.getPessoa().getTipoPessoa().equals(EnumTipoPessoa.PREPOSTO_FORNECEDOR)) {
                prepostos.add(p);
            } else {
                representantes.add(p);
            }
        }
        initComponents();

    }

    private void initComponents() {

        usuarioLogado = getUsuarioLogadoDaSessao();
        setTitulo(frase);

        form = new Form<Entidade>("form", new CompoundPropertyModel<Entidade>(fornecedor));
        form.setOutputMarkupId(true);

        form.add(panelBreadcrump = new PanelBreadcrump("panelBreadcrump"));
        form.add(panelCnpj = new PanelCnpj("panelCnpj"));
        form.add(newDadosFornecedorPanel());
        form.add(panelDadosRepresentanteLegal = newPanelDadosRepresentanteLegal("panelRepresentantes", "Representante Legal", EnumTipoPessoa.REPRESENTANTE_LEGAL, representantes, prepostos));
        form.add(panelDadosPreposto = newPanelDadosPreposto("panelPrepostos", "Prepostos", EnumTipoPessoa.PREPOSTO_FORNECEDOR, prepostos, representantes));
        form.add(panelAnexo = new PanelAnexo("panelAnexo", listaDeanexos, readOnly, deixarTodosPaineisHabilitados));
        form.add(panelBotoes = new PanelBotoes("panelBotoes"));

        // Se for um cadastro novo não mostrar o dropDown e setar Ativo.
        if (fornecedor == null || fornecedor.getId() == null) {
            form.getModelObject().setStatusEntidade(EnumStatusEntidade.ATIVA);
        }

        // Se estiver no modo cadastro de contrato desabilitar por completo este
        // painel
        if (cadastrarContrato) {
            panelCnpj.setEnabled(false);
            dadosFornecedorPanel.setEnabled(false);
            panelAnexo.setEnabled(false);
            panelBreadcrump.setVisible(false);
        }

        // Mensagens de erro
        labelMensagem = new Label("mensagemErro", mensagem);
        labelMensagem.setEscapeModelStrings(false);
        form.add(labelMensagem);

        // Modal
        modalConfirm = newModal("modalConfirm");
        modalConfirm.show(false);
        modalLimparFormulario = newModalLimparFormulario("modalLimparFormulario");
        modalLimparFormulario.show(false);

        form.add(modalConfirm);
        form.add(modalLimparFormulario);
        add(form);
    }

    // paineis

    private class PanelBreadcrump extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelBreadcrump(String id) {
            super(id);

            add(lblBread = new Label("lblBread", frase));

            Link link = new Link("homePage") {
                private static final long serialVersionUID = 1L;

                @Override
                public void onClick() {
                    setResponsePage(HomePage.class);
                }
            };
            add(link);

            add(new Link("fornecedorPesquisaPage") {
                private static final long serialVersionUID = 1L;

                @Override
                public void onClick() {
                    setResponsePage(backPage);
                }
            });
        }
    }

    private class PanelBotoes extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelBotoes(String id) {
            super(id);
            setOutputMarkupId(true);
            add(newButtonSalvar()); // btnSalvar
            add(newButtonVoltar()); // btnVoltar

            add(newButtonSalvarContrato());
        }
    }

    private class PanelCnpj extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelCnpj(String id) {
            super(id);
            setOutputMarkupId(true);

            // Se estiver no modo cadastro de contrato desabilitar por completo
            // este painel
            if (cadastrarContrato) {
                setEnabled(false);
            }

            add(newTextFieldCnpj()); // numeroCnpj
            add(newButtonVerificarCnpj()); // btnVerificarCnpj
            add(newButtonCancelarEdicao()); // btnCancelarEdicao
        }
    }

    private DadosFornecedorPanel newDadosFornecedorPanel() {
        dadosFornecedorPanel = new DadosFornecedorPanel("panelDadosFornecedor", form, fornecedor, readOnly, deixarTodosPaineisHabilitados);
        return dadosFornecedorPanel;
    }

    private PanelDadosPessoaFornecedor newPanelDadosRepresentanteLegal(String id, String tituloPanel, EnumTipoPessoa tipo, List<PessoaEntidade> pessoas, List<PessoaEntidade> segundaLista) {
        panelDadosRepresentanteLegal = new PanelDadosPessoaFornecedor(id, pessoas, segundaLista, this, tituloPanel, tipo, getUsuarioLogadoDaSessao(), readOnly, deixarTodosPaineisHabilitados);
        return panelDadosRepresentanteLegal;
    }

    private PanelDadosPessoaFornecedor newPanelDadosPreposto(String id, String tituloPanel, EnumTipoPessoa tipo, List<PessoaEntidade> pessoas, List<PessoaEntidade> segundaLista) {
        panelDadosPreposto = new PanelDadosPessoaFornecedor(id, pessoas, segundaLista, this, tituloPanel, tipo, getUsuarioLogadoDaSessao(), readOnly, deixarTodosPaineisHabilitados);
        return panelDadosPreposto;
    }

    // COMPONENTES
    private Modal<String> newModal(String id) {
        Modal<String> modal = new TextContentModal(id, new LambdaModel<String>(this::getMsgConfirm, this::setMsgConfirm));
        modal.setBackdrop(Backdrop.STATIC);
        modal.addButton(newButtonFecharModal(modal));
        return modal;
    }

    private Modal<String> newModalLimparFormulario(String id) {
        Modal<String> modal = new TextContentModal(id, new LambdaModel<String>(this::getMsgConfirm, this::setMsgConfirm));
        modal.setBackdrop(Backdrop.STATIC);
        modal.addButton(newButtonLimparFormulario(modal));
        modal.addButton(newButtonFecharModal(modal));
        return modal;
    }

    public TextField<String> newTextFieldCnpj() {
        fieldCnpj = componentFactory.newTextField("numeroCnpj", "CNPJ", false, new PropertyModel(this, "numeroCnpjPesquisa"));
        fieldCnpj.add(StringValidator.maximumLength(18));
        actionTextFieldCnpj(fieldCnpj);
        fieldCnpj.setOutputMarkupId(true);

        fieldCnpj.setVisible(true);
        fieldCnpj.setEnabled(textFieldCnpjHabilitar);
        return fieldCnpj;
    }

    private Button newButtonVoltar() {
        Button button = componentFactory.newButton("btnVoltar", () -> voltar());
        button.setDefaultFormProcessing(false);
        return button;
    }

    private Button newButtonSalvar() {
        buttonSalvar = componentFactory.newButton("btnSalvar", () -> salvar());
        buttonSalvar.setOutputMarkupId(true);
        buttonSalvar.setEnabled(!readOnly && deixarTodosPaineisHabilitados);
        buttonSalvar.setVisible(!cadastrarContrato);
        authorize(buttonSalvar, RENDER, ROLE_MANTER_FORNECEDOR_INCLUIR, ROLE_MANTER_FORNECEDOR_ALTERAR);
        return buttonSalvar;
    }

    private AjaxSubmitLink newButtonSalvarContrato() {

        buttonSalvarCadastro = new AjaxSubmitLink("btnSalvarCadastro") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                salvar();
                actionSalvarCadastro(target);
            }
        };
        buttonSalvarCadastro.setOutputMarkupId(true);
        buttonSalvarCadastro.setVisible(cadastrarContrato);
        authorize(buttonSalvarCadastro, RENDER, ROLE_MANTER_FORNECEDOR_INCLUIR, ROLE_MANTER_FORNECEDOR_ALTERAR);
        return buttonSalvarCadastro;
    }

    public AjaxButton newButtonVerificarCnpj() {
        buttonVerificarCnpj = new AjaxButton("btnVerificarCnpj") {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                verificarCnpj(target);
            }
        };

        buttonVerificarCnpj.setOutputMarkupId(true);
        buttonVerificarCnpj.setVisible(botaoVerificarCnpjVisible);
        buttonVerificarCnpj.setDefaultFormProcessing(false);
        return buttonVerificarCnpj;
    }

    public AjaxSubmitLink newButtonCancelarEdicao() {
        buttonCancelarEdicao = new AjaxSubmitLink("btnCancelarEdicao") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form) {
                cancelarEdicao(target);
            }
        };

        buttonCancelarEdicao.setDefaultFormProcessing(false);
        buttonCancelarEdicao.setVisible(mostrarBotaoLimparFormulario());
        buttonCancelarEdicao.setOutputMarkupId(true);
        return buttonCancelarEdicao;
    }

    // ###################################################################################
    // AÇÕES

    // Esta ação somente será executada quando estiver criando um novo
    // representante a partir da tela de cadastro de contrato.
    private void actionSalvarCadastro(AjaxRequestTarget target) {
        ContratoPage page = (ContratoPage) backPage;

        List<PessoaEntidade> listaPessoas = new ArrayList<PessoaEntidade>();
        for (PessoaEntidade p1 : representantes) {
            PessoaEntidade pe = new PessoaEntidade();
            Pessoa pessoa = p1.getPessoa();
            Entidade entidade = p1.getEntidade();
            Long id = p1.getId();

            pe.setEntidade(entidade);
            pe.setPessoa(pessoa);
            pe.setId(id);
            listaPessoas.add(pe);
        }

        for (PessoaEntidade p1 : prepostos) {
            PessoaEntidade pe = new PessoaEntidade();
            Pessoa pessoa = p1.getPessoa();
            Entidade entidade = p1.getEntidade();
            Long id = p1.getId();

            pe.setEntidade(entidade);
            pe.setPessoa(pessoa);
            pe.setId(id);
            listaPessoas.add(pe);
        }

        page.getPanelDadosFornecedor().setListaDePessoasDesteFornecedor(listaPessoas);
        page.getPanelDadosFornecedor().atualizarListaDeRepresentantesAposAdicionarNovoFornecedor();
        page.getPanelDadosFornecedor().getPanelDropRepresentante().addOrReplace(page.getPanelDadosFornecedor().newDropDownRepresentante());
        page.getPanelDadosFornecedor().getPanelDropPreposto().addOrReplace(page.getPanelDadosFornecedor().newDropDownPreposto());

        target.add(page.getPanelDadosFornecedor().getPanelDropRepresentante());
        target.add(page.getPanelDadosFornecedor().getPanelDropRepresentante());

        setResponsePage(new FornecedorPage(getPageParameters(), backPage, fornecedor, false, true));
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
        AjaxDialogButton button = new AjaxDialogButton(Model.of("OK Limpar"), Buttons.Type.Primary) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(new FornecedorPage(getPageParameters(), backPage, new Entidade(), false));
            }
        };
        button.setOutputMarkupId(true);
        return button;
    }

    private void verificarCnpj(AjaxRequestTarget target) {

        EntidadePesquisaDto pesquisarCnpj = new EntidadePesquisaDto();
        pesquisarCnpj.setUsuarioLogado(usuarioLogado);

        Entidade enti = new Entidade();
        String cnpj = fieldCnpj.getRawInput();
        if (!validarCnpj(target, cnpj)) {
            mensagem.setObject(msg);
            target.add(labelMensagem);
            return;
        }

        enti.setNumeroCnpj(limparCampos(cnpj));

        pesquisarCnpj.setEntidade(enti);

        List<Entidade> encontrado = genericEntidadeService.buscar(pesquisarCnpj);

        boolean entidadeEncontradaEFornecedora = true;
        if (encontrado.size() > 0) {
            // Se a entidade encontrada já estiver cadastrada com beneficiário
            // não será permitido carrega-la aqui.
            if (encontrado.get(0).getPerfilEntidade() == EnumPerfilEntidade.BENEFICIARIO) {
                setMsgConfirm("O CNPJ informado esta sendo utilizado por uma entidade do tipo 'Beneficiario'.");
                entidadeEncontradaEFornecedora = false;
                modalConfirm.show(true);
            } else {
                buscarRepresentantesEPrepostos(target, encontrado);
                fornecedor = encontrado.get(0);
                List<EntidadeAnexo> lista = SideUtil.convertAnexoDtoToEntityEntidadeAnexo(anexoService.buscarPeloIdEntidade(encontrado.get(0).getId()));

                fornecedor.setAnexos(lista);
                listaDeanexos = lista;
                habilitarPanel = true;

                setMsgConfirm("Fornecedor já Cadastrado, os dados foram carregados.");
                modalConfirm.show(true);

                numeroCnpjPesquisa = fornecedor.getNumeroCnpj();
                numeroCnpj = numeroCnpjPesquisa;
            }
        } else {
            fornecedor = new Entidade();
            fornecedor.setNumeroCnpj(CPFUtils.clean(numeroCnpjPesquisa));
            numeroCnpj = numeroCnpjPesquisa;

            representantes.clear();
            prepostos.clear();
            panelAnexo.getListAnexoTemp().clear();

            form.setModelObject(fornecedor);
            form.getModelObject().setNumeroCnpj(CPFUtils.clean(numeroCnpjPesquisa));
        }

        if (fornecedor == null || fornecedor.getId() == null) {
            textFieldCnpjHabilitar = true;
        } else {
            textFieldCnpjHabilitar = false;
        }

        botaoVerificarCnpjVisible = false;
        botaoLimparFormularioVisible = true;
        deixarTodosPaineisHabilitados = true;
        readOnly = false;

        if (entidadeEncontradaEFornecedora) {
            atualizarTelaCompleta(target);
        } else {
            target.add(form);
        }
    }

    public void cancelarEdicao(AjaxRequestTarget target) {
        setMsgConfirm("Esta ação irá limpar todo o Formulário, deseja continuar?.");
        modalLimparFormulario.show(true);
        target.add(modalLimparFormulario);
    }

    private void buscarRepresentantesEPrepostos(AjaxRequestTarget target, List<Entidade> encontrado) {
        representantes.clear();
        prepostos.clear();

        form.setModelObject(encontrado.get(0));

        List<PessoaEntidade> listaDePessoas = new ArrayList<PessoaEntidade>();
        listaDePessoas = genericEntidadeService.buscarPessoa(encontrado.get(0));

        for (PessoaEntidade pe : listaDePessoas) {
            if (pe.getPessoa().getTipoPessoa() == EnumTipoPessoa.REPRESENTANTE_LEGAL) {
                representantes.add(pe);
                continue;
            }

            if (pe.getPessoa().getTipoPessoa() == EnumTipoPessoa.PREPOSTO_FORNECEDOR) {
                prepostos.add(pe);
                continue;
            }
        }
    }

    private void atualizarTelaCompleta(AjaxRequestTarget target) {
        form.addOrReplace(newDadosFornecedorPanel());
        form.addOrReplace(panelDadosRepresentanteLegal = newPanelDadosRepresentanteLegal("panelRepresentantes", "Representante Legal", EnumTipoPessoa.REPRESENTANTE_LEGAL, representantes, prepostos));
        form.addOrReplace(panelDadosPreposto = newPanelDadosPreposto("panelPrepostos", "Prepostos", EnumTipoPessoa.PREPOSTO_FORNECEDOR, prepostos, representantes));
        form.addOrReplace(panelAnexo = new PanelAnexo("panelAnexo", listaDeanexos, readOnly, deixarTodosPaineisHabilitados));

        fieldCnpj.setModelObject(numeroCnpj);

        panelCnpj.addOrReplace(newButtonVerificarCnpj());
        panelCnpj.addOrReplace(newTextFieldCnpj());
        panelCnpj.addOrReplace(newButtonCancelarEdicao());

        panelBotoes.addOrReplace(newButtonSalvar());

        target.appendJavaScript("atualizaCssDropDown();");
        target.add(panelBotoes);
        target.add(panelCnpj);
        target.add(form);
    }

    private boolean habilitarPanelDadosFornecedor() {
        if (readOnly) {
            return false;
        } else {
            if (habilitarPanel) {
                return true;
            } else {
                return false;
            }
        }
    }

    private void actionTextFieldCnpj(TextField<String> field) {
        AjaxFormComponentUpdatingBehavior onChangeAjaxBehavior = new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {

                // setar no model
            }
        };
        field.add(onChangeAjaxBehavior);
    }

    private void voltar() {
        setResponsePage(backPage);
    }

    private void salvar() {

        Long maiorIdPessoaAtual = pegarMaiorIdPessoaAtual(fornecedor.getPessoas());

        if (!validarCampos()) {
            return;
        }

        if (fornecedor.getStatusEntidade() == null) {
            fornecedor.setStatusEntidade(EnumStatusEntidade.ATIVA);
        }

        boolean editando = fornecedor.getId() == null ? false : true;

        fornecedor.setPessoas(new ArrayList<PessoaEntidade>());

        fornecedor.getPessoas().addAll(representantes);
        fornecedor.getPessoas().addAll(prepostos);
        fornecedor.setAnexos(panelAnexo.getListAnexoTemp());
        fornecedor.setPersonalidadeJuridica(EnumPersonalidadeJuridica.TODAS);

        if (!editando) {
            fornecedor.setOrigemCadastro(EnumOrigemCadastro.CADASTRO_INTERNO);
            fornecedor.setValidacaoCadastro(EnumValidacaoCadastro.VALIDADO);
        }

        inativarRepresentantePreposto();

        fornecedor = fornecedorService.incluirAlterar(fornecedor, usuarioLogado.getLogin());

        if (cadastrarContrato) {
            ContratoPage page = (ContratoPage) backPage;
            page.getPanelDadosFornecedor().setListaRepresentantes(representantes);
            page.getPanelDadosFornecedor().setListaPreposto(prepostos);
        }

        // A persistência poderá ser feita tanto pela tela do fornecedor
        // Quanto pela tela de cadastro de contrato
        // Se estiver na tela de cadastro de contrato não executar esta linha
        // abaixo

        if (editando) {
            getSession().info("Alterado com sucesso");
        } else {
            getSession().info("Salvo com sucesso.");
        }

        if (!cadastrarContrato) {
            setResponsePage(new FornecedorPage(getPageParameters(), backPage, fornecedor, false));
        }

        mailService.enviarEmailCadastroNovoFornecedor(fornecedor, maiorIdPessoaAtual, getUrlBase(Constants.PAGINA_ALTERACAO_SENHA));
    }

    // caso o fornecedor esteja setado como inativo, o metodo seta para inativo
    // os representantes e Prepostos.
    private void inativarRepresentantePreposto() {

        List<PessoaEntidade> listaPessoa = fornecedor.getPessoas();

        if (fornecedor.getStatusEntidade().equals(EnumStatusEntidade.INATIVA)) {
            for (int i = 0; i < listaPessoa.size(); i++) {
                listaPessoa.get(i).getPessoa().setStatusPessoa(EnumStatusPessoa.INATIVO);
            }
        }
        fornecedor.setPessoas(listaPessoa);
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

    private boolean validarCnpj(AjaxRequestTarget target, String cnpj) {
        boolean validar = true;
        validar = validarFormatoCnpj(validar, cnpj);
        msg = "";

        if (cnpj.length() < 14) {
            msg = "O 'CNPJ' deverá conter 14 caracteres.";
            return false;
        }

        if (!validar) {
            msg = "O 'CNPJ' informado está em um formato inválido.";
            return false;
        }

        mensagem.setObject(msg);
        target.add(labelMensagem);

        return validar;
    }

    private boolean validarFormatoCnpj(boolean valido, String cnpj) {
        CnpjUtil validar = new CnpjUtil(cnpj);
        valido = validar.isCnpjValido();
        return valido;
    }

    private boolean validarCnpj(String cnpj) {
        boolean validar = true;
        validar = validarFormatoCnpj(validar, cnpj);
        msg = "";

        if (cnpj.length() < 14) {
            addMsgError("O 'CNPJ' deverá conter 14 caracteres.");
            return false;
        }

        if (!validar) {
            addMsgError("O 'CNPJ' informado está em um formato inválido.");
            return false;
        }

        return validar;
    }

    private boolean validarCampos() {

        boolean camposBasicos = true;
        boolean cnpj = true;
        boolean email = true;
        boolean representantePreposto = true;

        camposBasicos = validarCamposBasicos();
        cnpj = validarCnpj();
        email = validarEmailUnico();
        representantePreposto = validarSePossuiRepresentanteEPreposto();

        if (camposBasicos && cnpj && email && representantePreposto) {
            return true;
        } else {
            return false;
        }
    }

    private boolean validarCamposBasicos() {
        boolean validar = true;

        if (dadosFornecedorPanel.getPanelUfMunicipio().getUfSelecionada() == null || dadosFornecedorPanel.getPanelUfMunicipio().getUfSelecionada().getId() == null) {
            addMsgError("O campo 'Estado' é obrigatório.");
            validar = false;
        }
        if (fornecedor.getMunicipio() == null) {
            addMsgError("O campo 'Município' é obrigatório.");
            validar = false;
        }

        if (limparCampos(fornecedor.getNumeroCep()).length() < 8) {
            addMsgError("O CEP deverá contar 8 caracteres númericos.");
            validar = false;
        }

        if (fornecedor.getNumeroTelefone() != null && fornecedor.getNumeroTelefone().length() < 10) {
            addMsgError("O Telefone do fornecedor deverá conter ao menos 10 números.");
            validar = false;
        }

        return validar;
    }

    private boolean validarEmailUnico() {

        boolean validar = true;

        if (!EmailValidator.validate(fornecedor.getEmail())) {
            addMsgError("O E-Mail da Entidade está em um formato inválido");
            validar = false;
            return validar;
        }

        // Irá validar se este e-mail é unico entre as Entidades
        if (!validarEmailUnicoSalvoNoBancoDeDados()) {
            validar = false;
            return validar;
        }
        return validar;
    }

    private boolean validarEmailUnicoSalvoNoBancoDeDados() {

        boolean validar = true;

        EntidadePesquisaDto dto = new EntidadePesquisaDto();
        Entidade buscar = new Entidade();
        buscar.setEmail(fornecedor.getEmail());
        dto.setEntidade(buscar);
        dto.setUsuarioLogado(getUsuarioLogadoDaSessao());
        List<Entidade> lista = genericEntidadeService.buscarSemPaginacao(dto);
        if (lista.size() > 0) {
            for (Entidade ent : lista) {
                if (ent.getEmail().equalsIgnoreCase(fornecedor.getEmail())) {
                    if (fornecedor.getId().longValue() != ent.getId().longValue()) {
                        addMsgError("O E-Mail Informado já esta em uso.");
                        return false;
                    }
                }
            }
        }
        return validar;
    }

    private boolean validarCnpj() {
        boolean validar = true;

        if (numeroCnpjPesquisa == null || "".equalsIgnoreCase(numeroCnpjPesquisa)) {
            addMsgError("O 'CNPJ' é obrigatório.");
            return false;
        }

        if (!validarFormatoCnpj(validar, numeroCnpjPesquisa)) {
            addMsgError("O 'CNPJ' informado está em um formato inválido.");
            return false;
        }

        if (numeroCnpjPesquisa.length() < 14) {
            addMsgError("O 'CNPJ' deverá conter 14 caracteres.");
            return false;
        }

        if (!verificarSeCnpjJaCadastrado()) {
            return false;
        }

        return true;
    }

    private boolean verificarSeCnpjJaCadastrado() {
        boolean valido = true;

        EntidadePesquisaDto pesquisarCnpj = new EntidadePesquisaDto();
        pesquisarCnpj.setUsuarioLogado(usuarioLogado);

        Entidade enti = new Entidade();
        enti.setNumeroCnpj(limparCampos(numeroCnpjPesquisa));
        pesquisarCnpj.setEntidade(enti);

        List<Entidade> encontrado = genericEntidadeService.buscar(pesquisarCnpj);
        if (encontrado.size() > 0) {
            if (fornecedor == null || fornecedor.getId() == null || fornecedor.getId().intValue() != encontrado.get(0).getId().intValue()) {
                addMsgError("O 'CNPJ' informado já esta em uso.");
                return false;
            }
        }

        return valido;
    }

    private boolean validarSePossuiRepresentanteEPreposto() {

        boolean valido = true;
        if (representantes.size() == 0) {
            addMsgError("É necessário cadastrar ao menos 1 representante legal.");
            valido = false;
        }

        if (prepostos.size() == 0) {
            addMsgError("É necessário cadastrar ao menos 1 preposto.");
            valido = false;
        }
        return valido;
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

    private boolean mostrarBotaoLimparFormulario() {
        if (readOnly) {
            return false;
        } else {
            if (botaoLimparFormularioVisible) {
                return true;
            } else {
                return false;
            }
        }
    }

    public String getMsgConfirm() {
        return msgConfirm;
    }

    public void setMsgConfirm(String msgConfirm) {
        this.msgConfirm = msgConfirm;
    }

    public Entidade getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Entidade fornecedor) {
        this.fornecedor = fornecedor;
    }
}
