package br.gov.mj.side.web.dto;

import br.gov.mj.infra.negocio.persistencia.BaseEntity;

public class ItemDto extends BaseEntity<Long> {

    private static final long serialVersionUID = 1L;
    private Long id;
    private Integer quantidade;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

}
