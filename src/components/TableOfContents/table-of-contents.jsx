import React from "react";
import PropTypes from "prop-types";
import { Link } from "gatsby";
import GithubSlugger from "github-slugger";
import Icon from "@skatteetaten/frontend-components/Icon";
import classNames from "classnames";

import styles from "./table-of-contents.module.css";

const slugger = new GithubSlugger();

function createAnchorLink(slug, name) {
  const nameSlug = slugger.slug(name);
  return `${slug}#${nameSlug}`;
}

const TableOfContents = ({ headings, slug, minHeaders = 8 }) => {
  slugger.reset();
  if (headings.length <= minHeaders) {
    return false;
  }
  return (
    <nav className={styles.toc}>
      <ul>
        {headings.map((header, index) => (
          <li
            key={`${header}-${index}`}
            className={classNames(
              styles.tocLevel,
              styles[`tocLevel${header.depth}`]
            )}
          >
            <Icon iconName="ArrowDown" />
            <Link to={createAnchorLink(slug, header.value)}>
              {header.value}
            </Link>
          </li>
        ))}
      </ul>
    </nav>
  );
};

TableOfContents.propTypes = {
  slug: PropTypes.string.isRequired,
  minHeaders: PropTypes.number,
  headings: PropTypes.arrayOf(
    PropTypes.shape({
      value: PropTypes.string.isRequired,
      depth: PropTypes.number.isRequired,
    })
  ).isRequired,
};

export default TableOfContents;
