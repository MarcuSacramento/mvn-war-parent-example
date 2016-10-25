package br.gov.mj.side.web.view.programa.contrato.ordemfornecimento;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraAjaxFallbackLink;
import br.gov.mj.infra.wicket.component.pagination.InfraAjaxPagingNavigator;
import br.gov.mj.infra.wicket.listener.FeedbackPanelListener;
import br.gov.mj.infra.wicket.model.LambdaModel;
import br.gov.mj.side.entidades.enums.EnumStatusComunicacaoOrdemFornecimento;
import br.gov.mj.side.entidades.enums.EnumStatusOrdemFornecimento;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.licitacao.contrato.Contrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.HistoricoComunicacaoGeracaoOrdemFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.ItensOrdemFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.OrdemFornecimentoContrato;
import br.gov.mj.side.web.dto.EnderecosOrdemFornecimentoDto;
import br.gov.mj.side.web.dto.enderecoLicitacao.AgrupamentoDto;
import br.gov.mj.side.web.dto.enderecoLicitacao.BemUfLicitacaoDto;
import br.gov.mj.side.web.dto.enderecoLicitacao.EnderecoDto;
import br.gov.mj.side.web.service.ContratoService;
import br.gov.mj.side.web.service.OrdemFornecimentoContratoService;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.view.HomePage;
import br.gov.mj.side.web.view.planejarLicitacao.ContratoPanelBotoes;
import br.gov.mj.side.web.view.programa.PanelFasePrograma;
import br.gov.mj.side.web.view.template.TemplatePage;

public class OrdemFornecimentoPage extends TemplatePage {
    private static final long serialVersionUID = 1L;

    // ##################### variaveis ########################

    private Form<OrdemFornecimentoPage> form;
    private Programa programa;
    private OrdemFornecimentoContrato ordemFornecimentoSelecionada = new OrdemFornecimentoContrato();
    private Contrato contratoSelecionado;
    private Page backPage;
    private Integer itensPorPagina = Constants.ITEMS_PER_PAGE_PAGINATION;
    private String coluna = "item";
    private AttributeAppender classArrow = new AttributeAppender("class", "mj_arrow_asc", " ");
    private AttributeAppender classArrowUnsorted = new AttributeAppender("class", "mj_arrow_unsorted", " ");
    private int order = 1; // 0 é ordenamento crescente, 1 decrescente
    private boolean checkTodosBensSelecionados;
    private boolean mostrarCampos = false;
    private HistoricoComunicacaoGeracaoOrdemFornecimentoContrato ultimoHistorico;

    private List<EnderecosOrdemFornecimentoDto> listaComTodosEnderecos = new ArrayList<EnderecosOrdemFornecimentoDto>();
    private List<ItensOrdemFornecimentoContrato> listaDeTodosItensJaEmUso = new ArrayList<ItensOrdemFornecimentoContrato>();
    private List<ItensOrdemFornecimentoContrato> listaDosItensDestaOf = new ArrayList<ItensOrdemFornecimentoContrato>();

    // ##################### paineis ##########################

    private PanelFasePrograma panelFasePrograma;
    private ContratoPanelBotoes execucaoPanelBotoes;
    private PanelLocaisDeEntrega panelLocaisDeEntrega;
    private PanelPrincipal panelPrincipal;

    // ##################### Componentes Wicket ###############

    private DataView<EnderecosOrdemFornecimentoDto> dataViewLocaisEntrega;
    private Link link;

    // ##################### Injeções de dependências #########

    @Inject
    private ContratoService contratoService;
    @Inject
    private ComponentFactory componentFactory;
    @Inject
    private OrdemFornecimentoContratoService ordemFornecimentoContratoService;

    public OrdemFornecimentoPage(final PageParameters pageParameters, OrdemFornecimentoContrato ordemFornecimento, Contrato contratoSelecionado, Page backPage) {
        super(pageParameters);
        this.programa = ordemFornecimento.getContrato().getPrograma();
        this.ordemFornecimentoSelecionada = ordemFornecimento;
        this.contratoSelecionado = contratoSelecionado;
        this.backPage = backPage;

        setTitulo("Gerenciar Programa");
        initVariaveis();
        initComponents();
    }

