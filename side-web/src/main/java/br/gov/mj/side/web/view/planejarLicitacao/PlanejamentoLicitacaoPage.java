package br.gov.mj.side.web.view.planejarLicitacao;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraDropDownChoice;
import br.gov.mj.infra.wicket.model.LambdaModel;
import br.gov.mj.side.entidades.enums.EnumStatusPrograma;
import br.gov.mj.side.entidades.enums.EnumTipoAgrupamentoLicitacao;
import br.gov.mj.side.entidades.enums.EnumTipoMinuta;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoPrograma;
import br.gov.mj.side.entidades.programa.inscricao.analise.ListaAvaliacaoPublicado;
import br.gov.mj.side.entidades.programa.licitacao.AgrupamentoLicitacao;
import br.gov.mj.side.entidades.programa.licitacao.BemUf;
import br.gov.mj.side.entidades.programa.licitacao.LicitacaoPrograma;
import br.gov.mj.side.entidades.programa.licitacao.SelecaoItem;
import br.gov.mj.side.web.dto.BemUfDto;
import br.gov.mj.side.web.dto.ItemBemDto;
import br.gov.mj.side.web.dto.PesquisaLicitacaoDto;
import br.gov.mj.side.web.service.InscricaoProgramaService;
import br.gov.mj.side.web.service.LicitacaoProgramaService;
import br.gov.mj.side.web.service.ListaPublicadoService;
import br.gov.mj.side.web.service.ProgramaService;
import br.gov.mj.side.web.util.SideUtil;
import br.gov.mj.side.web.view.components.AjaxDialogButton;
import br.gov.mj.side.web.view.planejarLicitacao.minutaTR.GerarMinutaTR;
import br.gov.mj.side.web.view.programa.PanelFasePrograma;
import br.gov.mj.side.web.view.template.TemplatePage;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal.Backdrop;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.TextContentModal;

@AuthorizeInstantiation({ PlanejamentoLicitacaoPage.ROLE_MANTER_PLANEJAR_LICITACAO_VISUALIZAR, PlanejamentoLicitacaoPage.ROLE_MANTER_PLANEJAR_LICITACAO_INCLUIR, PlanejamentoLicitacaoPage.ROLE_MANTER_PLANEJAR_LICITACAO_ALTERAR, PlanejamentoLicitacaoPage.ROLE_MANTER_PLANEJAR_LICITACAO_EXCLUIR,
        PlanejamentoLicitacaoPesquisaPage.ROLE_MANTER_PLANEJAR_LICITACAO_GERAR_MINUTA })
public class PlanejamentoLicitacaoPage extends TemplatePage {
    private static final long serialVersionUID = 1L;

    // constantes de permição de acesso
    public static final String ROLE_MANTER_PLANEJAR_LICITACAO_VISUALIZAR = "manter_planejar_licitacao:visualizar";
    public static final String ROLE_MANTER_PLANEJAR_LICITACAO_INCLUIR = "manter_planejar_licitacao:incluir";
    public static final String ROLE_MANTER_PLANEJAR_LICITACAO_ALTERAR = "manter_planejar_licitacao:alterar";
    public static final String ROLE_MANTER_PLANEJAR_LICITACAO_EXCLUIR = "manter_planejar_licitacao:excluir";
    public static final String ROLE_MANTER_PLANEJAR_LICITACAO_GERAR_MINUTA = "manter_planejar_licitacao:gerar_minuta";

    private PanelFasePrograma panelFasePrograma;
    private ContratoPanelBotoes execucaoPanelBotoes;
    private PanelInformacoesBasicasLicitacao panelInformacoesBasicas;
    private PanelItensLicitacao panelItensLicitacao;
    private PanelBotoes panelBotoes;
    private PanelAbas panelAbas;

    private WebMarkupContainer containerDadosLicitacao;
    private WebMarkupContainer containerItemGrupo;
    private WebMarkupContainer containerGerarMinuta;

    private Form<LicitacaoPrograma> form;
    private AjaxButton buttonSalvar;
    private Page backPage;
    private AjaxSubmitLink buttonCancelarEdicao;
    private AjaxFallbackButton buttonMinuta;
    private Modal<String> modalLimparFormulario;
    private String msgConfirm;

    private EnumTipoMinuta tipoMinuta;
    private String tipoMinutaGerart = "HTML";
    private String abaAtual = "pageDadosLicitacao";
    private String abaAnterior = "";
    private Integer abaClicada;

    private List<Programa> listaProgramas = new ArrayList<Programa>();
    private List<InscricaoPrograma> listaInscricoesProgramaSelecionado = new ArrayList<InscricaoPrograma>();
    private List<BemUfDto> listaLocaisEntrega = new ArrayList<BemUfDto>();
    private List<BemUfDto> listaComDadosOriginaisBemUfDto = new ArrayList<BemUfDto>();

    private boolean readOnly;
    private boolean mostrarBotaoTrocarLicitacao;

