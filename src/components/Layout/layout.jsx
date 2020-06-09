import React from "react";
import PropTypes from "prop-types";
import { Helmet } from "react-helmet";
import { StaticQuery, graphql } from "gatsby";

import Header from "../Header";
import Footer from "../Footer";
import favicon from "../../../favicon.png";

import "prismjs/themes/prism.css";
import "./layout.css";

class Layout extends React.Component {
  state = {
    showMobileMenu: false,
  };

  toggleMobileMenu = () => {
    this.setState((state) => ({
      showMobileMenu: !state.showMobileMenu,
    }));
  };

  render() {
    const { children } = this.props;
    return (
      <StaticQuery
        query={graphql`
          query SiteTitleQuery {
            site {
              siteMetadata {
                title
                menu {
                  href
                  name
                }
              }
            }
          }
        `}
        render={({ site }) => (
          <>
            <Helmet
              title={site.siteMetadata.title}
              link={[
                {
                  href: favicon,
                  rel: "shortcut icon",
                },
              ]}
            />
            <Header
              menu={site.siteMetadata.menu}
              title={site.siteMetadata.title}
              style={{ marginBottom: "35px" }}
              onToggleMenu={this.toggleMobileMenu}
              showMobileMenu={this.state.showMobileMenu}
            />
            <div>{children}</div>
            <Footer />
          </>
        )}
      />
    );
  }
}

Layout.propTypes = {
  children: PropTypes.object,
};

export default Layout;
