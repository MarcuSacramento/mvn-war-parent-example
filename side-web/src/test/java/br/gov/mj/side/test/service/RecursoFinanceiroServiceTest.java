package br.gov.mj.side.test.service;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJBException;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.gov.mj.apoio.entidades.SubFuncao;
import br.gov.mj.apoio.entidades.UnidadeExecutora;
import br.gov.mj.infra.negocio.exception.BusinessException;
import br.gov.mj.infra.negocio.persistencia.IGenericPersister;
import br.gov.mj.side.entidades.AcaoOrcamentaria;
import br.gov.mj.side.entidades.EmendaParlamentar;
import br.gov.mj.side.entidades.enums.EnumPersonalidadeJuridica;
import br.gov.mj.side.entidades.enums.EnumStatusPrograma;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.ProgramaBem;
import br.gov.mj.side.entidades.programa.ProgramaCriterioAcompanhamento;
import br.gov.mj.side.entidades.programa.ProgramaCriterioAvaliacao;
import br.gov.mj.side.entidades.programa.ProgramaCriterioElegibilidade;
import br.gov.mj.side.entidades.programa.ProgramaKit;
import br.gov.mj.side.entidades.programa.ProgramaPotencialBeneficiarioUf;
import br.gov.mj.side.entidades.programa.ProgramaRecursoFinanceiro;
import br.gov.mj.side.entidades.programa.RecursoFinanceiroEmenda;
import br.gov.mj.side.web.dao.AnexoProgramaDAO;
import br.gov.mj.side.web.dao.ProgramaDAO;
import br.gov.mj.side.web.dao.PublicizacaoDAO;
import br.gov.mj.side.web.dao.RecursoFinanceiroDAO;
import br.gov.mj.side.web.dto.AcaoEmendaComSaldoDto;
import br.gov.mj.side.web.dto.EmendaComSaldoDto;
import br.gov.mj.side.web.dto.ProgramaPesquisaDto;
import br.gov.mj.side.web.service.AnexoProgramaService;
import br.gov.mj.side.web.service.ProgramaService;
import br.gov.mj.side.web.service.RecursoFinanceiroService;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.ResourcesProducer;
import br.gov.mj.side.web.util.SideUtil;
import br.gov.mj.side.web.util.UtilDAO;

@RunWith(Arquillian.class)
public class RecursoFinanceiroServiceTest {

    private static final String USUARIO_LOGADO = "usuario.logado";

    @PersistenceContext
    EntityManager em;

    @Inject
    UserTransaction tx;

    @Inject
    IGenericPersister genericPersister;

    @Inject
    private RecursoFinanceiroService recursoFinanceiroService;

    @Inject
    private ProgramaService programaService;

    @Inject
    private AnexoProgramaService anexoProgramaService;

    @Deployment
    public static Archive<?> createDeployment() {
        File[] jars = Maven.configureResolver().withRemoteRepo("artifactory-mj", "http://cgtimaven.mj.gov.br/artifactory/repo/", "default").resolve("br.gov.mj.infra:infra-negocio:1.2.0", "org.jacoco:org.jacoco.core:0.7.5.201505241946", "org.jadira.usertype:usertype.extended:3.2.0.GA")
                .withTransitivity().asFile();

        Archive<?> file = ShrinkWrap.create(WebArchive.class, "side-test.war").addPackages(true, "br.gov.mj.seg.entidades").addPackages(true, "br.gov.mj.apoio.entidades").addPackages(true, "br.gov.mj.side.entidades").addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                .addAsResource("ufs.csv", "ufs.csv").addAsResource("municipios.csv", "municipios.csv").addAsResource("funcao.csv", "funcao.csv").addAsResource("subfuncao.csv", "subfuncao.csv").addAsResource("orgao.csv", "orgao.csv").addAsResource("unidadeexecutora.csv", "unidadeexecutora.csv")
                .addAsResource("bem.csv", "bem.csv").addAsResource("init.txt", "init.txt").addAsResource("subelementos.csv", "subelementos.csv").addAsResource("elementos.csv", "elementos.csv").addAsResource("beneficiarioemenda.csv", "beneficiarioemenda.csv")
                .addAsResource("emendaparlamentar.csv", "emendaparlamentar.csv").addAsResource("acaoorcamentaria.csv", "acaoorcamentaria.csv").addAsResource("partidos.csv", "partidos.csv").addAsResource("kit.csv", "kit.csv").addAsResource("kitbem.csv", "kitbem.csv")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml").addAsWebInfResource("side-test-ds.xml", "side-test-ds.xml").addAsWebInfResource("test-jboss-web.xml", "jboss-web.xml").addClass(ResourcesProducer.class).addClass(ProgramaDAO.class).addClass(UtilDAO.class)
                .addClass(PublicizacaoDAO.class).addClass(ProgramaPesquisaDto.class).addClass(ProgramaService.class).addClass(AnexoProgramaService.class).addClass(AnexoProgramaDAO.class).addClass(Constants.class).addClass(RecursoFinanceiroDAO.class).addClass(RecursoFinanceiroService.class)
                .addClass(AcaoEmendaComSaldoDto.class).addClass(EmendaComSaldoDto.class).addAsLibraries(jars);
        System.out.println(file.toString(true));
        return file;
    }

