package br.gov.mj.side.web.view.acaoorcamentaria;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.validation.validator.RangeValidator;
import br.gov.mj.apoio.entidades.PartidoPolitico;
import br.gov.mj.apoio.entidades.Uf;
import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraAjaxConfirmButton;
import br.gov.mj.infra.wicket.component.InfraDropDownChoice;
import br.gov.mj.infra.wicket.converter.MoneyBigDecimalConverter;
import br.gov.mj.side.entidades.BeneficiarioEmendaParlamentar;
import br.gov.mj.side.entidades.EmendaParlamentar;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.enums.EnumTipoEmenda;
import br.gov.mj.side.web.dto.EntidadePesquisaDto;
import br.gov.mj.side.web.service.GenericEntidadeService;
import br.gov.mj.side.web.service.PartidoService;
import br.gov.mj.side.web.service.UfService;
import br.gov.mj.side.web.util.CnpjUtil;
import br.gov.mj.side.web.util.MascaraUtils;
import br.gov.mj.side.web.view.HomePage;
import br.gov.mj.side.web.view.components.validators.NumericValidator;
import br.gov.mj.side.web.view.template.TemplatePage;

public class EmendaParlamentarPage extends TemplatePage {
    private static final long serialVersionUID = 1L;

    private Page backPage;
    private AcaoOrcamentariaPage acaoOrcamentariaPage;
    private Form<EmendaParlamentar> form;
    private Form<EmendaParlamentarPage> formAdicionarBeneficiario;
    private IModel<Boolean> selected = new Model<Boolean>();

    private EmendaParlamentar emendaParlamentar;
    private String cpfTemporario = "";
    private String numeroTemporario = "";
    private String titulo;
    private boolean editando;
    private int posicaoBeneficiarioLista;

    private BeneficiarioEmendaParlamentar beneficiario;
    private AjaxButton buttonAdicionar;
    private Button btnConfirmar;
    private AjaxButton buttonSalverEdicao;
    private AjaxSubmitLink buttonCancelarEdicao;

    private PanelFonte panelFonte;
    private PanelAdicionarBeneficiario panelAdicionarBeneficiario;
    private PanelDataViewBeneficiarios panelDataViewBeneficiarios;

    private List<BeneficiarioEmendaParlamentar> listBeneficiario;

    private DataView<BeneficiarioEmendaParlamentar> dataview;
    private boolean habilitarNomeBeneficiario = Boolean.FALSE;
    

    @Inject
    private ComponentFactory componentFactory;
    @Inject
    private PartidoService partidoService;
    @Inject
    private UfService ufService;
    @Inject
    private GenericEntidadeService genericEntidadeService;

    public EmendaParlamentarPage(final PageParameters pageParameters) {
        super(pageParameters);
        initVariaveis();
        initComponents();
        setTitulo(titulo);
        criarBreadcrumb();
    }

    public EmendaParlamentarPage(final PageParameters pageParameters, Page backPage, EmendaParlamentar emenda, Boolean readonly) {
        super(pageParameters);

        this.backPage = backPage;
        this.emendaParlamentar = emenda;

        initVariaveis();
        initComponents();
        if (readonly) {
            modoVisulizar();
        }
        setTitulo(titulo);
        criarBreadcrumb();
    }

    public void initVariaveis() {

        acaoOrcamentariaPage = (AcaoOrcamentariaPage) backPage;

        beneficiario = new BeneficiarioEmendaParlamentar();

        if (emendaParlamentar != null && (emendaParlamentar.getNumeroEmendaParlamantar() != null && !"".equalsIgnoreCase(emendaParlamentar.getNumeroEmendaParlamantar()))) {
            titulo = "Alterar Emenda Parlamentar";
            if (emendaParlamentar.getBeneficiariosEmendaParlamentar() != null) {
                if (!emendaParlamentar.getBeneficiariosEmendaParlamentar().isEmpty() && emendaParlamentar.getBeneficiariosEmendaParlamentar().size() > 0) {
                    listBeneficiario = new ArrayList<BeneficiarioEmendaParlamentar>(emendaParlamentar.getBeneficiariosEmendaParlamentar());
                } else {
                    listBeneficiario = new ArrayList<BeneficiarioEmendaParlamentar>();
                }

                selected = new Model<Boolean>();
                selected.setObject(emendaParlamentar.getPossuiLiberacao());
                editando = true;
                numeroTemporario = emendaParlamentar.getNumeroEmendaParlamantar();
            }
        } else {
            listBeneficiario = new ArrayList<BeneficiarioEmendaParlamentar>();
            titulo = "Adicionar Emenda Parlamentar";
            emendaParlamentar = new EmendaParlamentar();
            editando = false;
            selected.setObject(false);
        }
    }

