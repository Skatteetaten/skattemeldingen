# Skattemelding

Dette kodetreet inneholder kildekode til [implementasjonsguiden for systeminnsending av skattemeldingen](https://skatteetaten.github.io/skattemeldingen/).

## Initial setup

Getting the gatsby-starter-skatteetaten module (TODO: Add more docs)

    git submodule init
    git submodule update

## How to build manually

The project is built using npm from the current Node LTS. Install with [nvm](https://github.com/nvm-sh/nvm);

    nvm install --lts

Then run

    npm install

to install the dependencies.

    npm start

will start a local web server and continuously build the documentation as you make changes.
The documentation will be available at http://localhost:8000/documentation

## Deploying to github pages

## Adding a \/documentation page

Add a folder in /docs/documentation. Name it properly, as it will be reflected in the url.

In that folder, add an index.md file. At the beginning of that file, add

    ---
    icon: ""
    title: ""
    description: ""
    ---

The values will be displayed in the documentation root page. Finally, go wild with markdown.

Icon reference: https://skatteetaten.github.io/frontend-components/#icon
