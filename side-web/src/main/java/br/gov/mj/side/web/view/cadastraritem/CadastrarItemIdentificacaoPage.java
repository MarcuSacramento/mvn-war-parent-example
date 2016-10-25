package br.gov.mj.side.web.view.cadastraritem;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraAjaxFallbackLink;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.enums.EnumPerfilEntidade;
import br.gov.mj.side.entidades.programa.licitacao.contrato.Contrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.ObjetoFornecimentoContrato;
import br.gov.mj.side.web.dto.ContratoDto;
import br.gov.mj.side.web.service.ContratoService;
import br.gov.mj.side.web.service.OrdemFornecimentoContratoService;
import br.gov.mj.side.web.view.HomePage;
import br.gov.mj.side.web.view.template.TemplatePage;

@AuthorizeInstantiation({ CadastrarItemIdentificacaoPage.ROLE_MANTER_CADASTRAR_ITEM_INCLUIR, CadastrarItemIdentificacaoPage.ROLE_MANTER_CADASTRAR_ITEM_ALTERAR, CadastrarItemIdentificacaoPage.ROLE_MANTER_CADASTRAR_ITEM_EXCLUIR, CadastrarItemIdentificacaoPage.ROLE_MANTER_CADASTRAR_ITEM_VISUALIZAR })
public class CadastrarItemIdentificacaoPage extends TemplatePage {

    private static final long serialVersionUID = 1L;

    public static final String ROLE_MANTER_CADASTRAR_ITEM_INCLUIR = "manter_cadastrar_item:incluir";
    public static final String ROLE_MANTER_CADASTRAR_ITEM_ALTERAR = "manter_cadastrar_item:alterar";
    public static final String ROLE_MANTER_CADASTRAR_ITEM_EXCLUIR = "manter_cadastrar_item:excluir";
    public static final String ROLE_MANTER_CADASTRAR_ITEM_VISUALIZAR = "manter_cadastrar_item:visualizar";

    private Form<ObjetoFornecimentoContrato> form;
    private ObjetoFornecimentoContrato objetoFornecimentoContrato;
    private String codigoVerificacao;
    private Boolean validar = Boolean.FALSE;
    private EnumPerfilEntidade perfilUsuarioLogado;
    private Entidade entidadeLogada;

    @Inject
    private ComponentFactory componentFactory;
    @Inject
    private OrdemFornecimentoContratoService ordemFornecimentoContratoService;
    @Inject
    private ContratoService contratoService;

    public CadastrarItemIdentificacaoPage(PageParameters pageParameters) {
        super(pageParameters);
        setTitulo("Cadastrar Item");
        this.entidadeLogada = (Entidade) getSessionAttribute("entidade");
        this.perfilUsuarioLogado = entidadeLogada.getPerfilEntidade();

        initComponents();
    }

    private void initComponents() {
        form = componentFactory.newForm("form", new CompoundPropertyModel<ObjetoFornecimentoContrato>(objetoFornecimentoContrato));

        form.add(componentFactory.newLink("lnkPainelFornecedor", HomePage.class));
        form.add(newTextFieldIdentificacao());
        form.add(newButtonCadastrarItem());
        form.add(newButtonVoltar());

        add(form);
    }

    // componentes
    public TextField<String> newTextFieldIdentificacao() {
        TextField<String> field = componentFactory.newTextField("codigoVerificacao", "Identificação", Boolean.TRUE, new PropertyModel<String>(this, "codigoVerificacao"));
        actionTextFieldIdentificacao(field);
        return field;
    }

    private Button newButtonCadastrarItem() {
        Button btnCadastrarItem = componentFactory.newButton("btnCadastrarItem", () -> buscaItemCadastro());
        authorize(btnCadastrarItem, RENDER, CadastrarItemIdentificacaoPage.ROLE_MANTER_CADASTRAR_ITEM_INCLUIR);
        return btnCadastrarItem;
    }

    private InfraAjaxFallbackLink<Void> newButtonVoltar() {
        return componentFactory.newAjaxFallbackLink("btnVoltar", (target) -> voltar());
    }

    // acao

    // pega o valor inserido no input e seta na variavel.
    private void actionTextFieldIdentificacao(TextField<String> field) {
        AjaxFormComponentUpdatingBehavior onChangeAjaxBehavior = new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                codigoVerificacao = field.getConvertedInput();
            }
        };
        field.add(onChangeAjaxBehavior);
    }

    // busca o item.
    private void buscaItemCadastro() {
        if (codigoVerificacao != null) {
            this.objetoFornecimentoContrato = this.ordemFornecimentoContratoService.buscarObjetoFornecimentoContrato(Long.parseLong(codigoVerificacao));
            if (this.objetoFornecimentoContrato != null) {
                validaEntidadeLogada();
                if (validar) {
                    setResponsePage(new ConferenciaCadastrarItemPage(getPageParameters(), this, Long.parseLong(codigoVerificacao)));
                } else {
                    getSession().error("Código de identificação não localizado.");
                }
            } else {
                getSession().error("Código de identificação não localizado.");
            }

        } else {
            getSession().error("Por favor, informe a identificação.");
        }

    }

    // valida se o usuario logado pode acessar o item pesquisado.
    private void validaEntidadeLogada() {

        if (perfilUsuarioLogado.equals(EnumPerfilEntidade.FORNECEDOR)) {
            ContratoDto contratoDto = new ContratoDto();
            List<Contrato> listaContrato = new ArrayList<>();

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
                validar = Boolean.TRUE;
            }
        }
    }

    private void voltar() {
        setResponsePage(HomePage.class);
    }
}
