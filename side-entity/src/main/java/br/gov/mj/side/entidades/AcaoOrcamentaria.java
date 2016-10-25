package br.gov.mj.side.entidades;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;

import br.gov.mj.infra.negocio.persistencia.BaseEntity;

/**
 * The persistent class for the tb_rec_recusro database table.
 * 
 */
@Entity
@Table(name = "tb_aco_acao_orcamentaria", schema = "side")
public class AcaoOrcamentaria extends BaseEntity<Long> {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "aco_id_acao_orcamentaria")
    @SequenceGenerator(name = "TB_ACO_ACAO_ORCAMENTARIA_GENERATOR", sequenceName = "SIDE.SEQ_TB_ACO_ACAO_ORCAMENTARIA", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TB_ACO_ACAO_ORCAMENTARIA_GENERATOR")
    @NotNull
    private Long id;

    @Column(name = "aco_nu_programa_ppa")
    @NotNull
    private String numeroProgramaPPA;

    @Column(name = "aco_no_programa_ppa")
    @NotNull
    private String nomeProgramaPPA;

    @Column(name = "aco_nu_acao_orcamentaria")
    @NotNull
    private String numeroAcaoOrcamentaria;

    @Column(name = "aco_no_acao_orcamentaria")
    @NotNull
    private String nomeAcaoOrcamentaria;

    @Column(name = "aco_vl_valor_previsto")
    @NotNull
    private BigDecimal valorPrevisto;

    @Column(name = "aco_no_usuario_cadastro")
    private String usuarioCadastro;

    @Column(name = "aco_no_usuario_alteracao")
    private String usuarioAlteracao;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Column(name = "aco_dt_data_cadastro")
    private LocalDateTime dataCadastro;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Column(name = "aco_dt_data_alteracao")
    private LocalDateTime dataAlteracao;

    @Column(name = "aco_an_ano_acao_orcamentaria")
    @NotNull
    private Integer anoAcaoOrcamentaria;

    @OneToMany(mappedBy = "acaoOrcamentaria", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<EmendaParlamentar> emendasParlamentares = new ArrayList<EmendaParlamentar>();

    public AcaoOrcamentaria() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroProgramaPPA() {
        return numeroProgramaPPA;
    }

    public void setNumeroProgramaPPA(String numeroProgramaPPA) {
        this.numeroProgramaPPA = numeroProgramaPPA;
    }

    public String getNomeProgramaPPA() {
        return nomeProgramaPPA;
    }

    public void setNomeProgramaPPA(String nomeProgramaPPA) {
        this.nomeProgramaPPA = nomeProgramaPPA;
    }

    public String getNumeroAcaoOrcamentaria() {
        return numeroAcaoOrcamentaria;
    }

    public void setNumeroAcaoOrcamentaria(String numeroAcaoOrcamentaria) {
        this.numeroAcaoOrcamentaria = numeroAcaoOrcamentaria;
    }

    public String getNomeAcaoOrcamentaria() {
        return nomeAcaoOrcamentaria;
    }

    public void setNomeAcaoOrcamentaria(String nomeAcaoOrcamentaria) {
        this.nomeAcaoOrcamentaria = nomeAcaoOrcamentaria;
    }

    public BigDecimal getValorPrevisto() {
        return valorPrevisto;
    }

    public void setValorPrevisto(BigDecimal valorPrevisto) {
        this.valorPrevisto = valorPrevisto;
    }

    public List<EmendaParlamentar> getEmendasParlamentares() {
        return emendasParlamentares;
    }

    public void setEmendasParlamentares(List<EmendaParlamentar> emendasParlamentares) {
        this.emendasParlamentares = emendasParlamentares;
    }

    public String getUsuarioCadastro() {
        return usuarioCadastro;
    }

    public void setUsuarioCadastro(String usuarioCadastro) {
        this.usuarioCadastro = usuarioCadastro;
    }

    public String getUsuarioAlteracao() {
        return usuarioAlteracao;
    }

    public void setUsuarioAlteracao(String usuarioAlteracao) {
        this.usuarioAlteracao = usuarioAlteracao;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public LocalDateTime getDataAlteracao() {
        return dataAlteracao;
    }

    public void setDataAlteracao(LocalDateTime dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }

    @Transient
    public String getNumeroNomeAcaoOrcamentaria() {
        return this.numeroAcaoOrcamentaria + " / " + this.anoAcaoOrcamentaria + " - " + this.nomeAcaoOrcamentaria;
    }

    @Transient
    public BigDecimal getTotalValor() {
        BigDecimal retorno = BigDecimal.ZERO;
        for (EmendaParlamentar emenda : this.emendasParlamentares) {
            BigDecimal quantidadeEmBigDecimal = new BigDecimal(String.valueOf(emenda.getValorPrevisto()));
            retorno = quantidadeEmBigDecimal.add(retorno);
        }
        if (getValorPrevisto() != null) {
            retorno = getValorPrevisto().add(retorno);
        }
        return retorno;
    }

    public Integer getAnoAcaoOrcamentaria() {
        return anoAcaoOrcamentaria;
    }

    public void setAnoAcaoOrcamentaria(Integer anoAcaoOrcamentaria) {
        this.anoAcaoOrcamentaria = anoAcaoOrcamentaria;
    }

}