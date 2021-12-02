package de.aservo.confapi.jira.service;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.config.properties.LnFDefaultColorProvider;
import com.atlassian.jira.config.properties.LogoProvider;
import com.atlassian.jira.config.properties.UiSettingsStateManager;
import com.atlassian.jira.config.util.JiraHome;
import com.atlassian.jira.lookandfeel.LogoChoice;
import com.atlassian.jira.lookandfeel.LookAndFeelProperties;
import com.atlassian.jira.lookandfeel.upload.UploadService;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import de.aservo.confapi.commons.exception.InternalServerErrorException;
import de.aservo.confapi.commons.model.SettingsBrandingColorSchemeBean;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

import static de.aservo.confapi.jira.model.util.SettingsColourSchemeBeanUtilTest.getDummyBaseColourScheme;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ComponentAccessor.class)
public class SettingsBrandingServiceTest {

    private ApplicationProperties applicationProperties;
    private UploadService uploadService;
    private LookAndFeelProperties lookAndFeelProperties;
    private SettingsBrandingServiceImpl settingsBrandingService;

    @Before
    public void setup() {
        //when using powermock we cannot initialize with @Mock or @InjectMocks unfortunately
        applicationProperties = mock(ApplicationProperties.class);
        uploadService = mock(UploadService.class);
        lookAndFeelProperties = mock(LookAndFeelProperties.class);

        JiraHome jiraHome = mock(JiraHome.class);
        JiraAuthenticationContext authenticationContext = mock(JiraAuthenticationContext.class);
        PluginSettingsFactory globalSettingsFactory = mock(PluginSettingsFactory.class);

        PluginSettings pluginSettings = mock(PluginSettings.class);
        doReturn(pluginSettings).when(globalSettingsFactory).createGlobalSettings();

        settingsBrandingService = new SettingsBrandingServiceImpl(applicationProperties, authenticationContext, jiraHome, globalSettingsFactory, lookAndFeelProperties, uploadService);
    }

    @Test
    public void testGetColourScheme() {

        Map<String, Object> dummyBaseColourScheme = getDummyBaseColourScheme();
        doReturn(dummyBaseColourScheme).when(applicationProperties).asMap();

        SettingsBrandingColorSchemeBean colourScheme = settingsBrandingService.getColourScheme();

        assertEquals(SettingsBrandingColorSchemeBean.EXAMPLE_1.getTopBar(), colourScheme.getTopBar());
    }

    @Test
    public void testSetColourScheme() {

        SettingsBrandingColorSchemeBean schemeBean = SettingsBrandingColorSchemeBean.EXAMPLE_1;

        Map<String, Object> dummyBaseColourScheme = getDummyBaseColourScheme();
        doReturn(dummyBaseColourScheme).when(applicationProperties).asMap();

        SettingsBrandingColorSchemeBean colourScheme = settingsBrandingService.setColourScheme(schemeBean);

        assertEquals(schemeBean.getTopBar(), colourScheme.getTopBar());
    }

    //InternalServerErrorException -> FileNotFoundException is expected because no logofile is present in the filesystem at test time
    @Test(expected = InternalServerErrorException.class)
    public void testGetLogo() {
        settingsBrandingService.getLogo();
        verify(uploadService).getLogoDirectory();
    }

    @Test
    public void testSetLogo() {

        PowerMock.mockStatic(ComponentAccessor.class);
        expect(ComponentAccessor.getComponent(LnFDefaultColorProvider.class)).andStubReturn(mock(LnFDefaultColorProvider.class));
        expect(ComponentAccessor.getComponent(LogoProvider.class)).andStubReturn(mock(LogoProvider.class));
        expect(ComponentAccessor.getComponent(EventPublisher.class)).andStubReturn(mock(EventPublisher.class));
        expect(ComponentAccessor.getComponent(UiSettingsStateManager.class)).andStubReturn(mock(UiSettingsStateManager.class));
        PowerMock.replay(ComponentAccessor.class);

        InputStream is = new ByteArrayInputStream("".getBytes());
        settingsBrandingService.setLogo(is);

        verify(lookAndFeelProperties).setLogoChoice(LogoChoice.UPLOAD);
    }

    //InternalServerErrorException -> FileNotFoundException is expected because no logofile is present in the filesystem at test time
    @Test(expected = InternalServerErrorException.class)
    public void testGetFavicon() {
        settingsBrandingService.getFavicon();
        verify(uploadService).getLogoDirectory();
    }

    @Test
    public void testSetFavicon() {

        PowerMock.mockStatic(ComponentAccessor.class);
        expect(ComponentAccessor.getComponent(LnFDefaultColorProvider.class)).andStubReturn(mock(LnFDefaultColorProvider.class));
        expect(ComponentAccessor.getComponent(LogoProvider.class)).andStubReturn(mock(LogoProvider.class));
        expect(ComponentAccessor.getComponent(EventPublisher.class)).andStubReturn(mock(EventPublisher.class));
        expect(ComponentAccessor.getComponent(UiSettingsStateManager.class)).andStubReturn(mock(UiSettingsStateManager.class));
        PowerMock.replay(ComponentAccessor.class);

        InputStream is = new ByteArrayInputStream("".getBytes());
        settingsBrandingService.setFavicon(is);

        verify(lookAndFeelProperties).setFaviconChoice(LogoChoice.UPLOAD);
    }
}
