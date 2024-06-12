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
import type { Owner } from './Owner';
import {
    OwnerFromJSON,
    OwnerFromJSONTyped,
    OwnerToJSON,
} from './Owner';
import type { RegistrationAgency } from './RegistrationAgency';
import {
    RegistrationAgencyFromJSON,
    RegistrationAgencyFromJSONTyped,
    RegistrationAgencyToJSON,
} from './RegistrationAgency';

/**
 * 
 * @export
 * @interface Id
 */
export interface Id {
    /**
     * The identifier of the raid, e.g. https://raid.org.au/102.100.100/zzz
     * @type {string}
     * @memberof Id
     */
    id: string;
    /**
     * The URI of the Identifier scheme. For example, https://raid.org
     * 
     * @type {string}
     * @memberof Id
     */
    schemaUri: string;
    /**
     * 
     * @type {RegistrationAgency}
     * @memberof Id
     */
    registrationAgency: RegistrationAgency;
    /**
     * 
     * @type {Owner}
     * @memberof Id
     */
    owner: Owner;
    /**
     * The URL for the raid via the minting raid agency system, e.g  https://raid.org.au/10378.1/1695863
     * 
     * @type {string}
     * @memberof Id
     */
    raidAgencyUrl?: string;
    /**
     * The license under which the RAiD Metadata Record associated with this Identifier has been issued.
     * @type {string}
     * @memberof Id
     */
    license: string;
    /**
     * The version of the resource. Read-only. Increments automatically on update.
     * @type {number}
     * @memberof Id
     */
    version: number;
}

/**
 * Check if a given object implements the Id interface.
 */
export function instanceOfId(value: object): boolean {
    let isInstance = true;
    isInstance = isInstance && "id" in value;
    isInstance = isInstance && "schemaUri" in value;
    isInstance = isInstance && "registrationAgency" in value;
    isInstance = isInstance && "owner" in value;
    isInstance = isInstance && "license" in value;
    isInstance = isInstance && "version" in value;

    return isInstance;
}

export function IdFromJSON(json: any): Id {
    return IdFromJSONTyped(json, false);
}

export function IdFromJSONTyped(json: any, ignoreDiscriminator: boolean): Id {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'id': json['id'],
        'schemaUri': json['schemaUri'],
        'registrationAgency': RegistrationAgencyFromJSON(json['registrationAgency']),
        'owner': OwnerFromJSON(json['owner']),
        'raidAgencyUrl': !exists(json, 'raidAgencyUrl') ? undefined : json['raidAgencyUrl'],
        'license': json['license'],
        'version': json['version'],
    };
}

export function IdToJSON(value?: Id | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'id': value.id,
        'schemaUri': value.schemaUri,
        'registrationAgency': RegistrationAgencyToJSON(value.registrationAgency),
        'owner': OwnerToJSON(value.owner),
        'raidAgencyUrl': value.raidAgencyUrl,
        'license': value.license,
        'version': value.version,
    };
}
