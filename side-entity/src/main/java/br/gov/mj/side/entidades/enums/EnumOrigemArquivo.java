package br.gov.mj.side.entidades.enums;

import br.gov.mj.infra.negocio.persistencia.BaseEnum;

public enum EnumOrigemArquivo implements BaseEnum<String> {
    PATRIMONIAMENTO("PATR", "Patrimoniamento"), CADASTRO_CONFORMIDADES("CONF", "Cadastro das formatações pelo beneficiário/fornecedor");

    private String valor;
    private String descricao;

    private EnumOrigemArquivo(String valor, String descricao) {
        this.valor = valor;
        this.descricao = descricao;
    }

    @Override
    public String getValor() {
        return this.valor;
    }

    public String getDescricao() {
        return this.descricao;
    }
}
