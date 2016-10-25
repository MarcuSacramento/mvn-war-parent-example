package br.gov.mj.side.web.dao;

import java.math.BigDecimal;
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

import br.gov.mj.side.entidades.AcaoOrcamentaria;
import br.gov.mj.side.entidades.EmendaParlamentar;
import br.gov.mj.side.entidades.enums.EnumStatusPrograma;
import br.gov.mj.side.entidades.programa.ProgramaRecursoFinanceiro;
import br.gov.mj.side.entidades.programa.RecursoFinanceiroEmenda;
import br.gov.mj.side.web.dto.EmendaComSaldoDto;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class RecursoFinanceiroDAO {

    @Inject
    private EntityManager em;

    private List<ProgramaRecursoFinanceiro> buscarProgramaRecursoFinanceiro(Long idAcaoOrcamentaria) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<ProgramaRecursoFinanceiro> criteriaQuery = criteriaBuilder.createQuery(ProgramaRecursoFinanceiro.class);
        Root<ProgramaRecursoFinanceiro> root = criteriaQuery.from(ProgramaRecursoFinanceiro.class);

        Predicate[] predicates = extractPredicatesDeProgramaRecursoFinanceiro(idAcaoOrcamentaria, criteriaBuilder, root);
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates);
        TypedQuery<ProgramaRecursoFinanceiro> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    private List<RecursoFinanceiroEmenda> buscarRecursoFinanceiroEmenda(Long idEmendaParlamentar) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<RecursoFinanceiroEmenda> criteriaQuery = criteriaBuilder.createQuery(RecursoFinanceiroEmenda.class);
        Root<RecursoFinanceiroEmenda> root = criteriaQuery.from(RecursoFinanceiroEmenda.class);
        Predicate[] predicates = extractPredicatesDeRecursoFinanceiroEmenda(idEmendaParlamentar, criteriaBuilder, root);
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates);
        TypedQuery<RecursoFinanceiroEmenda> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public BigDecimal buscarSaldoAcaoOrcamentaria(Long idAcaoOrcamentaria) {

        BigDecimal total = BigDecimal.ZERO;

        AcaoOrcamentaria acaoOrcamentaria = em.find(AcaoOrcamentaria.class, idAcaoOrcamentaria);
        total = total.add(acaoOrcamentaria.getValorPrevisto());

        List<ProgramaRecursoFinanceiro> lista = buscarProgramaRecursoFinanceiro(idAcaoOrcamentaria);

        for (ProgramaRecursoFinanceiro programaRecursoFinanceiro : lista) {
            total = total.subtract(programaRecursoFinanceiro.getValorUtilizar());

        }
        return total;

    }

    private BigDecimal buscarSaldoEmendaParlamentar(Long idEmendaParlamentar) {

        BigDecimal total = BigDecimal.ZERO;

        List<RecursoFinanceiroEmenda> lista = buscarRecursoFinanceiroEmenda(idEmendaParlamentar);

        for (RecursoFinanceiroEmenda recursoFinanceiroEmenda : lista) {
            total = total.add(recursoFinanceiroEmenda.getValorUtilizar());

        }
        return total;

    }

    public List<EmendaComSaldoDto> buscarEmendasComSaldo(Long idAcaoOrcamentaria) {

        List<EmendaComSaldoDto> listaRetorno = new ArrayList<EmendaComSaldoDto>();

        List<EmendaParlamentar> listaEmendasTotal = em.find(AcaoOrcamentaria.class, idAcaoOrcamentaria).getEmendasParlamentares();

        for (EmendaParlamentar emendaParlamentar : listaEmendasTotal) {

            EmendaComSaldoDto emendaComSaldoDto = new EmendaComSaldoDto(emendaParlamentar, emendaParlamentar.getValorPrevisto().subtract(buscarSaldoEmendaParlamentar(emendaParlamentar.getId())));
            listaRetorno.add(emendaComSaldoDto);
        }

        return listaRetorno;

    }

    private Predicate[] extractPredicatesDeProgramaRecursoFinanceiro(Long idAcaoOrcamentaria, CriteriaBuilder criteriaBuilder, Root<?> root) {
        List<Predicate> predicates = new ArrayList<>();
        if (idAcaoOrcamentaria != null) {
            predicates.add(criteriaBuilder.equal(root.get("acaoOrcamentaria").get("id"), idAcaoOrcamentaria));

            List<EnumStatusPrograma> lista = new ArrayList<EnumStatusPrograma>();
            lista.add(EnumStatusPrograma.ABERTO_REC_PROPOSTAS);
            lista.add(EnumStatusPrograma.EM_ANALISE);
            lista.add(EnumStatusPrograma.ABERTO_REC_LOC_ENTREGA);
            lista.add(EnumStatusPrograma.ABERTO_PLANEJAMENTO_LICITACAO);
            lista.add(EnumStatusPrograma.ABERTO_GERACAO_CONTRATO);
            lista.add(EnumStatusPrograma.PUBLICADO);
            lista.add(EnumStatusPrograma.EM_EXECUCAO);
            lista.add(EnumStatusPrograma.ACOMPANHAMENTO);
            predicates.add(root.get("programa").get("statusPrograma").in(lista));
        }

        return predicates.toArray(new Predicate[] {});
    }

    private Predicate[] extractPredicatesDeRecursoFinanceiroEmenda(Long idEmendaParlamentar, CriteriaBuilder criteriaBuilder, Root<?> root) {
        List<Predicate> predicates = new ArrayList<>();
        if (idEmendaParlamentar != null) {
            predicates.add(criteriaBuilder.equal(root.get("emendaParlamentar").get("id"), idEmendaParlamentar));

            List<EnumStatusPrograma> lista = new ArrayList<EnumStatusPrograma>();
            lista.add(EnumStatusPrograma.ABERTO_REC_PROPOSTAS);
            lista.add(EnumStatusPrograma.EM_ANALISE);
            lista.add(EnumStatusPrograma.ABERTO_REC_LOC_ENTREGA);
            lista.add(EnumStatusPrograma.ABERTO_PLANEJAMENTO_LICITACAO);
            lista.add(EnumStatusPrograma.ABERTO_GERACAO_CONTRATO);
            lista.add(EnumStatusPrograma.PUBLICADO);
            lista.add(EnumStatusPrograma.EM_EXECUCAO);
            lista.add(EnumStatusPrograma.ACOMPANHAMENTO);
            
            predicates.add(root.get("recursoFinanceiro").get("programa").get("statusPrograma").in(lista));
        }

        return predicates.toArray(new Predicate[] {});
    }

}
