package br.gov.mj.apoio.entidades.enums;

import br.gov.mj.infra.negocio.persistencia.BaseEnum;

public enum EnumParametrosSistema implements BaseEnum<String> {
    LDAP_HOST("LDAP_HOST", "Host do LDAP"), 
    LDAP_DOMINIO("LDAP_DOMINIO", "Domínio do LDAP"),
    LDAP_CONTEXTO("LDAP_CONTEXTO", "Contexto do LDAP");
   
    private String valor;
    private String descricao;

    private EnumParametrosSistema(String valor, String descricao) {
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
