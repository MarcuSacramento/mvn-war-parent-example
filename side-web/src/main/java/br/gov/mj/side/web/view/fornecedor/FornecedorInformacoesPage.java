package br.gov.mj.side.web.view.fornecedor;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraAjaxFallbackLink;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.entidade.EntidadeAnexo;
import br.gov.mj.side.entidades.entidade.PessoaEntidade;
import br.gov.mj.side.entidades.enums.EnumTipoPessoa;
import br.gov.mj.side.web.service.AnexoEntidadeService;
import br.gov.mj.side.web.service.GenericEntidadeService;
import br.gov.mj.side.web.util.SideUtil;
import br.gov.mj.side.web.view.HomePage;
import br.gov.mj.side.web.view.fornecedor.paineis.FornecedorInformacoesPanel;
import br.gov.mj.side.web.view.fornecedor.paineis.PanelFornecedorAnexo;
import br.gov.mj.side.web.view.fornecedor.paineis.PanelFornecedorDados;
import br.gov.mj.side.web.view.template.TemplatePage;

@AuthorizeInstantiation({ FornecedorInformacoesPage.ROLE_MANTER_INFORMACAO_VISUALIZAR })
public class FornecedorInformacoesPage extends TemplatePage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String ROLE_MANTER_INFORMACAO_VISUALIZAR = "manter_informacao:visualizar";

	// #######################################_VARIAVEIS_############################################
	private Form<Entidade> form;
	private Entidade fornecedor = new Entidade();
    private List<PessoaEntidade> representantes = new ArrayList<PessoaEntidade>();
    private List<PessoaEntidade> prepostos = new ArrayList<PessoaEntidade>();
    private List<EntidadeAnexo> listaDeanexos = new ArrayList<EntidadeAnexo>();	
	
	// #######################################_ELEMENTOS_DO_WICKET_##################################
	private FornecedorInformacoesPanel fornecedorInformacoesPanel;
	private PanelFornecedorDados panelDadosRepresentanteLegal;
	private PanelFornecedorDados panelDadosPreposto;
	private PanelFornecedorAnexo panelAnexo;
	private PanelButton panelButton;
	
	// #####################################_INJEÇÃO_DE_DEPENDENCIA_#################################
    @Inject
    private GenericEntidadeService genericEntidadeService;
    
    @Inject
    private AnexoEntidadeService anexoService;
    
    @Inject
	private ComponentFactory componentFactory;
	
	// #####################################_CONSTRUTOR_#############################################
	public FornecedorInformacoesPage(PageParameters pageParameters) {
		super(pageParameters);

		initVariaveis();
		initComponents();

	}
	
	private void initVariaveis() {
		fornecedor = (Entidade) getSessionAttribute("entidade");
		pesquisarRepresentantes();
		listaDeanexos = SideUtil.convertAnexoDtoToEntityEntidadeAnexo(anexoService.buscarPeloIdEntidade(fornecedor.getId()));
	}

	private void initComponents() {
		setTitulo("Informações Cadastrais");
		
		form = new Form<Entidade>("form", new CompoundPropertyModel<Entidade>(fornecedor));
		form.add(componentFactory.newLink("homePage", HomePage.class));
		
		fornecedorInformacoesPanel = new FornecedorInformacoesPanel("fornecedorInformacoesPanel", fornecedor);		
		form.add(fornecedorInformacoesPanel);
		
		panelDadosRepresentanteLegal = newPanelDadosRepresentanteLegal("panelRepresentantes", representantes);
		form.add(panelDadosRepresentanteLegal);
		
		panelDadosPreposto = newPanelDadosPreposto("panelPrepostos", prepostos);
		form.add(panelDadosPreposto);
		
		panelAnexo = new PanelFornecedorAnexo("panelAnexo", listaDeanexos);
		form.add(panelAnexo);
		
		panelButton = new PanelButton();
		form.add(panelButton);
		
		add(form);		
	}

	// ####################################_PAINEIS_###############################################
    private PanelFornecedorDados newPanelDadosRepresentanteLegal(String id, List<PessoaEntidade> pessoas) {
        panelDadosRepresentanteLegal = new PanelFornecedorDados(id, pessoas);
        return panelDadosRepresentanteLegal;
    }
    
    private PanelFornecedorDados newPanelDadosPreposto(String id, List<PessoaEntidade> pessoas) {
        panelDadosPreposto = new PanelFornecedorDados(id, pessoas);
        return panelDadosPreposto;
    }

	private class PanelButton extends WebMarkupContainer {
		public PanelButton() {
			super("panelButton");
			setOutputMarkupId(Boolean.TRUE);
			add(newButtonVoltar());
		}
	}

	// ####################################_COMPONENTE_WICKET_###############################################
	private InfraAjaxFallbackLink<Void> newButtonVoltar() {
		return componentFactory.newAjaxFallbackLink("btnVoltar", (target) -> setResponsePage(HomePage.class));
	}
	
	// ####################################_AÇÕES_###############################################
	// Busca todos os representantes cadastrados e os divide em Representante legal e Preposto
    private void pesquisarRepresentantes() {
        List<PessoaEntidade> listaDerepresentantes = genericEntidadeService.buscarPessoa(fornecedor);
        fornecedor.setPessoas(listaDerepresentantes);

        for (PessoaEntidade p : fornecedor.getPessoas()) {
            if (p.getPessoa().getTipoPessoa().equals(EnumTipoPessoa.PREPOSTO_FORNECEDOR)) {
                prepostos.add(p);
            } else {
                representantes.add(p);
            }
        }
    }
}
