package br.gov.mj.side.test.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJBException;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import org.apache.commons.io.IOUtils;
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

import br.gov.mj.apoio.entidades.Elemento;
import br.gov.mj.apoio.entidades.SubElemento;
import br.gov.mj.infra.negocio.exception.BusinessException;
import br.gov.mj.infra.negocio.persistencia.IGenericPersister;
import br.gov.mj.side.entidades.AnexoBem;
import br.gov.mj.side.entidades.Bem;
import br.gov.mj.side.entidades.HistoricoBem;
import br.gov.mj.side.entidades.Kit;
import br.gov.mj.side.entidades.KitBem;
import br.gov.mj.side.entidades.TagBem;
import br.gov.mj.side.web.dao.BemDAO;
import br.gov.mj.side.web.dao.HistoricoBemDAO;
import br.gov.mj.side.web.dao.KitDAO;
import br.gov.mj.side.web.dao.ProgramaDAO;
import br.gov.mj.side.web.dao.PublicizacaoDAO;
import br.gov.mj.side.web.dto.ProgramaPesquisaDto;
import br.gov.mj.side.web.service.BemService;
import br.gov.mj.side.web.service.HistoricoBemService;
import br.gov.mj.side.web.service.KitService;
import br.gov.mj.side.web.service.ProgramaService;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.ResourcesProducer;
import br.gov.mj.side.web.util.UtilDAO;

@RunWith(Arquillian.class)
public class BemServiceTest {

    @PersistenceContext
    EntityManager em;

    @Inject
    UserTransaction tx;

    @Inject
    IGenericPersister genericPersister;

    @Inject
    BemService bemService;

    @Inject
    HistoricoBemService historicoBemService;

    @Deployment
    public static Archive<?> createDeployment() {
        File[] jars = Maven.configureResolver().withRemoteRepo("artifactory-mj", "http://cgtimaven.mj.gov.br/artifactory/repo/", "default")
                .resolve("br.gov.mj.infra:infra-negocio:1.2.0", "org.jacoco:org.jacoco.core:0.7.5.201505241946", "commons-io:commons-io:2.4", "org.jadira.usertype:usertype.extended:3.2.0.GA").withTransitivity().asFile();

        Archive<?> file = ShrinkWrap.create(WebArchive.class, "side-test.war").addPackages(true, "br.gov.mj.side.entidades").addPackages(true, "br.gov.mj.apoio.entidades").addPackages(true, "br.gov.mj.seg.entidades").addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                .addAsResource("elementos.csv", "elementos.csv").addAsResource("init.txt", "init.txt").addAsResource("teste_upload.txt", "teste_upload.txt").addAsResource("subelementos.csv", "subelementos.csv").addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsWebInfResource("side-test-ds.xml", "side-test-ds.xml").addAsWebInfResource("test-jboss-web.xml", "jboss-web.xml").addClass(ResourcesProducer.class).addClass(PublicizacaoDAO.class).addClass(BemDAO.class).addClass(KitDAO.class).addClass(ProgramaDAO.class).addClass(UtilDAO.class)
                .addClass(ProgramaPesquisaDto.class).addClass(BemService.class).addClass(ProgramaService.class).addClass(KitService.class).addClass(HistoricoBemService.class).addClass(HistoricoBemDAO.class).addClass(Constants.class).addAsLibraries(jars);
        System.out.println(file.toString(true));
        return file;
    }

    @Test(expected = IllegalArgumentException.class)
    @InSequence(1)
    public void testIncluirNull() throws Throwable {
        try {
            bemService.incluirAlterar(null);
        } catch (EJBException e) {
            Throwable th = e.getCause();
            throw th;
        }
    }

