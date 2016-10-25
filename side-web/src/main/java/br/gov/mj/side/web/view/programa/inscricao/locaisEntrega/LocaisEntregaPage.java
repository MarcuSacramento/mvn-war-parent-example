package br.gov.mj.side.web.view.programa.inscricao.locaisEntrega;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.validation.validator.StringValidator;

import br.gov.mj.apoio.entidades.Municipio;
import br.gov.mj.apoio.entidades.TipoEndereco;
import br.gov.mj.apoio.entidades.Uf;
import br.gov.mj.infra.negocio.exception.BusinessException;
import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraAjaxConfirmButton;
import br.gov.mj.infra.wicket.component.InfraAjaxFallbackLink;
import br.gov.mj.seg.entidades.Usuario;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.entidade.LocalEntregaEntidade;
import br.gov.mj.side.entidades.entidade.PessoaEntidade;
import br.gov.mj.side.entidades.enums.EnumStatusLocalEntrega;
import br.gov.mj.side.web.service.BeneficiarioService;
import br.gov.mj.side.web.service.MunicipioService;
import br.gov.mj.side.web.service.TipoEnderecoService;
import br.gov.mj.side.web.service.UfService;
import br.gov.mj.side.web.view.DashboardPanel;
import br.gov.mj.side.web.view.HomePage;
import br.gov.mj.side.web.view.components.converters.RemoveMascarasStringConverter;
import br.gov.mj.side.web.view.dashboard.paineis.PanelUfMunicipio;
import br.gov.mj.side.web.view.template.TemplatePage;

/**
 * Pagina de cadastro, exibição e alteração dos Locais de Entrega
 * 
 * @author diego.mota
 *
 */

@AuthorizeInstantiation({ LocaisEntregaPage.ROLE_MANTER_LOCAL_ENTREGA_ALTERAR, LocaisEntregaPage.ROLE_MANTER_LOCAL_ENTREGA_EXCLUIR, LocaisEntregaPage.ROLE_MANTER_LOCAL_ENTREGA_HABILITAR_DESABILITAR, LocaisEntregaPage.ROLE_MANTER_LOCAL_ENTREGA_INCLUIR,
        LocaisEntregaPage.ROLE_MANTER_LOCAL_ENTREGA_VISUALIZAR })
public class LocaisEntregaPage extends TemplatePage {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    // ###################################################################################
    // constantes
    private static final String MODO_EDICAO = "edicao";
    private static final String MODO_VISUALIZACAO = "visualizacao";
    private static final String MODO_CRIACAO = "criacao";

    // constantes de permição de acesso
    public static final String ROLE_MANTER_LOCAL_ENTREGA_INCLUIR = "manter_local_entrega:incluir";
    public static final String ROLE_MANTER_LOCAL_ENTREGA_ALTERAR = "manter_local_entrega:alterar";
    public static final String ROLE_MANTER_LOCAL_ENTREGA_HABILITAR_DESABILITAR = "manter_local_entrega:habilitar_desabilitar";
    public static final String ROLE_MANTER_LOCAL_ENTREGA_EXCLUIR = "manter_local_entrega:excluir";
    public static final String ROLE_MANTER_LOCAL_ENTREGA_VISUALIZAR = "manter_local_entrega:visualizar";

    // ###################################################################################
    // variaveis
    private List<Uf> listaUF;
    private List<Municipio> listaMunicipio;
    private List<TipoEndereco> listaTipoEndereco;
    private Uf ufSelecionada;
    private String modo = MODO_CRIACAO;
    private LocalEntregaEntidade localEntrega;
    private Entidade entidade;
    private Usuario usuarioLogado;

    // elementos do Wicket
    private DashboardPanel dashboardPessoasPanel;
    private Form<LocalEntregaEntidade> form;
    private PanelLocalEntrega panelLocalEntrega;
    private PanelDataView panelDataView;
    private PanelUfMunicipio<LocalEntregaEntidade> panelUfMunicipio;
    private DropDownChoice<Uf> dropDownUF;
    private DropDownChoice<Municipio> dropDownMunicipio;
    private DropDownChoice<TipoEndereco> dropDownTipoEndereco;
    private DataView<LocalEntregaEntidade> dataviewLocaisEntrega;

