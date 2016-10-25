package br.gov.mj.side.web.view.dashboard.paineis;

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

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraDropDownChoice;
import br.gov.mj.apoio.entidades.Municipio;
import br.gov.mj.apoio.entidades.TipoEndereco;
import br.gov.mj.apoio.entidades.TipoEntidade;
import br.gov.mj.apoio.entidades.Uf;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.enums.EnumPersonalidadeJuridica;
import br.gov.mj.side.entidades.enums.EnumStatusEntidade;
import br.gov.mj.side.web.service.MunicipioService;
import br.gov.mj.side.web.service.TipoEnderecoService;
import br.gov.mj.side.web.service.TipoEntidadeService;
import br.gov.mj.side.web.service.UfService;

public class PanelDadosEntidadeDashboard extends Panel {
    private static final long serialVersionUID = 1L;

    private static final String ONCHANGE = "onchange";

    private Entidade entidadeAtual;

    private PanelPrincipalEntidade panelPrincipalEntidade;
    private PanelUfMunicipio panelUfMunicipio;

    private AttributeAppender classDropDown = new AttributeAppender("class", "js-example-basic-single", " ");

    private Uf nomeUf = new Uf();

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

    public PanelDadosEntidadeDashboard(String id, Entidade entidadeAtual) {
        super(id);
        this.entidadeAtual=entidadeAtual;
        initVariaveis();
        setOutputMarkupId(true);

        add(panelPrincipalEntidade = new PanelPrincipalEntidade("panelPrincipalEntidade"));
    }

    private void initVariaveis() {
            nomeUf = entidadeAtual.getMunicipio().getUf();
    }

    private class PanelPrincipalEntidade extends WebMarkupContainer {

        private static final long serialVersionUID = 1L;

        public PanelPrincipalEntidade(String id) {
            super(id);
            setOutputMarkupId(true);

            add(getTextFieldCnpj());
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
            add(getTextFieldNumeroProcesso()); // processo
        }
    }

    private class PanelUfMunicipio extends WebMarkupContainer {
        public PanelUfMunicipio(String id) {
            super(id);
            setOutputMarkupId(true);
            add(getDropDownUf()); // nomeUf
            add(getDropDownCidade()); // nomeMunicipio
        }
    }

    // COMPONENTES
    
    public TextField<String> getTextFieldCnpj() {
        TextField<String> fieldCnpj = componentFactory.newTextField("numeroCnpj", "CNPJ", false, null);
        fieldCnpj.add(StringValidator.maximumLength(18));
        actionTextField(fieldCnpj);
        fieldCnpj.setOutputMarkupId(true);
        fieldCnpj.setEnabled(false);
        return fieldCnpj;
    }

    public DropDownChoice<TipoEntidade> getDropDownTipo() {
        List<TipoEntidade> lista = new ArrayList<TipoEntidade>();
        lista = tipoEntidadeService.buscarTodos();

        InfraDropDownChoice<TipoEntidade> drop = componentFactory.newDropDownChoice("tipoEntidade", "Tipo", false, "id", "descricaoTipoEntidade", null, lista, null);
        actionDropDownTipoEntidade(drop);
        drop.setNullValid(true);
        drop.setOutputMarkupId(true);

        return drop;
    }

    public TextField<String> getTextFieldNome() {
        TextField<String> field = componentFactory.newTextField("nomeEntidade", "Nome (Entidade)", false, null);
        field.add(StringValidator.maximumLength(200));
        actionTextField(field);
        return field;
    }

    public DropDownChoice<EnumPersonalidadeJuridica> getDropDownNaturezaJuridica() {
        List<EnumPersonalidadeJuridica> listaTemp = Arrays.asList(EnumPersonalidadeJuridica.values());
        List<EnumPersonalidadeJuridica> lista = new ArrayList<EnumPersonalidadeJuridica>();
        for (EnumPersonalidadeJuridica temp : listaTemp) {
            if (!"T".equalsIgnoreCase(temp.getValor())) {
                lista.add(temp);
            }
        }

        InfraDropDownChoice<EnumPersonalidadeJuridica> drop = componentFactory.newDropDownChoice("personalidadeJuridica", "Natureza Jurídica", false, "valor", "descricao", null, lista, null);
        drop.setChoiceRenderer(criarRendererNaturezaJuridica());
        actionDropDownTipoNaturezaJuridica(drop);
        drop.setNullValid(true);
        drop.setOutputMarkupId(true);

        return drop;
    }

