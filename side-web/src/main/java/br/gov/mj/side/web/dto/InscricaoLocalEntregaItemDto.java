package br.gov.mj.side.web.dto;

import java.io.Serializable;

import br.gov.mj.side.entidades.programa.inscricao.InscricaoLocalEntrega;
import br.gov.mj.side.web.view.enums.EnumTipoItemEntrega;

public class InscricaoLocalEntregaItemDto implements Serializable, Comparable<InscricaoLocalEntregaItemDto> {

    private static final long serialVersionUID = 1L;

    private String nome;
    private Long idInscricaoProgramaBem;
    private Long idInscricaoProgramaKit;
    private Integer quantidade;
    private InscricaoLocalEntrega localEntrega;
    private EnumTipoItemEntrega tipoItem;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public InscricaoLocalEntrega getLocalEntrega() {
        return localEntrega;
    }

    public void setLocalEntrega(InscricaoLocalEntrega localEntrega) {
        this.localEntrega = localEntrega;
    }

    public EnumTipoItemEntrega getTipoItem() {
        return tipoItem;
    }

    public void setTipoItem(EnumTipoItemEntrega tipoItem) {
        this.tipoItem = tipoItem;
    }

    public Long getIdInscricaoProgramaBem() {
        return idInscricaoProgramaBem;
    }

    public void setIdInscricaoProgramaBem(Long idInscricaoProgramaBem) {
        this.idInscricaoProgramaBem = idInscricaoProgramaBem;
    }

    public Long getIdInscricaoProgramaKit() {
        return idInscricaoProgramaKit;
    }

    public void setIdInscricaoProgramaKit(Long idInscricaoProgramaKit) {
        this.idInscricaoProgramaKit = idInscricaoProgramaKit;
    }

    @Override
    public int compareTo(InscricaoLocalEntregaItemDto outroItem) {
        if (this.getLocalEntrega().getLocalEntregaEntidade().getId() > outroItem.getLocalEntrega().getLocalEntregaEntidade().getId()) {
            return 1;
        }
        if (this.getLocalEntrega().getLocalEntregaEntidade().getId() < outroItem.getLocalEntrega().getLocalEntregaEntidade().getId()) {
            return -1;
        }
        return 0;
    }

    public boolean equals(Object obj) {
        InscricaoLocalEntregaItemDto outroItem = (InscricaoLocalEntregaItemDto) obj;
        boolean teste = this.idInscricaoProgramaBem == outroItem.idInscricaoProgramaBem && this.idInscricaoProgramaKit == outroItem.idInscricaoProgramaKit && this.localEntrega.getLocalEntregaEntidade().equals(outroItem.localEntrega.getLocalEntregaEntidade())
                && this.tipoItem.equals(outroItem.getTipoItem());
        return teste;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