    private LicitacaoPrograma licitacaoPrograma = new LicitacaoPrograma();
    private Programa programa;
    private AttributeAppender classeActive = new AttributeAppender("class", "active", " ");
    private AttributeModifier classeInactive = new AttributeModifier("class", "");

    @Inject
    private ComponentFactory componentFactory;

    @Inject
    private InscricaoProgramaService inscricaoService;

    @Inject
    private ProgramaService programaService;

    @Inject
    private ListaPublicadoService listaPublicadoService;

    @Inject
    private LicitacaoProgramaService licitacaoService;

    public PlanejamentoLicitacaoPage(final PageParameters pageParameters, Page backPage, Programa programa, AjaxRequestTarget target, Integer abaClicada) {
        super(pageParameters);
        this.backPage = backPage;
        this.readOnly = readOnly;
        this.programa = programa;
        this.abaClicada = abaClicada;

        buscarALIcitacaoDestePrograma();
        listaDeProgramasElegiveis();

        initComponents();

        if (licitacaoPrograma != null || licitacaoPrograma.getId() != null) {
            preencheAsListasNoModoEditar();
        }

        form.getModelObject().setPrograma(programa);

        setTitulo("Gerenciar Programa");

        if (licitacaoPrograma == null || licitacaoPrograma.getId() == null) {
            atualizarPainelLocaisDeEntrega(target);
        }

        actionAba(target, "pageDadosLicitacao");
    }

    private void initComponents() {

        form = new Form<LicitacaoPrograma>("form", new CompoundPropertyModel<LicitacaoPrograma>(new LicitacaoPrograma()));
        add(form);

        form.setModelObject(licitacaoPrograma);

        form.add(panelAbas = new PanelAbas("panelAbas"));
        form.add(panelFasePrograma = new PanelFasePrograma("panelFasePrograma", programa, backPage));
        form.add(execucaoPanelBotoes = new ContratoPanelBotoes("execucaoPanelPotoes", programa, backPage, "planejamento"));

        modalLimparFormulario = newModalLimparFormulario("modalLimparFormulario");
        modalLimparFormulario.show(false);

        form.add(modalLimparFormulario);

        form.add(panelInformacoesBasicas = new PanelInformacoesBasicasLicitacao("panelInformacoesBasicas", form.getModelObject(), this, readOnly));
        form.add(panelItensLicitacao = new PanelItensLicitacao("panelItensLicitacao", listaLocaisEntrega, licitacaoPrograma, readOnly));

        form.add(newButtonBaixarMinutaTr()); // btnGerarMinuta
        form.add(newDropDownTipoMinuta()); // dropEscolhaMinuta)
        form.add(panelBotoes = new PanelBotoes("panelBotoes", readOnly));
    }

    /*
     * OS PAINEIS SÃO ADICIONADOS ABAIXO
     */

    // Panel Principal com todas as abas
    private class PanelAbas extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelAbas(String id) {
            super(id);
            setOutputMarkupId(true);

