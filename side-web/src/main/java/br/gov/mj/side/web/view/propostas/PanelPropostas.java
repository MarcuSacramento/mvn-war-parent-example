package br.gov.mj.side.web.view.propostas;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.metadata.MetaDataRoleAuthorizationStrategy;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.validator.StringValidator;

import com.googlecode.wicket.jquery.ui.form.button.AjaxButton;

import br.gov.mj.apoio.entidades.Uf;
import br.gov.mj.infra.negocio.persistencia.EnumOrder;
import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraDropDownChoice;
import br.gov.mj.infra.wicket.component.pagination.InfraAjaxPagingNavigator;
import br.gov.mj.infra.wicket.model.LambdaModel;
import br.gov.mj.side.entidades.enums.EnumAbaFaseAnalise;
import br.gov.mj.side.entidades.enums.EnumResultadoFinalAnaliseElegibilidade;
import br.gov.mj.side.entidades.enums.EnumStatusInscricao;
import br.gov.mj.side.entidades.enums.EnumStatusPrograma;
import br.gov.mj.side.entidades.enums.EnumTipoMinuta;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoPrograma;
import br.gov.mj.side.web.dto.AnaliseDto;
import br.gov.mj.side.web.dto.BensVinculadosEntidadeDto;
import br.gov.mj.side.web.dto.PropostaPesquisaDto;
import br.gov.mj.side.web.service.InscricaoProgramaService;
import br.gov.mj.side.web.service.UfService;
import br.gov.mj.side.web.util.CnpjUtil;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.SideUtil;
import br.gov.mj.side.web.view.enums.EnumSelecao;
import br.gov.mj.side.web.view.programa.inscricao.ListaPropostaPage;

import com.googlecode.wicket.jquery.ui.form.button.AjaxButton;

public class PanelPropostas extends Panel {
    private static final long serialVersionUID = 1L;

    private Form<PropostaPesquisaDto> form;
    private Page backPage;
    private Programa programa;

    private PanelPesquisa panelPesquisa;
    private PanelResultadosInscritos panelResultadosInscritos;

    private Button buttonElegiveis;
    private Button btnPesquisar;
    private Button buttonClassificados;
    private DataView<InscricaoPrograma> dataView;
    private AnaliseDto analiseDto;

    private Integer itensPorPagina = Constants.ITEMS_PER_PAGE_PAGINATION;

    private InscricoesProvider inscricoesProvider;

    @Inject
    private ComponentFactory componentFactory;

    @Inject
    private UfService ufService;

    @Inject
    private InscricaoProgramaService inscricaoService;

    public PanelPropostas(String id, Programa programa, AnaliseDto analiseDto, Page backPage) {
        super(id);
        this.backPage = backPage;
        this.programa = programa;
        this.analiseDto = analiseDto;

        iniComponents();
    }

    private void iniComponents() {

        // Se a pesquisa for clicada a partir da aba 'classificacao' trazer
        // somente as inscrições elegiveis.
        PropostaPesquisaDto pesquisa = new PropostaPesquisaDto();
        if (analiseDto.getAbaClicada() == EnumAbaFaseAnalise.CLASSIFICACAO){
            pesquisa.getInscricao().setResultadoFinalAnaliseElegibilidade(EnumResultadoFinalAnaliseElegibilidade.ELEGIVEL);
        }

        pesquisa.getInscricao().setPrograma(programa);
        form = new Form<PropostaPesquisaDto>("form", new CompoundPropertyModel<PropostaPesquisaDto>(pesquisa));
        add(form);

        panelPesquisa = new PanelPesquisa("panelPesquisa"); // panelPesquisa
        panelPesquisa.setVisible(true);
        panelResultadosInscritos = new PanelResultadosInscritos("panelResultadosInscritos"); // panelResultadosInscritos

        form.add(panelPesquisa);
        form.add(panelResultadosInscritos);
    }

    // Paineis
    private class PanelPesquisa extends WebMarkupContainer {

        private static final long serialVersionUID = 1L;

