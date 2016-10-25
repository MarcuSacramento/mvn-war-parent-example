package br.gov.mj.side.entidades.programa.inscricao;

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

import br.gov.mj.infra.negocio.persistencia.BaseEntity;
import br.gov.mj.side.entidades.entidade.Pessoa;

@Entity
@Table(name = "tb_cor_comissao_recebimento", schema = "side")
public class ComissaoRecebimento extends BaseEntity<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "cor_id_comissao_recebimento")
    @SequenceGenerator(name = "tb_cor_comissao_recebimento_generator", sequenceName = "side.seq_tb_cor_comissao_recebimento_anexo", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tb_cor_comissao_recebimento_generator")
    @NotNull
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cor_fk_ipg_id_inscricao_programa")
    @NotNull
    private InscricaoPrograma inscricaoPrograma;

    @ManyToOne
    @JoinColumn(name = "cor_fk_pso_id_pessoa")
    @NotNull
    private Pessoa membroComissao;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Column(name = "cor_dt_data_cadastro")
    @NotNull
    private LocalDateTime dataCadastro;

    public ComissaoRecebimento() {
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

    public Pessoa getMembroComissao() {
        return membroComissao;
    }

    public void setMembroComissao(Pessoa membroComissao) {
        this.membroComissao = membroComissao;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

}