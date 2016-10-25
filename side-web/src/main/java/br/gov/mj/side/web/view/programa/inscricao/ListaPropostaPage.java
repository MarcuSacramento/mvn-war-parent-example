package br.gov.mj.side.web.view.programa.inscricao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.request.handler.resource.ResourceRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.convert.IConverter;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.pagination.InfraAjaxPagingNavigator;
import br.gov.mj.infra.wicket.model.LambdaModel;
import br.gov.mj.side.entidades.enums.EnumAbaFaseAnalise;
import br.gov.mj.side.entidades.enums.EnumResultadoFinalAnaliseElegibilidade;
import br.gov.mj.side.entidades.enums.EnumStatusInscricao;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoPrograma;
import br.gov.mj.side.entidades.programa.inscricao.analise.ListaAvaliacaoPublicado;
import br.gov.mj.side.entidades.programa.inscricao.analise.ListaElegibilidadePublicado;
import br.gov.mj.side.web.dto.AnaliseDto;
import br.gov.mj.side.web.dto.PermissaoProgramaDto;
import br.gov.mj.side.web.service.InscricaoProgramaService;
import br.gov.mj.side.web.service.ListaPublicadoService;
import br.gov.mj.side.web.service.ProgramaService;
import br.gov.mj.side.web.service.PublicizacaoService;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.InscricaoListClassificacaoReportBuilder;
import br.gov.mj.side.web.util.InscricaoListReportBuilder;
import br.gov.mj.side.web.util.SideUtil;
import br.gov.mj.side.web.util.SortableInscricaoAvaliacaoDataProvider;
import br.gov.mj.side.web.util.SortableInscricaoElegibilidadeDataProvider;
import br.gov.mj.side.web.view.components.converters.CpfCnpjConverter;
import br.gov.mj.side.web.view.enums.EnumSelecao;
import br.gov.mj.side.web.view.programa.PanelFasePrograma;
import br.gov.mj.side.web.view.programa.visualizacao.ProgramaAnexosPublicadosPanel;
import br.gov.mj.side.web.view.propostas.PropostasEnviadasPage;
import br.gov.mj.side.web.view.template.TemplatePage;

public class ListaPropostaPage extends TemplatePage {
    private static final long serialVersionUID = 1L;

    private Form<ListaPropostaPage> form;
    private Programa programa;
    private PanelFasePrograma panelFasePrograma;

    @Inject
    private ComponentFactory componentFactory;

    @Inject
    private ProgramaService programaService;
    
    @Inject
    private ListaPublicadoService listaPublicadoService;

    @Inject
    private InscricaoProgramaService inscricaoProgramaService;
    
    @Inject
    private PublicizacaoService publicizacaoService;

    private EnumSelecao opcaoEscolhida;
    private EnumSelecao opcaoEnviada;
    private DataView<InscricaoPrograma> dataView;
    private GridListaElegibilidadePanel gridListaElegibilidadePanel;
    private GridListaAvaliacaoPanel gridListaAvaliacaoPanel;
    private ProgramaAnexosPublicadosPanel programaAnexosPublicadosPanel;
    private ButtonsPanel buttonsPanel;
    private List<ListaElegibilidadePublicado> listasPublicadasElegibilidade;
    private List<ListaAvaliacaoPublicado> listaAvaliacaoPublicados;
    private String subTitulo = "";
    private Label lblSubtitulo;
    private List<InscricaoPrograma> listaInscritos = new ArrayList<InscricaoPrograma>();
    private String botaoClicado;
    private Page backPage;

    public ListaPropostaPage(PageParameters pageParameters) {
        super(pageParameters);
        setTitulo("Lista de Propostas");

        String id = pageParameters.get("programa").toString();
        if (StringUtils.isBlank(id)) {
            getSession().error("Solicitação inválida.");
            setResponsePage(getApplication().getHomePage());
        }
        setPrograma(programaService.buscarPeloId(new Long(id)));
        listasPublicadasElegibilidade = SideUtil.convertAnexoDtoToEntityListaElegibilidadePublicado(listaPublicadoService.buscarListaElegibilidadePublicadoPeloIdPrograma(programa.getId()));
        listaAvaliacaoPublicados = SideUtil.convertAnexoDtoToEntityListaAvaliacaoPublicado(listaPublicadoService.buscarListaAvaliacaoPublicadoPeloIdPrograma(programa.getId()));

                
        initComponents();
    }