        public PanelPesquisa(String id) {
            super(id);
            setOutputMarkupId(true);

            add(newTextFieldProponente()); // inscricao.pessoaEntidade.entidade.nomeEntidade
            add(newTextFieldCnpj()); // inscricao.pessoaEntidade.entidade.numeroCnpj
            add(newDropDownUf()); // inscricao.pessoaEntidade.entidade.municipio.uf
            add(newTextNumeroInscricao()); // inscricao.id
            
            btnPesquisar = newButtonPesquisar();
            authorize(btnPesquisar, RENDER, PropostasEnviadasPage.ROLE_MANTER_ANALISE_VISUALIZAR);
            btnPesquisar.setVisible(true);
            add(btnPesquisar);
        }
    }

    private class PanelResultadosInscritos extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelResultadosInscritos(String id) {
            super(id);
            setOutputMarkupId(true);

            inscricoesProvider = new InscricoesProvider();

            add(newDataViewInscricoes(inscricoesProvider)); // dataPropostas
            add(newDropItensPorPagina()); // itensPorPagina
            add(newButtonVisualizarPesquisaPropostas()); // btnMostrarPesquisaPropostas
            add(newButtonGerarListaElegiveis()); // btnListaElegiveis
            add(newDownloadRelatorioBens());
            add(newLabelNomeTabela());

