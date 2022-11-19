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
	./deploy.sh
