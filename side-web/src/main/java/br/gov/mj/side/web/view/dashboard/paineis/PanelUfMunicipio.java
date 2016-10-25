package br.gov.mj.side.web.view.dashboard.paineis;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.PropertyModel;

import br.gov.mj.apoio.entidades.Municipio;
import br.gov.mj.apoio.entidades.Uf;
import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraDropDownChoice;
import br.gov.mj.side.web.service.MunicipioService;
import br.gov.mj.side.web.service.UfService;

public class PanelUfMunicipio<T> extends WebMarkupContainer {
    private static final long serialVersionUID = 1L;

    // ##################################################################################
    // constantes

    // ##################################################################################
    // variaveis
    private Uf ufSelecionada = new Uf();
    private T entidade;
    private String idExpression;
    private Boolean readOnly;

    // variaveis de componentes
    private AttributeAppender classDropDown = new AttributeAppender("class", "basic-single", " ");
    private PainelPrincipal painelPrincipal;

    // injeção de dependencias
    @Inject
    private UfService ufService;
    @Inject
    private MunicipioService municipioService;
    @Inject
    private ComponentFactory componentFactory;

    // ##################################################################################
    // Construtores, Inits & destroyers

    /**
     * 
     * @param id
     * @param entidadeAtual
     * @param components
     *            - permite acrescenta novos componentes em tempo de
     *            instanciação sem a nescessidade de extender o painel. No
     *            entanto deve-se levar em consideração que o componente será
     *            acescentado sempre dentro do PanelDadosEntidades e sempre ao
     *            final. Lembrando-se que é preciso acrescentar o compoente ao
     *            html também
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws InvocationTargetException
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    public PanelUfMunicipio(String id, T entidade, String idExpression) throws NoSuchMethodException, SecurityException, NoSuchFieldException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        super(id);
        this.entidade = entidade;
        this.idExpression = idExpression;

        if (entidade != null && idExpression != null && entidade.getClass().getDeclaredField(idExpression) != null) {
            Method method = entidade.getClass().getDeclaredMethod("get" + idExpression.substring(0, 1).toUpperCase().concat(idExpression.substring(1)));
            Municipio m = (Municipio) method.invoke(entidade);
            if (m != null) {
                ufSelecionada = m.getUf();
            }
        }

        add(newPainelPrincipal());
        setOutputMarkupId(true);

    }

    // ##################################################################################
    // apaineis

    private class PainelPrincipal extends WebMarkupContainer {

        public PainelPrincipal() {
            super("painelPrincipal");
            add(getDropDownUf()); // nomeUf
            add(getDropDownCidade()); // nomeMunicipio
        }

    }

    // ##################################################################################
    // componentes

    public DropDownChoice<Uf> getDropDownUf() {

        List<Uf> lista = ufService.buscarTodos();

        InfraDropDownChoice<Uf> drop = componentFactory.newDropDownChoice("nomeUf", "Estado", false, "id", "nomeUf", new PropertyModel(this, "ufSelecionada"), lista, null);
        drop.setChoiceRenderer(new ChoiceRenderer<Uf>("nomeSigla", "id"));
        actionDropDownUf(drop);
        drop.setNullValid(true);
        drop.add(classDropDown);
        drop.setOutputMarkupId(true);

        return drop;
    }

    public DropDownChoice<Municipio> getDropDownCidade() {

        List<Municipio> lista = new ArrayList<Municipio>();
        if (ufSelecionada != null && ufSelecionada.getId() != null) {
            lista = municipioService.buscarPelaUfId(ufSelecionada.getId());
        }

        InfraDropDownChoice<Municipio> drop = componentFactory.newDropDownChoice("municipio", "Município", false, "id", "nomeMunicipio", new PropertyModel(entidade, idExpression), lista, null);
        actionDropDownMunicipio(drop);
        drop.setNullValid(true);
        drop.setOutputMarkupId(true);
        drop.add(classDropDown);

        return drop;
    }

    public void actionDropDownUf(DropDownChoice<Uf> drop) {
        drop.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                painelPrincipal.addOrReplace(getDropDownCidade());
                target.appendJavaScript("atualizaCssDropDown();");
                target.add(painelPrincipal);
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

    private PainelPrincipal newPainelPrincipal() {
        painelPrincipal = new PainelPrincipal();
        return painelPrincipal;
    }
    // ##################################################################################
    // comportamentos

    public Uf getUfSelecionada() {
        return ufSelecionada;
    }

    public void setUfSelecionada(Uf ufSelecionada) {
        this.ufSelecionada = ufSelecionada;
    }

    public PainelPrincipal getPainelPrincipal() {
        return painelPrincipal;
    }

    public void setPainelPrincipal(PainelPrincipal painelPrincipal) {
        this.painelPrincipal = painelPrincipal;
    }

    // ##################################################################################
    // metodos privados

}
