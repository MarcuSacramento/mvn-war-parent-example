package br.gov.mj.side.web.view.components;

import java.util.Locale;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.resource.ContextRelativeResource;

public class LanguagePanel extends Panel {
    private static final long serialVersionUID = 1L;

    public LanguagePanel(String id) {
        super(id);

        Form<Void> languageForm = new Form<Void>("languageForm");

        SubmitLink btnPtBr = newSubmitLink("btnPtBr", "pt");

        languageForm.add(btnPtBr);
        btnPtBr.add(newImage("imgPtBr", "/images/br.png"));

        SubmitLink btnEn = newSubmitLink("btnEn", "en");
        languageForm.add(btnEn);
        btnEn.add(newImage("imgEn", "/images/en.png"));

        SubmitLink btnFr = newSubmitLink("btnFr", "fr");
        languageForm.add(btnFr);
        btnFr.add(newImage("imgFr", "/images/fr.png"));

        SubmitLink btnEs = newSubmitLink("btnEs", "es");
        languageForm.add(btnEs);
        btnEs.add(newImage("imgEs", "/images/es.png"));
        add(languageForm);

    }

    private Image newImage(String id, String pathRelativeToContextRoot) {
        return new Image(id, new ContextRelativeResource(pathRelativeToContextRoot));
    }

    private SubmitLink newSubmitLink(String id, String locale) {
        return new SubmitLink(id) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit() {
                changeUserLocaleTo(locale);
            }
        };
    }

    private void changeUserLocaleTo(String localeString) {
        getSession().setLocale(new Locale(localeString));
    }
}