    private void initVariaveis() {
        buscaTodosOsItensJaEmUso();
        buscarTodosItensDestaOrdemDeServico();
        itensPorPagina = Constants.ITEMS_PER_PAGE_PAGINATION;
        
        if (ordemFornecimentoSelecionada != null && ordemFornecimentoSelecionada.getId() != null) {
            ultimoHistorico = ordemFornecimentoContratoService.buscarUltimoHistoricoComunicacaoGeracaoOrdemFornecimentoContrato(ordemFornecimentoSelecionada, false);
            coluna = "quantidadeSelecionada"; // ordem que será feita a ordenação do dataView.
            order = -1; //será mostrada a quantidade selecionada pelo cliente do maior para o menor
        }

        mostrarCamposDeTextField();

        List<AgrupamentoDto> listaAgrupamentosLicitacao = contratoService.buscarEnderecosDoContrato(contratoSelecionado);
        montarEnderecosEntrega(listaAgrupamentosLicitacao);
    }

    private void initComponents() {
        form = new Form<OrdemFornecimentoPage>("form", new CompoundPropertyModel<OrdemFornecimentoPage>(this));
        add(form);

        form.add(link = new Link("link") {
            @Override
            public void onClick() {
                setResponsePage(HomePage.class);
            }
        });

        form.add(execucaoPanelBotoes = new ContratoPanelBotoes("execucaoPanelPotoes", programa, backPage, "ordemFornecimento"));
        form.add(panelFasePrograma = new PanelFasePrograma("panelFasePrograma", programa, backPage));
        form.add(panelPrincipal = new PanelPrincipal("panelPrincipal"));

        form.add(newButtonSalvar()); // btnSalvar
        form.add(newButtonVoltar()); // btnVoltar
    }

    // PAINEIS

    private class PanelPrincipal extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelPrincipal(String id) {
            super(id);
            setOutputMarkupId(true);

