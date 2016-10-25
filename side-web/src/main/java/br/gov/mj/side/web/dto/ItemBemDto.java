package br.gov.mj.side.web.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import br.gov.mj.side.entidades.enums.EnumTipoAgrupamentoLicitacao;
import br.gov.mj.side.entidades.programa.licitacao.AgrupamentoLicitacao;
import br.gov.mj.side.entidades.programa.licitacao.contrato.Contrato;

public class ItemBemDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer contadorTemp;
    private Boolean objetoJaPersistido = false;
    private AgrupamentoLicitacao agrupamentoLicitacao;
    private LicitacaoNomeModelDto nomeModelDto;
    private EnumTipoAgrupamentoLicitacao tipoAgrupamentoLicitacao;
    private List<BemUfDto> listaDeBens = new ArrayList<BemUfDto>();
    private String unidadeDeMedida;
    private Long quantidadeRegistrar;
    private Long quantidadeImediata;
    private BigDecimal valorUnitario;
    private BigDecimal valorTotalImediato;
    private BigDecimal valorTotalRegistrar;
    private Contrato contrato;
    private Boolean selecionado;
    private Long idParaFormatacao;

    public Long getIdParaFormatacao() {
        return idParaFormatacao;
    }

    public void setIdParaFormatacao(Long idParaFormatacao) {
        this.idParaFormatacao = idParaFormatacao;
    }

    public Boolean getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(Boolean selecionado) {
        this.selecionado = selecionado;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public EnumTipoAgrupamentoLicitacao getTipoAgrupamentoLicitacao() {
        return tipoAgrupamentoLicitacao;
    }

    public void setTipoAgrupamentoLicitacao(EnumTipoAgrupamentoLicitacao tipoAgrupamentoLicitacao) {
        this.tipoAgrupamentoLicitacao = tipoAgrupamentoLicitacao;
    }

    public List<BemUfDto> getListaDeBens() {
        return listaDeBens;
    }

    public void setListaDeBens(List<BemUfDto> listaDeBens) {
        this.listaDeBens = listaDeBens;
    }

    public String getUnidadeDeMedida() {
        return unidadeDeMedida;
    }

    public void setUnidadeDeMedida(String unidadeDeMedida) {
        this.unidadeDeMedida = unidadeDeMedida;
    }

    public Long getQuantidadeRegistrar() {
        return quantidadeRegistrar;
    }

    public void setQuantidadeRegistrar(Long quantidadeRegistrar) {
        this.quantidadeRegistrar = quantidadeRegistrar;
    }

    public Long getQuantidadeImediata() {
        return quantidadeImediata;
    }

    public void setQuantidadeImediata(Long quantidadeImediata) {
        this.quantidadeImediata = quantidadeImediata;
    }

    public BigDecimal getValorTotalImediato() {
        return valorTotalImediato;
    }

    public void setValorTotalImediato(BigDecimal valorTotalImediato) {
        this.valorTotalImediato = valorTotalImediato;
    }

    public BigDecimal getValorTotalRegistrar() {
        return valorTotalRegistrar;
    }

    public void setValorTotalRegistrar(BigDecimal valorTotalRegistrar) {
        this.valorTotalRegistrar = valorTotalRegistrar;
    }

    public AgrupamentoLicitacao getAgrupamentoLicitacao() {
        return agrupamentoLicitacao;
    }

    public void setAgrupamentoLicitacao(AgrupamentoLicitacao agrupamentoLicitacao) {
        this.agrupamentoLicitacao = agrupamentoLicitacao;
    }

    public LicitacaoNomeModelDto getNomeModelDto() {
        return nomeModelDto;
    }

    public void setNomeModelDto(LicitacaoNomeModelDto nomeModelDto) {
        this.nomeModelDto = nomeModelDto;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public static Comparator<ItemBemDto> getComparator(int order, String coluna) {
        return new Comparator<ItemBemDto>() {
            @Override
            public int compare(ItemBemDto o1, ItemBemDto o2) {
                int valor = 0;

                if ("nome".equalsIgnoreCase(coluna)) {
                    valor = o1.getNomeModelDto().getNomeGrupo().toUpperCase().compareTo(o2.getNomeModelDto().getNomeGrupo().toUpperCase()) * order;
                }
                return valor;
            }
        };
    }

    public Integer getContadorTemp() {
        return contadorTemp;
    }

    public void setContadorTemp(Integer contadorTemp) {
        this.contadorTemp = contadorTemp;
    }

    public Boolean getObjetoJaPersistido() {
        return objetoJaPersistido;
    }

    public void setObjetoJaPersistido(Boolean objetoJaPersistido) {
        this.objetoJaPersistido = objetoJaPersistido;
    }
}
