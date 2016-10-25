package br.gov.mj.side.web.view.propostas;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.side.entidades.enums.EnumAbaFaseAnalise;
import br.gov.mj.side.entidades.enums.EnumStatusInscricao;
import br.gov.mj.side.entidades.enums.EnumStatusPrograma;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.ProgramaHistoricoPublicizacao;
import br.gov.mj.side.entidades.programa.inscricao.analise.ListaAvaliacaoPublicado;
import br.gov.mj.side.entidades.programa.inscricao.analise.ListaElegibilidadePublicado;
import br.gov.mj.side.web.dto.AnaliseDto;
import br.gov.mj.side.web.dto.PropostaPesquisaDto;
import br.gov.mj.side.web.service.ListaPublicadoService;
import br.gov.mj.side.web.service.ProgramaService;
import br.gov.mj.side.web.util.SideUtil;
import br.gov.mj.side.web.view.programa.PanelFasePrograma;
import br.gov.mj.side.web.view.template.TemplatePage;

@AuthorizeInstantiation({ PropostasEnviadasPage.ROLE_MANTER_ANALISE_VISUALIZAR, PropostasEnviadasPage.ROLE_MANTER_ANALISE_ELEGIBILIDADE, PropostasEnviadasPage.ROLE_MANTER_ANALISE_AVALIACAO })
public class PropostasEnviadasPage extends TemplatePage {
    private static final long serialVersionUID = 1L;

    public static final String ROLE_MANTER_ANALISE_VISUALIZAR = "manter_analise:visualizar";
    public static final String ROLE_MANTER_ANALISE_ELEGIBILIDADE = "manter_analise:analisar_elegibilidade";
    public static final String ROLE_MANTER_ANALISE_AVALIACAO = "manter_analise:analisar_avaliacao";

    private Form<PropostaPesquisaDto> form;
    private PropostaPesquisaDto pesquisaClassificados = new PropostaPesquisaDto();
    
    private PanelFasePrograma panelFasePrograma;
    private PanelAbas panelAbas;

    private PanelAbaElegibilidade panelAbaElegibilidade;
    private PanelAbaClassificacao panelAbaClassificacao;
    private PanelPropostas panelPropostas;
    private PanelPropostas panelPropostasClassificacao;

    private PropostasEnviadasPage propostasEnviadasPage;

    private WebMarkupContainer containerAlert;
    private WebMarkupContainer containerElegibilidade;
    private WebMarkupContainer containerClassificacao;

    private List<ListaElegibilidadePublicado> listasPublicadasElegibilidade = new ArrayList<ListaElegibilidadePublicado>();
    private List<ListaAvaliacaoPublicado> listasPublicadasAvaliacao = new ArrayList<ListaAvaliacaoPublicado>();

    private Page backPage;
    private Programa programa;
    private Integer abaClicada;
    private Boolean geradaSegundaListaElegibilidade = false;
    private Boolean geradaSegundaListaClassificacao = false;
    private AnaliseDto analiseDto;

    private AttributeAppender classeActive = new AttributeAppender("class", "active", " ");
    private AttributeModifier classeInactive = new AttributeModifier("class", "");

    private String idAtual = "abaElegibilidade";
    private String abaAnterior = "";

    @Inject
    private ComponentFactory componentFactory;

    @Inject
    private ListaPublicadoService listaPublicadoService;

    @Inject
    private ProgramaService programaService;

    public PropostasEnviadasPage(final PageParameters pageParameters) {
        super(pageParameters);
        setTitulo("Pesquisar Propostas");
        initComponents();
    }

    public PropostasEnviadasPage(final PageParameters pageParameters, Programa programa, Page backPage, AjaxRequestTarget target, AnaliseDto analiseDto,Integer abaClicada) {
        super(pageParameters);
        this.backPage = backPage;
        this.programa = programa;
        this.abaClicada = abaClicada;
        this.analiseDto = analiseDto;
        this.propostasEnviadasPage = this;

        setTitulo("Gerenciar Programa");
        initVariaveis();
        initComponents();
        ativarDesativarAbasAoIniciarAPrimeiraVez(target);
    }

