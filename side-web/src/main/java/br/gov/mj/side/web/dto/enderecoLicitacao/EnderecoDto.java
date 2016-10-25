package br.gov.mj.side.web.dto.enderecoLicitacao;

import java.io.Serializable;

import br.gov.mj.side.entidades.entidade.LocalEntregaEntidade;

public class EnderecoDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private LocalEntregaEntidade localEntregaEntidade;
    private Long quantidade;

    public Long getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Long quantidade) {
        this.quantidade = quantidade;
    }

    public LocalEntregaEntidade getLocalEntregaEntidade() {
        return localEntregaEntidade;
    }

    public void setLocalEntregaEntidade(LocalEntregaEntidade localEntregaEntidade) {
        this.localEntregaEntidade = localEntregaEntidade;
    }

}