    @Test(expected = IllegalArgumentException.class)
    @InSequence(1)
    public void testBuscarComNull() throws Throwable {

        try {
            recursoFinanceiroService.buscarSaldoAcaoOrcamentaria(null);
        } catch (EJBException e) {
            Throwable th = e.getCause();
            throw th;
        }

    }

    @Test(expected = IllegalArgumentException.class)
    @InSequence(2)
    public void testBuscarComObjetoSemId() throws Throwable {

        AcaoOrcamentaria acaoOrcamentaria = new AcaoOrcamentaria();

        try {
            recursoFinanceiroService.buscarSaldoAcaoOrcamentaria(acaoOrcamentaria);
        } catch (EJBException e) {
            Throwable th = e.getCause();
            throw th;
        }

    }

    @Test(expected = BusinessException.class)
    @InSequence(3)
    public void testBuscarComIdInexistente() throws Throwable {

        AcaoOrcamentaria acaoOrcamentaria = new AcaoOrcamentaria();
        acaoOrcamentaria.setId(3l);

        try {
            recursoFinanceiroService.buscarSaldoAcaoOrcamentaria(acaoOrcamentaria);
        } catch (EJBException e) {
            Throwable th = e.getCause();
            throw th;
        }

    }

    @Test
    @InSequence(4)
    public void testBuscarSemRetirar() {
        AcaoOrcamentaria acaoOrcamentaria = new AcaoOrcamentaria();
        acaoOrcamentaria.setId(1l);
        AcaoEmendaComSaldoDto acaoEmendaComSaldoDto = recursoFinanceiroService.buscarSaldoAcaoOrcamentaria(acaoOrcamentaria);
        Assert.assertTrue(acaoEmendaComSaldoDto.getSaldoAcaoOrcamentaria().compareTo(new BigDecimal(1000.50)) == 0);
        Assert.assertTrue(acaoEmendaComSaldoDto.getListaSaldoEmenda().get(0).getSaldo().compareTo(new BigDecimal(100.50)) == 0);
    }

