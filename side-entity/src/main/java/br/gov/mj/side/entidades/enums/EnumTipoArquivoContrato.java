package br.gov.mj.side.entidades.enums;

import br.gov.mj.infra.negocio.persistencia.BaseEnum;

public enum EnumTipoArquivoContrato implements BaseEnum<String> {
    CONTRATO("C", "Contrato"), TERMO_ADITIVO("T", "Termo Aditivo"), INDICACAO_PREPOSTO("I", "Indicação Preposto"), OUTROS("O", "Outros");

    private String valor;
    private String descricao;

    private EnumTipoArquivoContrato(String valor, String descricao) {
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
