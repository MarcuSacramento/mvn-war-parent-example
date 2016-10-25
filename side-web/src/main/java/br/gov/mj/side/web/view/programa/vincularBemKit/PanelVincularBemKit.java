package br.gov.mj.side.web.view.programa.vincularBemKit;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.validation.validator.PatternValidator;
import org.apache.wicket.validation.validator.StringValidator;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraAjaxConfirmButton;
import br.gov.mj.infra.wicket.component.InfraAjaxFallbackLink;
import br.gov.mj.infra.wicket.component.pagination.InfraAjaxPagingNavigator;
import br.gov.mj.infra.wicket.converter.MoneyBigDecimalConverter;
import br.gov.mj.infra.wicket.listener.FeedbackPanelListener;
import br.gov.mj.side.entidades.Bem;
import br.gov.mj.side.entidades.Kit;
import br.gov.mj.side.entidades.programa.ProgramaBem;
import br.gov.mj.side.entidades.programa.ProgramaKit;
import br.gov.mj.side.web.service.BemService;
import br.gov.mj.side.web.service.KitService;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.view.components.validators.CustomRangeValidator;
import br.gov.mj.side.web.view.programa.ProgramaPage;
import br.gov.mj.side.web.view.programa.recursofinanceiro.RecursoFinanceiroPanel;

public class PanelVincularBemKit extends Panel {
    private static final String PAGINATOR_BEM = "paginatorBem";

    private static final String PAGINATOR_KIT = "paginatorKit";

    private static final String NOME_BEM = "nomeBem";

    private static final String NOME_KIT = "nomeKit";

    private static final long serialVersionUID = 1L;

    private Page backPage;
    private Form<PanelVincularBemKit> form;

    private PanelQuantidadeBem panelQuantidadeBem;
    private PanelQuantidadeKit panelQuantidadeKit;
    private PanelDrop panelDrop;
    private PanelPrincipal panelPrincipal;
    private PanelDataViewBem panelDataViewBem;
    private PanelDataViewKit panelDataViewKit;
    private PanelMaximoProposta panelMaximoProposta;

    private BemSelecionadosProvider bemProvider;
    private KitSelecionadosProvider kitProvider;

    private DataView<ProgramaKit> dataviewKitsAdicionado;
    private DataView<ProgramaBem> dataviewBensAdicionados;
    private InfraAjaxPagingNavigator paginatorKit;
    private InfraAjaxPagingNavigator paginatorBem;
    private DropDownChoice dropCategoria;
    private DropDownChoice<Bem> dropBem;
    private DropDownChoice<Kit> dropKit;
    private Label labelMensagem;
    private Model<String> mensagem = Model.of("");
    private TextField<String> text;
    private TextField<String> textMaxima;
    private Label valorTotal;

    private Kit kitSelecionado;
    private Bem bemSelecionado;

    private List<ProgramaKit> listaKitsSelecionados;
    private List<ProgramaBem> listaBensSelecionados;

    private List<Bem> listaTodosBens = new ArrayList<Bem>();
    private List<Kit> listaTodosKits = new ArrayList<Kit>();
    private int contKit = 0;
    private int contBem = 0;

    private String categoria = "";
    private String quantidadeBens = "";
    private String quantidadeMaximBens ="";
    private String dropChoice = "";
    private Boolean readOnly;

    @Inject
    private ComponentFactory componentFactory;

    @Inject
    private KitService kitService;

    @Inject
    private BemService bemService;

    public PanelVincularBemKit(String id, Page backPage, Boolean readOnly) {
        super(id);
        setOutputMarkupId(true);
        
        this.readOnly = readOnly;
        this.backPage = backPage;
        initVariaveis();
        panelPrincipal = new PanelPrincipal("panelPrincipal");
        panelDataViewBem = new PanelDataViewBem("panelDataViewBem");
        panelDataViewKit = new PanelDataViewKit("panelDataViewKit");
        panelMaximoProposta = new PanelMaximoProposta("panelMaximoProposta");

        add(panelPrincipal);
        add(panelDataViewBem);
        add(panelDataViewKit);
        add(panelMaximoProposta);
    }

