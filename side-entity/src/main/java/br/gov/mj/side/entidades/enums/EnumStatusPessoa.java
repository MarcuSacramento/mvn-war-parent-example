package br.gov.mj.side.entidades.enums;

import java.util.Arrays;
import java.util.List;

import br.gov.mj.infra.negocio.persistencia.BaseEnum;

public enum EnumStatusPessoa implements BaseEnum<String> {
    ATIVO("A", "Ativo"), INATIVO("I", "Inativo");

    private String valor;
    private String descricao;

    private EnumStatusPessoa(String valor, String descricao) {
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

    public EnumStatusPessoa getEnum(){
    	return this;
    }
    public static List<EnumStatusPessoa> asList(){
    	return Arrays.asList(values());
    }
}
