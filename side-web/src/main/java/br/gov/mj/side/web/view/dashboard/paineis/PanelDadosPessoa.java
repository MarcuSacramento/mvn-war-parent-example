package br.gov.mj.side.web.view.dashboard.paineis;

import java.awt.Paint;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.validation.validator.StringValidator;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraAjaxFallbackLink;
import br.gov.mj.infra.wicket.component.InfraDropDownChoice;
import br.gov.mj.infra.wicket.component.pagination.InfraAjaxPagingNavigator;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.entidade.LocalEntregaEntidade;
import br.gov.mj.side.entidades.entidade.Pessoa;
import br.gov.mj.side.entidades.entidade.PessoaEntidade;
import br.gov.mj.side.entidades.enums.EnumStatusPessoa;
import br.gov.mj.side.entidades.enums.EnumTipoPessoa;
import br.gov.mj.side.web.util.CPFUtils;
import br.gov.mj.side.web.util.DataUtil;
import br.gov.mj.side.web.view.components.converters.CpfCnpjConverter;
import br.gov.mj.side.web.view.components.converters.CpfMaskedHideConverter;
import br.gov.mj.side.web.view.components.converters.LocalDateConverter;
import br.gov.mj.side.web.view.components.converters.LocalDateTimeConverter;
import br.gov.mj.side.web.view.components.converters.RemoveMascarasStringConverter;


/**
 * Painel preparado para exibir os dados do tipo Pessoa, podendo ser acrescentado a qualquer pagina onde esses dados forem manipulados.
 * Caso seja necessário acrescentar novos componetes os mesmos poderãos ser passados ao construtor, minimizando a necessidade de refatoração ou herança
 * 
 * OBS: Os seguintes elementos ainda não foram implementados: Validação dos campos. Botão para adicionar a DataView. Botão para remover da  DataView. Botão de Salvar.
 * Botão de excluir. Sub-painel para busca de pessoas assim como as funcinalidades associadas Motivo: no presente momento é apenas utilizado para exibição
 * 
 * @author diego.mota
 *
 */
public class PanelDadosPessoa extends Panel {
    private static final long serialVersionUID = 1L;
	//###################################################################################
	//constantes

	//###################################################################################
	//variaveis
	private PessoaEntidade 					pessoaEntidade;
	private List<PessoaEntidade>			pessoas;
	private Boolean							readOnly				=	false;
	private Boolean							hideMainPanelWhenEmpty	=	false;
	private EnumTipoPessoa					tipoPessoa;
	
	//elementos do Wicket
    private PanelPrincipal 					panelPrincipal;
    private Form<Entidade>					form;
    private DataView<PessoaEntidade>		dataViewPessoa;
    private PanelDataView					panelDataView;
    private InfraAjaxFallbackLink			buttonCancelar;

	//###################################################################################
	//injeçãod e dependencia
	@Inject private	ComponentFactory		componentFactory;
	
	
	//###################################################################################
	//conscructs, inits & destroiers
	
	/**
	 * Painel preparado para exibir os dados do tipo Pessoa, podendo ser acrescentado a qualquer pagina onde esses dados forem manipulados.
	 * Caso seja necessário acrescentar novos componetes os mesmos poderãos ser passados ao construtor, minimizando a necessidade de refatoração ou herança
	 * 
	 * OBS: A validação de campos ainda não foi implementada
	 * @param id
	 * @param form
	 * @param entidade
	 * @param tituloDoPainel
	 * @param components - passe aqui 0,1 ou N instancias de componentes que serão acrescentadas ao final do painel
	 */
    public PanelDadosPessoa(String id, Form<Entidade> form, List<PessoaEntidade> pessoas, EnumTipoPessoa tipoPessoa) {
    	super(id);
    	init(form, pessoas,null, tipoPessoa);
    }
	
	
	/**
	 * Painel preparado para exibir os dados do tipo Pessoa, podendo ser acrescentado a qualquer pagina onde esses dados forem manipulados.
	 * Caso seja necessário acrescentar novos componetes os mesmos poderãos ser passados ao construtor, minimizando a necessidade de refatoração ou herança
	 * 
	 * OBS: A validação de campos ainda não foi implementada
	 * @param id
	 * @param form
	 * @param entidade
	 * @param tituloDoPainel
	 * @param components - passe aqui 0,1 ou N instancias de componentes que serão acrescentadas ao final do painel
	 */
    public PanelDadosPessoa(String id, Form<Entidade> form, List<PessoaEntidade> pessoas, String tituloDoPainel, EnumTipoPessoa tipoPessoa) {
        super(id);
       
        init(form, pessoas,tituloDoPainel, tipoPessoa);
       
    }
    
