package de.aservo.confapi.jira.service;

import atlassian.mail.server.DefaultTestPopMailServerImpl;
import atlassian.mail.server.DefaultTestSmtpMailServerImpl;
import atlassian.mail.server.OtherTestPopMailServerImpl;
import atlassian.mail.server.OtherTestSmtpMailServerImpl;
import com.atlassian.mail.MailException;
import com.atlassian.mail.server.MailServerManager;
import com.atlassian.mail.server.PopMailServer;
import com.atlassian.mail.server.SMTPMailServer;
import de.aservo.confapi.commons.exception.BadRequestException;
import de.aservo.confapi.commons.model.MailServerPopBean;
import de.aservo.confapi.commons.model.MailServerSmtpBean;
import de.aservo.confapi.jira.model.util.MailServerPopBeanUtil;
import de.aservo.confapi.jira.model.util.MailServerSmtpBeanUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MailServerServiceTest {

    @Mock
    private MailServerManager mailServerManager;

    private MailServerServiceImpl mailServerService;

    @Before
    public void setup() {
        mailServerService = new MailServerServiceImpl(mailServerManager);
    }

    @Test
    public void testGetSmtpMailServer() {
        final SMTPMailServer smtpMailServer = new DefaultTestSmtpMailServerImpl();
        doReturn(smtpMailServer).when(mailServerManager).getDefaultSMTPMailServer();

        final MailServerSmtpBean bean = mailServerService.getMailServerSmtp();

        assertEquals(smtpMailServer.getName(), bean.getName());
        assertEquals(smtpMailServer.getDescription(), bean.getDescription());
        assertEquals(smtpMailServer.getHostname(), bean.getHost());
        assertEquals(smtpMailServer.getTimeout(), bean.getTimeout());
        assertEquals(smtpMailServer.getUsername(), bean.getUsername());
        assertNull(bean.getPassword());
        assertEquals(smtpMailServer.getDefaultFrom(), bean.getFrom());
        assertEquals(smtpMailServer.getPrefix(), bean.getPrefix());
        assertEquals(smtpMailServer.isTlsRequired(), bean.getUseTls());
        assertEquals(smtpMailServer.getMailProtocol().getProtocol(), bean.getProtocol());
        assertEquals(smtpMailServer.getPort(), String.valueOf(bean.getPort()));
    }

    @Test
    public void testGetSmtpMailServerIsNull() {
        final MailServerSmtpBean response = mailServerService.getMailServerSmtp();
        assertNull(response);
    }

    @Test
    public void testPutSmtpMaiLServerUpdate() throws Exception {
        final SMTPMailServer defaultSmtpMailServer = new DefaultTestSmtpMailServerImpl();
        doReturn(true).when(mailServerManager).isDefaultSMTPMailServerDefined();
        doReturn(defaultSmtpMailServer).when(mailServerManager).getDefaultSMTPMailServer();

        final SMTPMailServer updateSmtpMailServer = new OtherTestSmtpMailServerImpl();
        final MailServerSmtpBean requestMailServerSmtpBean = MailServerSmtpBeanUtil.toMailServerSmtpBean(updateSmtpMailServer);
        assertNotNull(requestMailServerSmtpBean);
        final MailServerSmtpBean responseMailServerSmtpBean = mailServerService.setMailServerSmtp(requestMailServerSmtpBean);

        final ArgumentCaptor<SMTPMailServer> smtpMailServerCaptor = ArgumentCaptor.forClass(SMTPMailServer.class);
        verify(mailServerManager).update(smtpMailServerCaptor.capture());
        final SMTPMailServer smtpMailServer = smtpMailServerCaptor.getValue();

        assertEquals(MailServerSmtpBeanUtil.toMailServerSmtpBean(updateSmtpMailServer), MailServerSmtpBeanUtil.toMailServerSmtpBean(smtpMailServer));
        assertEquals(requestMailServerSmtpBean, responseMailServerSmtpBean);
    }

    @Test
    public void testPutSmtpMaiLServerCreate() throws Exception {
        final SMTPMailServer createSmtpMailServer = new DefaultTestSmtpMailServerImpl();
        final MailServerSmtpBean requestMailServerSmtpBean = MailServerSmtpBeanUtil.toMailServerSmtpBean(createSmtpMailServer);
        assertNotNull(requestMailServerSmtpBean);
        final MailServerSmtpBean responseMailServerSmtpBean = mailServerService.setMailServerSmtp(requestMailServerSmtpBean);

        final ArgumentCaptor<SMTPMailServer> smtpMailServerCaptor = ArgumentCaptor.forClass(SMTPMailServer.class);
        verify(mailServerManager).create(smtpMailServerCaptor.capture());
        final SMTPMailServer smtpMailServer = smtpMailServerCaptor.getValue();

        assertEquals(MailServerSmtpBeanUtil.toMailServerSmtpBean(createSmtpMailServer), MailServerSmtpBeanUtil.toMailServerSmtpBean(smtpMailServer));
        assertEquals(requestMailServerSmtpBean, responseMailServerSmtpBean);
    }

    @Test
    public void testPutSmtpMaiLServerWithoutPort() throws Exception {
        final SMTPMailServer createSmtpMailServer = new DefaultTestSmtpMailServerImpl();
        createSmtpMailServer.setPort(null);

        final MailServerSmtpBean requestMailServerSmtpBean = MailServerSmtpBeanUtil.toMailServerSmtpBean(createSmtpMailServer);
        assertNotNull(requestMailServerSmtpBean);
        final MailServerSmtpBean responseMailServerSmtpBean = mailServerService.setMailServerSmtp(requestMailServerSmtpBean);

        final ArgumentCaptor<SMTPMailServer> smtpMailServerCaptor = ArgumentCaptor.forClass(SMTPMailServer.class);
        verify(mailServerManager).create(smtpMailServerCaptor.capture());
        final SMTPMailServer smtpMailServer = smtpMailServerCaptor.getValue();

        assertEquals(createSmtpMailServer.getMailProtocol().getDefaultPort(), smtpMailServer.getPort());
        assertEquals(requestMailServerSmtpBean, responseMailServerSmtpBean);
    }

    @Test(expected = BadRequestException.class)
    public void testPutSmtpMaiLServerException() throws MailException {
        doThrow(new MailException("SMTP test exception")).when(mailServerManager).create(any());

        final SMTPMailServer createSmtpMailServer = new DefaultTestSmtpMailServerImpl();
        final MailServerSmtpBean requestMailServerSmtpBean = MailServerSmtpBeanUtil.toMailServerSmtpBean(createSmtpMailServer);
        assertNotNull(requestMailServerSmtpBean);
        mailServerService.setMailServerSmtp(requestMailServerSmtpBean);
    }

    @Test
    public void testGetPopMailServer() {
        final PopMailServer popMailServer = new DefaultTestPopMailServerImpl();
        doReturn(popMailServer).when(mailServerManager).getDefaultPopMailServer();

        final MailServerPopBean bean = mailServerService.getMailServerPop();

        assertEquals(popMailServer.getName(), bean.getName());
        assertEquals(popMailServer.getDescription(), bean.getDescription());
        assertEquals(popMailServer.getHostname(), bean.getHost());
        assertEquals(popMailServer.getTimeout(), bean.getTimeout());
        assertEquals(popMailServer.getUsername(), bean.getUsername());
        assertNull(bean.getPassword());
        assertEquals(popMailServer.getMailProtocol().getProtocol(), bean.getProtocol());
        assertEquals(popMailServer.getPort(), String.valueOf(bean.getPort()));
    }

    @Test
    public void testGetPopMailServerIsNull() {
        final MailServerPopBean bean = mailServerService.getMailServerPop();
        assertNull(bean);
    }

    @Test
    public void testPutPopMaiLServerUpdate() throws Exception {
        final PopMailServer defaultPopMailServer = new DefaultTestPopMailServerImpl();
        doReturn(defaultPopMailServer).when(mailServerManager).getDefaultPopMailServer();

        final PopMailServer updatePopMailServer = new OtherTestPopMailServerImpl();
        final MailServerPopBean requestMailServerPopBean = MailServerPopBeanUtil.toMailServerPopBean(updatePopMailServer);
        assertNotNull(requestMailServerPopBean);
        final MailServerPopBean responseMailServerPopBean = mailServerService.setMailServerPop(requestMailServerPopBean);

        final ArgumentCaptor<PopMailServer> popMailServerCaptor = ArgumentCaptor.forClass(PopMailServer.class);
        verify(mailServerManager).update(popMailServerCaptor.capture());
        final PopMailServer popMailServer = popMailServerCaptor.getValue();

        assertEquals(MailServerPopBeanUtil.toMailServerPopBean(updatePopMailServer), MailServerPopBeanUtil.toMailServerPopBean(popMailServer));
        assertEquals(requestMailServerPopBean, responseMailServerPopBean);
    }

    @Test
    public void testPutPopMaiLServerCreate() throws Exception {
        final PopMailServer createPopMailServer = new DefaultTestPopMailServerImpl();
        final MailServerPopBean requestMailServerPopBean = MailServerPopBeanUtil.toMailServerPopBean(createPopMailServer);
        assertNotNull(requestMailServerPopBean);
        final MailServerPopBean responseMailServerPopBean = mailServerService.setMailServerPop(requestMailServerPopBean);

        final ArgumentCaptor<PopMailServer> popMailServerCaptor = ArgumentCaptor.forClass(PopMailServer.class);
        verify(mailServerManager).create(popMailServerCaptor.capture());
        final PopMailServer popMailServer = popMailServerCaptor.getValue();

        MailServerPopBean from1 = MailServerPopBeanUtil.toMailServerPopBean(createPopMailServer);
        MailServerPopBean from2 = MailServerPopBeanUtil.toMailServerPopBean(popMailServer);

        assertEquals(from1, from2);
        assertEquals(requestMailServerPopBean, responseMailServerPopBean);
    }

    @Test
    public void testPutPopMaiLServerWithoutPort() throws Exception {
        final PopMailServer createPopMailServer = new DefaultTestPopMailServerImpl();
        createPopMailServer.setPort(null);

        final MailServerPopBean requestMailServerPopBean = MailServerPopBeanUtil.toMailServerPopBean(createPopMailServer);
        assertNotNull(requestMailServerPopBean);
        final MailServerPopBean responseMailServerPopBean = mailServerService.setMailServerPop(requestMailServerPopBean);

        final ArgumentCaptor<PopMailServer> popMailServerCaptor = ArgumentCaptor.forClass(PopMailServer.class);
        verify(mailServerManager).create(popMailServerCaptor.capture());
        final PopMailServer popMailServer = popMailServerCaptor.getValue();

        assertEquals(createPopMailServer.getMailProtocol().getDefaultPort(), popMailServer.getPort());
        assertEquals(requestMailServerPopBean, responseMailServerPopBean);
    }

    @Test(expected = BadRequestException.class)
    public void testPutPopMaiLServerException() throws Exception {
        doThrow(new MailException("POP test exception")).when(mailServerManager).create(any());

        final PopMailServer createPopMailServer = new DefaultTestPopMailServerImpl();
        final MailServerPopBean requestMailServerPopBean = MailServerPopBeanUtil.toMailServerPopBean(createPopMailServer);
        assertNotNull(requestMailServerPopBean);
        mailServerService.setMailServerPop(requestMailServerPopBean);
    }

}
