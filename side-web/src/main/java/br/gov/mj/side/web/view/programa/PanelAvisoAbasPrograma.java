package br.gov.mj.side.web.view.programa;

import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

import br.gov.mj.apoio.entidades.Funcao;
import br.gov.mj.apoio.entidades.Orgao;
import br.gov.mj.apoio.entidades.SubFuncao;
import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.ProgramaBem;
import br.gov.mj.side.entidades.programa.ProgramaKit;
import br.gov.mj.side.entidades.programa.ProgramaPotencialBeneficiarioUf;

public class PanelAvisoAbasPrograma extends Panel {
    private static final long serialVersionUID = 1L;

    private PanelPrincipal panelPrincipal;
    
    private Label lblErro;
    private Label lblOk;
    
    private Boolean abaClicada = false;
    private Boolean mostrarErro = false;
    private Boolean mostrarOk = false;
    private Boolean abaValida = false;
    
    private String mensagem = "";
    private Programa programa;
    private String abaAtual = "";

    @Inject
    private ComponentFactory componentFactory;

    public PanelAvisoAbasPrograma(String id, Programa programa, Boolean abaClicada) {
        super(id);
        setOutputMarkupId(true);
        this.abaAtual = id;
        this.abaClicada = abaClicada;
        this.programa = programa;
        
        validarPrimeiraAba();

        add(panelPrincipal = new PanelPrincipal("panelPrincipal"));
    }

