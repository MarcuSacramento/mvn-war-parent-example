package br.gov.mj.side.entidades.programa;

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

import br.gov.mj.apoio.entidades.Municipio;
import br.gov.mj.infra.negocio.persistencia.BaseEntity;

@Entity
@Table(name = "tb_ppm_programa_potencial_beneficiario_municipio", schema = "side")
public class PotencialBeneficiarioMunicipio extends BaseEntity<Long> {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ppm_id_programa_potencial_beneficiario_municipio")
    @SequenceGenerator(name = "tb_ppm_programa_potencial_beneficiario_municipio_generator", sequenceName = "side.seq_tb_ppm_programa_potencial_beneficiario_municipio", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tb_ppm_programa_potencial_beneficiario_municipio_generator")
    @NotNull
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ppu_fk_ppu_id_programa_potencial_beneficiario_uf")
    @NotNull
    private ProgramaPotencialBeneficiarioUf potencialBeneficiarioUF;

    @ManyToOne
    @JoinColumn(name = "ppm_fk_mun_id_municipio")
    @NotNull
    private Municipio municipio;

    public PotencialBeneficiarioMunicipio() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProgramaPotencialBeneficiarioUf getPotencialBeneficiarioUF() {
        return potencialBeneficiarioUF;
    }

    public void setPotencialBeneficiarioUF(ProgramaPotencialBeneficiarioUf potencialBeneficiarioUF) {
        this.potencialBeneficiarioUF = potencialBeneficiarioUF;
    }

    public Municipio getMunicipio() {
        return municipio;
    }

    public void setMunicipio(Municipio municipio) {
        this.municipio = municipio;
    }

}