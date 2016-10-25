package br.gov.mj.side.web.view.planejarLicitacao;

import javax.inject.Inject;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraAjaxFallbackLink;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.web.view.contrato.ContratoPesquisaPage;
import br.gov.mj.side.web.view.programa.contrato.formatacaoitens.FormatacaoItensPesquisaPage;
import br.gov.mj.side.web.view.programa.contrato.ordemfornecimento.OrdemFornecimentoPesquisaPage;

import com.googlecode.wicket.jquery.ui.form.button.AjaxButton;

public class ContratoPanelBotoes extends Panel {
    private static final long serialVersionUID = 1L;

    private PanelPrincipal panelPrincipal;

    private Page backPage;
    private Programa programa;
    private String botaoClicado = "";

    private AttributeModifier classeActivePlanejamento = new AttributeModifier("class", "btn btn-primary btn-sm");
    private AttributeModifier classeActiveContOs = new AttributeModifier("class", "btn btn-primary btn-sm custom");
    private AttributeModifier classeActiveContFormatacaoItens = new AttributeModifier("class", "btn btn-primary btn-sm custom");
    private AttributeModifier classeActiveContComunicarFornecedor = new AttributeModifier("class", "btn btn-primary btn-sm custom");
    @Inject
    private ComponentFactory componentFactory;

    public ContratoPanelBotoes(String id, Programa programa, Page backPage, String botaoClicado) {
        super(id);
        this.backPage = backPage;
        this.programa = programa;
        this.botaoClicado = botaoClicado;

        initComponents();
    }

    private void initComponents() {
        panelPrincipal = new PanelPrincipal("panelPrincipal");
        add(panelPrincipal);
    }

    private class PanelPrincipal extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelPrincipal(String id) {
            super(id);
            setOutputMarkupId(true);

            add(newButtonPlanejamento()); // btnPlanejamento
            add(newButtonContrato()); // btnContrato
            add(newButtonFormatacaoItens()); // btnFormatacaoItens
            add(newButtonOs()); // btnOs
        }
    }

    // Cada WebMarkupContainer deste é um <li> no Html
    // OS COMPONENTES VIRÃO ABAIXO
    
    // 1.Planejamento
    private InfraAjaxFallbackLink<Void> newButtonPlanejamento() {
        InfraAjaxFallbackLink<Void> btn = componentFactory.newAjaxFallbackLink("btnPlanejamento", (target) -> acaoBotao(target, "planejamento"));

        btn.setOutputMarkupId(true);
        if ("planejamento".equalsIgnoreCase(botaoClicado)) {
            btn.add(classeActivePlanejamento);
        }

        return btn;
    }

    // 2.Contrato
    private InfraAjaxFallbackLink<Void> newButtonContrato() {
        InfraAjaxFallbackLink<Void> btn = componentFactory.newAjaxFallbackLink("btnContrato", (target) -> acaoBotao(target, "contrato"));

        btn.setOutputMarkupId(true);
        if ("contrato".equalsIgnoreCase(botaoClicado)) {
            btn.add(classeActiveContOs);
        }

        btn.setOutputMarkupId(true);
        return btn;
    }

    //3.Formatação de Itens
    private InfraAjaxFallbackLink<Void> newButtonFormatacaoItens() {
        InfraAjaxFallbackLink<Void> btn = componentFactory.newAjaxFallbackLink("btnFormatacaoItens", (target) -> acaoBotao(target, "formatacaoItens"));

        btn.setOutputMarkupId(true);
        if ("formatacaoItens".equalsIgnoreCase(botaoClicado)) {
            btn.add(classeActiveContFormatacaoItens);
        }

        btn.setOutputMarkupId(true);
        return btn;
    }
    
    //4.Ordem de Fornecimento
    private InfraAjaxFallbackLink<Void> newButtonOs() {
        InfraAjaxFallbackLink<Void> btn = componentFactory.newAjaxFallbackLink("btnOs", (target) -> acaoBotao(target, "ordemFornecimento"));

        btn.setOutputMarkupId(true);
        if ("ordemFornecimento".equalsIgnoreCase(botaoClicado)) {
            btn.add(classeActiveContOs);
        }

        btn.setOutputMarkupId(true);
        return btn;
    }

    // AÇÕES

    private void acaoBotao(AjaxRequestTarget target, String botaoClicado) {

        if ("planejamento".equalsIgnoreCase(botaoClicado)) {
            setResponsePage(new PlanejamentoLicitacaoPage(new PageParameters(), backPage, programa, target, 4));
        }

        if ("contrato".equalsIgnoreCase(botaoClicado)) {
            setResponsePage(new ContratoPesquisaPage(new PageParameters(), programa, backPage));
        }

        if ("formatacaoItens".equalsIgnoreCase(botaoClicado)) {
            setResponsePage(new FormatacaoItensPesquisaPage(new PageParameters(), programa, backPage));
        }

        if ("ordemFornecimento".equalsIgnoreCase(botaoClicado)) {
            setResponsePage(new OrdemFornecimentoPesquisaPage(new PageParameters(), programa, backPage));
        }
    }
}
