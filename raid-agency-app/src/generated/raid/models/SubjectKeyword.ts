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
import type { Language } from './Language';
import {
    LanguageFromJSON,
    LanguageFromJSONTyped,
    LanguageToJSON,
    LanguageToJSONTyped,
} from './Language';

/**
 * 
 * @export
 * @interface SubjectKeyword
 */
export interface SubjectKeyword {
    /**
     * 
     * @type {string}
     * @memberof SubjectKeyword
     */
    text: string;
    /**
     * 
     * @type {Language}
     * @memberof SubjectKeyword
     */
    language?: Language;
}

/**
 * Check if a given object implements the SubjectKeyword interface.
 */
export function instanceOfSubjectKeyword(value: object): value is SubjectKeyword {
    if (!('text' in value) || value['text'] === undefined) return false;
    return true;
}

export function SubjectKeywordFromJSON(json: any): SubjectKeyword {
    return SubjectKeywordFromJSONTyped(json, false);
}

export function SubjectKeywordFromJSONTyped(json: any, ignoreDiscriminator: boolean): SubjectKeyword {
    if (json == null) {
        return json;
    }
    return {
        
        'text': json['text'],
        'language': json['language'] == null ? undefined : LanguageFromJSON(json['language']),
    };
}

  export function SubjectKeywordToJSON(json: any): SubjectKeyword {
      return SubjectKeywordToJSONTyped(json, false);
  }

  export function SubjectKeywordToJSONTyped(value?: SubjectKeyword | null, ignoreDiscriminator: boolean = false): any {
    if (value == null) {
        return value;
    }

    return {
        
        'text': value['text'],
        'language': LanguageToJSON(value['language']),
    };
}

