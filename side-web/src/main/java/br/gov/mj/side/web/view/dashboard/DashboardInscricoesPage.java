package br.gov.mj.side.web.view.dashboard;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import br.gov.mj.infra.negocio.persistencia.EnumOrder;
import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.pagination.InfraAjaxPagingNavigator;
import br.gov.mj.infra.wicket.model.LambdaModel;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.entidade.PessoaEntidade;
import br.gov.mj.side.entidades.enums.EnumStatusInscricao;
import br.gov.mj.side.entidades.enums.EnumStatusPrograma;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoPrograma;
import br.gov.mj.side.web.service.InscricaoProgramaService;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.view.DashboardPanel;
import br.gov.mj.side.web.view.HomePage;
import br.gov.mj.side.web.view.programa.inscricao.ConfirmarInscricaoProgramaPage;
import br.gov.mj.side.web.view.programa.inscricao.InscricaoProgramaPage;
import br.gov.mj.side.web.view.template.TemplatePage;

public class DashboardInscricoesPage extends TemplatePage {
    private static final long serialVersionUID = 1L;

    private PanelDataviewResultados panelDataviewResultados;
    private DashboardPanel dashboardPessoasPanel;

    private Form<DashboardInscricoesPage> form;
    private DataView<InscricaoPrograma> dataView;
    private Integer itensPorPagina = Constants.ITEMS_PER_PAGE_PAGINATION;

    private Entidade entidadeLogada = new Entidade();

    @Inject
    private InscricaoProgramaService inscricaoService;

    @Inject
    private ComponentFactory componentFactory;

    private List<InscricaoPrograma> listaDeInscricoes = new ArrayList<InscricaoPrograma>();

    public DashboardInscricoesPage(final PageParameters pageParameters) {
        super(pageParameters);

        initVariaveis();
        initComponentes();

        setTitulo(entidadeLogada.getNomeEntidade());
    }

    private void initVariaveis() {

        entidadeLogada = (Entidade) getSessionAttribute("entidade");
        InscricaoPrograma buscar = new InscricaoPrograma();
        PessoaEntidade pe = new PessoaEntidade();
        pe.setEntidade(entidadeLogada);
        buscar.setPessoaEntidade(pe);
        listaDeInscricoes = inscricaoService.buscarSemPaginacao(buscar);
    }

    private void initComponentes() {
        form = new Form<DashboardInscricoesPage>("form", new CompoundPropertyModel<DashboardInscricoesPage>(this));
        add(form);

        dashboardPessoasPanel = new DashboardPanel("dashboardPessoasPanel");
        authorize(dashboardPessoasPanel, RENDER, HomePage.ROLE_MANTER_INSCRICAO_VISUALIZAR);
        form.add(dashboardPessoasPanel);

        panelDataviewResultados = new PanelDataviewResultados("panelDataviewResultados");

        form.add(panelDataviewResultados);

    }

    private class PanelDataviewResultados extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelDataviewResultados(String id) {
            super(id);
            setOutputMarkupId(true);
            InscricoesProvider ip = new InscricoesProvider();

            add(getDataViewInscricoes(ip)); // dataInscricoes

            add(getDropItensPorPagina()); // itensPorPagina