    public class PanelPrincipal extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelPrincipal(String id) {
            super(id);
            setOutputMarkupId(true);
            add(new PanelOk("panelOk"));
            add(new PanelErro("panelErro"));
            
        }
    }
    
    public class PanelOk extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelOk(String id) {
            super(id);
            setOutputMarkupId(true);
            setVisible(mostrarOk);
        }
    }
    
    public class PanelErro extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelErro(String id) {
            super(id);
            setOutputMarkupId(true);
            setVisible(mostrarErro);
        }
    }

    // ações
    
    public String mensagemRetornar(){
        if(abaClicada && mostrarErro){
            return "<i class='glyphicon glyphicon-equalizer' />";
        }else{
            if(abaClicada && mostrarOk){
                return "<i class='glyphicon glyphicon-pencil' />";
            }
        }
        
        return "";
    }
    
    private void validarPrimeiraAba(){
        if(abaClicada && "img-infoGerais".equals(abaAtual)){
            if (!validarFinalizar(programa)) {
                mostrarErro = true;
                mostrarOk = false;
            }else{
                mostrarErro = false;
                mostrarOk = true;
            }
        }
    }

    public boolean validarFinalizar(Programa programa) {
        boolean validar = true;

        validar = validarNomePrograma(programa, validar);
        validar = validarNomeFantasia(programa, validar);
        validar = validarDescricao(programa, validar);
        validar = validarAnoPrograma(programa, validar);
        /*validar = validarFuncao(validar);
        validar = validarSubFuncao(validar);
        validar = validarRecursoFinanceiro(programa, validar);
        validar = validarValorMaximoProposta(programa, validar);
        validar = validarOrgao(validar);
        validar = validarUnidadeExecutora(programa, validar);
        validar = validarLimitacaoGeografica(programa, validar);
        validar = validarPotenciaisBeneficiarios(programa, validar);
        validar = validarNomePersonalidadeJuridica(programa, validar);
        validar = validarBensKits(programa, validar);
        validar = validarCriterioElegibilidade(programa, validar);
        validar = validarCriterioAcompanhamento(programa, validar);
        validar = validarNumeroProcessoSei(programa, validar);
        validar = validarCriterioAvaliacao(programa, validar);*/

        return validar;
    }

    public boolean validarNomePrograma(Programa programa, boolean valor) {
        boolean validar = valor;

        if (programa.getNomePrograma() == null || "".equalsIgnoreCase(programa.getNomePrograma())) {
            mensagem="O campo 'Nome do Programa' é obrigatório.";
            validar = false;
        }

        return validar;
    }

    public boolean validarNomeFantasia(Programa programa, boolean valor) {
        boolean validar = valor;

        if (programa.getNomeFantasiaPrograma() == null || "".equalsIgnoreCase(programa.getNomeFantasiaPrograma())) {
            mensagem="O campo 'Nome Fantasia' é obrigatório.";
            validar = false;
        }

        return validar;
    }

    public boolean validarDescricao(Programa programa, boolean valor) {
        boolean validar = valor;

        if (programa.getDescricaoPrograma() == null || "".equalsIgnoreCase(programa.getDescricaoPrograma())) {
            mensagem="O campo 'Descrição' é obrigatório.";
            validar = false;
        }

        return validar;
    }

    public boolean validarAnoPrograma(Programa programa, boolean valor) {
        boolean validar = valor;
        if (programa.getAnoPrograma() == null || programa.getAnoPrograma() == 0) {
            mensagem="O campo 'Ano' é obrigatório.";
            validar = false;
        } else {
           boolean correto = programa.getAnoPrograma().toString().length() == 4;
            if (!correto) {
                mensagem="'Ano' não é um valor válido";
                validar = false;
            }
        }
        return validar;
    }

    public Boolean getAbaValida() {
        return abaValida;
    }

    public void setAbaValida(Boolean abaValida) {
        this.abaValida = abaValida;
    }

    /*public boolean validarFuncao(boolean valor) {
        boolean validar = valor;

        Funcao func = panelInformacoesGerais.getFuncao();
        if (func == null || func.getId() == null) {
            addMsgError("O campo 'Função' é obrigatório.");
            validar = false;
        }

        return validar;
    }

    public boolean validarSubFuncao(boolean valor) {
        boolean validar = valor;

        SubFuncao sub = panelInformacoesGerais.getSubFuncao();
        if (sub == null || sub.getId() == null) {
            addMsgError("O campo 'Subfunção' é obrigatório.");
            validar = false;
        }

        return validar;
    }

    public boolean validarRecursoFinanceiro(Programa programa, boolean valor) {
        boolean validar = valor;

        if (programa.getRecursosFinanceiros() == null || programa.getRecursosFinanceiros().isEmpty()) {
            addMsgError("Vincule ao menos 1 Recurso Financeiro.");
            validar = false;
        }
        return validar;
    }

    private boolean validarValorMaximoProposta(Programa programa, boolean valor) {
        boolean validar = valor;

        if (programa.getValorMaximoProposta() == null || programa.getValorMaximoProposta().intValue() == 0) {
            addMsgError("O Valor Máximo por Proposta não pode ser '0'.");
            return false;
        }

        if (programa.getValorMaximoProposta() != null && recursoFinanceiroPanel.getValorTotal() != null) {
            if (programa.getValorMaximoProposta().doubleValue() > recursoFinanceiroPanel.getValorTotal().doubleValue()) {
                addMsgError("O Valor Máximo por Proposta não pode ser maior do que o Valor Total do Recurso Financeiro.");
                validar = false;
            }
        }
        return validar;
    }

    public boolean validarCriterioAvaliacao(Programa programa, boolean valor) {
        boolean validar = valor;

        if (programa.getCriteriosAvaliacao() == null || programa.getCriteriosAvaliacao().isEmpty()) {
            addMsgError("Vincule ao menos 1 Critério de Avaliação.");
            validar = false;
        }

        return validar;
    }

    public boolean validarOrgao(boolean valor) {
        boolean validar = valor;

        Orgao org = panelInformacoesGerais.getOrgao();
        if (org == null || org.getId() == null) {
            addMsgError("O campo 'Órgão (Cód. SIORG)' é obrigatório.");
            validar = false;
        }

        return validar;
    }

    public boolean validarUnidadeExecutora(Programa programa, boolean valor) {
        boolean validar = valor;

        if (programa.getUnidadeExecutora() == null || programa.getUnidadeExecutora().getId() == null) {
            addMsgError("O campo 'Unidade Executora' é obrigatório.");
            validar = false;
        }

        return validar;
    }

    public boolean validarLimitacaoGeografica(Programa programa, boolean valor) {
        boolean validar = valor;

        if (programa.getPossuiLimitacaoGeografica() == null) {
            addMsgError("O campo 'Limitação Geográfica' é obrigatório");
            validar = false;
        }

        return validar;
    }

    public boolean validarPotenciaisBeneficiarios(Programa programa, boolean valor) {
        boolean validar = valor;

        if (programa.getPossuiLimitacaoGeografica() != null && programa.getPossuiLimitacaoGeografica()) {
            if (programa.getPotenciaisBeneficiariosUf() == null || programa.getPotenciaisBeneficiariosUf().isEmpty()) {
                addMsgError("É necessário a escolha de pelo menos uma UF'");
                validar = false;
            }

        }

        if (programa.getPossuiLimitacaoMunicipalEspecifica() != null && programa.getPossuiLimitacaoMunicipalEspecifica()) {
            if (programa.getPotenciaisBeneficiariosUf() != null || !programa.getPotenciaisBeneficiariosUf().isEmpty()) {
                int i = 0;
                for (ProgramaPotencialBeneficiarioUf p : programa.getPotenciaisBeneficiariosUf()) {
                    if (p.getPotencialBeneficiarioMunicipios() != null && !p.getPotencialBeneficiarioMunicipios().isEmpty()) {
                        i++;
                    }
                }

                if (i == 0) {
                    addMsgError("É necessário a escolha de pelo menos um Município'");
                    validar = false;
                }
            }

        }

        return validar;
    }

    public boolean validarNomePersonalidadeJuridica(Programa programa, boolean valor) {
        boolean validar = valor;

        if (programa.getTipoPersonalidadeJuridica() == null) {
            addMsgError("O campo 'Personalidade Jurídica' é obrigatório.");
            validar = false;
        }

        return validar;
    }

    public boolean validarBensKits(Programa programa, boolean valor) {
        boolean validar = valor;

        if ((programa.getProgramaKits() == null || programa.getProgramaKits().isEmpty()) && (programa.getProgramaBens() == null || programa.getProgramaBens().isEmpty())) {
            addMsgError("Adicione ao menos 1 Bem ou 1 Kit.");
            validar = false;
        } else {
            if (programa.getProgramaKits() == null || !programa.getProgramaKits().isEmpty()) {
                List<ProgramaKit> listaKit = programa.getProgramaKits();
                int flag = 0;
                int qtdMaximaMaior = 0;
                for (ProgramaKit kit : listaKit) {
                    if (kit.getQuantidade() == null || kit.getQuantidade() < 1) {
                        flag++;
                    }

                    if (kit.getQuantidadePorProposta() == null) {
                        kit.setQuantidadePorProposta(0);
                    }

                    if (kit.getQuantidadePorProposta() != null && (kit.getQuantidadePorProposta() > kit.getQuantidade())) {
                        qtdMaximaMaior++;
                    }
                }

                if (flag > 0) {
                    addMsgError("Existe Kit adicionado a lista com a QUANTIDADE TOTAL '0', informe um valor para este campo.");
                    validar = false;
                } else {
                    if (qtdMaximaMaior > 0) {
                        addMsgError("Existe Kit com a QUANTIDADE MÁXIMA POR PROPOSTA maior do que a QUANTIDADE TOTAL.");
                        validar = false;
                    }
                }
            }

            if (programa.getProgramaBens() == null || !programa.getProgramaBens().isEmpty()) {
                List<ProgramaBem> listaBem = programa.getProgramaBens();
                int flag = 0;
                int qtdMaximaMaior = 0;
                for (ProgramaBem bem : listaBem) {
                    if (bem.getQuantidade() == null || bem.getQuantidade() < 1) {
                        flag++;
                    }

                    if (bem.getQuantidadePorProposta() == null) {
                        bem.setQuantidadePorProposta(0);
                    }

                    if (bem.getQuantidadePorProposta() != null && (bem.getQuantidadePorProposta() > bem.getQuantidade())) {
                        qtdMaximaMaior++;
                    }
                }

                if (flag > 0) {
                    addMsgError("Existe Bem adicionado a lista com a QUANTIDADE TOTAL '0', informe um valor para este campo.");
                    validar = false;
                } else {

                    if (qtdMaximaMaior > 0) {
                        addMsgError("Existe Bem com a QUANTIDADE MÁXIMA POR PROPOSTA maior do que a QUANTIDADE TOTAL.");
                        validar = false;
                    }
                }
            }
        }

        return validar;
    }

    public boolean validarNumeroProcessoSei(Programa programa, boolean valor) {
        boolean validar = valor;

        if (programa.getNumeroProcessoSEI() == null || "".equalsIgnoreCase(programa.getNumeroProcessoSEI())) {
            addMsgError("O campo 'Número do Processo SEI' é obrigatório.");
            validar = false;
        } else {
            if (programa.getNumeroProcessoSEI().length() < 17) {
                addMsgError("O campo 'Número do Processo SEI' deverá conter 17 caracteres númericos.");
                validar = false;
            }
        }

        return validar;
    }

    public boolean validarCriterioElegibilidade(Programa programa, boolean valor) {
        boolean validar = valor;

        if (programa.getCriteriosElegibilidade() == null || programa.getCriteriosElegibilidade().isEmpty()) {
            addMsgError("Adicione ao menos 1 Critério de Elegibilidade e de Avaliação de Propostas.");
            validar = false;
        }

        return validar;
    }

    public boolean validarCriterioAcompanhamento(Programa programa, boolean valor) {
        boolean validar = valor;

        if (programa.getCriteriosAcompanhamento() == null || programa.getCriteriosAcompanhamento().isEmpty()) {
            addMsgError("Adicione ao menos 1 Critério de Acompanhamento.");
            validar = false;
        }

        return validar;
    }*/
}
