package br.gov.mj.side.web.view.fornecedor.paineis;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.validation.validator.StringValidator;

import br.gov.mj.apoio.entidades.TipoEndereco;
import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraDropDownChoice;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.enums.EnumStatusEntidade;
import br.gov.mj.side.web.service.ContratoService;
import br.gov.mj.side.web.service.TipoEnderecoService;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.view.components.converters.RemoveMascarasStringConverter;
import br.gov.mj.side.web.view.dashboard.paineis.PanelUfMunicipio;

import com.googlecode.wicket.jquery.ui.form.button.Button;

public class DadosFornecedorPanel extends Panel {   
    private static final long serialVersionUID = 1L;    

    // ##################################################################################
    // variaveis
    private Entidade fornecedor;
    private Boolean readOnly = false;
    private Boolean deixarTodosPaineisHabilitados = false;
    private Boolean mostrarDropDownStatusEntidade = true;
    private Boolean possuiContrato = false;

    // variaveis de componentes
    private PanelPrincipal panelPrincipal;
    private PanelUfMunicipio panelUfMunicipio;
    private Form<Entidade> form;
    private Button buttonSalvar;

    // injeção de dependencias
    @Inject
    private ComponentFactory componentFactory;
    @Inject
    private TipoEnderecoService tipoEnderecoService;
    @Inject
    private ContratoService contratoService;

    // ##################################################################################
    // Construtores, Inits & destroyers

    /**
     * Painel preparado para exibir os dados do tipo Representante, podendo ser
     * acrescentado a qualquer pagina onde esses dados forem manipulados. Caso
     * seja necessário acrescentar novos componetes os mesmos poderãos ser
     * passados ao construtor, minimizando a necessidade de refatoração ou
     * herança
     * 
     * OBS: Os seguintes elementos ainda não foram implementados: Validação dos
     * campos. DataView para exibição de uma lista de fornecedores . Sub-painel
     * para busca de fornecedor assim como todas as funcionalidades associadas.
     * 
     * @param id
     * @param form
     * @param fornecedor
     * @param components
     *            - permite acrescenta novos componentes em tempo de
     *            instanciação sem a nescessidade de extender o painel. No
     *            entanto deve-se levar em consideração que o componente será
     *            acescentado sempre dentro do PanelDadosEntidades e sempre ao
     *            final. Lembrando-se que é preciso acrescentar o compoente ao
     *            html também
     */
    public DadosFornecedorPanel(String id, Form form, Entidade fornecedor,Boolean readOnly,Boolean deixarTodosPaineisHabilitados) {
        super(id);
        this.form = form;
        this.fornecedor = fornecedor;
        this.readOnly = readOnly;
        this.deixarTodosPaineisHabilitados = deixarTodosPaineisHabilitados;
        
        //Se for um cadastro novo não mostrar o dropDown e setar Ativo.
        if(fornecedor == null || fornecedor.getId() == null){
            mostrarDropDownStatusEntidade = false;
        }
        
        setOutputMarkupId(true);
        add(newPanelPrincipal());
    }
    
    // ##################################################################################
    // apaineis
    /**
     * Painel principal contendo os campos corespondentes aos dados do
     * fornecedor
     * 
     * @author diego.mota
     *
     */
    public class PanelPrincipal extends WebMarkupContainer {

        private static final long serialVersionUID = 1L;

        public PanelPrincipal() {
            super("panelPrincipal");
            setOutputMarkupId(true);
            setEnabled(acionarComponente());

            add(getTextField(String.class, "nomeEntidade", "Razão Social", true, null, 200, null));
            try {
                add(panelUfMunicipio = new PanelUfMunicipio("PanelUfMunicipio", fornecedor, "municipio"));

            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // adiciona drop down de tipo de endereço
            add(getDropDown(TipoEndereco.class, "tipoEndereco", "Tipo de Endereço", true, "id", "descricaoTipoEndereco", null, tipoEnderecoService.buscarTodos()));
            // -----
            add(getTextField(String.class, "descricaoEndereco", "Endereço", true, null, 200, null));
            add(getTextField(String.class, "numeroEndereco", "Número", true, null, 200, null));
            add(getTextField(String.class, "complementoEndereco", "Complemento", false, null, 200, null));
            add(getTextField(String.class, "bairro", "Bairro", true, null, 200, null));
            add(getTextField(String.class, "numeroCep", "CEP", true, null, 9, new RemoveMascarasStringConverter()));
            add(getTextField(String.class, "numeroTelefone", "Telefone", false, null, 13, new RemoveMascarasStringConverter()));
            add(getTextField(String.class, "nomeContato", "Contato", true, null, 200, null));
            add(getTextField(String.class, "email", "E-mail", true, null, 200, null));
            
            TextArea<String> textArea = new TextArea<String>("observacoes", new PropertyModel<String>(fornecedor, "observacoes"));
            textArea.add(StringValidator.maximumLength(Constants.TAMANHO_CARACTERES_CAMPO_TEXT_AREA));
            add(textArea);
            
            add(getDropDownStatus()); // statusEntidade
            add(getLabelAtivar()); // lblAtivar
        }
    }

