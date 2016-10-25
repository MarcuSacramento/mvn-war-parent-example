package br.gov.mj.side.web.dao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.gov.mj.side.entidades.programa.inscricao.InscricaoPrograma;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoProgramaCriterioElegibilidade;
import br.gov.mj.side.entidades.programa.inscricao.analise.HistoricoAnaliseElegibilidade;
import br.gov.mj.side.entidades.programa.inscricao.analise.InscricaoAnexoAnalise;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class InscricaoProgramaElegibilidadeDAO {

    private static final String INSCRICAO_PROGRAMA = "inscricaoPrograma";

    @Inject
    private EntityManager em;

    public List<HistoricoAnaliseElegibilidade> buscarHistoricoAnaliseElegibilidade(Long id) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<HistoricoAnaliseElegibilidade> criteriaQuery = criteriaBuilder.createQuery(HistoricoAnaliseElegibilidade.class);
        Root<HistoricoAnaliseElegibilidade> root = criteriaQuery.from(HistoricoAnaliseElegibilidade.class);
        List<Predicate> predicates = new ArrayList<>();
        if (id != null) {
            predicates.add(criteriaBuilder.equal(root.get(INSCRICAO_PROGRAMA).get("id"), id));
        }
        criteriaQuery.orderBy(criteriaBuilder.desc(root.get("id")));
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<HistoricoAnaliseElegibilidade> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public InscricaoPrograma buscarPeloId(Long id) {

        return em.find(InscricaoPrograma.class, id);
    }

    public InscricaoPrograma alterar(boolean possuiHistorico, InscricaoPrograma inscricaoPrograma, String usuarioLogado) {

        InscricaoPrograma inscricaoProgramaParaMerge = buscarPeloId(inscricaoPrograma.getId());

        inscricaoProgramaParaMerge.setUsuarioAlteracao(usuarioLogado);
        inscricaoProgramaParaMerge.setDataAlteracao(LocalDateTime.now());
        inscricaoProgramaParaMerge.setStatusInscricao(inscricaoPrograma.getStatusInscricao());
        inscricaoProgramaParaMerge.setResultadoFinalAnaliseElegibilidade(inscricaoPrograma.getResultadoFinalAnaliseElegibilidade());
        inscricaoProgramaParaMerge.setDescricaoJustificativaElegibilidade(inscricaoPrograma.getDescricaoJustificativaElegibilidade());
        inscricaoProgramaParaMerge.setMotivoAnaliseElegibilidade(inscricaoPrograma.getMotivoAnaliseElegibilidade());
        inscricaoProgramaParaMerge.setEstaEmFaseRecursoElegibilidade(inscricaoPrograma.getEstaEmFaseRecursoElegibilidade());
        inscricaoProgramaParaMerge.setFinalizadoRecursoElegibilidade(inscricaoPrograma.getFinalizadoRecursoElegibilidade());
        inscricaoProgramaParaMerge.setNumeroProcessoSEIRecursoElegibilidade(inscricaoPrograma.getNumeroProcessoSEIRecursoElegibilidade());

        sincronizarAnexos(inscricaoPrograma.getAnexos(), inscricaoProgramaParaMerge);
        sincronizarInscricaoElegibilidades(inscricaoPrograma.getProgramasCriterioElegibilidade(), inscricaoProgramaParaMerge);

        if (possuiHistorico) {
            HistoricoAnaliseElegibilidade historicoAnaliseElegibilidade = new HistoricoAnaliseElegibilidade();
            historicoAnaliseElegibilidade.setDataCadastro(LocalDateTime.now());
            historicoAnaliseElegibilidade.setInscricaoPrograma(inscricaoProgramaParaMerge);
            historicoAnaliseElegibilidade.setUsuarioCadastro(usuarioLogado);
            historicoAnaliseElegibilidade.setDescricaoJustificativa(inscricaoPrograma.getDescricaoJustificativaElegibilidade());
            historicoAnaliseElegibilidade.setMotivoAnalise(inscricaoPrograma.getMotivoAnaliseElegibilidade());
            historicoAnaliseElegibilidade.setResultadoFinalAnalise(inscricaoPrograma.getResultadoFinalAnaliseElegibilidade());
            em.persist(historicoAnaliseElegibilidade);
        }
        em.merge(inscricaoProgramaParaMerge);
        return inscricaoProgramaParaMerge;
    }

    private void sincronizarAnexos(List<InscricaoAnexoAnalise> anexos, InscricaoPrograma entityAtual) {
        // remover os excluidos
        List<InscricaoAnexoAnalise> anexosAux = new ArrayList<InscricaoAnexoAnalise>(entityAtual.getAnexos());
        for (InscricaoAnexoAnalise anexoAtual : anexosAux) {
            if (!anexos.contains(anexoAtual)) {
                entityAtual.getAnexos().remove(anexoAtual);
            }
        }

        // adiciona os novos
        for (InscricaoAnexoAnalise anexoNovo : anexos) {
            if (anexoNovo.getId() == null) {
                anexoNovo.setInscricaoPrograma(entityAtual);
                anexoNovo.setDataCadastro(LocalDateTime.now());
                anexoNovo.setTamanho(new Long(anexoNovo.getConteudo().length));
                entityAtual.getAnexos().add(anexoNovo);
            }
        }

    }

    private void sincronizarInscricaoElegibilidades(List<InscricaoProgramaCriterioElegibilidade> elegibilidadesDaInscricao, InscricaoPrograma inscricaoAtual) {

        List<InscricaoProgramaCriterioElegibilidade> listaElegibilidadesAtualParaAtualizar = new ArrayList<InscricaoProgramaCriterioElegibilidade>();
        List<InscricaoProgramaCriterioElegibilidade> listaElegibilidadesAdicionar = new ArrayList<InscricaoProgramaCriterioElegibilidade>();

        /*
         * seleciona apenas os Elegibilidades que foram mantidas na lista vinda
         * do service
         */
        for (InscricaoProgramaCriterioElegibilidade elegibilidadeAtual : inscricaoAtual.getProgramasCriterioElegibilidade()) {
            if (elegibilidadesDaInscricao.contains(elegibilidadeAtual)) {
                listaElegibilidadesAtualParaAtualizar.add(elegibilidadeAtual);
            }
        }

        /* remove a lista atual de Elegibilidades */
        inscricaoAtual.getProgramasCriterioElegibilidade().clear();

        /*
         * atualiza atributos nos elegibilidades vindos do service para
         * persistir
         */
        for (InscricaoProgramaCriterioElegibilidade elegibilidadeParaAtualizar : listaElegibilidadesAtualParaAtualizar) {
            for (InscricaoProgramaCriterioElegibilidade elegibilidadeDoPrograma : elegibilidadesDaInscricao) {
                if (elegibilidadeDoPrograma.getId() != null && elegibilidadeDoPrograma.getId().equals(elegibilidadeParaAtualizar.getId())) {
                    elegibilidadeParaAtualizar.setAceitaCriterioElegibilidade(elegibilidadeDoPrograma.getAceitaCriterioElegibilidade());
                    elegibilidadeParaAtualizar.setDescricaoMotivo(elegibilidadeDoPrograma.getDescricaoMotivo());
                    listaElegibilidadesAdicionar.add(elegibilidadeParaAtualizar);
                }
            }
        }
        // adiciona os Elegibilidades atualizados
        inscricaoAtual.getProgramasCriterioElegibilidade().addAll(listaElegibilidadesAdicionar);
    }

    public HistoricoAnaliseElegibilidade buscarUltimoHistoricoAnaliseElegibilidade(InscricaoPrograma inscricaoPrograma) {
        List<HistoricoAnaliseElegibilidade> listaHistorico = null;
        listaHistorico = buscarHistoricoAnaliseElegibilidade(inscricaoPrograma.getId());

        if (!listaHistorico.isEmpty()) {
            return listaHistorico.get(0);
        } else {
            return new HistoricoAnaliseElegibilidade();
        }
    }

}
