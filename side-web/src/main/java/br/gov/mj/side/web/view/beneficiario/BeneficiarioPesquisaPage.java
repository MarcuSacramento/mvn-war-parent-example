package br.gov.mj.side.web.view.beneficiario;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.validator.StringValidator;

import br.gov.mj.apoio.entidades.Municipio;
import br.gov.mj.apoio.entidades.TipoEntidade;
import br.gov.mj.apoio.entidades.Uf;
import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraAjaxConfirmButton;
import br.gov.mj.infra.wicket.component.InfraAjaxFallbackLink;
import br.gov.mj.infra.wicket.component.InfraDropDownChoice;
import br.gov.mj.infra.wicket.component.pagination.InfraAjaxPagingNavigator;
import br.gov.mj.infra.wicket.model.LambdaModel;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.entidade.EntidadeAnexo;
import br.gov.mj.side.entidades.entidade.PessoaEntidade;
import br.gov.mj.side.entidades.enums.EnumOrigemCadastro;
import br.gov.mj.side.entidades.enums.EnumPersonalidadeJuridica;
import br.gov.mj.side.entidades.enums.EnumStatusEntidade;
import br.gov.mj.side.entidades.enums.EnumStatusPessoa;
import br.gov.mj.side.entidades.enums.EnumStatusPrograma;
import br.gov.mj.side.entidades.enums.EnumValidacaoCadastro;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.web.dto.EntidadePesquisaDto;
import br.gov.mj.side.web.service.AnexoEntidadeService;
import br.gov.mj.side.web.service.BeneficiarioService;
import br.gov.mj.side.web.service.ProgramaService;
import br.gov.mj.side.web.service.TipoEntidadeService;
import br.gov.mj.side.web.service.UfService;
import br.gov.mj.side.web.util.CnpjUtil;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.MascaraUtils;
import br.gov.mj.side.web.util.SideUtil;
import br.gov.mj.side.web.util.SortableEntidadeDataProvider;
import br.gov.mj.side.web.view.template.TemplatePage;

@AuthorizeInstantiation({ BeneficiarioPesquisaPage.ROLE_MANTER_BENEFICIARIO_INCLUIR, BeneficiarioPesquisaPage.ROLE_MANTER_BENEFICIARIO_ALTERAR,
		BeneficiarioPesquisaPage.ROLE_MANTER_BENEFICIARIO_CONSULTAR ,BeneficiarioPesquisaPage.ROLE_MANTER_BENEFICIARIO_VALIDAR_CADASTRO })
public class BeneficiarioPesquisaPage extends TemplatePage {
	private static final long serialVersionUID = 1L;

	public static final String ROLE_MANTER_BENEFICIARIO_INCLUIR = "manter_beneficiario:incluir";
	public static final String ROLE_MANTER_BENEFICIARIO_ALTERAR = "manter_beneficiario:alterar";
	public static final String ROLE_MANTER_BENEFICIARIO_CONSULTAR = "manter_beneficiario:consultar";
	public static final String ROLE_MANTER_BENEFICIARIO_VALIDAR_CADASTRO = "manter_beneficiario:validar_cadastro";

	private PanelPrincipal panelPrincipal;
	private PanelGridResultados panelResultados;

	private Uf nomeUf = new Uf();

	private Form<EntidadePesquisaDto> form;
	private EntidadePesquisaDto entidade = new EntidadePesquisaDto();
	private SortableEntidadeDataProvider dp;
	private Integer itensPorPagina = Constants.ITEMS_PER_PAGE_PAGINATION;

	private DataView<Entidade> dataView;

	@Inject
	private ComponentFactory componentFactory;
	@Inject
	private TipoEntidadeService tipoEntidadeService;
	@Inject
	private UfService ufService;
	@Inject
	private BeneficiarioService beneficiarioService;	
	@Inject
	private AnexoEntidadeService anexoEntidadeService;
	@Inject
        private ProgramaService programaService;

