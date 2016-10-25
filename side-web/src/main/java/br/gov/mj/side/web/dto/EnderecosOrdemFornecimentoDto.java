package br.gov.mj.side.web.dto;

import java.io.Serializable;
import java.util.Comparator;

import br.gov.mj.apoio.entidades.Uf;
import br.gov.mj.side.entidades.Bem;
import br.gov.mj.side.entidades.entidade.LocalEntregaEntidade;
import br.gov.mj.side.entidades.programa.licitacao.contrato.ItensOrdemFornecimentoContrato;

public class EnderecosOrdemFornecimentoDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Bem bem;
    private Uf uf;
    private String endereco;
    private Long quantidade;
    private Integer quatidadeRestante;
    private Boolean selecionado;
    private Integer quantidadeDaOf;
    private LocalEntregaEntidade localEntregaEntidade;
    private Integer idOrdemFornecimento;
    private Long idAgrupamento;
    private Integer quantidadeAnterior = 0;
    private ItensOrdemFornecimentoContrato itemOrdemFornecimento;

    public Bem getBem() {
        return bem;
    }

    public void setBem(Bem bem) {
        this.bem = bem;
    }

    public Uf getUf() {
        return uf;
    }

    public void setUf(Uf uf) {
        this.uf = uf;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public Long getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Long quantidade) {
        this.quantidade = quantidade;
    }

    public Integer getQuantidadeAnterior() {
        return quantidadeAnterior;
    }

    public void setQuantidadeAnterior(Integer quantidadeAnterior) {
        this.quantidadeAnterior = quantidadeAnterior;
    }

    public static Comparator<EnderecosOrdemFornecimentoDto> getComparator(int order, String coluna) {
        return new Comparator<EnderecosOrdemFornecimentoDto>() {
            @Override
            public int compare(EnderecosOrdemFornecimentoDto o1, EnderecosOrdemFornecimentoDto o2) {
                int valor = 0;

                if ("item".equalsIgnoreCase(coluna)) {
                    valor = o1.getBem().getNomeBem().toUpperCase().compareTo(o2.getBem().getNomeBem().toUpperCase()) * order;
                } else if ("uf".equalsIgnoreCase(coluna)) {
                    valor = o1.getUf().getNomeSigla().toUpperCase().compareTo(o2.getUf().getNomeSigla().toUpperCase()) * order;
                } else if ("endereco".equalsIgnoreCase(coluna)) {
                    valor = o1.getEndereco().toUpperCase().compareTo(o2.getEndereco().toUpperCase()) * order;
                } else if ("quantidade".equalsIgnoreCase(coluna)) {
                    valor = o1.getQuantidade().compareTo(o2.getQuantidade()) * order;
                } else if ("quantidadeSelecionada".equalsIgnoreCase(coluna)) {
                    o1.setQuantidadeDaOf(o1.getQuantidadeDaOf() == null ? 0 : o1.getQuantidadeDaOf());
                    o2.setQuantidadeDaOf(o2.getQuantidadeDaOf() == null ? 0 : o2.getQuantidadeDaOf());
                    valor = o1.getQuantidadeDaOf().compareTo(o2.getQuantidadeDaOf()) * order;
                } else if ("quantidadeRestante".equalsIgnoreCase(coluna)) {
                    valor = o1.getQuatidadeRestante().compareTo(o2.getQuatidadeRestante()) * order;
                }
                return valor;
            }
        };
    }

    public Boolean getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(Boolean selecionado) {
        this.selecionado = selecionado;
    }

    public Integer getQuantidadeDaOf() {
        return quantidadeDaOf;
    }

    public void setQuantidadeDaOf(Integer quantidadeDaOf) {
        this.quantidadeDaOf = quantidadeDaOf;
    }

    public LocalEntregaEntidade getLocalEntregaEntidade() {
        return localEntregaEntidade;
    }

    public void setLocalEntregaEntidade(LocalEntregaEntidade localEntregaEntidade) {
        this.localEntregaEntidade = localEntregaEntidade;
    }

    public Integer getQuatidadeRestante() {
        return quatidadeRestante;
    }

    public void setQuatidadeRestante(Integer quatidadeRestante) {
        this.quatidadeRestante = quatidadeRestante;
    }

    public Integer getIdOrdemFornecimento() {
        return idOrdemFornecimento;
    }

    public void setIdOrdemFornecimento(Integer idOrdemFornecimento) {
        this.idOrdemFornecimento = idOrdemFornecimento;
    }

    public ItensOrdemFornecimentoContrato getItemOrdemFornecimento() {
        return itemOrdemFornecimento;
    }

    public void setItemOrdemFornecimento(ItensOrdemFornecimentoContrato itemOrdemFornecimento) {
        this.itemOrdemFornecimento = itemOrdemFornecimento;
    }

    public Long getIdAgrupamento() {
        return idAgrupamento;
    }

    public void setIdAgrupamento(Long idAgrupamento) {
        this.idAgrupamento = idAgrupamento;
    }
}