    private void init(Form<Entidade> form, List<PessoaEntidade> pessoas,String tituloDoPainel,EnumTipoPessoa tipoPessoa){
    	 setOutputMarkupId(true);
    	 this.form			=	form;
         this.pessoas		=	pessoas;
         this.tipoPessoa	=	tipoPessoa;
         add(newTitulo(tituloDoPainel));
         add(newPanelPrincipal());
         add(newPanelDataView());
         
    }

	//###################################################################################
	//classes privadas

    //paineis
    
    /**
     * O sub-painel principal foi declarado como publico, assim pode ser manipulado diretamente a partir da pagina por meio do atributo panelPrincipal
     * @author eder.alves
     *
     */
    public class PanelPrincipal extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelPrincipal() {
            super("panelPrincipal");
            setOutputMarkupId(true);
            
            add(getTextField(String.class, "pessoa.numeroCpf", "CPF", true, true, 13, new CpfMaskedHideConverter()));
            add(getTextField(String.class,"pessoa.nomePessoa", "Nome", true, true, 200, null));
            add(getTextField(String.class,"pessoa.numeroTelefone", "Telefone", true, true, 13, new RemoveMascarasStringConverter()));
            add(getTextField(String.class, "pessoa.email", "E-mail", true, true, 200, null));
            

