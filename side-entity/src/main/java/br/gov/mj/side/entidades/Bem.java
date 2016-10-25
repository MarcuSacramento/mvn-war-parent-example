package br.gov.mj.side.entidades;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;

import br.gov.mj.apoio.entidades.SubElemento;
import br.gov.mj.infra.negocio.persistencia.BaseEntity;

@Entity
@Table(name = "tb_bem_bem", schema = "side")
public class Bem extends BaseEntity<Long> {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "bem_id_bem")
    @SequenceGenerator(name = "TB_BEM_BEM_BEMIDBEM_GENERATOR", sequenceName = "SIDE.SEQ_TB_BEM_BEM", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TB_BEM_BEM_BEMIDBEM_GENERATOR")
    @NotNull
    private Long id;

    @Column(name = "bem_ds_bem")
    @NotNull
    private String descricaoBem;

    @Column(name = "bem_no_bem")
    @NotNull
    private String nomeBem;

    @Column(name = "bem_vl_bem")
    @NotNull
    private BigDecimal valorEstimadoBem;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    @Column(name = "bem_dt_data_estimativa")
    private LocalDate dataEstimativa;

    @OneToMany(mappedBy = "bem", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<AnexoBem> anexos = new ArrayList<AnexoBem>();

    @OneToMany(mappedBy = "bem", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<TagBem> tags = new ArrayList<TagBem>();

    @OneToMany(mappedBy = "bem", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<HistoricoBem> historicoBemValores = new ArrayList<HistoricoBem>();

    @ManyToOne
    @JoinColumn(name = "bem_fk_sue_id_subelemento")
    @NotNull
    private SubElemento subElemento;

    @Column(name = "bem_no_catmat")
    private String nomeCatmat;

    @Transient
    private Boolean itemSelecionadoFormatacao;

    public Bem() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricaoBem() {
        return descricaoBem;
    }

    public void setDescricaoBem(String descricaoBem) {
        this.descricaoBem = descricaoBem;
    }

    public String getNomeBem() {
        return nomeBem;
    }

    public void setNomeBem(String nomeBem) {
        this.nomeBem = nomeBem;
    }

    public BigDecimal getValorEstimadoBem() {
        return valorEstimadoBem;
    }

    public void setValorEstimadoBem(BigDecimal valorEstimadoBem) {
        this.valorEstimadoBem = valorEstimadoBem;
    }

    public List<AnexoBem> getAnexos() {
        return anexos;
    }

    public void setAnexos(List<AnexoBem> anexos) {
        this.anexos = anexos;
    }

    public List<TagBem> getTags() {
        return tags;
    }

    public void setTags(List<TagBem> tags) {
        this.tags = tags;
    }

    public SubElemento getSubElemento() {
        return subElemento;
    }

    public void setSubElemento(SubElemento subElemento) {
        this.subElemento = subElemento;
    }

    public static Comparator<Bem> getComparator(int order, String coluna) {
        return new Comparator<Bem>() {
            @Override
            public int compare(Bem o1, Bem o2) {
                int valor = 0;

                if ("nome".equalsIgnoreCase(coluna)) {
                    valor = o1.getNomeBem().toUpperCase().compareTo(o2.getNomeBem().toUpperCase()) * order;
                } else if ("descricao".equalsIgnoreCase(coluna)) {
                    valor = o1.getDescricaoBem().toUpperCase().compareTo(o2.getDescricaoBem().toUpperCase()) * order;
                }
                return valor;
            }
        };
    }

    public List<HistoricoBem> getHistoricoBemValores() {
        return historicoBemValores;
    }

    public void setHistoricoBemValores(List<HistoricoBem> historicoBemValores) {
        this.historicoBemValores = historicoBemValores;
    }

    public LocalDate getDataEstimativa() {
        return dataEstimativa;
    }

    public void setDataEstimativa(LocalDate dataEstimativa) {
        this.dataEstimativa = dataEstimativa;
    }

    public String getNomeCatmat() {
        return nomeCatmat;
    }

    public void setNomeCatmat(String nomeCatmat) {
        this.nomeCatmat = nomeCatmat;
    }

    public Boolean getItemSelecionadoFormatacao() {
        return itemSelecionadoFormatacao;
    }

    public void setItemSelecionadoFormatacao(Boolean itemSelecionadoFormatacao) {
        this.itemSelecionadoFormatacao = itemSelecionadoFormatacao;
    }

}