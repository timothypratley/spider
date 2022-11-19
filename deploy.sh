#!/bin/sh
cd resources/public
mkdir -p cljs-out/webapp
cp ../../target/public/cljs-out/webapp/main_bundle.js cljs-out/webapp
rm -rf .git
git init
git add .
git commit -m "Deploy to GitHub Pages"
git push --force --quiet "git@github.com:timothypratley/spider.git" main:gh-pages
rm -rf .git
rm -rf cljs-out
echo https://timothypratley.github.io/spider
