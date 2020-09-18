package de.aservo.confapi.jira.service;

import com.atlassian.crowd.embedded.api.CrowdDirectoryService;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectoryType;
import com.atlassian.crowd.exception.DirectoryCurrentlySynchronisingException;
import com.atlassian.crowd.model.directory.ImmutableDirectory;
import de.aservo.confapi.commons.exception.InternalServerErrorException;
import de.aservo.confapi.commons.model.AbstractDirectoryBean;
import de.aservo.confapi.commons.model.DirectoriesBean;
import de.aservo.confapi.commons.model.DirectoryCrowdBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.atlassian.crowd.directory.RemoteCrowdDirectory.*;
import static com.atlassian.crowd.directory.SynchronisableDirectoryProperties.*;
import static com.atlassian.crowd.directory.SynchronisableDirectoryProperties.SyncGroupMembershipsAfterAuth.WHEN_AUTHENTICATION_CREATED_THE_USER;
import static com.atlassian.crowd.model.directory.DirectoryImpl.ATTRIBUTE_KEY_USE_NESTED_GROUPS;
import static de.aservo.confapi.jira.model.util.DirectoryBeanUtil.toDirectoryBean;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@RunWith(MockitoJUnitRunner.class)
public class DirectoriesServiceTest {

    @Mock
    private CrowdDirectoryService crowdDirectoryService;

    @InjectMocks
    private DirectoriesServiceImpl directoryService;

    @Test
    public void testGetDirectories() {
        Directory directory = createDirectory();
        doReturn(Collections.singletonList(directory)).when(crowdDirectoryService).findAllDirectories();

        DirectoriesBean directories = directoryService.getDirectories();

        assertEquals(directories.getDirectories().iterator().next(), toDirectoryBean(directory));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetDirectoriesUriException() {
        Directory directory = createDirectory("öäöää://uhveuehvde");
        doReturn(Collections.singletonList(directory)).when(crowdDirectoryService).findAllDirectories();
        directoryService.getDirectories();
    }

    @Test
    public void testSetDirectoriesWithoutExistingDirectory() {
        Directory directory = createDirectory();
        doReturn(directory).when(crowdDirectoryService).addDirectory(any(Directory.class));
        doReturn(Collections.singletonList(directory)).when(crowdDirectoryService).findAllDirectories();

        DirectoryCrowdBean directoryBean = (DirectoryCrowdBean) toDirectoryBean(directory);
        directoryBean.getServer().setAppPassword("test");
        DirectoriesBean directoryAdded = directoryService.setDirectories(new DirectoriesBean(Collections.singletonList(directoryBean)), false);

        assertEquals(directoryAdded.getDirectories().iterator().next().getName(), directoryBean.getName());
    }

    @Test
    public void testSetDirectoryWithExistingDirectory() {
        Directory directory = createDirectory();
        doReturn(Collections.singletonList(directory)).when(crowdDirectoryService).findAllDirectories();
        doReturn(directory).when(crowdDirectoryService).addDirectory(any(Directory.class));

        DirectoryCrowdBean directoryBean = (DirectoryCrowdBean) toDirectoryBean(directory);
        directoryBean.getServer().setAppPassword("test");
        DirectoriesBean directoryAdded = directoryService.setDirectories(new DirectoriesBean(Collections.singletonList(directoryBean)), false);

        assertEquals(directoryAdded.getDirectories().iterator().next().getName(), directoryBean.getName());
    }

    @Test
    public void testSetDirectoryWithConnectionTest() {
        Directory directory = createDirectory();
        doReturn(directory).when(crowdDirectoryService).addDirectory(any(Directory.class));
        doReturn(Collections.singletonList(directory)).when(crowdDirectoryService).findAllDirectories();

        DirectoryCrowdBean directoryBean = (DirectoryCrowdBean) toDirectoryBean(directory);
        directoryBean.getServer().setAppPassword("test");
        DirectoriesBean directoryAdded = directoryService.setDirectories(new DirectoriesBean(Collections.singletonList(directoryBean)), true);

        assertEquals(directoryAdded.getDirectories().iterator().next().getName(), directoryBean.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddDirectoryUriException() {
        Directory responseDirectory = createDirectory("öäöää://uhveuehvde");
        doReturn(responseDirectory).when(crowdDirectoryService).addDirectory(any());

        Directory directory = createDirectory();
        DirectoryCrowdBean directoryBean = (DirectoryCrowdBean) toDirectoryBean(directory);

        directoryService.addDirectory(directoryBean, false);
    }

    @Test
    public void testAddDirectory() {
        Directory directory = createDirectory();
        doReturn(directory).when(crowdDirectoryService).addDirectory(any(Directory.class));

        DirectoryCrowdBean directoryBean = (DirectoryCrowdBean) toDirectoryBean(directory);
        directoryBean.getServer().setAppPassword("test");

        AbstractDirectoryBean directoryAdded = directoryService.addDirectory(directoryBean, false);
        assertEquals(directoryAdded.getName(), directoryBean.getName());
    }


    @Test(expected = InternalServerErrorException.class)
    public void testSetDirectoryDirectoryCurrentlySynchronisingException() throws DirectoryCurrentlySynchronisingException {
        Directory directory = createDirectory();
        doReturn(Collections.singletonList(directory)).when(crowdDirectoryService).findAllDirectories();
        doThrow(new DirectoryCurrentlySynchronisingException(1L)).when(crowdDirectoryService).removeDirectory(1L);

        DirectoryCrowdBean directoryBean = (DirectoryCrowdBean) toDirectoryBean(directory);
        directoryBean.getServer().setAppPassword("test");
        directoryService.setDirectories(new DirectoriesBean(Collections.singletonList(directoryBean)), false);
    }

    private Directory createDirectory() {
        return createDirectory("http://localhost");
    }

    private Directory createDirectory(String url) {
        Map<String, String> attributes = new HashMap<>();
        attributes.put(CROWD_SERVER_URL, url);
        attributes.put(APPLICATION_PASSWORD, "test");
        attributes.put(APPLICATION_NAME, "confluence-client");
        attributes.put(CACHE_SYNCHRONISE_INTERVAL, "3600");
        attributes.put(ATTRIBUTE_KEY_USE_NESTED_GROUPS, "false");
        attributes.put(INCREMENTAL_SYNC_ENABLED, "true");
        attributes.put(SYNC_GROUP_MEMBERSHIP_AFTER_SUCCESSFUL_USER_AUTH_ENABLED, WHEN_AUTHENTICATION_CREATED_THE_USER.getValue());
        return ImmutableDirectory.builder("test", DirectoryType.CROWD, "test.class").setId(1L).setAttributes(attributes).build();
    }

}
