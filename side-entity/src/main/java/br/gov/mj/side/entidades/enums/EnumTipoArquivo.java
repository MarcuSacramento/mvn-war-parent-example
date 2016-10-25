package br.gov.mj.side.entidades.enums;

import br.gov.mj.infra.negocio.persistencia.BaseEnum;

public enum EnumTipoArquivo implements BaseEnum<String> {
    NORMA("N", "Norma"), EDITAL("E", "Edital"), ATA_ADMINISTRATIVA("A", "Ata Administrativa"), DOCUMENTO("D", "Documento"), PLANTA("P", "Planta"), RELATORIO("R", "Relatório"), IMAGEM("I", "Imagem"), LOGOMARCA_PROGRAMA("L", "Logomarca do Programa"), OUTROS("O", "Outros");

    private String valor;
    private String descricao;

    private EnumTipoArquivo(String valor, String descricao) {
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