    // ##################################################################################
    // componentes

    /**
     * Metodo generico para boteção de um componente TextField configurado
     * 
     * @param type
     *            - Define o tipo de dados a ser recebido pelo textField, ex:
     *            String, Integer, long, etc.
     * @param id
     *            - Nome do componetne a ser referenciado por meio do wicket:id
     *            no html
     * @param label
     *            - Label a ser exibido
     * @param required
     * @param model
     * @param maxLength
     * @param converter
     *            - recebe um conversor. 'null para anular'
     * @param behaviors
     * @return TextField<String>
     */

    public <T> TextField<T> getTextField(Class<T> type, String id, String label, Boolean required, IModel model, Integer maxLength, IConverter converter, Behavior... behaviors) {

        TextField<T> text = new TextField<T>(id) {
            @Override
            public <C> IConverter<C> getConverter(java.lang.Class<C> type) {
                if (converter != null) {
                    return converter;
                } else {
                    return super.getConverter(type);
                }
            };
        };
        text.setModel(model);
        text.setRequired(required);
        text.setLabel(new ResourceModel(label, label));
        text.setEnabled(acionarComponente());

        for (Behavior b : behaviors) {
            text.add(b);
        }
        text.add(StringValidator.maximumLength(maxLength));

        return text;
    }

    /**
     * Metodo generico para recupera ruma instancia configurada de um dropDow.
     * 
     * @param type
     * @param id
     * @param label
     * @param required
     * @param idExpression
     *            - Atributo do elemento selecionado a server como identificador
     * @param labelExpression
     *            - Atributo do elemento selecionado a ser exibido.
     * @param model
     * @param choices
     * @param behaviors
     * @return
     */
    
    private InfraDropDownChoice<EnumStatusEntidade> getDropDownStatus() {
        List<EnumStatusEntidade> lista = Arrays.asList(EnumStatusEntidade.values());

        InfraDropDownChoice<EnumStatusEntidade> dropDownChoice = componentFactory.newDropDownChoice("statusEntidade", "Ativar Fornecedor", true, "valor", "descricao", null, lista, null);
        dropDownChoice.setNullValid(false);
        dropDownChoice.setEnabled(ativarDropDown());
        dropDownChoice.setVisible(mostrarDropDownStatusEntidade);
        return dropDownChoice;
    }
    
    private Label getLabelAtivar() {
        Label labelAtivar = new Label("lblAtivar", "Ativar Fornecedor");
        labelAtivar.setVisible(mostrarDropDownStatusEntidade);
        return labelAtivar;
    }
    
    public <T> DropDownChoice<T> getDropDown(Class<T> type, String id, String label, boolean required, String idExpression, String labelExpression, IModel<T> model, List choices, Behavior... behaviors) {

        InfraDropDownChoice<T> drop = componentFactory.newDropDownChoice(id, label, required, idExpression, labelExpression, null, choices, null, behaviors);
        drop.setNullValid(true);
        drop.setOutputMarkupId(true);

        return drop;
    }

    private PanelPrincipal newPanelPrincipal() {
        panelPrincipal = new PanelPrincipal();
        panelPrincipal.setEnabled(acionarComponente());
        return panelPrincipal;
    }

    //AÇÕES
    
    private boolean ativarDropDown(){
        if(readOnly){
            return false;
        }else{
            if(!possuiContrato){
                return true; 
            }else{
                return false;
            }
        }
    }
    
    private boolean acionarComponente(){
        if(readOnly){
            return false;
        }else{
            if(deixarTodosPaineisHabilitados){
                return true;
            }else{
                return false;
            }
        }
    }

    // ##################################################################################
    // getters & setters
    public PanelPrincipal getPanelPrincipal() {
        return panelPrincipal;
    }

    public void setPanelPrincipal(PanelPrincipal panelPrincipal) {
        this.panelPrincipal = panelPrincipal;
    }

    public Boolean getMostrarDropDownStatusEntidade() {
        return mostrarDropDownStatusEntidade;
    }

    public void setMostrarDropDownStatusEntidade(Boolean mostrarDropDownStatusEntidade) {
        this.mostrarDropDownStatusEntidade = mostrarDropDownStatusEntidade;
    }

    public PanelUfMunicipio getPanelUfMunicipio() {
        return panelUfMunicipio;
    }

    public void setPanelUfMunicipio(PanelUfMunicipio panelUfMunicipio) {
        this.panelUfMunicipio = panelUfMunicipio;
    }
}
