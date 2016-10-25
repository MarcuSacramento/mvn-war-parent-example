package br.gov.mj.side.web.view.execucao;

import javax.inject.Inject;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.validator.StringValidator;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraAjaxFallbackLink;
import br.gov.mj.infra.wicket.component.pagination.InfraAjaxPagingNavigator;
import br.gov.mj.infra.wicket.model.LambdaModel;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.licitacao.contrato.ObjetoFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.TermoDoacao;
import br.gov.mj.side.web.dto.AnexoDto;
import br.gov.mj.side.web.dto.TermoDoacaoDto;
import br.gov.mj.side.web.dto.enderecoLicitacao.DoacoesConcluidasDto;
import br.gov.mj.side.web.service.AnexoTermoDoacaoService;
import br.gov.mj.side.web.service.MailService;
import br.gov.mj.side.web.service.TermoDoacaoService;
import br.gov.mj.side.web.util.CnpjUtil;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.DataUtil;
import br.gov.mj.side.web.util.SideUtil;
import br.gov.mj.side.web.util.SortableDoacaoConluidaDataProvider;
import br.gov.mj.side.web.util.SortableDoacaoDataProvider;
import br.gov.mj.side.web.view.programa.PanelFasePrograma;
import br.gov.mj.side.web.view.template.TemplatePage;

public class ConfirmarDoacaoPage extends TemplatePage {

    private static final long serialVersionUID = 1L;

    // #######################################_VARIAVEIS_############################################
    private Form<ConfirmarDoacaoPage> form;
    private Programa programa;
    private Page backPage;
    private Integer abaClicada;
    private Label labelMensagem;
    private SortableDoacaoDataProvider dp1;
    private SortableDoacaoConluidaDataProvider dp2;

    // #######################################_CONSTANTE_############################################
    private Integer itensPorPaginaTermosDoacaoGerados = Constants.ITEMS_PER_PAGE_PAGINATION;
    private Integer itensPorPaginaDoacoesConcluidas = Constants.ITEMS_PER_PAGE_PAGINATION;
    private boolean btnConcluirD = Boolean.FALSE;

    // #######################################_ELEMENTOS_WICKET_############################################
    private DataView<TermoDoacao> dataViewObjetoTermosDoacaoGerados;
    private DataView<DoacoesConcluidasDto> dataViewObjetoDoacoesConcluidas;
    private Model<String> mensagem = Model.of("");

    // #######################################_PAINEIS_############################################
    private PanelTermosDoacaoGerados panelTermosDoacaoGerados;
    private PanelDoacoesConcluidas panelDoacoesConcluidas;

    // #####################################_INJECAO_DE_DEPENDENCIA_##############################################
    @Inject
    private ComponentFactory componentFactory;

    @Inject
    private TermoDoacaoService termoDoacaoService;

    @Inject
    private AnexoTermoDoacaoService anexoTermoDoacaoService;

    @Inject
    private MailService mailService;

    // #####################################_CONSTRUTOR_##############################################
    public ConfirmarDoacaoPage(final PageParameters pageParameters, Programa programa, Page backPage, Integer abaClicada) {
        super(pageParameters);
        setTitulo("Gerenciar Programa");
        this.backPage = backPage;
        this.programa = programa;
        this.abaClicada = abaClicada;

        initVariaveis();
        initComponents();
    }

    private void initVariaveis() {
        TermoDoacaoDto termoDoacaoDto = new TermoDoacaoDto();
        termoDoacaoDto.setPrograma(programa);

        dp1 = new SortableDoacaoDataProvider(termoDoacaoService, termoDoacaoDto);
        dp2 = new SortableDoacaoConluidaDataProvider(termoDoacaoService, termoDoacaoDto);
    }

    private void initComponents() {
        form = new Form<ConfirmarDoacaoPage>("form", new CompoundPropertyModel<ConfirmarDoacaoPage>(this));
        add(form);

        form.add(new PanelFasePrograma("panelFasePrograma", programa, backPage, abaClicada));
        form.add(new ExecucaoPanelBotoes("execucaoPanelPotoes", programa, backPage, "ConfirmarDoacao"));

        form.add(panelTermosDoacaoGerados = newPanelTermosDoacaoGerados());
        form.add(panelDoacoesConcluidas = newPanelDoacoesConcluidas());
        form.add(new PanelButton());
    }

    // ####################################_PAINEIS_##############################################
    // PAINEL TERMO DE DOACAO GERADOS
    public PanelTermosDoacaoGerados newPanelTermosDoacaoGerados() {
        panelTermosDoacaoGerados = new PanelTermosDoacaoGerados();
        setOutputMarkupId(Boolean.TRUE);
        return panelTermosDoacaoGerados;
    }

    private class PanelTermosDoacaoGerados extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelTermosDoacaoGerados() {
            super("panelTermosDoacaoGerados");