            add(getTextField(String.class, "pessoa.descricaoCargo", "Cargo", true, true, 200, null));
            add(getTextField(LocalDate.class, "pessoa.dataInicioExercicio", "Periodo de Exercicio: Data Inicio", true, true, 10, new LocalDateConverter()));
            add(getTextField(LocalDate.class, "pessoa.dataFimExercicio", "Periodo de Exercicio: Data Limite", true, true, 10, new LocalDateConverter()));
            add(getTextField(String.class, "pessoa.enderecoCorrespondencia", "Endereco de Correspondencia", true, true, 200, null));
            add(getDropDown(EnumStatusPessoa.class, "pessoa.statusPessoa", "Ativo", true, "enum", "descricao", true, EnumStatusPessoa.asList()));
        }
    }
    
    /**
     * O sub-painel da data view foi declarado como publico, assim pode ser manipulado diretamente a partir da pagina por meio do atributo panelDataView
     * @author eder.alves
     *
     */
    private class PanelDataView extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelDataView() {
            super("panelDataView");
            add(getButtonCancelar());
            add(newDataViewPessoa());
            add(new InfraAjaxPagingNavigator("pagination", dataViewPessoa));
        }
    }
    
	//###################################################################################
	//criação e configuração de componentes
    /**
	* Metodo generico para boteção de um componente TextField configurado
	* @param type - Define o tipo de dados a ser recebido pelo textField, ex: String, Integer, long, etc.
	* @param id - Nome do componetne a ser referenciado por meio do wicket:id no html
	* @param label - Label a ser exibido
	* @param required
	* @param model - ao receber true ira instanciar um PropertyModel configurado para o tipo PessoaEntidade com o mesmo identificador passado no atributo id
	* @param maxLength
	* @param converter - recebe um conversor. 'null para anular'
	* @param behaviors
	* @return TextField<String>
	*/
	public <T> TextField<T> getTextField(Class<T> type, String id,String label, Boolean required, Boolean model, Integer maxLength,IConverter converter, Behavior... behaviors){
		

		
		 TextField<T> text = new TextField<T>(id){
			 @Override
			 public <C> IConverter<C> getConverter(java.lang.Class<C> type) {
					 if(converter!=null){
						 return converter;
					 }else{
						 return super.getConverter(type);
					 }
				 };
			 };
		if(model){
			text.setModel((IModel<T>) newPropertyModelPessoaEntidade(id));
		}
		text.setRequired(required);
		text.setLabel(new ResourceModel(label, label));
		
		for(Behavior b:behaviors){
			text.add(b);
		}
		text.add(StringValidator.maximumLength(maxLength));
		
	    return text;
	}
	
	/**
	* Metodo generico para recupera ruma instancia configurada de um dropDow.
	* @param type
	* @param id
	* @param label
	* @param required
	* @param idExpression - Atributo do elemento selecionado a server como identificador
	* @param labelExpression - Atributo do elemento selecionado a ser exibido.
	* @param model
	* @param choices
	* @param behaviors
	* @return
	*/
	public <T>DropDownChoice<T> getDropDown(Class<T> type,String id, String label,boolean required,
			  String idExpression, String labelExpression, Boolean model, List choices, Behavior... behaviors) {

	        InfraDropDownChoice<T> drop = componentFactory
	        		.newDropDownChoice(id, label, required,  idExpression,  labelExpression, model?newPropertyModelPessoaEntidade(id):null, choices, null,behaviors);
	        drop.setNullValid(true);
	        drop.setOutputMarkupId(true);

	        return drop;
	}
	 /**
	  * Monta e configura a dataView para exibição de pessoass
	  * @return
	  */
	 private DataView newDataViewPessoa() {
		 dataViewPessoa= new DataView<PessoaEntidade>("dataTitulares", new ListDataProvider(pessoas!=null?pessoas:new ArrayList<PessoaEntidade>())) {
           private static final long serialVersionUID = 1L;

           @SuppressWarnings("unchecked")
           @Override
           protected void populateItem(Item<PessoaEntidade> item) {
           	item.clearOriginalDestination();
           	item.add(new Label("pessoa.nomePessoa",item.getModelObject().getPessoa().getNomePessoa()));
           	item.add(new Label("pessoa.numeroCpf", CPFUtils.esconderCpfMascarado(item.getModelObject().getPessoa().getNumeroCpf())));
           	item.add(new Label("pessoa.descricaoCargo",item.getModelObject().getPessoa().getDescricaoCargo()));
           	item.add(new Label("exercicio", DataUtil.converteDataDeLocalDateParaString(item.getModelObject().getPessoa().getDataInicioExercicio(), "dd/MM/yyyy")
           			+(item.getModelObject().getPessoa().getDataFimExercicio()!=null?" - "+
           					DataUtil.converteDataDeLocalDateParaString(item.getModelObject().getPessoa().getDataFimExercicio(),"dd/MM/yyyy"):"")));
           	item.add(new Label("pessoa.statusPessoa.descricao",item.getModelObject().getPessoa().getStatusPessoa().getDescricao()));
           	item.add(getButtonVisualizarDataView(item));
           }
       };
       return dataViewPessoa;
	 }
	 
	 /**
	  * retorna uma instancia de AjaxSubmitLink configuarada para iniciar a edição de um item da dataView 
	  * @return
	  */
	 private InfraAjaxFallbackLink<Void> getButtonVisualizarDataView(Item<PessoaEntidade> item){
		 
		InfraAjaxFallbackLink<Void> button=componentFactory.newAjaxFallbackLink("btnVisualizar", (target)-> visualizar(target,item));
		
		return button;
	 }
	 
	 /**
	  * retorna uma instancia de AjaxSubmitLink configuarada para iniciar a edição de um item da dataView 
	  * @return
	  */
	 private InfraAjaxFallbackLink<Void> getButtonCancelar(){
		 
		buttonCancelar=componentFactory.newAjaxFallbackLink("btnCancelar", (target)-> cancelar(target));
		buttonCancelar.setVisible(false);
		return buttonCancelar;
	 }
	 
	private PanelDataView newPanelDataView(){
		panelDataView=new PanelDataView();
		
		return panelDataView;
	}
	
	
	/**
	 * Retorna um property model configurado para o atributo informado
	 * @param id - valor que identifica o atributo
	 * @return
	 */
	private PropertyModel<PessoaEntidade> newPropertyModelPessoaEntidade(String id){
		return new PropertyModel<PessoaEntidade>(pessoaEntidade,id);
	}
	private PanelPrincipal newPanelPrincipal(){
		panelPrincipal=new PanelPrincipal();

		if(hideMainPanelWhenEmpty){
			panelPrincipal.setVisible(false);
		}
		if(readOnly){
			panelPrincipal.setEnabled(false);
		}
		return panelPrincipal;
	}
	/**
	 * Configura o titulo que aparecerá no cabeçalho do painel
	 * @param titulo
	 * @return
	 */
	private Label newTitulo(String titulo){
		return componentFactory.newLabel("tituloPanel", (titulo!=null)?titulo:"Titulo");
	}
	//###################################################################################
	//metodos privados
	

	/**
	 * Exibe os dados  apenas para leitura
	 * @param target
	 * @param item
	 */
	private void visualizar(AjaxRequestTarget target, Item<PessoaEntidade> item){
		pessoaEntidade=item.getModelObject();
		addOrReplace(newPanelPrincipal());
		if(hideMainPanelWhenEmpty){
			panelPrincipal.setVisible(true);
			buttonCancelar.setVisible(true);
		}
		form.addOrReplace(this);
		target.add(form);
		
	}
	
	private void cancelar(AjaxRequestTarget target){
		pessoaEntidade= new PessoaEntidade();
		addOrReplace(newPanelPrincipal());
		if(hideMainPanelWhenEmpty){
			buttonCancelar.setVisible(false);
		}
		form.addOrReplace(this);
		target.add(form);
	}
	
	//###################################################################################
	//metodos publicos
	/**
	 * Habilita a opção de esconder o painel principal quando não possuir dados a serem exibidos. Re-instancia o painel principal
	 */
	public void enableHideMainPanelWhenEmpty(){
		hideMainPanelWhenEmpty=true;
		addOrReplace(newPanelPrincipal());
	}
	/**
	 * abilita o readOnly para o painel principal, impedindo edição dos dados. Re-instancia o painel principal
	 */
	public void enableReadOnly(){
		readOnly=true;
		addOrReplace(newPanelPrincipal());
	}

	//###################################################################################
	//getters & setters
	public PanelPrincipal getPanelPrincipal() {
		return panelPrincipal;
	}

	public void setPanelPrincipal(PanelPrincipal panelPrincipal) {
		this.panelPrincipal = panelPrincipal;
	}

	public PanelDataView getPanelDataView() {
		return panelDataView;
	}

	public void setPanelDataView(PanelDataView panelDataView) {
		this.panelDataView = panelDataView;
	}

	public DataView<PessoaEntidade> getDataViewPessoa() {
		return dataViewPessoa;
	}

	public void setDataViewPessoa(DataView<PessoaEntidade> dataViewPessoa) {
		this.dataViewPessoa = dataViewPessoa;
	}


	public Boolean getReadOnly() {
		return readOnly;
	}


	public void setReadOnly(Boolean readOnly) {
		this.readOnly = readOnly;
	}


	public Boolean getHideMainPanelWhenEmpty() {
		return hideMainPanelWhenEmpty;
	}


	public void setHideMainPanelWhenEmpty(Boolean hideMainPanelWhenEmpty) {
		this.hideMainPanelWhenEmpty = hideMainPanelWhenEmpty;
	}
	

}
