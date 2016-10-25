package br.gov.mj.side.web.view.programa.inscricao;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.authroles.authorization.strategies.role.metadata.MetaDataRoleAuthorizationStrategy;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.convert.IConverter;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.pagination.InfraAjaxPagingNavigator;
import br.gov.mj.infra.wicket.converter.MoneyBigDecimalConverter;
import br.gov.mj.infra.wicket.listener.FeedbackPanelListener;
import br.gov.mj.infra.wicket.model.LambdaModel;
import br.gov.mj.side.entidades.programa.ProgramaBem;
import br.gov.mj.side.entidades.programa.ProgramaKit;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoPrograma;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoProgramaBem;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoProgramaKit;
import br.gov.mj.side.web.service.ProgramaService;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.view.beneficiario.BeneficiarioPesquisaPage;

public class DadosSolicitacaoPanel extends Panel {

    private static final long serialVersionUID = 1L;

    private PanelPrincipalSolicitacao panelPrincipalSolicitacao;

    private PanelValores panelSomenteLabelValorMaximo;
    private PanelDataViewBens panelDataViewBens;
    private PanelDataViewKits panelDataViewKits;
    private KitSelecionadosProvider kitSelecionadosProvider;
    private BemSelecionadosProvider bemSelecionadosProvider;
    private boolean readOnly;
    
    private DataView<InscricaoProgramaBem> dataviewBensAdicionados;
    private DataView<InscricaoProgramaKit> dataviewKitsAdicionados;
    private InfraAjaxPagingNavigator paginatorKit;
    private InfraAjaxPagingNavigator paginatorBem;
    private Label labelValorMaximoPorProposta;

    private InscricaoPrograma inscricaoPrograma;
    private BigDecimal valorUtilizado;
    private BigDecimal valorMaximoPorProposta;

    private String className = "has-success";
    private PropertyModel<String> modelClassCss = new PropertyModel<String>(this, "className");
    private AttributeModifier classCss = AttributeModifier.append("class", modelClassCss);

    private boolean checkTodosBensSelecionados;
    private boolean checkTodosKitsSelecionados;
    private Integer itensBensPorPagina = Constants.ITEMS_PER_PAGE_PAGINATION;
    private Integer itensKitsPorPagina = Constants.ITEMS_PER_PAGE_PAGINATION;
    private long paginaAtualBem;
    private long paginaAtualKit;

    private List<InscricaoProgramaBem> listaDeBens = new ArrayList<InscricaoProgramaBem>();
    private List<InscricaoProgramaKit> listaDeKits = new ArrayList<InscricaoProgramaKit>();

    @Inject
    private ProgramaService programaService;

    @Inject
    private ComponentFactory componentFactory;

    public DadosSolicitacaoPanel(String id, InscricaoPrograma inscricaoPrograma, boolean readOnly) {
        super(id);
        setReadOnly(readOnly);
        this.inscricaoPrograma = inscricaoPrograma;
        initVariaveis();
        initComponentes();
    }

    private void initVariaveis() {
        popularListaBens();
        popularListaKits();
        valorMaximoPorProposta = inscricaoPrograma.getPrograma().getValorMaximoProposta();
        valorUtilizado = calcularValorUtilizado();

    }

    private void popularListaKits() {
        List<ProgramaKit> kitsPrograma = programaService.buscarProgramakit(inscricaoPrograma.getPrograma());
        List<InscricaoProgramaKit> kitsSelecionados = inscricaoPrograma.getProgramasKit();

        for (ProgramaKit kit : kitsPrograma) {
            InscricaoProgramaKit inscricaoProgramaKit = new InscricaoProgramaKit();
            inscricaoProgramaKit.setProgramaKit(kit);
            listaDeKits.add(inscricaoProgramaKit);
        }

        // Setar objeto dos já selecionados
        for (InscricaoProgramaKit kitInscricao : listaDeKits) {
            for (InscricaoProgramaKit kitSelecionadoInscricao : kitsSelecionados) {
                if (kitInscricao.getProgramaKit().equals(kitSelecionadoInscricao.getProgramaKit())) {
                    kitInscricao.setId(kitSelecionadoInscricao.getId());
                    kitInscricao.setProgramaKit(kitSelecionadoInscricao.getProgramaKit());
                    kitInscricao.setQuantidade(kitSelecionadoInscricao.getQuantidade());
                }
            }
        }
    }

