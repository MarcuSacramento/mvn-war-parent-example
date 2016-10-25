package br.gov.mj.side.web.view.programa.criterioElegibilidade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.StringValidator;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraAjaxConfirmButton;
import br.gov.mj.infra.wicket.component.pagination.InfraAjaxPagingNavigator;
import br.gov.mj.side.entidades.programa.ProgramaCriterioElegibilidade;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.view.programa.ProgramaPage;

public class PanelCriterioElegibilidadePage extends Panel {
    private static final String PAGINATOR = "paginator";

    private static final long serialVersionUID = 1L;

    private Page backPage;

    private PanelPrincipal panelPrincipal;
    private PanelDados panelDadosBasicos;
    private PanelBotoes panelBotoes;
    private PanelDataViewCriterio panelDataViewCriterio;
    private List<ProgramaCriterioElegibilidade> listaCriterio;
    private ProgramaCriterioElegibilidade itemSelecionadoEditar = new ProgramaCriterioElegibilidade();
    private ProgramaCriterioElegibilidade itemSelecionadoExcluir;
    private CriterioProvider criterioProvider;

    private DataView dataViewCriterio;
    private AjaxButton buttonAdicionar;
    private AjaxButton buttonSalvarEdicao;
    private AjaxSubmitLink buttonCancelarEdicao;
    private InfraAjaxPagingNavigator paginator;
    private Label labelMensagem;

    private int itensPorPagina = 5;
    private String nome = "";
    private String descricao = "";
    private String verificacao = "";
    private Boolean possuiObrigatoriedadeDeAnexo;
    private boolean modoEdicao = false;
    private Model<String> mensagem = Model.of("");

    @Inject
    private ComponentFactory componentFactory;

    public PanelCriterioElegibilidadePage(String id, Page backPage, Boolean readOnly) {
        super(id);
        setOutputMarkupId(true);

        this.backPage = backPage;
        this.modoEdicao = readOnly;
        
        initVariaveis();
        panelPrincipal = new PanelPrincipal("panelPrincipalElegibilidade");

        add(panelPrincipal);
    }

    public void initVariaveis() {
        criterioProvider = new CriterioProvider();

        ProgramaPage page = (ProgramaPage) backPage;
        listaCriterio = page.getForm().getModelObject().getCriteriosElegibilidade() != null ? page.getForm().getModelObject().getCriteriosElegibilidade() : new ArrayList<ProgramaCriterioElegibilidade>();

        labelMensagem = new Label("mensagemElegibilidade", mensagem);
        labelMensagem.setEscapeModelStrings(false);
        labelMensagem.setOutputMarkupId(true);
    }

    public class PanelPrincipal extends WebMarkupContainer {
        public PanelPrincipal(String id) {
            super(id);
            setOutputMarkupId(true);

            panelDataViewCriterio = new PanelDataViewCriterio("panelDataViewCriterio");
            panelDadosBasicos = new PanelDados("panelDadosBasicos");
            panelBotoes = new PanelBotoes("panelBotoes");
            
            panelBotoes.setEnabled(!modoEdicao);
            panelDadosBasicos.setEnabled(!modoEdicao);

            add(panelDataViewCriterio);
            add(panelDadosBasicos);
            add(panelBotoes);

            add(labelMensagem);

        }
    }

    public class PanelDataViewCriterio extends WebMarkupContainer {
        public PanelDataViewCriterio(String id) {
            super(id);
            setOutputMarkupId(true);
            add(getDataViewBensAdicionados(criterioProvider));
            add(getPaginator());

        }
    }

    public class PanelDados extends WebMarkupContainer {
        public PanelDados(String id) {
            super(id);
            setOutputMarkupId(true);

            add(getTextFieldNome());// nomeElegibilidade
            add(getTextAreaDescricao());// descricaoElegibilidade
            add(getTextAreaVerificacao()); // verificacaoElegibilidade
            add(newDropDownAnexoObrigatorio());
        }
    }

    public class PanelBotoes extends WebMarkupContainer {
        public PanelBotoes(String id) {
            super(id);
            setOutputMarkupId(true);

            add(getButtonAdicionar()); // btnAdicionar
            add(getButtonSalvarEdicao()); // btnSalvarEdicao
            add(getButtonCancelarEdicao()); // btnCancelarEdicao
        }
    }

