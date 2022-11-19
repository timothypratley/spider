.PHONY: all setup update clean dev serve test

all: setup test min

setup:
	clojure -P
	npm ci

update:
	clojure -M:outdated --every --write
	npm update --include=dev

major:
	ncu -u

clean:
	rm -rf target/cljs-out

test:
	clojure -M:test-clj
	clojure -M:test-cljs

min: clean
	clojure -M:prod

dev:
	clojure -M:dev

serve: clean
	clojure -M:serve

deploy: min
	cd resources/public
	mkdir -p cljs-out/webapp
	cp target/cljs-out/webapp/main_bundle.js cljs-out/webapp
	rm -rf .git
	git init
	git add .
	git commit -m "Deploy to GitHub Pages"
	git push --force --quiet "git@github.com:timothypratley/spider.git" main:gh-pages
	rm -rf .git
	rm -rf cljs-out
	echo https://timothypratley.github.io/spider
