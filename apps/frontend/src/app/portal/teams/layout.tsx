import React from "react";

import { ResourceProvider } from "@/providers/resource-provider";

const Layout = ({ children }: { children: React.ReactNode }) => (
  <ResourceProvider resourceId="teams">
    <div>{children}</div>
  </ResourceProvider>
);

export default Layout;
