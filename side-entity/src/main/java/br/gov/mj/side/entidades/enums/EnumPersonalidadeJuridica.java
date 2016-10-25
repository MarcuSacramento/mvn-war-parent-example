package br.gov.mj.side.entidades.enums;

import java.util.Arrays;
import java.util.List;

import br.gov.mj.infra.negocio.persistencia.BaseEnum;

public enum EnumPersonalidadeJuridica implements BaseEnum<String> {
    PUBLICA("U", "Pública"), 
    PRIVADA("I", "Privada sem fins lucrativos"), 
    TODAS("T", "Todas");

    private String valor;
    private String descricao;

    private EnumPersonalidadeJuridica(String valor, String descricao) {
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

    public static List<EnumPersonalidadeJuridica> getAsList() {
        return Arrays.asList(values());
    }

}
