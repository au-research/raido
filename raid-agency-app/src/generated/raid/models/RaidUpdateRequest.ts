/* tslint:disable */
/* eslint-disable */
/**
 * RAID v2 API
 * This file is where all the endpoint paths are defined, it\'s the \"top level\' of the OpenAPI definition that links all the different files together. The `3.0` in the filename refers to this file being based on OpenAPI 3.0  as opposed to OpenAPI 3.1, which the tooling doesn\'t support yet. The `2.0.0` in the version field refers to the fact that there\'s already  a `1.0.0` used for the legacy RAiD application. Note that swagger ui doesn\'t currently work with our spec,  see https://github.com/swagger-api/swagger-ui/issues/7724 But the spec works fine with openapi-generator tooling. 
 *
 * The version of the OpenAPI document: 2.0.0
 * Contact: contact@raid.org
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

import { mapValues } from '../runtime';
import type { RelatedObject } from './RelatedObject';
import {
    RelatedObjectFromJSON,
    RelatedObjectFromJSONTyped,
    RelatedObjectToJSON,
    RelatedObjectToJSONTyped,
} from './RelatedObject';
import type { Description } from './Description';
import {
    DescriptionFromJSON,
    DescriptionFromJSONTyped,
    DescriptionToJSON,
    DescriptionToJSONTyped,
} from './Description';
import type { Organisation } from './Organisation';
import {
    OrganisationFromJSON,
    OrganisationFromJSONTyped,
    OrganisationToJSON,
    OrganisationToJSONTyped,
} from './Organisation';
import type { ModelDate } from './ModelDate';
import {
    ModelDateFromJSON,
    ModelDateFromJSONTyped,
    ModelDateToJSON,
    ModelDateToJSONTyped,
} from './ModelDate';
import type { Access } from './Access';
import {
    AccessFromJSON,
    AccessFromJSONTyped,
    AccessToJSON,
    AccessToJSONTyped,
} from './Access';
import type { Contributor } from './Contributor';
import {
    ContributorFromJSON,
    ContributorFromJSONTyped,
    ContributorToJSON,
    ContributorToJSONTyped,
} from './Contributor';
import type { Title } from './Title';
import {
    TitleFromJSON,
    TitleFromJSONTyped,
    TitleToJSON,
    TitleToJSONTyped,
} from './Title';
import type { RelatedRaid } from './RelatedRaid';
import {
    RelatedRaidFromJSON,
    RelatedRaidFromJSONTyped,
    RelatedRaidToJSON,
    RelatedRaidToJSONTyped,
} from './RelatedRaid';
import type { AlternateIdentifier } from './AlternateIdentifier';
import {
    AlternateIdentifierFromJSON,
    AlternateIdentifierFromJSONTyped,
    AlternateIdentifierToJSON,
    AlternateIdentifierToJSONTyped,
} from './AlternateIdentifier';
import type { Subject } from './Subject';
import {
    SubjectFromJSON,
    SubjectFromJSONTyped,
    SubjectToJSON,
    SubjectToJSONTyped,
} from './Subject';
import type { Id } from './Id';
import {
    IdFromJSON,
    IdFromJSONTyped,
    IdToJSON,
    IdToJSONTyped,
} from './Id';
import type { AlternateUrl } from './AlternateUrl';
import {
    AlternateUrlFromJSON,
    AlternateUrlFromJSONTyped,
    AlternateUrlToJSON,
    AlternateUrlToJSONTyped,
} from './AlternateUrl';
import type { TraditionalKnowledgeLabel } from './TraditionalKnowledgeLabel';
import {
    TraditionalKnowledgeLabelFromJSON,
    TraditionalKnowledgeLabelFromJSONTyped,
    TraditionalKnowledgeLabelToJSON,
    TraditionalKnowledgeLabelToJSONTyped,
} from './TraditionalKnowledgeLabel';
import type { SpatialCoverage } from './SpatialCoverage';
import {
    SpatialCoverageFromJSON,
    SpatialCoverageFromJSONTyped,
    SpatialCoverageToJSON,
    SpatialCoverageToJSONTyped,
} from './SpatialCoverage';

/**
 * 
 * @export
 * @interface RaidUpdateRequest
 */
export interface RaidUpdateRequest {
    /**
     * 
     * @type {Id}
     * @memberof RaidUpdateRequest
     */
    identifier: Id;
    /**
     * 
     * @type {Array<Title>}
     * @memberof RaidUpdateRequest
     */
    title?: Array<Title>;
    /**
     * 
     * @type {ModelDate}
     * @memberof RaidUpdateRequest
     */
    date?: ModelDate;
    /**
     * 
     * @type {Array<Description>}
     * @memberof RaidUpdateRequest
     */
    description?: Array<Description>;
    /**
     * 
     * @type {Access}
     * @memberof RaidUpdateRequest
     */
    access: Access;
    /**
     * 
     * @type {Array<AlternateUrl>}
     * @memberof RaidUpdateRequest
     */
    alternateUrl?: Array<AlternateUrl>;
    /**
     * 
     * @type {Array<Contributor>}
     * @memberof RaidUpdateRequest
     */
    contributor?: Array<Contributor>;
    /**
     * 
     * @type {Array<Organisation>}
     * @memberof RaidUpdateRequest
     */
    organisation?: Array<Organisation>;
    /**
     * 
     * @type {Array<Subject>}
     * @memberof RaidUpdateRequest
     */
    subject?: Array<Subject>;
    /**
     * 
     * @type {Array<RelatedRaid>}
     * @memberof RaidUpdateRequest
     */
    relatedRaid?: Array<RelatedRaid>;
    /**
     * 
     * @type {Array<RelatedObject>}
     * @memberof RaidUpdateRequest
     */
    relatedObject?: Array<RelatedObject>;
    /**
     * 
     * @type {Array<AlternateIdentifier>}
     * @memberof RaidUpdateRequest
     */
    alternateIdentifier?: Array<AlternateIdentifier>;
    /**
     * 
     * @type {Array<SpatialCoverage>}
     * @memberof RaidUpdateRequest
     */
    spatialCoverage?: Array<SpatialCoverage>;
    /**
     * 
     * @type {Array<TraditionalKnowledgeLabel>}
     * @memberof RaidUpdateRequest
     */
    traditionalKnowledgeLabel?: Array<TraditionalKnowledgeLabel>;
}

