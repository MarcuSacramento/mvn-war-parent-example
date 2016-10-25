package br.gov.mj.side.web.view.programa.contrato.formatacaoitens;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraAjaxConfirmButton;
import br.gov.mj.infra.wicket.component.pagination.InfraAjaxPagingNavigator;
import br.gov.mj.infra.wicket.model.LambdaModel;
import br.gov.mj.side.entidades.enums.EnumFormaVerificacaoFormatacao;
import br.gov.mj.side.entidades.enums.EnumResponsavelPreencherFormatacaoItem;
import br.gov.mj.side.entidades.enums.EnumTipoCampoFormatacao;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.licitacao.contrato.Contrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.FormatacaoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.FormatacaoItensContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.ItensFormatacao;
import br.gov.mj.side.web.service.FormatacaoItensContratoService;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.view.planejarLicitacao.ContratoPanelBotoes;
import br.gov.mj.side.web.view.programa.PanelFasePrograma;
import br.gov.mj.side.web.view.template.TemplatePage;

public class FormatacaoItensPage extends TemplatePage {

    private static final long serialVersionUID = 1L;

    private static final String ONCHANGE = "onchange";
    private Integer itensPorPaginaItensContrato = Constants.ITEMS_PER_PAGE_PAGINATION;
    private Integer itensPorPaginaFormatacao = Constants.ITEMS_PER_PAGE_PAGINATION;
    private static final String CLASS = "class";

    private AttributeAppender classArrow = new AttributeAppender(CLASS, "mj_arrow_asc", " ");
    private AttributeAppender classArrowUnsorted = new AttributeAppender(CLASS, "mj_arrow_unsorted", " ");

    private Page backPage;
    private Programa programa;
    private List<ItensFormatacao> listaItensFormatacao;
    private Boolean isListaSelecionada;
    private Contrato contrato;
    private FormatacaoContrato formatacaoContrato;
    private List<FormatacaoItensContrato> listaFormatacaoItensContrato = new ArrayList<FormatacaoItensContrato>();

    private Form<FormatacaoContrato> form;
    private FormatacaoItensContrato formatacaoItensContrato;
    private EnumFormaVerificacaoFormatacao listaFormaVerificacao;
    private EnumTipoCampoFormatacao listaTipoCampoFormatacao;
    private DropDownChoice<EnumTipoCampoFormatacao> dropEnumTipoCampoFormatacao;
    private DropDownChoice<EnumResponsavelPreencherFormatacaoItem> dropResponsavelPreencher;
    private List<ItensFormatacao> listaItensJaSelecionados = new ArrayList<ItensFormatacao>();
    private String coluna = "item";
    private int order = 1;

    private PanelFasePrograma panelFasePrograma;
    private ContratoPanelBotoes execucaoPanelBotoes;
    private PanelFormatacaoItens panelFormatacaoItens;
    private PanelTiposDeCampo panelTiposDeCampo;
    private PanelIdentificadorUnico panelIdentificadorUnico;
    private PanelDispositivoMovel panelDispositivoMovel;
    private PanelOutrasOpcoes panelOutrasOpcoes;
    private PanelListaItensContrato panelListaItensContrato;
    private PanelListaFormatacoes panelListaFormatacoes;

    private DataView<ItensFormatacao> dataViewListaItem;
    private DataView<FormatacaoItensContrato> dataViewListaFormatacaoContrato;

    @Inject
    private ComponentFactory componentFactory;
    @Inject
    private FormatacaoItensContratoService formatacaoItensContratoService;

    public FormatacaoItensPage(PageParameters pageParameters, Page backPage, Programa programa, List<ItensFormatacao> listaItensFormatacao, FormatacaoContrato formatacaoContrato, Boolean isListaSelecionada, Contrato contrato) {
        super(pageParameters);

        this.backPage = backPage;
        this.programa = programa;
        this.listaItensFormatacao = listaItensFormatacao;
        this.isListaSelecionada = isListaSelecionada;
        this.contrato = contrato;
        this.formatacaoContrato = formatacaoContrato;

        setTitulo("Gerenciar Programa");
        initComponents();
        initVariaveis();
    }

    private void initVariaveis() {
        if (formatacaoContrato.getId() != null) {
            listaFormatacaoItensContrato = formatacaoItensContratoService.buscarFormatacaoItensContrato(formatacaoContrato);
        }
    }

    private void initComponents() {
        form = componentFactory.newForm("form", new CompoundPropertyModel<FormatacaoContrato>(formatacaoContrato));

        form.add(panelFasePrograma = new PanelFasePrograma("panelFasePrograma", programa, backPage));
        form.add(execucaoPanelBotoes = new ContratoPanelBotoes("execucaoPanelPotoes", programa, backPage, "formatacaoItens"));
        form.add(panelFormatacaoItens = new PanelFormatacaoItens("panelFormatacao"));

        form.add(panelTiposDeCampo = new PanelTiposDeCampo("panelTiposDeCampo"));
        panelTiposDeCampo.setVisible(formatacaoItensContrato != null);

        form.add(panelListaItensContrato = new PanelListaItensContrato("panelListaItensContrato"));
        form.add(panelListaFormatacoes = new PanelListaFormatacoes("panelListaFormatacoesContrato"));

        form.add(newButtonVoltar());
        form.add(newButtonSalvar());

        add(form);
    }