    private void initComponents() {
        form = componentFactory.newForm("form", new CompoundPropertyModel<EmendaParlamentar>(emendaParlamentar));

        panelFonte = new PanelFonte("panelFonte");
        panelDataViewBeneficiarios = new PanelDataViewBeneficiarios("panelDataViewBeneficiarios");
        formAdicionarBeneficiario = componentFactory.newForm("formAdicionarBeneficiario", new CompoundPropertyModel<EmendaParlamentarPage>(this));
        panelAdicionarBeneficiario = new PanelAdicionarBeneficiario("panelAdicionarBeneficiario");

        formAdicionarBeneficiario.add(panelAdicionarBeneficiario);

        form.add(formAdicionarBeneficiario);
        form.add(panelFonte);
        form.add(panelDataViewBeneficiarios);
        form.add(getButtonConfirmar());// btnConfirmar
        form.add(getButtonVoltar());// btnCancelar

        add(form);
    }

    /*
     * ABAIXO SERÃO DESENVOLVIDOS OS PAINEIS
     */

    public class PanelFonte extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelFonte(String id) {
            super(id);

            add(getTextFieldNumero());// numeroEmendaParlamantar
            add(getTextFieldNome());// nomeEmendaParlamentar
            add(getDropDownPartidos());// partidoPolitico
            add(getDropDownUf());// uf
            add(getTextFieldNomeParlamentar());// nomeParlamentar
            add(getTextFieldCargo());// nomeCargoParlamentar
            add(getDropDownTipo());// tipoEmenda
            add(getRadioLiberacao()); // radioLiberacao
            add(getTextFieldValorPrevisto()); // valorPrevisto
        }
    }

    public class PanelAdicionarBeneficiario extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelAdicionarBeneficiario(String id) {
            super(id);
            add(getTextFieldAdicionarCnpj()); // beneficiario.numeroCnpjBeneficiario
            add(getTextFieldAdicionarNome()); // beneficiario.nomeBeneficiario
            add(newButtonPesquisarCpf());
            add(getButtonAdicionar()); // btnAdicionar
            add(getButtonSalvarEdicao()); // btnSalvarEdicao
            add(getButtonCancelarEdicao()); // btnCancelarEdicao

            visualizarOcultarBotoesEdicao(false);
        }
    }

    public class PanelDataViewBeneficiarios extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelDataViewBeneficiarios(String id) {
            super(id);
            add(getDataViewBeneficiarios());// dataBeneficiarios
        }
    }

    public void criarBreadcrumb() {
        form.add(componentFactory.newLink("lnkDashboard", HomePage.class));
        form.add(componentFactory.newLink("lnkPesquisar", AcaoOrcamentariaPesquisaPage.class));

        Link<Void> linkBackPage = new Link<Void>("lnkBackPage") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                setResponsePage(backPage);
            }
        };
        form.add(linkBackPage);
        linkBackPage.add(componentFactory.newLabel("lblBackPageTitulo", acaoOrcamentariaPage.getTitulo()));
        form.add(new Label("lnkFinal", titulo));
    }

    // Será chamado somente se estiver no modo 'visulizar'
    public void modoVisulizar() {
        titulo = "Visualizar Emenda Parlamentar";
        panelFonte.setEnabled(false);
        panelDataViewBeneficiarios.setEnabled(false);
        formAdicionarBeneficiario.setEnabled(false);
        formAdicionarBeneficiario.setEnabled(false);
        btnConfirmar.setEnabled(false);
    }

    /*
     * ABAIXO SERÃO DESENVOLVIDOS OS COMPONENTES
     */

    public TextField<String> getTextFieldNumero() {
        TextField<String> field = componentFactory.newTextField("numeroEmendaParlamantar", "Número", false, null, null);
        field.add(new NumericValidator());
        return field;
    }

    public TextField<String> getTextFieldNome() {
        return componentFactory.newTextField("nomeEmendaParlamentar", "Nome", false, null,null);
    }

    public InfraDropDownChoice<PartidoPolitico> getDropDownPartidos() {
        InfraDropDownChoice<PartidoPolitico> dropDownPartidos = componentFactory.newDropDownChoice("partidoPolitico", "Partido", false, "id", "siglaNome", null, partidoService.buscarTodos(), null);
        dropDownPartidos.setNullValid(true);
        dropDownPartidos.setOutputMarkupId(true);
        return dropDownPartidos;
    }

    public InfraDropDownChoice<Uf> getDropDownUf() {
        InfraDropDownChoice<Uf> dropDownUf = componentFactory.newDropDownChoice("uf", "UF", false, "id", "nomeSigla", null, ufService.buscarTodos(), null);
        dropDownUf.setNullValid(true);
        return dropDownUf;
    }

    public TextField<String> getTextFieldNomeParlamentar() {
        return componentFactory.newTextField("nomeParlamentar", "Nome do Parlamentar", false, null, null);
    }

    public TextField<String> getTextFieldCargo() {
        return componentFactory.newTextField("nomeCargoParlamentar", "Cargo", false, null, null);
    }

    public InfraDropDownChoice<EnumTipoEmenda> getDropDownTipo() {
        InfraDropDownChoice<EnumTipoEmenda> dropDownTipo = componentFactory.newDropDownChoice("tipoEmenda", "Tipo", false, "valor", "descricao", null, Arrays.asList(EnumTipoEmenda.values()), null);
        dropDownTipo.setNullValid(true);
        return dropDownTipo;
    }

    public RadioGroup<Boolean> getRadioLiberacao() {
        RadioGroup<Boolean> group = new RadioGroup<Boolean>("group", selected);
        group.add(new Radio<Boolean>("sim", new Model<Boolean>(true)));
        group.add(new Radio<Boolean>("nao", new Model<Boolean>(false)));
        group.setRequired(true);
        group.setLabel(Model.of("Liberação"));
        return group;
    }

    public TextField<BigDecimal> getTextFieldValorPrevisto() {

        TextField<BigDecimal> field = new TextField<BigDecimal>("valorPrevisto") {
            private static final long serialVersionUID = 1L;

            @SuppressWarnings("unchecked")
            @Override
            public <C> IConverter<C> getConverter(Class<C> type) {
                return (IConverter<C>) new MoneyBigDecimalConverter();
            }
        };
        field.setLabel(Model.of("Valor Previsto"));
        field.setRequired(false);
        field.add(RangeValidator.range(new BigDecimal("0.00"), new BigDecimal("999999999999.99")));
        return field;
    }

    // PANEL ADICIONAR BENEFICIÁRIO
    public TextField<String> getTextFieldAdicionarCnpj() {
        TextField<String> text = componentFactory.newTextField("beneficiario.numeroCnpjBeneficiario", "CNPJ", false, new PropertyModel<String>(beneficiario, "numeroCnpjBeneficiario"));
        return text;
    }

    public TextField<String> getTextFieldAdicionarNome() {
    	TextField<String> text = componentFactory.newTextField("beneficiario.nomeBeneficiario", "Nome", false, new PropertyModel<String>(beneficiario, "nomeBeneficiario"), null);
    	text.setEnabled(this.habilitarNomeBeneficiario);
        return text;
    }
    
    public AjaxSubmitLink newButtonPesquisarCpf() {
        AjaxSubmitLink button = new AjaxSubmitLink("btnPesquisarBeneficio", form) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form) {
            	 boolean validar = true;
                 if (beneficiario.getNumeroCnpjBeneficiario() == null || "".equalsIgnoreCase(beneficiario.getNumeroCnpjBeneficiario())) {
                     addMsgError("Informe um número de 'CNPJ' valido.");
                     target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
                     return;
                 } else {
	            	 String cnpj = limparCampos(beneficiario.getNumeroCnpjBeneficiario());
	                 validar = validarCnpj(cnpj, validar);
	                 if (!validar) {
	                     return;
	                 } else {
	                	 
	                	 EntidadePesquisaDto entidadePesquisaDto = new EntidadePesquisaDto();
	                	 Entidade entidade = new Entidade();
	                	 entidade.setNumeroCnpj(cnpj);
	                	 
	                	 entidadePesquisaDto.setEntidade(entidade);
	                	 entidadePesquisaDto.setUsuarioLogado(getUsuarioLogadoDaSessao());
	                	 
	                	 List<Entidade> entidadeCnpj = new ArrayList<Entidade>();
	                	 entidadeCnpj = genericEntidadeService.buscar(entidadePesquisaDto);
	                	 
	                	 if(entidadeCnpj.size()>0){
	                		 beneficiario.setNomeBeneficiario(entidadeCnpj.get(0).getNomeEntidade());
	                	 }else{
	                		 habilitarNomeBeneficiario = Boolean.TRUE;
	                	 }
	                	 
	                	 panelAdicionarBeneficiario.addOrReplace(getTextFieldAdicionarNome());
	                	 target.add(panelAdicionarBeneficiario);
	                 }
                 }
            }
        };
        return button;
    }	

    public Button getButtonAdicionar() {
        buttonAdicionar = new AjaxButton("btnAdicionar") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                adicionar(target);
                
            }
        };
        return buttonAdicionar;
    }

    // PANEL DATAVIEW
    public DataView<BeneficiarioEmendaParlamentar> getDataViewBeneficiarios() {
        dataview = new DataView<BeneficiarioEmendaParlamentar>("dataBeneficiarios", new ListDataProvider<BeneficiarioEmendaParlamentar>(listBeneficiario)) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<BeneficiarioEmendaParlamentar> item) {
                item.clearOriginalDestination();
                item.add(new Label("cnpjBeneficiario", CnpjUtil.imprimeCNPJ(item.getModelObject().getNumeroCnpjBeneficiario())));
                item.add(new Label("nomeBeneficiario", item.getModelObject().getNomeBeneficiario()));
                item.add(getButtonEditar(item));
                item.add(getButtonExcluir(item));
            }
        };
        return dataview;
    }

    public AjaxSubmitLink getButtonEditar(Item<BeneficiarioEmendaParlamentar> item) {
        AjaxSubmitLink button = new AjaxSubmitLink("btnEditar", form) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form) {

                visualizarOcultarBotoesEdicao(true);

                beneficiario.setNomeBeneficiario(item.getModelObject().getNomeBeneficiario());
                beneficiario.setNumeroCnpjBeneficiario(MascaraUtils.limparFormatacaoMascara(item.getModelObject().getNumeroCnpjBeneficiario()));
                cpfTemporario = beneficiario.getNumeroCnpjBeneficiario();
                posicaoBeneficiarioLista = item.getIndex();

                panelAdicionarBeneficiario.addOrReplace(getTextFieldAdicionarCnpj());
                panelAdicionarBeneficiario.addOrReplace(getTextFieldAdicionarNome());
                target.add(panelAdicionarBeneficiario);
            }
        };
        button.setDefaultFormProcessing(false);
        return button;
    }

    public InfraAjaxConfirmButton getButtonExcluir(Item<BeneficiarioEmendaParlamentar> item) {
        return componentFactory.newAJaxConfirmButton("btnExcluir", "MSG001", form, (target, formz) -> excluirAtributo(target, item));
    }

    public AjaxButton getButtonSalvarEdicao() {
        buttonSalverEdicao = new AjaxButton("btnSalvarEdicao") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {

                if (!validarEditarBeneficiario()) {
                    return;
                }

                for (BeneficiarioEmendaParlamentar b : listBeneficiario) {
                    String cpfLista = b.getNumeroCnpjBeneficiario();

                    if (cpfTemporario.equalsIgnoreCase(cpfLista)) {
                        boolean validar = validarCnpj(beneficiario.getNumeroCnpjBeneficiario(), true);
                        if (!validar) {
                            atualizarDataViewEAdicionarBeneficiario(target);
                            return;
                        }
                        b.setNumeroCnpjBeneficiario(MascaraUtils.limparFormatacaoMascara(beneficiario.getNumeroCnpjBeneficiario()));
                        b.setNomeBeneficiario(beneficiario.getNomeBeneficiario());
                        break;
                    }
                }
                beneficiario = new BeneficiarioEmendaParlamentar();
                cpfTemporario = "";
                visualizarOcultarBotoesEdicao(false);
                atualizarDataViewEAdicionarBeneficiario(target);
            }
        };
        return buttonSalverEdicao;
    }

    public AjaxSubmitLink getButtonCancelarEdicao() {
        buttonCancelarEdicao = new AjaxSubmitLink("btnCancelarEdicao", form) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form) {
                beneficiario = new BeneficiarioEmendaParlamentar();
                visualizarOcultarBotoesEdicao(false);
                atualizarDataViewEAdicionarBeneficiario(target);
            }
        };
        buttonCancelarEdicao.setDefaultFormProcessing(false);
        return buttonCancelarEdicao;
    }

    public Button getButtonConfirmar() {
        btnConfirmar = new AjaxButton("btnConfirmar") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
            	salvar(target);
            	target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
            }
        };
        
        return btnConfirmar;
    }

    public Button getButtonVoltar() {
        Button button = new Button("btnVoltar") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit() {
                voltar();
            }
        };
        button.setDefaultFormProcessing(false);
        return button;
    }

    /*
     * AQUI VIRÃO AS AÇÕES DA CLASSE
     */

    public void salvar(AjaxRequestTarget target) {
        String msg = "";

        AcaoOrcamentariaPage fonte = (AcaoOrcamentariaPage) backPage;
        EmendaParlamentar emenda = form.getModelObject();
        emenda.setBeneficiariosEmendaParlamentar(listBeneficiario);
        emenda.setPossuiLiberacao(selected.getObject());
        
        if (!validarSalvarrBeneficiario(emenda)) {
            return;
        }

        List<EmendaParlamentar> lista = fonte.getEntity().getEmendasParlamentares();
        for (EmendaParlamentar ep : lista) {
            if (ep.hashCode() == emenda.hashCode()) {
                if (editando) {
                    addMsgInfo("Emenda alterada com sucesso.");
                } else {
                    addMsgInfo("Emenda cadastrada com sucesso.");
                }
                return;
            }
        }

        if (editando) {
            for (EmendaParlamentar ep : lista) {
                if (numeroTemporario.equalsIgnoreCase(ep.getNumeroEmendaParlamantar())) {
                    ep = emenda;
                    break;
                }
            }
            fonte.getEntity().setEmendasParlamentares(lista);
            fonte.atualizarLabelTotal();
            msg = "Emenda alterada com sucesso.";
        } else {
            fonte.getEntity().getEmendasParlamentares().add(emenda);
            fonte.atualizarLabelTotal();
            msg = "Emenda cadastrada com sucesso.";
        }
        getSession().info(msg);
        setResponsePage(new EmendaParlamentarPage(null, this, emenda, false));
    }

    private boolean validarSalvarrBeneficiario(EmendaParlamentar emenda) {
    	boolean validar = true;
    	if(emenda!=null &&  emenda.getNumeroEmendaParlamantar()==null){
    		addMsgError("Campo 'Número' é obrigatório.");
            validar = false;
    	}
    	if(emenda!=null &&  emenda.getNomeEmendaParlamentar()==null){
    		addMsgError("Campo 'Nome' é obrigatório.");
            validar = false;
    	}
    	if(emenda!=null &&  emenda.getPartidoPolitico()==null){
    		addMsgError("Campo 'Partido' é obrigatório.");
            validar = false;
    	}
    	if(emenda!=null &&  emenda.getUf()==null){
    		addMsgError("Campo 'UF' é obrigatório.");
    		validar = false;
    	}
    	if(emenda!=null &&  emenda.getNomeParlamentar()==null){
    		addMsgError("Campo 'Nome do Parlamentar' é obrigatório.");
    		validar = false;
    	}
    	if(emenda!=null &&  emenda.getNomeCargoParlamentar()==null){
    		addMsgError("Campo 'Campo 'Cargo' é obrigatório.");
    		validar = false;
    	}
    	if(emenda!=null &&  emenda.getTipoEmenda()==null){
    		addMsgError("Campo 'Tipo' é obrigatório.");
    		validar = false;
    	}
    	if(emenda!=null &&  emenda.getValorPrevisto()==null){
    		addMsgError("Campo 'Valor Previsto' é obrigatório.");
    		validar = false;
    	}
		return validar;
	}

	private void voltar() {
        setResponsePage(backPage);
    }

    private void excluirAtributo(AjaxRequestTarget target, Item<BeneficiarioEmendaParlamentar> item) {
        listBeneficiario.remove(item.getModelObject());
        if (item.getModelObject().getNumeroCnpjBeneficiario().equalsIgnoreCase(cpfTemporario)) {
            beneficiario = new BeneficiarioEmendaParlamentar();
            cpfTemporario = "";
            visualizarOcultarBotoesEdicao(false);
        }
        atualizarDataViewEAdicionarBeneficiario(target);
    }

    public void adicionar(AjaxRequestTarget target) {

        if (!validarAdicionarBeneficiario()) {
        	target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
            return;
        }
        beneficiario.setNumeroCnpjBeneficiario(MascaraUtils.limparFormatacaoMascara(beneficiario.getNumeroCnpjBeneficiario()));
        listBeneficiario.add(beneficiario);
        beneficiario = new BeneficiarioEmendaParlamentar();
        atualizarDataViewEAdicionarBeneficiario(target);
    }

    public boolean validarAdicionarBeneficiario() {
        boolean validar = true;
        String cnpj = MascaraUtils.limparFormatacaoMascara(beneficiario.getNumeroCnpjBeneficiario() == null ? "" : beneficiario.getNumeroCnpjBeneficiario());

        for (BeneficiarioEmendaParlamentar b : listBeneficiario) {
            String cnpjLista = b.getNumeroCnpjBeneficiario();
            if (cnpj.equalsIgnoreCase(cnpjLista)) {
                addMsgError("O 'CNPJ' informado já foi adicionado à lista de beneficiários.");
                return false;
            }
        }

        if (beneficiario.getNumeroCnpjBeneficiario() == null || "".equalsIgnoreCase(beneficiario.getNumeroCnpjBeneficiario())) {
            addMsgError("O campo 'CNPJ' é obrigatório");
            validar = false;
        } else {
            validar = validarCnpj(cnpj, validar);
        }

        if (beneficiario.getNomeBeneficiario() == null || "".equalsIgnoreCase(beneficiario.getNomeBeneficiario())) {
            addMsgError("O campo 'Nome' é obrigatório");
            validar = false;
        }
        return validar;
    }

    public boolean validarEditarBeneficiario() {
        boolean validar = true;

        if (beneficiario.getNumeroCnpjBeneficiario() == null || "".equalsIgnoreCase(beneficiario.getNumeroCnpjBeneficiario())) {
            addMsgError("O campo 'CNPJ' é obrigatório");
            validar = false;
        } else {
            int i = 0;
            int contador = 0;
            for (BeneficiarioEmendaParlamentar b : listBeneficiario) {
                String cpfLista = b.getNumeroCnpjBeneficiario();

                if (MascaraUtils.limparFormatacaoMascara(beneficiario.getNumeroCnpjBeneficiario()).equalsIgnoreCase(cpfLista)) {
                    if (posicaoBeneficiarioLista != contador) {
                        addMsgError("O campo 'CNPJ' informado já esta em uso.");
                        validar = false;
                        i++;
                    }
                }
                contador++;
            }

            if (i > 0) {
                validar = false;
            } else {
                validar = validarCnpj(beneficiario.getNumeroCnpjBeneficiario(), validar);
            }
        }

        if (beneficiario.getNomeBeneficiario() == null || "".equalsIgnoreCase(beneficiario.getNomeBeneficiario())) {
            addMsgError("O campo 'Nome' é obrigatório");
            validar = false;
        }

        return validar;
    }

    public boolean validarCnpj(String cnpj, boolean validar) {
        boolean valido = validar;
        if (cnpj.length() < 14) {
            addMsgError("O 'CNPJ' deverá conter 14 digitos.");
            valido = false;
        } else {
            CnpjUtil cnpjValido = new CnpjUtil(cnpj);
            if (!cnpjValido.isCnpjValido()) {
                addMsgError("O 'CNPJ' informado esta em um formato inválido.");
                valido = false;
            }
        }
        return valido;
    }

    public void atualizarDataViewEAdicionarBeneficiario(AjaxRequestTarget target) {
        panelAdicionarBeneficiario.addOrReplace(getTextFieldAdicionarCnpj());
        panelAdicionarBeneficiario.addOrReplace(getTextFieldAdicionarNome());
        panelDataViewBeneficiarios.addOrReplace(getDataViewBeneficiarios());
        target.appendJavaScript("ancorarResultadoPesquisa('#ancoraPesquisa');");
        target.add(panelAdicionarBeneficiario);
        target.add(panelDataViewBeneficiarios);
    }

    // Irá ocultar ou mostrar os botões de adicionar/editar/cancelar ediçao do
    // 'Adicionar Beneficiário'
    public void visualizarOcultarBotoesEdicao(boolean editando) {
        buttonAdicionar.setVisible(!editando);
        buttonSalverEdicao.setVisible(editando);
        buttonCancelarEdicao.setVisible(editando);
    }
    
    private String limparCampos(String valor) {
        String value = valor;
        value = value.replace(".", "");
        value = value.replace("/", "");
        value = value.replace("-", "");
        value = value.replace("(", "");
        value = value.replace(")", "");
        return value;
    }
}