    private void popularListaBens() {

        List<ProgramaBem> bensPrograma = programaService.buscarProgramaBem(inscricaoPrograma.getPrograma());
        List<InscricaoProgramaBem> bensSelecionados = inscricaoPrograma.getProgramasBem();
        
        for (ProgramaBem bemPrograma : bensPrograma) {
            InscricaoProgramaBem inscricaoProgramaBem = new InscricaoProgramaBem();
            inscricaoProgramaBem.setProgramaBem(bemPrograma);
            listaDeBens.add(inscricaoProgramaBem);
        }

        // Setar objeto dos já selecionados
        for (InscricaoProgramaBem bemInscricao : listaDeBens) {
            for (InscricaoProgramaBem bemSelecionadoInscricao : bensSelecionados) {
                if (bemInscricao.getProgramaBem().equals(bemSelecionadoInscricao.getProgramaBem())) {
                    bemInscricao.setId(bemSelecionadoInscricao.getId());
                    bemInscricao.setQuantidade(bemSelecionadoInscricao.getQuantidade());
                    bemInscricao.setProgramaBem(bemSelecionadoInscricao.getProgramaBem());
                }
            }
        }
    }

    private void initComponentes() {
        panelPrincipalSolicitacao = new PanelPrincipalSolicitacao("panelPrincipalSolicitacao");

        add(panelPrincipalSolicitacao);
    }

    private class PanelPrincipalSolicitacao extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelPrincipalSolicitacao(String id) {
            super(id);
            setOutputMarkupId(true);
            add(getLabelValorMaximoMoeda());
            add(panelSomenteLabelValorMaximo = new PanelValores("panelSomenteLabelValorMaximo"));
            panelDataViewBens = new PanelDataViewBens("panelDataViewBens");
            if(bemSelecionadosProvider.size()==0){
            	panelDataViewBens.setVisible(Boolean.FALSE);
            }
            add(panelDataViewBens);
            
