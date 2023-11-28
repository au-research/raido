package au.org.raid.api.factory;

import au.org.raid.idl.raidv2.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Calendar;
import java.util.List;

public class DatacitePayloadFactory {
    private void createTitleNode(ObjectNode attributesNode, List<Title> titleList) {
        boolean isFirstTitle = true;
        ArrayNode titlesNode = attributesNode.putArray("titles");
        for (Title title : titleList) {
            ObjectNode titleNode = titlesNode.addObject();
            if (isFirstTitle) {
                titleNode.put("title", title.getText());
                isFirstTitle = false;
            } else {
                titleNode.put("title", String.format("%s (%s through %s)", title.getText(), title.getStartDate(), title.getEndDate()));
                if(title.getType().getId().contains("alternative")) {
                    titleNode.put("titleType", "AlternativeTitle");
                }
            }
        }
    }
    private void createDateNode(ObjectNode attributesNode, Date date) {
        ArrayNode datesNode = attributesNode.putArray("dates");
        ObjectNode dateNode = datesNode.addObject();
        dateNode.put("date", date.getStartDate());
        dateNode.put("dateType", "Available");
    }
    private void createCreatorNode(ObjectNode attributesNode, Owner owner){
        ArrayNode creatorsNode = attributesNode.putArray("creators");
        ObjectNode creatorNode = creatorsNode.addObject();
        creatorNode.put("name", owner.getId());
    }
    private void createResourceTypeNode(ObjectNode attributesNode){
        ObjectNode resourceTypeNode = attributesNode.putObject("types");
        resourceTypeNode.put("ris", "GEN");
        resourceTypeNode.put("bibtex", "misc");
        resourceTypeNode.put("citeproc", "article");
        resourceTypeNode.put("schemaOrg", "CreativeWork");
        resourceTypeNode.put("resourceType", "RAiD Project");
        resourceTypeNode.put("resourceTypeGeneral", "Other");
    }
    private void createContributorNode(ArrayNode contributorsNode, List<Contributor> contributorList){
        for (Contributor contributor : contributorList) {
            ObjectNode contributorNode = contributorsNode.addObject();
            contributorNode.put("name", "Name for " + contributor.getId());
            contributorNode.put("contributorType", "Researcher");
            // ArrayNode nameIdentifiersNode = contributorNode.putArray("nameIdentifiers");
            // ObjectNode nameIdentifierNode = nameIdentifiersNode.addObject();
            // nameIdentifierNode.put("schemeUri", "https://orcid.org");
            // nameIdentifierNode.put("nameIdentifier", contributor.getId());
            // nameIdentifierNode.put("nameIdentifierScheme", "ORCID");
        }
    }
    private void createOrganisationNode(ArrayNode contributorsNode, List<Organisation> organisationList){
        for (Organisation organisation : organisationList) {
            ObjectNode contributorNode = contributorsNode.addObject();
            contributorNode.put("name", "Name for " + organisation.getId());
            contributorNode.put("contributorType", "ResearchGroup");
        }
    }
    private void createRelatedIdentifiersNode(ArrayNode relatedIdentifiersNode, List<RelatedObject> realtedObjectList) {
        for (RelatedObject relatedObject : realtedObjectList) {
            ObjectNode relatedIdentifierNode = relatedIdentifiersNode.addObject();
            relatedIdentifierNode.put("relatedIdentifier", relatedObject.getId());
            relatedIdentifierNode.put("relatedIdentifierType", "Handle");
            relatedIdentifierNode.put("relationType", "HasPart");
            relatedIdentifierNode.put("resourceTypeGeneral", "Other");
            relatedIdentifierNode.put("relatedMetadataScheme", "");
            relatedIdentifierNode.put("schemeUri", relatedObject.getSchemaUri());
            relatedIdentifierNode.put("schemeType", relatedObject.getType().getId());
        }
    }
    private void createAlternateIdentifiersNode(ObjectNode attributesNode, List<AlternateIdentifier> alternateIdentifierList) {
        ArrayNode alternateIdentifiersNode = attributesNode.putArray("alternateIdentifiers");
        for (AlternateIdentifier alternateIdentifier : alternateIdentifierList) {
            ObjectNode alternateIdentifierNode = alternateIdentifiersNode.addObject();
            alternateIdentifierNode.put("alternateIdentifierType", alternateIdentifier.getType());
            alternateIdentifierNode.put("alternateIdentifier", alternateIdentifier.getId());
        }
    }
    private void createAlternateUrlsNode(ArrayNode relatedIdentifiersNode, List<AlternateUrl> alternateUrlList){
        for (AlternateUrl alternateUrl : alternateUrlList) {
            ObjectNode alternateUrlNode = relatedIdentifiersNode.addObject();
            alternateUrlNode.put("relatedIdentifier", alternateUrl.getUrl());
            alternateUrlNode.put("relatedIdentifierType", "URL");
            alternateUrlNode.put("relationType", "IsSourceOf");
            alternateUrlNode.put("resourceTypeGeneral", "Other");
            alternateUrlNode.put("relatedMetadataScheme", "");
        }
    }
    private void createRelatedRaidsNode(ArrayNode relatedIdentifiersNode, List<RelatedRaid> relatedRaidList){
        for (RelatedRaid relatedRaid : relatedRaidList) {
            ObjectNode relatedRaidNode = relatedIdentifiersNode.addObject();
            relatedRaidNode.put("relatedIdentifier", relatedRaid.getId());
            relatedRaidNode.put("relatedIdentifierType", "URL");
            relatedRaidNode.put("relationType", "HasPart");
            relatedRaidNode.put("resourceTypeGeneral", "Other");
            relatedRaidNode.put("relatedMetadataScheme", "");
        }
    }
    private void createAccessNode(ObjectNode attributesNode, Id identifier){
        ArrayNode accessNode = attributesNode.putArray("rightsList");
        ObjectNode accessNodeObject = accessNode.addObject();
        accessNodeObject.put("rights", identifier.getLicense());
        // TODO: Get correct URL for license
        accessNodeObject.put("rightsUri", "https://creativecommons.org/licenses/by/4.0/");
    }
    private void createDescriptionNode(ObjectNode attributesNode, List<Description> descriptionList){
        ArrayNode descriptionsNode = attributesNode.putArray("descriptions");
        for (Description description : descriptionList) {
            ObjectNode descriptionNode = descriptionsNode.addObject();
            descriptionNode.put("description", description.getText());
            descriptionNode.put("descriptionType", "Abstract");
        }
    }

