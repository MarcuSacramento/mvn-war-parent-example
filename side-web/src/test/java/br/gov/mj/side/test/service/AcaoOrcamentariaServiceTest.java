package br.gov.mj.side.test.service;

import java.io.File;
import java.math.BigDecimal;
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

import br.gov.mj.apoio.entidades.PartidoPolitico;
import br.gov.mj.apoio.entidades.Uf;
import br.gov.mj.infra.negocio.exception.BusinessException;
import br.gov.mj.infra.negocio.persistencia.EnumOrder;
import br.gov.mj.infra.negocio.persistencia.IGenericPersister;
import br.gov.mj.side.entidades.AcaoOrcamentaria;
import br.gov.mj.side.entidades.BeneficiarioEmendaParlamentar;
import br.gov.mj.side.entidades.EmendaParlamentar;
import br.gov.mj.side.entidades.enums.EnumTipoEmenda;
import br.gov.mj.side.web.dao.AcaoOrcamentariaDAO;
import br.gov.mj.side.web.dao.ProgramaDAO;
import br.gov.mj.side.web.dao.PublicizacaoDAO;
import br.gov.mj.side.web.dto.AcaoOrcamentariaDto;
import br.gov.mj.side.web.dto.ProgramaPesquisaDto;
import br.gov.mj.side.web.service.AcaoOrcamentariaService;
import br.gov.mj.side.web.service.ProgramaService;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.ResourcesProducer;
import br.gov.mj.side.web.util.UtilDAO;

@RunWith(Arquillian.class)
public class AcaoOrcamentariaServiceTest {

    private static final String USUARIO_LOGADO = "usuario.logado";

    @PersistenceContext
    EntityManager em;

    @Inject
    UserTransaction tx;

    @Inject
    IGenericPersister genericPersister;

    @Inject
    private AcaoOrcamentariaService acaoOrcamentariaService;

    @Deployment
    public static Archive<?> createDeployment() {
        File[] jars = Maven.configureResolver().withRemoteRepo("artifactory-mj", "http://cgtimaven.mj.gov.br/artifactory/repo/", "default").resolve("br.gov.mj.infra:infra-negocio:1.2.0", "org.jacoco:org.jacoco.core:0.7.5.201505241946", "org.jadira.usertype:usertype.extended:3.2.0.GA")
                .withTransitivity().asFile();

        Archive<?> file = ShrinkWrap.create(WebArchive.class, "side-test.war").addPackages(true, "br.gov.mj.side.entidades").addPackages(true, "br.gov.mj.apoio.entidades").addPackages(true, "br.gov.mj.seg.entidades").addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                .addAsResource("ufs.csv", "ufs.csv").addAsResource("partidos.csv", "partidos.csv").addAsResource("init.txt", "init.txt").addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml").addAsWebInfResource("side-test-ds.xml", "side-test-ds.xml")
                .addAsWebInfResource("test-jboss-web.xml", "jboss-web.xml").addClass(ResourcesProducer.class).addClass(AcaoOrcamentariaDAO.class).addClass(PublicizacaoDAO.class).addClass(UtilDAO.class).addClass(AcaoOrcamentariaDto.class).addClass(AcaoOrcamentariaService.class)
                .addClass(ProgramaService.class).addClass(AcaoOrcamentariaDAO.class).addClass(ProgramaDAO.class).addClass(ProgramaPesquisaDto.class).addClass(Constants.class).addAsLibraries(jars);
        System.out.println(file.toString(true));
        return file;
    }

    @Test(expected = NullPointerException.class)
    @InSequence(1)
    public void testPersistirInserindoObjetoAcaoOrcamentariaVazia() throws Throwable {
        AcaoOrcamentaria acaoOrcamentaria = new AcaoOrcamentaria();
        try {
            acaoOrcamentariaService.incluirAlterar(acaoOrcamentaria, USUARIO_LOGADO);
        } catch (EJBException e) {
            Throwable th = e.getCause();
            throw th;
        }

    }

    @Test(expected = NullPointerException.class)
    @InSequence(2)
    public void testPersistirInserindoAtributosNull() throws Throwable {
        AcaoOrcamentaria acaoOrcamentaria = new AcaoOrcamentaria();
        acaoOrcamentaria.setNomeAcaoOrcamentaria("Nome da Ação Oreçamentária");

        try {
            acaoOrcamentariaService.incluirAlterar(acaoOrcamentaria, USUARIO_LOGADO);
        } catch (EJBException e) {
            Throwable th = e.getCause().getCause();
            throw th;
        }

    }

    @Test(expected = IllegalArgumentException.class)
    @InSequence(3)
    public void testIncluirNull() throws Throwable {
        try {
            acaoOrcamentariaService.incluirAlterar(null, USUARIO_LOGADO);
        } catch (EJBException e) {
            Throwable th = e.getCause();
            throw th;
        }
    }

    @Test
    @InSequence(4)
    public void testIncluirSemEmenda() {
        AcaoOrcamentaria acaoOrcamentaria = new AcaoOrcamentaria();

        acaoOrcamentaria.setValorPrevisto(new BigDecimal("10.99"));
        acaoOrcamentaria.setNomeAcaoOrcamentaria("Nome da Ação Orçamentária");
        acaoOrcamentaria.setNumeroAcaoOrcamentaria("10/2015");
        acaoOrcamentaria.setNomeProgramaPPA("Nome do Programa PPA");
        acaoOrcamentaria.setNumeroProgramaPPA("11/2015");
        acaoOrcamentaria.setAnoAcaoOrcamentaria(2015);

        acaoOrcamentariaService.incluirAlterar(acaoOrcamentaria, USUARIO_LOGADO);
        Assert.assertNotNull(acaoOrcamentaria.getId());
    }