    /*
     * ABAIXO SERÃO IMPLEMENTADOS OS COMPONENTES
     */

    public TextField<String> getTextFieldNome() {
        TextField<String> text = new TextField("nomeElegibilidade", new PropertyModel(this, "nome"));
        text.setOutputMarkupId(true);
        text.add(StringValidator.maximumLength(200));
        return text;
    }

    public TextArea<String> getTextAreaDescricao() {
        TextArea text = new TextArea<String>("descricaoElegibilidade", new PropertyModel(this, "descricao"));
        text.setLabel(Model.of("Descrição"));
        text.setOutputMarkupId(true);
        text.add(StringValidator.maximumLength(Constants.TAMANHO_CARACTERES_CAMPO_TEXT_AREA));

        return text;
    }

    public TextArea<String> getTextAreaVerificacao() {
        TextArea text = new TextArea<String>("verificacaoElegibilidade", new PropertyModel(this, "verificacao"));
        text.setLabel(Model.of("Forma de Verificação"));
        text.setOutputMarkupId(true);
        text.add(StringValidator.maximumLength(Constants.TAMANHO_CARACTERES_CAMPO_TEXT_AREA));

        return text;
    }
    
    private DropDownChoice<Boolean> newDropDownAnexoObrigatorio(){
        DropDownChoice<Boolean> dropDownChoice = new DropDownChoice<Boolean>("anexoObrigatorio",new PropertyModel<Boolean>(this, "possuiObrigatoriedadeDeAnexo"),Arrays.asList(Boolean.TRUE,Boolean.FALSE));
        dropDownChoice.setChoiceRenderer(new IChoiceRenderer<Boolean>() {
            private static final long serialVersionUID = 1L;

            public String getDisplayValue(Boolean object) {
                return object?"Sim":"Não";
            };
            
            public String getIdValue(Boolean object, int index) {
                return object.toString();
            };
        });
        return dropDownChoice;
    }

