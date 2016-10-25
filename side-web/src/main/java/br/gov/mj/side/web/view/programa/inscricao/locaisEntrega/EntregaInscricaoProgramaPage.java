package br.gov.mj.side.web.view.programa.inscricao.locaisEntrega;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import br.gov.mj.infra.negocio.exception.BusinessException;
import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.model.LambdaModel;
import br.gov.mj.side.entidades.entidade.LocalEntregaEntidade;
import br.gov.mj.side.entidades.enums.EnumStatusLocalEntrega;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoLocalEntrega;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoLocalEntregaBem;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoLocalEntregaKit;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoPrograma;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoProgramaBem;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoProgramaKit;
import br.gov.mj.side.web.dto.InscricaoLocalEntregaItemDto;
import br.gov.mj.side.web.dto.PermissaoProgramaDto;
import br.gov.mj.side.web.service.BeneficiarioService;
import br.gov.mj.side.web.service.InscricaoProgramaService;
import br.gov.mj.side.web.service.PublicizacaoService;
import br.gov.mj.side.web.view.components.AjaxDialogButton;
import br.gov.mj.side.web.view.dashboard.DashboardInscricoesPage;
import br.gov.mj.side.web.view.enums.EnumTipoItemEntrega;
import br.gov.mj.side.web.view.programa.inscricao.InscricaoNavPanel;
import br.gov.mj.side.web.view.template.TemplatePage;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal.Backdrop;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.TextContentModal;

public class EntregaInscricaoProgramaPage extends TemplatePage {

    private static final long serialVersionUID = 1L;

    private LocalEntregaEntidade localEntregaEntidadeSelecionada;
    private InscricaoPrograma inscricao;
    private List<InscricaoProgramaBem> bensInscricao;
    private List<InscricaoProgramaKit> kitsInscricao;
    private List<InscricaoLocalEntregaBem> bensEntregaSelecionados = new ArrayList<InscricaoLocalEntregaBem>();
    private List<InscricaoLocalEntregaKit> kitsEntregaSelecionados = new ArrayList<InscricaoLocalEntregaKit>();

    private boolean permissaoVincular = false;

    private List<InscricaoLocalEntregaItemDto> itens;

    private Form<InscricaoPrograma> form;

    private WebMarkupContainer containerInscricaoBem;
    private WebMarkupContainer containerEntregaBem;
    private WebMarkupContainer containerInscricaoKit;
    private WebMarkupContainer containerEntregaKit;
    private WebMarkupContainer containerLocaisEntrega;
    private DropDownChoice<LocalEntregaEntidade> dropLocalEntregaEntidade;

    private Modal<String> modalConfirm;
    private String msgConfirmacao;

    @Inject
    private BeneficiarioService beneficiarioService;

    @Inject
    private ComponentFactory componentFactory;

    @Inject
    private InscricaoProgramaService inscricaoProgramaService;

    @Inject
    PublicizacaoService publicizacaoService;

    public EntregaInscricaoProgramaPage(PageParameters pageParameters, InscricaoPrograma inscricao) {
        super(pageParameters);
        this.inscricao = inscricao;
        initObjects();
        initComponents();
    }

    private void initObjects() {

        PermissaoProgramaDto permissoesPrograma = publicizacaoService.buscarPermissoesPrograma(inscricao.getPrograma());
        if (permissoesPrograma != null) {
            permissaoVincular = permissoesPrograma.getVincularLocaisDeEntrega();
        }

        // lista para manipulação dos bens definidos na inscrição, que podem ser
        // adicionados
        bensInscricao = inscricaoProgramaService.buscarInscricaoProgramaBem(inscricao);

        // lista para manipulação dos kits definidos na inscrição, que podem ser
        // adicionados
        kitsInscricao = inscricaoProgramaService.buscarInscricaoProgramaKit(inscricao);

        // Recupera locais de entrega caso exista para edição
        inscricao.setLocaisEntregaInscricao(inscricaoProgramaService.buscarInscricaoLocalEntrega(inscricao));
        if (inscricao.getLocaisEntregaInscricao() != null && !inscricao.getLocaisEntregaInscricao().isEmpty()) {
            for (InscricaoLocalEntrega local : inscricao.getLocaisEntregaInscricao()) {
                local.setBensEntrega(inscricaoProgramaService.buscarInscricaoLocalEntregaBem(local));
                local.setKitsEntrega(inscricaoProgramaService.buscarInscricaoLocalEntregaKit(local));
            }
            bensInscricao.clear();
            kitsInscricao.clear();
        } else {
            inscricao.setLocaisEntregaInscricao(new ArrayList<InscricaoLocalEntrega>());
        }

        // lista temporaria para apresentação dos itens vinculados na tela.
        // Criada para apresentar os itens agrupados pelo local de entrega
        itens = new ArrayList<InscricaoLocalEntregaItemDto>();
        sincronizarItensDto();
    }

