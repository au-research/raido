import { accessGenerator } from "@/entities/access/access-generator";
import { dateCleaner } from "@/entities/date/data-components/date-cleaner";
import { dateGenerator } from "@/entities/date/data-components/date-generator";
import { titleGenerator } from "@/entities/title/data-components/title-generator";
import {
  Access,
  AlternateIdentifier,
  AlternateUrl,
  Contributor,
  Description,
  Id,
  ModelDate,
  Organisation,
  RaidCreateRequest,
  RaidDto,
  RelatedObject,
  RelatedRaid,
  SpatialCoverage,
  Subject,
  Title,
  TraditionalKnowledgeLabel,
} from "@/generated/raid";

export const raidRequest = (data: RaidDto): RaidDto => {
  return {
    identifier: data?.identifier || ({} as Id),
    description: data?.description || ([] as Description[]),
    title: data?.title || ([] as Title[]),
    access: data?.access || ({} as Access),
    alternateUrl: data?.alternateUrl || ([] as AlternateUrl[]),
    relatedRaid: data?.relatedRaid || ([] as RelatedRaid[]),
    date: dateCleaner(data?.date) || ({} as ModelDate),
    contributor: data?.contributor || ([] as Contributor[]),
    alternateIdentifier:
      data?.alternateIdentifier || ([] as AlternateIdentifier[]),
    organisation: data?.organisation || ([] as Organisation[]),
    relatedObject: data?.relatedObject || ([] as RelatedObject[]),
    spatialCoverage: data?.spatialCoverage || ([] as SpatialCoverage[]),
    subject: data?.subject || ([] as Subject[]),
    traditionalKnowledgeLabel:
      data?.traditionalKnowledgeLabel || ([] as TraditionalKnowledgeLabel[]),
  };
};

export const newRaid: RaidCreateRequest = {
  title: [titleGenerator()],
  // description: [descriptionGenerator()],
  date: dateGenerator(),
  access: accessGenerator(),
  // organisation: [organisationGenerator()],
  contributor: [],
  // subject: [subjectGenerator()],
  // relatedRaid: [],
  alternateUrl: [],
  // spatialCoverage: [spatialCoverageGenerator()],
  // relatedObject: [
  //   relatedObjectGenerator(),
  //   relatedObjectGenerator(),
  //   relatedObjectGenerator(),
  // ],
  // alternateIdentifier: [
  //   alternateIdentifierGenerator(),
  //   alternateIdentifierGenerator(),
  //   alternateIdentifierGenerator(),
  // ],
};

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export function getErrorMessageForField(obj: any, keyString: string): any {
  const keys = keyString.split("."); // Split the keyString into an array of keys
  let value = obj;

  for (const key of keys) {
    if (value && key in value) {
      value = value[key]; // Traverse the object
    } else {
      return undefined; // Return undefined if any key is not found
    }
  }

  return value;
}

export const httpStatusCodes: Map<number, string> = new Map([
  // 1xx Informational
  [100, "Continue"],
  [101, "Switching Protocols"],
  [102, "Processing"],
  [103, "Early Hints"],

  // 2xx Success
  [200, "OK"],
  [201, "Created"],
  [202, "Accepted"],
  [203, "Non-Authoritative Information"],
  [204, "No Content"],
  [205, "Reset Content"],
  [206, "Partial Content"],
  [207, "Multi-Status"],
  [208, "Already Reported"],
  [226, "IM Used"],

  // 3xx Redirection
  [300, "Multiple Choices"],
  [301, "Moved Permanently"],
  [302, "Found"],
  [303, "See Other"],
  [304, "Not Modified"],
  [305, "Use Proxy"],
  [307, "Temporary Redirect"],
  [308, "Permanent Redirect"],

  // 4xx Client Errors
  [400, "Bad Request"],
  [401, "Unauthorized"],
  [402, "Payment Required"],
  [403, "Forbidden"],
  [404, "Not Found"],
  [405, "Method Not Allowed"],
  [406, "Not Acceptable"],
  [407, "Proxy Authentication Required"],
  [408, "Request Timeout"],
  [409, "Conflict"],
  [410, "Gone"],
  [411, "Length Required"],
  [412, "Precondition Failed"],
  [413, "Payload Too Large"],
  [414, "URI Too Long"],
  [415, "Unsupported Media Type"],
  [416, "Range Not Satisfiable"],
  [417, "Expectation Failed"],
  [418, "I'm a teapot"],
  [421, "Misdirected Request"],
  [422, "Unprocessable Entity"],
  [423, "Locked"],
  [424, "Failed Dependency"],
  [425, "Too Early"],
  [426, "Upgrade Required"],
  [428, "Precondition Required"],
  [429, "Too Many Requests"],
  [431, "Request Header Fields Too Large"],
  [451, "Unavailable For Legal Reasons"],

  // 5xx Server Errors
  [500, "Internal Server Error"],
  [501, "Not Implemented"],
  [502, "Bad Gateway"],
  [503, "Service Unavailable"],
  [504, "Gateway Timeout"],
  [505, "HTTP Version Not Supported"],
  [506, "Variant Also Negotiates"],
  [507, "Insufficient Storage"],
  [508, "Loop Detected"],
  [510, "Not Extended"],
  [511, "Network Authentication Required"],
]);