    @Test
    @InSequence(5)
    public void testBuscarRetirando() {

        Programa programa = new Programa();

        programa.setNomePrograma("nome programa");
        programa.setNomeFantasiaPrograma("nome fantasia programa");
        programa.setDescricaoPrograma("descricao programa");
        programa.setSubFuncao(em.find(SubFuncao.class, 31l));
        programa.setUnidadeExecutora(em.find(UnidadeExecutora.class, 1l));
        programa.setNumeroProcessoSEI("11111");
        programa.setPossuiLimitacaoGeografica(false);
        programa.setPossuiLimitacaoMunicipalEspecifica(false);
        programa.setTipoPersonalidadeJuridica(EnumPersonalidadeJuridica.TODAS);
        programa.setUsuarioCadastro(USUARIO_LOGADO);
        programa.setDataCadastro(LocalDateTime.now());
        programa.setAnoPrograma(2015);
        programa.setValorMaximoProposta(BigDecimal.ONE);
        programa.setStatusPrograma(EnumStatusPrograma.EM_ELABORACAO);
        programaService.incluirAlterar(programa, USUARIO_LOGADO);

        Programa programaAltera = em.find(Programa.class, programa.getId());
        programaAltera.setAnexos(SideUtil.convertAnexoDtoToEntityProgramaAnexo(anexoProgramaService.buscarPeloIdPrograma(programa.getId())));

        programaAltera.setStatusPrograma(EnumStatusPrograma.EM_ELABORACAO);

        List<ProgramaRecursoFinanceiro> listaRecursosFinanceiros = new ArrayList<ProgramaRecursoFinanceiro>();

        ProgramaRecursoFinanceiro recursoFinanceiro = new ProgramaRecursoFinanceiro();

        recursoFinanceiro.setAcaoOrcamentaria(em.find(AcaoOrcamentaria.class, 1l));
        recursoFinanceiro.setValorUtilizar(new BigDecimal("100.10"));

        List<RecursoFinanceiroEmenda> listaEmendas = new ArrayList<RecursoFinanceiroEmenda>();

        RecursoFinanceiroEmenda emenda1 = new RecursoFinanceiroEmenda();
        emenda1.setRecursoFinanceiro(recursoFinanceiro);
        emenda1.setValorUtilizar(new BigDecimal("8.05"));
        emenda1.setEmendaParlamentar(em.find(EmendaParlamentar.class, 1l));
        listaEmendas.add(emenda1);
        recursoFinanceiro.setRecursoFinanceiroEmendas(listaEmendas);
        listaRecursosFinanceiros.add(recursoFinanceiro);
        programaAltera.setRecursosFinanceiros(listaRecursosFinanceiros);
        programaAltera.setPotenciaisBeneficiariosUf(new ArrayList<ProgramaPotencialBeneficiarioUf>());
        programaAltera.setProgramaBens(new ArrayList<ProgramaBem>());
        programaAltera.setProgramaKits(new ArrayList<ProgramaKit>());
        programaAltera.setCriteriosElegibilidade(new ArrayList<ProgramaCriterioElegibilidade>());
        programaAltera.setCriteriosAcompanhamento(new ArrayList<ProgramaCriterioAcompanhamento>());
        programaAltera.setCriteriosAvaliacao(new ArrayList<ProgramaCriterioAvaliacao>());

        programaService.incluirAlterar(programaAltera, USUARIO_LOGADO);

        AcaoOrcamentaria acaoOrcamentaria = new AcaoOrcamentaria();
        acaoOrcamentaria.setId(1l);
        AcaoEmendaComSaldoDto acaoEmendaComSaldoDto = recursoFinanceiroService.buscarSaldoAcaoOrcamentaria(acaoOrcamentaria);
        Assert.assertTrue(acaoEmendaComSaldoDto.getSaldoAcaoOrcamentaria().compareTo(new BigDecimal("1000.50")) == 0);
        Assert.assertTrue(acaoEmendaComSaldoDto.getListaSaldoEmenda().get(0).getSaldo().compareTo(new BigDecimal("100.50")) == 0);
    }

