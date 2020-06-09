import React from "react";
import Grid from "@skatteetaten/frontend-components/Grid";

const doubleColGrid = {
  sm: 10,
  smPush: 1,
  md: 10,
  mdPush: 1,
  lg: 10,
  lgPush: 1,
  xl: 3,
  xlPush: 3,
  xxl: 3,
  xxlPush: 3,
};

const singleColGrid = {
  ...doubleColGrid,
  xl: 6,
  xlPush: 3,
  xxl: 6,
  xxlPush: 3,
};

const SingleColumnRow = ({ children }) => (
  <Grid.Row>
    <Grid.Col {...singleColGrid}>{children}</Grid.Col>
  </Grid.Row>
);

const DoubleColumnRow = ({ children }) => (
  <Grid.Row>
    {React.Children.map(children, (child) => {
      if (!child) {
        return false;
      }
      return <Grid.Col {...doubleColGrid}>{child}</Grid.Col>;
    })}
  </Grid.Row>
);

DoubleColumnRow.propTypes = {
  children: (props, propName, componentName) => {
    if (props[propName].filter((child) => child).length !== 2) {
      return new Error(`${propName} must contain two elements`);
    }
  },
};

export { SingleColumnRow, DoubleColumnRow };
