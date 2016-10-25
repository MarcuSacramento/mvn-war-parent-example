package br.gov.mj.side.web.view.execucao;

import javax.inject.Inject;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraAjaxFallbackLink;
import br.gov.mj.side.entidades.programa.Programa;


public class ExecucaoPanelBotoes extends Panel {
    private static final long serialVersionUID = 1L;

    private PanelPrincipal panelPrincipal;

    private Page backPage;
    private Programa programa;
    private String botaoClicado = "";

    private AttributeModifier classeActiveAnaliseEntrega = new AttributeModifier("class", "btn btn-primary btn-sm");
    @Inject
    private ComponentFactory componentFactory;

    public ExecucaoPanelBotoes(String id, Programa programa, Page backPage, String botaoClicado) {
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
            add(newButtonTermoDoacao());
            add(newButtonConfirmarDoacao());
        }
    }

    // Cada WebMarkupContainer deste é um <li> no Html
    // OS COMPONENTES VIRÃO ABAIXO
    
    // 1.Analise Entrega
    private InfraAjaxFallbackLink<Void> newButtonPlanejamento() {
        InfraAjaxFallbackLink<Void> btn = componentFactory.newAjaxFallbackLink("btnAnaliseEntrega", (target) -> acaoBotao(target, "AnaliseEntrega"));

        btn.setOutputMarkupId(true);
        if ("AnaliseEntrega".equalsIgnoreCase(botaoClicado)) {
            btn.add(classeActiveAnaliseEntrega);
        }

        return btn;
    }
    
    // 2. Termo de Doação
    private InfraAjaxFallbackLink<Void> newButtonTermoDoacao() {
        InfraAjaxFallbackLink<Void> btn = componentFactory.newAjaxFallbackLink("btnTermoDoacao", (target) -> acaoBotao(target, "TermoDoacao"));

        btn.setOutputMarkupId(true);
        if ("TermoDoacao".equalsIgnoreCase(botaoClicado)) {
            btn.add(classeActiveAnaliseEntrega);
        }

        return btn;
    }
    
    
    // 3. Confirmar Doação
    private InfraAjaxFallbackLink<Void> newButtonConfirmarDoacao() {
        InfraAjaxFallbackLink<Void> btn = componentFactory.newAjaxFallbackLink("btnConfirmarDoacao", (target) -> acaoBotao(target, "ConfirmarDoacao"));

        btn.setOutputMarkupId(true);
        if ("ConfirmarDoacao".equalsIgnoreCase(botaoClicado)) {
            btn.add(classeActiveAnaliseEntrega);
        }

        return btn;
    }

    // AÇÕES

    private void acaoBotao(AjaxRequestTarget target, String botaoClicado) {

        if ("AnaliseEntrega".equalsIgnoreCase(botaoClicado)) {
            setResponsePage(new AcompanharOrdemFornecimentoPage(new PageParameters(), programa, backPage,5));
        }else  if ("TermoDoacao".equalsIgnoreCase(botaoClicado)) {
            setResponsePage(new TermoDoacaoPage(new PageParameters(), programa, backPage,5));
        }else  if ("ConfirmarDoacao".equalsIgnoreCase(botaoClicado)) {
            setResponsePage(new ConfirmarDoacaoPage(new PageParameters(), programa, backPage,5));
        }
    }
}
