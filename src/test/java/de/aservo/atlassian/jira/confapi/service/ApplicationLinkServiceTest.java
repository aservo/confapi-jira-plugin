package de.aservo.atlassian.jira.confapi.service;

import atlassian.settings.setup.DefaultApplicationLink;
import atlassian.settings.setup.DefaultApplicationType;
import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.applinks.spi.link.ApplicationLinkDetails;
import com.atlassian.applinks.spi.link.MutatingApplicationLinkService;
import com.atlassian.applinks.spi.manifest.ManifestNotFoundException;
import com.atlassian.applinks.spi.util.TypeAccessor;
import de.aservo.atlassian.confapi.model.ApplicationLinkBean;
import de.aservo.atlassian.confapi.model.type.ApplicationLinkTypes;
import de.aservo.atlassian.jira.confapi.model.type.DefaultAuthenticationScenario;
import de.aservo.atlassian.jira.confapi.model.util.ApplicationLinkBeanUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.validation.ValidationException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationLinkServiceTest {

    @Mock
    private MutatingApplicationLinkService mutatingApplicationLinkService;

    @Mock
    private TypeAccessor typeAccessor;

    @InjectMocks
    private ApplicationLinkServiceImpl applicationLinkService;

    @Test
    public void testDefaultDefaultAuthenticationScenarioImpl() {
        DefaultAuthenticationScenario defaultAuthenticationScenario = new DefaultAuthenticationScenario();
        assertTrue(defaultAuthenticationScenario.isCommonUserBase());
        assertTrue(defaultAuthenticationScenario.isTrusted());
    }

    @Test
    public void testGetApplicationLinks() throws URISyntaxException {
        ApplicationLink applicationLink = createApplicationLink();
        doReturn(Collections.singletonList(applicationLink)).when(mutatingApplicationLinkService).getApplicationLinks();

        List<ApplicationLinkBean> applicationLinks = applicationLinkService.getApplicationLinks();

        assertEquals(applicationLinks.get(0), ApplicationLinkBeanUtil.toApplicationLinkBean(applicationLink));
    }

    @Test
    public void testAddApplicationLinkWithoutExistingTargetLink()
            throws URISyntaxException, ManifestNotFoundException {

        ApplicationLink applicationLink = createApplicationLink();
        ApplicationLinkBean applicationLinkBean = createApplicationLinkBean();

        doReturn(applicationLink).when(mutatingApplicationLinkService).createApplicationLink(
                any(ApplicationType.class), any(ApplicationLinkDetails.class));
        doReturn(new DefaultApplicationType()).when(typeAccessor).getApplicationType(any());

        ApplicationLinkBean applicationLinkResponse = applicationLinkService.addApplicationLink(applicationLinkBean);

        assertEquals(applicationLinkResponse.getName(), applicationLinkBean.getName());
        assertNotEquals(applicationLinkResponse, applicationLinkBean);
    }

    @Test
    public void testAddApplicationLinkWithExistingTargetLink() throws URISyntaxException, ManifestNotFoundException {
        ApplicationLink applicationLink = createApplicationLink();
        ApplicationLinkBean applicationLinkBean = createApplicationLinkBean();

        doReturn(applicationLink).when(mutatingApplicationLinkService).createApplicationLink(
                any(ApplicationType.class), any(ApplicationLinkDetails.class));
        doReturn(applicationLink).when(mutatingApplicationLinkService).getPrimaryApplicationLink(any());
        doReturn(new DefaultApplicationType()).when(typeAccessor).getApplicationType(any());

        ApplicationLinkBean applicationLinkResponse = applicationLinkService.addApplicationLink(applicationLinkBean);

        assertEquals(applicationLinkResponse.getName(), applicationLinkBean.getName());
        assertNotEquals(applicationLinkResponse, applicationLinkBean);
    }

    @Test(expected = ValidationException.class)
    public void testAddApplicationLinkMissingLinkType() throws URISyntaxException {
        ApplicationLinkBean applicationLinkBean = createApplicationLinkBean();
        applicationLinkBean.setLinkType(null);

        applicationLinkService.addApplicationLink(applicationLinkBean);
    }

    @Test
    public void testApplicationLinkTypeConverter() throws URISyntaxException, ManifestNotFoundException {
        for (ApplicationLinkTypes linkType : ApplicationLinkTypes.values()) {
            ApplicationLink applicationLink = createApplicationLink();
            ApplicationLinkBean applicationLinkBean = createApplicationLinkBean();
            applicationLinkBean.setLinkType(linkType);

            doReturn(applicationLink).when(mutatingApplicationLinkService).createApplicationLink(
                    any(ApplicationType.class), any(ApplicationLinkDetails.class));
            doReturn(new DefaultApplicationType()).when(typeAccessor).getApplicationType(any());

            ApplicationLinkBean applicationLinkResponse = applicationLinkService.addApplicationLink(applicationLinkBean);

            assertEquals(applicationLinkResponse.getName(), applicationLinkBean.getName());
            // TODO: assertEquals(applicationLinkResponse.getLinkType(), applicationLink.getType());
        }
    }

    private ApplicationLinkBean createApplicationLinkBean() throws URISyntaxException {
        ApplicationLinkBean bean = ApplicationLinkBeanUtil.toApplicationLinkBean(createApplicationLink());
        bean.setLinkType(ApplicationLinkTypes.CROWD);
        bean.setUsername("test");
        bean.setPassword("test");
        return bean;
    }

    private ApplicationLink createApplicationLink() throws URISyntaxException {
        ApplicationId applicationId = new ApplicationId(UUID.randomUUID().toString());
        URI uri = new URI("http://localhost");
        return new DefaultApplicationLink(applicationId, new DefaultApplicationType(), "test", uri, uri, false, false);
    }

}
