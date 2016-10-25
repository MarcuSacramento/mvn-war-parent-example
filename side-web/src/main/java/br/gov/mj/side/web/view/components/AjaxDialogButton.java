package br.gov.mj.side.web.view.components;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.model.IModel;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.ButtonBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;

public abstract class AjaxDialogButton extends AjaxLink<String> {
    private static final long serialVersionUID = 6969235733660381915L;

    private final ButtonBehavior buttonBehavior;

    public AjaxDialogButton(IModel<String> model, Buttons.Type type) {
        super("button", model);

        setBody(getDefaultModel());
        buttonBehavior = new ButtonBehavior(type);
        add(buttonBehavior);
    }

    @Override
    public abstract void onClick(AjaxRequestTarget target);
}