	public BeneficiarioPesquisaPage(final PageParameters pageParameters) {
		super(pageParameters);
		setTitulo("Pesquisar Beneficiário");

		form = componentFactory.newForm("form", new CompoundPropertyModel<EntidadePesquisaDto>(entidade));

		InfraAjaxFallbackLink<Void> btnNovo = getButtonNovo();
		authorize(btnNovo, RENDER, BeneficiarioPesquisaPage.ROLE_MANTER_BENEFICIARIO_INCLUIR);

		Button btnPesquisar = getButtonPesquisar();
		authorize(btnPesquisar, RENDER, BeneficiarioPesquisaPage.ROLE_MANTER_BENEFICIARIO_CONSULTAR);

		panelPrincipal = new PanelPrincipal("panelPrincipal");
		panelResultados = new PanelGridResultados("panelResultados");
		panelResultados.setVisible(false);

		form.add(btnPesquisar);
		form.add(btnNovo);
		form.add(panelPrincipal);
		form.add(panelResultados);

		add(form);
	}

	// PAINEIS

	private class PanelPrincipal extends WebMarkupContainer {
		private static final long serialVersionUID = 1L;

		public PanelPrincipal(String id) {
			super(id);
			setOutputMarkupId(true);

			add(getTextFieldCnpj()); // entidade.numeroCnpj
			add(getTextFieldNomeEntidade()); // entidade.nomeEntidade
			add(getDropDownTipo()); // entidade.tipoEntidade
			add(getDropDownNaturezaJuridica()); // entidade.personalidadeJuridica
			add(getDropDownStatus()); // entidade.statusEntidade
			add(getDropDownSituacaoCadastro()); //entidade.validacaoCadastro
			add(getDropDownPrograma()); //entidade.programaPreferencial
			add(getTextFieldCpfRepresentante()); // representante.numeroCpf
			add(getTextFieldNomeRepresentante()); // representante.nomeRepresentante
			add(getTextFieldCpfTitular()); // titular.numeroCpf
			add(getTextFieldNomeTitular()); // titular.nomeTitular
			add(getDropDownUf()); // nomeUf
		}
	}

	private class PanelGridResultados extends WebMarkupContainer {
		private static final long serialVersionUID = 1L;

		public PanelGridResultados(String id) {
			super(id);
			setOutputMarkupId(true);
			form.getModelObject().setUsuarioLogado(getUsuarioLogadoDaSessao());
			dp = new SortableEntidadeDataProvider(beneficiarioService, form.getModelObject());
			add(getDataViewResultado(dp));
			add(getDropItensPorPagina()); // itensPorPagina
			add(new InfraAjaxPagingNavigator("pagination", dataView));

			add(new OrderByBorder<String>("orderByCnpj", "numeroCnpj", dp));
			add(new OrderByBorder<String>("orderByNome", "nomeEntidade", dp));
			add(new OrderByBorder<String>("orderByTipo", "tipoEntidade", dp));
			add(new OrderByBorder<String>("orderByEstado", "municipio.uf.siglaUf", dp));
			add(new OrderByBorder<String>("orderBySituacao", "statusEntidade", dp));
			add(new OrderByBorder<String>("orderByOrigemCadastro", "origemCadastro", dp));
		}
	}

	// COMPONENTES

	private TextField<String> getTextFieldCnpj() {
		TextField<String> field = componentFactory.newTextField("entidade.numeroCnpj", "CNPJ", false, null);
		field.add(StringValidator.maximumLength(18));
		return field;
	}

	private TextField<String> getTextFieldNomeEntidade() {
		TextField<String> field = componentFactory.newTextField("entidade.nomeEntidade", "Nome (Entidade)", false, null);
		field.add(StringValidator.maximumLength(200));
		return field;
	}

	public DropDownChoice<TipoEntidade> getDropDownTipo() {
		List<TipoEntidade> lista = new ArrayList<TipoEntidade>();
		lista = tipoEntidadeService.buscarTodos();

		InfraDropDownChoice<TipoEntidade> drop = componentFactory.newDropDownChoice("entidade.tipoEntidade", "Tipo", false, "id", "descricaoTipoEntidade", null, lista, null);
		drop.setNullValid(true);
		drop.setOutputMarkupId(true);
		return drop;
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

		InfraDropDownChoice<EnumPersonalidadeJuridica> drop = componentFactory.newDropDownChoice("entidade.personalidadeJuridica", "Natureza Jurídica", false, "valor",
				"descricao", null, lista, null);
		drop.setChoiceRenderer(criarRendererNaturezaJuridica());
		drop.setNullValid(true);
		drop.setOutputMarkupId(true);
		return drop;
	}

	public DropDownChoice<EnumStatusEntidade> getDropDownStatus() {
		List<EnumStatusEntidade> listaTemp = Arrays.asList(EnumStatusEntidade.values());
		InfraDropDownChoice<EnumStatusEntidade> drop = componentFactory
				.newDropDownChoice("entidade.statusEntidade", "Situação", false, "valor", "descricao", null, listaTemp, null);
		drop.setNullValid(true);
		drop.setOutputMarkupId(true);
		return drop;
	}
	
