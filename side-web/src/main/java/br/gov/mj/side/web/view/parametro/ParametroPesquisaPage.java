package br.gov.mj.side.web.view.parametro;

import javax.inject.Inject;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import br.gov.mj.apoio.entidades.Parametro;
import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraAjaxFallbackLink;
import br.gov.mj.infra.wicket.component.pagination.InfraAjaxPagingNavigator;
import br.gov.mj.side.web.view.HomePage;
import br.gov.mj.side.web.view.template.TemplatePage;

/**
 * Classe desenvolvida para pesquisa de parâmetros.
 * 
 *  INCOMPLETO
 * 
 * @author alexandre.martins
 */

@AuthorizeInstantiation({ ParametroPesquisaPage.ROLE_MANTER_PARAMETRO_VISUALIZAR, ParametroPesquisaPage.ROLE_MANTER_PARAMETRO_INCLUIR,
		ParametroPesquisaPage.ROLE_MANTER_PARAMETRO_ALTERAR })
public class ParametroPesquisaPage extends TemplatePage {

	private static final long serialVersionUID = 1L;

	public static final String ROLE_MANTER_PARAMETRO_VISUALIZAR = "manter_parametro:visualizar";
	public static final String ROLE_MANTER_PARAMETRO_INCLUIR = "manter_parametro:incluir";
	public static final String ROLE_MANTER_PARAMETRO_ALTERAR = "manter_parametro:alterar";

	private Form<Parametro> form;
	private Parametro entity;
	private InfraAjaxFallbackLink<Void> btnNovo;
	private static final String SIGLA_PARAMETRO = "siglaParametro";
	private static final String VALOR_PARAMETRO = "valorParametro";
	private Button btnPesquisar;
	private boolean pesquisado = false;
	private PanelGridResultados panelGridResultados;

	@Inject
	private ComponentFactory componentFactory;

	public ParametroPesquisaPage(final PageParameters pageParameters) {
		super(pageParameters);
		setTitulo("Pesquisar Parâmetro");
		setEntity(new Parametro());
		initComponets();
	}

	protected void initComponets() {
		form = componentFactory.newForm("form", new CompoundPropertyModel<Parametro>(entity));

		form.add(componentFactory.newLink("lnkDashboard", HomePage.class));

		btnNovo = getButtonNovo();
		authorize(btnNovo, RENDER, ParametroPesquisaPage.ROLE_MANTER_PARAMETRO_INCLUIR);
		form.add(btnNovo);

		form.add(componentFactory.newTextField(SIGLA_PARAMETRO, "Sigla do Parâmetro", false, null));

		form.add(componentFactory.newTextField(VALOR_PARAMETRO, "Valor do Parâmetro", false, null));

		btnPesquisar = getButtonPesquisar();
		authorize(btnPesquisar, RENDER, ParametroPesquisaPage.ROLE_MANTER_PARAMETRO_VISUALIZAR);
		form.add(btnPesquisar);

		panelGridResultados = new PanelGridResultados("panelGridResultados");
		panelGridResultados.setOutputMarkupId(true);
		panelGridResultados.setVisible(isPesquisado());
		form.add(panelGridResultados);

		add(form);
	}

	private InfraAjaxFallbackLink<Void> getButtonNovo() {
		return componentFactory.newAjaxFallbackLink("btnAdicionarNovo", (target) -> adicionarNovo());
	}

	private void adicionarNovo() {
		setResponsePage(new ParametroPage(null, new Parametro(), this, false));
	}

	private Button getButtonPesquisar() {
		return componentFactory.newButton("btnPesquisar", () -> pesquisar());
	}

	private void pesquisar() {
		setPesquisado(true);
		panelGridResultados.setVisible(isPesquisado());
	}

	private class PanelGridResultados extends WebMarkupContainer {

		private static final long serialVersionUID = 1L;

		public PanelGridResultados(String id) {
			super(id);

			add(new InfraAjaxPagingNavigator("pagination", null));
		}

	}

	public Parametro getEntity() {
		return entity;
	}

	public void setEntity(Parametro entity) {
		this.entity = entity;
	}

	public boolean isPesquisado() {
		return pesquisado;
	}

	public void setPesquisado(boolean pesquisado) {
		this.pesquisado = pesquisado;
	}

}
