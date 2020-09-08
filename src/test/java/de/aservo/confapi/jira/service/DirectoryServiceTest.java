package de.aservo.confapi.jira.service;

import com.atlassian.crowd.embedded.api.CrowdDirectoryService;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectoryType;
import com.atlassian.crowd.model.directory.ImmutableDirectory;
import de.aservo.confapi.commons.model.DirectoriesBean;
import de.aservo.confapi.jira.model.util.DirectoryBeanUtil;
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
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class DirectoryServiceTest {

    @Mock
    private CrowdDirectoryService crowdDirectoryService;

    @InjectMocks
    private DirectoryServiceImpl userDirectoryService;

    @Test
    public void testGetDirectories() {
        Directory directory = createDirectory();
        doReturn(Collections.singletonList(directory)).when(crowdDirectoryService).findAllDirectories();

        DirectoriesBean directories = userDirectoryService.getDirectories();

        assertEquals(directories.getDirectories().iterator().next(), DirectoryBeanUtil.toDirectoryBean(directory));
    }

    /*
    @Test
    public void testSetDirectoryWithoutExistingDirectory() {
        Directory directory = createDirectory();
        doReturn(directory).when(crowdDirectoryService).addDirectory(any(Directory.class));

        DirectoryBean directoryBean = DirectoryBeanUtil.toDirectoryBean(directory);
        directoryBean.setAppPassword("test");
        DirectoryBean directoryAdded = userDirectoryService.setDirectory(directoryBean, false);

        assertEquals(directoryAdded.getName(), directoryBean.getName());
    }

    @Test
    public void testSetDirectoryWithExistingDirectory() {
        Directory directory = createDirectory();
        doReturn(Collections.singletonList(directory)).when(crowdDirectoryService).findAllDirectories();
        doReturn(directory).when(crowdDirectoryService).addDirectory(any(Directory.class));

        DirectoryBean directoryBean = DirectoryBeanUtil.toDirectoryBean(directory);
        directoryBean.setAppPassword("test");
        DirectoryBean directoryAdded = userDirectoryService.setDirectory(directoryBean, false);

        assertEquals(directoryAdded.getName(), directoryBean.getName());
    }

    @Test
    public void testSetDirectoryWithConnectionTest() {
        Directory directory = createDirectory();
        doReturn(directory).when(crowdDirectoryService).addDirectory(any(Directory.class));

        DirectoryBean directoryBean = DirectoryBeanUtil.toDirectoryBean(directory);
        directoryBean.setAppPassword("test");
        DirectoryBean directoryAdded = userDirectoryService.setDirectory(directoryBean, true);

        assertEquals(directoryAdded.getName(), directoryBean.getName());
    }

    @Test(expected = ValidationException.class)
    public void testSetDirectoryInvalidBean() {
        Directory directory = createDirectory();
        DirectoryBean directoryBean = DirectoryBeanUtil.toDirectoryBean(directory);
        directoryBean.setAppPassword("test");
        directoryBean.setClientName(null);

        userDirectoryService.setDirectory(directoryBean, false);
    }

    @Test(expected = InternalServerErrorException.class)
    public void testSetDirectoryDirectoryCurrentlySynchronisingException() throws DirectoryCurrentlySynchronisingException {
        Directory directory = createDirectory();
        doReturn(Collections.singletonList(directory)).when(crowdDirectoryService).findAllDirectories();
        doThrow(new DirectoryCurrentlySynchronisingException(1L)).when(crowdDirectoryService).removeDirectory(1L);

        DirectoryBean directoryBean = DirectoryBeanUtil.toDirectoryBean(directory);
        directoryBean.setAppPassword("test");
        DirectoryBean directoryAdded = userDirectoryService.setDirectory(directoryBean, false);
    }
    */

    private Directory createDirectory() {
        Map<String, String> attributes = new HashMap<>();
        attributes.put(CROWD_SERVER_URL, "http://localhost");
        attributes.put(APPLICATION_PASSWORD, "test");
        attributes.put(APPLICATION_NAME, "confluence-client");
        attributes.put(CACHE_SYNCHRONISE_INTERVAL, "3600");
        attributes.put(ATTRIBUTE_KEY_USE_NESTED_GROUPS, "false");
        attributes.put(INCREMENTAL_SYNC_ENABLED, "true");
        attributes.put(SYNC_GROUP_MEMBERSHIP_AFTER_SUCCESSFUL_USER_AUTH_ENABLED, WHEN_AUTHENTICATION_CREATED_THE_USER.getValue());
        return ImmutableDirectory.builder("test", DirectoryType.CROWD, "test.class").setId(1L).setAttributes(attributes).build();
    }

}
