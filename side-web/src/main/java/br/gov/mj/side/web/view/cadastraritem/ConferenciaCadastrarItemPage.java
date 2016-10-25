package br.gov.mj.side.web.view.cadastraritem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraAjaxFallbackLink;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.enums.EnumPerfilEntidade;
import br.gov.mj.side.entidades.enums.EnumSituacaoBem;
import br.gov.mj.side.entidades.enums.EnumSituacaoPreenchimentoItemFormatacaoFornecedor;
import br.gov.mj.side.entidades.programa.licitacao.contrato.Contrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.FormatacaoObjetoFornecimento;
import br.gov.mj.side.entidades.programa.licitacao.contrato.ObjetoFornecimentoContrato;
import br.gov.mj.side.web.dto.ContratoDto;
import br.gov.mj.side.web.service.ContratoService;
import br.gov.mj.side.web.service.OrdemFornecimentoContratoService;
import br.gov.mj.side.web.util.SideUtil;
import br.gov.mj.side.web.view.HomePage;
import br.gov.mj.side.web.view.fornecedor.OrdemFornecimentoContratoPage;
import br.gov.mj.side.web.view.programa.inscricao.RegistrarRecebimentoPage;
import br.gov.mj.side.web.view.template.TemplatePage;

@AuthorizeInstantiation({ ConferenciaCadastrarItemPage.ROLE_MANTER_CADASTRAR_ITEM_INCLUIR, ConferenciaCadastrarItemPage.ROLE_MANTER_CADASTRAR_ITEM_ALTERAR, ConferenciaCadastrarItemPage.ROLE_MANTER_CADASTRAR_ITEM_EXCLUIR, ConferenciaCadastrarItemPage.ROLE_MANTER_CADASTRAR_ITEM_VISUALIZAR })
public class ConferenciaCadastrarItemPage extends TemplatePage {

    private static final long serialVersionUID = 1L;

    public static final String ROLE_MANTER_CADASTRAR_ITEM_INCLUIR = "manter_cadastrar_item:incluir";
    public static final String ROLE_MANTER_CADASTRAR_ITEM_ALTERAR = "manter_cadastrar_item:alterar";
    public static final String ROLE_MANTER_CADASTRAR_ITEM_EXCLUIR = "manter_cadastrar_item:excluir";
    public static final String ROLE_MANTER_CADASTRAR_ITEM_VISUALIZAR = "manter_cadastrar_item:visualizar";

    private Form<ObjetoFornecimentoContrato> form;
    private Page backPage;
    private ObjetoFornecimentoContrato objetoFornecimentoContrato;
    private Long codigoVerificacao;
    private List<FormatacaoObjetoFornecimento> listaFormatacao = new ArrayList<>();
    private int indiceFormatacaoContrato;
    private Boolean isMobile;
    private AjaxSubmitLink buttonConferencia;
    private AjaxSubmitLink buttonVoltar;
    private AjaxSubmitLink buttonIdentificarItem;
    private AjaxSubmitLink buttonQuesitoPreenchidos;
    private AjaxSubmitLink buttonQuesitoNaoPreenchidos;
    private StringValue hash;
    private Boolean validar = Boolean.FALSE;
    private EnumPerfilEntidade perfilUsuarioLogado;
    private Entidade entidadeLogada;

    private PanelGridConferencia panelGridConferencia;
    private PanelConformidadeItem panelConformidadeItem;

    @Inject
    private ComponentFactory componentFactory;
    @Inject
    private OrdemFornecimentoContratoService ordemFornecimentoContratoService;
    @Inject
    private ContratoService contratoService;

    // construtor para acesso via QRCode
    public ConferenciaCadastrarItemPage(PageParameters pageParameters) {
        super(pageParameters);
        this.entidadeLogada = (Entidade) getSessionAttribute("entidade");
        this.perfilUsuarioLogado = entidadeLogada.getPerfilEntidade();

        // recupera da sessao do usuario logado, a resolucao do dispositivo
        this.isMobile = (Boolean) getSessionAttribute("isMobile");

        validaHash();
        initVariaveis();
        initComponents();
        validaEntidadeLogada();
        setTitulo(this.objetoFornecimentoContrato.getItem().getNomeBem());
    }