            add(new InfraAjaxPagingNavigator("pagination", dataView));
            add(new OrderByBorder<String>("orderByProponente", "pessoaEntidade.entidade.nomeEntidade", inscricoesProvider));
            add(new OrderByBorder<String>("orderByCnpj", "pessoaEntidade.entidade.numeroCnpj", inscricoesProvider));
            add(new OrderByBorder<String>("orderBySituacao", "statusInscricao", inscricoesProvider));
            add(new OrderByBorder<String>("orderByIdInscricao", "id", inscricoesProvider));
        }
    }

    // Componentes

    private Label newLabelNomeTabela() {
        String nomeTabela = "";

        if (analiseDto.getAbaClicada() == EnumAbaFaseAnalise.ELEGIBILIDADE) {
            nomeTabela = "Lista de Propostas";
        } else {
            nomeTabela = "Lista de Propostas Elegíveis";
        }

        Label lbl = new Label("lblNomeTabela", nomeTabela);
        lbl.setOutputMarkupId(true);
        return lbl;
    }

    private Label newLabelNomeBotao() {
                Label lbl = new Label("lblNomeBotao", gerarTextoDoBotao());
        lbl.setOutputMarkupId(true);
        return lbl;
    }
    
    private Button newButtonPesquisar() {
        AjaxFallbackButton btnMostrarPesquisa = componentFactory.newAjaxFallbackButton("btnPesquisar", null, (target, form) -> pesquisar(target));
        btnMostrarPesquisa.setOutputMarkupId(true);
        return btnMostrarPesquisa;
    }

    private TextField<String> newTextFieldProponente() {
        TextField<String> textFieldProponente = componentFactory.newTextField("inscricao.pessoaEntidade.entidade.nomeEntidade", "Proponente", false, null);
        textFieldProponente.add(StringValidator.maximumLength(30));
        return textFieldProponente;
    }

    private TextField<String> newTextFieldCnpj() {
        TextField<String> textFieldCnpj = componentFactory.newTextField("inscricao.pessoaEntidade.entidade.numeroCnpj", "CNPJ", false, null);
        textFieldCnpj.add(StringValidator.maximumLength(30));
        return textFieldCnpj;
    }

    private InfraDropDownChoice<Uf> newDropDownUf() {
        InfraDropDownChoice<Uf> dropDownUf = componentFactory.newDropDownChoice("inscricao.pessoaEntidade.entidade.municipio.uf", "Estado", false, "id", "nomeSigla", null, ufService.buscarTodos(), null);
        dropDownUf.setNullValid(true);
        return dropDownUf;
    }

    private TextField<Integer> newTextNumeroInscricao() {
        TextField<Integer> textFieldNumeroInscricao = componentFactory.newTextField("inscricao.id", "Nº da Proposta", false, null);
        textFieldNumeroInscricao.setOutputMarkupId(true);
        return textFieldNumeroInscricao;
    }

    private DataView<InscricaoPrograma> newDataViewInscricoes(InscricoesProvider ip) {
        dataView = new DataView<InscricaoPrograma>("dataPropostas", ip) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<InscricaoPrograma> item) {

                String inscricaoId = item.getModelObject().getId().toString();
                StringBuilder sb = new StringBuilder(inscricaoId);
                while (sb.length() < 7) {
                    sb.insert(0, '0');
                }

                item.add(new Label("codigoInscricao", sb.toString()));
                item.add(new Label("nomeEntidade", item.getModelObject().getPessoaEntidade().getEntidade().getNomeEntidade()));
                item.add(new Label("cnpjEntidade", CnpjUtil.imprimeCNPJ(item.getModelObject().getPessoaEntidade().getEntidade().getNumeroCnpj())));
                
                String situacaoEntidade = verificarSituacaoEntidade(item);
                item.add(new Label("situacaoEntidade", situacaoEntidade));
                item.add(newButtonVisualizar(item)); // btnVisualizar
                item.add(newButtonAnalisar(item).setVisible(visualizarBotaoAnalise())); // btnAnalisar
            }
        };
        dataView.setItemsPerPage(Constants.ITEMS_PER_PAGE_PAGINATION);
        return dataView;
    }
    
    private boolean visualizarBotaoAnalise(){
        
        //Se o programa não for nulo
        if(programa != null && programa.getId() != null){
            
            //Se a aba clicada for a de elegibilidade
            if(analiseDto.getAbaClicada() == EnumAbaFaseAnalise.ELEGIBILIDADE){
                
                //Se o programa em fase de analise
                if(programa.getStatusPrograma() == EnumStatusPrograma.EM_ANALISE){
                    
                    //Se a segunda lista de analise de elegibilidade tiver sido gerada então não mostrar mais o botão
                    if(analiseDto.isGeradaSegundaListaElegibilidade()){
                        return false;
                    }else{
                        return true;
                    }
                    
                }else{
                    return false;
                }
            }else{
                
              //Se o programa em fase de analise
                if(programa.getStatusPrograma() == EnumStatusPrograma.EM_ANALISE){
                    
                    //Se a segunda lista de analise de elegibilidade tiver sido gerada então não mostrar mais o botão
                    if(analiseDto.isGeradaSegundaListaClassificacao()){
                        return false;
                    }else{
                        return true;
                    }
                    
                }else{
                    return false;
                }                
            }
        }        
        return true;
    }
    
    private boolean visualizarBotaoGerarListaElegiveis(){
      //Se o programa não for nulo
        if(programa != null && programa.getId() != null){
           
            if(permiteMostrarBotaoNaFaseAtual()){
                return true;
            }else{
                return false;
            }
            
        }else{
            return false;
        }
    }
    
    private boolean permiteMostrarBotaoNaFaseAtual(){
        
        EnumStatusPrograma elaboracao = EnumStatusPrograma.EM_ELABORACAO;
        EnumStatusPrograma formulado = EnumStatusPrograma.FORMULADO;
        EnumStatusPrograma publicado = EnumStatusPrograma.PUBLICADO;
        EnumStatusPrograma abertoPropostas = EnumStatusPrograma.ABERTO_REC_PROPOSTAS;
        EnumStatusPrograma suspensoPrazoPropostas = EnumStatusPrograma.SUSPENSO_PRAZO_PROPOSTAS;
        
        if(programa.getStatusPrograma() == elaboracao || programa.getStatusPrograma() == formulado ||
                programa.getStatusPrograma() == publicado || programa.getStatusPrograma() == abertoPropostas ||
                programa.getStatusPrograma() == suspensoPrazoPropostas) {
            return false;
        }else{
            return true;
        }
    }
    
    
    private DropDownChoice<Integer> newDropItensPorPagina() {
        DropDownChoice<Integer> dropDownChoice = new DropDownChoice<Integer>("itensPorPagina", new LambdaModel<Integer>(this::getItensPorPagina, this::setItensPorPagina), Constants.QUANTIDADE_ITENS_TABELA);
        dropDownChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
            };
        });
        return dropDownChoice;
    }

    private Button newButtonVisualizarPesquisaPropostas() {
        AjaxFallbackButton btnMostrarPesquisa = componentFactory.newAjaxFallbackButton("btnMostrarPesquisaPropostas", null, (target, form) -> actionMostrarPesquisa(target, "abaElegibilidade"));
        btnMostrarPesquisa.setOutputMarkupId(true);
        authorize(btnMostrarPesquisa, RENDER, PropostasEnviadasPage.ROLE_MANTER_ANALISE_VISUALIZAR);
        return btnMostrarPesquisa;
    }

    private Button newButtonGerarListaElegiveis() {

        buttonElegiveis = new AjaxButton("btnGerarLista") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                PageParameters parameters = new PageParameters();
                parameters.add("programa", programa.getId());

                if (analiseDto.getAbaClicada() == EnumAbaFaseAnalise.ELEGIBILIDADE) {
                    setResponsePage(new ListaPropostaPage(parameters, EnumSelecao.PROPOSTAS_ELEGIVEIS, backPage));
                } else {
                    setResponsePage(new ListaPropostaPage(parameters, EnumSelecao.CLASSIFICACAO_PROSPOSTA, backPage));
                }
            }
        };
        
        AttributeAppender titleBotao = new AttributeAppender("title", gerarTextoDoBotao(), " ");
        buttonElegiveis.add(titleBotao);
        buttonElegiveis.add(newLabelNomeBotao());
        buttonElegiveis.setOutputMarkupId(true);
        buttonElegiveis.setVisible(visualizarBotaoGerarListaElegiveis());
        return buttonElegiveis;
    }

    private Button newButtonVisualizar(Item<InscricaoPrograma> item) {
        return componentFactory.newButton("btnVisualizar", () -> visualizar(item));
    }

    private Button newButtonAnalisar(Item<InscricaoPrograma> item) {
        Button btnAnalisar = componentFactory.newButton("btnAnalisar", () -> analisar(item));
        btnAnalisar.setVisible(mostrarBotaoAnalisar(item));
        return btnAnalisar;
    }

    private Button newDownloadRelatorioBens() {
        Button btndownloadRR = componentFactory.newButton("btndownloadRelatorioBens", () -> downloadRelatorioBens());
            if (analiseDto.getAbaClicada() == EnumAbaFaseAnalise.ELEGIBILIDADE) {
                btndownloadRR.setVisible(Boolean.FALSE);
            }
        return btndownloadRR;
    }

    // Provider

    private class InscricoesProvider extends SortableDataProvider<InscricaoPrograma, String> {
        private static final long serialVersionUID = 1L;

        public InscricoesProvider() {
            setSort("id", SortOrder.ASCENDING);
        }

        @Override
        public Iterator<InscricaoPrograma> iterator(long first, long size) {
            return inscricaoService.buscarPaginado(form.getModelObject().getInscricao(), (int) first, (int) size, getSort().isAscending() ? EnumOrder.ASC : EnumOrder.DESC, getSort().getProperty()).iterator();
        }

        @Override
        public long size() {
            return inscricaoService.buscarSemPaginacao(form.getModelObject().getInscricao()).size();
        }

        @Override
        public IModel<InscricaoPrograma> model(InscricaoPrograma object) {
            return new CompoundPropertyModel<InscricaoPrograma>(object);
        }
    }

    // Ações
    
    private String gerarTextoDoBotao(){
        String nomeTabela = "";
        if (analiseDto.getAbaClicada() == EnumAbaFaseAnalise.ELEGIBILIDADE) {
            if (!analiseDto.isGeradaSegundaListaElegibilidade()) {
                nomeTabela = "Visualizar a lista PRELIMINAR de propostas elegíveis";
            } else {
               nomeTabela = "Visualizar a lista DEFINITIVA de propostas elegíveis";
            }
        } else {
            if (!analiseDto.isGeradaSegundaListaClassificacao()) {
                nomeTabela = "Visualizar a lista PRELIMINAR de propostas classificadas";
            } else {
                nomeTabela = "Visualizar a lista DEFINITIVA de propostas classificadas";
            }
        }
        return nomeTabela;
    }
    
    private String verificarSituacaoEntidade(Item<InscricaoPrograma> item){
        String situacao;
        if(analiseDto.getAbaClicada() == EnumAbaFaseAnalise.CLASSIFICACAO){
            
            if(item.getModelObject().getStatusInscricao() == EnumStatusInscricao.CONCLUIDA_ANALISE_ELEGIBILIDADE){
                situacao = "Análise de Classificação Pendente";
            }else{
                if(item.getModelObject().getStatusInscricao() == EnumStatusInscricao.CONCLUIDA_ANALISE_AVALIACAO){
                    situacao = "Análise de Classificação Concluída";
                }else{
                    situacao = item.getModelObject().getStatusInscricao().getDescricao();
                }
            }
           
        }else{
            
            if(item.getModelObject().getStatusInscricao() == EnumStatusInscricao.ANALISE_AVALIACAO ||
                    item.getModelObject().getStatusInscricao() == EnumStatusInscricao.CONCLUIDA_ANALISE_AVALIACAO){
                
                situacao = EnumStatusInscricao.CONCLUIDA_ANALISE_ELEGIBILIDADE.getDescricao();
                
            }else{
                situacao = item.getModelObject().getStatusInscricao().getDescricao();
            }
        }
        return situacao;
    }

    private void pesquisar(AjaxRequestTarget target) {
        panelResultadosInscritos.setVisible(true);
        target.add(panelResultadosInscritos);
        target.add(form);
    }

    private void actionMostrarPesquisa(AjaxRequestTarget target, String botaoClicado) {
        panelPesquisa.setVisible(true);
        btnPesquisar.setVisible(true);

        target.add(panelPesquisa, btnPesquisar);
    }

    private void visualizar(Item<InscricaoPrograma> item) {
        setResponsePage(new PropostaPage(null, item.getModelObject(),analiseDto, backPage, true));
    }

    private void analisar(Item<InscricaoPrograma> item) {
        setResponsePage(new PropostaPage(null, item.getModelObject(), analiseDto, backPage, false));
    }

    private boolean mostrarBotaoAnalisar(Item<InscricaoPrograma> item) {
        EnumStatusInscricao statusInscricao = item.getModelObject().getStatusInscricao();
        if (statusInscricao == EnumStatusInscricao.EM_ELABORACAO || statusInscricao == EnumStatusInscricao.FINALIZADA) {
            return false;
        } else {
            return true;
        }
    }

    private void downloadRelatorioBens() {
        RelatorioBensOfBuilder jasper = new RelatorioBensOfBuilder(SideUtil.getCaminhoCompletoRelatorios(getHttpServletRequest().getServletContext()));
        jasper.setNome("Relatório Locais Entrega");
        jasper.setTipo(EnumTipoMinuta.PDF);
        jasper.setDataList(popularLista());
        ByteArrayOutputStream exportar = jasper.exportToByteArray();
        SideUtil.download(exportar.toByteArray(), "Relatório Locais Entrega" + ".pdf");
    }

    private List<BensVinculadosEntidadeDto> popularLista() {
        List<BensVinculadosEntidadeDto> listabensVinculadosEntidadeDtos = new ArrayList<BensVinculadosEntidadeDto>(); 
        
        listabensVinculadosEntidadeDtos = inscricaoService.buscarBensVinculadosAEnderecos(programa);
        
        return listabensVinculadosEntidadeDtos;
    }

    protected HttpServletRequest getHttpServletRequest() {
        RequestCycle cycle = RequestCycle.get();
        HttpServletRequest request = (HttpServletRequest) cycle.getRequest().getContainerRequest();
        return request;
    }

    public Integer getItensPorPagina() {
        return itensPorPagina;
    }

    public void setItensPorPagina(Integer itensPorPagina) {
        this.itensPorPagina = itensPorPagina;
    }

    private void authorize(Component component, Action action, String... roles) {
        String s = StringUtils.join(roles, ",");
        MetaDataRoleAuthorizationStrategy.authorize(component, action, s);
    }
}
