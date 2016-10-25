package br.gov.mj.side.entidades.programa.inscricao.analise;

import java.time.LocalDateTime;

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

import org.hibernate.annotations.Type;

import br.gov.mj.apoio.entidades.MotivoAnalise;
import br.gov.mj.infra.negocio.persistencia.BaseEntity;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoPrograma;

@Entity
@Table(name = "tb_haa_historico_analise_avaliacao", schema = "side")
public class HistoricoAnaliseAvaliacao extends BaseEntity<Long> {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "haa_id_historico_analise_avaliacao")
    @SequenceGenerator(name = "tb_haa_historico_analise_avaliacao_generator", sequenceName = "side.seq_tb_haa_historico_analise_avaliacao", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tb_haa_historico_analise_avaliacao_generator")
    @NotNull
    private Long id;

    @ManyToOne
    @JoinColumn(name = "haa_fk_ipg_id_inscricao_programa")
    @NotNull
    private InscricaoPrograma inscricaoPrograma;

    @Column(name = "haa_vl_pontuacao_final_analise")
    @NotNull
    private Integer pontuacaoFinal;

    @Column(name = "haa_ds_descricao_justificativa")
    private String descricaoJustificativa;

    @ManyToOne
    @JoinColumn(name = "haa_fk_mae_id_motivo_analise")
    private MotivoAnalise motivoAnalise;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Column(name = "haa_dt_data_cadastro")
    @NotNull
    private LocalDateTime dataCadastro;

    @Column(name = "haa_no_usuario_cadastro")
    @NotNull
    private String usuarioCadastro;

    public HistoricoAnaliseAvaliacao() {
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

    public Integer getPontuacaoFinal() {
        return pontuacaoFinal;
    }

    public void setPontuacaoFinal(Integer pontuacaoFinal) {
        this.pontuacaoFinal = pontuacaoFinal;
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