package br.gov.mj.side.web.view.programa.criterioAcompanhamento;

import java.util.ArrayList;
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
import org.apache.wicket.markup.html.form.Form;
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
import br.gov.mj.side.entidades.programa.ProgramaCriterioAcompanhamento;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.view.programa.ProgramaPage;

public class PanelCriterioAcompanhamentoPage extends Panel {
    private static final String PAGINATOR = "paginator";

    private static final long serialVersionUID = 1L;

    private Page backPage;

    private PanelPrincipal panelPrincipal;
    private PanelDados panelDadosBasicos;
    private PanelBotoes panelBotoes;
    private PanelDataViewCriterio panelDataViewCriterio;
    private List<ProgramaCriterioAcompanhamento> listaCriterio;
    private ProgramaCriterioAcompanhamento itemSelecionadoEditar;
    private ProgramaCriterioAcompanhamento itemSelecionadoExcluir;
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
    private boolean modoEdicao = false;
    private Model<String> mensagem = Model.of("");

    @Inject
    private ComponentFactory componentFactory;

    public PanelCriterioAcompanhamentoPage(String id, Page backPage, Boolean readOnly) {
        super(id);
        setOutputMarkupId(true);

        this.modoEdicao = readOnly;
        this.backPage = backPage;
        initVariaveis();
        panelPrincipal = new PanelPrincipal("panelPrincipal");

        add(panelPrincipal);
    }

    public void initVariaveis() {
        criterioProvider = new CriterioProvider();

        ProgramaPage page = (ProgramaPage) backPage;
        listaCriterio = page.getForm().getModelObject().getCriteriosAcompanhamento() != null ? page.getForm().getModelObject().getCriteriosAcompanhamento() : new ArrayList<ProgramaCriterioAcompanhamento>();
        labelMensagem = new Label("mensagem", mensagem);
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

            panelDadosBasicos.setEnabled(!modoEdicao);
            panelBotoes.setEnabled(!modoEdicao);
            
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

            add(getTextFieldNome());// nomeAcompanhamento
            add(getTextAreaDescricao());// descricaoAcompanhamento
            add(getTextAreaVerificacao()); // verificacaoAcompanhamento
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
        TextField<String> text = new TextField("nomeAcompanhamento", new PropertyModel(this, "nome"));
        text.setOutputMarkupId(true);
        text.add(StringValidator.maximumLength(200));
        return text;
    }

    public TextArea<String> getTextAreaDescricao() {
        TextArea text = new TextArea<String>("descricaoAcompanhamento", new PropertyModel(this, "descricao"));
        text.setLabel(Model.of("Descrição"));
        text.setOutputMarkupId(true);
        text.add(StringValidator.maximumLength(Constants.TAMANHO_CARACTERES_CAMPO_TEXT_AREA));

        return text;
    }

    public TextArea<String> getTextAreaVerificacao() {
        TextArea text = new TextArea<String>("verificacaoAcompanhamento", new PropertyModel(this, "verificacao"));
        text.setLabel(Model.of("Forma de Verificação"));
        text.setOutputMarkupId(true);
        text.add(StringValidator.maximumLength(Constants.TAMANHO_CARACTERES_CAMPO_TEXT_AREA));

        return text;
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

    public AjaxButton getButtonEditar(Item<ProgramaCriterioAcompanhamento> item) {
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

    public InfraAjaxConfirmButton getButtonExcluir(Item<ProgramaCriterioAcompanhamento> item) {
        InfraAjaxConfirmButton button =  componentFactory.newAJaxConfirmButton("btnExcluir", "MSG001", null, (target, formz) -> excluirCriterio(target, item));
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
        buttonCancelarEdicao = new AjaxSubmitLink("btnCancelarEdicaoCriterio") {
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

    public DataView<ProgramaCriterioAcompanhamento> getDataViewBensAdicionados(CriterioProvider cp) {

        dataViewCriterio = new DataView<ProgramaCriterioAcompanhamento>("dataViewCriterio", cp) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<ProgramaCriterioAcompanhamento> item) {
                item.clearOriginalDestination();
                item.add(new Label("nomeCriterio", item.getModelObject().getNomeCriterioAcompanhamento()));
                item.add(new Label("descricaoCriterio", item.getModelObject().getDescricaoCriterioAcompanhamento()));
                item.add(new Label("verificacaoCriterio", item.getModelObject().getFormaAcompanhamento()));
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

        itemSelecionadoEditar.setNomeCriterioAcompanhamento(nome);
        itemSelecionadoEditar.setDescricaoCriterioAcompanhamento(descricao);
        itemSelecionadoEditar.setFormaAcompanhamento(verificacao);

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

        ProgramaCriterioAcompanhamento criterio = new ProgramaCriterioAcompanhamento();
        criterio.setNomeCriterioAcompanhamento(nome);
        criterio.setDescricaoCriterioAcompanhamento(descricao);
        criterio.setFormaAcompanhamento(verificacao);
        listaCriterio.add(criterio);

        modoEdicao = false;
        zerarCamposDadosBasicos();

        panelDadosBasicos.addOrReplace(getTextFieldNome());
        panelDadosBasicos.addOrReplace(getTextAreaDescricao());
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

    public void editarCriterio(AjaxRequestTarget target, Item<ProgramaCriterioAcompanhamento> item) {
        itemSelecionadoEditar = item.getModelObject();
        modoEdicao = true;

        nome = itemSelecionadoEditar.getNomeCriterioAcompanhamento();
        descricao = itemSelecionadoEditar.getDescricaoCriterioAcompanhamento();
        verificacao = itemSelecionadoEditar.getFormaAcompanhamento();

        panelDadosBasicos.addOrReplace(getTextFieldNome());
        panelDadosBasicos.addOrReplace(getTextAreaDescricao());
        panelDadosBasicos.addOrReplace(getTextAreaVerificacao());

        panelBotoes.addOrReplace(getButtonAdicionar());
        panelBotoes.addOrReplace(getButtonSalvarEdicao());
        panelBotoes.addOrReplace(getButtonCancelarEdicao());

        target.add(panelDadosBasicos);
        target.add(panelBotoes);
    }

    public void excluirCriterio(AjaxRequestTarget target, Item<ProgramaCriterioAcompanhamento> item) {
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
            msg += "<p><li> Informe uma 'Forma de Acompanhamento' para o critério.</li><p />";
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

    private class CriterioProvider extends SortableDataProvider<ProgramaCriterioAcompanhamento, String> {
        private static final long serialVersionUID = 1L;

        public CriterioProvider() {
            setSort("nomeCriterio", SortOrder.ASCENDING);
        }

        @Override
        public Iterator<ProgramaCriterioAcompanhamento> iterator(long first, long size) {

            List<ProgramaCriterioAcompanhamento> listTemp = new ArrayList<ProgramaCriterioAcompanhamento>();
            int firstTemp = 0;
            int flagTemp = 0;
            for (ProgramaCriterioAcompanhamento k : listaCriterio) {
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
        public IModel<ProgramaCriterioAcompanhamento> model(ProgramaCriterioAcompanhamento object) {
            return new CompoundPropertyModel<ProgramaCriterioAcompanhamento>(object);
        }
    }
}
