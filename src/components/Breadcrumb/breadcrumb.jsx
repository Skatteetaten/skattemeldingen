import React from "react";
import PropTypes from "prop-types";

import styles from "./breadcrumb.module.css";

export default class Breadcrumb extends React.PureComponent {
  static propTypes = {
    path: PropTypes.string.isRequired,
    /** (options: { href: string, name: string}) => ReactElement | HTML */
    renderLink: PropTypes.func,
  };

  static defaultProps = {
    renderLink: ({ href, name }) => {
      return <a href={href}>{name}</a>;
    },
  };

  capitalize = (str) => {
    if (!str || str.length < 1) return str;
    return str.slice(0, 1).toUpperCase() + str.slice(1);
  };

  nextPath = (path, current) => {
    const paths = path.split(current);
    return paths.length > 0 ? paths[0] + current : path;
  };

  render() {
    const { path, renderLink, ...rest } = this.props;

    const paths = path
      .split("/")
      .filter((p) => p && p.length > 0 && p.search("#") === -1)
      .map((p, i) => (
        <li key={i} className={styles.breadcrumbsItem}>
          {i > 0 && <span>/</span>}
          {renderLink({
            href: this.nextPath(path, p),
            name: this.capitalize(p),
          })}
        </li>
      ));

    return (
      <nav aria-label="breadcrumb" {...rest}>
        <ol className={styles.breadcrumbs}>
          {paths.slice(0, paths.length - 1)}
        </ol>
      </nav>
    );
  }
}