    @Test
    @InSequence(5)
    public void testIncluir() {
        // nova Ação Orçamentária
        AcaoOrcamentaria acaoOrcamentaria = new AcaoOrcamentaria();
        acaoOrcamentaria.setValorPrevisto(new BigDecimal("10.00"));
        acaoOrcamentaria.setNomeAcaoOrcamentaria("Nome da Ação Orçamentária2");
        acaoOrcamentaria.setNumeroAcaoOrcamentaria("11/2015");
        acaoOrcamentaria.setNomeProgramaPPA("Nome do Programa PPA2");
        acaoOrcamentaria.setNumeroProgramaPPA("12/2015");
        acaoOrcamentaria.setAnoAcaoOrcamentaria(2015);
        acaoOrcamentariaService.incluirAlterar(acaoOrcamentaria, USUARIO_LOGADO);
        Assert.assertNotNull(acaoOrcamentaria.getId());
    }

    @Test
    @InSequence(6)
    public void testIncluirComEmenda() {
        // nova Ação Orçamentária
        AcaoOrcamentaria acaoOrcamentaria = new AcaoOrcamentaria();
        acaoOrcamentaria.setValorPrevisto(new BigDecimal("10.00"));
        acaoOrcamentaria.setNomeAcaoOrcamentaria("Nome da Ação Orçamentária2");
        acaoOrcamentaria.setNumeroAcaoOrcamentaria("11/2015");
        acaoOrcamentaria.setNomeProgramaPPA("Nome do Programa PPA2");
        acaoOrcamentaria.setNumeroProgramaPPA("12/2015");
        acaoOrcamentaria.setAnoAcaoOrcamentaria(2015);

        // nova emenda da Ação orçamentária
        EmendaParlamentar emenda = new EmendaParlamentar();
        emenda.setValorPrevisto(new BigDecimal("6.00"));
        emenda.setNomeCargoParlamentar("Senador");
        emenda.setNomeEmendaParlamentar("Nome da emenda");
        emenda.setNomeParlamentar("José da silva");
        emenda.setNumeroEmendaParlamantar("121/2015");

        // novo partido do parlamentar
        PartidoPolitico partido = new PartidoPolitico();
        partido.setId(13l);

        emenda.setPartidoPolitico(partido);
        emenda.setPossuiLiberacao(true);
        emenda.setTipoEmenda(EnumTipoEmenda.INDIVIDUAL);

        // nova uf do partido do parlamentar
        Uf uf = new Uf();
        uf.setId(9l);
        emenda.setUf(uf);

        List<EmendaParlamentar> emendasParlamentares = new ArrayList<EmendaParlamentar>();
        emendasParlamentares.add(emenda);
        acaoOrcamentaria.setEmendasParlamentares(emendasParlamentares);

        acaoOrcamentariaService.incluirAlterar(acaoOrcamentaria, USUARIO_LOGADO);

        AcaoOrcamentaria acaoOrcamentariaRecuperado = acaoOrcamentariaService.buscarPeloId(acaoOrcamentaria.getId());

        Assert.assertNotNull(acaoOrcamentaria.getId());
        Assert.assertEquals(acaoOrcamentariaRecuperado.getEmendasParlamentares().size(), 1l);
    }

    @Test
    @InSequence(7)
    public void testIncluirComEmendaEBeneficiario() {
        // nova Ação Orçamentária
        AcaoOrcamentaria acaoOrcamentaria = new AcaoOrcamentaria();
        acaoOrcamentaria.setValorPrevisto(new BigDecimal("10.00"));
        acaoOrcamentaria.setNomeAcaoOrcamentaria("Nome da Ação Orçamentária2");
        acaoOrcamentaria.setNumeroAcaoOrcamentaria("11/2015");
        acaoOrcamentaria.setNomeProgramaPPA("Nome do Programa PPA2");
        acaoOrcamentaria.setNumeroProgramaPPA("12/2015");
        acaoOrcamentaria.setAnoAcaoOrcamentaria(2015);

        // nova emenda da Ação Orçamntária
        EmendaParlamentar emenda = new EmendaParlamentar();
        emenda.setValorPrevisto(new BigDecimal("6.00"));
        emenda.setNomeCargoParlamentar("Senador");
        emenda.setNomeEmendaParlamentar("Nome da emenda");
        emenda.setNomeParlamentar("José da silva");
        emenda.setNumeroEmendaParlamantar("121/2015");

        // novo partido do parlamentar
        PartidoPolitico partido = new PartidoPolitico();
        partido.setId(13l);

        emenda.setPartidoPolitico(partido);
        emenda.setPossuiLiberacao(true);
        emenda.setTipoEmenda(EnumTipoEmenda.INDIVIDUAL);

        // nova uf do partido do parlamentar
        Uf uf = new Uf();
        uf.setId(9l);
        emenda.setUf(uf);

        BeneficiarioEmendaParlamentar beneficiarioEmendaParlamentar = new BeneficiarioEmendaParlamentar();
        beneficiarioEmendaParlamentar.setNomeBeneficiario("William da Silva");
        beneficiarioEmendaParlamentar.setNumeroCnpjBeneficiario("12112121217787");

        List<BeneficiarioEmendaParlamentar> beneficiariosEmendaParlamentar = new ArrayList<BeneficiarioEmendaParlamentar>();
        beneficiariosEmendaParlamentar.add(beneficiarioEmendaParlamentar);

        emenda.setBeneficiariosEmendaParlamentar(beneficiariosEmendaParlamentar);
        List<EmendaParlamentar> emendasParlamentares = new ArrayList<EmendaParlamentar>();
        emendasParlamentares.add(emenda);
        acaoOrcamentaria.setEmendasParlamentares(emendasParlamentares);

        acaoOrcamentariaService.incluirAlterar(acaoOrcamentaria, USUARIO_LOGADO);

        Assert.assertNotNull(acaoOrcamentaria.getId());
    }

