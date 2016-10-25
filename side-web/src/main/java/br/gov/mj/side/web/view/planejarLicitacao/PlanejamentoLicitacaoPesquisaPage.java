package br.gov.mj.side.web.view.planejarLicitacao;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import br.gov.mj.apoio.entidades.Regiao;
import br.gov.mj.apoio.entidades.Uf;
import br.gov.mj.infra.negocio.persistencia.EnumOrder;
import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraAjaxConfirmButton;
import br.gov.mj.infra.wicket.component.InfraAjaxFallbackLink;
import br.gov.mj.infra.wicket.component.InfraDropDownChoice;
import br.gov.mj.infra.wicket.component.InfraLocalDateTextField;
import br.gov.mj.infra.wicket.component.pagination.InfraAjaxPagingNavigator;
import br.gov.mj.infra.wicket.model.LambdaModel;
import br.gov.mj.side.entidades.Bem;
import br.gov.mj.side.entidades.enums.EnumStatusPrograma;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.inscricao.analise.ListaAvaliacaoPublicado;
import br.gov.mj.side.entidades.programa.licitacao.AgrupamentoLicitacao;
import br.gov.mj.side.entidades.programa.licitacao.BemUf;
import br.gov.mj.side.entidades.programa.licitacao.LicitacaoPrograma;
import br.gov.mj.side.entidades.programa.licitacao.SelecaoItem;
import br.gov.mj.side.web.dto.PesquisaLicitacaoDto;
import br.gov.mj.side.web.service.BemService;
import br.gov.mj.side.web.service.InscricaoProgramaService;
import br.gov.mj.side.web.service.LicitacaoProgramaService;
import br.gov.mj.side.web.service.ListaPublicadoService;
import br.gov.mj.side.web.service.ProgramaService;
import br.gov.mj.side.web.service.RegiaoService;
import br.gov.mj.side.web.service.UfService;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.DataUtil;
import br.gov.mj.side.web.util.SideUtil;
import br.gov.mj.side.web.view.HomePage;
import br.gov.mj.side.web.view.components.AjaxDialogButton;
import br.gov.mj.side.web.view.planejarLicitacao.minutaTR.GerarMinutaTR;
import br.gov.mj.side.web.view.template.TemplatePage;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal.Backdrop;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.TextContentModal;


@AuthorizeInstantiation({ 
    PlanejamentoLicitacaoPesquisaPage.ROLE_MANTER_PLANEJAR_LICITACAO_VISUALIZAR,
    PlanejamentoLicitacaoPesquisaPage.ROLE_MANTER_PLANEJAR_LICITACAO_INCLUIR,
    PlanejamentoLicitacaoPesquisaPage.ROLE_MANTER_PLANEJAR_LICITACAO_ALTERAR,
    PlanejamentoLicitacaoPesquisaPage.ROLE_MANTER_PLANEJAR_LICITACAO_EXCLUIR,
    PlanejamentoLicitacaoPesquisaPage.ROLE_MANTER_PLANEJAR_LICITACAO_GERAR_MINUTA
})

public class PlanejamentoLicitacaoPesquisaPage extends TemplatePage {
    private static final long serialVersionUID = 1L;
    
  //constantes de permição de acesso
    public static final String ROLE_MANTER_PLANEJAR_LICITACAO_VISUALIZAR = "manter_planejar_licitacao:visualizar";
    public static final String ROLE_MANTER_PLANEJAR_LICITACAO_INCLUIR = "manter_planejar_licitacao:incluir";
    public static final String ROLE_MANTER_PLANEJAR_LICITACAO_ALTERAR = "manter_planejar_licitacao:alterar";
    public static final String ROLE_MANTER_PLANEJAR_LICITACAO_EXCLUIR = "manter_planejar_licitacao:excluir";
    public static final String ROLE_MANTER_PLANEJAR_LICITACAO_GERAR_MINUTA = "manter_planejar_licitacao:gerar_minuta";
    
    private DataView<LicitacaoPrograma> dataViewLicitacao;
    
