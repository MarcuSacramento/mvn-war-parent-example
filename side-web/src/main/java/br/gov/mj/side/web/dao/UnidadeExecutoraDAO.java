package br.gov.mj.side.web.dao;

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

import org.apache.commons.lang3.StringUtils;

import br.gov.mj.apoio.entidades.Orgao;
import br.gov.mj.apoio.entidades.UnidadeExecutora;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.UtilDAO;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class UnidadeExecutoraDAO {

    private static final String NOME_UNIDADE_EXECUTORA = "nomeUnidadeExecutora";
    @Inject
    private EntityManager em;

    public List<UnidadeExecutora> buscar(UnidadeExecutora unidadeExecutora) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<UnidadeExecutora> criteriaQuery = criteriaBuilder.createQuery(UnidadeExecutora.class);
        Root<UnidadeExecutora> root = criteriaQuery.from(UnidadeExecutora.class);

        Predicate[] predicates = extractPredicates(unidadeExecutora, criteriaBuilder, root);
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates);

        criteriaQuery.orderBy(criteriaBuilder.asc(root.get(NOME_UNIDADE_EXECUTORA)));

        TypedQuery<UnidadeExecutora> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public List<UnidadeExecutora> buscarPeloOrgaoId(Long id) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<UnidadeExecutora> criteriaQuery = criteriaBuilder.createQuery(UnidadeExecutora.class);
        Root<UnidadeExecutora> root = criteriaQuery.from(UnidadeExecutora.class);

        Orgao org = new Orgao();
        org.setId(id);
        UnidadeExecutora uex = new UnidadeExecutora();
        uex.setOrgao(org);

        Predicate[] predicates = extractPredicates(uex, criteriaBuilder, root);
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates);

        criteriaQuery.orderBy(criteriaBuilder.asc(root.get(NOME_UNIDADE_EXECUTORA)));

        TypedQuery<UnidadeExecutora> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    private Predicate[] extractPredicates(UnidadeExecutora unidadeExecutora, CriteriaBuilder criteriaBuilder, Root<?> root) {
        List<Predicate> predicates = new ArrayList<>();
        if (unidadeExecutora.getId() != null) {
            predicates.add(criteriaBuilder.equal(root.get("id"), unidadeExecutora.getId()));
        }
        if (StringUtils.isNotBlank(unidadeExecutora.getNomeUnidadeExecutora())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.function(Constants.FUNCAO_RETIRA_ACENTO,String.class,criteriaBuilder.lower(root.get(NOME_UNIDADE_EXECUTORA))), "%" + UtilDAO.removerAcentos(unidadeExecutora.getNomeUnidadeExecutora().toLowerCase()) + "%"));
        }

        if (unidadeExecutora.getOrgao() != null && unidadeExecutora.getOrgao().getId() != null) {
            predicates.add(criteriaBuilder.equal(root.get("orgao").get("id"), unidadeExecutora.getOrgao().getId()));
        }

        if (unidadeExecutora.getOrgao() != null && StringUtils.isNotBlank(unidadeExecutora.getOrgao().getNomeOrgao())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.function(Constants.FUNCAO_RETIRA_ACENTO,String.class,criteriaBuilder.lower(root.get("orgao").get("nomeOrgao"))), "%" + UtilDAO.removerAcentos(unidadeExecutora.getOrgao().getNomeOrgao().toLowerCase()) + "%"));
        }

        return predicates.toArray(new Predicate[] {});

    }

}
