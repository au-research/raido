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

import { exists, mapValues } from '../runtime';
/**
 * 
 * @export
 * @interface ContributorPosition
 */
export interface ContributorPosition {
    /**
     * 
     * @type {string}
     * @memberof ContributorPosition
     */
    schemaUri: string;
    /**
     * Principal or Lead Investigator
     * @type {string}
     * @memberof ContributorPosition
     */
    id: string;
    /**
     * 
     * @type {string}
     * @memberof ContributorPosition
     */
    startDate: string;
    /**
     * 
     * @type {string}
     * @memberof ContributorPosition
     */
    endDate?: string;
}

/**
 * Check if a given object implements the ContributorPosition interface.
 */
export function instanceOfContributorPosition(value: object): boolean {
    let isInstance = true;
    isInstance = isInstance && "schemaUri" in value;
    isInstance = isInstance && "id" in value;
    isInstance = isInstance && "startDate" in value;

    return isInstance;
}

export function ContributorPositionFromJSON(json: any): ContributorPosition {
    return ContributorPositionFromJSONTyped(json, false);
}

export function ContributorPositionFromJSONTyped(json: any, ignoreDiscriminator: boolean): ContributorPosition {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'schemaUri': json['schemaUri'],
        'id': json['id'],
        'startDate': json['startDate'],
        'endDate': !exists(json, 'endDate') ? undefined : json['endDate'],
    };
}

export function ContributorPositionToJSON(value?: ContributorPosition | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'schemaUri': value.schemaUri,
        'id': value.id,
        'startDate': value.startDate,
        'endDate': value.endDate,
    };
}

