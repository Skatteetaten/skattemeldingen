import React from "react";
import { Link } from "gatsby";
import classnames from "classnames/bind";
import Logo from "@skatteetaten/frontend-components/TopBanner/assets/ske-logo.svg";
import Image from "@skatteetaten/frontend-components/Image";

import styles from "./header.module.css";

const cx = classnames.bind(styles);

const isActive = (href) => ({ isCurrent, isPartiallyCurrent }) => {
  const activeStyle = {
    className: styles.mainHeaderMenuActive,
  };

  if (href === "/" && isCurrent) {
    return activeStyle;
  }

  return href !== "/" && isPartiallyCurrent ? activeStyle : null;
};

const HeaderMenu = ({ menu = [], showMobileMenu }) => (
  <nav
    className={cx({
      mainHeaderNav: true,
      mainHeaderNavHidden: !showMobileMenu,
    })}
  >
    <ul className={styles.mainHeaderMenu}>
      {menu.map((item, index) => (
        <li key={`${item.href}-${index}`}>
          <Link
            to={item.href}
            getProps={isActive(item.href)}
            activeClassName={styles.mainHeaderMenuActive}
          >
            {item.name}
          </Link>
        </li>
      ))}
    </ul>
  </nav>
);

const Header = ({ title, menu, onToggleMenu, showMobileMenu, ...rest }) => (
  <div>
    <div {...rest} className={styles.mainHeader}>
      <div className={styles.mainHeaderContent}>
        <div className={styles.mainHeaderWrapper}>
          <div>
            <Image src={Logo} className={styles.mainHeaderLogo} />
          </div>
          <div className={styles.mainHeaderButton}>
            <button onClick={onToggleMenu}>
              <i className="material-icons">menu</i>
            </button>
          </div>
        </div>
        {menu && <HeaderMenu menu={menu} showMobileMenu={showMobileMenu} />}
      </div>
    </div>
  </div>
);

export default Header;
