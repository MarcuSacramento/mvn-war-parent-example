package br.gov.mj.side.web.dto;


public class ItensNotaRemessaDto {

    private Long idItem;
    private Long idBem;
    private String nomeBem;
    private Long idLocalEntrega;
    private Long idOrdemFornecimento;

    public Long getIdBem() {
        return idBem;
    }

    public void setIdBem(Long idBem) {
        this.idBem = idBem;
    }

    public Long getIdLocalEntrega() {
        return idLocalEntrega;
    }

    public void setIdLocalEntrega(Long idLocalEntrega) {
        this.idLocalEntrega = idLocalEntrega;
    }

    public Long getIdOrdemFornecimento() {
        return idOrdemFornecimento;
    }

    public void setIdOrdemFornecimento(Long idOrdemFornecimento) {
        this.idOrdemFornecimento = idOrdemFornecimento;
    }

    public Long getIdItem() {
        return idItem;
    }

    public void setIdItem(Long idItem) {
        this.idItem = idItem;
    }

    public String getNomeBem() {
        return nomeBem;
    }

    public void setNomeBem(String nomeBem) {
        this.nomeBem = nomeBem;
    }

}
