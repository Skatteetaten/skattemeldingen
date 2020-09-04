import React from "react";
import Grid from "@skatteetaten/frontend-components/Grid";
import { graphql } from "gatsby";
import { renderAst } from '../components/renderAst';

import TableOfContents from "starter/components/TableOfContents";

import styles from "./documentation-template.module.css";

const mainGrid = {
  sm: 10,
  smPush: 1,
  md: 10,
  mdPush: 1,
  lg: 10,
  lgPush: 1,
  xl: 10,
  xlPush: 1,
  xxl: 10,
  xxlPush: 1,
};

export default function Template({ data }) {
  const { markdownRemark } = data;
  const { frontmatter, fields, htmlAst, headings } = markdownRemark;

  return (
    <Grid>
      <Grid.Row>
        <Grid.Col {...mainGrid}>
          <h1>{frontmatter.title}</h1>
          {headings && (
            <TableOfContents headings={headings} slug={fields.slug} minHeaders={1} />
          )}
          <div
            className={styles.documentationContainer}>
            { renderAst(htmlAst) }
          </div>
        </Grid.Col>
      </Grid.Row>
    </Grid>
  );
}

export const pageQuery = graphql`
  query DocumentationBySlug($slug: String!) {
    markdownRemark(fields: { slug: { eq: $slug } }) {
      htmlAst
      headings {
        value
        depth
      }
      fields {
        slug
      }
      frontmatter {
        title
      }
    }
  }
`;
