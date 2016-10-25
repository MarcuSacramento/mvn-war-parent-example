package br.gov.mj.side.entidades;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.gov.mj.apoio.entidades.PartidoPolitico;
import br.gov.mj.apoio.entidades.Uf;
import br.gov.mj.infra.negocio.persistencia.BaseEntity;
import br.gov.mj.side.entidades.enums.EnumTipoEmenda;

@Entity
@Table(name = "tb_epa_emenda_parlamentar", schema = "side")
public class EmendaParlamentar extends BaseEntity<Long> {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "epa_id_emenda_parlamentar")
    @SequenceGenerator(name = "TB_EPA_EMENDA_PARLAMENTAR_EPAIDEMENDAPARLAMENTAR_GENERATOR", sequenceName = "SIDE.SEQ_TB_EPA_EMENDA_PARLAMENTAR", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TB_EPA_EMENDA_PARLAMENTAR_EPAIDEMENDAPARLAMENTAR_GENERATOR")
    @NotNull
    private Long id;

    @Column(name = "epa_bo_possui_liberacao")
    @NotNull
    private Boolean possuiLiberacao;

    @Column(name = "epa_no_nome_cargo_parlamentar")
    @NotNull
    private String nomeCargoParlamentar;

    @Column(name = "epa_no_nome_emenda_parlamentar")
    @NotNull
    private String nomeEmendaParlamentar;

    @Column(name = "epa_no_nome_parlamentar")
    @NotNull
    private String nomeParlamentar;

    @Column(name = "epa_nu_numero_emenda_parlamentar")
    @NotNull
    private String numeroEmendaParlamantar;

    @Enumerated(EnumType.STRING)
    @Type(type = "br.gov.mj.infra.negocio.persistencia.BaseEnumType", parameters = @Parameter(value = "br.gov.mj.side.entidades.enums.EnumTipoEmenda", name = "enumClassName"))
    @Column(name = "epa_tp_tipo_emenda")
    @NotNull
    private EnumTipoEmenda tipoEmenda;

    @Column(name = "epa_vl_valor_previsto")
    @NotNull
    private BigDecimal valorPrevisto;

    @OneToMany(mappedBy = "emendaParlamentar", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<BeneficiarioEmendaParlamentar> beneficiariosEmendaParlamentar = new ArrayList<BeneficiarioEmendaParlamentar>();

    @ManyToOne
    @JoinColumn(name = "epa_euf_id_estado")
    @NotNull
    private Uf uf;

    @ManyToOne
    @JoinColumn(name = "epa_fk_ppo_id_partido_politico")
    @NotNull
    private PartidoPolitico partidoPolitico;

    @ManyToOne
    @JoinColumn(name = "epa_aco_id_acao_orcamentaria")
    @NotNull
    private AcaoOrcamentaria acaoOrcamentaria;

    public EmendaParlamentar() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getPossuiLiberacao() {
        return possuiLiberacao;
    }

    public void setPossuiLiberacao(Boolean possuiLiberacao) {
        this.possuiLiberacao = possuiLiberacao;
    }

    public String getNomeCargoParlamentar() {
        return nomeCargoParlamentar;
    }

    public void setNomeCargoParlamentar(String nomeCargoParlamentar) {
        this.nomeCargoParlamentar = nomeCargoParlamentar;
    }

    public String getNomeEmendaParlamentar() {
        return nomeEmendaParlamentar;
    }

    public void setNomeEmendaParlamentar(String nomeEmendaParlamentar) {
        this.nomeEmendaParlamentar = nomeEmendaParlamentar;
    }

    public String getNomeParlamentar() {
        return nomeParlamentar;
    }

    public void setNomeParlamentar(String nomeParlamentar) {
        this.nomeParlamentar = nomeParlamentar;
    }

    public String getNumeroEmendaParlamantar() {
        return numeroEmendaParlamantar;
    }

    public void setNumeroEmendaParlamantar(String numeroEmendaParlamantar) {
        this.numeroEmendaParlamantar = numeroEmendaParlamantar;
    }

    public BigDecimal getValorPrevisto() {
        return valorPrevisto;
    }

    public void setValorPrevisto(BigDecimal valorPrevisto) {
        this.valorPrevisto = valorPrevisto;
    }

    public List<BeneficiarioEmendaParlamentar> getBeneficiariosEmendaParlamentar() {
        return beneficiariosEmendaParlamentar;
    }

    public void setBeneficiariosEmendaParlamentar(List<BeneficiarioEmendaParlamentar> beneficiariosEmendaParlamentar) {
        this.beneficiariosEmendaParlamentar = beneficiariosEmendaParlamentar;
    }

    public Uf getUf() {
        return uf;
    }

    public void setUf(Uf uf) {
        this.uf = uf;
    }

    public PartidoPolitico getPartidoPolitico() {
        return partidoPolitico;
    }

    public void setPartidoPolitico(PartidoPolitico partidoPolitico) {
        this.partidoPolitico = partidoPolitico;
    }

    public EnumTipoEmenda getTipoEmenda() {
        return tipoEmenda;
    }

    public void setTipoEmenda(EnumTipoEmenda tipoEmenda) {
        this.tipoEmenda = tipoEmenda;
    }

    public AcaoOrcamentaria getAcaoOrcamentaria() {
        return acaoOrcamentaria;
    }

    public void setAcaoOrcamentaria(AcaoOrcamentaria acaoOrcamentaria) {
        this.acaoOrcamentaria = acaoOrcamentaria;
    }

    @Transient
    public String getNumeroNome() {
        return this.numeroEmendaParlamantar + " - " + this.nomeEmendaParlamentar;
    }

}