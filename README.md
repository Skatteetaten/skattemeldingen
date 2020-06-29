# Skattemelding


## Initial setup

Getting the gatsby-starter-skatteetaten module (TODO: Add more docs)

    git submodule init
    git submodule update

## How to build

The project is built using npm from the current Node LTS. Install with [nvm](https://github.com/creationix/nvm);

    nvm install --lts

Then run

    npm install

to install the dependencies.

    npm start

will start a local web server and continuously build the documentation as you make changes.

## Deploying to github pages

To update the gh-pages branch and in turn publish to https://skatteetaten.github.io/skattemeldingen/ run

    npm run build && npm run deploy
