/* eslint-disable-next-line */
import NextAuth from "next-auth";

declare module "next-auth" {
  import { AuthorityType } from "@/types/users";

  /**
   * Returned by `useSession`, `getSession` and received as a prop on the `SessionProvider` React Context
   */
  export interface User {
    id?: number;
    name?: string | null;
    email?: string | null;
    imageUrl?: string | null;
    accessToken?: string;
    firstName?: string;
    lastName?: string;
    authorities: Array<AuthorityType>;
  }

  interface Session {
    accessToken?: string; // Add accessToken to the session
    provider?: string; // Add provider for OAuth2
    user: DefaultUser & {
      id: string;
      langKey: string;
    };
    error?: string; // Custom property for handling errors
  }
}

declare module "next-auth/jwt" {
  interface JWT {
    id?: string;
    accessToken?: string; // Add accessToken to JWT
    provider?: string; // Add provider to JWT
    user?: DefaultUser & {
      id: string;
    };
  }
}