    @Test(expected = IllegalArgumentException.class)
    @InSequence(8)
    public void testBuscarPeloIdNull() throws Throwable {
        try {
            acaoOrcamentariaService.buscarPeloId(null);
        } catch (EJBException e) {
            Throwable th = e.getCause();
            throw th;
        }
    }

    @Test(expected = BusinessException.class)
    @InSequence(9)
    public void testIncluirRegistroNomeNumeroDuplicado() {

        AcaoOrcamentaria acaoOrcamentaria1 = new AcaoOrcamentaria();

        acaoOrcamentaria1.setValorPrevisto(new BigDecimal("10.99"));
        acaoOrcamentaria1.setNomeAcaoOrcamentaria("Nome da Ação Orçamentária");
        acaoOrcamentaria1.setNumeroAcaoOrcamentaria("10/2015");
        acaoOrcamentaria1.setNomeProgramaPPA("Nome do Programa PPA");
        acaoOrcamentaria1.setNumeroProgramaPPA("11/2015");
        acaoOrcamentaria1.setAnoAcaoOrcamentaria(2015);

        AcaoOrcamentaria acaoOrcamentaria2 = new AcaoOrcamentaria();

        acaoOrcamentaria2.setValorPrevisto(new BigDecimal("10.99"));
        acaoOrcamentaria2.setNomeAcaoOrcamentaria("Nome da Ação Orçamentária");
        acaoOrcamentaria2.setNumeroAcaoOrcamentaria("10/2015");
        acaoOrcamentaria2.setNomeProgramaPPA("Nome do Programa PPA");
        acaoOrcamentaria2.setNumeroProgramaPPA("11/2015");
        acaoOrcamentaria2.setAnoAcaoOrcamentaria(2015);

        acaoOrcamentariaService.incluirAlterar(acaoOrcamentaria1, USUARIO_LOGADO);
        acaoOrcamentariaService.incluirAlterar(acaoOrcamentaria2, USUARIO_LOGADO);
    }

    @Test(expected = BusinessException.class)
    @InSequence(10)
    public void testIncluirRegistroNomeNumeroPPADuplicado() {

        AcaoOrcamentaria acaoOrcamentaria1 = new AcaoOrcamentaria();

        acaoOrcamentaria1.setValorPrevisto(new BigDecimal("10.99"));
        acaoOrcamentaria1.setNomeAcaoOrcamentaria("Nome da Ação Orçamentária");
        acaoOrcamentaria1.setNumeroAcaoOrcamentaria("10/2015");
        acaoOrcamentaria1.setNomeProgramaPPA("Nome do Programa PPA");
        acaoOrcamentaria1.setNumeroProgramaPPA("11/2015");
        acaoOrcamentaria1.setAnoAcaoOrcamentaria(2015);

        AcaoOrcamentaria acaoOrcamentaria2 = new AcaoOrcamentaria();

        acaoOrcamentaria2.setValorPrevisto(new BigDecimal("10.99"));
        acaoOrcamentaria2.setNomeAcaoOrcamentaria("Nome da Ação Orçamentária");
        acaoOrcamentaria2.setNumeroAcaoOrcamentaria("10/2015");
        acaoOrcamentaria2.setNomeProgramaPPA("Nome do Programa PPA");
        acaoOrcamentaria2.setNumeroProgramaPPA("11/2015");
        acaoOrcamentaria2.setAnoAcaoOrcamentaria(2015);

        acaoOrcamentariaService.incluirAlterar(acaoOrcamentaria1, USUARIO_LOGADO);
        acaoOrcamentariaService.incluirAlterar(acaoOrcamentaria2, USUARIO_LOGADO);
    }

    @Test
    @InSequence(11)
    public void testAlterar() {
        AcaoOrcamentaria acaoOrcamentariaNovo = new AcaoOrcamentaria();

        acaoOrcamentariaNovo.setValorPrevisto(new BigDecimal("10.99"));
        acaoOrcamentariaNovo.setNomeAcaoOrcamentaria("Nome da Ação Orçamentária");
        acaoOrcamentariaNovo.setNumeroAcaoOrcamentaria("10/2015");
        acaoOrcamentariaNovo.setNomeProgramaPPA("Nome do Programa PPA");
        acaoOrcamentariaNovo.setNumeroProgramaPPA("11/2015");
        acaoOrcamentariaNovo.setAnoAcaoOrcamentaria(2015);
        acaoOrcamentariaService.incluirAlterar(acaoOrcamentariaNovo, USUARIO_LOGADO);

        AcaoOrcamentaria acaoOrcamentariaAlterar = acaoOrcamentariaService.buscarPeloId(acaoOrcamentariaNovo.getId());

        if (acaoOrcamentariaAlterar != null) {
            acaoOrcamentariaAlterar.setValorPrevisto(new BigDecimal("1.99"));
            acaoOrcamentariaAlterar.setNomeAcaoOrcamentaria("Nome da Ação Orçamentária2");
            acaoOrcamentariaAlterar.setNumeroAcaoOrcamentaria("10/2016");
            acaoOrcamentariaAlterar.setNomeProgramaPPA("Nome do Programa PPA2");
            acaoOrcamentariaAlterar.setNumeroProgramaPPA("11/2016");
            acaoOrcamentariaService.incluirAlterar(acaoOrcamentariaAlterar, USUARIO_LOGADO);
        }
        Assert.assertEquals(acaoOrcamentariaAlterar.getId(), acaoOrcamentariaNovo.getId());
        Assert.assertFalse(acaoOrcamentariaAlterar.getValorPrevisto() == acaoOrcamentariaNovo.getValorPrevisto());
        Assert.assertFalse(acaoOrcamentariaAlterar.getNomeAcaoOrcamentaria() == acaoOrcamentariaNovo.getNomeAcaoOrcamentaria());
        Assert.assertFalse(acaoOrcamentariaAlterar.getNumeroAcaoOrcamentaria() == acaoOrcamentariaNovo.getNumeroAcaoOrcamentaria());
        Assert.assertFalse(acaoOrcamentariaAlterar.getNomeProgramaPPA() == acaoOrcamentariaNovo.getNomeProgramaPPA());
        Assert.assertFalse(acaoOrcamentariaAlterar.getNumeroProgramaPPA() == acaoOrcamentariaNovo.getNumeroProgramaPPA());

    }

