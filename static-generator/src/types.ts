export type Dates = {
  startDate: string;
  endDate: string | null;
};

export type Description = {
  text: string;
  type: {
    id: string;
    schemaUri: string;
  };
  language: {
    id: string;
    schemaUri: string;
  };
};

type ContributorPosition = {
  schemaUri: string;
  id: string;
  startDate: string;
  endDate: string | null;
};

type ContributorRole = {
  schemaUri: string;
  id: string;
};

export type Contributor = {
  id: string;
  position: ContributorPosition[];
  role: ContributorRole[];
  leader: boolean | null;
  contact: boolean | null;
};

type OrganisationRole = {
  schemaUri: string;
  id: string;
  startDate: string;
  endDate: string | null;
};

export type Organisation = {
  schemaUri: string;
  id: string;
  role: OrganisationRole[];
};

export type Title = {
  text: string;
  type: { id: string; schemaUri: string };
  startDate: string;
  endDate: string;
  language: { id: string; schemaUri: string };
};

export type Metadata = {
  title: Title[];
  contributor: Contributor[];
};

export type Subject = {
  id: string;
  schemaUri: string;
  keyword: {
    text: string;
    language: {
      id: string;
      schemaUri: string;
    };
  }[];
};

export type RelatedObject = {
  id: string;
  schemaUri: string;
  type: {
    id: string;
    schemaUri: string;
  };
  category: {
    id: string;
    schemaUri: string;
  };
};

export type AlternateIdentifier = {
  id: string;
  type: string;
};

export type AlternateUrl = {
  url: string;
};

export type RelatedRaid = {
  id: string;
  type: {
    id: string;
    schemaUri: string;
  };
};

export type Access = {
  type: {
    id: string;
    schemaUri: string;
  };
  accessStatement: {
    text: string;
    language: {
      id: string;
      schemaUri: string;
    };
  };
  embargoExpiry: string;
};

export type SpatialCoverage = {
  id: string;
  schemaUri: string;
  place: string;
  language: {
    id: string;
    schemaUri: string;
  };
};