            add(new OrderByBorder<String>("orderByCodigo", "programa.identificadorProgramaPublicado", ip));
            add(new OrderByBorder<String>("orderByNomePrograma", "programa.nomePrograma", ip));
            add(new OrderByBorder<String>("orderByDataInscricao", "dataCadastro", ip));
            add(new OrderByBorder<String>("orderBySituacao", "statusInscricao", ip));
            add(new OrderByBorder<String>("orderBySituacaoPriograma", "programa.statusPrograma", ip));
            add(new InfraAjaxPagingNavigator("pagination", dataView));
        }
    }

    private Button getButtonEditarInscricao(Item<InscricaoPrograma> item) {
        Button btnDetalhes = componentFactory.newButton("btnDetalhesInscricao", () -> irPaginaEditarInscricao(item));
        btnDetalhes.setVisible(mostrarBotaoEditarInscricao(item));
        return btnDetalhes;
    }

    private Button getButtonVisualizarInscricao(Item<InscricaoPrograma> item) {
        Button btnDetalhes = componentFactory.newButton("btnVisualizarInscricao", () -> irPaginaVisualizarInscricao(item));
        return btnDetalhes;
    }

    // Somente irá aparecer se estiver em analise
    private Button getButtonAnalise(Item<InscricaoPrograma> item) {
        Button btnDetalhes = componentFactory.newButton("btnAnalise", () -> irPaginaEditarInscricao(item));
        boolean mostrarBotao = inscricaoEmAnalise(item.getModelObject());
        btnDetalhes.setVisible(mostrarBotao);
        return btnDetalhes;
    }

    // Componentes

    private DataView<InscricaoPrograma> getDataViewInscricoes(InscricoesProvider ip) {
        dataView = new DataView<InscricaoPrograma>("dataInscricoes", ip) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<InscricaoPrograma> item) {
                item.add(new Label("programa.identificadorProgramaPublicado", item.getModelObject().getPrograma().getCodigoIdentificadorProgramaPublicado()));
                item.add(new Label("programa.nomePrograma"));
                item.add(new Label("dataCadastro", dataCadastroBR(item.getModelObject().getDataCadastro())));
                item.add(new Label("situacaoPrograma", item.getModelObject().getPrograma().getStatusPrograma().getDescricao()));
                item.add(new Label("situacao", item.getModelObject().getStatusInscricao().getDescricao()));

                item.add(getButtonEditarInscricao(item)); // btnDetalhesInscricao
                item.add(getButtonVisualizarInscricao(item)); // btnVisualizarInscricao

                // Por enquanto deixar este botão oculto, mostrar quando as
                // telas de analise estiverem prontas.
                // item.add(getButtonAnalise(item)); //btnAnalise
            }

        };
        dataView.setItemsPerPage(Constants.ITEMS_PER_PAGE_PAGINATION);
        return dataView;
    }

    private DropDownChoice<Integer> getDropItensPorPagina() {
        DropDownChoice<Integer> dropDownChoice = new DropDownChoice<Integer>("itensPorPagina", new LambdaModel<Integer>(this::getItensPorPagina, this::setItensPorPagina), Constants.QUANTIDADE_ITENS_TABELA);
        dropDownChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                dataView.setItemsPerPage(getItensPorPagina());
                target.add(panelDataviewResultados);
            };
        });
        return dropDownChoice;
    }

    // PROVIDER

    private class InscricoesProvider extends SortableDataProvider<InscricaoPrograma, String> {
        private static final long serialVersionUID = 1L;

        public InscricoesProvider() {
            setSort("programa.nomePrograma", SortOrder.ASCENDING);
        }

        @Override
        public Iterator<InscricaoPrograma> iterator(long first, long size) {

            InscricaoPrograma inscricao=new InscricaoPrograma();
            PessoaEntidade entidadePessoa=new PessoaEntidade();
            entidadePessoa.setEntidade(entidadeLogada);
            inscricao.setPessoaEntidade(entidadePessoa);
            return inscricaoService.buscarPaginado(inscricao, (int) first, (int) size, getSort().isAscending() ? EnumOrder.ASC : EnumOrder.DESC, getSort().getProperty()).iterator();

        }

        @Override
        public long size() {
            InscricaoPrograma inscricao=new InscricaoPrograma();
            PessoaEntidade entidadePessoa=new PessoaEntidade();
            entidadePessoa.setEntidade(entidadeLogada);
            inscricao.setPessoaEntidade(entidadePessoa);
            return inscricaoService.contarPaginado(inscricao);
        }

        @Override
        public IModel<InscricaoPrograma> model(InscricaoPrograma object) {
            return new CompoundPropertyModel<InscricaoPrograma>(object);
        }
    }

    // AÇÕES

    private void irPaginaEditarInscricao(Item<InscricaoPrograma> item) {
        setResponsePage(new InscricaoProgramaPage(new PageParameters(), item.getModelObject(), this,false));
    }

    private void irPaginaVisualizarInscricao(Item<InscricaoPrograma> item) {
        setResponsePage(new InscricaoProgramaPage(new PageParameters(), item.getModelObject(), this,true));
    }

    // Irá mostrar este botão somente se estiver em analise
    private boolean inscricaoEmAnalise(InscricaoPrograma inscricao) {
        return inscricao.getStatusInscricao() == EnumStatusInscricao.ANALISE_AVALIACAO || inscricao.getStatusInscricao() == EnumStatusInscricao.ANALISE_ELEGIBILIDADE;
    }

    public String dataCadastroBR(LocalDateTime dataCadastro) {
        DateTimeFormatter sdfPadraoBR = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        if (dataCadastro != null) {
            return sdfPadraoBR.format(dataCadastro);
        }
        return " - ";
    }
    
    private boolean mostrarBotaoEditarInscricao(Item<InscricaoPrograma> item)
    {
        boolean visivel=true;
        
        EnumStatusPrograma statusPrograma=item.getModelObject().getPrograma().getStatusPrograma();
        EnumStatusInscricao statusInscricao=item.getModelObject().getStatusInscricao();
        if(statusPrograma!=EnumStatusPrograma.ABERTO_REC_PROPOSTAS)
        {
            visivel = false;
        }
        else
        {
            if(statusInscricao != EnumStatusInscricao.EM_ELABORACAO)
            {
                visivel = false;
            }
        }
        return visivel;
    }

    public Integer getItensPorPagina() {
        return itensPorPagina;
    }

    public void setItensPorPagina(Integer itensPorPagina) {
        this.itensPorPagina = itensPorPagina;
    }

    public List<InscricaoPrograma> getListaDeInscricoes() {
        return listaDeInscricoes;
    }

    public void setListaDeInscricoes(List<InscricaoPrograma> listaDeInscricoes) {
        this.listaDeInscricoes = listaDeInscricoes;
    }

}