    @Test(expected = IllegalArgumentException.class)
    @InSequence(12)
    public void testExcluirNull() throws Throwable {
        try {
            acaoOrcamentariaService.excluir(null);
        } catch (Exception e) {
            Throwable t = e.getCause();
            throw t;
        }
    }

    @Test(expected = BusinessException.class)
    @InSequence(13)
    public void testExcluirRegistroInvalido() {
        acaoOrcamentariaService.excluir(999l);
    }

    @Test
    @InSequence(14)
    public void testExcluir() {
        AcaoOrcamentaria acaoOrcamentariaNovo = new AcaoOrcamentaria();

        acaoOrcamentariaNovo.setValorPrevisto(new BigDecimal("10.99"));
        acaoOrcamentariaNovo.setNomeAcaoOrcamentaria("Nome da Ação Orçamentária");
        acaoOrcamentariaNovo.setNumeroAcaoOrcamentaria("10/2015");
        acaoOrcamentariaNovo.setNomeProgramaPPA("Nome do Programa PPA");
        acaoOrcamentariaNovo.setNumeroProgramaPPA("11/2015");
        acaoOrcamentariaNovo.setAnoAcaoOrcamentaria(2015);

        acaoOrcamentariaService.incluirAlterar(acaoOrcamentariaNovo, USUARIO_LOGADO);
        acaoOrcamentariaService.excluir(acaoOrcamentariaNovo.getId());

        AcaoOrcamentaria acaoOrcamentariaExclusao = acaoOrcamentariaService.buscarPeloId(acaoOrcamentariaNovo.getId());
        Assert.assertNull(acaoOrcamentariaExclusao);
    }

    @Test
    @InSequence(15)
    public void testExcluirComEmenda() {

        // nova Ação Orçamentária
        AcaoOrcamentaria acaoOrcamentaria = new AcaoOrcamentaria();
        acaoOrcamentaria.setValorPrevisto(new BigDecimal("10.00"));
        acaoOrcamentaria.setNomeAcaoOrcamentaria("Nome da Ação Orçamentária2");
        acaoOrcamentaria.setNumeroAcaoOrcamentaria("11/2015");
        acaoOrcamentaria.setNomeProgramaPPA("Nome do Programa PPA2");
        acaoOrcamentaria.setNumeroProgramaPPA("12/2015");
        acaoOrcamentaria.setAnoAcaoOrcamentaria(2015);

        // nova emenda da Ação Orçamentária
        EmendaParlamentar emenda = new EmendaParlamentar();
        emenda.setValorPrevisto(new BigDecimal("6.00"));
        emenda.setNomeCargoParlamentar("Senador");
        emenda.setNomeEmendaParlamentar("Nome da emenda");
        emenda.setNomeParlamentar("José da silva");
        emenda.setNumeroEmendaParlamantar("121/2015");

        // novo partido do parlamentar
        PartidoPolitico partido = new PartidoPolitico();
        partido.setId(13l);

        emenda.setPartidoPolitico(partido);
        emenda.setPossuiLiberacao(true);
        emenda.setTipoEmenda(EnumTipoEmenda.INDIVIDUAL);

        // nova uf do partido do parlamentar
        Uf uf = new Uf();
        uf.setId(9l);
        emenda.setUf(uf);

        List<EmendaParlamentar> emendasParlamentares = new ArrayList<EmendaParlamentar>();
        emendasParlamentares.add(emenda);
        acaoOrcamentaria.setEmendasParlamentares(emendasParlamentares);

        acaoOrcamentariaService.incluirAlterar(acaoOrcamentaria, USUARIO_LOGADO);
        acaoOrcamentariaService.excluir(acaoOrcamentaria.getId());

        AcaoOrcamentaria acaoOrcamentariaExclusao = acaoOrcamentariaService.buscarPeloId(acaoOrcamentaria.getId());
        Assert.assertNull(acaoOrcamentariaExclusao);
    }

