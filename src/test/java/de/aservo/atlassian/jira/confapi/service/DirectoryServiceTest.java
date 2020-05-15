package de.aservo.atlassian.jira.confapi.service;

import com.atlassian.crowd.embedded.api.CrowdDirectoryService;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectoryType;
import com.atlassian.crowd.exception.DirectoryCurrentlySynchronisingException;
import com.atlassian.crowd.model.InternalEntityTemplate;
import com.atlassian.crowd.model.directory.DirectoryImpl;
import de.aservo.atlassian.confapi.exception.InternalServerErrorException;
import de.aservo.atlassian.confapi.model.DirectoryBean;
import de.aservo.atlassian.jira.confapi.model.util.DirectoryBeanUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.validation.ValidationException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.atlassian.crowd.directory.RemoteCrowdDirectory.*;
import static com.atlassian.crowd.directory.SynchronisableDirectoryProperties.*;
import static com.atlassian.crowd.model.directory.DirectoryImpl.ATTRIBUTE_KEY_USE_NESTED_GROUPS;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

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

        List<DirectoryBean> directories = userDirectoryService.getDirectories();

        assertEquals(directories.get(0), DirectoryBeanUtil.toDirectoryBean(directory));
    }

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

    private Directory createDirectory() {
        Map<String, String> attributes = new HashMap<>();
        attributes.put(CROWD_SERVER_URL, "http://localhost");
        attributes.put(APPLICATION_PASSWORD, "test");
        attributes.put(APPLICATION_NAME, "confluence-client");
        attributes.put(CACHE_SYNCHRONISE_INTERVAL, "3600");
        attributes.put(ATTRIBUTE_KEY_USE_NESTED_GROUPS, "false");
        attributes.put(INCREMENTAL_SYNC_ENABLED, "true");
        // TODO: FIX (how?) attributes.put(SYNC_GROUP_MEMBERSHIP_AFTER_SUCCESSFUL_USER_AUTH_ENABLED, WHEN_AUTHENTICATION_CREATED_THE_USER.getValue());

        final InternalEntityTemplate internalEntityTemplate = new InternalEntityTemplate();
        internalEntityTemplate.setId(1L);
        internalEntityTemplate.setName("test");
        internalEntityTemplate.setActive(true);

        final DirectoryImpl directory = new DirectoryImpl(internalEntityTemplate);
        directory.setType(DirectoryType.CROWD);
        directory.setImplementationClass("test.class");
        directory.setAttributes(attributes);

        return directory;
    }

}