            panelDataViewKits = new PanelDataViewKits("panelDataViewKits");
            if( kitSelecionadosProvider.size()==0){
            	panelDataViewKits.setVisible(Boolean.FALSE);
            }
            add(panelDataViewKits);

        }
    }

    private class PanelDataViewBens extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelDataViewBens(String id) {
            super(id);
            setOutputMarkupId(true);

            add(getCheckAdicionarTodosBens()); // checkAdicionarTodosBem
            bemSelecionadosProvider = new BemSelecionadosProvider();
            add(getDataViewBensAdicionados()); // dataBens
            add(getPaginatorBem()); // paginationBens
            add(getDropItensBensPorPagina()); // itensBensPorPagina
        }
    }

    private class PanelDataViewKits extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelDataViewKits(String id) {
            super(id);
            setOutputMarkupId(true);

            add(getCheckAdicionarTodosKits()); // checkAdicionarTodosKits
            kitSelecionadosProvider = new KitSelecionadosProvider();
            add(getDataViewKitsAdicionados()); // dataKits
            add(getPaginatorKit()); // paginationKits
            add(getDropItensKitsPorPagina()); // itensKitsPorPagina

        }
    }

    private class PanelValores extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelValores(String id) {
            super(id);
            setOutputMarkupId(true);
            add(getLabelValorEstimado());
            add(getLabelMsgValorExcedido());
            definirClassName();
            add(classCss);
        }
    }

    // Paineis que mostrarão a quantidade de kits e bens selecionados

    private class PanelQuantidadeItemBem extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelQuantidadeItemBem(String id, Item<InscricaoProgramaBem> item, Boolean bemJaSelecionado) {
            super(id);
            setOutputMarkupId(true);
            TextField<Integer> textQuantidade = new TextField<Integer>("quantidade", new PropertyModel<Integer>(item.getModel(), "quantidade"));
            textQuantidade.add(new AjaxFormComponentUpdatingBehavior("onkeyup") {
                private static final long serialVersionUID = 1L;

                protected void onUpdate(AjaxRequestTarget target) {
                    desabilitarFeedBackListener();
                    // Atualiza a quantidade do bem selecionado na lista de bens da Inscrição do Programa
                    InscricaoProgramaBem bemModel = item.getModelObject();
                    for (InscricaoProgramaBem bem : inscricaoPrograma.getProgramasBem()) {
                        if (bem.getProgramaBem().equals(bemModel.getProgramaBem())) {
                            bem.setQuantidade(bemModel.getQuantidade());
                        }
                    }
                    atualizarValorUtilizado(target);
                }
            });
            textQuantidade.setEnabled(bemJaSelecionado);
            add(textQuantidade);
            setEnabled(!isReadOnly());
        }
    }

    private void atualizarValorUtilizado(AjaxRequestTarget target) {
        valorUtilizado = calcularValorUtilizado();
        panelSomenteLabelValorMaximo.addOrReplace(getLabelValorEstimado());
        panelSomenteLabelValorMaximo.addOrReplace(getLabelMsgValorExcedido());
        definirClassName();

        target.add(panelSomenteLabelValorMaximo);
    }

    private void definirClassName() {
        if (isValorExcedido()) {
            className = "has-error";
        } else {
            className = "has-success";
        }
    }

    private boolean isValorExcedido() {
        return valorUtilizado.compareTo(valorMaximoPorProposta) > 0;
    }

    public Label getLabelMsgValorExcedido() {
        Label lblMsgValorExcedido = componentFactory.newLabel("lblMsgValorExcedido", getString("MT016"));
        lblMsgValorExcedido.setVisible(isValorExcedido());
        return lblMsgValorExcedido;
    }

    private class PanelQuantidadeItemKit extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelQuantidadeItemKit(String id, Item<InscricaoProgramaKit> item, Boolean kitJaSelecionado) {
            super(id);
            setOutputMarkupId(true);
            TextField<Integer> textQuantidade = new TextField<Integer>("quantidade", new PropertyModel<Integer>(item.getModel(), "quantidade"));
            textQuantidade.add(new AjaxFormComponentUpdatingBehavior("onkeyup") {
                private static final long serialVersionUID = 1L;

                protected void onUpdate(AjaxRequestTarget target) {
                    desabilitarFeedBackListener();
                    
                    // Atualiza a quantidade do kit selecionado na lista de kits da Inscrição do Programa
                    InscricaoProgramaKit kitModel = item.getModelObject();
                    for (InscricaoProgramaKit kit : inscricaoPrograma.getProgramasKit()) {
                        if (kit.getProgramaKit().equals(kitModel.getProgramaKit())) {
                            kit.setQuantidade(kitModel.getQuantidade());
                        }
                    }
                    atualizarValorUtilizado(target);

                }
            });
            textQuantidade.setEnabled(kitJaSelecionado);
            add(textQuantidade);
            setEnabled(!isReadOnly());
        }
    }

    public DataView<InscricaoProgramaBem> getDataViewBensAdicionados() {

        dataviewBensAdicionados = new DataView<InscricaoProgramaBem>("dataBens", bemSelecionadosProvider) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<InscricaoProgramaBem> item) {
                item.clearOriginalDestination();

                // Irá verificar se este checkBox já virá marcado ou não
                Boolean bemJaSelecionado = verificarBemJaAdicionadoALista(item.getModelObject());
                item.add(getCheckAdicionarBem(item, bemJaSelecionado)); // checkAdicionarBem
                item.add(new BemInfo("bem", item.getModelObject().getProgramaBem()));
                item.add(new PanelQuantidadeItemBem("quantidadeItemBemPanel", item, bemJaSelecionado));
            }
        };
        dataviewBensAdicionados.setItemsPerPage(getItensBensPorPagina());
        return dataviewBensAdicionados;
    }

    public DataView<InscricaoProgramaKit> getDataViewKitsAdicionados() {

        dataviewKitsAdicionados = new DataView<InscricaoProgramaKit>("dataKits", kitSelecionadosProvider) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<InscricaoProgramaKit> item) {
                item.clearOriginalDestination();

                // Irá verificar se este checkBox já virá marcado ou não
                Boolean kitJaSelecionado = verificarKitJaAdicionadoALista(item.getModelObject());
                item.add(getCheckAdicionarKit(item, kitJaSelecionado)); // checkAdicionarKit
                item.add(new KitInfo("kit", item.getModelObject().getProgramaKit()));
                item.add(new PanelQuantidadeItemKit("quantidadeItemKitPanel", item, kitJaSelecionado));
            }
        };
        dataviewKitsAdicionados.setItemsPerPage(getItensBensPorPagina());
        return dataviewKitsAdicionados;
    }

    public InfraAjaxPagingNavigator getPaginatorBem() {
        paginatorBem = new InfraAjaxPagingNavigator("paginationBens", dataviewBensAdicionados);
        return paginatorBem;
    }

    public InfraAjaxPagingNavigator getPaginatorKit() {
        paginatorKit = new InfraAjaxPagingNavigator("paginationKits", dataviewKitsAdicionados);
        return paginatorKit;
    }

    private AjaxCheckBox getCheckAdicionarTodosBens() {

        AjaxCheckBox checkAdicionarTodosBens = new AjaxCheckBox("checkAdicionarTodosBem", new PropertyModel<Boolean>(this, "checkTodosBensSelecionados")) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                desabilitarFeedBackListener();
                acaoCheckAdicionarTodosbens();
                valorUtilizado = calcularValorUtilizado();
                atualizarBemAposAdicionarBensEKitsALista(target);
            }
        };
        checkAdicionarTodosBens.setOutputMarkupId(true);
        checkAdicionarTodosBens.setVisible(!isReadOnly());
        MetaDataRoleAuthorizationStrategy.authorize(checkAdicionarTodosBens, RENDER, InscricaoProgramaPage.ROLE_MANTER_INSCRICAO_INCLUIR);
        return checkAdicionarTodosBens;
    }

    private AjaxCheckBox getCheckAdicionarTodosKits() {

        AjaxCheckBox checkAdicionarTodosKits = new AjaxCheckBox("checkAdicionarTodosKits", new PropertyModel<Boolean>(this, "checkTodosKitsSelecionados")) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                desabilitarFeedBackListener();
                acaoCheckAdicionarTodosKits();
                valorUtilizado = calcularValorUtilizado();
                atualizarKitAposAdicionarBensEKitsALista(target);
            }
        };
        checkAdicionarTodosKits.setOutputMarkupId(true);
        checkAdicionarTodosKits.setVisible(!isReadOnly());
        MetaDataRoleAuthorizationStrategy.authorize(checkAdicionarTodosKits, RENDER, InscricaoProgramaPage.ROLE_MANTER_INSCRICAO_INCLUIR);
        return checkAdicionarTodosKits;
    }

    private AjaxCheckBox getCheckAdicionarBem(Item<InscricaoProgramaBem> item, Boolean bemJaSelecionado) {

        AjaxCheckBox checkAdicionarBem = new AjaxCheckBox("checkAdicionarBem", new Model<Boolean>(bemJaSelecionado)) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                desabilitarFeedBackListener();
                acaoCheck(item, bemJaSelecionado, target);
            }
        };
        checkAdicionarBem.setOutputMarkupId(true);
        checkAdicionarBem.setVisible(!isReadOnly());
        MetaDataRoleAuthorizationStrategy.authorize(checkAdicionarBem, RENDER, InscricaoProgramaPage.ROLE_MANTER_INSCRICAO_INCLUIR);
        return checkAdicionarBem;
    }

    private AjaxCheckBox getCheckAdicionarKit(Item<InscricaoProgramaKit> item, Boolean kitJaSelecionado) {

        AjaxCheckBox checkAdicionarKit = new AjaxCheckBox("checkAdicionarKit", new Model<Boolean>(kitJaSelecionado)) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                desabilitarFeedBackListener();
                acaoCheckKit(item, kitJaSelecionado, target);
            }
        };
        checkAdicionarKit.setOutputMarkupId(true);
        checkAdicionarKit.setVisible(!isReadOnly());
        MetaDataRoleAuthorizationStrategy.authorize(checkAdicionarKit, RENDER, InscricaoProgramaPage.ROLE_MANTER_INSCRICAO_INCLUIR);
        return checkAdicionarKit;
    }

    private DropDownChoice<Integer> getDropItensBensPorPagina() {
        DropDownChoice<Integer> dropDownChoice = new DropDownChoice<Integer>("itensBensPorPagina", new LambdaModel<Integer>(this::getItensBensPorPagina, this::setItensBensPorPagina), Constants.QUANTIDADE_ITENS_TABELA);
        dropDownChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                desabilitarFeedBackListener();
                dataviewBensAdicionados.setItemsPerPage(getItensBensPorPagina());
                panelDataViewBens.addOrReplace(getPaginatorBem());

                target.add(panelDataViewBens);
            };
        });
        return dropDownChoice;
    }

    private DropDownChoice<Integer> getDropItensKitsPorPagina() {
        DropDownChoice<Integer> dropDownChoice = new DropDownChoice<Integer>("itensKitsPorPagina", new LambdaModel<Integer>(this::getItensKitsPorPagina, this::setItensKitsPorPagina), Constants.QUANTIDADE_ITENS_TABELA);
        dropDownChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                desabilitarFeedBackListener();
                dataviewKitsAdicionados.setItemsPerPage(getItensKitsPorPagina());
                target.add(panelDataViewKits);
            };
        });
        return dropDownChoice;
    }

    private Label getLabelValorMaximoMoeda() {
        labelValorMaximoPorProposta = new Label("lblValorMaximoMoeda", valorMaximoPorProposta) {
            private static final long serialVersionUID = 1L;

            @SuppressWarnings("unchecked")
            @Override
            public <C> IConverter<C> getConverter(Class<C> type) {
                return (IConverter<C>) new MoneyBigDecimalConverter();
            }

        };
        labelValorMaximoPorProposta.setEnabled(true);
        return labelValorMaximoPorProposta;
    }

    private Label getLabelValorEstimado() {
        Label labelEstimado = new Label("lblValorEstimado", valorUtilizado) {
            private static final long serialVersionUID = 1L;

            @SuppressWarnings("unchecked")
            @Override
            public <C> IConverter<C> getConverter(Class<C> type) {
                return (IConverter<C>) new MoneyBigDecimalConverter();
            }
        };
        labelEstimado.setOutputMarkupId(true);
        return labelEstimado;
    }

    // AÇÕES

    public String formatoDinheiro(double bigDecimal) {
        return NumberFormat.getCurrencyInstance().format(bigDecimal);
    }

    private void acaoCheckAdicionarTodosbens() {
        if (checkTodosBensSelecionados) {
            for (InscricaoProgramaBem ipb : listaDeBens) {
                if (!verificarBemJaAdicionadoALista(ipb)) {
                    InscricaoProgramaBem itemNovo = new InscricaoProgramaBem();
                    itemNovo = ipb;
                    itemNovo.setQuantidade(0);
                    inscricaoPrograma.getProgramasBem().add(itemNovo);
                }
            }
        } else {
            inscricaoPrograma.getProgramasBem().clear();

            for (InscricaoProgramaBem ipb : listaDeBens) {
                ipb.setQuantidade(null);
            }
        }
    }

    private void acaoCheckAdicionarTodosKits() {
        if (checkTodosKitsSelecionados) {
            for (InscricaoProgramaKit ipk : listaDeKits) {
                if (!verificarKitJaAdicionadoALista(ipk)) {
                    InscricaoProgramaKit itemNovo = new InscricaoProgramaKit();
                    itemNovo = ipk;
                    itemNovo.setQuantidade(0);
                    inscricaoPrograma.getProgramasKit().add(itemNovo);
                }
            }
        } else {
            inscricaoPrograma.getProgramasKit().clear();

            for (InscricaoProgramaKit ipk : listaDeKits) {
                ipk.setQuantidade(null);
            }
        }
    }

    private void acaoCheck(Item<InscricaoProgramaBem> item, Boolean bemJaSelecionado, AjaxRequestTarget target) {

        if (bemJaSelecionado) {
            List<InscricaoProgramaBem> lista = inscricaoPrograma.getProgramasBem();
            for (InscricaoProgramaBem ipb : lista) {
                if (ipb.getProgramaBem().getId() == item.getModelObject().getProgramaBem().getId()) {
                    inscricaoPrograma.getProgramasBem().remove(item.getModelObject());
                    item.getModelObject().setQuantidade(null);
                    break;
                }
            }
        } else {
            InscricaoProgramaBem itemNovo = new InscricaoProgramaBem();
            itemNovo = item.getModelObject();
            itemNovo.setQuantidade(0);
            inscricaoPrograma.getProgramasBem().add(itemNovo);
        }

        valorUtilizado = calcularValorUtilizado();
        atualizarBemAposAdicionarBensEKitsALista(target);
    }

    private void acaoCheckKit(Item<InscricaoProgramaKit> item, Boolean kitJaSelecionado, AjaxRequestTarget target) {

        if (kitJaSelecionado) {
            List<InscricaoProgramaKit> lista = inscricaoPrograma.getProgramasKit();
            for (InscricaoProgramaKit ipk : lista) {
                if (ipk.getProgramaKit().getId() == item.getModelObject().getProgramaKit().getId()) {
                    inscricaoPrograma.getProgramasKit().remove(item.getModelObject());
                    item.getModelObject().setQuantidade(null);
                    break;
                }
            }
        } else {
            InscricaoProgramaKit itemNovo = new InscricaoProgramaKit();
            itemNovo = item.getModelObject();
            itemNovo.setQuantidade(0);
            inscricaoPrograma.getProgramasKit().add(itemNovo);
        }

        valorUtilizado = calcularValorUtilizado();
        atualizarKitAposAdicionarBensEKitsALista(target);
    }

    private void atualizarBemAposAdicionarBensEKitsALista(AjaxRequestTarget target) {
        paginaAtualBem = dataviewBensAdicionados.getCurrentPage();

        // panelSomenteLabelValorMaximo.addOrReplace(getLabelValorEstimado());
        atualizarValorUtilizado(target);
        panelDataViewBens.addOrReplace(getDataViewBensAdicionados());
        panelDataViewBens.addOrReplace(getPaginatorBem());
        dataviewBensAdicionados.setCurrentPage(paginaAtualBem);

        target.add(panelSomenteLabelValorMaximo);
        target.add(panelDataViewBens);
    }

    private void atualizarKitAposAdicionarBensEKitsALista(AjaxRequestTarget target) {
        paginaAtualKit = dataviewKitsAdicionados.getCurrentPage();

        atualizarValorUtilizado(target);
        panelDataViewKits.addOrReplace(getDataViewKitsAdicionados());
        dataviewKitsAdicionados.setCurrentPage(paginaAtualKit);

        target.add(panelSomenteLabelValorMaximo);
        target.add(panelDataViewKits);
    }

    // Irá verificar se o Bem clicado já esta na lista de selecionados ou não
    private boolean verificarBemJaAdicionadoALista(InscricaoProgramaBem item) {
        List<InscricaoProgramaBem> lista = inscricaoPrograma.getProgramasBem();
        boolean jaEstaNaLista = false;
        for (InscricaoProgramaBem ipb : lista) {
            if (ipb.getProgramaBem().getId() == item.getProgramaBem().getId()) {
                jaEstaNaLista = true;
                break;
            }
        }

        return jaEstaNaLista;
    }

    // Irá verificar se o Kit clicado já esta na lista de selecionados ou não
    private boolean verificarKitJaAdicionadoALista(InscricaoProgramaKit item) {
        List<InscricaoProgramaKit> lista = inscricaoPrograma.getProgramasKit();
        boolean jaEstaNaLista = false;
        for (InscricaoProgramaKit ipk : lista) {
            if (ipk.getProgramaKit().getId() == item.getProgramaKit().getId()) {
                jaEstaNaLista = true;
                break;
            }
        }

        return jaEstaNaLista;
    }

    // PROVIDER

    private class BemSelecionadosProvider extends SortableDataProvider<InscricaoProgramaBem, String> {
        private static final long serialVersionUID = 1L;

        public BemSelecionadosProvider() {
            // setSort(NOME_BEM, SortOrder.ASCENDING);
        }

        @Override
        public Iterator<InscricaoProgramaBem> iterator(long first, long size) {

            List<InscricaoProgramaBem> bemTemp = new ArrayList<InscricaoProgramaBem>();

            int firstTemp = 0;
            int flagTemp = 0;
            for (InscricaoProgramaBem k : listaDeBens) {
                if (firstTemp >= first) {
                    if (flagTemp <= size) {
                        bemTemp.add(k);
                        flagTemp++;
                    }
                }
                firstTemp++;
            }
            return bemTemp.iterator();
        }

        @Override
        public long size() {
            return listaDeBens.size();
        }

        @Override
        public IModel<InscricaoProgramaBem> model(InscricaoProgramaBem object) {
            return new CompoundPropertyModel<InscricaoProgramaBem>(object);
        }
    }

    public Integer getItensBensPorPagina() {
        return itensBensPorPagina;
    }

    public void setItensBensPorPagina(Integer itensBensPorPagina) {
        this.itensBensPorPagina = itensBensPorPagina;
    }

    private BigDecimal calcularValorUtilizado() {
        BigDecimal total = BigDecimal.ZERO;
        total = total.add(calcularTotalValorUtilizadoBens());
        total = total.add(calcularTotalValorUtilizadoKits());

        return total;
    }

    // TODO Adicionar metodo na entidade InscricaoPrograma.
    private BigDecimal calcularTotalValorUtilizadoBens() {
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal totalParcial;

        for (InscricaoProgramaBem bemUtilizado : listaDeBens) {
            if (bemUtilizado.getQuantidade() != null) {
                totalParcial = BigDecimal.ZERO;
                totalParcial = bemUtilizado.getProgramaBem().getBem().getValorEstimadoBem().multiply(new BigDecimal(bemUtilizado.getQuantidade()));
                total = total.add(totalParcial);
            }
        }

        return total;
    }

    // TODO Adicionar metodo na entidade InscricaoPrograma.
    private BigDecimal calcularTotalValorUtilizadoKits() {
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal totalParcial;

        for (InscricaoProgramaKit kitUtilizado : listaDeKits) {
            if (kitUtilizado.getQuantidade() != null) {
                totalParcial = BigDecimal.ZERO;
                totalParcial = kitUtilizado.getProgramaKit().getKit().getValorEstimado().multiply(new BigDecimal(kitUtilizado.getQuantidade()));
                total = total.add(totalParcial);
            }
        }

        return total;
    }

    private class KitSelecionadosProvider extends SortableDataProvider<InscricaoProgramaKit, String> {
        private static final long serialVersionUID = 1L;

        public KitSelecionadosProvider() {
            // setSort(NOME_BEM, SortOrder.ASCENDING);
        }

        @Override
        public Iterator<InscricaoProgramaKit> iterator(long first, long size) {

            List<InscricaoProgramaKit> bemTemp = new ArrayList<InscricaoProgramaKit>();

            int firstTemp = 0;
            int flagTemp = 0;
            for (InscricaoProgramaKit k : listaDeKits) {
                if (firstTemp >= first) {
                    if (flagTemp <= size) {
                        bemTemp.add(k);
                        flagTemp++;
                    }
                }
                firstTemp++;
            }
            return bemTemp.iterator();
        }

        @Override
        public long size() {
            return listaDeKits.size();
        }

        @Override
        public IModel<InscricaoProgramaKit> model(InscricaoProgramaKit object) {
            return new CompoundPropertyModel<InscricaoProgramaKit>(object);
        }
    }

    public Integer getItensKitsPorPagina() {
        return itensKitsPorPagina;
    }

    public void setItensKitsPorPagina(Integer itensKitsPorPagina) {
        this.itensKitsPorPagina = itensKitsPorPagina;
    }

    private class BemInfo extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        @SuppressWarnings("unused")
        ProgramaBem programaBem;

        public BemInfo(String id, ProgramaBem programaBem) {
            super(id);

            add(componentFactory.newLabel("nomeBem", programaBem.getBem().getNomeBem()));
            add(componentFactory.newLabel("descricaoBem", programaBem.getBem().getDescricaoBem()));
            
            String quantidadeProposta = "";
            if(programaBem ==null || (programaBem.getQuantidadePorProposta() == null || programaBem.getQuantidadePorProposta() == 0)){
                quantidadeProposta = " - ";
            }else{
                quantidadeProposta = programaBem.getQuantidadePorProposta().toString();
            }
            
            add(componentFactory.newLabel("quantidade", quantidadeProposta));
            add(new Label("valorEstimado", programaBem.getBem().getValorEstimadoBem()) {
                private static final long serialVersionUID = 1L;

                @SuppressWarnings("unchecked")
                @Override
                public <C> IConverter<C> getConverter(Class<C> type) {
                    return (IConverter<C>) new MoneyBigDecimalConverter();
                }

            });
        }
    }

    private class KitInfo extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        @SuppressWarnings("unused")
        ProgramaBem programaBem;

        public KitInfo(String id, ProgramaKit programaKit) {
            super(id);

            add(componentFactory.newLabel("nomeKit", programaKit.getKit().getNomeKit()));
            add(componentFactory.newLabel("descricaoKit", programaKit.getKit().getDescricaoKit()));
            
            String quantidadeProposta = "";
            if(programaKit==null || (programaKit.getQuantidadePorProposta() == null || programaKit.getQuantidadePorProposta() == 0)){
                quantidadeProposta = " - ";
            }else{
                quantidadeProposta = programaKit.getQuantidadePorProposta().toString();
            }
            
            add(componentFactory.newLabel("quantidade", quantidadeProposta));
            add(new Label("valorEstimado", programaKit.getKit().getValorEstimado()) {
                private static final long serialVersionUID = 1L;

                @SuppressWarnings("unchecked")
                @Override
                public <C> IConverter<C> getConverter(Class<C> type) {
                    return (IConverter<C>) new MoneyBigDecimalConverter();
                }

            });
        }
    }

    /**
     * Desabilita FEEDBACK_LISTENER para não remover mensagens do feedbackPanel
     */
    private void desabilitarFeedBackListener(){
        RequestCycle.get().setMetaData(FeedbackPanelListener.SKIP_FEEDBACK_LISTENER, Boolean.TRUE);
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }
}