            labelMensagem = new Label("mensagem", mensagem);
            labelMensagem.setEscapeModelStrings(false);
            add(labelMensagem);

            add(new OrderByBorder<String>("orderBynomeBeneficiario", "nomeBeneficiario", dp1));
            add(new OrderByBorder<String>("orderBynumeroDocumentoSEI", "numeroDocumentoSei", dp1));
            add(newDropItensPorPaginaTermosDoacaoGerados());
            add(newDataViewTermosDoacaoGerados());
            add(new InfraAjaxPagingNavigator("paginationTermosDoacaoGerados", dataViewObjetoTermosDoacaoGerados));
        }
    }

    // PAINEL DOACOES CONCLUIDAS
    public PanelDoacoesConcluidas newPanelDoacoesConcluidas() {
        panelDoacoesConcluidas = new PanelDoacoesConcluidas();
        setOutputMarkupId(Boolean.TRUE);
        return panelDoacoesConcluidas;
    }

    private class PanelDoacoesConcluidas extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelDoacoesConcluidas() {
            super("panelDoacoesConcluidas");

            add(new OrderByBorder<String>("orderBynomeBeneficiario1", "nomeBeneficiario", dp2));
            add(newDropItensPorPaginaDoacoesConcluidas());
            add(newDataViewDoacoesConcluidas());
            add(new InfraAjaxPagingNavigator("paginationDoacoesConcluidas", dataViewObjetoDoacoesConcluidas));
        }
    }

    // PAINEL BOTAO
    private class PanelButton extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelButton() {
            super("panelButton");
            setOutputMarkupId(Boolean.TRUE);

            add(componentFactory.newAjaxFallbackLink("btnVoltar", (target) -> setResponsePage(backPage)));
        }
    }

    // ####################################_COMPONENTE_WICKET_###############################################
    private DropDownChoice<Integer> newDropItensPorPaginaTermosDoacaoGerados() {
        DropDownChoice<Integer> dropDownChoice = new DropDownChoice<Integer>("itensPorPaginaTermosDoacaoGerados", new LambdaModel<Integer>(this::getItensPorPaginaTermosDoacaoGerados, this::setItensPorPaginaTermosDoacaoGerados), Constants.QUANTIDADE_ITENS_TABELA);
        dropDownChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                dataViewObjetoTermosDoacaoGerados.setItemsPerPage(getItensPorPaginaTermosDoacaoGerados());
                target.add(panelTermosDoacaoGerados);
            };
        });
        return dropDownChoice;
    }

    // DataView Termos de Doacao Gerados
    private DataView<TermoDoacao> newDataViewTermosDoacaoGerados() {
        dataViewObjetoTermosDoacaoGerados = new DataView<TermoDoacao>("listaTermosDoacaoGerados", dp1) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<TermoDoacao> item) {
                AttributeAppender classeAtivarPopover = new AttributeAppender("class", "pop", " ");

                item.add(new Label("nomeBeneficiario"));
                item.add(new Label("numeroCnpj", CnpjUtil.imprimeCNPJ(item.getModelObject().getNumeroCnpj())));

                String[] listaItens = formatarListaItensTDG(item);

                String iconeIdItem = "<a tabindex=\"0\" role=\"button\" data-toggle=\"popover\" data-trigger=\"focus\" data-content=\"" + listaItens[0] + "\"><i style=\"color:green;\" class=\"fa fa-info-circle\"> </i></a>";
                Label lblItem = new Label("itens", iconeIdItem);
                lblItem.setEscapeModelStrings(false);
                lblItem.setOutputMarkupId(true);
                if (item.getModelObject().getObjetosFornecimentoContrato().size() > 10)
                    lblItem.add(classeAtivarPopover);
                item.add(lblItem);

                item.add(new Label("dataGeracao", DataUtil.converteDataDeLocalDateParaString(item.getModelObject().getDataGeracao(), "dd/MM/yyyy")));

                item.add(new Label("numeroProcessoSEI"));
                item.add(newTextFieldNDocumentoSEI(item));

                Button btnDownloadTD = componentFactory.newButton("btnDownloadTD", () -> downloadTD(item));
                item.add(btnDownloadTD);

                InfraAjaxFallbackLink<Void> btnConcluirDoacao = componentFactory.newAjaxFallbackLink("btnConcluirDoacao", (target) -> actionGerarTermoDoacao(target, item));
                btnConcluirDoacao.setVisible(btnConcluirD);
                item.add(btnConcluirDoacao);
            }

        };
        dataViewObjetoTermosDoacaoGerados.setItemsPerPage(getItensPorPaginaTermosDoacaoGerados() == null ? Constants.ITEMS_PER_PAGE_PAGINATION : getItensPorPaginaTermosDoacaoGerados());
        return dataViewObjetoTermosDoacaoGerados;
    }

    private String[] formatarListaItensTDG(Item<TermoDoacao> item) {
        String[] lista = new String[1];
        StringBuilder sb = new StringBuilder(1);

        sb.append("<table><tr><td><center>  ID  </center></td><td>&emsp;</td><td><center>  ITEM  </center></td></tr><tr><td colspan='3'><hr></hr></td></tr>");

        for (ObjetoFornecimentoContrato obj : item.getModelObject().getObjetosFornecimentoContrato()) {
            sb.append("<tr><td><center>  " + obj.getId() + "  </center>  </td><td>&emsp;</td><td><center>  " + obj.getItem().getNomeBem() + "  </center></td></tr>");
        }
        sb.append("</table>");

        lista[0] = sb.toString();

        return lista;
    }

    public TextField<String> newTextFieldNDocumentoSEI(Item<TermoDoacao> item) {
        TextField<String> field = componentFactory.newTextField("numeroDocumentoSei", "Nº Documento SEI", false, new PropertyModel<String>(item.getModelObject(), "numeroDocumentoSei"));
        if (item.getModelObject().getNumeroDocumentoSei() != null && !item.getModelObject().getNumeroDocumentoSei().equalsIgnoreCase("")) {
            field.setEnabled(false);
            btnConcluirD = Boolean.FALSE;
        } else {
            field.setEnabled(true);
            btnConcluirD = Boolean.TRUE;
            acaoNDocumentoSEI(field);
        }
        field.add(StringValidator.maximumLength(8));
        return field;
    }

    private TextField<String> acaoNDocumentoSEI(TextField<String> field) {
        field.add(new AjaxFormComponentUpdatingBehavior("onkeyup") {
            private static final long serialVersionUID = 1L;

            protected void onUpdate(AjaxRequestTarget target) {
                // Setar no model
            }
        });
        return field;
    }

    // DataView Doacoes Concluidas
    private DataView<DoacoesConcluidasDto> newDataViewDoacoesConcluidas() {
        dataViewObjetoDoacoesConcluidas = new DataView<DoacoesConcluidasDto>("listaDoacoesConcluidas", dp2) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<DoacoesConcluidasDto> item) {
                AttributeAppender classeAtivarPopover = new AttributeAppender("class", "pop", " ");

                item.add(new Label("nomeBeneficiario", item.getModelObject().getEntidade().getNomeEntidade()));
                item.add(new Label("numeroCnpj", CnpjUtil.imprimeCNPJ(item.getModelObject().getEntidade().getNumeroCnpj())));

                String[] listaItens = formatarListaItensDC(item);

                item.add(new Label("quantidadeEntregue"));
                String iconeIdItem = "<a tabindex=\"0\" role=\"button\" data-placement=\"left\" data-trigger=\"focus\" data-toggle=\"popover\" data-content=\"" + listaItens[0] + "\"><i style=\"color:green;\" class=\"fa fa-info-circle\"> </i></a>";
                Label lblItem = new Label("itens", iconeIdItem);
                lblItem.setEscapeModelStrings(false);
                lblItem.setOutputMarkupId(true);
                if (item.getModelObject().getListaItensEntregues().size() > 10)
                    lblItem.add(classeAtivarPopover);
                item.add(lblItem);

                item.add(new Label("quantidadeDoado"));
                String iconeIdItem1 = "<a tabindex=\"0\" role=\"button\" data-placement=\"left\" data-trigger=\"focus\" data-toggle=\"popover\" data-content=\"" + listaItens[1] + "\"><i style=\"color:green;\" class=\"fa fa-info-circle\"> </i></a>";
                Label lblItem1 = new Label("itens1", iconeIdItem1);
                lblItem1.setEscapeModelStrings(false);
                lblItem1.setOutputMarkupId(true);
                if (item.getModelObject().getListaItensDoados().size() > 10)
                    lblItem1.add(classeAtivarPopover);
                item.add(lblItem1);
            }
        };
        dataViewObjetoDoacoesConcluidas.setItemsPerPage(getItensPorPaginaDoacoesConcluidas() == null ? Constants.ITEMS_PER_PAGE_PAGINATION : getItensPorPaginaDoacoesConcluidas());
        return dataViewObjetoDoacoesConcluidas;
    }

    private String[] formatarListaItensDC(Item<DoacoesConcluidasDto> item) {
        String[] lista = new String[2];

        StringBuilder sb = new StringBuilder(1);
        StringBuilder sb1 = new StringBuilder(1);

        sb.append("<table><tr><td><center>  ID  </center></td><td>&emsp;</td><td><center>  ITEM  </center></td></tr><tr><td colspan='3'><hr></hr></td></tr>");

        for (ObjetoFornecimentoContrato obj : item.getModelObject().getListaItensEntregues()) {
            sb.append("<tr><td><center>  " + obj.getId() + "  </center>  </td><td>&emsp;</td><td><center>  " + obj.getItem().getNomeBem() + "  </center></td></tr>");
        }
        sb.append("</table>");

        sb1.append("<table><tr><td><center>  ID  </center></td><td>&emsp;</td><td><center>  ITEM  </center></td></tr><tr><td colspan='3'><hr></hr></td></tr>");

        for (ObjetoFornecimentoContrato obj : item.getModelObject().getListaItensDoados()) {
            sb1.append("<tr><td><center>  " + obj.getId() + "  </center>  </td><td>&emsp;</td><td><center>  " + obj.getItem().getNomeBem() + "  </center></td></tr>");
        }
        sb1.append("</table>");

        lista[0] = sb.toString();
        lista[1] = sb1.toString();

        return lista;
    }

    private DropDownChoice<Integer> newDropItensPorPaginaDoacoesConcluidas() {
        DropDownChoice<Integer> dropDownChoice = new DropDownChoice<Integer>("itensPorPaginaDoacoesConcluidas", new LambdaModel<Integer>(this::getItensPorPaginaDoacoesConcluidas, this::setItensPorPaginaDoacoesConcluidas), Constants.QUANTIDADE_ITENS_TABELA);
        dropDownChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                dataViewObjetoDoacoesConcluidas.setItemsPerPage(getItensPorPaginaDoacoesConcluidas());
                target.add(panelDoacoesConcluidas);
            };
        });
        return dropDownChoice;
    }

    // ####################################_ACOES_###############################################
    private void downloadTD(Item<TermoDoacao> item) {
        TermoDoacao termo = new TermoDoacao();
        termo = item.getModelObject();
        if (termo.getId() != null) {
            AnexoDto retorno = anexoTermoDoacaoService.buscarPeloId(termo.getId());
            SideUtil.download(retorno.getConteudo(), retorno.getNomeAnexo() + ".pdf");
        }
    }

    private void actionGerarTermoDoacao(AjaxRequestTarget target, Item<TermoDoacao> item) {
        if (!validarObrigatoriedade(target, item)) {
            target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackError');");
            return;
        }

        termoDoacaoService.concluirDoacao(item.getModelObject(), programa, getIdentificador());
        mailService.enviarEmailConcluirDoacao(item.getModelObject());

        initVariaveis();

        panelTermosDoacaoGerados.addOrReplace(newDataViewTermosDoacaoGerados());
        panelTermosDoacaoGerados.addOrReplace(new InfraAjaxPagingNavigator("paginationTermosDoacaoGerados", dataViewObjetoTermosDoacaoGerados));
        panelDoacoesConcluidas.addOrReplace(newDataViewDoacoesConcluidas());
        panelDoacoesConcluidas.addOrReplace(new InfraAjaxPagingNavigator("paginationDoacoesConcluidas", dataViewObjetoDoacoesConcluidas));
        target.add(panelTermosDoacaoGerados);
        target.add(panelDoacoesConcluidas);
        target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
        addMsgInfo("Doação concluída com sucesso!");
    }

    private boolean validarObrigatoriedade(AjaxRequestTarget target, Item<TermoDoacao> item) {
        boolean validar = Boolean.TRUE;
        String msg = "";

        if ("".equals(item.getModelObject().getNumeroDocumentoSei()) || item.getModelObject().getNumeroDocumentoSei() == null) {
            msg += "<p><li> Campo 'Número do Documento SEI' é obrigatório.</li><p />";
            validar = Boolean.FALSE;
        }

        if (item.getModelObject().getNumeroDocumentoSei().length() < 8) {
            msg += "<p><li> Campo 'Número do Documento SEI' são com 8 caracteres.</li><p />";
            validar = Boolean.FALSE;
        }

        if (!validar) {
            mensagem.setObject(msg);
        } else {
            mensagem.setObject("");
        }
        target.add(labelMensagem);
        return validar;
    }

    // ####################################_GETTERS_E_SETTERS_###############################################
    public Integer getItensPorPaginaTermosDoacaoGerados() {
        return itensPorPaginaTermosDoacaoGerados;
    }

    public void setItensPorPaginaTermosDoacaoGerados(Integer itensPorPaginaTermosDoacaoGerados) {
        this.itensPorPaginaTermosDoacaoGerados = itensPorPaginaTermosDoacaoGerados;
    }

    public Integer getItensPorPaginaDoacoesConcluidas() {
        return itensPorPaginaDoacoesConcluidas;
    }

    public void setItensPorPaginaDoacoesConcluidas(Integer itensPorPaginaDoacoesConcluidas) {
        this.itensPorPaginaDoacoesConcluidas = itensPorPaginaDoacoesConcluidas;
    }

}