    public ListaPropostaPage(PageParameters pageParameters,EnumSelecao botaoClicado, Page backPage) {
        super(pageParameters);
        setTitulo("Lista de Propostas");
        
        this.opcaoEscolhida = botaoClicado;
        this.backPage = backPage;
        String id = pageParameters.get("programa").toString();
        if (StringUtils.isBlank(id)) {
            getSession().error("Solicitação inválida.");
            setResponsePage(getApplication().getHomePage());
        }
        setPrograma(programaService.buscarPeloId(new Long(id)));
        listasPublicadasElegibilidade = SideUtil.convertAnexoDtoToEntityListaElegibilidadePublicado(listaPublicadoService.buscarListaElegibilidadePublicadoPeloIdPrograma(programa.getId()));
        listaAvaliacaoPublicados = SideUtil.convertAnexoDtoToEntityListaAvaliacaoPublicado(listaPublicadoService.buscarListaAvaliacaoPublicadoPeloIdPrograma(programa.getId()));
        initComponents();
        
        
        if(opcaoEscolhida == EnumSelecao.PROPOSTAS_ELEGIVEIS){
            gridListaElegibilidadePanel.setVisible(true);
            gridListaAvaliacaoPanel.setVisible(false);
            buttonsPanel.setVisible(true);
            programaAnexosPublicadosPanel.setVisible(!isPublicacaoListaPreliminar());
            lblSubtitulo.setVisible(true);
        }else{
            gridListaElegibilidadePanel.setVisible(false);
            gridListaAvaliacaoPanel.setVisible(true);
            buttonsPanel.setVisible(true);
            programaAnexosPublicadosPanel.setVisible(!isPublicacaoListaPreliminar());
            lblSubtitulo.setVisible(true);
        }
    }

    
    private void initComponents() {
        form = componentFactory.newForm("form", this);
        form.add(panelFasePrograma = new PanelFasePrograma("panelFasePrograma",programa,backPage));
        
        form.add(newLabelTituloPagina()); //lblTituloPagina
        
        lblSubtitulo = new Label("lblSubtitulo",new LambdaModel<String>(this::getSubTitulo,this::setSubTitulo));
        form.add(lblSubtitulo);
 
        gridListaElegibilidadePanel = newGridListaElegibilidadePanel();
        form.add(gridListaElegibilidadePanel);

        gridListaAvaliacaoPanel = newGridListaAvaliacaoPanel();
        form.add(gridListaAvaliacaoPanel);

        programaAnexosPublicadosPanel = new ProgramaAnexosPublicadosPanel("programaAnexosPublicadosPanel", programa);
        form.add(programaAnexosPublicadosPanel);
        
        buttonsPanel = newButtonsPanel();
        form.add(buttonsPanel);
        form.add(newButtonVoltar());
        
        setup();
        add(form);
    }

    
    
    private void setup(){
        //esconderTodosPanels();
        definirSubTitulo();
        if (EnumSelecao.PROPOSTAS_ELEGIVEIS.equals(opcaoEscolhida)){
            if(isVisualizacaoListaDefinitiva(EnumSelecao.PROPOSTAS_ELEGIVEIS)){
                lblSubtitulo.setVisible(false);
                programaAnexosPublicadosPanel.setVisible(true);
            }else{
                gridListaElegibilidadePanel.setVisible(true);
                lblSubtitulo.setDefaultModelObject(getSubTitulo());
                lblSubtitulo.setVisible(true);
                atualizarButtonPublicar();
            }
        }
        if (EnumSelecao.CLASSIFICACAO_PROSPOSTA.equals(opcaoEscolhida)){
           if(isVisualizacaoListaDefinitiva(EnumSelecao.CLASSIFICACAO_PROSPOSTA)){
               programaAnexosPublicadosPanel.setVisible(true);
           }else{
               lblSubtitulo.setDefaultModelObject(getSubTitulo());
               lblSubtitulo.setVisible(true);
               gridListaAvaliacaoPanel.setVisible(true);
               atualizarButtonPublicar();
           }
        }
    }
    
