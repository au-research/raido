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
import type { SubjectKeyword } from './SubjectKeyword';
import {
    SubjectKeywordFromJSON,
    SubjectKeywordFromJSONTyped,
    SubjectKeywordToJSON,
} from './SubjectKeyword';

/**
 * 
 * @export
 * @interface Subject
 */
export interface Subject {
    /**
     * 
     * @type {string}
     * @memberof Subject
     */
    id: string;
    /**
     * 
     * @type {string}
     * @memberof Subject
     */
    schemaUri: string;
    /**
     * 
     * @type {Array<SubjectKeyword>}
     * @memberof Subject
     */
    keyword?: Array<SubjectKeyword>;
}

/**
 * Check if a given object implements the Subject interface.
 */
export function instanceOfSubject(value: object): boolean {
    let isInstance = true;
    isInstance = isInstance && "id" in value;
    isInstance = isInstance && "schemaUri" in value;

    return isInstance;
}

export function SubjectFromJSON(json: any): Subject {
    return SubjectFromJSONTyped(json, false);
}

export function SubjectFromJSONTyped(json: any, ignoreDiscriminator: boolean): Subject {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'id': json['id'],
        'schemaUri': json['schemaUri'],
        'keyword': !exists(json, 'keyword') ? undefined : ((json['keyword'] as Array<any>).map(SubjectKeywordFromJSON)),
    };
}

export function SubjectToJSON(value?: Subject | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'id': value.id,
        'schemaUri': value.schemaUri,
        'keyword': value.keyword === undefined ? undefined : ((value.keyword as Array<any>).map(SubjectKeywordToJSON)),
    };
}

