package br.gov.mj.side.web.view.beneficiario.paineis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.StringValidator;

import br.gov.mj.apoio.entidades.Municipio;
import br.gov.mj.apoio.entidades.TipoEndereco;
import br.gov.mj.apoio.entidades.TipoEntidade;
import br.gov.mj.apoio.entidades.Uf;
import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraDropDownChoice;
import br.gov.mj.side.entidades.enums.EnumPersonalidadeJuridica;
import br.gov.mj.side.entidades.enums.EnumStatusEntidade;
import br.gov.mj.side.web.dto.EntidadeDto;
import br.gov.mj.side.web.service.MunicipioService;
import br.gov.mj.side.web.service.TipoEnderecoService;
import br.gov.mj.side.web.service.TipoEntidadeService;
import br.gov.mj.side.web.service.UfService;
import br.gov.mj.side.web.view.beneficiario.BeneficiarioPage;

public class PanelDadosEntidade extends Panel {
	private static final long serialVersionUID = 1L;

	private static final String ONCHANGE = "onchange";

	private EntidadeDto entidade;

	private PanelPrincipalEntidade panelPrincipalEntidade;
	private PanelUfMunicipio panelUfMunicipio;

	private AttributeAppender classDropDown = new AttributeAppender("class", "js-example-basic-single", " ");
	private Uf nomeUf = new Uf();
	private Boolean mostrarDropDownStatusEntidade = true;

	@Inject
	private ComponentFactory componentFactory;
	@Inject
	private UfService ufService;
	@Inject
	private MunicipioService municipioService;
	@Inject
	private TipoEntidadeService tipoEntidadeService;
	@Inject
	private TipoEnderecoService tipoEnderecoService;

	public PanelDadosEntidade(String id, Page backPage, EntidadeDto entidade) {
		super(id);
		this.entidade = entidade;

		initVariaveis();
		setOutputMarkupId(true);

		add(panelPrincipalEntidade = new PanelPrincipalEntidade("panelPrincipalEntidade"));
	}

	private void initVariaveis() {
		if (entidade.getId() != null) {
			nomeUf = entidade.getMunicipio().getUf();
		} else {
			// Se for um cadastro novo
			entidade.setStatusEntidade(EnumStatusEntidade.ATIVA);
			mostrarDropDownStatusEntidade = false;
		}
	}

	// PAINEIS

	private class PanelPrincipalEntidade extends WebMarkupContainer {

		private static final long serialVersionUID = 1L;

		public PanelPrincipalEntidade(String id) {
			super(id);
			setOutputMarkupId(true);

			add(getDropDownTipo()); // tipoEntidade
			add(getTextFieldNome()); // nomeEntidade
			add(getDropDownNaturezaJuridica()); // tipoNaturezaJuridica
			add(getTextFieldEndereco()); // descricaoEndereco
			add(getDropDownStatus()); // statusEntidade
			add(getLabelAtivar()); // lblAtivar

			panelUfMunicipio = new PanelUfMunicipio("panelUfMunicipio");
			add(panelUfMunicipio);

			add(getDropDownTipoEndereco());// tipoEndereco
			add(getTextFieldNumero()); // numeroEndereco
			add(getTextFieldComplemento()); // complementoEndereco
			add(getTextFieldBairro()); // bairro
			add(getTextFieldCep()); // numeroCep
			add(getTextFieldTelefone()); // numeroTelefone
			add(getTextFieldTelefoneFax()); // numeroFoneFax
			add(getTextFieldEmail()); // email
			add(getLabelProcessoSei()); //lblProcessoSei
			add(getTextFieldNumeroProcesso()); // processo
		}
	}

	private class PanelUfMunicipio extends WebMarkupContainer {
		private static final long serialVersionUID = 1L;

		public PanelUfMunicipio(String id) {
			super(id);
			setOutputMarkupId(true);
			add(getDropDownUf()); // nomeUf
			add(getDropDownCidade()); // nomeMunicipio
		}
	}