    public Button getButtonAdicionar() {
        buttonAdicionar = new AjaxButton("btnAdicionar") {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                adicionar(target);
            }
        };
        buttonAdicionar.setVisible(!modoEdicao);
        return buttonAdicionar;
    }

    public AjaxButton getButtonEditar(Item<ProgramaCriterioElegibilidade> item) {
        AjaxButton button = new AjaxButton("btnEditar") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form form) {
                editarCriterio(target, item);
            }
        };
        button.setEnabled(!modoEdicao);
        return button;
    }

    public InfraAjaxConfirmButton getButtonExcluir(Item<ProgramaCriterioElegibilidade> item) {
        InfraAjaxConfirmButton button = componentFactory.newAJaxConfirmButton("btnExcluir", "MSG001", null, (target, formz) -> excluirCriterio(target, item));
        button.setEnabled(!modoEdicao);
        return button;
    }

    public AjaxButton getButtonSalvarEdicao() {
        buttonSalvarEdicao = new AjaxButton("btnSalvarEdicao") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                salvarEdicao(target);
            }
        };
        buttonSalvarEdicao.setVisible(modoEdicao);
        return buttonSalvarEdicao;
    }

    public AjaxSubmitLink getButtonCancelarEdicao() {
        buttonCancelarEdicao = new AjaxSubmitLink("btnCancelarEdicao") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form form) {
                cancelarEdicao(target);
            }
        };
        buttonCancelarEdicao.setDefaultFormProcessing(false);
        buttonCancelarEdicao.setVisible(modoEdicao);
        return buttonCancelarEdicao;
    }

    public DataView<ProgramaCriterioElegibilidade> getDataViewBensAdicionados(CriterioProvider cp) {

        dataViewCriterio = new DataView<ProgramaCriterioElegibilidade>("dataViewCriterio", cp) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<ProgramaCriterioElegibilidade> item) {
                item.clearOriginalDestination();
                item.add(new Label("nomeCriterio", item.getModelObject().getNomeCriterioElegibilidade()));
                item.add(new Label("descricaoCriterio", item.getModelObject().getDescricaoCriterioElegibilidade()));
                item.add(new Label("verificacaoCriterio", item.getModelObject().getFormaVerificacao()));
                item.add(new Label("possuiObrigatoriedadeDeAnexo",item.getModelObject().getPossuiObrigatoriedadeDeAnexo()?"Sim":"Não"));
                item.add(getButtonEditar(item));
                item.add(getButtonExcluir(item));
            }
        };
        dataViewCriterio.setItemsPerPage(itensPorPagina);

        return dataViewCriterio;
    }

    public InfraAjaxPagingNavigator getPaginator() {
        if (paginator == null) {
            paginator = new InfraAjaxPagingNavigator(PAGINATOR, dataViewCriterio);
        }
        return paginator;
    }

    /*
     * AS AÇÕES SERÃO IMPLEMENTAS ABAIXO
     */

    public void salvarEdicao(AjaxRequestTarget target) {
        if (!validar(target)) {
            return;
        }

        modoEdicao = false;

        itemSelecionadoEditar.setNomeCriterioElegibilidade(nome);
        itemSelecionadoEditar.setDescricaoCriterioElegibilidade(descricao);
        itemSelecionadoEditar.setFormaVerificacao(verificacao);
        itemSelecionadoEditar.setPossuiObrigatoriedadeDeAnexo(possuiObrigatoriedadeDeAnexo);

        zerarCamposDadosBasicos();

        panelDadosBasicos.addOrReplace(getTextFieldNome());
        panelDadosBasicos.addOrReplace(getTextAreaDescricao());
        panelDadosBasicos.addOrReplace(getTextAreaVerificacao());

        panelDataViewCriterio.addOrReplace(getDataViewBensAdicionados(criterioProvider));
        panelBotoes.addOrReplace(getButtonAdicionar());
        panelBotoes.addOrReplace(getButtonSalvarEdicao());
        panelBotoes.addOrReplace(getButtonCancelarEdicao());

        target.add(panelDataViewCriterio);
        target.add(panelBotoes);
        target.add(panelDadosBasicos);

    }

    public void adicionar(AjaxRequestTarget target) {

        if (!validar(target)) {
            return;
        }

        ProgramaCriterioElegibilidade criterio = new ProgramaCriterioElegibilidade();
        criterio.setNomeCriterioElegibilidade(nome);
        criterio.setDescricaoCriterioElegibilidade(descricao);
        criterio.setFormaVerificacao(verificacao);
        criterio.setPossuiObrigatoriedadeDeAnexo(possuiObrigatoriedadeDeAnexo);
        listaCriterio.add(criterio);

        modoEdicao = false;
        zerarCamposDadosBasicos();

        panelDadosBasicos.addOrReplace(getTextFieldNome());
        panelDadosBasicos.addOrReplace(getTextAreaDescricao());
        panelDadosBasicos.addOrReplace(getTextAreaVerificacao());
        panelDadosBasicos.addOrReplace(getTextAreaVerificacao());
        
        panelBotoes.addOrReplace(getButtonAdicionar());
        panelBotoes.addOrReplace(getButtonSalvarEdicao());
        panelBotoes.addOrReplace(getButtonCancelarEdicao());

        panelDataViewCriterio.addOrReplace(getDataViewBensAdicionados(criterioProvider));
        panelDataViewCriterio.addOrReplace(new InfraAjaxPagingNavigator(PAGINATOR, dataViewCriterio));

        target.add(panelDataViewCriterio);
        target.add(panelDadosBasicos);
        target.add(panelBotoes);
    }

    public void editarCriterio(AjaxRequestTarget target, Item<ProgramaCriterioElegibilidade> item) {
        itemSelecionadoEditar = item.getModelObject();
        modoEdicao = true;

        nome = itemSelecionadoEditar.getNomeCriterioElegibilidade();
        descricao = itemSelecionadoEditar.getDescricaoCriterioElegibilidade();
        verificacao = itemSelecionadoEditar.getFormaVerificacao();
        possuiObrigatoriedadeDeAnexo = itemSelecionadoEditar.getPossuiObrigatoriedadeDeAnexo();

        panelDadosBasicos.addOrReplace(getTextFieldNome());
        panelDadosBasicos.addOrReplace(getTextAreaDescricao());
        panelDadosBasicos.addOrReplace(getTextAreaVerificacao());

        panelBotoes.addOrReplace(getButtonAdicionar());
        panelBotoes.addOrReplace(getButtonSalvarEdicao());
        panelBotoes.addOrReplace(getButtonCancelarEdicao());

        target.add(panelDadosBasicos);
        target.add(panelBotoes);
    }

    public void excluirCriterio(AjaxRequestTarget target, Item<ProgramaCriterioElegibilidade> item) {
        itemSelecionadoExcluir = item.getModelObject();

        if (listaCriterio.contains(itemSelecionadoExcluir)) {
            if (itemSelecionadoEditar != null) {
                if (itemSelecionadoEditar.equals(itemSelecionadoExcluir)) {
                    modoEdicao = false;

                    zerarCamposDadosBasicos();

                    panelDadosBasicos.addOrReplace(getTextFieldNome());
                    panelDadosBasicos.addOrReplace(getTextAreaDescricao());
                    panelDadosBasicos.addOrReplace(getTextAreaVerificacao());

                    panelBotoes.addOrReplace(getButtonAdicionar());
                    panelBotoes.addOrReplace(getButtonSalvarEdicao());
                    panelBotoes.addOrReplace(getButtonCancelarEdicao());

                    target.add(panelDadosBasicos);
                    target.add(panelBotoes);
                }
            }

            listaCriterio.remove(itemSelecionadoExcluir);
        }

        panelDataViewCriterio.addOrReplace(getDataViewBensAdicionados(criterioProvider));
        panelDataViewCriterio.addOrReplace(new InfraAjaxPagingNavigator(PAGINATOR, dataViewCriterio));

        target.add(panelDataViewCriterio);
    }

    public void cancelarEdicao(AjaxRequestTarget target) {
        modoEdicao = false;

        zerarCamposDadosBasicos();

        panelDadosBasicos.addOrReplace(getTextFieldNome());
        panelDadosBasicos.addOrReplace(getTextAreaDescricao());
        panelDadosBasicos.addOrReplace(getTextAreaVerificacao());
        panelDadosBasicos.addOrReplace(newDropDownAnexoObrigatorio());

        panelBotoes.addOrReplace(getButtonAdicionar());
        panelBotoes.addOrReplace(getButtonSalvarEdicao());
        panelBotoes.addOrReplace(getButtonCancelarEdicao());

        target.add(panelDadosBasicos);
        target.add(panelBotoes);
    }

    public void zerarCamposDadosBasicos() {
        nome = "";
        descricao = "";
        verificacao = "";
        possuiObrigatoriedadeDeAnexo = null;
    }

    public boolean validar(AjaxRequestTarget target) {
        boolean validar = true;

        String msg = "";
        if (nome == null || "".equalsIgnoreCase(nome)) {
            msg += "<p><li> Informe um 'Nome' para o critério.</li><p />";
            validar = false;
        }

        if (descricao == null || "".equalsIgnoreCase(descricao)) {
            msg += "<p><li> Informe uma 'Descrição' para o critério.</li><p />";
            validar = false;
        }

        if (verificacao == null || "".equalsIgnoreCase(verificacao)) {
            msg += "<p><li> Informe uma 'Forma de Verificação' para o critério.</li><p />";
            validar = false;
        }
        if (possuiObrigatoriedadeDeAnexo == null) {
            msg += "<p><li> Informe se é necessário 'Anexo Obrigatório' para o critério.</li><p/>";
            validar = false;
        }
        
        if (!validar) {
            mensagem.setObject(msg);
        } else {
            mensagem.setObject("");
        }
        target.add(labelMensagem);

        return validar;
    }

    /*
     * PROVIDER
     */

    private class CriterioProvider extends SortableDataProvider<ProgramaCriterioElegibilidade, String> {
        private static final long serialVersionUID = 1L;

        public CriterioProvider() {
            setSort("nomeCriterio", SortOrder.ASCENDING);
        }

        @Override
        public Iterator<ProgramaCriterioElegibilidade> iterator(long first, long size) {

            List<ProgramaCriterioElegibilidade> listTemp = new ArrayList<ProgramaCriterioElegibilidade>();
            int firstTemp = 0;
            int flagTemp = 0;
            for (ProgramaCriterioElegibilidade k : listaCriterio) {
                if (firstTemp >= first) {
                    if (flagTemp <= size) {
                        listTemp.add(k);
                        flagTemp++;
                    }
                }
                firstTemp++;
            }

            return listTemp.iterator();
        }

        @Override
        public long size() {
            return listaCriterio.size();
        }

        @Override
        public IModel<ProgramaCriterioElegibilidade> model(ProgramaCriterioElegibilidade object) {
            return new CompoundPropertyModel<ProgramaCriterioElegibilidade>(object);
        }
    }
}