    private void definirSubTitulo(){
        if(isPublicacaoListaPreliminar()){
            setSubTitulo("Lista de "+opcaoEscolhida.getDescricao()+" - Preliminar");
        }
        
        if(isPublicacaoListaDefinitiva()){
            setSubTitulo("Lista de "+opcaoEscolhida.getDescricao()+" - Definitiva");
        }
    }

    private void atualizarButtonPublicar() {
        buttonsPanel.addOrReplace(newButtonPublicarElegibilidade());
        buttonsPanel.addOrReplace(newButtonPublicarClassificacao());
        buttonsPanel.setVisible(true);
    }

    private void esconderTodosPanels() {
        gridListaElegibilidadePanel.setVisible(false);
        gridListaAvaliacaoPanel.setVisible(false);
        buttonsPanel.setVisible(false);
        programaAnexosPublicadosPanel.setVisible(false);
        lblSubtitulo.setVisible(false);
    }

    private ButtonsPanel newButtonsPanel() {
        return new ButtonsPanel("buttonsPanel");
    }
    
    private Label newLabelTituloPagina(){
        
        String textoLabel;
        if(opcaoEscolhida == opcaoEscolhida.CLASSIFICACAO_PROSPOSTA){
            textoLabel ="Propostas Classificadas";
        }else{
            textoLabel = opcaoEscolhida.getDescricao();
        }
        Label labelTitulo = new Label("lblTituloPagina","Lista de "+textoLabel);
        labelTitulo.setOutputMarkupId(true);
        return labelTitulo;
    }

    private GridListaElegibilidadePanel newGridListaElegibilidadePanel() {
        return new GridListaElegibilidadePanel("gridListaElegibilidadePanel");
    }

    private GridListaAvaliacaoPanel newGridListaAvaliacaoPanel() {
        return new GridListaAvaliacaoPanel("gridListaAvaliacaoPanel");
    }

    private List<EnumSelecao> getListEnumSelecao() {
        List<EnumSelecao> lista  = new ArrayList<EnumSelecao>();
        if(listasPublicadasElegibilidade.size() != 2){
            lista.add(EnumSelecao.PROPOSTAS_ELEGIVEIS);
        }else{
            lista = Arrays.asList(EnumSelecao.values());
        }
        return lista;
    }

    private void selecionarOpcao(AjaxRequestTarget target) {
        //setup();
        atualizarTela(target);
        
    }

    private void atualizarTela(AjaxRequestTarget target) {
        target.add(gridListaElegibilidadePanel,gridListaAvaliacaoPanel, buttonsPanel,programaAnexosPublicadosPanel,lblSubtitulo);
    }

