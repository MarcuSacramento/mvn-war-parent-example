package br.gov.mj.side.web.view.cadastraritem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraAjaxFallbackLink;
import br.gov.mj.infra.wicket.component.InfraDropDownChoice;
import br.gov.mj.infra.wicket.component.pagination.InfraAjaxPagingNavigator;
import br.gov.mj.infra.wicket.model.LambdaModel;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.enums.EnumFormaVerificacaoFormatacao;
import br.gov.mj.side.entidades.enums.EnumPerfilEntidade;
import br.gov.mj.side.entidades.enums.EnumSituacaoBem;
import br.gov.mj.side.entidades.enums.EnumTipoPatrimonio;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.web.dto.PatrimoniamentoResultadoPesquisaDto;
import br.gov.mj.side.web.dto.PatrimoniamentoTipo;
import br.gov.mj.side.web.dto.PatrimonioObjetoFornecimentoDto;
import br.gov.mj.side.web.service.BeneficiarioService;
import br.gov.mj.side.web.service.PatrimonioService;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.view.HomePage;
import br.gov.mj.side.web.view.components.EntityDataProvider;
import br.gov.mj.side.web.view.template.TemplatePage;

@AuthorizeInstantiation({ PesquisarItemPage.ROLE_MANTER_PESQUISAR_ITEM_PATRIMONIO_PESQUISAR_ITEM })
public class PesquisarItemPage  extends TemplatePage  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7573841874089409377L;
	
	// ####################################_VARIAVEIS_##############################################
	 private Form<PatrimonioObjetoFornecimentoDto> form;
	 private EnumPerfilEntidade perfilUsuarioLogado;
	 private Entidade entidadeLogada;
	 private PatrimonioObjetoFornecimentoDto patrimonioObjetoFornecimentoDto = new PatrimonioObjetoFornecimentoDto();
	 private PatrimonioObjetoFornecimentoDto patrimonioObjetoFornecimentoDtoTemp = new PatrimonioObjetoFornecimentoDto();
	 
	 private String nomeBem=null;
	 private String identificadorUnico=null;
	 private Long numeroQrCode=null;
	 private EnumSituacaoBem situacaoBem=null;
	 private Programa programaSelecionado = new Programa();
	 
	 
	 List<PatrimoniamentoResultadoPesquisaDto> listaPatrimoniamentoResultadoPesquisaDto = new ArrayList<PatrimoniamentoResultadoPesquisaDto>();
	 
	// ####################################_COMPONETE_WICKET_##############################################
	 public static final String ROLE_MANTER_PESQUISAR_ITEM_PATRIMONIO_PESQUISAR_ITEM = "manter_pesquisar_item_patrimonio:pesquisar_item";
	 private PanelPesquisa panelPesquisa;
	 private PanelListaItens panelListaItens;
	 private PanelBotoes panelBotoes;
	 private DataView<PatrimoniamentoResultadoPesquisaDto> dataViewPatrimoniamentoResultadoPesquisaDto;
	 private InfraAjaxFallbackLink btnRegistrarPatrimonio;
	 
	// ####################################_CONSTANTE_##############################################
	 private Integer itensPorPagina = Constants.ITEMS_PER_PAGE_PAGINATION;
	 
	// ####################################_INJECT_##############################################
	 @Inject
	 private ComponentFactory componentFactory;
	 @Inject
	 private PatrimonioService patrimonioService;
	 @Inject
	 private BeneficiarioService beneficiarioService;

	
	 
	public PesquisarItemPage(PageParameters pageParameters) {
		super(pageParameters);
		 setTitulo("Pesquisar Item");
	     this.entidadeLogada = (Entidade) getSessionAttribute("entidade");
	     this.perfilUsuarioLogado = entidadeLogada.getPerfilEntidade();
		 initComponents();
	}
	
	public PesquisarItemPage(PageParameters pageParameters,PatrimonioObjetoFornecimentoDto patrimonioObjetoFornecimentoDtoTemp) {
		super(pageParameters);
		 setTitulo("Pesquisar Item");
		 this.entidadeLogada = (Entidade) getSessionAttribute("entidade");
		 this.perfilUsuarioLogado = entidadeLogada.getPerfilEntidade();
		 this.patrimonioObjetoFornecimentoDtoTemp = patrimonioObjetoFornecimentoDtoTemp;
		 this.patrimonioObjetoFornecimentoDtoTemp.setEntidade(entidadeLogada);
	     listaPatrimoniamentoResultadoPesquisaDto = patrimonioService.pesquisarListaDeItens(this.patrimonioObjetoFornecimentoDtoTemp);
		 initComponents();
	}

	private void initComponents() {
		    form = componentFactory.newForm("form", new CompoundPropertyModel<PatrimonioObjetoFornecimentoDto>(patrimonioObjetoFornecimentoDto));

	        form.add(componentFactory.newLink("lnkPainelPesquisarItem", HomePage.class));
	        form.add(panelPesquisa = newPanelPesquisa());
	        panelListaItens = newPanelListaItens();
	        panelListaItens.setVisible(listaPatrimoniamentoResultadoPesquisaDto.size()>0?true:false);
	        form.add(panelListaItens);
	        form.add(panelBotoes = newPanelBotoes());
	        add(form);
	}
	
	// ####################################_PAINEIS_##############################################
    // PAINEL PESQUISA
    public PanelPesquisa newPanelPesquisa() {
        panelPesquisa = new PanelPesquisa();
        setOutputMarkupId(Boolean.TRUE);
        return panelPesquisa;
    }

    private class PanelPesquisa extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelPesquisa() {
            super("panelPesquisa");
            add(newTextFieldItem());
            add(newTextFieldIdentificadorUnico());
            add(newTextFieldNumeroQrCode());
            add(newDropDownSituacao());
            add(newDropDownPrograma());
            add(newButtonPesquisar());
            add(newButtonLimpar());
        }
    }
    
    // ####################################_PAINEL_LISTA_ITENS_###############################################
    
    public PanelListaItens newPanelListaItens() {
    	panelListaItens = new PanelListaItens();
        setOutputMarkupId(Boolean.TRUE);
        return panelListaItens;
    }

    private class PanelListaItens extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelListaItens() {
            super("panelListaItens");
            add(newDataViewItens());
            add(newDropItensPorPagina()); // itensPorPagina
            add(new InfraAjaxPagingNavigator("paginationItens", dataViewPatrimoniamentoResultadoPesquisaDto));

            
        }
    }
    
    public PanelBotoes newPanelBotoes() {
    	panelBotoes = new PanelBotoes();
        setOutputMarkupId(Boolean.TRUE);
        return panelBotoes;
    }
    
    private class PanelBotoes extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelBotoes() {
        	  super("panelBotoes");
            setOutputMarkupId(true);
            add(newButtonVoltar()); // btnVoltar
        }
    }
    
    
    // ####################################_COMPONENTE_WICKET_###############################################
    
    private InfraAjaxFallbackLink<Void> newButtonPesquisar() {
    	InfraAjaxFallbackLink<Void> btn = componentFactory.newAjaxFallbackLink("btnPesquisar", (target) -> actionButtonPesquisar(target));
		btn.setOutputMarkupId(true);
		return btn;
	}
    
    private InfraAjaxFallbackLink<Void> newButtonLimpar() {
    	InfraAjaxFallbackLink<Void> btn = componentFactory.newAjaxFallbackLink("btnLimpar", (target) -> actionButtonLimpar(target));
		btn.setOutputMarkupId(true);
		return btn;
	}
    
    private void actionButtonLimpar(AjaxRequestTarget target) {
    	this.nomeBem=null;
    	this.identificadorUnico=null;
    	this.numeroQrCode=null;
    	this.situacaoBem=null;
    	this.programaSelecionado = new Programa();
    	this.patrimonioObjetoFornecimentoDto = new PatrimonioObjetoFornecimentoDto();
    	
    	panelPesquisa.addOrReplace(newTextFieldItem());
    	panelPesquisa.addOrReplace(newTextFieldIdentificadorUnico());
    	panelPesquisa.addOrReplace(newTextFieldNumeroQrCode());
    	panelPesquisa.addOrReplace(newDropDownSituacao());
    	panelPesquisa.addOrReplace(newDropDownPrograma());
    	target.add(panelPesquisa);
        target.appendJavaScript("atualizaCssDropDown();");
	}

	private void actionButtonPesquisar(AjaxRequestTarget target) {
    	patrimonioObjetoFornecimentoDto = new PatrimonioObjetoFornecimentoDto();
    	if(nomeBem !=null){
    		patrimonioObjetoFornecimentoDto.setNomeBem(nomeBem);
    	}
    	if(identificadorUnico !=null){
    		patrimonioObjetoFornecimentoDto.setIdentificadorUnico(identificadorUnico);
    	}
    	if(numeroQrCode !=null){
    		patrimonioObjetoFornecimentoDto.setNumeroQrCode(numeroQrCode);
    	}
    	if(situacaoBem!=null){
    		patrimonioObjetoFornecimentoDto.setSituacaoBem(situacaoBem);
    	}
    	if(programaSelecionado!=null){
    		patrimonioObjetoFornecimentoDto.setPrograma(programaSelecionado);
    	}
    	patrimonioObjetoFornecimentoDto.setEntidade(entidadeLogada);
    	patrimonioObjetoFornecimentoDto.setFormaVerificacao(EnumFormaVerificacaoFormatacao.UNITARIA);
    	patrimonioObjetoFornecimentoDtoTemp = patrimonioObjetoFornecimentoDto;
    	
    	List<PatrimoniamentoResultadoPesquisaDto> listaPatrimoniamentoResultadoPesquisaDtoTemp = new ArrayList<PatrimoniamentoResultadoPesquisaDto>();
    	listaPatrimoniamentoResultadoPesquisaDtoTemp = patrimonioService.pesquisarListaDeItens(patrimonioObjetoFornecimentoDto);
    	listaPatrimoniamentoResultadoPesquisaDto = patrimonioService.verificarObjetosPassiveisDePatrimoniamento(listaPatrimoniamentoResultadoPesquisaDtoTemp);    	
    	if(listaPatrimoniamentoResultadoPesquisaDto.size()>0){
    		atualizarPaneis(target);
    	}else{
    		addMsgInfo("Nenhum resultado foi encontrado!");
    		atualizarPaneis(target);
            target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
    	}
    	
    }
    
    public void atualizarPaneis(AjaxRequestTarget target){
    	this.nomeBem=null;
    	this.identificadorUnico=null;
    	this.numeroQrCode=null;
    	this.situacaoBem=null;
    	this.programaSelecionado = new Programa();
    	this.patrimonioObjetoFornecimentoDto = new PatrimonioObjetoFornecimentoDto();
    	
    	panelPesquisa.addOrReplace(newTextFieldItem());
    	panelPesquisa.addOrReplace(newTextFieldIdentificadorUnico());
    	panelPesquisa.addOrReplace(newTextFieldNumeroQrCode());
    	panelPesquisa.addOrReplace(newDropDownSituacao());
    	panelPesquisa.addOrReplace(newDropDownPrograma());
    	target.add(panelPesquisa);
    	
    	panelListaItens.addOrReplace(newDataViewItens());
    	panelListaItens.addOrReplace(new InfraAjaxPagingNavigator("paginationItens", dataViewPatrimoniamentoResultadoPesquisaDto));
        panelListaItens.setVisible(listaPatrimoniamentoResultadoPesquisaDto.size()>0?true:false);
        target.add(panelListaItens);
        target.appendJavaScript("atualizaCssDropDown();");
    }
    
    public TextField<String> newTextFieldItem() {
		TextField<String> field = componentFactory.newTextField("nomeBem","Item", false, new PropertyModel<String>(this,"nomeBem"));
		action(field);
		return field;
	}
    
    public TextField<String> newTextFieldIdentificadorUnico() {
		TextField<String> field = componentFactory.newTextField("identificadorUnico","Identificador único", false, new PropertyModel<String>(this,"identificadorUnico"));
		action(field);
		return field;
	}
    
    public TextField<Long> newTextFieldNumeroQrCode() {
		TextField<Long> field = componentFactory.newTextField("numeroQrCode","Número QrCode", false, new PropertyModel<Long>(this,"numeroQrCode"));
		actionQrCode(field);
		return field;
	}
    
    public InfraDropDownChoice<EnumSituacaoBem> newDropDownSituacao() {
    	 List<EnumSituacaoBem> listaTemp = Arrays.asList(EnumSituacaoBem.values());
        InfraDropDownChoice<EnumSituacaoBem> drop = componentFactory.newDropDownChoice("situacaoBem", "Situação", Boolean.FALSE, "valor", "descricao", new PropertyModel<EnumSituacaoBem>(this, "situacaoBem"), listaTemp, null);
        actionDrop(drop);
        drop.setNullValid(true);
        drop.setOutputMarkupId(true);
        return drop;
    }
    
    private void  actionDrop(DropDownChoice<EnumSituacaoBem> drop) {
        drop.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1L;

            protected void onUpdate(AjaxRequestTarget target) {
            	// Setar no model
            }
        });
    }
    
    private InfraDropDownChoice<Programa> newDropDownPrograma() {
    	List<Programa> listaprograma = beneficiarioService.buscarTodosProgramasDaEntidade(entidadeLogada);
        InfraDropDownChoice<Programa> dropDownChoice = componentFactory.newDropDownChoice("programaSelecionado", "Programa", Boolean.FALSE, "id", "nomePrograma", new LambdaModel<Programa>(this::getProgramaSelecionado, this::setProgramaSelecionado), listaprograma, null);
        dropDownChoice.setNullValid(Boolean.TRUE);
        actionDownPrograma(dropDownChoice);
        return dropDownChoice;
    }
    
    private void actionDownPrograma(InfraDropDownChoice<Programa> dropDownChoice) {
        AjaxFormComponentUpdatingBehavior onChangeAjaxBehavior = new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                // Setar no Model
            }
        };
        dropDownChoice.add(onChangeAjaxBehavior);
    }

	

	private TextField<Long> actionQrCode(TextField<Long> text) {
    	text.add(new AjaxFormComponentUpdatingBehavior("onkeyup") {
			private static final long serialVersionUID = 1L;

			protected void onUpdate(AjaxRequestTarget target) {
				// Setar no model
			}
		});
		return text;
	}

	private TextField<String> action(TextField<String> text) {
		text.add(new AjaxFormComponentUpdatingBehavior("onkeyup") {
			private static final long serialVersionUID = 1L;

			protected void onUpdate(AjaxRequestTarget target) {
				// Setar no model
			}
		});
		return text;
	}

	public DataView<PatrimoniamentoResultadoPesquisaDto> newDataViewItens() {
		dataViewPatrimoniamentoResultadoPesquisaDto = new DataView<PatrimoniamentoResultadoPesquisaDto>("listaItens", new EntityDataProvider<PatrimoniamentoResultadoPesquisaDto>(listaPatrimoniamentoResultadoPesquisaDto)) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<PatrimoniamentoResultadoPesquisaDto> item) {
            	item.add(new Label("nomePrograma", item.getModelObject().getPrograma().getNomePrograma()));
            	item.add(new Label("identificador", item.getModelObject().getObjetoFornecimentoContrato().getId()));
            	item.add(new Label("nomeItem", item.getModelObject().getBem().getNomeBem()));
            	item.add(new Label("tipoSituacao", item.getModelObject().getObjetoFornecimentoContrato().getSituacaoBem()!=null? item.getModelObject().getObjetoFornecimentoContrato().getSituacaoBem().getDescricao():"-"));
            	if(item.getModelObject().getObjetoFornecimentoContrato().getTipoPatrimonio()!=null && item.getModelObject().getObjetoFornecimentoContrato().getTipoPatrimonio().equals(EnumTipoPatrimonio.MULTIPLO)){
            		item.add(new Label("tipoPatrimonio",item.getModelObject().getObjetoFornecimentoContrato().getTipoPatrimonio().getDescricao()));
            	}else if(item.getModelObject().getObjetoFornecimentoContrato().getTipoPatrimonio()!=null && item.getModelObject().getObjetoFornecimentoContrato().getTipoPatrimonio().equals(EnumTipoPatrimonio.UNICO)){
            		for(PatrimoniamentoTipo obj: item.getModelObject().getDetalhePatrimonio()){
            			item.add(new Label("tipoPatrimonio",obj.getNumeroPatrimonio()));
            		}
            	}else if(item.getModelObject().getObjetoFornecimentoContrato().getTipoPatrimonio()!=null && item.getModelObject().getObjetoFornecimentoContrato().getTipoPatrimonio().equals(EnumTipoPatrimonio.NAO_PATRIMONIAVEL)){
            		item.add(new Label("tipoPatrimonio","Item de Consumo"));
            	}else{
            		item.add(new Label("tipoPatrimonio"," - "));
            	}
            	
            	AttributeAppender classeAtivarPopover = new AttributeAppender("class", "pop", " ");
            	StringBuilder sb = new StringBuilder(1);
            	 if(item.getModelObject().getObjetoFornecimentoContrato().getTipoPatrimonio()!=null && item.getModelObject().getObjetoFornecimentoContrato().getTipoPatrimonio().equals(EnumTipoPatrimonio.MULTIPLO)){
                     sb.append("<table><tr><td><center>  Nome do Item  </center></td><td>&emsp;</td><td><center>  Número Patrimônio  </center></td></tr><tr><td colspan='3'><hr></hr></td></tr>");
            		 for(PatrimoniamentoTipo obj: item.getModelObject().getDetalhePatrimonio()){
            			  sb.append("<tr><td><center>  " + obj.getNomePatrimonio()  + "  </center>  </td><td>&emsp;</td><td><center>  " + obj.getNumeroPatrimonio() + "  </center></td></tr>");
            		 }
            		 sb.append("</table>");
            	 }else{
            		 sb.append("<table><tr><td> EM DESENVOLVIMENTO! </td></tr></table>");
            	 }
            	 
            	 String iconeIdItem = "<a role=\"button\" data-placement=\"left\" data-toggle=\"popover\" data-content=\"" + sb.toString() + "\"><i style=\"color:green;\" class=\"fa fa-info-circle\"> </i></a>";
                 Label lblDescricaoInfMotivo = new Label("infMotivo", iconeIdItem);
                 lblDescricaoInfMotivo.setEscapeModelStrings(false);
                 lblDescricaoInfMotivo.setOutputMarkupId(true);
                 if (item.getModelObject().getDetalhePatrimonio().size() > 10){
                	 lblDescricaoInfMotivo.add(classeAtivarPopover);
                 }
                 
                 if(item.getModelObject().getObjetoFornecimentoContrato().getTipoPatrimonio()!=null && item.getModelObject().getObjetoFornecimentoContrato().getTipoPatrimonio().equals(EnumTipoPatrimonio.MULTIPLO)){
                	 lblDescricaoInfMotivo.setVisible(Boolean.TRUE);
            	}else{
            		 lblDescricaoInfMotivo.setVisible(Boolean.FALSE);
            	}
                item.add(lblDescricaoInfMotivo);	 
            	
            	btnRegistrarPatrimonio = componentFactory.newAjaxFallbackLink("btnRegistrarPatrimonio", (target) -> registrarPatrimonio(target, item));
            	if(item.getModelObject().getObjetoFornecimentoContrato().getSituacaoBem()!=null && item.getModelObject().getObjetoFornecimentoContrato().getSituacaoBem().equals(EnumSituacaoBem.DOADO)){
            		btnRegistrarPatrimonio.setVisible(Boolean.TRUE);
            	}else{
            		btnRegistrarPatrimonio.setVisible(Boolean.FALSE);
            	}
                item.add(btnRegistrarPatrimonio);

            }
        };
        dataViewPatrimoniamentoResultadoPesquisaDto.setItemsPerPage(getItensPorPagina()== null?Constants.ITEMS_PER_PAGE_PAGINATION:getItensPorPagina());
        return dataViewPatrimoniamentoResultadoPesquisaDto;
    }
    
    private DropDownChoice<Integer> newDropItensPorPagina() {
        DropDownChoice<Integer> dropDownChoice = new DropDownChoice<Integer>("itensPorPagina", new LambdaModel<Integer>(this::getItensPorPagina, this::setItensPorPagina), Constants.QUANTIDADE_ITENS_TABELA);
        dropDownChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
            	dataViewPatrimoniamentoResultadoPesquisaDto.setItemsPerPage(getItensPorPagina());
                target.add(panelListaItens);
            };
        });
        return dropDownChoice;
    }
    
    private void registrarPatrimonio(AjaxRequestTarget target, Item<PatrimoniamentoResultadoPesquisaDto> item) {
    	setResponsePage(new RegistrarPatrimonioPage(getPageParameters(), item.getModelObject().getObjetoFornecimentoContrato(), new PesquisarItemPage(getPageParameters()),patrimonioObjetoFornecimentoDtoTemp));
	} 

    private InfraAjaxFallbackLink<Void> newButtonVoltar() {
        InfraAjaxFallbackLink<Void> btn = componentFactory.newAjaxFallbackLink("btnVoltar", (target) -> actionVoltar());
        btn.setOutputMarkupId(true);
        return btn;
    }
    private void actionVoltar() {
        setResponsePage(HomePage.class);
    }
    // ####################################_GETS_AND_SETS###############################################

	public Integer getItensPorPagina() {
		return itensPorPagina;
	}

	public void setItensPorPagina(Integer itensPorPagina) {
		this.itensPorPagina = itensPorPagina;
	}

	public Programa getProgramaSelecionado() {
		return programaSelecionado;
	}

	public void setProgramaSelecionado(Programa programaSelecionado) {
		this.programaSelecionado = programaSelecionado;
	}
}
