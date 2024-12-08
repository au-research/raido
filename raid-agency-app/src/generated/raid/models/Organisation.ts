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
import type { OrganisationRole } from './OrganisationRole';
import {
    OrganisationRoleFromJSON,
    OrganisationRoleFromJSONTyped,
    OrganisationRoleToJSON,
    OrganisationRoleToJSONTyped,
} from './OrganisationRole';

/**
 * 
 * @export
 * @interface Organisation
 */
export interface Organisation {
    /**
     * 
     * @type {string}
     * @memberof Organisation
     */
    id: string;
    /**
     * 
     * @type {string}
     * @memberof Organisation
     */
    schemaUri: string;
    /**
     * 
     * @type {Array<OrganisationRole>}
     * @memberof Organisation
     */
    role: Array<OrganisationRole>;
}

/**
 * Check if a given object implements the Organisation interface.
 */
export function instanceOfOrganisation(value: object): value is Organisation {
    if (!('id' in value) || value['id'] === undefined) return false;
    if (!('schemaUri' in value) || value['schemaUri'] === undefined) return false;
    if (!('role' in value) || value['role'] === undefined) return false;
    return true;
}

export function OrganisationFromJSON(json: any): Organisation {
    return OrganisationFromJSONTyped(json, false);
}

export function OrganisationFromJSONTyped(json: any, ignoreDiscriminator: boolean): Organisation {
    if (json == null) {
        return json;
    }
    return {
        
        'id': json['id'],
        'schemaUri': json['schemaUri'],
        'role': ((json['role'] as Array<any>).map(OrganisationRoleFromJSON)),
    };
}

  export function OrganisationToJSON(json: any): Organisation {
      return OrganisationToJSONTyped(json, false);
  }

  export function OrganisationToJSONTyped(value?: Organisation | null, ignoreDiscriminator: boolean = false): any {
    if (value == null) {
        return value;
    }

    return {
        
        'id': value['id'],
        'schemaUri': value['schemaUri'],
        'role': ((value['role'] as Array<any>).map(OrganisationRoleToJSON)),
    };
}

