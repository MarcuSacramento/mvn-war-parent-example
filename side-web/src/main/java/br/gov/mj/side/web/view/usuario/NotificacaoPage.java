package br.gov.mj.side.web.view.usuario;

import org.apache.wicket.request.mapper.parameter.PageParameters;

import br.gov.mj.side.web.view.template.TemplatePage;

public class NotificacaoPage extends TemplatePage {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public NotificacaoPage(final PageParameters pageParameters) {
        super(pageParameters);
        setTitulo("Gerenciar Notificações");

    }
}
