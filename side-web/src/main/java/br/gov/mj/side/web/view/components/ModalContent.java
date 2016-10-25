package br.gov.mj.side.web.view.components;

import org.apache.wicket.PageReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;

public class ModalContent extends WebPage {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private PanelAdicionarBem panelAdicionarBem;
    private String quantidade;

    public ModalContent(final PageReference modalWindowPage, final ModalWindow window) {

        add(new AjaxLink<Void>("closeOK") {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                if (modalWindowPage != null)
                    window.close(target);
            }
        });

        panelAdicionarBem = new PanelAdicionarBem("panelAdicionarBem");
        add(panelAdicionarBem);
    }

    private class PanelAdicionarBem extends WebMarkupContainer {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public PanelAdicionarBem(String id) {
            super(id);
            setOutputMarkupId(true);
            add(getTextFieldQuantidade());// quantidade
        }
    }

    public TextField<String> getTextFieldQuantidade() {
        return new TextField<String>("quantidade", new PropertyModel<String>(this, "quantidade"));
    }

    public String getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(String quantidade) {
        this.quantidade = quantidade;
    }
}
