package it.cnr.cool.rest;

import it.cnr.cool.cmis.service.CMISService;
import it.cnr.cool.cmis.service.CMISSessionManager;
import it.cnr.cool.security.SecurityChecked;
import it.cnr.cool.service.modelDesigner.ModelDesignerService;
import it.cnr.cool.service.util.AlfrescoDocument;
import it.cnr.cool.service.util.AlfrescoModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.*;

@Path("models")
@Component
@Produces(MediaType.APPLICATION_JSON)
@SecurityChecked
public class ModelDesigner {

    @Autowired
    private ModelDesignerService modelDesignerService;
    @Autowired
    private CMISSessionManager cmisSessionManager;
    @Autowired
    private CMISService cmisService;


    @GET
    public Map<String, Object> getModels(@Context HttpServletRequest req) {
        Map<String, Object> model = new HashMap<String, Object>();

        List<AlfrescoModel> models = modelDesignerService
                .getModels(cmisSessionManager.getCurrentCMISSession(req
                        .getSession(false)));
        model.put("models", models);
        return model;
    }


    @POST
    public Map<String, Object> createModel(@Context HttpServletRequest req,
                                           @FormParam("prefixModel") String prefixModel,
                                           @FormParam("nameFile") String nameXml) {
        Map<String, Object> model = modelDesignerService.createModel(
                cmisSessionManager.createAdminSession(), prefixModel, nameXml);
        return model;
    }


    @POST
    @Path("activate/{id}/{version}")
    public Map<String, Object> activateModel(@Context HttpServletRequest req,
                                             @PathParam("id") String id,
                                             @PathParam("version") String version,
                                             @QueryParam("activate") boolean activate) {
        Map<String, Object> model = modelDesignerService.activateModel(
                cmisSessionManager.createAdminSession(), "workspace://spaceStore/" + id + ";" + version, activate,
                cmisService.getCurrentBindingSession(req));
        return model;
    }


    @PUT
    @Path("{id}/{version}")
    public Map<String, Object> updateMoldel(@Context HttpServletRequest req,
                                            @FormParam("xml") String xml,
                                            @FormParam("nameFile") String nameXml,
                                            @FormParam("nameTemplate") String nameTemplate,
                                            @FormParam("generateTemplate") boolean generateTemplate,
                                            @PathParam("id") String id,
                                            @PathParam("version") String version) {
        Map<String, Object> model = modelDesignerService.updateModel(
                cmisSessionManager.createAdminSession(), xml, nameXml,
                "workspace://spaceStore/" + id + ";" + version);
        return model;
    }


    @DELETE
    @Path("{id}/{version}")
    public Map<String, Object> deleteModel(@Context HttpServletRequest req, @PathParam("id") String id, @PathParam("version") String version) {
        Map<String, Object> model = modelDesignerService.deleteModel(
                cmisSessionManager.createAdminSession(), "workspace://spaceStore/" + id + ";" + version,
                cmisService.getCurrentBindingSession(req));
        return model;
    }


    @DELETE
    @Path("property/{id}/{version}")
    public Map<String, Object> deleteProperty(@Context HttpServletRequest req, @PathParam("id") String id,
                                              @PathParam("version") String version,
                                              @QueryParam("property") String property,
                                              @QueryParam("typeName") String typeName) {
        Map<String, Object> model = modelDesignerService.deleteProperty(
                cmisSessionManager.createAdminSession(), "workspace://spaceStore/" + id + ";" + version, typeName, property);
        return model;
    }


    // restituisce i documenti associati al model associato al nodeRef
    @GET
    @Path("docsByPath/{id}/{version}")
    public Map<String, Object> getDocsByPath(@Context HttpServletRequest req, @QueryParam("aspectsName") String aspectsName, @PathParam("id") String id, @PathParam("version") String version) {
        Map<String, Object> model = new HashMap<String, Object>();

        List<AlfrescoDocument> alfrescoDocs = modelDesignerService
                .getDocsByPath(cmisSessionManager.getCurrentCMISSession(req
                        .getSession(false)), "workspace://spaceStore/" + id + ";" + version);
        model.put("docs", alfrescoDocs);
        List<AlfrescoDocument> alfrescoTemplates = new ArrayList<>();
        if (!aspectsName.equals("null")) {
            List<String> aspectsList = Arrays.asList(aspectsName);
            alfrescoTemplates = modelDesignerService.getTemplatesByAspectsName(cmisSessionManager.getCurrentCMISSession(req.getSession(false)), aspectsList);
        }
        model.put("templates", alfrescoTemplates);
        return model;
    }


    // restituisce i documenti del tipo specificato nel TypeName
    @GET
    @Path("docsByTypeName")
    public Map<String, Object> getDocsByTypeName(
            @Context HttpServletRequest req,
            @QueryParam("typeName") String tipeName) {
        Map<String, Object> model = new HashMap<String, Object>();

        List<AlfrescoDocument> alfrescoDocs = modelDesignerService
                .getDocsByTypeName(cmisSessionManager.getCurrentCMISSession(req
                        .getSession(false)), tipeName);
        model.put("docs", alfrescoDocs);
        return model;
    }

    // genera i node template con gli aspetti che gli vengono passati
    @POST
    @Path("generateTemplate")
    public Map<String, Object> generateTemplate(
            @Context HttpServletRequest req,
            @QueryParam("nameTemplate") String nameTemplate, @QueryParam("selectedAspects") String selectedAspects) {
        Map<String, Object> model = modelDesignerService.generateTemplate(cmisSessionManager.getCurrentCMISSession(req
                .getSession(false)), nameTemplate, Arrays.asList(selectedAspects.split(",")));
        return model;
    }
}