	public DropDownChoice<EnumValidacaoCadastro> getDropDownSituacaoCadastro() {
            List<EnumValidacaoCadastro> listaTemp = Arrays.asList(EnumValidacaoCadastro.values());
            InfraDropDownChoice<EnumValidacaoCadastro> drop = componentFactory
                            .newDropDownChoice("entidade.validacaoCadastro", "Situação do Cadastro", false, "valor", "descricao", null, listaTemp, null);
            drop.setNullValid(true);
            drop.setOutputMarkupId(true);
            return drop;
    }
	
	private InfraDropDownChoice<Programa> getDropDownPrograma() {
	        List<Programa> listaTemp = programaService.buscar(null);
	        
	        List<Programa> listaProgramas = new ArrayList<Programa>();
	        EnumStatusPrograma statusElaboracao = EnumStatusPrograma.EM_ELABORACAO;
                EnumStatusPrograma statusFormulado = EnumStatusPrograma.FORMULADO;
	        for(Programa prg:listaTemp){
	            if(prg.getStatusPrograma() != statusElaboracao && prg.getStatusPrograma() != statusFormulado){
	                listaProgramas.add(prg);
	            }
	        }
	        
	        InfraDropDownChoice<Programa> dropDownPrograma = componentFactory.newDropDownChoice("entidade.programaPreferencial", "Programa", false, "id", "codigoIdentificadorProgramaPublicadoENomePrograma", null, listaProgramas, null);
	        dropDownPrograma.setNullValid(true);
	        return dropDownPrograma;
	    }

	private TextField<String> getTextFieldCpfRepresentante() {
		TextField<String> field = componentFactory.newTextField("representante.numeroCpf", "CPF (Representante)", false, null);
		field.add(StringValidator.maximumLength(14));
		return field;
	}

	private TextField<String> getTextFieldNomeRepresentante() {
		TextField<String> field = componentFactory.newTextField("representante.nomePessoa", "Nome (Representante)", false, null);
		field.add(StringValidator.maximumLength(200));
		return field;
	}

	private TextField<String> getTextFieldCpfTitular() {
		TextField<String> field = componentFactory.newTextField("titular.numeroCpf", "CPF (Titular)", false, null);
		field.add(StringValidator.maximumLength(14));
		return field;
	}

