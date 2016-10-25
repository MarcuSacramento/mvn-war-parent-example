package br.gov.mj.side.entidades.enums;

import br.gov.mj.infra.negocio.persistencia.BaseEnum;

public enum EnumTipoObjeto implements BaseEnum<String> {
    ORIGINAIS("ORIG", "Originais"), TODOS_DEVOLVIDOS("DEVO", "Devolvidos"),DEVOLVIDOS_SEM_VINCULO_COM_NOTA_REMESSA("DEVO", "Devolvidos"), SEM_VINCULO_COM_NOTA_REMESSA("SVIN", "Devolvidos"),AMBOS("AMBO", "Ambos");

    private String valor;
    private String descricao;

    private EnumTipoObjeto(String valor, String descricao) {
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