    @Test
    @InSequence(16)
    public void testExcluirComEmendaEBeneficiario() {

        // nova Ação Orçamentária
        AcaoOrcamentaria acaoOrcamentaria = new AcaoOrcamentaria();
        acaoOrcamentaria.setValorPrevisto(new BigDecimal("10.00"));
        acaoOrcamentaria.setNomeAcaoOrcamentaria("Nome da Ação Orçamentária2");
        acaoOrcamentaria.setNumeroAcaoOrcamentaria("11/2015");
        acaoOrcamentaria.setNomeProgramaPPA("Nome do Programa PPA2");
        acaoOrcamentaria.setNumeroProgramaPPA("12/2015");
        acaoOrcamentaria.setAnoAcaoOrcamentaria(2015);

        // nova emenda da Ação Orçamentária
        EmendaParlamentar emenda = new EmendaParlamentar();
        emenda.setValorPrevisto(new BigDecimal("6.00"));
        emenda.setNomeCargoParlamentar("Senador");
        emenda.setNomeEmendaParlamentar("Nome da emenda");
        emenda.setNomeParlamentar("José da silva");
        emenda.setNumeroEmendaParlamantar("121/2015");

        // novo partido do parlamentar
        PartidoPolitico partido = new PartidoPolitico();
        partido.setId(13l);

        emenda.setPartidoPolitico(partido);
        emenda.setPossuiLiberacao(true);
        emenda.setTipoEmenda(EnumTipoEmenda.INDIVIDUAL);

        // nova uf do partido do parlamentar
        Uf uf = new Uf();
        uf.setId(9l);
        emenda.setUf(uf);

        BeneficiarioEmendaParlamentar beneficiarioEmendaParlamentar = new BeneficiarioEmendaParlamentar();
        beneficiarioEmendaParlamentar.setNomeBeneficiario("William da Silva");
        beneficiarioEmendaParlamentar.setNumeroCnpjBeneficiario("12112121217787");

        List<BeneficiarioEmendaParlamentar> beneficiariosEmendaParlamentar = new ArrayList<BeneficiarioEmendaParlamentar>();
        beneficiariosEmendaParlamentar.add(beneficiarioEmendaParlamentar);

        emenda.setBeneficiariosEmendaParlamentar(beneficiariosEmendaParlamentar);
        List<EmendaParlamentar> emendasParlamentares = new ArrayList<EmendaParlamentar>();
        emendasParlamentares.add(emenda);
        acaoOrcamentaria.setEmendasParlamentares(emendasParlamentares);

        acaoOrcamentariaService.incluirAlterar(acaoOrcamentaria, USUARIO_LOGADO);
        acaoOrcamentariaService.excluir(acaoOrcamentaria.getId());

        AcaoOrcamentaria acaoOrcamentariaExclusao = acaoOrcamentariaService.buscarPeloId(acaoOrcamentaria.getId());
        Assert.assertNull(acaoOrcamentariaExclusao);
    }

    @Test
    @InSequence(17)
    public void testBuscarNull() {

        // nova Ação Orçamentária
        AcaoOrcamentaria acaoOrcamentaria1 = new AcaoOrcamentaria();
        acaoOrcamentaria1.setValorPrevisto(new BigDecimal("10.00"));
        acaoOrcamentaria1.setNomeAcaoOrcamentaria("Nome da Ação Orçamentária2");
        acaoOrcamentaria1.setNumeroAcaoOrcamentaria("11/2015");
        acaoOrcamentaria1.setNomeProgramaPPA("Nome do Programa PPA2");
        acaoOrcamentaria1.setNumeroProgramaPPA("12/2015");
        acaoOrcamentaria1.setAnoAcaoOrcamentaria(2015);
        acaoOrcamentariaService.incluirAlterar(acaoOrcamentaria1, USUARIO_LOGADO);

        // nova Ação Orçamentária
        AcaoOrcamentaria acaoOrcamentaria2 = new AcaoOrcamentaria();
        acaoOrcamentaria2.setValorPrevisto(new BigDecimal("10.00"));
        acaoOrcamentaria2.setNomeAcaoOrcamentaria("Nome da Ação Orçamentária3");
        acaoOrcamentaria2.setNumeroAcaoOrcamentaria("12/2015");
        acaoOrcamentaria2.setNomeProgramaPPA("Nome do Programa PPA2");
        acaoOrcamentaria2.setNumeroProgramaPPA("13/2015");
        acaoOrcamentaria2.setAnoAcaoOrcamentaria(2015);

        acaoOrcamentariaService.incluirAlterar(acaoOrcamentaria2, USUARIO_LOGADO);

        List<AcaoOrcamentaria> lista = acaoOrcamentariaService.buscar(null); // Deve
                                                                             // retornar
        // todos
        Assert.assertEquals(2, lista.size());
    }

    @Test
    @InSequence(18)
    public void testBuscarPorNomeLike() {

        // nova Ação Orçamentária
        AcaoOrcamentaria acaoOrcamentaria1 = new AcaoOrcamentaria();
        acaoOrcamentaria1.setValorPrevisto(new BigDecimal("10.00"));
        acaoOrcamentaria1.setNomeAcaoOrcamentaria("Nome da Ação Orçamentária2");
        acaoOrcamentaria1.setNumeroAcaoOrcamentaria("11/2015");
        acaoOrcamentaria1.setNomeProgramaPPA("Nome do Programa PPA2");
        acaoOrcamentaria1.setNumeroProgramaPPA("12/2015");
        acaoOrcamentaria1.setAnoAcaoOrcamentaria(2015);

        acaoOrcamentariaService.incluirAlterar(acaoOrcamentaria1, USUARIO_LOGADO);

        // nova Ação Orçamentária
        AcaoOrcamentaria acaoOrcamentaria2 = new AcaoOrcamentaria();
        acaoOrcamentaria2.setValorPrevisto(new BigDecimal("10.00"));
        acaoOrcamentaria2.setNomeAcaoOrcamentaria("Nome da Ação Orçamentária3");
        acaoOrcamentaria2.setNumeroAcaoOrcamentaria("12/2015");
        acaoOrcamentaria2.setNomeProgramaPPA("Nome do Programa PPA3");
        acaoOrcamentaria2.setNumeroProgramaPPA("13/2015");
        acaoOrcamentaria2.setAnoAcaoOrcamentaria(2016);

        acaoOrcamentariaService.incluirAlterar(acaoOrcamentaria2, USUARIO_LOGADO);

        // nova Ação Orçamentária
        AcaoOrcamentaria acaoOrcamentaria3 = new AcaoOrcamentaria();
        acaoOrcamentaria3.setValorPrevisto(new BigDecimal("10.00"));
        acaoOrcamentaria3.setNomeAcaoOrcamentaria("Nome da Ação Orçamentária4");
        acaoOrcamentaria3.setNumeroAcaoOrcamentaria("13/2015");
        acaoOrcamentaria3.setNomeProgramaPPA("Nome do Programa PPA4");
        acaoOrcamentaria3.setNumeroProgramaPPA("14/2015");
        acaoOrcamentaria3.setAnoAcaoOrcamentaria(2017);

        acaoOrcamentariaService.incluirAlterar(acaoOrcamentaria3, USUARIO_LOGADO);

        AcaoOrcamentaria acaoOrcamentariaPesquisa = new AcaoOrcamentaria();
        acaoOrcamentariaPesquisa.setNomeAcaoOrcamentaria("ment");
        List<AcaoOrcamentaria> lista = acaoOrcamentariaService.buscar(acaoOrcamentariaPesquisa);
        Assert.assertEquals(3, lista.size());
    }

