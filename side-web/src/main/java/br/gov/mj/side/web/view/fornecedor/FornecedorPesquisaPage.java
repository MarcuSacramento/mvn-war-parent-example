package br.gov.mj.side.web.view.fornecedor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.validator.StringValidator;

import br.gov.mj.apoio.entidades.Uf;
import br.gov.mj.infra.negocio.persistencia.EnumOrder;
import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraAjaxConfirmButton;
import br.gov.mj.infra.wicket.component.InfraAjaxFallbackLink;
import br.gov.mj.infra.wicket.component.InfraDropDownChoice;
import br.gov.mj.infra.wicket.component.pagination.InfraAjaxPagingNavigator;
import br.gov.mj.infra.wicket.model.LambdaModel;
import br.gov.mj.side.entidades.Bem;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.entidade.EntidadeAnexo;
import br.gov.mj.side.entidades.entidade.PessoaEntidade;
import br.gov.mj.side.entidades.enums.EnumStatusEntidade;
import br.gov.mj.side.entidades.enums.EnumStatusPessoa;
import br.gov.mj.side.entidades.enums.EnumStatusPrograma;
import br.gov.mj.side.entidades.enums.EnumTipoPessoa;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.web.dto.EntidadePesquisaDto;
import br.gov.mj.side.web.service.AnexoEntidadeService;
import br.gov.mj.side.web.service.BemService;
import br.gov.mj.side.web.service.FornecedorService;
import br.gov.mj.side.web.service.GenericEntidadeService;
import br.gov.mj.side.web.service.ProgramaService;
import br.gov.mj.side.web.service.UfService;
import br.gov.mj.side.web.util.CnpjUtil;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.SideUtil;
import br.gov.mj.side.web.view.HomePage;
import br.gov.mj.side.web.view.template.TemplatePage;

/**
 * Classe destinada a pagina de pesquisa de fornecedores
 * 
 * @author ronald.calazans
 *
 */

@AuthorizeInstantiation({ FornecedorPesquisaPage.ROLE_MANTER_FORNECEDOR_VISUALIZAR, FornecedorPesquisaPage.ROLE_MANTER_FORNECEDOR_INCLUIR, FornecedorPesquisaPage.ROLE_MANTER_FORNECEDOR_ALTERAR, FornecedorPesquisaPage.ROLE_MANTER_FORNECEDOR_EXCLUIR })
public class FornecedorPesquisaPage extends TemplatePage {
    private static final long serialVersionUID = 1L;

    public static final String ROLE_MANTER_FORNECEDOR_VISUALIZAR = "manter_fornecedor:visualizar";
    public static final String ROLE_MANTER_FORNECEDOR_INCLUIR = "manter_fornecedor:incluir";
    public static final String ROLE_MANTER_FORNECEDOR_ALTERAR = "manter_fornecedor:alterar";
    public static final String ROLE_MANTER_FORNECEDOR_EXCLUIR = "manter_fornecedor:excluir";

    private EntidadePesquisaDto fornecedorPesquisaDto = new EntidadePesquisaDto();
    private List<Bem> listaBems;

    // compoentes do wicket
    private PanelPrincipal panelPrincipal;
    private PanelDataView panelDataView;
    private Form<EntidadePesquisaDto> form;
    private DataView<Entidade> dataViewForcededores;

    private ProviderFornecedor provider;
    private Integer itensPorPagina = Constants.ITEMS_PER_PAGE_PAGINATION;

    // injeção de dependencias

    @Inject
    private UfService ufService;
    @Inject
    private ComponentFactory componentFactory;
    @Inject
    private FornecedorService fornecedorService;
    @Inject
    private ProgramaService programaService;
    @Inject
    private BemService bemService;
    @Inject
    private AnexoEntidadeService anexoEntidadeService;
    @Inject
    private GenericEntidadeService genericEntidadeService;

    // ##################################################################################
    // construtores,intis * destroyers
    public FornecedorPesquisaPage(final PageParameters pageParameters) {
        super(pageParameters);

        initComponents();
        criarBreadcrump();
        setTitulo("Pesquisar Fornecedor");
    }

