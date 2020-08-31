import React from "react";
import Grid from "@skatteetaten/frontend-components/Grid";
import { SingleColumnRow } from "../components/Columns";

const IndexPage = ({
  data: {
    allMarkdownRemark: { edges },
  },
}) => {
  const content = edges
    .filter(({ node }) => node.fields && node.fields.slug === '/frontpage/')
    .map(edge => <div key="front" dangerouslySetInnerHTML={{ __html: edge.node.html }} />);

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
    allMarkdownRemark {
      edges {
        node {
          html
          fields {
            slug
          }
        }
      }
    }
  }
`;
