package br.gov.mj.side.web.view.dashboard.paineis;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.criteria.From;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.validation.validator.StringValidator;
import org.hibernate.SQLQuery.ReturnProperty;

import br.gov.mj.apoio.entidades.TipoEndereco;
import br.gov.mj.apoio.entidades.TipoEntidade;
import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraDropDownChoice;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.enums.EnumPersonalidadeJuridica;
import br.gov.mj.side.web.service.TipoEnderecoService;
import br.gov.mj.side.web.service.TipoEntidadeService;
import br.gov.mj.side.web.view.components.converters.CpfCnpjConverter;
import br.gov.mj.side.web.view.components.converters.RemoveMascarasStringConverter;

/**
 * Painel preparado para exibir os dados do tipo Entidade, podendo ser acrescentado a qualquer pagina onde esses dados forem manipulados.
 * Caso seja necessário acrescentar novos componetes os mesmos poderãos ser passados ao construtor, minimizando a necessidade de refatoração ou herança
 * 
 * OBS: Os seguintes elementos ainda não foram implementados: Validação dos campos. DataView para exibição de uma lista de entidades. Sub-painel para busca de entidade assim
 * como todas as funcionalidades associadas. Motivo: no presente momento é apenas utilizado para exibição
 * 
 * @author diego.mota
 *
 */
public class PanelDadosEntidade extends Panel {
    private static final long serialVersionUID = 1L;

    //##################################################################################
    //constantes
    
    //##################################################################################
    //variaveis
    private Entidade 					entidadeAtual;
    private Boolean						readOnly=false;
    
    //variaveis de componentes
    private PanelPrincipalEntidade		panelPrincipalEntidade;
    private Form<Entidade>				form;
    
    //injeção de dependencias
    @Inject private ComponentFactory 	componentFactory;
    @Inject private TipoEntidadeService tipoEntidadeService;
    @Inject private TipoEnderecoService	tipoEnderecoService;

    //##################################################################################
    //Construtores, Inits & destroyers
    
    /**
     * Painel preparado para exibir os dados do tipo Entidade, podendo ser acrescentado a qualquer pagina onde esses dados forem manipulados.
     * Caso seja necessário acrescentar novos componetes os mesmos poderãos ser passados ao construtor, minimizando a necessidade de refatoração ou herança
     * 
     * OBS: Os seguintes elementos ainda não foram implementados: Validação dos campos. DataView para exibição de uma lista de entidades. Sub-painel para busca de entidade assim
     * como todas as funcionalidades associadas. Motivo: no presente momento é apenas utilizado para exibição
     * @param id
     * @param entidadeAtual
     * @param components - permite acrescenta novos componentes em tempo de instanciação sem a nescessidade
     * de extender o painel. No entanto deve-se levar em consideração que o componente será acescentado sempre dentro
     * do PanelDadosEntidades e sempre ao final. Lembrando-se que é preciso acrescentar o compoente ao html também
     */
    public PanelDadosEntidade(String id, Form<Entidade> form, Entidade entidadeAtual,Component... components) {
        super(id);
        this.form=form;
        this.entidadeAtual=entidadeAtual;
        setOutputMarkupId(true);
        add(newPanelPrincipalEntidade());
    	
    	
    	//assim é possivel acrescentar novos componentes ao painel sem precisar extendelo
    	for(Component c:components){
    		add(c);
    	}
    }

    //##################################################################################
    //apaineis
    /**
     * Painel principal contendo os campos corespondentes aos dados da entidade
     * @author diego.mota
     *
     */
    public class PanelPrincipalEntidade extends WebMarkupContainer {

        private static final long serialVersionUID = 1L;

