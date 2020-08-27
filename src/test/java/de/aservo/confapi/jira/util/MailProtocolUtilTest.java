package de.aservo.confapi.jira.util;

import com.atlassian.mail.MailProtocol;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class MailProtocolUtilTest {

    @Test
    public void testFind() {
        final MailProtocol protocolImap = MailProtocolUtil.find(MailProtocol.IMAP.getProtocol(), null);
        assertEquals(MailProtocol.IMAP, protocolImap);
    }

    @Test
    public void testFindNotFoundDefaultValue() {
        final MailProtocol protocolDefault = MailProtocolUtil.find("abc", null);
        assertNull(protocolDefault);
    }

    @Test
    public void testFindBlankDefaultValue() {
        final MailProtocol protocolDefault = MailProtocolUtil.find("", null);
        assertNull(protocolDefault);
    }

}
