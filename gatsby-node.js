const path = require(`path`);
const { createFilePath } = require("gatsby-source-filesystem");
const _ = require("lodash");
const slash = require(`slash`);

exports.createPages = ({ graphql, actions }) => {
  const { createPage } = actions;
  return new Promise((resolve, reject) => {
    const documentationTemplate = path.resolve(
      "src/templates/documentation-template.js"
    );
    // Query for markdown nodes to use in creating pages.
    resolve(
      graphql(
        `
          {
            allMarkdownRemark(limit: 1000) {
              edges {
                node {
                  fields {
                    slug
                  }
                }
              }
            }
          }
        `
      ).then((result) => {
        if (result.errors) {
          reject(result.errors);
        }

        // Create docs pages.
        result.data.allMarkdownRemark.edges.forEach((edge) => {
          const slug = _.get(edge, `node.fields.slug`);
          if (!slug) return;

          createPage({
            path: `${edge.node.fields.slug}`, // required
            component: slash(documentationTemplate),
            context: {
              slug: edge.node.fields.slug,
            },
          });
        });
        return;
      })
    );
  });
};

exports.onCreateNode = ({ node, getNode, actions }) => {
  const { createNodeField } = actions;
  if (node.internal.type === `MarkdownRemark`) {
    const slug = createFilePath({ node, getNode, basePath: `docs` });
    createNodeField({
      node,
      name: `slug`,
      value: slug,
    });
  }
};

exports.onCreateWebpackConfig = ({ stage, actions }) => {
  actions.setWebpackConfig({
    resolve: {
      alias: {
        starter: path.resolve(__dirname, "src"),
      },
    },
  });
};