    @Test
    @InSequence(19)
    public void testBuscarPorNomeAcaoOrcamentariaENumeroAcaoOrcamentariaLike() {

        // nova Ação Orçamentária
        AcaoOrcamentaria acaoOrcamentaria1 = new AcaoOrcamentaria();
        acaoOrcamentaria1.setValorPrevisto(new BigDecimal("10.00"));
        acaoOrcamentaria1.setNomeAcaoOrcamentaria("Nome da Ação Orçamentária1");
        acaoOrcamentaria1.setNumeroAcaoOrcamentaria("11/2016");
        acaoOrcamentaria1.setNomeProgramaPPA("Nome do Programa PPA2");
        acaoOrcamentaria1.setNumeroProgramaPPA("12/2015");
        acaoOrcamentaria1.setAnoAcaoOrcamentaria(2015);

        acaoOrcamentariaService.incluirAlterar(acaoOrcamentaria1, USUARIO_LOGADO);

        // nova Ação Orçamentária
        AcaoOrcamentaria acaoOrcamentaria2 = new AcaoOrcamentaria();
        acaoOrcamentaria2.setValorPrevisto(new BigDecimal("10.00"));
        acaoOrcamentaria2.setNomeAcaoOrcamentaria("Nome da Ação Orçamentária2");
        acaoOrcamentaria2.setNumeroAcaoOrcamentaria("12/2016");
        acaoOrcamentaria2.setNomeProgramaPPA("Nome do Programa PPA3");
        acaoOrcamentaria2.setNumeroProgramaPPA("12/2015");
        acaoOrcamentaria2.setAnoAcaoOrcamentaria(2016);

        acaoOrcamentariaService.incluirAlterar(acaoOrcamentaria2, USUARIO_LOGADO);

        // nova Ação Orçamentária
        AcaoOrcamentaria acaoOrcamentaria3 = new AcaoOrcamentaria();
        acaoOrcamentaria3.setValorPrevisto(new BigDecimal("10.00"));
        acaoOrcamentaria3.setNomeAcaoOrcamentaria("Nome da Ação Orçamentária3");
        acaoOrcamentaria3.setNumeroAcaoOrcamentaria("13/2017");
        acaoOrcamentaria3.setNomeProgramaPPA("Nome do Programa PPA4");
        acaoOrcamentaria3.setNumeroProgramaPPA("12/2015");
        acaoOrcamentaria3.setAnoAcaoOrcamentaria(2017);

        acaoOrcamentariaService.incluirAlterar(acaoOrcamentaria3, USUARIO_LOGADO);

        AcaoOrcamentaria acaoOrcamentariaPesquisa = new AcaoOrcamentaria();
        acaoOrcamentariaPesquisa.setNomeAcaoOrcamentaria("ação or");
        acaoOrcamentariaPesquisa.setNumeroAcaoOrcamentaria("2016");
        List<AcaoOrcamentaria> lista = acaoOrcamentariaService.buscar(acaoOrcamentariaPesquisa);
        Assert.assertEquals(2, lista.size());
    }

    @Test
    @InSequence(20)
    public void testBuscarPaginadoNull() {

        for (int i = 0; i < 11; i++) {
            AcaoOrcamentaria acaoOrcamentaria = new AcaoOrcamentaria();
            acaoOrcamentaria.setValorPrevisto(new BigDecimal("10.00"));
            acaoOrcamentaria.setNomeAcaoOrcamentaria("Nome da Ação Orçamentária" + i);
            acaoOrcamentaria.setNumeroAcaoOrcamentaria("11/2016" + i);
            acaoOrcamentaria.setNomeProgramaPPA("Nome do Programa PPA2" + i);
            acaoOrcamentaria.setNumeroProgramaPPA("12/2015" + i);
            acaoOrcamentaria.setAnoAcaoOrcamentaria(2015);

            acaoOrcamentariaService.incluirAlterar(acaoOrcamentaria, USUARIO_LOGADO);

        }
        List<AcaoOrcamentaria> lista = acaoOrcamentariaService.buscarPaginado(null, null, null, 1, 5, EnumOrder.ASC, "nomeAcaoOrcamentaria");
        Assert.assertEquals(5, lista.size());
    }

    @Test
    @InSequence(21)
    public void testContarPaginadoNull() {
        for (int i = 0; i < 3; i++) {
            AcaoOrcamentaria acaoOrcamentaria = new AcaoOrcamentaria();
            acaoOrcamentaria.setValorPrevisto(new BigDecimal("10.00"));
            acaoOrcamentaria.setNomeAcaoOrcamentaria("Nome da Ação Orçamentária" + i);
            acaoOrcamentaria.setNumeroAcaoOrcamentaria("11/2016" + i);
            acaoOrcamentaria.setNomeProgramaPPA("Nome do Programa PPA2" + i);
            acaoOrcamentaria.setNumeroProgramaPPA("12/2015" + i);
            acaoOrcamentaria.setAnoAcaoOrcamentaria(2015);

            acaoOrcamentariaService.incluirAlterar(acaoOrcamentaria, USUARIO_LOGADO);
        }
        long total = acaoOrcamentariaService.contarPaginado(null, null);
        Assert.assertEquals(3l, total);
    }