            add(newContainerDadosLicitacao()); // abaDadosLicitacao
            add(newContainerItemGrupo()); // abaItemGrupo
            add(newContainerGerarMinuta()); // abaGerarMinuta
        }
    }

    private class PanelBotoes extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelBotoes(String id, Boolean readOnly) {
            super(id);

            setOutputMarkupId(true);
            add(newButtonSalvar()); // btnSalvar
            add(newButtonVoltar()); // btnVoltar
        }
    }

    // Abas individuais
    private WebMarkupContainer newContainerDadosLicitacao() {
        containerDadosLicitacao = new WebMarkupContainer("abaDadosLicitacao");
        containerDadosLicitacao.setOutputMarkupId(true);

        AjaxFallbackLink button = newLinkDadosDaLicitacao(); // btnDadosLicitacao
        containerDadosLicitacao.add(button);

        return containerDadosLicitacao;
    }

    private WebMarkupContainer newContainerItemGrupo() {
        containerItemGrupo = new WebMarkupContainer("abaItemGrupo");
        containerItemGrupo.setOutputMarkupId(true);

        AjaxFallbackLink button = newLinkItemGrupo(); // btnElegibilidade
        containerItemGrupo.add(button);
        return containerItemGrupo;
    }

    private WebMarkupContainer newContainerGerarMinuta() {
        containerGerarMinuta = new WebMarkupContainer("abaGerarMinuta");
        containerGerarMinuta.setOutputMarkupId(true);
        containerGerarMinuta.setVisible((licitacaoPrograma == null || licitacaoPrograma.getId() == null) ? false : true);

        AjaxFallbackLink button = newLinkGerarMinuta(); // btnGerarMinuta
        containerGerarMinuta.add(button);
        return containerGerarMinuta;
    }

    /*
     * OS COMPONENTES DOS PAINEIS ESTÃO ABAIXO
     */

    // BOTÕES DE CADA UMA DAS ABAS
    private AjaxFallbackLink<Void> newLinkDadosDaLicitacao() {
        AjaxFallbackLink<Void> btnInfo = componentFactory.newAjaxFallbackLink("btnDadosLicitacao", (target) -> actionAba(target, "pageDadosLicitacao"));
        btnInfo.setOutputMarkupId(true);
        return btnInfo;
    }

    private AjaxFallbackLink<Void> newLinkItemGrupo() {
        AjaxFallbackLink<Void> btnInfo = componentFactory.newAjaxFallbackLink("btnItemGrupo", (target) -> actionAba(target, "pageItemGrupo"));
        btnInfo.setOutputMarkupId(true);
        return btnInfo;
    }

    private AjaxFallbackLink<Void> newLinkGerarMinuta() {
        AjaxFallbackLink<Void> btnInfo = componentFactory.newAjaxFallbackLink("btnGerarMinuta", (target) -> actionAba(target, "pageGerarMinuta"));
        btnInfo.setOutputMarkupId(true);
        return btnInfo;
    }

    private InfraDropDownChoice<EnumTipoMinuta> newDropDownTipoMinuta() {
        List<EnumTipoMinuta> listaEnum = Arrays.asList(EnumTipoMinuta.values());
        InfraDropDownChoice<EnumTipoMinuta> dropDownChoice = componentFactory.newDropDownChoice("dropEscolhaMinuta", "Escolha Minuta", false, "valor", "descricao", new PropertyModel<EnumTipoMinuta>(this, "tipoMinuta"), listaEnum, null);
        dropDownChoice.setNullValid(true);
        authorize(dropDownChoice, RENDER, ROLE_MANTER_PLANEJAR_LICITACAO_GERAR_MINUTA);
        return dropDownChoice;
    }

    private Button newButtonBaixarMinutaTr() {
        Button btnDownload = componentFactory.newButton("btnGerarMinuta", () -> download(licitacaoPrograma));
        btnDownload.setVisible(true);
        authorize(btnDownload, RENDER, ROLE_MANTER_PLANEJAR_LICITACAO_GERAR_MINUTA);
        return btnDownload;
    }

    private void cancelarEdicao(AjaxRequestTarget target) {
        msgConfirm = "Esta ação irá desconsiderar todas as alterações realizadas, deseja continuar?.";
        modalLimparFormulario.show(true);
        target.add(modalLimparFormulario);
    }

    private Modal<String> newModalLimparFormulario(String id) {
        Modal<String> modal = new TextContentModal(id, new LambdaModel<String>(this::getMsgConfirm, this::setMsgConfirm));
        modal.setBackdrop(Backdrop.STATIC);
        modal.addButton(newButtonLimparFormulario(modal));
        modal.addButton(newButtonFecharModal(modal));
        return modal;
    }

    private AjaxDialogButton newButtonLimparFormulario(Modal<String> modal) {
        return new AjaxDialogButton(Model.of("OK"), Buttons.Type.Primary) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(new PlanejamentoLicitacaoPage(new PageParameters(), backPage, programa, null, abaClicada));
            }
        };
    }

    private AjaxDialogButton newButtonFecharModal(Modal<String> modal) {
        return new AjaxDialogButton(Model.of("Cancelar"), Buttons.Type.Primary) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                modal.show(false);
                modal.close(target);
            }
        };
    }

    private Button newButtonVoltar() {
        Button button = componentFactory.newButton("btnVoltar", () -> actionVoltar());
        button.setDefaultFormProcessing(false);
        return button;
    }

    private AjaxButton newButtonSalvar() {
        buttonSalvar = new AjaxButton("btnSalvar") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                actionSalvar(target);
                target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
            }
        };
        buttonSalvar.setOutputMarkupId(true);
        buttonSalvar.setEnabled(botoesEnabled());
        authorize(buttonSalvar, RENDER, ROLE_MANTER_PLANEJAR_LICITACAO_INCLUIR, ROLE_MANTER_PLANEJAR_LICITACAO_ALTERAR);
        return buttonSalvar;
    }

    /*
     * O PROVIDER SERÁ IMPLEMENTADO ABAIXO
     */

    /*
     * AS AÇÕES SERÃO IMPLEMENTADAS ABAIXO
     */

    private void download(LicitacaoPrograma item) {

        if (tipoMinuta != null) {
            GerarMinutaTR gerarMinuta = new GerarMinutaTR(SideUtil.getCaminhoCompletoRelatorios(getHttpServletRequest().getServletContext()));
            gerarMinuta.setTipoMinuta(tipoMinuta);
            ByteArrayOutputStream export = gerarMinuta.exportToByteArray(item, programaService, licitacaoService, inscricaoService);

            LicitacaoPrograma a = item;

            SideUtil.download(export.toByteArray(), a.getPrograma().getNomePrograma() + tipoDeMinutaAGerar());

        }

    }

    private void buscarALIcitacaoDestePrograma() {
        PesquisaLicitacaoDto pesquisa = new PesquisaLicitacaoDto();
        pesquisa.setPrograma(programa);

        List<LicitacaoPrograma> licitacaoEncontrada = licitacaoService.buscarSemPaginacao(pesquisa);
        if (licitacaoEncontrada.size() > 0) {
            licitacaoPrograma = licitacaoEncontrada.get(0);
        }
    }

    public void actionAba(AjaxRequestTarget target, String abaClicada) {

        // Se a aba clicada agora for igual a última clicada não atualizar nada.
        if (abaClicada.equalsIgnoreCase(abaAnterior)) {
            return;
        }

        target.appendJavaScript("ocultarAba('pageDadosLicitacao');");

        // Mostra a aba clicada
        target.appendJavaScript("mostrarAba('" + abaClicada + "');");

        // oculta a aba Anterior
        target.appendJavaScript("ocultarAba('" + abaAnterior + "');");

        abaAnterior = abaClicada;

        atualizarAbas(target, abaClicada);
    }

    private void atualizarAbas(AjaxRequestTarget target, String aba) {

        if ("pageDadosLicitacao".equalsIgnoreCase(aba)) {
            containerDadosLicitacao.add(classeActive);
            containerItemGrupo.add(classeInactive);
            containerGerarMinuta.add(classeInactive);
        } else {
            if ("pageItemGrupo".equalsIgnoreCase(aba)) {
                containerDadosLicitacao.add(classeInactive);
                containerItemGrupo.add(classeActive);
                containerGerarMinuta.add(classeInactive);
            } else {
                containerDadosLicitacao.add(classeInactive);
                containerItemGrupo.add(classeInactive);
                containerGerarMinuta.add(classeActive);
            }
        }

        containerDadosLicitacao.addOrReplace(newLinkDadosDaLicitacao());
        containerItemGrupo.addOrReplace(newLinkItemGrupo());
        containerGerarMinuta.addOrReplace(newLinkGerarMinuta());

        target.add(containerDadosLicitacao);
        target.add(containerItemGrupo);
        target.add(containerGerarMinuta);
    }

    private void listaDeProgramasElegiveis() {
        List<Programa> programa = new ArrayList<Programa>();
        programa = programaService.buscar(new Programa());

        for (Programa prog : programa) {
            List<ListaAvaliacaoPublicado> listaAvaliacao = SideUtil.convertAnexoDtoToEntityListaAvaliacaoPublicado(listaPublicadoService.buscarListaAvaliacaoPublicadoPeloIdPrograma(prog.getId()));

            if (listaAvaliacao.size() > 1 && (prog.getStatusPrograma() == EnumStatusPrograma.ABERTO_PLANEJAMENTO_LICITACAO || prog.getStatusPrograma() == EnumStatusPrograma.ABERTO_GERACAO_CONTRATO)) {
                listaProgramas.add(prog);
            }
        }
    }

    private void preencheAsListasNoModoEditar() {
        if (form.getModelObject().getPrograma() == null) {
            listaLocaisEntrega = new ArrayList<BemUfDto>();
        } else {
            listaLocaisEntrega = inscricaoService.buscarListaBemUfDto(programa);
        }
        copiarConteudoDaListaDeLocaisDeEntrega();

        // A lista original sempre será usada quando um item ou grupo for
        // excluido, baseado nela vou voltar a primeira tabela aos valores
        // originais.
        panelItensLicitacao.setListaComDadosOriginaisBemUfDto(listaComDadosOriginaisBemUfDto);

        // listaLocaisEntrega é a lista usada para gerar o dataView da primeira
        // tabela, a de locais de entrega.
        panelItensLicitacao.setListaLocaisEntrega(new ArrayList<BemUfDto>());

        // Atualiza os contadores. Sempre que forem adicionados novos itens e
        // grupos o contador deverá iniciar em 1
        panelItensLicitacao.setContadorGruposAdicionados(1);
        panelItensLicitacao.setContadorItensAdicionados(1);
    }

    public void atualizarPainelLocaisDeEntrega(AjaxRequestTarget target) {

        listaLocaisEntrega = inscricaoService.buscarListaBemUfDto(programa);
        copiarConteudoDaListaDeLocaisDeEntrega();

        // A lista original sempre será usada quando um item ou grupo for
        // excluido, baseado nela vou voltar a primeira tabela aos valores
        // originais.
        panelItensLicitacao.setListaComDadosOriginaisBemUfDto(listaComDadosOriginaisBemUfDto);

        // listaLocaisEntrega é a lista usada para gerar o dataView da primeira
        // tabela, a de locais de entrega.
        panelItensLicitacao.setListaLocaisEntrega(listaLocaisEntrega);

        // Atualiza os contadores. Sempre que forem adicionados novos itens e
        // grupos o contador deverá iniciar em 1
        panelItensLicitacao.setContadorGruposAdicionados(1);
        panelItensLicitacao.setContadorItensAdicionados(1);

        panelItensLicitacao.getPanelPrimeiro().addOrReplace(panelItensLicitacao.newDataViewPrimeiraLista());
        panelItensLicitacao.getPanelPrimeiro().addOrReplace(panelItensLicitacao.newPaginatorPrimeiraLista());
        target.add(panelItensLicitacao);
    }

    private void copiarConteudoDaListaDeLocaisDeEntrega() {

        listaComDadosOriginaisBemUfDto = new ArrayList<BemUfDto>();

        for (BemUfDto bem : listaLocaisEntrega) {

            BemUfDto novo = new BemUfDto();
            novo.setBem(bem.getBem());
            novo.setQuantidade(bem.getQuantidade());
            novo.setUf(bem.getUf());
            novo.setSelecionado(false);

            listaComDadosOriginaisBemUfDto.add(novo);
        }
    }

    private void actionVoltar() {
        setResponsePage(backPage);
    }

    private void actionSalvar(AjaxRequestTarget target) {
        if (!validarCampos()) {
            panelItensLicitacao.setPrimeiroLoop(true);
            return;
        }

        List<AgrupamentoLicitacao> listaPronta = montarListaAgrupamentos();

        // Irá verificar se todos os inputs dos Grupos e Itens foram preenchidos
        if (!validarSeTodosInputsDaListaForamDigitados(listaPronta)) {
            return;
        }

        licitacaoPrograma.setListaAgrupamentoLicitacao(listaPronta);

        licitacaoService.incluirAlterar(licitacaoPrograma, getIdentificador());

        if (licitacaoPrograma.getId() == null) {
            getSession().info("Licitação salva com sucesso.");
        } else {
            getSession().info("Licitação editada com sucesso.");
        }
        setResponsePage(new PlanejamentoLicitacaoPage(new PageParameters(), backPage, programa, target, abaClicada));
    }

    private List<AgrupamentoLicitacao> montarListaAgrupamentos() {
        List<AgrupamentoLicitacao> agrupamento = montarListaDeItens();
        List<AgrupamentoLicitacao> listaPronta = montarListaDeGrupos(agrupamento);
        return listaPronta;
    }

    private List<AgrupamentoLicitacao> montarListaDeItens() {
        List<AgrupamentoLicitacao> agrupamento = new ArrayList<AgrupamentoLicitacao>();

        List<ItemBemDto> listaItens = new ArrayList<ItemBemDto>();
        listaItens = panelItensLicitacao.getListaDeItens();
        for (ItemBemDto item : listaItens) {

            AgrupamentoLicitacao ag1 = new AgrupamentoLicitacao();
            ag1.setNomeAgrupamento(item.getNomeModelDto().getNomeGrupo());
            ag1.setTipoAgrupamentoLicitacao(item.getTipoAgrupamentoLicitacao());
            ag1.setId(item.getAgrupamentoLicitacao() != null ? item.getAgrupamentoLicitacao().getId() : null);

            if (item.getAgrupamentoLicitacao() != null) {
                ag1.setLicitacaoPrograma(licitacaoPrograma != null ? licitacaoPrograma : null);
            }

            List<SelecaoItem> listaSelecao = new ArrayList<SelecaoItem>();
            SelecaoItem selecao = new SelecaoItem();

            String unidadeMedida = item.getUnidadeDeMedida();
            Long quantidadeImediata = item.getQuantidadeImediata();
            BigDecimal valorUnitario = item.getValorUnitario();
            BigDecimal valorTotalImediata = item.getValorTotalImediato();
            BigDecimal valorTotalRegistrar = item.getValorTotalRegistrar();

            selecao.setUnidadeMedida(unidadeMedida);
            selecao.setQuantidadeImediata(quantidadeImediata);
            selecao.setValorUnitario(valorUnitario);
            selecao.setValorTotalImediato(valorTotalImediata);
            selecao.setValorTotalARegistrar(valorTotalRegistrar);
            selecao.setAgrupamentoLicitacao(item.getAgrupamentoLicitacao() != null ? item.getAgrupamentoLicitacao() : null);

            List<BemUf> listaUf = new ArrayList<BemUf>();

            for (BemUfDto item2 : item.getListaDeBens()) {

                BemUf bemUf = new BemUf();
                bemUf.setBem(item2.getBem());
                bemUf.setQuantidade(item2.getQuantidade());
                bemUf.setUf(item2.getUf());
                bemUf.setSelecaoItem(item2.getSelecaoItem());
                bemUf.setId(item2.getId() != null ? item2.getId() : null);

                if (item2.getSelecaoItem() == null) {

                } else {
                    selecao.setId(item2.getSelecaoItem().getId());
                }

                listaUf.add(bemUf);
            }

            selecao.setListaBemUf(listaUf);
            listaSelecao.add(selecao);
            ag1.setListaSelecaoItem(listaSelecao);

            agrupamento.add(ag1);
        }
        return agrupamento;
    }

    private List<AgrupamentoLicitacao> montarListaDeGrupos(List<AgrupamentoLicitacao> agrupamento) {
        // Montando os Grupos
        List<ItemBemDto> listaGrupos = new ArrayList<ItemBemDto>();
        listaGrupos = panelItensLicitacao.getListaDeGrupos();

        List<AgrupamentoLicitacao> listaAgrupamentot = new ArrayList<AgrupamentoLicitacao>();
        Integer numeroTemp = 0;
        AgrupamentoLicitacao ag1 = new AgrupamentoLicitacao();
        int flag = 0;

        for (ItemBemDto item : listaGrupos) {

            if (numeroTemp != item.getContadorTemp()) {
                numeroTemp = item.getContadorTemp();

                // Ira entrar neste 'if' somente depois que for criado a
                // primeira instancia de AgrupamentoLicitação
                if (flag > 0) {
                    agrupamento.add(ag1);
                }

                ag1 = new AgrupamentoLicitacao();
                ag1.setNomeAgrupamento(item.getNomeModelDto().getNomeGrupo());
                ag1.setTipoAgrupamentoLicitacao(item.getTipoAgrupamentoLicitacao());
                ag1.setId(item.getAgrupamentoLicitacao() != null ? item.getAgrupamentoLicitacao().getId() : null);

                if (item.getAgrupamentoLicitacao() != null) {
                    ag1.setLicitacaoPrograma(licitacaoPrograma != null ? licitacaoPrograma : null);
                }

                flag++;
            }

            SelecaoItem selecao = new SelecaoItem();

            String unidadeMedida = item.getUnidadeDeMedida();
            Long quantidadeImediata = item.getQuantidadeImediata();
            BigDecimal valorUnitario = item.getValorUnitario();
            BigDecimal valorTotalImediata = item.getValorTotalImediato();
            BigDecimal valorTotalRegistrar = item.getValorTotalRegistrar();

            selecao.setUnidadeMedida(unidadeMedida);
            selecao.setQuantidadeImediata(quantidadeImediata);
            selecao.setValorUnitario(valorUnitario);
            selecao.setValorTotalImediato(valorTotalImediata);
            selecao.setValorTotalARegistrar(valorTotalRegistrar);
            selecao.setAgrupamentoLicitacao(item.getAgrupamentoLicitacao() != null ? item.getAgrupamentoLicitacao() : null);

            List<BemUf> listaUf = new ArrayList<BemUf>();

            for (BemUfDto item2 : item.getListaDeBens()) {

                BemUf bemUf = new BemUf();
                bemUf.setBem(item2.getBem());
                bemUf.setQuantidade(item2.getQuantidade());
                bemUf.setUf(item2.getUf());
                bemUf.setSelecaoItem(item2.getSelecaoItem());
                bemUf.setId(item2.getId() != null ? item2.getId() : null);

                if (item2.getSelecaoItem() == null) {

                } else {
                    selecao.setId(item2.getSelecaoItem().getId());
                }

                listaUf.add(bemUf);
            }

            selecao.setListaBemUf(listaUf);
            ag1.getListaSelecaoItem().add(selecao);
        }

        // Insere o ultimo grupo;
        if (flag > 0) {
            agrupamento.add(ag1);
        }
        return agrupamento;
    }

    private boolean validarCampos() {
        boolean validar = true;
        form.getModelObject().setPrograma(programa);
        licitacaoPrograma = form.getModelObject();

        validar = validarInformacoesBasicas(validar, licitacaoPrograma);
        validar = validarSeTodosItensForamEscolhidos(validar);

        return validar;
    }

    private boolean validarInformacoesBasicas(boolean validar, LicitacaoPrograma licitacaoPrograma) {

        if (licitacaoPrograma.getPrograma() == null || licitacaoPrograma.getPrograma().getId() == null) {
            addMsgError("O campo 'Programa' é obrigatório.");
            validar = false;
        }

        if (licitacaoPrograma.getObjeto() == null || licitacaoPrograma.getObjeto().length() == 0) {
            addMsgError("O campo 'Objeto' é obrigatório.");
            validar = false;
        }

        if (licitacaoPrograma.getJustificativa() == null || licitacaoPrograma.getJustificativa().length() == 0) {
            addMsgError("O campo 'Justificativa' é obrigatório.");
            validar = false;
        }

        if (licitacaoPrograma.getEspecificacoesEQuantidadeDoObjeto() == null || licitacaoPrograma.getEspecificacoesEQuantidadeDoObjeto().length() == 0) {
            addMsgError("O campo 'Especificações e Quantidade do Objeto' é obrigatório.");
            validar = false;
        }

        if (licitacaoPrograma.getRecebimentoEAceitacaoDosMateriais() == null || licitacaoPrograma.getRecebimentoEAceitacaoDosMateriais().length() == 0) {
            addMsgError("O campo 'Recebimento e Aceitação dos Materiais' é obrigatório.");
            validar = false;
        }

        if (licitacaoPrograma.getPrazoLocalEFormaDeEntrega() == null || licitacaoPrograma.getPrazoLocalEFormaDeEntrega().length() == 0) {
            addMsgError("O campo 'Prazo, Local e Forma de Entrega' é obrigatório.");
            validar = false;
        }

        if (licitacaoPrograma.getMetodologiaDeAvaliacaoEAceiteDosMateriais() == null || licitacaoPrograma.getMetodologiaDeAvaliacaoEAceiteDosMateriais().length() == 0) {
            addMsgError("O campo 'Metodologia de Avaliação e Aceite dos Materiais' é obrigatório.");
            validar = false;
        }

        if (licitacaoPrograma.getDataInicialPeriodoExecucao() == null) {
            addMsgError("A data inicial de execução é obrigatória.");
            validar = false;
        }

        if (licitacaoPrograma.getDataFinalPeriodoExecucao() == null) {
            addMsgError("A data final de execução é obrigatória.");
            validar = false;
        }

        if (licitacaoPrograma.getDataInicialPeriodoExecucao() != null && licitacaoPrograma.getDataFinalPeriodoExecucao() != null) {
            if (licitacaoPrograma.getDataInicialPeriodoExecucao().isAfter(licitacaoPrograma.getDataFinalPeriodoExecucao())) {
                addMsgError("A data inicial de execução não pode ser superior a data final de execução.");
                validar = false;
            }
        }
        return validar;
    }

    private boolean validarSeTodosItensForamEscolhidos(boolean validar) {
        if (panelItensLicitacao.getListaLocaisEntrega().size() != 0) {
            addMsgError("Todos os itens da tabela 'Locais de Entrega' deverão ser selecionados.");
            validar = false;
        }

        return validar;
    }

    private boolean validarSeExistemGruposComMesmoNome(boolean validar) {

        // Verificar se Existem Grupos com o mesmo nome
        List<ItemBemDto> lista = panelItensLicitacao.getListaDeGrupos();
        for (ItemBemDto ibd : lista) {
            String nomeLoop = ibd.getNomeModelDto().getNomeGrupo();
            Integer idTemp = ibd.getContadorTemp();

            for (ItemBemDto ibd2 : lista) {
                String nomeLoop2 = ibd2.getNomeModelDto().getNomeGrupo();
                Integer idTemp2 = ibd2.getContadorTemp();

                if (nomeLoop.equalsIgnoreCase(nomeLoop2) && idTemp != idTemp2) {
                    addMsgError("Não é possível cadastrar 2 grupos com o mesmo nome.");
                    return false;
                }
            }
        }
        return validar;
    }

    private boolean validarSeExistemItensComMesmoNome(boolean validar) {

        // Verificar se Existem Grupos com o mesmo nome
        List<ItemBemDto> lista = panelItensLicitacao.getListaDeItens();
        for (ItemBemDto ibd : lista) {
            String nomeLoop = ibd.getNomeModelDto().getNomeGrupo();
            Integer idTemp = ibd.getContadorTemp();

            for (ItemBemDto ibd2 : lista) {
                String nomeLoop2 = ibd2.getNomeModelDto().getNomeGrupo();
                Integer idTemp2 = ibd2.getContadorTemp();

                if (nomeLoop.equalsIgnoreCase(nomeLoop2) && idTemp != idTemp2) {
                    addMsgError("Não é possível cadastrar 2 itens com o mesmo nome.");
                    return false;
                }
            }
        }
        return validar;
    }

    private boolean validarSeTodosInputsDaListaForamDigitados(List<AgrupamentoLicitacao> listaPronta) {
        boolean validar = true;
        int qtdItensVazios = 0;
        int qtdGruposVazios = 0;
        int qtdItensComQtdMaiorQueQuantidadeImediata = 0;
        int qtdGruposComQtdMaiorQueQuantidadeImediata = 0;

        for (AgrupamentoLicitacao agrup : listaPronta) {
            if (agrup.getNomeAgrupamento() == null || "".equalsIgnoreCase(agrup.getNomeAgrupamento())) {
                if (agrup.getTipoAgrupamentoLicitacao() == EnumTipoAgrupamentoLicitacao.GRUPO) {
                    qtdGruposVazios++;
                } else {
                    qtdItensVazios++;
                }
            }

            for (SelecaoItem si : agrup.getListaSelecaoItem()) {
                if (agrup.getTipoAgrupamentoLicitacao() == EnumTipoAgrupamentoLicitacao.GRUPO) {
                    qtdGruposVazios += verificarCampoNuloGrupo(si);
                    qtdGruposComQtdMaiorQueQuantidadeImediata += verificarGruposOndeQuantidadeImediataMaiorQueQuantidade(si);
                } else {
                    qtdItensVazios += verificarCampoNuloItem(si);
                    qtdItensComQtdMaiorQueQuantidadeImediata += verificarItensOndeQuantidadeImediataMaiorQueQuantidade(si);
                }
            }
        }

        if (qtdGruposVazios > 0) {
            addMsgError("Existe(m) " + qtdGruposVazios + " campo(s) vazio(s) ou com valores '0' na tabela 'Grupos Selecionados'.");
            validar = false;
        }

        if (qtdItensVazios > 0) {
            addMsgError("Existe(m) " + qtdItensVazios + " campo(s) vazio(s) ou com valores '0' na tabela 'Itens Selecionados'.");
            validar = false;
        }

        if (qtdGruposComQtdMaiorQueQuantidadeImediata > 0) {
            addMsgError("Existe(m) " + qtdGruposComQtdMaiorQueQuantidadeImediata + " grupo(s) com Item(s) cujo valor(es) da 'Quantidade Imediata' é maior que a 'Quantidade a Registrar'.");
            validar = false;
        }

        if (qtdItensComQtdMaiorQueQuantidadeImediata > 0) {
            addMsgError("Existe(m) " + qtdItensComQtdMaiorQueQuantidadeImediata + " item(s) onde o valor da 'Quantidade Imediata' é maior que a 'Quantidade a Registrar'.");
            validar = false;
        }

        return validar;
    }

    private Integer verificarCampoNuloItem(SelecaoItem si) {
        int vazios = 0;

        if (si.getUnidadeMedida() == null || "".equalsIgnoreCase(si.getUnidadeMedida())) {
            vazios++;
        }

        /*
         * if (si.getQuantidadeImediata() == null || si.getQuantidadeImediata()
         * == 0) { vazios++; }
         */

        if (si.getValorUnitario() == null || si.getValorUnitario().doubleValue() == 0) {
            vazios++;
        }

        /*
         * if (si.getValorTotalImediato() == null ||
         * si.getValorTotalImediato().doubleValue() == 0) { vazios++; }
         */

        return vazios;
    }

    private Integer verificarGruposOndeQuantidadeImediataMaiorQueQuantidade(SelecaoItem si) {

        int cont = 0;
        Integer qtd = 0;
        for (BemUf bem : si.getListaBemUf()) {
            qtd += bem.getQuantidade().intValue();
        }

        /*
         * if (qtd < si.getQuantidadeImediata().intValue()) { cont++; return 1;
         * }
         */
        return cont;
    }

    private Integer verificarItensOndeQuantidadeImediataMaiorQueQuantidade(SelecaoItem si) {

        int cont = 0;
        Integer qtd = 0;
        for (BemUf bem : si.getListaBemUf()) {
            qtd += bem.getQuantidade().intValue();
        }

        /*
         * if (qtd < si.getQuantidadeImediata().intValue()) { cont++; return 1;
         * }
         */
        return cont;
    }

    private Integer verificarCampoNuloGrupo(SelecaoItem si) {
        int vazios = 0;

        if (si.getUnidadeMedida() == null || "".equalsIgnoreCase(si.getUnidadeMedida())) {
            vazios++;
        }

        /*
         * if (si.getQuantidadeImediata() == null || si.getQuantidadeImediata()
         * == 0) { vazios++; }
         */

        if (si.getValorUnitario() == null || si.getValorUnitario().doubleValue() == 0) {
            vazios++;
        }

        /*
         * if (si.getValorTotalImediato() == null ||
         * si.getValorTotalImediato().doubleValue() == 0) { vazios++; }
         */

        return vazios;
    }

    private boolean botoesEnabled() {
        if (readOnly) {
            return false;
        } else {
            return true;
        }

    }

    private String limparNumeroSei(String valor) {
        if (valor == null || "".equalsIgnoreCase(valor)) {
            return "";
        }
        String value = valor;
        value = value.replace(".", "");
        value = value.replace("/", "");
        value = value.replace("-", "");
        value = value.replace("R$", "");
        value = value.replace(" ", "");
        value = value.replace(",", ".");
        return value;
    }

    private boolean dropProgramaEnabled() {
        if (readOnly || (licitacaoPrograma != null && licitacaoPrograma.getId() != null)) {
            return false;
        } else {
            return true;
        }
    }

    private boolean mostrarBotaoTrocarLicitacao() {
        if (readOnly) {
            return false;
        } else {
            if (licitacaoPrograma == null || licitacaoPrograma.getId() == null) {
                return false;
            } else {
                return true;
            }
        }
    }

    private String tipoDeMinutaAGerar() {
        if (tipoMinuta == EnumTipoMinuta.DOC) {
            return ".doc";
        } else {
            if (tipoMinuta == EnumTipoMinuta.ODT) {
                return ".odt";
            } else {
                if (tipoMinuta == EnumTipoMinuta.PDF) {
                    return ".pdf";
                } else {
                    return ".html";
                }
            }
        }
    }

    private String gerarTituloPagina() {
        String tituloPagina = "";
        if (readOnly) {
            tituloPagina = "Visualizar Licitação";
        } else {
            if (licitacaoPrograma == null || licitacaoPrograma.getId() == null) {
                tituloPagina = "Planejar Licitação";
            } else {
                tituloPagina = "Editar Licitação";
            }
        }
        return tituloPagina;
    }

    public String getMsgConfirm() {
        return msgConfirm;
    }

    public void setMsgConfirm(String msgConfirm) {
        this.msgConfirm = msgConfirm;
    }
}
