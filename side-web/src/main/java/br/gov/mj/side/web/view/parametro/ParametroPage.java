package br.gov.mj.side.web.view.parametro;

import org.apache.wicket.Page;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import br.gov.mj.apoio.entidades.Parametro;
import br.gov.mj.side.web.view.template.TemplatePage;

/**
 * INCOMPLETO
 * 
 * @author alexandre.martins
 *
 */

public class ParametroPage extends TemplatePage {

	private static final long serialVersionUID = 1L;

	public ParametroPage(PageParameters pageParameters, Parametro entidade, Page backPage, boolean readOnly) {
		super(pageParameters);

	}

}
