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
import type { RelatedObjectType } from './RelatedObjectType';
import {
    RelatedObjectTypeFromJSON,
    RelatedObjectTypeFromJSONTyped,
    RelatedObjectTypeToJSON,
    RelatedObjectTypeToJSONTyped,
} from './RelatedObjectType';
import type { RelatedObjectCategory } from './RelatedObjectCategory';
import {
    RelatedObjectCategoryFromJSON,
    RelatedObjectCategoryFromJSONTyped,
    RelatedObjectCategoryToJSON,
    RelatedObjectCategoryToJSONTyped,
} from './RelatedObjectCategory';

/**
 * 
 * @export
 * @interface RelatedObject
 */
export interface RelatedObject {
    /**
     * 
     * @type {string}
     * @memberof RelatedObject
     */
    id?: string;
    /**
     * 
     * @type {string}
     * @memberof RelatedObject
     */
    schemaUri?: string;
    /**
     * 
     * @type {RelatedObjectType}
     * @memberof RelatedObject
     */
    type?: RelatedObjectType;
    /**
     * 
     * @type {Array<RelatedObjectCategory>}
     * @memberof RelatedObject
     */
    category?: Array<RelatedObjectCategory>;
}

/**
 * Check if a given object implements the RelatedObject interface.
 */
export function instanceOfRelatedObject(value: object): value is RelatedObject {
    return true;
}

export function RelatedObjectFromJSON(json: any): RelatedObject {
    return RelatedObjectFromJSONTyped(json, false);
}

export function RelatedObjectFromJSONTyped(json: any, ignoreDiscriminator: boolean): RelatedObject {
    if (json == null) {
        return json;
    }
    return {
        
        'id': json['id'] == null ? undefined : json['id'],
        'schemaUri': json['schemaUri'] == null ? undefined : json['schemaUri'],
        'type': json['type'] == null ? undefined : RelatedObjectTypeFromJSON(json['type']),
        'category': json['category'] == null ? undefined : ((json['category'] as Array<any>).map(RelatedObjectCategoryFromJSON)),
    };
}

  export function RelatedObjectToJSON(json: any): RelatedObject {
      return RelatedObjectToJSONTyped(json, false);
  }

  export function RelatedObjectToJSONTyped(value?: RelatedObject | null, ignoreDiscriminator: boolean = false): any {
    if (value == null) {
        return value;
    }

    return {
        
        'id': value['id'],
        'schemaUri': value['schemaUri'],
        'type': RelatedObjectTypeToJSON(value['type']),
        'category': value['category'] == null ? undefined : ((value['category'] as Array<any>).map(RelatedObjectCategoryToJSON)),
    };
}

