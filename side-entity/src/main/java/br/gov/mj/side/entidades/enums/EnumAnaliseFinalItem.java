package br.gov.mj.side.entidades.enums;

import br.gov.mj.infra.negocio.persistencia.BaseEnum;

public enum EnumAnaliseFinalItem implements BaseEnum<String> {

    NAO_ANALISADO("NANA", "Não analisado"), ACEITO("ACEI", "Aceito"), NAO_ACEITO("NACE", "Não aceito"), ACEITO_RESSALVA("ARES", "Aceito com ressalva"), DEVOLVIDO("DEVO","Devolvido para correção");

    private String valor;
    private String descricao;

    private EnumAnaliseFinalItem(String valor, String descricao) {
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