    // construtor para acesso via sistema
    public ConferenciaCadastrarItemPage(PageParameters pageParameters, Page backPage, Long codigoVerificacao) {
        super(pageParameters);
        this.backPage = backPage;
        this.codigoVerificacao = codigoVerificacao;
        this.entidadeLogada = (Entidade) getSessionAttribute("entidade");
        this.perfilUsuarioLogado = entidadeLogada.getPerfilEntidade();

        // recupera da sessao do usuario logado, a resolucao do dispositivo
        this.isMobile = (Boolean) getSessionAttribute("isMobile");

        initVariaveis();
        initComponents();
        setTitulo(this.objetoFornecimentoContrato.getItem().getNomeBem());
    }

    private void initVariaveis() {
        buscaObjetoFornecimentoContrato();
        buscaListaFortacao();
    }

    private void initComponents() {
        form = componentFactory.newForm("form", new CompoundPropertyModel<ObjetoFornecimentoContrato>(objetoFornecimentoContrato));
        form.add(panelGridConferencia = new PanelGridConferencia("panelGridConferencia"));

        add(form);
    }

    // paineis
    private class PanelGridConferencia extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelGridConferencia(String id) {
            super(id);

            add(newDataViewListaConferencia());
            add(panelConformidadeItem = new PanelConformidadeItem("panelConformidadeItem"));
            add(newButtonConferencia());
            add(newButtonVoltar());
            add(newButtonVoltarIdentificarItem());
        }
    }

    private class PanelConformidadeItem extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelConformidadeItem(String id) {
            super(id);

            add(newLabelConformidade());
            add(newButtonQuesitosPreenchidos());
            add(newButtonQuesitoNaoPreenchidos());
            setVisible(perfilUsuarioLogado.equals(EnumPerfilEntidade.BENEFICIARIO));
        }
    }

    // componentes
    private Label newLabelConformidade() {
        Label lblConformidade = componentFactory.newLabel("txtLabelConformidade", objetoFornecimentoContrato.getEstadoDeNovo() != null ? EnumSituacaoPreenchimentoItemFormatacaoFornecedor.PREENCHIDO.getDescricao() : EnumSituacaoPreenchimentoItemFormatacaoFornecedor.NAO_PREENCHIDO.getDescricao());
        lblConformidade.setOutputMarkupId(Boolean.TRUE);
        lblConformidade.setVisible(!isMobile);
        return lblConformidade;
    }

    public AjaxSubmitLink newButtonQuesitoNaoPreenchidos() {
        buttonQuesitoNaoPreenchidos = new AjaxSubmitLink("btnQuesitoNaoPreenchido", form) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form) {
                setResponsePage(new CadastrarItemPage(getPageParameters(), backPage, objetoFornecimentoContrato, listaFormatacao.size(), perfilUsuarioLogado, alterarCadastroItens()));
            }
        };
        buttonQuesitoNaoPreenchidos.setOutputMarkupId(Boolean.TRUE);
        buttonQuesitoNaoPreenchidos.setVisible(isMobile && objetoFornecimentoContrato.getEstadoDeNovo() == null);
        return buttonQuesitoNaoPreenchidos;
    }

    public AjaxSubmitLink newButtonQuesitosPreenchidos() {
        buttonQuesitoPreenchidos = new AjaxSubmitLink("btnQuesitoPreenchido", form) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form) {
                setResponsePage(new CadastrarItemPage(getPageParameters(), backPage, objetoFornecimentoContrato, listaFormatacao.size(), perfilUsuarioLogado, alterarCadastroItens()));
            }
        };
        buttonQuesitoPreenchidos.setOutputMarkupId(Boolean.TRUE);
        buttonQuesitoPreenchidos.setVisible(isMobile && objetoFornecimentoContrato.getEstadoDeNovo() != null);
        return buttonQuesitoPreenchidos;
    }

    public AjaxSubmitLink newButtonConferencia() {
        buttonConferencia = new AjaxSubmitLink("btnConferencia", form) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form) {
                setResponsePage(new CadastrarItemPage(getPageParameters(), backPage, objetoFornecimentoContrato, 0, perfilUsuarioLogado, alterarCadastroItens()));
            }
        };
        buttonConferencia.setOutputMarkupId(Boolean.TRUE);
        buttonConferencia.setVisible(!isMobile);
        return buttonConferencia;
    }

    public InfraAjaxFallbackLink<Void> newButtonVoltar() {
        InfraAjaxFallbackLink<Void> buttonVoltar = componentFactory.newAjaxFallbackLink("btnVoltar", (target) -> voltar(target));
        buttonVoltar.setOutputMarkupId(Boolean.TRUE);
        buttonVoltar.setVisible(!isMobile);

        return buttonVoltar;
    }

    private void voltar(AjaxRequestTarget target) {
        if (backPage != null) {
            String tipoPessoa = getPageParameters().get("tipoPessoa").toString();
            if ("P".equals(tipoPessoa)) {
                setResponsePage(new OrdemFornecimentoContratoPage(getPageParameters()));
            } else if ("R".equals(tipoPessoa)) {
                setResponsePage(new RegistrarRecebimentoPage(getPageParameters()));
            } else {
                setResponsePage(backPage);
            }
        } else {
            setResponsePage(new CadastrarItemIdentificacaoPage(getPageParameters()));
        }
    }

    public AjaxSubmitLink newButtonVoltarIdentificarItem() {
        buttonIdentificarItem = new AjaxSubmitLink("btnVoltarIdentificacao", form) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form) {
                setResponsePage(new CadastrarItemIdentificacaoPage(getPageParameters()));
            }
        };
        buttonIdentificarItem.setOutputMarkupId(Boolean.TRUE);
        buttonIdentificarItem.setVisible(isMobile);
        return buttonIdentificarItem;
    }

    // gride
    public DataView<FormatacaoObjetoFornecimento> newDataViewListaConferencia() {
        return new DataView<FormatacaoObjetoFornecimento>("listaFormatacaoItens", new ProviderFormatacoes()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<FormatacaoObjetoFornecimento> item) {

                item.add(new Label("txtTituloQuesito", item.getModelObject().getFormatacao().getTituloQuesito()));

                Label situacao = new Label("txtSituacao", item.getModelObject().getFormatacaoResposta() != null ? EnumSituacaoPreenchimentoItemFormatacaoFornecedor.PREENCHIDO.getDescricao() : EnumSituacaoPreenchimentoItemFormatacaoFornecedor.NAO_PREENCHIDO.getDescricao());
                situacao.setVisible(!isMobile);
                item.add(situacao);

                InfraAjaxFallbackLink<Void> btnConferenciaCadastrada = componentFactory.newAjaxFallbackLink("btnConferenciaCadastrada", (target) -> alterar(item));
                authorize(btnConferenciaCadastrada, RENDER, ConferenciaCadastrarItemPage.ROLE_MANTER_CADASTRAR_ITEM_ALTERAR);
                btnConferenciaCadastrada.setVisible(item.getModelObject().getFormatacaoResposta() != null && isMobile);
                item.add(btnConferenciaCadastrada);

                InfraAjaxFallbackLink<Void> btnConferenciaNaoCadastrada = componentFactory.newAjaxFallbackLink("btnConferenciaNaoCadastrada", (target) -> alterar(item));
                authorize(btnConferenciaNaoCadastrada, RENDER, ConferenciaCadastrarItemPage.ROLE_MANTER_CADASTRAR_ITEM_ALTERAR);
                btnConferenciaNaoCadastrada.setVisible(item.getModelObject().getFormatacaoResposta() == null && isMobile);
                item.add(btnConferenciaNaoCadastrada);

            }
        };
    }

    // provaider
    private class ProviderFormatacoes extends SortableDataProvider<FormatacaoObjetoFornecimento, String> {
        private static final long serialVersionUID = 1L;

        @Override
        public Iterator<FormatacaoObjetoFornecimento> iterator(long first, long size) {
            return listaFormatacao.iterator();
        }

        @Override
        public long size() {
            return listaFormatacao.size();
        }

        @Override
        public IModel<FormatacaoObjetoFornecimento> model(FormatacaoObjetoFornecimento object) {
            return new CompoundPropertyModel<FormatacaoObjetoFornecimento>(object);
        }
    }

    // acoes

    // mobile: metodo para capturar o indice do item clicado na lista e envia o
    // objeto e o indice do item clicado
    private void alterar(Item<FormatacaoObjetoFornecimento> item) {
        for (int i = 0; i < listaFormatacao.size(); i++) {
            if (item.getModelObject().equals(listaFormatacao.get(i))) {
                indiceFormatacaoContrato = i;
            }
        }
        setResponsePage(new CadastrarItemPage(getPageParameters(), this, objetoFornecimentoContrato, indiceFormatacaoContrato, perfilUsuarioLogado, alterarCadastroItens()));
    }

    // valida se o usuario logado pode acessar o item pesquisado (FORNECEDOR ou
    // BENEFICIARIO)
    private void validaEntidadeLogada() {

        if (perfilUsuarioLogado.equals(EnumPerfilEntidade.FORNECEDOR)) {
            ContratoDto contratoDto = new ContratoDto();
            List<Contrato> listaContrato = new ArrayList<>();

            // gestor nao tem entidade logada.
            if (entidadeLogada == null) {
                getSession().error("Perfil sem autorização para acessar o item.");
                setResponsePage(new HomePage(null));
            } else {
                contratoDto.setFornecedor(entidadeLogada);
                listaContrato = contratoService.buscarSemPaginacao(contratoDto);
                if (!listaContrato.isEmpty() && listaContrato.size() > 0) {
                    for (Contrato contrato : listaContrato) {
                        if (contrato.equals(this.objetoFornecimentoContrato.getOrdemFornecimento().getContrato())) {
                            validar = Boolean.TRUE;
                        }
                    }
                    if (!validar) {
                        getSession().error("Item não localizado em seus contratos.");
                        setResponsePage(new HomePage(null));
                    }
                } else {
                    getSession().error("Código de identificação não localizado.");
                    setResponsePage(new HomePage(null));
                }
            }
        } else if (perfilUsuarioLogado.equals(EnumPerfilEntidade.BENEFICIARIO)) {
            if (entidadeLogada.equals(this.objetoFornecimentoContrato.getLocalEntrega().getEntidade())) {
                direcionarRegistroPatrimonio();
                validar = Boolean.TRUE;
            }
        }
    }

    // possibilita a alteração dos itens ou não
    private Boolean alterarCadastroItens() {
        if (this.objetoFornecimentoContrato.getObjetoDevolvido() == null || this.objetoFornecimentoContrato.getObjetoDevolvido() == false) {
            return true;
        } else {
            return false;
        }
    }

    // vai para a página de Registrar Patrimônio
    private void direcionarRegistroPatrimonio() {
        if (this.objetoFornecimentoContrato.getSituacaoBem().equals(EnumSituacaoBem.DOADO)) {
            setResponsePage(new RegistrarPatrimonioPage(getPageParameters(), objetoFornecimentoContrato, new HomePage(null), null));
        }
    }

    // verifica se o hash veio nulo.
    // nao vindo nulo, converte para Long.
    private void validaHash() {
        hash = getRequest().getRequestParameters().getParameterValue("hash");

        if (!hash.isNull()) {
            codigoVerificacao = Long.parseLong(hash.toString());
        } else {
            if (codigoVerificacao == null) {
                getSession().error("Item não localizado.");
                setResponsePage(new HomePage(null));
            }
        }
    }

    // busca ObjetoFornecimentoContrato do codigo de verificacao.
    private void buscaObjetoFornecimentoContrato() {
        this.objetoFornecimentoContrato = this.ordemFornecimentoContratoService.buscarObjetoFornecimentoContratoClicado(codigoVerificacao);
        if (this.objetoFornecimentoContrato == null) {
            getSession().error("Código de identificação não localizado.");
            setResponsePage(new HomePage(null));
        }
    }

    // busca lista de formatacoes de acordo com o codigo de verificacao e perfil
    // do usuario logado (FORNECEDOR ou BENEFICIARIO)
    private void buscaListaFortacao() {
        this.listaFormatacao.addAll(SideUtil.convertDtoToEntityFormatacaoObjetoFornecimentoDto(this.ordemFornecimentoContratoService.buscarListaFormatacaoObjetoFornecimento(codigoVerificacao, perfilUsuarioLogado)));
    }
}