            add(panelLocaisDeEntrega = new PanelLocaisDeEntrega("panelLocaisDeEntrega"));
        }
    }

    private class PanelLocaisDeEntrega extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelLocaisDeEntrega(String id) {
            super(id);
            setOutputMarkupId(true);

            add(newCheckAdicionarTodosBens()); // checkAdicionarTodosBem

            add(newDropItensPorPagina()); // itensPorPagina
            add(newDataViewListaContrato()); // listaContrato
            add(new InfraAjaxPagingNavigator("pagination", dataViewLocaisEntrega));

            add(newLabelSortDescricao()); // lblOrderItem
            add(newLabelSortEndereco()); // lblOrderEndereco
            add(newLabelSortUf()); // lblOrderUf
            add(newLabelSortQuantidade()); // lblOrderQuantidade
            add(newLabelSortQuantidadeAnterior()); // lblOrderQuantidadeAnterior
            add(newLabelSortQuantidadeRestante()); // lblOrderQuantidadeRestante

            add(newButtonOrdenarItem()); // btnOrdenarItem
            add(newButtonOrdenarEndereco()); // btnOrdenarEndereco
            add(newButtonOrdenarUf()); // btnOrdenarUf
            add(newButtonOrdenarQuantidade()); // btnOrdenarQuantidade
            add(newButtonOrdenarQuantidadeAnterior()); // btnOrdenarQuantidadeAnterior
            add(newButtonOrdenarQuantidadeRestante()); // btnOrdenarQuantidadeRestante
        }
    }

    private class PanelQuantidadedaOF extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelQuantidadedaOF(String id, Item<EnderecosOrdemFornecimentoDto> item) {
            super(id);
            setOutputMarkupId(true);
            add(newTextFieldQuantidadeOf(item)); // txtQuatidadeOf
        }
    }

    // Componentes

    private AjaxCheckBox newCheckAdicionarTodosBens() {

        AjaxCheckBox checkAdicionarTodosBens = new AjaxCheckBox("checkAdicionarTodosBem", new PropertyModel<Boolean>(this, "checkTodosBensSelecionados")) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                desabilitarFeedBackListener();
                acaoCheckAdicionarTodosbens();
                atualizarDataView(target);
            }
        };
        checkAdicionarTodosBens.setOutputMarkupId(true);
        checkAdicionarTodosBens.setEnabled(mostrarCampos);
        return checkAdicionarTodosBens;
    }

    private DropDownChoice<Integer> newDropItensPorPagina() {
        DropDownChoice<Integer> dropDownChoice = new DropDownChoice<Integer>("itensPorPagina", new LambdaModel<Integer>(this::getItensPorPagina, this::setItensPorPagina), Constants.QUANTIDADE_ITENS_TABELA);
        dropDownChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                dataViewLocaisEntrega.setItemsPerPage(getItensPorPagina());
                target.add(panelLocaisDeEntrega);
            };
        });
        return dropDownChoice;
    }

    public DataView<EnderecosOrdemFornecimentoDto> newDataViewListaContrato() {
        dataViewLocaisEntrega = new DataView<EnderecosOrdemFornecimentoDto>("listaContrato", new ListaLocaisEntregaProvider()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<EnderecosOrdemFornecimentoDto> item) {

                Boolean ativarLinha = ativarLinha(item);

                Label lblItem = new Label("itemEntrega", item.getModelObject().getBem().getNomeBem());
                Label lblUf = new Label("ufEntrega", item.getModelObject().getUf().getSiglaUf());
                Label lblEndereco = new Label("enderecoEntrega", item.getModelObject().getEndereco());
                Label lblQuantidade = new Label("quantidadeTotal", item.getModelObject().getQuantidade());
                Label lblQuantidadeRestante = new Label("quantidadeRestante", item.getModelObject().getQuatidadeRestante());
                PanelQuantidadedaOF panelQtdOf = new PanelQuantidadedaOF("panelQuantidadeOf", item);

                lblItem.setEnabled(ativarLinha);
                lblQuantidade.setEnabled(ativarLinha);
                lblUf.setEnabled(ativarLinha);
                lblQuantidadeRestante.setEnabled(ativarLinha);
                panelQtdOf.setEnabled(ativarLinha);

                item.add(newCheckAdicionarBem(item)); // checkAdicionarBem
                item.add(lblItem);
                item.add(lblUf);
                item.add(lblEndereco);
                item.add(lblQuantidade);
                item.add(lblQuantidadeRestante);
                item.add(panelQtdOf);
            }
        };
        dataViewLocaisEntrega.setItemsPerPage(getItensPorPagina());
        return dataViewLocaisEntrega;
    }

    private boolean ativarLinha(Item<EnderecosOrdemFornecimentoDto> dto) {
        if (dto.getModelObject().getSelecionado() == null || !dto.getModelObject().getSelecionado()) {
            return false;
        } else {
            return true;
        }
    }

    private AjaxCheckBox newCheckAdicionarBem(Item<EnderecosOrdemFornecimentoDto> item) {

        AjaxCheckBox checkAdicionarBem = new AjaxCheckBox("checkAdicionarBem", new Model<Boolean>(item.getModelObject().getSelecionado())) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {

                if (item.getModelObject().getSelecionado() == null || !item.getModelObject().getSelecionado()) {
                    item.getModelObject().setSelecionado(true);
                } else {
                    item.getModelObject().setSelecionado(false);
                }

                atualizarDataView(target);
            }
        };
        checkAdicionarBem.setOutputMarkupId(true);
        checkAdicionarBem.setEnabled(mostrarCampos);
        return checkAdicionarBem;
    }

    public Label newLabelSortDescricao() {
        Label label = new Label("lblOrderItem", "...");

        if ("item".equalsIgnoreCase(coluna)) {
            label.add(classArrow);
        } else {
            label.add(classArrowUnsorted);
        }
        return label;
    }

    public Label newLabelSortUf() {
        Label label = new Label("lblOrderUf", "...");

        if ("uf".equalsIgnoreCase(coluna)) {
            label.add(classArrow);
        } else {
            label.add(classArrowUnsorted);
        }
        return label;
    }

    public Label newLabelSortEndereco() {
        Label label = new Label("lblOrderEndereco", "...");

        if ("endereco".equalsIgnoreCase(coluna)) {
            label.add(classArrow);
        } else {
            label.add(classArrowUnsorted);
        }
        return label;
    }

    public Label newLabelSortQuantidade() {
        Label label = new Label("lblOrderQuantidade", "...");

        if ("quantidade".equalsIgnoreCase(coluna)) {
            label.add(classArrow);
        } else {
            label.add(classArrowUnsorted);
        }
        return label;
    }

    public Label newLabelSortQuantidadeAnterior() {
        Label label = new Label("lblOrderQuantidadeAnterior", "...");

        if ("quantidadeSelecionada".equalsIgnoreCase(coluna)) {
            label.add(classArrow);
        } else {
            label.add(classArrowUnsorted);
        }
        return label;
    }

    public Label newLabelSortQuantidadeRestante() {
        Label label = new Label("lblOrderQuantidadeRestante", "...");

        if ("quantidadeRestante".equalsIgnoreCase(coluna)) {
            label.add(classArrow);
        } else {
            label.add(classArrowUnsorted);
        }
        return label;
    }

    public AjaxSubmitLink newButtonOrdenarItem() {
        return new AjaxSubmitLink("btnOrdenarItem", form) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form) {
                coluna = "item";
                mudarOrdemTabela();
                atualizarDataView(target);
            }
        };
    }

    public AjaxSubmitLink newButtonOrdenarUf() {
        return new AjaxSubmitLink("btnOrdenarUf", form) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form) {
                coluna = "uf";
                mudarOrdemTabela();
                atualizarDataView(target);
            }
        };
    }

    public AjaxSubmitLink newButtonOrdenarEndereco() {
        return new AjaxSubmitLink("btnOrdenarEndereco", form) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form) {
                coluna = "endereco";
                mudarOrdemTabela();
                atualizarDataView(target);
            }
        };
    }

    public AjaxSubmitLink newButtonOrdenarQuantidade() {
        return new AjaxSubmitLink("btnOrdenarQuantidade", form) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form) {
                coluna = "quantidade";
                mudarOrdemTabela();
                atualizarDataView(target);
            }
        };
    }

    public AjaxSubmitLink newButtonOrdenarQuantidadeAnterior() {
        return new AjaxSubmitLink("btnOrdenarQuantidadeAnterior", form) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form) {
                coluna = "quantidadeSelecionada";
                mudarOrdemTabela();
                atualizarDataView(target);
            }
        };
    }

    public AjaxSubmitLink newButtonOrdenarQuantidadeRestante() {
        return new AjaxSubmitLink("btnOrdenarQuantidadeRestante", form) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form) {
                coluna = "quantidadeRestante";
                mudarOrdemTabela();
                atualizarDataView(target);
            }
        };
    }
    
    private InfraAjaxFallbackLink newButtonSalvar(){
        InfraAjaxFallbackLink buttonSalvar = componentFactory.newAjaxFallbackLink("btnSalvar", (target) -> actionSalvar(target));
        buttonSalvar.setVisible(mostrarCampos);
        return buttonSalvar;
    }

    public AjaxSubmitLink newButtonVoltar() {
        AjaxSubmitLink buttonVoltar = new AjaxSubmitLink("btnVoltar", form) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form form) {
                actionVoltar(target);
            }
        };

        return buttonVoltar;
    }

    public TextField<Integer> newTextFieldQuantidadeOf(Item<EnderecosOrdemFornecimentoDto> item) {
        TextField<Integer> field = componentFactory.newTextField("txtQuatidadeOf", "Quantidade OF", false, new PropertyModel<Integer>(item.getModelObject(), "quantidadeDaOf"));
        acaoQuantidadeDaOf(field, item);
        field.setEnabled(mostrarCampos);
        return field;
    }

    // PROVIDER

    public class ListaLocaisEntregaProvider extends SortableDataProvider<EnderecosOrdemFornecimentoDto, String> {
        private static final long serialVersionUID = 1L;

        public ListaLocaisEntregaProvider() {
            // contruct
        }

        @Override
        public Iterator<? extends EnderecosOrdemFornecimentoDto> iterator(long first, long size) {

            List<EnderecosOrdemFornecimentoDto> listaRetorno = new ArrayList<EnderecosOrdemFornecimentoDto>();

            // Ordena a lista de acordo com a coluna clicada.
            Collections.sort(listaComTodosEnderecos, EnderecosOrdemFornecimentoDto.getComparator(order, coluna));

            int firstTemp = 0;
            int flagTemp = 0;
            for (EnderecosOrdemFornecimentoDto k : listaComTodosEnderecos) {
                if (firstTemp >= first) {
                    if (flagTemp <= size) {
                        listaRetorno.add(k);
                        flagTemp++;
                    }
                }
                firstTemp++;
            }

            return listaRetorno.iterator();
        }

        @Override
        public long size() {
            return listaComTodosEnderecos.size();
        }

        @Override
        public IModel<EnderecosOrdemFornecimentoDto> model(EnderecosOrdemFornecimentoDto object) {
            return new CompoundPropertyModel<EnderecosOrdemFornecimentoDto>(object);
        }
    }

    // AÇÕES

    private void mostrarCamposDeTextField() {
        // Se não tiver histórico então não foi comunicado nenhuma vez
        // sendo assim pode-se editar os campos
        if (ultimoHistorico != null) {
            if (ultimoHistorico.getPossuiComunicado()) {
            	mostrarCampos = false;
            } else {
                mostrarCampos = true;
            }
        } else {
            mostrarCampos = true;
        }
    }

    // Busca todo os itens que já estão em uso neste contrato, independente da
    // Of.
    private void buscaTodosOsItensJaEmUso() {
        List<OrdemFornecimentoContrato> listaDeOrdemDeFornecimentos = ordemFornecimentoContratoService.buscarOrdemFornecimentoContrato(contratoSelecionado);
        for (OrdemFornecimentoContrato ordemFornecimentoContrato : listaDeOrdemDeFornecimentos) {
        	
        	HistoricoComunicacaoGeracaoOrdemFornecimentoContrato historico = ordemFornecimentoContratoService.buscarUltimoHistoricoComunicacaoGeracaoOrdemFornecimentoContrato(ordemFornecimentoContrato, false);
        	if(historico == null || !historico.getPossuiCancelamento()){
        		List<ItensOrdemFornecimentoContrato> listaItem = ordemFornecimentoContratoService.buscarItensOrdemFornecimentoContrato(ordemFornecimentoContrato);
                listaDeTodosItensJaEmUso.addAll(listaItem);
        	}
            
        }
    }

    // Busca somente os itens em uso nesta ordem de Serviço clicada.
    private void buscarTodosItensDestaOrdemDeServico() {
    	
        if (ordemFornecimentoSelecionada != null && ordemFornecimentoSelecionada.getId() != null) {
        	listaDosItensDestaOf = ordemFornecimentoContratoService.buscarItensOrdemFornecimentoContrato(ordemFornecimentoSelecionada);
        }
    }

    public void atualizarDataView(AjaxRequestTarget target) {

        long paginaAtual = dataViewLocaisEntrega.getCurrentPage();

        panelLocaisDeEntrega.addOrReplace(newDataViewListaContrato());
        panelLocaisDeEntrega.addOrReplace(new InfraAjaxPagingNavigator("pagination", dataViewLocaisEntrega));

        panelLocaisDeEntrega.addOrReplace(newCheckAdicionarTodosBens());
        panelLocaisDeEntrega.addOrReplace(newLabelSortDescricao());
        panelLocaisDeEntrega.addOrReplace(newLabelSortEndereco());
        panelLocaisDeEntrega.addOrReplace(newLabelSortUf());
        panelLocaisDeEntrega.addOrReplace(newLabelSortQuantidade());

        dataViewLocaisEntrega.setCurrentPage(paginaAtual);

        target.add(panelLocaisDeEntrega);
    }

    public void mudarOrdemTabela() {
        if (order == 1) {
            order = -1;
            classArrow = new AttributeAppender("class", "mj_arrow_desc", " ");
        } else {
            order = 1;
            classArrow = new AttributeAppender("class", "mj_arrow_asc", " ");
        }
    }

    private void montarEnderecosEntrega(List<AgrupamentoDto> listaEnderecos) {

        listaComTodosEnderecos = new ArrayList<EnderecosOrdemFornecimentoDto>();

        for (AgrupamentoDto agrup : listaEnderecos) {
            for (BemUfLicitacaoDto bemuf : agrup.getBensUfs()) {

                List<EnderecosOrdemFornecimentoDto> listaTemporariaDeEnderecos = new ArrayList<EnderecosOrdemFornecimentoDto>();
                for (EnderecoDto ende : bemuf.getEndereco()) {

                    EnderecosOrdemFornecimentoDto endereco = new EnderecosOrdemFornecimentoDto();
                    endereco.setBem(bemuf.getBem());
                    endereco.setUf(bemuf.getUf());
                    endereco.setQuantidade(ende.getQuantidade());
                    endereco.setEndereco(ende.getLocalEntregaEntidade().getEnderecoCompleto());
                    endereco.setLocalEntregaEntidade(ende.getLocalEntregaEntidade());
                    endereco.setQuatidadeRestante(buscarQuantidadeRestanteDeItens(ende, bemuf, agrup));
                    verificarQuantidadeDeItensSelecionadosNestaUf(bemuf, ende, endereco);

                    // Se não existir mais quantidade restante para este item
                    // então esta linha não será mostrada com exceção de esta
                    // ordem de fornecimento
                    // conter o próprio bem na lista.
                    if (endereco.getQuatidadeRestante() == 0) {
                        if (endereco.getQuantidadeDaOf() != null) {
                            listaTemporariaDeEnderecos.add(endereco);
                        }
                        continue;
                    }
                    listaTemporariaDeEnderecos.add(endereco);
                }

                listaComTodosEnderecos.addAll(listaTemporariaDeEnderecos);
            }
        }

        // Ordena os endereços pelo bem
        // Collections.sort(listaComTodosEnderecos,EnderecosOrdemFornecimentoDto.getComparator(0,"item"));
    }

    private void verificarQuantidadeDeItensSelecionadosNestaUf(BemUfLicitacaoDto bemuf, EnderecoDto ende, EnderecosOrdemFornecimentoDto endereco) {
        for (ItensOrdemFornecimentoContrato buscar : listaDosItensDestaOf) {
            if (buscar.getItem().getId().intValue() == bemuf.getBem().getId().intValue() && buscar.getLocalEntrega().getId().intValue() == ende.getLocalEntregaEntidade().getId().intValue()) {

                endereco.setQuantidadeDaOf(buscar.getQuantidade());
                endereco.setQuantidadeAnterior(buscar.getQuantidade());
                endereco.setSelecionado(true);
                endereco.setItemOrdemFornecimento(buscar);
                break;
            }
        }
    }

    private Integer buscarQuantidadeRestanteDeItens(EnderecoDto local, BemUfLicitacaoDto bemuf, AgrupamentoDto agrup) {

        Integer restante = local.getQuantidade().intValue();
        for (ItensOrdemFornecimentoContrato item : listaDeTodosItensJaEmUso) {
            if (local.getLocalEntregaEntidade().getId().intValue() == item.getLocalEntrega().getId().intValue() && bemuf.getBem().getId().intValue() == item.getItem().getId().intValue()) {
                restante -= item.getQuantidade();
            }
        }
        return restante;
    }

    private void actionSalvar(AjaxRequestTarget target) {

        boolean ordemNova = ordemFornecimentoSelecionada != null && ordemFornecimentoSelecionada.getId() != null?false:true;
        if (!validarCampos()) {
            target.focusComponent(link);
            return;
        }
        
        ordemFornecimentoSelecionada.setStatusOrdemFornecimento(EnumStatusOrdemFornecimento.NAO_COMUNICADA);
        ordemFornecimentoSelecionada.setStatusComunicacaoOrdemFornecimento(EnumStatusComunicacaoOrdemFornecimento.NAO_COMUNICADA);
        ordemFornecimentoContratoService.incluirAlterar(ordemFornecimentoSelecionada, getNomeCompleto());

        if(ordemNova){
            getSession().info("Ordem de fornecimento criada com sucesso.");
        }else{
            getSession().info("Ordem de fornecimento alterada com sucesso.");
        }
        
        setResponsePage(new OrdemFornecimentoPage(getPageParameters(), ordemFornecimentoSelecionada, contratoSelecionado, backPage));
    }

    private void actionVoltar(AjaxRequestTarget target) {
        setResponsePage(new OrdemFornecimentoPesquisaPage(new PageParameters(), programa, backPage, ordemFornecimentoSelecionada.getContrato()));
    }

    private void montarOrdemFornecimento() {
        List<ItensOrdemFornecimentoContrato> listaDeOF = new ArrayList<ItensOrdemFornecimentoContrato>();
        for (EnderecosOrdemFornecimentoDto end : listaComTodosEnderecos) {
            if (end.getSelecionado() != null && end.getSelecionado()) {

                // Se este item já estiver sido salvo antes, e continar na lista
                // dos selecionados então este item será adicionado
                // Diretamente a lista de Of's que serão salvas agora, o else
                // será chamado caso este item seja novo.
                if (end.getItemOrdemFornecimento() != null) {
                    end.getItemOrdemFornecimento().setQuantidade(end.getQuantidadeDaOf());
                    listaDeOF.add(end.getItemOrdemFornecimento());
                } else {
                    ItensOrdemFornecimentoContrato of = new ItensOrdemFornecimentoContrato();
                    of.setItem(end.getBem());
                    of.setLocalEntrega(end.getLocalEntregaEntidade());
                    of.setQuantidade(end.getQuantidadeDaOf());

                    listaDeOF.add(of);
                }
            }
        }

        if (ordemFornecimentoSelecionada != null) {
            ordemFornecimentoSelecionada.setListaItensOrdemFornecimento(listaDeOF);
            ordemFornecimentoSelecionada.setDataAlteracao(LocalDateTime.now());
            ordemFornecimentoSelecionada.setUsuarioAlteracao(getNomeCompleto());
        } else {

            ordemFornecimentoSelecionada.setContrato(contratoSelecionado);
            ordemFornecimentoSelecionada.setDataCadastro(LocalDateTime.now());
            ordemFornecimentoSelecionada.setDataAlteracao(LocalDateTime.now());
            ordemFornecimentoSelecionada.setListaItensOrdemFornecimento(listaDeOF);
            ordemFornecimentoSelecionada.setUsuarioAlteracao(getNomeCompleto());
            ordemFornecimentoSelecionada.setUsuarioCadastro(getNomeCompleto());
        }
    }

    private TextField<Integer> acaoQuantidadeDaOf(TextField<Integer> textQuantidade, Item<EnderecosOrdemFornecimentoDto> item) {
        textQuantidade.add(new AjaxFormComponentUpdatingBehavior("onkeyup") {
            private static final long serialVersionUID = 1L;

            protected void onUpdate(AjaxRequestTarget target) {
                desabilitarFeedBackListener();
                // Setar no model
            }
        });
        return textQuantidade;
    }

    private boolean validarCampos() {

        boolean valido;
        valido = verificarSeTodasQuantidadesForamPreenchidas();
        
        //Irá montar a lista com os itens desta OF para persistir
        //e depois verificar se algum item foi selecionado para esta O.F.
        if(valido){
            montarOrdemFornecimento();
            valido = verificarSeEstaTentandoSalvarAOfSemSelecionarNenhumItem(valido);
        }
        
        return valido;
    }
    
    private boolean verificarSeEstaTentandoSalvarAOfSemSelecionarNenhumItem(boolean valido){
        
        if(ordemFornecimentoSelecionada.getListaItensOrdemFornecimento() != null && ordemFornecimentoSelecionada.getListaItensOrdemFornecimento().isEmpty()){
            addMsgError("Selecione ao menos 1 item para esta Ordem de Fornecimento.");
            valido = false;
        }
        
        return valido;
    }

    private boolean verificarSeTodasQuantidadesForamPreenchidas() {

        int quantidadeNaoPreenchida = 0;
        int quantidadeDigitadaMaiorQueARestante = 0;
        for (EnderecosOrdemFornecimentoDto end : listaComTodosEnderecos) {
            if (end.getSelecionado() != null && end.getSelecionado() && (end.getQuantidadeDaOf() == null || end.getQuantidadeDaOf() == 0)) {
                quantidadeNaoPreenchida++;
            }

            if (end.getQuantidadeDaOf() != null && end.getSelecionado() != null && end.getSelecionado()) {
                int qtdRestanteTotalMenosOfInformada = end.getQuantidade().intValue() - end.getQuantidadeDaOf().intValue();

                if (qtdRestanteTotalMenosOfInformada >= 0) {

                    if (end.getQuantidadeDaOf().intValue() > end.getQuatidadeRestante().intValue()) {

                        if (end.getQuantidadeDaOf().intValue() > end.getQuantidadeAnterior().intValue()) {

                            int quantidadeOfMenosAnterior = end.getQuantidadeDaOf() - end.getQuantidadeAnterior();
                            int quantidadeRestanteMenosAInformada = end.getQuatidadeRestante().intValue() - quantidadeOfMenosAnterior;

                            if (quantidadeRestanteMenosAInformada < 0) {
                                quantidadeDigitadaMaiorQueARestante++;
                            }
                        }
                    }
                } else {
                    quantidadeDigitadaMaiorQueARestante++;
                }
            }
        }

        boolean valido = true;
        if (quantidadeDigitadaMaiorQueARestante > 0) {
            addMsgError("Existe(m) " + quantidadeDigitadaMaiorQueARestante + " item(ns) com a 'Quantidade da OF' maior do que a 'Quantidade Restante'.");
            valido = false;
        }

        if (quantidadeNaoPreenchida > 0) {
            addMsgError("Existe(m) " + quantidadeNaoPreenchida + " item(ns) selecionado(s) com a 'Quantidade da OF' não informada(s).");
            valido = false;
        }
        return valido;
    }

    private void acaoCheckAdicionarTodosbens() {
        for (EnderecosOrdemFornecimentoDto enderecos : listaComTodosEnderecos) {
            if (checkTodosBensSelecionados) {
                enderecos.setSelecionado(true);
            } else {
                enderecos.setSelecionado(null);
            }
        }

    }

    /**
     * Desabilita FEEDBACK_LISTENER para não remover mensagens do feedbackPanel
     */
    private void desabilitarFeedBackListener() {
        RequestCycle.get().setMetaData(FeedbackPanelListener.SKIP_FEEDBACK_LISTENER, Boolean.TRUE);
    }

    public Integer getItensPorPagina() {
        return itensPorPagina;
    }

    public void setItensPorPagina(Integer itensPorPagina) {
        this.itensPorPagina = itensPorPagina;
    }

}