    private TextField<String> getTextFieldEndereco() {
        TextField<String> field = componentFactory.newTextField("descricaoEndereco", "Endereço", false, null);
        field.add(StringValidator.maximumLength(200));
        actionTextField(field);
        return field;
    }

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
        }

        InfraDropDownChoice<Municipio> drop = componentFactory.newDropDownChoice("municipio", "Municipio", false, "id", "nomeMunicipio", null, lista, null);
        actionDropDownMunicipio(drop);
        drop.setNullValid(true);
        drop.setOutputMarkupId(true);
        drop.add(classDropDown);

        return drop;
    }

    public DropDownChoice<TipoEndereco> getDropDownTipoEndereco() {
        List<TipoEndereco> lista = new ArrayList<TipoEndereco>();
        lista = tipoEnderecoService.buscarTodos();

        InfraDropDownChoice<TipoEndereco> drop = componentFactory.newDropDownChoice("tipoEndereco", "Tipo de Endereço", false, "id", "descricaoTipoEndereco", null, lista, null);
        actionDropDownTipoEndereco(drop);
        drop.setNullValid(true);
        drop.setOutputMarkupId(true);

        return drop;
    }

    private TextField<String> getTextFieldNumero() {
        TextField<String> field = componentFactory.newTextField("numeroEndereco", "Número", false, null);
        field.add(StringValidator.maximumLength(200));
        actionTextField(field);
        return field;
    }

    private TextField<String> getTextFieldComplemento() {
        TextField<String> field = componentFactory.newTextField("complementoEndereco", "Complemento", false, null);
        field.add(StringValidator.maximumLength(200));
        actionTextField(field);
        return field;
    }

    private TextField<String> getTextFieldBairro() {
        TextField<String> field = componentFactory.newTextField("bairro", "Bairro", false, null);
        field.add(StringValidator.maximumLength(200));
        actionTextField(field);
        return field;
    }

    private TextField<String> getTextFieldCep() {
        TextField<String> field = componentFactory.newTextField("numeroCep", "CEP", false, null);
        field.add(StringValidator.maximumLength(9));
        actionTextField(field);
        return field;
    }

    private TextField<String> getTextFieldTelefone() {
        TextField<String> field = componentFactory.newTextField("numeroTelefone", "Telefone (Entidade)", false, null);
        field.add(StringValidator.maximumLength(13));
        actionTextField(field);
        return field;
    }

    private TextField<String> getTextFieldTelefoneFax() {
        TextField<String> field = componentFactory.newTextField("numeroFoneFax", "Telefone/FAX", false, null);
        field.add(StringValidator.maximumLength(13));
        actionTextField(field);
        return field;
    }

    private TextField<String> getTextFieldEmail() {
        TextField<String> field = componentFactory.newTextField("email", "E-mail (Entidade)", false, null);
        field.add(StringValidator.maximumLength(200));
        actionTextField(field);
        return field;
    }

    private TextField<String> getTextFieldNumeroProcesso() {
        TextField<String> field = componentFactory.newTextField("numeroProcessoSEI", "Nº Processo (NUP)", false, null);
        field.add(StringValidator.maximumLength(20));
        actionTextField(field);
        return field;
    }

    private InfraDropDownChoice<EnumStatusEntidade> getDropDownStatus() {
        List<EnumStatusEntidade> lista = Arrays.asList(EnumStatusEntidade.values());

        InfraDropDownChoice<EnumStatusEntidade> dropDownChoice = componentFactory.newDropDownChoice("statusEntidade", "Ativar Entidade", false, "valor", "descricao", null, lista, null);
        dropDownChoice.setNullValid(true);
        return dropDownChoice;
    }

    private Label getLabelAtivar() {
        Label labelAtivar = new Label("lblAtivar", "Ativar Entidade");
        return labelAtivar;
    }

    // AÇÕES

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

    private void actionTextField(TextField<String> field) {
        AjaxFormComponentUpdatingBehavior onChangeAjaxBehavior = new AjaxFormComponentUpdatingBehavior(ONCHANGE) {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                // setando no Model
            }
        };
        field.add(onChangeAjaxBehavior);
    }

    public void actionDropDownTipoEntidade(DropDownChoice<TipoEntidade> drop) {
        drop.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {

                // setando no Model
            }
        });
    }

    public void actionDropDownTipoEndereco(DropDownChoice<TipoEndereco> drop) {
        drop.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {

                // setando no Model
            }
        });
    }

    public void actionDropDownTipoNaturezaJuridica(DropDownChoice<EnumPersonalidadeJuridica> drop) {
        drop.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                // setando no Model
            }
        });
    }

    public void actionDropDownUf(DropDownChoice<Uf> drop) {
        drop.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {

                panelUfMunicipio.addOrReplace(getDropDownCidade());
                target.add(panelUfMunicipio);
            }
        });
    }

    public void actionDropDownMunicipio(DropDownChoice<Municipio> drop) {
        drop.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {

                // setando no Model
            }
        });
    }

    private String limparCnpj(String valor) {
        String value = valor;
        value = value.replace(".", "");
        value = value.replace("/", "");
        value = value.replace("-", "");
        return value;
    }

    public Uf getNomeUf() {
        return nomeUf;
    }

    public void setNomeUf(Uf nomeUf) {
        this.nomeUf = nomeUf;
    }

}