	// COMPONENTES

	public DropDownChoice<TipoEntidade> getDropDownTipo() {
		List<TipoEntidade> lista = new ArrayList<TipoEntidade>();
		lista = tipoEntidadeService.buscarTodos();

		InfraDropDownChoice<TipoEntidade> drop = componentFactory.newDropDownChoice("tipoEntidade", "Tipo", false, "id", "descricaoTipoEntidade", null, lista, null);
		drop.setNullValid(true);
		drop.setOutputMarkupId(true);
		return drop;
	}

	public TextField<String> getTextFieldNome() {
		TextField<String> field = componentFactory.newTextField("nomeEntidade", "Nome (Entidade)", false, null);
		field.add(StringValidator.maximumLength(200));
		return field;
	}

	@SuppressWarnings("unchecked")
	public DropDownChoice<EnumPersonalidadeJuridica> getDropDownNaturezaJuridica() {
		List<EnumPersonalidadeJuridica> listaTemp = Arrays.asList(EnumPersonalidadeJuridica.values());
		List<EnumPersonalidadeJuridica> lista = new ArrayList<EnumPersonalidadeJuridica>();
		for (EnumPersonalidadeJuridica temp : listaTemp) {
			if (!"T".equalsIgnoreCase(temp.getValor())) {
				lista.add(temp);
			}
		}

		InfraDropDownChoice<EnumPersonalidadeJuridica> drop = componentFactory.newDropDownChoice("personalidadeJuridica", "Natureza Jurídica", false, "valor", "descricao", null,
				lista, null);
		drop.setChoiceRenderer(criarRendererNaturezaJuridica());
		drop.setNullValid(true);
		drop.setOutputMarkupId(true);
		return drop;
	}

