package br.gov.mj.side.web.view.contrato;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.validator.StringValidator;

import br.gov.mj.apoio.entidades.Regiao;
import br.gov.mj.apoio.entidades.Uf;
import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraAjaxConfirmButton;
import br.gov.mj.infra.wicket.component.InfraAjaxFallbackLink;
import br.gov.mj.infra.wicket.component.InfraDropDownChoice;
import br.gov.mj.infra.wicket.component.pagination.InfraAjaxPagingNavigator;
import br.gov.mj.infra.wicket.model.LambdaModel;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.enums.EnumStatusEntidade;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.licitacao.AgrupamentoLicitacao;
import br.gov.mj.side.entidades.programa.licitacao.BemUf;
import br.gov.mj.side.entidades.programa.licitacao.SelecaoItem;
import br.gov.mj.side.entidades.programa.licitacao.contrato.Contrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.FormatacaoContrato;
import br.gov.mj.side.web.dto.ContratoDto;
import br.gov.mj.side.web.dto.EntidadePesquisaDto;
import br.gov.mj.side.web.service.BemService;
import br.gov.mj.side.web.service.BeneficiarioService;
import br.gov.mj.side.web.service.ContratoService;
import br.gov.mj.side.web.service.FormatacaoItensContratoService;
import br.gov.mj.side.web.service.FornecedorService;
import br.gov.mj.side.web.service.LicitacaoProgramaService;
import br.gov.mj.side.web.service.ProgramaService;
import br.gov.mj.side.web.service.UfService;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.SortableContratoDataProvider;
import br.gov.mj.side.web.view.planejarLicitacao.ContratoPanelBotoes;
import br.gov.mj.side.web.view.programa.PanelFasePrograma;
import br.gov.mj.side.web.view.template.TemplatePage;

@AuthorizeInstantiation({ ContratoPesquisaPage.ROLE_MANTER_CONTRATO_VISUALIZAR, ContratoPesquisaPage.ROLE_MANTER_CONTRATO_INCLUIR, ContratoPesquisaPage.ROLE_MANTER_CONTRATO_ALTERAR, ContratoPesquisaPage.ROLE_MANTER_CONTRATO_EXCLUIR })
public class ContratoPesquisaPage extends TemplatePage {

    private static final long serialVersionUID = 1L;

    public static final String ROLE_MANTER_CONTRATO_VISUALIZAR = "manter_contrato:visualizar";
    public static final String ROLE_MANTER_CONTRATO_INCLUIR = "manter_contrato:incluir";
    public static final String ROLE_MANTER_CONTRATO_ALTERAR = "manter_contrato:alterar";
    public static final String ROLE_MANTER_CONTRATO_EXCLUIR = "manter_contrato:excluir";

    private PanelPesquisa panelPesquisa;
    private PanelResultado panelResultado;
    private PanelFasePrograma panelFasePrograma;
    private ContratoPanelBotoes execucaoPanelBotoes;

    private Form<ContratoDto> form;
    private ContratoDto contratoDto = new ContratoDto();
    private SortableContratoDataProvider dataProvider;
    private Integer itensPorPagina = Constants.ITEMS_PER_PAGE_PAGINATION;
    private DataView<Contrato> dataView;
    private Programa programa;
    private Page backPage;
    private String botaoClicado;

    private InfraAjaxFallbackLink btnNovo;
    private Button btnPesquisar;
    private Button btnOcultarButton;

    @Inject
    private ComponentFactory componentFactory;
    @Inject
    private ProgramaService programaService;
    @Inject
    private BemService bemService;
    @Inject
    private FornecedorService fornecedorService;
    @Inject
    private BeneficiarioService beneficiarioService;
    @Inject
    private ContratoService contratoService;
    @Inject
    private LicitacaoProgramaService licitacaoProgramaService;
    @Inject
    private UfService ufService;
    @Inject
    private FormatacaoItensContratoService formatacaoItensContratoService;

    public ContratoPesquisaPage(final PageParameters pageParameters) {
        super(pageParameters);

        setTitulo("Gerenciar Programa");
        initComponents();
    }

    public ContratoPesquisaPage(final PageParameters pageParameters, Programa programa, Page backPage) {
        super(pageParameters);
        this.programa = programa;
        this.backPage = backPage;

        setTitulo("Gerenciar Programa");
        initComponents();
    }

