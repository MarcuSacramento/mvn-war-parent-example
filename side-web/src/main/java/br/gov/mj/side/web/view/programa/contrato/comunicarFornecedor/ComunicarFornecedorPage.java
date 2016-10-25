package br.gov.mj.side.web.view.programa.contrato.comunicarFornecedor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.validator.StringValidator;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraAjaxFallbackLink;
import br.gov.mj.infra.wicket.component.pagination.InfraAjaxPagingNavigator;
import br.gov.mj.side.entidades.enums.EnumStatusPrograma;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.licitacao.contrato.HistoricoComunicacaoGeracaoOrdemFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.OrdemFornecimentoContrato;
import br.gov.mj.side.web.service.ContratoService;
import br.gov.mj.side.web.service.GenericEntidadeService;
import br.gov.mj.side.web.service.MailService;
import br.gov.mj.side.web.service.OrdemFornecimentoContratoService;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.DataUtil;
import br.gov.mj.side.web.util.MascaraUtils;
import br.gov.mj.side.web.view.planejarLicitacao.ContratoPanelBotoes;
import br.gov.mj.side.web.view.programa.PanelFasePrograma;
import br.gov.mj.side.web.view.programa.contrato.ordemfornecimento.MinutaOrdemFornecimentoPage;
import br.gov.mj.side.web.view.programa.contrato.ordemfornecimento.OrdemFornecimentoPesquisaPage;
import br.gov.mj.side.web.view.template.TemplatePage;

import com.googlecode.wicket.jquery.ui.form.button.AjaxButton;
import com.googlecode.wicket.jquery.ui.form.button.Button;

public class ComunicarFornecedorPage extends TemplatePage {
    private static final long serialVersionUID = 1L;

    // ##################### Constantes #######################
    private static final String ONCHANGE = "onchange";

    // ##################### variaveis ########################

    private Form<ComunicarFornecedorPage> form;
    private Programa programa;
    private Page backPage;

    private HistoricoComunicacaoGeracaoOrdemFornecimentoContrato ultimoHistorico;
    private HistoricoComunicacaoGeracaoOrdemFornecimentoContrato ultimoHistoricoComunicado;
    private OrdemFornecimentoContrato ordemDeFornecimento = new OrdemFornecimentoContrato();
    private String numeroDocumentoSei;
    private boolean mostrarContainerAviso = false;

    private List<HistoricoComunicacaoGeracaoOrdemFornecimentoContrato> listaHistorico = new ArrayList<HistoricoComunicacaoGeracaoOrdemFornecimentoContrato>();

    // ##################### paineis ##########################

    private PanelFasePrograma panelFasePrograma;
    private ContratoPanelBotoes execucaoPanelBotoes;
    private PanelHistorico panelHistorico;
    private PanelDadosBasicos panelDadosBasicos;

    // ##################### Componentes Wicket ###############

    private DataView<HistoricoComunicacaoGeracaoOrdemFornecimentoContrato> dataViewHistorico;

    // ##################### Injeções de dependências #########

    @Inject
    private ComponentFactory componentFactory;
    @Inject
    private ContratoService contratoService;
    @Inject
    private GenericEntidadeService genericService;
    @Inject
    private OrdemFornecimentoContratoService ordemFornecimentoContratoService;
    @Inject
    private MailService mailService;

    public ComunicarFornecedorPage(final PageParameters pageParameters, Programa programa, OrdemFornecimentoContrato ordemFornecimento, Page backPage) {
        super(pageParameters);
        this.programa = programa;
        this.ordemDeFornecimento = ordemFornecimento;
        this.backPage = backPage;

        setTitulo("Gerenciar Programa");
        initVariaveis();
        initComponents();

    }

    public ComunicarFornecedorPage(final PageParameters pageParameters, Programa programa, Page backPage) {
        super(pageParameters);
        this.programa = programa;
        this.backPage = backPage;

        setTitulo("Gerenciar Programa");
        initVariaveis();
        initComponents();

    }

    private void initVariaveis() {
        listaHistorico = ordemFornecimentoContratoService.buscarHistoricoComunicacaoGeracaoOrdemFornecimentoContrato(ordemDeFornecimento,true);
        ultimoHistorico = ordemFornecimentoContratoService.buscarUltimoHistoricoComunicacaoGeracaoOrdemFornecimentoContrato(ordemDeFornecimento, false);
        ultimoHistoricoComunicado = ordemFornecimentoContratoService.buscarUltimoHistoricoComunicacaoGeracaoOrdemFornecimentoContrato(ordemDeFornecimento, true);
        
        //Se já tiver of comunicada então guardar o numero do documento sei
        if(listaHistorico != null && listaHistorico.size() > 0){
        	numeroDocumentoSei = listaHistorico.get(0).getNumeroDocumentoSei();
        }
        
        //Se não houver nenhum histórico de geração de minuta criado então mostrar o aviso.
        if(ultimoHistorico == null){
        	mostrarContainerAviso = true;
        }
    }

