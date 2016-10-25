package br.gov.mj.side.web.dao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import br.gov.mj.apoio.entidades.Municipio;
import br.gov.mj.apoio.entidades.PartidoPolitico;
import br.gov.mj.apoio.entidades.SubFuncao;
import br.gov.mj.apoio.entidades.Uf;
import br.gov.mj.apoio.entidades.UnidadeExecutora;
import br.gov.mj.infra.negocio.persistencia.EnumOrder;
import br.gov.mj.side.entidades.AcaoOrcamentaria;
import br.gov.mj.side.entidades.Bem;
import br.gov.mj.side.entidades.BeneficiarioEmendaParlamentar;
import br.gov.mj.side.entidades.EmendaParlamentar;
import br.gov.mj.side.entidades.Kit;
import br.gov.mj.side.entidades.KitBem;
import br.gov.mj.side.entidades.enums.EnumStatusPrograma;
import br.gov.mj.side.entidades.enums.EnumTipoPrograma;
import br.gov.mj.side.entidades.programa.PotencialBeneficiarioMunicipio;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.ProgramaAnexo;
import br.gov.mj.side.entidades.programa.ProgramaBem;
import br.gov.mj.side.entidades.programa.ProgramaCriterioAcompanhamento;
import br.gov.mj.side.entidades.programa.ProgramaCriterioAvaliacao;
import br.gov.mj.side.entidades.programa.ProgramaCriterioAvaliacaoOpcaoResposta;
import br.gov.mj.side.entidades.programa.ProgramaCriterioElegibilidade;
import br.gov.mj.side.entidades.programa.ProgramaHistoricoPublicizacao;
import br.gov.mj.side.entidades.programa.ProgramaKit;
import br.gov.mj.side.entidades.programa.ProgramaPotencialBeneficiarioUf;
import br.gov.mj.side.entidades.programa.ProgramaRecursoFinanceiro;
import br.gov.mj.side.entidades.programa.RecursoFinanceiroEmenda;
import br.gov.mj.side.entidades.programa.licitacao.AgrupamentoLicitacao;
import br.gov.mj.side.entidades.programa.licitacao.contrato.Contrato;
import br.gov.mj.side.web.dto.ProgramaComboDto;
import br.gov.mj.side.web.dto.ProgramaPesquisaDto;
import br.gov.mj.side.web.dto.enderecoLicitacao.BemUfLicitacaoDto;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.UtilDAO;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class ProgramaDAO {

    private static final String PROGRAMA = "programa";
    private static final String NOME_PROGRAMA = "nomePrograma";
    @Inject
    private EntityManager em;

    @Inject
    PublicizacaoDAO publicizacaoDAO;

    public List<ProgramaComboDto> buscarProgramas() {

        List<ProgramaComboDto> listaRetorno = new ArrayList<ProgramaComboDto>();
        List<?> listaProgramas = em.createQuery("select p.id, p.nomePrograma from Programa p order by p.nomePrograma asc").getResultList();

        for (Object object : listaProgramas) {
            Object[] o = (Object[]) object;
            listaRetorno.add(new ProgramaComboDto((Long) o[0], (String) o[1]));
        }

        return listaRetorno;
    }

    public List<AgrupamentoLicitacao> buscarProgramaAgrupamentoLicitacao(Long id) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<AgrupamentoLicitacao> criteriaQuery = criteriaBuilder.createQuery(AgrupamentoLicitacao.class);
        Root<AgrupamentoLicitacao> root = criteriaQuery.from(AgrupamentoLicitacao.class);
        List<Predicate> predicates = new ArrayList<>();
        if (id != null) {
            predicates.add(criteriaBuilder.equal(root.get("licitacaoPrograma").get("programa").get("id"), id));
        }
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<AgrupamentoLicitacao> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public List<ProgramaCriterioElegibilidade> buscarProgramaCriterioElegibilidade(Long id) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<ProgramaCriterioElegibilidade> criteriaQuery = criteriaBuilder.createQuery(ProgramaCriterioElegibilidade.class);
        Root<ProgramaCriterioElegibilidade> root = criteriaQuery.from(ProgramaCriterioElegibilidade.class);
        List<Predicate> predicates = new ArrayList<>();
        if (id != null) {
            predicates.add(criteriaBuilder.equal(root.get(PROGRAMA).get("id"), id));
        }
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<ProgramaCriterioElegibilidade> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public List<ProgramaCriterioAcompanhamento> buscarProgramaCriterioAcompanhamento(Long id) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<ProgramaCriterioAcompanhamento> criteriaQuery = criteriaBuilder.createQuery(ProgramaCriterioAcompanhamento.class);
        Root<ProgramaCriterioAcompanhamento> root = criteriaQuery.from(ProgramaCriterioAcompanhamento.class);
        List<Predicate> predicates = new ArrayList<>();
        if (id != null) {
            predicates.add(criteriaBuilder.equal(root.get(PROGRAMA).get("id"), id));
        }
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<ProgramaCriterioAcompanhamento> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public List<ProgramaKit> buscarProgramakit(Long id) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<ProgramaKit> criteriaQuery = criteriaBuilder.createQuery(ProgramaKit.class);
        Root<ProgramaKit> root = criteriaQuery.from(ProgramaKit.class);
        List<Predicate> predicates = new ArrayList<>();
        if (id != null) {
            predicates.add(criteriaBuilder.equal(root.get(PROGRAMA).get("id"), id));
        }
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<ProgramaKit> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public List<ProgramaBem> buscarProgramaBem(Long id) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<ProgramaBem> criteriaQuery = criteriaBuilder.createQuery(ProgramaBem.class);
        Root<ProgramaBem> root = criteriaQuery.from(ProgramaBem.class);
        List<Predicate> predicates = new ArrayList<>();
        if (id != null) {
            predicates.add(criteriaBuilder.equal(root.get(PROGRAMA).get("id"), id));
        }
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<ProgramaBem> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public List<ProgramaPotencialBeneficiarioUf> buscarProgramaPotencialBeneficiarioUf(Long id) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<ProgramaPotencialBeneficiarioUf> criteriaQuery = criteriaBuilder.createQuery(ProgramaPotencialBeneficiarioUf.class);
        Root<ProgramaPotencialBeneficiarioUf> root = criteriaQuery.from(ProgramaPotencialBeneficiarioUf.class);
        List<Predicate> predicates = new ArrayList<>();
        if (id != null) {
            predicates.add(criteriaBuilder.equal(root.get(PROGRAMA).get("id"), id));
        }
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<ProgramaPotencialBeneficiarioUf> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public List<ProgramaHistoricoPublicizacao> buscarHistoricoPublicizacao(Long id) {
        return publicizacaoDAO.buscarHistoricoPublicizacao(id);
    }

    public List<ProgramaCriterioAvaliacao> buscarProgramaCriterioAvaliacao(Long id) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<ProgramaCriterioAvaliacao> criteriaQuery = criteriaBuilder.createQuery(ProgramaCriterioAvaliacao.class);
        Root<ProgramaCriterioAvaliacao> root = criteriaQuery.from(ProgramaCriterioAvaliacao.class);
        List<Predicate> predicates = new ArrayList<>();
        if (id != null) {
            predicates.add(criteriaBuilder.equal(root.get(PROGRAMA).get("id"), id));
        }
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<ProgramaCriterioAvaliacao> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public List<Programa> buscarPaginado(ProgramaPesquisaDto programaPesquisaDto, int first, int size, EnumOrder order, String propertyOrder) {

        List<Programa> lista1 = buscarSemPaginacao(programaPesquisaDto, order, propertyOrder);
        // filtra paginado
        List<Programa> listaRetorno = new ArrayList<Programa>();
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

    public List<Programa> buscarSemPaginacao(ProgramaPesquisaDto programaPesquisaDto) {
        return buscarSemPaginacao(programaPesquisaDto, EnumOrder.ASC, NOME_PROGRAMA);
    }

    public List<Programa> buscarSemPaginacaoOrdenado(ProgramaPesquisaDto programaPesquisaDto, EnumOrder order, String propertyOrder) {
        return buscarSemPaginacao(programaPesquisaDto, order, propertyOrder);
    }

    private List<Programa> buscarSemPaginacao(ProgramaPesquisaDto programaPesquisaDto, EnumOrder order, String propertyOrder) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Programa> criteriaQuery = criteriaBuilder.createQuery(Programa.class);
        Root<Programa> root = criteriaQuery.from(Programa.class);

        Predicate[] predicates = extractPredicates(programaPesquisaDto.getPrograma(), criteriaBuilder, root);
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates);

        if (order.isAscOrder()) {
            criteriaQuery.orderBy(criteriaBuilder.asc(UtilDAO.extractObjectOrder(propertyOrder, root)));
        } else {
            criteriaQuery.orderBy(criteriaBuilder.desc(UtilDAO.extractObjectOrder(propertyOrder, root)));
        }

        TypedQuery<Programa> query = em.createQuery(criteriaQuery);
        return retornaListaFiltrada(programaPesquisaDto, query);
    }

    public Long contarPaginado(ProgramaPesquisaDto programaPesquisaDto) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<Programa> root = criteriaQuery.from(Programa.class);

        criteriaQuery.select(criteriaBuilder.count(root));
        Predicate[] predicates = extractPredicates(programaPesquisaDto.getPrograma(), criteriaBuilder, root);
        criteriaQuery.where(predicates);

        TypedQuery<Long> query = em.createQuery(criteriaQuery);

        return retornarCountListaFiltrada(programaPesquisaDto, query);

    }

    public List<Programa> buscar(Programa programa) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Programa> criteriaQuery = criteriaBuilder.createQuery(Programa.class);
        Root<Programa> root = criteriaQuery.from(Programa.class);

        Predicate[] predicates = extractPredicates(programa, criteriaBuilder, root);
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates);

        criteriaQuery.orderBy(criteriaBuilder.asc(root.get(NOME_PROGRAMA)));

        TypedQuery<Programa> query = em.createQuery(criteriaQuery);

        return query.getResultList();
    }

    private List<Programa> retornaListaFiltrada(ProgramaPesquisaDto programaPesquisaDto, TypedQuery<Programa> query) {
        List<Programa> programasFiltradosPorPredicates = query.getResultList();
        if (programaPesquisaDto.getPesquisaPublica()) {
            filtrarProgramasPublicados(programasFiltradosPorPredicates);
        }
        return retornaListaFiltrada(programasFiltradosPorPredicates, programaPesquisaDto);

    }

    private List<Programa> retornaListaFiltrada(List<Programa> programasFiltradosPorPredicates, ProgramaPesquisaDto programaPesquisaDto) {

        List<Programa> programasFiltradosFinal = new ArrayList<Programa>();
        boolean possuiUf = false;
        boolean possuiMunicipio = false;
        boolean possuiEmendaParlamentar = false;
        boolean possuiAcaoOrcamentaria = false;
        boolean possuiElemento = false;
        boolean possuiBem = false;
        boolean possuiKit = false;
        boolean possuiTipoPrograma = false;
        boolean possuiPartidoParlamantar = false;
        boolean possuiUfParlamentar = false;
        boolean possuiNomeParlamentar = false;
        boolean possuiCargoParlamentar = false;
        boolean possuiCnpjBeneficiario = false;
        boolean possuiCodigoIdentificadorProgramaPublicado = false;

        if (!programaPesquisaDto.getListaUf().isEmpty() || !programaPesquisaDto.getListaMunicipio().isEmpty() || programaPesquisaDto.getEmendaParlamentar() != null || programaPesquisaDto.getAcaoOrcamentaria() != null || programaPesquisaDto.getElemento() != null
                || programaPesquisaDto.getKit() != null || programaPesquisaDto.getBem() != null || programaPesquisaDto.getTipoPrograma() != null || programaPesquisaDto.getPartidoParlamentar() != null || programaPesquisaDto.getCnpjBeneficiario() != null
                || programaPesquisaDto.getUfParlamentar() != null || programaPesquisaDto.getNomeParlamentar() != null || programaPesquisaDto.getCargoParlamentar() != null || programaPesquisaDto.getCodigoIdentificadorProgramaPublicado() != null) {

            for (Programa programa : programasFiltradosPorPredicates) {

                // uf
                if (programaPesquisaDto.getListaUf().isEmpty() || existeUfsNoPrograma(programa.getPotenciaisBeneficiariosUf(), programaPesquisaDto.getListaUf())) {
                    possuiUf = true;
                }

                // Municipio
                if (programaPesquisaDto.getListaMunicipio().isEmpty() || existeMunicipiosNoPrograma(programa.getPotenciaisBeneficiariosUf(), programaPesquisaDto.getListaMunicipio())) {
                    possuiMunicipio = true;
                }

                // Emenda Parlamentar
                if (programaPesquisaDto.getEmendaParlamentar() == null || existeEmendaParlamentarNoPrograma(programa.getRecursosFinanceiros(), programaPesquisaDto.getEmendaParlamentar())) {
                    possuiEmendaParlamentar = true;
                }

                // Partido do Parlamentar
                if (programaPesquisaDto.getPartidoParlamentar() == null || existePartidoParlamentar(programa.getRecursosFinanceiros(), programaPesquisaDto.getPartidoParlamentar())) {
                    possuiPartidoParlamantar = true;
                }

                // UF do Parlamentar
                if (programaPesquisaDto.getUfParlamentar() == null || existeUfParlamentar(programa.getRecursosFinanceiros(), programaPesquisaDto.getUfParlamentar())) {
                    possuiUfParlamentar = true;
                }

                // Nome do Parlamentar
                if (programaPesquisaDto.getNomeParlamentar() == null || existeNomeParlamentar(programa.getRecursosFinanceiros(), programaPesquisaDto.getNomeParlamentar())) {
                    possuiNomeParlamentar = true;
                }

                // Cargo do Parlamentar
                if (programaPesquisaDto.getCargoParlamentar() == null || existeCargoParlamentar(programa.getRecursosFinanceiros(), programaPesquisaDto.getCargoParlamentar())) {
                    possuiCargoParlamentar = true;
                }

                // CNPJ BENEFICIARIO
                if (programaPesquisaDto.getCnpjBeneficiario() == null || existeCnpjBeneficiario(programa.getRecursosFinanceiros(), programaPesquisaDto.getCnpjBeneficiario())) {
                    possuiCnpjBeneficiario = true;
                }

                // Ação Orçamentária
                if (programaPesquisaDto.getAcaoOrcamentaria() == null || existeAcaoOrcamentariaNoPrograma(programa.getRecursosFinanceiros(), programaPesquisaDto.getAcaoOrcamentaria())) {
                    possuiAcaoOrcamentaria = true;
                }

                // Elemento
                if (programaPesquisaDto.getElemento() == null || existeElementoNoPrograma(programa, programaPesquisaDto.getElemento())) {
                    possuiElemento = true;
                }

                // Bem
                if (programaPesquisaDto.getBem() == null || existeBemNoProgramaBem(programa.getProgramaBens(), programaPesquisaDto.getBem())) {
                    possuiBem = true;
                }

                // Kit
                if (programaPesquisaDto.getKit() == null || existeKitNoProgramaKit(programa.getProgramaKits(), programaPesquisaDto.getKit())) {
                    possuiKit = true;
                }

                // Tipo Programa
                if (programaPesquisaDto.getTipoPrograma() == null || existeTipoNoPrograma(programa, programaPesquisaDto.getTipoPrograma())) {
                    possuiTipoPrograma = true;
                }

                // CodigoIdentificadorProgramaPublicado
                if (programaPesquisaDto.getCodigoIdentificadorProgramaPublicado() == null || existeCodigoIdentificadorProgramaPublicado(programa, programaPesquisaDto.getCodigoIdentificadorProgramaPublicado())) {
                    possuiCodigoIdentificadorProgramaPublicado = true;
                }

                if (possuiUf && possuiMunicipio && possuiEmendaParlamentar && possuiCnpjBeneficiario && possuiAcaoOrcamentaria && possuiElemento && possuiBem && possuiKit && possuiTipoPrograma && possuiUfParlamentar && possuiPartidoParlamantar && possuiNomeParlamentar && possuiCargoParlamentar
                        && possuiCodigoIdentificadorProgramaPublicado) {
                    programasFiltradosFinal.add(programa);
                }

                possuiUf = false;
                possuiMunicipio = false;
                possuiEmendaParlamentar = false;
                possuiCnpjBeneficiario = false;
                possuiAcaoOrcamentaria = false;
                possuiElemento = false;
                possuiBem = false;
                possuiKit = false;
                possuiTipoPrograma = false;
                possuiPartidoParlamantar = false;
                possuiUfParlamentar = false;
                possuiNomeParlamentar = false;
                possuiCargoParlamentar = false;
                possuiCodigoIdentificadorProgramaPublicado = false;

            }
        } else {
            return programasFiltradosPorPredicates;
        }

        return programasFiltradosFinal;

    }

    private Long retornarCountListaFiltrada(ProgramaPesquisaDto programaPesquisaDto, TypedQuery<Long> query) {

        if (!programaPesquisaDto.getListaUf().isEmpty() || !programaPesquisaDto.getListaMunicipio().isEmpty() || programaPesquisaDto.getEmendaParlamentar() != null || programaPesquisaDto.getAcaoOrcamentaria() != null || programaPesquisaDto.getElemento() != null) {
            List<Programa> lista = buscar(programaPesquisaDto.getPrograma());
            return (long) retornaListaFiltrada(lista, programaPesquisaDto).size();

        } else {
            return query.getSingleResult();
        }
    }

    private void filtrarProgramasPublicados(List<Programa> listaProgramas) {
        for (int i = listaProgramas.size() - 1; i >= 0; i--) {
            if (listaProgramas.get(i).getStatusPrograma() == EnumStatusPrograma.EM_ELABORACAO || listaProgramas.get(i).getStatusPrograma() == EnumStatusPrograma.FORMULADO) {
                listaProgramas.remove(i);
            }
        }
    }

    public Programa buscarPeloId(Long id) {

        return em.find(Programa.class, id);
    }

    public void excluir(Programa programa) {
        em.remove(programa);

    }

    public Programa incluir(Programa programa, String usuarioLogado) {

        /* seta data de cadastro, usuario logado e status inicial */
        programa.setUsuarioCadastro(usuarioLogado);
        programa.setDataCadastro(LocalDateTime.now());

        /* atribuindo SubFuncao e Unidade Executora com contexto transacional */
        programa.setSubFuncao(em.find(SubFuncao.class, programa.getSubFuncao().getId()));
        programa.setUnidadeExecutora(em.find(UnidadeExecutora.class, programa.getUnidadeExecutora().getId()));

        /*
         * Setar programa dentro de cada anexo para resolver a questão da
         * referência bidirecional
         */
        List<ProgramaAnexo> listaProgramaAnexo = new ArrayList<ProgramaAnexo>();
        for (ProgramaAnexo programaAnexo : programa.getAnexos()) {
            programaAnexo.setPrograma(programa);
            programaAnexo.setDataCadastro(LocalDateTime.now());
            programaAnexo.setTamanho(new Long(programaAnexo.getConteudo().length));
            listaProgramaAnexo.add(programaAnexo);
        }
        programa.setAnexos(listaProgramaAnexo);

        /*
         * Setar programa dentro de cada CriteriosAcompanhamento para resolver a
         * questão da referência bidirecional
         */
        List<ProgramaCriterioAcompanhamento> listaProgramaCriterioAcompanhamento = new ArrayList<ProgramaCriterioAcompanhamento>();
        for (ProgramaCriterioAcompanhamento programaCriterioAcompanhamento : programa.getCriteriosAcompanhamento()) {
            programaCriterioAcompanhamento.setPrograma(programa);
            listaProgramaCriterioAcompanhamento.add(programaCriterioAcompanhamento);
        }
        programa.setCriteriosAcompanhamento(listaProgramaCriterioAcompanhamento);

        /*
         * Setar programa dentro de cada CriteriosElegibilidade para resolver a
         * questão da referência bidirecional
         */
        List<ProgramaCriterioElegibilidade> listaProgramaCriterioElegibilidade = new ArrayList<ProgramaCriterioElegibilidade>();
        for (ProgramaCriterioElegibilidade programaCriterioElegibilidade : programa.getCriteriosElegibilidade()) {
            programaCriterioElegibilidade.setPrograma(programa);
            listaProgramaCriterioElegibilidade.add(programaCriterioElegibilidade);
        }
        programa.setCriteriosElegibilidade(listaProgramaCriterioElegibilidade);

        /*
         * Setar programa dentro de cada ProgramaKits para resolver a questão da
         * referência bidirecional
         */
        List<ProgramaKit> listaProgramaKit = new ArrayList<ProgramaKit>();
        for (ProgramaKit programaKit : programa.getProgramaKits()) {
            programaKit.setPrograma(programa);
            listaProgramaKit.add(programaKit);
        }
        programa.setProgramaKits(listaProgramaKit);

        /*
         * Setar programa dentro de cada ProgramaBens para resolver a questão da
         * referência bidirecional
         */
        List<ProgramaBem> listaProgramaBem = new ArrayList<ProgramaBem>();
        for (ProgramaBem programaBem : programa.getProgramaBens()) {
            programaBem.setPrograma(programa);
            listaProgramaBem.add(programaBem);
        }
        programa.setProgramaBens(listaProgramaBem);

        /*
         * Setar programa dentro de cada RecursosFinanceiros para resolver a
         * questão da referência bidirecional
         */
        List<ProgramaRecursoFinanceiro> listaProgramaRecursoFinanceiro = new ArrayList<ProgramaRecursoFinanceiro>();
        for (ProgramaRecursoFinanceiro programaRecursoFinanceiro : programa.getRecursosFinanceiros()) {
            programaRecursoFinanceiro.setPrograma(programa);

            /*
             * Setar Recurso Financeiro dentro de cada Emenda para resolver a
             * questão da referência bidirecional
             */
            List<RecursoFinanceiroEmenda> listaRecursoFinanceiroEmenda = new ArrayList<RecursoFinanceiroEmenda>();
            for (RecursoFinanceiroEmenda recursoFinanceiroEmenda : programaRecursoFinanceiro.getRecursoFinanceiroEmendas()) {
                recursoFinanceiroEmenda.setRecursoFinanceiro(programaRecursoFinanceiro);
                listaRecursoFinanceiroEmenda.add(recursoFinanceiroEmenda);
            }
            programaRecursoFinanceiro.setRecursoFinanceiroEmendas(listaRecursoFinanceiroEmenda);

            listaProgramaRecursoFinanceiro.add(programaRecursoFinanceiro);
        }
        programa.setRecursosFinanceiros(listaProgramaRecursoFinanceiro);

        /*
         * Setar programa dentro de cada ProgramaPotencialBeneficiarioUf para
         * resolver a questão da referência bidirecional
         */
        List<ProgramaPotencialBeneficiarioUf> listaProgramaPotencialBeneficiarioUf = new ArrayList<ProgramaPotencialBeneficiarioUf>();
        for (ProgramaPotencialBeneficiarioUf programaPotencialBeneficiarioUf : programa.getPotenciaisBeneficiariosUf()) {
            programaPotencialBeneficiarioUf.setPrograma(programa);

            /*
             * Setar Uf dentro de cada Municipio para resolver a questão da
             * referência bidirecional
             */
            List<PotencialBeneficiarioMunicipio> listaPotencialBeneficiarioMunicipio = new ArrayList<PotencialBeneficiarioMunicipio>();
            for (PotencialBeneficiarioMunicipio potencialBeneficiarioMunicipio : programaPotencialBeneficiarioUf.getPotencialBeneficiarioMunicipios()) {
                potencialBeneficiarioMunicipio.setPotencialBeneficiarioUF(programaPotencialBeneficiarioUf);
                listaPotencialBeneficiarioMunicipio.add(potencialBeneficiarioMunicipio);
            }
            programaPotencialBeneficiarioUf.setPotencialBeneficiarioMunicipios(listaPotencialBeneficiarioMunicipio);

            listaProgramaPotencialBeneficiarioUf.add(programaPotencialBeneficiarioUf);
        }
        programa.setPotenciaisBeneficiariosUf(listaProgramaPotencialBeneficiarioUf);

        /*
         * Setar programa dentro de cada CriteriosAvaliacao para resolver a
         * questão da referência bidirecional
         */
        List<ProgramaCriterioAvaliacao> listaProgramaCriterioAvaliacao = new ArrayList<ProgramaCriterioAvaliacao>();
        for (ProgramaCriterioAvaliacao programaCriterioAvaliacao : programa.getCriteriosAvaliacao()) {
            programaCriterioAvaliacao.setPrograma(programa);

            /*
             * Setar CriterioAvaliacao dentro de cada Opcao Respostaq para
             * resolver a questão da referência bidirecional
             */
            List<ProgramaCriterioAvaliacaoOpcaoResposta> listaProgramaCriterioAvaliacaoOpcaoResposta = new ArrayList<ProgramaCriterioAvaliacaoOpcaoResposta>();
            for (ProgramaCriterioAvaliacaoOpcaoResposta criterioAvaliacaoOpcaoResposta : programaCriterioAvaliacao.getCriteriosAvaliacaoOpcaoResposta()) {
                criterioAvaliacaoOpcaoResposta.setCriterioAvaliacao(programaCriterioAvaliacao);
                listaProgramaCriterioAvaliacaoOpcaoResposta.add(criterioAvaliacaoOpcaoResposta);
            }
            programaCriterioAvaliacao.setCriteriosAvaliacaoOpcaoResposta(listaProgramaCriterioAvaliacaoOpcaoResposta);
            listaProgramaCriterioAvaliacao.add(programaCriterioAvaliacao);
        }
        programa.setCriteriosAvaliacao(listaProgramaCriterioAvaliacao);

        em.persist(programa);
        return programa;
    }

    public Programa alterar(Programa programa, String usuarioLogado) {

        Programa programaParaMerge = buscarPeloId(programa.getId());

        programaParaMerge.setUsuarioAlteracao(usuarioLogado);
        programaParaMerge.setDataAlteracao(LocalDateTime.now());

        sincronizarAnexos(programa.getAnexos(), programaParaMerge);
        sincronizarCriteriosAcompanhamento(programa.getCriteriosAcompanhamento(), programaParaMerge);
        sincronizarCriteriosElegibilidade(programa.getCriteriosElegibilidade(), programaParaMerge);
        sincronizarProgramaKits(programa.getProgramaKits(), programaParaMerge);
        sincronizarProgramaBens(programa.getProgramaBens(), programaParaMerge);
        sincronizarRecursosFinanceiros(programa.getRecursosFinanceiros(), programaParaMerge);
        sincronizarPotenciaisBeneficiariosUf(programa.getPotenciaisBeneficiariosUf(), programaParaMerge);
        sincronizarCriteriosAvaliacao(programa.getCriteriosAvaliacao(), programaParaMerge);

        programaParaMerge.setNomePrograma(programa.getNomePrograma());
        programaParaMerge.setNomeFantasiaPrograma(programa.getNomeFantasiaPrograma());
        programaParaMerge.setDescricaoPrograma(programa.getDescricaoPrograma());
        programaParaMerge.setSubFuncao(em.find(SubFuncao.class, programa.getSubFuncao().getId()));
        programaParaMerge.setUnidadeExecutora(em.find(UnidadeExecutora.class, programa.getUnidadeExecutora().getId()));
        programaParaMerge.setNumeroProcessoSEI(programa.getNumeroProcessoSEI());
        programaParaMerge.setPossuiLimitacaoGeografica(programa.getPossuiLimitacaoGeografica());
        programaParaMerge.setPossuiLimitacaoMunicipalEspecifica(programa.getPossuiLimitacaoMunicipalEspecifica());
        programaParaMerge.setTipoPersonalidadeJuridica(programa.getTipoPersonalidadeJuridica());
        programaParaMerge.setStatusPrograma(programa.getStatusPrograma());
        programaParaMerge.setAnoPrograma(programa.getAnoPrograma());
        programaParaMerge.setIdentificadorProgramaPublicado(programa.getIdentificadorProgramaPublicado());
        programaParaMerge.setValorMaximoProposta(programa.getValorMaximoProposta());

        return em.merge(programaParaMerge);
    }

    public Programa atualizarInformacoesBasicasPrograma(Programa programa, String usuarioLogado) {
        programa.setUsuarioAlteracao(usuarioLogado);
        return em.merge(programa);
    }

    public Programa publicar(ProgramaHistoricoPublicizacao programaHistoricoPublicizacao, Long programaId, String usuarioLogado) {
        Programa programaParaMerge = buscarPeloId(programaId);
        programaParaMerge.setUsuarioAlteracao(usuarioLogado);
        programaParaMerge.setDataAlteracao(LocalDateTime.now());
        programaParaMerge.setStatusPrograma(EnumStatusPrograma.PUBLICADO);

        programaHistoricoPublicizacao.setDataCadastro(LocalDateTime.now());
        programaHistoricoPublicizacao.setUsuarioCadastro(usuarioLogado);
        programaHistoricoPublicizacao.setPrograma(programaParaMerge);
        programaHistoricoPublicizacao.setStatusPrograma(EnumStatusPrograma.PUBLICADO);
        em.persist(programaHistoricoPublicizacao);

        return em.merge(programaParaMerge);
    }

    public Programa prorrogar(ProgramaHistoricoPublicizacao programaHistoricoPublicizacao, Long programaId, String usuarioLogado) {
        Programa programaParaMerge = buscarPeloId(programaId);
        programaParaMerge.setUsuarioAlteracao(usuarioLogado);
        programaParaMerge.setDataAlteracao(LocalDateTime.now());

        programaHistoricoPublicizacao.setDataCadastro(LocalDateTime.now());
        programaHistoricoPublicizacao.setUsuarioCadastro(usuarioLogado);
        programaHistoricoPublicizacao.setPrograma(programaParaMerge);
        programaHistoricoPublicizacao.setStatusPrograma(EnumStatusPrograma.PUBLICADO);
        em.persist(programaHistoricoPublicizacao);

        return em.merge(programaParaMerge);
    }

    public Programa suspender(Long programaId, String usuarioLogado) {
        Programa programaParaMerge = buscarPeloId(programaId);
        programaParaMerge.setUsuarioAlteracao(usuarioLogado);
        programaParaMerge.setDataAlteracao(LocalDateTime.now());
        programaParaMerge.setStatusPrograma(EnumStatusPrograma.SUSPENSO);

        ProgramaHistoricoPublicizacao programaHistoricoPublicizacao = new ProgramaHistoricoPublicizacao();

        programaHistoricoPublicizacao.setDataCadastro(LocalDateTime.now());
        programaHistoricoPublicizacao.setUsuarioCadastro(usuarioLogado);
        programaHistoricoPublicizacao.setPrograma(programaParaMerge);
        programaHistoricoPublicizacao.setStatusPrograma(EnumStatusPrograma.SUSPENSO);
        em.persist(programaHistoricoPublicizacao);

        return em.merge(programaParaMerge);
    }

    public Programa cancelar(Long programaId, String usuarioLogado) {
        Programa programaParaMerge = buscarPeloId(programaId);
        programaParaMerge.setUsuarioAlteracao(usuarioLogado);
        programaParaMerge.setDataAlteracao(LocalDateTime.now());
        programaParaMerge.setStatusPrograma(EnumStatusPrograma.CANCELADO);

        ProgramaHistoricoPublicizacao programaHistoricoPublicizacao = new ProgramaHistoricoPublicizacao();

        programaHistoricoPublicizacao.setDataCadastro(LocalDateTime.now());
        programaHistoricoPublicizacao.setUsuarioCadastro(usuarioLogado);
        programaHistoricoPublicizacao.setPrograma(programaParaMerge);
        programaHistoricoPublicizacao.setStatusPrograma(EnumStatusPrograma.CANCELADO);
        em.persist(programaHistoricoPublicizacao);

        return em.merge(programaParaMerge);
    }

    private Predicate[] extractPredicates(Programa programa, CriteriaBuilder criteriaBuilder, Root<?> root) {
        List<Predicate> predicates = new ArrayList<>();

        // código Identificador não nulo
        if (programa != null && programa.getIdentificadorProgramaPublicado() != null && programa.getIdentificadorProgramaPublicado().equals(0)) {
            predicates.add(criteriaBuilder.isNotNull(root.get("identificadorProgramaPublicado")));
        }

        // código
        if (programa != null && programa.getId() != null) {
            predicates.add(criteriaBuilder.equal(root.get("id"), programa.getId()));
        }

        // nome
        if (programa != null && StringUtils.isNotBlank(programa.getNomePrograma())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.function(Constants.FUNCAO_RETIRA_ACENTO, String.class, criteriaBuilder.lower(root.get(NOME_PROGRAMA))), "%" + UtilDAO.removerAcentos(programa.getNomePrograma().toLowerCase()) + "%"));
        }

        // nome fantasia
        if (programa != null && StringUtils.isNotBlank(programa.getNomeFantasiaPrograma())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.function(Constants.FUNCAO_RETIRA_ACENTO, String.class, criteriaBuilder.lower(root.get("nomeFantasiaPrograma"))), "%" + UtilDAO.removerAcentos(programa.getNomeFantasiaPrograma().toLowerCase()) + "%"));
        }

        // ano
        if (programa != null && programa.getAnoPrograma() != null) {
            predicates.add(criteriaBuilder.equal(root.get("anoPrograma"), programa.getAnoPrograma()));
        }

        // orgao
        if (programa != null && programa.getUnidadeExecutora() != null && programa.getUnidadeExecutora().getOrgao() != null && programa.getUnidadeExecutora().getOrgao().getId() != null) {
            predicates.add(criteriaBuilder.equal(root.get("unidadeExecutora").get("orgao").get("id"), programa.getUnidadeExecutora().getOrgao().getId()));
        }

        // unidade executora
        if (programa != null && programa.getUnidadeExecutora() != null && programa.getUnidadeExecutora().getId() != null) {
            predicates.add(criteriaBuilder.equal(root.get("unidadeExecutora").get("id"), programa.getUnidadeExecutora().getId()));
        }

        // limitacao geográfica
        if (programa != null && programa.getPossuiLimitacaoGeografica() != null) {
            predicates.add(criteriaBuilder.equal(root.get("possuiLimitacaoGeografica"), programa.getPossuiLimitacaoGeografica()));
        }

        // limitacao municipal Específica
        if (programa != null && programa.getPossuiLimitacaoMunicipalEspecifica() != null) {
            predicates.add(criteriaBuilder.equal(root.get("possuiLimitacaoMunicipalEspecifica"), programa.getPossuiLimitacaoMunicipalEspecifica()));
        }

        // personalidade Jurídica
        if (programa != null && programa.getTipoPersonalidadeJuridica() != null) {
            predicates.add(criteriaBuilder.equal(root.get("tipoPersonalidadeJuridica"), programa.getTipoPersonalidadeJuridica()));
        }

        // numero processo SEI
        if (programa != null && StringUtils.isNotBlank(programa.getNumeroProcessoSEI())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.function(Constants.FUNCAO_RETIRA_ACENTO, String.class, criteriaBuilder.lower(root.get("numeroProcessoSEI"))), "%" + UtilDAO.removerAcentos(programa.getNumeroProcessoSEI().toLowerCase()) + "%"));
        }

        // status
        if (programa != null && programa.getStatusPrograma() != null) {
            predicates.add(criteriaBuilder.equal(root.get("statusPrograma"), programa.getStatusPrograma()));
        }

        return predicates.toArray(new Predicate[] {});

    }

    private boolean existeUfsNoPrograma(List<ProgramaPotencialBeneficiarioUf> programaPotencialBeneficiarioUf, List<Uf> listaUf) {
        for (Uf uf : listaUf) {
            if (!pegarListaDeUf(programaPotencialBeneficiarioUf).contains(uf)) {
                return false;
            }
        }
        return true;
    }

    private List<Uf> pegarListaDeUf(List<ProgramaPotencialBeneficiarioUf> potenciaisBeneficiariosUf) {
        List<Uf> ufs = new ArrayList<Uf>();
        for (ProgramaPotencialBeneficiarioUf potencialBeneficiarioUf : potenciaisBeneficiariosUf) {
            ufs.add(potencialBeneficiarioUf.getUf());
        }
        return ufs;
    }

    private boolean existeMunicipiosNoPrograma(List<ProgramaPotencialBeneficiarioUf> potenciaisBeneficiariosUf, List<Municipio> listaMunicipio) {
        for (Municipio municipio : listaMunicipio) {
            if (!pegarListaDeMunicipio(potenciaisBeneficiariosUf).contains(municipio)) {
                return false;
            }
        }
        return true;
    }

    private List<Municipio> pegarListaDeMunicipio(List<ProgramaPotencialBeneficiarioUf> potenciaisBeneficiariosUf) {
        List<Municipio> municipios = new ArrayList<Municipio>();
        for (ProgramaPotencialBeneficiarioUf potencialBeneficiarioUf : potenciaisBeneficiariosUf) {
            for (PotencialBeneficiarioMunicipio potencialBeneficiarioMunicipio : potencialBeneficiarioUf.getPotencialBeneficiarioMunicipios()) {
                municipios.add(potencialBeneficiarioMunicipio.getMunicipio());
            }
        }
        return municipios;
    }

    private boolean existeEmendaParlamentarNoPrograma(List<ProgramaRecursoFinanceiro> listaProgramaRecursoFinanceiro, EmendaParlamentar emendaParlamentar) {
        for (ProgramaRecursoFinanceiro recursoFinanceiro : listaProgramaRecursoFinanceiro) {
            for (RecursoFinanceiroEmenda recursoFinanceiroEmenda : recursoFinanceiro.getRecursoFinanceiroEmendas()) {
                if (emendaParlamentar.getId().equals(recursoFinanceiroEmenda.getEmendaParlamentar().getId())) {
                    return true;

                }
            }
        }
        return false;
    }

    private boolean existePartidoParlamentar(List<ProgramaRecursoFinanceiro> listaProgramaRecursoFinanceiro, PartidoPolitico partido) {
        for (ProgramaRecursoFinanceiro recursoFinanceiro : listaProgramaRecursoFinanceiro) {
            for (RecursoFinanceiroEmenda recursoFinanceiroEmenda : recursoFinanceiro.getRecursoFinanceiroEmendas()) {

                if (partido != null && partido.getId().intValue() == recursoFinanceiroEmenda.getEmendaParlamentar().getPartidoPolitico().getId().intValue()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean existeUfParlamentar(List<ProgramaRecursoFinanceiro> listaProgramaRecursoFinanceiro, Uf uf) {
        for (ProgramaRecursoFinanceiro recursoFinanceiro : listaProgramaRecursoFinanceiro) {
            for (RecursoFinanceiroEmenda recursoFinanceiroEmenda : recursoFinanceiro.getRecursoFinanceiroEmendas()) {
                if (uf != null && uf.getId().intValue() == recursoFinanceiroEmenda.getEmendaParlamentar().getUf().getId().intValue()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean existeNomeParlamentar(List<ProgramaRecursoFinanceiro> listaProgramaRecursoFinanceiro, String nomeParlamentar) {
        for (ProgramaRecursoFinanceiro recursoFinanceiro : listaProgramaRecursoFinanceiro) {
            for (RecursoFinanceiroEmenda recursoFinanceiroEmenda : recursoFinanceiro.getRecursoFinanceiroEmendas()) {

                if (nomeParlamentar != null && UtilDAO.removerAcentos(recursoFinanceiroEmenda.getEmendaParlamentar().getNomeParlamentar().toUpperCase()).contains(UtilDAO.removerAcentos(nomeParlamentar.toUpperCase()))) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean existeCargoParlamentar(List<ProgramaRecursoFinanceiro> listaProgramaRecursoFinanceiro, String cargoParlamentar) {
        for (ProgramaRecursoFinanceiro recursoFinanceiro : listaProgramaRecursoFinanceiro) {
            for (RecursoFinanceiroEmenda recursoFinanceiroEmenda : recursoFinanceiro.getRecursoFinanceiroEmendas()) {

                if (cargoParlamentar != null && UtilDAO.removerAcentos(recursoFinanceiroEmenda.getEmendaParlamentar().getNomeCargoParlamentar().toUpperCase()).contains(UtilDAO.removerAcentos(cargoParlamentar.toUpperCase()))) {
                    return true;
                }

            }
        }
        return false;
    }

    private boolean existeCnpjBeneficiario(List<ProgramaRecursoFinanceiro> listaProgramaRecursoFinanceiro, BeneficiarioEmendaParlamentar cnpjPesquisa) {
        for (ProgramaRecursoFinanceiro recursoFinanceiro : listaProgramaRecursoFinanceiro) {
            for (RecursoFinanceiroEmenda recursoEmenda : recursoFinanceiro.getRecursoFinanceiroEmendas()) {
                List<BeneficiarioEmendaParlamentar> listaBeneficiarios = recursoEmenda.getEmendaParlamentar().getBeneficiariosEmendaParlamentar();

                for (BeneficiarioEmendaParlamentar cnpj : listaBeneficiarios) {
                    if (cnpj.getNumeroCnpjBeneficiario().equalsIgnoreCase(limparCnpj(cnpjPesquisa.getNumeroCnpjBeneficiario()))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean existeAcaoOrcamentariaNoPrograma(List<ProgramaRecursoFinanceiro> listaProgramaRecursoFinanceiro, AcaoOrcamentaria acaoOrcamentaria) {
        for (ProgramaRecursoFinanceiro recursoFinanceiro : listaProgramaRecursoFinanceiro) {
            if (acaoOrcamentaria.getId().equals(recursoFinanceiro.getAcaoOrcamentaria().getId())) {
                return true;
            }
        }
        return false;
    }

    private boolean existeElementoNoPrograma(Programa programa, Elemento elemento) {
        return existeElementoNoProgramaBem(programa.getProgramaBens(), elemento) || existeElementoNoProgramaKit(programa.getProgramaKits(), elemento);
    }

    private boolean existeElementoNoProgramaBem(List<ProgramaBem> listaprogramaBens, Elemento elemento) {
        for (ProgramaBem programaBem : listaprogramaBens) {
            if (elemento.getId().equals(programaBem.getBem().getSubElemento().getElemento().getId())) {
                return true;
            }
        }
        return false;
    }

    private boolean existeElementoNoProgramaKit(List<ProgramaKit> listaProgramaKits, Elemento elemento) {
        for (ProgramaKit programaKit : listaProgramaKits) {
            for (KitBem kitBem : programaKit.getKit().getKitsBens()) {
                if (elemento.getId().equals(kitBem.getBem().getSubElemento().getElemento().getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean existeBemNoProgramaBem(List<ProgramaBem> listaprogramaBens, Bem bem) {
        for (ProgramaBem programaBem : listaprogramaBens) {
            if (bem.getId().equals(programaBem.getBem().getId())) {
                return true;
            }
        }
        return false;
    }

    private boolean existeKitNoProgramaKit(List<ProgramaKit> listaProgramaKits, Kit kit) {
        for (ProgramaKit programaKit : listaProgramaKits) {
            if (kit.getId().equals(programaKit.getKit().getId())) {
                return true;
            }
        }
        return false;
    }

    private boolean existeTipoNoPrograma(Programa programa, EnumTipoPrograma tipo) {
        List<ProgramaHistoricoPublicizacao> lista = publicizacaoDAO.buscarHistoricoPublicizacao(programa.getId());
        if (!lista.isEmpty() && lista.get(0).getTipoPrograma().equals(tipo)) {
            return true;
        }
        return false;
    }

    private boolean existeCodigoIdentificadorProgramaPublicado(Programa programa, String codigoIdentificadorProgramaPublicado) {
        return UtilDAO.removerAcentos(programa.getCodigoIdentificadorProgramaPublicado().toLowerCase()).contains(UtilDAO.removerAcentos(codigoIdentificadorProgramaPublicado.toLowerCase()));
    }

    public void sincronizarAnexos(List<ProgramaAnexo> anexos, Programa entityAtual) {
        // remover os excluidos
        List<ProgramaAnexo> anexosAux = new ArrayList<ProgramaAnexo>(entityAtual.getAnexos());

        for (ProgramaAnexo anexoAtual : anexosAux) {
            if (!anexos.contains(anexoAtual)) {
                entityAtual.getAnexos().remove(anexoAtual);
            }
        }

        // adiciona os novos
        for (ProgramaAnexo anexoNovo : anexos) {
            if (anexoNovo.getId() == null) {
                anexoNovo.setPrograma(entityAtual);
                anexoNovo.setDataCadastro(LocalDateTime.now());
                anexoNovo.setTamanho(new Long(anexoNovo.getConteudo().length));
                entityAtual.getAnexos().add(anexoNovo);
            }
        }

    }

    private void sincronizarCriteriosAcompanhamento(List<ProgramaCriterioAcompanhamento> criteriosAcompanhamentoDoPrograma, Programa programaAtual) {

        List<ProgramaCriterioAcompanhamento> listaCriterioAcompanhamentoAtualParaAtualizar = new ArrayList<ProgramaCriterioAcompanhamento>();
        List<ProgramaCriterioAcompanhamento> listaCriterioAcompanhamentoAdicionar = new ArrayList<ProgramaCriterioAcompanhamento>();

        /*
         * seleciona apenas os Criterio de Acompanhamento que foram mantidas na
         * lista vinda do service
         */
        for (ProgramaCriterioAcompanhamento criterioAcompanhamentoAtual : programaAtual.getCriteriosAcompanhamento()) {
            if (criteriosAcompanhamentoDoPrograma.contains(criterioAcompanhamentoAtual)) {
                listaCriterioAcompanhamentoAtualParaAtualizar.add(criterioAcompanhamentoAtual);
            }

        }

        /* remove a lista atual de Criterio de Acompanhamento */
        programaAtual.getCriteriosAcompanhamento().clear();

        /* adiciona os novos na lista de Criterio de Acompanhamento */
        for (ProgramaCriterioAcompanhamento criterioAcompanhamentoNovo : criteriosAcompanhamentoDoPrograma) {
            if (criterioAcompanhamentoNovo.getId() == null) {
                /* atender referencia bidirecional */
                criterioAcompanhamentoNovo.setPrograma(programaAtual);
                programaAtual.getCriteriosAcompanhamento().add(criterioAcompanhamentoNovo);
            }
        }

        /*
         * atualiza atributos nos Criterios de Acompanhamento vindos do service
         * para persistir
         */
        for (ProgramaCriterioAcompanhamento criterioAcompanhamentoParaAtualizar : listaCriterioAcompanhamentoAtualParaAtualizar) {
            for (ProgramaCriterioAcompanhamento criterioAcompanhamento : criteriosAcompanhamentoDoPrograma) {
                if (criterioAcompanhamento.getId() != null && criterioAcompanhamento.getId().equals(criterioAcompanhamentoParaAtualizar.getId())) {

                    criterioAcompanhamentoParaAtualizar.setNomeCriterioAcompanhamento(criterioAcompanhamento.getNomeCriterioAcompanhamento());
                    criterioAcompanhamentoParaAtualizar.setDescricaoCriterioAcompanhamento(criterioAcompanhamento.getDescricaoCriterioAcompanhamento());
                    criterioAcompanhamentoParaAtualizar.setFormaAcompanhamento(criterioAcompanhamento.getFormaAcompanhamento());
                    listaCriterioAcompanhamentoAdicionar.add(criterioAcompanhamentoParaAtualizar);
                }
            }
        }
        /* adiciona os Criterios de Acompanhamento atualizados */
        programaAtual.getCriteriosAcompanhamento().addAll(listaCriterioAcompanhamentoAdicionar);
    }

    private void sincronizarCriteriosElegibilidade(List<ProgramaCriterioElegibilidade> criteriosElegibilidadeDoPrograma, Programa programaAtual) {

        List<ProgramaCriterioElegibilidade> listaCriterioElegibilidadeAtualParaAtualizar = new ArrayList<ProgramaCriterioElegibilidade>();
        List<ProgramaCriterioElegibilidade> listaCriterioElegibilidadeAdicionar = new ArrayList<ProgramaCriterioElegibilidade>();

        /*
         * seleciona apenas os Criterio de Elegibilidade que foram mantidas na
         * lista vinda do service
         */
        for (ProgramaCriterioElegibilidade criterioElegibilidadeAtual : programaAtual.getCriteriosElegibilidade()) {
            if (criteriosElegibilidadeDoPrograma.contains(criterioElegibilidadeAtual)) {
                listaCriterioElegibilidadeAtualParaAtualizar.add(criterioElegibilidadeAtual);
            }

        }

        /* remove a lista atual de Criterio de Elegibilidade */
        programaAtual.getCriteriosElegibilidade().clear();

        /* adiciona os novos na lista de Criterio de Elegibilidade */
        for (ProgramaCriterioElegibilidade criterioElegibilidadeNovo : criteriosElegibilidadeDoPrograma) {
            if (criterioElegibilidadeNovo.getId() == null) {
                /* atender referencia bidirecional */
                criterioElegibilidadeNovo.setPrograma(programaAtual);
                programaAtual.getCriteriosElegibilidade().add(criterioElegibilidadeNovo);
            }
        }

        /*
         * atualiza atributos nos Criterios de Elegibilidade vindos do service
         * para persistir
         */
        for (ProgramaCriterioElegibilidade criterioElegibilidadeParaAtualizar : listaCriterioElegibilidadeAtualParaAtualizar) {
            for (ProgramaCriterioElegibilidade criterioElegibilidade : criteriosElegibilidadeDoPrograma) {
                if (criterioElegibilidade.getId() != null && criterioElegibilidade.getId().equals(criterioElegibilidadeParaAtualizar.getId())) {

                    criterioElegibilidadeParaAtualizar.setNomeCriterioElegibilidade(criterioElegibilidade.getNomeCriterioElegibilidade());
                    criterioElegibilidadeParaAtualizar.setDescricaoCriterioElegibilidade(criterioElegibilidade.getDescricaoCriterioElegibilidade());
                    criterioElegibilidadeParaAtualizar.setFormaVerificacao(criterioElegibilidade.getFormaVerificacao());
                    criterioElegibilidadeParaAtualizar.setPossuiObrigatoriedadeDeAnexo(criterioElegibilidade.getPossuiObrigatoriedadeDeAnexo());
                    listaCriterioElegibilidadeAdicionar.add(criterioElegibilidadeParaAtualizar);
                }
            }
        }
        /* adiciona os Criterios de Elegibilidade atualizados */
        programaAtual.getCriteriosElegibilidade().addAll(listaCriterioElegibilidadeAdicionar);
    }

    private void sincronizarProgramaKits(List<ProgramaKit> kitsDoPrograma, Programa programaAtual) {

        List<ProgramaKit> listaKitsAtualParaAtualizar = new ArrayList<ProgramaKit>();
        List<ProgramaKit> listaKitsAdicionar = new ArrayList<ProgramaKit>();

        /*
         * seleciona apenas os Kits que foram mantidas na lista vinda do service
         */
        for (ProgramaKit kitAtual : programaAtual.getProgramaKits()) {
            if (kitsDoPrograma.contains(kitAtual)) {
                listaKitsAtualParaAtualizar.add(kitAtual);
            }

        }

        /* remove a lista atual de Kits */
        programaAtual.getProgramaKits().clear();

        /* adiciona os novos na lista de Kits */
        for (ProgramaKit kitNovo : kitsDoPrograma) {
            if (kitNovo.getId() == null) {
                /* atender referencia bidirecional */
                kitNovo.setPrograma(programaAtual);
                programaAtual.getProgramaKits().add(kitNovo);
            }
        }

        /* atualiza atributos nos kits vindos do service para persistir */
        for (ProgramaKit kitParaAtualizar : listaKitsAtualParaAtualizar) {
            for (ProgramaKit kitDoPrograma : kitsDoPrograma) {
                if (kitDoPrograma.getId() != null && kitDoPrograma.getId().equals(kitParaAtualizar.getId())) {
                    kitParaAtualizar.setQuantidade(kitDoPrograma.getQuantidade());
                    kitParaAtualizar.setQuantidadePorProposta(kitDoPrograma.getQuantidadePorProposta());
                    listaKitsAdicionar.add(kitParaAtualizar);
                }
            }
        }
        // adiciona os Kits atualizados
        programaAtual.getProgramaKits().addAll(listaKitsAdicionar);
    }

    private void sincronizarProgramaBens(List<ProgramaBem> bensDoPrograma, Programa programaAtual) {

        List<ProgramaBem> listaBensAtualParaAtualizar = new ArrayList<ProgramaBem>();
        List<ProgramaBem> listaBensAdicionar = new ArrayList<ProgramaBem>();

        /*
         * seleciona apenas os Bens que foram mantidas na lista vinda do service
         */
        for (ProgramaBem bemAtual : programaAtual.getProgramaBens()) {
            if (bensDoPrograma.contains(bemAtual)) {
                listaBensAtualParaAtualizar.add(bemAtual);
            }

        }

        /* remove a lista atual de Bens */
        programaAtual.getProgramaBens().clear();

        /* adiciona os novos na lista de Bens */
        for (ProgramaBem bemNovo : bensDoPrograma) {
            if (bemNovo.getId() == null) {
                /* atender referencia bidirecional */
                bemNovo.setPrograma(programaAtual);
                programaAtual.getProgramaBens().add(bemNovo);
            }
        }

        /* atualiza atributos nos bens vindos do service para persistir */
        for (ProgramaBem bemParaAtualizar : listaBensAtualParaAtualizar) {
            for (ProgramaBem bemDoPrograma : bensDoPrograma) {
                if (bemDoPrograma.getId() != null && bemDoPrograma.getId().equals(bemParaAtualizar.getId())) {
                    bemParaAtualizar.setQuantidade(bemDoPrograma.getQuantidade());
                    bemParaAtualizar.setQuantidadePorProposta(bemDoPrograma.getQuantidadePorProposta());
                    listaBensAdicionar.add(bemParaAtualizar);
                }
            }
        }
        // adiciona os Bens atualizados
        programaAtual.getProgramaBens().addAll(listaBensAdicionar);
    }

    private void sincronizarRecursosFinanceiros(List<ProgramaRecursoFinanceiro> recursosDoPrograma, Programa programaAtual) {

        List<ProgramaRecursoFinanceiro> listaRecursosAtualParaAtualizar = new ArrayList<ProgramaRecursoFinanceiro>();
        List<ProgramaRecursoFinanceiro> listaRecursosAdicionar = new ArrayList<ProgramaRecursoFinanceiro>();

        /*
         * seleciona apenas os Recursos que foram mantidas na lista vinda do
         * service
         */
        for (ProgramaRecursoFinanceiro recursoAtual : programaAtual.getRecursosFinanceiros()) {
            if (recursosDoPrograma.contains(recursoAtual)) {
                listaRecursosAtualParaAtualizar.add(recursoAtual);
            }

        }

        /* remove a lista atual de Recursos */
        programaAtual.getRecursosFinanceiros().clear();

        /* adiciona os novos na lista de Recursos */
        for (ProgramaRecursoFinanceiro recursoNovo : recursosDoPrograma) {
            if (recursoNovo.getId() == null) {
                /* atender referencia bidirecional */
                recursoNovo.setPrograma(programaAtual);

                List<RecursoFinanceiroEmenda> listaRecursoFinanceiroEmendaNovo = new ArrayList<RecursoFinanceiroEmenda>();
                for (RecursoFinanceiroEmenda recursoFinanceiroEmendaNovo : recursoNovo.getRecursoFinanceiroEmendas()) {
                    /* atender referencia bidirecional */
                    recursoFinanceiroEmendaNovo.setRecursoFinanceiro(recursoNovo);
                    listaRecursoFinanceiroEmendaNovo.add(recursoFinanceiroEmendaNovo);
                }
                recursoNovo.setRecursoFinanceiroEmendas(listaRecursoFinanceiroEmendaNovo);
                programaAtual.getRecursosFinanceiros().add(recursoNovo);
            }
        }

        /* atualiza atributos nos recursos vindos do service para persistir */
        for (ProgramaRecursoFinanceiro recursoParaAtualizar : listaRecursosAtualParaAtualizar) {
            for (ProgramaRecursoFinanceiro recursoDoPrograma : recursosDoPrograma) {
                if (recursoDoPrograma.getId() != null && recursoDoPrograma.getId().equals(recursoParaAtualizar.getId())) {

                    sincronizarEmendas(recursoDoPrograma.getRecursoFinanceiroEmendas(), recursoParaAtualizar);
                    recursoParaAtualizar.setValorUtilizar(recursoDoPrograma.getValorUtilizar());
                    listaRecursosAdicionar.add(recursoParaAtualizar);
                }
            }
        }
        // adiciona os Recursos atualizados
        programaAtual.getRecursosFinanceiros().addAll(listaRecursosAdicionar);
    }

    private void sincronizarEmendas(List<RecursoFinanceiroEmenda> emendasDoRecurso, ProgramaRecursoFinanceiro recursoAtual) {

        List<RecursoFinanceiroEmenda> listaEmendasAtualParaAtualizar = new ArrayList<RecursoFinanceiroEmenda>();
        List<RecursoFinanceiroEmenda> listaEmendasAdicionar = new ArrayList<RecursoFinanceiroEmenda>();

        /*
         * seleciona apenas as Emendas que foram mantidas na lista vinda do
         * service
         */
        for (RecursoFinanceiroEmenda emendaAtual : recursoAtual.getRecursoFinanceiroEmendas()) {
            if (emendasDoRecurso.contains(emendaAtual)) {
                listaEmendasAtualParaAtualizar.add(emendaAtual);
            }

        }

        /* remove a lista atual de Emendas */
        recursoAtual.getRecursoFinanceiroEmendas().clear();

        /* adiciona os novos na lista de Emendas */
        for (RecursoFinanceiroEmenda emendaNovo : emendasDoRecurso) {
            if (emendaNovo.getId() == null) {
                /* atender referencia bidirecional */
                emendaNovo.setRecursoFinanceiro(recursoAtual);
                recursoAtual.getRecursoFinanceiroEmendas().add(emendaNovo);
            }
        }

        /* atualiza atributos nas emendas vindos do service para persistir */
        for (RecursoFinanceiroEmenda emendaParaAtualizar : listaEmendasAtualParaAtualizar) {
            for (RecursoFinanceiroEmenda emendaDoRecurso : emendasDoRecurso) {
                if (emendaDoRecurso.getId() != null && emendaDoRecurso.getId().equals(emendaParaAtualizar.getId())) {

                    emendaParaAtualizar.setValorUtilizar(emendaDoRecurso.getValorUtilizar());
                    listaEmendasAdicionar.add(emendaParaAtualizar);
                }
            }
        }
        // adiciona os Emendas atualizadas
        recursoAtual.getRecursoFinanceiroEmendas().addAll(listaEmendasAdicionar);
    }

    private void sincronizarPotenciaisBeneficiariosUf(List<ProgramaPotencialBeneficiarioUf> ufsDoPrograma, Programa programaAtual) {

        List<ProgramaPotencialBeneficiarioUf> listaUfsAtualParaAtualizar = new ArrayList<ProgramaPotencialBeneficiarioUf>();
        List<ProgramaPotencialBeneficiarioUf> listaUfsAdicionar = new ArrayList<ProgramaPotencialBeneficiarioUf>();

        /*
         * seleciona apenas os Ufs que foram mantidas na lista vinda do service
         */
        for (ProgramaPotencialBeneficiarioUf ufAtual : programaAtual.getPotenciaisBeneficiariosUf()) {
            if (ufsDoPrograma.contains(ufAtual)) {
                listaUfsAtualParaAtualizar.add(ufAtual);
            }

        }

        /* remove a lista atual de Ufs */
        programaAtual.getPotenciaisBeneficiariosUf().clear();

        /* adiciona os novos na lista de Ufs */
        for (ProgramaPotencialBeneficiarioUf ufNovo : ufsDoPrograma) {
            if (ufNovo.getId() == null) {
                /* atender referencia bidirecional */
                ufNovo.setPrograma(programaAtual);
                programaAtual.getPotenciaisBeneficiariosUf().add(ufNovo);
            }
        }

        /* atualiza atributos nos uf vindos do service para persistir */
        for (ProgramaPotencialBeneficiarioUf ufParaAtualizar : listaUfsAtualParaAtualizar) {
            for (ProgramaPotencialBeneficiarioUf ufDoPrograma : ufsDoPrograma) {
                if (ufDoPrograma.getId() != null && ufDoPrograma.getId().equals(ufParaAtualizar.getId())) {

                    sincronizarMunicipios(ufDoPrograma.getPotencialBeneficiarioMunicipios(), ufParaAtualizar);
                    listaUfsAdicionar.add(ufParaAtualizar);
                }
            }
        }
        // adiciona os Ufs atualizados
        programaAtual.getPotenciaisBeneficiariosUf().addAll(listaUfsAdicionar);
    }

    private void sincronizarMunicipios(List<PotencialBeneficiarioMunicipio> municipiosDaUf, ProgramaPotencialBeneficiarioUf ufAtual) {

        List<PotencialBeneficiarioMunicipio> listaMunicipiosAtualParaAtualizar = new ArrayList<PotencialBeneficiarioMunicipio>();

        /*
         * seleciona apenas os Municipios que foram mantidas na lista vinda do
         * service
         */
        for (PotencialBeneficiarioMunicipio municipioAtual : ufAtual.getPotencialBeneficiarioMunicipios()) {
            if (municipiosDaUf.contains(municipioAtual)) {
                listaMunicipiosAtualParaAtualizar.add(municipioAtual);
            }

        }

        /* remove a lista atual de Municipios */
        ufAtual.getPotencialBeneficiarioMunicipios().clear();

        /* adiciona os novos na lista de Municipios */
        for (PotencialBeneficiarioMunicipio municipioNovo : municipiosDaUf) {
            if (municipioNovo.getId() == null) {
                /* atender referencia bidirecional */
                municipioNovo.setPotencialBeneficiarioUF(ufAtual);
                ufAtual.getPotencialBeneficiarioMunicipios().add(municipioNovo);
            }
        }

        // adiciona os Municipios atualizados
        ufAtual.getPotencialBeneficiarioMunicipios().addAll(listaMunicipiosAtualParaAtualizar);
    }

    private void sincronizarCriteriosAvaliacao(List<ProgramaCriterioAvaliacao> criteriosAvaliacaoDoPrograma, Programa programaAtual) {

        List<ProgramaCriterioAvaliacao> listaCriteriosAvaliacaoAtualParaAtualizar = new ArrayList<ProgramaCriterioAvaliacao>();
        List<ProgramaCriterioAvaliacao> listaCriteriosAvaliacaoAdicionar = new ArrayList<ProgramaCriterioAvaliacao>();

        /*
         * seleciona apenas os CriteriosAvaliacao que foram mantidas na lista
         * vinda do service
         */
        for (ProgramaCriterioAvaliacao programaCriterioAvaliacaoAtual : programaAtual.getCriteriosAvaliacao()) {
            if (criteriosAvaliacaoDoPrograma.contains(programaCriterioAvaliacaoAtual)) {
                listaCriteriosAvaliacaoAtualParaAtualizar.add(programaCriterioAvaliacaoAtual);
            }

        }

        /* remove a lista atual de CriteriosAvaliacao */
        programaAtual.getCriteriosAvaliacao().clear();

        /* adiciona os novos na lista de CriteriosAvaliacao */
        for (ProgramaCriterioAvaliacao programaCriterioAvaliacaoNovo : criteriosAvaliacaoDoPrograma) {
            if (programaCriterioAvaliacaoNovo.getId() == null) {
                /* atender referencia bidirecional */
                programaCriterioAvaliacaoNovo.setPrograma(programaAtual);

                List<ProgramaCriterioAvaliacaoOpcaoResposta> listaCriterioAvaliacaoOpcaoRespostaNovo = new ArrayList<ProgramaCriterioAvaliacaoOpcaoResposta>();
                for (ProgramaCriterioAvaliacaoOpcaoResposta criterioAvaliacaoOpcaoRespostaNovo : programaCriterioAvaliacaoNovo.getCriteriosAvaliacaoOpcaoResposta()) {
                    /* atender referencia bidirecional */
                    criterioAvaliacaoOpcaoRespostaNovo.setCriterioAvaliacao(programaCriterioAvaliacaoNovo);
                    listaCriterioAvaliacaoOpcaoRespostaNovo.add(criterioAvaliacaoOpcaoRespostaNovo);
                }
                programaCriterioAvaliacaoNovo.setCriteriosAvaliacaoOpcaoResposta(listaCriterioAvaliacaoOpcaoRespostaNovo);
                programaAtual.getCriteriosAvaliacao().add(programaCriterioAvaliacaoNovo);

            }
        }

        /*
         * atualiza atributos nos CriteriosAvaliacao vindos do service para
         * persistir
         */
        for (ProgramaCriterioAvaliacao programaCriterioAvaliacaoParaAtualizar : listaCriteriosAvaliacaoAtualParaAtualizar) {
            for (ProgramaCriterioAvaliacao programaCriterioAvaliacaoDoPrograma : criteriosAvaliacaoDoPrograma) {
                if (programaCriterioAvaliacaoDoPrograma.getId() != null && programaCriterioAvaliacaoDoPrograma.getId().equals(programaCriterioAvaliacaoParaAtualizar.getId())) {

                    programaCriterioAvaliacaoParaAtualizar.setNomeCriterioAvaliacao(programaCriterioAvaliacaoDoPrograma.getNomeCriterioAvaliacao());
                    programaCriterioAvaliacaoParaAtualizar.setDescricaoCriterioAvaliacao(programaCriterioAvaliacaoDoPrograma.getDescricaoCriterioAvaliacao());
                    programaCriterioAvaliacaoParaAtualizar.setFormaVerificacao(programaCriterioAvaliacaoDoPrograma.getFormaVerificacao());
                    programaCriterioAvaliacaoParaAtualizar.setTipoResposta(programaCriterioAvaliacaoDoPrograma.getTipoResposta());
                    programaCriterioAvaliacaoParaAtualizar.setPesoResposta(programaCriterioAvaliacaoDoPrograma.getPesoResposta());
                    programaCriterioAvaliacaoParaAtualizar.setPossuiObrigatoriedadeDeAnexo(programaCriterioAvaliacaoDoPrograma.getPossuiObrigatoriedadeDeAnexo());
                    programaCriterioAvaliacaoParaAtualizar.setUtilizadoParaCriterioDesempate(programaCriterioAvaliacaoDoPrograma.getUtilizadoParaCriterioDesempate());

                    sincronizarCriteriosAvaliacaoOpcaoResposta(programaCriterioAvaliacaoDoPrograma.getCriteriosAvaliacaoOpcaoResposta(), programaCriterioAvaliacaoParaAtualizar);
                    listaCriteriosAvaliacaoAdicionar.add(programaCriterioAvaliacaoParaAtualizar);
                }
            }
        }

        // adiciona os CriteriosAvaliacao atualizados
        programaAtual.getCriteriosAvaliacao().addAll(listaCriteriosAvaliacaoAdicionar);
    }

    private void sincronizarCriteriosAvaliacaoOpcaoResposta(List<ProgramaCriterioAvaliacaoOpcaoResposta> respostasDaAvaliacao, ProgramaCriterioAvaliacao avaliacaoAtual) {

        List<ProgramaCriterioAvaliacaoOpcaoResposta> listaRespostasAtualParaAtualizar = new ArrayList<ProgramaCriterioAvaliacaoOpcaoResposta>();

        /*
         * seleciona apenas as respostas que foram mantidas na lista vinda do
         * service
         */
        for (ProgramaCriterioAvaliacaoOpcaoResposta respostaAtual : avaliacaoAtual.getCriteriosAvaliacaoOpcaoResposta()) {
            if (respostasDaAvaliacao.contains(respostaAtual)) {
                listaRespostasAtualParaAtualizar.add(respostaAtual);
            }

        }

        /* remove a lista atual de respostas */
        avaliacaoAtual.getCriteriosAvaliacaoOpcaoResposta().clear();

        /* adiciona os novos na lista de respostas */
        for (ProgramaCriterioAvaliacaoOpcaoResposta respostaNovo : respostasDaAvaliacao) {
            if (respostaNovo.getId() == null) {
                /* atender referencia bidirecional */
                respostaNovo.setCriterioAvaliacao(avaliacaoAtual);
                avaliacaoAtual.getCriteriosAvaliacaoOpcaoResposta().add(respostaNovo);
            }
        }
        // adiciona as respostas atualizados
        avaliacaoAtual.getCriteriosAvaliacaoOpcaoResposta().addAll(listaRespostasAtualParaAtualizar);
    }

    public List<ProgramaRecursoFinanceiro> buscarProgramaRecursoFinanceiroPeloIdPrograma(Long id) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<ProgramaRecursoFinanceiro> criteriaQuery = criteriaBuilder.createQuery(ProgramaRecursoFinanceiro.class);
        Root<ProgramaRecursoFinanceiro> root = criteriaQuery.from(ProgramaRecursoFinanceiro.class);
        List<Predicate> predicates = new ArrayList<>();
        if (id != null) {
            predicates.add(criteriaBuilder.equal(root.get(PROGRAMA).get("id"), id));
        }
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<ProgramaRecursoFinanceiro> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }
    
    //Ira retornar todos os bens do programa, incluindo os bens que estão em kits.
    public List<Bem> buscarTodosOsBensDoPrograma(Programa programa) {
        List<ProgramaBem> todosOsBens = buscarOsBensDoPrograma(programa);
        List<ProgramaKit> todosOsKits = buscarTodosOsKitsDoPrograma(programa);

        Map<Long, Bem> mapa = new HashMap<Long, Bem>();

        for (ProgramaBem bem : todosOsBens) {

            Long mapaBem = bem.getId();
            if (!mapa.containsKey(mapaBem)) {
                mapa.put(mapaBem, bem.getBem());
                continue;
            }
        }

        for (ProgramaKit kit : todosOsKits) {

            Kit kitPrograma = kit.getKit();
            for (KitBem bemKit : kitPrograma.getKitsBens()) {

                Long mapaBem = bemKit.getBem().getId();
                if (!mapa.containsKey(mapaBem)) {
                    mapa.put(mapaBem, bemKit.getBem());
                    continue;
                }
            }
        }

        List<Bem> listaDeBens = new ArrayList<Bem>();
        listaDeBens.addAll(mapa.values());
        return listaDeBens;
    }
    
    public List<ProgramaBem> buscarOsBensDoPrograma(Programa programa){
        
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<ProgramaBem> criteriaQuery = criteriaBuilder.createQuery(ProgramaBem.class);
        Root<ProgramaBem> root = criteriaQuery.from(ProgramaBem.class);
        List<Predicate> predicates = new ArrayList<>();
        
        predicates.add(criteriaBuilder.equal(root.get(PROGRAMA).get("id"), programa.getId()));
        
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<ProgramaBem> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }
    
    public List<ProgramaKit> buscarTodosOsKitsDoPrograma(Programa programa){
        
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<ProgramaKit> criteriaQuery = criteriaBuilder.createQuery(ProgramaKit.class);
        Root<ProgramaKit> root = criteriaQuery.from(ProgramaKit.class);
        List<Predicate> predicates = new ArrayList<>();
        
        predicates.add(criteriaBuilder.equal(root.get(PROGRAMA).get("id"), programa.getId()));
        
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<ProgramaKit> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    private String limparCnpj(String valor) {
        String value = "";
        if (valor != null) {
            value = valor;
            value = value.replace(".", "");
            value = value.replace("/", "");
            value = value.replace("-", "");
        }
        return value;
    }

    public List<Programa> buscarPorStatus(EnumStatusPrograma statusPrograma) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Programa> criteriaQuery = criteriaBuilder.createQuery(Programa.class);
        Root<Programa> root = criteriaQuery.from(Programa.class);
        List<Predicate> predicates = new ArrayList<>();

        if (statusPrograma != null) {
            predicates.add(criteriaBuilder.equal(root.get("statusPrograma"), statusPrograma));
        }
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<Programa> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

}
