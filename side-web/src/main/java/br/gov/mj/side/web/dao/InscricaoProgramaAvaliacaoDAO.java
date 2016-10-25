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
import br.gov.mj.side.entidades.programa.inscricao.InscricaoProgramaCriterioAvaliacao;
import br.gov.mj.side.entidades.programa.inscricao.analise.HistoricoAnaliseAvaliacao;
import br.gov.mj.side.entidades.programa.inscricao.analise.InscricaoAnexoAnalise;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class InscricaoProgramaAvaliacaoDAO {

    private static final String INSCRICAO_PROGRAMA = "inscricaoPrograma";

    @Inject
    private EntityManager em;

    public List<HistoricoAnaliseAvaliacao> buscarHistoricoAnaliseAvaliacao(Long id) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<HistoricoAnaliseAvaliacao> criteriaQuery = criteriaBuilder.createQuery(HistoricoAnaliseAvaliacao.class);
        Root<HistoricoAnaliseAvaliacao> root = criteriaQuery.from(HistoricoAnaliseAvaliacao.class);
        List<Predicate> predicates = new ArrayList<>();
        if (id != null) {
            predicates.add(criteriaBuilder.equal(root.get(INSCRICAO_PROGRAMA).get("id"), id));
        }
        criteriaQuery.orderBy(criteriaBuilder.desc(root.get("id")));
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<HistoricoAnaliseAvaliacao> query = em.createQuery(criteriaQuery);
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
        inscricaoProgramaParaMerge.setPontuacaoFinal(inscricaoPrograma.getPontuacaoFinal());
        inscricaoProgramaParaMerge.setDescricaoJustificativaAvaliacao(inscricaoPrograma.getDescricaoJustificativaAvaliacao());
        inscricaoProgramaParaMerge.setMotivoAnaliseAvaliacao(inscricaoPrograma.getMotivoAnaliseAvaliacao());
        inscricaoProgramaParaMerge.setEstaEmFaseRecursoAvaliacao(inscricaoPrograma.getEstaEmFaseRecursoAvaliacao());
        inscricaoProgramaParaMerge.setFinalizadoRecursoAvaliacao(inscricaoPrograma.getFinalizadoRecursoAvaliacao());
        inscricaoProgramaParaMerge.setNumeroProcessoSEIRecursoAvaliacao(inscricaoPrograma.getNumeroProcessoSEIRecursoAvaliacao());

        sincronizarAnexos(inscricaoPrograma.getAnexos(), inscricaoProgramaParaMerge);
        sincronizarInscricaoAvaliacoes(inscricaoPrograma.getProgramasCriterioAvaliacao(), inscricaoProgramaParaMerge);

        if (possuiHistorico) {
            HistoricoAnaliseAvaliacao historicoAnaliseAvaliacao = new HistoricoAnaliseAvaliacao();
            historicoAnaliseAvaliacao.setDataCadastro(LocalDateTime.now());
            historicoAnaliseAvaliacao.setInscricaoPrograma(inscricaoProgramaParaMerge);
            historicoAnaliseAvaliacao.setUsuarioCadastro(usuarioLogado);
            historicoAnaliseAvaliacao.setDescricaoJustificativa(inscricaoPrograma.getDescricaoJustificativaAvaliacao());
            historicoAnaliseAvaliacao.setMotivoAnalise(inscricaoPrograma.getMotivoAnaliseAvaliacao());
            historicoAnaliseAvaliacao.setPontuacaoFinal(inscricaoPrograma.getPontuacaoFinal());
            em.persist(historicoAnaliseAvaliacao);
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

    private void sincronizarInscricaoAvaliacoes(List<InscricaoProgramaCriterioAvaliacao> avaliacoesDaInscricao, InscricaoPrograma inscricaoAtual) {

        List<InscricaoProgramaCriterioAvaliacao> listaAvaliacoesAtualParaAtualizar = new ArrayList<InscricaoProgramaCriterioAvaliacao>();
        List<InscricaoProgramaCriterioAvaliacao> listaAvaliacoesAdicionar = new ArrayList<InscricaoProgramaCriterioAvaliacao>();

        /*
         * seleciona apenas os Avaliacoes que foram mantidas na lista vinda do
         * service
         */
        for (InscricaoProgramaCriterioAvaliacao avaliacaoAtual : inscricaoAtual.getProgramasCriterioAvaliacao()) {
            if (avaliacoesDaInscricao.contains(avaliacaoAtual)) {
                listaAvaliacoesAtualParaAtualizar.add(avaliacaoAtual);
            }
        }

        /* remove a lista atual de Avaliacoes */
        inscricaoAtual.getProgramasCriterioAvaliacao().clear();

        /* atualiza atributos nos Avaliacoes vindos do service para persistir */
        for (InscricaoProgramaCriterioAvaliacao avaliacaoParaAtualizar : listaAvaliacoesAtualParaAtualizar) {
            for (InscricaoProgramaCriterioAvaliacao avaliacaoDoPrograma : avaliacoesDaInscricao) {
                if (avaliacaoDoPrograma.getId() != null && avaliacaoDoPrograma.getId().equals(avaliacaoParaAtualizar.getId())) {
                    avaliacaoParaAtualizar.setAceitaCriterioAvaliacao(avaliacaoDoPrograma.getAceitaCriterioAvaliacao());
                    avaliacaoParaAtualizar.setDescricaoMotivo(avaliacaoDoPrograma.getDescricaoMotivo());
                    avaliacaoParaAtualizar.setNotaCriterio(avaliacaoDoPrograma.getNotaCriterio());
                    listaAvaliacoesAdicionar.add(avaliacaoParaAtualizar);
                }
            }
        }
        // adiciona os Avaliacoes atualizados
        inscricaoAtual.getProgramasCriterioAvaliacao().addAll(listaAvaliacoesAdicionar);
    }

    public HistoricoAnaliseAvaliacao buscarUltimoHistoricoAnaliseAvaliacao(InscricaoPrograma inscricaoPrograma) {
        List<HistoricoAnaliseAvaliacao> listaHistorico = null;
        listaHistorico = buscarHistoricoAnaliseAvaliacao(inscricaoPrograma.getId());

        if (!listaHistorico.isEmpty()) {
            return listaHistorico.get(0);
        } else {
            return new HistoricoAnaliseAvaliacao();
        }
    }

}