    // ###################################################################################
    // injeçãod e dependencia

    @Inject
    private ComponentFactory componentFactory;
    @Inject
    private UfService ufService;
    @Inject
    private MunicipioService municipioService;
    @Inject
    private TipoEnderecoService tipoEnderecoService;
    @Inject
    private BeneficiarioService beneficiarioService;

    // ###################################################################################
    // conscructs, inits & destroiers
    public LocaisEntregaPage(PageParameters pageParameters) {
        super(pageParameters);

        setTitulo("Informações da Entidade");

        PessoaEntidade pessoaEntidade = (PessoaEntidade) getSessionAttribute("pessoaEntidade");

        if (pessoaEntidade != null) {
            entidade = pessoaEntidade.getEntidade();
            entidade.setLocaisEntregaEntidade(beneficiarioService.buscarLocaisEntrega(entidade, null));
        }

        usuarioLogado = getUsuarioLogadoDaSessao();

        initPage();
    }

    public void initPage() {
        localEntrega = new LocalEntregaEntidade();

        form = componentFactory.newForm("form", new CompoundPropertyModel<LocalEntregaEntidade>(localEntrega));

        dashboardPessoasPanel = new DashboardPanel("dashboardPessoasPanel");
        authorize(dashboardPessoasPanel, RENDER, HomePage.ROLE_MANTER_INSCRICAO_VISUALIZAR);
        form.add(dashboardPessoasPanel);

        form.add(new EntidadeNavPanel("navPanel", this));
        form.add(getPanelLocalEntrega());
        form.add(getAdicionarButton());
        form.add(getLimparButton());
        form.add(getPanelDataView());
        form.add(getSalvarButton());

        add(form);
    }

    // ###################################################################################
    // classes privadas

    // paineis

    /**
     * Painel principal contendo os clampos a serem preenchidos quando a
     * inserção/alteração de um local de entrega
     * 
     * @author diego.mota
     *
     */
    private class PanelLocalEntrega extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelLocalEntrega(String id) {
            super(id);
        }