    @Before
    public void createData() throws Exception {

        // tx.begin();
        //
        // em.createNativeQuery("drop sequence SIDE.SEQ_TB_ABM_ANEXO_BEM").executeUpdate();
        // em.createNativeQuery("drop sequence SIDE.SEQ_TB_ACO_ACAO_ORCAMENTARIA").executeUpdate();
        // em.createNativeQuery("drop sequence SIDE.SEQ_TB_BEM_BEM").executeUpdate();
        // em.createNativeQuery("drop sequence SIDE.SEQ_TB_BEP_BENEFICIARIO_EMENDA_PARLAMENTAR").executeUpdate();
        // em.createNativeQuery("drop sequence SIDE.SEQ_TB_EPA_EMENDA_PARLAMENTAR").executeUpdate();
        // em.createNativeQuery("drop sequence SIDE.SEQ_TB_KIB_KIT_BEM").executeUpdate();
        // em.createNativeQuery("drop sequence SIDE.SEQ_TB_KIT_KIT").executeUpdate();
        // em.createNativeQuery("drop sequence SIDE.SEQ_TB_PAN_PROGRAMA_ANEXO").executeUpdate();
        // em.createNativeQuery("drop sequence SIDE.SEQ_TB_PRG_PROGRAMA").executeUpdate();
        // em.createNativeQuery("drop sequence SIDE.SEQ_TB_TBM_TAG_BEM").executeUpdate();
        // em.createNativeQuery("drop sequence side.seq_tb_pca_programa_criterio_acompanhamento").executeUpdate();
        // em.createNativeQuery("drop sequence side.seq_tb_pce_programa_criterio_elegibilidade").executeUpdate();
        // em.createNativeQuery("drop sequence side.seq_tb_ppm_programa_potencial_beneficiario_municipio").executeUpdate();
        // em.createNativeQuery("drop sequence side.seq_tb_ppu_programa_potencial_beneficiario_uf").executeUpdate();
        // em.createNativeQuery("drop sequence side.seq_tb_prb_programa_bem").executeUpdate();
        // em.createNativeQuery("drop sequence side.seq_tb_prf_programa_recurso_financeiro").executeUpdate();
        // em.createNativeQuery("drop sequence side.seq_tb_prk_programa_kit").executeUpdate();
        // em.createNativeQuery("drop sequence side.seq_tb_rfe_recurso_financeiro_emenda_parlamentar").executeUpdate();
        // em.createNativeQuery("create sequence SIDE.SEQ_TB_ABM_ANEXO_BEM start with 1 increment by 1").executeUpdate();
        // em.createNativeQuery("create sequence SIDE.SEQ_TB_ACO_ACAO_ORCAMENTARIA start with 1 increment by 1").executeUpdate();
        // em.createNativeQuery("create sequence SIDE.SEQ_TB_BEM_BEM start with 1 increment by 1").executeUpdate();
        // em.createNativeQuery("create sequence SIDE.SEQ_TB_BEP_BENEFICIARIO_EMENDA_PARLAMENTAR start with 1 increment by 1").executeUpdate();
        // em.createNativeQuery("create sequence SIDE.SEQ_TB_EPA_EMENDA_PARLAMENTAR start with 1 increment by 1").executeUpdate();
        // em.createNativeQuery("create sequence SIDE.SEQ_TB_KIB_KIT_BEM start with 1 increment by 1").executeUpdate();
        // em.createNativeQuery("create sequence SIDE.SEQ_TB_KIT_KIT start with 1 increment by 1").executeUpdate();
        // em.createNativeQuery("create sequence SIDE.SEQ_TB_PAN_PROGRAMA_ANEXO start with 1 increment by 1").executeUpdate();
        // em.createNativeQuery("create sequence SIDE.SEQ_TB_PRG_PROGRAMA start with 1 increment by 1").executeUpdate();
        // em.createNativeQuery("create sequence SIDE.SEQ_TB_TBM_TAG_BEM start with 1 increment by 1").executeUpdate();
        // em.createNativeQuery("create sequence side.seq_tb_pca_programa_criterio_acompanhamento start with 1 increment by 1").executeUpdate();
        // em.createNativeQuery("create sequence side.seq_tb_pce_programa_criterio_elegibilidade start with 1 increment by 1").executeUpdate();
        // em.createNativeQuery("create sequence side.seq_tb_ppm_programa_potencial_beneficiario_municipio start with 1 increment by 1").executeUpdate();
        // em.createNativeQuery("create sequence side.seq_tb_ppu_programa_potencial_beneficiario_uf start with 1 increment by 1").executeUpdate();
        // em.createNativeQuery("create sequence side.seq_tb_prb_programa_bem start with 1 increment by 1").executeUpdate();
        // em.createNativeQuery("create sequence side.seq_tb_prf_programa_recurso_financeiro start with 1 increment by 1").executeUpdate();
        // em.createNativeQuery("create sequence side.seq_tb_prk_programa_kit start with 1 increment by 1").executeUpdate();
        // em.createNativeQuery("create sequence side.seq_tb_rfe_recurso_financeiro_emenda_parlamentar start with 1 increment by 1").executeUpdate();
        //
        // em.createQuery("delete from PotencialBeneficiarioMunicipio").executeUpdate();
        // em.createQuery("delete from ProgramaPotencialBeneficiarioUf").executeUpdate();
        // em.createQuery("delete from Programa").executeUpdate();
        // em.createQuery("delete from KitBem").executeUpdate();
        // em.createQuery("delete from Kit").executeUpdate();
        // em.createQuery("delete from Bem").executeUpdate();
        // em.createQuery("delete from SubElemento").executeUpdate();
        // em.createQuery("delete from Elemento").executeUpdate();
        // em.createQuery("delete from BeneficiarioEmendaParlamentar").executeUpdate();
        // em.createQuery("delete from EmendaParlamentar").executeUpdate();
        // em.createQuery("delete from AcaoOrcamentaria").executeUpdate();
        // em.createQuery("delete from PartidoPolitico").executeUpdate();
        // em.createQuery("delete from UnidadeExecutora").executeUpdate();
        // em.createQuery("delete from Orgao").executeUpdate();
        // em.createQuery("delete from SubFuncao").executeUpdate();
        // em.createQuery("delete from Funcao").executeUpdate();
        // em.createQuery("delete from Municipio").executeUpdate();
        // em.createQuery("delete from Uf").executeUpdate();
        // tx.commit();
        //
        // String line = "";
        //
        // /* Uf */
        // try (BufferedReader br = new BufferedReader(new
        // InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("/ufs.csv"),
        // "UTF-8"))) {
        //
        // while ((line = br.readLine()) != null) {
        // String[] values = line.split(";");
        //
        // Uf uf = new Uf();
        // uf.setId(new Long(values[0]));
        // uf.setNomeUf(values[1]);
        // uf.setSiglaUf(values[2]);
        // genericPersister.persist(uf);
        // }
        // } catch (Exception e) {
        // throw new RuntimeException(e);
        // }
        //
        // /* Municipio */
        // try (BufferedReader br = new BufferedReader(new
        // InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("/municipios.csv"),
        // "UTF-8"))) {
        //
        // while ((line = br.readLine()) != null) {
        // String[] values = line.split(";");
        //
        // Municipio municipio = new Municipio();
        // municipio.setId(new Long(values[0]));
        // municipio.setUf(genericPersister.findById(Uf.class, new
        // Long(values[1])));
        // municipio.setNomeMunicipio(values[2]);
        // genericPersister.persist(municipio);
        // }
        // } catch (Exception e) {
        // throw new RuntimeException(e);
        // }
        //
        // /* Funcao */
        // try (BufferedReader br = new BufferedReader(new
        // InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("/funcao.csv"),
        // "UTF-8"))) {
        //
        // while ((line = br.readLine()) != null) {
        // String[] values = line.split(";");
        //
        // Funcao f = new Funcao();
        // f.setId(new Long(values[0]));
        // f.setNomeFuncao(values[1]);
        // genericPersister.persist(f);
        // }
        // } catch (Exception e) {
        // throw new RuntimeException(e);
        // }
        //
        // /* SubFuncao */
        // try (BufferedReader br = new BufferedReader(new
        // InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("/subfuncao.csv"),
        // "UTF-8"))) {
        //
        // while ((line = br.readLine()) != null) {
        // String[] values = line.split(";");
        //
        // SubFuncao sf = new SubFuncao();
        // sf.setId(new Long(values[0]));
        // sf.setNomeSubFuncao(values[1]);
        // sf.setFuncao(genericPersister.findById(Funcao.class, new
        // Long(values[2])));
        // genericPersister.persist(sf);
        // }
        // } catch (Exception e) {
        // throw new RuntimeException(e);
        // }
        //
        // /* Orgao */
        // try (BufferedReader br = new BufferedReader(new
        // InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("/orgao.csv"),
        // "UTF-8"))) {
        //
        // while ((line = br.readLine()) != null) {
        // String[] values = line.split(";");
        //
        // Orgao o = new Orgao();
        // o.setId(new Long(values[0]));
        // o.setNomeOrgao(values[1]);
        // o.setSiglaOrgao(values[2]);
        // genericPersister.persist(o);
        // }
        // } catch (Exception e) {
        // throw new RuntimeException(e);
        // }
        //
        // /* Unidade Executora */
        // try (BufferedReader br = new BufferedReader(new
        // InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("/unidadeexecutora.csv"),
        // "UTF-8"))) {
        //
        // while ((line = br.readLine()) != null) {
        // String[] values = line.split(";");
        //
        // UnidadeExecutora uex = new UnidadeExecutora();
        // uex.setId(new Long(values[0]));
        // uex.setNomeUnidadeExecutora(values[1]);
        // uex.setOrgao(genericPersister.findById(Orgao.class, new
        // Long(values[2])));
        // genericPersister.persist(uex);
        // }
        // } catch (Exception e) {
        // throw new RuntimeException(e);
        // }
        //
        // /* partidos */
        // try (BufferedReader br = new BufferedReader(new
        // InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("/partidos.csv"),
        // "UTF-8"))) {
        //
        // while ((line = br.readLine()) != null) {
        // String[] values = line.split(";");
        //
        // PartidoPolitico pp = new PartidoPolitico();
        // pp.setId(new Long(values[0]));
        // pp.setNomePartido(values[1]);
        // pp.setSiglaPartido(values[2]);
        // genericPersister.persist(pp);
        // }
        // } catch (Exception e) {
        // throw new RuntimeException(e);
        // }
        //
        // /* acaoorcamentaria */
        // try (BufferedReader br = new BufferedReader(new
        // InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("/acaoorcamentaria.csv"),
        // "UTF-8"))) {
        //
        // while ((line = br.readLine()) != null) {
        // String[] values = line.split(";");
        // AcaoOrcamentaria ao = new AcaoOrcamentaria();
        // ao.setNumeroProgramaPPA(values[1]);
        // ao.setNomeProgramaPPA(values[2]);
        // ao.setNumeroAcaoOrcamentaria(values[3]);
        // ao.setNomeAcaoOrcamentaria(values[4]);
        // ao.setValorPrevisto(new BigDecimal(values[5]));
        // ao.setUsuarioCadastro(USUARIO_LOGADO);
        // ao.setDataCadastro(LocalDateTime.now());
        // ao.setUsuarioAlteracao(USUARIO_LOGADO);
        // ao.setDataAlteracao(LocalDateTime.now());
        // ao.setAnoAcaoOrcamentaria(2015);
        // genericPersister.persist(ao);
        // }
        // } catch (Exception e) {
        // throw new RuntimeException(e);
        // }
        //
        // /* emendaParlamentar */
        // try (BufferedReader br = new BufferedReader(new
        // InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("/emendaparlamentar.csv"),
        // "UTF-8"))) {
        //
        // while ((line = br.readLine()) != null) {
        // String[] values = line.split(";");
        // EmendaParlamentar ep = new EmendaParlamentar();
        // ep.setAcaoOrcamentaria(genericPersister.findById(AcaoOrcamentaria.class,
        // new Long(values[1])));
        // ep.setNumeroEmendaParlamantar(values[2]);
        // ep.setNomeEmendaParlamentar(values[3]);
        // ep.setPartidoPolitico(genericPersister.findById(PartidoPolitico.class,
        // new Long(values[4])));
        // ep.setUf(genericPersister.findById(Uf.class, new Long(values[5])));
        // ep.setNomeParlamentar(values[6]);
        // ep.setNomeCargoParlamentar(values[7]);
        // ep.setPossuiLiberacao(false);
        // ep.setValorPrevisto(new BigDecimal(values[9]));
        // ep.setTipoEmenda(EnumTipoEmenda.INDIVIDUAL);
        // genericPersister.persist(ep);
        // }
        // } catch (Exception e) {
        // throw new RuntimeException(e);
        // }
        //
        // /* beneficiarioEmendaParlamentar */
        // try (BufferedReader br = new BufferedReader(new
        // InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("/beneficiarioemenda.csv"),
        // "UTF-8"))) {
        //
        // while ((line = br.readLine()) != null) {
        // String[] values = line.split(";");
        // BeneficiarioEmendaParlamentar bep = new
        // BeneficiarioEmendaParlamentar();
        // bep.setEmendaParlamentar(genericPersister.findById(EmendaParlamentar.class,
        // new Long(values[1])));
        // bep.setNumeroCnpjBeneficiario(values[2]);
        // bep.setNomeBeneficiario(values[3]);
        // genericPersister.persist(bep);
        // }
        // } catch (Exception e) {
        // throw new RuntimeException(e);
        // }
        //
        // /* elementos */
        // try (BufferedReader br = new BufferedReader(new
        // InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("/elementos.csv"),
        // "UTF-8"))) {
        //
        // while ((line = br.readLine()) != null) {
        // String[] values = line.split(";");
        //
        // Elemento e = new Elemento();
        // e.setId(new Long(values[0]));
        // e.setNomeElemento(values[1]);
        // genericPersister.persist(e);
        // }
        // } catch (Exception e) {
        // throw new RuntimeException(e);
        // }
        //
        // /* subelementos */
        // try (BufferedReader br = new BufferedReader(new
        // InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("/subelementos.csv"),
        // "UTF-8"))) {
        //
        // while ((line = br.readLine()) != null) {
        // String[] values = line.split(";");
        //
        // SubElemento se = new SubElemento();
        // se.setId(new Long(values[0]));
        // se.setNomeSubElemento(values[1]);
        // Elemento e = new Elemento();
        // e = genericPersister.findById(Elemento.class, new Long(values[2]));
        // se.setElemento(e);
        // genericPersister.persist(se);
        // }
        // } catch (Exception e) {
        // throw new RuntimeException(e);
        // }
        //
        // /* Bem */
        // try (BufferedReader br = new BufferedReader(new
        // InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("/bem.csv"),
        // "UTF-8"))) {
        //
        // while ((line = br.readLine()) != null) {
        // String[] values = line.split(";");
        // Bem b = new Bem();
        // b.setNomeBem(values[1]);
        // b.setDescricaoBem(values[2]);
        // b.setValorEstimadoBem(new BigDecimal(values[3]));
        // b.setSubElemento(genericPersister.findById(SubElemento.class, new
        // Long(values[4])));
        // genericPersister.persist(b);
        // }
        // } catch (Exception e) {
        // throw new RuntimeException(e);
        // }
        //
        // /* Kit */
        // try (BufferedReader br = new BufferedReader(new
        // InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("/kit.csv"),
        // "UTF-8"))) {
        //
        // while ((line = br.readLine()) != null) {
        // String[] values = line.split(";");
        // Kit k = new Kit();
        // k.setNomeKit(values[1]);
        // k.setDescricaoKit(values[2]);
        // genericPersister.persist(k);
        // }
        // } catch (Exception e) {
        // throw new RuntimeException(e);
        // }
        //
        // /* KitBem */
        // try (BufferedReader br = new BufferedReader(new
        // InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("/kitbem.csv"),
        // "UTF-8"))) {
        //
        // while ((line = br.readLine()) != null) {
        // String[] values = line.split(";");
        // KitBem kib = new KitBem();
        // kib.setQuantidade(Integer.parseInt(values[1]));
        // kib.setKit(genericPersister.findById(Kit.class, new
        // Long(values[2])));
        // kib.setBem(genericPersister.findById(Bem.class, new
        // Long(values[3])));
        // genericPersister.persist(kib);
        // }
        // } catch (Exception e) {
        // throw new RuntimeException(e);
        // }

    }

}