	private TextField<String> getTextFieldNomeTitular() {
		TextField<String> field = componentFactory.newTextField("titular.nomePessoa", "Nome (Titular)", false, null);
		field.add(StringValidator.maximumLength(200));
		return field;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public DropDownChoice<Uf> getDropDownUf() {
		List<Uf> lista = ufService.buscarTodos();
		InfraDropDownChoice<Uf> drop = componentFactory.newDropDownChoice("nomeUf", "Estado", false, "id", "nomeUf", new PropertyModel(this, "nomeUf"), lista, null);
		drop.setChoiceRenderer(new ChoiceRenderer<Uf>("nomeSigla", "id"));
		drop.setNullValid(true);
		drop.setOutputMarkupId(true);
		return drop;
	}

	private DataView<Entidade> getDataViewResultado(SortableEntidadeDataProvider dp) {
		dataView = new DataView<Entidade>("entidades", dp) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<Entidade> item) {

				item.add(new Label("numeroCnpj", CnpjUtil.imprimeCNPJ(item.getModelObject().getNumeroCnpj())));
				item.add(new Label("nomeEntidade"));
				item.add(new Label("tipoEntidade", item.getModelObject().getTipoEntidade().getDescricaoTipoEntidade()));
				item.add(new Label("estado", item.getModelObject().getMunicipio().getUf().getNomeSigla()));
				item.add(new Label("statusEntidade.descricao"));
				item.add(new Label("origemCadastro",item.getModelObject().getOrigemCadastro().getDescricao()));

				Button btnAlterar = componentFactory.newButton("btnAlterar", () -> alterar(item));
				authorize(btnAlterar, RENDER, BeneficiarioPesquisaPage.ROLE_MANTER_BENEFICIARIO_ALTERAR);
				btnAlterar.setVisible(item.getModelObject().getOrigemCadastro() == EnumOrigemCadastro.CADASTRO_INTERNO);
				item.add(btnAlterar);
				
				Button btnAlterarPermissoes = componentFactory.newButton("btnAlterarPermissoes", () -> alterarPermissoes(item));
                                authorize(btnAlterarPermissoes, RENDER, BeneficiarioPesquisaPage.ROLE_MANTER_BENEFICIARIO_ALTERAR);
                                btnAlterarPermissoes.setVisible(item.getModelObject().getOrigemCadastro() == EnumOrigemCadastro.CADASTRO_EXTERNO);
                                item.add(btnAlterarPermissoes);

				Button btnVisualizar = getButtonVisualizar(item);
				authorize(btnVisualizar, RENDER, BeneficiarioPesquisaPage.ROLE_MANTER_BENEFICIARIO_CONSULTAR);
				authorize(btnVisualizar,RENDER,BeneficiarioPesquisaPage.ROLE_MANTER_BENEFICIARIO_VALIDAR_CADASTRO);
				item.add(btnVisualizar);

				InfraAjaxConfirmButton btnAtivar = componentFactory.newAJaxConfirmButton("btnAtivar", "MT028", form, (target, formz) -> alterarSituacao(target, item));
				authorize(btnAtivar, RENDER, BeneficiarioPesquisaPage.ROLE_MANTER_BENEFICIARIO_ALTERAR);
				btnAtivar.setVisible(mostrarBotaoAtivar(item));
				item.add(btnAtivar);

				InfraAjaxConfirmButton btnInativar = componentFactory.newAJaxConfirmButton("btnInativar", "MT028", form, (target, formz) -> alterarSituacao(target, item));
				authorize(btnInativar, RENDER, BeneficiarioPesquisaPage.ROLE_MANTER_BENEFICIARIO_ALTERAR);
				btnInativar.setVisible(mostrarBotaoDesativar(item));
				item.add(btnInativar);
			}
		};
		dataView.setItemsPerPage(Constants.ITEMS_PER_PAGE_PAGINATION);
		return dataView;
	}
	
	public Button getButtonVisualizar(Item<Entidade> item) {
	      Button btn = componentFactory.newButton("btnVisualizar", () -> visualizar(item));
	      btn.setOutputMarkupId(true);
	      return btn;
	}

	private DropDownChoice<Integer> getDropItensPorPagina() {
		DropDownChoice<Integer> dropDownChoice = new DropDownChoice<Integer>("itensPorPagina", new LambdaModel<Integer>(this::getItensPorPagina, this::setItensPorPagina),
		        Constants.QUANTIDADE_ITENS_TABELA);
		dropDownChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				dataView.setItemsPerPage(getItensPorPagina());
				target.add(panelResultados);
			};
		});
		return dropDownChoice;
	}

	public InfraAjaxFallbackLink<Void> getButtonNovo() {
		return componentFactory.newAjaxFallbackLink("btnNovo", (target) -> adicionarNovo());
	}

	private Button getButtonPesquisar() {
		return componentFactory.newButton("btnPesquisar", () -> pesquisar());
	}

	// AÇÕES
	
	private boolean mostrarBotaoAtivar(Item<Entidade> item){
            if(item.getModelObject().getOrigemCadastro() == EnumOrigemCadastro.CADASTRO_EXTERNO){
                if(item.getModelObject().getValidacaoCadastro() == EnumValidacaoCadastro.NAO_ANALISADO){
                    return false;
                }else{
                    return item.getModelObject().getStatusEntidade().equals(EnumStatusEntidade.INATIVA);
                }
            }
            return item.getModelObject().getStatusEntidade().equals(EnumStatusEntidade.INATIVA);
        }
        
        private boolean mostrarBotaoDesativar(Item<Entidade> item){
            if(item.getModelObject().getOrigemCadastro() == EnumOrigemCadastro.CADASTRO_EXTERNO){
                if(item.getModelObject().getValidacaoCadastro() == EnumValidacaoCadastro.NAO_ANALISADO){
                    return false;
                }else{
                    return item.getModelObject().getStatusEntidade().equals(EnumStatusEntidade.ATIVA);
                }
            }
            return item.getModelObject().getStatusEntidade().equals(EnumStatusEntidade.ATIVA);
        }

	private void alterarSituacao(AjaxRequestTarget target, Item<Entidade> item) {
		List<EntidadeAnexo> listaAnexos = new ArrayList<EntidadeAnexo>();
		List<PessoaEntidade> representantes = new ArrayList<PessoaEntidade>();
		List<PessoaEntidade> titulares = new ArrayList<PessoaEntidade>();
		List<PessoaEntidade> pessoas = new ArrayList<PessoaEntidade>();

		Entidade beneficiario = item.getModelObject();

		if (!getSideSession().hasRole(ROLE_MANTER_BENEFICIARIO_ALTERAR)) {
			throw new SecurityException();
		}
		listaAnexos = SideUtil.convertAnexoDtoToEntityEntidadeAnexo(anexoEntidadeService.buscarPeloIdEntidade(beneficiario.getId()));
		representantes = beneficiarioService.buscarRepresentanteEntidade(beneficiario, false);
		titulares = beneficiarioService.buscarTitularEntidade(beneficiario);

		if (beneficiario.getStatusEntidade().equals(EnumStatusEntidade.ATIVA)) {
			beneficiario.setStatusEntidade(EnumStatusEntidade.INATIVA);
			pessoas = inativarTitularesERepresentantes(titulares, representantes);
		} else {
			beneficiario.setStatusEntidade(EnumStatusEntidade.ATIVA);
		}

		beneficiario.setAnexos(listaAnexos);
		beneficiario.setPessoas(pessoas);

		beneficiarioService.incluirAlterar(beneficiario, getIdentificador());
		addMsgInfo("MT027");
		target.add(panelResultados);
	}

	private List<PessoaEntidade> inativarTitularesERepresentantes(List<PessoaEntidade> titulares, List<PessoaEntidade> representantes) {
		List<PessoaEntidade> listaPessoa = new ArrayList<PessoaEntidade>();

		for (PessoaEntidade tit : titulares) {
			tit.getPessoa().setStatusPessoa(EnumStatusPessoa.INATIVO);
			listaPessoa.add(tit);
		}

		for (PessoaEntidade rep : representantes) {
			rep.getPessoa().setStatusPessoa(EnumStatusPessoa.INATIVO);
			listaPessoa.add(rep);
		}

		return listaPessoa;
	}

	private void visualizar(Item<Entidade> item) {
		setResponsePage(new BeneficiarioPage(new PageParameters(), this, item.getModelObject(), true,false));
	}

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

	private void alterar(Item<Entidade> item) {
		if (!getSideSession().hasRole(ROLE_MANTER_BENEFICIARIO_ALTERAR)) {
			throw new SecurityException();
		}
		setResponsePage(new BeneficiarioPage(new PageParameters(), this, item.getModelObject(), false,false));
	}
	
	private void alterarPermissoes(Item<Entidade> item) {
            if (!getSideSession().hasRole(ROLE_MANTER_BENEFICIARIO_ALTERAR)) {
                    throw new SecurityException();
            }
            setResponsePage(new BeneficiarioPage(new PageParameters(), this, item.getModelObject(), true,true));
    }

	private void adicionarNovo() {
		if (!getSideSession().hasRole(ROLE_MANTER_BENEFICIARIO_INCLUIR)) {
			throw new SecurityException();
		}
		setResponsePage(new BeneficiarioPage(null, this, new Entidade(), false,false));
	}

	private void pesquisar() {
		if (!getSideSession().hasRole(ROLE_MANTER_BENEFICIARIO_CONSULTAR)) {
			throw new SecurityException();
		}

		EntidadePesquisaDto dto = new EntidadePesquisaDto();
		dto = form.getModelObject();

		if (dto.getEntidade() != null) {
			dto.getEntidade().setMunicipio(new Municipio());
		}

		if (dto.getEntidade() != null && dto.getEntidade().getNumeroCnpj() != null && !"".equalsIgnoreCase(dto.getEntidade().getNumeroCnpj())) {
			dto.getEntidade().setNumeroCnpj(MascaraUtils.limparFormatacaoMascara(dto.getEntidade().getNumeroCnpj()));
		}

		if (nomeUf != null && nomeUf.getId() != null) {
			Municipio municipio = new Municipio();
			municipio.setUf(nomeUf);
			if (dto.getEntidade() == null) {
				Entidade ent = new Entidade();
				ent.setMunicipio(municipio);
				dto.setEntidade(ent);
			} else {
				dto.getEntidade().setMunicipio(municipio);
			}
		}
		panelResultados.setVisible(true);
	}

	// Actions

	public Integer getItensPorPagina() {
		return itensPorPagina;
	}

	public void setItensPorPagina(Integer itensPorPagina) {
		this.itensPorPagina = itensPorPagina;
	}
}