    // TODO rever
    // @Test
    // @InSequence(22)
    // public void testBuscarAcoesOrcamentaria() {
    // for (int i = 0; i < 3; i++) {
    // AcaoOrcamentaria acaoOrcamentaria = new AcaoOrcamentaria();
    // acaoOrcamentaria.setValorPrevisto(new BigDecimal("10.00"));
    // acaoOrcamentaria.setNomeAcaoOrcamentaria("Nome da Ação Orçamentária" +
    // i);
    // acaoOrcamentaria.setNumeroAcaoOrcamentaria("11/2016" + i);
    // acaoOrcamentaria.setNomeProgramaPPA("Nome do Programa PPA2" + i);
    // acaoOrcamentaria.setNumeroProgramaPPA("12/2015" + i);
    // acaoOrcamentaria.setAnoAcaoOrcamentaria(2015);
    //
    // acaoOrcamentariaService.incluirAlterar(acaoOrcamentaria, USUARIO_LOGADO);
    // }
    // List<AcaoOrcamentariaDto> lista =
    // acaoOrcamentariaService.buscarAcoesOrcamentaria();
    // Assert.assertEquals(3l, lista.size());
    // }

    @Test(expected = BusinessException.class)
    @InSequence(23)
    public void testAlterarComEmendaDuplicada() {
        AcaoOrcamentaria acaoOrcamentariaNova = new AcaoOrcamentaria();

        acaoOrcamentariaNova.setValorPrevisto(new BigDecimal("10.99"));
        acaoOrcamentariaNova.setNomeAcaoOrcamentaria("Nome da Ação Orçamentária");
        acaoOrcamentariaNova.setNumeroAcaoOrcamentaria("10/2015");
        acaoOrcamentariaNova.setNomeProgramaPPA("Nome do Programa PPA");
        acaoOrcamentariaNova.setNumeroProgramaPPA("11/2015");
        acaoOrcamentariaNova.setAnoAcaoOrcamentaria(2015);

        // nova emenda da Ação Orçamentária
        EmendaParlamentar emenda = new EmendaParlamentar();
        emenda.setValorPrevisto(new BigDecimal("6.00"));
        emenda.setNomeCargoParlamentar("Senador");
        emenda.setNomeEmendaParlamentar("Nome da emenda");
        emenda.setNomeParlamentar("José da silva");
        emenda.setNumeroEmendaParlamantar("121/2015");
        emenda.setPossuiLiberacao(true);
        emenda.setTipoEmenda(EnumTipoEmenda.INDIVIDUAL);

        // novo partido do parlamentar
        PartidoPolitico partido = new PartidoPolitico();
        partido.setId(13l);
        emenda.setPartidoPolitico(partido);

        // nova uf do partido do parlamentar
        Uf uf = new Uf();
        uf.setId(9l);
        emenda.setUf(uf);

        // nova emenda da Ação Orçamentária
        EmendaParlamentar emenda2 = new EmendaParlamentar();
        emenda2.setValorPrevisto(new BigDecimal("6.00"));
        emenda2.setNomeCargoParlamentar("Senador");
        emenda2.setNomeEmendaParlamentar("Nome da emenda");
        emenda2.setNomeParlamentar("José da silva");
        emenda2.setNumeroEmendaParlamantar("121/2015");
        emenda2.setPossuiLiberacao(true);
        emenda2.setTipoEmenda(EnumTipoEmenda.BANCADA);

        // novo partido do parlamentar
        PartidoPolitico partido2 = new PartidoPolitico();
        partido2.setId(13l);
        emenda2.setPartidoPolitico(partido2);

        // nova uf do partido do parlamentar
        Uf uf2 = new Uf();
        uf2.setId(9l);
        emenda2.setUf(uf2);

        List<EmendaParlamentar> emendasParlamentares = new ArrayList<EmendaParlamentar>();
        emendasParlamentares.add(emenda);
        emendasParlamentares.add(emenda2);

        acaoOrcamentariaNova.setEmendasParlamentares(emendasParlamentares);

        acaoOrcamentariaService.incluirAlterar(acaoOrcamentariaNova, USUARIO_LOGADO);

        AcaoOrcamentaria acaoOrcamentariaAlterar = acaoOrcamentariaService.buscarPeloId(acaoOrcamentariaNova.getId());

        if (acaoOrcamentariaAlterar != null) {
            acaoOrcamentariaAlterar.setValorPrevisto(new BigDecimal("1.99"));
            acaoOrcamentariaAlterar.setNomeAcaoOrcamentaria("Nome da Ação Orçamentária2");
            acaoOrcamentariaAlterar.setNumeroAcaoOrcamentaria("10/2016");
            acaoOrcamentariaAlterar.setNomeProgramaPPA("Nome do Programa PPA2");
            acaoOrcamentariaAlterar.setNumeroProgramaPPA("11/2016");

            EmendaParlamentar emAlterar = acaoOrcamentariaAlterar.getEmendasParlamentares().get(1);
            emAlterar.setNomeEmendaParlamentar(emAlterar.getNomeEmendaParlamentar() + " ALTERADO");
            acaoOrcamentariaAlterar.getEmendasParlamentares().clear();
            acaoOrcamentariaAlterar.getEmendasParlamentares().add(emAlterar);

            // nova emenda da Ação Orçamentária
            EmendaParlamentar emenda3 = new EmendaParlamentar();
            emenda3.setValorPrevisto(new BigDecimal("6.00"));
            emenda3.setNomeCargoParlamentar("Senador");
            emenda3.setNomeEmendaParlamentar("Nome da emenda");
            emenda3.setNomeParlamentar("José da silva");
            emenda3.setNumeroEmendaParlamantar("121/2015");
            emenda3.setPossuiLiberacao(true);
            emenda3.setTipoEmenda(EnumTipoEmenda.RELATOR);

            // novo partido do parlamentar
            PartidoPolitico partido3 = new PartidoPolitico();
            partido3.setId(13l);
            emenda3.setPartidoPolitico(partido3);

            // nova uf do partido do parlamentar
            Uf uf3 = new Uf();
            uf3.setId(9l);
            emenda3.setUf(uf3);

            acaoOrcamentariaAlterar.getEmendasParlamentares().add(emenda3);
            acaoOrcamentariaService.incluirAlterar(acaoOrcamentariaAlterar, USUARIO_LOGADO);

        }

    }

