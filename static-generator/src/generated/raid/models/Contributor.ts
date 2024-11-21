import type { ContributorPosition } from './ContributorPosition';
import type { ContributorRole } from './ContributorRole';

export interface Contributor {
    id: string;
    schemaUri: string;
    position: Array<ContributorPosition>;
    role: Array<ContributorRole>;
    leader?: boolean;
    contact?: boolean;
}