    @Test
    @InSequence(2)
    public void testIncluirComAnexo() {
        Bem bem = new Bem();
        bem.setNomeBem("Novo bem");
        bem.setDescricaoBem("Novo bem descrição");
        bem.setSubElemento(genericPersister.findById(SubElemento.class, 33901414l));
        bem.setValorEstimadoBem(new BigDecimal("10000.00"));
        bem.setDataEstimativa(LocalDate.parse("2015-01-01"));

        TagBem t1 = new TagBem();
        t1.setNomeTag("TAG1");
        t1.setValorTag("XYZ");
        bem.getTags().add(t1);

        TagBem t2 = new TagBem();
        t2.setNomeTag("TAG2");
        t2.setValorTag("ZYX");
        bem.getTags().add(t2);

        AnexoBem a = new AnexoBem();
        a.setNomeAnexo("elementos.csv");

        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("teste_upload.txt");
        try {
            byte[] bytes = IOUtils.toByteArray(resourceAsStream);
            a.setConteudo(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        bem.getAnexos().add(a);

        bemService.incluirAlterar(bem);
        Assert.assertNotNull(bem.getId());
    }

    @Test(expected = IllegalArgumentException.class)
    @InSequence(3)
    public void testBuscarPeloIdNull() throws Throwable {
        try {
            bemService.buscarPeloId(null);
        } catch (EJBException e) {
            Throwable th = e.getCause();
            throw th;
        }
    }

    @Test(expected = BusinessException.class)
    @InSequence(4)
    public void testIncluirRegistroNomeDuplicado() {

        Bem bem1 = new Bem();
        bem1.setNomeBem("Novo bem");
        bem1.setDescricaoBem("Novo bem descrição");
        bem1.setSubElemento(genericPersister.findById(SubElemento.class, 33901414l));
        bem1.setValorEstimadoBem(new BigDecimal("10000.00"));
        bem1.setDataEstimativa(LocalDate.parse("2015-01-01"));

        Bem bem2 = new Bem();
        bem2.setNomeBem("Novo bem");
        bem2.setDescricaoBem("Novo bem descrição II");
        bem2.setSubElemento(genericPersister.findById(SubElemento.class, 33901414l));
        bem2.setValorEstimadoBem(new BigDecimal("100000.00"));

        bemService.incluirAlterar(bem1);
        bemService.incluirAlterar(bem2);
    }

    @Test(expected = BusinessException.class)
    @InSequence(5)
    public void testIncluirRegistroTagsDuplicado() {

        Bem bem = new Bem();
        bem.setNomeBem("Novo bem");
        bem.setDescricaoBem("Novo bem descrição");
        bem.setSubElemento(genericPersister.findById(SubElemento.class, 33901414l));
        bem.setValorEstimadoBem(new BigDecimal("10000.00"));
        bem.setDataEstimativa(LocalDate.parse("2015-01-01"));

        TagBem t1 = new TagBem();
        t1.setNomeTag("TAG1");
        t1.setValorTag("XYZ");
        bem.getTags().add(t1);

        TagBem t2 = new TagBem();
        t2.setNomeTag("TAG1");
        t2.setValorTag("XYZ");
        bem.getTags().add(t2);

        TagBem t3 = new TagBem();
        t3.setNomeTag("TAG1");
        t3.setValorTag("XYZ");
        bem.getTags().add(t3);
        bemService.incluirAlterar(bem);

    }

    @Test
    @InSequence(6)
    public void testAlterar() {
        Bem bemNovo = new Bem();

        bemNovo.setNomeBem("Novo bem 1");
        bemNovo.setDescricaoBem("Nova descrição 1");
        bemNovo.setSubElemento(genericPersister.findById(SubElemento.class, 33903030l));
        bemNovo.setValorEstimadoBem(new BigDecimal("100.00"));
        bemNovo.setDataEstimativa(LocalDate.parse("2015-02-01"));
        bemService.incluirAlterar(bemNovo);

        Bem bemAlterar = bemService.buscarPeloId(bemNovo.getId());

        if (bemAlterar != null) {
            bemAlterar.setNomeBem("Novo bem 1 alterado");
            bemAlterar.setDescricaoBem("Nova descrição 1 alterado");
            bemAlterar.setSubElemento(genericPersister.findById(SubElemento.class, 33903030l));
            bemAlterar.setValorEstimadoBem(new BigDecimal("1000.00"));
            bemAlterar.setAnexos(new ArrayList<AnexoBem>());
            bemAlterar.setTags(new ArrayList<TagBem>());
            bemAlterar.setHistoricoBemValores(new ArrayList<HistoricoBem>());
            bemAlterar.setDataEstimativa(LocalDate.parse("2015-03-01"));
            bemService.incluirAlterar(bemAlterar);
        }
        Assert.assertEquals(bemAlterar.getId(), bemNovo.getId());
        Assert.assertFalse(bemAlterar.getNomeBem() == bemNovo.getNomeBem());
        Assert.assertFalse(bemAlterar.getDescricaoBem() == bemNovo.getDescricaoBem());
        Assert.assertFalse(bemAlterar.getValorEstimadoBem() == bemNovo.getValorEstimadoBem());
        Assert.assertFalse(bemAlterar.getSubElemento().getId() == bemNovo.getSubElemento().getId());
        Assert.assertFalse(bemAlterar.getDataEstimativa().equals(bemNovo.getDataEstimativa()));
    }

    @Test(expected = IllegalArgumentException.class)
    @InSequence(7)
    public void testExcluirNull() throws Throwable {
        try {
            bemService.excluir(null);
        } catch (Exception e) {
            Throwable t = e.getCause();
            throw t;
        }
    }

    @Test(expected = BusinessException.class)
    @InSequence(8)
    public void testExcluirRegistroInvalido() {
        bemService.excluir(555l);
    }

    @Test(expected = BusinessException.class)
    @InSequence(9)
    public void testExcluirRegistroComConstraint() {

        Kit kit = new Kit();
        Bem bem = new Bem();
        KitBem kitBem = new KitBem();
        List<KitBem> kitBens = new ArrayList<KitBem>();

        bem.setNomeBem("Novo bem");
        bem.setDescricaoBem("Novo bem descrição");
        bem.setSubElemento(genericPersister.findById(SubElemento.class, 33901414l));
        bem.setValorEstimadoBem(new BigDecimal("10000.00"));
        bemService.incluirAlterar(bem);

        bem = bemService.buscarPeloId(bem.getId());

        kitBens.add(kitBem);
        kitBem.setBem(bem);
        kitBem.setQuantidade(10);
        kitBem.setKit(kit);

        kit.setDescricaoKit("Descrição do kit");
        kit.setNomeKit("Nome do kit");

        kit.setKitsBens(kitBens);

        genericPersister.persist(kit);
        bemService.excluir(bem.getId());

    }

    @Test
    @InSequence(10)
    public void testExcluir() {
        Bem bem = new Bem();
        bem.setNomeBem("Novo bem");
        bem.setDescricaoBem("Novo bem descrição");
        bem.setSubElemento(genericPersister.findById(SubElemento.class, 33901414l));
        bem.setValorEstimadoBem(new BigDecimal("10000.00"));
        bemService.incluirAlterar(bem);

        bemService.excluir(bem.getId());
        Bem bemExclusao = bemService.buscarPeloId(bem.getId());
        Assert.assertNull(bemExclusao);
    }

    @Test
    @InSequence(11)
    public void testBuscarNull() {

        Bem bem = new Bem();
        bem.setNomeBem("Novo bem 1");
        bem.setDescricaoBem("Novo bem descrição");
        bem.setSubElemento(genericPersister.findById(SubElemento.class, 33901414l));
        bem.setValorEstimadoBem(new BigDecimal("10000.00"));
        bemService.incluirAlterar(bem);

        Bem bem2 = new Bem();
        bem2.setNomeBem("Novo bem 2");
        bem2.setDescricaoBem("Novo bem descrição");
        bem2.setSubElemento(genericPersister.findById(SubElemento.class, 33901414l));
        bem2.setValorEstimadoBem(new BigDecimal("10000.00"));
        bemService.incluirAlterar(bem2);

        Bem bem3 = new Bem();
        bem3.setNomeBem("Novo bem 3");
        bem3.setDescricaoBem("Novo bem descrição");
        bem3.setSubElemento(genericPersister.findById(SubElemento.class, 33901414l));
        bem3.setValorEstimadoBem(new BigDecimal("10000.00"));
        bemService.incluirAlterar(bem3);

        List<Bem> lista = bemService.buscar(null); // Deve retornar todos
        Assert.assertEquals(3, lista.size());
    }

    @Test
    @InSequence(12)
    public void testBuscarPorNomeLike() {

        Bem bem = new Bem();
        bem.setNomeBem("Bem teste");
        bem.setDescricaoBem("Novo bem descrição");
        bem.setSubElemento(genericPersister.findById(SubElemento.class, 33901414l));
        bem.setValorEstimadoBem(new BigDecimal("10000.00"));
        bemService.incluirAlterar(bem);

        Bem bem2 = new Bem();
        bem2.setNomeBem("Novo bem 2");
        bem2.setDescricaoBem("Novo bem descrição");
        bem2.setSubElemento(genericPersister.findById(SubElemento.class, 33901414l));
        bem2.setValorEstimadoBem(new BigDecimal("10000.00"));
        bemService.incluirAlterar(bem2);

        Bem bem3 = new Bem();
        bem3.setNomeBem("Novo bem 3");
        bem3.setDescricaoBem("Novo bem descrição");
        bem3.setSubElemento(genericPersister.findById(SubElemento.class, 33901414l));
        bem3.setValorEstimadoBem(new BigDecimal("10000.00"));
        bemService.incluirAlterar(bem3);

        Bem bemPesquisa = new Bem();
        bemPesquisa.setNomeBem("novo");
        List<Bem> lista = bemService.buscar(bemPesquisa);
        Assert.assertEquals(2, lista.size());
    }

    @Test
    @InSequence(13)
    public void testBuscarPorElemento() {

        Bem bem = new Bem();
        bem.setNomeBem("Bem teste");
        bem.setDescricaoBem("Novo bem descrição");
        bem.setSubElemento(genericPersister.findById(SubElemento.class, 33903984l));
        bem.setValorEstimadoBem(new BigDecimal("10000.00"));
        bemService.incluirAlterar(bem);

        Bem bem2 = new Bem();
        bem2.setNomeBem("Novo bem 2");
        bem2.setDescricaoBem("Novo bem descrição");
        bem2.setSubElemento(genericPersister.findById(SubElemento.class, 33903985l));
        bem2.setValorEstimadoBem(new BigDecimal("10000.00"));
        bemService.incluirAlterar(bem2);

        Bem bem3 = new Bem();
        bem3.setNomeBem("Novo bem 3");
        bem3.setDescricaoBem("Novo bem descrição");
        bem3.setSubElemento(genericPersister.findById(SubElemento.class, 33901414l));
        bem3.setValorEstimadoBem(new BigDecimal("10000.00"));
        bemService.incluirAlterar(bem3);

        Bem bemPesquisa = new Bem();
        Elemento elemento = genericPersister.findById(Elemento.class, 339039l);
        SubElemento subelemento = new SubElemento();
        subelemento.setElemento(elemento);
        bemPesquisa.setSubElemento(subelemento);
        List<Bem> lista = bemService.buscar(bemPesquisa);
        Assert.assertEquals(2, lista.size());
    }

    @Test
    @InSequence(14)
    public void testBuscarPorSubelemento() {

        Bem bem = new Bem();
        bem.setNomeBem("Bem teste");
        bem.setDescricaoBem("Novo bem descrição");
        bem.setSubElemento(genericPersister.findById(SubElemento.class, 33903984l));
        bem.setValorEstimadoBem(new BigDecimal("10000.00"));
        bemService.incluirAlterar(bem);

        Bem bem2 = new Bem();
        bem2.setNomeBem("Novo bem 2");
        bem2.setDescricaoBem("Novo bem descrição");
        bem2.setSubElemento(genericPersister.findById(SubElemento.class, 33903985l));
        bem2.setValorEstimadoBem(new BigDecimal("10000.00"));
        bemService.incluirAlterar(bem2);

        Bem bem3 = new Bem();
        bem3.setNomeBem("Novo bem 3");
        bem3.setDescricaoBem("Novo bem descrição");
        bem3.setSubElemento(genericPersister.findById(SubElemento.class, 33903985l));
        bem3.setValorEstimadoBem(new BigDecimal("10000.00"));
        bemService.incluirAlterar(bem3);

        Bem bemPesquisa = new Bem();
        SubElemento subelemento = genericPersister.findById(SubElemento.class, 33903985l);
        bemPesquisa.setSubElemento(subelemento);
        List<Bem> lista = bemService.buscar(bemPesquisa);
        Assert.assertEquals(2, lista.size());
    }

    @Test
    @InSequence(15)
    public void testBuscarPaginadoNull() {

        for (int i = 0; i < 11; i++) {
            Bem bem = new Bem();
            bem.setNomeBem("Novo bem " + i);
            bem.setDescricaoBem("Novo bem descrição" + i);
            bem.setSubElemento(genericPersister.findById(SubElemento.class, 33903985l));
            bem.setValorEstimadoBem(new BigDecimal("10000.00"));
            bemService.incluirAlterar(bem);
        }
        List<Bem> lista = bemService.buscarPaginado(null, 1, 5);
        Assert.assertEquals(5, lista.size());
    }

    @Test
    @InSequence(16)
    public void testBuscarPaginadoPorElemento() {
        for (int i = 0; i < 10; i++) {
            Bem bem = new Bem();
            bem.setNomeBem("Novo bem " + i);
            bem.setDescricaoBem("Novo bem descrição" + i);
            bem.setSubElemento(genericPersister.findById(SubElemento.class, 33903985l));
            bem.setValorEstimadoBem(new BigDecimal("10000.00"));
            bemService.incluirAlterar(bem);
        }

        Bem bemPesquisa = new Bem();
        Elemento elemento = genericPersister.findById(Elemento.class, 339039l);
        SubElemento subelemento = new SubElemento();
        subelemento.setElemento(elemento);
        bemPesquisa.setSubElemento(subelemento);
        List<Bem> lista = bemService.buscarPaginado(bemPesquisa, 1, 11);
        Assert.assertEquals(9, lista.size());

    }

    @Test
    @InSequence(17)
    public void testBuscarPaginadoPorSubElemento() {

        for (int i = 0; i < 3; i++) {
            Bem bem = new Bem();
            bem.setNomeBem("Novo bem " + i);
            bem.setDescricaoBem("Novo bem descrição" + i);
            bem.setSubElemento(genericPersister.findById(SubElemento.class, 33903985l));
            bem.setValorEstimadoBem(new BigDecimal("10000.00"));
            bemService.incluirAlterar(bem);
        }

        Bem bemPesquisa = new Bem();
        SubElemento subelemento = genericPersister.findById(SubElemento.class, 33903985l);
        bemPesquisa.setSubElemento(subelemento);
        List<Bem> lista = bemService.buscarPaginado(bemPesquisa, 1, 5);
        Assert.assertEquals(2, lista.size());
    }

    @Test
    @InSequence(18)
    public void testContarPaginadoNull() {
        for (int i = 0; i < 3; i++) {
            Bem bem = new Bem();
            bem.setNomeBem("Novo bem " + i);
            bem.setDescricaoBem("Novo bem descrição" + i);
            bem.setSubElemento(genericPersister.findById(SubElemento.class, 33903985l));
            bem.setValorEstimadoBem(new BigDecimal("10000.00"));
            bemService.incluirAlterar(bem);
        }
        long total = bemService.contarPaginado(null);
        Assert.assertEquals(3l, total);
    }

    @Test
    @InSequence(19)
    public void testContarPaginado() {
        for (int i = 0; i < 3; i++) {
            Bem bem = new Bem();
            bem.setNomeBem("Novo bem " + i);
            bem.setDescricaoBem("Novo bem descrição" + i);
            bem.setSubElemento(genericPersister.findById(SubElemento.class, 33903985l));
            bem.setValorEstimadoBem(new BigDecimal("10000.00"));
            bemService.incluirAlterar(bem);
        }

        Bem bemPesquisa = new Bem();
        bemPesquisa.setNomeBem("novo");
        long total = bemService.contarPaginado(null);
        Assert.assertEquals(3l, total);
    }

    @Test
    @InSequence(20)
    public void testIncluirComHistorico() {
        Bem bem = new Bem();
        bem.setNomeBem("Novo bem");
        bem.setDescricaoBem("Novo bem descrição");
        bem.setSubElemento(genericPersister.findById(SubElemento.class, 33901414l));
        bem.setValorEstimadoBem(new BigDecimal("10000.00"));
        bem.setDataEstimativa(LocalDate.now());

        bemService.incluirAlterar(bem);

        List<HistoricoBem> listaHistorico = historicoBemService.buscarPeloIdBem(bem.getId());
        Assert.assertEquals(1, listaHistorico.size());
    }

    @Test(expected = BusinessException.class)
    @InSequence(21)
    public void testAlterarComDataEstimativaAnterior() {
        Bem bem = new Bem();
        bem.setNomeBem("Novo bem");
        bem.setDescricaoBem("Novo bem descrição");
        bem.setSubElemento(genericPersister.findById(SubElemento.class, 33901414l));
        bem.setValorEstimadoBem(new BigDecimal("10000.00"));
        bem.setDataEstimativa(LocalDate.parse("2015-05-01"));

        bemService.incluirAlterar(bem);

        Bem bemAlterar = bemService.buscarPeloId(bem.getId());

        bemAlterar.setValorEstimadoBem(new BigDecimal("10001.00"));
        bemAlterar.setDataEstimativa(LocalDate.parse("2015-01-01"));
        bemAlterar.setHistoricoBemValores(new ArrayList<HistoricoBem>());
        bemAlterar.setTags(new ArrayList<TagBem>());
        bemAlterar.setAnexos(new ArrayList<AnexoBem>());

        bemService.incluirAlterar(bemAlterar);

    }

    @Test
    @InSequence(22)
    public void testAlterarComHistorico() {
        Bem bemNovo = new Bem();

        bemNovo.setNomeBem("Novo bem 1");
        bemNovo.setDescricaoBem("Nova descrição 1");
        bemNovo.setSubElemento(genericPersister.findById(SubElemento.class, 33903030l));
        bemNovo.setValorEstimadoBem(new BigDecimal("100.00"));
        bemNovo.setDataEstimativa(LocalDate.parse("2015-02-01"));
        bemService.incluirAlterar(bemNovo);

        Bem bemAlterar = bemService.buscarPeloId(bemNovo.getId());

        bemAlterar.setNomeBem("Novo bem 1 alterado");
        bemAlterar.setDescricaoBem("Nova descrição 1 alterado");
        bemAlterar.setSubElemento(genericPersister.findById(SubElemento.class, 33903030l));
        bemAlterar.setValorEstimadoBem(new BigDecimal("1000.00"));
        bemAlterar.setAnexos(new ArrayList<AnexoBem>());
        bemAlterar.setHistoricoBemValores(new ArrayList<HistoricoBem>());
        bemAlterar.setTags(new ArrayList<TagBem>());
        bemAlterar.setAnexos(new ArrayList<AnexoBem>());
        bemAlterar.setDataEstimativa(LocalDate.parse("2015-03-01"));
        bemService.incluirAlterar(bemAlterar);

        List<HistoricoBem> listaHistorico = historicoBemService.buscarPeloIdBem(bemAlterar.getId());
        Assert.assertEquals(2, listaHistorico.size());

    }

    @Before
    public void createData() throws Exception {

        // tx.begin();
        // em.createQuery("delete from HistoricoBem").executeUpdate();
        // em.createQuery("delete from TagBem").executeUpdate();
        // em.createQuery("delete from AnexoBem").executeUpdate();
        // em.createQuery("delete from KitBem").executeUpdate();
        // em.createQuery("delete from Kit").executeUpdate();
        // em.createQuery("delete from Bem").executeUpdate();
        // em.createQuery("delete from SubElemento").executeUpdate();
        // em.createQuery("delete from Elemento").executeUpdate();
        // tx.commit();
        //
        // String line = "";
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
    }

}
