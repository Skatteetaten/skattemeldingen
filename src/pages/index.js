import React from "react";
import Grid from "@skatteetaten/frontend-components/Grid";
import { SingleColumnRow } from "../components/Columns";
import { graphql } from 'gatsby';
import { renderAst } from '../components/renderAst';

const IndexPage = ({
  data: {
    allMarkdownRemark: { edges },
  },
}) => {
  const content = edges
    .map(({ node }) => (
      <div key={node.id}>
        { renderAst(node.htmlAst) }
      </div>
    ));

  return (
    <div>
      <Grid>
        <SingleColumnRow>
          { content }
        </SingleColumnRow>
      </Grid>
    </div>
  );
};

export default IndexPage;

export const pageQuery = graphql`
  query IndexQuery {
    allMarkdownRemark(filter: {fields: {slug: {eq: "/frontpage/"}}}) {
      edges {
        node {
          id
          htmlAst
          fields {
            slug
          }
        }
      }
    }
  }
`;