    private void initComponents() {
        form = new Form<ComunicarFornecedorPage>("form", new CompoundPropertyModel<ComunicarFornecedorPage>(this));
        add(form);
        
        form.add(execucaoPanelBotoes = new ContratoPanelBotoes("execucaoPanelPotoes", programa, backPage, "comunicarFornecedor"));
        form.add(panelFasePrograma = new PanelFasePrograma("panelFasePrograma", programa, backPage));
        form.add(newContainerAviso("containerAviso"));
        form.add(panelDadosBasicos = new PanelDadosBasicos("panelDadosBasicos")); // panelDadosBasicos
        form.add(panelHistorico = new PanelHistorico("panelHistorico")); // panelHistorico
        form.add(newButtonVoltar()); // btnVoltar
    }

    // PAINEIS

    private class PanelDadosBasicos extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelDadosBasicos(String id) {
            super(id);
            setOutputMarkupId(true);
            
            add(newLabelTituloEmail()); //lblTituloEmail
            add(newLabelEmail()); // lblEmail
            add(newLabelTituloData()); //lblTituloData
            add(newLabelData()); // lblData
            add(newLabelNumeroSei()); //lblSei
            add(newTextFieldNumeroSei()); // numeroSei
            add(newButtonComunicarFornecedor()); // btnComunicarFornecedor
        }
    }

    private class PanelHistorico extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelHistorico(String id) {
            super(id);
            setOutputMarkupId(true);

            add(newDataViewOrdensDeFornecimento()); // listaHistorico
            add(new InfraAjaxPagingNavigator("pagination", dataViewHistorico));
        }
    }
    
    private WebMarkupContainer newContainerAviso(String id){
    	WebMarkupContainer containerAviso = new WebMarkupContainer(id);
    	containerAviso.setVisible(mostrarContainerAviso);
    	containerAviso.add(newButtonGerarMinuta()); //btnComunicarFornecedor
    	return containerAviso;
    }

    // Componentes
    
    private Label newLabelTituloEmail() {
        Label lbl = new Label("lblTituloEmail", "E-Mail");
        lbl.setOutputMarkupId(true);
        lbl.setVisible(minutaJaComunicada());
        return lbl;
    }

    private Label newLabelTituloData() {
        Label lbl = new Label("lblTituloData", "Data da última comunicação");
        lbl.setOutputMarkupId(true);
        lbl.setVisible(minutaJaComunicada());
        return lbl;
    }

    private Label newLabelEmail() {
        Label lbl = new Label("lblEmail", ordemDeFornecimento.getContrato().getPreposto().getEmail());
        lbl.setOutputMarkupId(true);
        lbl.setVisible(minutaJaComunicada());
        return lbl;
    }
    
    private Label newLabelNumeroSei() {
        Label lbl = new Label("lblSei", "*Número Documento SEI");
        lbl.setOutputMarkupId(true);
        lbl.setVisible(mostrarLabelTextFieldDocumentoSei());
        return lbl;
    }

    private Label newLabelData() {
    	
    	String dataComunicacao = " - ";
    	LocalDateTime data = null;
    	if(listaHistorico != null && listaHistorico.size()>0){
    		data = listaHistorico.get(0).getDataComunicacao();
    	}
        
        if(data!=null){
            dataComunicacao = DataUtil.converteDataDeLocalDateParaString(data, "dd/MM/yyyy HH:mm:ss");
        }   
        
        Label lbl = new Label("lblData", dataComunicacao);
        lbl.setOutputMarkupId(true);
        lbl.setVisible(minutaJaComunicada());
        return lbl;
    }

    public TextField<String> newTextFieldNumeroSei() {
        TextField<String> field = componentFactory.newTextField("numeroSei", "Número Documento SEI", false, new PropertyModel<String>(this, "numeroDocumentoSei"));
        field.add(StringValidator.maximumLength(8));
        field.setEnabled(!minutaJaComunicada());
        field.setVisible(mostrarLabelTextFieldDocumentoSei());
        return field;
    }

    public AjaxSubmitLink newButtonComunicarFornecedor() {
        AjaxSubmitLink buttonComunicarFornecedor = new AjaxSubmitLink("btnComunicarFornecedor", form) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form form) {
                actionComunicarFornecedor(target);
            }
        };
        buttonComunicarFornecedor.setOutputMarkupId(true);
        buttonComunicarFornecedor.setVisible(mostrarBotaoComunicar());
        return buttonComunicarFornecedor;
    }

    public DataView<HistoricoComunicacaoGeracaoOrdemFornecimentoContrato> newDataViewOrdensDeFornecimento() {
        dataViewHistorico = new DataView<HistoricoComunicacaoGeracaoOrdemFornecimentoContrato>("listaHistorico", new ProviderHistoricoComunicacao()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<HistoricoComunicacaoGeracaoOrdemFornecimentoContrato> item) {
                
                String dataGeracao = DataUtil.converteDataDeLocalDateParaString(item.getModelObject().getDataGeracao(), "dd/MM/yyyy HH:mm:ss");
                String dataComunicacao = DataUtil.converteDataDeLocalDateParaString(item.getModelObject().getDataComunicacao(), "dd/MM/yyyy HH:mm:ss");
                String dataCancelamento = DataUtil.converteDataDeLocalDateParaString(item.getModelObject().getDataCancelamento(), "dd/MM/yyyy HH:mm:ss");
                String statusOf = item.getModelObject().getPossuiCancelamento()?"Cancelada":"Comunicada";
                
                item.add(new Label("dataGeracao",dataGeracao));
                item.add(new Label("dataComunicacao",dataComunicacao));
                item.add(new Label("statusOf",statusOf));
                item.add(new Label("dataCancelamento",dataCancelamento));
                item.add(new Label("motivoCancelamento"));
            }
        };
        dataViewHistorico.setItemsPerPage(Constants.ITEMS_PER_PAGE_PAGINATION);
        return dataViewHistorico;
    }
    
    private AjaxButton newButtonGerarMinuta() {        
        AjaxButton button = new AjaxButton("btnGerarMinuta") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
            	actionGerarMinuta(ordemDeFornecimento);
            }
        };
        button.setOutputMarkupId(true);
        return button;
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

    // PROVIDER
    private class ProviderHistoricoComunicacao extends SortableDataProvider<HistoricoComunicacaoGeracaoOrdemFornecimentoContrato, String> {
        private static final long serialVersionUID = 1L;

        @Override
        public Iterator<HistoricoComunicacaoGeracaoOrdemFornecimentoContrato> iterator(long first, long size) {

            return listaHistorico.iterator();
        }

        @Override
        public long size() {
            return listaHistorico.size();
        }

        @Override
        public IModel<HistoricoComunicacaoGeracaoOrdemFornecimentoContrato> model(HistoricoComunicacaoGeracaoOrdemFornecimentoContrato object) {
            return new CompoundPropertyModel<HistoricoComunicacaoGeracaoOrdemFornecimentoContrato>(object);
        }
    }

    // AÇÕES
    
    private void actionGerarMinuta(OrdemFornecimentoContrato item) {
        setResponsePage(new MinutaOrdemFornecimentoPage(getPageParameters(), programa, item, backPage,true));
    }

    private boolean minutaJaComunicada() {
        //Se não houver histórico então não existe minuta gerada.
    	if(ultimoHistorico == null){
    		return false;
    	}else{
    		if(listaHistorico != null && listaHistorico.size()>0){
                return true;
            } else {
                return false;
            }
    	}
    }
    
    private boolean mostrarLabelTextFieldDocumentoSei() {
    	if(ultimoHistorico == null){
    		return false;
    	}else{
    		return true;
    	}
    }
    
    private boolean mostrarBotaoComunicar(){
    	if(ultimoHistorico != null){
    		if(!ultimoHistorico.getPossuiComunicado() || (ultimoHistorico.getPossuiComunicado() && ultimoHistorico.getPossuiCancelamento())){
    			return true;
    		}else{
    			return false;
    		}
    	}else{
    		return false;
    	}
    }

    private void actionVoltar(AjaxRequestTarget target) {
        setResponsePage(new OrdemFornecimentoPesquisaPage(new PageParameters(), programa, backPage,ordemDeFornecimento.getContrato()));
    }

    private void actionComunicarFornecedor(AjaxRequestTarget target) {
    	
    	if(!validarNumeroSei()){
    		return;
    	}
    	
    	String sei = MascaraUtils.limparFormatacaoMascara(numeroDocumentoSei);
        ordemFornecimentoContratoService.comunicarMinuta(ordemDeFornecimento,sei,getIdentificador());
        mailService.enviarEmailAoComunicarFornecedor(ordemDeFornecimento,sei);
        
        getSession().info("Ordem de fornecimento comunicada com sucesso.");
        programa.setStatusPrograma(EnumStatusPrograma.EM_EXECUCAO);
        setResponsePage(new ComunicarFornecedorPage(new PageParameters(), programa, ordemDeFornecimento, backPage));
        
    }
    
    private boolean validarNumeroSei(){
    	if(numeroDocumentoSei == null || "".equalsIgnoreCase(numeroDocumentoSei)){
    		addMsgError("O campo 'Número Documento SEI' é obrigatório.");
    		return false;
    	}
    	return true;
    }
}
