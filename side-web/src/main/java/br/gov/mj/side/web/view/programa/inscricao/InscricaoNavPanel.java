package br.gov.mj.side.web.view.programa.inscricao;

import javax.inject.Inject;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraAjaxFallbackLink;
import br.gov.mj.side.entidades.enums.EnumStatusInscricao;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoPrograma;
import br.gov.mj.side.web.view.programa.inscricao.locaisEntrega.EntregaInscricaoProgramaPage;
import br.gov.mj.side.web.view.programa.inscricao.membroComissaoRecebimento.ComissaoRecebimentoPage;

public class InscricaoNavPanel extends Panel {

    private static final long serialVersionUID = 1L;

    @Inject
    private ComponentFactory componentFactory;

    public InscricaoNavPanel(String id, InscricaoPrograma inscricao, Page backPage, Page page) {
        super(id);
        boolean permissaoFase = Boolean.FALSE;
        if (inscricao != null && inscricao.getStatusInscricao() != null) {
            if (!inscricao.getStatusInscricao().equals(EnumStatusInscricao.EM_ELABORACAO)) {
                permissaoFase = Boolean.TRUE;
            }
        }

        // 1- Inscrição
        InfraAjaxFallbackLink<Void> lnkInscricao = componentFactory.newAjaxFallbackLink("lnkInscricao", (target) -> setResponsePage(new InscricaoProgramaPage(new PageParameters(), inscricao, backPage, true)));
        WebMarkupContainer containerInscricao = new WebMarkupContainer("containerInscricao");
        containerInscricao.add(lnkInscricao);
        add(containerInscricao);

        // 2- Endereços de Entrega
        InfraAjaxFallbackLink<Void> lnkEntrega = componentFactory.newAjaxFallbackLink("lnkEntrega", (target) -> setResponsePage(new EntregaInscricaoProgramaPage(new PageParameters(), inscricao)));
        WebMarkupContainer containerEntrega = new WebMarkupContainer("containerEntrega");
        containerEntrega.add(lnkEntrega);
        containerEntrega.setVisible(permissaoFase);
        add(containerEntrega);

        // 3- Comissão de Recebimento
        InfraAjaxFallbackLink<Void> lnkComissaoRecebimento = componentFactory.newAjaxFallbackLink("lnkComissaoRecebimento", (target) -> setResponsePage(new ComissaoRecebimentoPage(new PageParameters(), inscricao)));
        WebMarkupContainer containerComissaoRecebimento = new WebMarkupContainer("containerComissaoRecebimento");
        containerComissaoRecebimento.add(lnkComissaoRecebimento);
        containerComissaoRecebimento.setVisible(permissaoFase);
        add(containerComissaoRecebimento);

        // 4- Recebimento
        InfraAjaxFallbackLink<Void> lnkRegistrarRecebimento = componentFactory.newAjaxFallbackLink("lnkRegistrarRecebimento", (target) -> setResponsePage(new RegistrarRecebimentoPage(new PageParameters(), inscricao)));
        WebMarkupContainer containerRegistrarRecebimento = new WebMarkupContainer("containerRegistrarRecebimento");
        containerRegistrarRecebimento.add(lnkRegistrarRecebimento);
        containerRegistrarRecebimento.setVisible(permissaoFase);
        add(containerRegistrarRecebimento);

        if (page instanceof InscricaoProgramaPage) {
            containerInscricao.add(AttributeModifier.append("class", "active"));
        } else if (page instanceof EntregaInscricaoProgramaPage) {
            containerEntrega.add(AttributeModifier.append("class", "active"));
        } else if (page instanceof ComissaoRecebimentoPage) {
            containerComissaoRecebimento.add(AttributeModifier.append("class", "active"));
        } else if (page instanceof RegistrarRecebimentoPage) {
            containerRegistrarRecebimento.add(AttributeModifier.append("class", "active"));
        }
    }

}