    @Test
    @InSequence(24)
    public void testAlterarComBeneficiarios() {
        AcaoOrcamentaria acaoOrcamentariaNova = new AcaoOrcamentaria();

        acaoOrcamentariaNova.setValorPrevisto(new BigDecimal("10.99"));
        acaoOrcamentariaNova.setNomeAcaoOrcamentaria("Nome da Ação Orçamentária");
        acaoOrcamentariaNova.setNumeroAcaoOrcamentaria("10/2015");
        acaoOrcamentariaNova.setNomeProgramaPPA("Nome do Programa PPA");
        acaoOrcamentariaNova.setNumeroProgramaPPA("11/2015");
        acaoOrcamentariaNova.setAnoAcaoOrcamentaria(2015);

        // nova emenda da Ação Orçamentária
        EmendaParlamentar emenda = new EmendaParlamentar();
        emenda.setValorPrevisto(new BigDecimal("6.00"));
        emenda.setNomeCargoParlamentar("Senador");
        emenda.setNomeEmendaParlamentar("Nome da emenda");
        emenda.setNomeParlamentar("José da silva");
        emenda.setNumeroEmendaParlamantar("121/2015");
        emenda.setPossuiLiberacao(true);
        emenda.setTipoEmenda(EnumTipoEmenda.INDIVIDUAL);

        // novo partido do parlamentar
        PartidoPolitico partido = new PartidoPolitico();
        partido.setId(13l);
        emenda.setPartidoPolitico(partido);

        // nova uf do partido do parlamentar
        Uf uf = new Uf();
        uf.setId(9l);
        emenda.setUf(uf);

        // /////////////////Beneficiarios
        List<BeneficiarioEmendaParlamentar> beneficiarios = new ArrayList<BeneficiarioEmendaParlamentar>();

        BeneficiarioEmendaParlamentar ben1 = new BeneficiarioEmendaParlamentar();
        ben1.setNomeBeneficiario("Ana da silva");
        ben1.setNumeroCnpjBeneficiario("11111111111111");

        BeneficiarioEmendaParlamentar ben2 = new BeneficiarioEmendaParlamentar();
        ben2.setNomeBeneficiario("Pedro");
        ben2.setNumeroCnpjBeneficiario("22222222222222");

        beneficiarios.add(ben1);
        beneficiarios.add(ben2);
        emenda.setBeneficiariosEmendaParlamentar(beneficiarios);
        // ////////////////

        List<EmendaParlamentar> emendasParlamentares = new ArrayList<EmendaParlamentar>();
        emendasParlamentares.add(emenda);
        acaoOrcamentariaNova.setEmendasParlamentares(emendasParlamentares);

        acaoOrcamentariaService.incluirAlterar(acaoOrcamentariaNova, USUARIO_LOGADO);

        AcaoOrcamentaria acaoOrcamentariaAlterar = acaoOrcamentariaService.buscarPeloId(acaoOrcamentariaNova.getId());

        EmendaParlamentar emendaAlterar = acaoOrcamentariaAlterar.getEmendasParlamentares().get(0);

        List<BeneficiarioEmendaParlamentar> beneficiariosAlterar = new ArrayList<BeneficiarioEmendaParlamentar>();
        beneficiariosAlterar = emendaAlterar.getBeneficiariosEmendaParlamentar();
        beneficiariosAlterar.remove(1);

        beneficiariosAlterar.get(0).setNomeBeneficiario("NOME ALTERADO");

        BeneficiarioEmendaParlamentar ben3 = new BeneficiarioEmendaParlamentar();
        ben3.setNomeBeneficiario("jose");
        ben3.setNumeroCnpjBeneficiario("33333333333333");

        beneficiariosAlterar.add(ben3);

        emendaAlterar.setBeneficiariosEmendaParlamentar(beneficiariosAlterar);

        acaoOrcamentariaAlterar.getEmendasParlamentares().clear();
        acaoOrcamentariaAlterar.getEmendasParlamentares().add(emendaAlterar);

        AcaoOrcamentaria acaoOrcamentariaAlterada = null;
        if (acaoOrcamentariaAlterar != null) {

            acaoOrcamentariaService.incluirAlterar(acaoOrcamentariaAlterar, USUARIO_LOGADO);
            acaoOrcamentariaAlterada = acaoOrcamentariaService.buscarPeloId(acaoOrcamentariaAlterar.getId());

        }

        Assert.assertEquals(acaoOrcamentariaAlterar.getEmendasParlamentares().get(0).getBeneficiariosEmendaParlamentar().get(0).getNomeBeneficiario(), acaoOrcamentariaAlterada.getEmendasParlamentares().get(0).getBeneficiariosEmendaParlamentar().get(0).getNomeBeneficiario());

    }

    @Before
    public void createData() throws Exception {

        // tx.begin();
        // em.createQuery("delete from BeneficiarioEmendaParlamentar").executeUpdate();
        // em.createQuery("delete from EmendaParlamentar").executeUpdate();
        // em.createQuery("delete from AcaoOrcamentaria").executeUpdate();
        // em.createQuery("delete from Uf").executeUpdate();
        // em.createQuery("delete from PartidoPolitico").executeUpdate();
        // tx.commit();
        //
        // String line = "";
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
    }

}