    public class PanelPrincipal extends WebMarkupContainer {
        public PanelPrincipal(String id) {
            super(id);
            setOutputMarkupId(true);

            add(getButtonPesquisa());
            add(getDropDownCategoria());

            add(getTextFieldQuantidade());
            add(getTextFieldQuantidadeMaxima());
            add(getButtonAdicionar());

            panelQuantidadeBem = new PanelQuantidadeBem("panelQuantidadeBem");
            panelQuantidadeKit = new PanelQuantidadeKit("panelQuantidadeKit");
            panelDrop = new PanelDrop("panelDrop");

            add(panelQuantidadeBem);
            add(panelQuantidadeKit);
            add(panelDrop);

            panelQuantidadeBem.setVisible(false);
            panelQuantidadeKit.setVisible(false);
            panelDrop.setVisible(true);

            add(labelMensagem);
        }
    }

    private class PanelQuantidadeBem extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelQuantidadeBem(String id) {
            super(id);
            setOutputMarkupId(true);
            add(getDropDownNomeBem()); // nomeBemAuto
        }
    }

    private class PanelQuantidadeKit extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelQuantidadeKit(String id) {
            super(id);
            setOutputMarkupId(true);

            add(getDropDownNomeKit()); // nomeKitAuto
        }
    }

    public class PanelDataViewKit extends WebMarkupContainer {
        public PanelDataViewKit(String id) {
            super(id);

            setOutputMarkupId(true);
            add(getDataViewKitsAdicionados()); // dataKits
            add(getPaginatorKit()); // paginatorKit
        }
    }

    private class PanelDrop extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelDrop(String id) {
            super(id);
            setOutputMarkupId(true);

            add(getDropDown()); // dropChoice
        }
    }
    
    public class PanelMaximoProposta extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelMaximoProposta(String id) {
            super(id);
            setOutputMarkupId(true);

            add(newTextFieldValorMaximoProposta()); //valorMaximoProposta
            add(newLabelValorTotal()); //lblValorTotal
        }
    }

    public class PanelDataViewBem extends WebMarkupContainer {
        public PanelDataViewBem(String id) {
            super(id);

            setOutputMarkupId(true);
            add(getDataViewBensAdicionados()); // dataBens
            add(getPaginatorBem()); // paginatorBem
        }
    }

    public void initVariaveis() {
        bemProvider = new BemSelecionadosProvider();
        kitProvider = new KitSelecionadosProvider();

        if (listaKitsSelecionados == null) {
            ProgramaPage programa = (ProgramaPage) backPage;
            listaKitsSelecionados = programa.getForm().getModelObject().getProgramaKits();
        }

        if (listaBensSelecionados == null) {
            ProgramaPage programa = (ProgramaPage) backPage;
            listaBensSelecionados = programa.getForm().getModelObject().getProgramaBens();
        }

        labelMensagem = new Label("mensagemVincular", mensagem);
        labelMensagem.setEscapeModelStrings(false);
        labelMensagem.setOutputMarkupId(true);
    }

    /*
     * ABAIXO SERÃO IMPLEMENTADOS OS COMPONENTES
     */

    public DataView<ProgramaKit> getDataViewKitsAdicionados() {
        dataviewKitsAdicionado = new DataView<ProgramaKit>("dataKits", kitProvider) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<ProgramaKit> item) {
                item.clearOriginalDestination();
                item.add(new Label(NOME_KIT, item.getModelObject().getKit().getNomeKit()));
                item.add(new Label("descricao", item.getModelObject().getKit().getDescricaoKit()));
                item.add(getButtonExcluirKit(item));
                item.add(new QuantidadeItemKitPanel("quantidadeItemKitPanel", item));
                item.add(new QuantidadeMaximaItemKitPanel("quantidadeMaximaItemKitPanel", item));
                
            }
        };
        dataviewKitsAdicionado.setItemsPerPage(10);
        return dataviewKitsAdicionado;
    }

    private class QuantidadeItemKitPanel extends WebMarkupContainer {
        private static final String QUANTIDADE = "quantidade";
        private static final long serialVersionUID = 1L;

        public QuantidadeItemKitPanel(String id, Item<ProgramaKit> item) {
            super(id);
            TextField<Integer> tfValorUtilizar = new TextField<Integer>("quantidadeKit", new PropertyModel<Integer>(item.getModel(), QUANTIDADE));
            tfValorUtilizar.add(new AjaxFormComponentUpdatingBehavior("onkeyup") {
                private static final long serialVersionUID = 1L;

                @Override
                protected void onUpdate(AjaxRequestTarget target) {
                    // Desabilita o Feedback listener
                    RequestCycle.get().setMetaData(FeedbackPanelListener.SKIP_FEEDBACK_LISTENER, Boolean.TRUE);

                    // Ajax para enviar e atualizar o model com o valor da
                    // quantidade
                }
            });
            tfValorUtilizar.setEnabled(!readOnly);
            add(tfValorUtilizar);
        }
    }
    
    private class QuantidadeMaximaItemKitPanel extends WebMarkupContainer {
        private static final String QUANTIDADE = "quantidade";
        private static final long serialVersionUID = 1L;

        public QuantidadeMaximaItemKitPanel(String id, Item<ProgramaKit> item) {
            super(id);
            TextField<Integer> tfValorUtilizar = new TextField<Integer>("quantidadeMaximaKit", new PropertyModel<Integer>(item.getModel(), "quantidadePorProposta"));
            tfValorUtilizar.add(new AjaxFormComponentUpdatingBehavior("onkeyup") {
                private static final long serialVersionUID = 1L;

                @Override
                protected void onUpdate(AjaxRequestTarget target) {
                    // Desabilita o Feedback listener
                    RequestCycle.get().setMetaData(FeedbackPanelListener.SKIP_FEEDBACK_LISTENER, Boolean.TRUE);
                    calcularValorMaximo(target);
                    // Ajax para enviar e atualizar o model com o valor da
                    // quantidade
                }
            });
            tfValorUtilizar.setEnabled(!readOnly);
            add(tfValorUtilizar);
        }
    }

    public DataView<ProgramaBem> getDataViewBensAdicionados() {

        dataviewBensAdicionados = new DataView<ProgramaBem>("dataBens", bemProvider) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<ProgramaBem> item) {
                item.clearOriginalDestination();
                item.add(new Label(NOME_BEM, item.getModelObject().getBem().getNomeBem()));
                item.add(new Label("descricao",item.getModelObject().getBem().getDescricaoBem()));
                item.add(new QuantidadeItemBemPanel("quantidadeItemBemPanel", item));
                item.add(new QuantidadeMaximaItemBemPanel("quantidadeItemMaximoBemPanel", item));
                item.add(getButtonExcluirBem(item));
            }
        };
        dataviewBensAdicionados.setItemsPerPage(10);
        return dataviewBensAdicionados;
    }

    private class QuantidadeItemBemPanel extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public QuantidadeItemBemPanel(String id, Item<ProgramaBem> item) {
            super(id);
            TextField<Integer> tfValorUtilizar = new TextField<Integer>("quantidade", new PropertyModel<Integer>(item.getModel(), "quantidade"));
            tfValorUtilizar.add(new AjaxFormComponentUpdatingBehavior("onkeyup") {
                private static final long serialVersionUID = 1L;

                @Override
                protected void onUpdate(AjaxRequestTarget target) {
                    // Desabilita o Feedback listener
                    RequestCycle.get().setMetaData(FeedbackPanelListener.SKIP_FEEDBACK_LISTENER, Boolean.TRUE);

                    // Ajax para enviar e atualizar o model com o valor da
                    // quantidade

                }
            });
            tfValorUtilizar.setEnabled(!readOnly);
            add(tfValorUtilizar);
        }
    }
    
    private class QuantidadeMaximaItemBemPanel extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public QuantidadeMaximaItemBemPanel(String id, Item<ProgramaBem> item) {
            super(id);
            TextField<Integer> tfValorUtilizar = new TextField<Integer>("quantidadeMaxima", new PropertyModel<Integer>(item.getModel(), "quantidadePorProposta"));
            tfValorUtilizar.add(new AjaxFormComponentUpdatingBehavior("onkeyup") {
                private static final long serialVersionUID = 1L;

                @Override
                protected void onUpdate(AjaxRequestTarget target) {
                    // Desabilita o Feedback listener
                    RequestCycle.get().setMetaData(FeedbackPanelListener.SKIP_FEEDBACK_LISTENER, Boolean.TRUE);
                    calcularValorMaximo(target);
                    // Ajax para enviar e atualizar o model com o valor da
                    // quantidade

                }
            });
            tfValorUtilizar.setEnabled(!readOnly);
            add(tfValorUtilizar);
        }
    }
    
    private void calcularValorMaximo(AjaxRequestTarget target){
        ProgramaPage page = (ProgramaPage) backPage;
        calcularValorMaximoProposta(page);
        
        panelMaximoProposta.addOrReplace(newTextFieldValorMaximoProposta());
        panelMaximoProposta.addOrReplace(newLabelValorTotal());
        page.getRecursoFinanceiroPanel().addOrReplace(page.getRecursoFinanceiroPanel().newLabelValorMaximoProposta());
        
        target.add(panelMaximoProposta);        
        target.add(page.getRecursoFinanceiroPanel());
    }
    

    public InfraAjaxFallbackLink<Void> newButtonNovos() {
        return componentFactory.newAjaxFallbackLink("btnPesquisar", (target) -> pesquisar(target));
    }

    public AjaxSubmitLink getButtonPesquisa() {
        AjaxSubmitLink link = new AjaxSubmitLink("btnPesquisar", form) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form form) {
                pesquisar(target);
            }
        };
        link.setEnabled(!readOnly);
        return link;
    }

    public InfraAjaxConfirmButton getButtonExcluirBem(Item<ProgramaBem> item) {
        InfraAjaxConfirmButton button = componentFactory.newAJaxConfirmButton("btnExcluirBem", "MSG007", null, (target, formz) -> excluirBem(target, item));
        button.setEnabled(!readOnly);
        return button;
    }

    public InfraAjaxConfirmButton getButtonExcluirKit(Item<ProgramaKit> item) {
        InfraAjaxConfirmButton button = componentFactory.newAJaxConfirmButton("btnExcluirKit", "MSG006", null, (target, formz) -> excluirKit(target, item));
        button.setEnabled(!readOnly);
        return button;
    }

    public DropDownChoice getDropDownCategoria() {
        List<String> categorias = new ArrayList<String>();
        categorias.add("Kit");
        categorias.add("Bens");

        dropCategoria = new DropDownChoice<String>("dropCategoria", new PropertyModel<String>(this, "categoria"), categorias);
        actionDropDownCategoria(dropCategoria);
        dropCategoria.setNullValid(true);
        dropCategoria.setOutputMarkupId(true);
        dropCategoria.setEnabled(!readOnly);
        return dropCategoria;
    }

    public DropDownChoice<Kit> getDropDownNomeKit() {
        if (contKit == 0) {
            listaTodosKits = kitService.buscar(new Kit());
            contKit++;
        }

        dropKit = new DropDownChoice<Kit>("nomeKitAuto", new PropertyModel<Kit>(this, "kitSelecionado"), listaTodosKits, new ChoiceRenderer<Kit>(NOME_KIT));
        dropKit.setOutputMarkupId(true);
        dropKit.setNullValid(true);
        dropKit.setEnabled(!readOnly);
        return dropKit;
    }

    public DropDownChoice<Bem> getDropDownNomeBem() {

        if (contBem == 0) {
            listaTodosBens = bemService.buscar(new Bem());
            contBem++;
        }

        dropBem = new DropDownChoice<Bem>("nomeBemAuto", new PropertyModel<Bem>(this, "bemSelecionado"), listaTodosBens, new ChoiceRenderer<Bem>(NOME_BEM));
        dropBem.setOutputMarkupId(true);
        dropBem.setNullValid(true);
        dropBem.setEnabled(!readOnly);
        return dropBem;
    }

    public DropDownChoice<String> getDropDown() {
        List<String> lista = new ArrayList<String>();
        DropDownChoice<String> drop = new DropDownChoice<String>("dropChoice", new PropertyModel<String>(this, "dropChoice"), lista);
        drop.setNullValid(true);
        drop.setEnabled(!readOnly);
        return drop;
    }

    public TextField<String> getTextFieldQuantidade() {
        text = componentFactory.newTextField("quantidadeBens", "Quantidade", false, new PropertyModel(this, "quantidadeBens"));
        text.setRequired(false);
        text.add(StringValidator.maximumLength(10));
        text.add(new PatternValidator(Constants.REGEX_NUMEROS));
        text.setEnabled(!readOnly);
        return text;
    }
    
    public TextField<String> getTextFieldQuantidadeMaxima() {
        textMaxima = componentFactory.newTextField("quantidadeMaximaBens", "QuantidadeMaxima", false, new PropertyModel(this, "quantidadeMaximBens"));
        textMaxima.setRequired(false);
        textMaxima.add(StringValidator.maximumLength(10));
        textMaxima.add(new PatternValidator(Constants.REGEX_NUMEROS));
        textMaxima.setEnabled(!readOnly);
        return textMaxima;
    }
    
    public TextField<BigDecimal> newTextFieldValorMaximoProposta() {
        TextField<BigDecimal> field = new TextField<BigDecimal>("valorMaximoProposta") {
            private static final long serialVersionUID = 1L;

            @SuppressWarnings("unchecked")
            @Override
            public <C> IConverter<C> getConverter(Class<C> type) {
                return (IConverter<C>) new MoneyBigDecimalConverter();
            }
        };
        field.setLabel(Model.of("Valor Máximo por Proposta"));
        field.setRequired(false);
        //field.setEnabled(!readOnly);
        field.add(new CustomRangeValidator(new BigDecimal("0.00"), new BigDecimal("999999999999.99")));
        field.setEnabled(!readOnly);
        actionTextFieldNomeFantasia(field);
        return field;
    }

    public InfraAjaxPagingNavigator getPaginatorKit() {
        if (paginatorKit == null) {
            paginatorKit = new InfraAjaxPagingNavigator(PAGINATOR_KIT, dataviewKitsAdicionado);
        }
        return paginatorKit;
    }

    public InfraAjaxPagingNavigator getPaginatorBem() {
        if (paginatorBem == null) {
            paginatorBem = new InfraAjaxPagingNavigator(PAGINATOR_BEM, dataviewBensAdicionados);
        }
        return paginatorBem;
    }

    public AjaxSubmitLink getButtonAdicionar() {
        AjaxSubmitLink link = new AjaxSubmitLink("btnAdicionarBem", form) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form form) {
                if ("Bens".equalsIgnoreCase(categoria)) {
                    adicionarBem(target);
                } else {
                    adicionarKit(target);
                }
                calcularValorMaximo(target);
            }
        };
        link.setEnabled(!readOnly);
        
        return link;
    }
    
    public Label newLabelValorTotal(){
        
        //Acessa o panel dos recurso financeiros
        ProgramaPage page = (ProgramaPage) backPage;
        BigDecimal big = new BigDecimal("0");
        if(page.getRecursoFinanceiroPanel().getValorTotal()!=null){
            big = page.getRecursoFinanceiroPanel().getValorTotal();
        }
        
        Label lbl = new Label("lblValorTotal",big){
            private static final long serialVersionUID = 1L;

            @SuppressWarnings("unchecked")
            @Override
            public <C> IConverter<C> getConverter(Class<C> type) {
                return (IConverter<C>) new MoneyBigDecimalConverter();
            }
        };
        return lbl;
    }

    /*
     * AS AÇÕES SERÃO MONTADAS ABAIXO
     */
    
    private void actionTextFieldNomeFantasia(TextField field) {
        AjaxFormComponentUpdatingBehavior onChangeAjaxBehavior = new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                
                ProgramaPage page = (ProgramaPage)backPage;
                page.getRecursoFinanceiroPanel().addOrReplace(page.getRecursoFinanceiroPanel().newLabelValorMaximoProposta());
                target.add(page.getRecursoFinanceiroPanel());
            }
        };
        field.add(onChangeAjaxBehavior);
    }
    
    private void calcularValorMaximoProposta(ProgramaPage page )
    {   
        //Calcula o valor dos Bens selecionados
        BigDecimal valorTempBem = new BigDecimal(0);
        for(ProgramaBem pb:page.getForm().getModelObject().getProgramaBens())
        {
            BigDecimal quantidade;
            if(pb.getQuantidadePorProposta() == null)
            {
                quantidade = new BigDecimal("0");
            }
            else
            {
                quantidade = new BigDecimal(pb.getQuantidadePorProposta());
            }
             
            valorTempBem = valorTempBem.add(quantidade.multiply(pb.getBem().getValorEstimadoBem()));
        }
        
        //Calcula o valor de todos os Kits Selecionados
        BigDecimal valorTempKit = new BigDecimal(0);
        for(ProgramaKit pb:page.getForm().getModelObject().getProgramaKits())
        {
            BigDecimal quantidade;
            if(pb.getQuantidadePorProposta() == null)
            {
                quantidade = new BigDecimal("0");
            }
            else
            {
                quantidade = new BigDecimal(pb.getQuantidadePorProposta());
            }
             
            valorTempKit = valorTempKit.add(quantidade.multiply(pb.getKit().getValorEstimado()));
        }
        BigDecimal valorSomar = new BigDecimal("0");
        
        //Soma o valor do kit e dos Bens e seta novalor do formulário.
        valorSomar = valorSomar.add(valorTempBem);
        valorSomar = valorSomar.add(valorTempKit);
        page.getForm().getModelObject().setValorMaximoProposta(valorSomar);
    }

    private void pesquisar(AjaxRequestTarget target) {

        String msg = "";
        if ("Bens".equalsIgnoreCase(categoria)) {
            setResponsePage(new VincularBemPesquisaPage(null, backPage));
        } else {
            if ("Kit".equalsIgnoreCase(categoria)) {
                setResponsePage(new VincularKitPesquisaPage(null, backPage));
            } else {
                msg += "<p><li> Informe uma 'Categoria para Pesquisar'.</li><p />";
            }
        }

        mensagem.setObject(msg);

        target.appendJavaScript("atualizaCssDropDown();");
        target.add(labelMensagem);
    }

    public boolean visualizarKit() {
        boolean visualizarKit = true;
        if ("Bens".equalsIgnoreCase(categoria)) {
            visualizarKit = false;
        } else {
            visualizarKit = true;
        }
        return visualizarKit;
    }

    public void adicionarBem(AjaxRequestTarget target) {
        if (!validarAdicaoBem(target)) {
            return;
        }
        ProgramaBem bemP = new ProgramaBem();
        bemP.setBem(bemSelecionado);
        bemP.setQuantidade(Integer.parseInt(quantidadeBens));
        if(quantidadeMaximBens == null){
            quantidadeMaximBens = "0";
        }
        bemP.setQuantidadePorProposta(Integer.parseInt(quantidadeMaximBens));
        listaBensSelecionados.add(bemP);

        text.setModelObject("");
        textMaxima.setModelObject("");
        panelPrincipal.addOrReplace(getTextFieldQuantidade());
        panelPrincipal.addOrReplace(getTextFieldQuantidadeMaxima());

        bemSelecionado = new Bem();
        categoria = "";

        panelQuantidadeBem.setVisible(false);
        panelQuantidadeKit.setVisible(false);
        panelDrop.setVisible(true);

        panelQuantidadeBem.addOrReplace(getDropDownNomeBem());
        panelQuantidadeKit.addOrReplace(getDropDownNomeKit());
        panelDrop.addOrReplace(getDropDown());

        panelDataViewBem.addOrReplace(getDataViewBensAdicionados());
        panelDataViewBem.addOrReplace(new InfraAjaxPagingNavigator(PAGINATOR_BEM, dataviewBensAdicionados));

        target.add(panelDataViewBem);
        target.add(panelPrincipal);
        target.add(panelQuantidadeBem);
        target.add(panelDrop);
    }

    public void adicionarKit(AjaxRequestTarget target) {

        if (!validarAdicaoKit(target)) {
            return;
        }

        ProgramaKit kitP = new ProgramaKit();
        kitP.setKit(kitSelecionado);
        kitP.setQuantidade(Integer.parseInt(quantidadeBens));
        if(quantidadeMaximBens == null){
            quantidadeMaximBens = "0";
        }
        kitP.setQuantidadePorProposta(Integer.parseInt(quantidadeMaximBens));
        listaKitsSelecionados.add(kitP);

        kitSelecionado = new Kit();

        panelQuantidadeBem.setVisible(false);
        panelQuantidadeKit.setVisible(false);
        panelDrop.setVisible(true);

        panelQuantidadeBem.addOrReplace(getDropDownNomeBem());
        panelQuantidadeKit.addOrReplace(getDropDownNomeKit());
        panelDrop.addOrReplace(getDropDown());

        text.setModelObject("");
        textMaxima.setModelObject("");
        categoria = "";
        panelPrincipal.addOrReplace(getTextFieldQuantidade());
        panelPrincipal.addOrReplace(getTextFieldQuantidadeMaxima());

        panelDataViewKit.addOrReplace(getDataViewKitsAdicionados());
        panelDataViewKit.addOrReplace(new InfraAjaxPagingNavigator(PAGINATOR_KIT, dataviewKitsAdicionado));

        target.add(panelDataViewKit);
        target.add(panelPrincipal);
        target.add(panelQuantidadeKit);
        target.add(panelDrop);
    }

    public boolean validarAdicaoBem(AjaxRequestTarget target) {
        boolean validar = true;
        String msg = "";

        int i = 0;

        if (categoria == null || "".equalsIgnoreCase(categoria)) {
            validar = false;
            msg += "<p><li> Informe uma 'Categoria'.</li><p />";
        }

        for (ProgramaBem bem : listaBensSelecionados) {
            if (bemSelecionado != null && bem.getBem().getId().intValue() == bemSelecionado.getId().intValue()) {
                i++;
                validar = false;
                msg += "<p><li> O Bem selecionado já se encontra na lista.</li><p />";
                break;
            }
        }

        if (i == 0) {
            if (bemSelecionado == null) {
                validar = false;
                msg += "<p><li> Informe um Kit/Bem para ser adicionado a lista.</li><p />";
            }

            if (quantidadeBens == null || "".equalsIgnoreCase(quantidadeBens) || Integer.parseInt(quantidadeBens) < 1) {
                validar = false;
                msg += "<p><li> Informe uma 'Quantidade'.</li><p />";
            }           
        }
        
        if(quantidadeMaximBens != null && !"".equalsIgnoreCase(quantidadeMaximBens)){
            if(Integer.parseInt(quantidadeMaximBens)>Integer.parseInt(quantidadeBens)){
                validar = false;
                msg += "<p><li> A quantidade máxima por proposta não pode ser maior que a quantidade total.</li><p />";
            }
        }

        if (!validar) {
            mensagem.setObject(msg);
        } else {
            mensagem.setObject("");
        }
        target.add(labelMensagem);

        return validar;
    }

    public boolean validarAdicaoKit(AjaxRequestTarget target) {
        boolean validar = true;
        String msg = "";

        int i = 0;

        if (categoria == null || "".equalsIgnoreCase(categoria)) {
            validar = false;
            msg += "<p><li> Informe uma 'Categoria'.</li><p />";
        }

        for (ProgramaKit bem : listaKitsSelecionados) {
            if (kitSelecionado != null && bem.getKit().getId().intValue() == kitSelecionado.getId().intValue()) {
                i++;
                validar = false;
                msg += "<p><li> O Kit selecionado já se encontra na lista.</li><p />";
                break;
            }
        }

        if (i == 0) {
            if (kitSelecionado == null) {
                validar = false;
                msg += "<p><li> Informe um Kit/Bem para ser adicionado a lista.</li><p />";
            }

            if (quantidadeBens == null || "".equalsIgnoreCase(quantidadeBens)) {
                validar = false;
                msg += "<p><li> Informe uma 'Quantidade'.</li><p />";
            }
        }
        
        if(quantidadeMaximBens != null && !"".equalsIgnoreCase(quantidadeMaximBens)){
            if(Integer.parseInt(quantidadeMaximBens)>Integer.parseInt(quantidadeBens)){
                validar = false;
                msg += "<p><li> A quantidade máxima por proposta não pode ser maior que a quantidade total.</li><p />";
            }
        }

        if (!validar) {
            mensagem.setObject(msg);
        } else {
            mensagem.setObject("");
        }
        target.add(labelMensagem);

        return validar;
    }

    private void excluirBem(AjaxRequestTarget target, Item<ProgramaBem> item) {

        for (ProgramaBem prog : listaBensSelecionados) {
            if (prog.getBem().getId().intValue() == item.getModelObject().getBem().getId()) {
                listaBensSelecionados.remove(item.getModelObject());
                break;
            }
        }
        calcularValorMaximo(target);
        panelDataViewBem.addOrReplace(getPaginatorBem());
        panelDataViewBem.addOrReplace(new InfraAjaxPagingNavigator(PAGINATOR_BEM, dataviewBensAdicionados));
        target.add(panelDataViewBem);
    }

    private void excluirKit(AjaxRequestTarget target, Item<ProgramaKit> item) {

        for (ProgramaKit kit : listaKitsSelecionados) {
            if (kit.getKit().getId().intValue() == item.getModelObject().getKit().getId()) {
                listaKitsSelecionados.remove(item.getModelObject());
                break;
            }
        }
        panelDataViewKit.addOrReplace(getDataViewKitsAdicionados());
        panelDataViewKit.addOrReplace(new InfraAjaxPagingNavigator(PAGINATOR_KIT, dataviewKitsAdicionado));
        calcularValorMaximo(target);
        target.add(panelDataViewKit);
    }

    public void actionDropDownCategoria(DropDownChoice dropElemento) {
        dropElemento.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {

                if ("Kit".equalsIgnoreCase(categoria)) {
                    dropKit.setChoices(new ArrayList<Kit>());
                    panelQuantidadeKit.setVisible(true);
                    panelQuantidadeBem.setVisible(false);
                    panelDrop.setVisible(false);
                } else {
                    if ("Bens".equalsIgnoreCase(categoria)) {
                        panelQuantidadeBem.setVisible(true);
                        panelQuantidadeKit.setVisible(false);
                        panelDrop.setVisible(false);
                    } else {
                        panelQuantidadeBem.setVisible(false);
                        panelQuantidadeKit.setVisible(false);
                        panelDrop.setVisible(true);
                    }
                }

                panelQuantidadeBem.addOrReplace(getDropDownNomeBem());
                panelQuantidadeKit.addOrReplace(getDropDownNomeKit());
                panelDrop.addOrReplace(getDropDown());

                target.appendJavaScript("atualizaCssDropDown();");
                target.add(panelQuantidadeBem);
                target.add(panelQuantidadeKit);
                target.add(panelDrop);
            }

        });
    }

    /*
     * PROVIDERS
     */

    private class BensSelecionadosProvider implements IDataProvider<ProgramaBem> {
        private static final long serialVersionUID = 1L;

        public BensSelecionadosProvider() {
        }

        @Override
        public void detach() {
            // Verificar a usabilidade desse método
        }

        @Override
        public Iterator<ProgramaBem> iterator(long first, long size) {

            List<ProgramaBem> lista = ((ProgramaPage) backPage).getForm().getModelObject().getProgramaBens();
            return lista.iterator();
        }

        @Override
        public long size() {
            List<ProgramaBem> lista = ((ProgramaPage) backPage).getForm().getModelObject().getProgramaBens();
            return lista.size();
        }

        @Override
        public IModel<ProgramaBem> model(ProgramaBem object) {
            return new CompoundPropertyModel<ProgramaBem>(object);
        }
    }

    private class BemSelecionadosProvider extends SortableDataProvider<ProgramaBem, String> {
        private static final long serialVersionUID = 1L;

        public BemSelecionadosProvider() {
            setSort(NOME_BEM, SortOrder.ASCENDING);
        }

        @Override
        public Iterator<ProgramaBem> iterator(long first, long size) {

            List<ProgramaBem> bemTemp = new ArrayList<ProgramaBem>();

            int firstTemp = 0;
            int flagTemp = 0;
            for (ProgramaBem k : listaBensSelecionados) {
                if (firstTemp >= first) {
                    if (flagTemp <= size) {
                        bemTemp.add(k);
                        flagTemp++;
                    }
                }
                firstTemp++;
            }
            return bemTemp.iterator();
        }

        @Override
        public long size() {
            return listaBensSelecionados.size();
        }

        @Override
        public IModel<ProgramaBem> model(ProgramaBem object) {
            return new CompoundPropertyModel<ProgramaBem>(object);
        }
    }

    private class KitSelecionadosProvider extends SortableDataProvider<ProgramaKit, String> {
        private static final long serialVersionUID = 1L;

        public KitSelecionadosProvider() {
            setSort(NOME_KIT, SortOrder.ASCENDING);
        }

        @Override
        public Iterator<ProgramaKit> iterator(long first, long size) {

            List<ProgramaKit> bemTemp = new ArrayList<ProgramaKit>();

            int firstTemp = 0;
            int flagTemp = 0;
            for (ProgramaKit k : listaKitsSelecionados) {
                if (firstTemp >= first) {
                    if (flagTemp <= size) {
                        bemTemp.add(k);
                        flagTemp++;
                    }
                }
                firstTemp++;
            }
            return bemTemp.iterator();
        }

        @Override
        public long size() {
            return listaKitsSelecionados.size();
        }

        @Override
        public IModel<ProgramaKit> model(ProgramaKit object) {
            return new CompoundPropertyModel<ProgramaKit>(object);
        }
    }

    public String getDropChoice() {
        return dropChoice;
    }

    public void setDropChoice(String dropChoice) {
        this.dropChoice = dropChoice;
    }

    public PanelMaximoProposta getPanelMaximoProposta() {
        return panelMaximoProposta;
    }

    public void setPanelMaximoProposta(PanelMaximoProposta panelMaximoProposta) {
        this.panelMaximoProposta = panelMaximoProposta;
    }
}