    private PanelPrincipal panelPrincipal;
    private PanelEstado panelEstado;
    private PanelResultado panelResultado;
    private ProviderLicitacao provider;
    
    private Integer itensPorPagina = 5;
    
    private List<Uf> listaUf = new ArrayList<Uf>();
    private List<LicitacaoPrograma> listaDeLicitacoes = new ArrayList<LicitacaoPrograma>();
    private List<Programa> listaProgramas = new ArrayList<Programa>();
    private Regiao regiao = new Regiao();
    private Modal<String> modalConfirmar;
    private String msgConfirm;
    private LicitacaoPrograma licitacaoEscolhidaGerarMinuta;
    
    private boolean resultadoVisivel = false;
    
    private Form<PesquisaLicitacaoDto> form;

    @Inject
    private ComponentFactory componentFactory;
    
    @Inject
    private BemService bemService;
    
    @Inject
    private RegiaoService regiaoService;
    
    @Inject
    private UfService ufService;
    
    @Inject
    private LicitacaoProgramaService licitacaoService;
    
    @Inject
    private ProgramaService programaService;
    
    @Inject
    private ListaPublicadoService listaPublicadoService;
    
    @Inject
    private InscricaoProgramaService inscricaoService;
    
    private List<String> listaTemporaria = new ArrayList<String>();
 
    public PlanejamentoLicitacaoPesquisaPage(final PageParameters pageParameters) {
        super(pageParameters);
        
        setTitulo("Pesquisar Planejamento de Licitação");
        initVariaveis();
        initComponents();
        criarBreadcrump();
    }
    
    private void initVariaveis(){
        
        listaDeProgramasElegiveis();
    }
    

    private void initComponents() {
        form =new Form<PesquisaLicitacaoDto>("form",new CompoundPropertyModel<PesquisaLicitacaoDto>(new PesquisaLicitacaoDto()));
        add(form);
        
        form.add(panelPrincipal = new PanelPrincipal("panelPrincipal"));
        form.add(panelResultado = new PanelResultado("panelResultado"));
        panelResultado.setVisible(false);
        
        form.add(newButtonNovo()); //btnNovo
        form.add(newButtonPesquisar()); //btnPesquisar
        
        modalConfirmar = newModalConfirmar("modalConfirmar");
        modalConfirmar.show(false);

        form.add(modalConfirmar);
    }
    
    private void criarBreadcrump() {
        Link link = new Link("homePage") {
            @Override
            public void onClick() {
                setResponsePage(HomePage.class);
            }
        };
        form.add(link);        
    }
    
    /*
     * OS PAINEIS SERÃO IMPLEMENTADOS ABAIXO
     */
    
    private class PanelPrincipal extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelPrincipal(String id) {
            super(id);
            setOutputMarkupId(true);