	private TextField<String> getTextFieldEndereco() {
		TextField<String> field = componentFactory.newTextField("descricaoEndereco", "Endereço", false, null);
		field.add(StringValidator.maximumLength(200));
		return field;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public DropDownChoice<Uf> getDropDownUf() {
		List<Uf> lista = ufService.buscarTodos();
		InfraDropDownChoice<Uf> drop = componentFactory.newDropDownChoice("nomeUf", "Estado", false, "id", "nomeUf", new PropertyModel(this, "nomeUf"), lista, null);
		drop.setChoiceRenderer(new ChoiceRenderer<Uf>("nomeSigla", "id"));
		actionDropDownUf(drop);
		drop.setNullValid(true);
		drop.add(classDropDown);
		drop.setOutputMarkupId(true);
		drop.setOutputMarkupId(true);
		return drop;
	}

	public DropDownChoice<Municipio> getDropDownCidade() {
		List<Municipio> lista = new ArrayList<Municipio>();
		if (nomeUf != null && nomeUf.getId() != null) {
			lista = municipioService.buscarPelaUfId(nomeUf.getId());
		} else {
			this.entidade.setMunicipio(null);
		}

		InfraDropDownChoice<Municipio> drop = componentFactory.newDropDownChoice("municipio", "Municipio", false, "id", "nomeMunicipio", null, lista, null);
		drop.setNullValid(true);
		drop.setOutputMarkupId(true);
		drop.add(classDropDown);
		return drop;
	}

	public DropDownChoice<TipoEndereco> getDropDownTipoEndereco() {
		List<TipoEndereco> lista = new ArrayList<TipoEndereco>();
		lista = tipoEnderecoService.buscarTodos();

		InfraDropDownChoice<TipoEndereco> drop = componentFactory.newDropDownChoice("tipoEndereco", "Tipo de Endereço", false, "id", "descricaoTipoEndereco", null, lista, null);
		drop.setNullValid(true);
		drop.setOutputMarkupId(true);
		return drop;
	}

	private TextField<String> getTextFieldNumero() {
		TextField<String> field = componentFactory.newTextField("numeroEndereco", "Número", false, null);
		field.add(StringValidator.maximumLength(200));
		return field;
	}

	private TextField<String> getTextFieldComplemento() {
		TextField<String> field = componentFactory.newTextField("complementoEndereco", "Complemento", false, null);
		field.add(StringValidator.maximumLength(200));
		return field;
	}

	private TextField<String> getTextFieldBairro() {
		TextField<String> field = componentFactory.newTextField("bairro", "Bairro", false, null);
		field.add(StringValidator.maximumLength(200));
		return field;
	}

	private TextField<String> getTextFieldCep() {
		TextField<String> field = componentFactory.newTextField("numeroCep", "CEP", false, null);
		field.add(StringValidator.maximumLength(9));
		return field;
	}

	private TextField<String> getTextFieldTelefone() {
		TextField<String> field = componentFactory.newTextField("numeroTelefone", "Telefone (Entidade)", false, null);
		field.add(StringValidator.maximumLength(13));
		return field;
	}

	private TextField<String> getTextFieldTelefoneFax() {
		TextField<String> field = componentFactory.newTextField("numeroFoneFax", "Telefone/FAX", false, null);
		field.add(StringValidator.maximumLength(13));
		return field;
	}

	private TextField<String> getTextFieldEmail() {
		TextField<String> field = componentFactory.newTextField("email", "E-mail (Entidade)", false, null);
		field.add(StringValidator.maximumLength(200));
		return field;
	}

	private TextField<String> getTextFieldNumeroProcesso() {
		TextField<String> field = componentFactory.newTextField("numeroProcessoSEI", "Nº Processo (NUP)", false, null);
		field.add(StringValidator.maximumLength(20));
		field.setVisible(entidade.getUsuario()!=null);
		return field;
	}
	
	private Label getLabelProcessoSei(){
	    Label lbl = new Label("lblProcessoSei","*Nº Processo (NUP)");
	    lbl.setVisible(entidade.getUsuario()!=null);
	    return lbl;
	}

	private InfraDropDownChoice<EnumStatusEntidade> getDropDownStatus() {
		List<EnumStatusEntidade> lista = Arrays.asList(EnumStatusEntidade.values());
		InfraDropDownChoice<EnumStatusEntidade> dropDownChoice = componentFactory.newDropDownChoice("statusEntidade", "Ativar Entidade", false, "valor", "descricao", null, lista,
				null);
		dropDownChoice.setNullValid(true);
		dropDownChoice.setVisible(mostrarDropDownStatusEntidade);
		return dropDownChoice;
	}

	private Label getLabelAtivar() {
		Label labelAtivar = new Label("lblAtivar", "* Ativar Entidade");
		labelAtivar.setVisible(mostrarDropDownStatusEntidade);
		return labelAtivar;
	}

	// AÇÕES

	@SuppressWarnings("rawtypes")
	private IChoiceRenderer criarRendererNaturezaJuridica() {
		return new IChoiceRenderer<EnumPersonalidadeJuridica>() {
			private static final long serialVersionUID = 1L;

			@Override
			public Object getDisplayValue(EnumPersonalidadeJuridica object) {
				if ("I".equalsIgnoreCase(object.getValor())) {
					return "Privada Sem Fins Lucrativos";
				} else {
					return object.getDescricao();
				}
			}

			@Override
			public String getIdValue(EnumPersonalidadeJuridica object, int index) {
				return object.toString();
			}
		};
	}

	public void actionDropDownUf(DropDownChoice<Uf> drop) {
		drop.add(new AjaxFormComponentUpdatingBehavior(ONCHANGE) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {

				panelUfMunicipio.addOrReplace(getDropDownCidade());
				target.add(panelUfMunicipio);
			}
		});
	}

	public Uf getNomeUf() {
		return nomeUf;
	}

	public void setNomeUf(Uf nomeUf) {
		this.nomeUf = nomeUf;
	}

}
