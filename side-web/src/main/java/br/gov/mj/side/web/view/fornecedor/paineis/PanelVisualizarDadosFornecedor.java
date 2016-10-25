package br.gov.mj.side.web.view.fornecedor.paineis;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import br.gov.mj.apoio.entidades.Uf;
import br.gov.mj.infra.wicket.component.InfraDropDownChoice;
import br.gov.mj.seg.entidades.Usuario;
import br.gov.mj.side.entidades.entidade.PessoaEntidade;
import br.gov.mj.side.entidades.enums.EnumStatusPessoa;
import br.gov.mj.side.entidades.enums.EnumTipoPessoa;
import br.gov.mj.side.entidades.programa.licitacao.contrato.Contrato;
import br.gov.mj.side.web.service.GenericEntidadeService;
import br.gov.mj.side.web.util.MascaraUtils;
import br.gov.mj.side.web.view.fornecedor.ContratoPage2;

public class PanelVisualizarDadosFornecedor extends Panel {

	private static final long serialVersionUID = 1L;
	private PanelInformacoesFornecedro panelInformacoesFornecedor;
	private PanelDadosDoFornecedorEscolhido panelDadosDoFornecedorEscolhido;
	private PanelVisualizarRepresentantePreposto panelAdicionarRepresentante;
	private PanelVisualizarRepresentantePreposto panelAdicionarPreposto;
	private PanelDropRepresentante panelDropRepresentante;
	private PanelDropPreposto panelDropPreposto;
	private TextField<String> fieldNomeEntidade;
	private InfraDropDownChoice<Uf> dropEstado;
	private Contrato contrato;
	private Page backPage;
	private ContratoPage2 page;
	private Usuario usuarioLogado;
	private PessoaEntidade prepostoEscolhido;
	private PessoaEntidade representanteEscolhido;
	private List<PessoaEntidade> listaDePessoasDesteFornecedor = new ArrayList<PessoaEntidade>();
	private List<PessoaEntidade> listaRepresentantes = new ArrayList<PessoaEntidade>();
	private List<PessoaEntidade> listaPreposto = new ArrayList<PessoaEntidade>();

	@Inject
	private GenericEntidadeService genericService;

	public PanelVisualizarDadosFornecedor(String id, Page backPage, Contrato contrato, Boolean cadastroNovo, Usuario usuarioLogado) {
		super(id);
		setOutputMarkupId(true);
		this.backPage = backPage;
		this.contrato = contrato;
		this.usuarioLogado = usuarioLogado;

		initVariaveis();
		initComponents();
	}

	private class PanelInformacoesFornecedro extends WebMarkupContainer {

		private static final long serialVersionUID = 1L;

		public PanelInformacoesFornecedro(String id) {
			super(id);
			setOutputMarkupId(true);

			add(newLabelCnpjFornecedor());
		}
	}

	public class PanelDropRepresentante extends WebMarkupContainer {
		private static final long serialVersionUID = 1L;

		public PanelDropRepresentante(String id) {
			super(id);
			setOutputMarkupId(true);

			add(newLabelDropDownRepresentante()); // dropDownRepresentante
			add(newLabelCpfRepresentanLegal());
		}
	}

	public class PanelDropPreposto extends WebMarkupContainer {
		private static final long serialVersionUID = 1L;

		public PanelDropPreposto(String id) {
			super(id);
			setOutputMarkupId(true);

			add(newLabelDropDownPreposto()); // dropDownPreposto
			add(newLabelCpfPreposto());
		}
	}

	private class PanelDadosDoFornecedorEscolhido extends WebMarkupContainer {
		private static final long serialVersionUID = 1L;

		public PanelDadosDoFornecedorEscolhido(String id) {
			super(id);
			setOutputMarkupId(true);

			// FORNECEDOR
			add(newLabelRazaoSocial());
			add(newLabelDropDownEstado());
			add(newLabelContato());
			add(newLabelTelefoneFornecedor());
		}

	}

	private void initVariaveis() {
		page = (ContratoPage2) backPage;

		if (contrato != null && contrato.getId() != null) {
			setarListasDeRepresentantesEPreposto();
			setarRepresentanteEPrepostoSelecionado();

			PessoaEntidade pe1 = new PessoaEntidade();
			pe1.setPessoa(contrato.getRepresentanteLegal());
			representanteEscolhido = pe1;

			PessoaEntidade pe2 = new PessoaEntidade();
			pe2.setPessoa(contrato.getPreposto());
			prepostoEscolhido = pe2;
		}

	}

