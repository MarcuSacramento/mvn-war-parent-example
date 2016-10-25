package br.gov.mj.side.test.service;

import java.io.File;
import java.util.List;

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

import br.gov.mj.apoio.entidades.Municipio;
import br.gov.mj.infra.negocio.persistencia.IGenericPersister;
import br.gov.mj.side.web.dao.MunicipioDAO;
import br.gov.mj.side.web.service.MunicipioService;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.ResourcesProducer;

@RunWith(Arquillian.class)
public class MunicipioServiceTest {

    @PersistenceContext
    EntityManager em;

    @Inject
    UserTransaction tx;

    @Inject
    IGenericPersister genericPersister;

    @Inject
    private MunicipioService municipioService;

    @Deployment
    public static Archive<?> createDeployment() {
        File[] jars = Maven.configureResolver().withRemoteRepo("artifactory-mj", "http://cgtimaven.mj.gov.br/artifactory/repo/", "default").resolve("br.gov.mj.infra:infra-negocio:1.2.0", "org.jacoco:org.jacoco.core:0.7.5.201505241946", "org.jadira.usertype:usertype.extended:3.2.0.GA")
                .withTransitivity().asFile();

        Archive<?> file = ShrinkWrap.create(WebArchive.class, "side-test.war").addPackages(true, "br.gov.mj.seg.entidades").addPackages(true, "br.gov.mj.apoio.entidades").addPackages(true, "br.gov.mj.side.entidades").addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                .addAsResource("init.txt", "init.txt").addAsResource("ufs.csv", "ufs.csv").addAsResource("municipios.csv", "municipios.csv").addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml").addAsWebInfResource("side-test-ds.xml", "side-test-ds.xml")
                .addAsWebInfResource("test-jboss-web.xml", "jboss-web.xml").addClass(ResourcesProducer.class).addClass(MunicipioDAO.class).addClass(MunicipioService.class).addClass(Constants.class).addAsLibraries(jars);
        System.out.println(file.toString(true));
        return file;
    }

    @Test
    @InSequence(1)
    public void testBuscarPeloIdNull() throws Throwable {

        List<Municipio> lista = municipioService.buscarPelaUfId(null);
        Assert.assertTrue(lista.size() == 74);
    }

    @Test
    @InSequence(2)
    public void testBuscarPelaSiglaNull() throws Throwable {
        List<Municipio> lista = municipioService.buscarPelaUfSigla(null);
        Assert.assertTrue(lista.size() == 74);
    }

    @Test
    @InSequence(3)
    public void testBuscarPorId() {
        List<Municipio> lista = municipioService.buscarPelaUfId(5l);
        Assert.assertTrue(lista.size() == 52);
    }

    @Test
    @InSequence(4)
    public void testBuscarPorSigla() {
        List<Municipio> lista = municipioService.buscarPelaUfSigla("ro");
        Assert.assertTrue(lista.size() == 52);
    }

    @Before
    public void createData() throws Exception {
        //
        // tx.begin();
        // em.createQuery("delete from Municipio").executeUpdate();
        // em.createQuery("delete from Uf").executeUpdate();
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

    }

}
