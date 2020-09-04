import React from "react";
import Grid from "@skatteetaten/frontend-components/Grid";
import NavigationTile from "@skatteetaten/frontend-components/NavigationTile";
import SkeBasis from "@skatteetaten/frontend-components/SkeBasis";
import { SingleColumnRow } from "../components/Columns";
import { graphql, Link } from 'gatsby';

const DocumentationPage = ({
  data: {
    allMarkdownRemark: { edges },
    site: { pathPrefix },
  },
}) => {
  const prefix = process.env.NODE_ENV !== 'production' ? '' : pathPrefix;
  const contents = edges
    .filter(
      ({ node }) =>
        node.fields && node.fields.slug.search("/documentation/") >= 0
    )
    .map(({ node }) => ({
      to: `${prefix}${node.fields.slug}`,
      icon: node.frontmatter.icon,
      heading: node.frontmatter.title,
      description: node.frontmatter.description || "",
    }));

  return (
    <SkeBasis>
      <Grid>
        <SingleColumnRow>
          <h1>Dokumentasjon</h1>
        </SingleColumnRow>
        <SingleColumnRow>
          <NavigationTile
            contents={contents}
            renderContent={(to, content) => <Link to={to}>{content}</Link>}
          />
        </SingleColumnRow>
      </Grid>
    </SkeBasis>
  );
};

export default DocumentationPage;

export const pageQuery = graphql`
  query DocumentationQuery {
    site {
      pathPrefix
    }
    allMarkdownRemark(sort: { order: ASC, fields: [frontmatter___title] }) {
      edges {
        node {
          fields {
            slug
          }
          frontmatter {
            title
            icon
            description
          }
        }
      }
    }
  }
`;
