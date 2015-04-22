/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import at.ac.tuwien.dsg.rsybl.controllercommunication.interactionProcessing.InitiateInteractions;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.communication.Interaction;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.communicationModel.Role;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IRole;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

/**
 *
 * @author Georgiana
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CloudAMPQTest {

    private static InitiateInteractions initiateInteractions;

    public CloudAMPQTest() {
    }

    @BeforeClass
    public static void setUpClass() {
//            initiateInteractions = new InitiateInteractions();
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Test
    public void initQueueAndCreateMessage() {
        initiateInteractions = new InitiateInteractions();

        Interaction myInteraction = new Interaction();
        myInteraction.setId(UUID.randomUUID().toString());
        myInteraction.setInitiationDate(new Date());
        myInteraction.setDialogId(UUID.randomUUID().toString());
        IRole role = new Role();
        role.setRoleName("EC");
        IRole ecrole = new Role();
        ecrole.setRoleName("EC");
        myInteraction.setInitiator(ecrole);
        myInteraction.setReceiver(role);
        initiateInteractions.initiateInteraction(myInteraction);
        initiateInteractions.closeInteraction();

    }

    @Test
    public void readMessage() {
        initiateInteractions = new InitiateInteractions();
        InitiateInteractions trala = new InitiateInteractions();
        trala.startListeningToMessages();
        Interaction myInteraction = new Interaction();
        myInteraction.setId(UUID.randomUUID().toString());
        myInteraction.setInitiationDate(new Date());
        myInteraction.setDialogId(UUID.randomUUID().toString());
        IRole role = new Role();
        role.setRoleName("EC");
        IRole ecrole = new Role();
        ecrole.setRoleName("EC");
        myInteraction.setInitiator(ecrole);
        myInteraction.setReceiver(role);
        initiateInteractions.initiateInteraction(myInteraction);

        try {
            Thread.sleep(20000);
        } catch (InterruptedException ex) {
            Logger.getLogger(CloudAMPQTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<Interaction> interactions = trala.getReadMessages();
        System.err.println(interactions.size());
        assertTrue(interactions.size() == 1);
        System.out.println(interactions.get(0).getInitiationDate());
        initiateInteractions.closeInteraction();
        trala.closeInteraction();
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
}
