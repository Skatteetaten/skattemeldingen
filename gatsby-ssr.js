/**
 * Implement Gatsby's SSR (Server Side Rendering) APIs in this file.
 *
 * See: https://www.gatsbyjs.org/docs/ssr-apis/
 */
import React from "react";
import SkeBasis from "@skatteetaten/frontend-components/SkeBasis";
import Layout from "starter/components/Layout";

export const wrapRootElement = ({ element }) => {
  return (
    <SkeBasis>
      <Layout>{element}</Layout>
    </SkeBasis>
  );
};