        public PanelLocalEntrega() {
            super("panelLocalEntrega");

            try {
                add(panelUfMunicipio = new PanelUfMunicipio<LocalEntregaEntidade>("PanelUfMunicipio", localEntrega, "municipio"));
            } catch (NoSuchMethodException | SecurityException | NoSuchFieldException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                e.printStackTrace();
            }

            add(getDropDownTipoEndereco());
            add(getTextField("nomeEndereco", "Nome", false, null, 200, null));
            add(getTextField("descricaoEndereco", "Endereço", false, null, 200, null));
            add(getTextField("numeroEndereco", "Número", false, null, 100, null));
            add(getTextField("complementoEndereco", "Complemento", false, null, 200, null));
            add(getTextField("bairro", "Bairro", false, null, 200, null));
            add(getTextField("numeroCep", "CEP", false, null, 9, new RemoveMascarasStringConverter()));
            add(getTextField("numeroTelefone", "Telefone", false, null, 12, new RemoveMascarasStringConverter()));
            add(getTextField("numeroFoneFax", "Telefone/ FAX", false, null, 12, new RemoveMascarasStringConverter()));

        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof PanelLocalEntrega)) {
                return false;
            }
            PanelLocalEntrega p = (PanelLocalEntrega) o;
            return p.getId().equals(this.getId());
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }

    /**
     * Classe anonima para criação do painel dedicado ao dataView de locais de
     * entrega
     * 
     * @author diego.mota
     *
     */
    private class PanelDataView extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelDataView() {
            super("panelDataView");
            setOutputMarkupId(true);
            add(getDataViewLocaisEntrega());
        }
    }

    // ###################################################################################
    // criação e configuração de componentes

    public PanelLocalEntrega getPanelLocalEntrega() {
        panelLocalEntrega = new PanelLocalEntrega();
        authorize(panelLocalEntrega, RENDER, ROLE_MANTER_LOCAL_ENTREGA_INCLUIR, ROLE_MANTER_LOCAL_ENTREGA_VISUALIZAR, ROLE_MANTER_LOCAL_ENTREGA_ALTERAR);

        authorize(panelLocalEntrega, ENABLE, ROLE_MANTER_LOCAL_ENTREGA_INCLUIR, ROLE_MANTER_LOCAL_ENTREGA_ALTERAR);

        return panelLocalEntrega;
    }

    public PanelDataView getPanelDataView() {
        panelDataView = new PanelDataView();

        authorize(panelDataView, RENDER, ROLE_MANTER_LOCAL_ENTREGA_VISUALIZAR);
        return panelDataView;
    }

    /**
     * retorna uma instancia do componente DropDownChoice devidamente
     * configurada para exibir a lista de UFs com seleção obrigatoria.
     * 
     * @return DropDownChoice<Uf>
     */
    public DropDownChoice<Uf> getDropDownUF() {
        listaUF = ufService.buscarTodos();
        dropDownUF = new DropDownChoice<Uf>("Estado", new PropertyModel<Uf>(this, "ufSelecionada"), listaUF, new ChoiceRenderer<Uf>("nomeUf"));

        actionDropDownUF(dropDownUF); // ao selecionar a UF preenche-se a lista
                                      // de municipios e atualiza
        dropDownUF.setNullValid(false);
        dropDownUF.setRequired(false);
        dropDownUF.setOutputMarkupId(true);

        return dropDownUF;
    }

    /**
     * retorna uma instancia do componente DropDownChoice devidamente
     * configurada para exibir a lista de Municipios caso a UF esteja
     * selecionada, com seleção obrigatoria.
     * 
     * @return DropDownChoice<Municipio>
     */
    public DropDownChoice<Municipio> getDropDownMunicipio() {
        if (ufSelecionada != null && ufSelecionada.getId() != null) {
            listaMunicipio = municipioService.buscarPelaUfId(ufSelecionada.getId());
        } else {
            listaMunicipio = new ArrayList<Municipio>();
        }

        dropDownMunicipio = componentFactory.newDropDownChoice("municipio", "Município", true, null, "nomeMunicipio", null, listaMunicipio, null);

        return dropDownMunicipio;
    }

    /**
     * retorna uma instancia do componente DropDownChoice devidamente
     * configurada para exibir a lista de Tipos de endereco com seleção
     * obrigatoria.
     * 
     * @return DropDownChoice<TipoEndereco>
     */
    public DropDownChoice<TipoEndereco> getDropDownTipoEndereco() {
        listaTipoEndereco = tipoEnderecoService.buscarTodos();
        dropDownTipoEndereco = componentFactory.newDropDownChoice("tipoEndereco", "Tipo Endereço", false, null, "descricaoTipoEndereco", null, listaTipoEndereco, null);

        return dropDownTipoEndereco;
    }

    /**
     * Metodo generico para boteção de um componente TextField configurado
     * 
     * @param id
     *            - Nome do componetne a ser referenciado por meio do wicket:id
     *            no html
     * @param label
     *            - Label a ser exibido
     * @param required
     * @param model
     * @param maxLength
     * @param behaviors
     *            - Pode inserir zero, um ou mais comportamentos que serãoa
     *            cionados pelo componente
     * @return TextField<String>
     */
    public TextField<String> getTextField(String id, String label, Boolean required, IModel model, Integer maxLength, IConverter converter, Behavior... behaviors) {

        TextField<String> text = new TextField<String>(id) {
            private static final long serialVersionUID = 1L;

            @Override
            public <C> IConverter<C> getConverter(java.lang.Class<C> type) {
                if (converter != null) {
                    return converter;
                } else {
                    return super.getConverter(type);
                }
            };
        };
        text.setModel(model);
        text.setRequired(required);
        text.setLabel(new ResourceModel(label, label));

        for (Behavior b : behaviors) {
            text.add(b);
        }
        text.add(StringValidator.maximumLength(maxLength));

        return text;
    }

    /**
     * recupera o botão com coportamento de adicionar o item a dataView
     * 
     * @return
     */
    public AjaxSubmitLink getAdicionarButton() {
        AjaxSubmitLink button = new AjaxSubmitLink("btnAdicionar") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);

                adicionar(target);
            }
        };
        button.add(componentFactory.newLabel("txtBtnAdicionarAtualizar", (modo.equals(MODO_CRIACAO) ? "Adicionar" : "Atualizar")));

        authorize(button, RENDER, ROLE_MANTER_LOCAL_ENTREGA_INCLUIR, ROLE_MANTER_LOCAL_ENTREGA_EXCLUIR, ROLE_MANTER_LOCAL_ENTREGA_VISUALIZAR, ROLE_MANTER_LOCAL_ENTREGA_ALTERAR);

        return button;
    }

    /**
     * recupera o botão com coportamento de adicionar o item a dataView
     * 
     * @return
     */
    public InfraAjaxFallbackLink<Void> getLimparButton() {
        return componentFactory.newAjaxFallbackLink("btnLimpar", (target) -> limpar(target));
    }

    /**
     * recupera o botão com coportamento de salvar e atulizar o registro
     * 
     * @return
     */
    public InfraAjaxFallbackLink<Void> getSalvarButton() {
        InfraAjaxFallbackLink<Void> button = componentFactory.newAjaxFallbackLink("btnSalvar", (target) -> salvar(target));

        authorize(button, RENDER, ROLE_MANTER_LOCAL_ENTREGA_ALTERAR, ROLE_MANTER_LOCAL_ENTREGA_EXCLUIR, ROLE_MANTER_LOCAL_ENTREGA_INCLUIR);
        return button;
    }

    /**
     * recupera o botão com comportamento anular a ação atual(inserção ou
     * alteração) ou voltar para a pagina anterior
     * 
     * @return
     */
    public Button getButtonVoltar() {
        Button button = new Button("btnVoltar") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit() {
            }
        };
        button.setDefaultFormProcessing(false);
        return button;
    }

    /**
     * Monta e configura a dataView para exibição dos locais de entrega
     * cadastrados
     * 
     * @return
     */
    public DataView<LocalEntregaEntidade> getDataViewLocaisEntrega() {
        dataviewLocaisEntrega = new DataView<LocalEntregaEntidade>("dataLocais", new ListDataProvider<LocalEntregaEntidade>(entidade.getLocaisEntregaEntidade())) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<LocalEntregaEntidade> item) {
                item.clearOriginalDestination();
                item.add(new Label("nomeLocal", item.getModelObject().getNomeEndereco()));
                item.add(new Label("nomeEstado", item.getModelObject().getMunicipio().getUf().getNomeUf()));
                item.add(new Label("nomeMunicipio", item.getModelObject().getMunicipio().getNomeMunicipio()));
                item.add(getButtonVisualizarDataView(item));
                item.add(getButtonEditarDataView(item));
                item.add(getButtonDesabilitarDataView(item));
                item.add(getButtonHabilitarDataView(item));
                item.add(getButtonRemoverDataView(item));
            }
        };
        return dataviewLocaisEntrega;
    }

    /**
     * retorna uma instancia de AjaxSubmitLink configuarada para iniciar a
     * edição de um item da dataView de locais de entrega
     * 
     * @return
     */
    public InfraAjaxFallbackLink<Void> getButtonEditarDataView(Item<LocalEntregaEntidade> item) {

        InfraAjaxFallbackLink<Void> button = componentFactory.newAjaxFallbackLink("btnEditarItemDataView", (target) -> editar(target, item));
        if (item.getModelObject().isPossuiVinculo()) {
            button.setEnabled(false);
            button.setVisible(false);
        }

        authorize(button, RENDER, ROLE_MANTER_LOCAL_ENTREGA_ALTERAR);

        return button;
    }

    /**
     * retorna uma instancia de AjaxSubmitLink configuarada para iniciar a
     * edição de um item da dataView de locais de entrega
     * 
     * @return
     */
    public InfraAjaxFallbackLink<Void> getButtonVisualizarDataView(Item<LocalEntregaEntidade> item) {

        InfraAjaxFallbackLink<Void> button = componentFactory.newAjaxFallbackLink("btnVisualizarDataView", (target) -> visualizar(target, item));
        authorize(button, RENDER, ROLE_MANTER_LOCAL_ENTREGA_VISUALIZAR);
        return button;
    }

    /**
     * retorna uma instancia de AjaxSubmitLink configuarada para desabilitar um
     * item da dataView de locais de entrega
     * 
     * @return
     */
    public InfraAjaxFallbackLink<Void> getButtonDesabilitarDataView(Item<LocalEntregaEntidade> item) {
        InfraAjaxFallbackLink<Void> button = componentFactory.newAjaxFallbackLink("btnDesabilitarItemDataView", (target) -> habilitarDesabilitar(target, item));

        if (item.getModelObject() != null && item.getModelObject().getStatusLocalEntrega().equals(EnumStatusLocalEntrega.DESABILITADO)) {
            button.setEnabled(false);
            button.setVisible(false);
        }

        authorize(button, RENDER, ROLE_MANTER_LOCAL_ENTREGA_HABILITAR_DESABILITAR);

        return button;
    }

    /**
     * retorna uma instancia de AjaxSubmitLink configuarada para habilitar um
     * item da dataView de locais de entrega
     * 
     * @return
     */
    public InfraAjaxFallbackLink<Void> getButtonHabilitarDataView(Item<LocalEntregaEntidade> item) {
        InfraAjaxFallbackLink<Void> button = componentFactory.newAjaxFallbackLink("btnHabilitarItemDataView", (target) -> habilitarDesabilitar(target, item));

        if (item.getModelObject() != null && item.getModelObject().getStatusLocalEntrega().equals(EnumStatusLocalEntrega.HABILITADO)) {
            button.setEnabled(false);
            button.setVisible(false);
        }

        authorize(button, RENDER, ROLE_MANTER_LOCAL_ENTREGA_HABILITAR_DESABILITAR);

        return button;
    }

    /**
     * retorna uma instancia de AjaxSubmitLink configuarada para remover um item
     * da dataView de locais de entrega
     * 
     * @return
     */
    public InfraAjaxConfirmButton getButtonRemoverDataView(Item<LocalEntregaEntidade> item) {
        InfraAjaxConfirmButton button = componentFactory.newAJaxConfirmButton("btnRemoverItemDataView", "MSG008", form, (target, form) -> remover(target, item));
        button.setDefaultFormProcessing(false);
        if (item.getModelObject().isPossuiVinculo()) {
            button.setEnabled(false);
            button.setVisible(false);
        }

        authorize(button, RENDER, ROLE_MANTER_LOCAL_ENTREGA_EXCLUIR);

        return button;
    }
    // ###################################################################################
    // metodos de controle

    /**
     * configura o comportamento do drop down de ufs quando é selecionado um
     * elemento. Carrega a lista de municipios de acordo com a UF selecionada.
     * 
     * @param dropDownUF
     */
    public void actionDropDownUF(DropDownChoice<Uf> dropDownUF) {
        dropDownUF.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                if (ufSelecionada != null && ufSelecionada.getId() != null) {
                    listaMunicipio = municipioService.buscarPelaUfId(ufSelecionada.getId());
                    dropDownMunicipio.setChoices(listaMunicipio);
                    panelLocalEntrega.addOrReplace(dropDownMunicipio);
                    target.add(panelLocalEntrega);
                }
            }
        });
    }

    /**
     * adiciona o item em edição para a dataView
     * 
     * @param target
     */
    private void adicionar(AjaxRequestTarget target) {

        try {
            if (!validarCamposBasicos()) {
                return;
            }

            if (validaInformacoesInseridas()) {
                if (modo.equals(MODO_CRIACAO)) {
                    localEntrega.setStatusLocalEntrega(EnumStatusLocalEntrega.HABILITADO);
                }
                entidade.getLocaisEntregaEntidade().add(localEntrega);
                localEntrega = new LocalEntregaEntidade();
                form.setModelObject(localEntrega); // aparentemente os valores
                                                   // passados ao modelObjet são
                                                   // copais e não referencias..
                                                   // assim sendo é preciso
                                                   // substitui-los manualmente

                ufSelecionada = null; // limpa o campo de seleção de uf

                modo = MODO_CRIACAO;

                recarregaFormulario(target);

                target.add(form);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * remove o item da dataView e adiociona-o ao formulario de edição
     * 
     * @param target
     * @param item
     */
    private void editar(AjaxRequestTarget target, Item<LocalEntregaEntidade> item) {

        modo = MODO_EDICAO;
        exibeLocal(target, item);
    }

    /**
     * Limpa os dados prenchidos no formulario
     * 
     * @param target
     */
    public void limpar(AjaxRequestTarget target) {
        if (MODO_EDICAO.equals(modo)) {
            // entidade.getLocaisEntregaEntidade().add(localEntrega);
        }
        localEntrega = new LocalEntregaEntidade();
        form.setModelObject(localEntrega);
        ufSelecionada = null;
        modo = MODO_CRIACAO;
        recarregaFormulario(target);
    }

    /**
     * Exibe os dados do local selecionado apenas para leitura
     * 
     * @param target
     * @param item
     */
    private void visualizar(AjaxRequestTarget target, Item<LocalEntregaEntidade> item) {

        modo = MODO_VISUALIZACAO;
        exibeLocal(target, item);
    }

    /**
     * Copia os dados do local selecionado para o formulario
     * 
     * @param target
     * @param item
     */
    private void exibeLocal(AjaxRequestTarget target, Item<LocalEntregaEntidade> item) {
        localEntrega = item.getModelObject();
        form.setModelObject(localEntrega); // aparentemente os valores passados
                                           // ao modelObjet são copais e não
                                           // referencias.. assim sendo é
                                           // preciso substitui-los manualmente

        ufSelecionada = localEntrega.getMunicipio().getUf(); // A uf é
                                                             // armazenada numa
                                                             // variavel local
                                                             // que precisa ser
                                                             // atualizada para
                                                             // edição

        recarregaFormulario(target);
    }

    /**
     * Processa os dados da dataview
     */
    private void salvar(AjaxRequestTarget target) {
        try {
            entidade = beneficiarioService.sincronizarLocaisDeEntrega(entidade, usuarioLogado.getLogin());
            getSession().info(getString("MT017"));
            target.add(panelDataView);

            // setResponsePage(this);

        } catch (BusinessException e) {
            Map<String, Object[]> originalMessages = e.getErrorMessages();
            for (String originalMsg : originalMessages.keySet()) {
                addMsgError(originalMsg, originalMessages.get(originalMsg));
            }
        }
    }

    private boolean validarCamposBasicos() {
        boolean validar = true;

        if (localEntrega.getNomeEndereco() == null || "".equalsIgnoreCase(localEntrega.getNomeEndereco())) {
            addMsgError("O campo 'Nome' é obrigatório");
            validar = false;
        }

        if (panelUfMunicipio.getUfSelecionada() == null) {
            addMsgError("O campo 'Estado' é obrigatório");
            validar = false;
        }

        if (localEntrega.getMunicipio() == null) {
            addMsgError("O campo 'Município' é obrigatório");
            validar = false;
        }

        if (localEntrega.getTipoEndereco() == null) {
            addMsgError("O campo 'Tipo Endereço' é obrigatório");
            validar = false;
        }

        if (localEntrega.getDescricaoEndereco() == null || "".equalsIgnoreCase(localEntrega.getDescricaoEndereco())) {
            addMsgError("O campo 'Endereço' é obrigatório");
            validar = false;
        }

        if (localEntrega.getNumeroEndereco() == null || "".equalsIgnoreCase(localEntrega.getNumeroEndereco())) {
            addMsgError("O campo 'Número' é obrigatório");
            validar = false;
        }

        if (localEntrega.getBairro() == null || "".equalsIgnoreCase(localEntrega.getBairro())) {
            addMsgError("O campo 'Bairro' é obrigatório");
            validar = false;
        }

        if (localEntrega.getNumeroCep() == null || "".equalsIgnoreCase(localEntrega.getNumeroCep())) {
            addMsgError("O campo 'CEP' é obrigatório");
            validar = false;
        }

        if (localEntrega.getNumeroTelefone() == null || "".equalsIgnoreCase(localEntrega.getNumeroTelefone())) {
            addMsgError("O campo 'Telefone' é obrigatório");
            validar = false;
        }

        return validar;
    }

    private boolean validaInformacoesInseridas() {
        boolean valid = true;

        LocalEntregaEntidade temp = new LocalEntregaEntidade();

        for (LocalEntregaEntidade le : entidade.getLocaisEntregaEntidade()) {
            if (localEntrega.getNomeEndereco().equalsIgnoreCase(le.getNomeEndereco())) {

                if (modo.equalsIgnoreCase(MODO_EDICAO)) {
                    temp = le;
                }

                if (le != localEntrega) {
                    addMsgError("MN045", localEntrega.getNomeEndereco());
                    valid = false;
                    break;
                }
            }
        }

        if (localEntrega.getTipoEndereco() == null) {
            addMsgError("O campo 'Tipo de Endereço' é obrigatório.");
            valid = false;
        }

        if (localEntrega.getNumeroCep().length() < 8) {
            addMsgError("MN046", "CEP");
            valid = false;
        }
        if (localEntrega.getNumeroTelefone().length() < 10) {
            addMsgError("MN046", "Telefone");
            valid = false;
        }
        if (localEntrega.getNumeroFoneFax() != null && localEntrega.getNumeroFoneFax().length() > 0 && localEntrega.getNumeroFoneFax().length() < 10) {
            addMsgError("MN046", "Telefone/ Fax");
            valid = false;
        }

        if (modo.equalsIgnoreCase(MODO_EDICAO) && valid) {
            entidade.getLocaisEntregaEntidade().remove(temp);
        }

        return valid;
    }

    /**
     * reinstancia os paineis do formulario, em seguida atualiza-o
     * 
     * @param target
     */
    private void recarregaFormulario(AjaxRequestTarget target) {
        panelLocalEntrega = new PanelLocalEntrega();
        if (modo.equals(MODO_VISUALIZACAO)) {
            panelLocalEntrega.setEnabled(false);
        }
        form.addOrReplace(panelLocalEntrega); // atualiza o painel do formulario

        AjaxSubmitLink adicionarButton = getAdicionarButton();
        if (modo.equals(MODO_VISUALIZACAO)) {
            adicionarButton.setEnabled(false);
            adicionarButton.setVisible(false);
        }
        form.addOrReplace(adicionarButton);

        form.addOrReplace(getPanelDataView()); // adiciona o painel da tabela

        target.add(form);
    }

    /**
     * Habilita ou desabilita um local de entrega
     * 
     * @param target
     * @param item
     */
    private void habilitarDesabilitar(AjaxRequestTarget target, Item<LocalEntregaEntidade> item) {

        if (item.getModelObject().getStatusLocalEntrega().equals(EnumStatusLocalEntrega.DESABILITADO)) {
            item.getModelObject().setStatusLocalEntrega(EnumStatusLocalEntrega.HABILITADO);
        } else {
            item.getModelObject().setStatusLocalEntrega(EnumStatusLocalEntrega.DESABILITADO);
        }
        form.addOrReplace(new PanelDataView()); // atualiza o painel da tabela
        target.add(form);
    }

    /**
     * Exclui um item da lista de locais de entrega
     * 
     * @param target
     */
    private void remover(AjaxRequestTarget target, Item<LocalEntregaEntidade> item) {
        entidade.getLocaisEntregaEntidade().remove(item.getModelObject());

        form.addOrReplace(new PanelDataView()); // atualiza o painel da tabela
        target.add(form);
    }

}
