package br.gov.mj.side.web.view.programa.inscricao.locaisEntrega;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;

import br.gov.mj.side.web.view.dashboard.InformacoesEntidadePage;
import br.gov.mj.side.web.view.dashboard.InformacoesRepresentantesPage;
import br.gov.mj.side.web.view.programa.inscricao.membroComissaoRecebimento.CadastrarMembroComissaoPage;

public class EntidadeNavPanel extends Panel {

    private static final long serialVersionUID = 1L;

    public EntidadeNavPanel(String id, Page page) {
        super(id);

        Link<Void> lnkInformacoesEntidade = new Link<Void>("lnkInformacoesEntidade") {
            private static final long serialVersionUID = 1L;
            
            @Override
            public void onClick() {
                setResponsePage(InformacoesEntidadePage.class);
            }
        };
        WebMarkupContainer containerInformacoesEntidade = new WebMarkupContainer("containerInformacoesEntidade");
        containerInformacoesEntidade.add(lnkInformacoesEntidade);
        add(containerInformacoesEntidade);

        Link<Void> lnkLocaisEntrega = new Link<Void>("lnkLocaisEntrega") {
            private static final long serialVersionUID = 1L;
            
            @Override
            public void onClick() {
                setResponsePage(LocaisEntregaPage.class);
            }
        };
        WebMarkupContainer containerLocaisEntrega = new WebMarkupContainer("containerLocaisEntrega");
        containerLocaisEntrega.add(lnkLocaisEntrega);
        add(containerLocaisEntrega);

        Link<Void> lnkRepresentantes = new Link<Void>("lnkRepresentantes") {
            private static final long serialVersionUID = 1L;
            
            @Override
            public void onClick() {
                setResponsePage(InformacoesRepresentantesPage.class);
            }
        };
        WebMarkupContainer containerRepresentantes = new WebMarkupContainer("containerRepresentantes");
        containerRepresentantes.add(lnkRepresentantes);
        add(containerRepresentantes);
        
       Link<Void> lnkMembroComissaoRecebimento = new Link<Void>("lnkMembroComissaoRecebimento") {
            private static final long serialVersionUID = 1L;
            
            @Override
            public void onClick() {
                setResponsePage(CadastrarMembroComissaoPage.class);
            }
        };
        WebMarkupContainer containerMembroComissaoRecebimento = new WebMarkupContainer("containerMembroComissaoRecebimento");
        containerMembroComissaoRecebimento.add(lnkMembroComissaoRecebimento);
        add(containerMembroComissaoRecebimento);

        // Adiciona atributo para ativar a aba
        if (page instanceof InformacoesEntidadePage) {
            containerInformacoesEntidade.add(AttributeModifier.append("class", "active"));
        } else if (page instanceof InformacoesRepresentantesPage) {
            containerRepresentantes.add(AttributeModifier.append("class", "active"));
        } else if (page instanceof LocaisEntregaPage) {
            containerLocaisEntrega.add(AttributeModifier.append("class", "active"));
        }else if (page instanceof CadastrarMembroComissaoPage) {
        	containerMembroComissaoRecebimento.add(AttributeModifier.append("class", "active"));
        } 
    }
}