	private void initComponents() {

		add(panelInformacoesFornecedor = new PanelInformacoesFornecedro("panelInformacoesFornecedor"));
		add(panelDadosDoFornecedorEscolhido = new PanelDadosDoFornecedorEscolhido("panelDadosDoFornecedorEscolhido"));
		add(panelDropRepresentante = new PanelDropRepresentante("panelDropRepresentante"));
		add(panelDropPreposto = new PanelDropPreposto("panelDropPreposto"));

		add(panelAdicionarRepresentante = new PanelVisualizarRepresentantePreposto("panelAdicionarRepresentante", listaRepresentantes, listaPreposto,
				EnumTipoPessoa.REPRESENTANTE_LEGAL, page));
		panelAdicionarRepresentante.setVisible(false);

		add(panelAdicionarPreposto = new PanelVisualizarRepresentantePreposto("panelAdicionarPreposto", listaPreposto, listaRepresentantes, EnumTipoPessoa.PREPOSTO_FORNECEDOR, page));
		panelAdicionarPreposto.setVisible(false);
	}

	// CRIAÇÃO DOS COMPONENTES
	
	 private Label newLabelCnpjFornecedor() {
	        return new Label("cnpjFornecedor",MascaraUtils.formatarMascaraCpfCnpj(contrato.getFornecedor().getNumeroCnpj()));
	 }
	 
	 private Label newLabelDropDownRepresentante() {
	        return new Label("dropDownRepresentante",representanteEscolhido.getPessoa().getNomePessoa());
	 }
	
	 private Label newLabelCpfRepresentanLegal() {
	        return new Label("cpfRepresentanLegal",MascaraUtils.formatarMascaraCpfCnpj(representanteEscolhido.getPessoa().getNumeroCpf()));
	 } 

	 private Label newLabelDropDownPreposto() {
	        return new Label("dropDownPreposto",prepostoEscolhido.getPessoa().getNomePessoa());
	 }  
	 
	 private Label newLabelCpfPreposto() {
	        return new Label("cpfPreposto",MascaraUtils.formatarMascaraCpfCnpj(prepostoEscolhido.getPessoa().getNumeroCpf()));
	 } 

	 private Label newLabelRazaoSocial() {
	        return new Label("razaoSocial",contrato.getFornecedor().getNomeEntidade());
	 }

	 private Label newLabelDropDownEstado() {
	        return new Label("dropDownEstado",contrato.getFornecedor().getMunicipio().getUf().getNomeUf());
	 }
	
	 private Label newLabelContato() {
	        return new Label("contato",contrato.getFornecedor().getNomeContato());
	 }
	 
	 private Label newLabelTelefoneFornecedor() {
	        return new Label("telefoneFornecedor",MascaraUtils.formatarMascaraTelefone(contrato.getFornecedor().getNumeroTelefone()));
	 }

	

	// AÇÕES
	private void setarListasDeRepresentantesEPreposto() {
		listaDePessoasDesteFornecedor = genericService.buscarPessoa(contrato.getFornecedor());
		for (PessoaEntidade pe : listaDePessoasDesteFornecedor) {
			if (pe.getPessoa().getTipoPessoa() == EnumTipoPessoa.REPRESENTANTE_LEGAL && pe.getPessoa().getStatusPessoa() == EnumStatusPessoa.ATIVO) {
				listaRepresentantes.add(pe);
			} else {
				if (pe.getPessoa().getTipoPessoa() == EnumTipoPessoa.PREPOSTO_FORNECEDOR && pe.getPessoa().getStatusPessoa() == EnumStatusPessoa.ATIVO) {
					listaPreposto.add(pe);
				}
			}
		}
	}

	public void atualizarListaDeRepresentantesAposAdicionarNovoFornecedor() {

		listaRepresentantes.clear();
		listaPreposto.clear();
		for (PessoaEntidade pe : listaDePessoasDesteFornecedor) {
			if (pe.getPessoa().getTipoPessoa() == EnumTipoPessoa.REPRESENTANTE_LEGAL && pe.getPessoa().getStatusPessoa() == EnumStatusPessoa.ATIVO) {
				listaRepresentantes.add(pe);
			} else {
				if (pe.getPessoa().getStatusPessoa() == EnumStatusPessoa.ATIVO) {
					listaPreposto.add(pe);
				}
			}
		}
	}

	private void setarRepresentanteEPrepostoSelecionado() {
		PessoaEntidade pe = new PessoaEntidade();
		pe.setPessoa(contrato.getRepresentanteLegal());

		PessoaEntidade prep = new PessoaEntidade();
		prep.setPessoa(contrato.getPreposto());

		representanteEscolhido = pe;
		prepostoEscolhido = prep;
	}

