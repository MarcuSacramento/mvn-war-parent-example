package br.gov.mj.side.entidades.programa.inscricao.analise;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.gov.mj.apoio.entidades.MotivoAnalise;
import br.gov.mj.infra.negocio.persistencia.BaseEntity;
import br.gov.mj.side.entidades.enums.EnumResultadoFinalAnaliseElegibilidade;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoPrograma;

@Entity
@Table(name = "tb_hae_historico_analise_elegibilidade", schema = "side")
public class HistoricoAnaliseElegibilidade extends BaseEntity<Long> {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "hae_id_historico_analise_elegibilidade")
    @SequenceGenerator(name = "tb_hae_historico_analise_elegibilidade_generator", sequenceName = "side.seq_tb_hae_historico_analise_elegibilidade", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tb_hae_historico_analise_elegibilidade_generator")
    @NotNull
    private Long id;

    @ManyToOne
    @JoinColumn(name = "hae_fk_ipg_id_inscricao_programa")
    @NotNull
    private InscricaoPrograma inscricaoPrograma;

    @Enumerated(EnumType.STRING)
    @Type(type = "br.gov.mj.infra.negocio.persistencia.BaseEnumType", parameters = @Parameter(value = "br.gov.mj.side.entidades.enums.EnumResultadoFinalAnaliseElegibilidade", name = "enumClassName"))
    @Column(name = "hae_tp_resultado_final_analise")
    @NotNull
    private EnumResultadoFinalAnaliseElegibilidade resultadoFinalAnalise;

    @Column(name = "hae_ds_descricao_justificativa")
    private String descricaoJustificativa;

    @ManyToOne
    @JoinColumn(name = "hae_fk_mae_id_motivo_analise")
    private MotivoAnalise motivoAnalise;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Column(name = "hae_dt_data_cadastro")
    @NotNull
    private LocalDateTime dataCadastro;

    @Column(name = "hae_no_usuario_cadastro")
    @NotNull
    private String usuarioCadastro;

    public HistoricoAnaliseElegibilidade() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public InscricaoPrograma getInscricaoPrograma() {
        return inscricaoPrograma;
    }

    public void setInscricaoPrograma(InscricaoPrograma inscricaoPrograma) {
        this.inscricaoPrograma = inscricaoPrograma;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public String getUsuarioCadastro() {
        return usuarioCadastro;
    }

    public void setUsuarioCadastro(String usuarioCadastro) {
        this.usuarioCadastro = usuarioCadastro;
    }

    public EnumResultadoFinalAnaliseElegibilidade getResultadoFinalAnalise() {
        return resultadoFinalAnalise;
    }

    public void setResultadoFinalAnalise(EnumResultadoFinalAnaliseElegibilidade resultadoFinalAnalise) {
        this.resultadoFinalAnalise = resultadoFinalAnalise;
    }

    public String getDescricaoJustificativa() {
        return descricaoJustificativa;
    }

    public void setDescricaoJustificativa(String descricaoJustificativa) {
        this.descricaoJustificativa = descricaoJustificativa;
    }

    public MotivoAnalise getMotivoAnalise() {
        return motivoAnalise;
    }

    public void setMotivoAnalise(MotivoAnalise motivoAnalise) {
        this.motivoAnalise = motivoAnalise;
    }

}