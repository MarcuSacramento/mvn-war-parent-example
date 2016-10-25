package br.gov.mj.side.web.dto.enderecoLicitacao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.programa.licitacao.contrato.ObjetoFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.TermoDoacao;

public class DoacoesConcluidasDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Entidade entidade;
    private String numeroProcessoSei;
    private List<ObjetoFornecimentoContrato> listaItensEntregues = new ArrayList<ObjetoFornecimentoContrato>();
    private List<ObjetoFornecimentoContrato> listaItensDoados = new ArrayList<ObjetoFornecimentoContrato>();
    private Integer quantidadeEntregue;
    private Integer quantidadeDoado;
    
    public Entidade getEntidade() {
        return entidade;
    }
    
    public void setEntidade(Entidade entidade) {
        this.entidade = entidade;
    }
    public List<ObjetoFornecimentoContrato> getListaItensEntregues() {
        return listaItensEntregues;
    }
    public void setListaItensEntregues(List<ObjetoFornecimentoContrato> listaItensEntregues) {
        this.listaItensEntregues = listaItensEntregues;
    }
    public List<ObjetoFornecimentoContrato> getListaItensDoados() {
        return listaItensDoados;
    }
    public void setListaItensDoados(List<ObjetoFornecimentoContrato> listaItensDoados) {
        this.listaItensDoados = listaItensDoados;
    }

    public Integer getQuantidadeEntregue() {
        return quantidadeEntregue;
    }

    public void setQuantidadeEntregue(Integer quantidadeEntregue) {
        this.quantidadeEntregue = quantidadeEntregue;
    }

    public Integer getQuantidadeDoado() {
        return quantidadeDoado;
    }

    public void setQuantidadeDoado(Integer quantidadeDoado) {
        this.quantidadeDoado = quantidadeDoado;
    }

    public String getNumeroProcessoSei() {
        return numeroProcessoSei;
    }

    public void setNumeroProcessoSei(String numeroProcessoSei) {
        this.numeroProcessoSei = numeroProcessoSei;
    }
    
    public static Comparator<DoacoesConcluidasDto> getComparator(int order, String coluna) {
        return new Comparator<DoacoesConcluidasDto>() {
            @Override
            public int compare(DoacoesConcluidasDto o1, DoacoesConcluidasDto o2) {
                int valor = 0;

                if ("numeroProcessoSei".equalsIgnoreCase(coluna)) {
                    valor = o1.getNumeroProcessoSei().toUpperCase().compareTo(o2.getNumeroProcessoSei().toUpperCase()) * order;
                }
                if ("nomeBeneficiario".equalsIgnoreCase(coluna)) {
                    if(o1.getEntidade() != null && o2.getEntidade()!=null){
                        valor = o1.getEntidade().getNomeEntidade().toUpperCase().compareTo(o2.getEntidade().getNomeEntidade().toUpperCase()) * order;
                    }
                }
                return valor;
            }
        };
    }
}