    private void initComponents() {
        setTitulo(getTituloNomePrograma());

        form = componentFactory.newForm("form", inscricao);
        form.add(new Label("lblMsgAlerta", getString("MT023")).setVisible(!permissaoVincular));

        form.add(new InscricaoNavPanel("navPanel", inscricao, null, this));
        dropLocalEntregaEntidade = newDownDownLocalEntrega();
        form.add(dropLocalEntregaEntidade);
        form.add(newButtonNovoEndereco());

        containerInscricaoBem = new WebMarkupContainer("containerInscricaoBem");
        containerInscricaoBem.add(newListViewInscricaoBens());

        AjaxFallbackButton btnAddAllInscricaoBem = componentFactory.newAjaxFallbackButton("btnAddAll", form, (target, form) -> moverTodosDireitaBem(target));
        btnAddAllInscricaoBem.setVisible(permissaoVincular);
        containerInscricaoBem.add(btnAddAllInscricaoBem);
        form.add(containerInscricaoBem);

        containerEntregaBem = new WebMarkupContainer("containerEntregaBem");
        containerEntregaBem.add(newListViewEntregaBens());
        AjaxFallbackButton btnRemoveAllEntregaBem = componentFactory.newAjaxFallbackButton("btnRemoveAll", form, (target, form) -> moverTodosEsquerdaBem(target));
        btnRemoveAllEntregaBem.setVisible(permissaoVincular);
        containerEntregaBem.add(btnRemoveAllEntregaBem);
        form.add(containerEntregaBem);

        containerInscricaoKit = new WebMarkupContainer("containerIncricaoKit");
        containerInscricaoKit.add(newListViewInscricaoKits());
        AjaxFallbackButton btnAddAllInscricaoKit = componentFactory.newAjaxFallbackButton("btnAddAll", form, (target, form) -> moverTodosDireitaKit(target));
        btnAddAllInscricaoKit.setVisible(permissaoVincular);
        containerInscricaoKit.add(btnAddAllInscricaoKit);
        form.add(containerInscricaoKit);

        containerEntregaKit = new WebMarkupContainer("containerEntregaKit");
        containerEntregaKit.add(newListViewEntregaKits());
        AjaxFallbackButton btnRemoveEntregaKits = componentFactory.newAjaxFallbackButton("btnRemoveAll", form, (target, form) -> moverTodosEsquerdaKit(target));
        btnRemoveEntregaKits.setVisible(permissaoVincular);
        containerEntregaKit.add(btnRemoveEntregaKits);
        form.add(containerEntregaKit);

        form.add(newButtonAdicionarLocalEntrega());

        containerLocaisEntrega = new WebMarkupContainer("containerLocaisEntrega");
        containerLocaisEntrega.add(newListViewLocaisEntrega());
        form.add(containerLocaisEntrega);

        form.add(newButtonSalvar());
        form.add(newButtonVoltar());

        modalConfirm = newModal("modalConfirm");
        modalConfirm.show(false);

        add(modalConfirm);

        add(form);
    }

    private Button newButtonVoltar() {
        return componentFactory.newButton("btnVoltar", () -> setResponsePage(DashboardInscricoesPage.class));
    }

    private Button newButtonSalvar() {
        Button button = componentFactory.newButton("btnSalvar", () -> salvar());
        button.setVisible(permissaoVincular);
        return button;
    }

    private AjaxFallbackButton newButtonAdicionarLocalEntrega() {
        AjaxFallbackButton button = componentFactory.newAjaxFallbackButton("btnAddLocalEntrega", form, (target, form) -> adicionarLocalEntrega(target));
        button.setVisible(permissaoVincular);
        return button;
    }

    private AjaxFallbackButton newButtonNovoEndereco() {
        AjaxFallbackButton button = componentFactory.newAjaxFallbackButton("btnNovoEndereco", form, (target, form) -> novoEndereco(target));
        button.setVisible(permissaoVincular);
        return button;
    }

    private void novoEndereco(AjaxRequestTarget target) {
        if (!inscricao.getLocaisEntregaInscricao().isEmpty()) {
            setMsgConfirmacao(getString("MT024"));
            if (target != null) {
                modalConfirm.show(true);
                target.add(modalConfirm);
            }
        } else {
            setResponsePage(LocaisEntregaPage.class);
        }
    }

    private void salvar() {
        atualizarReferenciaItens();
        inscricaoProgramaService.validarQuantidadesParaEntrega(inscricao, new BusinessException());
        inscricaoProgramaService.sincronizarLocaisEntrega(inscricao, getIdentificador());
        getSession().info(getString("MT021"));
        setResponsePage(DashboardInscricoesPage.class);
    }

    /**
     * Para todos os locais de entrega(InscricaoLocalEntrega), percorre a lista
     * de bens(InscricaoLocalEntregaBem) e kits(InscricaoLocalEntregaKit)
     * atualizando a referência do objeto inscricaoProgramaBem e
     * InscricaoProgramaKit
     *
     */
    private void atualizarReferenciaItens() {
        for (InscricaoLocalEntrega inscricaoLocalEntrega : inscricao.getLocaisEntregaInscricao()) {
            // Atualiza a referência do objeto InscricaoProgramaBem dentro de
            // InscricaoLocalEntregaBem para cada item da lista
            for (InscricaoLocalEntregaBem inscricaoLocalEntregaBem : inscricaoLocalEntrega.getBensEntrega()) {
                for (InscricaoProgramaBem inscricaoProgramaBem : inscricao.getProgramasBem()) {
                    if (inscricaoLocalEntregaBem.getInscricaoProgramaBem().getId() == inscricaoProgramaBem.getId()) {
                        inscricaoLocalEntregaBem.setInscricaoProgramaBem(inscricaoProgramaBem);
                    }
                }
            }
            // Atualiza a referência do objeto InscricaoProgramaKit dentro de
            // InscricaoLocalEntregaKit para cada item da lista
            for (InscricaoLocalEntregaKit inscricaoLocalEntregaKit : inscricaoLocalEntrega.getKitsEntrega()) {
                for (InscricaoProgramaKit inscricaoProgramaKit : inscricao.getProgramasKit()) {
                    if (inscricaoLocalEntregaKit.getInscricaoProgramaKit().getId() == inscricaoProgramaKit.getId()) {
                        inscricaoLocalEntregaKit.setInscricaoProgramaKit(inscricaoProgramaKit);
                    }
                }
            }
        }
    }

