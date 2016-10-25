package br.gov.mj.side.web.dao;

import java.util.ArrayList;
import java.util.Collections;
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

import br.gov.mj.apoio.entidades.Elemento;
import br.gov.mj.side.entidades.Bem;
import br.gov.mj.side.entidades.Kit;
import br.gov.mj.side.entidades.KitBem;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.UtilDAO;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class KitDAO {

    @Inject
    private EntityManager em;

    public List<Kit> buscar(Kit kit) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Kit> criteriaQuery = criteriaBuilder.createQuery(Kit.class);
        Root<Kit> root = criteriaQuery.from(Kit.class);

        Predicate[] predicates = extractPredicates(kit, criteriaBuilder, root);
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates);

        criteriaQuery.orderBy(criteriaBuilder.asc(root.get("nomeKit")));

        TypedQuery<Kit> query = em.createQuery(criteriaQuery);

        return query.getResultList();
    }

    public List<Kit> buscarPaginado(Kit kit, Bem bem, Elemento elemento, int first, int size, String coluna, int order) {

        // busca itens de kit
        List<Kit> lista1 = pesquisar(kit, bem, elemento);
        if (coluna != null) {
            lista1 = ordenarKit(lista1, coluna, order);
        }

        // filtra paginado
        List<Kit> listaRetorno = new ArrayList<Kit>();

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

    public List<Kit> ordenarKit(List<Kit> lista, String coluna, int order) {
        Collections.sort(lista, Kit.getComparator(order, coluna));
        return lista;
    }

    public List<Kit> pesquisar(Kit kit, Bem bem, Elemento elemento) {

        // busca itens de kit
        List<Kit> lista1 = buscar(kit);
        List<Kit> lista2 = new ArrayList<Kit>();
        List<Kit> lista3 = new ArrayList<Kit>();

        // filtra lista de kits pelo bem
        if (bem != null && bem.getId() != null) {
            for (Kit objKit : lista1) {
                for (KitBem kitBem : objKit.getKitsBens()) {
                    if (kitBem.getBem().getId().equals(bem.getId())) {
                        lista2.add(objKit);
                    }
                }

            }
        } else {
            lista2.addAll(lista1);
        }

        // filtra lista de kits e bens filtrados pelo elemento
        if (elemento != null && elemento.getId() != null) {
            boolean possuiElemento = false;
            for (Kit objKit : lista2) {
                for (KitBem kitBem : objKit.getKitsBens()) {
                    if (kitBem.getBem().getSubElemento().getElemento().getId().equals(elemento.getId())) {
                        possuiElemento = true;
                        break;
                    }
                }
                if (possuiElemento) {
                    lista3.add(objKit);
                    possuiElemento = false;
                }
            }
        } else {
            lista3.addAll(lista2);
        }

        return lista3;
    }

    public Kit buscarPeloId(Long id) {
        return em.find(Kit.class, id);
    }

    public void excluir(Kit kit) {
        em.remove(kit);
    }

    public Kit incluir(Kit kit) {

        // Setar kit dentro de cada kitBem para resolver a questão da referência
        // bidirecional
        List<KitBem> listaKitBensSetadosComEntidadeKit = new ArrayList<KitBem>();
        for (KitBem kitBem : kit.getKitsBens()) {
            kitBem.setKit(kit);
            listaKitBensSetadosComEntidadeKit.add(kitBem);
        }
        kit.setKitsBens(listaKitBensSetadosComEntidadeKit);

        em.persist(kit);
        return kit;
    }

    public Kit alterar(Kit kit) {

        Kit kitParaMerge = em.find(Kit.class, kit.getId());

        List<KitBem> listaBensDoKitAtual = new ArrayList<KitBem>(kitParaMerge.getKitsBens());
        sincronizarBensDoKit(kit.getKitsBens(), listaBensDoKitAtual, kitParaMerge);

        kitParaMerge.setDescricaoKit(kit.getDescricaoKit());
        kitParaMerge.setNomeKit(kit.getNomeKit());

        return em.merge(kitParaMerge);

    }

    private Predicate[] extractPredicates(Kit kit, CriteriaBuilder criteriaBuilder, Root<?> root) {
        List<Predicate> predicates = new ArrayList<>();
        if (kit != null && kit.getId() != null) {
            predicates.add(criteriaBuilder.equal(root.get("id"), kit.getId()));
        }
        if (kit != null && StringUtils.isNotBlank(kit.getNomeKit())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.function(Constants.FUNCAO_RETIRA_ACENTO,String.class,criteriaBuilder.lower(root.get("nomeKit"))), "%" + UtilDAO.removerAcentos(kit.getNomeKit().toLowerCase()) + "%"));
        }

        return predicates.toArray(new Predicate[] {});

    }

    private void sincronizarBensDoKit(List<KitBem> bensDoKit, List<KitBem> listaBensDoKitAtual, Kit kitAtual) {

        List<KitBem> listaBensDoKitAtualParaAtualizar = new ArrayList<KitBem>();
        List<KitBem> listaBensDoKitAdicionar = new ArrayList<KitBem>();

        // seleciona apenas os bens do kit que foram mantidos na lista vinda do
        // service
        for (KitBem bemDoKitAtual : listaBensDoKitAtual) {
            if (bensDoKit.contains(bemDoKitAtual)) {
                listaBensDoKitAtualParaAtualizar.add(bemDoKitAtual);
            }

        }

        // remove a lista atual de bens do Kit
        kitAtual.getKitsBens().clear();

        // adiciona os novos na lista de Bens do Kit
        for (KitBem bensDoKitNovo : bensDoKit) {
            if (bensDoKitNovo.getId() == null) {
                bensDoKitNovo.setKit(kitAtual);
                kitAtual.getKitsBens().add(bensDoKitNovo);
            }
        }

        // atualiza quantidade no bem do kit vindo do service para persistir
        for (KitBem kitBemParaAtualizar : listaBensDoKitAtualParaAtualizar) {
            for (KitBem kitBem : bensDoKit) {
                if (kitBem.getId() != null && kitBem.getId().equals(kitBemParaAtualizar.getId())) {
                    kitBemParaAtualizar.setQuantidade(kitBem.getQuantidade());
                    listaBensDoKitAdicionar.add(kitBemParaAtualizar);
                }
            }
        }

        // adicona os bens do Kit atualizados com a quantidade
        kitAtual.getKitsBens().addAll(listaBensDoKitAdicionar);
    }

}
