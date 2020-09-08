package de.aservo.confapi.jira.rest;

import atlassian.mail.server.DefaultTestPopMailServerImpl;
import atlassian.mail.server.DefaultTestSmtpMailServerImpl;
import atlassian.mail.server.OtherTestPopMailServerImpl;
import atlassian.mail.server.OtherTestSmtpMailServerImpl;
import com.atlassian.mail.MailException;
import com.atlassian.mail.server.MailServerManager;
import com.atlassian.mail.server.PopMailServer;
import com.atlassian.mail.server.SMTPMailServer;
import de.aservo.confapi.commons.constants.ConfAPI;
import de.aservo.confapi.commons.model.ErrorCollection;
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

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import static de.aservo.confapi.commons.junit.ResourceAssert.assertResourcePath;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MailServerResourceTest {

    @Mock
    private MailServerManager mailServerManager;

    private MailServerResourceImpl mailServerResource;

    @Before
    public void setup() {
        mailServerResource = new MailServerResourceImpl(mailServerManager);
    }

    @Test
    public void testResourcePath() {
        assertResourcePath(mailServerResource, ConfAPI.MAIL_SERVER);
    }

    @Test
    public void testGetSmtpMailServer() {
        final SMTPMailServer smtpMailServer = new DefaultTestSmtpMailServerImpl();
        doReturn(smtpMailServer).when(mailServerManager).getDefaultSMTPMailServer();

        final Response response = mailServerResource.getMailServerSmtp();
        final MailServerSmtpBean bean = (MailServerSmtpBean) response.getEntity();

        assertEquals(smtpMailServer.getName(), bean.getName());
        assertEquals(smtpMailServer.getDescription(), bean.getDescription());
        assertEquals(smtpMailServer.getHostname(), bean.getHost());
        assertEquals(smtpMailServer.getTimeout(), bean.getTimeout());
        assertEquals(smtpMailServer.getUsername(), bean.getUsername());
        assertNull(bean.getPassword());
        assertEquals(smtpMailServer.getDefaultFrom(), bean.getFrom());
        assertEquals(smtpMailServer.getPrefix(), bean.getPrefix());
        assertEquals(smtpMailServer.isTlsRequired(), bean.isTls());
        assertEquals(smtpMailServer.getMailProtocol().getProtocol(), bean.getProtocol());
        assertEquals(smtpMailServer.getPort(), String.valueOf(bean.getPort()));
    }

    @Test
    public void testGetSmtpMailServerNoContent() {
        final Response response = mailServerResource.getMailServerSmtp();
        final ErrorCollection bean = (ErrorCollection) response.getEntity();

        assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());
        assertTrue(bean.hasAnyErrors());
    }

    @Test
    public void testPutSmtpMaiLServerUpdate() throws Exception {
        final SMTPMailServer defaultSmtpMailServer = new DefaultTestSmtpMailServerImpl();
        doReturn(true).when(mailServerManager).isDefaultSMTPMailServerDefined();
        doReturn(defaultSmtpMailServer).when(mailServerManager).getDefaultSMTPMailServer();

        final SMTPMailServer updateSmtpMailServer = new OtherTestSmtpMailServerImpl();
        final MailServerSmtpBean requestMailServerSmtpBean = MailServerSmtpBeanUtil.toMailServerSmtpBean(updateSmtpMailServer);
        final Response response = mailServerResource.setMailServerSmtp(requestMailServerSmtpBean);
        final MailServerSmtpBean responseMailServerSmtpBean = (MailServerSmtpBean) response.getEntity();

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
        final Response response = mailServerResource.setMailServerSmtp(requestMailServerSmtpBean);
        final MailServerSmtpBean responseMailServerSmtpBean = (MailServerSmtpBean) response.getEntity();

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
        final Response response = mailServerResource.setMailServerSmtp(requestMailServerSmtpBean);
        final MailServerSmtpBean responseMailServerSmtpBean = (MailServerSmtpBean) response.getEntity();

        final ArgumentCaptor<SMTPMailServer> smtpMailServerCaptor = ArgumentCaptor.forClass(SMTPMailServer.class);
        verify(mailServerManager).create(smtpMailServerCaptor.capture());
        final SMTPMailServer smtpMailServer = smtpMailServerCaptor.getValue();

        assertEquals(createSmtpMailServer.getMailProtocol().getDefaultPort(), smtpMailServer.getPort());
        assertEquals(requestMailServerSmtpBean, responseMailServerSmtpBean);
    }

    @Test
    public void testPutSmtpMaiLServerException() throws Exception {
        doThrow(new MailException("SMTP test exception")).when(mailServerManager).create(any());

        final SMTPMailServer createSmtpMailServer = new DefaultTestSmtpMailServerImpl();
        final MailServerSmtpBean requestMailServerSmtpBean = MailServerSmtpBeanUtil.toMailServerSmtpBean(createSmtpMailServer);
        final Response response = mailServerResource.setMailServerSmtp(requestMailServerSmtpBean);
        final ErrorCollection responseErrorCollection = (ErrorCollection) response.getEntity();

        assertEquals(response.getStatus(), Status.BAD_REQUEST.getStatusCode());
        assertTrue(responseErrorCollection.hasAnyErrors());
    }

    @Test
    public void testGetPopMailServer() {
        final PopMailServer popMailServer = new DefaultTestPopMailServerImpl();
        doReturn(popMailServer).when(mailServerManager).getDefaultPopMailServer();

        final Response response = mailServerResource.getMailServerPop();
        final MailServerPopBean bean = (MailServerPopBean) response.getEntity();

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
    public void testGetPopMailServerNoContent() {
        final Response response = mailServerResource.getMailServerPop();
        final ErrorCollection responseErrorCollection = (ErrorCollection) response.getEntity();

        assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());
        assertTrue(responseErrorCollection.hasAnyErrors());
    }

    @Test
    public void testPutPopMaiLServerUpdate() throws Exception {
        final PopMailServer defaultPopMailServer = new DefaultTestPopMailServerImpl();
        doReturn(defaultPopMailServer).when(mailServerManager).getDefaultPopMailServer();

        final PopMailServer updatePopMailServer = new OtherTestPopMailServerImpl();
        final MailServerPopBean requestMailServerPopBean = MailServerPopBeanUtil.toMailServerPopBean(updatePopMailServer);
        final Response response = mailServerResource.setMailServerPop(requestMailServerPopBean);
        final MailServerPopBean responseMailServerPopBean = (MailServerPopBean) response.getEntity();

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
        final Response response = mailServerResource.setMailServerPop(requestMailServerPopBean);
        final MailServerPopBean responseMailServerPopBean = (MailServerPopBean) response.getEntity();

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
        final Response response = mailServerResource.setMailServerPop(requestMailServerPopBean);
        final MailServerPopBean responseMailServerPopBean = (MailServerPopBean) response.getEntity();

        final ArgumentCaptor<PopMailServer> popMailServerCaptor = ArgumentCaptor.forClass(PopMailServer.class);
        verify(mailServerManager).create(popMailServerCaptor.capture());
        final PopMailServer popMailServer = popMailServerCaptor.getValue();

        assertEquals(createPopMailServer.getMailProtocol().getDefaultPort(), popMailServer.getPort());
        assertEquals(requestMailServerPopBean, responseMailServerPopBean);
    }

    @Test
    public void testPutPopMaiLServerException() throws Exception {
        doThrow(new MailException("POP test exception")).when(mailServerManager).create(any());

        final PopMailServer createPopMailServer = new DefaultTestPopMailServerImpl();
        final MailServerPopBean requestMailServerPopBean = MailServerPopBeanUtil.toMailServerPopBean(createPopMailServer);
        final Response response = mailServerResource.setMailServerPop(requestMailServerPopBean);
        final ErrorCollection responseErrorCollection = (ErrorCollection) response.getEntity();

        assertEquals(response.getStatus(), Status.BAD_REQUEST.getStatusCode());
        assertTrue(responseErrorCollection.hasAnyErrors());
    }

}
