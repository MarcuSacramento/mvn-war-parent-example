package br.gov.mj.side.web.dto;

public class KitDto {

    private String nomeKit;
    private String descricaoKit;
    private String quantidadeKit;
    private String quantidadeMaxPorProposta;

    public String getNomeKit() {
        return nomeKit;
    }

    public void setNomeKit(String nomeKit) {
        this.nomeKit = nomeKit;
    }

    public String getQuantidadeKit() {
        return quantidadeKit;
    }

    public void setQuantidadeKit(String quantidadeKit) {
        this.quantidadeKit = quantidadeKit;
    }

    public String getDescricaoKit() {
        return descricaoKit;
    }

    public void setDescricaoKit(String descricaoKit) {
        this.descricaoKit = descricaoKit;
    }

    public String getQuantidadeMaxPorProposta() {
        return quantidadeMaxPorProposta;
    }

    public void setQuantidadeMaxPorProposta(String quantidadeMaxPorProposta) {
        this.quantidadeMaxPorProposta = quantidadeMaxPorProposta;
    }
}
