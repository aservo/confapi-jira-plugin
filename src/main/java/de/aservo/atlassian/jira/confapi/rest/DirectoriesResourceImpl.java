package de.aservo.atlassian.jira.confapi.rest;

import de.aservo.atlassian.confapi.constants.ConfAPI;
import de.aservo.atlassian.confapi.rest.AbstractDirectoriesResourceImpl;
import de.aservo.atlassian.confapi.service.api.DirectoryService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path(ConfAPI.DIRECTORIES)
@Produces(MediaType.APPLICATION_JSON)
@Component
public class DirectoriesResourceImpl extends AbstractDirectoriesResourceImpl {

    @Inject
    public DirectoriesResourceImpl(DirectoryService directoryService) {
        super(directoryService);
    }

    @Override
    public Response getDirectories() {
        throw new RuntimeException("REST Exception Mapper Test");
    }

}
