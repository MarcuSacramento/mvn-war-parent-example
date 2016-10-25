package br.gov.mj.side.web.dao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import br.gov.mj.apoio.entidades.PartidoPolitico;
import br.gov.mj.apoio.entidades.Uf;
import br.gov.mj.infra.negocio.persistencia.EnumOrder;
import br.gov.mj.side.entidades.AcaoOrcamentaria;
import br.gov.mj.side.entidades.BeneficiarioEmendaParlamentar;
import br.gov.mj.side.entidades.EmendaParlamentar;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.UtilDAO;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class AcaoOrcamentariaDAO {

    private static final String NOME_ACAO_ORCAMENTARIA = "nomeAcaoOrcamentaria";
    @Inject
    private EntityManager em;

    public List<EmendaParlamentar> buscarEmendaParlamentar(AcaoOrcamentaria acaoOrcamentaria) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<EmendaParlamentar> criteriaQuery = criteriaBuilder.createQuery(EmendaParlamentar.class);
        Root<EmendaParlamentar> root = criteriaQuery.from(EmendaParlamentar.class);
        List<Predicate> predicates = new ArrayList<>();
        if (acaoOrcamentaria.getId() != null) {
            predicates.add(criteriaBuilder.equal(root.get("acaoOrcamentaria").get("id"), acaoOrcamentaria.getId()));
        }
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<EmendaParlamentar> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public List<BeneficiarioEmendaParlamentar> buscarBeneficiarioEmendaParlamentar(EmendaParlamentar emendaParlamentar) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<BeneficiarioEmendaParlamentar> criteriaQuery = criteriaBuilder.createQuery(BeneficiarioEmendaParlamentar.class);
        Root<BeneficiarioEmendaParlamentar> root = criteriaQuery.from(BeneficiarioEmendaParlamentar.class);
        List<Predicate> predicates = new ArrayList<>();
        if (emendaParlamentar.getId() != null) {
            predicates.add(criteriaBuilder.equal(root.get("emendaParlamentar").get("id"), emendaParlamentar.getId()));
        }
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<BeneficiarioEmendaParlamentar> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<EmendaParlamentar> buscarEmendaParlamentarUtilizada() {
        String hql = "select distinct rfe.emendaParlamentar from RecursoFinanceiroEmenda rfe";
        Query q = em.createQuery(hql);
        return q.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<AcaoOrcamentaria> buscarAcaoOrcamentariaUtilizada() {
        String hql = "select distinct prf.acaoOrcamentaria from ProgramaRecursoFinanceiro prf";
        Query q = em.createQuery(hql);
        return q.getResultList();
    }

    public List<AcaoOrcamentaria> buscarPaginado(AcaoOrcamentaria acaoOrcamentaria, Boolean emendasVinculadas, EmendaParlamentar emenda, int first, int size, EnumOrder order, String propertyOrder) {

        List<AcaoOrcamentaria> lista1 = buscar(acaoOrcamentaria, emendasVinculadas, emenda, order, propertyOrder);

        // filtra paginado
        List<AcaoOrcamentaria> listaRetorno = new ArrayList<AcaoOrcamentaria>();

        if (!lista1.isEmpty()) {
            int inicio = first;
            int fim = first + size;

            if (fim > lista1.size()) {
                fim = lista1.size();
            }
            for (int i = inicio; i < fim; i++) {
                listaRetorno.add(lista1.get(i));
            }
        }

        return listaRetorno;

    }

    public Long contarPaginado(AcaoOrcamentaria acaoOrcamentaria, Boolean emendasVinculadas) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<AcaoOrcamentaria> root = criteriaQuery.from(AcaoOrcamentaria.class);

        criteriaQuery.select(criteriaBuilder.count(root));
        Predicate[] predicates = extractPredicates(acaoOrcamentaria, criteriaBuilder, root);
        criteriaQuery.where(predicates);

        TypedQuery<Long> query = em.createQuery(criteriaQuery);

        return retornarCountEmendasVinculadas(acaoOrcamentaria, emendasVinculadas, query);

    }

    public List<AcaoOrcamentaria> buscar(AcaoOrcamentaria acaoOrcamentaria) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<AcaoOrcamentaria> criteriaQuery = criteriaBuilder.createQuery(AcaoOrcamentaria.class);
        Root<AcaoOrcamentaria> root = criteriaQuery.from(AcaoOrcamentaria.class);

        Predicate[] predicates = extractPredicates(acaoOrcamentaria, criteriaBuilder, root);
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates);

        criteriaQuery.orderBy(criteriaBuilder.asc(root.get(NOME_ACAO_ORCAMENTARIA)));

        TypedQuery<AcaoOrcamentaria> query = em.createQuery(criteriaQuery);

        return query.getResultList();
    }

    public List<AcaoOrcamentaria> buscarDuplicado(AcaoOrcamentaria acaoOrcamentaria, boolean isAnoAcao, boolean isNumeroAcao, boolean isNomePPA, boolean isNumeroPPA) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<AcaoOrcamentaria> criteriaQuery = criteriaBuilder.createQuery(AcaoOrcamentaria.class);
        Root<AcaoOrcamentaria> root = criteriaQuery.from(AcaoOrcamentaria.class);

        Predicate[] predicates = extractPredicatesDuplicado(acaoOrcamentaria, criteriaBuilder, root, isAnoAcao, isNumeroAcao, isNomePPA, isNumeroPPA);
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates);

        criteriaQuery.orderBy(criteriaBuilder.asc(root.get(NOME_ACAO_ORCAMENTARIA)));

        TypedQuery<AcaoOrcamentaria> query = em.createQuery(criteriaQuery);

        return query.getResultList();
    }

    public List<AcaoOrcamentaria> buscar(AcaoOrcamentaria acaoOrcamentaria, Boolean emendasVinculadas, EmendaParlamentar emenda) {
        return buscar(acaoOrcamentaria, emendasVinculadas, emenda, EnumOrder.ASC, NOME_ACAO_ORCAMENTARIA);
    }

    public List<AcaoOrcamentaria> buscar(AcaoOrcamentaria acaoOrcamentaria, Boolean emendasVinculadas, EmendaParlamentar emenda, EnumOrder order, String propertyOrder) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<AcaoOrcamentaria> criteriaQuery = criteriaBuilder.createQuery(AcaoOrcamentaria.class);
        Root<AcaoOrcamentaria> root = criteriaQuery.from(AcaoOrcamentaria.class);

        Predicate[] predicates = extractPredicates(acaoOrcamentaria, criteriaBuilder, root);
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates);

        if (order.isAscOrder()) {
            criteriaQuery.orderBy(criteriaBuilder.asc(UtilDAO.extractObjectOrder(propertyOrder, root)));
        } else {
            criteriaQuery.orderBy(criteriaBuilder.desc(UtilDAO.extractObjectOrder(propertyOrder, root)));
        }

        TypedQuery<AcaoOrcamentaria> query = em.createQuery(criteriaQuery);

        return retornaListaEmendasVinculadas(emendasVinculadas, emenda, query);
    }

    private List<AcaoOrcamentaria> retornaListaEmendasVinculadas(Boolean emendasVinculadas, EmendaParlamentar emenda, TypedQuery<AcaoOrcamentaria> query) {
        if (emendasVinculadas == null) {
            return query.getResultList();
        } else {
            List<AcaoOrcamentaria> lista = query.getResultList();
            List<AcaoOrcamentaria> listaRetornoComEmendas = new ArrayList<AcaoOrcamentaria>();
            List<AcaoOrcamentaria> listaRetornoSemEmendas = new ArrayList<AcaoOrcamentaria>();

            for (AcaoOrcamentaria objAcaoOrcamentaria : lista) {
                if (!objAcaoOrcamentaria.getEmendasParlamentares().isEmpty()) {

                    if (emenda != null) {
                        boolean possuiEmenda = false;
                        for (EmendaParlamentar emendaParlamentar : objAcaoOrcamentaria.getEmendasParlamentares()) {
                            if (emendaParlamentar.getNumeroEmendaParlamantar().equals(emenda.getNumeroEmendaParlamantar())) {
                                possuiEmenda = true;
                            }
                        }
                        if (possuiEmenda) {
                            listaRetornoComEmendas.add(objAcaoOrcamentaria);
                        }
                    } else {
                        listaRetornoComEmendas.add(objAcaoOrcamentaria);
                    }

                } else {
                    listaRetornoSemEmendas.add(objAcaoOrcamentaria);
                }
            }

            if (emendasVinculadas) {
                return listaRetornoComEmendas;
            } else {
                return listaRetornoSemEmendas;
            }

        }
    }

    private Long retornarCountEmendasVinculadas(AcaoOrcamentaria acaoOrcamentaria, Boolean emendasVinculadas, TypedQuery<Long> query) {
        if (emendasVinculadas == null) {
            return query.getSingleResult();
        } else {
            List<AcaoOrcamentaria> lista = buscar(acaoOrcamentaria);
            List<AcaoOrcamentaria> listaRetornoComEmendas = new ArrayList<AcaoOrcamentaria>();
            List<AcaoOrcamentaria> listaRetornoSemEmendas = new ArrayList<AcaoOrcamentaria>();

            for (AcaoOrcamentaria objAcaoOrcamentaria : lista) {
                if (!objAcaoOrcamentaria.getEmendasParlamentares().isEmpty()) {
                    listaRetornoComEmendas.add(objAcaoOrcamentaria);
                } else {
                    listaRetornoSemEmendas.add(objAcaoOrcamentaria);
                }
            }
            if (emendasVinculadas) {
                return new Long(listaRetornoComEmendas.size());
            } else {
                return new Long(listaRetornoSemEmendas.size());
            }

        }
    }

    public AcaoOrcamentaria buscarPeloId(Long id) {
        return em.find(AcaoOrcamentaria.class, id);
    }

    public void excluir(AcaoOrcamentaria acaoOrcamentaria) {
        em.remove(acaoOrcamentaria);
    }

    public AcaoOrcamentaria incluir(AcaoOrcamentaria acaoOrcamentaria, String usuarioLogado) {

        acaoOrcamentaria.setDataCadastro(LocalDateTime.now());
        acaoOrcamentaria.setUsuarioCadastro(usuarioLogado);

        // Setar acaoOrcamentaria dentro de cada emenda para resolver a questão
        // da
        // referência bidirecional
        List<EmendaParlamentar> listaEmendasParlamentaresSetadosComEntidadeAcaoOrcamentaria = new ArrayList<EmendaParlamentar>();
        for (EmendaParlamentar emendaParlamentar : acaoOrcamentaria.getEmendasParlamentares()) {

            // atribuindo Uf e Partido no contexto transacional
            emendaParlamentar.setPartidoPolitico(em.find(PartidoPolitico.class, emendaParlamentar.getPartidoPolitico().getId()));
            emendaParlamentar.setUf(em.find(Uf.class, emendaParlamentar.getUf().getId()));

            // Setar emendaParlamentar dentro de cada beneficiario para resolver
            // a questão da referência bidirecional
            List<BeneficiarioEmendaParlamentar> listaBeneficiarioEmendaParlamentarSetadosComEmendaParlamentar = new ArrayList<BeneficiarioEmendaParlamentar>();
            for (BeneficiarioEmendaParlamentar beneficiarioEmendaParlamentar : emendaParlamentar.getBeneficiariosEmendaParlamentar()) {
                beneficiarioEmendaParlamentar.setEmendaParlamentar(emendaParlamentar);
                listaBeneficiarioEmendaParlamentarSetadosComEmendaParlamentar.add(beneficiarioEmendaParlamentar);
            }

            emendaParlamentar.setBeneficiariosEmendaParlamentar(listaBeneficiarioEmendaParlamentarSetadosComEmendaParlamentar);
            emendaParlamentar.setAcaoOrcamentaria(acaoOrcamentaria);
            listaEmendasParlamentaresSetadosComEntidadeAcaoOrcamentaria.add(emendaParlamentar);
        }
        acaoOrcamentaria.setEmendasParlamentares(listaEmendasParlamentaresSetadosComEntidadeAcaoOrcamentaria);

        em.persist(acaoOrcamentaria);
        return acaoOrcamentaria;
    }

    public AcaoOrcamentaria alterar(AcaoOrcamentaria acaoOrcamentaria, String usuarioLogado) {

        AcaoOrcamentaria acaoOrcamentariaParaMerge = em.find(AcaoOrcamentaria.class, acaoOrcamentaria.getId());

        acaoOrcamentariaParaMerge.setDataAlteracao(LocalDateTime.now());
        acaoOrcamentariaParaMerge.setUsuarioAlteracao(usuarioLogado);

        sincronizarEmendas(acaoOrcamentaria.getEmendasParlamentares(), acaoOrcamentariaParaMerge);

        acaoOrcamentariaParaMerge.setNumeroProgramaPPA(acaoOrcamentaria.getNumeroProgramaPPA());
        acaoOrcamentariaParaMerge.setNomeProgramaPPA(acaoOrcamentaria.getNomeProgramaPPA());
        acaoOrcamentariaParaMerge.setNumeroAcaoOrcamentaria(acaoOrcamentaria.getNumeroAcaoOrcamentaria());
        acaoOrcamentariaParaMerge.setNomeAcaoOrcamentaria(acaoOrcamentaria.getNomeAcaoOrcamentaria());
        acaoOrcamentariaParaMerge.setValorPrevisto(acaoOrcamentaria.getValorPrevisto());
        acaoOrcamentariaParaMerge.setAnoAcaoOrcamentaria(acaoOrcamentaria.getAnoAcaoOrcamentaria());
        return em.merge(acaoOrcamentariaParaMerge);

    }

    private Predicate[] extractPredicates(AcaoOrcamentaria acaoOrcamentaria, CriteriaBuilder criteriaBuilder, Root<?> root) {
        List<Predicate> predicates = new ArrayList<>();
        if (acaoOrcamentaria != null && acaoOrcamentaria.getId() != null) {
            predicates.add(criteriaBuilder.equal(root.get("id"), acaoOrcamentaria.getId()));
        }

        if (acaoOrcamentaria != null && acaoOrcamentaria.getAnoAcaoOrcamentaria() != null) {
            predicates.add(criteriaBuilder.equal(root.get("anoAcaoOrcamentaria"), acaoOrcamentaria.getAnoAcaoOrcamentaria()));
        }

        if (acaoOrcamentaria != null && StringUtils.isNotBlank(acaoOrcamentaria.getNomeAcaoOrcamentaria())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.function(Constants.FUNCAO_RETIRA_ACENTO,String.class,criteriaBuilder.lower(root.get(NOME_ACAO_ORCAMENTARIA))), "%" + UtilDAO.removerAcentos(acaoOrcamentaria.getNomeAcaoOrcamentaria().toLowerCase()) + "%"));
        }

        if (acaoOrcamentaria != null && StringUtils.isNotBlank(acaoOrcamentaria.getNumeroAcaoOrcamentaria())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.function(Constants.FUNCAO_RETIRA_ACENTO,String.class,criteriaBuilder.lower(root.get("numeroAcaoOrcamentaria"))), "%" + UtilDAO.removerAcentos(acaoOrcamentaria.getNumeroAcaoOrcamentaria().toLowerCase()) + "%"));
        }

        return predicates.toArray(new Predicate[] {});

    }

    private Predicate[] extractPredicatesDuplicado(AcaoOrcamentaria acaoOrcamentaria, CriteriaBuilder criteriaBuilder, Root<?> root, boolean isAnoAcao, boolean isNumeroAcao, boolean isNomePPA, boolean isNumeroPPA) {
        List<Predicate> predicates = new ArrayList<>();

        if (acaoOrcamentaria != null && acaoOrcamentaria.getId() != null) {
            predicates.add(criteriaBuilder.notEqual(root.get("id"), acaoOrcamentaria.getId()));
        }

        if (isAnoAcao) {
            predicates.add(criteriaBuilder.equal(root.get("anoAcaoOrcamentaria"), acaoOrcamentaria.getAnoAcaoOrcamentaria()));
        }

        if (isNumeroAcao) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.function(Constants.FUNCAO_RETIRA_ACENTO,String.class,criteriaBuilder.lower(root.get("numeroAcaoOrcamentaria"))), "%" + UtilDAO.removerAcentos(acaoOrcamentaria.getNumeroAcaoOrcamentaria().toLowerCase()) + "%"));
        }

        if (isNomePPA) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.function(Constants.FUNCAO_RETIRA_ACENTO,String.class,criteriaBuilder.lower(root.get("nomeProgramaPPA"))), "%" + UtilDAO.removerAcentos(acaoOrcamentaria.getNomeProgramaPPA().toLowerCase()) + "%"));
        }

        if (isNumeroPPA) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.function(Constants.FUNCAO_RETIRA_ACENTO,String.class,criteriaBuilder.lower(root.get("numeroProgramaPPA"))), "%" + UtilDAO.removerAcentos(acaoOrcamentaria.getNumeroProgramaPPA().toLowerCase()) + "%"));
        }

        return predicates.toArray(new Predicate[] {});

    }

    private void sincronizarEmendas(List<EmendaParlamentar> emendasParlamentares, AcaoOrcamentaria acaoOrcamentariaAtual) {

        List<EmendaParlamentar> listaEmendasAtualParaAtualizar = new ArrayList<EmendaParlamentar>();
        List<EmendaParlamentar> listaEmendasAdicionar = new ArrayList<EmendaParlamentar>();

        // seleciona apenas as emendas da acaoOrcamentaria que foram mantidas na
        // lista
        // vinda do service
        for (EmendaParlamentar emendaAtual : acaoOrcamentariaAtual.getEmendasParlamentares()) {
            if (emendasParlamentares.contains(emendaAtual)) {
                listaEmendasAtualParaAtualizar.add(emendaAtual);
            }

        }

        // remove a lista atual de Emendas Parlamentares
        acaoOrcamentariaAtual.getEmendasParlamentares().clear();

        // adiciona os novos na lista de Emendas
        for (EmendaParlamentar emendaNovo : emendasParlamentares) {
            if (emendaNovo.getId() == null) {
                // atender referencia bidirecional
                emendaNovo.setAcaoOrcamentaria(acaoOrcamentariaAtual);

                List<BeneficiarioEmendaParlamentar> listaBeneficiarioEmendaParlamentar = new ArrayList<BeneficiarioEmendaParlamentar>();
                for (BeneficiarioEmendaParlamentar beneficiario : emendaNovo.getBeneficiariosEmendaParlamentar()) {
                    // atender referencia bidirecional
                    beneficiario.setEmendaParlamentar(emendaNovo);
                    listaBeneficiarioEmendaParlamentar.add(beneficiario);
                }
                emendaNovo.setBeneficiariosEmendaParlamentar(listaBeneficiarioEmendaParlamentar);
                acaoOrcamentariaAtual.getEmendasParlamentares().add(emendaNovo);

            }
        }

        // atualiza atributos nas emendas vindos do service para persistir
        for (EmendaParlamentar emendaParaAtualizar : listaEmendasAtualParaAtualizar) {
            for (EmendaParlamentar emendaParlamentar : emendasParlamentares) {
                if (emendaParlamentar.getId() != null && emendaParlamentar.getId().equals(emendaParaAtualizar.getId())) {

                    sincronizarBeneficiarios(emendaParlamentar.getBeneficiariosEmendaParlamentar(), emendaParaAtualizar);
                    emendaParaAtualizar.setNumeroEmendaParlamantar(emendaParlamentar.getNumeroEmendaParlamantar());
                    emendaParaAtualizar.setNomeEmendaParlamentar(emendaParlamentar.getNomeEmendaParlamentar());
                    emendaParaAtualizar.setPartidoPolitico(em.find(PartidoPolitico.class, emendaParlamentar.getPartidoPolitico().getId()));
                    emendaParaAtualizar.setUf(em.find(Uf.class, emendaParlamentar.getUf().getId()));
                    emendaParaAtualizar.setNomeParlamentar(emendaParlamentar.getNomeParlamentar());
                    emendaParaAtualizar.setNomeCargoParlamentar(emendaParlamentar.getNomeCargoParlamentar());
                    emendaParaAtualizar.setPossuiLiberacao(emendaParlamentar.getPossuiLiberacao());
                    emendaParaAtualizar.setValorPrevisto(emendaParlamentar.getValorPrevisto());
                    emendaParaAtualizar.setTipoEmenda(emendaParlamentar.getTipoEmenda());

                    listaEmendasAdicionar.add(emendaParaAtualizar);
                }
            }
        }

        // adicona as emendas atualizadas
        acaoOrcamentariaAtual.getEmendasParlamentares().addAll(listaEmendasAdicionar);
    }

    private void sincronizarBeneficiarios(List<BeneficiarioEmendaParlamentar> beneficiariosEmendaParlamentar, EmendaParlamentar emendaAtual) {

        List<BeneficiarioEmendaParlamentar> listaBeneficiariosAtualParaAtualizar = new ArrayList<BeneficiarioEmendaParlamentar>();
        List<BeneficiarioEmendaParlamentar> listaBeneficiariosAdicionar = new ArrayList<BeneficiarioEmendaParlamentar>();

        // seleciona apenas os beneficiarios da emenda que foram mantidas na
        // lista vinda do service
        for (BeneficiarioEmendaParlamentar beneficiarioAtual : emendaAtual.getBeneficiariosEmendaParlamentar()) {
            if (beneficiariosEmendaParlamentar.contains(beneficiarioAtual)) {
                listaBeneficiariosAtualParaAtualizar.add(beneficiarioAtual);
            }

        }

        // remove a lista atual de Beneficiarios
        emendaAtual.getBeneficiariosEmendaParlamentar().clear();

        // adiciona os novos na lista de beneficiarios
        for (BeneficiarioEmendaParlamentar beneficiarioNovo : beneficiariosEmendaParlamentar) {
            if (beneficiarioNovo.getId() == null) {
                // atender referencia bidirecional
                beneficiarioNovo.setEmendaParlamentar(emendaAtual);
                emendaAtual.getBeneficiariosEmendaParlamentar().add(beneficiarioNovo);
            }
        }

        // atualiza atributos nos beneficiarios vindos do service para persistir
        for (BeneficiarioEmendaParlamentar beneficiarioEmendaParlamentarParaAtualizar : listaBeneficiariosAtualParaAtualizar) {
            for (BeneficiarioEmendaParlamentar beneficiarioEmendaParlamentar : beneficiariosEmendaParlamentar) {
                if (beneficiarioEmendaParlamentar.getId() != null && beneficiarioEmendaParlamentar.getId().equals(beneficiarioEmendaParlamentarParaAtualizar.getId())) {
                    beneficiarioEmendaParlamentarParaAtualizar.setNomeBeneficiario(beneficiarioEmendaParlamentar.getNomeBeneficiario());
                    beneficiarioEmendaParlamentarParaAtualizar.setNumeroCnpjBeneficiario(beneficiarioEmendaParlamentar.getNumeroCnpjBeneficiario());
                    listaBeneficiariosAdicionar.add(beneficiarioEmendaParlamentarParaAtualizar);
                }
            }
        }

        // adicona as emendas atualizadas
        emendaAtual.getBeneficiariosEmendaParlamentar().addAll(listaBeneficiariosAdicionar);
    }

}