    private void initComponents() {
        form = componentFactory.newForm("form", new ContratoDto());
        form.getModelObject().setCodigoPrograma(programa.getCodigoIdentificadorProgramaPublicado());

        form.add(execucaoPanelBotoes = new ContratoPanelBotoes("execucaoPanelPotoes", programa, backPage, "contrato"));
        form.add(panelFasePrograma = new PanelFasePrograma("panelFasePrograma", programa, backPage));

        panelPesquisa = new PanelPesquisa("panelPesquisa");
        panelPesquisa.setVisible(false);

        btnNovo = newButtonNovo();
        authorize(btnNovo, RENDER, ContratoPesquisaPage.ROLE_MANTER_CONTRATO_INCLUIR);

        btnPesquisar = newButtonPesquisar();
        authorize(btnPesquisar, RENDER, ContratoPesquisaPage.ROLE_MANTER_CONTRATO_VISUALIZAR);
        btnPesquisar.setVisible(false);

        btnOcultarButton = newButtonOcultarPesquisa();
        authorize(btnOcultarButton, RENDER, ContratoPesquisaPage.ROLE_MANTER_CONTRATO_VISUALIZAR);
        btnOcultarButton.setVisible(false);

        panelResultado = new PanelResultado("panelResultado");
        panelResultado.setVisible(true);

        form.add(newButtonVoltar()); // btnVoltar
        form.add(btnNovo);
        form.add(panelPesquisa);
        form.add(btnPesquisar);
        form.add(btnOcultarButton);
        form.add(panelResultado);
        add(form);
    }

    private InfraAjaxFallbackLink<Void> newButtonNovo() {
        return componentFactory.newAjaxFallbackLink("btnAdicionarNovo", (target) -> adicionarNovo());
    }

    private void adicionarNovo() {
        Contrato novo = new Contrato();
        novo.setPrograma(programa);
        setResponsePage(new ContratoPage(getPageParameters(), this, novo, programa, false, false));
    }

    private Button newButtonPesquisar() {
        return componentFactory.newButton("btnPesquisar", () -> pesquisar());
    }

    private Button newButtonOcultarPesquisa() {
        AjaxFallbackButton btnMostrarPesquisa = componentFactory.newAjaxFallbackButton("btnOcultarPesquisa", null, (target, form) -> actionOcultarPesquisa(target));
        btnMostrarPesquisa.setOutputMarkupId(true);
        authorize(btnMostrarPesquisa, RENDER, ROLE_MANTER_CONTRATO_VISUALIZAR);
        return btnMostrarPesquisa;
    }

    // Paineis

    private class PanelPesquisa extends WebMarkupContainer {

        private static final long serialVersionUID = 1L;

        public PanelPesquisa(String id) {
            super(id);
            setOutputMarkupId(true);

            add(newDropDownFornecedor()); // entidade
            add(newTextFieldNumeroContrato()); // numeroContrato
            add(newDropDownUf()); // uf
        }
    }

    private class PanelResultado extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelResultado(String id) {
            super(id);
            SortableContratoDataProvider dataProvider = new SortableContratoDataProvider(contratoService, form.getModelObject());
            dataView = newDataViewResultado(dataProvider);
            add(dataView);

            add(newButtonVisualizarPesquisa()); // btnMostrarPesquisa
            add(newDropItensPorPagina());

