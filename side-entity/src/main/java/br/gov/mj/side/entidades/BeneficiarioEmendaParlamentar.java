package br.gov.mj.side.entidades;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import br.gov.mj.infra.negocio.persistencia.BaseEntity;

@Entity
@Table(name = "tb_bep_beneficiario_emenda_parlamentar", schema = "side")
public class BeneficiarioEmendaParlamentar extends BaseEntity<Long> {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "bep_id_beneficiario_emenda_parlamentar")
    @SequenceGenerator(name = "TB_BEP_BENEFICIARIO_EMENDA_PARLAMENTAR_BEPIDBENEFICIARIOEMENDAPARLAMENTAR_GENERATOR", sequenceName = "SIDE.SEQ_TB_BEP_BENEFICIARIO_EMENDA_PARLAMENTAR", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TB_BEP_BENEFICIARIO_EMENDA_PARLAMENTAR_BEPIDBENEFICIARIOEMENDAPARLAMENTAR_GENERATOR")
    @NotNull
    private Long id;

    @Column(name = "bep_no_nome")
    @NotNull
    private String nomeBeneficiario;

    @Column(name = "bep_nu_numero_cnpj")
    @NotNull
    private String numeroCnpjBeneficiario;

    @ManyToOne
    @JoinColumn(name = "bep_epa_id_emenda_parlamentar")
    @NotNull
    private EmendaParlamentar emendaParlamentar;

    public BeneficiarioEmendaParlamentar() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeBeneficiario() {
        return nomeBeneficiario;
    }

    public void setNomeBeneficiario(String nomeBeneficiario) {
        this.nomeBeneficiario = nomeBeneficiario;
    }

    public String getNumeroCnpjBeneficiario() {
        return numeroCnpjBeneficiario;
    }

    public void setNumeroCnpjBeneficiario(String numeroCnpjBeneficiario) {
        this.numeroCnpjBeneficiario = numeroCnpjBeneficiario;
    }

    public EmendaParlamentar getEmendaParlamentar() {
        return emendaParlamentar;
    }

    public void setEmendaParlamentar(EmendaParlamentar emendaParlamentar) {
        this.emendaParlamentar = emendaParlamentar;
    }

}