    private void initComponents() {
        form = componentFactory.newForm("form", new EntidadePesquisaDto());

        fornecedorPesquisaDto.setUsuarioLogado(getUsuarioLogadoDaSessao());

        listaBems = bemService.buscarTodos();

        form.add(panelPrincipal = new PanelPrincipal("panelPrincipal"));
        form.add(panelDataView = new PanelDataView("panelDataView"));
        panelDataView.setVisible(false);

        form.add(newButtonNovo()); // btnNovo
        form.add(newButtonPesquisar()); // btnPesquisar

        add(form);
    }

    // ##################################################################################
    // componentes wicket
    @SuppressWarnings("rawtypes")
    private void criarBreadcrump() {
        Link link = new Link("homePage") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                setResponsePage(HomePage.class);
            }
        };
        form.add(link);
    }

    // Paineis

    private class PanelPrincipal extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelPrincipal(String id) {
            super(id);
            setOutputMarkupId(true);

            add(newTextFieldCnpj());
            add(newTextFieldRazãoSocial());
            add(newDropDownUf());
            add(newTextFieldContrato());
            add(newDropDownPrograma());
            add(newDropDownBem());
        }
    }

    private class PanelDataView extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelDataView(String id) {
            super(id);
            setOutputMarkupId(true);

            provider = new ProviderFornecedor(form.getModelObject());
            add(newDataViewFornecedores(provider)); // dataView
            add(newDropItensPorPagina()); // itensPorPagina
            add(new InfraAjaxPagingNavigator("pagination", dataViewForcededores));

            add(new OrderByBorder<String>("orderByCnpj", "numeroCnpj", provider));
            add(new OrderByBorder<String>("orderByRazaoSocial", "nomeEntidade", provider));
            add(new OrderByBorder<String>("orderByEstado", "municipio.uf.siglaUf", provider));
            add(new OrderByBorder<String>("orderBySituacao", "statusEntidade", provider));
        }

    }

    // Componentes

    private TextField<String> newTextFieldCnpj() {
        TextField<String> field = componentFactory.newTextField("entidade.numeroCnpj", "CNPJ", false, null);
        field.add(StringValidator.maximumLength(18));
        return field;
    }

    private TextField<String> newTextFieldRazãoSocial() {
        TextField<String> field = componentFactory.newTextField("entidade.nomeEntidade", "Razão Social", false, null);
        field.add(StringValidator.maximumLength(18));
        return field;
    }

    private InfraDropDownChoice<Uf> newDropDownUf() {
        InfraDropDownChoice<Uf> dropDownUf = componentFactory.newDropDownChoice("entidade.municipio.uf", "Estado", false, "id", "nomeSigla", null, ufService.buscarTodos(), null);
        dropDownUf.setNullValid(true);
        return dropDownUf;
    }

    private TextField<String> newTextFieldContrato() {
        TextField<String> field = componentFactory.newTextField("contrato.numeroContrato", "Contrato", false, null);
        field.add(StringValidator.maximumLength(18));
        return field;
    }

    private InfraDropDownChoice<Programa> newDropDownPrograma() {
        Programa programa = new Programa();
        programa.setStatusPrograma(EnumStatusPrograma.ABERTO_GERACAO_CONTRATO);
        List<Programa> listaProgramas = programaService.buscar(programa);

        InfraDropDownChoice<Programa> drop = componentFactory.newDropDownChoice("programa", "Programa", false, "id", "nomePrograma", null, listaProgramas, null);
        drop.setNullValid(true);
        return drop;
    }

    private InfraDropDownChoice<Bem> newDropDownBem() {
        List<Bem> listaBens = bemService.buscarTodos();
        InfraDropDownChoice<Bem> drop = componentFactory.newDropDownChoice("bem", "Tipo de Bem", false, "id", "nomeBem", null, listaBens, null);
        drop.setNullValid(true);
        return drop;
    }

    private DropDownChoice<Integer> newDropItensPorPagina() {
        DropDownChoice<Integer> dropDownChoice = new DropDownChoice<Integer>("itensPorPagina", new LambdaModel<Integer>(this::getItensPorPagina, this::setItensPorPagina), Constants.QUANTIDADE_ITENS_TABELA);
        dropDownChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                dataViewForcededores.setItemsPerPage(itensPorPagina);
                target.add(panelDataView);
            };
        });
        return dropDownChoice;
    }

    private DataView<Entidade> newDataViewFornecedores(ProviderFornecedor provider) {
        dataViewForcededores = new DataView<Entidade>("dataView", provider) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<Entidade> item) {
                item.add(new Label("cnpj", CnpjUtil.imprimeCNPJ(item.getModelObject().getNumeroCnpj())));
                item.add(new Label("razaoSocial", item.getModelObject().getNomeEntidade()));
                item.add(new Label("estado", item.getModelObject().getMunicipio().getUf().getNomeUf()));
                item.add(new Label("statusEntidade.descricao"));
                item.add(newButtonEditar(item)); // btnAlterar
                item.add(newButtonVisulizar(item)); // btnVisualizar
                item.add(newButtonAtivar(item)); // btnAtivar
                item.add(newButtonInativar(item)); // btnInativar
            }
        };
        dataViewForcededores.setItemsPerPage(Constants.ITEMS_PER_PAGE_PAGINATION);
        return dataViewForcededores;
    }

    private InfraAjaxConfirmButton newButtonInativar(Item<Entidade> item) {
        InfraAjaxConfirmButton btnInativar = componentFactory.newAJaxConfirmButton("btnInativar", "MT032", form, (target, formz) -> alterarSituacao(target, item));
        authorize(btnInativar, RENDER, ROLE_MANTER_FORNECEDOR_ALTERAR);
        btnInativar.setVisible(item.getModelObject().getStatusEntidade().equals(EnumStatusEntidade.ATIVA));
        return btnInativar;
    }

    private InfraAjaxConfirmButton newButtonAtivar(Item<Entidade> item) {
        InfraAjaxConfirmButton btnAtivar = componentFactory.newAJaxConfirmButton("btnAtivar", "MT033", form, (target, formz) -> alterarSituacao(target, item));
        authorize(btnAtivar, RENDER, ROLE_MANTER_FORNECEDOR_ALTERAR);
        btnAtivar.setVisible(item.getModelObject().getStatusEntidade().equals(EnumStatusEntidade.INATIVA));
        return btnAtivar;
    }

    private Button newButtonEditar(Item<Entidade> item) {
        Button btnAlterar = componentFactory.newButton("btnAlterar", () -> acaoEditar(item));
        btnAlterar.setOutputMarkupId(true);
        btnAlterar.setVisible(true);
        authorize(btnAlterar, RENDER, ROLE_MANTER_FORNECEDOR_ALTERAR);
        return btnAlterar;
    }

    private Button newButtonVisulizar(Item<Entidade> item) {
        Button btnVisualizar = componentFactory.newButton("btnVisualizar", () -> acaoVisualizar(item));
        btnVisualizar.setOutputMarkupId(true);
        return btnVisualizar;
    }

    public InfraAjaxFallbackLink<Void> newButtonNovo() {
        InfraAjaxFallbackLink<Void> btnNovo = componentFactory.newAjaxFallbackLink("btnNovo", (target) -> adicionarNovo());
        authorize(btnNovo, RENDER, ROLE_MANTER_FORNECEDOR_INCLUIR);
        return btnNovo;
    }

    public AjaxSubmitLink newButtonPesquisar() {
        AjaxSubmitLink button = new AjaxSubmitLink("btnPesquisar", form) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form) {
                pesquisar();

                target.add(panelDataView);
                target.appendJavaScript("ancorarResultadoPesquisa('#ancoraPesquisa');");
            }
        };
        authorize(button, RENDER, FornecedorPesquisaPage.ROLE_MANTER_FORNECEDOR_VISUALIZAR);
        return button;
    }

    // Provider
    private class ProviderFornecedor extends SortableDataProvider<Entidade, String> {
        private static final long serialVersionUID = 1L;

        private EntidadePesquisaDto entidadePesquisaDto;

        public ProviderFornecedor(EntidadePesquisaDto entidadePesquisaDto) {
            this.entidadePesquisaDto = entidadePesquisaDto;
            setSort("nomeEntidade", SortOrder.ASCENDING);
        }

        @Override
        public Iterator<Entidade> iterator(long first, long count) {
            entidadePesquisaDto.setUsuarioLogado(getUsuarioLogadoDaSessao());
            return fornecedorService.buscarPaginado(entidadePesquisaDto, (int) first, (int) count, getSort().isAscending() ? EnumOrder.ASC : EnumOrder.DESC, getSort().getProperty()).iterator();
        }

        @Override
        public long size() {
            entidadePesquisaDto.setUsuarioLogado(getUsuarioLogadoDaSessao());
            return fornecedorService.buscarSemPaginacao(entidadePesquisaDto).size();
        }

        @Override
        public IModel<Entidade> model(Entidade object) {
            return new CompoundPropertyModel<Entidade>(object);
        }
    }

    // Acao

    private void alterarSituacao(AjaxRequestTarget target, Item<Entidade> item) {
        List<EntidadeAnexo> listaAnexos = new ArrayList<EntidadeAnexo>();
        List<PessoaEntidade> pessoas = new ArrayList<PessoaEntidade>();

        Entidade fornecedor = item.getModelObject();

        if (!getSideSession().hasRole(ROLE_MANTER_FORNECEDOR_ALTERAR)) {
            throw new SecurityException();
        }

        listaAnexos = SideUtil.convertAnexoDtoToEntityEntidadeAnexo(anexoEntidadeService.buscarPeloIdEntidade(fornecedor.getId()));

        if (fornecedor.getStatusEntidade().equals(EnumStatusEntidade.ATIVA)) {
            fornecedor.setStatusEntidade(EnumStatusEntidade.INATIVA);
            pessoas = inativarPrepostosERepresentantes(fornecedor);
        } else {
            fornecedor.setStatusEntidade(EnumStatusEntidade.ATIVA);
        }

        fornecedor.setAnexos(listaAnexos);
        fornecedor.setPessoas(pessoas);

        fornecedorService.incluirAlterar(fornecedor, getIdentificador());
        addMsgInfo("MN032");
        target.add(panelDataView);
    }

    private List<PessoaEntidade> inativarPrepostosERepresentantes(Entidade fornecedor) {
        List<PessoaEntidade> listaDeRepresentantesPrepostos = genericEntidadeService.buscarPessoa(fornecedor);
        List<PessoaEntidade> listaPessoa = new ArrayList<PessoaEntidade>();

        for (PessoaEntidade rep : listaDeRepresentantesPrepostos) {
            if (rep.getPessoa().getTipoPessoa().equals(EnumTipoPessoa.REPRESENTANTE_LEGAL)) {
                rep.getPessoa().setStatusPessoa(EnumStatusPessoa.INATIVO);
            } else if (rep.getPessoa().getTipoPessoa().equals(EnumTipoPessoa.PREPOSTO_FORNECEDOR)) {
                rep.getPessoa().setStatusPessoa(EnumStatusPessoa.INATIVO);
            }
            listaPessoa.add(rep);
        }
        return listaPessoa;
    }

    private void acaoVisualizar(Item<Entidade> item) {
        setResponsePage(new FornecedorPage(getPageParameters(), this, item.getModelObject(), true));
    }

    private void acaoEditar(Item<Entidade> item) {
        setResponsePage(new FornecedorPage(getPageParameters(), this, item.getModelObject(), false));
    }

    private void pesquisar() {
        panelDataView.setVisible(true);
    }

    private void adicionarNovo() {
        setResponsePage(new FornecedorPage(getPageParameters(), this, new Entidade(), false));
    }

    public Integer getItensPorPagina() {
        return itensPorPagina;
    }

    public void setItensPorPagina(Integer itensPorPagina) {
        this.itensPorPagina = itensPorPagina;
    }

}