            add(new OrderByBorder<String>("orderByNumeroContrato", "numeroContrato", dataProvider));
            add(new OrderByBorder<String>("orderByFornecedor", "fornecedor.nomeEntidade", dataProvider));
            add(new InfraAjaxPagingNavigator("pagination", dataView));
        }
    }

    // Componentes do painel de pesquisa

    private TextField<String> newTextFieldCodigoPrograma() {
        TextField<String> text = componentFactory.newTextField("codigoPrograma", "Código Programa", false, null);
        text.setVisible(false);
        return text;
    }

    private TextField<String> newTextFieldNomePrograma() {
        TextField<String> field = componentFactory.newTextField("nomePrograma", "Número Contrato", false, null);
        field.add(StringValidator.maximumLength(20));
        field.setVisible(false);
        return field;
    }

    private InfraDropDownChoice<Uf> newDropDownUf() {
        List<Uf> listaUf = ufService.buscarTodos();
        InfraDropDownChoice<Uf> drop = componentFactory.newDropDownChoice("uf", "UF", false, "id", "nomeUf", null, listaUf, null);
        drop.setNullValid(true);
        drop.setVisible(true);
        return drop;
    }

    private InfraDropDownChoice<Entidade> newDropDownFornecedor() {
        EntidadePesquisaDto entidadePesquisaDto = new EntidadePesquisaDto();
        Entidade entidade = new Entidade();
        entidade.setStatusEntidade(EnumStatusEntidade.ATIVA);
        entidadePesquisaDto.setEntidade(entidade);
        entidadePesquisaDto.setUsuarioLogado(getUsuarioLogadoDaSessao());

        List<Entidade> listaFornecedores = fornecedorService.buscarSemPaginacao(entidadePesquisaDto);
        InfraDropDownChoice<Entidade> drop = componentFactory.newDropDownChoice("fornecedor", "Fornecedor", false, "id", "nomeEntidade", null, listaFornecedores, null);
        drop.setNullValid(true);
        return drop;
    }

    private TextField<String> newTextFieldNumeroContrato() {
        TextField<String> field = componentFactory.newTextField("numeroContrato", "Número Contrato", false, null);
        field.add(StringValidator.maximumLength(20));
        return field;
    }

    private Button newButtonVisualizarPesquisa() {
        AjaxFallbackButton btnMostrarPesquisa = componentFactory.newAjaxFallbackButton("btnMostrarPesquisa", null, (target, form) -> actionMostrarPesquisa(target));
        btnMostrarPesquisa.setOutputMarkupId(true);
        authorize(btnMostrarPesquisa, RENDER, ROLE_MANTER_CONTRATO_VISUALIZAR);
        return btnMostrarPesquisa;
    }

    private Button newButtonEditar(Item<Contrato> item) {
        List<FormatacaoContrato> listaFormatacao = formatacaoItensContratoService.buscarFormatacaoContrato(item.getModelObject());
        Button btnAlterar = componentFactory.newButton("btnAlterar", () -> acaoEditar(item));
        btnAlterar.setOutputMarkupId(true);
        btnAlterar.setVisible(listaFormatacao.size() <= 0 && listaFormatacao.isEmpty());
        authorize(btnAlterar, RENDER, ROLE_MANTER_CONTRATO_ALTERAR);
        return btnAlterar;
    }

    private Button newButtonVisulizar(Item<Contrato> item) {
        Button btnVisualizar = componentFactory.newButton("btnVisualizar", () -> acaoVisualizar(item));
        btnVisualizar.setOutputMarkupId(true);
        authorize(btnVisualizar, RENDER, ROLE_MANTER_CONTRATO_VISUALIZAR);
        return btnVisualizar;
    }

    private Button newButtonInformacao(Item<Contrato> item) {
        List<FormatacaoContrato> listaFormatacao = formatacaoItensContratoService.buscarFormatacaoContrato(item.getModelObject());
        Button btnInformacao = componentFactory.newButton("btnInformacao", null);
        btnInformacao.setVisible(listaFormatacao.size() > 0 && !listaFormatacao.isEmpty());
        return btnInformacao;
    }

    private InfraAjaxConfirmButton newButtonExcluir(Item<Contrato> item) {
        List<FormatacaoContrato> listaFormatacao = formatacaoItensContratoService.buscarFormatacaoContrato(item.getModelObject());
        InfraAjaxConfirmButton btnExcluir = componentFactory.newAJaxConfirmButton("btnExcluir", "MN048", form, (target, formz) -> excluir(target, item));
        btnExcluir.setVisible(listaFormatacao.size() <= 0 && listaFormatacao.isEmpty());
        authorize(btnExcluir, RENDER, ROLE_MANTER_CONTRATO_EXCLUIR);
        return btnExcluir;
    }

    private DropDownChoice<Integer> newDropItensPorPagina() {
        DropDownChoice<Integer> dropDownChoice = new DropDownChoice<Integer>("itensPorPagina", new LambdaModel<Integer>(this::getItensPorPagina, this::setItensPorPagina), Constants.QUANTIDADE_ITENS_TABELA);
        dropDownChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                dataView.setItemsPerPage(getItensPorPagina());
                target.add(panelResultado);
            };
        });
        return dropDownChoice;
    }

    private Button newButtonVoltar() {
        Button button = componentFactory.newButton("btnVoltar", () -> actionVoltar());
        button.setDefaultFormProcessing(false);
        return button;
    }

    // DATA VIEW

    public DataView<Contrato> newDataViewResultado(SortableContratoDataProvider dataProvider) {
        dataView = new DataView<Contrato>("listaContrato", dataProvider) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<Contrato> item) {

                List<String> lista = verificarAgrupamentosRegioesUf(item);

                String grupoItem = lista.get(0);
                String estado = lista.get(1);
                String regiao = lista.get(2);

                item.add(new Label("numeroContrato"));
                item.add(new Label("grupoItem", grupoItem));
                item.add(new Label("fornecedor.nomeEntidade"));
                item.add(new Label("estadoUf", estado));
                item.add(new Label("regiaoMunicipio", regiao));

                item.add(newButtonEditar(item));
                item.add(newButtonInformacao(item));
                item.add(newButtonExcluir(item));
                item.add(newButtonVisulizar(item));
            }
        };
        dataView.setItemsPerPage(Constants.ITEMS_PER_PAGE_PAGINATION);
        return dataView;
    }

    // AÇÕES

    private void actionVoltar() {
        setResponsePage(backPage);
    }

    private void pesquisar() {
        panelResultado.setVisible(true);
    }

    private void actionOcultarPesquisa(AjaxRequestTarget target) {
        panelPesquisa.setVisible(false);
        btnPesquisar.setVisible(false);
        btnOcultarButton.setVisible(false);

        target.add(panelPesquisa, btnPesquisar, btnOcultarButton);
    }

    private void actionMostrarPesquisa(AjaxRequestTarget target) {
        panelPesquisa.setVisible(true);
        btnPesquisar.setVisible(true);
        btnOcultarButton.setVisible(true);

        target.add(panelPesquisa, btnPesquisar, btnOcultarButton);
    }

    private List<String> verificarAgrupamentosRegioesUf(Item<Contrato> item) {

        String stringRegiao = new String("");
        String stringUf = new String("");
        String grupoItem = "";
        List<Regiao> reg = new ArrayList<Regiao>();
        List<Uf> uf = new ArrayList<Uf>();
        List<String> listaRetornar = new ArrayList<String>();

        List<AgrupamentoLicitacao> listaLicitacao = contratoService.buscarAgrupamentoLicitacao(item.getModelObject());

        // Busca o nome dos agrupamentos
        Collections.sort(listaLicitacao, AgrupamentoLicitacao.getComparator(1, "tipo"));
        for (AgrupamentoLicitacao agrup : listaLicitacao) {

            if (!grupoItem.equalsIgnoreCase("")) {
                grupoItem = grupoItem + "/ ";
            }
            grupoItem += " " + agrup.getNomeAgrupamento() + " ";
        }

        for (AgrupamentoLicitacao agrup : listaLicitacao) {
            List<SelecaoItem> listaItem = licitacaoProgramaService.buscarSelecaoItem(agrup);
            for (SelecaoItem si : listaItem) {

                List<BemUf> listUf = new ArrayList<BemUf>();
                listUf = licitacaoProgramaService.buscarBemUf(si);
                for (BemUf bemuf : listUf) {

                    // Verifica se a região já foi adicionada
                    if (!reg.contains(bemuf.getUf().getRegiao())) {
                        reg.add(bemuf.getUf().getRegiao());

                        if (!stringRegiao.equalsIgnoreCase("")) {
                            stringRegiao = stringRegiao.concat(" / ");
                        }
                        stringRegiao = stringRegiao.concat(" ").concat(bemuf.getUf().getRegiao().getSiglaRegiao()).concat(" ");
                    }

                    // Verifica se a UF já foi cadastrada
                    if (!uf.contains(bemuf.getUf())) {
                        uf.add(bemuf.getUf());

                        if (!stringUf.equalsIgnoreCase("")) {
                            stringUf = stringUf.concat(" / ");
                        }
                        stringUf = stringUf.concat(" ").concat(bemuf.getUf().getSiglaUf()).concat(" ");
                    }
                }
            }
        }

        listaRetornar.add(grupoItem);
        listaRetornar.add(stringRegiao);
        listaRetornar.add(stringUf);
        return listaRetornar;
    }

    private void acaoEditar(Item<Contrato> item) {
        setResponsePage(new ContratoPage(getPageParameters(), this, item.getModelObject(), programa, false, false));
    }

    private void excluir(AjaxRequestTarget target, Item<Contrato> item) {
        if (!getSideSession().hasRole(ROLE_MANTER_CONTRATO_EXCLUIR)) {
            throw new SecurityException();
        }
        contratoService.excluir(item.getModelObject().getId());
        target.add(panelResultado);
    }

    private void acaoVisualizar(Item<Contrato> item) {
        setResponsePage(new ContratoPage(getPageParameters(), this, item.getModelObject(), programa, true, false));
    }

    public Integer getItensPorPagina() {
        return itensPorPagina;
    }

    public void setItensPorPagina(Integer itensPorPagina) {
        this.itensPorPagina = itensPorPagina;
    }

}