    private DropDownChoice<LocalEntregaEntidade> newDownDownLocalEntrega() {
        DropDownChoice<LocalEntregaEntidade> dropDown = componentFactory.newDropDownChoice("localEntregaEntidadeSelecionada", "Endereço para entrega", false, "id", "nomeEndereco", new LambdaModel<LocalEntregaEntidade>(this::getLocalEntrega, this::setLocalEntrega),
                beneficiarioService.buscarLocaisEntrega(inscricao.getPessoaEntidade().getEntidade(), EnumStatusLocalEntrega.HABILITADO), null);
        dropDown.setEnabled(permissaoVincular);
        return dropDown;
    }

    private ListView<InscricaoProgramaBem> newListViewInscricaoBens() {
        return new PropertyListView<InscricaoProgramaBem>("programasBem", bensInscricao) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<InscricaoProgramaBem> item) {
                item.add(new Label("programaBem.bem.nomeBem"));
                item.add(new Label("quantidade"));
                AjaxFallbackButton btnAdd = componentFactory.newAjaxFallbackButton("btnAdd", form, (target, form) -> moverDireitaBem(item, target));
                btnAdd.setVisible(permissaoVincular);
                item.add(btnAdd);
            }
        };
    }

    private ListView<InscricaoProgramaKit> newListViewInscricaoKits() {
        return new PropertyListView<InscricaoProgramaKit>("programasKit", kitsInscricao) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<InscricaoProgramaKit> item) {
                item.add(new Label("programaKit.kit.nomeKit"));
                item.add(new Label("quantidade"));
                AjaxFallbackButton btnAdd = componentFactory.newAjaxFallbackButton("btnAdd", form, (target, form) -> moverDireitaKit(item, target));
                btnAdd.setVisible(permissaoVincular);
                item.add(btnAdd);

            }
        };
    }

    private ListView<InscricaoLocalEntregaBem> newListViewEntregaBens() {
        return new PropertyListView<InscricaoLocalEntregaBem>("bensEntregaSelecionados", bensEntregaSelecionados) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<InscricaoLocalEntregaBem> item) {
                item.add(new Label("inscricaoProgramaBem.programaBem.bem.nomeBem"));
                item.add(new QtdBemEntregaPanel("qtdPanel", item));
                AjaxFallbackButton btnRemove = componentFactory.newAjaxFallbackButton("btnRemove", form, (target, form) -> moverEsquerdaBem(item, target));
                btnRemove.setVisible(permissaoVincular);
                item.add(btnRemove);
            }
        };
    }

    private class QtdBemEntregaPanel extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public QtdBemEntregaPanel(String id, ListItem<InscricaoLocalEntregaBem> item) {
            super(id);
            add(new Label("inscricaoProgramaBem.quantidade"));
            add(new TextField<Integer>("quantidade", new PropertyModel<Integer>(item.getModelObject(), "quantidade")));
        }

    }

    private ListView<InscricaoLocalEntregaKit> newListViewEntregaKits() {
        return new PropertyListView<InscricaoLocalEntregaKit>("kitsEntregaSelecionados", kitsEntregaSelecionados) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<InscricaoLocalEntregaKit> item) {
                item.add(new Label("inscricaoProgramaKit.programaKit.kit.nomeKit"));
                item.add(new QtdKitEntregaPanel("qtdPanel", item));
                AjaxFallbackButton btnRemove = componentFactory.newAjaxFallbackButton("btnRemove", form, (target, form) -> moverEsquerdaKit(item, target));
                btnRemove.setVisible(permissaoVincular);
                item.add(btnRemove);
            }
        };
    }

    private class QtdKitEntregaPanel extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public QtdKitEntregaPanel(String id, ListItem<InscricaoLocalEntregaKit> item) {
            super(id);
            add(new Label("inscricaoProgramaKit.quantidade"));
            add(new TextField<Integer>("quantidade", new PropertyModel<Integer>(item.getModelObject(), "quantidade")));
        }

    }

    private ListView<InscricaoLocalEntregaItemDto> newListViewLocaisEntrega() {
        return new PropertyListView<InscricaoLocalEntregaItemDto>("locaisEntregaInscricao", itens) {
            private static final long serialVersionUID = 1L;

            private long idLocalAnterior = 0;

            @Override
            protected void populateItem(ListItem<InscricaoLocalEntregaItemDto> item) {

                // Campo que receberá o atributo rowspan
                Label lbl = new Label("localEntrega.localEntregaEntidade.nomeEndereco");
                InscricaoLocalEntrega localEntrega = item.getModelObject().getLocalEntrega();
                int qtde = contarQtdItensParaLocalEntrega(localEntrega);
                if (qtde != 1) {
                    if (qtde > 1 && idLocalAnterior != localEntrega.getLocalEntregaEntidade().getId()) {
                        lbl.add(new AttributeAppender("rowspan", new Model<Integer>(qtde)));
                    } else if (idLocalAnterior == localEntrega.getLocalEntregaEntidade().getId()) {
                        lbl.setVisible(false);
                    }
                }
                item.add(lbl);
                item.add(new Label("nome"));
                item.add(new Label("quantidade"));
                item.add(new Label("tipoItem.descricao"));
                AjaxFallbackButton btnRemoverItem = componentFactory.newAjaxFallbackButton("btnRemoverItem", form, (target, form) -> removerItens(item, target));
                btnRemoverItem.setVisible(permissaoVincular);
                item.add(btnRemoverItem);

                idLocalAnterior = localEntrega.getLocalEntregaEntidade().getId();
            }

            @Override
            protected void onAfterRender() {
                idLocalAnterior = 0;
                super.onAfterRender();
            }
        };
    }

    /**
     * Recupera a quantidade de InscricaoLocalEntrega na Lista de itens
     * 
     * @param localEntrega
     * @return int
     */
    private int contarQtdItensParaLocalEntrega(InscricaoLocalEntrega localEntrega) {
        int total = 0;
        for (InscricaoLocalEntregaItemDto itemDto : itens) {
            if (itemDto.getLocalEntrega().getLocalEntregaEntidade().getId() == localEntrega.getLocalEntregaEntidade().getId()) {
                total++;
            }
        }
        return total;
    }

    private void moverTodosDireitaBem(AjaxRequestTarget target) {
        if (!bensInscricao.isEmpty()) {
            List<InscricaoProgramaBem> listTemp = new ArrayList<InscricaoProgramaBem>(bensInscricao);

            for (InscricaoProgramaBem inscricaoBem : listTemp) {
                InscricaoLocalEntregaBem entregaBem = criarInscricaoLocalEntregaBem(inscricaoBem);
                adicionarEntregaBem(entregaBem);
                removerInscricaoBem(inscricaoBem);
            }
            target.add(containerEntregaBem, containerInscricaoBem);
        }
    }

    private void moverTodosEsquerdaBem(AjaxRequestTarget target) {
        if (!bensEntregaSelecionados.isEmpty()) {
            List<InscricaoLocalEntregaBem> listTemp = new ArrayList<InscricaoLocalEntregaBem>(bensEntregaSelecionados);
            for (InscricaoLocalEntregaBem entregaBem : listTemp) {
                adicionarInscricaoProgramaBem(entregaBem.getInscricaoProgramaBem());
                removerEntregaBem(entregaBem);
            }
            target.add(containerEntregaBem, containerInscricaoBem);
        }
    }

    private void moverDireitaBem(ListItem<InscricaoProgramaBem> item, AjaxRequestTarget target) {
        InscricaoProgramaBem inscricaoBem = item.getModelObject();
        InscricaoLocalEntregaBem entregaBem = criarInscricaoLocalEntregaBem(inscricaoBem);
        adicionarEntregaBem(entregaBem);
        removerInscricaoBem(inscricaoBem);
        target.add(containerEntregaBem, containerInscricaoBem);
    }

    private void moverEsquerdaBem(ListItem<InscricaoLocalEntregaBem> item, AjaxRequestTarget target) {
        InscricaoLocalEntregaBem entregaBem = item.getModelObject();
        adicionarInscricaoProgramaBem(entregaBem.getInscricaoProgramaBem());
        removerEntregaBem(entregaBem);
        target.add(containerEntregaBem, containerInscricaoBem);
    }

    private void moverTodosDireitaKit(AjaxRequestTarget target) {
        if (!kitsInscricao.isEmpty()) {
            List<InscricaoProgramaKit> listTemp = new ArrayList<InscricaoProgramaKit>(kitsInscricao);

            for (InscricaoProgramaKit inscricaoKit : listTemp) {
                InscricaoLocalEntregaKit entregaKit = criarInscricaoLocalEntregaKit(inscricaoKit);
                adicionarEntregaKit(entregaKit);
                removerInscricaoKit(inscricaoKit);
            }
            target.add(containerEntregaKit, containerInscricaoKit);
        }
    }

    private void moverTodosEsquerdaKit(AjaxRequestTarget target) {
        if (!kitsEntregaSelecionados.isEmpty()) {
            List<InscricaoLocalEntregaKit> listTemp = new ArrayList<InscricaoLocalEntregaKit>(kitsEntregaSelecionados);
            for (InscricaoLocalEntregaKit entregaKit : listTemp) {
                adicionarInscricaoProgramaKit(entregaKit.getInscricaoProgramaKit());
                removerEntregaKit(entregaKit);
            }
            target.add(containerEntregaKit, containerInscricaoKit);
        }
    }

    private void moverDireitaKit(ListItem<InscricaoProgramaKit> item, AjaxRequestTarget target) {
        InscricaoProgramaKit inscricaoKit = item.getModelObject();
        InscricaoLocalEntregaKit entregaKit = criarInscricaoLocalEntregaKit(inscricaoKit);
        adicionarEntregaKit(entregaKit);
        removerInscricaoKit(inscricaoKit);
        target.add(containerEntregaKit, containerInscricaoKit);
    }

    private void moverEsquerdaKit(ListItem<InscricaoLocalEntregaKit> item, AjaxRequestTarget target) {
        InscricaoLocalEntregaKit entregaKit = item.getModelObject();
        adicionarInscricaoProgramaKit(entregaKit.getInscricaoProgramaKit());
        removerEntregaKit(entregaKit);
        target.add(containerEntregaKit, containerInscricaoKit);
    }

    private InscricaoLocalEntregaBem criarInscricaoLocalEntregaBem(InscricaoProgramaBem inscricaoBem) {
        InscricaoLocalEntregaBem entregaBem = new InscricaoLocalEntregaBem();
        entregaBem.setInscricaoProgramaBem(inscricaoBem);
        return entregaBem;
    }

    private InscricaoLocalEntregaKit criarInscricaoLocalEntregaKit(InscricaoProgramaKit inscricaoKit) {
        InscricaoLocalEntregaKit entregaKit = new InscricaoLocalEntregaKit();
        entregaKit.setInscricaoProgramaKit(inscricaoKit);
        return entregaKit;
    }

    private void adicionarEntregaBem(InscricaoLocalEntregaBem entregaBem) {
        boolean result = true;
        for (InscricaoLocalEntregaBem inscricaoLocalEntregaBem : bensEntregaSelecionados) {
            if (inscricaoLocalEntregaBem.getInscricaoProgramaBem().getId() == entregaBem.getInscricaoProgramaBem().getId()) {
                result = false;
            }
        }
        if (result) {
            bensEntregaSelecionados.add(entregaBem);
        }
    }

    private void adicionarEntregaKit(InscricaoLocalEntregaKit entregaKit) {
        boolean result = true;
        for (InscricaoLocalEntregaKit inscricaoLocalEntregaKit : kitsEntregaSelecionados) {
            if (inscricaoLocalEntregaKit.getInscricaoProgramaKit().getId() == entregaKit.getInscricaoProgramaKit().getId()) {
                result = false;
            }
        }
        if (result) {
            kitsEntregaSelecionados.add(entregaKit);
        }
    }

    private void adicionarInscricaoProgramaBem(InscricaoProgramaBem inscricaoBem) {
        bensInscricao.add(inscricaoBem);
    }

    private void adicionarInscricaoProgramaKit(InscricaoProgramaKit inscricaoKit) {
        kitsInscricao.add(inscricaoKit);
    }

    private void removerEntregaBem(InscricaoLocalEntregaBem entregaBem) {
        bensEntregaSelecionados.remove(entregaBem);
    }

    private void removerEntregaKit(InscricaoLocalEntregaKit entregaKit) {
        kitsEntregaSelecionados.remove(entregaKit);
    }

    private void removerInscricaoBem(InscricaoProgramaBem inscricaoBem) {
        bensInscricao.remove(inscricaoBem);
    }

    private void removerInscricaoKit(InscricaoProgramaKit inscricaoKit) {
        kitsInscricao.remove(inscricaoKit);
    }

    public LocalEntregaEntidade getLocalEntrega() {
        return localEntregaEntidadeSelecionada;
    }

    public void setLocalEntrega(LocalEntregaEntidade localEntrega) {
        this.localEntregaEntidadeSelecionada = localEntrega;
    }

    private void adicionarLocalEntrega(AjaxRequestTarget target) {

        if (validar()) {
            // Busca o local de entrega
            InscricaoLocalEntrega inscricaoLocalEntrega = buscarInscricaoLocalEntrega(localEntregaEntidadeSelecionada);
            if (inscricaoLocalEntrega != null) {
                // Caso exista atualiza os itens
                atualizarItens(inscricaoLocalEntrega);
            } else {
                // Caso não exista cria um novo local de entrega
                inscricaoLocalEntrega = criarInscricaoLocalEntrega(); // ok
                inscricao.getLocaisEntregaInscricao().add(inscricaoLocalEntrega); // ok
            }
            sincronizarItensDto(); // ok
            subtrairQtdesSelecionadas(); // ok
            bensEntregaSelecionados.clear(); // ok
            kitsEntregaSelecionados.clear(); // ok
            dropLocalEntregaEntidade.setModelObject(new LocalEntregaEntidade());
            target.add(containerEntregaBem, containerEntregaKit, containerInscricaoBem, containerInscricaoKit, containerLocaisEntrega, dropLocalEntregaEntidade);
        }

    }

    /**
     * Sincroniza a lista de bens e kits selecionados com a lista da inscrição
     * local entrega.
     * 
     * @param inscricaoLocalEntregam
     */
    private void atualizarItens(InscricaoLocalEntrega inscricaoLocalEntrega) {

        for (InscricaoLocalEntregaBem bemEntregaSelecionado : bensEntregaSelecionados) {
            if (existeBemNoLocalEntrega(bemEntregaSelecionado, inscricaoLocalEntrega)) {
                for (InscricaoLocalEntregaBem bemEntrega : inscricaoLocalEntrega.getBensEntrega()) {
                    if (bemEntrega.getInscricaoProgramaBem().getId() == bemEntregaSelecionado.getInscricaoProgramaBem().getId()) {
                        bemEntrega.setQuantidade(bemEntrega.getQuantidade() + bemEntregaSelecionado.getQuantidade());
                    }
                }
            } else {
                bemEntregaSelecionado.setInscricaoLocalEntrega(inscricaoLocalEntrega);
                inscricaoLocalEntrega.getBensEntrega().add(bemEntregaSelecionado);
            }
        }

        for (InscricaoLocalEntregaKit kitEntregaSelecionado : kitsEntregaSelecionados) {
            if (existeKitNoLocalEntrega(kitEntregaSelecionado, inscricaoLocalEntrega)) {
                for (InscricaoLocalEntregaKit kitEntrega : inscricaoLocalEntrega.getKitsEntrega()) {
                    if (kitEntrega.getInscricaoProgramaKit().getId() == kitEntregaSelecionado.getInscricaoProgramaKit().getId()) {
                        kitEntrega.setQuantidade(kitEntrega.getQuantidade() + kitEntregaSelecionado.getQuantidade());
                    }
                }
            } else {
                kitEntregaSelecionado.setInscricaoLocalEntrega(inscricaoLocalEntrega);
                inscricaoLocalEntrega.getKitsEntrega().add(kitEntregaSelecionado);
            }
        }
    }

    private boolean existeBemNoLocalEntrega(InscricaoLocalEntregaBem bemEntregaSelecionado, InscricaoLocalEntrega inscricaoLocalEntrega) {
        for (InscricaoLocalEntregaBem inscricaoLocalEntregaBem : inscricaoLocalEntrega.getBensEntrega()) {
            if (inscricaoLocalEntregaBem.getInscricaoProgramaBem().getId() == bemEntregaSelecionado.getInscricaoProgramaBem().getId()) {
                return true;
            }
        }
        return false;
    }

    private boolean existeKitNoLocalEntrega(InscricaoLocalEntregaKit kitEntregaSelecionado, InscricaoLocalEntrega inscricaoLocalEntrega) {
        for (InscricaoLocalEntregaKit inscricaoLocalEntregaKit : inscricaoLocalEntrega.getKitsEntrega()) {
            if (inscricaoLocalEntregaKit.getInscricaoProgramaKit().getId() == kitEntregaSelecionado.getInscricaoProgramaKit().getId()) {
                return true;
            }
        }
        return false;
    }

    private InscricaoLocalEntrega buscarInscricaoLocalEntrega(LocalEntregaEntidade localEntregaEntidade) {
        for (InscricaoLocalEntrega inscricaoLocalEntrega : inscricao.getLocaisEntregaInscricao()) {
            if (inscricaoLocalEntrega.getLocalEntregaEntidade().equals(localEntregaEntidade)) {
                return inscricaoLocalEntrega;
            }
        }
        return null;
    }

    /**
     * Subtrai as quantidades utilizadas nos itens selecionados e retorna o
     * objeto com a quantidade atualizada para a lista de itens da inscrição
     * para ser selecionado posteriormente.
     */
    private void subtrairQtdesSelecionadas() {
        for (InscricaoLocalEntregaBem inscricaoLocalEntregaBem : bensEntregaSelecionados) {
            InscricaoProgramaBem inscricaoProgramaBem = inscricaoLocalEntregaBem.getInscricaoProgramaBem();
            int total = inscricaoProgramaBem.getQuantidade() - inscricaoLocalEntregaBem.getQuantidade();
            if (total > 0) {
                inscricaoProgramaBem.setQuantidade(total);
                bensInscricao.add(inscricaoProgramaBem);
            }
        }
        for (InscricaoLocalEntregaKit inscricaoLocalEntregaKit : kitsEntregaSelecionados) {
            InscricaoProgramaKit inscricaoProgramaKit = inscricaoLocalEntregaKit.getInscricaoProgramaKit();
            int total = inscricaoProgramaKit.getQuantidade() - inscricaoLocalEntregaKit.getQuantidade();
            if (total > 0) {
                inscricaoProgramaKit.setQuantidade(total);
                kitsInscricao.add(inscricaoProgramaKit);
            }
        }
    }

    /**
     * Sincroniza bens e kits vinculados ao local de entrega com a lista
     * temporaria que será apresentada na página
     * 
     * @param inscricaoLocalEntrega
     */
    private void sincronizarItensDto() {
        itens.clear();

        for (InscricaoLocalEntrega local : inscricao.getLocaisEntregaInscricao()) {
            for (InscricaoLocalEntregaBem inscricaoLocalEntregaBem : local.getBensEntrega()) {
                InscricaoLocalEntregaItemDto itemDto = new InscricaoLocalEntregaItemDto();

                itemDto.setLocalEntrega(inscricaoLocalEntregaBem.getInscricaoLocalEntrega());
                itemDto.setIdInscricaoProgramaBem(inscricaoLocalEntregaBem.getInscricaoProgramaBem().getId());
                itemDto.setNome(inscricaoLocalEntregaBem.getInscricaoProgramaBem().getProgramaBem().getBem().getNomeBem());
                itemDto.setQuantidade(inscricaoLocalEntregaBem.getQuantidade());
                itemDto.setTipoItem(EnumTipoItemEntrega.BEM);
                itens.add(itemDto);
            }
            for (InscricaoLocalEntregaKit inscricaoLocalEntregaKit : local.getKitsEntrega()) {
                InscricaoLocalEntregaItemDto itemDto = new InscricaoLocalEntregaItemDto();

                itemDto.setLocalEntrega(inscricaoLocalEntregaKit.getInscricaoLocalEntrega());
                itemDto.setIdInscricaoProgramaKit(inscricaoLocalEntregaKit.getInscricaoProgramaKit().getId());
                itemDto.setNome(inscricaoLocalEntregaKit.getInscricaoProgramaKit().getProgramaKit().getKit().getNomeKit());
                itemDto.setQuantidade(inscricaoLocalEntregaKit.getQuantidade());
                itemDto.setTipoItem(EnumTipoItemEntrega.KIT);
                itens.add(itemDto);
            }
        }

        // É necessário ordernar a lista para que funcione o agrupamento pelo
        // endereço na tabela com atributo 'rowspan' na página html
        Collections.sort(itens);
    }

    private InscricaoLocalEntrega criarInscricaoLocalEntrega() {
        InscricaoLocalEntrega localEntrega = new InscricaoLocalEntrega();

        localEntrega.setBensEntrega(new ArrayList<InscricaoLocalEntregaBem>(bensEntregaSelecionados));

        for (InscricaoLocalEntregaBem bemEntrega : localEntrega.getBensEntrega()) {
            bemEntrega.setInscricaoLocalEntrega(localEntrega);
        }

        localEntrega.setKitsEntrega(new ArrayList<InscricaoLocalEntregaKit>(kitsEntregaSelecionados));

        for (InscricaoLocalEntregaKit kitEntrega : localEntrega.getKitsEntrega()) {
            kitEntrega.setInscricaoLocalEntrega(localEntrega);
        }

        localEntrega.setLocalEntregaEntidade(getLocalEntrega());
        return localEntrega;
    }

    // metodo para retornar o titulo da pagina(nome do programa).
    private String getTituloNomePrograma() {
        String titulo = new String();
        if (this.inscricao == null) {
            titulo = "";
        } else {
            titulo = "Programa: ".concat(this.inscricao.getPrograma().getNomePrograma());
        }

        return titulo;
    }

    private boolean validar() {
        boolean valid = true;
        if (localEntregaEntidadeSelecionada == null) {
            valid = false;
            addMsgError("MT018", "Endereço para entrega");
        }

        if (bensEntregaSelecionados.isEmpty() && kitsEntregaSelecionados.isEmpty()) {
            valid = false;
            addMsgError("MT020");
        }

        for (InscricaoLocalEntregaBem entregaBem : bensEntregaSelecionados) {
            if (entregaBem.getQuantidade() != null) {
                if (entregaBem.getQuantidade() <= 0 || entregaBem.getQuantidade() > entregaBem.getInscricaoProgramaBem().getQuantidade()) {
                    valid = false;
                    addMsgError("MT025", "bem", entregaBem.getInscricaoProgramaBem().getProgramaBem().getBem().getNomeBem());
                    break;
                }
            } else {
                addMsgError("MT022", "bem", entregaBem.getInscricaoProgramaBem().getProgramaBem().getBem().getNomeBem());
                valid = false;
                break;
            }
        }

        for (InscricaoLocalEntregaKit entregaKit : kitsEntregaSelecionados) {
            if (entregaKit.getQuantidade() != null) {
                if (entregaKit.getQuantidade() <= 0 || entregaKit.getQuantidade() > entregaKit.getInscricaoProgramaKit().getQuantidade()) {
                    valid = false;
                    addMsgError("MT025", "kit", entregaKit.getInscricaoProgramaKit().getProgramaKit().getKit().getNomeKit());
                    break;
                }
            } else {
                addMsgError("MT022", "kit", entregaKit.getInscricaoProgramaKit().getProgramaKit().getKit().getNomeKit());
                valid = false;
                break;
            }
        }
        return valid;
    }

    private void removerItens(ListItem<InscricaoLocalEntregaItemDto> item, AjaxRequestTarget target) {
        if (bensEntregaSelecionados.isEmpty() && kitsEntregaSelecionados.isEmpty()) {
            InscricaoLocalEntregaItemDto itemDto = item.getModelObject();
            if (EnumTipoItemEntrega.BEM.equals(itemDto.getTipoItem())) {
                removerItemBem(itemDto);
                target.add(containerInscricaoBem);
            } else if (EnumTipoItemEntrega.KIT.equals(itemDto.getTipoItem())) {
                removerItemKit(itemDto);
                target.add(containerInscricaoKit);
            }
            sincronizarItensDto();
            target.add(containerLocaisEntrega);
        } else {
            addMsgError("Para remover um item nenhum bem ou kit pode estar selecionado sem ter sido adicionado.");
        }

    }

    private void removerItemBem(InscricaoLocalEntregaItemDto itemDto) {
        InscricaoLocalEntregaBem inscricaoLocalEntregaBemTemp = null;
        InscricaoLocalEntrega inscricaoLocalEntregaTemp = null;
        Long idInscricaoProgramaBem = itemDto.getIdInscricaoProgramaBem();

        // Busca objetos a serem removidos
        for (InscricaoLocalEntrega InscricaoLocalEntrega : inscricao.getLocaisEntregaInscricao()) {
            for (InscricaoLocalEntregaBem inscricaoLocalEntregaBem : InscricaoLocalEntrega.getBensEntrega()) {
                if (inscricaoLocalEntregaBem.getInscricaoProgramaBem().getId() == idInscricaoProgramaBem && itemDto.getLocalEntrega().getLocalEntregaEntidade().getId() == inscricaoLocalEntregaBem.getInscricaoLocalEntrega().getLocalEntregaEntidade().getId()) {
                    inscricaoLocalEntregaTemp = InscricaoLocalEntrega;
                    inscricaoLocalEntregaBemTemp = inscricaoLocalEntregaBem;
                }
            }
        }
        removerItem(inscricaoLocalEntregaBemTemp, inscricaoLocalEntregaTemp, itemDto);

    }

    private void removerItem(InscricaoLocalEntregaBem inscricaoLocalEntregaBemTemp, InscricaoLocalEntrega inscricaoLocalEntregaTemp, InscricaoLocalEntregaItemDto itemDto) {
        if (inscricaoLocalEntregaBemTemp != null && inscricaoLocalEntregaTemp != null) {
            retornarQuantidadeUtilizada(inscricaoLocalEntregaBemTemp);
            if (inscricao.getLocaisEntregaInscricao().contains(inscricaoLocalEntregaTemp)) {
                inscricaoLocalEntregaTemp.getBensEntrega().remove(inscricaoLocalEntregaBemTemp);
                // Remove inscricaoLocalEntrega caso as lista esteja vazia, não
                // faz sentido existir local de entrega sem itens
                if (inscricaoLocalEntregaTemp.getBensEntrega().isEmpty() && inscricaoLocalEntregaTemp.getKitsEntrega().isEmpty()) {
                    inscricao.getLocaisEntregaInscricao().remove(inscricaoLocalEntregaTemp);
                }
            }
        }
    }

    private void retornarQuantidadeUtilizada(InscricaoLocalEntregaBem inscricaoLocalEntregaBem) {
        if (bensInscricao.contains(inscricaoLocalEntregaBem.getInscricaoProgramaBem())) {
            for (InscricaoProgramaBem bi : bensInscricao) {
                if (bi.getId() == inscricaoLocalEntregaBem.getInscricaoProgramaBem().getId()) {
                    int total = bi.getQuantidade() + inscricaoLocalEntregaBem.getQuantidade();
                    bi.setQuantidade(total);
                }
            }
        } else {
            inscricaoLocalEntregaBem.getInscricaoProgramaBem().setQuantidade(inscricaoLocalEntregaBem.getQuantidade());
            adicionarInscricaoProgramaBem(inscricaoLocalEntregaBem.getInscricaoProgramaBem());
        }
    }

    private void retornarQuantidadeUtilizada(InscricaoLocalEntregaKit inscricaoLocalEntregaKit) {
        if (kitsInscricao.contains(inscricaoLocalEntregaKit.getInscricaoProgramaKit())) {
            for (InscricaoProgramaKit ki : kitsInscricao) {
                if (ki.getId() == inscricaoLocalEntregaKit.getInscricaoProgramaKit().getId()) {
                    int total = ki.getQuantidade() + inscricaoLocalEntregaKit.getQuantidade();
                    ki.setQuantidade(total);
                }
            }
        } else {
            inscricaoLocalEntregaKit.getInscricaoProgramaKit().setQuantidade(inscricaoLocalEntregaKit.getQuantidade());
            adicionarInscricaoProgramaKit(inscricaoLocalEntregaKit.getInscricaoProgramaKit());
        }
    }

    private void removerItemKit(InscricaoLocalEntregaItemDto itemDto) {
        InscricaoLocalEntregaKit inscricaoLocalEntregaKitRemover = null;
        InscricaoLocalEntrega inscricaoLocalEntregaRemover = null;
        Long idInscricaoProgramaKit = itemDto.getIdInscricaoProgramaKit();

        // Busca objetos a serem removidos
        for (InscricaoLocalEntrega InscricaoLocalEntrega : inscricao.getLocaisEntregaInscricao()) {
            for (InscricaoLocalEntregaKit inscricaoLocalEntregaKit : InscricaoLocalEntrega.getKitsEntrega()) {
                if (inscricaoLocalEntregaKit.getInscricaoProgramaKit().getId() == idInscricaoProgramaKit && itemDto.getLocalEntrega().getLocalEntregaEntidade().getId() == inscricaoLocalEntregaKit.getInscricaoLocalEntrega().getLocalEntregaEntidade().getId()) {
                    inscricaoLocalEntregaRemover = InscricaoLocalEntrega;
                    inscricaoLocalEntregaKitRemover = inscricaoLocalEntregaKit;
                }
            }
        }
        removerItem(inscricaoLocalEntregaKitRemover, inscricaoLocalEntregaRemover, itemDto);

    }

    private void removerItem(InscricaoLocalEntregaKit inscricaoLocalEntregaKitRemover, InscricaoLocalEntrega inscricaoLocalEntregaRemover, InscricaoLocalEntregaItemDto itemDto) {
        if (inscricaoLocalEntregaKitRemover != null && inscricaoLocalEntregaRemover != null) {
            retornarQuantidadeUtilizada(inscricaoLocalEntregaKitRemover);
            if (inscricao.getLocaisEntregaInscricao().contains(inscricaoLocalEntregaRemover)) {
                inscricaoLocalEntregaRemover.getKitsEntrega().remove(inscricaoLocalEntregaKitRemover);
                // Remove inscricaoLocalEntrega caso as lista estejam vazias,
                // não faz sentido existir local de entrega sem itens
                if (inscricaoLocalEntregaRemover.getBensEntrega().isEmpty() && inscricaoLocalEntregaRemover.getKitsEntrega().isEmpty()) {
                    inscricao.getLocaisEntregaInscricao().remove(inscricaoLocalEntregaRemover);
                }
            }
        }
    }

    private Modal<String> newModal(String id) {
        Modal<String> modal = new TextContentModal(id, new LambdaModel<String>(this::getMsgConfirmacao, this::setMsgConfirmacao));
        modal.setBackdrop(Backdrop.STATIC);
        modal.addButton(newButtonCorfirmar(modal));
        modal.addButton(newButtonCancelar(modal));
        return modal;
    }

    private AjaxDialogButton newButtonCorfirmar(Modal<String> modal) {
        return new AjaxDialogButton(Model.of("Confirmar"), Buttons.Type.Primary) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                modal.show(false);
                modal.close(target);
                setResponsePage(LocaisEntregaPage.class);
            }
        };
    }

    private AjaxDialogButton newButtonCancelar(Modal<String> modal) {
        return new AjaxDialogButton(Model.of("Cancelar"), Buttons.Type.Danger) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                modal.show(false);
                modal.close(target);
            }
        };

    }

    public String getMsgConfirmacao() {
        return msgConfirmacao;
    }

    public void setMsgConfirmacao(String msgConfirmacao) {
        this.msgConfirmacao = msgConfirmacao;
    }
}