            add(newDropDownPrograma()); //dropPrograma
            add(newDateTextFieldPrazoExecucao()); //prazoExecucao
            add(newDropDownBem()); //item
            add(newDropDownRegiao()); //regiao
            add(panelEstado = new PanelEstado("panelEstado"));
        }
    }
    
    private class PanelEstado extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelEstado(String id) {
            super(id);
            setOutputMarkupId(true);
            add(newDropDownEstado()); //Uf
        }
    }
    
    private class PanelResultado extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelResultado(String id) {
            super(id);
            setOutputMarkupId(true);
            
            provider = new ProviderLicitacao();
            
            add(newDataViewLicitacao(provider)); //dataResultado
            add(newDropItensPorPagina()); //itensPorPagina
            add(newPaginatorLicitacao()); //paginationLicitacao
            
            add(new OrderByBorder<String>("orderByCodigo", "programa.identificadorProgramaPublicado", provider));
            add(new OrderByBorder<String>("orderByNome", "programa.nomePrograma", provider));
            add(new OrderByBorder<String>("orderByPrazo", "dataFinalPeriodoExecucao", provider));
        }
    }
    
    
    
    /*
     * OS COMPONENTES SERÃO IMPLEMENTADOS ABAIXO
     */
    
    private DropDownChoice<Integer> newDropItensPorPagina() {
        DropDownChoice<Integer> dropDownChoice = new DropDownChoice<Integer>("itensPorPagina", new LambdaModel<Integer>(this::getItensPorPagina, this::setItensPorPagina), Constants.QUANTIDADE_ITENS_TABELA);
        dropDownChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                dataViewLicitacao.setItemsPerPage(getItensPorPagina());
                target.add(panelResultado);
            };
        });
        return dropDownChoice;
    }
    
    public InfraAjaxPagingNavigator newPaginatorLicitacao() {
        return new InfraAjaxPagingNavigator("pagination", dataViewLicitacao);
    }
    
    private DropDownChoice<Programa> newDropDownPrograma() {
        DropDownChoice<Programa> dropPrograma = new DropDownChoice<Programa>("dropPrograma", new PropertyModel<Programa>(form.getModelObject(), "programa"), listaProgramas, new ChoiceRenderer<Programa>("codigoIdentificadorProgramaPublicadoENomePrograma"));
        dropPrograma.setOutputMarkupId(true);
        dropPrograma.setNullValid(true);
        return dropPrograma;
    }  
    
    private InfraLocalDateTextField newDateTextFieldPrazoExecucao() {
        InfraLocalDateTextField field = componentFactory.newDateTextFieldWithDatePicker("prazoExecucao", "Prazo Execução", false, null, "dd/MM/yyyy", "pt-BR");
        field.setOutputMarkupId(true);
        return field;
    }
    
    private InfraDropDownChoice<Bem> newDropDownBem() {
        List<Bem> listaBem = bemService.buscar(new Bem());
        InfraDropDownChoice<Bem> dropDownChoice = componentFactory.newDropDownChoice("item", "Item", false, "id", "nomeBem", null, listaBem, null);
        dropDownChoice.setNullValid(true);
        return dropDownChoice;
    }
    
    private InfraDropDownChoice<Regiao> newDropDownRegiao() {
        
        List<Regiao> listaRegiao = regiaoService.buscarTodos();
        InfraDropDownChoice<Regiao> dropRegiao = componentFactory.newDropDownChoice("regiao", "Região", false, "id", "nomeRegiao", new PropertyModel<Regiao>(this,"regiao"), listaRegiao, null);
        dropRegiao.setNullValid(true);
        dropRegiao.setOutputMarkupId(true);
        actionDropDownRegiao(dropRegiao);
        return dropRegiao;
    }
    
    private InfraDropDownChoice<Uf> newDropDownEstado() {
        InfraDropDownChoice<Uf> dropDownEstado = componentFactory.newDropDownChoice("UfPesquisa", "Estado", false, "id", "nomeUf", null, listaUf, null);
        dropDownEstado.setNullValid(true);
        dropDownEstado.setOutputMarkupId(true);
        return dropDownEstado;
    }
    
    
    public InfraAjaxFallbackLink<Void> newButtonNovo() {
        InfraAjaxFallbackLink btnNovo =  componentFactory.newAjaxFallbackLink("btnNovo", (target) -> adicionarNovo());
        authorize(btnNovo, RENDER, ROLE_MANTER_PLANEJAR_LICITACAO_INCLUIR);
        
        return btnNovo;
    }
    
    private Button newButtonEditar(Item<LicitacaoPrograma> item,boolean minutaGerada){
        Button btnAlterar = componentFactory.newButton("btnAlterar", () -> acaoEditar(item));
        btnAlterar.setOutputMarkupId(true);
        btnAlterar.setVisible(true);
        authorize(btnAlterar, RENDER, ROLE_MANTER_PLANEJAR_LICITACAO_ALTERAR);
        return btnAlterar;
    }
    
    private Button newButtonVisulizar(Item<LicitacaoPrograma> item){
        Button btnVisualizar = componentFactory.newButton("btnVisualizar", () -> acaoVisualizar(item));
        btnVisualizar.setOutputMarkupId(true);
        authorize(btnVisualizar, RENDER, ROLE_MANTER_PLANEJAR_LICITACAO_VISUALIZAR);
        return btnVisualizar;
    }
    
    private Button newButtonPesquisar() {
        Button btnPesquisar = componentFactory.newButton("btnPesquisar", () -> pesquisar());
        authorize(btnPesquisar, RENDER, ROLE_MANTER_PLANEJAR_LICITACAO_VISUALIZAR);
        return btnPesquisar;
    }
    
    private InfraAjaxConfirmButton newButtonExcluirPlanejamento(Item<LicitacaoPrograma> item,boolean minutaGerada){
        InfraAjaxConfirmButton btnExcluir =  componentFactory.newAJaxConfirmButton("btnExcluir", "MN048", form, (target, formz) -> excluir(target, item));
        authorize(btnExcluir, RENDER, ROLE_MANTER_PLANEJAR_LICITACAO_EXCLUIR);
        btnExcluir.setVisible(!minutaGerada);
        return btnExcluir;
    }
    
    //Será incluso na próxima Sprint
     
    private DataView<LicitacaoPrograma> newDataViewLicitacao(ProviderLicitacao provider) {
        dataViewLicitacao = new DataView<LicitacaoPrograma>("dataResultado", provider) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<LicitacaoPrograma> item) {

                String grupoItem = verificarGrupoItem(item);
                String estado = verificarEstados(item);  
                
                boolean minutaGerada = item.getModelObject().getMinutaGerada() != null;
                
                item.add(new Label("resultCodigoPrograma",item.getModelObject().getPrograma().getCodigoIdentificadorProgramaPublicado()));
                item.add(new Label("resultNomePrograma",item.getModelObject().getPrograma().getNomePrograma()));
                item.add(new Label("resultGrupoItem",grupoItem));
                item.add(new Label("resultEstado",estado));
                item.add(new Label("resultPrazo",DataUtil.converteDataDeLocalDateParaString(item.getModelObject().getDataFinalPeriodoExecucao(),"dd/MM/yyyy")));
                item.add(newButtonEditar(item,minutaGerada)); //btnAlterar
                item.add(newButtonExcluirPlanejamento(item,minutaGerada));
                item.add(newButtonVisulizar(item)); //btnVisualizar
                item.add(newButtonGerarMinutaTr(item.getModelObject(),minutaGerada)); //btnGerarMinuta
                
                Button btnDownload = componentFactory.newButton("btnDonwload", () -> download(item));
                btnDownload.setVisible(minutaGerada);
                item.add(btnDownload);
            }
        };
        dataViewLicitacao.setItemsPerPage(Constants.ITEMS_PER_PAGE_PAGINATION);
        return dataViewLicitacao;
    }
    
    private Modal<String> newModalConfirmar(String id) {
        Modal<String> modal = new TextContentModal(id, new LambdaModel<String>(this::getMsgConfirm, this::setMsgConfirm));
        modal.setBackdrop(Backdrop.STATIC);
        modal.addButton(newButtonConfirmar(modal));
        modal.addButton(newButtonFecharModal(modal));
        return modal;
    }
    
    private AjaxFallbackButton newButtonGerarMinutaTr(LicitacaoPrograma licitacao, Boolean minutaGerada) {
        AjaxFallbackButton buttonAdicionar = componentFactory.newAjaxFallbackButton("btnGerarMinuta", null, (target, form) -> acaoGerarMinutaMostrarModal(target,licitacao));
        authorize(buttonAdicionar, RENDER, ROLE_MANTER_PLANEJAR_LICITACAO_GERAR_MINUTA);
        buttonAdicionar.setEnabled(true);
        buttonAdicionar.setVisible(true);
        return buttonAdicionar;
    }
    
    
    
    private void download(Item<LicitacaoPrograma> item) {
    	
        GerarMinutaTR jasper = new GerarMinutaTR(SideUtil.getCaminhoCompletoRelatorios(getHttpServletRequest().getServletContext()));
        jasper.setNomeMinuta("MinutaTR - "+item.getModelObject().getPrograma().getNomePrograma());
        ByteArrayOutputStream export = jasper.exportToByteArray(item.getModelObject(),programaService,licitacaoService,inscricaoService);
        item.getModelObject().setMinutaGerada(export.toByteArray());
        
        LicitacaoPrograma persistido = licitacaoService.atualizarInformacoesBasicas(item.getModelObject(),getIdentificador());
        
        LicitacaoPrograma a = item.getModelObject();
       
        SideUtil.download(a.getMinutaGerada(), a.getPrograma().getNomePrograma()+".odt");

    }
    
    private AjaxDialogButton newButtonConfirmar(Modal<String> modal) {
        return new AjaxDialogButton(Model.of("OK"), Buttons.Type.Primary) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                gerarMinuta(licitacaoEscolhidaGerarMinuta,target,modal);
            }
        };
    }
    
    private AjaxDialogButton newButtonFecharModal(Modal<String> modal) {
        return new AjaxDialogButton(Model.of("Cancelar"), Buttons.Type.Primary) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                modal.show(false);
                modal.close(target);
            }
        };
    }
    
    /*
     * OS PROVIDERS VIRÃO ABAIXO
     */
    
    private class ProviderLicitacao extends SortableDataProvider<LicitacaoPrograma, String> {
        private static final long serialVersionUID = 1L;

        
        public ProviderLicitacao() {
            setSort("programa.nomePrograma", SortOrder.ASCENDING);
        }

        @Override
        public Iterator<LicitacaoPrograma> iterator(long first, long size) {

            return licitacaoService.buscarPaginado(form.getModelObject(), (int)first, (int)size, getSort().isAscending() ? EnumOrder.ASC : EnumOrder.DESC, getSort().getProperty()).iterator();
        }

        @Override
        public long size() {
            return licitacaoService.buscarSemPaginacao(form.getModelObject()).size();
        }

        @Override
        public IModel<LicitacaoPrograma> model(LicitacaoPrograma object) {
            return new CompoundPropertyModel<LicitacaoPrograma>(object);
        }
    }
    
    
    /*
     * AS AÇÕES SERÃO IMPLEMENTADAS ABAIXO
     */
    
    private void excluir(AjaxRequestTarget target,Item<LicitacaoPrograma> item){
        licitacaoService.excluir(item.getModelObject().getId());
        
        panelResultado.addOrReplace(newDataViewLicitacao(provider));
        target.add(panelResultado);
    }
    
    private void listaDeProgramasElegiveis(){
        List<Programa> programa = new ArrayList<Programa>();
        programa = programaService.buscar(new Programa());
        
        for(Programa prog:programa){
            List<ListaAvaliacaoPublicado> listaAvaliacao = SideUtil.convertAnexoDtoToEntityListaAvaliacaoPublicado(listaPublicadoService.buscarListaAvaliacaoPublicadoPeloIdPrograma(prog.getId()));          
            if(listaAvaliacao.size()>1 && 
                    (prog.getStatusPrograma() == EnumStatusPrograma.ABERTO_PLANEJAMENTO_LICITACAO || prog.getStatusPrograma() == EnumStatusPrograma.ABERTO_GERACAO_CONTRATO)){
                listaProgramas.add(prog);
            }
        }
    }
    
    private String verificarGrupoItem(Item<LicitacaoPrograma> item){
        String grupoItem ="";
        
        List<AgrupamentoLicitacao> listaLicitacao = licitacaoService.buscarAgrupamentoLicitacao(item.getModelObject());
        
        Collections.sort(listaLicitacao, AgrupamentoLicitacao.getComparator(1, "tipo"));
        for (AgrupamentoLicitacao agrup : listaLicitacao) {
            
            if(!grupoItem.equalsIgnoreCase("")){
                grupoItem = grupoItem+"/ ";
            }
            grupoItem += " " + agrup.getNomeAgrupamento()+" ";
        }
        
        return grupoItem;
    }
    
    private String verificarEstados(Item<LicitacaoPrograma> item) {

        String stringUf = new String("");
        List<Uf> reg = new ArrayList<Uf>();
        
        List<AgrupamentoLicitacao> listaLicitacao = licitacaoService.buscarAgrupamentoLicitacao(item.getModelObject());
        
        for (AgrupamentoLicitacao agrup : listaLicitacao) {
            
            List<SelecaoItem> listaItem = licitacaoService.buscarSelecaoItem(agrup);
            for(SelecaoItem si:listaItem){
                
                List<BemUf> listUf=new ArrayList<BemUf>();
                listUf = licitacaoService.buscarBemUf(si);
                for(BemUf bemuf:listUf){
                    if (!reg.contains(bemuf.getUf())) {
                        reg.add(bemuf.getUf());
                        
                        if(!stringUf.equalsIgnoreCase("")){
                            stringUf = stringUf+"/ ";
                        }
                        stringUf = stringUf + " " + bemuf.getUf().getSiglaUf() + "";
                    }
                }
            }
        }
        return stringUf;
    }
    
    private void acaoVisualizar(Item<LicitacaoPrograma> item){
        setResponsePage(new PlanejamentoLicitacaoPage(new PageParameters(), this, null,null,4));
    }
    
    private void acaoEditar(Item<LicitacaoPrograma> item){
        setResponsePage(new PlanejamentoLicitacaoPage(new PageParameters(), this, null,null,4));
    }
    
    public void actionDropDownRegiao(DropDownChoice dropRegiao) {
        dropRegiao.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                if(regiao == null){
                    listaUf = new ArrayList<Uf>();
                }else{
                    listaUf = ufService.buscarPorRegiao(regiao);
                }
                
                panelEstado.addOrReplace(newDropDownEstado());
                target.add(panelEstado);
            }
        });
    }
    
    private void mostrarModalConfirmar(AjaxRequestTarget target,LicitacaoPrograma licitacao){
        
        //Se a Minuta não tiver sido gerada então avisar
        if(licitacao.getMinutaGerada() == null){
            msgConfirm = "Ao gerar a Minuta do Termo de Referência não será mais possível alterar nem excluir o planejamento da Licitação, deseja continuar?";
            modalConfirmar.show(true);
            target.add(modalConfirmar);
            licitacaoEscolhidaGerarMinuta = licitacao;
        }else{
            gerarMinuta(licitacao,target,null);
        }
    }
    
    private void pesquisar()
    {
        panelResultado.setVisible(true);
    }
    
    private void adicionarNovo()
    {
        setResponsePage(new PlanejamentoLicitacaoPage(new PageParameters(), this, null,null,4));
    }
    
    private void acaoGerarMinutaMostrarModal(AjaxRequestTarget target, LicitacaoPrograma licitacao){
        mostrarModalConfirmar(target,licitacao);
    }
    
    private void gerarMinuta(LicitacaoPrograma licitacao,AjaxRequestTarget target, Modal<String> modal){
    	GerarMinutaTR jasper = new GerarMinutaTR(SideUtil.getCaminhoCompletoRelatorios(getHttpServletRequest().getServletContext()));
        jasper.setNomeMinuta("MinutaTR - "+licitacao.getPrograma().getNomePrograma());
        ByteArrayOutputStream export = jasper.exportToByteArray(licitacao,programaService,licitacaoService,inscricaoService);
        licitacao.setMinutaGerada(export.toByteArray());
        
        LicitacaoPrograma persistido = licitacaoService.atualizarInformacoesBasicas(licitacao,getIdentificador());
        Programa programaLicitacao = persistido.getPrograma();
        
        //Atualiza o Status do programa
        programaLicitacao.setStatusPrograma(EnumStatusPrograma.ABERTO_GERACAO_CONTRATO);
        programaService.atualizarInformacoesBasicasPrograma(programaLicitacao, getIdentificador());
        
        panelResultado.addOrReplace(newDataViewLicitacao(provider));
        target.add(panelResultado);
        
        if(modal!=null){
            modal.show(false);
            modal.close(target);
        }
    }

    public Integer getItensPorPagina() {
        return itensPorPagina;
    }

    public void setItensPorPagina(Integer itensPorPagina) {
        this.itensPorPagina = itensPorPagina;
    }

    public String getMsgConfirm() {
        return msgConfirm;
    }

    public void setMsgConfirm(String msgConfirm) {
        this.msgConfirm = msgConfirm;
    }

    
}
