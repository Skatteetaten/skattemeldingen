/**
 * Implement Gatsby's Browser APIs in this file.
 *
 * See: https://www.gatsbyjs.org/docs/browser-apis/
 */
import React from "react";
import Layout from "starter/components/Layout";
import SkeBasis from "@skatteetaten/frontend-components/SkeBasis";

export const onInitialClientRender = () => {
  const body = document.getElementsByTagName("body")[0];
  body.setAttribute("style", "display: block");
};

export const wrapPageElement = ({ element }) => {
  return <Layout>{element}</Layout>;
};

export const wrapRootElement = ({ element }) => {
  return <SkeBasis>{element}</SkeBasis>;
};
