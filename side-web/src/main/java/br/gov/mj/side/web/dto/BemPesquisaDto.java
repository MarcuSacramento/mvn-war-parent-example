package br.gov.mj.side.web.dto;

public class BemPesquisaDto {

    private String nomeBem;
    private String descricaoBem;
    private String nomeElemento;
    private String nomeSubelemento;

    public String getNomeBem() {
        return nomeBem;
    }

    public void setNomeBem(String nomeBem) {
        this.nomeBem = nomeBem;
    }

    public String getDescricaoBem() {
        return descricaoBem;
    }

    public void setDescricaoBem(String descricaoBem) {
        this.descricaoBem = descricaoBem;
    }

    public String getNomeElemento() {
        return nomeElemento;
    }

    public void setNomeElemento(String nomeElemento) {
        this.nomeElemento = nomeElemento;
    }

    public String getNomeSubelemento() {
        return nomeSubelemento;
    }

    public void setNomeSubelemento(String nomeSubelemento) {
        this.nomeSubelemento = nomeSubelemento;
    }
}