/**
 * Check if a given object implements the RaidUpdateRequest interface.
 */
export function instanceOfRaidUpdateRequest(value: object): value is RaidUpdateRequest {
    if (!('identifier' in value) || value['identifier'] === undefined) return false;
    if (!('access' in value) || value['access'] === undefined) return false;
    return true;
}

export function RaidUpdateRequestFromJSON(json: any): RaidUpdateRequest {
    return RaidUpdateRequestFromJSONTyped(json, false);
}

export function RaidUpdateRequestFromJSONTyped(json: any, ignoreDiscriminator: boolean): RaidUpdateRequest {
    if (json == null) {
        return json;
    }
    return {
        
        'identifier': IdFromJSON(json['identifier']),
        'title': json['title'] == null ? undefined : ((json['title'] as Array<any>).map(TitleFromJSON)),
        'date': json['date'] == null ? undefined : ModelDateFromJSON(json['date']),
        'description': json['description'] == null ? undefined : ((json['description'] as Array<any>).map(DescriptionFromJSON)),
        'access': AccessFromJSON(json['access']),
        'alternateUrl': json['alternateUrl'] == null ? undefined : ((json['alternateUrl'] as Array<any>).map(AlternateUrlFromJSON)),
        'contributor': json['contributor'] == null ? undefined : ((json['contributor'] as Array<any>).map(ContributorFromJSON)),
        'organisation': json['organisation'] == null ? undefined : ((json['organisation'] as Array<any>).map(OrganisationFromJSON)),
        'subject': json['subject'] == null ? undefined : ((json['subject'] as Array<any>).map(SubjectFromJSON)),
        'relatedRaid': json['relatedRaid'] == null ? undefined : ((json['relatedRaid'] as Array<any>).map(RelatedRaidFromJSON)),
        'relatedObject': json['relatedObject'] == null ? undefined : ((json['relatedObject'] as Array<any>).map(RelatedObjectFromJSON)),
        'alternateIdentifier': json['alternateIdentifier'] == null ? undefined : ((json['alternateIdentifier'] as Array<any>).map(AlternateIdentifierFromJSON)),
        'spatialCoverage': json['spatialCoverage'] == null ? undefined : ((json['spatialCoverage'] as Array<any>).map(SpatialCoverageFromJSON)),
        'traditionalKnowledgeLabel': json['traditionalKnowledgeLabel'] == null ? undefined : ((json['traditionalKnowledgeLabel'] as Array<any>).map(TraditionalKnowledgeLabelFromJSON)),
    };
}

  export function RaidUpdateRequestToJSON(json: any): RaidUpdateRequest {
      return RaidUpdateRequestToJSONTyped(json, false);
  }

  export function RaidUpdateRequestToJSONTyped(value?: RaidUpdateRequest | null, ignoreDiscriminator: boolean = false): any {
    if (value == null) {
        return value;
    }

    return {
        
        'identifier': IdToJSON(value['identifier']),
        'title': value['title'] == null ? undefined : ((value['title'] as Array<any>).map(TitleToJSON)),
        'date': ModelDateToJSON(value['date']),
        'description': value['description'] == null ? undefined : ((value['description'] as Array<any>).map(DescriptionToJSON)),
        'access': AccessToJSON(value['access']),
        'alternateUrl': value['alternateUrl'] == null ? undefined : ((value['alternateUrl'] as Array<any>).map(AlternateUrlToJSON)),
        'contributor': value['contributor'] == null ? undefined : ((value['contributor'] as Array<any>).map(ContributorToJSON)),
        'organisation': value['organisation'] == null ? undefined : ((value['organisation'] as Array<any>).map(OrganisationToJSON)),
        'subject': value['subject'] == null ? undefined : ((value['subject'] as Array<any>).map(SubjectToJSON)),
        'relatedRaid': value['relatedRaid'] == null ? undefined : ((value['relatedRaid'] as Array<any>).map(RelatedRaidToJSON)),
        'relatedObject': value['relatedObject'] == null ? undefined : ((value['relatedObject'] as Array<any>).map(RelatedObjectToJSON)),
        'alternateIdentifier': value['alternateIdentifier'] == null ? undefined : ((value['alternateIdentifier'] as Array<any>).map(AlternateIdentifierToJSON)),
        'spatialCoverage': value['spatialCoverage'] == null ? undefined : ((value['spatialCoverage'] as Array<any>).map(SpatialCoverageToJSON)),
        'traditionalKnowledgeLabel': value['traditionalKnowledgeLabel'] == null ? undefined : ((value['traditionalKnowledgeLabel'] as Array<any>).map(TraditionalKnowledgeLabelToJSON)),
    };
}

