package br.gov.mj.side.entidades.programa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import br.gov.mj.apoio.entidades.Uf;
import br.gov.mj.infra.negocio.persistencia.BaseEntity;

@Entity
@Table(name = "tb_ppu_programa_potencial_beneficiario_uf", schema = "side")
public class ProgramaPotencialBeneficiarioUf extends BaseEntity<Long> {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ppu_id_programa_potencial_beneficiario_uf")
    @SequenceGenerator(name = "tb_ppu_programa_potencial_beneficiario_uf_generator", sequenceName = "side.seq_tb_ppu_programa_potencial_beneficiario_uf", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tb_ppu_programa_potencial_beneficiario_uf_generator")
    @NotNull
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ppu_fk_euf_id_estado")
    @NotNull
    private Uf uf;

    @ManyToOne
    @JoinColumn(name = "ppu_fk_prg_id_programa")
    @NotNull
    private Programa programa;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "potencialBeneficiarioUF", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<PotencialBeneficiarioMunicipio> potencialBeneficiarioMunicipios = new ArrayList<PotencialBeneficiarioMunicipio>();

    public ProgramaPotencialBeneficiarioUf() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Uf getUf() {
        return uf;
    }

    public void setUf(Uf uf) {
        this.uf = uf;
    }

    public Programa getPrograma() {
        return programa;
    }

    public void setPrograma(Programa programa) {
        this.programa = programa;
    }

    public List<PotencialBeneficiarioMunicipio> getPotencialBeneficiarioMunicipios() {
        return potencialBeneficiarioMunicipios;
    }

    public void setPotencialBeneficiarioMunicipios(List<PotencialBeneficiarioMunicipio> potencialBeneficiarioMunicipios) {
        this.potencialBeneficiarioMunicipios = potencialBeneficiarioMunicipios;
    }

}