        public PanelPrincipalEntidade() {
            super("panelPrincipalEntidade");
            setOutputMarkupId(true);

        	add(getTextField(String.class, "numeroCnpj", "CNPJ", true, null, 18, new CpfCnpjConverter()));
        	
        	//adiciona drop down de tipo de entidade
        	add(
        			getDropDown(
        					TipoEntidade.class, 
        					"tipoEntidade", 
        					"Tipo",
        					true, 
        					"id", 
        					"descricaoTipoEntidade", 
        					null, 
        					tipoEntidadeService.buscarTodos())
        					);
        	//-------
        	
        	add(getTextField(String.class, "nomeEntidade", "Nome", true, null, 200, null));
        	
        	//adiciona drop down de natureza juridica
        	add(
        			getDropDown(
        					EnumPersonalidadeJuridica.class, 
        					"personalidadeJuridica", 
        					"Nautureza Juridica", 
        					true, 
        					"valor", 
        					"descricao", 
        					null,
        					EnumPersonalidadeJuridica.getAsList())
        		);
        	
        	try {
				add(new PanelUfMunicipio("PanelUfMunicipio",entidadeAtual,"municipio"));
			} catch (NoSuchFieldException | SecurityException
					| IllegalArgumentException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        	//adiciona drop down de tipo de endereço
        	add(
        			getDropDown(TipoEndereco.class,
        					"tipoEndereco", 
        					"Tipo de Endereço", 
        					true,  
        					"id", 
        					"descricaoTipoEndereco", 
        					null, 
        					tipoEnderecoService.buscarTodos())    			
        			);
        	//-----
        	add(getTextField(String.class, "descricaoEndereco", "Endereço", true, null, 200, null));
        	add(getTextField(String.class, "numeroEndereco", "Numero", true, null, 200, null));
        	add(getTextField(String.class, "complementoEndereco", "Complemento", false, null, 200, null));
        	add(getTextField(String.class, "bairro", "Bairro", true, null, 200, null));
        	add(getTextField(String.class, "numeroCep", "CEP", true, null, 9, new RemoveMascarasStringConverter()));
        	add(getTextField(String.class, "numeroTelefone", "Telefone", true, null, 13,new RemoveMascarasStringConverter()));
        	add(getTextField(String.class, "numeroFoneFax", "Telefone/Fax", false, null, 13,new RemoveMascarasStringConverter()));
        	add(getTextField(String.class, "email", "E-mail", true, null, 200, null));
        	add(getTextField(String.class, "numeroProcessoSEI", "Numero do Processo", true, null, 200, null));

        }
    }
    
    //##################################################################################
    //componentes

	/**
	* Metodo generico para boteção de um componente TextField configurado
	* @param type - Define o tipo de dados a ser recebido pelo textField, ex: String, Integer, long, etc.
	* @param id - Nome do componetne a ser referenciado por meio do wicket:id no html
	* @param label - Label a ser exibido
	* @param required
	* @param model
	* @param maxLength
	* @param converter - recebe um conversor. 'null para anular'
	* @param behaviors
	* @return TextField<String>
	*/
	public <T> TextField<T> getTextField(Class<T> type, String id,String label, Boolean required, IModel model, Integer maxLength,IConverter converter, Behavior... behaviors){
		

		
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
		text.setModel(model);
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
			  String idExpression, String labelExpression, IModel<T> model, List choices, Behavior... behaviors) {

	        InfraDropDownChoice<T> drop = componentFactory
	        		.newDropDownChoice(id, label, required, idExpression, labelExpression, null, choices, null,behaviors);
	        drop.setNullValid(true);
	        drop.setOutputMarkupId(true);

	        return drop;
	}
	//##################################################################################
	//comportamentos
	  
	  
	//##################################################################################
	//metodos privados
	private PanelPrincipalEntidade newPanelPrincipalEntidade(){
		  panelPrincipalEntidade=new PanelPrincipalEntidade();
		  if(readOnly){
			  panelPrincipalEntidade.setEnabled(false);
		  }
		  return panelPrincipalEntidade;
	}
	
	//##################################################################################
	//metodos publicos
	/**
	 * abilita o readOnly para o painel principal, impedindo edição dos dados. Re-instancia o painel principal
	 */
	public void enableReadOnly(){
		readOnly=true;
		addOrReplace(newPanelPrincipalEntidade());
	}
	
	//##################################################################################
	//getters & setters
	public PanelPrincipalEntidade getPanelPrincipalEntidade() {
		return panelPrincipalEntidade;
	}

	public void setPanelPrincipalEntidade(
			PanelPrincipalEntidade panelPrincipalEntidade) {
		this.panelPrincipalEntidade = panelPrincipalEntidade;
	}

	public Boolean getReadOnly() {
		return readOnly;
	}

	public void setReadOnly(Boolean readOnly) {
		this.readOnly = readOnly;
	}
	
	  
}
