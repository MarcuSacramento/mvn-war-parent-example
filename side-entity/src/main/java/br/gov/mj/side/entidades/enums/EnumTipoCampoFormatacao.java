package br.gov.mj.side.entidades.enums;

import br.gov.mj.infra.negocio.persistencia.BaseEnum;

public enum EnumTipoCampoFormatacao implements BaseEnum<String> {

    ALFANUMERICO("ALF", "Alfanumérico"), ANEXO("ANE", "Anexo"), BOLEANO("BOL", "Verdadeiro/Falso ou Sim/Não"), DATA("DAT", "Data"), FOTO("FOT", "Foto"), NUMERICO("NUM", "Numérico"), TEXTO("TEX", "Texto"), VIDEO("VID", "Vídeo");

    private String valor;
    private String descricao;

    private EnumTipoCampoFormatacao(String valor, String descricao) {
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