    private DataView<InscricaoPrograma> newDataViewPropostasElegiveis() {
        return new DataView<InscricaoPrograma>("inscricoes", new SortableInscricaoElegibilidadeDataProvider(getPrograma(), inscricaoProgramaService,null)) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<InscricaoPrograma> item) {

                item.add(new Label("pessoaEntidade.entidade.nomeEntidade"));
                item.add(new Label("pessoaEntidade.entidade.numeroCnpj"){
                    private static final long serialVersionUID = 1L;

                    @SuppressWarnings("unchecked")
                    @Override
                    public <C> IConverter<C> getConverter(Class<C> type) {
                        return (IConverter<C>) new CpfCnpjConverter();
                    }
                });
                item.add(new Label("pessoaEntidade.entidade.tipoEntidade.descricaoTipoEntidade"));
                item.add(new Label("pessoaEntidade.entidade.municipio.uf.nomeUf"));
                setItemsPerPage(Constants.ITEMS_PER_PAGE_PAGINATION);
            }
        };
    }

    private DataView<InscricaoPrograma> newDataViewClassificacaoPropostas() {
        return new DataView<InscricaoPrograma>("inscricoes", new SortableInscricaoAvaliacaoDataProvider(getPrograma(), inscricaoProgramaService)) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<InscricaoPrograma> item) {
                item.add(new Label("colocacao"));
                item.add(new Label("pessoaEntidade.entidade.nomeEntidade"));
                item.add(new Label("pessoaEntidade.entidade.numeroCnpj"){
                    private static final long serialVersionUID = 1L;

                    @SuppressWarnings("unchecked")
                    @Override
                    public <C> IConverter<C> getConverter(Class<C> type) {
                        return (IConverter<C>) new CpfCnpjConverter();
                    }
                });
                item.add(new Label("pessoaEntidade.entidade.tipoEntidade.descricaoTipoEntidade"));
                item.add(new Label("pessoaEntidade.entidade.municipio.uf.nomeUf"));
                
                if (item.getModelObject().isClassificado()) {
                    item.add(AttributeModifier.append("class", "success"));
                }
                
                setItemsPerPage(Constants.ITEMS_PER_PAGE_PAGINATION);
            }
        };
    }

    public Programa getPrograma() {
        return programa;
    }

    public void setPrograma(Programa programa) {
        this.programa = programa;
    }

    private class GridListaElegibilidadePanel extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public GridListaElegibilidadePanel(String id) {
            super(id);

            dataView = newDataViewPropostasElegiveis();
            add(dataView);
            add(new InfraAjaxPagingNavigator("pagination", dataView));
        }
    }

    private class GridListaAvaliacaoPanel extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public GridListaAvaliacaoPanel(String id) {
            super(id);

            dataView = newDataViewClassificacaoPropostas();
            add(dataView);
            add(new InfraAjaxPagingNavigator("pagination", dataView));
        }
    }

    private Button newButtonVoltar() {
        AjaxButton buttonVoltar = new AjaxButton("btnVoltar") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                voltar(target);
            }
        };
        buttonVoltar.setOutputMarkupId(true);
        return buttonVoltar;
    }

    private void voltar(AjaxRequestTarget target) {
        AnaliseDto analiseDto = new AnaliseDto();
        analiseDto.setAbaClicada(opcaoEscolhida == EnumSelecao.CLASSIFICACAO_PROSPOSTA?EnumAbaFaseAnalise.CLASSIFICACAO:EnumAbaFaseAnalise.ELEGIBILIDADE);
        setResponsePage(new PropostasEnviadasPage(new PageParameters(), programa, backPage,target,analiseDto,3));
    }

    private class ButtonsPanel extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public ButtonsPanel(String id) {
            super(id);

            add(newButtonExportarPdf());
            add(newButtonPublicarElegibilidade());
            add(newButtonPublicarClassificacao());

        }

    }

    private String getLabelPublicar() {
        if (isPublicacaoListaDefinitiva()) {
            return "Publicar Lista DEFINITIVA";
        }
        if (isPublicacaoListaPreliminar()) {
            return "Publicar Lista PRELIMINAR";
        }
        return "";

    }

    private Button newButtonPublicarElegibilidade() {
        Button btn = componentFactory.newButton("btnPublicarElegibilidade", () -> publicar());
        btn.add(new Label("lblElegibilidade", getLabelPublicar()));
        btn.setVisible(opcaoEscolhida == EnumSelecao.PROPOSTAS_ELEGIVEIS && !isVisualizacaoListaDefinitiva(EnumSelecao.PROPOSTAS_ELEGIVEIS));
        return btn;
    }
    
    private Button newButtonPublicarClassificacao() {
        Button btn = componentFactory.newButton("btnPublicarClassificacao", () -> publicar());
        btn.add(new Label("lblClassificacao", getLabelPublicar()));
        btn.setVisible(opcaoEscolhida == EnumSelecao.CLASSIFICACAO_PROSPOSTA && !isVisualizacaoListaDefinitiva(EnumSelecao.CLASSIFICACAO_PROSPOSTA));
        return btn;
    }

    private void publicar() {

        if(hasPermissionPublicacao()){
            setResponsePage(new PublicarListaPropostaPage( new PageParameters(),getPrograma(),opcaoEscolhida,backPage));
        }else{
            addMsgError("Não é possível realizar a publicação da lista gerada. Existe(em) proposta(s) pendente(s) de análise.");
        }
    }
    
    private boolean hasPermissionPublicacao(){
        boolean publicar = false;
        PermissaoProgramaDto permissao = publicizacaoService.buscarPermissoesPrograma(programa);
        if(EnumSelecao.PROPOSTAS_ELEGIVEIS.equals(opcaoEscolhida)){
            if(permissao.getPublicarListaElegibilidade()){
                publicar = true;
            }
        }else if(EnumSelecao.CLASSIFICACAO_PROSPOSTA.equals(opcaoEscolhida)){
            if(permissao.getPublicarListaAvaliacao()){
                publicar = true;
            }
        }
        return publicar;
    }
    

    private Button newButtonExportarPdf() {
        Button btn = componentFactory.newButton("btnExportarPdf", () -> exportarPdf());
        btn.setDefaultFormProcessing(false);
        return btn;
    }
    
    private void exportarPdf() {
        ResourceRequestHandler handler = null;
        if(isListaClassificacao()){
            InscricaoListClassificacaoReportBuilder builder = new InscricaoListClassificacaoReportBuilder(getSubTitulo()+" (NÃO PUBLICADA)");
            handler = new ResourceRequestHandler(builder.exportToPdf(inscricaoProgramaService.gerarListaClassificacaoAvaliacaoSemPaginacao(programa)), getPageParameters());
        }
        if(isListaElegibilidade()){
            InscricaoListReportBuilder builder = new InscricaoListReportBuilder(getSubTitulo()+" (NÃO PUBLICADA)");
            //List<InscricaoPrograma> lista = inscricaoProgramaService.gerarListaClassificacaoElegibilidadeSemPaginacao(programa,EnumStatusInscricao.CONCLUIDA_ANALISE_ELEGIBILIDADE);
            InscricaoPrograma ip = new InscricaoPrograma();
            ip.setResultadoFinalAnaliseElegibilidade(EnumResultadoFinalAnaliseElegibilidade.ELEGIVEL);
            ip.setPrograma(programa);
            List<InscricaoPrograma> lista = inscricaoProgramaService.buscarSemPaginacao(ip);
            handler = new ResourceRequestHandler(builder.exportToPdf(lista),getPageParameters());
        }
        if(handler != null){
            getRequestCycle().scheduleRequestHandlerAfterCurrent(handler);
        }else{
            addMsgError("Erro ao exportar lista para PDF");
        }
    }

    private boolean isPublicacaoListaPreliminar() {
        if (EnumSelecao.PROPOSTAS_ELEGIVEIS.equals(opcaoEscolhida)) {
            return listasPublicadasElegibilidade.isEmpty();
        }
        if (EnumSelecao.CLASSIFICACAO_PROSPOSTA.equals(opcaoEscolhida)) {
            return listaAvaliacaoPublicados.isEmpty();
        }
        return false;
    }

    private boolean isPublicacaoListaDefinitiva() {
        if (EnumSelecao.PROPOSTAS_ELEGIVEIS.equals(opcaoEscolhida)) {
            return listasPublicadasElegibilidade.size() == 1;
        }
        if (EnumSelecao.CLASSIFICACAO_PROSPOSTA.equals(opcaoEscolhida)) {
            return listaAvaliacaoPublicados.size() == 1;
        }
        return false;
    }

    private boolean isVisualizacaoListaDefinitiva(EnumSelecao opcao) {
        if (EnumSelecao.PROPOSTAS_ELEGIVEIS.equals(opcao)) {
            return listasPublicadasElegibilidade.size() == 2;
        }
        if (EnumSelecao.CLASSIFICACAO_PROSPOSTA.equals(opcao)) {
            return listaAvaliacaoPublicados.size() == 2;
        }
        return false;
    }
    
    private boolean isListaClassificacao(){
        return EnumSelecao.CLASSIFICACAO_PROSPOSTA.equals(opcaoEscolhida);
    }
    
    private boolean isListaElegibilidade(){
        return EnumSelecao.PROPOSTAS_ELEGIVEIS.equals(opcaoEscolhida);
    }

    public String getSubTitulo() {
        return subTitulo;
    }

    public void setSubTitulo(String subTitulo) {
        this.subTitulo = subTitulo;
    }
}