    public String payloadForCreate(RaidCreateRequest request, String handle){
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode rootNode = objectMapper.createObjectNode();

        ObjectNode dataNode = rootNode.putObject("data");
        dataNode.put("type", "dois");

        ObjectNode attributesNode = dataNode.putObject("attributes");
        attributesNode.put("prefix", "10.82841");
        attributesNode.put("doi", handle);

        ArrayNode identifiersNode = dataNode.putArray("identifiers");
        ObjectNode identifierNode = identifiersNode.addObject();
        identifierNode.put("identifier", handle);
        identifierNode.put("identifierType", "DOI");

        ObjectNode typesNode = attributesNode.putObject("types");
        typesNode.put("resourceTypeGeneral", "Audiovisual");

        // CREATOR
        createCreatorNode(attributesNode, request.getIdentifier().getOwner());

        // DATES
        createDateNode(attributesNode, request.getDate());

        // TITLES
        createTitleNode(attributesNode, request.getTitle());

        // PUBLISHER
        attributesNode.put("publisher", request.getIdentifier().getId());

        // PUBLICATION YEAR
        attributesNode.put("publicationYear", Calendar.getInstance().get(Calendar.YEAR));

        // RESOURCE TYPE
        createResourceTypeNode(attributesNode);

        // CONTRIBUTORS & ORGANISATIONS
        ArrayNode contributorsNode = attributesNode.putArray("contributors");

        // CONTRIBUTORS
        createContributorNode(contributorsNode, request.getContributor());

        // ORGANISATIONS
        createOrganisationNode(contributorsNode, request.getOrganisation());

        ArrayNode relatedIdentifiersNode = attributesNode.putArray("relatedIdentifiers");

        // RELATED OBJECTS
        createRelatedIdentifiersNode(relatedIdentifiersNode, request.getRelatedObject());

        // ALTERNATE IDENTIFIERS
        createAlternateIdentifiersNode(attributesNode, request.getAlternateIdentifier());

        // ALTERNATE URLS
        createAlternateUrlsNode(relatedIdentifiersNode, request.getAlternateUrl());

        // RELATED RAIDS
        createRelatedRaidsNode(relatedIdentifiersNode, request.getRelatedRaid());

        // ACCESS
        createAccessNode(attributesNode, request.getIdentifier());

        // DESCRIPTIONS
        createDescriptionNode(attributesNode, request.getDescription());

        return rootNode.toString();
    }
    public String payloadForUpdate(RaidUpdateRequest request, String handle){
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode rootNode = objectMapper.createObjectNode();

        ObjectNode dataNode = rootNode.putObject("data");
        dataNode.put("type", "dois");

        ObjectNode attributesNode = dataNode.putObject("attributes");
        attributesNode.put("prefix", "10.82841");
        attributesNode.put("doi", handle);

        ArrayNode identifiersNode = dataNode.putArray("identifiers");
        ObjectNode identifierNode = identifiersNode.addObject();
        identifierNode.put("identifier", handle);
        identifierNode.put("identifierType", "DOI");

        ObjectNode typesNode = attributesNode.putObject("types");
        typesNode.put("resourceTypeGeneral", "Audiovisual");

        // CREATOR
        createCreatorNode(attributesNode, request.getIdentifier().getOwner());

        // DATES
        createDateNode(attributesNode, request.getDate());

        // TITLES
        createTitleNode(attributesNode, request.getTitle());

        // PUBLISHER
        attributesNode.put("publisher", request.getIdentifier().getId());

        // PUBLICATION YEAR
        attributesNode.put("publicationYear", Calendar.getInstance().get(Calendar.YEAR));

        // RESOURCE TYPE
        createResourceTypeNode(attributesNode);

        // CONTRIBUTORS & ORGANISATIONS
        ArrayNode contributorsNode = attributesNode.putArray("contributors");

        // CONTRIBUTORS
        createContributorNode(contributorsNode, request.getContributor());

        // ORGANISATIONS
        createOrganisationNode(contributorsNode, request.getOrganisation());

        ArrayNode relatedIdentifiersNode = attributesNode.putArray("relatedIdentifiers");

        // RELATED OBJECTS
        createRelatedIdentifiersNode(relatedIdentifiersNode, request.getRelatedObject());

        // ALTERNATE IDENTIFIERS
        createAlternateIdentifiersNode(attributesNode, request.getAlternateIdentifier());

        // ALTERNATE URLS
        createAlternateUrlsNode(relatedIdentifiersNode, request.getAlternateUrl());

        // RELATED RAIDS
        createRelatedRaidsNode(relatedIdentifiersNode, request.getRelatedRaid());

        // ACCESS
        createAccessNode(attributesNode, request.getIdentifier());

        // DESCRIPTIONS
        createDescriptionNode(attributesNode, request.getDescription());

        return rootNode.toString();
    }
}
