import React from "react";
import styles from "./quote.module.css";

const Quote = ({ children, source, ...rest }) => (
  <div {...rest} className={styles.quote}>
    <p>{children}</p>
    <strong>{source}</strong>
  </div>
);

export default Quote;
