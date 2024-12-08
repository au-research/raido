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
/**
 * Metadata schema block containing the start and end date of the RAiD.
 * @export
 * @interface ModelDate
 */
export interface ModelDate {
    /**
     * 
     * @type {string}
     * @memberof ModelDate
     */
    startDate: string;
    /**
     * 
     * @type {string}
     * @memberof ModelDate
     */
    endDate?: string;
}

/**
 * Check if a given object implements the ModelDate interface.
 */
export function instanceOfModelDate(value: object): value is ModelDate {
    if (!('startDate' in value) || value['startDate'] === undefined) return false;
    return true;
}

export function ModelDateFromJSON(json: any): ModelDate {
    return ModelDateFromJSONTyped(json, false);
}

export function ModelDateFromJSONTyped(json: any, ignoreDiscriminator: boolean): ModelDate {
    if (json == null) {
        return json;
    }
    return {
        
        'startDate': json['startDate'],
        'endDate': json['endDate'] == null ? undefined : json['endDate'],
    };
}

  export function ModelDateToJSON(json: any): ModelDate {
      return ModelDateToJSONTyped(json, false);
  }

  export function ModelDateToJSONTyped(value?: ModelDate | null, ignoreDiscriminator: boolean = false): any {
    if (value == null) {
        return value;
    }

    return {
        
        'startDate': value['startDate'],
        'endDate': value['endDate'],
    };
}