    // Paineis
    private class PanelListaFormatacoes extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelListaFormatacoes(String id) {
            super(id);
            setOutputMarkupId(true);

            SortableFormatacaoItensDataProvider dp = new SortableFormatacaoItensDataProvider();
            dataViewListaFormatacaoContrato = newDataViewListaFormatacao(dp);

            add(newDropItensPorPaginaFormatacao());
            add(dataViewListaFormatacaoContrato);

            add(new InfraAjaxPagingNavigator("paginationFormatacao", dataViewListaFormatacaoContrato));
        }

    }

    private class PanelFormatacaoItens extends WebMarkupContainer {

        private static final long serialVersionUID = 1L;

        public PanelFormatacaoItens(String id) {
            super(id);
            setOutputMarkupId(true);

            add(newDropDownFormaVerificacao());
            add(newDropDownTipoCampoFormatacao());
            add(newDropDownResponsavelPreencher());
            add(newTextFieldTituloQuesito());
            add(newTextFieldOrientacaoFornecedor());
        }

    }

    private class PanelTiposDeCampo extends WebMarkupContainer {

        private static final long serialVersionUID = 1L;

        public PanelTiposDeCampo(String id) {
            super(id);
            setOutputMarkupId(true);

            panelIdentificadorUnico = new PanelIdentificadorUnico("panelIdentificadorUnico");
            panelDispositivoMovel = new PanelDispositivoMovel("panelDispositivoMovel");
            panelOutrasOpcoes = new PanelOutrasOpcoes("panelOutrasOpcoes");

            add(newCheckboxInformacaoOpcional());
            add(panelIdentificadorUnico);
            add(panelDispositivoMovel);
            add(panelOutrasOpcoes);
            add(newButtonAdicionar());
            add(newButtonCancelar());
        }

    }

    private class PanelIdentificadorUnico extends WebMarkupContainer {

        private static final long serialVersionUID = 1L;

        public PanelIdentificadorUnico(String id) {
            super(id);
            setOutputMarkupId(true);

            add(newCheckboxIdentificacaoUnica());
        }

    }

    private class PanelDispositivoMovel extends WebMarkupContainer {

        private static final long serialVersionUID = 1L;

        public PanelDispositivoMovel(String id) {
            super(id);
            setOutputMarkupId(true);

            add(newCheckboxDispositivelMovel());
        }

    }

    private class PanelOutrasOpcoes extends WebMarkupContainer {

        private static final long serialVersionUID = 1L;

        public PanelOutrasOpcoes(String id) {
            super(id);
            setOutputMarkupId(true);

            add(newCheckboxGps());
            add(newCheckboxData());
        }

    }

    private class PanelListaItensContrato extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelListaItensContrato(String id) {
            super(id);
            setOutputMarkupId(true);

            ProviderItensSelecionados dp = new ProviderItensSelecionados();
            dataViewListaItem = newDataViewListaItens(dp);
            add(newCheckBoxSelecionarTudo());
            add(dataViewListaItem);
            add(newDropItensPorPaginaItens());

            add(newLabelSortNome());
            add(newLabelSortDescricao());
            add(newButtonOrdenarNome());
            add(newButtonOrdenarDescricao());

            add(new InfraAjaxPagingNavigator("paginationItens", dataViewListaItem));
        }
    }

    // Componentes
    private AjaxCheckBox newCheckBoxSelecionarTudo() {
        AjaxCheckBox check = new AjaxCheckBox("groupSelector", new PropertyModel<Boolean>(this, "isListaSelecionada")) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                selecionaLista(target);
            }

        };
        return check;
    }

    private AjaxSubmitLink newButtonAdicionar() {
        AjaxSubmitLink btnAdicionar = new AjaxSubmitLink("btnAdicionar") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                adicionarFormatacaoLista(target);
            }
        };
        btnAdicionar.add(componentFactory.newLabel("txtBtnAdicionarAlterar", formatacaoContrato.getId() == null ? "Adicionar" : "Alterar"));
        btnAdicionar.setOutputMarkupId(Boolean.TRUE);
        return btnAdicionar;
    }

    private AjaxSubmitLink newButtonCancelar() {
        AjaxSubmitLink btnCancelar = new AjaxSubmitLink("btnCancelar") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit();
                limparFormulario(target);
            }
        };
        btnCancelar.setOutputMarkupId(Boolean.TRUE);
        return btnCancelar;
    }

    private InfraAjaxConfirmButton newButtonExcluir(Item<FormatacaoItensContrato> item) {
        return componentFactory.newAJaxConfirmButton("btnExcluir", "MT029", form, (target, formz) -> acaoExcluir(target, item));
    }

    private AjaxSubmitLink newButtonEditar(Item<FormatacaoItensContrato> item) {
        AjaxSubmitLink btnEditar = new AjaxSubmitLink("btnAlterar") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                acaoEditar(target, item);
            }
        };
        btnEditar.setDefaultFormProcessing(false);
        btnEditar.setOutputMarkupId(true);
        return btnEditar;
    }

    private AjaxCheckBox newCheckBoxItem() {
        AjaxCheckBox check = new AjaxCheckBox("groupSelector", new PropertyModel<Boolean>(this, "isListaSelecionada")) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                selecionaLista(target);
            }

        };
        return check;
    }

    private CheckBox newCheckboxData() {
        return new CheckBox("possuiData", new PropertyModel<Boolean>(this, "formatacaoItensContrato.possuiData"));
    }

    private CheckBox newCheckboxGps() {
        return new CheckBox("possuiGPS", new PropertyModel<Boolean>(this, "formatacaoItensContrato.possuiGPS"));
    }

    private CheckBox newCheckboxDispositivelMovel() {
        return new CheckBox("possuiDispositivoMovel", new PropertyModel<Boolean>(this, "formatacaoItensContrato.possuiDispositivoMovel"));
    }

    private CheckBox newCheckboxInformacaoOpcional() {

        Boolean habilita = Boolean.TRUE;
        if (formatacaoItensContrato != null) {
            if (formatacaoItensContrato.getPossuiInformacaoOpcional() != null) {
                habilita = !formatacaoItensContrato.getPossuiIdentificadorUnico();
            }
        }

        CheckBox checkBox = new CheckBox("possuiInformacaoOpcional", new PropertyModel<Boolean>(this, "formatacaoItensContrato.possuiInformacaoOpcional"));
        checkBox.setEnabled(habilita);
        checkBox.setOutputMarkupId(Boolean.TRUE);
        return checkBox;
    }

    private AjaxCheckBox newCheckboxIdentificacaoUnica() {
        AjaxCheckBox check = new AjaxCheckBox("possuiIdentificadorUnico", new PropertyModel<Boolean>(this, "formatacaoItensContrato.possuiIdentificadorUnico")) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                alteraParaObrigatorio(target);
            }

        };
        return check;
    }

    private Button newButtonSalvar() {
        Button btnSalvar = componentFactory.newButton("btnSalvar", () -> salvar());
        btnSalvar.setDefaultFormProcessing(false);
        return btnSalvar;
    }

    private Button newButtonVoltar() {
        Button buttonVoltar = componentFactory.newButton("btnVoltar", () -> voltar());
        buttonVoltar.setDefaultFormProcessing(false);
        return buttonVoltar;
    }

    private DropDownChoice<EnumFormaVerificacaoFormatacao> newDropDownFormaVerificacao() {
        DropDownChoice<EnumFormaVerificacaoFormatacao> drop = new DropDownChoice<EnumFormaVerificacaoFormatacao>("formaVerificacao", new PropertyModel<EnumFormaVerificacaoFormatacao>(this, "formatacaoItensContrato.formaVerificacao"), Arrays.asList(EnumFormaVerificacaoFormatacao.values()));
        drop.setChoiceRenderer(new ChoiceRenderer<EnumFormaVerificacaoFormatacao>("descricao", "valor"));
        drop.setRequired(true);
        drop.setNullValid(true);
        drop.setOutputMarkupId(true);
        drop.setLabel(Model.of("Forma de Verificação"));
        return drop;
    }

    private DropDownChoice<EnumResponsavelPreencherFormatacaoItem> newDropDownResponsavelPreencher() {
        dropResponsavelPreencher = new DropDownChoice<EnumResponsavelPreencherFormatacaoItem>("responsavelPreencher", new PropertyModel<EnumResponsavelPreencherFormatacaoItem>(this, "formatacaoItensContrato.responsavelFormatacao"), Arrays.asList(EnumResponsavelPreencherFormatacaoItem.values()));
        dropResponsavelPreencher.setRequired(true);
        dropResponsavelPreencher.setChoiceRenderer(new ChoiceRenderer<EnumResponsavelPreencherFormatacaoItem>("descricao", "valor"));
        dropResponsavelPreencher.setNullValid(true);
        dropResponsavelPreencher.setLabel(Model.of("Responsável por preencher esta verificação"));
        return dropResponsavelPreencher;
    }

    private DropDownChoice<EnumTipoCampoFormatacao> newDropDownTipoCampoFormatacao() {
        dropEnumTipoCampoFormatacao = new DropDownChoice<EnumTipoCampoFormatacao>("tipoCampo", new PropertyModel<EnumTipoCampoFormatacao>(this, "formatacaoItensContrato.tipoCampo"), Arrays.asList(EnumTipoCampoFormatacao.values()));
        dropEnumTipoCampoFormatacao.setRequired(true);
        dropEnumTipoCampoFormatacao.setChoiceRenderer(new ChoiceRenderer<EnumTipoCampoFormatacao>("descricao", "valor"));
        dropEnumTipoCampoFormatacao.setNullValid(true);
        dropEnumTipoCampoFormatacao.setLabel(Model.of("Tipo de Campo"));
        actionDropDown(dropEnumTipoCampoFormatacao);
        return dropEnumTipoCampoFormatacao;
    }

    private TextField<String> newTextFieldTituloQuesito() {
        TextField<String> fieldTituloQuesito = componentFactory.newTextField("tituloQuesito", "Título do Quesito", true, new PropertyModel<String>(this, "formatacaoItensContrato.tituloQuesito"));
        fieldTituloQuesito.setOutputMarkupId(true);
        return fieldTituloQuesito;
    }

    private TextField<String> newTextFieldOrientacaoFornecedor() {
        TextField<String> textField = componentFactory.newTextField("orientacaoFornecedores", "Orientação para o Fornecedor", true, new PropertyModel<String>(this, "formatacaoItensContrato.orientacaoFornecedores"));
        textField.setOutputMarkupId(true);
        return textField;
    }

    private DropDownChoice<Integer> newDropItensPorPaginaItens() {
        DropDownChoice<Integer> dropDownChoice = new DropDownChoice<Integer>("itensPorPaginaItens", new LambdaModel<Integer>(this::getItensPorPaginaItensContrato, this::setItensPorPaginaItensContrato), Constants.QUANTIDADE_ITENS_TABELA);
        dropDownChoice.add(new AjaxFormComponentUpdatingBehavior(ONCHANGE) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                dataViewListaItem.setItemsPerPage(getItensPorPaginaItensContrato());
                target.add(panelListaItensContrato);
            };
        });
        dropDownChoice.setOutputMarkupId(Boolean.TRUE);
        return dropDownChoice;
    }

    private DropDownChoice<Integer> newDropItensPorPaginaFormatacao() {
        DropDownChoice<Integer> dropDownChoice = new DropDownChoice<Integer>("itensPorPaginaFormatacao", new LambdaModel<Integer>(this::getItensPorPaginaFormatacao, this::setItensPorPaginaFormatacao), Constants.QUANTIDADE_ITENS_TABELA);
        dropDownChoice.add(new AjaxFormComponentUpdatingBehavior(ONCHANGE) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                dataViewListaFormatacaoContrato.setItemsPerPage(getItensPorPaginaFormatacao());
                target.add(panelListaFormatacoes);
            };
        });
        return dropDownChoice;
    }

    private Label newLabelSortNome() {
        Label label = null;
        label = new Label("lblOrderNome", "...");

        if ("item".equalsIgnoreCase(coluna)) {
            label.add(classArrow);
        } else {
            label.add(classArrowUnsorted);
        }
        return label;
    }

    private Label newLabelSortDescricao() {
        Label label = null;
        label = new Label("lblOrderDescricao", "...");

        if ("descricao".equalsIgnoreCase(coluna)) {
            label.add(classArrow);
        } else {
            label.add(classArrowUnsorted);
        }
        return label;
    }

    private AjaxSubmitLink newButtonOrdenarNome() {
        AjaxSubmitLink btnOrdenar = new AjaxSubmitLink("btnOrdenarNome", form) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form) {
                coluna = "item";
                mudarOrdemTabela();
                atualizarSetasOrdenacao(target);
            }
        };
        btnOrdenar.setDefaultFormProcessing(false);
        return btnOrdenar;
    }

    private AjaxSubmitLink newButtonOrdenarDescricao() {
        AjaxSubmitLink btnOrdenar = new AjaxSubmitLink("btnOrdenarDescricao", form) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form) {
                coluna = "descricao";
                mudarOrdemTabelaDescricao();
                atualizarSetasOrdenacao(target);
            }
        };
        btnOrdenar.setDefaultFormProcessing(false);
        return btnOrdenar;
    }

    // acoes

    // atualiza as sentas da ordenacao da lista de itens para formatacao
    private void atualizarSetasOrdenacao(AjaxRequestTarget target) {

        ProviderItensSelecionados dp = new ProviderItensSelecionados();
        dataViewListaItem = newDataViewListaItens(dp);

        panelListaItensContrato.addOrReplace(newCheckBoxSelecionarTudo());
        panelListaItensContrato.addOrReplace(dataViewListaItem);
        panelListaItensContrato.addOrReplace(newDropItensPorPaginaItens());
        panelListaItensContrato.addOrReplace(newLabelSortNome());
        panelListaItensContrato.addOrReplace(newLabelSortDescricao());
        panelListaItensContrato.addOrReplace(newButtonOrdenarNome());
        panelListaItensContrato.addOrReplace(newButtonOrdenarDescricao());
        panelListaItensContrato.addOrReplace(new InfraAjaxPagingNavigator("paginationItens", dataViewListaItem));
        target.add(panelListaItensContrato);
    }

    // altera a ordenacao da tabela de itens pelo nome do item
    private void mudarOrdemTabela() {
        if (order == 1) {
            order = -1;
            classArrow = new AttributeAppender(CLASS, "mj_arrow_desc", " ");
            listaItensFormatacao.sort(Collections.reverseOrder((ItensFormatacao o1, ItensFormatacao o2) -> o1.getItem().getNomeBem().compareToIgnoreCase(o2.getItem().getNomeBem())));
        } else {
            order = 1;
            classArrow = new AttributeAppender(CLASS, "mj_arrow_asc", " ");
            listaItensFormatacao.sort((ItensFormatacao o1, ItensFormatacao o2) -> o1.getItem().getNomeBem().compareToIgnoreCase(o2.getItem().getNomeBem()));
        }
    }

    // altera a ordenacao da tabela de itens pela descricao do item
    private void mudarOrdemTabelaDescricao() {
        if (order == 1) {
            order = -1;
            classArrow = new AttributeAppender(CLASS, "mj_arrow_desc", " ");
            listaItensFormatacao.sort(Collections.reverseOrder((ItensFormatacao o1, ItensFormatacao o2) -> o1.getItem().getDescricaoBem().compareToIgnoreCase(o2.getItem().getDescricaoBem())));
        } else {
            order = 1;
            classArrow = new AttributeAppender(CLASS, "mj_arrow_asc", " ");
            listaItensFormatacao.sort((ItensFormatacao o1, ItensFormatacao o2) -> o1.getItem().getDescricaoBem().compareToIgnoreCase(o2.getItem().getDescricaoBem()));
        }
    }

    // limpa formulario quando clica no botao CANCELAR
    private void limparFormulario(AjaxRequestTarget target) {
        formatacaoItensContrato = new FormatacaoItensContrato();
        target.appendJavaScript("atualizaCssDropDown();");
        target.add(panelListaFormatacoes, panelFormatacaoItens, panelTiposDeCampo);
    }

    // adiciona a formatacao na lista de formatacoes para ser persistida
    private void adicionarFormatacaoLista(AjaxRequestTarget target) {
        validaCheckBox();
        removeFormatacaoLista();
        formatacaoContrato.setItens(listaItensJaSelecionados);
        listaFormatacaoItensContrato.add(formatacaoItensContrato);
        formatacaoItensContrato = new FormatacaoItensContrato();
        panelTiposDeCampo.setVisible(false);
        target.appendJavaScript("atualizaCssDropDown();");
        target.add(panelListaFormatacoes, panelFormatacaoItens, panelTiposDeCampo);
    }

    // remove a formatacao da lista de formatacoes a ser persistida
    private void removeFormatacaoLista() {
        for (int i = 0; i < listaFormatacaoItensContrato.size(); i++) {
            if (formatacaoItensContrato.equals(listaFormatacaoItensContrato.get(i))) {
                listaFormatacaoItensContrato.remove(i);
            }
        }
    }

    // acao do botao EXCLUIR, para excluir a formatacao da lista
    private void acaoExcluir(AjaxRequestTarget target, Item<FormatacaoItensContrato> item) {
        for (int i = 0; i < listaFormatacaoItensContrato.size(); i++) {
            if (item.getModelObject().equals(listaFormatacaoItensContrato.get(i))) {
                listaFormatacaoItensContrato.remove(i);
            }
        }
        getSession().info("Formatação Removido com Sucesso!");
        target.add(panelListaFormatacoes);
    }

    // acao do botao EDITAR, para editar a formatacao da lista
    private void acaoEditar(AjaxRequestTarget target, Item<FormatacaoItensContrato> item) {
        formatacaoItensContrato = item.getModelObject();

        panelFormatacaoItens.addOrReplace(newDropDownFormaVerificacao());
        panelFormatacaoItens.addOrReplace(newDropDownTipoCampoFormatacao());
        panelFormatacaoItens.addOrReplace(newDropDownResponsavelPreencher());
        panelFormatacaoItens.addOrReplace(newTextFieldTituloQuesito());
        panelFormatacaoItens.addOrReplace(newTextFieldOrientacaoFornecedor());

        panelTiposDeCampo.addOrReplace(newCheckboxInformacaoOpcional());
        panelIdentificadorUnico.addOrReplace(newCheckboxIdentificacaoUnica());
        panelDispositivoMovel.addOrReplace(newCheckboxDispositivelMovel());
        panelOutrasOpcoes.addOrReplace(newCheckboxGps());
        panelOutrasOpcoes.addOrReplace(newCheckboxData());
        panelTiposDeCampo.addOrReplace(newButtonAdicionar());
        panelTiposDeCampo.setVisible(true);

        exibirCheckBoxModoEdicao(item.getModelObject().getTipoCampo());

        target.appendJavaScript("ancorarResultadoPesquisa('#ancoraPesquisa');");
        target.appendJavaScript("atualizaCssDropDown();");
        target.add(panelFormatacaoItens, panelTiposDeCampo);
    }

    // metodo do checkbox para marcar ou desmarcar a lista de objetos
    private void selecionaLista(AjaxRequestTarget target) {
        for (ItensFormatacao itemF : listaItensFormatacao) {
            if (isListaSelecionada) {
                itemF.getItem().setItemSelecionadoFormatacao(Boolean.TRUE);
            } else {
                itemF.getItem().setItemSelecionadoFormatacao(Boolean.FALSE);
            }
        }
        panelListaItensContrato.addOrReplace(newCheckBoxItem());
        target.add(panelListaItensContrato);
    }

    // altera para obrigatorio a formatacao, caso seja selecionado a opcao
    // IDENTIFICADOR UNICO
    private void alteraParaObrigatorio(AjaxRequestTarget target) {

        if (formatacaoItensContrato.getPossuiIdentificadorUnico()) {
            formatacaoItensContrato.setPossuiInformacaoOpcional(Boolean.FALSE);
        } else {
            formatacaoItensContrato.setPossuiInformacaoOpcional(null);
        }

        panelTiposDeCampo.addOrReplace(newCheckboxInformacaoOpcional());
        target.add(panelTiposDeCampo);
    }

    // marca ou desmarca o objeto do check clicado
    private void selecionarItemParaFormatacao(Item<ItensFormatacao> item) {
        Boolean valorItem = item.getModelObject().getItem().getItemSelecionadoFormatacao();

        if (valorItem == null) {
            item.getModelObject().getItem().setItemSelecionadoFormatacao(Boolean.TRUE);
        } else if (valorItem == false) {
            item.getModelObject().getItem().setItemSelecionadoFormatacao(Boolean.TRUE);
        } else if (valorItem == true) {
            item.getModelObject().getItem().setItemSelecionadoFormatacao(Boolean.FALSE);
        }
    }

    private void salvar() {

        if (!listaFormatacaoItensContrato.isEmpty() && listaFormatacaoItensContrato.size() > 0) {
            adicionaItensParaFormatacao();
            boolean validaFornecedoreBeneficiario = validaFornecedorBeneficiario();

            if (validaFornecedoreBeneficiario) {
                if (!listaItensJaSelecionados.isEmpty() && listaItensJaSelecionados.size() > 0) {
                    formatacaoContrato.setContrato(contrato);
                    formatacaoContrato.setListaItensFormatacao(listaFormatacaoItensContrato);
                    formatacaoContrato.setItens(listaItensJaSelecionados);

                    boolean insert = formatacaoContrato.getId() == null ? true : false;
                    formatacaoContrato = formatacaoItensContratoService.incluirAlterar(formatacaoContrato, getIdentificador());
                    listaFormatacaoItensContrato = formatacaoContrato.getListaItensFormatacao();
                    if (insert) {
                        getSession().info("Formatação cadastrada com sucesso.");
                    } else {
                        getSession().info("Formatação alterada com sucesso.");
                    }
                } else {
                    getSession().error("Por favor, selecione um item para formatação.");
                }
            } else {
                getSession().error("Por favor, preencha pelo menos uma formatação para o Fornecedor e Beneficiário");
            }

        } else {
            getSession().error("Lista de formatações está vazia.");
        }
    }

    private boolean validaFornecedorBeneficiario() {
        boolean validar = Boolean.TRUE;
        int countBeneficiario = 0;
        int countFornecedor = 0;
        int countAmbos = 0;
        for (FormatacaoItensContrato fomatacaoItens : listaFormatacaoItensContrato) {
            if (fomatacaoItens.getResponsavelFormatacao().equals(EnumResponsavelPreencherFormatacaoItem.AMBOS)) {
                countAmbos++;
                break;
            } else if (fomatacaoItens.getResponsavelFormatacao().equals(EnumResponsavelPreencherFormatacaoItem.BENEFICIARIO)) {
                countBeneficiario++;
            } else if (fomatacaoItens.getResponsavelFormatacao().equals(EnumResponsavelPreencherFormatacaoItem.FORNECEDOR)) {
                countFornecedor++;
            }
        }

        if ((countBeneficiario > 0) && (countFornecedor > 0) || countAmbos > 0) {
            validar = Boolean.TRUE;
        } else {
            validar = Boolean.FALSE;
        }

        return validar;
    }

    // separa os itens para formatacao selecionados para ser persistido
    private void adicionaItensParaFormatacao() {
        listaItensJaSelecionados.clear();
        for (ItensFormatacao itemF : listaItensFormatacao) {
            if (Boolean.TRUE.equals(itemF.getItem().getItemSelecionadoFormatacao())) {
                listaItensJaSelecionados.add(itemF);
            }
        }
    }

    // altera o valor dos checkboxs para FALSE caso nao tenha sido selecionado
    private void validaCheckBox() {
        if (formatacaoItensContrato.getPossuiData() == null) {
            formatacaoItensContrato.setPossuiData(Boolean.FALSE);
        }

        if (formatacaoItensContrato.getPossuiDispositivoMovel() == null) {
            formatacaoItensContrato.setPossuiDispositivoMovel(Boolean.FALSE);
        }

        if (formatacaoItensContrato.getPossuiGPS() == null) {
            formatacaoItensContrato.setPossuiGPS(Boolean.FALSE);
        }

        if (formatacaoItensContrato.getPossuiIdentificadorUnico() == null) {
            formatacaoItensContrato.setPossuiIdentificadorUnico(Boolean.FALSE);
        }

        if (formatacaoItensContrato.getPossuiInformacaoOpcional() == null) {
            formatacaoItensContrato.setPossuiInformacaoOpcional(Boolean.FALSE);
        }
    }

    // acao para validar o tipo de campo selecionado e exibir/enibir o painel
    // com os checkbox de tipos
    private void actionDropDown(DropDownChoice<EnumTipoCampoFormatacao> drop) {
        drop.add(new AjaxFormComponentUpdatingBehavior(ONCHANGE) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                alterarValoresCheckBox();
                exibirCheckBox(drop);

                panelTiposDeCampo.addOrReplace(newCheckboxInformacaoOpcional());
                panelTiposDeCampo.setVisible(Boolean.TRUE);
                target.add(panelTiposDeCampo);
            }
        });
    }

    // altera o valor dos checkbox quando alterar o valor do drop down com os
    // tipos de campo
    private void alterarValoresCheckBox() {
        formatacaoItensContrato.setPossuiInformacaoOpcional(null);
        formatacaoItensContrato.setPossuiIdentificadorUnico(null);
        formatacaoItensContrato.setPossuiDispositivoMovel(null);
        formatacaoItensContrato.setPossuiData(null);
        formatacaoItensContrato.setPossuiGPS(null);
        newCheckboxInformacaoOpcional();
    }

    // exibe o painel com os checkboxs com os tipos de acordo com a opcao
    // selecionada no drop down do tipo de campo
    private void exibirCheckBox(DropDownChoice<EnumTipoCampoFormatacao> drop) {
        if (drop.getValue() == EnumTipoCampoFormatacao.ALFANUMERICO.getValor()) {
            panelIdentificadorUnico.setVisible(true);
            panelDispositivoMovel.setVisible(false);
            panelOutrasOpcoes.setVisible(false);
        }

        if (drop.getValue() == EnumTipoCampoFormatacao.ANEXO.getValor()) {
            panelIdentificadorUnico.setVisible(false);
            panelDispositivoMovel.setVisible(false);
            panelOutrasOpcoes.setVisible(false);
        }

        if (drop.getValue() == EnumTipoCampoFormatacao.BOLEANO.getValor()) {
            panelIdentificadorUnico.setVisible(false);
            panelDispositivoMovel.setVisible(false);
            panelOutrasOpcoes.setVisible(false);
        }

        if (drop.getValue() == EnumTipoCampoFormatacao.DATA.getValor()) {
            panelIdentificadorUnico.setVisible(false);
            panelDispositivoMovel.setVisible(false);
            panelOutrasOpcoes.setVisible(false);
        }

        if (drop.getValue() == EnumTipoCampoFormatacao.FOTO.getValor()) {
            panelIdentificadorUnico.setVisible(true);
            panelDispositivoMovel.setVisible(true);
            panelOutrasOpcoes.setVisible(true);
        }

        if (drop.getValue() == EnumTipoCampoFormatacao.NUMERICO.getValor()) {
            panelIdentificadorUnico.setVisible(true);
            panelDispositivoMovel.setVisible(false);
            panelOutrasOpcoes.setVisible(false);
        }

        if (drop.getValue() == EnumTipoCampoFormatacao.TEXTO.getValor()) {
            panelIdentificadorUnico.setVisible(false);
            panelDispositivoMovel.setVisible(false);
            panelOutrasOpcoes.setVisible(false);
        }

        if (drop.getValue() == EnumTipoCampoFormatacao.VIDEO.getValor()) {
            panelIdentificadorUnico.setVisible(false);
            panelDispositivoMovel.setVisible(false);
            panelOutrasOpcoes.setVisible(false);
        }
    }

    // exibe o painel com os checkboxs com os tipos de acordo com a opcao
    // selecionada no drop down do tipo de campo
    protected void exibirCheckBoxModoEdicao(EnumTipoCampoFormatacao enumTipoCampoFormatacao) {
        if (enumTipoCampoFormatacao == EnumTipoCampoFormatacao.ALFANUMERICO) {
            panelIdentificadorUnico.setVisible(true);
            panelDispositivoMovel.setVisible(false);
            panelOutrasOpcoes.setVisible(false);
        }

        if (enumTipoCampoFormatacao == EnumTipoCampoFormatacao.ANEXO) {
            panelIdentificadorUnico.setVisible(false);
            panelDispositivoMovel.setVisible(false);
            panelOutrasOpcoes.setVisible(false);
        }

        if (enumTipoCampoFormatacao == EnumTipoCampoFormatacao.BOLEANO) {
            panelIdentificadorUnico.setVisible(false);
            panelDispositivoMovel.setVisible(false);
            panelOutrasOpcoes.setVisible(false);
        }

        if (enumTipoCampoFormatacao == EnumTipoCampoFormatacao.DATA) {
            panelIdentificadorUnico.setVisible(false);
            panelDispositivoMovel.setVisible(false);
            panelOutrasOpcoes.setVisible(false);
        }

        if (enumTipoCampoFormatacao == EnumTipoCampoFormatacao.FOTO) {
            panelIdentificadorUnico.setVisible(true);
            panelDispositivoMovel.setVisible(true);
            panelOutrasOpcoes.setVisible(true);
        }

        if (enumTipoCampoFormatacao == EnumTipoCampoFormatacao.NUMERICO) {
            panelIdentificadorUnico.setVisible(true);
            panelDispositivoMovel.setVisible(false);
            panelOutrasOpcoes.setVisible(false);
        }

        if (enumTipoCampoFormatacao == EnumTipoCampoFormatacao.TEXTO) {
            panelIdentificadorUnico.setVisible(false);
            panelDispositivoMovel.setVisible(false);
            panelOutrasOpcoes.setVisible(false);
        }

        if (enumTipoCampoFormatacao == EnumTipoCampoFormatacao.VIDEO) {
            panelIdentificadorUnico.setVisible(false);
            panelDispositivoMovel.setVisible(false);
            panelOutrasOpcoes.setVisible(false);
        }
    }

    private void voltar() {
        setResponsePage(new FormatacaoItensPesquisaPage(getPageParameters(), programa, backPage));
    }

    // Dataview

    // lista de itens das formatacoes
    private DataView<ItensFormatacao> newDataViewListaItens(ProviderItensSelecionados dp) {
        dataViewListaItem = new DataView<ItensFormatacao>("listaItensContrato", dp) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<ItensFormatacao> item) {
                CheckBox check = new CheckBox("check1", new PropertyModel<Boolean>(item.getModelObject(), "item.itemSelecionadoFormatacao"));
                check.add(new AjaxEventBehavior(ONCHANGE) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    protected void onEvent(AjaxRequestTarget target) {
                        selecionarItemParaFormatacao(item);
                    }
                });

                if (item.getModelObject() == null) {
                    listaItensJaSelecionados.clear();
                    listaItensJaSelecionados.add(item.getModelObject());
                }
                item.add(check);
                item.add(new Label("nomeBem", item.getModelObject().getItem().getNomeBem()));
                item.add(new Label("descricaoBem", item.getModelObject().getItem().getDescricaoBem()));
            }
        };
        dataViewListaItem.setItemsPerPage(Constants.ITEMS_PER_PAGE_PAGINATION);
        return dataViewListaItem;
    }

    // lista de formatacoes
    private DataView<FormatacaoItensContrato> newDataViewListaFormatacao(SortableFormatacaoItensDataProvider dp) {
        dataViewListaFormatacaoContrato = new DataView<FormatacaoItensContrato>("listaFormatacaoItens", dp) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<FormatacaoItensContrato> item) {

                item.add(new Label("verificacao", item.getModelObject().getFormaVerificacao().getDescricao()));
                item.add(new Label("tipoCampo", item.getModelObject().getTipoCampo().getDescricao()));
                item.add(new Label("tituloQuesito", item.getModelObject().getTituloQuesito()));
                item.add(new Label("identificadorUnico", item.getModelObject().getPossuiIdentificadorUnico() == true ? "Sim" : "Não"));
                item.add(new Label("opcional", item.getModelObject().getPossuiInformacaoOpcional() == true ? "Sim" : "Não"));

                item.add(newButtonEditar(item));
                item.add(newButtonExcluir(item));
            }
        };
        dataViewListaFormatacaoContrato.setItemsPerPage(Constants.ITEMS_PER_PAGE_PAGINATION);
        return dataViewListaFormatacaoContrato;
    }

    // Provaider
    private class ProviderItensSelecionados extends SortableDataProvider<ItensFormatacao, String> {
        private static final long serialVersionUID = 1L;

        @Override
        public Iterator<ItensFormatacao> iterator(long first, long size) {

            List<ItensFormatacao> listaRetorno = new ArrayList<ItensFormatacao>();
            if (!listaItensFormatacao.isEmpty()) {
                int inicio = (int) first;
                int fim = (int) (first + size);

                if (fim > listaItensFormatacao.size()) {
                    fim = listaItensFormatacao.size();
                }
                for (int i = inicio; i < fim; i++) {
                    listaRetorno.add(listaItensFormatacao.get(i));
                }
            }

            return listaRetorno.iterator();
        }

        @Override
        public long size() {
            return listaItensFormatacao.size();
        }

        @Override
        public IModel<ItensFormatacao> model(ItensFormatacao object) {
            return new CompoundPropertyModel<ItensFormatacao>(object);
        }
    }

    private class SortableFormatacaoItensDataProvider extends SortableDataProvider<FormatacaoItensContrato, String> {
        private static final long serialVersionUID = 1L;

        @Override
        public Iterator<FormatacaoItensContrato> iterator(long first, long count) {
            return listaFormatacaoItensContrato.iterator();
        }

        @Override
        public long size() {
            return listaFormatacaoItensContrato.size();
        }

        @Override
        public IModel<FormatacaoItensContrato> model(FormatacaoItensContrato object) {
            return new CompoundPropertyModel<FormatacaoItensContrato>(object);
        }

    }

    // getters e setters
    public Integer getItensPorPaginaItensContrato() {
        return itensPorPaginaItensContrato;
    }

    public void setItensPorPaginaItensContrato(Integer itensPorPaginaItensContrato) {
        this.itensPorPaginaItensContrato = itensPorPaginaItensContrato;
    }

    public Integer getItensPorPaginaFormatacao() {
        return itensPorPaginaFormatacao;
    }

    public void setItensPorPaginaFormatacao(Integer itensPorPaginaFormatacao) {
        this.itensPorPaginaFormatacao = itensPorPaginaFormatacao;
    }

}