	public void actionAtualizarDrop(AjaxRequestTarget target, EnumTipoPessoa tipoPessoa) {

		if (tipoPessoa == EnumTipoPessoa.REPRESENTANTE_LEGAL) {
			panelDropRepresentante.addOrReplace(newLabelDropDownRepresentante());

			panelAdicionarRepresentante.setVisible(false);
			atualizarPanelAdicionarRepresentante(target);
			target.appendJavaScript("atualizarDropDown();");
			target.add(panelDropRepresentante);
		} else {
			panelDropPreposto.addOrReplace(newLabelDropDownPreposto());
			panelAdicionarPreposto.setVisible(false);
			atualizarPanelAdicionarPreposto(target);
			target.appendJavaScript("atualizarDropDown();");
			target.add(panelDropPreposto);
		}

	}

	private void actionOcultarPanelAdicionarRepresentante(AjaxRequestTarget target) {
		panelAdicionarRepresentante.setVisible(false);
		atualizarPanelAdicionarRepresentante(target);
	}

	private void actionOcultarPanelAdicionarPreposto(AjaxRequestTarget target) {
		panelAdicionarPreposto.setVisible(false);
		atualizarPanelAdicionarPreposto(target);
	}

	public void actionOcultarPanel(AjaxRequestTarget target, EnumTipoPessoa tipoPessoa) {
		if (tipoPessoa == EnumTipoPessoa.REPRESENTANTE_LEGAL) {
			actionOcultarPanelAdicionarRepresentante(target);
		} else {
			actionOcultarPanelAdicionarPreposto(target);
		}
	}

	/*
	 * Esta ação será execultada sempre que clicar ou no botão de adicionar ou
	 * de cancelar adição de um novo representante
	 */
	private void atualizarPanelAdicionarRepresentante(AjaxRequestTarget target) {
		//panelDropRepresentante.addOrReplace(newButtonAdicionarRepresentante());

		target.appendJavaScript("atualizarDropDown();");
		target.add(panelAdicionarRepresentante);
		target.add(panelDropRepresentante);
	}

	/*
	 * Esta ação será execultada sempre que clicar ou no botão de adicionar ou
	 * de cancelar adição de um novo preposto
	 */

	private void atualizarPanelAdicionarPreposto(AjaxRequestTarget target) {

		target.appendJavaScript("atualizarDropDown();");
		target.add(panelAdicionarPreposto);
		target.add(panelDropPreposto);
	}

	public Usuario getUsuarioLogado() {
		return usuarioLogado;
	}

	public PessoaEntidade getPrepostoEscolhido() {
		return prepostoEscolhido;
	}

	public PessoaEntidade getRepresentanteEscolhido() {
		return representanteEscolhido;
	}

	public List<PessoaEntidade> getListaRepresentantes() {
		return listaRepresentantes;
	}

	public void setListaRepresentantes(List<PessoaEntidade> listaRepresentantes) {
		this.listaRepresentantes = listaRepresentantes;
	}

	public List<PessoaEntidade> getListaPreposto() {
		return listaPreposto;
	}

	public void setListaPreposto(List<PessoaEntidade> listaPreposto) {
		this.listaPreposto = listaPreposto;
	}

	public PanelDropRepresentante getPanelDropRepresentante() {
		return panelDropRepresentante;
	}

	public void setPanelDropRepresentante(PanelDropRepresentante panelDropRepresentante) {
		this.panelDropRepresentante = panelDropRepresentante;
	}

	public PanelDropPreposto getPanelDropPreposto() {
		return panelDropPreposto;
	}

	public void setPanelDropPreposto(PanelDropPreposto panelDropPreposto) {
		this.panelDropPreposto = panelDropPreposto;
	}

	public List<PessoaEntidade> getListaDePessoasDesteFornecedor() {
		return listaDePessoasDesteFornecedor;
	}

	public void setListaDePessoasDesteFornecedor(List<PessoaEntidade> listaDePessoasDesteFornecedor) {
		this.listaDePessoasDesteFornecedor = listaDePessoasDesteFornecedor;
	}

	public Contrato getContrato() {
		return contrato;
	}

	public void setContrato(Contrato contrato) {
		this.contrato = contrato;
	}

	public TextField<String> getFieldNomeEntidade() {
		return fieldNomeEntidade;
	}

	public void setFieldNomeEntidade(TextField<String> fieldNomeEntidade) {
		this.fieldNomeEntidade = fieldNomeEntidade;
	}

	public InfraDropDownChoice<Uf> getDropEstado() {
		return dropEstado;
	}

	public void setDropEstado(InfraDropDownChoice<Uf> dropEstado) {
		this.dropEstado = dropEstado;
	}

}