    private void initVariaveis() {
        
        if(programa != null && programa.getId() != null){
            listasPublicadasElegibilidade = SideUtil.convertAnexoDtoToEntityListaElegibilidadePublicado(listaPublicadoService.buscarListaElegibilidadePublicadoPeloIdPrograma(programa.getId()));
            pesquisaClassificados.getInscricao().setPrograma(programa);
            
            // Irá verificar se foram geradas todas as listas nesta fase de analise;
            if (listasPublicadasElegibilidade.size() == 2) {
                geradaSegundaListaElegibilidade = true;

                listasPublicadasAvaliacao = SideUtil.convertAnexoDtoToEntityListaAvaliacaoPublicado(listaPublicadoService.buscarListaAvaliacaoPublicadoPeloIdPrograma(programa.getId()));
                if (listasPublicadasAvaliacao.size() == 2) {
                    geradaSegundaListaClassificacao = true;
                }
            }
        }
    }

    // Panel Principal com todas as abas
    private class PanelAbas extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelAbas(String id) {
            super(id);
            setOutputMarkupId(true);

            add(newContainerElegibilidade()); // abaElegibilidade
            add(newContainerClassificacao()); // abaClassificacao
        }
    }

    // Estes paineis são paginas de cada aba, o conteúdo delas.
    private class PanelAbaElegibilidade extends WebMarkupContainer {
        public PanelAbaElegibilidade(String id) {
            super(id);
            
            AnaliseDto analiseDto = new AnaliseDto();
            analiseDto.setAbaClicada(EnumAbaFaseAnalise.ELEGIBILIDADE);
            analiseDto.setGeradaSegundaListaElegibilidade(geradaSegundaListaElegibilidade);
            analiseDto.setGeradaSegundaListaClassificacao(geradaSegundaListaClassificacao);
            
            add(panelPropostas = new PanelPropostas("panelPropostas",programa, analiseDto,propostasEnviadasPage));
        }
    }

    private class PanelAbaClassificacao extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelAbaClassificacao(String id) {
            super(id);
            
            AnaliseDto analiseDto = new AnaliseDto();
            analiseDto.setAbaClicada(EnumAbaFaseAnalise.CLASSIFICACAO);
            analiseDto.setGeradaSegundaListaElegibilidade(geradaSegundaListaElegibilidade);
            analiseDto.setGeradaSegundaListaClassificacao(geradaSegundaListaClassificacao);
            
            add(panelPropostasClassificacao = new PanelPropostas("panelPropostasClassificacao",programa,analiseDto,propostasEnviadasPage));
        }
    }

    // Abas individuais
    private WebMarkupContainer newContainerElegibilidade() {
        containerElegibilidade = new WebMarkupContainer("abaElegibilidade");
        containerElegibilidade.setOutputMarkupId(true);

        AjaxFallbackLink button = newLinkElegibilidade(); // btnElegibilidade
        containerElegibilidade.add(button);
        return containerElegibilidade;
    }

    private WebMarkupContainer newContainerClassificacao() {
        containerClassificacao = new WebMarkupContainer("abaClassificacao");
        containerClassificacao.setOutputMarkupId(true);

        AjaxFallbackLink button = newLinkClassificacao(); // btnClassificacao
        containerClassificacao.add(button);
        containerClassificacao.setVisible(listasPublicadasElegibilidade.size() == 2);
        return containerClassificacao;
    }

    private WebMarkupContainer newContainerAlert() {
        containerAlert = new WebMarkupContainer("containerAlert");

        List<ProgramaHistoricoPublicizacao> historico = new ArrayList<ProgramaHistoricoPublicizacao>();
        if(programa != null && programa.getId() != null){
            historico = programaService.buscarHistoricoPublicizacao(programa);
        } 

        String mensagem = "";
        if (historico == null || historico.isEmpty()) {
            mensagem = " - ";
        } else {
            LocalDate inicio = historico.get(0).getDataInicialCadastroLocalEntrega();
            LocalDate dataFinal = historico.get(0).getDataFinalCadastroLocalEntrega();
            mensagem = "O Programa está em fase de recebimento de locais de entrega que vai de " + dataFormatada(inicio) + " até " + dataFormatada(dataFinal) + " .";
        }

        Label lbl = componentFactory.newLabel("msgAlerta", mensagem);
        containerAlert.add(lbl);
        containerAlert.setVisible(programa.getStatusPrograma() == EnumStatusPrograma.ABERTO_REC_LOC_ENTREGA);

        return containerAlert;
    }

    private void initComponents() {
        PropostaPesquisaDto pesquisa = new PropostaPesquisaDto();
        pesquisa.getInscricao().setPrograma(programa);
        form = componentFactory.newForm("form", pesquisa);
        add(form);

        // painel

        panelAbas = new PanelAbas("panelAbas");
        panelFasePrograma = new PanelFasePrograma("panelFasePrograma", programa, backPage, abaClicada);
        panelAbaElegibilidade = new PanelAbaElegibilidade("panelAbaElegibilidade");
        panelAbaClassificacao = new PanelAbaClassificacao("panelAbaClassificacao");

        form.add(panelAbas);
        form.add(panelFasePrograma);
        form.add(panelAbaElegibilidade);
        form.add(panelAbaClassificacao);

        form.add(newContainerAlert()); // containerAlert
    }

    // COMPONENTES

    // BOTÕES DE CADA UMA DAS ABAS
    private AjaxFallbackLink<Void> newLinkElegibilidade() {
        AjaxFallbackLink<Void> btnElegibilidade = componentFactory.newAjaxFallbackLink("btnElegibilidade", (target) -> actionAba(target, "abaElegibilidade"));
        btnElegibilidade.setOutputMarkupId(true);

        return btnElegibilidade;
    }

    private AjaxFallbackLink<Void> newLinkClassificacao() {
        AjaxFallbackLink<Void> btnClassificacao = componentFactory.newAjaxFallbackLink("btnClassificados", (target) -> actionAba(target, "abaClassificacao"));
        btnClassificacao.setOutputMarkupId(true);

        return btnClassificacao;
    }

    // AÇÕES

    private String dataFormatada(LocalDate data) {
        DateTimeFormatter sdfPadraoBR = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        if (data != null) {
            data.format(sdfPadraoBR);
            return sdfPadraoBR.format(data);
        }
        return " - ";

    }

    private void voltar() {
        setResponsePage(backPage);
    }

    public void actionAba(AjaxRequestTarget target, String abaClicada) {

        // Se a aba clicada agora for igual a última clicada não atualizar nada.
        if (abaClicada.equalsIgnoreCase(abaAnterior)) {
            return;
        }

        target.appendJavaScript("ocultarAba('abaElegibilidade');");

        if (abaAnterior != null || "".equalsIgnoreCase(abaAnterior)) {

            // oculta a aba Anterior
            target.appendJavaScript("ocultarAba('" + abaAnterior + "');");
        }

        // Mostra a aba clicada
        target.appendJavaScript("mostrarAba('" + abaClicada + "');");

        abaAnterior = abaClicada;
        atualizarAbas(target, abaClicada);
    }

    private void atualizarAbas(AjaxRequestTarget target, String aba) {

        if ("abaElegibilidade".equalsIgnoreCase(aba)) {
            containerElegibilidade.add(classeActive);
            containerClassificacao.add(classeInactive);
        } else {
            containerClassificacao.add(classeActive);
            containerElegibilidade.add(classeInactive);
        }

        containerClassificacao.addOrReplace(newLinkClassificacao());
        containerElegibilidade.addOrReplace(newLinkElegibilidade());
        target.add(containerElegibilidade);
        target.add(containerClassificacao);
    }

    private void ativarDesativarAbasAoIniciarAPrimeiraVez(AjaxRequestTarget target) {
       
        String abaASerAberta = "";
        if(analiseDto.getAbaClicada() == EnumAbaFaseAnalise.ELEGIBILIDADE){
            panelAbaElegibilidade.add(classeActive);
            containerElegibilidade.add(classeActive);
            abaASerAberta = "abaElegibilidade";
        }else{
            panelAbaClassificacao.add(classeActive);
            containerClassificacao.add(classeActive);
            abaASerAberta="abaClassificacao";
        }
        actionAba(target, abaASerAberta);
    }
}
